class WinMainWindow
!!!169474.cpp!!!	WinMainWindow(in pD3DDevice : IDirect3DDevice9)
m_pD3DDevice = pD3DDevice;
!!!148354.cpp!!!	AboutDialogProc(inout hwndDlg : HWND, inout uMsg : UINT, inout wParam : WPARAM, inout lParam : LPARAM) : INT_PTR CALLBACK
switch (uMsg)
{
  case WM_COMMAND:
  {
    switch (LOWORD(wParam))
    {
      case IDOK:
      case IDCANCEL:
      {
        EndDialog(hwndDlg, (INT_PTR) LOWORD(wParam));
        return (INT_PTR) TRUE;
      }
    }
    break;
  }

  case WM_INITDIALOG:
    return (INT_PTR) TRUE;
}

return (INT_PTR) FALSE;
!!!148226.cpp!!!	MainWndProc(inout hWnd : HWND, inout msg : UINT, inout wParam : WPARAM, inout lParam : LPARAM) : LRESULT CALLBACK
static HINSTANCE hInstance;

switch (msg)
{  
  case WM_COMMAND:
  {
    switch (LOWORD(wParam))
    {
      case ID_HELP_ABOUT:
      {
        DialogBox(hInstance, MAKEINTRESOURCE(IDD_ABOUTDIALOG), hWnd, &AboutDialogProc);
        return 0;
      }

      case ID_FILE_EXIT:
      {
        DestroyWindow(hWnd);
        return 0;
      }
    }
    break;
  }

  case WM_GETMINMAXINFO:
  {
    MINMAXINFO *minMax = (MINMAXINFO*) lParam;
    minMax->ptMinTrackSize.x = 220;
    minMax->ptMinTrackSize.y = 110;

    return 0;
  }

  case WM_SYSCOMMAND:
    {
    switch (LOWORD(wParam))
    {
      case ID_HELP_ABOUT:
      {
        DialogBox(hInstance, MAKEINTRESOURCE(IDD_ABOUTDIALOG), hWnd, &AboutDialogProc);
        return 0;
      }
    }
    break;
  }
    
  case WM_CREATE:
  {
    hInstance = ((LPCREATESTRUCT) lParam)->hInstance;
    return 0;
  }

  case WM_DESTROY:
  {
    PostQuitMessage(0);
    return 0;
  }
}

return DefWindowProc(hWnd, msg, wParam, lParam);

!!!148098.cpp!!!	WinMain(inout hInstance : HINSTANCE, inout hPrevInstance : HINSTANCE, inout lpCmdLine : LPSTR, in nCmdShow : int) : int
INITCOMMONCONTROLSEX icc;
WNDCLASSEX wc;
LPCTSTR MainWndClass = TEXT("SlotPuzzle");
HWND hWnd;
HACCEL hAccelerators;
HMENU hSysMenu;
MSG msg;
  

// Initialise common controls.
icc.dwSize = sizeof(icc);
icc.dwICC = ICC_WIN95_CLASSES;
InitCommonControlsEx(&icc);
  
// Class for the main window.
wc.cbSize        = sizeof(wc);
wc.style         = 0;
wc.lpfnWndProc   = &MainWndProc;
wc.cbClsExtra    = 0;
wc.cbWndExtra    = 0;
wc.hInstance     = hInstance;
wc.hIcon         = (HICON) LoadImage(hInstance, MAKEINTRESOURCE(IDI_APPICON), IMAGE_ICON, 0, 0, LR_SHARED);
wc.hCursor       = (HCURSOR) LoadImage(NULL, IDC_ARROW, IMAGE_CURSOR, 0, 0, LR_SHARED);
wc.hbrBackground = (HBRUSH) (COLOR_BTNFACE + 1);
wc.lpszMenuName  = MAKEINTRESOURCE(IDR_MAINMENU);
wc.lpszClassName = MainWndClass;
wc.hIconSm       = (HICON) LoadImage(hInstance, MAKEINTRESOURCE(IDI_APPICON), IMAGE_ICON, 16, 16, LR_SHARED);
  
Clock *myClock = new Clock(); 

// Register the window classes, or error.
if (! RegisterClassEx(&wc))
{
  ERROR_MESSAGE(L"Error registering window class.");
  return 0;
}

RECT rect;
rect.top = (long)0;
rect.left = (long)0;
rect.right = (long)WINDOW_WIDTH;
rect.bottom = (long)WINDOW_HEIGHT;

AdjustWindowRectEx(&rect,
 		   WS_OVERLAPPEDWINDOW,
		   FALSE,
		   WS_EX_APPWINDOW | WS_EX_WINDOWEDGE);

