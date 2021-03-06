package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class BlockLayer extends Layer{

	Vector<Integer> blockShape;
	Vector<Integer> blockOffset;
	Vector<Integer> inCoords;
	int sourceSize;
	
	static Logger LOG =  Logger.getLogger(BlockLayer.class.getName()); 
	
	CoordIterator blockIterator;
	Vector<Integer> outSeqShape;
	
	private RangeView outIt=new RangeView(null,null);
	private RangeView inErr=new RangeView(null,null);
	
	private IndexRange tempoutItIR=new IndexRange(0,0);
	private IndexRange tempInActsIR=new IndexRange(0,0);
	private IndexRange tempOutItIR=new IndexRange(0,0);
	private IndexRange tempInErrIR=new IndexRange(0,0);
	
	/**
	 * auto add method
	 * @param name
	 * @param numSeqDims
	 * @param inputSize
	 * @param outputSize
	 * @param src
	 */
	public BlockLayer(String name, int numSeqDims, int inputSize, int outputSize, Layer src) {
		super(name, numSeqDims, inputSize, outputSize, src);		
	}
	
	
	public BlockLayer(Layer src, Vector<Integer> blockShape){
		super(src.name+"_block",src.num_seq_dims(),0,RangeUtils.product(blockShape)*src.output_size(),src);
		this.blockShape=blockShape;
		this.blockOffset=Helpers.createVector(this.num_seq_dims(), 0);
		this.inCoords=Helpers.createVector(this.num_seq_dims(), 0);
		this.sourceSize=src.outputActivations.depth;
		
		Vector<Integer> dirs=new Vector<>();
		this.blockIterator=new CoordIterator(blockShape,dirs,false);
		this.outSeqShape=Helpers.createVector(this.num_seq_dims(), 0);
		WeightContainer.link_layers(this.source.name, this.name, this.source.name+"_to_"+this.name, 0, 0);
			
	}
	
	public void start_sequence(){
		
		//LOG.info("lq BlockLayer start_sequence: this.source:"+this.source.name+" output_seq_shape"+this.source.output_seq_shape()+" blockShape:"+blockShape);
		for(int i=0;i<outSeqShape.size();++i){
			outSeqShape.set(i, (int)Math.ceil((double)(this.source.output_seq_shape().get(i))/(double)(blockShape.get(i))));
		}
		
		outputActivations.reshape(outSeqShape);
		outputErrors.reshape(outputActivations,0.0);
		
	}
	
	public void feed_forward(Vector<Integer> outCoords){
		
		//IndexRange outIt=this.outputActivations.getView(outCoords);
		
		//ir//IndexRange tempoutItIR=new IndexRange(0,0);
		this.outputActivations.getView(outCoords,tempoutItIR);
		
		Helpers.range_multiply(blockOffset, outCoords, blockShape);
		//LOG.info("lq outCoords:"+outCoords+" outIt.end-outIt.begin:"+(outIt.end-outIt.start)+" outputActivations.shape:"+this.outputActivations.shape+" output_seq_shape:"+this.output_seq_shape()+" blockShape:"+blockShape+" blockOffset:"+blockOffset+" blockIterator.size:"+blockIterator.size()+" sourceSize:"+sourceSize);
		
		blockIterator.begin();
		
		//IndexRange inActs=null;
		//ir//IndexRange tempInActsIR=new IndexRange(0,0);
		
		int innerIndex=tempoutItIR.start;
		
		//String inActstr="";
		//int index=0;
		for(int i=0;i<blockIterator.size();i++){
			//index++;
			Helpers.range_plus(inCoords, blockIterator.pt,blockOffset );
			//inActs=this.source.outputActivations.at(inCoords);
			this.source.outputActivations.at(inCoords,tempInActsIR);
			//LOG.info("lq inCoords:"+inCoords+" inActs.end-inActs.begin:"+(inActs.end-inActs.start)+" blockIterator:"+blockIterator.pt+" blockOffset:"+blockOffset);
			//inActstr+=(this.source.outputActivations.fetchData(inActs)+" ");
			//inActstr+=(inActs+" ");
			if(tempInActsIR.end>tempInActsIR.start&&tempInActsIR.end<=this.source.outputActivations.data.size()){
				this.outputActivations.fill(innerIndex,innerIndex+sourceSize, this.source.outputActivations.fetchData(tempInActsIR));
			}else{
				this.outputActivations.fill(innerIndex,innerIndex+sourceSize,0.0);
			}
			
			innerIndex+=sourceSize;
			blockIterator.increment();		
		}
		
		//LOG.info("lq BlockLayer outCoords:"+outCoords+" outAct"+this.outputActivations.fetchData(outIt)+" index:"+index);	
	}
	
	public void feed_back(Vector<Integer> outCoords){
		
       // RangeView outIt=new RangeView(this.outputErrors.getView(outCoords),this.outputErrors);
        
		//outIt.setMem(this.outputErrors.getView(outCoords), this.outputErrors);
        //ir//IndexRange tempOutItIR=new IndexRange(0,0);
        this.outputErrors.getView(outCoords,tempOutItIR);
        outIt.setMem(tempOutItIR, this.outputErrors);
        
   		int outItIndex=0;
		
		Helpers.range_multiply(blockOffset, outCoords, blockShape);
		
		blockIterator.begin();
		
		double oval=-1;
		for(int i=0;i<blockIterator.size();i++){
			
			Helpers.range_plus(inCoords, blockIterator.pt, blockOffset);
			//RangeView inErr=new RangeView(this.source.outputErrors.at(inCoords),this.source.outputErrors);
			
			//inErr.setMem(this.source.outputErrors.at(inCoords), this.source.outputErrors);
			//ir//IndexRange tempInErrIR=new IndexRange(0,0);
			this.source.outputErrors.at(inCoords,tempInErrIR);
			inErr.setMem(tempInErrIR, this.source.outputErrors);
			
			if(inErr.begin()){
				
				for(int j=0;j<sourceSize;j++){
					oval=inErr.getOffset(j);
					inErr.setOffset(j, oval+outIt.getOffset(outItIndex+j));
					
				}
				
			}
			
			outItIndex+=sourceSize;
			blockIterator.increment();	
		}
		
		
		
	}

}
