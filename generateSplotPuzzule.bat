rem Copies BoUML generated SlotPuzzle source files to the Eclipse SlotPuzzle project
rem

set MY_DEVELOP=%USERPROFILE%\Documents\My Develop
set SLOTPUZZLE_DESIGN_HOME=%MY_DEVELOP%\SlotPuzzle\source

set SLOTPUZZLE_IDE_HOME=%USERPROFILE%\workspace\SlotPuzzle

copy "%SLOTPUZZLE_DESIGN_HOME%\GameBoard.cpp" "%SLOTPUZZLE_IDE_HOME%\GameBoard.cpp"
copy "%SLOTPUZZLE_DESIGN_HOME%\GameBoard.h" "%SLOTPUZZLE_IDE_HOME%"
copy "%SLOTPUZZLE_DESIGN_HOME%\ScoreBoard.cpp" "%SLOTPUZZLE_IDE_HOME%"
copy "%SLOTPUZZLE_DESIGN_HOME%\ScoreBoard.h" "%SLOTPUZZLE_IDE_HOME%"
copy "%SLOTPUZZLE_DESIGN_HOME%\SlotMachineSlot.cpp" "%SLOTPUZZLE_IDE_HOME%"
copy "%SLOTPUZZLE_DESIGN_HOME%\SlotMachineSlot.h" "%SLOTPUZZLE_IDE_HOME%"
copy "%SLOTPUZZLE_DESIGN_HOME%\SlotPuzzle.cpp" "%SLOTPUZZLE_IDE_HOME%"
copy "%SLOTPUZZLE_DESIGN_HOME%\SlotPuzzle.h" "%SLOTPUZZLE_IDE_HOME%"


