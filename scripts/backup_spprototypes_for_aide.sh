#!/bin/bash
#
# Set-up SPProtypes to run on Android via AIDE
#

check_for_dependencies() {
    echo "check_for_dependencies..."
}

get_location_of_this_script_execution() {
    LOCATION_OF_THIS_SCRIPT=$(dirname "${0}")
}


backup_spprototypes_files() {
    echo "backup_spprototypes_files..."
    for file_to_copy in `cat ${SPPROTOTYPES_FILE_LIST}`
    do
        SRC_FILE="${AIDE_PROJECTS_HOME}/SPPrototypesTemplate-13052017/${file_to_copy}"
        DST_FILE="${SLOTPUZZLE_SPPROTOTYPE_HOME}/${file_to_copy}"
        DST_DIR="$(dirname ${DST_FILE})"
        if [ -f ${SRC_FILE} ] ; then
	    echo "Copying file ${SRC_FILE}"
            echo "To file ${DST_FILE}"
            mkdir -p ${DST_DIR}
            cp ${SRC_FILE} ${DST_FILE}
        else
            echo "Cannot copy file ${SRC_FILE} - it does not exist" 
        fi
    done
}

# Main program starts here

if [ -z "${SLOTPUZZLE_GIT_DIR}" ] ; then
    SLOTPUZZLE_GIT_DIR=/usr/local/src/SlotPuzzle
fi

if [ -z "${AIDE_PROJECTS_HOME}" ] ; then
    AIDE_PROJECTS_HOME=/sdcard/AppProjects
fi

if [  -z "${SLOTPUZZLE_SPPROTOTYPE_HOME}" ] ; then
    SLOTPUZZLE_SPPROTOTYPE_HOME="${SLOTPUZZLE_GIT_DIR}/source/java/2d/SPPrototypesTemplate"
fi

if [ -z "${SLOTPUZZLE_TEMPLATE_AIDE_SRC_HOME}" ] ; then
    SLOTPUZZLE_TEMPLATE_AIDE_SRC_HOME="${AIDE_PROJECTS_HOME}/SPPrototypesTemplate/spprototypes/src"
fi

if [ -z "${SPPROTOTYPES_FILE_LIST}" ] ; then
    SPPROTOTYPES_FILE_LIST=${SLOTPUZZLE_GIT_DIR}/etc/SPPrototypesTemplateFileList.conf
fi

get_location_of_this_script_execution
echo "${LOCATION_OF_THIS_SCRIPT}"

backup_spprototypes_files

