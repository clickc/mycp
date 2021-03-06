package org.click.media.rnn;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * resize may exists some errors
 * @author lq
 */
public class CollapseLayer extends Layer{

	Vector<Boolean> activeDims=new Vector<>();
	Vector<Integer> outSeqShape=new Vector<>();
	
	static Logger LOG =  Logger.getLogger(CollapseLayer.class.getName()); 
	
	private RangeView outActsView=new RangeView(null,null);
	private RangeView inputActsView=new RangeView(null,null);
	
	private RangeView outputErrorsView=new RangeView(null,null);
	private RangeView inputErrorsView=new RangeView(null,null);
	
	private IndexRange tempOutActsIR=new IndexRange(0,0);
	private IndexRange tempInputActsIR=new IndexRange(0,0);
	private IndexRange tempOutputErrorsIR=new IndexRange(0,0);
	private IndexRange tempInputErrorsIR=new IndexRange(0,0);
	
	private ResizeUtils ru=new ResizeUtils();
	
	public CollapseLayer(Layer src,Layer des,Vector<Boolean> activeDims)
	{
		super(des.name + "_collapse",des.directions,des.input_size(),des.input_size(),src);
		this.activeDims=activeDims;
		
		//this.activeDims=ResizeUtils.resizeBoolean(this.activeDims,src.num_seq_dims(),false);
		
		this.activeDims=ru.resizeBoolean(this.activeDims,src.num_seq_dims(),false);
		
	}
	
	public void start_sequence(){
		outSeqShape.clear();
		for(int i=0;i<activeDims.size();++i){
			
			if(activeDims.get(i)){
				outSeqShape.add(source.output_seq_shape().get(i));
			}
		}
		
		this.inputActivations.reshape(source.output_seq_shape(), 0.0);
		this.outputActivations.reshape(outSeqShape,0.0);
		reshape_errors();
		
	}
	
	public Vector<Integer> get_out_coords(Vector<Integer> inCoords){
		
		Vector<Integer> outCoords=new Vector<>();
		for(int i=0;i<inCoords.size();++i){
			if(activeDims.get(i)){
				outCoords.add(inCoords.get(i));
			}
		}
		
		return outCoords;
	}
	
	public void feed_forward(Vector<Integer> coords){
		//Helpers.range_plus_equals(this.outputActivations.g, b);
		/*
		IndexRange outActsRange=this.outputActivations.getView(get_out_coords(coords));
		List<Double> outActsPiece=this.outputActivations.fetchData(outActsRange);
		List<Double> inputActPiece=this.inputActivations.fetchData(this.inputActivations.getView(coords));
		
		double oval;
		for(int i=0;i<outActsPiece.size();i++){
			oval=outActsPiece.get(i);
			outActsPiece.set(i, inputActPiece.get(i)+oval);
		}
		
		this.outputActivations.fill(outActsRange.start,outActsRange.end, outActsPiece);
		*/
	
		//minimize the instantiation of list
		//RangeView outActsView=new RangeView(this.outputActivations.getView(get_out_coords(coords)),this.outputActivations);
		//outActsView.setMem(this.outputActivations.getView(get_out_coords(coords)), this.outputActivations);
		//ir//IndexRange tempOutActsIR=new IndexRange(0,0);
		this.outputActivations.getView(get_out_coords(coords),tempOutActsIR);
		outActsView.setMem(tempOutActsIR, this.outputActivations);
		
		//RangeView inputActsView=new RangeView(this.inputActivations.getView(coords),this.inputActivations);
		//inputActsView.setMem(this.inputActivations.getView(coords), this.inputActivations);
		//ir//IndexRange tempInputActsIR=new IndexRange(0,0);
		this.inputActivations.getView(coords,tempInputActsIR);
		inputActsView.setMem(tempInputActsIR, this.inputActivations);
		
		double oval;
		for(int i=0;i<outActsView.size();i++){
			oval=outActsView.getOffset(i);
			outActsView.setOffset(i, oval+inputActsView.getOffset(i));
		}
		
		//LOG.info("lq CollapseLayer coords:"+coords+" get_out_coords:"+get_out_coords(coords)+" outputActivations:"+outActsView.getRangeData());
		
	}
	
	public void feed_back(Vector<Integer> coords){
		
		Vector<Integer> outLQCoords=get_out_coords(coords);
		//RangeView outputErrorsView=new RangeView(this.outputErrors.getView(get_out_coords(coords)),this.outputErrors);
		//outputErrorsView.setMem(this.outputErrors.getView(get_out_coords(coords)), this.outputErrors);
		
		//ir//IndexRange tempOutputErrorsIR=new IndexRange(0,0);
		this.outputErrors.getView(get_out_coords(coords),tempOutputErrorsIR);
		outputErrorsView.setMem(tempOutputErrorsIR, this.outputErrors);
		
		//RangeView inputErrorsView=new RangeView(this.inputErrors.getView(coords),this.inputErrors);
		//inputErrorsView.setMem(this.inputErrors.getView(coords), this.inputErrors);
		//ir//IndexRange tempInputErrorsIR=new IndexRange(0,0);
		this.inputErrors.getView(coords,tempInputErrorsIR);
		inputErrorsView.setMem(tempInputErrorsIR, this.inputErrors);
		
		for(int i=0;i<inputErrorsView.size();i++){
			inputErrorsView.setOffset(i,outputErrorsView.getOffset(i) );
		}
		
		//bug20//LOG.info("lq CollapseLayer feed_back coords:"+coords+" outLQCoords:"+outLQCoords+" outputErrorsStr:"+outputErrorsView.indexRange+" outputErrors:"+outputErrorsView.getRangeData()+" inputErrorStr:"+inputErrorsView.indexRange+" inputErrors:"+inputErrorsView.getRangeData());
		
	}
	
	
}
