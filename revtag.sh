#!/bin/bash

echo "#define APP_VERSION \""`git log -n 1 --pretty="format:%h %ci"`"\"" >source/version.h 
echo "#define SLOTPUZZLE_BUILD \""`git rev-list --all | wc -l | sed "s/\[ \t]//g"`"\"" >>source/version.h
