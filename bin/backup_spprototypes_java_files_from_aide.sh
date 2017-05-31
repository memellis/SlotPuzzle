#!/bin/bash
#
# Backup SPPrototype Java files from AIDE
# The default backup target is /usr/local/src/SlotPuzzle
# Which is configured as a Git repository
#

check_for_dependencies() {
    echo "check_for_dependencies"
}

get_location_of_this_script_execution() {
    LOCATION_OF_THIS_SCRIPT=$(dirname "${0}")
}

define_environment_variables() {
    echo "define_environment_variables..."

    if [ -z "${SLOTPUZZLE_GIT_DIR}" ] ; then
        SLOTPUZZLE_GIT_DIR=/usr/local/src/SlotPuzzle
    fi

    if [ -z "${AIDE_PROJECTS_HOME}" ] ; then
        AIDE_PROJECTS_HOME=/sdcard/AppProjects
    fi

    if [  -z "${SLOTPUZZLE_CORE_SRC}" ] ; then
        SLOTPUZZLE_CORE_SRC="${SLOTPUZZLE_GIT_DIR}/source/java/2d/core/src/java"
    fi

    if [ -z "${SLOTPUZZLE_TEMPLATE_AIDE_HOME}" ] ; then
        SLOTPUZZLE_TEMPLATE_AIDE_HOME="${AIDE_PROJECTS_HOME}/SPPrototypesTemplate"
    fi

    if [ -z "${SLOTPUZZLE_TEMPLATE_AIDE_SRC_HOME}" ] ; then
        SLOTPUZZLE_TEMPLATE_AIDE_SRC_HOME="${AIDE_PROJECTS_HOME}/SPPrototypesTemplate/spprototypes/src"
    fi

    if [ -z "${SLOTPUZZLE_SPPROTOTYPE_HOME}" ] ; then
        SLOTPUZZLE_SPPROTOTYPE_HOME="${SLOTPUZZLE_GIT_DIR}/source/java/2d/SPPrototypesTemplate"
    fi

    if [ -z "${SLOTPUZZLE_SPPROTOTYPE_FILE_LIST}" ] ; then
        SLOTPUZZLE_SPPROTOTYPE_TEMPLATE_FILE_LIST="${SLOTPUZZLE_GIT_DIR}/etc/SPPrototypesTemplateFileList.conf"
    fi

    if [ -z "${SLOTPUZZLE_SPPROTOTYPE_JAVA_FILE_LIST}" ] ; then
        SLOTPUZZLE_SPPROTOTYPE_JAVA_FILE_LIST="${SLOTPUZZLE_GIT_DIR}/etc/SPPrototypesTemplateJavaFiles.conf"
    fi
}

copy_files_from_src_to_dst_via_conf_file() {
    echo "copy_files_from_src_to_dst_via_conf_file..."
    SRC_DIR=${1}
    DST_DIR=${2}
    CONF_FILE=${3}
    for FILE_TO_COPY in `cat ${CONF_FILE}`
    do
        SRC_FILE="${FILE_TO_COPY##*/}"
        DST_PATH_PREFIX="${FILE_TO_COPY##*src/}"
        DST_PATH_PREFIX="${DST_PATH_PREFIX%/*}"
        DST_FILE="${DST_DIR}/${DST_PATH_PREFIX}/${SRC_FILE}"
        DST_DIRNAME="$(dirname ${DST_FILE})"
        if [ -f ${file_to_copy} ] ; then
	    echo "Copying file ${FILE_TO_COPY}"
            echo "To file ${DST_FILE}"
            mkdir -p ${DST_DIRNAME}
            cp ${FILE_TO_COPY} ${DST_FILE}
        else
            echo "Cannot copy file ${SRC_FILE} - it does not exist" 
        fi
    done
}

# Main program starts here

get_location_of_this_script_execution
echo "${LOCATION_OF_THIS_SCRIPT}"

define_environment_variables

copy_files_from_src_to_dst_via_conf_file \
    ${SLOTPUZZLE_TEMPLATE_AIDE_SRC_HOME} \
    ${SLOTPUZZLE_CORE_SRC} \
    ${SLOTPUZZLE_SPPROTOTYPE_JAVA_FILE_LIST}
