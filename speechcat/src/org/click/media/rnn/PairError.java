package org.click.media.rnn;

public class PairError {

	public int key;
	
	public DatasetErrors dataErrors;
	
	public PairError(int key,DatasetErrors dataErrors){
		this.key=key;
		this.dataErrors=dataErrors;
	}
	
	
}
