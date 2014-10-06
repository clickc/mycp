//Download by http://www.NewXing.com
// Window.cpp: implementation of the CWindow class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "Minica.h"
#include "Window.h"
#include "MyFileDialog.h"
#include <shlobj.h>

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
CWindow::CWindow()
{
	wnd.m_hWnd = GetActiveWindow();
	wnd.GetWindowText(caption);
}

CWindow::~CWindow()
{
	EndWaitCursor();
	wnd.m_hWnd = 0;//������0������Windows���رյ�ǰ����
}

///////////////////////////////////////////////////////////////////////////////
// CWindows Functions
///////////////////////////////////////////////////////////////////////////////
#define STR_MAXLEN			1000

#define CHECK(x)			{if( !(x) ) return false;}
#define CHECK_MSG(x,msg)	{if( !(x) ) {ShowMessage(msg);return false;}}

/******************************************************************************/
//	���ƣ�ShowWaitCursor()
//	���ܣ���ʾ�ȴ����
//  ������
//	���أ�
//  ��ע��

/******************************************************************************/
void CWindow::ShowWaitCursor()
{
	wnd.m_hWnd = GetActiveWindow();
	wnd.GetWindowText(caption);
	wnd.BeginWaitCursor();
}
/******************************************************************************/
//	���ƣ�EndWaitCursor
//	���ܣ������ȴ����
//  ������
//	���أ�
//  ��ע��

/******************************************************************************/
void CWindow::EndWaitCursor()
{
	wnd.EndWaitCursor();
    wnd.SetWindowText(caption);
}
/******************************************************************************/
//	���ƣ�SetWindowCaption
//	���ܣ����ô��ڱ���
//  ������
//	���أ�
//  ��ע��
/******************************************************************************/
void CWindow::SetWindowCaption(char *Str,int v1,int v2,int v3)
{
	CString	    newname;
	newname.Format(Str,v1,v2,v3);
	wnd.SetWindowText(newname);
}
/******************************************************************************/
//	���ƣ�ShowMessage
//	���ܣ���ʾ��Ϣ��
//  ������
//	���أ�
//  ��ע��

