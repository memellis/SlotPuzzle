
#include "Reel.h"

Reel::Reel(IDirect3DDevice9* p_d3d9Device, const DWORD reelResolution, float reelDiameter, float reelWidth) {
  m_pd3d9Device = p_d3d9Device;
  
  if( FAILED(m_pd3d9Device->CreateVertexBuffer(reelResolution*2*sizeof(CUSTOM_VERTEX),
                                           0, 
  				         D3DFVF_CUSTOMVERTEX,
    		  	  	  	 D3DPOOL_DEFAULT,
  				         &vertexBuffer,
  					 NULL)))
  {
    ERROR_MESSAGE(L"Failed to lock vertex buffer.");
  }
    
  // Fill the vertex buffer. We are setting the tu and tv texture
  // coordinates, which range from 0.0 to 1.0
    
  if(FAILED(vertexBuffer->Lock(0, 0, (void**)&vertices, 0)))
  {
    ERROR_MESSAGE(L"Failed to lock vertex buffer.");
  }
    
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
  
  m_pd3d9Device->SetRenderState(D3DRS_LIGHTING, false);
  // Turn on the zbuffer
  m_pd3d9Device->SetRenderState( D3DRS_ZENABLE, TRUE );
        
  // Use D3DX to create a texture from a file based image
  if( FAILED( D3DXCreateTextureFromFile( m_pd3d9Device, L"C://Users//Mark Ellis//Documents//My Develop//SlotPuzzle//source//Debug//reel2.jpg", &m_pTexture)))
  {
    // If texture is not in current folder, try parent folder
    ERROR_MESSAGE(L"Could not find reel texture.");
  }
}

IDirect3DVertexBuffer9* Reel::getVertexBuffer() {
  return vertexBuffer;
}

LPDIRECT3DTEXTURE9 Reel::getTexture() {
  return m_pTexture;
}

