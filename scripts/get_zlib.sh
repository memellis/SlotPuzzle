#!/bin/bash
#
# Build Zlib for MinGW
#

SOURCE_URL=http://prdownloads.sourceforge.net/libpng/zlib-1.2.8.tar.gz?download
SOURCE_ARCHIVE=zlib-1.2.8.tar.gz
SOURCE="${SOURCE_ARCHIVE%.*.*}"

hash wget 2>/dev/null || { echo >&2 "I require wget but it's not installed.  Aborting."; exit 1; }
hash tar 2>/dev/null || { echo >&2 "I require tar but it's not installed.  Aborting."; exit 1; }
hash make 2>/dev/null || { echo >&2 "I require make but it's not installed.  Aborting."; exit 1; }

INTERACTIVE_MODE="NO"
FULL_SCRIPT_NAME="${0}"

# Check for environment variables set, if not, set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW; }
[ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${MINGW_HOME}; }

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
make -f win32/Makefile.gcc
cp -v zlib1.dll ${MINGW_HOME}/bin
cp -v zconf.h zlib.h ${MINGW_HOME}/include
cp -v libz.a libz.dll.a ${MINGW_HOME}/lib
