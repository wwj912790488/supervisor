#!/bin/sh

#
# Variables
#
BUG_REPORT_VERSION=1.0.2
BUG_REPORT_ROOT=/root
BUG_REPORT_FILE=bugreport
BUG_REPORT_PATH=${BUG_REPORT_ROOT}/bugreport
TOMCAT_PATH=/usr/local/arcvideo/supervisor/tomcat
TASK_ID=
LOG_TIME=
LOG_ENDTIME=
START_AT=`date +%s`

INCLUDE_HARDWARE_INFORMATION=
INCLUDE_SYSTEM_SETTINGS=
INCLUDE_SYSTEM_LOGS=
INCLUDE_SOFTWARE_INFORMATION=
INCLUDE_AGENT_LOGS=
INCLUDE_COMMANDER_LOGS=
INCLUDE_NVIDIA_LOGS=
INCLUDE_AS_LOG=

#
# Show usage.
# Usage:
#    usage appName
#
usage() {
    echo "Usage: `basename $1`: [rslwacn] [-i taskid] [-t time] [-T endtime] [-p path] [-f filename]"
    echo "  -r include hardware information"
    echo "  -s include system settings"
    echo "  -l include system logs"
    echo "  -w include software info"
    echo "  -a include agent logs"
    echo "  -c include commander logs"
    echo "  -n include nvidia bug reports"
    echo "  -o include ASLOG"
    echo "  -i set task id."
    echo "  -t set log time"
    echo "  -T set log end time"
    echo "  -p set path of log file"
    echo "  -f set file name log file"
    echo "  -v show version"
    echo "  -h show this help"
    exit 1
}

#
# Write bug report logs.
# Usage:
#    writelog message
#
writeLog() {
	message="[`date +"%F %T.%3N"`] [$1] $2"
	echo ${message} && echo ${message} >> ${BUG_REPORT_LOG}
}

#
# Write bug report error logs.
# Usage:
#    writeError message
#
writeError() {
	writeLog "ERROR" "$1"
}

#
# Write bug report warnning logs.
# Usage:
#    writeWarn message
#
writeWarn() {
	writeLog "WARN" "$1"
}

#
# Write bug report info logs.
# Usage:
#    writeInfo message
#
writeInfo() {
	writeLog "INFO" "$1"
}

#
# Copy file to the specified path.
# Usage:
#    copyfile file1 path
#
copyfile() {
	if [ -f $1 ] ; then
		writeInfo "copy file $1"
		cp -rfpL $1 $2
	else
		writeWarn "copy file fail: $1 not exist or not a file."
	fi
}

#
# Copy files to the specified path.
# Usage:
#    copyfiles files path
#
copyfiles() {
	if (ls $1) >/dev/null 2>&1 ; then
		writeInfo "copy files: $1."
		cp -rfp $1 $2
	else
		writeWarn "copy files: $1 not found."
	fi
}

#
# Test application i exist or not.
# Usage:
#    whichapp appname
#
whichapp() {
	if (which $1) >/dev/null 2>&1 ; then
		return 0
	else
		writeError "$1 not installed"
		return 1
	fi
}

#
# Convert yyyyMMddHHmmss to timestamp.
# Usage:
#    totimestamp 20150304171922
#
totimestamp() {
	echo $1 | awk '{system("date +%s -d \""substr($0, 1, 4)"-"substr($0, 5, 2)"-"substr($0, 7, 2)" "substr($0, 9, 2)":"substr($0, 11, 2)":"substr($0, 13, 2)"\"")}'
}

#
# Read option from config file.
# Usage:
#    readconf filename option
#
readconf() {
	echo `cat "$1" | grep -E "$2" | awk '{print substr($0,index($0,"=")+1)}' | awk -F"\r" '{print $1}'`
}

