package org.click.media.rnn;

public class SlappyLog {

	//the infinity max value of log value
	public static final double logInfinity=1e100;
	
	//the infinity min value of log value, origin value is infinity close to zero
	public static final double logZero=-1e100;
	
	public static final double expMax=Double.MAX_VALUE;
	
	public static final double expMin=Double.MIN_VALUE;
	
	//right?
	public static final double expLimit=Math.log(Double.MAX_VALUE);
	
	
	public static double safe_exp(double x){
		
		if(x==logZero){
			return 0;
		}
		
	    if(x>=expLimit){
	    	return expMax;
	    }
	    
	    return Math.exp(x);
		
	}
	
	public static double log_add(double x,double y){
		
		//System.err.println("x:"+x+" logZero:"+logZero+" x==logZero:"+(x==logZero));
		if(x==logZero){
			return y;
		}
		
		//System.err.println("y:"+y+" logZero:"+logZero+" y==logZero:"+(y==logZero));
		if(y==logZero){
			return x;	
		  // return y;	
		}
		
		double temp=-1;
		if(x<y){
			temp=x;
			x=y;
			y=temp;
		}
		
		return x+Math.log(1.0+safe_exp(y-x));
		
	}
	
	public static void main(String[] args){
		double oval=-47.0464;
		
		double fvar=-1e+100;
		double bvar=-47.0651;
		
		System.err.println("fvar+bvar:"+(fvar+bvar));
		System.err.println("fvar+bvar:"+SlappyLog.log_add(fvar,bvar));
		System.err.println("2+3:"+SlappyLog.log_add(2,3));
		System.err.println(SlappyLog.log_add(oval, fvar+bvar));
	}
	
	
}
