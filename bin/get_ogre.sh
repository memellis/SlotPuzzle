#!/bin/bash
#
# Build OGRE for MinGW
#
# Usage: $0 [-d] [-r] [-i]
# -d      Build Debug version of OGRE. This is the default build type."
# -r      Build Release version of OGRE."
# -i      Interactive mode to remove a previous build."
#         The default is to remove a previous build."

# Script assumes hg, cmake are installed 
hash hg 2>/dev/null || { echo >&2 "I require hg but it's not installed.  Aborting."; exit 1; }
hash cmake 2>/dev/null || { echo >&2 "I require cmake but it's not installed.  Aborting."; exit 1; }

OGREDEPS_DIR=ogredeps
OGRE_DIR=ogre
BUILD_TYPE="Debug"
INTERACTIVE_MODE="No"
 
# Check for environment variables set, if not, set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW_1; }
[ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${MINGW_HOME}/opt; }
[ -z "${OGRE_DEPENDENCIES_DIR}" ] && { OGRE_DEPENDENCIES_DIR=${BUILD_DIR}/${OGREDEPS_DIR}/dependencies; }
[ -z "${DXSDK_DIR}" ] && { DXSDK_DIR="H:/Program Files (x86)/Microsoft DirectX SDK (June 2010)"; export DXSDK_DIR; }

if [ $# -ge 1 ]
then

	if [ $? -ne 0 ]
	then
	    echo ""
	    echo "Usage: $0 [-h] [-d] [-r] [-i]"
		echo "  -h      Prints this usage message."
		echo "  -d      Build Debug version of OGRE. This is the default build type."
		echo "  -r      Build Release version of OGRE."
		echo "  -i      Interactive mode for to remove a previous build."
		echo "          The default is to remove a previous build."
		exit 1
	fi
	
    while getopts dhir flag; do
      case $flag in
         d)
            BUILD_TYPE="Debug"
            ;;
	     h)
  	        echo ""
	        echo "Usage: $0 [-h] [-d] [-r] [-i]"
		    echo "  -h      Prints this usage message."
		    echo "  -d      Build Debug version of OGRE. This is the default build type."
		    echo "  -r      Build Release version of OGRE."
		    echo "  -i      Interactive mode for to remove a previous build."
		    echo "          The default is to remove a previous build."
		    exit 1
	        ;;
         i)
            INTERACTIVE_MODE="Yes"
            ;;
         r)
            BUILD_TYPE="Release"
            ;;
         ?)
            exit;
            ;;
        esac
    done

    shift $(( OPTIND - 1 ));
fi
 
   
# Source general-purpose functions
if [ -f ${HOME}/bin/my_functions.sh ]
then
    source ${HOME}/bin/my_functions.sh
fi

CURRENT_WORKING_PATH=${PATH}
pushd ${BUILD_DIR} > /dev/null
if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${OGREDEPS_DIR} build"
    then
      rm -fr ${OGREDEPS_DIR}  
    fi
else
    rm -fr ${OGREDEPS_DIR}
fi
mkdir -p ${OGREDEPS_DIR}/build
pushd ${OGREDEPS_DIR} > /dev/null

echo "Fetching ogredeps source code..."
hg clone https://bitbucket.org/cabalistic/ogredeps
pushd build > /dev/null
PATH=${MINGW_HOME}:${MINGW_HOME}/bin:${MINGW_HOME}/opt/bin:.
echo "Building ogredeps source..."
cmake ../${OGREDEPS_DIR} -DCMAKE_INSTALL_PREFIX:PATH=${OGRE_DEPENDENCIES_DIR} -DCMAKE_BUILD_TYPE:STRING=${BUILD_TYPE} -G"Eclipse CDT4 - MinGW Makefiles"
mingw32-make -j${NUMBER_OF_PROCESSORS}
mingw32-make install

# Now build Ogre

PATH=${CURRENT_WORKING_PATH}
pushd ${BUILD_DIR} > /dev/null
if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${OGRE_DIR} build"
    then
        rm -fr ${OGRE_DIR}
    fi
else
    rm -fr ${OGRE_DIR}
fi
mkdir -p ${OGRE_DIR}/build
pushd ${OGRE_DIR} > /dev/null

echo "Fetching OGRE source..."
hg clone https://bitbucket.org/sinbad/ogre/ -u v1-8-1
pushd build > /dev/null
PATH=${MINGW_HOME}:${MINGW_HOME}/bin:${MINGW_HOME}/opt/bin:.
echo "Building OGRE source..."
cmake ../ogre -DCMAKE_PREFIX_PATH:PATH="${DXSDK_DIR}" -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_DIR} -DOGRE_DEPENDENCIES_DIR="${OGRE_DEPENDENCIES_DIR}" -DCMAKE_BUILD_TYPE:STRING=${BUILD_TYPE} -DOGRE_INSTALL_SAMPLES:BOOL=ON -G"Eclipse CDT4 - MinGW Makefiles"
mingw32-make -j${NUMBER_OF_PROCESSORS}
mingw32-make install
PATH=${CURRENT_WORKING_PATH}
cp ${OGRE_DEPENDENCIES_DIR}/${BUILD_TYPE}/bin/cg.dll ${INSTALL_DIR}/bin/${BUILD_TYPE}

exit 0