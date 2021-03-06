package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class SoftmaxLayer extends FlatLayer{

	static Logger LOG =  Logger.getLogger(SoftmaxLayer.class.getName()); 
	
	Vector<String> targetLabels;
	
	//SeqBuffer<Log<real_t> > logActivations;
	//we add log constraint implied
	SeqBufferDouble logActivations;
	
	//we add log constraint implied
	SeqBufferDouble unnormedlogActivations;
	
	SeqBufferDouble unnormedActivations;
	
	private RangeView unnormedLogActs=new RangeView(null,null);
	private RangeView inputActivationsView=new RangeView(null,null);
	private RangeView unnormedActs=new RangeView(null,null);
	private RangeView outputActivationsView=new RangeView(null,null);
	private RangeView logActivationsView=new RangeView(null,null);
	
	private RangeView outActs=new RangeView(null,null);
	private RangeView outErrs=new RangeView(null,null);
	private RangeView inputErrorsView=new RangeView(null,null);
	private RangeView seqView=new RangeView(null,null);
	
	private IndexRange tempUnnormedLogActsIR=new IndexRange(0,0);
	private IndexRange tempInputActivationsIR=new IndexRange(0,0);
	private IndexRange tempUnnormedActsIR=new IndexRange(0,0);
	private IndexRange tempOutputActivationsIR=new IndexRange(0,0);
	private IndexRange tempLogActivationsIR=new IndexRange(0,0);
	private IndexRange tempOutActsIR=new IndexRange(0,0);
	private IndexRange tempOutErrsIR=new IndexRange(0,0);
	private IndexRange tempInputErrorsIR=new IndexRange(0,0);
	private IndexRange tempSeqIR=new IndexRange(0,0);
	
	
	public SoftmaxLayer(String name,int numSeqDims,Vector<String> labs){
		//why inputSize==outputSize
		super(name,numSeqDims,labs.size(),null);
		targetLabels=labs;
		logActivations=new SeqBufferDouble(this.output_size());
		unnormedlogActivations=new SeqBufferDouble(this.output_size());
		unnormedActivations=new SeqBufferDouble(this.output_size());
	}
	
	public void start_sequence(){
		super.start_sequence();
		this.logActivations.reshape(this.inputActivations);
		this.unnormedlogActivations.reshape(this.logActivations);
		this.unnormedActivations.reshape(logActivations);
		
	}
	
	public void feed_forward(Vector<Integer> coords){
		
		//RangeView unnormedLogActs=new RangeView(this.unnormedlogActivations.getView(coords),this.unnormedlogActivations);		
		//unnormedLogActs.setMem(this.unnormedlogActivations.getView(coords), this.unnormedlogActivations);
		//ir//IndexRange tempUnnormedLogActsIR=new IndexRange(0,0); 
		this.unnormedlogActivations.getView(coords,tempUnnormedLogActsIR);
		unnormedLogActs.setMem(tempUnnormedLogActsIR, this.unnormedlogActivations);

		//RangeView inputActivationsView=new RangeView(this.inputActivations.getView(coords),this.inputActivations);
		//inputActivationsView.setMem(this.inputActivations.getView(coords),this.inputActivations);
		//ir//IndexRange tempInputActivationsIR=new IndexRange(0,0);
		this.inputActivations.getView(coords,tempInputActivationsIR);
		inputActivationsView.setMem(tempInputActivationsIR,this.inputActivations);

		
		double offset=minMaxMean(this.inputActivations,coords);
		for(int i=0;i<unnormedLogActs.size();i++){
			unnormedLogActs.setOffset(i, inputActivationsView.getOffset(i)-offset);
		}
		
		//RangeView unnormedActs=new RangeView(this.unnormedActivations.getView(coords),this.unnormedActivations);
		//unnormedActs.setMem(this.unnormedActivations.getView(coords), this.unnormedActivations);
		//ir//IndexRange tempUnnormedActsIR=new IndexRange(0,0);
		this.unnormedActivations.getView(coords,tempUnnormedActsIR);
		unnormedActs.setMem(tempUnnormedActsIR, this.unnormedActivations);
		
		for(int i=0;i<unnormedActs.size();i++){
			unnormedActs.setOffset(i, Math.exp(unnormedLogActs.getOffset(i)));
		}
		
		double Z=0;
		for(int i=0;i<unnormedActs.size();i++){
			Z+=unnormedActs.getOffset(i);
		}
		
		//RangeView outputActivationsView=new RangeView(this.outputActivations.getView(coords),this.outputActivations);
		//outputActivationsView.setMem(this.outputActivations.getView(coords), this.outputActivations);
		//ir//IndexRange tempOutputActivationsIR=new IndexRange(0,0);
		this.outputActivations.getView(coords,tempOutputActivationsIR);
		outputActivationsView.setMem(tempOutputActivationsIR, this.outputActivations);

		
		for(int i=0;i<outputActivationsView.size();i++){
			outputActivationsView.setOffset(i, unnormedActs.getOffset(i)/Z);
		}
		
		double LZ=Math.log(Z);
	
		//RangeView logActivationsView=new RangeView(this.logActivations.getView(coords),this.logActivations);
		//logActivationsView.setMem(this.logActivations.getView(coords), this.logActivations);
		//ir//IndexRange tempLogActivationsIR=new IndexRange(0,0);
		this.logActivations.getView(coords,tempLogActivationsIR);
		logActivationsView.setMem(tempLogActivationsIR, this.logActivations);

		
		for(int i=0;i<logActivationsView.size();i++){
			logActivationsView.setOffset(i, unnormedLogActs.getOffset(i)-LZ);
		}
		
		//LOG.info("lq SoftmaxLayer outputActivations coords:"+coords+" outputActivations:"+outputActivationsView.getRangeData()+" LZ:"+LZ+" Z:"+Z);
		//LOG.info("lq SoftmaxLayer logActivations coords:"+coords+" logActivations:"+logActivationsView.getRangeData());

	}
	
	public void feed_back(Vector<Integer> coords){
		
		//RangeView outActs=new RangeView(this.outputActivations.getView(coords),this.outputActivations);
		//outActs.setMem(this.outputActivations.getView(coords), this.outputActivations);
		//ir//IndexRange tempOutActsIR=new IndexRange(0,0);
		this.outputActivations.getView(coords,tempOutActsIR);
		outActs.setMem(tempOutActsIR, this.outputActivations);
		
		//LOG.info("lq SoftmaxLayer feed_back outActs coords:"+coords+" outActs.size:"+outActs.size()+" outActs:"+outActs.getRangeData());
		//RangeView outErrs=new RangeView(this.outputErrors.getView(coords),this.outputErrors);
		//outErrs.setMem(this.outputErrors.getView(coords), this.outputErrors);
		//ir//IndexRange tempOutErrsIR=new IndexRange(0,0);
		this.outputErrors.getView(coords,tempOutErrsIR);
		outErrs.setMem(tempOutErrsIR, this.outputErrors);
		
		//LOG.info("lq SoftmaxLayer feed_back outErrs coords:"+coords+" outErrs.size:"+outErrs.size()+" outErrs:"+outErrs.getRangeData());
		double Z=0;
		for(int i=0;i<outActs.size();i++){
			Z+=(outActs.getOffset(i)*outErrs.getOffset(i));
		}
		
		//right?Accuracy loss?
		//int Z2=(int)(Math.round(Z));
		//Z=Z2;
		//LOG.info("lq SoftmaxLayer feed_back Z coords:"+coords+" Z:"+Z);
		
		//RangeView inputErrorsView=new RangeView(this.inputErrors.getView(coords),this.inputErrors);
		//inputErrorsView.setMem(this.inputErrors.getView(coords), this.inputErrors);
		//ir//IndexRange tempInputErrorsIR=new IndexRange(0,0);
		this.inputErrors.getView(coords,tempInputErrorsIR);
		inputErrorsView.setMem(tempInputErrorsIR, this.inputErrors);
		
		for(int t=0;t<inputErrorsView.size();t++){
			inputErrorsView.setOffset(t, outActs.getOffset(t)*(outErrs.getOffset(t)-Z));
		}
		
		//bug20//LOG.info("lq SoftmaxLayer feed_back coords:"+coords+" outActStr:"+outActs.indexRange+" outActs:"+outActs.getRangeData()+" outErrStr:"+outErrs.indexRange+" outErrs:"+outErrs.getRangeData()+" inputErrorsStr:"+inputErrorsView.indexRange+" inputErrors:"+inputErrorsView.getRangeData());
		
	}
	
	
	public double minMaxMean(SeqBufferDouble seq,Vector<Integer> coords){
		
		double mean=0.0,max=Double.MIN_VALUE,min=Double.MAX_VALUE,temp=0.0;
		
		//RangeView seqView=new RangeView(seq.getView(coords),seq);		
		//seqView.setMem(seq.getView(coords), seq);
		//ir//IndexRange tempSeqIR=new IndexRange(0,0);
		seq.getView(coords,tempSeqIR);
		seqView.setMem(tempSeqIR, seq);
		
        for(int i=0;i<seqView.size();i++){
        	temp=seqView.getOffset(i);
        	
        	if(temp>max){
        		max=temp;
        	}else if(temp<min){
        		min=temp;
        	}
        }
        
        mean=(max+min)/2;
		
        return mean;
	}
	
	
	
	
}
