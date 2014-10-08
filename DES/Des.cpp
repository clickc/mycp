//Download by http://www.NewXing.com
// Des.cpp: implementation of the CDes class.
//
//////////////////////////////////////////////////////////////////////
#include "stdafx.h"
#include "Minica.h"
#include "Des.h"
#include "Window.h"
#include "GfL.h"
#include "cc.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

///////////////////////////////////////////////////////////////////////////////
// DES Tables Define
///////////////////////////////////////////////////////////////////////////////

// initial permutation IP
const static char IP_Table[64] = 
{
	58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
	62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
	57, 49, 41, 33, 25, 17,  9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
    61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
};
// final permutation IP^-1 
const static char IPR_Table[64] = 
{
	40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
	38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
    36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
	34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41,  9, 49, 17, 57, 25
};
// expansion operation matrix
static const char E_Table[48] = 
{
	32,  1,  2,  3,  4,  5,  4,  5,  6,  7,  8,  9,
	 8,  9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
	16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
	24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,  1
};
// 32-bit permutation function P used on the output of the S-boxes 
const static char P_Table[32] = 
{
	16, 7, 20, 21, 29, 12, 28, 17, 1,  15, 23, 26, 5,  18, 31, 10,
	2,  8, 24, 14, 32, 27, 3,  9,  19, 13, 30, 6,  22, 11, 4,  25
};
// permuted choice table (key) 
const static char PC1_Table[56] = 
{
	57, 49, 41, 33, 25, 17,  9,  1, 58, 50, 42, 34, 26, 18,
	10,  2, 59, 51, 43, 35, 27, 19, 11,  3, 60, 52, 44, 36,
	63, 55, 47, 39, 31, 23, 15,  7, 62, 54, 46, 38, 30, 22,
	14,  6, 61, 53, 45, 37, 29, 21, 13,  5, 28, 20, 12,  4
};
// permuted choice key (table) 
const static char PC2_Table[48] = 
{
	14, 17, 11, 24,  1,  5,  3, 28, 15,  6, 21, 10,
	23, 19, 12,  4, 26,  8, 16,  7, 27, 20, 13,  2,
	41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
	44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
};
// number left rotations of pc1 
const static char LOOP_Table[16] = 
{
	1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1
};
// The (in)famous S-boxes 
const static char S_Box[8][4][16] = 
{
	// S1 
	14,	 4,	13,	 1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7,
	 0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8,
	 4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0,
    15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13,
	// S2 
    15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10,
	 3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5,
	 0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15,
    13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9,
	// S3 
    10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8,
	13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1,
	13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7,
     1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12,
	// S4 
     7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15,
	13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9,
	10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4,
     3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14,
	// S5 
     2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9,
	14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6,
	 4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14,
    11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3,
	// S6 
    12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11,
	10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8,
	 9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6,
     4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13,
	// S7 
     4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1,
	13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6,
	 1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2,
     6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12,
	// S8 
    13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7,
	 1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2,
	 7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8,
     2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11
};

///////////////////////////////////////////////////////////////////////////////
// CDes Functions
///////////////////////////////////////////////////////////////////////////////
#define CHECK(x)			{if( !(x) ) return false;}
#define CHECK_MSG(x,msg)	{if( !(x) ){CWindow::ShowMessage(msg);return false;}}
#define FILE_CHECK(x,file)	{if( !(x) ) {RunError(file);return false;}}

