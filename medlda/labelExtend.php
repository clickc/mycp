<?php

$stdin = fopen('php://stdin','r');
if(false===$stdin)
{
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
}
$line="";
$oldla_file="";
$newla_file="";
while(!feof($stdin))
{
   $line=trim(fgets($stdin,4096*64));
   if(strlen($line)>0)
   { 
     $seg_arr=split("[ \t]",$line);
    for($i=0;$i<count($seg_arr);$i++)
    {
//     printf("seg_arr[%d]=%s \n",$i,$seg_arr[$i]);
    }
    if(count($seg_arr)!=2)
    {
     echo "输入词典路径和host文件路径,中间用空格分开\n";
    }
    else
    {
     $oldla_file=$seg_arr[0];
     $newla_file=$seg_arr[1];
    }
   }
} 

//echo "dict_file:".$dict_file."\n";
//echo "nout_file:".$nout_file."\n";

$label_hash=array();

$old_in=fopen($oldla_file,'r');
$newla_in=fopen($newla_file,'r');
$ran_num=rand(0,9);
$label="";
$host="";
while(!feof($old_in))
{
  $line=trim(fgets($old_in,4096*16));
  $seg_arr=split("[ \t]",$line);
  if(count($seg_arr)<2)
  {
   continue;
  }
  $label=$seg_arr[0];
  $host=$seg_arr[1];
  if(!isset($label_hash[$host]))
  {
   $label_hash[$host]=$label;
  }
}
fclose($old_in);

$old_out=fopen($oldla_file,'a');
$new_item="";
while(!feof($newla_in))
{
  $line=trim(fgets($newla_in,4096*16));
  $seg_arr=split("[ \t]",$line);
  if(count($seg_arr)<2)
  {
   continue;
  }
  $label=$seg_arr[0];
  $host=$seg_arr[1];
  if($label=="股票证券")
  {
   $label="财经";
  }
  if($label=="减肥丰胸")
  {
   $label="女性时尚"; 
  }
  if($label=="军事历史")
  {
   $label="新闻资讯";
  }
  if($label=="工作")
  {
   $label="招聘";
  }
  $new_item=$label." ".$host."\n";
  if(!isset($label_hash[$host]))
  {
   fwrite($old_out,$new_item);
   $label_hash[$host]=$label;
  }
}

fclose($old_out);
fclose($newla_in); 
?>
