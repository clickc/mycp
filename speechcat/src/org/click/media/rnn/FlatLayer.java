package org.click.media.rnn;

import java.util.Vector;

public class FlatLayer extends Layer{

	public FlatLayer(String name, int numSeqDims, int inputSize, int outputSize, Layer src) {
		super(name, numSeqDims, inputSize, outputSize, src);
		// TODO Auto-generated constructor stub
	}
	
	public FlatLayer(String name,int numSeqDims,int size,Layer src){
		super(name, numSeqDims, size, size, src);
	}
	
	public FlatLayer(String name,Vector<Integer> dirs,int size,Layer src){
		super(name, dirs, size, size, src);
	}
	

}
