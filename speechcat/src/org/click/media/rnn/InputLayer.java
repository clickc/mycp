package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class InputLayer extends Layer{

	static Logger LOG =  Logger.getLogger(InputLayer.class.getName()); 
	
	public InputLayer(String name,int numSeqDims,int size,Vector<String> inputLabels){
		super(name, numSeqDims, 0, size,null);
	}
	
	public void copy_inputs(SeqBufferDouble inputs){
		//LOG.info("copy_inputs before outputActivations.shape:"+this.outputActivations.shape+" outputActivations.seq_shape:"+this.outputActivations.seq_shape());
		this.outputActivations=inputs;
		//LOG.info("copy_inputs after outputActivations.shape:"+this.outputActivations.shape+" outputActivations.seq_shape:"+this.outputActivations.seq_shape());
		this.outputErrors.reshape(this.outputActivations, 0.0);
	}
	
	
}
