#!/usr/bin/env bash

# Source the necessary environment variables for the consumer role
source ./setenv.sh consumer

# Display the directory for debugging (optional)
echo ${ABSPATH2CLASSES}

# Change to the classes directory where the Java classes are compiled
cd ${ABSPATH2CLASSES} || exit 1  # exit if directory doesn't exist

# Run the Java consumer to consume messages from RabbitMQ
java -cp ${CLASSPATH} \
     ${JAVAPACKAGEROLEPATH}.${CONSUMER_CLASS_PREFIX} ${BROKER_HOST} ${BROKER_PORT} ${BROKER_QUEUE}

# Optionally, navigate to JavaScript directory for client-side logic
echo ${ABSPATH2SRC}/${JAVASCRIPTSPATH}
cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH} || exit 1  # exit if directory doesn't exist
