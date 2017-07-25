#!/bin/sh

###############################################################################
# Main Script for the Agent Server
###############################################################################

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Set AGENT_HOME if not set
[ -z "$AGENT_HOME" ] && AGENT_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

#
# Setup Java path
#
# Make sure prerequisite environment variables are set
if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
  JAVA_PATH=`which java 2>/dev/null`
  if [ "x$JAVA_PATH" != "x" ]; then
    JAVA_PATH=`dirname $JAVA_PATH 2>/dev/null`
    JRE_HOME=`dirname $JAVA_PATH 2>/dev/null`
  fi
  if [ "x$JRE_HOME" = "x" ]; then
    # XXX: Should we try other locations?
    if [ -x /usr/bin/java ]; then
      JRE_HOME=/usr
    fi
  fi
  if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
    echo "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined"
    echo "At least one of these environment variable is needed to run this program"
    exit 1
  fi
fi
if [ -z "$JRE_HOME" ]; then
  JRE_HOME="$JAVA_HOME"
fi

#
# Set standard commands for invoking Java.
#
_RUNJAVA=$JRE_HOME/bin/java

#
# Set classpath.
#
CLASSPATH=
for file in "$AGENT_HOME/lib"/*; do
    CLASSPATH=$CLASSPATH:"$file"
done

#
# Configurations
#
AGENT_CONFIG="$AGENT_HOME/conf/agent.properties"
LOG4J_CONFIG="$AGENT_HOME/conf/log4j.properties"
BOOT_LOGFILE="$AGENT_HOME/logs/boot.log"

#
# Execute agent command.
#
if [ "$1" = "start" ]; then
	eval exec \"$_RUNJAVA\" \
		-cp \"$CLASSPATH\" \
		-Duser.dir=\"$AGENT_HOME/bin\" \
		-Dagent.home=\"$AGENT_HOME\" \
		-Dagent.log4j=\"$LOG4J_CONFIG\" \
		-Dagent.config=\"$AGENT_CONFIG\" \
		com.arcsoft.supervisor.agent.Application \
		&> "$BOOT_LOGFILE" "&"
else
	eval exec \"$_RUNJAVA\" \
		-cp \"$CLASSPATH\" \
		-Duser.dir=\"$AGENT_HOME/bin\" \
		-Dagent.home=\"$AGENT_HOME\" \
		-Dagent.log4j=\"$LOG4J_CONFIG\" \
		-Dagent.config=\"$AGENT_CONFIG\" \
		com.arcsoft.supervisor.agent.Application "$@"
fi
