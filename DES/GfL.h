//Download by http://www.NewXing.com
// GfL.h: interface for the CGfL class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_GFL_H__A14B2820_E049_11D6_B0C1_00E04C391A51__INCLUDED_)
#define AFX_GFL_H__A14B2820_E049_11D6_B0C1_00E04C391A51__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000


//////////////////////////////////////////////////////////////////////
//ͨ�ÿ�(class CGfL)
class CGfL
{
public:
   
	static  bool Transform(bool *Out,bool *In,const char *Table,UINT len);//�任
	static  bool Xor(bool *InA,const bool *InB,UINT len);//���
	static  bool RotateL(char *In,UINT len,UINT loop);//ѭ������
   
	static  bool ByteToBit(bool *Out,const char *In,UINT len,UINT num=8);//�ֽ���ת����λ��
	static  bool BitToByte(char *Out,const bool *In,UINT len,UINT num=8);//λ��ת�����ֽ���
	static  bool HalfByteToByte(char *Out,const char *In,UINT len);//���ֽ���ת�����ֽ���
	static  bool ByteToHalfByte(char *Out,const char *In,UINT len);//�ֽ���ת���ɰ��ֽ���
	static  int  StrToHalfByte(char *Out,char *In,UINT len);//�ַ���ת���ɰ��ֽ���
	static  int  HalfByteToStr(char *Out,char *In,UINT len);//���ֽ���ת�����ַ���
};

#endif // !defined(AFX_GFL_H__A14B2820_E049_11D6_B0C1_00E04C391A51__INCLUDED_)
