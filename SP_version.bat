et OS_GIT_REPO=c:\OpenSim_GIT
:: ================================
:: Appending Path to access all binaries used by GIT
::
PATH = C:\Program Files (x86)\Git\bin;%PATH%; 
echo %PATH%
::
echo ON
cd %OS_GIT_REPO%
::
sh.exe -x revtag.sh
