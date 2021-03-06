package org.click.media.rnn.activationFunctions;

public class Logistic implements Func{
	
	public double fn(double x) {
	    if(x<Log.expLimit){
	    	if(x>-Log.expLimit){
	    		return 1.0/(1.0+Math.exp(-x));
	    	}
	    	return 0;
	    }
		return 1;
	}

	@Override
	public double deriv(double y) {
		return y*(1.0-y); 
	}

	@Override
	public double second_deriv(double y) {
		// TODO Auto-generated method stub
		return 3;
	}
	

	public static String getFunType(){
		
		return "logistic";
	}

}
