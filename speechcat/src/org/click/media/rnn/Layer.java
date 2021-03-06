package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class Layer extends DataExporter{
     
	//the element of directions is either 1 or -1
	public Vector<Integer> directions;
	
	public SeqBufferDouble inputActivations;
	public SeqBufferDouble outputActivations;
	public SeqBufferDouble inputErrors;
	public SeqBufferDouble outputErrors;
	
	Layer source;
	static Logger LOGO =  Logger.getLogger(LstmLayer.class.getName()); 

	
	public Layer(String name,int numSeqDims,int inputSize,int outputSize,Layer src){
		super(name);
		System.err.println("construct layer from Layer DIM name:"+name);
		System.err.println("inputSize:"+inputSize);
		inputActivations=new SeqBufferDouble(inputSize);
		System.err.println("outputSize:"+outputSize);
		outputActivations=new SeqBufferDouble(outputSize);
		inputErrors=new SeqBufferDouble(inputSize);
		outputErrors=new SeqBufferDouble(outputSize);
		source=src;
		directions=Helpers.createVector(numSeqDims, 1);
		
	}
	
	public Layer(String name,Vector<Integer> dirs,int inputSize,int outputSize,Layer src){
		super(name);
		System.err.println("construct layer from Layer DIR name:"+name);
		
		//this.directions=dirs;
		//we should copy the content of dirs not address?
		this.directions=Helpers.copyVector(dirs);
		System.out.println("construct layer from Layer DIR name:"+name+" inputSize:"+inputSize);
		this.inputActivations=new SeqBufferDouble(inputSize);
		this.outputActivations=new SeqBufferDouble(outputSize);
		this.inputErrors=new SeqBufferDouble(inputSize);
		this.outputErrors=new SeqBufferDouble(outputSize);
		this.source=src;	
	}
	
	public int num_seq_dims(){
		return directions.size();
	}
	
	/**
	 * the number of output is superimposed into the depth of one coordinate
	 * @return
	 */
	public int output_size(){
		return outputActivations.depth;
	}
	
	public int input_size(){
		return inputActivations.depth;
	}
	
	public Vector<Integer> output_seq_shape(){
		return outputActivations.seq_shape();
	}
	
	public void reshape_errors(){
		inputErrors.reshape(inputActivations, 0.0);
		outputErrors.reshape(outputActivations, 0.0);
	}
	
	public void start_sequence(){
		inputActivations.reshape(source.output_seq_shape(),0.0);
		//LOGO.info("lq Layer name "+this.name+" source:"+source.name+" output_seq_shape:"+source.output_seq_shape());
		outputActivations.reshape(source.output_seq_shape(), 0.0);
		reshape_errors();	
	}
	
	
	public CoordIterator input_seq_begin(){
		return input_size()!=0?inputActivations.begin(directions):outputActivations.begin(directions);
	}
	
    public CoordIterator input_seq_rbegin(){
		return input_size()!=0?inputActivations.rbegin(directions):outputActivations.rbegin(directions);
	}
	
    /*
	public IndexRange out_acts(Vector<Integer> coords){
		return outputActivations.getView(coords);
	}
	
	public IndexRange out_errs(Vector<Integer> coords){
		return outputErrors.getView(coords);
	}
	*/
	
	
   
	public void out_acts(Vector<Integer> coords,IndexRange result){
		//return outputActivations.getView(coords,result);
		outputActivations.getView(coords,result);
	}
	
	public void out_errs(Vector<Integer> coords,IndexRange result){
		//return outputErrors.getView(coords);
		outputErrors.getView(coords,result);
	}
	
	
	public void feed_forward(Vector<Integer> coords){
		
	}
	
	public void feed_back(Vector<Integer> coords){
		
	}
	
	public void update_derivs(Vector<Integer> coords){
		
	}
	
	
	public String dataSizeInfo(){
		
		String dsInfo="layer:"+this.name+"\t";
		dsInfo+=("inputActivations.size:"+inputActivations.data.size()+"\t");
		dsInfo+=("outputActivations.size:"+outputActivations.data.size()+"\t");
		dsInfo+=("inputErrors.size:"+inputErrors.data.size()+"\t");
		dsInfo+=("outputErrors.size:"+outputErrors.data.size()+"\t");
		
		return dsInfo;
	}
	
    public String shapeSizeInfo(){
		
		String shapeInfo="layer:"+this.name+"\t";
		shapeInfo+=("inputActivations.shape:"+inputActivations.shape+"\t");
		shapeInfo+=("outputActivations.shape:"+outputActivations.shape+"\t");
		shapeInfo+=("inputErrors.shape:"+inputErrors.shape+"\t");
		shapeInfo+=("outputErrors.shape:"+outputErrors.shape+"\t");
		
		return shapeInfo;
	}
    
    public String depthSizeInfo(){
		
 		String shapeInfo="layer:"+this.name+"\t";
 		shapeInfo+=("inputActivations.depth:"+inputActivations.depth+"\t");
 		shapeInfo+=("outputActivations.depth:"+outputActivations.depth+"\t");
 		shapeInfo+=("inputErrors.depth:"+inputErrors.depth+"\t");
 		shapeInfo+=("outputErrors.depth:"+outputErrors.depth+"\t");
 		
 		return shapeInfo;
 	}
	
	public String toString(){
		
		String desc="";
		desc+=(this.getClass().getSimpleName()+" ");
		desc+=(this.name+" ");
		desc+=(" "+this.num_seq_dims()+"D");
		
		if(directions.size()>0){
			desc+=(" (");
			for(int d=0;d<directions.size();d++){
				desc+=((directions.get(d)>0)?"+":"-");
			}
			desc+=")";
		}
		
		if(input_size()==0){
			desc+=(" size "+output_size());
		}else if(output_size()==0||input_size()==output_size()){
			desc+=(" size "+input_size());
		}else{
			desc+=(" inputSize "+input_size()+" outputSize "+output_size());
		}
		
		if(source!=null){
			desc+=(" source \""+source.name+"\"");
		}
		
		return desc;
	}
}
