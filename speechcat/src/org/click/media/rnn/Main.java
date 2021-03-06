package org.click.media.rnn;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

public class Main {
	
	static Logger LOG =  Logger.getLogger(Main.class.getName()); 
	
	static Map<String,Integer> validDatasets=new HashMap<>();
	
	static {
		validDatasets.put("train", 1);
		validDatasets.put("test", 1);
		validDatasets.put("val", 1);
	}
	
	public static void printHelp(){
		
	   System.err.println("usage [config_options] config_file");	
	   System.err.println("config_options syntax: -<variable_name>=<variable_value>");
	   System.err.println("whitespace not allowed in variable names or values");
	   System.err.println("all config_file variables overwritten by config_options");
	   System.err.println("setting <variable_value> = \"\" removes the variable from the config");
	   System.err.println("repeated variables overwritten by last specified");
	   
	}

	public static void main(String[] args){
	
		    if(args.length<1){
		    	printHelp();
		    	System.exit(1);
		    }
		    
		    Properties prop=ArgsTools.mkPropertiesFromArg(args);		
	        ConfigFile conf=new ConfigFile(args[args.length-1]);
	         
	        boolean autosave=conf.getBoolean("autosave", "false");
	        String task=conf.getString("task", "");
	        
	        if(task.equals("prediction")&&conf.getInt("predictionSteps", "1")==1){
	        	conf.setString("task", "window-prediction");
	        	task=conf.getString("task", "");
	        }
	        
	        boolean display=conf.getBoolean("display", "false");
	        Vector<Integer> jacobianCoords=conf.getIntVector("jacobianCoords","");
	        boolean gradCheck=conf.getBoolean("gradCheck", "false");
	        
	        boolean verbose=conf.getBoolean("verbose", "false");
	        int displaySequence=conf.getInt("sequence", "0");
	        String dataset=conf.getString("dataset", "train");
	        
	        String dataFileString=dataset+"File";
	        String saveName="";
	        
	        if(autosave){
	        	saveName=conf.fileName.substring(0,conf.fileName.indexOf('.'));
	        	saveName+=("@"+TimeOpera.getCurrentTimeStamp());
	        }
	        
	        Vector<String> dataFiles=conf.getStringVector(dataFileString, "");
	        int dataFileNum=conf.getInt("dataFileNum", "0");
	        
		    String datafile=dataFiles.get(dataFileNum);
	        
		    System.err.println("datafile:"+datafile);
		    DataHeader header=new DataHeader(datafile,task,1,"ncfile");
		    
	        Mdrnn net=new MultilayerNet(conf,header);
	        
	        WeightContainer.initWeights();
	        
	        WeightContainer.build();
	        
	        //int numWeights=WeightContainer.weights.length;
	        
	        //net->build
	        net.print();
	        int numWeights=WeightContainer.weights.length;
	        LOG.info(numWeights+" weights");
	        Trainer trainer=new Trainer(net,conf,"trainer");
	        
	        WeightContainer.initFromMy();
	        double initWeightRange=conf.getDouble("initWeightRange", "0.1");
	        int numRandWts =WeightContainer.randomise(initWeightRange);
	        if(numRandWts>0){
	        	System.err.println(numRandWts+" uninitialised weights randomised");
	        }
	        
	        trainer.train(saveName);
	        
	}
	
	
	
	
}
