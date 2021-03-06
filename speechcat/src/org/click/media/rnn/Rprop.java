package org.click.media.rnn;

import java.util.Vector;

public class Rprop extends Optimiser{
	
	double[] deltas;
	double[] prevDerivs;
	double etaChange;
	double etaMin;
	double etaPlus;
	double minDelta;
	double maxDelta;
	double initDelta;
	double prevAvgDelta;
	boolean online;
	
	/**
	 * default on : false
	 * @param name
	 * @param weights
	 * @param derivatives
	 * @param on
	 */
	public Rprop(String name,double[] weights,double[] derivatives,boolean on){
		super(weights,derivatives);
		
		this.etaChange=0.01;
		this.etaMin=0.5;
		this.etaPlus=1.2;
		this.minDelta=1e-9;
		this.maxDelta=0.2;
		this.initDelta=0.01;
		this.prevAvgDelta=0;
		this.online=on;
		
		if (online)
		{
			//to do
			//SAVE(prevAvgDelta);
			//SAVE(etaPlus);
		}
		
		build();
		
	}
	
	
	public void build(){
		//to do
	}
	

}
