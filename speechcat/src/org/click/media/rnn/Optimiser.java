package org.click.media.rnn;

public class Optimiser {

	public double[] wts;
	public double[] derivs;
	
	public Optimiser(double[] wts,double[] derivs){
		this.wts=wts;
		this.derivs=derivs;
	}
	
	public void update_weights(){
		
	}
	
}
