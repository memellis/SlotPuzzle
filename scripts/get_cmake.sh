#!/bin/bash
#
# Build cmake from Windows source archive

# Script assumes wget
hash wget 2>/dev/null || { echo >&2 "I require wget but it's not installed.  Aborting."; exit 1; }
hash unzip 2>/dev/null || { echo >&2 "I require unzip but it's not installed.  Aborting."; exit 1; }

# Check for environment variables set, if not set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/MinGW; }
[ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${MINGW_HOME}/opt; }
[ -z "${MY_DOWNLOAD_DIR}" ] && { MY_DOWNLOAD_DIR=${HOME}/downloads; }

# Source general-purpose functions
source ${HOME}/bin/my_functions.sh

SOURCE_URL=http://www.cmake.org/files/v2.8/cmake-2.8.11.2.zip
SOURCE_ARCHIVE=`basename ${SOURCE_URL}`
SOURCE="${SOURCE_ARCHIVE%.*}"

# Get cmake source if necessary
if [ ! -f ${MY_DOWNLOAD_DIR}/${SOURCE_ARCHIVE} ]
then
	mkdir -p ${MY_DOWNLOAD_DIR}
    pushd ${MY_DOWNLOAD_DIR} > /dev/null
	echo "Downloading ${SOURCE_ARCHIVE}"
	wget ${SOURCE_URL}
	popd > /dev/null
fi

echo "Building cmake..."
mkdir -p ${BUILD_DIR}
if [ -d ${BUILD_DIR}/${SOURCE} ]
then
	if ask "Remove previous ${BUILD_DIR}/${SOURCE} build"
	then
        rm -fr ${BUILD_DIR}/${SOURCE}
	fi
fi
pushd ${BUILD_DIR} > /dev/null
unzip ${MY_DOWNLOAD_DIR}/${SOURCE_ARCHIVE}
pushd ${SOURCE} > /dev/null
./configure --prefix=${INSTALL_DIR}
make
make install
