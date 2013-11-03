#!/bin/bash
#
# Build Boost for mingw
#
# $1 = (Debug|Release)
#    Default - Debug
#

BOOST_SOURCE_URL=http://sourceforge.net/projects/boost/files/boost/1.53.0/boost_1_53_0.tar.gz/download
BOOST_SOURCE_ARCHIVE=boost_1_53_0.tar.gz
BOOST_SOURCE="${BOOST_SOURCE_ARCHIVE%.*.*}"

hash wget 2>/dev/null || { echo >&2 "I require wget but it's not installed.  Aborting."; exit 1; }
hash tar 2>/dev/null || { echo >&2 "I require tar but it's not installed.  Aborting."; exit 1; }

interactiveMode="No"

# Check for environment variables set, if not, set default value
[ -z "${BOOST_BUILD_DIR}" ] && { BOOST_BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/MinGW; }
[ -z "${BOOST_INSTALL_DIR}" ] && { BOOST_INSTALL_DIR=${MINGW_HOME}/opt; }

# Source general-purpose functions
source ${HOME}/bin/my_functions.sh

pushd ${BOOST_BUILD_DIR} > /dev/null
if [ "${interactiveMode}" = "Yes" ] 
then
    if ask "Remove previous ${BOOST_BUILD_DIR}/${BOOST_SOURCE} build"
    then
      rm -fr ${BOOST_BUILD_DIR}/${BOOST_SOURCE}  
    fi
else
    rm -fr ${BOOST_BUILD_DIR}/${BOOST_SOURCE}
fi
mkdir -p ${BOOST_BUILD_DIR}/build
pushd ${BOOST_BUILD_DIR} > /dev/null

# Get boost archive if necessary
if [ ! -f ${BOOST_SOURCE_ARCHIVE} ]
then
    wget ${BOOST_SOURCE_URL}
fi
tar zxvf ${BOOST_SOURCE_ARCHIVE}
pushd ${BOOST_SOURCE}
pushd tools/build/v2/engine
./build.sh mingw
popd 
pwd
./tools/build/v2/engine/bin.ntx86/bjam.exe toolset=gcc variant=debug,release link=shared,static threading=multi --prefix=${MINGW_HOME}
cp -r boost ${BOOST_INSTALL_DIR}/include
cp -r stage/lib ${BOOST_INSTALL_DIR}