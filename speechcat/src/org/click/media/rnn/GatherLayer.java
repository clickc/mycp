package org.click.media.rnn;

import java.util.Vector;

public class GatherLayer extends Layer{

	Vector<Layer> sources;
	
	public GatherLayer(String name,Vector<Layer> srcs,int srcsize){
		super(name,srcs.firstElement().num_seq_dims(),0,srcsize,srcs.firstElement());
		
		
	}
	

	
}
