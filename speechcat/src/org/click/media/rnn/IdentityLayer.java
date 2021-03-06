package org.click.media.rnn;

import java.util.Vector;

public class IdentityLayer extends FlatLayer{
	
	public IdentityLayer(String name,int numSeqDims,int size){
		super(name,numSeqDims, size,null);
	}
	
	public IdentityLayer(String name,Vector<Integer> directions,int size){
		super(name,directions,size,null);
	}
	

}
