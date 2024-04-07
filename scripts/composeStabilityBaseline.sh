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


./gradlew :ci-actions:compose-stability:run --args='generateBaseline' || exit 1
git add ci-actions/compose-stability/ivy-compose-stability-baseline.txt || exit 1
git commit -m "Add Compose Stability baseline" || exit 1
echo "Compose Stability baseline added."
echo "[SUCCESS] Compose Stability baseline committed. Do 'git push'."