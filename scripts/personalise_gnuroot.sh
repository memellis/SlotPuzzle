#!/bin/bash
#
# Personalise GNURoot
#
# Start of code execution

FILE_TO_APPEND_TO_BASH_BASHRC=file_to_append_to_bash.bashrc
ETC_BASH_BASHRC_FILE=/etc/bash.bashrc
PERSONALISED_GNUROOT_TAG="Personalised GNURoot" 
CURRENT_DATE_TIME=`date +%Y%m%d-%H%M`

SRC_ROOT_BASHRC=root_bashrc
DEST_ROOT_BASHRC=/root/.bashrc


if [ -f ${DEST_ROOT_BASHRC} ] ; then
    DEST_ROOT_BASHRC_BACKUP=${DEST_ROOT_BASHRC}.${CURRENT_DATE_TIME}
    echo "Saving ${DEST_ROOT_BASHRC} to ${DEST_ROOT_BASHRC_BACKUP}"
    cp ${DEST_ROOT_BASHRC} ${DEST_ROOT_BASHRC_BACKUP}
fi
cp ${SRC_ROOT_BASHRC} ${DEST_ROOT_BASHRC}

if grep "Personalised GNURoot" ${ETC_BASH_BASHRC_FILE} > /dev/null ; then
    echo "File already to added to bash.bashrc"
else
    echo "Will source bashrc to bash.bashrc"
    echo "" >> ${ETC_BASH_BASHRC_FILE}
    echo "# ${PERSONALISED_GNUROOT_TAG} added on ${CURRENT_DATE_TIME}" >> ${ETC_BASH_BASHRC_FILE}
    cat ${FILE_TO_APPEND_TO_BASH_BASHRC} >> ${ETC_BASH_BASHRC_FILE} 
fi


