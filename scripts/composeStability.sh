#!/bin/bash

# -------------------
# 0. ROOT DIRECTORY CHECK
# -------------------

# Check if the script is being run from the root directory of the repo
if [ ! -f "settings.gradle.kts" ]; then
    echo "ERROR:"
    echo "Please run this script from the root directory of the repo."
    exit 1
fi

./gradlew assembleDemo -PcomposeCompilerReports=true || exit 1
./gradlew :ci-actions:compose-stability:run  || exit 1