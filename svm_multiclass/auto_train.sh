#!/bin/bash

for((i=0;i<1;i++))
do
./svm_multiclass_learn -c 5000 trvec.txt product_model 
./svm_multiclass_classify tevec.txt product_model example4/predictions >> log.txt
#rm example4/ntrainSample.txt example4/ntestSample.txt
#echo example4/nntrainSample.txt example4/ntrainSample.txt example4/ntestSample.txt |php randomGen.php

#echo example4/nntrainSample.txt example4/ntrainSample.txt example4/ntestSample.txt example4/hosts.txt example4/tr_host.txt example4/te_host.txt |php randomGen.php
./svm_multiclass_learn -c 5000 example4/train_format.txt example4/model
./svm_multiclass_classify example4/test_format.txt example4/model example4/predictions >> log.txt
#echo example4/ntestSample.txt example4/te_host.txt example4/tag_index.txt example4/predictions example4/nprediction.txt example4/wsf.txt |php predInterface.php  >> log.txt

done

