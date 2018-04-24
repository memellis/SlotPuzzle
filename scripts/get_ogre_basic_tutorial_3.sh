#!/bin/bash
#
# Build OGRE Tutorial 3 via MinGW
#

# Script assumes hg, cmake are installed 

hash cmake 2>/dev/null || { echo >&2 "I require cmake but it's not installed.  Aborting."; exit 1; }
hash unzip 2>/dev/null || { echo >&2 "I require unzip but it's not installed.  Aborting."; exit 1; }
hash wget 2>/dev/null || { echo >&2 "I require wget but it's not installed.  Aborting."; exit 1; }

# Check for environment variables set, if not, set default value
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/h/MinGW_1; MINGW_HOME_WINDOWS="h:/MinGW_1"; }
[ -z "${BUILD_DIR}" ] && { BUILD_DIR=${HOME}/build; }
[ -z "${MY_DOWNLOAD_DIR}" ] && { MY_DOWNLOAD_DIR=${HOME}/downloads; }

DEBUG_EXTENSION="_d"
BUILD_TYPE="Debug"
INTERACTIVE_MODE="No"
OGRE_BASIC_TUTORIAL3_DIR=ogre_basic_tutorial_3

SOURCE_URL="http://www.ogre3d.org/tikiwiki/tiki-download_wiki_attachment.php?attId=141&download=y"
SOURCE_ARCHIVE=clean_ogre_cmake_project.zip
SOURCE="${SOURCE_ARCHIVE%.*}"

echo $SOURCE