// Create instance of main window.
hWnd = CreateWindowEx(0,
		      MainWndClass,
                      TEXT("SlotPuzzle"),
                      WS_OVERLAPPEDWINDOW,
                      CW_USEDEFAULT,
                      CW_USEDEFAULT,
                      rect.right-rect.left,
                      rect.bottom-rect.top, 
		      NULL,
 		      NULL,
		      hInstance,
		      NULL);
  
// Error if window creation failed.
if (! hWnd)
{
  ERROR_MESSAGE(L"Error creating main window.");
  return 0;
}
  
// Load accelerators.
hAccelerators = LoadAccelerators(hInstance, MAKEINTRESOURCE(IDR_ACCELERATOR));
  
// Add "about" to the system menu.
hSysMenu = GetSystemMenu(hWnd, FALSE);
InsertMenu(hSysMenu, 5, MF_BYPOSITION | MF_SEPARATOR, 0, NULL);
InsertMenu(hSysMenu, 6, MF_BYPOSITION, ID_HELP_ABOUT, TEXT("About"));
  
// Show window and force a paint.
ShowWindow(hWnd, nCmdShow);
UpdateWindow(hWnd);
SetForegroundWindow(hWnd);
SetFocus(hWnd);

D3D9Device *d3d9Device = new D3D9Device(hWnd);
 
d3d9Device->createDevice();

WinMainWindow *myWMW =  new WinMainWindow(d3d9Device->getD3D9Device());

if(FAILED(myWMW->initialise()))
{
    ERROR_MESSAGE(L"Could not initialise.");
    myWMW->cleanUp();
    delete [] myWMW;
    delete [] myClock;
    return 0;
}


ZeroMemory(&msg, sizeof(msg));

// Main message loop.
while(msg.message != WM_QUIT)
{
    if (! TranslateAccelerator(msg.hwnd, hAccelerators, &msg)) 
    {
        float dt = myClock->update();

        if(FAILED(myWMW->update(dt)))
        {
            ERROR_MESSAGE(L"Failed to update.");
            break;
        }

         myWMW->m_pD3DDevice->Clear(0,
                                    0,
                                    D3DCLEAR_TARGET,
                                    D3DCOLOR_XRGB(0, 0, 0),
                                    1.0f,
                                    0.0f);

        if(FAILED(myWMW->render()))
        {
            ERROR_MESSAGE(L"Render failed.");
            break;
        }


        myWMW->m_pD3DDevice->Present(NULL, NULL, NULL, NULL);

        if(PeekMessage(&msg, NULL, 0U, 0U, PM_REMOVE))
        {
            TranslateMessage(&msg);
            DispatchMessage(&msg);
        }
    }
}

myWMW->cleanUp();
delete [] myWMW;
delete [] myClock;

return (int) msg.wParam;
!!!154882.cpp!!!	initialise() : HRESULT
reel = new Reel(m_pD3DDevice, CYLINDER_LENGTH, 1, 0.25);

