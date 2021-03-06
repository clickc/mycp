package org.click.media.rnn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.click.media.speechcat.facility.SSO;


/**
 * we use 'Vector' only for configure information
 * @author blue
 */
public class ConfigFile {

	public Map<String,String> options=new HashMap<>();
	
	public String fileName;
	
	public ConfigFile(){
		
	}
	
	public ConfigFile(String confFile){
		
		try{
			
			this.fileName=confFile;
			
			BufferedReader br=new BufferedReader(new FileReader(confFile));
			
			String l="";
			String[] tokens=null;
			
			while((l=br.readLine())!=null){
				
				if(l.startsWith("#")){
					continue;
				}
				
			    tokens=l.split("=");
			    if(tokens.length!=2){
			    	continue;
			    }
			    
			    options.put(tokens[0], tokens[1]);
			}
			
			br.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String getString(String propName,String defaultValue){
		
		propName=propName.trim();
		
		if(!(options.containsKey(propName))){
			return defaultValue;
		}else{
			return options.get(propName);
		}
		
	}
	
	public void setString(String propName,String value){
		options.put(propName, value);
	}
	
	public int getInt(String propName,String defaultValue){
		
		propName=propName.trim();
		
		if(!(options.containsKey(propName))){
			return Integer.parseInt(defaultValue);
		}else{
			return Integer.parseInt(options.get(propName));
		} 

	}
	
	public double getDouble(String propName,String defaultValue){
		
		propName=propName.trim();
		
		if(!(options.containsKey(propName))){
			return Double.parseDouble(defaultValue);
		}else{
			return Double.parseDouble(options.get(propName));
		} 
		
	}
	
	public boolean getBoolean(String propName,String defaultValue){
		
		propName=propName.trim();
		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);	
		if(!(options.containsKey(propName))){
			return Boolean.parseBoolean(defaultValue);
		}else{
			return Boolean.parseBoolean(options.get(propName));
		} 
		
	}
	
	public Vector<Integer> getIntVector(String propName,String defaultValue){
		
		propName=propName.trim();
		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);		
		if(!(options.containsKey(propName))){
			return split2int(defaultValue);
		}else{
			return split2int(options.get(propName));
		} 
		
	}
	
     public Vector<Boolean> getBooleanVector(String propName,boolean defaultValue,int num){
		
		propName=propName.trim();
		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);			
		if(!(options.containsKey(propName))){
			
			Vector<Boolean> defaultVec=new Vector<>();
			for(int i=0;i<num;i++){
				defaultVec.add(defaultValue);
			}
			
			return defaultVec;
			
		}else{
			return split2boolean(options.get(propName));
		} 
		
	}
     
     public Vector<Integer> getIntVector(String propName,int defaultValue,int num){
 		
 		propName=propName.trim();
 		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);			
 		if(!(options.containsKey(propName))){
 			
 			Vector<Integer> defaultVec=new Vector<>();
 			for(int i=0;i<num;i++){
 				defaultVec.add(defaultValue);
 			}
 			
 			return defaultVec;
 			
 		}else{
 			return split2int(options.get(propName));
 		} 
 		
 	}
	
	public Vector<String> getStringVector(String propName,String defaultValue){
		
		propName=propName.trim();
		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);	
		if(!(options.containsKey(propName))){
			return split2string(defaultValue);
		}else{
			return split2string(options.get(propName));
		} 
		
	}
	
    public Vector<String> gettStringVector(String propName,String defaultValue,int num){
 		
 		propName=propName.trim();
 		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);			
 		if(!(options.containsKey(propName))){
 			
 			Vector<String> defaultVec=new Vector<>();
 			for(int i=0;i<num;i++){
 				defaultVec.add(defaultValue);
 			}
 			
 			return defaultVec;
 			
 		}else{
 			return split2string(options.get(propName));
 		} 
 		
 	}
    
  public Vector<String> gettStringVectorMul(String propName,String defaultValue,int num){
 		
 		propName=propName.trim();
 		System.err.println("propName:"+propName+" defaultValue:"+defaultValue);	
 		String trueValue="";
 		if(!(options.containsKey(propName))){			
 			trueValue=defaultValue;	
 		}else{
 			trueValue=options.get(propName);
 		} 
 		
 		Vector<String> result=new Vector<>();
 		for(int i=0;i<num;i++){
 			result.add(trueValue);
 		}
 		
 		return result;
 		
 	}
	
	public Vector<Vector<Integer>> getIntArray(String propName,String defaultValue){
		
	    propName=propName.trim();
	    System.err.println("propName:"+propName+" defaultValue:"+defaultValue);	
		if(!(options.containsKey(propName))){
			return split2intArray(defaultValue);
		}else{
			return split2intArray(options.get(propName));
		} 
		
	}
	
	public Vector<Integer> split2int(String option){
		
		Vector<Integer> confVec=new Vector<>();
		
		if(SSO.tioe(option)){
			return confVec;
		}
		
		String[] splits=option.split(",");
		
		for(int i=0;i<splits.length;i++){
			confVec.add(Integer.parseInt(splits[i]));
		}
		
		return confVec;		
	}
	
	public Vector<Boolean> split2boolean(String option){
		
		Vector<Boolean> confVec=new Vector<>();
		
		if(SSO.tioe(option)){
			return confVec;
		}
		
		String[] splits=option.split(",");
		
		for(int i=0;i<splits.length;i++){
			confVec.add(Boolean.parseBoolean(splits[i]));
		}
		
		return confVec;		
	}
	
	public Vector<String> split2string(String option){
		
		Vector<String> confVec=new Vector<>();
		
		if(SSO.tioe(option)){
			return confVec;
		}
		
		String[] splits=option.split(",");
		
		for(int i=0;i<splits.length;i++){
			confVec.add(splits[i]);
		}
		
		return confVec;		
	}
	
	public Vector<Vector<Integer>> split2intArray(String option){
		
		Vector<Vector<Integer>> confArray=new Vector<Vector<Integer>>();
		
		if(SSO.tioe(option)){
			return confArray;
		}
		
		String[] opts=option.split(";");
		for(int j=0;j<opts.length;j++)
		{
			confArray.add(split2int(opts[j]));
		}
		
		return confArray;		
	}
		
	
	public static void main(String[] args){
		
		String confFile="rnn_exmpales/arabic_offline_handwriting/transcription.config";
		
		ConfigFile cf=new ConfigFile(confFile);
		
		for(Map.Entry<String, String> item:cf.options.entrySet()){
			System.err.println(item.getKey()+"\t"+item.getValue());
		}
		
		System.err.println(cf.getString("trainFile", ""));
		System.err.println(cf.getString("maxTestsNoBest", ""));
		System.err.println(cf.getString("inputBlock", ""));
		System.err.println(cf.getString("learnRate", ""));
		System.err.println(cf.getString("hiddenBlock", ""));
				
		Vector<Integer> inputBlock=cf.getIntVector("inputBlock", "");
		
		for(int i=0;i<inputBlock.size();i++){
			System.err.println(inputBlock.get(i));
		}
		
		Vector<Vector<Integer>> hiddenBlock=cf.getIntArray("hiddenBlock", "");
		for(int i=0;i<hiddenBlock.size();i++){
			for(int j=0;j<hiddenBlock.get(i).size();j++)
			{
			   System.err.print(hiddenBlock.get(i).get(j)+" ");
			}
			System.err.println();
		}
				
	}
	
	public void set_val(String name,String val){
		options.put(name, val);
	}
	
	
}
