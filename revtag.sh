#!/bin/bash

echo "#define APP_VERSION \""`git log -n 1 --pretty="format:%h %ci"`"\"" >source/version.h 
