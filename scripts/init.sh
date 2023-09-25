#!/bin/bash

# scripts/init.sh

# Define directories
HOOKS_DIR="git/hooks"
GIT_HOOKS_DIR=".git/hooks"
BASE_DIR="$(pwd)"

# Check if the .git directory exists
if [ ! -d "$GIT_HOOKS_DIR" ]; then
    echo "Error: This script should be run from the root of the git repository."
    exit 1
fi

# Set up symbolic link for pre-push hook
# ln -s -f $BASE_DIR/$HOOKS_DIR/pre-push $GIT_HOOKS_DIR/pre-push || exit -1
# echo "pre-push hook has been set up."

# Ensure the pre-push hook is executable
# chmod +x $GIT_HOOKS_DIR/pre-push || exit -1
# echo "pre-push hook is now executable."

chmod +x "$BASE_DIR"/scripts/create_module.sh || exit 1
chmod +x "$BASE_DIR"/scripts/detekt.sh || exit 1
chmod +x "$BASE_DIR"/scripts/detektBaseline.sh || exit 1
chmod +x "$BASE_DIR"/scripts/detektFormat.sh || exit 1
chmod +x "$BASE_DIR"/scripts/lint.sh || exit 1
chmod +x "$BASE_DIR"/scripts/lintBaseline.sh || exit 1
echo "scripts in '/scripts' are now executable."

echo "Repository setup complete!"