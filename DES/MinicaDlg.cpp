//Download by http://www.NewXing.com
// MinicaDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Minica.h"
#include "MinicaDlg.h"
#include "MyFileDialog.h"
#include "Window.h"
#include "GfL.h"
#include "AboutDlg.h"
#include  "md5checksum.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
extern  CMinicaApp         theApp;
#define CHECK(x)			{if( !(x) ) return;}
#define CHECK_MSG(x,msg)	{if( !(x) ){CWindow::ShowMessage(msg);return;}}


/////////////////////////////////////////////////////////////////////////////
// CMinicaDlg dialog

CMinicaDlg::CMinicaDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CMinicaDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CMinicaDlg)
	m_defpath = _T("");
	m_deskey = _T("");
	m_deskey2 = _T("");
	m_input = _T("");
	m_output = _T("");
	m_strDest = _T("");
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	CBitmap bmp;
	srand(GetTickCount());
	bmp.LoadBitmap(IDB_BKBITMAP1+(theApp.bk_mainui=rand()%5));
	m_BkBrush = CreatePatternBrush ((HBITMAP)bmp.GetSafeHandle()) ;
	bmp.DeleteObject();

    credit="                                                              "
           //"                                    "
		   "��ӭʹ�á�С�ͼ򵥼���ϵͳ����ǰ�汾1.0   "  
		   "                                            "
		   "                                            "
		   "                                           "
		   "                                           ";
}

void CMinicaDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CMinicaDlg)
	DDX_Text(pDX, IDC_DefPath, m_defpath);
	DDX_Text(pDX, IDC_DesKey, m_deskey);
	DDV_MaxChars(pDX, m_deskey, 16);
	DDX_Text(pDX, IDC_DesKey2, m_deskey2);
	DDV_MaxChars(pDX, m_deskey2, 16);
	DDX_Text(pDX, IDC_Input, m_input);
	DDX_Text(pDX, IDC_Output, m_output);
	DDX_Text(pDX, IDC_EDIT_DEST, m_strDest);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CMinicaDlg, CDialog)
	//{{AFX_MSG_MAP(CMinicaDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_ABOUT, OnAbout)
	ON_BN_CLICKED(IDC_Browse, OnBrowse)
	ON_BN_CLICKED(IDC_DefFolder, OnDefFolder)
	ON_BN_CLICKED(IDC_DefPathCHECK, OnDefPathCHECK)
	ON_BN_CLICKED(IDC_DelEptFileCHECK, OnDelEptFileCHECK)
	ON_BN_CLICKED(IDC_DelNoteCHECK, OnDelNoteCHECK)
	ON_BN_CLICKED(IDC_DelOrgFileCHECK, OnDelOrgFileCHECK)
	ON_BN_CLICKED(IDC_Open, OnOpen)
	ON_BN_CLICKED(IDC_OutFolder, OnOutFolder)
	ON_BN_CLICKED(IDC_Run, OnRun)
	ON_WM_TIMER()
	ON_WM_CTLCOLOR()
	ON_BN_CLICKED(ID_EXIT, OnExit)
	ON_BN_CLICKED(IDC_BUTTON_RESULT, OnButtonResult)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CMinicaDlg message handlers

BOOL CMinicaDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	// TODO: Add extra initialization here

	// Init Envrionment
	run_type = ENCRYPT;

    m_defpath = theApp.GetProfileString("InOut","DefPath","");

	usedefpath = theApp.GetProfileInt("InOut","IfUseDefPath",0)?true:false;
	if( usedefpath )
	{
		CheckDlgButton(IDC_DefPathCHECK,BST_CHECKED);
		EnableDefPathCtls(true);
	}
		delnote = theApp.GetProfileInt("Option","IfDelNote",1)?true:false;
	if( delnote )
		CheckDlgButton(IDC_DelNoteCHECK,BST_CHECKED);
	delorgfile = theApp.GetProfileInt("Option","IfDelOrgFile",0)?true:false;
	if( delorgfile )
		CheckDlgButton(IDC_DelOrgFileCHECK,BST_CHECKED);
	deleptfile = theApp.GetProfileInt("Option","IfDelEptFile",0)?true:false;
	if( deleptfile )
		CheckDlgButton(IDC_DelEptFileCHECK,BST_CHECKED);
	UpdateData(false);
	SetTimer(1,400,NULL);
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CMinicaDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
        CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CMinicaDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CMinicaDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CMinicaDlg::OnAbout() 
{
	// TODO: Add your control notification handler code here
	CAboutDlg  dlg;
	dlg.DoModal();
}

