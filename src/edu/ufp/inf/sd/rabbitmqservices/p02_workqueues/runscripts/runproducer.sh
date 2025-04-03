#!/usr/bin/env bash
#@REM ************************************************************************************
#@REM Description: run 
#@REM Author: Rui S. Moreira
#@REM Date: 10/04/2018
#@REM ************************************************************************************
#@REM Script usage: runsetup <role> (where role should be: server / client)
source ./setenv.sh producer

export QUEUE_TYPE=$1

export TASK_1=$1
export TASK_2=$2
export TASK_3=$3

echo ${ABSPATH2CLASSES}
cd ${ABSPATH2CLASSES}
#clear
#pwd
java -cp ${CLASSPATH} \
     ${JAVAPACKAGEROLEPATH}.${PRODUCER_CLASS_PREFIX} ${BROKER_HOST} ${BROKER_PORT} ${QUEUE_TYPE} ${TASK_1} ${TASK_2} ${TASK_3}


cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH}
#pwd