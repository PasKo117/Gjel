#!/bin/sh
# Gradle Wrapper startup script (minimal POSIX version)
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
MAX_FD=maximum
warn () { echo "$*"; }
die () { echo; echo "$APP_NAME: $*"; echo; exit 1; }

# resolve app home
CLASSPATH=""
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null
APP_HOME=$(cd "$(dirname "$0")" && pwd)
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -z "$JAVA_HOME" ] ; then
  JAVACMD="java"
  which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
  "-Dorg.gradle.appname=$APP_BASE_NAME" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
