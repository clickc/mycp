package org.click.media.rnn;

import java.util.Vector;

public class DataSequence {

	//data
	//here should double 
	//<real_t,double>
	//<size_t,int>
	SeqBufferDouble inputs;
	SeqBuffer<Integer> inputClasses;
	SeqBufferDouble targetPatterns;
	SeqBuffer<Integer> targetClasses;
	SeqBufferDouble importance;
	Vector<Integer> targetLabelSeq;
	//Vector<String> targetWordSeq;
	String tag;
	
	public DataSequence(DataSequence ds){
		this.inputs=ds.inputs;
		this.inputClasses=ds.inputClasses;
		this.targetPatterns=ds.targetPatterns;
		this.targetClasses=ds.targetClasses;
		this.importance=ds.importance;
		this.targetLabelSeq=ds.targetLabelSeq;
		this.tag=ds.tag;
	}
	
	public DataSequence(int inputDepth,int targetPattDepth){
		this.inputs=new SeqBufferDouble(inputDepth);
		this.inputClasses=new SeqBuffer<Integer>(0);
		this.targetPatterns=new SeqBufferDouble(targetPattDepth);
		this.targetClasses=new SeqBuffer<Integer>(0);
		this.importance=new SeqBufferDouble(0);
        this.targetLabelSeq=new Vector<>();
        //this.targetWordSeq=new Vector<>();
	}
	
	public int num_timessteps(){
		return inputs.seq_size();
	}
	
}
