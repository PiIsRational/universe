#!/bin/bash
export MYDIR=`dirname $0`

# Universe top level directory
export UNIVERSE=$MYDIR/../

# CheckerFramework Inference
export CFI="$MYDIR"/../../checker-framework-inference

# Dependencies
export CLASSPATH=$CFI/dist/commons-logging-1.2.jar:$CFI/dist/log4j-1.2.16.jar:./build/libs/universe.jar:$CLASSPATH
