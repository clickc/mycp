//Download by http://www.NewXing.com
// FileInOut.cpp: implementation of the CFileInOut class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "Minica.h"
#include "FileInOut.h"
#include "Window.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
CFileInOut::CFileInOut()
{
	fh_out = fh_in = 0;
}

CFileInOut::~CFileInOut()
{
	CloseFile();
}

///////////////////////////////////////////////////////////////////////////////
// CFileInOut Functions
///////////////////////////////////////////////////////////////////////////////
#define CHECK(x)			{if( !(x) ) return false;}
#define CHECK_MSG(x,msg)	{if( !(x) ){CWindow::ShowMessage(msg);return false;}}

/******************************************************************************/
//	���ƣ�OpenFile
//	���ܣ�����������ļ�
//  ������
//	���أ��򿪳ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CFileInOut::OpenFile(const char *OutFile,const char *InFile)
{
	// �����ļ�!=����ļ�
    CHECK_MSG( OutFile && InFile && strcmp(OutFile,InFile), "��������������������������ͬ!" )
	CHECK_MSG( (fh_in=_lopen(InFile,OF_READ))!=-1, "�����޷��������ļ�! " )

	if( CWindow::IsFileExist(OutFile) )
	{
		if( IDYES != MessageBox(GetActiveWindow(),"���ļ��Ѵ��ڣ��Ƿ񸲸�? ",
			                   "��ʾ",MB_YESNO | MB_ICONQUESTION) )
		{
			_lclose(fh_in);
			return false;
		}
	}
	if( (fh_out=_lcreat(OutFile,0)) == -1 )
	{
		_lclose(fh_in);
		CWindow::ShowMessage("�����޷���������ļ�! ");
		return	false;
	}

	return	true;
}
/******************************************************************************/
//	���ƣ�CloseFile()
//	���ܣ��ر���������ļ�
//  ������
//	���أ�
//  ��ע��
/******************************************************************************/
void CFileInOut::CloseFile()
{
	if( fh_in )
		_lclose(fh_in);
	if( fh_out )
		_lclose(fh_out);
}

/******************************************************************************/
//	���ƣ�RunError
//	���ܣ�������
//  ������
//	���أ�
//  ��ע��
/******************************************************************************/
void CFileInOut::RunError(const char *OutFile)
{
	CloseFile();
	if(OutFile)
		DeleteFile(OutFile);
}

///////////////////////////////////////////////////////////////////////////////
// End of Files
///////////////////////////////////////////////////////////////////////////////

