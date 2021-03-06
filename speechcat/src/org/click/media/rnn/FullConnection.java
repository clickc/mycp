package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class FullConnection extends Connection{

	static Logger LOG =  Logger.getLogger(FullConnection.class.getName()); 
	
	public Vector<Integer> delay;
	public Vector<Integer> delayedCoords;
	public FullConnection source;
	public Pair paramRange;
	
	private RangeView toInputErrorView=new RangeView(null,null);
	private RangeView fromOutputErrorView=new RangeView(null,null);
	
	private IndexRange tempToInputIR=new IndexRange(0,0);
	private IndexRange tempFromOutActsIR=new IndexRange(0,0);
	private IndexRange tempToInputErrorIR=new IndexRange(0,0);
	private IndexRange tempFromOutErrsIR=new IndexRange(0,0);
	//private IndexRange tempToInputErrorIR=new IndexRange(0,0);
	//private IndexRange tempFromOutActsIR=new IndexRange(0,0);
	
	private ResizeUtils ru=new ResizeUtils();
	
	public Vector<Integer> add_delay(Vector<Integer> toCoords){
		
		//LOG.info("add_delay: name:"+this.name+"  stoCoords:"+toCoords+" delay:"+delay);
		
		if(delay==null||delay.isEmpty()){
		   return toCoords;	
		}
		//LOG.info("add_delay before toCoords:"+toCoords+" delay:"+delay+" name:"+this.name+" delayedCoords:"+delayedCoords);
		Helpers.range_plus(delayedCoords, toCoords, delay);
		//LOG.info("add_delay after toCoords:"+toCoords+" delay:"+delay+" name:"+this.name+" delayedCoords:"+delayedCoords);
	
		//check this function 
		//c1:toCoords:60 19 dealy:1 0 E
		//c2:toCoords:60 19 dealy:0 1 E
		//c3:toCoords:60 18 dealy:1 0 E
		//c4:toCoords:60 18 dealy:0 1 R
		
		if(this.from.outputActivations.in_range(delayedCoords)){
			return delayedCoords;
		}
		
		return null;
	}

	public FullConnection(String n) {
		super(n);
		// TODO Auto-generated constructor stub
	}
	
	public FullConnection(String name,Layer f,Layer t,Vector<Integer> d,FullConnection s){
		super(name,f,t);
		//the FullConnection source of this FullConnection?
		this.source=s;
		paramRange=(source!=null)?source.paramRange:WeightContainer.new_parameters(this.from.output_size()*this.to.input_size(), this.from.name, this.to.name, name);
		
		if(source!=null){
			WeightContainer.link_layers(this.from.name, this.to.name, this.name, paramRange.first, paramRange.second);
		}
	    
		//should add set_delay(d)?
		set_delay(Helpers.copyVector(d));
		if(!(this.from.name.equals("bias"))&&!(this.from.name.equals(this.to.name))&&this.to.source==null){
			this.to.source=this.from;
		}
		
	}
	
	
	public void feed_forward(Vector<Integer> toCoords){
		
		//if(this.name.equals("bias_to_hidden_0_0")){
		//	System.err.println("begin to debug");
		//}
		
		Vector<Integer> fromCoords=add_delay(toCoords);
		
		//LOG.info("feed_forward toCoords:"+toCoords+" fromCoords:"+fromCoords+" this.from.outputActivations.des:"+this.from.outputActivations.trackDes());
		////lq///IndexRange out_actsLQ;
	////lq///IndexRange toInputLQ;
		
				
		if(fromCoords!=null){
		    ////lq///out_actsLQ=this.from.out_acts(fromCoords);
		    ////lq///toInputLQ=this.to.inputActivations.getView(toCoords);
			//if(this.name.indexOf("hidden_0_3")!=-1)
			//{
			  //LOG.info("lq FullConnection before name:"+this.name+" fromCoords:"+fromCoords+" toCoords:"+toCoords+" out_actsLQ:"+this.from.outputActivations.fetchData(out_actsLQ)+" toInputLQ:"+this.to.inputActivations.fetchData(toInputLQ)+" weightsLQ:"+WeightContainer.sliceWeights(paramRange)+" second-first:"+(paramRange.second-paramRange.first));
			//}
			
			//Matrix.dot(this.from.outputActivations, this.from.out_acts(fromCoords), paramRange.first, this.to.inputActivations, this.to.inputActivations.getView(toCoords));
			//ir//IndexRange tempToInputIR=new IndexRange(0,0);
			this.to.inputActivations.getView(toCoords,tempToInputIR);
			//Matrix.dot(this.from.outputActivations, this.from.out_acts(fromCoords), paramRange.first, this.to.inputActivations, tempToInputIR);
			
			//ir//IndexRange tempFromOutActsIR=new IndexRange(0,0);
			this.from.out_acts(fromCoords,tempFromOutActsIR);
			Matrix.dot(this.from.outputActivations, tempFromOutActsIR, paramRange.first, this.to.inputActivations, tempToInputIR);
			
			//if(this.name.indexOf("hidden_0_3")!=-1){
			 // LOG.info("lq FullConnection after name:"+this.name+" fromCoords:"+fromCoords+" toCoords:"+toCoords+" out_actsLQ:"+this.from.outputActivations.fetchData(out_actsLQ)+" toInputLQ:"+this.to.inputActivations.fetchData(toInputLQ)+" weightsLQ:"+WeightContainer.sliceWeights(paramRange)+" second-first:"+(paramRange.second-paramRange.first));
			//}
		}else{
		     ////lq///toInputLQ=this.to.inputActivations.getView(toCoords);
			//if(this.name.indexOf("hidden_0_3")!=-1){
			//LOG.info("lq FullConnection name:"+this.name+" fromCoords is null toCoords:"+toCoords+" out_actsLQ not defined toInputLQ:"+this.to.inputActivations.fetchData(toInputLQ)+" weightsLQ:"+WeightContainer.sliceWeights(paramRange)+" second-first:"+(paramRange.second-paramRange.first));
			//}
		}
		
	}
	
	public void feed_back(Vector<Integer> toCoords){
		
		Vector<Integer> fromCoords=add_delay(toCoords);
		
		if(fromCoords!=null){
			
			//LOG.info("lq FullConnection feed_back from.outputErrors shape:"+this.from.outputErrors.shape+" data.size:"+this.from.outputErrors.data.size());
			//RangeView toInputErrorView=new RangeView(this.to.inputErrors.getView(toCoords),this.to.inputErrors);
			//toInputErrorView.setMem(this.to.inputErrors.getView(toCoords), this.to.inputErrors);
			//ir//IndexRange tempToInputErrorIR=new IndexRange(0,0);
			this.to.inputErrors.getView(toCoords,tempToInputErrorIR);
			toInputErrorView.setMem(tempToInputErrorIR, this.to.inputErrors);

			
			//RangeView fromOutputErrorView=new RangeView(this.from.out_errs(fromCoords),this.from.outputErrors);
			//fromOutputErrorView.setMem(this.from.out_errs(fromCoords), this.from.outputErrors);
			//ir//IndexRange tempFromOutErrsIR=new IndexRange(0,0);
			this.from.out_errs(fromCoords,tempFromOutErrsIR);
			fromOutputErrorView.setMem(tempFromOutErrsIR, this.from.outputErrors);
			
			//LOG.info("lq FullConnection feed_back before fromOutputErrorView name:"+this.name+" toCoords:"+toCoords+" fromCoords:"+fromCoords+" fromOutputErrorView.size:"+fromOutputErrorView.size()+" fromOutputError:"+fromOutputErrorView.getRangeData());
			//bug20//LOG.info("lq FullConnection feed_back before dot_transpose name:"+this.name+" toCoords:"+toCoords+" fromCoords:"+fromCoords+" toInputErrorStr:"+toInputErrorView.indexRange+" toInputErrors:"+toInputErrorView.getRangeData()+" fromOutputErrorStr:"+fromOutputErrorView.indexRange+" fromOutputErrors:"+fromOutputErrorView.getRangeData());
			//Matrix.dot_transpose(this.to.inputErrors, this.to.inputErrors.getView(toCoords), paramRange.first, this.from.outputErrors, this.from.out_errs(fromCoords));
			//same with above?
			//Matrix.dot_transpose(this.to.inputErrors, tempToInputErrorIR, paramRange.first, this.from.outputErrors, this.from.out_errs(fromCoords));
			Matrix.dot_transpose(this.to.inputErrors, tempToInputErrorIR, paramRange.first, this.from.outputErrors, tempFromOutErrsIR);
			
			////lq//IPVL toInputErrorView=new IPVL(this.to.inputErrors.getView(toCoords),this.to.inputErrors);
			//LOG.info("lq FullConnection feed_back toInputError name:"+this.name+" toCoords:"+toCoords+" fromCoords:"+fromCoords+" toInputErrorView.size:"+toInputErrorView.size()+" toInputErrorView:"+toInputErrorView.getRangeData());
			//bug20//LOG.info("lq FullConnection feed_back after dot_transpose name:"+this.name+" toCoords:"+toCoords+" fromCoords:"+fromCoords+" toInputErrorStr:"+toInputErrorView.indexRange+" toInputErrors:"+toInputErrorView.getRangeData()+" fromOutputErrorStr:"+fromOutputErrorView.indexRange+" fromOutputErrors:"+fromOutputErrorView.getRangeData());

		}
	}
	
	public void update_derivs(Vector<Integer> toCoords){
		
		Vector<Integer> fromCoords = add_delay(toCoords);
		
		if(fromCoords!=null){
			//Matrix.outer(this.from.outputActivations,this.from.out_acts(fromCoords),paramRange.first,this.to.inputErrors,this.to.inputErrors.getView(toCoords));
		    //ir//IndexRange tempToInputErrorIR=new IndexRange(0,0);
		    this.to.inputErrors.getView(toCoords,tempToInputErrorIR);
		    
		    //Matrix.outer(this.from.outputActivations,this.from.out_acts(fromCoords),paramRange.first,this.to.inputErrors,tempToInputErrorIR);
		    //ir//IndexRange tempFromOutActsIR=new IndexRange(0,0);
		    this.from.out_acts(fromCoords,tempFromOutActsIR);
		    Matrix.outer(this.from.outputActivations,tempFromOutActsIR,paramRange.first,this.to.inputErrors,tempToInputErrorIR);
		    
			//LOG.info("lq FullConnection update_derivs derivs name:"+this.name+" toCoords:"+toCoords+" fromCoords:"+fromCoords+" derivs.end-derivs.begin:"+(paramRange.second-paramRange.first)+" derivs:"+WeightContainer.sliceDerivs(paramRange));
		}		
	}
	
	
	
	public void set_delay(Vector<Integer> d){
		
		this.delay=d;
		//delayedCoords=ResizeUtils.resize(delayedCoords, delay.size(), 0);
		delayedCoords=ru.resize(delayedCoords, delay.size(), 0);
	}
	
	public int num_weights(){
		return paramRange.second-paramRange.first;
	}
	
	public String toString(){
		
		String desc="";
		
		desc+=this.name;
		desc+=" ("+num_weights()+" wts";
		if(source!=null){
			desc+=" shared with "+source.name;
		}
		desc+=")";
		return desc;
	}
	
	
	

}
