#!/usr/bin/env bash
#************************************************************************************
# Description: Run HelloWorldClient
# Author: Rui S. Moreira
# Date: 20/02/2014
#************************************************************************************
# Script usage: runclient <role> (where role should be: server / client)

# Ensure necessary environment variables are sourced
source ./setenv.sh server

# Change directory to classes folder (or dist folder if preferred)
cd ${ABSPATH2CLASSES} || exit 1  # exit if the directory doesn't exist

# Clear the terminal screen (optional, for visual clarity)
# clear  # Uncomment if you want to clear the terminal screen before running

# Display the current classes directory
echo "Current directory: ${ABSPATH2CLASSES}"

# Run the Python 3 HTTP server
python3 -m http.server 8000

# Alternative: Python 2.7 HTTP server (commented out, just in case you need it)
# python -m SimpleHTTPServer 8000

# Go to the specified JavaScript path inside the source directory
cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH} || exit 1  # exit if the directory doesn't exist