#
# Left pad the string to the specified width.
# Usage:
#    leftpad "string" 10 "0"
#
leftpad() {
	tmpstr=$1
	for ((i=${#tmpstr}; i<$2; i++)) ; do
		tmpstr="$3"${tmpstr}
	done
	echo ${tmpstr}
}

#
# parse arguments
#
while getopts "rslwacnoi:t:T:p:f:v" arg
do
    case $arg in
    	r)  INCLUDE_HARDWARE_INFORMATION=1;;
    	s)  INCLUDE_SYSTEM_SETTINGS=1;;
    	l)  INCLUDE_SYSTEM_LOGS=1;;
    	w)  INCLUDE_SOFTWARE_INFORMATION=1;;
    	a)  INCLUDE_AGENT_LOGS=1;;
    	c)  INCLUDE_COMMANDER_LOGS=1;;
    	n)  INCLUDE_NVIDIA_LOGS=1;;
    	o)  INCLUDE_AS_LOG=1;;
        i)  TASK_ID=$OPTARG;;
        t)  LOG_TIME=$OPTARG;;
        T)  LOG_ENDTIME=$OPTARG;;
        p)  BUG_REPORT_ROOT=$OPTARG;;
        f)  BUG_REPORT_FILE=$OPTARG;;
        v)  echo "Version: ${BUG_REPORT_VERSION}" && exit 0;;
        ?)  usage $0;;
    esac
done

