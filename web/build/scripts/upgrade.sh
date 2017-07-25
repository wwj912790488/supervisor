#!/bin/sh
##############################################################################################
#                     Script for upgrade Commander / Agent                                   #
#                               Version: 20150701                                            #
#                                Author: TangWei                                             #
##############################################################################################

#
# Upgrade packages
#

# live commander zip package full path (such as /root/\#7875_D10_1.0.23.zip)
COMMANDER_PACKAGE=

UPDATE_TRANSCODER=true

WOWZA_INSTALL_PATH=/usr/local/WowzaStreamingEngine

WOWZA_STORAGEDIR=$WOWZA_INSTALL_PATH/applications/live/content/

IS_WOWZA_EXIST=`[ -f "$WOWZA_INSTALL_PATH/conf/Server.xml" ] && echo 1 || echo 0`

# jre tar.gz package full path (such as /root/server-jre-7u67-linux-x64.tar.gz)
JRE_PACKAGE=

# tomcat tar.gz package full path (such as /root/server-jre-7u67-linux-x64.tar.gz)
TOMCAT_PACKAGE=

#
# Set global variables
#
CLUSTER_IP=239.95.96.99
CLUSTER_PORT=8920
CLUSTER_INTERFACE=
# Virtual IP should be used here.
RTSP_IP=
AGENT_PORT=6000
AGENT_COMMAND_PORT=6001
TOMCAT_PORT=80
TOMCAT_HOME=/usr/local/tomcat
ARCVIDEO_HOME=/usr/local/arcsoft/arcvideo
TRANSCODER=transcoder-supervisor
TRANSCODER_WORKDIR=tmpdir
DATABASE_NAME=supervisordb
DATABASE_PWD=root
DISABLE_VNC=false
DISABLE_SMB=false
# If set to true, indicate start all services after upgrade complete.
START_AFTER_UPGRADE=true

#
# Local Variables
#
SUFFIX=`date +%Y%m%d%H%M%S`
UNZIP_TMP=/home/upgrade_$SUFFIX
BACKUP_HOME=/home/backup
SERVER_TYPE=$1
CURDIR=`pwd`
EXIST_DATABASE=false


function abspath() {
  if [ -d "$1" ]; then
    (cd "$1"; pwd)
  elif [ -f "$1" ]; then
    if [[ $1 == */* ]]; then
        echo "$(cd "${1%/*}"; pwd)/${1##*/}"
    else
        echo "$(pwd)/$1"
    fi
  fi
}

SUGGEST_COMMANDER_PACKAGE_PATH=$(abspath $(ls | grep zip | head -1))

echo "Enter path of the upgrade package archive file:"
echo "[$SUGGEST_COMMANDER_PACKAGE_PATH]"

read -e COMMANDER_PACKAGE

if [ "$COMMANDER_PACKAGE" == "" ]; then
	COMMANDER_PACKAGE=$SUGGEST_COMMANDER_PACKAGE_PATH
fi

#
# Check server type.
#
if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "agent" ] || [ "$SERVER_TYPE" == "all" ]; then
	echo "[INFO] install server as $SERVER_TYPE."
	if [ ! -f "$COMMANDER_PACKAGE" ] ;then
		echo "[ERROR] commander package is not exist."
		exit 1
	fi
elif [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
	FILENAME=`basename $0`
	echo "$FILENAME -h --help    # show usage"
	echo "$FILENAME -v --version # show version"
	echo "$FILENAME commander    # install commander"
	echo "$FILENAME agent        # install agent"
	echo "$FILENAME all          # install commander and agent in one server"
	exit 0
elif [ "$1" == "--version" ] || [ "$1" == "-v" ]; then
	cat $0 | grep "Version"":" | awk '{print $3}'
	exit 0
else
	FILENAME=`basename $0`
	echo "$FILENAME -h --help    # show usage"
	echo "$FILENAME -v --version # show version"
	echo "$FILENAME commander    # install commander"
	echo "$FILENAME agent        # install agent"
	echo "$FILENAME all          # install commander and agent in one server"
	exit 0
fi

# Configure cluster 
CLUSTER_CONFIGED=false

echo "[INFO] search from old cluster configuration"

if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "all" ]; then
	EXIST_CLUSTER_IP=$(mysql -u root -proot $DATABASE_NAME -se "select value from settings where \`key\`=\"cluster.ip\"" 2>/dev/null)
	EXIST_CLUSTER_PORT=$(mysql -u root -proot $DATABASE_NAME -se "select value from settings where \`key\`=\"cluster.port\"" 2>/dev/null)
	SYSTEM_UUID=`dmidecode -s system-uuid`
	EXIST_BIND_IP=$(mysql -u root -proot $DATABASE_NAME -se "select value from settings where \`key\`=\"cluster.bindAddr_$SYSTEM_UUID\"" 2>/dev/null)
