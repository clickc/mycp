//Download by http://www.NewXing.com
// AboutDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Minica.h"
#include "AboutDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
extern  CMinicaApp         theApp;
static  BOOL bClicked = FALSE;

char *pArrCredit = { "|�򵥼���ϵͳ(DES) Ver 1.0\t||Copyright (c) 2004|"
        "�㶫��ҵ��ѧ�����ѧԺ|��ȫ�뱣��С������||"
        "BITMAPBADGE^||"    // this is a quoted bitmap resource 
        "��Ա\r||200008014219 ��ΰ|200008014220 ��ΰ�||"
        "BITMAPCITY^||"    // this is a quoted bitmap resource 
        "Window֧����(class CWindow)\r|| ��ΰ�||"
        "ͨ�ÿ�(class CGfL)\r||��ΰ ��ΰ�||"
        "��������ļ���(class CFileInOut)\r||��ΰ||"
        "DES(class CDes)\r||��ΰ ��ΰ�||"            
        "����\r||��ΰ||"
        "* * * * * * * * *\t|||"
        "BITMAPZHULOU^||"
        "����֧��\r||��ΰ ��ΰ�||"
        "�ĵ�\r||��ΰ ��ΰ�||"
        "ϵͳ֧��\r||��ΰ�||"
        "����֧��\r||��ΰ�||||"
        "BITMAPBADGENAME^|||||||||||||||"
        };

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog


CAboutDlg::CAboutDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CAboutDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CAboutDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
	CBitmap bmp;
	srand(GetTickCount());
	do{theApp.bk_about=rand()%5;}while(theApp.bk_about==theApp.bk_mainui
		                      ||theApp.bk_about==1||theApp.bk_about==3);
	bmp.LoadBitmap(IDB_BKBITMAP1+theApp.bk_about);
	m_BkBrush = CreatePatternBrush ((HBITMAP)bmp.GetSafeHandle()) ;
	bmp.DeleteObject();

	bitmapIDs[0] = IDB_BKBITMAP2;
	bitmapIDs[1] = IDB_BKBITMAP4;
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
		// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
	ON_WM_CTLCOLOR()
	ON_WM_TIMER()
	ON_WM_DESTROY()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg message handlers
#define  DISPLAY_TIMER_ID		151

BOOL CAboutDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	// TODO: Add extra initialization here
	m_neuhomepage.SubclassDlgItem(IDC_NEUHOMEPAGE_STATIC,this);
	m_myemail.SubclassDlgItem(IDC_MYEMAIL_STATIC,this);

	m_neuhomepage.SetLink(TRUE)
				 .SetTextColor(RGB(0,0,255))
		         .SetFontUnderline(TRUE)
				 .SetLinkCursor(AfxGetApp()->LoadCursor(IDC_HAND));
	m_myemail.SetLink(TRUE)
		     .SetTextColor(RGB(0,0,255))
		     .SetFontUnderline(TRUE)
		     .SetLinkCursor(AfxGetApp()->LoadCursor(IDC_HAND));
	         
	m_credit.SubclassDlgItem(IDC_DISPLAY_STATIC,this);
	m_credit.SetCredits(pArrCredit,'|');
	m_credit.SetSpeed(DISPLAY_FAST);
	m_credit.SetColor(BACKGROUND_COLOR, RGB(0,0,255));
	m_credit.SetTransparent();
	m_credit.SetBkImage(bitmapIDs[0]);
	m_credit.StartScrolling();
	TimerOn = SetTimer(DISPLAY_TIMER_ID,5000,NULL);
    ASSERT(TimerOn != 0);

	return TRUE;  // return TRUE unless you set the focus to a control
	              // EXCEPTION: OCX Property Pages should return FALSE
}

HBRUSH CAboutDlg::OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor) 
{
{
	HBRUSH hbr = CDialog::OnCtlColor(pDC, pWnd, nCtlColor);

	// TODO: Change any attributes of the DC here
	if ( (nCtlColor==CTLCOLOR_DLG) || (nCtlColor==CTLCOLOR_STATIC) )
	{
		pDC->SetBkMode (TRANSPARENT);
		if( nCtlColor==CTLCOLOR_STATIC )
			pDC->SetTextColor(RGB(0,0,255));
		hbr = m_BkBrush;
	}
	// TODO: Return a different brush if the default is not desired
	return hbr;
}
}

void CAboutDlg::OnTimer(UINT nIDEvent) 
{
	// TODO: Add your message handler code here and/or call default
	if (nIDEvent != DISPLAY_TIMER_ID)
	{
		CDialog::OnTimer(nIDEvent);
		return;
	}

	static int index = 0;

	index = ++index % 2;
	
	m_credit.SetBkImage(bitmapIDs[index]);
		
	CDialog::OnTimer(nIDEvent);
}

void CAboutDlg::OnDestroy() 
{
	CDialog::OnDestroy();
	
	// TODO: Add your message handler code here
	if(TimerOn)
		ASSERT(KillTimer(DISPLAY_TIMER_ID));
}
