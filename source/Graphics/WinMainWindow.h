#ifndef _WINMAINWINDOW_H
#define _WINMAINWINDOW_H


#include <windows.h>
#define IDI_APPICON                     101
#define IDR_MAINMENU                    102
#define IDD_ABOUTDIALOG                 103
#define IDR_ACCELERATOR                 104
#define ID_HELP_ABOUT                   40001
#define ID_FILE_EXIT                    40002

#define IDC_STATIC -1

   int WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow);

    LRESULT CALLBACK MainWndProc(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);

    INT_PTR CALLBACK AboutDialogProc(HWND hwndDlg, UINT uMsg, WPARAM wParam, LPARAM lParam);


class WinMainWindow {
  public:

};
#endif