else
	AGENT_CONFIG_FILE=$ARCVIDEO_HOME/supervisor-agent/conf/agent.properties
	EXIST_CLUSTER_IP=$(sed -n "s/^cluster.ip=\([0-9\.]*\).*$/\1/p" $AGENT_CONFIG_FILE)
	EXIST_CLUSTER_PORT=$(sed -n "s/^cluster.port=\([0-9\.]*\).*$/\1/p" $AGENT_CONFIG_FILE)
	EXIST_BIND_IP=$(sed -n "s/^cluster.bind=\([0-9\.]*\).*$/\1/p" $AGENT_CONFIG_FILE)
fi

if [ "$EXIST_CLUSTER_IP" != "" ] && [ "$EXIST_CLUSTER_PORT" != "" ] && [ "$EXIST_BIND_IP" != "" ]; then
  echo "Old cluster configuration found: CLUSTER_IP=$EXIST_CLUSTER_IP, CLUSTER_PORT=$EXIST_CLUSTER_PORT, BIND_IP=$EXIST_BIND_IP. Is that ok?"
  echo "[y or n]"
  read -e USE_OLD_CLUSTER_CONFIG
  if [ "$USE_OLD_CLUSTER_CONFIG" == "" ] || [ "$USE_OLD_CLUSTER_CONFIG" == "y" ] || [ "$USE_OLD_CLUSTER_CONFIG" == "Y" ]; then
    CLUSTER_IP=$EXIST_CLUSTER_IP
    CLUSTER_PORT=$EXIST_CLUSTER_PORT
    BINDIP=$EXIST_BIND_IP
    CLUSTER_CONFIGED=true
  fi
fi

