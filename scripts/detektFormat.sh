#!/bin/bash

# Fetch changed and untracked Kotlin files
CHANGED_FILES=$(git diff --name-only HEAD | grep '\.kt[s]*$' | tr '\n' ',')

if [ -z "$CHANGED_FILES" ]; then
    echo "No Kotlin files have changed."
    exit 0
fi

# Run detektFormat only on those files
./gradlew detektFormat -Ddetekt.filesToCheck=$CHANGED_FILES
