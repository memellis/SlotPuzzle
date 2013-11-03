#!/bin/bash
#
# Build libxml2 for mingw
#

SOURCE_URL=ftp://xmlsoft.org/libxml2/libxml2-2.9.1.tar.gz
SOURCE_ARCHIVE=libxml2-2.9.1.tar.gz
SOURCE="${SOURCE_ARCHIVE%.*.*}"

hash make 2>/dev/null || { echo >&2 "I require make but it's not installed.  Aborting."; exit 1; }
hash tar 2>/dev/null || { echo >&2 "I require tar but it's not installed.  Aborting."; exit 1; }

INTERACTIVE_MODE="NO"

# Check for environment variables set, if not, set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW; }
[ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${MINGW_HOME}; }

echo $INSTALL_DIR

# Source general-purpose functions
source ${HOME}/bin/my_functions.sh

pushd ${BUILD_DIR} > /dev/null
if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${SOURCE} build"
    then
      rm -fr ${BUILD_DIR}/${SOURCE}  
    fi
else
    rm -fr ${BUILD_DIR}/${SOURCE}
fi
mkdir -p ${BUILD_DIR}/build
pushd ${BUILD_DIR} > /dev/null

# Get archive if necessary
if [ ! -f ${SOURCE_ARCHIVE} ]
then
    wget ${SOURCE_URL}
fi
tar zxvf ${SOURCE_ARCHIVE}
pushd ${SOURCE} > /dev/null
./configure --prefix=${INSTALL_DIR}
make
make install