if [ "$CLUSTER_CONFIGED" != "true" ]; then
  echo "[INFO] configure cluster..."
  echo "Enter CLUSTER_IP: [$CLUSTER_IP]"
  read -e INPUT_CLUSTER_IP
  if [ "$INPUT_CLUSTER_IP" != "" ]; then
    CLUSTER_IP=$INPUT_CLUSTER_IP
  fi
  echo "Enter CLUSTER_PORT: [$CLUSTER_PORT]"
  read -e INPUT_CLUSTER_PORT
  if [ "$INPUT_CLUSTER_PORT" != "" ]; then
    CLUSTER_PORT=$INPUT_CLUSTER_PORT
  fi
  while [ "$BINDIP" == "" ]
  do
    echo "Enter CLUSTER_INTERFACE: "
    read -e CLUSTER_INTERFACE
    BINDIP=$(ifconfig | sed -n "
        /$CLUSTER_INTERFACE/ {
                N
                N
                /RUNNING/ P
        }" | awk '{print substr($2, 6)}')
    BINDIP_COUNT=$(ifconfig | sed -n "
        /$CLUSTER_INTERFACE/ {
                N
                N
                /RUNNING/ P
        }" | awk '{print substr($2, 6)}' | wc -l)
    if [ "$BINDIP" == "" ]; then
        echo "[ERROR] cannot get bind address for \"$CLUSTER_INTERFACE\"."
    elif [ $BINDIP_COUNT -ne 1 ]; then
        BINDIP=
        echo "[ERROR] get more than one bind address $BINDIP, please specify CLUSTER_INTERFACE with a smaller range."
    else
        echo "[INFO] bind ip is $BINDIP"
    fi
  done
fi

# Configurate rtsp server if init install
if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "all" ] ;then
  mysql -u root -proot -se "use $DATABASE_NAME;" >/dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Enter RTSP server ip:"
    echo "[$BINDIP]"
    read -e RTSP_IP
    if [ "$RTSP_IP" == "" ] ;then
      RTSP_IP=$BINDIP
    fi
  fi
fi

#
# Backup old backup folder
#
if [ -d $BACKUP_HOME ] ;then
	mv $BACKUP_HOME $BACKUP_HOME"_"$SUFFIX
fi

if [ ! -d $UNZIP_TMP ] ;then
	mkdir -p $UNZIP_TMP
fi

if [ ! -d $BACKUP_HOME ] ;then
	mkdir -p $BACKUP_HOME
fi

#
# Backup the source folder to the dest.
# Usage:
#    backup_folder source dest
#
backup_folder() {
	if [ -d $1 ] ;then
		mv $1 $2
		echo "[INFO] $1 backup success";
	else
		echo "[WARN] backup failed: $1 no exist.";
	fi
}

#
# Exist service
# Usage:
#    exist_service service_name
#
exist_service() {
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		return 0
	else
		return 1
	fi
}

#
# Setup service.
# Usage:
#    setup_service daemon_file service_name
#
setup_service() {
	if [ -f $1 ] ;then
		cp -rf $1 /etc/init.d/
		chkconfig --add $2
		chkconfig $2 --level 2345 on
		echo "[INFO] service $2 setup success"
	else
		echo "[WARN] setup service failed: $1 no exist."
	fi
}

#
# Disable service.
# Usage:
#    disable_service service_name
#
disable_service() {
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		echo "[INFO] stop service '$1'"
		service $1 stop
		echo "[INFO] disable service '$1'"
		chkconfig $1 off
	else
		echo "[INFO] service '$1' not exist..."
	fi
}

#
# Start service.
# Usage:
#    start_service service_name
#
start_service() {
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		echo "[INFO] start service '$1'"
		service $1 start
	else
		echo "[ERROR] service '$1' not exist..."
	fi
}

#
# Stop service.
# Usage:
#    stop_service service_name
#
stop_service() {
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		echo "[INFO] stop service '$1'"
		service $1 stop
	else
		echo "[WARN] service '$1' not exist..."
	fi
}

#
# Commit lines in the specified file.
# Usage:
#    comment_lines string file
#
comment_lines() {
	sed -i "s|^\(\s*[^#]*\)\?$1|#&|g" $2
}

#
# Stop services before upgrade
#
echo "[INFO] shutdown tomcat ..."
$TOMCAT_HOME/bin/shutdown.sh >/dev/null 2>&1

if [[ $IS_WOWZA_EXIST = 1 ]]; then
	if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "all" ]; then
		stop_service WowzaStreamingEngine
		stop_service WowzaStreamingEngineManager
	fi
fi

if [ "$SERVER_TYPE" == "agent" ] || [ "$SERVER_TYPE" == "all" ]; then
	stop_service supervisorAgentd
#	stop_service monitord
#	stop_service loggingd
#	stop_service faultd
fi
if (pgrep -f java) >/dev/null 2>&1 ;then
	echo "[WARN] force kill java processes ..."
	pkill -9f java
fi
if (pgrep -f transcoder.exe) >/dev/null 2>&1 ;then
	echo "[WARN] force kill transcoder.exe processes ..."
	pkill -9f transcoder.exe
fi

#
# Backup relation folders
#
echo "[INFO] start backup ..."
if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "all" ]; then
	# backup database
	if exist_service mysql; then
		service mysql start
	elif exist_service mysqld; then
		service mysqld start
	fi
	if echo "use $DATABASE_NAME" | mysql -uroot -p$DATABASE_PWD >/dev/null 2>&1; then
		EXIST_DATABASE=true
	fi
	if [ "$EXIST_DATABASE" == "true" ]; then
		echo "[INFO] backup database $DATABASE_NAME"
		mysqldump -uroot -p$DATABASE_PWD --add-drop-database --databases $DATABASE_NAME > $BACKUP_HOME/supervisordb.dmp
	else
		echo "[WARN] database not found: $DATABASE_NAME"
	fi

	backup_folder $ARCVIDEO_HOME/supervisor $BACKUP_HOME/supervisor
	#backup_folder $ARCVIDEO_HOME/alert $BACKUP_HOME/alert
	#if [ "$SERVER_TYPE" != "all" ]; then
	#	backup_folder $ARCVIDEO_HOME/monitor $BACKUP_HOME/monitor
	#	if [ -d $ARCVIDEO_HOME/license ] ;then
	#		cp -rf $ARCVIDEO_HOME/license $BACKUP_HOME/
	#	fi
	#fi
