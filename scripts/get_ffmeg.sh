#!/bin/bash
#
# Build Git for MinGW, via git
#

SOURCE_URL=git://source.ffmpeg.org/ffmpeg.git
SOURCE=ffmpeg

hash git 2>/dev/null || { echo >&2 "I require git but it's not installed.  Aborting."; exit 1; }
hash make 2>/dev/null || { echo >&2 "I require make but it's not installed.  Aborting."; exit 1; }

INTERACTIVE_MODE="NO"
FULL_SCRIPT_NAME="${0}"

# Check for environment variables set, if not, set default value
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/MinGW; }
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

# Get source
git config --global core.autocrlf false
git clone ${SOURCE_URL}
pushd ${SOURCE} > /dev/null
configure --cpu=corei7
make
