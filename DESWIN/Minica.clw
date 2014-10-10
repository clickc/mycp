; CLW file contains information for the MFC ClassWizard

[General Info]
Version=1
LastClass=CMinicaDlg
LastTemplate=CFileDialog
NewFileInclude1=#include "stdafx.h"
NewFileInclude2=#include "Minica.h"

ClassCount=6
Class1=CMinicaApp
Class2=CMinicaDlg
Class3=CAboutDlg

ResourceCount=3
Resource1=IDD_ABOUTBOX
Resource2=IDR_MAINFRAME
Class4=CCreditStatic
Class5=CLabel
Class6=CMyFileDialog
Resource3=IDD_MINICA_DIALOG

[CLS:CMinicaApp]
Type=0
HeaderFile=Minica.h
ImplementationFile=Minica.cpp
Filter=N

[CLS:CMinicaDlg]
Type=0
HeaderFile=MinicaDlg.h
ImplementationFile=MinicaDlg.cpp
Filter=D
LastObject=CMinicaDlg
BaseClass=CDialog
VirtualFilter=dWC

[CLS:CAboutDlg]
Type=0
HeaderFile=aboutdlg.h
ImplementationFile=aboutdlg.cpp
BaseClass=CDialog
LastObject=CAboutDlg

[DLG:IDD_ABOUTBOX]
Type=1
Class=CAboutDlg
ControlCount=9
Control1=IDC_STATIC,static,1342177283
Control2=IDC_DISPLAY_STATIC,static,1342308352
Control3=IDC_STATIC,static,1342308352
Control4=IDC_STATIC,static,1342308352
Control5=IDC_STATIC,static,1342308352
Control6=IDC_NEUHOMEPAGE_STATIC,static,1342308352
Control7=IDC_STATIC,static,1342308352
Control8=IDC_MYEMAIL_STATIC,static,1342308352
Control9=IDOK,button,1342275584

[DLG:IDD_MINICA_DIALOG]
Type=1
Class=CMinicaDlg
ControlCount=30
Control1=IDC_STATIC,button,1342177287
Control2=IDC_STATIC,static,1342308352
Control3=IDC_Input,edit,1350631552
Control4=IDC_Browse,button,1342275584
Control5=IDC_STATIC,static,1342308352
Control6=IDC_Output,edit,1350631552
Control7=IDC_OutFolder,button,1342275584
Control8=IDC_DefPathCHECK,button,1342242819
Control9=IDC_DEFPATHSTATIC,static,1476526080
Control10=IDC_DefPath,edit,1484849280
Control11=IDC_Open,button,1342275584
Control12=IDC_DefFolder,button,1476493312
Control13=IDC_STATIC,button,1342177287
Control14=IDC_STATIC,static,1342308352
Control15=IDC_STATIC,static,1342308352
Control16=IDC_DesKey,edit,1350631584
Control17=IDC_DesKey2,edit,1350631584
Control18=IDC_STATIC,static,1342308352
Control19=IDC_STATIC,button,1342177287
Control20=IDC_DelOrgFileCHECK,button,1342242819
Control21=IDC_DelEptFileCHECK,button,1342242819
Control22=IDC_DelNoteCHECK,button,1342242819
Control23=IDC_Run,button,1342275584
Control24=IDC_ABOUT,button,1342275584
Control25=ID_EXIT,button,1342275584
Control26=IDC_Credit,static,1342312448
Control27=IDC_STATIC,button,1342177287
Control28=IDC_EDIT_DEST,edit,1350631552
Control29=IDC_BUTTON_RESULT,button,1342275584
Control30=IDC_STATIC,static,1342308352

[CLS:CCreditStatic]
Type=0
HeaderFile=CreditStatic.h
ImplementationFile=CreditStatic.cpp
BaseClass=CStatic
Filter=W

[CLS:CLabel]
Type=0
HeaderFile=Label.h
ImplementationFile=Label.cpp
BaseClass=CStatic
Filter=W

[CLS:CMyFileDialog]
Type=0
HeaderFile=MyFileDialog.h
ImplementationFile=MyFileDialog.cpp
BaseClass=CFileDialog
Filter=D

