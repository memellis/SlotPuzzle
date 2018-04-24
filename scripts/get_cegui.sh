#!/bin/bash
#
# Build CEGui for MinGW
# $1 = (Debug|Release)
#    Default - Debug
#
# Usage: $0 [-d] [-r] [-i]
# -d      Build Debug version of CEGui. This is the default build type."
# -r      Build Release version of CEGui."
# -i      Interactive mode to remove a previous build."
#         The default is to remove a previous build."

# Script assumes hg, cmake are installed 

hash hg 2>/dev/null || { echo >&2 "I require hg but it's not installed.  Aborting."; exit 1; }
hash cmake 2>/dev/null || { echo >&2 "I require cmake but it's not installed.  Aborting."; exit 1; }

CEGUI_DIR=cegui
DEBUG_EXTENSION="_d"
BUILD_TYPE="Debug"
INTERACTIVE_MODE="No"

# Check for environment variables set, if not, set default value
[ -z "${CEGUI_BUILD_DIR}" ] && { CEGUI_BUILD_DIR=${HOME}/build; }
[ -z "${MINGW_HOME}" ] && { MINGW_HOME=/MinGW; }
[ -z "${CEGUI_INSTALL_DIR}" ] && { CEGUI_INSTALL_DIR=${MINGW_HOME}/opt; }
[ -z "${OGRE_INSTALL_DIR}" ] && { OGRE_INSTALL_DIR=${MINGW_HOME}/opt; }
[ -z "${MY_PATCHES_DIR}" ] && { MY_PATCHES_DIR=${HOME}/patches; }
[ -z "${CEGUI_PATCHES_DIR}" ] && { CEGUI_PATCHES_DIR=${MY_PATCHES_DIR}; } 
[ -z "${BOOST_HOME}" ] && { BOOST_HOME=${MINGW_HOME}/opt; } 
[ -z "${DXSDK_DIR}" ] && { DXSDK_DIR="H:/Program Files (x86)/Microsoft DirectX SDK (June 2010)"; export DXSDK_DIR; }

if [ $# -ge 1 ]
then

	if [ $? -ne 0 ]
	then
	    echo ""
	    echo "Usage: ${FULL_SCRIPT_NAME##*/} [-h] [-d] [-r] [-i]"
		echo "  -h      Prints this usage message."
		echo "  -d      Build Debug version of CEGUI. This is the default build type."
		echo "  -r      Build Release version of CEGUI."
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
	        echo "Usage: ${FULL_SCRIPT_NAME##*/} [-h] [-d] [-r] [-i]"
		    echo "  -h      Prints this usage message."
		    echo "  -d      Build Debug version of CEGUI. This is the default build type."
		    echo "  -r      Build Release version of CEGUI."
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
pushd ${CEGUI_BUILD_DIR} > /dev/null

if [ "${INTERACTIVE_MODE}" = "Yes" ] 
then
    if ask "Remove previous ${CEGUI_BUILD_DIR}/${CEGUI_DIR} build"
    then
      rm -fr ${CEGUI_DIR}  
    fi
else
    rm -fr ${CEGUI_DIR}
fi
mkdir -p ${CEGUI_DIR}/build
pushd ${CEGUI_DIR} > /dev/null

hg clone https://bitbucket.org/cegui/cegui ${CEGUI_DIR}
pushd ${CEGUI_DIR} > /dev/null
hg update -C v0-8-2
popd > /dev/null
patch -p0 < ${CEGUI_PATCHES_DIR}/cegui_0.8.2_win32_mingw.patch
patch -p0 < ${CEGUI_PATCHES_DIR}/cegui_0.8.2_win32_mingw_dbghelp_library.patch
patch -p0 < ${CEGUI_PATCHES_DIR}/cegui_0.8.2_win32_mingw_dinput.patch
cp ${OGRE_INSTALL_DIR}/CMake/FindOgre.cmake ${CEGUI_DIR}/cmake
cp ${OGRE_INSTALL_DIR}/CMake/FindOIS.cmake ${CEGUI_DIR}/cmake
cp ${OGRE_INSTALL_DIR}/CMake/FindPkgMacros.cmake ${CEGUI_DIR}/cmake
cp ${OGRE_INSTALL_DIR}/CMake/PreprocessorUtils.cmake ${CEGUI_DIR}/cmake

pushd build > /dev/null
PATH=${MINGW_HOME}:${MINGW_HOME}/bin:${MINGW_HOME}/opt/bin:.
cmake ../${CEGUI_DIR} -DCMAKE_PREFIX_PATH:PATH="${MINGW_HOME};" -DCMAKE_INSTALL_PREFIX:PATH=${CEGUI_INSTALL_DIR} -DCMAKE_BUILD_TYPE:STRING=${BUILD_TYPE} -DCEGUI_BUILD_XMLPARSER_LIBXML2:BOOL=ON -DCEGUI_BUILD_XMLPARSER_EXPAT:BOOL=ON -DCEGUI_BUILD_RENDERER_DIRECT3D10:BOOL=OFF -DCEGUI_BUILD_RENDERER_OGRE:BOOL=ON -DCEGUI_SAMPLES_USE_DIRECT3D10:BOOL=OFF -DCEGUI_BUILD_PYTHON_MODULES:BOOL=OFF -DCEGUI_BUILD_IMAGECODEC_SILLY:BOOL=TRUE -DCEGUI_OPTION_DEFAULT_IMAGECODEC:STRING=SILLYImageCodec -G"Eclipse CDT4 - MinGW Makefiles"
PATH=${CURRENT_WORKING_PATH}
mingw32-make -j5 VERBOSE=1
mingw32-make install
cp ${OGRE_INSTALL_DIR}/bin/${BUILD_TYPE}/plugins${DEBUG_EXTENSION}.cfg ${CEGUI_INSTALL_DIR}/bin
if [ -f ${CEGUI_INSTALL_DIR}/bin/plugins${DEBUG_EXTENSION}.cfg ]
then
   chmod 644 ${CEGUI_INSTALL_DIR}/bin/plugins${DEBUG_EXTENSION}.cfg
fi	   
if [ "${BUILD_TYPE}" = "Debug" ]
then
   cp ${BOOST_HOME}/lib/libboost_system-mgw48-mt-d-1_53.dll ${CEGUI_INSTALL_DIR}/bin
   sed -i "s,PluginFolder=.,PluginFolder=${BUILD_TYPE}," ${CEGUI_INSTALL_DIR}/bin/plugins${DEBUG_EXTENSION}.cfg
else
   cp ${BOOST_HOME}/lib/libboost_system-mgw48-mt-1_53.dll ${CEGUI_INSTALL_DIR}/bin
   sed -i "s,PluginFolder=.,PluginFolder=${BUILD_TYPE}," ${CEGUI_INSTALL_DIR}/bin/plugins${DEBUG_EXTENSION}.cfg
fi
cp ${OGRE_INSTALL_DIR}/bin/${BUILD_TYPE}/resources${DEBUG_EXTENSION}.cfg ${CEGUI_INSTALL_DIR}/bin
sed -i 's,=../..,=../../../../media/,' ${CEGUI_INSTALL_DIR}/bin/resources${DEBUG_EXTENSION}.cfg
chmod 644 ${CEGUI_INSTALL_DIR}/bin/plugins${DEBUG_EXTENSION}.cfg	
chmod 644 ${CEGUI_INSTALL_DIR}/bin/resources${DEBUG_EXTENSION}.cfg
