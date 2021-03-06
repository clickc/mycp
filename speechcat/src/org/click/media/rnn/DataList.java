package org.click.media.rnn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

public class DataList {

	public Vector<String> filenames;
	public Vector<DataHeader> headers=new Vector<>();
	Map<String,Double> inputLabelHits=new HashMap<>();
	Map<String,Double> targetLabelCounts=new HashMap<>();
	String task;
	int numSequences;
	int numTimesteps;
	int totalTargetStringLength;
	
	NetcdfDataset dataset;
	int datasetIndex;
	DataSequence seq;
	int seqIndex;
	double dataFraction;
	boolean shuffled;
	
	boolean loadedSeq=false;
	
	static Logger LOG =  Logger.getLogger(DataList.class.getName()); 
	
	public DataList(Vector<String> filenames,String t,boolean shuffle,double loadFraction){
		
		this.filenames=filenames;
		this.task=t;
		numSequences=0;
		numTimesteps=0;
		totalTargetStringLength=0;
		
		//dataset=new NetcdfDataset();
		dataset=null;
		datasetIndex=-1;
		
		seq=new DataSequence(0,0);
		seqIndex=-1;
		dataFraction=loadFraction;
		shuffled=shuffle;
		
		String fn="";
		
		for(int i=0;i<filenames.size();i++){
		    fn=filenames.get(i);
		    headers.add(new DataHeader(fn,task,loadFraction,"ncfile"));
		    DataHeader curr=headers.lastElement();
		    numSequences+=curr.numSequences;
		    numTimesteps+=curr.numTimesteps;
		    totalTargetStringLength+=curr.totalTargetStringLength;
		    
		    for(Map.Entry<String, Integer> entry:curr.targetLabelCounts.entrySet()){
		    	if(!(targetLabelCounts.containsKey(entry.getKey()))){
		    		targetLabelCounts.put(entry.getKey(), (double)entry.getValue());
		    	}
		    	else{
		    		targetLabelCounts.put(entry.getKey(), targetLabelCounts.get(entry.getKey())+entry.getValue());
		    	}
		    }
		    
		    for(Map.Entry<String, Integer> entry:curr.inputLabelCounts.entrySet()){
		    	if(!(inputLabelHits.containsKey(entry.getKey()))){
		    		inputLabelHits.put(entry.getKey(), (double)entry.getValue());
		    	}
		    	else{
		    		inputLabelHits.put(entry.getKey(),inputLabelHits.get(entry.getKey())+entry.getValue());
		    	}
		    }
		    
		    
		}
		
		next_dataset();
	}
	
	public void clear_seq(){
		seqIndex = -1;
		seq = new DataSequence(0,0);
	}
	
	public void delete_dataset(){
		dataset = new NetcdfDataset();
		clear_seq();
	}
	
	/*
	public boolean next_dataset(){
		
		if(dataset!=null&&filenames.size()>1){
			delete_dataset();
		}
		
		if(datasetIndex>=(int)(filenames.size()-1))
		{
			datasetIndex = -1;
			return true;
		}
		
		++datasetIndex;
		
		if(dataset==null){
			dataset = new NetcdfDataset(filenames.get(datasetIndex), task, dataFraction);
		}
		
		if(dataset.size()==0){
			return next_dataset();
		}
		
		if(shuffled){
			dataset.shuffle_sequences();
		}
		
		return false;
	}
	
	public DataSequence next_sequence(){
		
		boolean finished=false;
		
		if(datasetIndex<0||seqIndex>=(dataset.sequences.size()-1)){
			finished = next_dataset();
			LOG.info("datasetIndex:"+datasetIndex+" seqIndex:"+seqIndex+" dataset.sequences.size:"+dataset.sequences.size()+" finished:"+finished);
		}
		
		if (finished)
		{
			clear_seq();
		}
		else
		{
			++seqIndex;
			seq = dataset.sequences.get(seqIndex);
		}
		return seq;
		
	}
	*/
	
	
public boolean next_dataset(){
		
		
	    System.err.println("datasetIndex:"+datasetIndex+" filenames.size:"+filenames.size()+" filenames:"+filenames);
		if(datasetIndex>=(int)(filenames.size()-1))
		{
			datasetIndex = -1;
			return true;
		}
		
		++datasetIndex;
		
		dataset = new NetcdfDataset(filenames.get(datasetIndex), task, dataFraction);
			
		if(dataset.size()==0){
			return next_dataset();
		}
		
		if(shuffled){
			dataset.shuffle_sequences();
		}
		
		return false;
	}
	

	public DataSequence next_sequence(){
		
		++seqIndex;
		if(seqIndex>=(dataset.sequences.size())){
		   return null;	
		}
		
		seq = dataset.sequences.get(seqIndex);
	
		return seq;
		
	}
	
	public DataSequence next_sequence_bak(){
		
		boolean finished=false;
		
		if(datasetIndex<0||seqIndex>=(dataset.sequences.size()-1)){
			finished = next_dataset();
			LOG.info("datasetIndex:"+datasetIndex+" seqIndex:"+seqIndex+" dataset.sequences.size:"+dataset.sequences.size()+" finished:"+finished);
		}
		
		if (finished)
		{
			return null;
			//clear_seq();
		}
		else
		{
			++seqIndex;
			seq = dataset.sequences.get(seqIndex);
		}
		return seq;
		
	}
	
	public DataSequence start(){
		
		datasetIndex = -1;
		clear_seq();
		if (shuffled)
		{
			///shuffle(filenames);
		}
		
		return next_sequence();
	}
	
	public int size(){
		return filenames.size();
	}
	
}
