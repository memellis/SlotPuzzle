#!/bin/bash
#
# Setup SlotPuzzle to work with AIDE
#

check_for_dependencies() {
    echo "Check for dependencies..."
    WGET="wget"
    hash ${WGET} 2>/dev/null || { echo >&2 "I require ${WGET} but it's not installed.  Aborting."; exit 1; }
}

define_environment_variables() {
    echo "define_enironment_variables..." 

    [ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; mkdir -p ${BUILD_DIR}; }
    [ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=${BUILD_DIR}/SlotPuzzle/source/java/2d; }

    INTERACTIVE_MODE="No"
    SOURCE_URL="https://libgdx.badlogicgames.com/nightlies/libgdx-nightly-latest.zip"
    SOURCE="libgdx-nightly-latest"
    SOURCE_ARCHIVE="libgdx-nightly-latest.zip"
}

source_my_functions() {
    echo "source_my_functions..."
    if [ -f ${HOME}/bin/my_functions.sh ]
    then
        source ${HOME}/bin/my_functions.sh
    fi
}

process_command_line_arguments() {
    echo "process_command_line_arguments..."
    ARGUMENTS="$*"
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
		    echo "  -i      Interactive mode to remove a previous build."
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
}

get_archive() {
    echo "get_archive..."
    local BUILD_DIR=${1}
    local SOURCE_ARCHIVE=${2}
    local SOURCE_URL=${3}
    local SOURCE=${4}
    local INTERACTIVE_MODE=${5}

    pushd ${BUILD_DIR} > /dev/null

    if [ ! -f ${SOURCE_ARCHIVE} ]
    then
        wget --no-check-certificate ${SOURCE_URL}
    fi

    if [ "${INTERACTIVE_MODE}" = "Yes" ]
    then
        if ask "Remove previous ${BUILD_DIR}/${SOURCE} build"
        then
            rm -fr ${BUILD_DIR}/${SOURCE}
        fi
    else
        rm -fr ${BUILD_DIR}/${SOURCE}
    fi

    mkdir -p ${BUILD_DIR}/${SOURCE}
    pushd ${BUILD_DIR}/${SOURCE}

    unzip ${BUILD_DIR}/${SOURCE_ARCHIVE}
}

check_directory() {
    echo "check_directory..."
    local DIRECTORY_TO_CHECK=${1}
    local MAKE_DIRECTORY_IF_NO_DIRECTORY=${2}
    if [ -z "${DIRECTORY_TO_CHECK}" ] ; then
        echo "DIRECTORY_TO_CHECK is empty"
        exit 1
    fi

    if [ ! -d ${DIRECTORY_TO_CHECK} ] ; then
        echo "create_aide_target ${DIRECTORY_TO_CHECK} is not a directry"
        if [ -z "${DIRECTORY_TO_CHECK}" ] ; then
            exit 1
        else
            mkdir -p ${DIRECTORY_TO_CHECK}
        fi
    fi
}

create_aide_target() {
    echo "create_aide_target..."

    local SRC_DIR=${1}
    local INSTALL_DIR=${2}

    check_directory "${SRC_DIR}"
    check_directory "${INSTALL_DIR}"

    #cp -r ${SRC_DIR}/core/src/com ${INSTALL_DIR}/gdx-game/src
    #cp -r ${SRC_DIR}/core/src/org ${INSTALL_DIR}/gdx-game/src
    mkdir -p ${INSTALL_DIR}/gdx-game/libs
    cp ${SRC_DIR}/gdx.jar ${INSTALL_DIR}/gdx-game/libs
    mkdir -p ${INSTALL_DIR}/gdx-game-android/libs
    cp ${SRC_DIR}/gdx-backend-android.jar ${INSTALL_DIR}/gdx-game-android/libs
    mkdir -p ${INSTALL_DIR}/gdx-game-android/libs/armeabi
    cp ${SRC_DIR}/armeabi/libgdx.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi
    mkdir -p ${INSTALL_DIR}/gdx-game-android/libs/armeabi-v7a
    cp ${SRC_DIR}/armeabi-v7a/libgdx.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi-v7a
    mkdir -p ${INSTALL_DIR}/gdx-game-android/libs/x86
    cp ${SRC_DIR}/x86/libgdx.so ${INSTALL_DIR}/gdx-game-android/libs/x86
    mkdir -p ${INSTALL_DIR}/gdx-game/libs
    cp ${SRC_DIR}/extensions/gdx-freetype/gdx-freetype.jar ${INSTALL_DIR}/gdx-game/libs
    cp ${SRC_DIR}/extensions/gdx-freetype/gdx-freetype-natives.jar ${INSTALL_DIR}/gdx-game/libs
    cp ${SRC_DIR}/extensions/gdx-freetype/armeabi/libgdx-freetype.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi
    cp ${SRC_DIR}/extensions/gdx-freetype/armeabi-v7a/libgdx-freetype.so ${INSTALL_DIR}/gdx-game-android/libs/armeabi-v7a
    cp ${SRC_DIR}/extensions/gdx-freetype/x86/libgdx-freetype.so ${INSTALL_DIR}/gdx-game-android/libs/x86
    #cp ${SRC_DIR}/libs/tween-engine-api.jar ${INSTALL_DIR}/gdx-game/libs
    #cp ${SRC_DIR}/libs/tween-engine-api-sources.jar ${INSTALL_DIR}/gdx-game/libs
}

# Main program starts here

check_for_dependencies

define_environment_variables

source_my_functions

process_command_line_arguments "$*"

get_archive ${BUILD_DIR} ${SOURCE_ARCHIVE} ${SOURCE_URL} ${SOURCE} ${INTERACTIVE_MODE}

create_aide_target ${BUILD_DIR}/${SOURCE} ${INSTALL_DIR}
