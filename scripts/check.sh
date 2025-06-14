#!/bin/bash
set -e

export MYDIR=`dirname $0`
. ./$MYDIR/setup.sh

echo $CLASSPATH

DEBUG=""
CHECKER="universe.UniverseChecker"

POSITIONAL=()
while [[ $# -gt 0 ]]; do
  key="$1"

  case $key in
    -d|--debug)
      ipaddr="$2"
      DEBUG="-J-Xdebug -J-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=${ipaddr}"
      shift # past argument
      shift # past value
      ;;
    *)    # unknown option
      POSITIONAL+=("$1") # save it in an array for later
      shift # past argument
      ;;
  esac
done

set -- "${POSITIONAL[@]}"

cmd="javac "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED "
cmd+="-J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED "
cmd+="-J--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED "

if [ "$DEBUG" == "" ]; then
	cmd+="-cp "${CLASSPATH}" -processor "${CHECKER}" "$@""
else
	cmd+=$DEBUG" -cp "${CLASSPATH}" -processor "${CHECKER}" -AatfDoNotCache "$@""
fi

eval "javac -version"
echo "$cmd"
eval "$cmd"
