//Download by http://www.NewXing.com
// MixedCS.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "Minica.h"
#include "MinicaDlg.h"
#include "GfL.h"
#include "Window.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CMixedCSApp

BEGIN_MESSAGE_MAP(CMinicaApp, CWinApp)
	//{{AFX_MSG_MAP(CMixedCSApp)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CMixedCSApp construction

CMinicaApp::CMinicaApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CMixedCSApp object

CMinicaApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CMixedCSApp initialization

BOOL CMinicaApp::InitInstance()
{
	AfxEnableControlContainer();

	// Standard initialization
	// If you are not using these features and wish to reduce the size
	//  of your final executable, you should remove from the following
	//  the specific initialization routines you do not need.

#ifdef _AFXDLL
	Enable3dControls();			// Call this when using MFC in a shared DLL
#else
	Enable3dControlsStatic();	// Call this when linking to MFC statically
#endif

	CMinicaDlg dlg;
	m_pMainWnd = &dlg;
	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		// TODO: Place code here to handle when the dialog is
		//  dismissed with OK
	}
	else if (nResponse == IDCANCEL)
	{
		// TODO: Place code here to handle when the dialog is
		//  dismissed with Cancel
	}

	// Since the dialog has been closed, return FALSE so that we exit the;,
	//  application, rather than start the application's message pump.
	return FALSE;
}

bool CMinicaApp::InputKeyStr(CString& KeyStr,UINT len)
{
    if( CWindow::InputStr(KeyStr) )
	{
		CheckKeyStr(KeyStr);
        KeyStr = KeyStr.Left(len);
		return	true;
	}
	return	false;
}

#define KEY_MAXLEN   1000

void CMinicaApp::CheckKeyStr(CString& KeyStr)
{
    char buf[KEY_MAXLEN+1];
	int len=CGfL::StrToHalfByte(buf,KeyStr.GetBuffer(0),KeyStr.GetLength());

	CGfL::HalfByteToStr(buf,buf,len);
    KeyStr = buf;
}