if [ "${TASK_ID}" != "" ] ; then
	if !(echo ${TASK_ID} | grep -E "^[0-9]+$") >/dev/null 2>&1 ; then
		echo "The specified task id(${TASK_ID}) is invalid."
		usage $0
	fi
	# format the log time to 
	if [ "${LOG_TIME}" != "" ] ; then
		parts=(`date +"%Y %m %d %H %M"`)
		parts[5]="0"
		if (echo ${LOG_TIME} | grep -E "^[0-9]+\:[0-9]+(\:[0-9]+)?$") >/dev/null 2>&1 ; then
			index=3
		elif (echo ${LOG_TIME} | grep -E "^[0-9]+-[0-9]+ [0-9]+\:[0-9]+(\:[0-9]+)?$") >/dev/null 2>&1 ; then
			index=1
		elif (echo ${LOG_TIME} | grep -E "^([0-9]{2})?[0-9]{2}-[0-9]+-[0-9]+ [0-9]+\:[0-9]+(\:[0-9]+)?$") >/dev/null 2>&1 ; then
			index=0
		else
			echo "The specified time(${LOG_TIME}) is invalid."
			usage $0
		fi
		for part in `echo ${LOG_TIME} | awk 'gsub("-|:"," ")'` ; do
			parts[${index}]=${part}
			((index++))
		done
		if [ ${#parts[0]} == 2 ] ; then
			parts[0]="20"${parts[0]}
		fi
		for ((i=0; i<6; i++)) ; do
			parts[$i]=$(leftpad ${parts[$i]} 2 "0")
		done
		LOG_TIME="${parts[0]}${parts[1]}${parts[2]}${parts[3]}${parts[4]}${parts[5]}"
		LOG_TIME_DISPLAY="${parts[0]}-${parts[1]}-${parts[2]} ${parts[3]}:${parts[4]}:${parts[5]}"
	else
		echo "The log time(-t) is not specified."
		usage $0
	fi
	
	# format the log end time to 
	if [ "${LOG_ENDTIME}" != "" ] ; then
		parts=(`date +"%Y %m %d %H %M"`)
		parts[5]="0"
		if (echo ${LOG_ENDTIME} | grep -E "^[0-9]+\:[0-9]+(\:[0-9]+)?$") >/dev/null 2>&1 ; then
			index=3
		elif (echo ${LOG_ENDTIME} | grep -E "^[0-9]+-[0-9]+ [0-9]+\:[0-9]+(\:[0-9]+)?$") >/dev/null 2>&1 ; then
			index=1
		elif (echo ${LOG_ENDTIME} | grep -E "^([0-9]{2})?[0-9]{2}-[0-9]+-[0-9]+ [0-9]+\:[0-9]+(\:[0-9]+)?$") >/dev/null 2>&1 ; then
			index=0
		else
			echo "The specified time(${LOG_ENDTIME}) is invalid."
			usage $0
		fi
		for part in `echo ${LOG_ENDTIME} | awk 'gsub("-|:"," ")'` ; do
			parts[${index}]=${part}
			((index++))
		done
		if [ ${#parts[0]} == 2 ] ; then
			parts[0]="20"${parts[0]}
		fi
		for ((i=0; i<6; i++)) ; do
			parts[$i]=$(leftpad ${parts[$i]} 2 "0")
		done
		LOG_ENDTIME="${parts[0]}${parts[1]}${parts[2]}${parts[3]}${parts[4]}${parts[5]}"
		LOG_ENDTIME_DISPLAY="${parts[0]}-${parts[1]}-${parts[2]} ${parts[3]}:${parts[4]}:${parts[5]}"
	else
		echo "The log end time(-T) is not specified."
	fi
fi

#
# export variables and functions
#
export ARCVIDEO_PATH=/usr/local/arcvideo/supervisor
export BUG_REPORT_LOG=${BUG_REPORT_PATH}/report.log
export -f writeLog
export -f writeError
export -f writeWarn
export -f writeInfo
export -f copyfile
export -f copyfiles
export -f whichapp
export -f totimestamp
export -f readconf

#
# clean bug report path
#
rm ${BUG_REPORT_PATH} -rf && mkdir -p ${BUG_REPORT_PATH}
writeInfo "-----------------------------------------------------------------------------------"
writeInfo "                              ArcVideo Bug Report Tool                             "
writeInfo "-----------------------------------------------------------------------------------"
writeInfo "   Version: ${BUG_REPORT_VERSION}"
writeInfo "  Start at: `date +\"%FT%T%z\"`"
writeInfo "   Task id: ${TASK_ID}"
writeInfo "ASLOG time: ${LOG_TIME_DISPLAY}"
writeInfo "-----------------------------------------------------------------------------------"

#
# package hardware info
#
if [ "${INCLUDE_HARDWARE_INFORMATION}" != "" ] ; then
	writeInfo ""
	writeInfo "package hardware information ..."
	cd ${BUG_REPORT_PATH} && mkdir hardware && cd hardware
	# dmidecode
	if whichapp dmidecode ; then
		dmidecode -s 2>&1| grep -E "^\\s+" | awk '{print $1; system("dmidecode -s "$1); print ""}' > hardware.txt
		dmidecode > dmidecode.txt
		dmidecode -t memory > memory.txt
		dmidecode -t processor > processor.txt
		if [ `dmidecode -s system-uuid | grep -E "^#" | wc -l` -gt 0 ] ; then
			writeError "dmidecode need to upgrade."
		fi
	fi
	# disk
	fdisk -l > disk.txt
	if whichapp smartctl ; then
		fdisk -l | grep "Disk /dev" | awk '{system("smartctl -a " substr($2, 1, length($2)-1))}' > smartctl.txt
	fi
	# memory
	cat /proc/meminfo > meminfo.txt
	# cpu
	cat /proc/cpuinfo > cpuinfo.txt
	# ipmi
	if whichapp ipmitool ; then
		ipmitool sensor > ipmi_sensor.txt
		ipmitool sel list > ipmi_sel.txt
	fi
fi

#
# package system log
#
if [ "${INCLUDE_SYSTEM_LOGS}" != "" ] ; then
	writeInfo ""
	writeInfo "package system logs ..."
	cd ${BUG_REPORT_PATH} && mkdir syslog && cd syslog
	#copyfiles "/var/log/*" "./"
	copyfiles "/var/log/messages*" "./"
	copyfiles "/var/log/dmesg*" "./"
	copyfile "/var/log/boot.log" "./"
	
fi

#
# package system settings
#
if [ "${INCLUDE_SYSTEM_SETTINGS}" != "" ] ; then
	writeInfo ""
	writeInfo "package system information ..."
	cd ${BUG_REPORT_PATH} && mkdir system && cd system
	copyfile /etc/hosts ./
	copyfile /etc/resolv.conf ./
	copyfile /etc/sysctl.conf ./
	copyfile /etc/rc.local ./
	copyfile /etc/profile ./
	copyfile /etc/grub.conf ./
	copyfile /etc/fstab ./
	copyfile /etc/udev/rules.d/70-persistent-net.rules ./
	copyfile /etc/security/limits.conf ./
	uname -a > os.txt
	cat /etc/system-release >> os.txt
	sysctl -a > ./sysctl_current.txt
	ulimit -a > ulimit.txt
	cat /proc/sys/vm/drop_caches > dropcaches.txt
	ls -l /etc > syslink.txt 2>&1
	
	writeInfo "list network information ..."
	ifconfig > ifconfig.txt
	ip addr > ipaddr.txt
	ip addr | grep "state" | awk -F":" '{print $2}' | grep -Ev "lo|virbr" | awk '{system("ethtool " $1); print "\n"}' > ethtool.txt
	
	# print speed less than 1000Mb/s
	ip addr | grep "state" | awk -F":" '{print $2}' | grep -Ev "lo|virbr" \
		| awk '{system("echo "$1" $(ethtool " $1 "| grep -E \"Speed\\:.*Mb/s\")")}' \
		| awk '{ if ($3 !="") print $1, $3, $3}' | awk 'gsub("Mb/s", "", $3) {if ($3<1000) system("writeError \""$1" speed ("$2") is less than 1000Mb/s\"")}'
	
	route > route.txt
	service iptables status > ./iptables.txt
	
	writeInfo "list services status ..."
	chkconfig --list > ./chkconfig.txt
	chkconfig --list | awk '{print $1; print "--------------------------"; system("service " $1 " status"); print ""}' > ./services.txt 2>&1
	
	# check system links
	ls -l /etc/grub.conf | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/grub.conf is not a link.\"")}'
	ls -l /etc/init.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/init.d is not a link.\"")}'
	ls -l /etc/localtime | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/localtime is not a link.\"")}'
	ls -l /etc/rc.local | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc.local is not a link.\"")}'
	ls -l /etc/rc.sysinit | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc.sysinit is not a link.\"")}'
	ls -l /etc/rc | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc is not a link.\"")}'
	ls -l /etc/rc0.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc0.d is not a link.\"")}'
	ls -l /etc/rc1.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc1.d is not a link.\"")}'
	ls -l /etc/rc2.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc2.d is not a link.\"")}'
	ls -l /etc/rc3.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc3.d is not a link.\"")}'
	ls -l /etc/rc4.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc4.d is not a link.\"")}'
	ls -l /etc/rc5.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc5.d is not a link.\"")}'
	ls -l /etc/rc6.d | grep "\->" | wc -l | awk '{if ($1==0) system("writeError \"/etc/rc6.d is not a link.\"")}'
	
	#writeInfo "list open files ..."
	#lsof > lsof.txt
	#cat /proc/sys/fs/file-nr > file-nr.txt
	#cat /proc/sys/fs/file-max > file-max.txt
	#filemax=`cat /proc/sys/fs/file-max`
	#if [ ${filemax} -lt 65535 ] ; then
	#	writeError "The number of max open files (${filemax}) is too low."
	#fi
	
	# list all df, sometime the remote mount may very slow, so execute async with timeout
	writeInfo "executing df ..."
	rm -rf df.txt
	(df && echo "DONE") > df.txt &
	waitTime=15
	while [ $waitTime -gt 0 ] && ( [ ! -f df.txt ] || [ ! "`tail -n 1 df.txt`" = "DONE" ] )
	do
		sleep 1
		((waitTime--))
	done
	if [ $waitTime -eq 0 ] ; then
		writeWarn "executing df timeout."
	fi
	
	# list all mount, sometime the remote mount may very slow, so execute async with timeout
	writeInfo "executing mount ..."
	rm -rf mount.txt
	(mount && echo "DONE") > mount.txt &
	waitTime=15
	while [ $waitTime -gt 0 ] && ( [ ! -f mount.txt ] || [ ! "`tail -n 1 mount.txt`" = "DONE" ] )
	do
		sleep 1
		((waitTime--))
	done
	if [ $waitTime -eq 0 ] ; then
		writeWarn "executing mount timeout."
	fi
