#ifndef _D3D9DEVICE_H
#define _D3D9DEVICE_H


#include <d3dx9.h>

#define WINDOW_WIDTH 800
#define WINDOW_HEIGHT 600
class D3D9Device {
  public:
    D3D9Device(HWND focusWindow);

    ~D3D9Device();


  protected:
    IDirect3D9 *m_pD3D;

    IDirect3DDevice9 *m_pD3DDevice;


  public:
    IDirect3DDevice9* getD3D9Device();

    void createDevice();

    void releaseDevice();


  private:
    HWND m_focusWindow;

};
#endif
