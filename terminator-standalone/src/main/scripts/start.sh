#!/bin/bash
# echo "StreamTerminator start script: pid=$$"


function show_help () {
  printf "usage:\t%s  [-Xmx=MAXIMUM_HEAP[g|m]] [-Xmn=YOUNG_HEAP[g|m]] [-logDir=LOG_DIR] [-configDir=CONFIG_DIR] [-iniFile=path/to/file.ini] [--jmxport=JMX_PORT|-p JMX_PORT] \n" ${0##*/}
  printf "\tThese arguments *must* appear before any other arguments or they may not be parsed.\n"
  printf "\tParsing ends after '--' or the first argument not beginning with '-'. \n"
} >&2


#
# Script options from command line arguments.
#
JMX_PORT=
MAXIMUM_HEAP=
YOUNG_HEAP=
LOG_DIR=
HOSTNAME=
OTHER_PARAMS=
INI_FILE=
DEFAULT_MAX_HEAP=
DEFAULT_YOUNG_HEAP=
while :
do
  case $1 in
    -h | --help | -\?)
      show_help
      exit 0
    ;;
    -p | --jmxport)
      JMX_PORT=$2
      shift 2
    ;;
    --jmxport=*)
      JMX_PORT=${1#*=}
      shift
    ;;
    -hostname=*)
      HOSTNAME=${1#*=}
      shift
    ;;
    -Xmx=*)
      MAXIMUM_HEAP=${1#*=}
      shift
    ;;
    -Xmn=*)
      YOUNG_HEAP=${1#*=}
      shift
    ;;
    -logDir=*)
      LOG_DIR=${1#*=}
      shift
    ;;
    -configDir=*)
      CONFIG_DIR=${1#*=}
      shift
    ;;
    -logConfigFile=*)
      LOG_CONFIG_FILE=${1#*=}
      shift
    ;;
    -iniFile=*)
      INI_FILE=${1#*=}
      shift
    ;;
    --) # End of all options
      shift
      break
    ;;
    -*)
      #echo "WARN: Unknown option: $1" >&2
      OTHER_PARAMS+=" ${1}"
      shift
    ;;
    *)  # no more options. Stop while loop
      break
    ;;
  esac
done
#echo "Passing the following parameters on to JVM: \$@= $@ "
#echo "OTHER_PARAMS = $OTHER_PARAMS"

#echo "Maximum heap size = $MAXIMUM_HEAP"
#echo "Young generation heap size = $YOUNG_HEAP"

#
# Logging and configuration directories
#
LOG_ARGS=
if [ -n "$LOG_DIR" ]; then
  LOG_DIR=${LOG_DIR%%/} # remove trailing / if present
  LOG_ARGS+=" -Dcom.ericsson.oss.mediation.streaming.log.dir=$LOG_DIR"
  mkdir -p $LOG_DIR
fi
if [ -n "$LOG_CONFIG_FILE" ]; then
  LOG_ARGS+=" -Dlogback.configurationFile=$LOG_CONFIG_FILE"
fi
#LOG_ARGS+=" -Dlogback_root_level=TRACE"

CONFIG_DIR_ARG=
if [ -n "$CONFIG_DIR" ]; then
  if [ ! -d "$CONFIG_DIR" ]; then
    echo "ERROR: configuration directory $CONFIG_DIR does not exist"
    exit 1
  fi
  CONFIG_DIR_ARG="-Dcom.ericsson.oss.mediation.streaming.config.dir=$CONFIG_DIR"
fi
# echo "Logging config = $LOG_ARGS"
# echo "Configuration directory config = $CONFIG_DIR_ARG"