fi
if [ "$SERVER_TYPE" == "agent" ] || [ "$SERVER_TYPE" == "all" ]; then
	backup_folder $ARCVIDEO_HOME/supervisor-agent $BACKUP_HOME/supervisor-agent
	#backup_folder $ARCVIDEO_HOME/logging $BACKUP_HOME/logging
	#backup_folder $ARCVIDEO_HOME/monitor $BACKUP_HOME/monitor
	#backup_folder $ARCVIDEO_HOME/fault $BACKUP_HOME/fault
	#backup_folder $ARCVIDEO_HOME/checkgpu $BACKUP_HOME/checkgpu
	#if [ -d $ARCVIDEO_HOME/license ] ;then
	#	cp -rf $ARCVIDEO_HOME/license $BACKUP_HOME/
	#fi
fi
if [ "$UPDATE_TRANSCODER" == "true" ] ;then
	rm -rf $ARCVIDEO_HOME/$TRANSCODER/core.*
	rm -rf $ARCVIDEO_HOME/$TRANSCODER/ASLOG-pid*
	backup_folder $ARCVIDEO_HOME/$TRANSCODER $BACKUP_HOME/transcoder-supervisor
fi
if [ -f "$TOMCAT_PACKAGE" ] ;then
	backup_folder $TOMCAT_HOME $BACKUP_HOME/tomcat
fi
if [ -f "$JRE_PACKAGE" ] ;then
	backup_folder /usr/local/jre $BACKUP_HOME/jre
fi

#
# Start upgrading
#
echo "[INFO] start upgrading ..."

if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "all" ]; then
	if [[ $IS_WOWZA_EXIST = 1 ]]; then
		echo "[INFO] setup wowza ..."
		if [ -f "$WOWZA_INSTALL_PATH/conf/Server.xml" ] ;then
		    echo "[INFO] start config Wowza."
		else
		    echo "[ERROR] can find wowza conf in default install path /usr/local/WowzaStreamingEngine. If you haven't install wowza, please run $FILENAME wowza first. If you install wowza in some where else, please set WOWZA_INSTALL_PATH to your actual install path." 
		    exit 1
		fi

		grep -q 'com.arcsoft.cloud.module.rts' $WOWZA_INSTALL_PATH/conf/Server.xml || sed -i "/<\/ServerListeners>/ i\
		\			<ServerListener> \\
						<BaseClass>com.wowza.wms.serverlistener.ServerListenerStartupStreamsMonitor</BaseClass> \\
					</ServerListener> \\
					<ServerListener> \\
						<BaseClass>com.arcsoft.cloud.module.rts.listener.WowzaServerListener</BaseClass> \\
		  		        </ServerListener>
		" $WOWZA_INSTALL_PATH/conf/Server.xml

		SERVER_PROPERTIES=$(($(grep -nr 'IServer.getProperties()' $WOWZA_INSTALL_PATH/conf/Server.xml | gawk '{print $1}' FS=":") + 2))

		echo "Server Properties line number $SERVER_PROPERTIES"

		grep -q 'startupStreamsMonitorApplicationList' $WOWZA_INSTALL_PATH/conf/Server.xml || sed -i "$SERVER_PROPERTIES i\
		\			<Property> \\
						<Name>startupStreamsMonitorApplicationList</Name> \\
						<Value>live</Value> \\
					</Property> \\
					<!-- Stream prefix. Valid values are: flv and mp4 \\
					<Property> \\
						<Name>startupStreamsMonitorStreamPrefix</Name> \\
						<Value>mp4</Value> \\
					</Property> \\
					--> \\
					<!-- MediaCaster type. Valid values are: rtp, rtp-record, shoutcast, shoutcast-record, liverepeater --> \\
					<Property> \\
						<Name>startupStreamsMonitorMediaCasterType</Name> \\
						<Value>rtp</Value> \\
					</Property> \\
					<!-- Pipe  (|) delimited list of file extensions for which to search --> \\
					<Property> \\
						<Name>startupStreamsMonitorExtensionFilter</Name> \\
						<Value>.stream|.sdp</Value> \\
					</Property> 
		" $WOWZA_INSTALL_PATH/conf/Server.xml

		sed -i "s|<StorageDir>\${com.wowza.wms.context.VHostConfigHome}/content</StorageDir>|<StorageDir>$WOWZA_STORAGEDIR</StorageDir>|" $WOWZA_INSTALL_PATH/conf/live/Application.xml

		APPLICATION_PROPERTIES=$(($(grep -nr 'IApplication.getProperties()' $WOWZA_INSTALL_PATH/conf/live/Application.xml | gawk '{print $1}' FS=":") + 2))

		echo "Application Properties line number $APPLICATION_PROPERTIES"

		grep -q 'startupStreamsMonitorMediaCasterType' $WOWZA_INSTALL_PATH/conf/live/Application.xml || sed -i "$APPLICATION_PROPERTIES i\
		\		<Property> \\
					<Name>startupStreamsMonitorMediaCasterType</Name> \\
					<Value>rtp</Value> \\
				</Property> \\
				<!-- Pipe delimited list of file extensions for which to search --> \\
				<Property> \\
					<Name>startupStreamsMonitorExtensionFilter</Name> \\
					<Value>.stream|.sdp</Value> \\
				</Property>
		" $WOWZA_INSTALL_PATH/conf/live/Application.xml	
		
		start_service WowzaStreamingEngine
		start_service WowzaStreamingEngineManager
	fi
