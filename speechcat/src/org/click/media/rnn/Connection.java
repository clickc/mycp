package org.click.media.rnn;

import java.util.Vector;

public class Connection extends Named{

	public Layer from;
	public Layer to;
		
	public Connection(String n) {
		super(n);
		// TODO Auto-generated constructor stub
	}
	
	public Connection(String name,Layer f,Layer t){
		
		super(name);
		this.from=f;
		this.to=t;
		
	}
	
	public int num_weights(){
		return 0;
	}
	
	public void feed_forward(Vector<Integer> coords){
		
	}
	
	public void feed_back(Vector<Integer> coords){
		
	}
	
	public void update_derivs(Vector<Integer> coords){
		
	}
	
	public Vector<Double> weights(){
		return new Vector<Double>();
	}
	

}