/******************************************************************************/
int CWindow::ShowMessage(const char *msg,const char *title,UINT flage)
{
	CHECK( msg && title )
	return MessageBox(GetActiveWindow(),msg,title,flage);
}
/******************************************************************************/
//	���ƣ�InputKeyStr
//	���ܣ���ѡ���ļ��Ի��򣬵����ַ���
//  ������
//	���أ�����ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CWindow::InputStr(CString &Str)
{
	CString Filter= "�ı��ļ�(*.txt)|*.txt|�����ļ�(*.*)|*.*||";

	CMyFileDialog FileDlg (TRUE, NULL, NULL, OFN_HIDEREADONLY, Filter);

    CHECK( FileDlg.DoModal()==IDOK )

	char buf[STR_MAXLEN+1];

	CHECK( GetStrFromFile(buf,FileDlg.GetPathName().GetBuffer(0)) )
    Str = buf;

	return true;
}
/******************************************************************************/
//	���ƣ�OutputKeyStr
//	���ܣ���ѡ���ļ��Ի��򣬵����ַ���
//  ������
//	���أ������ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CWindow::OutputStr(CString &Str)
{
	CHECK_MSG( !Str.IsEmpty(), "������!  " )

	CString Filter= "�ı��ļ�(*.txt)|*.txt|�����ļ�(*.*)|*.*||";

	CMyFileDialog FileDlg(FALSE, "txt", NULL, OFN_HIDEREADONLY, Filter);

    CHECK( FileDlg.DoModal()==IDOK )

	CHECK( SaveStrToFile(Str.GetBuffer(0),FileDlg.GetPathName().GetBuffer(0)) )

	return true;
}
/******************************************************************************/
//	���ƣ�GetFolder
//	���ܣ�Ŀ¼ѡ��Ի���
//  ������
//	���أ�ѡ��ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CWindow::GetFolder(CString &Folder)
{
	BROWSEINFO	bi; 
	ITEMIDLIST	*pidl; 
    char		Dir[256];

	memset(&bi, 0, sizeof(bi));
	bi.pszDisplayName = Dir; 
	bi.lpszTitle = "��ѡ��Ŀ¼"; 
	bi.ulFlags = BIF_RETURNONLYFSDIRS; 

	pidl = SHBrowseForFolder(&bi);
	CHECK( pidl!=NULL ) 
	SHGetPathFromIDList(pidl,Dir);
    GlobalFree(pidl);
	Folder = Dir;

	return true;
}
/******************************************************************************/
//	���ƣ�IsFileExist
//	���ܣ�����ļ��Ƿ����
//  ������
//	���أ����ڷ���true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CWindow::IsFileExist(const char *File)
{
	HFILE	fh;

	CHECK( File )
	CHECK( (fh=_lopen(File,OF_READ)) != -1 )

	_lclose(fh);
	return	true;
}
/******************************************************************************/
//	���ƣ�GetStrFromFile
//	���ܣ����ļ��ж�ȡ�ַ���
//  ������
//	���أ���ȡ�ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CWindow::GetStrFromFile(char *Str,const char *File)
{
	CHECK( Str && File );

    HFILE	fh;

    CHECK_MSG( (fh=_lopen(File,OF_READ)) != -1, "�����޷����ļ�! " )

	int len = _lread(fh,Str,STR_MAXLEN);
    Str[len] = '\0'; 
   
    _lclose(fh);
	return true;
}
/******************************************************************************/
//	���ƣ�SaveStrToFile
//	���ܣ������ַ������ļ���
//  ������
//	���أ�����ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CWindow::SaveStrToFile(const char *Str,const char *File)
{
	CHECK( Str && File );

    HFILE	fh;

	if( IsFileExist(File) )
		CHECK( IDYES == ShowMessage("���ļ��Ѵ��ڣ��Ƿ񸲸�? ","��ʾ",
			                        MB_YESNO | MB_ICONQUESTION) )
    CHECK_MSG( (fh=_lcreat(File,0)) != -1, "�����޷������ļ�! ")
	_lwrite(fh,Str,strlen(Str));

    _lclose(fh);
	return true;
}
/******************************************************************************/
//	���ƣ�GetFileNameWithExt
//	���ܣ���ȡ�ļ���(����չ��)
//  ������
//	���أ��ļ���(����չ��)
//  ��ע��
/******************************************************************************/
CString CWindow::GetFileNameWithExt(const CString &File)
{
    int i,j=File.GetLength();
    CString Name;

	for(i=j-1; i>=0 && File.GetAt(i)!='\\'; --i);
	Name = (i<0 ) ? File : File.Right(j-i-1);

	return Name;
}
/******************************************************************************/
//	���ƣ�GetFileNameNoExt
//	���ܣ���ȡ�ļ���(����չ��)
//  ������
//	���أ��ļ���(����չ��)
//  ��ע��
/******************************************************************************/
CString CWindow::GetFileNameNoExt(const CString &File)
{
    CString Name = GetFileNameWithExt(File);
	CString Ext  = GetExtName(Name);
	int		len  = Ext.IsEmpty() ? 0 : Ext.GetLength()+1;

	Name = Name.Left(Name.GetLength()-len);

	return Name;
}
/******************************************************************************/
//	���ƣ�GetExtName
//	���ܣ���ȡ��չ��
//  ������
//	���أ���չ��
//  ��ע��
/******************************************************************************/
CString CWindow::GetExtName(const CString &File)
{
    int i,j=File.GetLength();
    CString Ext;

	for(i=0; i<j && File.GetAt(i)!='.'; ++i);
	Ext = i<j ? File.Right(j-i-1) : "";

	return Ext;
}
/******************************************************************************/
//	���ƣ�GetPath
//	���ܣ���ȡ·��
//  ������
//	���أ�·��
//  ��ע��
/******************************************************************************/
CString CWindow::GetPath(const CString &File)
{
    int i,j=File.GetLength();
    CString Path;

	for(i=j-1; i>=0 && File.GetAt(i)!='\\'; --i);
	Path = (i<0 ) ? "" : File.Left(i);

	return Path;
}

///////////////////////////////////////////////////////////////////////////////
// End of Files
///////////////////////////////////////////////////////////////////////////////