void CMinicaDlg::OnBrowse() 
{
	// TODO: Add your control notification handler code here
	CString Filter= "�����ļ�(*.*)|*.*|�ı��ļ�(*.txt)|*.txt|"
		            "DES�����ļ�(*.des)|*.des||";
	CMyFileDialog FileDlg (TRUE, NULL, NULL, OFN_HIDEREADONLY, Filter);
	CHECK( FileDlg.DoModal() == IDOK )

	UpdateData(true);
	m_input = FileDlg.GetPathName();
	m_output = "";
	UpdateData(false);

	run_type  = DECRYPT;
	SetDlgItemText(IDC_Run,"����");
	if( m_input.Find(".des")>0 || m_input.Find(".DES")>0 )
	{
		BuildOutputFileName();
		
	}
	else
	{
		run_type = ENCRYPT;
		BuildOutputFileName();
		SetDlgItemText(IDC_Run,"����");
	}
}
void CMinicaDlg::BuildOutputFileName()
{
	UpdateData(true);
	if( m_input.IsEmpty() )
	{
		m_output = "";
		UpdateData(false);
		return;
	}

	CString path= (usedefpath && !m_defpath.IsEmpty()) ? m_defpath : CWindow::GetPath(m_input);
	CString name= CWindow::GetFileNameNoExt( m_output.IsEmpty() ? m_input : m_output );
	CString ext = CWindow::GetExtName(m_input);
	CString newext = (run_type==DECRYPT) ? "" : "des";

	if( path.GetAt( path.GetLength()-1 ) != '\\' )
		path += "\\";
	if( run_type==DECRYPT )
		if( ext.GetLength()>4 && ext.GetAt(ext.GetLength()-4)=='.' )
			ext = ext.Left( ext.GetLength()-4 );
		else
			ext = "";

		m_output = path + name;
		if( ext.GetLength() )
		    m_output += "." + ext;
		if( newext.GetLength() )
		    m_output += "." + newext;
		UpdateData(false);
}

void CMinicaDlg::OnDefFolder() 
{
	// TODO: Add your control notification handler code here
    UpdateData(true);
	CWindow::GetFolder(m_defpath);
	UpdateData(false);
	BuildOutputFileName();
}

void CMinicaDlg::OnDefPathCHECK() 
{
	// TODO: Add your control notification handler code here
	usedefpath = !usedefpath;
	if( usedefpath )
	{
		EnableDefPathCtls(true);
		BuildOutputFileName();
	}
	else
	{
		EnableDefPathCtls(false);
	}
}

void CMinicaDlg::OnDelEptFileCHECK() 
{
	// TODO: Add your control notification handler code here
	deleptfile = !deleptfile;
}

void CMinicaDlg::OnDelNoteCHECK() 
{
	// TODO: Add your control notification handler code here
	delnote = !delnote;
}

void CMinicaDlg::OnDelOrgFileCHECK() 
{
	// TODO: Add your control notification handler code here
	delorgfile = !delorgfile;
}

void CMinicaDlg::OnOpen() 
{
	// TODO: Add your control notification handler code here
	UpdateData(true);
	if( run_type == ENCRYPT)
		{
		CHECK_MSG(CWindow::IsFileExist(m_input.GetBuffer(0)),"�ļ�������!  ")
		ShellExecute(NULL,"open",m_input,NULL,NULL,SW_SHOWNORMAL);
	}
		else
	{
		CHECK_MSG(CWindow::IsFileExist(m_output.GetBuffer(0)),"�ļ�������!  ")
		ShellExecute(NULL,"open",m_output,NULL,NULL,SW_SHOWNORMAL);
	}
		
}

void CMinicaDlg::OnOutFolder() 
{
	// TODO: Add your control notification handler code here
    CString		defpath = m_defpath;
	bool        tmp = usedefpath;
	usedefpath = true;
	UpdateData(true);
	CWindow::GetFolder(m_defpath);
	UpdateData(false);
	BuildOutputFileName();
	m_defpath = defpath;
	usedefpath = tmp;
	UpdateData(false);
}