if [ $# -ge 1 ]
then

	if [ $? -ne 0 ]
	then
	    echo ""
	    echo "Usage: $0 [-h] [-d] [-r] [-i]"
		echo "  -h      Prints this usage message."
		echo "  -d      Build Debug version of OGRE Tutorial 3. This is the default build type."
		echo "  -r      Build Release version of OGRE Tutorial 3."
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
		    echo "  -d      Build Debug version of OGRE Tutorial 3. This is the default build type."
		    echo "  -r      Build Release version of OGRE Tutorial 3."
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

echo "Build type is ${BUILD_TYPE}."
echo "Interactive Mode is ${INTERACTIVE_MODE}."

# Source general-purpose functions
if [ -f ${HOME}/bin/my_functions.sh ]
then
    source ${HOME}/bin/my_functions.sh
fi

CURRENT_WORKING_PATH=${PATH}
pushd ${BUILD_DIR} > /dev/null

if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${BUILD_DIR}/${OGRE_BASIC_TUTORIAL3_DIR} build"
    then
      rm -fr ${OGRE_BASIC_TUTORIAL3_DIR}  
    fi
else
    rm -fr ${OGRE_BASIC_TUTORIAL3_DIR}
fi

# Get source archive if necessary
if [ ! -f ${MY_DOWNLOAD_DIR}/${SOURCE_ARCHIVE} ]
then
	mkdir -p ${MY_DOWNLOAD_DIR}
    pushd ${MY_DOWNLOAD_DIR} > /dev/null
	echo "Downloading ${SOURCE_ARCHIVE}"
	wget ${SOURCE_URL} -O ${SOURCE_ARCHIVE}
	popd > /dev/null
fi

mkdir -p ${OGRE_BASIC_TUTORIAL3_DIR}/build
pushd ${OGRE_BASIC_TUTORIAL3_DIR} > /dev/null
unzip ${MY_DOWNLOAD_DIR}/${SOURCE_ARCHIVE}

CWD=${PATH}
sed -i 's,set(OGRE_BOOST_COMPONENTS thread date_time),set(OGRE_BOOST_COMPONENTS system thread date_time),' ${SOURCE}/CMakeLists.txt
sed -i 's,${OGRE_PLUGIN_DIR_REL}/libOIS.dll,${OGRE_PLUGIN_DIR_REL}/OIS.dll,' ${SOURCE}/CMakeLists.txt
sed -i 's,${OGRE_PLUGIN_DIR_DBG}/libOIS_d.dll,${OGRE_PLUGIN_DIR_DBG}/OIS_d.dll,' ${SOURCE}/CMakeLists.txt

# CEGUI dependencies
mkdir -p clean_ogre_cmake_project/cmake_modules
cp ${BUILD_DIR}/ogre_tutorial_framework/scripts/cmake_modules/findCEGUI.cmake ${SOURCE}/cmake_modules
cp ${BUILD_DIR}/ogre/ogre/Cmake/Utils/FindPkgMacros.cmake ${SOURCE}/cmake_modules
cp ${BUILD_DIR}/ogre/ogre/Cmake/Utils/PreprocessorUtils.cmake ${SOURCE}/cmake_modules
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_cmake_project_findcegui.patch
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_cmake_project_resourcecfg.patch
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_cmake_project_cegui_include.patch
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_cmake_project_debug.patch
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_cmake_project_link_boost.patch
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_cmake_project_install_cg_dll.patch
patch -p0 < ${BUILD_DIR}/ogre_tutorial_framework/patch/ogre_basic_tutorial3_terrain_library.patch
cp ${BUILD_DIR}/ogre_tutorial_framework/src/BasicTutorial3Application.h ${SOURCE}/TutorialApplication.h
cp ${BUILD_DIR}/ogre_tutorial_framework/src/BasicTutorial3Application.cpp ${SOURCE}/TutorialApplication.cpp

if [ "${BUILD_TYPE}" = "Debug" ]
then
    cp clean_ogre_cmake_project/dist/bin/plugins.cfg ${SOURCE}/dist/bin/plugins_d.cfg
    cp clean_ogre_cmake_project/dist/bin/resources.cfg ${SOURCE}/dist/bin/resources_d.cfg
    sed -i "s,/usr/local/lib/OGRE,${MINGW_HOME_WINDOWS}/opt/bin/debug," ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,# Plugin=RenderSystem_Direct3D9, Plugin=RenderSystem_Direct3D9_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin=RenderSystem_GL,Plugin=RenderSystem_GL_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin_ParticleFX,Plugin_ParticleFX_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin=Plugin_BSPSceneManager,Plugin=Plugin_BSPSceneManager_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin=Plugin_CgProgramManager,Plugin=Plugin_CgProgramManager_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin=Plugin_PCZSceneManager,Plugin=Plugin_PCZSceneManager_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin=Plugin_OctreeZone,Plugin=Plugin_OctreeZone_d,' ${SOURCE}/dist/bin/plugins_d.cfg
    sed -i 's,Plugin=Plugin_OctreeSceneManager,Plugin=Plugin_OctreeSceneManager_d,' ${SOURCE}/dist/bin/plugins_d.cfg
	sed -i "s,../media,${MINGW_HOME_WINDOWS}/opt/media,g" ${SOURCE}/dist/bin/resources_d.cfg
	sed -i "s,path_to_cegui,${MINGW_HOME_WINDOWS}/opt/share/cegui-0,g" ${SOURCE}/dist/bin/resources_d.cfg
else
    sed -i "s,/usr/local/lib/OGRE,${MINGW_HOME_WINDOWS}/opt/bin/release," ${SOURCE}/dist/bin/plugins.cfg
    sed -i 's,# Plugin=RenderSystem_Direct3D9, Plugin=RenderSystem_Direct3D9,' ${SOURCE}/dist/bin/plugins.cfg
	sed -i "s,../media,${MINGW_HOME_WINDOWS}/opt/media,g" ${SOURCE}/dist/bin/resources.cfg
	sed -i "s,path_to_cegui,${MINGW_HOME_WINDOWS}/opt/share/cegui-0,g" ${SOURCE}/dist/bin/resources.cfg	
fi

sed -i 's,\[Imagesets\],\[imagesets\],' ${SOURCE}/dist/bin/resources${DEBUG_EXTENSION}.cfg
sed -i 's,\[Fonts\],\[fonts\],' ${SOURCE}/dist/bin/resources${DEBUG_EXTENSION}.cfg
sed -i 's,\[Schemes\],\[schemes\],' ${SOURCE}/dist/bin/resources${DEBUG_EXTENSION}.cfg
sed -i 's,\[LookNFeel\],\[looknfeel\],' ${SOURCE}/dist/bin/resources${DEBUG_EXTENSION}.cfg
sed -i 's,\[Layouts\],\[layouts\],' ${SOURCE}/dist/bin/resources${DEBUG_EXTENSION}.cfg
	
cd build
PATH=${MINGW_HOME}:${MINGW_HOME}/bin:${MINGW_HOME}/opt/bin:.
cmake ../clean_ogre_cmake_project -DCMAKE_INSTALL_PREFIX:PATH=${BUILD_DIR}/${SOURCE}/build/dist -DCMAKE_BUILD_TYPE:STRING=${BUILD_TYPE} -DCMAKE_PREFIX_PATH:PATH="${MINGW_HOME}/opt;${MINGW_HOME}/opt/include/cegui-0;" -DCMAKE_MODULE_PATH:PATH=${MINGW_HOME}/opt/CMake -G"Eclipse CDT4 - MinGW Makefiles"
mingw32-make VERBOSE=1
mingw32-make install
PATH=${CWD}
cp ${MINGW_HOME}/opt/bin/${BUILD_TYPE}/OgreTerrain${DEBUG_EXTENSION}.dll dist/bin
cp ${MINGW_HOME}/opt/bin/${BUILD_TYPE}/OgrePaging${DEBUG_EXTENSION}.dll dist/bin

exit 0