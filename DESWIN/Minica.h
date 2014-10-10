//Download by http://www.NewXing.com
// MixedCS.h : main header file for the MIXEDCS application
//

#if !defined(AFX_MIXEDCS_H__E52F1250_E06E_11D6_B0C2_00E04C391A51__INCLUDED_)
#define AFX_MIXEDCS_H__E52F1250_E06E_11D6_B0C2_00E04C391A51__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CMixedCSApp:
// See MixedCS.cpp for the implementation of this class
//

class CMinicaApp : public CWinApp
{
public:
	CMinicaApp();

    bool InputKeyStr(CString& KeyStr,UINT len=600);
    void CheckKeyStr(CString& KeyStr);

	int  bk_mainui,bk_rsakey,bk_about;
// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMixedCSApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CMixedCSApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_MIXEDCS_H__E52F1250_E06E_11D6_B0C2_00E04C391A51__INCLUDED_)
