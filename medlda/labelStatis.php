<?php

$stdin = fopen('php://stdin','r');
if(false===$stdin)
{
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
}
$line="";
$host="";
$label="";
$labelArr=array();
while(!feof($stdin))
{
   $line=trim(fgets($stdin,4096*64));
   $seg_arr=split("[ \t]",$line);
   if(count($seg_arr)<2)
   {
    continue;
   }
   $host=$seg_arr[0];
   $label=$seg_arr[1];
   if(!isset($labelArr[$label]))
   {
    $labelArr[$label]=$host."\n";
   }
   else
   {
    $labelArr[$label]=$labelArr[$label].$host."\n";
   } 
}
fclose($stdin);

//$dict_file="temp/dict.txt";
//$dict_out=fopen($dict_file,'w');
$dict_item="";
$dict_key="";
foreach($labelArr as $dict_key => $tempword)
{
  $dict_item=$dict_key."\n".$tempword."\n";
  echo $dict_item."\n";
//  fwrite($dict_out, $dict_item);
}



?>
