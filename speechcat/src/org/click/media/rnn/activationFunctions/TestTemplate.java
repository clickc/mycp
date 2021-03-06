package org.click.media.rnn.activationFunctions;

public class TestTemplate<F extends Func> {

	F f;
	
	public TestTemplate(F f){
		this.f=f;
	}
	
	public void test(){
	

		System.err.print("fn:"+f.fn(1));
		System.err.print("deriv:"+f.deriv(1));
		System.err.print("second_deriv:"+f.second_deriv(1));
	}
	
	public static void main(String[] args){
		
		
		TestTemplate<Logistic> t=new TestTemplate<Logistic>(new Logistic());
		
		t.test();
		
	}

}
