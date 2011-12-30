@echo off
set OS_GIT_REPO="c:\Users\Mark Ellis\Documents\My Develop\SlotPuzzle"
:: ================================
:: Appending Path to access all binaries used by GIT
::
PATH = C:\Program Files (x86)\Git\bin;%PATH%; 
::
cd %OS_GIT_REPO%
::
sh.exe -x revtag.sh
