//Download by http://www.NewXing.com
#if !defined(AFX_ABOUTDLG_H__2CDAA7E0_E3C8_11D6_B0C3_00E04C391A51__INCLUDED_)
#define AFX_ABOUTDLG_H__2CDAA7E0_E3C8_11D6_B0C3_00E04C391A51__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// AboutDlg.h : header file
//

//////////////////////////////////////////////////////////////////////
#include "HLink.h"
#include "CreditStatic.h"
#include "Label.h"

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog

class CAboutDlg : public CDialog
{
// Construction
public:
	CAboutDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	CLabel		m_neuhomepage;
	CLabel		m_myemail;
	CCreditStatic	m_credit;

		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HBRUSH		m_BkBrush;
	UINT		bitmapIDs[2];
	UINT		TimerOn;

	// Generated message map functions
	//{{AFX_MSG(CAboutDlg)
	virtual BOOL OnInitDialog();
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnDestroy();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_ABOUTDLG_H__2CDAA7E0_E3C8_11D6_B0C3_00E04C391A51__INCLUDED_)