fi

#
# package software info
#
if [ "${INCLUDE_SOFTWARE_INFORMATION}" != "" ] ; then
	writeInfo ""
	writeInfo "package software information ..."
	cd ${BUG_REPORT_PATH} && mkdir software && cd software
	
	# get software versions
	writeInfo "get software versions ..."
	java -version > jdk.txt 2>&1
	mysql --version > mysql.txt
	if [ -d ${ARCVIDEO_PATH}/supervisor-agent ] ; then
		ls ${ARCVIDEO_PATH}/supervisor-agent/lib/supervisor-agent-*.jar > agent.txt 2>&1
	fi
	if [ -d ${ARCVIDEO_PATH}/logging ] ; then
		ls ${ARCVIDEO_PATH}/logging/lib/arcvideo-logging-app*.jar > logging.txt 2>&1
	fi
	if [ -d ${ARCVIDEO_PATH}/supervisor ] ; then
		ls ${ARCVIDEO_PATH}/supervisor/WEB-INF/lib/supervisor-model*.jar > jssh.txt 2>&1
	fi
	pgrep -fl java > process_java.txt 2>&1
	
	# collect tomcat information
	if [ -d TOMCAT_PATH ] ; then
		writeInfo "get tomcat information ..."
		mkdir tomcat && cd tomcat
		TOMCAT_PATH/bin/version.sh > ./version.txt 2>&1
		copyfiles "TOMCAT_PATH/conf/Catalina/localhost/*.xml" "./"
		mkdir conf
		copyfile TOMCAT_PATH/conf/server.xml ./conf
		copyfile TOMCAT_PATH/conf/web.xml ./conf
		copyfile TOMCAT_PATH/logs/catalina.out ./
		cd ..
	fi
