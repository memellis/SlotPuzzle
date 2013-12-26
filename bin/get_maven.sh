#!/bin/bash
#
# Get Apache maven
#

# Script assumes tar, wget are installed 

hash tar 2>/dev/null || { echo >&2 "I require tar but it's not installed.  Aborting."; exit 1; }
hash wget 2>/dev/null || { echo >&2 "I require wget but it's not installed.  Aborting."; exit 1; }


# Check for environment variables set, if not, set default value
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW_1; MINGW_HOME_WINDOWS="h:/MinGW_1"; }
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z ${INSTALL_DIR} ] && { INSTALL_DIR=${MINGW_HOME}/opt; }

INTERACTIVE_MODE="No"
SOURCE_URL="http://mirrors.ukfast.co.uk/sites/ftp.apache.org/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.tar.gz"
SOURCE_ARCHIVE=apache-maven-3.1.1-bin.tar.gz
SOURCE="apache-maven-3.1.1"
MAVEN_DIR=maven

# Source general-purpose functions
if [ -f ${HOME}/bin/my_functions.sh ]
then
    source ${HOME}/bin/my_functions.sh
fi

if [ $# -ge 1 ]
then

	if [ $? -ne 0 ]
	then
	    echo ""
	    echo "Usage: $0 [-h] [-i]"
		echo "  -h      Prints this usage message."
		echo "  -i      Interactive mode for to remove a previous build."
		echo "          The default is to remove a previous build."
		exit 1
	fi
	
    while getopts dhir flag; do
      case $flag in
	     h)
  	        echo ""
	        echo "Usage: $0 [-h] [-d] [-r] [-i]"
		    echo "  -h      Prints this usage message."
		    echo "  -d      Build Debug version of OGRE Tutorial Framework. This is the default build type."
		    echo "  -r      Build Release version of OGRE Tutorial Framework."
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
      rm -fr ${BUILD_DIR}/${MAVEN_DIR}  
    fi
else
    rm -fr ${BUILD_DIR}/${MAVEN_DIR}
fi
ls

mkdir -p ${BUILD_DIR}/${MAVEN_DIR}
pushd ${BUILD_DIR}/${MAVEN_DIR} > /dev/null

tar zxvf ${BUILD_DIR}/${SOURCE_ARCHIVE}
cp -r ${SOURCE} ${INSTALL_DIR}
