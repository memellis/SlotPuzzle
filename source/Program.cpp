
#include <iostream>
#include <string>
using namespace std;

#ifdef SLOTPUZZLE_DEBUG
#include <cppunit/ui/text/TestRunner.h>
#endif
#include "Version.h"
#include "Program.h"

int main()
{
  string christianName;
  
  cout << "Welcome to SlotPuzzle" << endl;
  cout << "Please enter your Christian name> ";
  cin >> christianName;
  cout << "Hi " << christianName << endl; 
  
#ifdef SLOTPUZZLE_DEBUG
  cout << "Time for some unit testing" << endl;
#else
  Program::startOperation();
#endif
}

int Program::versionHigh;

int Program::versionLow;

void Program::startOperation()
{
  versionHigh = SLOTPUZZLE_BUILD_NUMBER;
  versionLow = SLOTPUZZLE_HASH_COMMIT;
  static const char *versionBuildTime = SLOTPUZZLE_HASH_COMMIT_TIME;
  cout << "Welcome to SlotPuzzle version " << versionHigh << "." << versionLow << " built on: " << versionBuildTime;
}

char *Program::versionBuildTime;

