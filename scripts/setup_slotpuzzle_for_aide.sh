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

    [ -z "${SLOTPUZZLE_HOME}" ] && { SLOTPUZZLE_HOME=/usr/local/src/SlotPuzzle; }
    [ -z "${BUILD_DIR}" ] && { BUILD_DIR=${SLOTPUZZLE_HOME}/build; mkdir -p ${BUILD_DIR}; }
    [ -z "${INSTALL_DIR}" ] && { INSTALL_DIR=/sdcard/AppProjects; }

    INTERACTIVE_MODE="No"
    LIBGDX_NIGHTIES_URL="https://libgdx.badlogicgames.com/nightlies/libgdx-nightly-latest.zip"
    LIBGDX_NIGHTIES_SOURCE="libgdx-nightly-latest"
    LIBGDX_NIGHTIES_SOURCE_ARCHIVE="libgdx-nightly-latest.zip"
    LIBGDX_BOX2DLIGHTS_ARCHIVE_URL="http://libgdx.badlogicgames.com/box2dlights/box2dlights-1.5-SNAPSHOT.jar"
    LIBGDX_BOX2DLIGHTS_ARCHIVE="box2dlights.jar"
    LIBGDX_BOX2DLIGHTS_SOURCE_ARCHIVE_URL="http://libgdx.badlogicgames.com/box2dlights/box2dlights-1.5-SNAPSHOT-sources.jar"
    LIBGDX_BOX2DLIGHTS_SOURCE_ARCHIVE="box2dlights-sources.jar"
    LIBGDX_UTILS_ARCHIVE_URL="http://search.maven.org/remotecontent?filepath=net/dermetfan/libgdx-utils/libgdx-utils/0.13.3/libgdx-utils-0.13.3.jar"
    LIBGDX_UTILS_ARCHIVE=libgdx-utils-0.13.3.jar
    LIBGDX_AI_ARCHIVE_URL="https://search.maven.org/remotecontent?filepath=com/badlogicgames/gdx/gdx-ai/1.8.1/gdx-ai-1.8.1.jar"
    LIBGDX_AI_ARCHIVE=gdx-ai-1.8.1.jar

    SLOTPUZZLE_NAME="slotpuzzle"
    SPPROTOTYPES_NAME="spprototypes"
    SLOTPUZZLE_2D_SOURCE="${SLOTPUZZLE_HOME}/source/java/2d"
    SPPROTOTYPES_TEMPLATE="${SLOTPUZZLE_2D_SOURCE}/SPPrototypesTemplate"
    SLOTPUZZLE_AIDE_TEMPLATE="${SLOTPUZZLE_2D_SOURCE}/SlotPuzzleAideTemplate"
    SLOTPUZZLE_ANDROID="${SLOTPUZZLE_2D_SOURCE}/android"
    SLOTPUZZLE_SPPROTOTYPES_ANDROID="${SLOTPUZZLE_2D_SOURCE}/androidSPPrototypes"
    SLOTPUZZLE_CORE="${SLOTPUZZLE_2D_SOURCE}/core"
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

    pushd ${BUILD_DIR} > /dev/null || { echo "Could not pushd ${BUILD_DIR}. Aborting..." ; exit 1; }

    if [ ! -f ${SOURCE_ARCHIVE} ]
    then
        wget --no-check-certificate ${SOURCE_URL} -O ${SOURCE_ARCHIVE}
    fi
}

