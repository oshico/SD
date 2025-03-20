#!/usr/bin/env bash
#************************************************************************************
# Description: run previously all batch files
# Author: Rui S. Moreira
# Date: 20/02/2019
#************************************************************************************

# ======================== Use Shell Parameters ========================
# Script usage: setenv <role> (where role should be: server / client)
export SCRIPT_ROLE=$1

# ======================== CHANGE BELOW ACCORDING YOUR PROJECT and PC SETTINGS ========================
# ==== PC STUFF ====
# Update this to your Java installation path on Debian
export JDK=/usr/lib/jvm/java-17-openjdk-amd64  # Example for OpenJDK 11
# export JDK=/usr/lib/jvm/java-8-openjdk-amd64  # Example for OpenJDK 8

# IDE settings (make sure these match your IntelliJ setup on Debian)
export NETBEANS=NetBeans
export INTELLIJ=IntelliJ
export CURRENT_IDE=${INTELLIJ}

# Set the username (replace with your actual username on Debian)
export USERNAME=$(whoami)

# ==== JAVA NAMING STUFF ====
export JAVAPROJ_NAME=SD
# Update this to match your project folder path in Debian
export JAVAPROJ=/home/${USERNAME}/IdeaProjects/${JAVAPROJ_NAME}

export PACKAGE=p06_visitor
export PACKAGE_PREFIX=edu.ufp.inf.sd.rmi
export PACKAGE_PREFIX_FOLDERS=edu/ufp/inf/sd/rmi
export SERVICE_NAME_ON_REGISTRY=VisitorService
export CLIENT_CLASS_PREFIX=VisitorElementsFolders
export SERVER_CLASS_PREFIX=ElementsFolders
export CLIENT_CLASS_POSTFIX=Client
export SERVER_CLASS_POSTFIX=Server
export SERVANT_IMPL_CLASS_POSTFIX=Impl

# ==== NETWORK STUFF ====
# Use your actual local IP address or keep localhost
export MYLOCALIP=localhost
# export MYLOCALIP=192.168.1.80  # Uncomment if using specific IP
export REGISTRY_HOST=${MYLOCALIP}
export REGISTRY_PORT=1099
export SERVER_RMI_HOST=${REGISTRY_HOST}
export SERVER_RMI_PORT=1098
export SERVER_CODEBASE_HOST=${SERVER_RMI_HOST}
export SERVER_CODEBASE_PORT=8000
export CLIENT_RMI_HOST=${REGISTRY_HOST}
export CLIENT_RMI_PORT=1097
export CLIENT_CODEBASE_HOST=${CLIENT_RMI_HOST}
export CLIENT_CODEBASE_PORT=8000

# ======================== DO NOT CHANGE AFTER THIS POINT =============================================
export JAVAPACKAGE=${PACKAGE_PREFIX}.${PACKAGE}
export JAVAPACKAGEROLE=${PACKAGE_PREFIX}.${PACKAGE}.${SCRIPT_ROLE}
export JAVAPACKAGEPATH=${PACKAGE_PREFIX_FOLDERS}/${PACKAGE}/${SCRIPT_ROLE}
export JAVASCRIPTSPATH=${PACKAGE_PREFIX_FOLDERS}/${PACKAGE}/runscripts
export JAVASECURITYPATH=${PACKAGE_PREFIX_FOLDERS}/${PACKAGE}/securitypolicies
export SERVICE_NAME=${SERVICE_PREFIX}Service
export SERVICE_URL=rmi://${REGISTRY_HOST}:${REGISTRY_PORT}/${SERVICE_NAME}

export SERVANT_ACTIVATABLE_IMPL_CLASS=${JAVAPACKAGEROLE}.${SERVER_CLASS_PREFIX}${SERVANT_ACTIVATABLE_IMPL_CLASS_POSTFIX}
export SERVANT_PERSISTENT_STATE_FILENAME=${SERVICE_PREFIX}Persistent.State

# Make sure your JDK is correctly added to your PATH
export PATH=${PATH}:${JDK}/bin

# Setting up IDE-specific paths for IntelliJ (make sure they match your Debian setup)
if [ "${CURRENT_IDE}" == "${NETBEANS}" ]; then
    export JAVAPROJ_CLASSES=build/classes/
    export JAVAPROJ_DIST=dist
    export JAVAPROJ_SRC=src
    export JAVAPROJ_DIST_LIB=lib
elif [ "${CURRENT_IDE}" == "${INTELLIJ}" ]; then
    export JAVAPROJ_CLASSES=out/production/${JAVAPROJ_NAME}/
    export JAVAPROJ_DIST=out/artifacts/${JAVAPROJ_NAME}
    export JAVAPROJ_SRC=src
    export JAVAPROJ_DIST_LIB=lib
fi

export JAVAPROJ_CLASSES_FOLDER=${JAVAPROJ}/${JAVAPROJ_CLASSES}
export JAVAPROJ_DIST_FOLDER=${JAVAPROJ}/${JAVAPROJ_DIST}
export JAVAPROJ_DIST_LIB_FOLDER=${JAVAPROJ}/${JAVAPROJ_DIST_LIB}
export JAVAPROJ_JAR_FILE=${JAVAPROJ_NAME}.jar
export MYSQL_CON_JAR=mysql-connector-java-5.1.38-bin.jar

export CLASSPATH=.:${JAVAPROJ_CLASSES_FOLDER}
# Uncomment if you need to use a JAR for MySQL
# export CLASSPATH=.:${JAVAPROJ_DIST_FOLDER}/${JAVAPROJ_JAR_FILE}:${JAVAPROJ_DIST_LIB_FOLDER}/${MYSQL_CON_JAR}

export ABSPATH2CLASSES=${JAVAPROJ}/${JAVAPROJ_CLASSES}
export ABSPATH2S
