#!/bin/bash
#
# init file for StreamTerminator (Startup script for an LSB service)
# *******************************************************************************
# * COPYRIGHT Ericsson 2013
# *
# * The copyright to the computer program(s) herein is the property of
# * Ericsson Inc. The programs may be used and/or copied only with written
# * permission from Ericsson Inc. or in accordance with the terms and
# * conditions stipulated in the agreement/contract under which the
# * program(s) have been supplied.
# *******************************************************************************
#
# chkconfig: 2345 85 15
# description: Init script for StreamTerminator
# config: pmstream.conf
# pidfile: /var/run/pmstream.pid

#
# In this section, update required-start and required-stop for any lsb services required before
# this service starts or stops
### BEGIN INIT INFO
# Provides: pmstream
# Required-Start: $local_fs $remote_fs $network $named
# Required-Stop: $local_fs $remote_fs $network
# Should-Start: distcache
# Short-Description: pmstream stream terminator
# Description: pmstream multiplexing stream termination service (based on Netty 4)
#
### END INIT INFO

# Source function library.
. /etc/rc.d/init.d/functions

STREAM_TERMINATOR=/opt/ericsson/pmstream/terminator-standalone/start.sh
# This will prevent initlog from swallowing up a pass-phrase prompt if
# mod_ssl needs a pass-phrase from the user.
INITLOG_ARGS=""
# paths to binary and lock/pid files
myapp=$STREAM_TERMINATOR
conf_file=/etc/ericsson/config/pmstream/pmstream.conf
prog="pmstream"
pidfile=${PIDFILE-/var/run/pmstream.pid}
log_conf_file="/etc/ericsson/config/pmstream/logback.xml"
default_log_dir="/var/ericsson/log/${prog}"
SCRIPT_NAME=`basename $0`

# Rsyslogger commands
info() {
  logger -s -t PMStream -p user.info "INFORMATION ( ${SCRIPT_NAME} ): $@"
}

error() {
  logger -s -t PMStream -p user.err "ERROR ( ${SCRIPT_NAME} ): $@"
}

warn() {
  logger -s -t PMStream -p user.err "WARNING ( ${SCRIPT_NAME} ): $@"
}

