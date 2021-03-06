package org.click.media.rnn.activationFunctions;

public interface Func {

	public double fn(double x);
	
	public double deriv(double y);
	
	public  double second_deriv(double y);
	
	public static String getFunType(){
		
		return "func";
	}
	
}
