#!/bin/bash

# Creates source/version.h
# With the following #defines
#
# SLOTPUZZLE_HASH_COMMIT
# SLOTPUZZLE_HASH_COMMIT_TIME
# SLOTPUZZLE_BUILD_NUMBER

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
echo "#define SLOTPUZZLE_HASH_COMMIT 0x"`git log -n 1 --pretty="format:%h"`"" >>source/Version.h 
echo "#define SLOTPUZZLE_HASH_COMMIT_TIME \""`git log -n 1 --pretty="format:%ci"`"\"" >>source/Version.h 
echo "#define SLOTPUZZLE_BUILD_NUMBER "`git rev-list --all | wc -l | sed "s/\[ \t]//g"`"" >>source/Version.h
echo "" >>source/Version.h
echo "#endif /* VERSION_H_ */" >> source//Version.h

