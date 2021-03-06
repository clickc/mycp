package org.click.media.rnn;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MultilayerNet extends Mdrnn{

	public MultilayerNet(ConfigFile conf, DataHeader data) {
		super(conf, data);
		String task=conf.getString("task", "");
		Vector<Integer> hiddenSizes=conf.getIntVector("hiddenSize", "");
		Vector<String> hiddenTypes=conf.gettStringVectorMul("hiddenType", "lstm", hiddenSizes.size());
		Vector<Vector<Integer> > hiddenBlocks=conf.getIntArray("hiddenBlock", "");
		
		Vector<Integer> subsampleSizes=conf.getIntVector("subsampleSize", "");
		String subsampleType=conf.getString("subsampleType", "tanh");
		boolean subsampleBias=conf.getBoolean("subsampleBias", "false");
		Vector<Boolean> recurrent=conf.getBooleanVector("recurrent", true, hiddenSizes.size());
		Layer input=this.get_input_layer();
		
		for(int i=0;i<hiddenSizes.size();i++){
			
			String level_suffix=Helpers.int_to_sortable_string(i, hiddenSizes.size());
			System.err.println("i:"+i+" hiddenTypes.size:"+hiddenTypes.size()+" "+hiddenTypes+" hiddenSizes.size:"+hiddenSizes.size()+" "+hiddenSizes+" recurrent:"+recurrent.size()+" "+recurrent);
			System.out.println("i:"+i+" hiddenTypes.size:"+hiddenTypes.size()+" "+hiddenTypes+" hiddenSizes.size:"+hiddenSizes.size()+" "+hiddenSizes+" recurrent:"+recurrent.size()+" "+recurrent);
			this.add_hidden_level(hiddenTypes.get(i), hiddenSizes.get(i), recurrent.get(i), "hidden_" + level_suffix, true);
		    this.connect_to_hidden_level(input, i);
			Vector<Layer> blocks=new Vector<>();
			
			if(i<hiddenBlocks.size()){
				
				for(Layer l:hiddenLevels.get(i)){
					blocks.add(this.add_layer(new BlockLayer(l,hiddenBlocks.get(i)),false, false));
				}
				
			}
			
			Vector<Layer> topLayers=blocks.size()!=0?blocks:hiddenLevels.get(i);
			if(i<subsampleSizes.size()){
				input=this.add_layer(subsampleType, "subsample_" + level_suffix, subsampleSizes.get(i), Helpers.createVector(this.num_seq_dims(), 1), subsampleBias, false);
			    for(Layer l:topLayers){
			    	Vector<Integer> noDelay=new Vector<>();
			    	this.connect_layers(l, input, noDelay);
			    }
			}else if(i<(hiddenSizes.size()-1)){
				input=this.add_layer(new GatherLayer("gather_" + level_suffix,topLayers,get_size(topLayers)), false, false);
			}
			
		}
		
		conf.set_val("inputSize", inputLayer.output_size()+"");
		
		if(data.targetLabels.size()!=0){
			
			String labelDelimiters=",.;:|+&_~*%$#^=-<>/?{}[]()";
			for(int k=0;k<labelDelimiters.length();k++){
				char c=labelDelimiters.charAt(k);
				boolean goodDelim = true;
				
				for(String s:data.targetLabels){
					if(s.indexOf(c)!=-1){
						goodDelim=false;
						break;
					}
				}
				
				if(goodDelim){
					conf.set_val("targetLabels", RangeUtils.delimStr(data.targetLabels, c));
					conf.set_val("labelDelimiter", c+"");
				}		
				
			}
		}
		
		String outputName = "output";
		Layer output=null;
		int outSeqDims=task.indexOf("sequence_")!=-1?0:num_seq_dims();
		
		if(task.equals("classification")){
			
		}else if(task.equals("transcription")){
			output=this.add_output_layer(new TranscriptionLayer(outputName,Helpers.make_target_labels(data.targetLabels),conf.getBoolean("confusionMatrix", "false")), true);
			if(this.num_seq_dims()>1){
				Vector<Boolean> activeDims=new Vector<>();
				activeDims.add(true);
				output=this.collapse_layer(hiddenLayers.lastElement(), output, activeDims);
			}	
		}else{
			System.err.println("unknown task " + task );
		}
		
		this.connect_from_hidden_level(hiddenLevels.size()-1,output);
		
	}
	
	public int get_size(Vector<Layer> srcs){
		int size=0;
		for(int i=0;i<srcs.size();i++){
			size+=srcs.get(i).output_size();
		}
		return size;
	}
	


	
	
	
}
