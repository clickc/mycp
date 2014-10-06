//Download by http://www.NewXing.com
// Window.h: interface for the CWindow class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_WINDOW_H__68BACE00_E104_11D6_B0C2_00E04C391A51__INCLUDED_)
#define AFX_WINDOW_H__68BACE00_E104_11D6_B0C2_00E04C391A51__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000


//////////////////////////////////////////////////////////////////////
//Windows֧����(class CWindow)
class CWindow  
{
public:
	CWindow();
	virtual ~CWindow();

	void    ShowWaitCursor();//��ʾ�ȴ����
	void    EndWaitCursor();//�����ȴ����
	void    SetWindowCaption(char *Str,int v1=0,int v2=0,int v3=0);//���ô��ڱ���
	static  int  ShowMessage(const char *msg,const char *title="��ʾ",
		                     UINT flage=MB_ICONINFORMATION);//��ʾ��Ϣ��
	static  bool InputStr(CString &Str);//��ѡ���ļ��Ի��򣬵����ַ���
	static  bool OutputStr(CString &Str);//��ѡ���ļ��Ի��򣬵����ַ���
	static  bool GetFolder(CString &Folder);//Ŀ¼ѡ��Ի���
   
	static  bool IsFileExist(const char *File);//����ļ��Ƿ����
	static  bool GetStrFromFile(char *Str,const char *File);//���ļ��ж�ȡ�ַ���
	static  bool SaveStrToFile(const char *Str,const char *File);//�����ַ������ļ���
    static  CString GetFileNameWithExt(const CString &File);//��ȡ�ļ���(����չ��)
	static  CString GetFileNameNoExt(const CString &File);//��ȡ�ļ���(����չ��)
    static  CString GetExtName(const CString &File);//��ȡ��չ��
    static  CString GetPath(const CString &File);//��ȡ·��

private:
	CWnd		wnd;//�����ָࣺ��ǰ�����
	CString	    caption;//���洰�ڱ���
};

#endif // !defined(AFX_WINDOW_H__68BACE00_E104_11D6_B0C2_00E04C391A51__INCLUDED_)
