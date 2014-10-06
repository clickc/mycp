//Download by http://www.NewXing.com
// FileInOut.h: interface for the CFileInOut class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FILEINOUT_H__581E2920_E13B_11D6_B0C2_00E04C391A51__INCLUDED_)
#define AFX_FILEINOUT_H__581E2920_E13B_11D6_B0C2_00E04C391A51__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000


//////////////////////////////////////////////////////////////////////
//��������ļ���(class CFileInOut)
class CFileInOut  
{
public:
	CFileInOut();
	virtual ~CFileInOut();
    
	bool    OpenFile(const char *OutFile,const char *InFile);//����������ļ�
	void    CloseFile();//�ر���������ļ�
	void    RunError(const char *OutFile);//������

protected:
    HFILE	fh_out,fh_in;//��������ļ����
};

#endif // !defined(AFX_FILEINOUT_H__581E2920_E13B_11D6_B0C2_00E04C391A51__INCLUDED_)
