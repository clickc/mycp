//Download by http://www.NewXing.com
// MinicaDlg.h : header file
//

#if !defined(AFX_MINICADLG_H__785D4CA2_601B_4F1A_994C_55F37707A9EA__INCLUDED_)
#define AFX_MINICADLG_H__785D4CA2_601B_4F1A_994C_55F37707A9EA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CMinicaDlg dialog
#include "Des.h"

class CMinicaDlg : public CDialog
{
// Construction
public:
	CMinicaDlg(CWnd* pParent = NULL);	// standard constructor
    void	EnableDefPathCtls(bool show);
	void    BuildOutputFileName();
// Dialog Data
	//{{AFX_DATA(CMinicaDlg)
	enum { IDD = IDD_MINICA_DIALOG };
	CString	m_defpath;
	CString	m_deskey;
	CString	m_deskey2;
	CString	m_input;
	CString	m_output;
	CString	m_strDest;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMinicaDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON       m_hIcon;
    HBRUSH		m_BkBrush;
	CDes        des;
	enum		{ENCRYPT,DECRYPT};
	bool		run_type;
	bool        usedefpath;
	bool        deleptfile,delorgfile,delnote;
	CString     credit;
	CString     Str;
	// Generated message map functions
	//{{AFX_MSG(CMinicaDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnAbout();
	afx_msg void OnBrowse();
	afx_msg void OnDefFolder();
	afx_msg void OnDefPathCHECK();
	afx_msg void OnDelEptFileCHECK();
	afx_msg void OnDelNoteCHECK();
	afx_msg void OnDelOrgFileCHECK();
	afx_msg void OnOpen();
	afx_msg void OnOutFolder();
	afx_msg void OnRun();
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
	afx_msg void OnExit();
	afx_msg void OnButtonResult();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_MINICADLG_H__785D4CA2_601B_4F1A_994C_55F37707A9EA__INCLUDED_)
