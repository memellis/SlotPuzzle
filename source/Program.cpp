
#include <iostream>
#include <string>
using namespace std;
#include "Program.h"

int main()
{
  string christianName;
  
  cout << "Welcome to SlotPuzzle" << endl;
  cout << "Please enter your Christian name> ";
  cin >> christianName;
  cout << "Hi " << christianName << endl; 
  
  Program::startOperation();
}

int Program::versionHigh;

int Program::versionLow;

void Program::startOperation()
{
  versionHigh = 0;
  versionLow = 1;
  cout << "Welcome to SlotPuzzle version " << versionHigh << "." << versionLow;
}

