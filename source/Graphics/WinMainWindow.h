#ifndef _WINMAINWINDOW_H
#define _WINMAINWINDOW_H


#include <windows.h>
#include <d3dx9.h>
#include "../Resources/resource.h"
#define WINDOW_WIDTH 800
#define WINDOW_HEIGHT 600

//error macro for Error Message dialog box
#define ERROR_MESSAGE(msg){MessageBox(NULL,msg,L"Error",MB_OK|MB_ICONEXCLAMATION);}
class WinMainWindow {
  public:
    int version;

 
 
 
    HRESULT initialise();

    HRESULT render();

    void cleanUp();

    IDirect3D9 *m_pD3D;

    IDirect3DDevice9 *m_pD3DDevice;

    IDirect3DVertexBuffer9 *m_pVertexBuffer;

};
#endif
