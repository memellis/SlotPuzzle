
#include "Clock.h"

void Clock::initialise() {
  QueryPerformanceCounter((LARGE_INTEGER*)&m_initialTimeStamp);
}

float Clock::update() {
  QueryPerformanceCounter((LARGE_INTEGER*)&m_currentTimeStamp);
  float dt = ((float)m_currentTimeStamp - (float)m_initialTimeStamp) * m_secondsPerCount;;
  m_initialTimeStamp = m_currentTimeStamp;
  return dt;
}

Clock::Clock() {
  __int64 countsPerSec = m_initialTimeStamp = m_currentTimeStamp = 0;
  m_secondsPerCount = 0.0f;
  
  QueryPerformanceFrequency((LARGE_INTEGER*)&countsPerSec);
  m_secondsPerCount = 1.0f/(float)countsPerSec;
}

