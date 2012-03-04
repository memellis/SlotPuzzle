
#include "D3D9Device.h"

D3D9Device::D3D9Device(HWND focusWindow) {
  m_focusWindow = focusWindow;
}

D3D9Device::~D3D9Device() {
}

IDirect3DDevice9* D3D9Device::getD3D9Device() {
  return m_pD3DDevice;
}

void D3D9Device::createDevice() {
  D3DPRESENT_PARAMETERS d3dpp;
  ZeroMemory(&d3dpp, sizeof(d3dpp));
  d3dpp.Windowed = true;
  d3dpp.hDeviceWindow = m_focusWindow;
  d3dpp.BackBufferCount = 1;
  d3dpp.BackBufferWidth = WINDOW_WIDTH;
  d3dpp.BackBufferHeight = WINDOW_HEIGHT;
  d3dpp.BackBufferFormat = D3DFMT_X8R8G8B8;
  d3dpp.SwapEffect = D3DSWAPEFFECT_DISCARD;
  
  m_pD3D = Direct3DCreate9(D3D_SDK_VERSION);
  
  HRESULT hr;
  
  hr = m_pD3D->CreateDevice(D3DADAPTER_DEFAULT,
  			  D3DDEVTYPE_HAL,
  			  m_focusWindow,
  			  D3DCREATE_SOFTWARE_VERTEXPROCESSING,
  			  &d3dpp,
  			  &m_pD3DDevice);
  
  
  
  
  
  
}

void D3D9Device::releaseDevice() {
}