void CMinicaDlg::OnRun() 
{
	// TODO: Add your control notification handler code here
	CHECK( IDYES == MessageBox("���Ҫ���иò�����? ","��ʾ",MB_YESNO | MB_ICONQUESTION) )

	bool	flage;
	DWORD   time=GetTickCount();
	//	CString str= (run_type==ENCRYPT) ? "����": "����",tmp;

	BuildOutputFileName();
	CHECK_MSG( m_deskey == m_deskey2, "����DES��Կ��һ��! " )
	if( run_type==ENCRYPT)
	{
			flage = des.Encrypt(m_output.GetBuffer(0),m_input.GetBuffer(0),
								m_deskey.GetBuffer(0));
	}
	else
	{
		flage = des.Decrypt(m_output.GetBuffer(0),m_input.GetBuffer(0),
								m_deskey.GetBuffer(0));
	}
	if( flage )
	{
		Str.Format("%s�ɹ�! ��ʱ%u�롣",(run_type==ENCRYPT)?"����":"����",(GetTickCount()-time)/1000);
		MessageBox(Str,"��ʾ",MB_ICONINFORMATION);

		if( (run_type==ENCRYPT && delorgfile) ||
			(run_type==DECRYPT && deleptfile) )
		{
			Str.Format("�Ƿ�ɾ���ļ���\n%s",m_input);
			if( !delnote )
				DeleteFile(m_input);
			else if( IDYES==MessageBox(Str,"��ʾ",MB_YESNO | MB_ICONQUESTION) )
					DeleteFile(m_input);
		}
	}
}

void CMinicaDlg::OnTimer(UINT nIDEvent) 
{
	// TODO: Add your message handler code here and/or call default
	CGfL::RotateL(credit.GetBuffer(0),credit.GetLength(),2);
	SetDlgItemText(IDC_Credit,credit.Left(100));
	
	CDialog::OnTimer(nIDEvent);
}

HBRUSH CMinicaDlg::OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor) 
{
	HBRUSH hbr = CDialog::OnCtlColor(pDC, pWnd, nCtlColor);
	
	// TODO: Change any attributes of the DC here
	if ( (nCtlColor==CTLCOLOR_EDIT) ||
		 (nCtlColor==CTLCOLOR_DLG)  ||
		 (nCtlColor==CTLCOLOR_STATIC) )
	{
		pDC->SetBkMode (TRANSPARENT);
		if( nCtlColor==CTLCOLOR_STATIC )
			pDC->SetTextColor(RGB(0,0,255));
		hbr = m_BkBrush;
	}
	// TODO: Return a different brush if the default is not desired
	return hbr;
}

void CMinicaDlg::OnExit() 
{
	// TODO: Add your control notification handler code here
	CHECK( IDYES==MessageBox("���Ҫ�˳��ó�����? ","��ʾ",MB_YESNO | MB_ICONQUESTION) )

	UpdateData(true);
	theApp.WriteProfileString("InOut","DefPath",m_defpath);
	theApp.WriteProfileInt("InOut","IfUseDefPath",usedefpath);
	theApp.WriteProfileInt("Option","IfDelNote",delnote);
	theApp.WriteProfileInt("Option","IfDelOrgFile",delorgfile);
	theApp.WriteProfileInt("Option","IfDelEptFile",deleptfile);

	DeleteObject(m_BkBrush);
	CDialog::OnOK();

	CAboutDlg  dlg;
	dlg.DoModal();

	
}
void CMinicaDlg::EnableDefPathCtls(bool show)
{
	if(show)
	{
		GetDlgItem(IDC_DEFPATHSTATIC)->EnableWindow(true);
		GetDlgItem(IDC_DefPath)->EnableWindow(true);
		GetDlgItem(IDC_DefFolder)->EnableWindow(true);
	}
	else
	{
		GetDlgItem(IDC_DEFPATHSTATIC)->EnableWindow(false);
		GetDlgItem(IDC_DefPath)->EnableWindow(false);
		GetDlgItem(IDC_DefFolder)->EnableWindow(false);
	}
}

void CMinicaDlg::OnButtonResult() 
{
	// TODO: Add your control notification handler code here
	UpdateData();//�����������б༭������ݸ��¼�ˢ����Ļ
	m_strDest = CMD5Checksum::GetMD5( (BYTE*)(const char*)m_deskey, m_deskey.GetLength() );
	UpdateData(FALSE);
}
