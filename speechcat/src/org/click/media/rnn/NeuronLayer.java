package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.click.media.rnn.activationFunctions.Func;

public class NeuronLayer<F extends Func> extends FlatLayer{
  
	static Logger LOG =  Logger.getLogger(NeuronLayer.class.getName()); 
	
	private RangeView inputErrorsView=new RangeView(null,null);
	private RangeView outputActivationsView=new RangeView(null,null);
	private RangeView outputErrorsView=new RangeView(null,null);
	
	F f;

    private IndexRange inputRange=new IndexRange(0,0);
	private IndexRange outputRange=new IndexRange(0,0);
    private IndexRange tempInputErrorsIR=new IndexRange(0,0);
    private IndexRange tempOutputActivationsIR=new IndexRange(0,0);
    private IndexRange tempOutputErrorsIR=new IndexRange(0,0);
    
	public NeuronLayer(String name, int numSeqDims, int inputSize, int outputSize, Layer src,F f) {
		super(name, numSeqDims, inputSize, outputSize, src);
		this.f=f;
		// TODO Auto-generated constructor stub
		
	}

	public NeuronLayer(String name,int  numDims,int size,F f){
		super(name,numDims,size,null);
		this.f=f;
		init();
		
	}
	
	public NeuronLayer(String name,Vector<Integer> directions,int size,F f){
		super(name,directions,size,null);
		this.f=f;
		init();
	}
	
	public void init(){
		
	}
	
	public void feed_forward(Vector<Integer> coords){
		//LOG.info("lq NeuronLayer inputActivations.shape:"+this.inputActivations.shape+" outputActivations.shape:"+this.outputActivations.shape);
	    //IndexRange inputRange=this.inputActivations.getView(coords);
		//ir//IndexRange inputRange=new IndexRange(0,0);
		this.inputActivations.getView(coords,inputRange);
	    
	    //IndexRange outputRange=this.outputActivations.getView(coords);
		//ir//IndexRange outputRange=new IndexRange(0,0);
		this.outputActivations.getView(coords,outputRange);
		
		Matrix.transform(this.inputActivations, inputRange.start, inputRange.end, this.outputActivations, outputRange.start, f);

		//Matrix.transform(this.inputActivations, inputRange.start, inputRange.end, this.outputActivations, outputRange.start, f);
	}
	

	public void feed_back(Vector<Integer> coords){
		
		
		//RangeView inputErrorsView=new RangeView(this.inputErrors.getView(coords),this.inputErrors);
		//inputErrorsView.setMem(this.inputErrors.getView(coords), this.inputErrors);
		//ir//IndexRange tempInputErrorsIR=new IndexRange(0,0);
		this.inputErrors.getView(coords,tempInputErrorsIR);
		inputErrorsView.setMem(tempInputErrorsIR, this.inputErrors);

		//LOG.info("lq NeuronLayer feed_back inputErrorsView before name:"+this.name+" coords:"+coords+" inputErrorsView.size:"+inputErrorsView.size()+" inputErrorsView:"+inputErrorsView.getRangeData());
		
		//RangeView outputActivationsView=new RangeView(this.outputActivations.getView(coords),this.outputActivations);
		//outputActivationsView.setMem(this.outputActivations.getView(coords), this.outputActivations);
		//ir//IndexRange tempOutputActivationsIR=new IndexRange(0,0);
		this.outputActivations.getView(coords,tempOutputActivationsIR);
		outputActivationsView.setMem(tempOutputActivationsIR, this.outputActivations);

		//LOG.info("lq NeuronLayer feed_back outputActivationsView name:"+this.name+" coords:"+coords+" outputActivationsView.size:"+outputActivationsView.size()+" outputActivationsView:"+outputActivationsView.getRangeData());
		
		//RangeView outputErrorsView=new RangeView(this.outputErrors.getView(coords),this.outputErrors);
		//outputErrorsView.setMem(this.outputErrors.getView(coords), this.outputErrors);
		//ir//IndexRange tempOutputErrorsIR=new IndexRange(0,0);
		this.outputErrors.getView(coords,tempOutputErrorsIR);
		outputErrorsView.setMem(tempOutputErrorsIR, this.outputErrors);
		
		//LOG.info("lq NeuronLayer feed_back outputErrorsView name:"+this.name+" coords:"+coords+" outputErrorsView.size:"+outputErrorsView.size()+" outputErrorsView:"+outputErrorsView.getRangeData());
		
		for(int t=0;t<inputErrorsView.size();t++){
			//inputErrorsView.setOffset(t, 0.5);
			inputErrorsView.setOffset(t, f.deriv(outputActivationsView.getOffset(t))*outputErrorsView.getOffset(t));
		}
		//LOG.info("lq NeuronLayer feed_back inputErrorsView after name:"+this.name+" coords:"+coords+" inputErrorsView.size:"+inputErrorsView.size()+" inputErrorsView:"+inputErrorsView.getRangeData());

	
	}
	
	
}
