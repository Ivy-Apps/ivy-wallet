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

# -------------------
# 1. OS DETECTION
# -------------------

# Detect the Operating System
OS="linux"
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS="macos"
fi

# Helper function for the sed command, as it behaves differently on macOS and Linux
sed_inplace() {
    if [ "$OS" == "macos" ]; then
        # For macOS, -i requires an extension. Using "" to avoid creating a backup.
        sed -i "" "$@"
    else
        # For Linux
        sed -i "$@"
    fi
}

# ---------------------
# 2. INPUT VALIDATION
# ---------------------

# Check if a module name is provided
if [ -z "$1" ]; then
    echo "ERROR:"
    echo "Please provide a module name. Usage:"
    echo "./scripts/create_module.sh my-module"
    exit 1
fi

MODULE_NAME=$1

# ---------------------
# 3. PATH CONFIGURATION
# ---------------------

# Define the source and destination paths
TEMPLATE_PATH="./templates/_module"
DEST_PATH="./$MODULE_NAME"

# --------------------------
# 4. MODULE CREATION CHECKS
# --------------------------

# Check if module already exists
if [ -d "$DEST_PATH" ]; then
    echo "Module with name $MODULE_NAME already exists!"
    exit 1
fi

# -----------------------------
# 5. COPYING THE TEMPLATE DATA
# -----------------------------

# Copy the template to a new module directory
cp -R "$TEMPLATE_PATH" "$DEST_PATH"

# -------------------------------------
# 6. RENAMING FILES AND FOLDER CONTENTS
# -------------------------------------

# Rename folders and files starting with "_"
find "$DEST_PATH" -depth -name '*_*' | while IFS= read -r f ; do
    mv "$f" "${f//_module/$MODULE_NAME}"  # String replace "_module" with the module name
done

# ---------------------------------
# 7. UPDATING PLACEHOLDERS IN FILES
# ---------------------------------

# Replace the placeholder "_module" with the module name in all files
if [[ "$OS" == "macos" ]]; then
    find "$DEST_PATH" -type f -exec sh -c 'sed -i "" "s/_module/'"$MODULE_NAME"'/g" "$0"' {} \;
else
    find "$DEST_PATH" -type f -exec sh -c 'sed -i "s/_module/'"$MODULE_NAME"'/g" "$0"' {} \;
fi

# Specifically update namespace in build files
if [[ "$OS" == "macos" ]]; then
    find "$DEST_PATH" -type f -name 'build.gradle.kts' -exec sh -c 'sed -i "" "s/com.ivy._module/com.ivy.'"$MODULE_NAME"'/g" "$0"' {} \;
else
    find "$DEST_PATH" -type f -name 'build.gradle.kts' -exec sh -c 'sed -i "s/com.ivy._module/com.ivy.'"$MODULE_NAME"'/g" "$0"' {} \;
fi

echo "Module ':$MODULE_NAME' created successfully."

# -------------------------------------
# 8. UPDATING THE PROJECT CONFIGURATION
# -------------------------------------

# Append the new module to settings.gradle.kts
echo "include(\":$MODULE_NAME\")" >> settings.gradle.kts
echo "Module ':$MODULE_NAME' added to 'settings.gradle.kts'."

# -------------------------------------
# 9. ADD THE MODULE TO GIT
# -------------------------------------

git add "$MODULE_NAME"
echo "Module added to git."

echo "Congrats! Module ':$MODULE_NAME' created successfully! Now sync the project."
