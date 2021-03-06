package org.click.media.rnn.activationFunctions;

public class Tanh implements Func{

	Maxmin1 mm1=new Maxmin1();
	
	@Override
	public double fn(double x) {
		return mm1.fn(2*x);
	}

	@Override
	public double deriv(double y) {
		return 1.0 - (y *  y);
	}

	@Override
	public double second_deriv(double y) {
		// TODO Auto-generated method stub
		return 0;
	}

}