extract_archive() {
    echo "unzip_archive..."
    local BUILD_DIR=${1}
    local SOURCE_ARCHIVE=${2}
    local SOURCE_URL=${3}
    local SOURCE=${4}
    local INTERACTIVE_MODE=${5}


    pushd ${BUILD_DIR} > /dev/null || { echo "Could not pushd ${BUILD_DIR}. Aborting..." ; exit 1; }

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
    pushd ${BUILD_DIR}/${SOURCE} > /dev/null  || { echo "Could not pushd ${BUILD_DIR}/${SOURCE}. Aborting..." ; exit 1; }

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

    local LIBGDX_SRC_DIR=${1}
    local AIDE_INSTALL_DIR=${2}
    local SPPROTOTYPES_TEMPLATE=${3}
    local SLOTPUZZLE_CORE=${4}
    local SLOTPUZZLE_ANDROID=${5}
    local SLOTPUZZLE_NAME=${6}

    check_directory "${LIBGDX_SRC_DIR}"
    check_directory "${AIDE_INSTALL_DIR}"

    mkdir -p ${AIDE_INSTALL_DIR}
    cp -r ${SLOTPUZZLE_2D_SOURCE}/SlotPuzzleAideTemplate/${SLOTPUZZLE_NAME} ${AIDE_INSTALL_DIR}
    cp -r ${SLOTPUZZLE_2D_SOURCE}/SlotPuzzleAideTemplate/${SLOTPUZZLE_NAME}-android ${AIDE_INSTALL_DIR}
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/src
    cp -r ${SLOTPUZZLE_CORE}/src/java/com ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/src
    cp -r ${SLOTPUZZLE_CORE}/src/java/org ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/src

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${LIBGDX_SRC_DIR}/gdx.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${SLOTPUZZLE_ANDROID}/AndroidManifest.xml ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/src
    cp -r ${SLOTPUZZLE_ANDROID}/src/com ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/src

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/handle
    cp ${SLOTPUZZLE_ANDROID}/assets/handle/handle.pack.atlas ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/handle
    cp ${SLOTPUZZLE_ANDROID}/assets/handle/handle.pack.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/handle
    cp ${SLOTPUZZLE_ANDROID}/assets/handle/handle.pack2.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/handle
    cp ${SLOTPUZZLE_ANDROID}/assets/handle/handle.pack3.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/handle
    cp ${SLOTPUZZLE_ANDROID}/assets/handle/handle.pack4.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/handle

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp ${SLOTPUZZLE_ANDROID}/assets/levels/blue.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp ${SLOTPUZZLE_ANDROID}/assets/levels/entrance01.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp ${SLOTPUZZLE_ANDROID}/assets/levels/entrance02.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/level 1 - 40x40.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/level 2 - 40x40.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/level 3 - 40x40.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/level 4 - 40x40.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/level 5 - 40x40.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/mini slot machine level.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/mini slot machine level 1.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/light_pink_transparent 1 40x40.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/light_pink_transparent 40x40.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/neon_tiles 40x40.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/slot_puzzle_title.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/WorldMap.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/world_image.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/levels
    cp "${SLOTPUZZLE_ANDROID}/assets/levels/mini slot machine level.tmx" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets
    cp "${SLOTPUZZLE_ANDROID}/assets/LiberationMono-Regular.ttf" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/loading_screen
    cp "${SLOTPUZZLE_ANDROID}/assets/loading_screen/progress_bar.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/loading_screen
    cp "${SLOTPUZZLE_ANDROID}/assets/loading_screen/progress_bar_base.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/loading_screen

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/particlesè
    cp "${SLOTPUZZLE_ANDROID}/assets/particles/explosion.particle" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/particles
    cp "${SLOTPUZZLE_ANDROID}/assets/particles/fire.particle" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/particles
    cp "${SLOTPUZZLE_ANDROID}/assets/particles/fire.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/particles
    cp "${SLOTPUZZLE_ANDROID}/assets/particles/ice.particle" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/particles
    cp "${SLOTPUZZLE_ANDROID}/assets/particles/stars.particle" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/particles

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/playingcards
    cp "${SLOTPUZZLE_ANDROID}/assets/playingcards/carddeck.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/playingcards
    cp "${SLOTPUZZLE_ANDROID}/assets/playingcards/carddeck.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/playingcards
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/reel
    cp "${SLOTPUZZLE_ANDROID}/assets/reel/reels.pack.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/reel
    cp "${SLOTPUZZLE_ANDROID}/assets/reel/reels.pack.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/reel
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/slot_handle
    cp ${SLOTPUZZLE_ANDROID}/assets/slot_handle/slot_handle.pack.atlas ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/slot_handle
    cp ${SLOTPUZZLE_ANDROID}/assets/slot_handle/slot_handle.pack.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/slot_handle

    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/cha-ching.mp3" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/click2.wav" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/jackpot.mp3" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/pull-lever.mp3" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/pull-lever1.mp3" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/pull-lever1.wav" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/reel-spinning.mp3" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/reel-stopped.mp3" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    cp "${SLOTPUZZLE_ANDROID}/assets/sounds/reel-stopped.wav" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/sounds
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/pack.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/puzzle.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/slot.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/splash1.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/splash2.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/splash3.pack.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    cp "${SLOTPUZZLE_ANDROID}/assets/splash/splash3.pack.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/splash
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/complete.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/game.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/GamePopUp.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/interactive.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/level.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/over.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/tiles.pack.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/tiles.pack.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    cp "${SLOTPUZZLE_ANDROID}/assets/tiles/white.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/tiles
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/default.fnt" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui/default-font.fnt
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/default.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui/default-fnt.png
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/default.fnt" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/default.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-blue.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-blue.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-commons.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-commons.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-gray.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-gray.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-green.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-green.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-orange.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-orange.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-red.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-red.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-white.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-white.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-yellow.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/ui-yellow.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/uiskin.atlas" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/uiskin.json" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui
    cp "${SLOTPUZZLE_ANDROID}/assets/ui/uiskin.png" ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets/ui

    cp "${SLOTPUZZLE_ANDROID}/assets/arial-15.fnt ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets
    cp "${SLOTPUZZLE_ANDROID}/assets/arial-15_00.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets
    cp "${SLOTPUZZLE_ANDROID}/assets/arial-32.fnt ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets
    cp "${SLOTPUZZLE_ANDROID}/assets/arial-32.png ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets
    cp "${SLOTPUZZLE_ANDROID}/assets/badlogicsmall.jpg ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/assets


    cp ${LIBGDX_SRC_DIR}/gdx-backend-android.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi
    cp ${LIBGDX_SRC_DIR}/armeabi/libgdx.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi-v7a
    cp ${LIBGDX_SRC_DIR}/armeabi-v7a/libgdx.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi-v7a
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/x86
    cp ${LIBGDX_SRC_DIR}/x86/libgdx.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/x86
    mkdir -p ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs

    cp ${LIBGDX_SRC_DIR}/extensions/gdx-freetype/gdx-freetype.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-freetype/gdx-freetype-natives.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-freetype/armeabi/libgdx-freetype.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-freetype/armeabi-v7a/libgdx-freetype.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi-v7a
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-freetype/x86/libgdx-freetype.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/x86

    cp ${LIBGDX_SRC_DIR}/extensions/gdx-box2d/gdx-box2d.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-box2d/gdx-box2d-natives.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-box2d/armeabi/libgdx-box2d.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-box2d/armeabi-v7a/libgdx-box2d.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/armeabi-v7a
    cp ${LIBGDX_SRC_DIR}/extensions/gdx-box2d/x86/libgdx-box2d.so ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-android/libs/x86

    cp ${BUILD_DIR}/${LIBGDX_BOX2DLIGHTS_ARCHIVE} ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${BUILD_DIR}/${LIBGDX_BOX2DLIGHTS_SOURCE_ARCHIVE} ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${SLOTPUZZLE_2D_SOURCE}/libs/tween-engine-api.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${SLOTPUZZLE_2D_SOURCE}/libs/tween-engine-api-sources.jar ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs

    cp ${BUILD_DIR}/${LIBGDX_UTILS_ARCHIVE} ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs
    cp ${BUILD_DIR}/${LIBGDX_AI_ARCHIVE} ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}/libs


    cp -r ${SLOTPUZZLE_AIDE_TEMPLATE}/slotpuzzle-prototypes-android ${AIDE_INSTALL_DIR}
    cp -r ${AIDE_INSTALL_DIR}/slotpuzzle-android/assets ${AIDE_INSTALL_DIR}/slotpuzzle-prototypes-android

    cp ${SLOTPUZZLE_SPPROTOTYPES_ANDROID}/src/com/ellzone/spprototypes/android/SPPrototypeActivity.java ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-prototypes-android/src/com/ellzone/spprototypes/android
    cp ${SLOTPUZZLE_SPPROTOTYPES_ANDROID}/src/com/ellzone/spprototypes/android/AndroidSPPrototypeStarter.java ${AIDE_INSTALL_DIR}/${SLOTPUZZLE_NAME}-prototypes-android/src/com/ellzone/spprototypes/android
}

