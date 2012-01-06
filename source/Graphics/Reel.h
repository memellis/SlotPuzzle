#ifndef _REEL_H
#define _REEL_H


#include <windows.h>
#include <d3dx9.h>
struct CUSTOM_VERTEX
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
//error macro for Error Message dialog box
#define ERROR_MESSAGE(msg){MessageBox(NULL,msg,L"Error",MB_OK|MB_ICONEXCLAMATION);}

class Reel {
  private:
    CUSTOM_VERTEX *vertices;


  public:
    Reel();

    HRESULT initialise(int reelResolution, float reelDiameter, float reelWidth, IDirect3DDevice9 *& d3dDevice);


  private:
    IDirect3DVertexBuffer9 *vertexBuffer;

    LPDIRECT3DTEXTURE9 texture;

};
#endif
