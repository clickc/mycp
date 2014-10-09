#include <stdio.h>

void test(char *a)
{

  printf("ta is %s \n",a);

}

int main(void)
{
  int a=5;
  int *p=&a;
  printf("p is 0x%x\n",p);
  printf("*p is %d\n",*p);
  
  char c='z';
  char *b="测试";
  printf("b.size: %d \n",sizeof(b));
  
  printf("c is %c \n",c);

  char *d="字符串";

  char ar[14]="字符串2";

  
  test(ar);

}
