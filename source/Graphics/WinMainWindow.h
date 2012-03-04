#ifndef _WINMAINWINDOW_H
#define _WINMAINWINDOW_H


#include <d3dx9.h>
#include <windows.h>
#include <commctrl.h>
#include "../Resources/resource.h"

#define MY_FVF D3DFVF_XYZ | D3DFVF_DIFFUSE
#define CYLINDER_LENGTH 50
#define WINDOW_WIDTH 800
#define WINDOW_HEIGHT 600
//error macro for Error Message dialog box
#define ERROR_MESSAGE(msg){MessageBox(NULL,msg,L"Error",MB_OK|MB_ICONEXCLAMATION);}

LPDIRECT3DTEXTURE9 g_pTexture = NULL; // Our texture

// A structure for our custom vertex type. We added texture coordinates
struct CUSTOMVERTEX
{
    D3DXVECTOR3 position; // The position
    D3DCOLOR color; // The color
#ifndef SHOW_HOW_TO_USE_TCI
    FLOAT tu, tv; // The texture coordinates
#endif
};

// Our custom FVF, which describes our custom vertex structure
#ifdef SHOW_HOW_TO_USE_TCI
#define D3DFVF_CUSTOMVERTEX (D3DFVF_XYZ|D3DFVF_DIFFUSE)
#else
#define D3DFVF_CUSTOMVERTEX (D3DFVF_XYZ|D3DFVF_DIFFUSE|D3DFVF_TEX1)
#endif
class Clock;
class Reel;
class D3D9Device;

class WinMainWindow {
  public:
    WinMainWindow(IDirect3DDevice9* pD3DDevice);

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
