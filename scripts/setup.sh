#!/bin/bash
export MYDIR=`dirname $0`

# Universe top level directory
export UNIVERSE=$MYDIR/../

# Dependencies
export CLASSPATH=./build/classes/java/main:./build/libs/checker-3.42.0-eisop3.jar:./build/libs/checker-qual-3.42.0-eisop3.jar:./build/libs/checker-util-3.42.0-eisop3.jar:./build/libs/universe.jar:./build/resources/main:$CLASSPATH
