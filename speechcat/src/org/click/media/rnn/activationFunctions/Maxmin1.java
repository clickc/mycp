package org.click.media.rnn.activationFunctions;

public class Maxmin1 implements Func{

	Logistic lc=new Logistic();
	
	@Override
	public double fn(double x) {
		return (2 * lc.fn(x)) - 1;
	}

	@Override
	public double deriv(double y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double second_deriv(double y) {
		// TODO Auto-generated method stub
		return 0;
	}

}
