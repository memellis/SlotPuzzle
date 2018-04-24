#! /bin/bash
# Build CEGUI Dependencies for MinGW
#

SOURCE_URL=http://prdownloads.sourceforge.net/crayzedsgui/cegui-deps-0.8.x-src.zip?download
SOURCE_ARCHIVE=cegui-deps-0.8.x-src.zip
SOURCE="${SOURCE_ARCHIVE%.*}"


# Script assumes unzip, cmake are installed 
hash unzip 2>/dev/null || { echo >&2 "I require unzip but it's not installed.  Aborting."; exit 1; }
hash cmake 2>/dev/null || { echo >&2 "I require cmake but it's not installed.  Aborting."; exit 1; }

BUILD_TYPE=Debug
INTERACTIVE_MODE="Yes"
CEGUI_DEPS_DIR=cegui-deps

# Source general-purpose functions
if [ -f ${HOME}/bin/my_functions.sh ]
then
    source ${HOME}/bin/my_functions.sh
fi

# Check for environment variables set, if not, set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${PATCHES_DIR}" ] && { PATCHES_DIR=${HOME}/patches; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/MinGW; }
[ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${MINGW_HOME}/opt; }

CURRENT_WORKING_PATH=${PATH}
pushd ${BUILD_DIR} > /dev/null
if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${CEGUI_DEPS_DIR} build"
    then
      rm -fr ${CEGUI_DEPS_DIR}  
    fi
else
    rm -fr ${CEGUI_DEPS_DIR}
fi
mkdir -p ${CEGUI_DEPS_DIR}/build
pushd ${CEGUI_DEPS_DIR} > /dev/null

# Get CEGUI Dependencies archive if necessary
if [ ! -f ${SOURCE_ARCHIVE} ]
then
    wget ${SOURCE_URL}
fi
unzip ${SOURCE_ARCHIVE}
patch -p0 < ${PATCHES_DIR}/cegui_0.8.2_win32_mingw_cegui_deps_def.patch
pushd build > /dev/null
PATH=${MINGW_HOME}:${MINGW_HOME}/bin:${MINGW_HOME}/opt/bin:.
cmake ../${SOURCE} -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_DIR} -DCMAKE_BUILD_TYPE:STRING=${BUILD_TYPE} -DCEGUI_BUILD_EFFECTS11:BOOL=OFF -G"Eclipse CDT4 - MinGW Makefiles"
PATH=${CURRENT_WORKING_PATH}
mingw32-make
cp -r ${BUILD_DIR}/${CEGUI_DEPS_DIR}/build/dependencies/include ${INSTALL_DIR}
cp  ${BUILD_DIR}/${CEGUI_DEPS_DIR}/build/dependencies/bin/* ${INSTALL_DIR}/bin
cp ${BUILD_DIR}/${CEGUI_DEPS_DIR}/build/dependencies/lib/dynamic/*  ${INSTALL_DIR}/lib
exit 0
