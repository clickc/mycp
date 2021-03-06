package org.click.media.rnn;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class NetcdfDataset {

	static Logger LOG =  Logger.getLogger(NetcdfDataset.class.getName()); 
	
	//data
	 NetcdfFile nc;
	 String fileName;
	 String task;
	 DataHeader header;
	 
	 Vector<DataSequence> sequences=new Vector<>();
	 SeqBuffer<Integer> inputSeqDims=new SeqBuffer<>(0);
	 SeqBuffer<Integer> targetSeqDims=new SeqBuffer<>(0);
	 
	 public NetcdfDataset(){
		 
	 }
	 
	 /**
	  * init dataFraction=1.0
	  * @param fname
	  * @param t
	  * @param dataFraction
	  */
	 public NetcdfDataset(String fname,String t,double dataFraction){
		
		 try{
		     nc=new NetcdfFile(fname);
		     
		     this.fileName=fname;
		     this.task=t;
		     System.err.println("begin to make header");
		     this.header=new DataHeader(fname,t,dataFraction,"ncfile");
		     System.err.println("after make header");
		     init();
		     int maxSeqs=nc.findDimension("numSeqs").getLength();
		     int numSeqs =  Helpers.bound((int)(dataFraction * maxSeqs), 1, maxSeqs);
		     System.err.println("begin to load sequence");
		     
		     load_sequences(0, numSeqs);
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public NetcdfDataset(String fname,String t,int seqNum){
		 
		 try{
		    nc=new NetcdfFile(fname);
		    this.fileName=fname;
		    this.task=t;
		    this.header=new DataHeader(fname,t,0,"ncfile");
		    init();
		    load_sequences(seqNum, seqNum + 1);
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public void init(){
		 
		 inputSeqDims.reshape_with_depth(Helpers.list_of(nc.findDimension("numSeqs").getLength(), 0), header.numDims);
		 
		 try{
		  Variable seqV=nc.findVariable("seqDims");
		  Array seqA=seqV.read();
		  int[][] dimscopy=( int[][])seqA.copyToNDJavaArray();
		  for(int i=0;i<dimscopy.length;i++){
			  for(int j=0;j<dimscopy[i].length;j++){
				  inputSeqDims.data.add(dimscopy[i][j]);
			  }
		  }
		  
		  targetSeqDims.reshape_with_depth(Helpers.list_of(nc.findDimension("numSeqs").getLength(), 0), header.numDims);
		  targetSeqDims = inputSeqDims;
		  
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 
	 }
	 
	 public int size(){
		 return sequences.size();
	 }
	 
	 public void shuffle_sequences(){
		 //to do
	 }
	 
	 public DataSequence get_sequence(int i){
		 return sequences.get(i);
	 }
	 
	 public int timesteps(){
		 
		 int total=0;
		 
		 for(int i=0;i<sequences.size();i++){
			 total+=sequences.get(i).inputs.seq_size();
		 }
		 
		 return total;
	 }
	 
	 public Pair seq_to_offset(int seqNum){
		 return new Pair(inputSeqDims.product(seqNum),targetSeqDims.product(seqNum));
	 }
	  
	 public Pair get_offset(int seqNum){
		 
		 Pair offset=new Pair(0,0);
		 
		 for(int i=0;i<seqNum;i++){
			 offset.plus(seq_to_offset(i));
		 }
		 
		 return offset;
	 }
	 
		public void writeToMyData(List<Double> list,int i){
			
			try{
				PrintWriter pw=new PrintWriter(new FileWriter("doc/mypic"+i+".txt"));
				
				for(int j=0;j<list.size();j++){
					pw.println(j+"\t"+list.get(j));
				}
				pw.flush();
				
				pw.close();
				
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	 /**
	  * begin to test with data flow
	  * @param first
	  * @param last
	  */
	 public	void load_sequences (int first, int last)
	 {
		
		 LOG.info("lq Netcdfdataset in load_sequences first:"+first+" last:"+last);
		 Pair offsets=get_offset(first);
		 
		 //List<String> wordTargetStrings=NetcdfUtils.load_string_array(nc, "wordTargetStrings");
		 List<String> targetStrings=NetcdfUtils.load_string_array(nc, "targetStrings");
		 List<String> seqTags=NetcdfUtils.load_string_array(nc, "seqTags");
		 
		 
		 for(int i=first;i<last;i++){
			
			 DataSequence seq=new DataSequence(header.inputSize,task.equals("regression")?header.outputSize:0); 
		
			 Vector<Integer> localInputSeqDims=inputSeqDims.getRangeVector(inputSeqDims.getView(i));
			 Vector<Integer> inputShape=Helpers.flip(localInputSeqDims);			 
			 int inputCount=RangeUtils.product(inputShape);
			 			 
			 Vector<Integer> localTargetSeqDims=targetSeqDims.getRangeVector(targetSeqDims.getView(i));
			 Vector<Integer> targetShape=Helpers.flip(localTargetSeqDims);
			 int targetCount=RangeUtils.product(targetShape);
			 
			 LOG.info("i="+i+" localInputSeqDims:"+localInputSeqDims+" inputShape:"+inputShape+" localTargetSeqDims:"+localTargetSeqDims+" targetShape:"+targetShape);
			 LOG.info("lq Netcdfdataset load_sequences before outputActivations.shape:"+seq.inputs.shape+" outputActivations.seq_shape:"+seq.inputs.seq_shape());
			 seq.inputs.load_to_seq_buffer(nc,seq.inputs, inputShape, "inputs", true, offsets.first, inputCount);
			 //writeToMyData(seq.inputs.data,i);
			 LOG.info("i="+i+" shape:"+seq.inputs.shape+" data:"+seq.inputs.data);
			 LOG.info("lq Netcdfdataset load_sequences middle outputActivations.shape:"+seq.inputs.shape+" outputActivations.seq_shape:"+seq.inputs.seq_shape());

			 System.err.println("inputs.size:"+seq.inputs.data.size());
			 LOG.info("inputs.size:"+seq.inputs.data.size());
			 
			 System.err.println("offsets:"+offsets);
			 LOG.info("offsets:"+offsets);
			 
			 if(task.equals("transcription")){
				 
				 if(nc.findVariable("wordTargetStrings")!=null){
				   // seq.targetWordSeq.add(wordTargetStrings.get(i));	
				  //  System.err.println("i="+i+"\t"+wordTargetStrings.get(i));
				 }
				 
				 seq.targetLabelSeq=header.str_to_label_seq(targetStrings.get(i));
			     System.err.println("seq.targetLabelSeq.size:"+seq.targetLabelSeq.size()+" seq.targetLabelSeq:"+seq.targetLabelSeq+" targetString:"+targetStrings.get(i));	 
			 }
			 
			 seq.tag=seqTags.get(i);
			 sequences.add(seq);
			 offsets.plus(new Pair(inputCount,targetCount));
			 ///LOG.info("lq Netcdfdataset load_sequences after outputActivations.shape:"+seq.inputs.shape+" outputActivations.seq_shape:"+seq.inputs.seq_shape());
		 }
		 
	    //to do		
	 }
	 
	 
	 
	 public static void main(String[] args) throws Exception{
		 
		 String fileName="rnn_exmpales/arabic_offline_handwriting/arabic_offline.nc";
		 String task="transcription";
		 double dataFraction=1.0;
		 
		 NetcdfDataset netcdf=new NetcdfDataset(fileName,task,dataFraction);
		 
		 NetcdfFileWriter fileWriter;
	
	
		 
		 
		 
		 
	 }
	 
	
}
