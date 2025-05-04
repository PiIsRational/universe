#!/bin/bash

echo Entering "$(cd "$(dirname "$0")" && pwd -P)/$(basename "$0")" in `pwd`

# Fail the whole script if any command fails
set -e

export SHELLOPTS

. ./.ci-build-without-test.sh

# the tests are included in the check step
./gradlew check
