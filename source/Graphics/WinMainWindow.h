#ifndef _WINMAINWINDOW_H
#define _WINMAINWINDOW_H


#include <windows.h>
#include <d3dx9.h>
#include "../Resources/resource.h"
#include "../Helpers/Clock.h"
#define WINDOW_WIDTH 800
#define WINDOW_HEIGHT 600

//error macro for Error Message dialog box
#define ERROR_MESSAGE(msg){MessageBox(NULL,msg,L"Error",MB_OK|MB_ICONEXCLAMATION);}
class Clock;

class WinMainWindow {
  public:
    int version;

 
 
 
    HRESULT initialise();

    HRESULT render();

    void cleanUp();

    IDirect3D9 *m_pD3D;

    IDirect3DDevice9 *m_pD3DDevice;

    IDirect3DVertexBuffer9 *m_pVertexBuffer;


  private:
    float m_angle;

    D3DXMATRIX m_worldMat;

    D3DXMATRIX m_viewMat;

    D3DXMATRIX m_projectionMat;

    D3DXVECTOR3 m_eyeVec;

    D3DXVECTOR3 m_lookVec;

    D3DXVECTOR3 m_upVec;


  public:
    HRESULT update(float dt);

};
#endif
