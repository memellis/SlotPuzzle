#!/bin/bash
#
# Build libCURL for MinGW
#

SOURCE_URL=http://curl.haxx.se/download/curl-7.32.0.tar.gz
SOURCE_ARCHIVE=curl-7.32.0.tar.gz
SOURCE="${SOURCE_ARCHIVE%.*.*}"

hash wget 2>/dev/null || { echo >&2 "I require wget but it's not installed.  Aborting."; exit 1; }
hash tar 2>/dev/null || { echo >&2 "I require tar but it's not installed.  Aborting."; exit 1; }
hash make 2>/dev/null || { echo >&2 "I require make but it's not installed.  Aborting."; exit 1; }
hash cmake 2>/dev/null || { echo >&2 "I require cmake but it's not installed.  Aborting."; exit 1; }

INTERACTIVE_MODE="NO"
FULL_SCRIPT_NAME="${0}"
CURL_DIR=curl

# Check for environment variables set, if not, set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW_1; }
[ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${MINGW_HOME}/opt; }

# Source general-purpose functions
source ${HOME}/bin/my_functions.sh

if [ $# -ge 1 ]
then

	if [ $? -ne 0 ]
	then
	    echo ""
	    echo "Usage: ${FULL_SCRIPT_NAME##*/} [-h] [-i]"
		echo "  -h      Prints this usage message."
		echo "  -i      Interactive mode for to remove a previous build."
		echo "          The default is to remove a previous build."
		exit 1
	fi
	
    while getopts hi flag; do
      case $flag in
       h)
  	        echo ""
	        echo "Usage: ${FULL_SCRIPT_NAME##*/} [-h] [-i]"
		    echo "  -h      Prints this usage message."
		    echo "  -i      Interactive mode for to remove a previous build."
		    echo "          The default is to remove a previous build."
		    exit 1
	        ;;
         i)
            INTERACTIVE_MODE="Yes"
            ;;
         ?)
            exit;
            ;;
        esac
    done

    shift $(( OPTIND - 1 ));
fi

pushd ${BUILD_DIR} > /dev/null

# Get archive if necessary
if [ ! -f ${SOURCE_ARCHIVE} ]
then
    wget ${SOURCE_URL}
fi

if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${CURL_DIR} build"
    then
      rm -fr ${BUILD_DIR}/${CURL_DIR}  
    fi
else
    rm -fr ${BUILD_DIR}/${CURL_DIR}
fi
mkdir -p ${BUILD_DIR}/${CURL_DIR}/build
pushd ${BUILD_DIR}/${CURL_DIR} > /dev/null

tar zxvf ${BUILD_DIR}/${SOURCE_ARCHIVE}
pushd build > /dev/null

CURRENT_WORKING_PATH=${PATH}
PATH=${MINGW_HOME}:${MINGW_HOME}/bin:${MINGW_HOME}/opt/bin.
cmake ../${SOURCE} -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_DIR} -DCMAKE_BUILD_TYPE:STRING=${BUILD_TYPE} -DOPENSSL_ROOT_DIR:PATH=${INSTALL_DIR} -G"Eclipse CDT4 - MinGW Makefiles"
mingw32-make -j${NUMBER_OF_PROCESSORS}
mingw32-make install

PATH=${CURRENT_WORKING_PATH}
# Workaround libcurl.dll reported as missing
cp ${INSTALL_DIR}/lib/libcurl.dll ${INSTALL_DIR}/bin
