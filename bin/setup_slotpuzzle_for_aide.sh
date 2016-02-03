#!/bin/bash
#
# Setup SlotPuzzle to work with AIDEs
#

hash wget 2>/dev/null || { echo >&2 "I require git but it's not installed.  Aborting."; exit 1; }


# Check for environment variables set, if not, set default valuess
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z ${INSTALL_DIR} ] && { INSTALL_DIR=${BUILD_DIR}/SlotPuzzle/source/java/2d; }

INTERACTIVE_MODE="No"
SOURCE_URL="https://libgdx.badlogicgames.com/nightlies/libgdx-nightly-latest.zip"
SOURCE="libgdx-nightly-latest"
SOURCE_ARCHIVE=libgdx-nightly-latest.zip

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
		    echo "  -d      Build Debug version of ${SOURCE}. This is the default build type."
		    echo "  -r      Build Release version of ${SOURCE}."
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
    wget --no-check-certificate ${SOURCE_URL}
fi

if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${SOURCE} build"
    then
      rm -fr ${BUILD_DIR}/{SOURCE}  
    fi
else
    rm -fr ${BUILD_DIR}/${SOURCE}
fi

mkdir -p ${BUILD_DIR}/${SOURCE}
pushd ${BUILD_DIR}/${SOURCE}

unzip ${BUILD_DIR}/${SOURCE_ARCHIVE}

cp -r ${INSTALL_DIR}/core/src/com ${INSTALL_DIR}/gdx-game/src
cp -r ${INSTALL_DIR}/core/src/org ${INSTALL_DIR}/gdx-game/src
cp gdx.jar ${INSTALL_DIR}/gdx-game/libs
cp gdx-backend-android.jar ${INSTALL_DIR}/gdx-game-android/libs
cp armeabi/libgdx.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi
cp armeabi-v7a/libgdx.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi-v7a/libs
cp x86/libgdx.so ${INSTALL_DIR}/gdx-game-android/libs/x86
cp extensions/gdx-freetype/gdx-freetype.jar ${INSTALL_DIR}/gdx-game/libs
cp extensions/gdx-freetype/gdx-freetype-natives.jar ${INSTALL_DIR}/gdx-game/libs
cp extensions/gdx-freetype/armeabi/libgdx-freetype.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi
cp extensions/gdx-freetype/armeabi-v7a/libgdx-freetype.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi-v7a/libgdx
cp extensions/gdx-freetype/x86/libgdx-freetype.so ${INSTALL_DIR}/gdx-game-android/libs/x86
cp ${INSTALL_DIR}/libs/tween-engine-api.jar ${INSTALL_DIR}/gdx-game/libs
cp ${INSTALL_DIR}/libs/tween-engine-api-sources.jar ${INSTALL_DIR}/gdx-game/libs
