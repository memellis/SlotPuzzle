#!/bun/bash
# Generate ${SLOTPUZZLE_GIT_DIR}/etc/SPPrototypesTemplateJavaFileso.conf

define_environment_variables() {
    echo "Defining environment variables..."

    if [ -z "${SLOTPUZZLE_GIT_DIR}" ] ; then
        SLOTPUZZLE_GIT_DIR="/usr/local/src/SlotPuzzle"
    fi

    if [ -z  "${AIDE_PROJECT_HOME}" ] ; then
        AIDE_PROJECT_HOME="/sdcard/AppProjects"
    fi

    if [ -z "${SLOTPUZZLE_CORE_SRC}" ] ; then
        SLOTPUZZLE_CORE_SRC=Â"${SLOTPUZZLE_GIT_DIR}/source/java/2d/core/src/java"
    fi

    if [ -z "${SLOTPUZZLE_TEMPLATE_AIDE_SRC_DIR}" ] ; then
        SLOTPUZZLE_TEMPLATE_AIDE_SRC_DIR="${AIDE_PROJECT_HOME}/SPPrototypesTemplate/spprototypes/src"
    fi

    if [ -z "${SLOTPUZZLE_SPPROTOTYPE_JAVA_FILE_LIST}" ] ; then
        SLOTPUZZLE_SPPROTOTYPE_JAVA_FILE_LIST="${SLOTPUZZLE_GIT_DIR}/etc/SPPrototypesTemplateJavaFiles.conf"
    fi
}

generate_files_list() {
    echo "generate_java_files_list..."
    BASE_DIR="${1}"
    FILE_LIST="${2}"
    FILE_FILTER="${3}"
    if [ ! -d "${BASE_DIR}" ] ; then
        echo "Base directory ${BASE_DIR} does not exist"
        exit 1
    fi

    find ${BASE_DIR} -name "${FILE_FILTER}" -print > ${FILE_LIST} 
}

# Main program starts here

define_environment_variables

generate_files_list "${SLOTPUZZLE_TEMPLATE_AIDE_SRC_DIR}" "${SLOTPUZZLE_SPPROTOTYPE_JAVA_FILE_LIST}" "*.java"