memory_prop=
#INI file parsing. Lines are parsed according to their prefix (one of mem, jvm or arg)
#Conventions is:
#   mem.<jvm_memory_property> -  these will be parsed first of all and are checked against defaults if they are not set in either conf or ini file
#   jvm.<jvm_arg>  - any argument for the JVM that is NOT memory related. E.g jvm.XXXX:+UseParallelOldGC. Will be parsed right after mem properties
#   arg.<property> - any -D property e.g. arg.Dio.netty.noResourceLeakDetection=true will be parsed last
#The structure should be the same as passed through the command line, without the leading hyphen
if [[ -n "$INI_FILE" ]]; then
  if [ ! -f "$INI_FILE" ]; then
    echo "ERROR: ini file $INI_FILE does not exist"
    exit 1
  fi
  total_lines=0
  empty_lines=0
  unkno_lines=0
  while read LINE; do
    total_lines=$((total_lines + 1))
    if [[ $LINE == \#* || -z $LINE ]]; then
      empty_lines=$((empty_lines + 1))
      continue
    fi
    LINE=${LINE%\#*}
    case "$LINE" in
      mem\.*)
        LINE=${LINE#mem.*}
        if [[ $LINE == Xmx* ]]; then
          DEFAULT_MAX_HEAP="-$LINE "
        elif [[ $LINE == Xmn* ]]; then
          DEFAULT_YOUNG_HEAP="-$LINE "
        fi
      ;;
      jvm\.*)
        LINE=${LINE#jvm.*}
        jvm_prop+="-${LINE%\#*} "
      ;;
      arg\.*)
        LINE=${LINE#arg.*}
        OTHER_PARAMS+="-${LINE%\#*} "
      ;;
      *)
        unkno_lines=$((unkno_lines + 1))
      ;;
    esac

  done < $INI_FILE
  echo -e "JVM Properties read from $INI_FILE.
  $empty_lines empty/commented line(s) skipped and $unkno_lines unrecognized line(s) skipped out of $total_lines total lines"
fi


jvm_prop+=" -server"
jvm_prop+=" -Xloggc:${LOG_DIR:-.}/gc.log"  # Logs gc details (Overrides -verbose:gc).

DEFAULT_MAX_HEAP=${DEFAULT_MAX_HEAP:="-Xmx2g"}  # Try to set defaults via INI file; if not present hard code defaults
DEFAULT_YOUNG_HEAP=${DEFAULT_YOUNG_HEAP:="-Xmn512m"}

MAXIMUM_HEAP=${MAXIMUM_HEAP:=$DEFAULT_MAX_HEAP}  # Try to use values from CONF file; if not present use values from default
YOUNG_HEAP=${YOUNG_HEAP:=$DEFAULT_YOUNG_HEAP}
INITIAL_HEAP=${MAXIMUM_HEAP/Xmx/Xms}

if [[ $MAXIMUM_HEAP != -Xmx* ]]; then MAXIMUM_HEAP="-Xmx$MAXIMUM_HEAP"; fi #Sanity check on -Xmx prefix and trailing space
if [[ $YOUNG_HEAP != -Xmn* ]]; then YOUNG_HEAP="-Xmn$YOUNG_HEAP"; fi       #Sanity check on -Xmn prefix and trailing space
if [[ $INITIAL_HEAP != -Xms* ]]; then INITIAL_HEAP="-Xms$INITIAL_HEAP"; fi #Sanity check on -Xms prefix and trailing space

memory_prop+=" $MAXIMUM_HEAP"  # set maximum Java heap size
memory_prop+=" $INITIAL_HEAP"  # set initial Java heap size
memory_prop+=" $YOUNG_HEAP"    # the size of the heap for the young generation


#
# JMX settings, only used if JMX_PORT is set
#
jmx_prop=
if [ -n "$JMX_PORT" ]; then
  jmx_prop+=" -Dcom.sun.management.jmxremote"
  jmx_prop+=" -Dcom.sun.management.jmxremote.port=${JMX_PORT}"
  jmx_prop+=" -Dcom.sun.management.jmxremote.authenticate=false"
  jmx_prop+=" -Dcom.sun.management.jmxremote.ssl=false"
  if [ -n "$HOSTNAME" ]; then
    jmx_prop+=" -Djava.rmi.server.hostname=${HOSTNAME}"
  fi
fi


# Information/debug options
debug_prop=" -XX:+PrintCommandLineFlags" # Print flags that appeared on the command line.
#
# Other debugging Options (only used if DEBUG is TRUE)
#
DEBUG=false
if [[ $DEBUG == true ]]; then
  #debug_prop+=" -XX:-CITime"  # Prints time spent in JIT Compiler.
  #debug_prop+=" -XX:ErrorFile=./hs_err_pid<pid>.log"     # If an error occurs, save the error data to this file.
  #debug_prop+=" -XX:OnError="<cmd args>;<cmdargs>"       # Run user-defined commands on fatal error. (Introduced in 1.4.2 update 9.)
  #debug_prop+=" -XX:OnOutOfMemoryError="<cmdargs>;<cmd args>"" #Run user-defined commands when an OutOfMemoryError is first thrown.
  debug_prop+=" -XX:+PrintClassHistogram"    #Print a histogram of class instances on Ctrl-Break. Manageable. The jmap -histo command provides equivalent functionality.
  debug_prop+=" -XX:+PrintConcurrentLocks"   # Print java.util.concurrent locks in Ctrl-Break thread dump. Manageable. The jstack -l command provides equivalent functionality.
  debug_prop+=" -XX:+PrintCommandLineFlags"  # Print flags that appeared on the command line.
  debug_prop+=" -XX:+PrintCompilation"       # Print message when a method is compiled.
  debug_prop+=" -XX:+PrintGC"                # Print messages at garbage collection. Manageable.
  debug_prop+=" -XX:+PrintGCDetails"         # Print more details at garbage collection. Manageable.
  debug_prop+=" -XX:+PrintGCTimeStamps"      # Print timestamps at garbage collection. Manageable.
  debug_prop+=" -XX:+PrintTenuringDistribution"  # Print tenuring age information.
  debug_prop+=" -XX:+TraceClassLoading"          # Trace loading of classes.
  debug_prop+=" -XX:+TraceClassLoadingPreorder"  # Trace all classes loaded in the order referenced (not loaded).
  debug_prop+=" -XX:+TraceClassResolution"       # Trace constant pool resolutions.
  debug_prop+=" -XX:+TraceClassUnloading"        # Trace unloading of classes.
  debug_prop+=" -XX:+TraceLoaderConstraints"     # Trace recording of loader constraints.
  debug_prop+=" -XX:+PerfSaveDataToFile"         # Saves jvmstat binary data on exit.
  debug_prop+=" -Xdebug -Xnoagent"               # Enables debugging support in the VM.
  #debug_prop+=" -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=<port>"  # Loads in-process debugging libraries and specifies what kind of connection will be made.
  debug_prop+=" -XX:+PrintConcurrentLocks"          # Print java.util.concurrent locks in Ctrl-Break thread dump.
fi


#
# Application/Component configuration
#
PMSTREAM_HOME=${PMSTREAM_HOME:-"$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"}
netty_jar="${PMSTREAM_HOME}/terminator-standalone.jar"
JRE="${PMSTREAM_HOME}/jre/bin/java"

#
# Linux configuration
#
ulimit -n 300000


echo "Executing netty terminator ..."
echo "------------------------------------------------------------------------"
JAVA_CMD="${JRE} $memory_prop ${jvm_prop} ${jmx_prop} ${debug_prop} ${LOG_ARGS} ${CONFIG_DIR_ARG} ${OTHER_PARAMS} $@ -jar ${netty_jar}"
echo ${JAVA_CMD}
echo "------------------------------------------------------------------------"

exec ${JAVA_CMD}