fi

#
# package agent log.
#
if [ "${INCLUDE_AGENT_LOGS}" != "" ] ; then
	if [ -d ${ARCVIDEO_PATH}/supervisor-agent ] ; then
		writeInfo ""
		writeInfo "package agent logs ..."
		cd ${BUG_REPORT_PATH} && mkdir agent && cd agent
		AGENT_HOME=${ARCVIDEO_PATH}/supervisor-agent
		writeInfo "agent home: ${AGENT_HOME}"
	
		# copy agent logs
		copyfile ${AGENT_HOME}/logs/boot.log ./
		copyfile ${AGENT_HOME}/conf/env.properties ./
		copyfile ${AGENT_HOME}/conf/agent.properties ./
		agentlog4j=${AGENT_HOME}/conf/log4j.properties
		if [ -f ${agentlog4j} ] ; then
			agentlogpath=$(readconf ${agentlog4j} "log4j.appender.*.File=")
			agentlogpath=${agentlogpath/$\{agent.home\}/${AGENT_HOME}}
			writeInfo "agent log path: ${agentlogpath}"
		else
			agentlogpath=${AGENT_HOME}/logs/log4j.log
			writeWarn "agent log path not set, use default path: ${agentlogpath}"
		fi
		copyfiles "${agentlogpath}*" "./"
		
	fi
fi

