
#include <iostream>
#include <string>
using namespace std;

#ifdef SLOTPUZZLE_DEBUG
#include <cppunit/ui/text/TestRunner.h>
#endif

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
  versionHigh = 0;
  versionLow = 1;
  cout << "Welcome to SlotPuzzle version " << versionHigh << "." << versionLow;
}

