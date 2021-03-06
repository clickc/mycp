package org.click.media.rnn;

import java.util.Vector;

/**
 * to do
 * @author lq
 */
public class BiasLayer extends Layer{

	IndexRange acts=new IndexRange(0,0);
	IndexRange errors=new IndexRange(0,0);
	
	public BiasLayer(){
		
		super("bias",0,0,1,null);
		//this.acts=this.outputActivations.getView(0);
		this.outputActivations.getView(0,this.acts);
		
		//this.errors=this.outputErrors.getView(0);
		this.outputErrors.getView(0,this.errors);
		
		this.outputActivations.data.set(0, 1.0);
	}
	
	public BiasLayer(String name, int numSeqDims, int inputSize, int outputSize, Layer src) {
		super(name, numSeqDims, inputSize, outputSize, src);
		// TODO Auto-generated constructor stub
	}
	
	/*
	@Override
	public IndexRange out_acts(Vector<Integer> coords){
		return acts;
	}

	@Override
	public IndexRange out_errs(Vector<Integer> coords){
		return errors;
	}
	*/
	
	@Override
	public void out_acts(Vector<Integer> coords,IndexRange result){
		//return acts;
		result.setRange(acts.getStart(), acts.getEnd());
	}

	@Override
	public void out_errs(Vector<Integer> coords,IndexRange result){
		//return errors;
		result.setRange(errors.getStart(), errors.getEnd());
	}
	
	public static void main(String[] args){
		BiasLayer bias=new BiasLayer();
		System.err.println("shape:"+bias.outputErrors.shape);
		//System.err.println(bias.outputErrors.getView(0));
	}
}