#
# package commander log.
#
if [ "${INCLUDE_COMMANDER_LOGS}" != "" ] ; then
	if [ -d ${ARCVIDEO_PATH}/supervisor ] ; then
		writeInfo ""
		writeInfo "package commander logs ..."
		cd ${BUG_REPORT_PATH} && mkdir commander && cd commander
	
		# get commander version
		#if [ -f ${ARCVIDEO_PATH}/supervisor/WEB-INF/classes/env.properties ] ; then
		#	cp ${ARCVIDEO_PATH}/supervisor/WEB-INF/classes/env.properties ./
		#fi
		#if [ -f ${ARCVIDEO_PATH}/supervisor/META-INF/maven/com.arcsoft/supervisor-web/pom.xml ] ; then
		#	head -n 10 ${ARCVIDEO_PATH}/supervisor/META-INF/maven/com.arcsoft/commander-web/pom.xml | grep version > commander.txt  2>&1
		#fi
	
		# copy commander logs
		cmdlog4j=${ARCVIDEO_PATH}/supervisor/WEB-INF/classes/log4j.properties
		if [ -f ${cmdlog4j} ] ; then
			#cmdlog=$(readconf ${cmdlog4j} "log4j.appender.*.File=")
			copyfile /data/supervisor/logs/* ./
			writeInfo "commander log path: ${cmdlog}"
		else
			#cmdlog=/usr/local/arcvideo/live/commander/logs/log.log
			writeWarn "commander log path not set, use default path: ${cmdlog}"
		fi
	
	
		# get database information
		dbconf="${ARCVIDEO_PATH}/supervisor/WEB-INF/classes/database.properties"
		dbname=$(readconf ${dbconf} "jdbc.url")
		dbname=${dbname##*/}
		dbname=${dbname%%\?*}
		dbuser=$(readconf ${dbconf} "jdbc.username")
		dbpwd=$(readconf ${dbconf} "jdbc.password")
		writeInfo "commander database name: [${dbname}]"
		writeInfo "commander database user: [${dbuser}]"
		copyfile /etc/my.cnf ./
		copyfile ${dbconf} ./
		mysql --version > mysql.txt
		echo "show master status\G" | mysql -B -u${dbuser} -p${dbpwd} ${dbname} > master.txt 2>&1
		echo "show slave status\G" | mysql -B -u${dbuser} -p${dbpwd} ${dbname} > slave.txt 2>&1
		echo "select * from settings;" | mysql -B -u${dbuser} -p${dbpwd} ${dbname} > settings.txt 2>&1
		echo "select * from server_groups;" | mysql -B -u${dbuser} -p${dbpwd} ${dbname} > groups.txt 2>&1
		echo "select * from servers;" | mysql -B -u${dbuser} -p${dbpwd} ${dbname} > servers.txt 2>&1
	fi
fi

#
# package transcoder logs
#
if [ "${INCLUDE_AS_LOG}" != "" ] ; then
if [ -d ${ARCVIDEO_PATH}/transcoder-supervisor/ ] ; then
	writeInfo ""
	writeInfo "package transcoder information ..."
	cd ${BUG_REPORT_PATH} && mkdir transcoder && cd transcoder
	# get running tasks and xmls
	pgrep -fl transcoder.exe > tasks.txt
