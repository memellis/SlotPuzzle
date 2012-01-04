
#include <windows.h>
#include <commctrl.h>

#include "WinMainWindow.h"

struct VERTEX {
	float x,
		  y,
		  z,
		  rhw;

	DWORD color;
};

#define MY_FVF D3DFVF_XYZRHW | D3DFVF_DIFFUSE

INT_PTR CALLBACK AboutDialogProc(HWND hwndDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
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
}

LRESULT CALLBACK MainWndProc(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
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
  
}

int WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
  INITCOMMONCONTROLSEX icc;
  WNDCLASSEX wc;
  LPCTSTR MainWndClass = TEXT("MinGW Win32 Application");
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
    
  WinMainWindow *myWMW =  new WinMainWindow();
  
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
    myWMW->cleanUp();
    delete [] myWMW;
  
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
    
  D3DPRESENT_PARAMETERS d3dpp;
  ZeroMemory(&d3dpp, sizeof(d3dpp));
  d3dpp.Windowed = true;
  d3dpp.hDeviceWindow = hWnd;
  d3dpp.BackBufferCount = 1;
  d3dpp.BackBufferWidth = WINDOW_WIDTH;
  d3dpp.BackBufferHeight = WINDOW_HEIGHT;
  d3dpp.BackBufferFormat = D3DFMT_X8R8G8B8;
  d3dpp.SwapEffect = D3DSWAPEFFECT_DISCARD;
  
  myWMW->m_pD3D = Direct3DCreate9(D3D_SDK_VERSION);
  
  if (FAILED(myWMW->m_pD3D->CreateDevice(D3DADAPTER_DEFAULT,
  	  	  	  	       D3DDEVTYPE_HAL,
  		  	  	       hWnd,
  		  	  	       D3DCREATE_SOFTWARE_VERTEXPROCESSING,
  		  	  	       &d3dpp,
  		  	  	       &myWMW->m_pD3DDevice)))
  {
      ERROR_MESSAGE(L"Failed to create D3D device.");
      myWMW->cleanUp();
      delete [] myWMW;
      return 0;
  }
  
  if(FAILED(myWMW->initialise()))
  {
      ERROR_MESSAGE(L"Could not initialise.");
      myWMW->cleanUp();
      delete [] myWMW;
      return 0;
  }
  
  
  // Main message loop.
  while(GetMessage(&msg, NULL, 0, 0))
  {
      if (! TranslateAccelerator(msg.hwnd, hAccelerators, &msg)) 
      {
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
  
  	TranslateMessage(&msg);
  	DispatchMessage(&msg);
      }
  }
  
  myWMW->cleanUp();
  delete [] myWMW;
  
  return (int) msg.wParam;
}

HRESULT WinMainWindow::initialise() {
  VERTEX verts[] =
  {
      {100.0f, 25.0f, 0.0f, 1.0f, D3DCOLOR_XRGB(255, 0, 0)},
      {175.0f, 175.0f, 0.0f, 1.0f, D3DCOLOR_XRGB(0, 255, 0)},
      {25.0f, 175.0f, 0.0f, 1.0f, D3DCOLOR_XRGB(0, 0, 255)}
  };
  
  if(FAILED(m_pD3DDevice->CreateVertexBuffer(3*sizeof(VERTEX),
  					   NULL,
  					   MY_FVF,
  					   D3DPOOL_DEFAULT,
  					   &m_pVertexBuffer,
  					   NULL)))
  {
      ERROR_MESSAGE(L"Failed to create vertex buffer.");
      return E_FAIL;
  }
  
  void* pVerts;
  if(FAILED(m_pVertexBuffer->Lock(0,
       			        sizeof(verts),
  				(void **)&pVerts,
  				NULL)))
  
  {
      ERROR_MESSAGE(L"Failed to lock vertex buffer.");
      return E_FAIL;
  }
  
  memcpy(pVerts, &verts, sizeof(verts));
  
  m_pVertexBuffer->Unlock();
  return S_OK;
}

HRESULT WinMainWindow::render() {
  if(SUCCEEDED(m_pD3DDevice->BeginScene()))
  {
      m_pD3DDevice->SetStreamSource(0,
      				  m_pVertexBuffer,
  				  0,
  				  sizeof(VERTEX));
  
      m_pD3DDevice->SetFVF(MY_FVF);
      m_pD3DDevice->DrawPrimitive(D3DPT_TRIANGLELIST, 0, 1);
      m_pD3DDevice->EndScene();
  
      return S_OK;
  
  }
  return E_FAIL;
}

void WinMainWindow::cleanUp() {
  if(m_pD3D)
      m_pD3D->Release();
  
  if(m_pD3DDevice)
      m_pD3DDevice->Release();
  
  if(m_pVertexBuffer)
      m_pVertexBuffer->Release();
}

