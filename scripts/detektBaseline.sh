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


./gradlew detektBaseline || exit 0
git add config/detekt/baseline.yml || exit 0
git commit -m "Add Detekt baseline" || exit 0
echo "Detekt baseline added."
echo "[SUCCESS] Detekt baseline committed. Do 'git push'."