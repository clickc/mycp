#include <iostream>
#include "Tool.h"

int nol_ll(const char *file) 
     /* Grep through file and count number of lines, maximum number of
        spaces per line, and longest line. */
{
  FILE *fl;
  int ic;
  char c;
  long current_length,current_wol;

  if ((fl = fopen (file, "r")) == NULL)
  { perror (file); exit (1); }
  current_length=0;
  while((ic=getc(fl)) != EOF) {
    c=(char)ic;
    current_length++;
  }
  fclose(fl);

  return current_length;
}
