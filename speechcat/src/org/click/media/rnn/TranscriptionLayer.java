package org.click.media.rnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class TranscriptionLayer extends SoftmaxLayer implements NetworkOutput{

	//we add log constraint implied
	SeqBufferDouble forwardVariables=new SeqBufferDouble(0);
	
	//we add log constraint implied
	SeqBufferDouble backwardVariables=new SeqBufferDouble(0);
	//const Log<real_t> logOne(1);
	//the logVal is zero??
	public static final double logOne=0;
	int blank;
	int totalSegments;
	int totalTime;
	
	//we add log constraint implied
	Vector<Double> dEdYTerms;
	
	Vector<Integer> outputLabelSeq;
	
	boolean confusionMatrix;
	
	private RangeView outputView=new RangeView(null,null);
	private RangeView logActs=new RangeView(null,null);
	private RangeView oldFvars=new RangeView(null,null);
	private RangeView fvars=new RangeView(null,null);
	private RangeView lastFvs=new RangeView(null,null);
	private RangeView lastBvs=new RangeView(null,null);
	
	private RangeView oldLogActs=new RangeView(null,null);
	private RangeView oldBvars=new RangeView(null,null);
	private RangeView bvars=new RangeView(null,null);
	
	//private  RangeView fvars=new RangeView(null,null);
	//private RangeView bvars=new RangeView(null,null);
	private RangeView outputErrorsView=new RangeView(null,null);
	private RangeView logActivationsView=new RangeView(null,null);
	
	private List<IndexRange> tempOutputIRList=new ArrayList<>();
	private List<IndexRange> tempLogActsIRList=new ArrayList<>();
	private List<IndexRange> tempOldFvarsIRList=new ArrayList<>();
	private List<IndexRange> tempFvarsIRList=new ArrayList<>();
	
	private IndexRange tempLastFvsIR=new IndexRange(0,0);
	private IndexRange tempLastBvsIR=new IndexRange(0,0);
	
	private List<IndexRange> tempOldLogActsIRList=new ArrayList<>();
	private List<IndexRange> tempOldBvarsIRList=new ArrayList<>();
	private List<IndexRange> tempBvarsIRList=new ArrayList<>();
	
	//private List<IndexRange> tempFvarsIRList=new ArrayList<>();
	private List<IndexRange> tempBvarsIR2List=new ArrayList<>();
	private List<IndexRange> tempOutputErrorsIRList=new ArrayList<>();
	private List<IndexRange> tempLogActivationsIRList=new ArrayList<>();
	
	/**
	 * targetlabs=labs U {blank}
	 * @param name
	 * @param targetlabs
	 * @param cm
	 */
	public TranscriptionLayer(String name,Vector<String> targetlabs,boolean cm){
		super(name,1,targetlabs);
		blank=Helpers.index(targetlabs, "blank");
		
		dEdYTerms=Helpers.createDoubleVector(output_size(),0.0);
		confusionMatrix=cm;
		criteria.clear();
		criteria.add("ctcError");
		criteria.add("labelError");
		criteria.add("sequenceError");
				
	}
	
	public void start_sequence(){
		super.start_sequence();
		
		int tempSize=outputActivations.seq_size();
		for(int i=0;i<tempSize;i++){
			tempOutputIRList.add(new IndexRange(0,0));
			tempLogActsIRList.add(new IndexRange(0,0));
			tempOldFvarsIRList.add(new IndexRange(0,0));
			tempFvarsIRList.add(new IndexRange(0,0));
			
			tempOldLogActsIRList.add(new IndexRange(0,0));
			tempOldBvarsIRList.add(new IndexRange(0,0));
			tempBvarsIRList.add(new IndexRange(0,0));
			tempFvarsIRList.add(new IndexRange(0,0));//dupliate?
			tempBvarsIR2List.add(new IndexRange(0,0));
			tempOutputErrorsIRList.add(new IndexRange(0,0));
			tempLogActivationsIRList.add(new IndexRange(0,0));
			
		}
		
	}
	
	public double prior_label_prob(int label){
		return logOne;
	}
	
	public Vector<Integer> path_to_string(Vector<Integer> path){
		
		Vector<Integer> str=new Vector<>();
		int prevLabel = -1;
		
		int index=0,label=-1;
		
		for(index=0;index<path.size();index++){
			label=path.get(index);
			if(label!=blank&&(str.isEmpty()||label!=str.lastElement()||prevLabel==blank)){
				str.add(label);
			}
			prevLabel=label;
		}
		
		return str;
	}
	
	//the vector should changed to be global
	public Vector<Integer> best_label_seq(){
		
		Vector<Integer> path=new Vector<>();
		
		for(int i=0;i<outputActivations.seq_size();i++){
			//RangeView outputView=new RangeView(outputActivations.getView(i),outputActivations);
			//outputView.setMem(outputActivations.getView(i), outputActivations);
			//ir//IndexRange tempOutputIR=new IndexRange(0,0);
			IndexRange tempOutputIR=tempOutputIRList.get(i);
			outputActivations.getView(i,tempOutputIR);
			outputView.setMem(tempOutputIR, outputActivations);

			path.add(arg_max(outputView));
		}
		
		return path_to_string(path);
	}
	
	public int arg_max(RangeView outputView){
		
		int maxIndex=-1;
		double maxElement=Double.MIN_VALUE;
		
		for(int i=0;i<outputView.size();i++){
			if(outputView.getOffset(i)>maxElement){
				maxIndex=i;
				maxElement=outputView.getOffset(i);
				
			}
		}
		
		return maxIndex;
	}
	
	/**
	 * default
	 * totalSegs: -1
	 * @param time
	 * @param totalSegs
	 * @return
	 */
	public Vector<Integer> segment_range(int time,int totalSegs){
		
		if(totalSegs<0){
			totalSegs=totalSegments;
		}
		
		int start=Math.max(0, totalSegs-(2*(totalTime-time)));
		int end=Math.min(totalSegs, 2*(time+1));
		
		Vector<Integer> segRange=new Vector<>();
		
		for(int i=start;i<end;i++){
			segRange.add(i);
		}
		
		return segRange;
	}

	@Override
	public double calculate_errors(DataSequence seq) {
		
		totalTime=this.outputActivations.seq_size();
		int requiredTime=seq.targetLabelSeq.size();
		int oldLabel = -1;
		
		int label;
		for(int i=0;i<seq.targetLabelSeq.size();i++){
			label=seq.targetLabelSeq.get(i);
			if(label==oldLabel){
				++requiredTime;
			}
			oldLabel=label;
		}
		
		if(totalTime<requiredTime){
			System.err.println(" warning, seq:"+seq.tag+" has requiredTime:"+requiredTime+"> totalTime:"+totalTime);
		}
		
		totalSegments=(seq.targetLabelSeq.size() * 2) + 1;
		
		//calculate the forward variables
		Vector<Integer> totalTimeShape=new Vector<>();
		totalTimeShape.add(totalTime);
		forwardVariables.reshape_with_depth(totalTimeShape, totalSegments,0.0);
		
		//need logZero initialization of implied log seqBuffer
		for(int i=0;i<forwardVariables.data.size();i++){
			forwardVariables.data.set(i, SlappyLog.logZero);
		}
		
		
		forwardVariables.data.set(0, logActivations.data.get(blank));
			
		if(totalSegments>1){
			forwardVariables.data.set(1, logActivations.data.get(seq.targetLabelSeq.get(0)));
		}
		
		//LOG.info("lq TranscriptionLayer seq.targetLabelSeq:"+seq.targetLabelSeq);
		for(int t=1;t<totalTime;t++){
			
			//LOG.info("lq TranscriptionLayer forward t:"+t+" totalTime:"+totalTime);
			//RangeView logActs=new RangeView(logActivations.getView(t),logActivations);
			//logActs.setMem(logActivations.getView(t), logActivations);
			//ir//IndexRange tempLogActsIR=new IndexRange(0,0);
			IndexRange tempLogActsIR=tempLogActsIRList.get(t);
			logActivations.getView(t,tempLogActsIR);
			logActs.setMem(tempLogActsIR, logActivations);
			
			//LOG.info("lq TranscriptionLayer logActs t:"+t+" logActs.size:"+logActs.size()+" logActs:"+logActs.getRangeData());
			
			//RangeView oldFvars=new RangeView(forwardVariables.getView(t-1),forwardVariables);
			//oldFvars.setMem(forwardVariables.getView(t-1), forwardVariables);
			//ir//IndexRange tempOldFvarsIR=new IndexRange(0,0);
			IndexRange tempOldFvarsIR=tempOldFvarsIRList.get(t);
			forwardVariables.getView(t-1,tempOldFvarsIR);
			oldFvars.setMem(tempOldFvarsIR, forwardVariables);
			
			//LOG.info("lq TranscriptionLayer oldFvars t-1:"+(t-1)+" oldFvars.size:"+oldFvars.size()+" oldFvars:"+oldFvars.getRangeData());
			
			//RangeView fvars=new RangeView(forwardVariables.getView(t),forwardVariables);
			//fvars.setMem(forwardVariables.getView(t), forwardVariables);
			//ir//IndexRange tempFvarsIR=new IndexRange(0,0);
			IndexRange tempFvarsIR=tempFvarsIRList.get(t);
			forwardVariables.getView(t,tempFvarsIR);
			fvars.setMem(tempFvarsIR, forwardVariables);
			
			//LOG.info("lq TranscriptionLayer fvars t:"+t+" fvars.size:"+fvars.size()+" fvars:"+fvars.getRangeData());
					
			//should use global vector instead?
			Vector<Integer> segmentRange=segment_range(t,-1);
						
			int sindex=0,s;
			for(sindex=0;sindex<segmentRange.size();sindex++){
				s=segmentRange.get(sindex);
				//LOG.info("lq TranscriptionLayer segment s:"+s+" segmentRange:"+segmentRange+" totalTime:"+totalTime);
				//log implied
				double fv;
				
				//s odd (label output)
				if(s%2==1){
					
					int labelIndex=s/2;
					int labelNum=seq.targetLabelSeq.get(labelIndex);
					
					fv=SlappyLog.log_add(oldFvars.getOffset(s), oldFvars.getOffset(s-1));
					
					if(s>1&&(labelNum!=seq.targetLabelSeq.get(labelIndex-1))){
						fv=SlappyLog.log_add(fv, oldFvars.getOffset(s-2));
					}
					
					fv+=(logActs.getOffset(labelNum)+prior_label_prob(labelIndex));
					//LOG.info("lq TranscriptionLayer odd s:"+s+" t:"+t+" labelNum:"+labelNum+" logActs[labelNum]:"+logActs.getOffset(labelNum)+" labelIndex:"+labelIndex+" prior_label_prob:"+prior_label_prob(labelIndex));
					
				}else{//s even (blank output)
					
					fv=oldFvars.getOffset(s);
					
					if(s!=0){
						fv=SlappyLog.log_add(fv,oldFvars.getOffset(s-1));
						//LOG.info("lq TranscriptionLayer snzero s:"+s+" t:"+t+" getOffset(s-1):"+oldFvars.getOffset(s-1)+" getOffset(s):"+oldFvars.getOffset(s)+" fv:"+fv);
					}
					
					fv+=logActs.getOffset(blank);
					//LOG.info("lq TranscriptionLayer even s:"+s+" t:"+t+" fv:"+fv+" oldFvars[s]:"+oldFvars.getOffset(s)+" blank:"+blank+" logActs[blank]:"+logActs.getOffset(blank));
				}
				
				fvars.setOffset(s, fv);
				//LOG.info("lq TranscriptionLayer sfv s:"+s+" t:"+t+" fv:"+fv);
			}
			
			//LOG.info("lq TranscriptionLayer tfvars t:"+t+" fvars:"+fvars.getRangeData());
		}
		
		//LOG.info("lq TranscriptionLayer forwardVariables depth:"+forwardVariables.depth+" shape:"+forwardVariables.shape+" data.size:"+forwardVariables.data.size()+" data:"+forwardVariables.data);
		
		//log implied
		//RangeView lastFvs=new RangeView(forwardVariables.getView(totalTime-1),forwardVariables);
		//lastFvs.setMem(forwardVariables.getView(totalTime-1), forwardVariables);
		//ir//IndexRange tempLastFvsIR=new IndexRange(0,0);
		
		forwardVariables.getView(totalTime-1,tempLastFvsIR);
		lastFvs.setMem(tempLastFvsIR, forwardVariables);
		
		//log implied
		double logProb=lastFvs.back();
		
		if(totalSegments>1){
			//logProb+=lastFvs.nth_last(2);
			logProb=SlappyLog.log_add(logProb, lastFvs.nth_last(2));
		}
		
		//calculate the backward variables
		Vector<Integer> backTotalTimeShape=new Vector<>();
		backTotalTimeShape.add(totalTime);
		backwardVariables.reshape_with_depth(backTotalTimeShape, totalSegments, SlappyLog.logZero);
		
		//LOG.info("lq TranscriptionLayer init backwardVariables depth:"+backwardVariables.depth+" shape:"+backwardVariables.shape+" data.size:"+backwardVariables.data.size()+" data:"+backwardVariables.data);
		
		//RangeView lastBvs=new RangeView(backwardVariables.getView(totalTime-1),backwardVariables);
		//lastBvs.setMem(backwardVariables.getView(totalTime-1), backwardVariables);
		//ir//IndexRange tempLastBvsIR=new IndexRange(0,0);
		backwardVariables.getView(totalTime-1,tempLastBvsIR);
		lastBvs.setMem(tempLastBvsIR, backwardVariables);
		
		//	lastBvs.back() = 1;
		lastBvs.setBack(0.0);
		
		if(totalSegments>1){
			//nth_last(lastBvs, 2) = 1;
			lastBvs.set_nth_last(2, 0.0);
		}
		
		//LOOP over time, calculating backward variables recursively
		//LOG.info("lq TranscriptionLayer lastBvs:"+lastBvs.getRangeData());
		for(int t=totalTime - 2;t>=0;t--){
			//LOG.info("lq TranscriptionLayer backward t:"+t+" totalTime:"+(totalTime-1));
			//RangeView oldLogActs=new RangeView(this.logActivations.getView(t+1),this.logActivations);
			//oldLogActs.setMem(this.logActivations.getView(t+1), this.logActivations);
			//ir//IndexRange tempOldLogActsIR=new IndexRange(0,0);
			IndexRange tempOldLogActsIR=tempOldLogActsIRList.get(t);
			this.logActivations.getView(t+1,tempOldLogActsIR);
			oldLogActs.setMem(tempOldLogActsIR, this.logActivations);
			
			//LOG.info("lq TranscriptionLayer oldLogActs t+1:"+(t+1)+" oldLogActs.size:"+oldLogActs.size()+" oldLogActs:"+oldLogActs.getRangeData());			
			
			//RangeView oldBvars=new RangeView(this.backwardVariables.getView(t+1),this.backwardVariables);
			//oldBvars.setMem(this.backwardVariables.getView(t+1), this.backwardVariables);
			//ir//IndexRange tempOldBvarsIR=new IndexRange(0,0);
			IndexRange tempOldBvarsIR=tempOldBvarsIRList.get(t);
			this.backwardVariables.getView(t+1,tempOldBvarsIR);
			oldBvars.setMem(tempOldBvarsIR, this.backwardVariables);

			//LOG.info("lq TranscriptionLayer oldBvars t+1:"+(t+1)+" oldBvars.size:"+oldBvars.size()+" oldBvars:"+oldBvars.getRangeData());
			
			//RangeView bvars=new RangeView(this.backwardVariables.getView(t),this.backwardVariables);
			//bvars.setMem(this.backwardVariables.getView(t), this.backwardVariables);
			//ir//IndexRange tempBvarsIR=new IndexRange(0,0);
			IndexRange tempBvarsIR=tempBvarsIRList.get(t);
			this.backwardVariables.getView(t,tempBvarsIR);
			bvars.setMem(tempBvarsIR, this.backwardVariables);
	
			//LOG.info("lq TranscriptionLayer bvars t:"+t+" bvars.size:"+bvars.size()+" bvars:"+bvars.getRangeData());
			
	        Vector<Integer> segmentRange=segment_range(t,-1);
				
			int sindex=0,s;
			for(sindex=0;sindex<segmentRange.size();sindex++){
			
				s=segmentRange.get(sindex);
				//LOG.info("lq TranscriptionLayer backward segment s:"+s+" t:"+t+" segment_range:"+segmentRange+" totalTime-1:"+(totalTime-1));
				
				//log implied
				double bv;
				
				//s odd (label output)
				if(s%2==1){
					
					int labelIndex = s/2;
					int labelNum = seq.targetLabelSeq.get(labelIndex);
					bv=SlappyLog.log_add(oldBvars.getOffset(s)+oldLogActs.getOffset(labelNum)+prior_label_prob(labelIndex), oldBvars.getOffset(s+1)+oldLogActs.getOffset(blank));
					
					if(s<(totalSegments-2)){
						int nextLabelNum=seq.targetLabelSeq.get(labelIndex+1);
						
						if(labelNum!=nextLabelNum){
							bv=SlappyLog.log_add(bv, oldBvars.getOffset(s+2)+oldLogActs.getOffset(nextLabelNum)+prior_label_prob(labelIndex + 1));
						}
						
					}
					
					//LOG.info("lq TranscriptionLayer backward odd s:"+s+" t:"+t+" labelNum:"+labelNum+" labelIndex:"+labelIndex+" oldBvars[s]:"+oldBvars.getOffset(s)+" oldLogActs[labelNum]:"+oldLogActs.getOffset(labelNum)+" oldBvars[s + 1]:"+oldBvars.getOffset(s+1)+" oldLogActs[blank]:"+oldLogActs.getOffset(blank));
						
				}else{//s even (blank output)
					
					bv=oldBvars.getOffset(s)+oldLogActs.getOffset(blank);
					if(s<(totalSegments-1)){
						bv=SlappyLog.log_add(bv, oldBvars.getOffset(s+1)+oldLogActs.getOffset(seq.targetLabelSeq.get(s/2))+prior_label_prob(s/2));
					}			
				}
				
				bvars.setOffset(s, bv);
				
			}			
		}
	
		
		LOG.info("lq TranscriptionLayer logProb:"+logProb);
		double oval=0;
		for(int time=0;time<totalTime;time++){
			
		     for(int i=0;i<dEdYTerms.size();i++){
		    	 dEdYTerms.set(i,SlappyLog.logZero);
		     }
			
		     //RangeView fvars=new RangeView(forwardVariables.getView(time),forwardVariables);
		     //fvars.setMem(forwardVariables.getView(time), forwardVariables);
		     //ir//IndexRange tempFvarsIR=new IndexRange(0,0);
		     IndexRange tempFvarsIR=tempFvarsIRList.get(time);
		     forwardVariables.getView(time,tempFvarsIR);
		     fvars.setMem(tempFvarsIR, forwardVariables);

		     //RangeView bvars=new RangeView(backwardVariables.getView(time),backwardVariables);
		    // bvars.setMem(backwardVariables.getView(time), backwardVariables);
		     //ir//IndexRange tempBvarsIR2=new IndexRange(0,0);
		     IndexRange tempBvarsIR2=tempBvarsIR2List.get(time);
		     backwardVariables.getView(time,tempBvarsIR2);
		     bvars.setMem(tempBvarsIR2, backwardVariables);

		     
		     for(int s=0;s<totalSegments;s++){
		    	//k = blank for even s, target label for odd s
		    	int k;
		    	if(s%2==1){
		    	   k=seq.targetLabelSeq.get(s/2);	
		    	}else{
		    		k=blank;
		    	}
		    	 
		    	oval=dEdYTerms.get(k);
		    	dEdYTerms.set(k, SlappyLog.log_add(oval,fvars.getOffset(s)+bvars.getOffset(s)));
		    	//bug20//if(s/2<seq.targetLabelSeq.size()){
		    	//bug20//   LOG.info("lq TranscriptionLayer  dEdYTerms time:"+time+" s:"+s+" k:"+k+" blank:"+blank+" totalTime:"+totalTime+" s/2:"+s/2+" seq.targetLabelSeq:"+seq.targetLabelSeq.get(s/2)+" fvars[s]:"+fvars.getOffset(s)+" bvars[s]:"+bvars.getOffset(s)+" oval:"+oval+" dEdYTerms[k]:"+dEdYTerms.get(k));
		    	//bug20//}else{
		    	//bug20//	LOG.info("lq TranscriptionLayer  dEdYTerms time:"+time+" s:"+s+" k:"+k+" blank:"+blank+" totalTime:"+totalTime+" s/2:"+s/2+" fvars[s]:"+fvars.getOffset(s)+" bvars[s]:"+bvars.getOffset(s)+" oval:"+oval+" dEdYTerms[k]:"+dEdYTerms.get(k));

		    	//bug20//}
		     }
		     
		    // RangeView outputErrorsView=new RangeView(this.outputErrors.getView(time),this.outputErrors);
		     //outputErrorsView.setMem(this.outputErrors.getView(time), this.outputErrors);
		     //ir//IndexRange tempOutputErrorsIR=new IndexRange(0,0);
		     IndexRange tempOutputErrorsIR=tempOutputErrorsIRList.get(time);
		     this.outputErrors.getView(time,tempOutputErrorsIR);
		     outputErrorsView.setMem(tempOutputErrorsIR, this.outputErrors);

		     // LOG.info("lq TranscriptionLayer outputErrorsView before:"+outputErrorsView.getRangeData()+" outputErrorsView.size:"+outputErrorsView.size()+" time:"+time+" totalTime:"+totalTime);
		    	
		     //RangeView logActivationsView=new RangeView(this.logActivations.getView(time),this.logActivations);
		    // logActivationsView.setMem(this.logActivations.getView(time), this.logActivations);
		     //ir//IndexRange tempLogActivationsIR=new IndexRange(0,0);
		     IndexRange tempLogActivationsIR=tempLogActivationsIRList.get(time);
		     this.logActivations.getView(time,tempLogActivationsIR);
		     logActivationsView.setMem(tempLogActivationsIR, this.logActivations);

		     
		     //LOG.info("lq TranscriptionLayer logActivationsView:"+logActivationsView.getRangeData()+" logActivationsView.size:"+logActivationsView.size()+" time:"+time+" totalTime:"+totalTime);
		    	
		     //LOG.info("lq TranscriptionLayer dEdYTerms:"+dEdYTerms+" dEdYTerms.size:"+dEdYTerms.size()+" time:"+time);
		    	
		     for(int t=0;t<dEdYTerms.size();t++){
		    	 
		    	outputErrorsView.setOffset(t, -Math.exp(dEdYTerms.get(t)-(logProb+logActivationsView.getOffset(t))));
		     }
		     
		    //bug20// LOG.info("lq TranscriptionLayer step time:"+time+" outputErrorLQ:"+outputErrorsView.getRangeData()+" logActivationsLQ:"+logActivationsView.getRangeData()+" dEdYTerms:"+dEdYTerms);
		     
		   //  LOG.info("lq TranscriptionLayer outputErrorsView after:"+outputErrorsView.getRangeData()+" outputErrorsView.size:"+outputErrorsView.size()+" time:"+time+" totalTime:"+totalTime);

		}
		
		
		//LOG.info("lq TranscriptionLayer finish backwardVariables depth:"+backwardVariables.depth+" shape:"+backwardVariables.shape+" data.size:"+backwardVariables.data.size()+" data:"+backwardVariables.data);
	
		//calculate the aligment errors
		outputLabelSeq=best_label_seq();
		LOG.info("lq TranscriptionLayer alignment outputLabelSeq:"+outputLabelSeq+" seq.targetLabelSeq:"+seq.targetLabelSeq);
		
		//the new method here consume lot of memory?
		StringAlignment alignment=new StringAlignment(seq.targetLabelSeq,outputLabelSeq,false,true,1,1,1);
		double labelError = alignment.distance;
		double substitutions = alignment.substitutions;
		double deletions = alignment.deletions;
		double insertions = alignment.insertions;
		
		LOG.info("lq TranscriptionLayer after alignment labelError:"+labelError+" substitutions:"+substitutions+" deletions:"+deletions+" insertions:"+insertions);
		
		double seqError=labelError!=0?1:0;
		double ctcError=-logProb;
		
		//store errors in map
		int normFactor = seq.targetLabelSeq.size();
		normFactors.put("labelError", (double)normFactor);
		normFactors.put("substitutions", (double)normFactor);
		normFactors.put("deletions", (double)normFactor);
		normFactors.put("insertions", (double)normFactor);
		
		errorMap.clear();
		errorMap.put("labelError", labelError);
		errorMap.put("seqError", seqError);
		errorMap.put("substitutions", substitutions);
		errorMap.put("deletions", deletions);
		errorMap.put("insertions", insertions);
		errorMap.put("ctcError", ctcError);
		
		//ignore temporarily
		//if (confusionMatrix)
		
		return ctcError;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> getErrorMap() {
		return errorMap;
	}

	@Override
	public Map<String, Double> getNormFactors() {
		return normFactors;
	}
	
	
	
	
	
}
