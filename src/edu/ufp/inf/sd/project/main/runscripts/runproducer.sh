#!/usr/bin/env bash

# Source the necessary environment variables for the producer role
source ./setenv.sh producer

# Assign words to variables
export WORD_1=$1
export WORD_2=$2
export WORD_3=$3

# Display the directory for debugging (optional)
echo ${ABSPATH2CLASSES}

# Change to the classes directory where the Java classes are compiled
cd ${ABSPATH2CLASSES} || exit 1  # exit if directory doesn't exist

# Run the Java producer to send messages to RabbitMQ
java -cp ${CLASSPATH} \
     ${JAVAPACKAGEROLEPATH}.${PRODUCER_CLASS_PREFIX} ${BROKER_HOST} ${BROKER_PORT} ${BROKER_QUEUE} ${WORD_1} ${WORD_2} ${WORD_3}

# Optionally, navigate to JavaScript directory for client-side logic
echo "JavaScript directory: ${ABSPATH2SRC}/${JAVASCRIPTSPATH}"
cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH} || exit 1  # exit if directory doesn't exist