package org.click.media.rnn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class WeightContainer extends DataExporter{

	static Logger LOG =  Logger.getLogger(WeightContainer.class.getName()); 
	
	public static double[] weights;
	public static double[] derivatives;
	public static int size=0;
	public static Map<Integer,Double> uniformInits=new HashMap<>();
	
	////static PrintWriter pw=null;
	
	static{
		/////try{
	/////	pw=new PrintWriter(new FileWriter("logs/myweights.txt"));
	/////}
	/////	catch(Exception e){
	/////		e.printStackTrace();
	/////	}
	}
	
	//static Logger LOG =  Logger.getLogger(WeightContainer.class.getName()); 
	
    //key:toName
	//value:make_tuple(fromName, connName, paramBegin, paramEnd)
	public static Map<String,LinkTuple> connections=new HashMap<>();
	
	
	public WeightContainer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	/**
	 * connName = ""
	 * paramBegin = 0
	 * paramEnd = 0
	 * @param fromName
	 * @param toName
	 * @param connName
	 * @param paramBegin
	 * @param paramEnd
	 */
	public static void link_layers(String fromName,String toName,String connName,int paramBegin,int paramEnd){
		connections.put(toName, new LinkTuple(fromName,connName,paramBegin,paramEnd));
	}
	
	public static void build(){
		//to do	
		derivatives=new double[weights.length];
		reset_derivs();
	}
	
	public static void reset_derivs()
	{
		for(int i=0;i<derivatives.length;i++){
			derivatives[i]=0.0;
		}
	}
	
	
	/*old
	public static int randomise(double range){
		
		int numRandWts = 0;
		
		double w;
		for(int i=0;i<weights.length;i++){
			w=weights[i];
			if(Double.isInfinite(w)){
			   w=Math.random()*range;
			   ++numRandWts;
			}
			
		}
		
		return numRandWts;
	}*/
	
    public static int randomise(double range){
		
		int numRandWts = 0;
		
		double w;
		for(int i=0;i<weights.length;i++){
			w=weights[i];
			//w=Math.random()*range;
			w=uniform(range);
			weights[i]=uniformInits.get(i);
			++numRandWts;
			
		}
		
		return numRandWts;
	}
    
    
    /**
     * generate random value between [-range,range]
     * @param range
     * @return
     */
    public static double uniform(double range){
    	
    	double rand=0.0;
    	
    	rand=2*(Math.random())*range-range;
    	
    	return rand;
    }

	public static Pair new_parameters(int numParams,String fromName,String toName,String connName){
		
		int begin=size;
		//weights.resize(weights.size() + numParams);
		//how to implement resize of array?
		size+=numParams;
		int end=size;
		//pw.println("lq create new parameters numParams:"+numParams+" begin:"+begin+" end:"+end+" fromName:"+fromName+" toName:"+toName+" connName:"+connName);
		//pw.flush();
		//LOG.info("lq create new parameters numParams:"+numParams+" begin:"+begin+" end:"+end+" fromName:"+fromName+" toName:"+toName+" connName:"+connName);

		link_layers(fromName,toName,connName,begin,end);
		
		return new Pair(begin,end);
		
	}
	
	public static void initWeights(){
		weights=new double[size];
	}
	
	public static void initFromMy(){
		
		try{
			BufferedReader br=new BufferedReader(new FileReader("logs/mlibcplus/uniweights3.txt"));
			//BufferedReader br=new BufferedReader(new FileReader("logs/mlibcplus/uniweightsdigit.txt"));
			
			String l;
			
			String[] tokens=null;
			while((l=br.readLine())!=null){
				tokens=l.trim().split("\\s+");
				if(tokens.length!=2){
					continue;
				}
				//LOG.info("weights index:"+tokens[0]+" value:"+tokens[1]);
				uniformInits.put(Integer.parseInt(tokens[0]), Double.parseDouble(tokens[1]));
			}
			
			br.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static List<Double> sliceWeights(Pair paramRange){
		
		List<Double> slice=new ArrayList<>();
		
		for(int i=paramRange.first;i<paramRange.second;i++){
			slice.add(weights[i]);
		}
		
		return slice;
	}
	
	public static List<Double> sliceDerivs(Pair paramRange){
		
		List<Double> slice=new ArrayList<>();
		
		for(int i=paramRange.first;i<paramRange.second;i++){
			slice.add(derivatives[i]);
		}
		
		return slice;
	}
	
	
	public static void main(String[] args){
		
		for(int i=0;i<100;i++){
			System.err.println("i="+i+" "+uniform(0.1));
		}
		
	}
	
}