# Main program starts here

check_for_dependencies

define_environment_variables

source_my_functions

process_command_line_arguments "$*"

get_archive ${BUILD_DIR} ${LIBGDX_NIGHTIES_SOURCE_ARCHIVE} ${LIBGDX_NIGHTIES_URL}

extract_archive ${BUILD_DIR} ${LIBGDX_NIGHTIES_SOURCE_ARCHIVE} ${LIBGDX_NIGHTIES_URL} ${LIBGDX_NIGHTIES_SOURCE} ${INTERACTIVE_MODE}

get_archive ${BUILD_DIR} ${LIBGDX_BOX2DLIGHTS_ARCHIVE} ${LIBGDX_BOX2DLIGHTS_ARCHIVE_URL}

get_archive ${BUILD_DIR} ${LIBGDX_BOX2DLIGHTS_SOURCE_ARCHIVE} ${LIBGDX_BOX2DLIGHTS_SOURCE_ARCHIVE_URL}

get_archive ${BUILD_DIR} ${LIBGDX_UTILS_ARCHIVE} ${LIBGDX_UTILS_ARCHIVE_URL}

get_archive ${BUILD_DIR} ${LIBGDX_AI_ARCHIVE} ${LIBGDX_AI_ARCHIVE_URL}

create_aide_target ${BUILD_DIR}/${LIBGDX_NIGHTIES_SOURCE} ${INSTALL_DIR}/SlotPuzzle ${SPPROTOTYPES_TEMPLATE} ${SLOTPUZZLE_CORE} ${SLOTPUZZLE_ANDROID} ${SLOTPUZZLE_NAME}

