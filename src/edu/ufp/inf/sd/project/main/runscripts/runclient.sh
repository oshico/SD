#!/usr/bin/env bash

# Source environment variables for the client role
source ./setenv.sh client

# Change to the classes directory
cd "${ABSPATH2CLASSES}" || exit 1  # Exit if the directory doesn't exist

# Run the Java client application with the necessary system properties
java -cp "${CLASSPATH}" \
     -Djava.security.policy="${CLIENT_SECURITY_POLICY}" \
     -Djava.rmi.server.codebase="${SERVER_CODEBASE}" \
     -D${JAVAPACKAGEROLE}.codebase="${CLIENT_CODEBASE}" \
     -D${JAVAPACKAGE}.servicename="${SERVICE_NAME_ON_REGISTRY}" \
     "${JAVAPACKAGEROLE}"."${CLIENT_CLASS_PREFIX}${CLIENT_CLASS_POSTFIX}" \
     "${REGISTRY_HOST}" "${REGISTRY_PORT}" "${SERVICE_NAME_ON_REGISTRY}"

# Change to the JavaScript directory
cd "${ABSPATH2SRC}/${JAVASCRIPTSPATH}" || exit 1  # Exit if the directory doesn't exist
