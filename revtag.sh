#!/bin/bash

echo "#define SLOTPUZZLE_HASH_COMMIT \""`git log -n 1 --pretty="format:%h"`"\"" >source/version.h 
echo "#define SLOTPUZZLE_HASH_COMMIT_TIME \""`git log -n 1 --pretty="format:%ci"`"\"" >>source/version.h 
echo "#define SLOTPUZZLE_BUILD \""`git rev-list --all | wc -l | sed "s/\[ \t]//g"`"\"" >>source/version.h
