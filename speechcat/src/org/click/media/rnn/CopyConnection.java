package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class CopyConnection extends Connection{
	
	static Logger LOG =  Logger.getLogger(CopyConnection.class.getName()); 
	
	private RangeView toInputView=new RangeView(null,null);
	private RangeView fromOutputView=new RangeView(null,null);
	
	private RangeView fromOutputErrView=new RangeView(null,null);
	private RangeView toInputErrView=new RangeView(null,null);
	
	private IndexRange tempToInputIR=new IndexRange(0,0);
	private IndexRange tempFromOutputIR=new IndexRange(0,0);
	private IndexRange tempFromOutputErrIR=new IndexRange(0,0);
	private IndexRange tempToInputErrIR=new IndexRange(0,0);
	
	public CopyConnection(Layer f,Layer t){
		super(f.name + "_to_" + t.name, f, t);
		this.to.source=this.from;
		WeightContainer.link_layers(this.from.name, this.to.name, "", 0, 0);
	}
	
	public void feed_forward(Vector<Integer> coords){
		
		//RangeView toInputView=new RangeView(this.to.inputActivations.getView(coords),this.to.inputActivations);		
		//toInputView.setMem(this.to.inputActivations.getView(coords), this.to.inputActivations);
		//ir//IndexRange tempToInputIR=new IndexRange(0,0);
		this.to.inputActivations.getView(coords,tempToInputIR);
		toInputView.setMem(tempToInputIR, this.to.inputActivations);
		
		//RangeView fromOutputView=new RangeView(this.from.outputActivations.getView(coords),this.from.outputActivations);	
		//fromOutputView.setMem(this.from.outputActivations.getView(coords), this.from.outputActivations);
		//ir//IndexRange tempFromOutputIR=new IndexRange(0,0);
		this.from.outputActivations.getView(coords,tempFromOutputIR);
		fromOutputView.setMem(tempFromOutputIR, this.from.outputActivations);
		
		double oval;
		for(int i=0;i<toInputView.size();i++){
			oval=toInputView.getOffset(i);
			toInputView.setOffset(i, oval+fromOutputView.getOffset(i));
		}
		
	}
	
	public void feed_back(Vector<Integer> coords){
		
		//RangeView fromOutputErrView=new RangeView(this.from.outputErrors.getView(coords),this.from.outputErrors);
		//fromOutputErrView.setMem(this.from.outputErrors.getView(coords), this.from.outputErrors);
		//ir//IndexRange tempFromOutputErrIR=new IndexRange(0,0);
		this.from.outputErrors.getView(coords,tempFromOutputErrIR);
		fromOutputErrView.setMem(tempFromOutputErrIR, this.from.outputErrors);
		
		//RangeView toInputErrView=new RangeView(this.to.inputErrors.getView(coords),this.to.inputErrors);
		//toInputErrView.setMem(this.to.inputErrors.getView(coords), this.to.inputErrors);
		//ir//IndexRange tempToInputErrIR=new IndexRange(0,0);
		this.to.inputErrors.getView(coords,tempToInputErrIR);
		toInputErrView.setMem(tempToInputErrIR, this.to.inputErrors);
		
		double oval;
		for(int i=0;i<fromOutputErrView.size();i++){
			oval=fromOutputErrView.getOffset(i);
			fromOutputErrView.setOffset(i, oval+toInputErrView.getOffset(i));
		}
		
		//bug20//LOG.info("lq CopyConnection feed_back coords:"+coords+" fromOutputErrorStr:"+fromOutputErrView.indexRange+" fromOutputErrorLQ:"+fromOutputErrView.getRangeData()+" toInputErrorStr:"+toInputErrView.indexRange+" toInputErrorLQ:"+toInputErrView.getRangeData());
		
		
	}
	
	
	

	public String toString(){
		return this.name+" (copy)";
	}
}
