package org.click.media.rnn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public interface NetworkOutput {

	public Map<String,Double> errorMap=new HashMap<>();
	public Map<String,Double> normFactors=new HashMap<>();
	public Vector<String> criteria=new Vector<>();
	
	public double calculate_errors(DataSequence seq);
	
	public String getName();
	
	public Map<String,Double> getErrorMap();
	
	public Map<String,Double> getNormFactors();
	
}
