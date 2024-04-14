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

# -----------------

# Check if the dot command is available
if ! command -v dot &> /dev/null
then
    echo "The 'dot' command is not found. This is required to generate SVGs from the Graphviz files."
    echo "Installation instructions:"
    echo "  - On macOS: You can install Graphviz using Homebrew with the command: 'brew install graphviz'"
    echo "  - On Ubuntu: You can install Graphviz using APT with the command: 'sudo apt-get install graphviz'"
    exit 1
fi

# Clean
rm all_modules.gv

# Generate modules graph
echo "Generating dependencies graph..."
./gradlew generateModulesGraphvizText --rerun-tasks -Pmodules.graph.output.gv=all_modules.gv || exit -1

# Convert the graph to SVG
dot -Tsvg "all_modules.gv" > "docs/assets/modules-graph.svg" || exit -1
echo 'Modules graph generated in "docs/assets/modules-graph.svg"'
open docs/assets/modules-graph.svg