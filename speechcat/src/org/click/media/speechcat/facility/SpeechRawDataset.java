package org.click.media.speechcat.facility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.click.media.rnn.DataHeader;
import org.click.media.rnn.DataSequence;
import org.click.media.rnn.SeqBuffer;

/**
 * input: 
 *   one dir: for example, rnn_examples/phoneme_transcription/data
 * 
 * provide similar interfaces with NetcdfDataset
 * @author blue
 */
public class SpeechRawDataset {
	
	 String task;
	 DataHeader header;
	 
	 String[] labels = {"h#","b","d","g","p","t","k","dx","q","jh","ch","s","sh","z","zh","f","th","v","dh","m","n","ng","em","en","eng","nx","l","r","w","y","hh","hv","el","iy","ih","eh","ey","ae","aa","aw","ay","ah","ao","oy","ow","uh","uw","ux","er","ax","ix","axr","ax-h","pau","epi","bcl","dcl","gcl","pcl","kcl","tcl"};
	 
	 List<DataSequence> sequences=new ArrayList<>();
	 SeqBuffer<Integer> inputSeqDims=new SeqBuffer<>(0);
	 SeqBuffer<Integer> targetSeqDims=new SeqBuffer<>(0);
	 
	 /**
	  * one item may include serveral files:
	  * WAV,PHN,TXT,WRD....
	  * @author blue
	  */
	 public class SpeechItem{
		 
		public File wavfile; 
		 
		public File phnfile; 
		 
		public File txtfile; 
		
		public File wrdfile; 
		
		public SpeechItem(File wavfile,File phnfile,File txtfile,File wrdfile){
			this.wavfile=wavfile;
			this.phnfile=phnfile;
			this.txtfile=txtfile;
			this.wrdfile=wrdfile;
		}
		
		
		public String toString(){
			
			String str="";
			
			str+=("wavfile:"+wavfile.getName()+"\t");
			str+=("phnfile:"+phnfile.getName()+"\t");
			str+=("txtfile:"+txtfile.getName()+"\t");
			str+=("wrdfile:"+wrdfile.getName()+"\t");
			
			return str.trim();
		}
		
	 }
	 
	 /**
	  * init dataFraction=1.0
	  * but here the fname is not a file but a directory
	  * @param fname
	  * @param t
	  * @param dataFraction
	  */
	 public SpeechRawDataset(String dirname,String t,double dataFraction){
		 
		 this.task=t;
		 
		 SpeechItem[] speechItems=constructSpeechItemsFromDir(dirname); 
		 
		 this.header=new DataHeader(speechItems,t,dataFraction,"rawwavdir");
		 
	     	 
		 	 
	 }
	 
	 public SpeechItem[] constructSpeechItemsFromDir(String dirname){
		 
		 try{
			 
			 File dir=new File(dirname);
			 File[] subfiles=dir.listFiles();
			 
			 String nosufix="",shortName="",suffix="";
			 
			 Map<String,Map<String,File>> groupfiles=new HashMap<>();
			 Map<String,File> groupItem=null;
			 
			 for(int i=0;i<subfiles.length;i++){
				 
				 shortName=subfiles[i].getName();
				 nosufix=(shortName.substring(0, shortName.lastIndexOf('.'))).trim();
				 
				if(!(groupfiles.containsKey(nosufix))){
					groupfiles.put(nosufix, new HashMap<>());
				}
				 
				suffix=(shortName.substring(shortName.lastIndexOf('.')+1,shortName.length())).trim();
				
				groupItem=groupfiles.get(nosufix);
				
				if(!(groupItem.containsKey(suffix))){
					groupItem.put(suffix, subfiles[i]);
				}
				
				//System.err.println("i="+i+"\tnosufix:"+nosufix+" suffix:"+suffix);
			 }
			 
			 SpeechItem[] speechItems=new SpeechItem[groupfiles.size()];
			 
			 SpeechItem tempItem=null;
			 
			 int index=0;
			 for(Map.Entry<String, Map<String,File>> entry:groupfiles.entrySet()){
				 
				 groupItem=entry.getValue();
				 File wavfile=groupItem.get("WAV");
				 File phnfile=groupItem.get("PHN");
				 File txtfile=groupItem.get("TXT");
				 File wrdfile=groupItem.get("WRD");
				
				 //System.err.println("key:"+entry.getKey()+" wavfile:"+wavfile+" phnfile:"+phnfile+" txtfile:"+txtfile+" wrdfile:"+wrdfile);
				 tempItem=new SpeechItem(wavfile,phnfile,txtfile,wrdfile);
				 
				 speechItems[index++]=tempItem;
				 
			 }
			 
			 
			 for(int i=0;i<speechItems.length;i++){
				 System.err.println("speechItems["+i+"]="+speechItems[i]);
			 }
			 
			 
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 
		 return null;
	 }
	
	
	 public DataSequence get_sequence(int i){
		 return sequences.get(i);
	 }
	
	 
	 public static void main(String[] args){
		 
		 String dirname="rnn_exmpales/phoneme_transcription/data";
		 String task="transcription";
		 double dataFraction=1;
		 
		 SpeechRawDataset srd=new SpeechRawDataset(dirname,task,dataFraction);
		 
		 
	 }
	
	
	

}