# remove copy all xmls	
#	pgrep -fl transcoder.exe | awk '{system("copyfile " $NF " ./")}'
	# copy config files
	copyfile ${ARCVIDEO_PATH}/transcoder-supervisor/ASCodec.ini ./
	copyfile ${ARCVIDEO_PATH}/transcoder-supervisor/ASLOG.ini ./
	# get modules and versions
	copyfile ${ARCVIDEO_PATH}/transcoder-supervisor/arcsoftversion.txt ./
	ls -l ${ARCVIDEO_PATH}/transcoder-supervisor/ > modules.txt  2>&1
	nm ${ARCVIDEO_PATH}/transcoder-supervisor/*.exe 2>/dev/null | grep VERSION > ./version.txt 2>&1
	nm ${ARCVIDEO_PATH}/transcoder-supervisor/*.so 2>/dev/null | grep VERSION >> ./version.txt 2>&1
	nm ${ARCVIDEO_PATH}/transcoder-supervisor/VMFRegister* 2>/dev/null | grep VERSION | awk '{gsub(".*VERSION_", ""); print $0}'>> ./version.txt
	# analyze core dump files
	if (ls ${ARCVIDEO_PATH}/transcoder-supervisor/core.* >/dev/null 2>&1) ; then
		writeInfo "analyze core dump files ..."
		ls ${ARCVIDEO_PATH}/transcoder-supervisor/core.* 2>/dev/null | awk '{system("gdb --batch "$0" >`basename "$0"`.txt 2>&1")}'
	else
		writeInfo "no core dump file found: ${ARCVIDEO_PATH}/transcoder/"
	fi

	# package aslogs for the specified task.
	if [ "${TASK_ID}" != "" ] ; then
		# get archived aslog path
		if [ -f ${ARCVIDEO_PATH}/logging/conf/logging.xml ] ; then
			LOG_ARCHIVE_PATH=`cat ${ARCVIDEO_PATH}/logging/conf/logging.xml  | grep -e "<archive>.*</archive>"`
			LOG_ARCHIVE_PATH=${LOG_ARCHIVE_PATH#*>}
			LOG_ARCHIVE_PATH=${LOG_ARCHIVE_PATH%<*}
			writeInfo "aslog archive path: ${LOG_ARCHIVE_PATH}"
		else
			LOG_ARCHIVE_PATH=/mnt/data/local-disk1/logarchive
			writeWarn "logging service may not installed, use default archive path: ${LOG_ARCHIVE_PATH}"
		fi
		
		if [ "${LOG_ENDTIME}" != "" ] ; then
			logtimestamp=$(totimestamp $LOG_TIME)
			logendtimestamp=$(totimestamp $LOG_ENDTIME)
			sum=$[ $logtimestamp + $logendtimestamp ]
			poor=$[ $logendtimestamp - $logtimestamp  ]
			sumr=$[ $sum / 2 ]
			poorr=$[ $poor / 2 ]
		else
			logtimestamp=$(totimestamp $LOG_TIME)
		fi
		
		export LOG_ARCHIVE_PATH
		
		# copy archived aslog
		if [ "${LOG_ENDTIME}" != "" ] ; then
			ls ${LOG_ARCHIVE_PATH}/${TASK_ID}_* 2>/dev/null \
				| awk -F"-" '{system("echo $(totimestamp "$(NF-1)") "$0)}' \
				| awk '$1=$1-'"$sumr"' { if (($1>=0 && $1<='$poorr') ||($1<0 && $1>=-'$poorr')) system("copyfile \""$2"\" \"./\"")}' 
		else
			ls ${LOG_ARCHIVE_PATH}/${TASK_ID}_* 2>/dev/null \
				| awk -F"-" '{system("echo $(totimestamp "$(NF-1)") "$0)}' \
				| awk '$1=$1-'"$logtimestamp"' { if (($1>=0 && $1<=750) ||($1<0 && $1>=-750)) system("copyfile \""$2"\" \"./\"")}' 
		fi
		if (ls ${TASK_ID}_*.zip >/dev/null 2>&1) || (ls ${TASK_ID}_*.lz >/dev/null 2>&1) ; then
			# copy firstlog, xml and cached uploaded hls files.
			(ls ${TASK_ID}_*.zip ${TASK_ID}_*.lz) 2>/dev/null \
				| awk -F"-" '{print $1, $2}' | uniq \
				| awk '{
					system("copyfile \"${LOG_ARCHIVE_PATH}/"$1"-"$2"-*-\\[first\\]*\" \"./\""); \
					system("copyfile \"${ARCVIDEO_PATH}/tmpdir/"$1".xml\" \"./\""); \
					gsub(".*_", "", $2); system("copyfiles \"/mnt/data/local-disk1/pid"$2"-*.m3u8*.txt\" \"./\"")}'
		else
			writeError "cannot find the archived aslogs."
			writeInfo "try to find the lastest aslog first log."
			ls ${LOG_ARCHIVE_PATH}/${TASK_ID}_*\[first\]* 2>/dev/null \
				| awk -F"-" '{print $(NF-1), $0}' | sort -r | head -n 1 \
				| awk '{system("copyfile \""$2"\" \"./\"")}'
			if ls ${TASK_ID}_*\[first\]* >/dev/null 2>&1 ; then
				(ls ${TASK_ID}_*\[first\]*) 2>/dev/null \
					| awk -F"-" '{print $1, $2}' | uniq \
					| awk '{system("copyfile \"${ARCVIDEO_PATH}/tmpdir/"$1".xml\" \"./\""); \
						gsub(".*_", "", $2); system("copyfiles \"/mnt/data/local-disk1/pid"$2"-*.m3u8*.txt\" \"./\"")}'
			else
				writeError "cannot find the first aslogs."
			fi
		fi

		# aslog path
		if [ -f ${ARCVIDEO_PATH}/transcoder-supervisor/ASLOG.ini ] ; then
			aslogpath=`cat ${ARCVIDEO_PATH}/transcoder-supervisor/ASLOG.ini | grep TP | awk -F"=" '{print $2}'`
			writeInfo "aslog log path: ${aslogpath}"
			
			# copy the current aslog and first log
			copyfiles ${aslogpath}/*--${TASK_ID}_*.lz "./"
		fi
	fi
fi
fi

#
# package nvidia bug reports
#
if [ "${INCLUDE_NVIDIA_LOGS}" != "" ] ; then
	KERNEL_RELEASE=`uname --kernel-release`
	NVIDIA_FILE=/lib/modules/$KERNEL_RELEASE/kernel/drivers/video/nvidia.ko
	if [ -f $NVIDIA_FILE ] ; then
		if whichapp nvidia-smi ; then
			writeInfo ""
			writeInfo "package nvidia information ..."
			cd ${BUG_REPORT_PATH} && mkdir nvidia && cd nvidia
			nvidia-smi -L > gpu.txt
			nvidia-smi > gpuinfo.txt
			if whichapp nvidia-bug-report.sh ; then
				nvidia-bug-report.sh > ./nvidia-bug-report.log 2>&1
				writeInfo "package nvidia information complete."
			fi
		fi
	fi
fi

#
# package and clean
#
writeInfo ""
writeInfo "-----------------------------------------------------------------------------------"
writeInfo "Summary"
writeInfo "-----------------------------------------------------------------------------------"
writeInfo "Total: $((`date +%s` - START_AT)) s"
writeInfo "Finished at: `date +\"%FT%T%z\"`"
writeInfo "Warning: `cat ${BUG_REPORT_LOG} | grep -E "^\[.*\]\s+\[WARN\]" | wc -l`"
writeInfo "Error: `cat ${BUG_REPORT_LOG} | grep -E "^\[.*\]\s+\[ERROR\]" | wc -l`"
writeInfo "-----------------------------------------------------------------------------------"
cd ${BUG_REPORT_PATH}
if whichapp zip ; then
	rm ${BUG_REPORT_FILE} -rf
	zip -ryq ${BUG_REPORT_ROOT}/${BUG_REPORT_FILE} *
else
	rm ${BUG_REPORT_FILE} -rf
	tar -czf ${BUG_REPORT_ROOT}/${BUG_REPORT_FILE} *
fi
cd ${BUG_REPORT_ROOT}
rm ${BUG_REPORT_PATH} -rf
