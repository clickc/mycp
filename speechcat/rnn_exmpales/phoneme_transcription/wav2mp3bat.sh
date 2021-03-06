#!/bin/bash  

cd ntrain
wavFiles=`ls *wav`

cd ..

for wavFile in $wavFiles
do
  
 # wavName=${wavFile/ntrain/}
  wavName=${wavFile%.wav}
  INPUTWAV="ntrain/"$wavName".wav"
  OUTPUTMP3="mp3train/"$wavName".mp3" 
  OUTPUTWAV="ctrain/"$wavName".wav"

  echo "INPUTWAV:"$INPUTWAV" OUTPUTMP3:"$OUTPUTMP3" OUTPUTWAV:"$OUTPUTWAV
  ffmpeg -i $INPUTWAV -codec:a libmp3lame -qscale:a 2 $OUTPUTMP3
  ffmpeg -i $OUTPUTMP3 $OUTPUTWAV
  

done


