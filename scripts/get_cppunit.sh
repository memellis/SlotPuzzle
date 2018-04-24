#!/bin/bash
#
# Build libxml2 for mingw
#

SOURCE_URL=http://sourceforge.net/projects/cppunit/files/cppunit/1.12.1/cppunit-1.12.1.tar.gz/download
SOURCE_ARCHIVE=cppunit-1.12.1.tar.gz
SOURCE="${SOURCE_ARCHIVE%.*.*}"

hash wget 2>/dev/null || { echo >&2 "I require make but it's not installed.  Aborting."; exit 1; }
hash make 2>/dev/null || { echo >&2 "I require make but it's not installed.  Aborting."; exit 1; }
hash tar 2>/dev/null || { echo >&2 "I require tar but it's not installed.  Aborting."; exit 1; }

INTERACTIVE_MODE="NO"
BUILD_TYPE="Debug"

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
	    echo "Usage: $0 [-h] [-d] [-r] [-i]"
		echo "  -h      Prints this usage message."
		echo "  -d      Build Debug version of cppunit. This is the default build type."
		echo "  -r      Build Release version of cppunit."
		echo "  -i      Interactive mode for to remove a previous build."
		echo "          The default is to remove a previous build."
		exit 1
	fi
	
    while getopts dhir flag; do
      case $flag in
         d)
            BUILD_TYPE="Debug"
			DEBUG_EXTENSION="_d"
            ;;
	     h)
  	        echo ""
	        echo "Usage: $0 [-h] [-d] [-r] [-i]"
		    echo "  -h      Prints this usage message."
		    echo "  -d      Build Debug version of cppunit. This is the default build type."
		    echo "  -r      Build Release version of cppunt."
		    echo "  -i      Interactive mode for to remove a previous build."
		    echo "          The default is to remove a previous build."
		    exit 1
	        ;;
         i)
            INTERACTIVE_MODE="Yes"
            ;;
         r)
            BUILD_TYPE="Release"
			DEBUG_EXTENSION=""
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
./configure --prefix=${INSTALL_DIR}
make
make install
