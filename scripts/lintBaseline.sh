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

rm app/lint-baseline.xml
echo "Old lint-baseline.xml removed."
./gradlew lintR
echo "Lint baseline generated. Testing correctness.."
./gradlew lintR || exit
git add app/lint-baseline.xml
git commit -m "Update Lint baseline"
echo "[SUCCESS] Lint baseline committed. Do 'git push'."