fi
#
# upgrade tomcat
#
if [ -f "$TOMCAT_PACKAGE" ] ;then
	echo "[INFO] unzip $TOMCAT_PACKAGE..."
	tar -xzf $TOMCAT_PACKAGE -C $UNZIP_TMP

	echo "[INFO] install tomcat ..."
	mkdir -p $TOMCAT_HOME
	cp -rf $UNZIP_TMP/apache-tomcat*/* $TOMCAT_HOME

	# change tomcat port
	if [ -f $TOMCAT_HOME/conf/server.xml ] ;then
		echo "[INFO] change tomcat port to $TOMCAT_PORT ..."
		sed -i "s/port=\"8080\"/port=\"$TOMCAT_PORT\" URIEncoding=\"UTF-8\"/g" $TOMCAT_HOME/conf/server.xml
	fi

	# add java options
	if [ -f $TOMCAT_HOME/bin/catalina.sh ] ;then
		echo "[INFO] add JAVA_OPTS to catalina.sh ..."
		sed -i '/#!\/bin\/sh/a\JAVA_OPTS="-server -Xms256m -Xmx2048m -XX:PermSize=64m -XX:MaxPermSize=256m"' $TOMCAT_HOME/bin/catalina.sh
 	fi

 	# enable linking
 	if [ -f $TOMCAT_HOME/conf/context.xml ] ;then
 		echo "[INFO] add allowLinking=true to context.xml ..."
 		sed -i 's/<Context>/<Context allowLinking="true">/g' $TOMCAT_HOME/conf/context.xml
 	fi
	echo "[INFO] install tomcat DONE"
else
	echo "[WARN] tomcat install package is not exist."
fi

#
# upgrade JRE
#
if [ -f "$JRE_PACKAGE" ] ;then
	echo "[INFO] unzip $JRE_PACKAGE..."
	tar -xzf $JRE_PACKAGE -C $UNZIP_TMP

	echo "[INFO] install jre ..."
	mkdir -p /usr/local/jre
	if (ls $UNZIP_TMP/jdk*) >/dev/null 2>&1 ;then
		cp -rf $UNZIP_TMP/jdk*/* /usr/local/jre
	elif (ls $UNZIP_TMP/jre*) >/dev/null 2>&1 ;then
		cp -rf $UNZIP_TMP/jre*/* /usr/local/jre
	fi
	echo "[INFO] install jre DONE"
else
	echo "[WARN] jre install package is not exist."
fi


#
# Starting upgrade commander components
#
if [ -f "$COMMANDER_PACKAGE" ] ;then

	IS_RTMP_EXIST=0

	#
	# Unzip commander package if necessary.
	#
	if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "agent" ] || [ "$SERVER_TYPE" == "all" ]; then
		echo "[INFO] unzip $COMMANDER_PACKAGE ..."
		unzip -q $COMMANDER_PACKAGE -d $UNZIP_TMP/supervisor

		# Deploye srs rtmp if exist
		IS_RTMP_EXIST=`[ -f "$UNZIP_TMP/supervisor/build/rtmpserver/SRS-CentOS6-x86_64-2.0.185.zip" ] && echo 1 || echo 0`
		if [[ $IS_RTMP_EXIST -eq 1 ]]; then
			mkdir -p $UNZIP_TMP/supervisor/build/rtmpserver/srs
			unzip -q $UNZIP_TMP/supervisor/build/rtmpserver/SRS-CentOS6-x86_64-2.0.185.zip -d $UNZIP_TMP/supervisor/build/rtmpserver/srs
			$UNZIP_TMP/supervisor/build/rtmpserver/srs/SRS-CentOS6-x86_64-2.0.185/INSTALL
			/etc/init.d/srs restart
		fi

	fi

	if [ "$UPDATE_TRANSCODER" == "true" ] ;then
		TRANSCODER_PACKAGE=$UNZIP_TMP/supervisor/build/transcoder/transcoder-supervisor.zip
	fi

	#
	# upgrade transcoder
	#
	if [ -f "$TRANSCODER_PACKAGE" ] ;then
		echo "[INFO] unzip $TRANSCODER_PACKAGE..."
		unzip -q $TRANSCODER_PACKAGE -d $UNZIP_TMP/transcoder-supervisor

		echo "[INFO] install transcoder ..."
		mv $UNZIP_TMP/transcoder-supervisor/transcoder/transcoder $ARCVIDEO_HOME/$TRANSCODER

		# content detect
		CONTENT_DETECT_PACKAGE=$UNZIP_TMP/supervisor/build/content-detect/detect.zip
		if [[ -f "$CONTENT_DETECT_PACKAGE" ]]; then
			unzip -q $CONTENT_DETECT_PACKAGE -d $UNZIP_TMP/transcoder-supervisor
			mv $UNZIP_TMP/transcoder-supervisor/detect/* $ARCVIDEO_HOME/$TRANSCODER
		fi

		cd $ARCVIDEO_HOME/$TRANSCODER
		chmod +x *.sh *.so *.exe VMFRegister
		./register.sh
		cd $CURDIR
		echo "[INFO] install transcoder DONE"
	else
		echo "[WARN] transcoder install package is not exist."
	fi

	#
	# upgrade agent components
	#
	if [ "$SERVER_TYPE" == "agent" ]  || [ "$SERVER_TYPE" == "all" ] ;then

		#
		# upgrade agent
		#
		echo "[INFO] install agent ..."
		unzip -q $UNZIP_TMP/supervisor/build/agent/agent.zip -d $ARCVIDEO_HOME/supervisor-agent
		if (ls $BACKUP_HOME/supervisor-agent/data/*) >/dev/null 2>&1 ;then
			echo "[INFO] copy agent data ..."
			cp -rf $BACKUP_HOME/supervisor-agent/data/* $ARCVIDEO_HOME/supervisor-agent/data/
		fi
		setup_service $ARCVIDEO_HOME/supervisor-agent/bin/supervisorAgentd supervisorAgentd
		echo "[INFO] install agent DONE"

		# remove startup agent script from startserver.sh
		if [ -f $ARCVIDEO_HOME/startup/startserver.sh ] ;then
			echo "[INFO] disable agent startup in startserver.sh ..."
			sed -i 's|.*$ARCVIDEO_HOME/supervisor-agent/bin/startup.sh.*||g' $ARCVIDEO_HOME/startup/startserver.sh
		fi

		# remove startup agent script from rc.local
		if [ -f /etc/rc.local ] ;then
			echo "[INFO] disable agent startup in rc.local ..."
			sed -i 's|.*$ARCVIDEO_HOME/agent/bin/startup.sh.*||g' /etc/rc.local
		fi

		# modify agent.properties
		AGENT_CONFIG_FILE=$ARCVIDEO_HOME/supervisor-agent/conf/agent.properties
		if [ -f $AGENT_CONFIG_FILE ] ;then
			sed -i "s/cluster\.ip=239.95.96.99/cluster\.ip=$CLUSTER_IP/g" $AGENT_CONFIG_FILE
			sed -i "s/cluster\.port=8920/cluster\.port=$CLUSTER_PORT/g" $AGENT_CONFIG_FILE
			sed -i "s/cluster\.bind=172.17.193.158/cluster\.bind=$BINDIP/g" $AGENT_CONFIG_FILE
			if [ $AGENT_PORT -ne 5000 ]; then
				echo "[INFO] set agent port to $AGENT_PORT"
				sed -i "s/#server\.port=5000/server\.port=$AGENT_PORT/g" $AGENT_CONFIG_FILE
			fi
			if [ $AGENT_COMMAND_PORT -ne 5001 ]; then
				echo "[INFO] set agent command port to $AGENT_COMMAND_PORT"
				sed -i "s/#command\.port=5001/command\.port=$AGENT_COMMAND_PORT/g" $AGENT_CONFIG_FILE
			fi
			echo "[INOF] cluster=$CLUSTER_IP:$CLUSTER_PORT, bind ip=$BINDIP:$AGENT_PORT"
		else
			echo "[ERROR] agent configuration file not found: $AGENT_CONFIG_FILE"
		fi

		# start after install
		if [ "$START_AFTER_UPGRADE" == "true" ]; then
			start_service supervisorAgentd
		fi

		#
		# disable tomcat startup script
		#
		if [ "$SERVER_TYPE" == "agent" ] && [ -f $ARCVIDEO_HOME/startup/startserver.sh ] ;then
			echo "[INFO] disable tomcat startup script in startserver.sh ..."
			comment_lines "$TOMCAT_HOME/bin/startup.sh" $ARCVIDEO_HOME/startup/startserver.sh
		fi
	fi

	#
	# install supervisor commander components
	#
	if [ "$SERVER_TYPE" == "commander" ] || [ "$SERVER_TYPE" == "all" ] ;then

		#
		# initialize database
		#
		if [ "$EXIST_DATABASE" != "true" ]; then
			DATABASE_FILE=$UNZIP_TMP/supervisor/build/db/supervisordb.sql
			echo "[INFO] initialize database $DATABASE_NAME ..."
			if [ "$DATABASE_NAME" != "supervisordb" ]; then
				sed -i "s|supervisordb|$DATABASE_NAME|g" $DATABASE_FILE
			fi

			# Change variables if use rtmp as rtsp server
			if [[ $IS_RTMP_EXIST -eq 1 ]]; then
				# Change dir to / because rtmp don't need storage dir
				WOWZA_STORAGEDIR="/"
				RTSP_URL="rtmp://$RTSP_IP:1935/live/"
			fi

			#
			# add system settings
			#
			SYSTEM_UUID=`dmidecode -s system-uuid`
			echo "" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('alert.auto-delete.days', '0');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.type', '0');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.bindAddr_$SYSTEM_UUID', '$BINDIP');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.ip', '$CLUSTER_IP');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.port', '$CLUSTER_PORT');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.heartbeat.interval', '100');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.heartbeat.timeout', '2000');" >> $DATABASE_FILE
			echo "insert into settings(\`key\`, \`value\`) values('cluster.ttl', '10');" >> $DATABASE_FILE
			echo "insert into configuration(\`type\`, \`id\`) values('2', 1);" >> $DATABASE_FILE
			echo "insert into configuration_rtsp(\`publishFolderPath\`, \`publishUrl\`, \`ip\`, \`publishUrlOfInternet\`, \`id\`) values('$WOWZA_STORAGEDIR', '$RTSP_URL', '$RTSP_IP', '', 1);" >> $DATABASE_FILE
			echo "insert into configuration_rtsp(\`publishFolderPath\`, \`mixedPublishUrl\`, \`ip\`, \`id\`) values('$WOWZA_STORAGEDIR', '$RTSP_URL', '$RTSP_IP', 1);" >> $DATABASE_FILE
			mysql -uroot -p$DATABASE_PWD -f < $DATABASE_FILE
			echo "[INFO] initialize database DONE"
		else
			echo "[INFO] upgrade databse $DATABASE_NAME ..."
			cd $UNZIP_TMP/supervisor/build/db
			chmod +x *
			# Execute script for upgrade db if exists
			if [[ -f "upgrade_db.sh" ]]; then
				./upgrade_db.sh
			fi
			cd $CURDIR

		fi

		#
		# install commander
		#
		echo "[INFO] install commander ..."
		unzip -q $UNZIP_TMP/supervisor/build/web/supervisor-web.war -d $ARCVIDEO_HOME/supervisor
		cp -rf $BACKUP_HOME/supervisor/WEB-INF/template/ $ARCVIDEO_HOME/supervisor/WEB-INF/
		cp -rf $BACKUP_HOME/supervisor/WEB-INF/sdp/ $ARCVIDEO_HOME/supervisor/WEB-INF/
		cp -rf $BACKUP_HOME/supervisor/WEB-INF/sdptemp/ $ARCVIDEO_HOME/supervisor/WEB-INF/


		# update database configuration if database name is not commanderdb
		if [ "$DATABASE_NAME" != "supervisordb" ]; then
			echo "[INFO] update commander database name $DATABASE_NAME."
			DATABASE_CONFIG_FILE=$ARCVIDEO_HOME/supervisor/WEB-INF/classes/database.properties
			if [ -f $DATABASE_CONFIG_FILE ]; then
				sed -i "s|supervisordb|$DATABASE_NAME|g" $DATABASE_CONFIG_FILE
				echo "[INFO] database name updated to $DATABASE_NAME"
			else
				echo "[INFO] database configuration file not found: $DATABASE_CONFIG_FILE"
			fi
		fi

		# TRANSCODER_CONFIG_FILE=$ARCVIDEO_HOME/commander/WEB-INF/classes/config.properties
		# if [ -f $TRANSCODER_CONFIG_FILE ]; then
		# 	if [ "$TRANSCODER" != "transcoder" ] ;then
		# 		echo "[INFO] set transcoder path: $ARCVIDEO_HOME/$TRANSCODER/"
		# 		sed -i "s|/usr/local/arcsoft/arcvideo/transcoder/|$ARCVIDEO_HOME/$TRANSCODER/|g" $TRANSCODER_CONFIG_FILE
		# 	fi
		# 	if [ "$TRANSCODER_WORKDIR" != "tmpdir" ]; then
		# 		echo "[INFO] set transcoder work dir: $ARCVIDEO_HOME/$TRANSCODER_WORKDIR/"
		# 		sed -i "s|/usr/local/arcsoft/arcvideo/tmpdir|$ARCVIDEO_HOME/$TRANSCODER_WORKDIR|g" $TRANSCODER_CONFIG_FILE
		# 	fi
		# else
		# 	echo "[ERROR] transcoder configuration file not found: $TRANSCODER_CONFIG_FILE"
		# fi

		echo "[INFO] deploy commander to tomcat ..."
		mkdir -p $TOMCAT_HOME/conf/Catalina/localhost/
		ROOT_XML=$TOMCAT_HOME/conf/Catalina/localhost/ROOT.xml
		echo '<?xml version="1.0" encoding="utf-8"?>' > $ROOT_XML
		echo '' >> $ROOT_XML
		echo '<Context docBase="/usr/local/arcsoft/arcvideo/supervisor" reloadable="false">' >> $ROOT_XML
		echo '</Context>' >> $ROOT_XML
		echo "[INFO] install commander DONE"

		#
		# start tomcat
		#
		if [ "$START_AFTER_UPGRADE" == "true" ]; then
			echo "[INFO] start tomcat ..."
			$TOMCAT_HOME/bin/startup.sh >/dev/null 2>&1
		fi
	fi

	
fi

#
# disable vnc service
#
if [ "$DISABLE_VNC" == "true" ]; then
	disable_service vncserver-x11-serviced
fi

#
# disable smb service
#
if [ "$DISABLE_SMB" == "true" ]; then
	disable_service smb
	if [ -f $ARCVIDEO_HOME/startup/startserver.sh ] ;then
		echo "[INFO] disable smb in startserver.sh ..."
		comment_lines "service\s\+smb\s\+start" $ARCVIDEO_HOME/startup/startserver.sh
	fi
fi

#
# clean data
#
echo "[INFO] clean ..."
rm -rf $UNZIP_TMP

echo "[INFO] upgrade finished."
exit 0
