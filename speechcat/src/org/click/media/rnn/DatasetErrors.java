package org.click.media.rnn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DatasetErrors {

	Map<String,Double> errors=new HashMap<>();
	Map<String,Double> normFactors=new HashMap<>();
	Vector<String> percentErrors=new Vector<>();
	private int seqNum=0;
	
	public DatasetErrors(){
		
	}
	
	public void clear(){
		setSeqNum(0);
		errors.clear();
		normFactors.clear();
	}
	
	/**
	 * default:
	 * normFactor: 1
	 * @param name
	 * @param error
	 * @param normFactor
	 */
	public void add_error(String name,double error,double normFactor){
		
		//setSeqNum(getSeqNum() + 1);
		
		if(!(errors.containsKey(name))){
			errors.put(name, error);
		}else{
			errors.put(name, errors.get(name)+error);
		}
		
		if(!(normFactors.containsKey(name))){
			normFactors.put(name, normFactor);
		}else{
			normFactors.put(name, normFactors.get(name)+normFactor);
		}
		
	}
	
	public void add_seq_errors(Map<String,Double> seqErrors,Map<String,Double> seqNorms){
	    
		setSeqNum(getSeqNum() + 1);
		
		for(Map.Entry<String, Double> entry:seqErrors.entrySet()){
			
			if(seqNorms.containsKey(entry.getKey())){
				add_error(entry.getKey(),entry.getValue(),seqNorms.get(entry.getKey()));
			}else{
				add_error(entry.getKey(),entry.getValue(),1);
			}		
		}

	}
	
    public void normalise(){
    	
    	double normFactor=-1;
    	
    	for(Map.Entry<String, Double> entry:errors.entrySet()){
    		
    		if(normFactors.containsKey(entry.getKey())){
    			normFactor=normFactors.get(entry.getKey());
    			errors.put(entry.getKey(), entry.getValue()/normFactor);
    		}
    		
    	}
    	
    }
    
    public void incrSeqNum(){
    	seqNum+=1;
    }

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	
	
	
	
}
