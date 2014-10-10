//Download by http://www.NewXing.com
// GfL.cpp: implementation of the CGfL class.
//
//////////////////////////////////////////////////////////////////////
#include "stdafx.h"
#include "Minica.h"
#include "GfL.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

///////////////////////////////////////////////////////////////////////////////
// CGfL Functions
///////////////////////////////////////////////////////////////////////////////
#define CHECK(x)		{if( !(x) ) return false;}

/******************************************************************************/
//	���ƣ�Transform
//	���ܣ��任
//  ������
//	���أ��任�ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::Transform(bool *Out,bool *In,const char *Table,UINT len)
{
    static bool Tmp[128];

	CHECK( Out && In && Table && len<=128 )

	for(UINT i=0; i<len; ++i)
	{
		// ע������� Table[i]-1����Ϊ���е���ʼλΪ1����C�����е������±��0��ʼ
        Tmp[i] = In[ Table[i]-1 ];
	}

    memcpy(Out,Tmp,len);

	return true;
}
/******************************************************************************/
//	���ƣ�RotateL
//	���ܣ�ѭ������
//  ������len���ƶ����ݵĳ��ȣ�loop��ѭ�����Ƶĳ���
//	���أ����Ƴɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::RotateL(char *In,UINT len,UINT loop)
{
    static char Tmp[256];

	CHECK( In && len && (loop%=len)<=256 )

	memcpy(Tmp,In,loop);
    memcpy(In,In+loop,len-loop);
    memcpy(In+len-loop,Tmp,loop);

	return true;
}
/******************************************************************************/
//	���ƣ�Xor
//	���ܣ����
//  ������
//	���أ����ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::Xor(bool *InA,const bool *InB,UINT len)
{
	CHECK( InA && InB )

	for(UINT i=0; i<len; ++i)
	{
        InA[i] ^= InB[i];   
	}

	return true;
}
/******************************************************************************/
//	���ƣ�ByteToBit
//	���ܣ����ֽ���ת����λ��
//  ������len���ֽ��鳤�ȣ�num��һ���ֽ�ת���ɼ���λ�ֽ�
//	���أ�ת���ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::ByteToBit(bool *Out,const char *In,UINT len,UINT num)
{
	CHECK( Out && In && num<=8 )

	for(UINT i=0,j; i<len; ++i,Out+=num)
	{
		for(j=0; j<num; ++j)
		{
			Out[j] = (In[i]>>j) & 1;
		}
	}

	return true;
}
/******************************************************************************/
//	���ƣ�BitToByte
//	���ܣ���λ��ת�����ֽ���
//  ������len��λ�鳤�ȣ�num������λ�ֽ�ת����һ���ֽ�
//	���أ�ת���ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::BitToByte(char *Out,const bool *In,UINT len,UINT num)
{
	CHECK( Out && In )

	memset(Out,0,(len+num-1)/num);
    for(UINT i=0,j,L=len/num; i<L; ++i,In+=num)
	{
		for(j=0; j<num; ++j)
		{
			Out[i] |= In[j]<<j;
		}
	}
	for(j=0; j<len%num; ++j)
	{
		Out[i] |= In[j]<<j;
	}

	return true;
}
/******************************************************************************/
//	���ƣ�HalfByteToByte
//	���ܣ������ֽ���ת�����ֽ���
//  ������
//	���أ�ת���ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::HalfByteToByte(char *Out,const char *In,UINT len)
{
	CHECK( Out && In )

	for(UINT i=0,j=len>>1; i<j; ++i)
	{
		*Out = In[0];
		*Out |= In[1]<<4;
		++Out; In += 2;
	}
   
	if( len%2 )
		*Out = *In;

	return true;
}
/******************************************************************************/
//	���ƣ�ByteToHalfByte
//	���ܣ����ֽ���ת���ɰ��ֽ���
//  ������
//	���أ�ת���ɹ�����true�����򷵻�false
//  ��ע��
/******************************************************************************/
bool CGfL::ByteToHalfByte(char *Out,const char *In,UINT len)
{
	CHECK( Out && In )

	for(UINT i=0; i<len; ++i)
	{
		Out[0] = (*In)&0xf;
		Out[1] = ((*In)>>4)&0xf;
		Out += 2; ++In;
	}

	return true;
}
/******************************************************************************/
//	���ƣ�StrToHalfByte
//	���ܣ����ַ���ת���ɰ��ֽ���
//  ������
//	���أ��Ϸ��ַ�('0'-'9','A'-'F')�ĸ���
//  ��ע��
/******************************************************************************/
int CGfL::StrToHalfByte(char *Out,char *In,UINT len)
{
	CHECK( Out && In )

	for(UINT i=0,j=0; i<len; ++i)
	{
		if( (In[i]>='0') && (In[i]<='9') )
			Out[j++] = In[i]-'0';
		else if( (In[i]>='A') && (In[i]<='F') )
			Out[j++] = In[i]-'A'+10;
		else if( (In[i]>='a') && (In[i]<='f') )
			Out[j++] = In[i]-'a'+10;
	}

	return j;
}
/******************************************************************************/
//	���ƣ�HalfByteToStr
//	���ܣ������ֽ���ת�����ַ���
//  ������
//	���أ��Ϸ���(0-15)�ĸ���
//  ��ע��
/******************************************************************************/
int  CGfL::HalfByteToStr(char *Out,char *In,UINT len)
{
	CHECK( Out && In )

	for(UINT i=0,j=0; i<len; ++i)
	{
		if( (In[i]>=0) && (In[i]<10) )
			Out[j++] = In[i]+'0';
		else if( (In[i]>9) && (In[i]<16) )
			Out[j++] = In[i]-10+'A';
	}
	Out[j] = '\0';

	return j-1;
}

///////////////////////////////////////////////////////////////////////////////
// End of Files
///////////////////////////////////////////////////////////////////////////////