declare -i RETVAL=0
declare -i JMX_PORT
base=${1##*/}

# Init script helper functions
function get_free_jmx_port() {
  while true; do
    sleep 0.1
    local output=
    output=`netstat -tnl |grep ":$JMX_PORT "`
    if [ -z "$output" ]; then
      break
    fi
    (( JMX_PORT+=1 ))
  done
}

status() {
  local deadInstanceCount=0
  local instanceCount=0
  pidsUp=
  jmxPorts=
  RETVAL=0
  if [ -f $pidfile ]; then
    while read line; do
      (( instanceCount+=1 ))
      ps -p $line  > /dev/null 2>&1
      if [ "$?" -ne "0" ]; then
        (( deadInstanceCount+=1 ))
        info "Checking status: "
        warning; error "Instance with PID $line is not running"
        RETVAL=1
      else
        pidsUp+=" $line"
        str=$(ps -efl|grep $line)
        str=${str#*-Dcom.sun.management.jmxremote.port=}
        str=${str%% *}
        jmxPorts+=" $str"
      fi
    done < $pidfile
    if [[ $RETVAL > 0 ]]; then
      if [[ -n "$pidsUp" ]]; then
        info "Checking status: "
        info "PIDs running: $pidsUp"
        info "JMX ports used: $jmxPorts"
        error "There are $deadInstanceCount of $instanceCount instances down"
        failure
        echo
      else
        failure; error "All instances down"
      fi
      return $RETVAL
    fi
  else
    failure; error "$prog is stopped"
    RETVAL=1
    return $RETVAL
  fi
  success; info "Checking status: "
  info "JMX ports used: $jmxPorts"
  info "PIDs running: $pidsUp"
  info "$prog is running (instances: $instanceCount)"
  RETVAL=0
  return $RETVAL
}


#
# Attempting to start while running is a failure.
# To work with the LITP failover:
# If a pid file exists and the service is not running then it assumed that the service
# was not stopped correctly and it will be restarted.
#
start() {
  info "Starting $prog: "
  if [ -f $pidfile ]; then
    warn "$pidfile already exists"
    warning;
    echo
    status
    if [ "$RETVAL" -ne "0" ]; then
      RETVAL=0 # Reset the status to 0 because we are restarting
      stop # and continue to start (that is restart)
    else
      RETVAL=1
      error "Service ${prog} is already running."
      failure;
      echo
      return $RETVAL
    fi
  fi

  if [ ! -f $( readlink -f $conf_file ) ]; then
    error "Error: No configuration - $conf_file does not exist."
    failure
    echo
    RETVAL=1
    return $RETVAL
  fi

  empty_lines=0
  JMX_PORT=20101

  while read LINE; do

    if [[ $LINE == \#* || -z $LINE ]]; then
      empty_lines=$((empty_lines + 1))
      continue
    fi

    get_free_jmx_port
    if [ "$?" -ne "0" ]; then
      failure; error "JMX Port $JMX_PORT already in use"
      RETVAL=1
      return $RETVAL
    fi

    # Try to extract the log directory from the config LINE, otherwise use the default
    LOG_DIR=
    LOG_DIR=${LINE##*-logDir=}
    LOG_DIR=${LOG_DIR%%[[:space:]]*}
    LOG_DIR=${LOG_DIR%%/} # remove trailing / if present
    LOG_DIR=${LOG_DIR:-${default_log_dir}} # fall back to default if not defined
    mkdir -p $LOG_DIR
    info "Starting instance (see start up log: ${LOG_DIR:-.}/start.log) ..."

    # Actually starting the application
    $myapp --jmxport=$JMX_PORT $LINE -logConfigFile=$log_conf_file > ${LOG_DIR:-.}/start.log 2>&1 < /dev/null &
    sleep 3 # give time to app to start before reporting status
    RETVAL=$?
    java_pid=$!

    if [ "$RETVAL" -eq "0" ]; then
      if ps -p $java_pid  > /dev/null 2>&1; then # still running
        echo $java_pid >> $pidfile
      else
        error "Instance not running. Not writing $java_pid to pidfile" >&2
        error "STOPPING ALL INSTANCES"
        stop
        failure; error "All instances stopped" >&2
        echo
        return 1
      fi
    else
      error "Instance with configuration $LINE not started"  >&2
      failure
      echo
      return $RETVAL
    fi
    (( JMX_PORT+=1 ))
  done < $conf_file

  if [ "$empty_lines" -ne "0" ]; then
    warn $LINE " Skipping $empty_lines comments or blank lines."
    warning
    echo
  fi

  success; info "Started successfully"
  echo
  return $RETVAL
}

# start/stop routines for applications

stop() {
  info $"Stopping $prog: "
  status
  if [ "$RETVAL" -ne "0" ]; then
    if [ -f $pidfile ]; then
      while read line; do
        kill -9 $line > /dev/null 2>&1
        if [ "$?" -ne "0" ]; then
          ps -p $line > /dev/null 2>&1
          if [ "$?" -eq "0" ]; then
            error "Could not stop process with PID $line"
            failure
            echo
            RETVAL=1
            return $RETVAL
          fi
        fi
      done < $pidfile
    else
      failure; error "$prog is already stopped"
      echo
      return $RETVAL
    fi
  else
    while read line; do
      kill -9 $line > /dev/null 2>&1
      if [ "$?" -ne "0" ]; then
        failure; error "Could not stop process with PID $line"
        echo
        RETVAL=1
        return $RETVAL
      fi
    done < $pidfile
  fi

  rm -f $pidfile
  RETVAL=$?
  if [ "$RETVAL" -ne "0" ]; then
    failure; error "Error: could not delete PID file $pidfile"
  else
    success; info "$prog stopped"
  fi
  echo
  return $RETVAL
}

case "$1" in
  start)
    start
    RETVAL=$?
  ;;
  stop)
    stop
    RETVAL=$?
  ;;
  restart)
    stop
    start
    RETVAL=$?
  ;;
  force-reload)
    (stop; start)
    RETVAL=$?
  ;;
  status)
    status
    RETVAL=$?
  ;;
  *)
    echo $"Usage: $0 {start|stop|restart|status|force-reload}"
    RETVAL=2
    [ "$1" = 'usage' ] && RETVAL=0
esac

exit $RETVAL

