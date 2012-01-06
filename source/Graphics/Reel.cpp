
#include "Reel.h"

Reel::Reel() {
}

HRESULT Reel::initialise(int reelResolution, float reelDiameter, float reelWidth, IDirect3DDevice9 *& d3dDevice) {
  if( FAILED(d3dDevice->CreateVertexBuffer(reelResolution*2*sizeof(CUSTOM_VERTEX),
                                           0, D3DFVF_CUSTOMVERTEX,
  		  	  	  	 D3DPOOL_DEFAULT, &vertexBuffer, NULL)))
  {
      ERROR_MESSAGE(L"Failed to lock vertex buffer.");
      return E_FAIL;
  }
  
  // Fill the vertex buffer. We are setting the tu and tv texture
  // coordinates, which range from 0.0 to 1.0
  
  if(FAILED(vertexBuffer->Lock(0, 0, (void**)&vertices, 0)))
      return E_FAIL;
  
  for(DWORD i=0; i<reelResolution; i++)
  {
      FLOAT theta = (2*D3DX_PI*i)/(reelResolution-1);
  
      vertices[2*i+0].position = D3DXVECTOR3(reelWidth, reelDiameter*cosf(theta), reelDiameter*sinf(theta));
      vertices[2*i+0].color    = 0xffffffff;
  
#ifndef SHOW_HOW_TO_USE_TCI
      vertices[2*i+0].tu       = ((FLOAT)i)/(reelResolution-1);
      vertices[2*i+0].tv       = 1.0f;
#endif
  
      vertices[2*i+1].position = D3DXVECTOR3(-reelWidth, reelDiameter*cosf(theta), reelDiameter*sinf(theta));
      vertices[2*i+1].color    = 0xff0fffff;
  
#ifndef SHOW_HOW_TO_USE_TCI
      vertices[2*i+1].tu       = ((FLOAT)i)/(reelResolution-1);
      vertices[2*i+1].tv       = 0.0f;
#endif
  }
  
  vertexBuffer->Unlock();
}