// Create the vertex buffer.
/*
if(FAILED(m_pD3DDevice->CreateVertexBuffer(CYLINDER_LENGTH*2*sizeof(CUSTOMVERTEX),
                                             0, D3DFVF_CUSTOMVERTEX,
                                             D3DPOOL_DEFAULT, &m_pVertexBuffer, NULL)))
{
    ERROR_MESSAGE(L"Failed to create vertex buffer.");
    return E_FAIL;
}



// Fill the vertex buffer. We are setting the tu and tv texture
// coordinates, which range from 0.0 to 1.0

CUSTOMVERTEX* pVertices;

if( FAILED( m_pVertexBuffer->Lock( 0, 0, (void**)&pVertices, 0 ) ) )
    return E_FAIL;

for( DWORD i=0; i<CYLINDER_LENGTH; i++ )
{
    FLOAT theta = (2*D3DX_PI*i)/(CYLINDER_LENGTH-1);

    pVertices[2*i+0].position = D3DXVECTOR3( 0.25, 1.0f*cosf(theta), 1.0f*sinf(theta) );
    pVertices[2*i+0].color    = 0xffffffff;

#ifndef SHOW_HOW_TO_USE_TCI
    pVertices[2*i+0].tu       = ((FLOAT)i)/(CYLINDER_LENGTH-1);
    pVertices[2*i+0].tv       = 1.0f;
#endif

    pVertices[2*i+1].position = D3DXVECTOR3( -0.25f, 1.0f*cosf(theta), 1.0f*sinf(theta) );
    pVertices[2*i+1].color    = 0xff0fffff;

#ifndef SHOW_HOW_TO_USE_TCI
    pVertices[2*i+1].tu       = ((FLOAT)i)/(CYLINDER_LENGTH-1);
    pVertices[2*i+1].tv       = 0.0f;
#endif

}
  
m_pVertexBuffer->Unlock();

m_pD3DDevice->SetRenderState(D3DRS_LIGHTING, false);
// Turn on the zbuffer
m_pD3DDevice->SetRenderState( D3DRS_ZENABLE, TRUE );
    
// Use D3DX to create a texture from a file based image
if( FAILED( D3DXCreateTextureFromFile( m_pD3DDevice, L"C://Users//Mark Ellis//Documents//My Develop//SlotPuzzle//source//Debug//reel2.jpg", &g_pTexture ) ) )
{
  // If texture is not in current folder, try parent folder
  ERROR_MESSAGE(L"Could not find reel texture.");
  return E_FAIL;
}

*/
return S_OK;
!!!155010.cpp!!!	render() : HRESULT
if(SUCCEEDED(m_pD3DDevice->BeginScene()))
{
    // Setup our texture. Using textures introduces the texture stage states,
    // which govern how textures get blended together (in the case of multiple
    // textures) and lighting information. In this case, we are modulating
    // (blending) our texture with the diffuse color of the vertices.

    m_pD3DDevice->SetTexture(0, reel->getTexture());
    m_pD3DDevice->SetTextureStageState(0, D3DTSS_COLOROP,   D3DTOP_MODULATE);
    m_pD3DDevice->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
    m_pD3DDevice->SetTextureStageState(0, D3DTSS_COLORARG2, D3DTA_DIFFUSE);
    m_pD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAOP,   D3DTOP_DISABLE);

    m_pD3DDevice->SetStreamSource(0,
                                  reel->getVertexBuffer(),
                                  0,
                                  sizeof(CUSTOMVERTEX));
  
    m_pD3DDevice->SetFVF(D3DFVF_CUSTOMVERTEX);
    m_pD3DDevice->DrawPrimitive(D3DPT_TRIANGLESTRIP, 0, 2*CYLINDER_LENGTH-2);
    m_pD3DDevice->EndScene();
  
    return S_OK;

}
return E_FAIL;
!!!155138.cpp!!!	cleanUp() : void
if(m_pD3DDevice)
    m_pD3DDevice->Release();

if(m_pVertexBuffer)
    m_pVertexBuffer->Release();
!!!155650.cpp!!!	update(in dt : float) : HRESULT
D3DXMATRIXA16 matWorld;

// WORLDMATRIX
// Set up the rotation matrix to generate 1 full rotation (2*PI radians)
// every 1000 ms. To avoid the loss of precision inherent in very high
// floating point numbers, the system time is modulated by the rotation
// period before conversion to a radian angle.

UINT  iTime  = timeGetTime() % 5000;

float fAngle = iTime * (2.0f * D3DX_PI) / 5000.0f;
D3DXMatrixRotationYawPitchRoll(&matWorld,0,-fAngle,0);

m_pD3DDevice->SetTransform(D3DTS_WORLD, &matWorld );

// VIEWMATRIX
// Set up our view matrix. A view matrix can be defined given an eye point,
// a point to lookat, and a direction for which way is up. Here, we set the
// eye five units back along the z-axis and up three units, look at the
// origin, and define "up" to be in the y-direction.

D3DXVECTOR3 vEyePt(0.0f, 2.0f,-5.0f );
D3DXVECTOR3 vLookatPt(0.0f, 0.0f, 0.0f );
D3DXVECTOR3 vUpVec(0.0f, 1.0f, 0.0f );

D3DXMATRIXA16 matView;
D3DXMatrixLookAtLH(&matView, &vEyePt, &vLookatPt, &vUpVec);
m_pD3DDevice->SetTransform(D3DTS_VIEW, &matView);

// PROJECTION MATRIX
// For the projection matrix, we set up a perspective transform (which
// transforms geometry from 3D view space to 2D viewport space, with
// a perspective divide making objects smaller in the distance). To build
// a perpsective transform, we need the field of view (1/4 pi is common),
// the aspect ratio, and the near and far clipping planes (which define at
// what distances geometry should be no longer be rendered).

D3DXMATRIXA16 matProj;

D3DXMatrixPerspectiveFovLH(&matProj, D3DX_PI/4, 1.0f, 1.0f, 100.0f);

m_pD3DDevice->SetTransform( D3DTS_PROJECTION, &matProj);

return S_OK;
!!!154882.cpp!!!	initialise() : HRESULT
reel = new Reel(m_pD3DDevice, CYLINDER_LENGTH, 1, 0.25);

