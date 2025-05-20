#!/bin/sh

ulimit -n unlimited

XDAG_VERSION="${project.version}"
XDAG_JARNAME="xdagj-${XDAG_VERSION}-executable.jar"
XDAG_OPTS="-t"

# Linux Java Home
#JAVA_HOME="/usr/local/java/"

# MacOS Java Home
#JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home/

# default JVM options
JAVA_OPTS="--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Xms4g -Xmx4g -XX:+ExitOnOutOfMemoryError -XX:+UseZGC"

JAVA_HEAPDUMP="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/xdag-heapdump"

JAVA_GC_LOG="-Xlog:gc*,gc+heap=trace,gc+age=trace,safepoint:file=./logs/xdag-gc-%t.log:time,level,tid,tags:filecount=8,filesize=10m"

XDAGJ_VERSION="-Dxdagj.version=${XDAG_VERSION}"

if [ ! -d "logs" ];then
  mkdir "logs"
fi

# start kernel
java ${JAVA_OPTS} ${JAVA_HEAPDUMP} ${JAVA_GC_LOG} ${XDAGJ_VERSION} -cp .:${XDAG_JARNAME} io.xdag.Bootstrap "$@"