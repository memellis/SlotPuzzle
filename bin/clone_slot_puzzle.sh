#!/bin/bash
#
# Clone SlotPuzzle git repository
#

# Script assumes hg, cmake are installed 

hash git 2>/dev/null || { echo >&2 "I require git but it's not installed.  Aborting."; exit 1; }

# Check for environment variables set, if not, set default value
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW_1; MINGW_HOME_WINDOWS="h:/MinGW_1"; }
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }

INTERACTIVE_MODE="No"
SOURCE_URL="git@github.com:memellis/SlotPuzzle.git"
SOURCE="SlotPuzzle"

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

CURRENT_WORKING_PATH=${PATH}
pushd ${BUILD_DIR} > /dev/null

if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${SOURCE} build"
    then
      rm -fr ${SOURCE}  
    fi
else
    rm -fr ${SOURCE}
fi

# Clone the repository
git clone ${SOURCE_URL}

# Copy bin shell scripts
pushd ${SOURCE} > /dev/null
mkdir -p bin
cp ${HOME}/bin/*.sh bin
pushd bin 

# Add files to git

for f in `ls`
do
    echo "Adding ${f} to git..."
    git add ${f}
done

# Commit changes
git commit -m "Sync script changes"

# Push changes to remote system
git push -u origin master


 




