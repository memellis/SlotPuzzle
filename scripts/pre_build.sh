#!/bin/bash
#
GIT_HOME="/c/Program\ Files"
ls ${GIT_HOME}
git branch -v | sed 's/no branch/no_branch/' \
    | awk '/^\*/ { print "#define APP_VERSION \"" $$2 " " $$3 "\"" }' \
    > source/version.h
