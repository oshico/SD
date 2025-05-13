#!/usr/bin/env bash

# Ensure necessary environment variables are sourced
source ./setenv.sh server

# Change directory to classes folder (or dist folder if preferred)
cd ${ABSPATH2CLASSES} || exit 1  # exit if the directory doesn't exist

# Display the current classes directory
echo "Current directory: ${ABSPATH2CLASSES}"

# Run the Python 3 HTTP server
python3 -m http.server 8000

# Go to the specified JavaScript path inside the source directory
cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH} || exit 1  # exit if the directory doesn't exist
