#ifndef _CLOCK_H
#define _CLOCK_H


#include <windows.h>
class Clock {
  public:
    void initialise();

    float update();


  private:
    __int64 m_initialTimeStamp;

    __int64 m_currentTimeStamp;

    float m_secondsPerCount;


  public:
    Clock();

};
#endif
