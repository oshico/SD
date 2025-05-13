#!/usr/bin/env bash

# Ensure necessary environment variables are sourced
source ./setenv.sh server

# Change to the classes directory (or appropriate directory)
cd ${ABSPATH2CLASSES} || exit 1  # exit if the directory doesn't exist

# Java RMI setup (ensure these system properties are valid)
java -cp ${CLASSPATH} \
     -Djava.rmi.server.codebase=${SERVER_CODEBASE} \
     -Djava.rmi.server.hostname=${SERVER_RMI_HOST} \
     -Djava.security.policy=${SERVER_SECURITY_POLICY} \
     ${JAVAPACKAGEROLE}.${SERVER_CLASS_PREFIX}${SERVER_CLASS_POSTFIX} ${REGISTRY_HOST} ${REGISTRY_PORT} ${SERVICE_NAME_ON_REGISTRY}

# Change to the JavaScript directory (from source path)
cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH} || exit 1  # exit if the directory doesn't exist