/******************************************************************************/
//	���ƣ�Encrypt
//	���ܣ�����
//  ������KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//	���أ����ܳɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ��ϵͳ�Զ�ʹ��3��DES����
/******************************************************************************/
bool CDes::Encrypt(const char *OutFile,const char *InFile,const char *KeyStr)
{
	CHECK( KeyStr && OpenFile(OutFile,InFile) )

	FILE_CHECK( Encrypt(fh_out,fh_in,KeyStr),OutFile )

	CloseFile();
	return true;
}
/******************************************************************************/
//	���ƣ�Decrypt
//	���ܣ�����
//  ������KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//	���أ����ܳɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ��ϵͳ�Զ�ʹ��3��DES����
/******************************************************************************/
bool CDes::Decrypt(const char *OutFile,const char *InFile,const char *KeyStr)
{
	CHECK( KeyStr && OpenFile(OutFile,InFile) )

	FILE_CHECK( Decrypt(fh_out,fh_in,KeyStr),OutFile )

	CloseFile();
	return true;
}
/******************************************************************************/
//	���ƣ�Encrypt
//	���ܣ�����
//  ������fh_out,fh_inΪ������������KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//	���أ����ܳɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ��ϵͳ�Զ�ʹ��3��DES����
/******************************************************************************/
bool CDes::Encrypt(HFILE &fh_out,HFILE &fh_in,const char *KeyStr)
{
	CWindow  wnd;
	long     len,k=0,TBlock;

    // ��������Կ
    CHECK( SetSubKey(KeyStr) )
	// �汾��Ϣ
	////deshead.Ver = 1;
    // �ļ�������Ϣ
	deshead.TLen = GetFileSize((HANDLE)fh_in,0);
	char ss[8];
    sprintf(ss,"%ld",deshead.TLen);  
    CWindow::ShowMessage(ss);
	// �����ܿ���
    TBlock=(deshead.TLen+BUFSIZE-1)/BUFSIZE;
	char s[48];
    sprintf(s,"%ld",TBlock);  
    CWindow::ShowMessage(s);
	// ������Կ��(���ڽ���ʱ��֤��Կ����ȷ��)
	memset(deskey,0,16);
	strcpy(deskey,KeyStr);
	CWindow::ShowMessage(deskey);
    sprintf(s,"size deskey is %d",sizeof(deskey));  
    CWindow::ShowMessage(s);

	Encrypt(deshead.DesKey,deskey,16);
		char sss[8];
    sprintf(sss,"0x%x",deshead.DesKey);  
		CWindow::ShowMessage(sss);
	// д����Ϣͷ
	_lwrite(fh_out,(char*)&deshead,sizeof(deshead));
    // ��ʾ�ȴ����
	wnd.ShowWaitCursor();

	// ��ȡ���ĵ�������
	while( (len=_lread(fh_in,databuf,BUFSIZE)) >0 )
	{   // ��ʾ���ܽ���
        wnd.SetWindowCaption("����%d�����ݣ�DES���ڼ��ܵ�%d��......",TBlock,++k);
        // �����������ȱ�Ϊ8�ı���
		len = ((len+7)>>3)<<3;
		// �ڻ������м���
		Encrypt(databuf,databuf,len);
		// ������д������ļ�
		_lwrite(fh_out,databuf,len);
	}

    // �����ȴ����
	wnd.EndWaitCursor();
	return true;
}
/******************************************************************************/
//	���ƣ�Decrypt
//	���ܣ�����
//  ������fh_out,fh_inΪ������������KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//	���أ����ܳɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ��ϵͳ�Զ�ʹ��3��DES����
/******************************************************************************/
bool CDes::Decrypt(HFILE &fh_out,HFILE &fh_in,const char *KeyStr)
{
	CWindow  wnd;
	long     len,k=0,TBlock;

    // ��������Կ
    CHECK( SetSubKey(KeyStr) )
	// ��ȡ��Ϣͷ����鳤��
	CHECK_MSG( _lread(fh_in,&deshead,sizeof(deshead)) == sizeof(deshead),
	           "���󣺸��ļ�������Ч��DES�����ļ�!" )
    // �汾����
<<<<<<< HEAD
	////CHECK_MSG( deshead.Ver ==1,"�ð�����޷����ܴ��ļ���\n��ʹ�øó�������°档")
=======
    //CHECK_MSG( deshead.Ver ==1,"�ð�����޷����ܴ��ļ���\n��ʹ�øó�������°档")
>>>>>>> origin/master
	// ������Կ��
    Decrypt(deshead.DesKey,deshead.DesKey,16);
	// ��֤��Կ����ȷ��
	memset(deskey,0,16);
	strcpy(deskey,KeyStr);//��Կ������һ��<=16
<<<<<<< HEAD
    ////CHECK_MSG( !memcmp(deshead.DesKey,deskey,16), "����DES��Կ����ȷ! ");
=======
	CWindow::ShowMessage(deshead.DesKey);
    CWindow::ShowMessage(deskey);
    CHECK_MSG( !memcmp(deshead.DesKey,deskey,16), "����DES��Կ����ȷ! ");
>>>>>>> origin/master
	// �����ܿ���
	TBlock=(deshead.TLen+BUFSIZE-1)/BUFSIZE;
	// ��ʾ�ȴ����
	wnd.ShowWaitCursor();

	// ��ȡ���ĵ�������
	while( (len=_lread(fh_in,databuf,BUFSIZE)) >0 )
	{   // ��ʾ���ܽ���
    	wnd.SetWindowCaption("����%d�����ݣ�DES���ڽ��ܵ�%d��......",TBlock,++k);
		// �����������ȱ�Ϊ8�ı���
		len = ((len+7)>>3)<<3;
		// �ڻ������н���
		Decrypt(databuf,databuf,len);
		// ������д������ļ�
		_lwrite(fh_out,databuf,len);
	}
	// ���ý����ļ�����
	_llseek(fh_out,deshead.TLen,SEEK_SET);
	SetEndOfFile((HANDLE)fh_out);

	// �����ȴ����
	wnd.EndWaitCursor();
	return true;
}
/******************************************************************************/
//	���ƣ�Encrypt
//	���ܣ�����
//  ������len�����ݳ��ȣ�������8�ı�����KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//        KeyStrĬ��ֵΪ0��������ڵ���ǰ���ù�����Կ�����ʡ�Ըò�������:
//        SetSubKey(KeyStr);Encrypt(data,data,len);��:Encrypt(data,data,len,KeyStr);
//	���أ����ܳɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ��ϵͳ�Զ�ʹ��3��DES����
/******************************************************************************/
bool CDes::Encrypt(char *Out,char *In,UINT len,const char *KeyStr)
{
    CHECK( Out && In && !(len&0x7) )
    char s[48];
    sprintf(s,"In E is 0x%x \n",In);  
               CWindow::ShowMessage(s);
	if( KeyStr )
		CHECK( SetSubKey(KeyStr) )

	if( !Is3DES )
	{   // 1��DES
		for(int i=0,j=len>>3; i<j; ++i)
		{
			
                sprintf(s,"SubKey[0] is 0x%x \n",&SubKey[0]);  
               CWindow::ShowMessage(s);

			DES(Out,In,&SubKey[0],ENCRYPT);
			Out += 8; In += 8;
		}
	}
	else
	{   // 3��DES E-D-E
		for(int i=0,j=len>>3; i<j; ++i)
		{
			DES(Out,In, &SubKey[0],ENCRYPT);
			DES(Out,Out,&SubKey[1],DECRYPT);
			DES(Out,Out,&SubKey[0],ENCRYPT);
			Out += 8; In += 8;
		}
	}

	return true;
}
/******************************************************************************/
//	���ƣ�Decrypt
//	���ܣ�����
//  ������len�����ݳ��ȣ�������8�ı�����KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//        KeyStrĬ��ֵΪ0��������ڵ���ǰ���ù�����Կ�����ʡ�Ըò�������:
//        SetSubKey(KeyStr);Decrypt(data,data,len);��:Decrypt(data,data,len,KeyStr);
//	���أ����ܳɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ��ϵͳ�Զ�ʹ��3��DES����
/******************************************************************************/
bool CDes::Decrypt(char *Out,char *In,UINT len,const char *KeyStr)
{
    CHECK( Out && In && !(len&0x7) )

	if( KeyStr )
		CHECK( SetSubKey(KeyStr) )
    if( !Is3DES )
	{   // 1��DES
		for(int i=0,j=len>>3; i<j; ++i)
		{
			DES(Out,In,&SubKey[0],DECRYPT);
			Out += 8; In += 8;
		}
	}
		
	else
	{   // 3��DES D-E-D
		for(int i=0,j=len>>3; i<j; ++i)
		{
			DES(Out,In, &SubKey[0],DECRYPT);
			DES(Out,Out,&SubKey[1],ENCRYPT);
			DES(Out,Out,&SubKey[0],DECRYPT);
			Out += 8; In += 8;
		}
	}
	

	return true;
}
/******************************************************************************/
//	���ƣ�SetSubKey
//	���ܣ���������Կ����������Կ
//  ������KeyStrΪ0��β����Կ��������<=16����Ϊ�����ַ�
//	���أ����óɹ�����true�����򷵻�false
//  ��ע������Կ����>8ʱ�������õ�2��Կ������3��DES��־
/******************************************************************************/
bool CDes::SetSubKey(const char *KeyStr)
{
    int		len;

	CHECK_MSG( KeyStr && (len=strlen(KeyStr))<=16 && len>0, 
		       "����DES��Կ��������Կ������Կ̫��!" )
	memset(deskey,0,16);
	memcpy(deskey,KeyStr,len);
	// ���õ�1��Կ
    /*
	SetSubKey(&SubKey[0],deskey);
	Is3DES = false;
	if( len>8 )
	{   // ��3��DES��־
		Is3DES = true;
		// ���õ�2��Կ
		SetSubKey(&SubKey[1],&deskey[8]);
	}
    */
	return true;
}
/******************************************************************************/
//	���ƣ�RandKeyStr
//	���ܣ����������Կ��
//  ������
//	���أ�KeyStr��ַ
//  ��ע��
/******************************************************************************/
char* CDes::RandKeyStr(char KeyStr[9])
{
    CHECK( KeyStr )

	srand(GetTickCount());
    for(int i=0; i<8; ++i)
	{
        KeyStr[i] = rand()%256;
	}
	while( !( KeyStr[7] && 0xf0 ) )
        KeyStr[7] = rand()%256;
	KeyStr[8] = '\0';
    // �������Խ������Ӳ���
	return KeyStr;
}
/******************************************************************************/
//	���ƣ�DES
//	���ܣ���/����
//  ������Type��ENCRYPT:����,DECRYPT:����
//	���أ�
//  ��ע��˽�к��������ü��ָ���Ƿ�Ϊ��
/******************************************************************************/
void CDes::DES(char Out[8],char In[8],const PSubKey pSubKey,bool Type)
{
    static bool M[64],Tmp[32],*Li,*Ri;

    // �������ֽ���ֽ��λ��
   	CGfL::ByteToBit(M,In,8);
   // char ss[8];
   // sprintf(ss,"M is 0x%x",M);  
   // CWindow::ShowMessage(ss);

   /// sprintf(ss,"In is 0x%x",In);  
   /// CWindow::ShowMessage(ss);

    // ��ʼ�任
    CGfL::Transform(M,M,IP_Table,64);

	if( Type == ENCRYPT )
		Li=&M[0],Ri=&M[32];
	else
		Li=&M[32],Ri=&M[0];

    for(int i=0; i<16; ++i)
    {
        memcpy(Tmp,Ri,32);
		if( Type == ENCRYPT )
			F_func(Ri,(*pSubKey)[i]);
		else
			F_func(Ri,(*pSubKey)[15-i]);
        CGfL::Xor(Ri,Li,32);
        memcpy(Li,Tmp,32);
    }
    // ���ʼ�任
    CGfL::Transform(M,M,IPR_Table,64);
	// ��λ��ϲ�������ֽ���
	CGfL::BitToByte(Out,M,64);
}
/******************************************************************************/
//	���ƣ�SetSubKey
//	���ܣ���������Կ����16Ȧ����Կ
//  ������
//	���أ�
//  ��ע��˽�к��������ü��ָ���Ƿ�Ϊ��
/******************************************************************************/
void CDes::SetSubKey(PSubKey pSubKey,const char Key[8])
{

    static  bool K[64],*KL=&K[0],*KR=&K[28];
	char ss[48];
    sprintf(ss,"Key is 0x%x",Key);  
    CWindow::ShowMessage(ss);
    // ����Կ�ֽ���ֽ����Կλ��
	CGfL::ByteToBit(K,Key,8);
	// ��Կ�任
    CGfL::Transform(K,K,PC1_Table,56);
    for(int i=0; i<16; ++i)
    {	// ѭ������
        CGfL::RotateL((char*)KL,28,LOOP_Table[i]);
        CGfL::RotateL((char*)KR,28,LOOP_Table[i]);
	
		// ѹ���任
        CGfL::Transform((*pSubKey)[i],K,PC2_Table,48);
	
        sprintf(ss,"pSubKey [%d] is 0x%x",i,(*pSubKey)[i]);  
        CWindow::ShowMessage(ss);
    }
}
/******************************************************************************/
//	���ƣ�F_func
//	���ܣ�f ����
//  ������
//	���أ�
//  ��ע��˽�к��������ü��ָ���Ƿ�Ϊ��
/******************************************************************************/
void CDes::F_func(bool In[32],const bool Ki[48])
{
    static bool MR[48];

	// ��չ�任
    CGfL::Transform(MR,In,E_Table,48);
    CGfL::Xor(MR,Ki,48);
    S_func(In,MR);
	// P �任
    CGfL::Transform(In,In,P_Table,32);
}
/******************************************************************************/
//	���ƣ�S_func
//	���ܣ�S �д���
//  ������
//	���أ�
//  ��ע��˽�к��������ü��ָ���Ƿ�Ϊ��
/******************************************************************************/
void CDes::S_func(bool Out[32],const bool In[48])
{
    for(char i=0,j,k; i<8; ++i)
    {
        j = (In[0]<<1) + In[5];
        k = (In[1]<<3) + (In[2]<<2) + (In[3]<<1) + In[4];
		CGfL::ByteToBit(Out,&S_Box[i][j][k],1,4);
		Out +=4; In += 6;
    }
}

///////////////////////////////////////////////////////////////////////////////
// End of Files
///////////////////////////////////////////////////////////////////////////////
