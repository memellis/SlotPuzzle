#!/bin/bash
#
# Copy java code from SlotPuzzle for AIDE to the 
# SlotPuzzle local git repository.

COM_SRC_DIR=/sdcard/AppProjects/SlotPuzzle/slotpuzzle/src/com
ORG_SRC_DIR=/sdcard/AppProjects/SlotPuzzle/slotpuzzle/src/org
DEST_DIR=/usr/local/src/SlotPuzzle/source/java/2d/core/src/java

cp -r ${COM_SRC_DIR} ${DEST_DIR}
cp -r ${ORG_SRC_DIR} ${DEST_DIR}
