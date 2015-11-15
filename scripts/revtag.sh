#!/bin/bash

# Creates source/version.h
# With the following #defines
#
# SLOTPUZZLE_HASH_COMMIT
# SLOTPUZZLE_HASH_COMMIT_TIME
# SLOTPUZZLE_BUILD_NUMBER

SLOTPUZZLE_HASH_COMMIT="0x`git log -n 1 --pretty="format:%h"`"
SLOTPUZZLE_HASH_COMMIT_TIME="\""`git log -n 1 --pretty="format:%ci"`"\""
SLOTPUZZLE_BUILD_NUMBER="`git rev-list --all | wc -l | sed "s/[ \t]//g"`"

echo "/*" > source/Version.h
echo " * Version.h" >> source/Version.h
echo " * Created on: "`date` >> source/Version.h
echo " *     Author: Mark Ellis" >> source/Version.h
echo " *" >> source/Version.h
echo "*/" >> source/Version.h
echo "" >> source/Version.h
echo "#ifndef VERSION_H_" >> source/Version.h
echo "#define VERSION_H_" >> source/Version.h
echo "" >> source/Version.h
echo "#define SLOTPUZZLE_HASH_COMMIT ${SLOTPUZZLE_HASH_COMMIT}" >>source/Version.h 
echo "#define SLOTPUZZLE_HASH_COMMIT_TIME ${SLOTPUZZLE_HASH_COMMIT_TIME}" >>source/Version.h 
echo "#define SLOTPUZZLE_BUILD_NUMBER ${SLOTPUZZLE_BUILD_NUMBER}" >>source/Version.h
echo "#define SLOTPUZZLE_FILE_VERSION \"0,1,${SLOTPUZZLE_BUILD_NUMBER},${SLOTPUZZLE_HASH_COMMIT}\"" >>source/version.h
echo "#define SLOTPUZZLE_PRODUCT_VERSION \"0,1,${SLOTPUZZLE_BUILD_NUMBER},${SLOTPUZZLE_HASH_COMMIT}\"" >>source/version.h
echo "" >>source/Version.h
echo "#endif /* VERSION_H_ */" >> source//Version.h

