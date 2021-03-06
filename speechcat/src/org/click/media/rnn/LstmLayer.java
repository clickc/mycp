package org.click.media.rnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.click.media.rnn.activationFunctions.Func;

public class LstmLayer<CI extends Func,CO extends Func,G extends Func> extends Layer{

	//activation functions
	CI ci;
	CO co;
	G g;
	
	//data
	int numBlocks; 
	int cellsPerBlock;
	int numCells;
	int gatesPerBlock;
	int unitsPerBlock;
	int peepsPerBlock;
	SeqBufferDouble inGateActs;
	SeqBufferDouble forgetGateActs;
	SeqBufferDouble outGateActs;
	SeqBufferDouble preOutGateActs;
	SeqBufferDouble states;
	SeqBufferDouble preGateStates;
	SeqBufferDouble cellErrors;
	Vector<Vector<Integer>> stateDelays;
	Vector<Integer> delayedCoords;
	
	Vector<IndexRange> oldStates;
	Vector<IndexRange> nextErrors;
	Vector<IndexRange> nextFgActs;
	Vector<IndexRange> nextCellErrors;
	
	LstmLayer<CI,CO,G> peepSource;
	Pair peepRange;
	
	static Logger LOG =  Logger.getLogger(LstmLayer.class.getName()); 

	private RangeView actBegin=new RangeView(null,null);
	private RangeView inActIt=new RangeView(null,null);
	private RangeView inGateActBegin=new RangeView(null,null);
	private RangeView fgActBegin=new RangeView(null,null);
	private RangeView outGateActBegin=new RangeView(null,null);
	private RangeView stateBegin=new RangeView(null,null);
	private RangeView preGateStateBegin=new RangeView(null,null);
	private RangeView preOutGateActBegin=new RangeView(null,null);
	private RangeView fgActEnd=new RangeView(null,null);
	private RangeView fgActs=new RangeView(null,null);
	
	//private RangeView inGateActBegin=new RangeView(null,null);
	private RangeView forgetGateActBegin=new RangeView(null,null);
	//private RangeView outGateActBegin=new RangeView(null,null);
	//private RangeView preGateStateBegin=new RangeView(null,null);
	//private RangeView preOutGateActBegin=new RangeView(null,null);
	private RangeView inErrs=new RangeView(null,null);
	private RangeView cellErrorBegin=new RangeView(null,null);
	private RangeView outputErrorBegin=new RangeView(null,null);
	private RangeView nextErrs=new RangeView(null,null);
	private RangeView nextFgActsD=new RangeView(null,null);
	private RangeView nextCellErrorsD=new RangeView(null,null);
	private RangeView os=new RangeView(null,null);
	private RangeView statesViewLQ=new RangeView(null,null);
	//private RangeView stateBegin=new RangeView(null,null);
	private RangeView errorBegin=new RangeView(null,null);
	//private RangeView os=new RangeView(null,null);
	
	private IndexRange indexRange1=new IndexRange(0,0);
	private IndexRange indexRange2=new IndexRange(0,0);
	private IndexRange indexRange3=new IndexRange(0,0);
	private IndexRange oldfgActEndindexRange=new IndexRange(0,0);
	
	private IndexRange tempActBeginIR=new IndexRange(0,0);
	private IndexRange tempInActItIR=new IndexRange(0,0);
	private IndexRange tempInGateActBeginIR=new IndexRange(0,0);
    private IndexRange tempFgActBeginIR=new IndexRange(0,0);	
	private IndexRange tempOutGateActBeginIR=new IndexRange(0,0);
	private IndexRange tempStateBeginIR=new IndexRange(0,0);
    private IndexRange tempPreGateStateBeginIR=new IndexRange(0,0);
    private IndexRange tempPreOutGateActBeginIR=new IndexRange(0,0);
    
    private List<IndexRange> numSeqDimsIRs=new ArrayList<>();
    //private IndexRange tempStatesIR;
    
    //private IndexRange tempInGateActBeginIR=new IndexRange(0,0);
    private IndexRange tempForgetGateActBeginIR=new IndexRange(0,0);
    //private IndexRange tempOutGateActBeginIR=new IndexRange(0,0);
   // private IndexRange tempPreGateStateBeginIR=new IndexRange(0,0);
   // private IndexRange tempPreOutGateActBeginIR=new IndexRange(0,0);
    private IndexRange tempInErrsIR=new IndexRange(0,0);
    private IndexRange tempCellErrorBeginIR=new IndexRange(0,0);
    private IndexRange tempOutputErrorBeginIR=new IndexRange(0,0);
    
    private List<IndexRange> tempOldStatesIRList=new ArrayList<>();
    private List<IndexRange> tempNextErrorsIRList=new ArrayList<>();   
    private List<IndexRange> tempNextFgActsIRList=new ArrayList<>();    
    private List<IndexRange> tempNextCellErrorsIRList=new ArrayList<>();
    
    private IndexRange tempStatesViewLQIR=new IndexRange(0,0);
    //private IndexRange tempStateBeginIR=new IndexRange(0,0);
    private IndexRange tempErrorBeginIR=new IndexRange(0,0);
    
    private List<IndexRange> tempStatesIRList=new ArrayList<>();
    
	private ResizeUtils ru=new ResizeUtils();

    
    
	public LstmLayer(String name, int numSeqDims, int inputSize, int outputSize, Layer src,CI ci,CO co,G g) {
		super(name, numSeqDims, inputSize, outputSize, src);
		this.ci=ci;
		this.co=co;
		this.g=g;
		// TODO Auto-generated constructor stub
	}
	
	public LstmLayer(String name,Vector<Integer> directions,int nb,int cpb,LstmLayer<CI,CO,G> ps,CI ci,CO co,G g){
		super(name,directions,(cpb + directions.size() + 2) * nb, nb,null);
		//cpb:numbler of cells nb:numbler of blocks  cpb + directions.size() + 2:cpb+directions.size+forget gate input gate number of weights to initialize
		System.out.println("LstmLayer name:"+name+" directions:"+directions+" nb:"+nb+" cpb:"+cpb+" (cpb + directions.size() + 2) * nb:"+(cpb + directions.size() + 2) * nb);
		this.ci=ci;
		this.co=co;
		this.g=g;
		
		
		this.numBlocks=nb;
		this.cellsPerBlock=cpb;
		this.numCells=numBlocks * cellsPerBlock;
		this.gatesPerBlock=this.num_seq_dims() + 2;
		this.unitsPerBlock=gatesPerBlock+cellsPerBlock;
		this.peepsPerBlock=gatesPerBlock * cellsPerBlock;
		this.inGateActs=new SeqBufferDouble(this.numBlocks);
		this.forgetGateActs=new SeqBufferDouble(this.numBlocks * this.num_seq_dims());
		this.outGateActs=new SeqBufferDouble(this.numBlocks);
		this.preOutGateActs=new SeqBufferDouble(this.numCells);
		this.states=new SeqBufferDouble(this.numCells);
		this.preGateStates=new SeqBufferDouble(this.numCells);
		this.cellErrors=new SeqBufferDouble(this.numCells);
		
		//stateDelays(this->num_seq_dims()) should be as follows?
		this.stateDelays=Helpers.createEmbedVector(this.num_seq_dims());
		
		this.delayedCoords=Helpers.createVector(this.num_seq_dims(), 0);
		this.oldStates=Helpers.createIndexRanges(this.num_seq_dims());
		this.nextErrors=Helpers.createIndexRanges(this.num_seq_dims());
		this.nextFgActs=Helpers.createIndexRanges(this.num_seq_dims());
		this.nextCellErrors=Helpers.createIndexRanges(this.num_seq_dims());
		this.peepSource=ps;
		this.peepRange=(this.peepSource!=null)?this.peepSource.peepRange:WeightContainer.new_parameters(peepsPerBlock*numBlocks, this.name, this.name, this.name + "_peepholes");
		
		//why always null
		if(peepSource!=null){
			WeightContainer.link_layers(name, name, name + "_peepholes", peepRange.first, peepRange.second);
		}
		
		for(int i=0;i<this.num_seq_dims();i++){
			//right?
			//stateDelays.set(i, ResizeUtils.resize(stateDelays.get(i), this.num_seq_dims(), 0));
			stateDelays.set(i, ru.resize(stateDelays.get(i), this.num_seq_dims(), 0));
			stateDelays.get(i).set(i, -directions.get(i));
		}
		
		
	}
	
	public void start_sequence(){
	    super.start_sequence();
	    inGateActs.reshape(this.output_seq_shape());
	    forgetGateActs.reshape(this.output_seq_shape());
        outGateActs.reshape(this.output_seq_shape());
        preOutGateActs.reshape(this.output_seq_shape());
        states.reshape(this.output_seq_shape());
        preGateStates.reshape(this.output_seq_shape());
        cellErrors.reshape(states);
        
        for(int d=0;d<this.num_seq_dims();d++){
        	numSeqDimsIRs.add(new IndexRange(0,0));
        	tempOldStatesIRList.add(new IndexRange(0,0));
        	tempNextErrorsIRList.add(new IndexRange(0,0));
        	tempNextFgActsIRList.add(new IndexRange(0,0));
        	tempNextCellErrorsIRList.add(new IndexRange(0,0));
        	
        	tempStatesIRList.add(new IndexRange(0,0));
        }
	}


	public void feed_forward(Vector<Integer> coords){
		
		//RangeView actBegin=new RangeView(this.outputActivations.getView(coords),this.outputActivations);
		//actBegin.setMem(this.outputActivations.getView(coords), this.outputActivations);
	    //ir//IndexRange tempActBeginIR=new IndexRange(0,0);
	    this.outputActivations.getView(coords,tempActBeginIR);
	    actBegin.setMem(tempActBeginIR, this.outputActivations);
	    
		//LOG.info("lq LstmLayer coords:"+coords+" actLQ.end-actLQ.begin:"+(actBegin.end()-actBegin.start()));
		
		//RangeView inActIt=new RangeView(this.inputActivations.getView(coords),this.inputActivations);
		//inActIt.setMem(this.inputActivations.getView(coords), this.inputActivations);
		//ir//IndexRange tempInActItIR=new IndexRange(0,0);
		this.inputActivations.getView(coords,tempInActItIR);
		inActIt.setMem(tempInActItIR, this.inputActivations);
		
		//LOG.info("lq LstmLayer coords:"+coords+" inActItLQ.end-inActItLQ.begin:"+(inActIt.end()-inActIt.start())+" inActItLQ:"+inActIt.getRangeData());
		
		//LOG.info("lq LstmLayer coords:"+coords+" this.inGateActs:"+this.inGateActs.trackDes());
		//RangeView inGateActBegin=new RangeView(this.inGateActs.getView(coords),this.inGateActs);
		//inGateActBegin.setMem(this.inGateActs.getView(coords), this.inGateActs);
	    //ir//IndexRange tempInGateActBeginIR=new IndexRange(0,0);
	    this.inGateActs.getView(coords,tempInGateActBeginIR);
	    inGateActBegin.setMem(tempInGateActBeginIR, this.inGateActs);
	    
		//LOG.info("lq LstmLayer coords:"+coords+" inGateActs.end-inGateActs.begin:"+(inGateActBegin.end()-inGateActBegin.start()));
		
		//RangeView fgActBegin=new RangeView(this.forgetGateActs.getView(coords),this.forgetGateActs);
		//fgActBegin.setMem(this.forgetGateActs.getView(coords), this.forgetGateActs);
		//ir//IndexRange tempFgActBeginIR=new IndexRange(0,0);
		this.forgetGateActs.getView(coords,tempFgActBeginIR);
		fgActBegin.setMem(tempFgActBeginIR, this.forgetGateActs);
		
		//LOG.info("lq LstmLayer coords:"+coords+" fgActLQ.end-fgActLQ.begin:"+(fgActBegin.end()-fgActBegin.start()));
		
		//RangeView outGateActBegin=new RangeView(this.outGateActs.getView(coords),this.outGateActs);
		//outGateActBegin.setMem(this.outGateActs.getView(coords), this.outGateActs);
		//ir//IndexRange tempOutGateActBeginIR=new IndexRange(0,0);
		this.outGateActs.getView(coords,tempOutGateActBeginIR);
		outGateActBegin.setMem(tempOutGateActBeginIR, this.outGateActs);
		
		//LOG.info("lq LstmLayer coords:"+coords+" outGateActs.end-outGateActs.begin:"+(outGateActBegin.end()-outGateActBegin.start()));
		
		//RangeView stateBegin=new RangeView(this.states.getView(coords),this.states);
		//stateBegin.setMem(this.states.getView(coords), this.states);
		//ir//IndexRange tempStateBeginIR=new IndexRange(0,0);
		this.states.getView(coords,tempStateBeginIR);
		stateBegin.setMem(tempStateBeginIR, this.states);
		
		//LOG.info("lq LstmLayer coords:"+coords+" state.end-state.begin:"+(stateBegin.end()-stateBegin.start()));
		
		//RangeView preGateStateBegin=new RangeView(this.preGateStates.getView(coords),this.preGateStates);
		//preGateStateBegin.setMem(this.preGateStates.getView(coords), this.preGateStates);
		//ir//IndexRange tempPreGateStateBeginIR=new IndexRange(0,0);
		this.preGateStates.getView(coords,tempPreGateStateBeginIR);
		preGateStateBegin.setMem(tempPreGateStateBeginIR, this.preGateStates);
		
		//LOG.info("lq LstmLayer coords:"+coords+" preGateState.end-preGateState.begin:"+(preGateStateBegin.end()-preGateStateBegin.start()));
		
		//RangeView preOutGateActBegin=new RangeView(this.preOutGateActs.getView(coords),this.preOutGateActs);
		//preOutGateActBegin.setMem(this.preOutGateActs.getView(coords),this.preOutGateActs);
		//ir//IndexRange tempPreOutGateActBeginIR=new IndexRange(0,0);
		this.preOutGateActs.getView(coords,tempPreOutGateActBeginIR);
		preOutGateActBegin.setMem(tempPreOutGateActBeginIR,this.preOutGateActs);
		
		//LOG.info("lq LstmLayer coords:"+coords+" preOutGateAct.end-preOutGateAct.begin:"+(preOutGateActBegin.end()-preOutGateActBegin.start()));
		
		for(int d=0;d<this.num_seq_dims();d++){
			Helpers.range_plus(delayedCoords, coords, stateDelays.get(d));
		
			//oldStates.set(d, states.at(delayedCoords));
			//ir//IndexRange tempStatesIR=new IndexRange(0,0);
			IndexRange tempStatesIR=numSeqDimsIRs.get(d);
			states.at(delayedCoords,tempStatesIR);
			oldStates.set(d, tempStatesIR);
		}
		
		Pair peepWtIt=peepRange;
		int peepWtItIndex=0;
		
		int cellStart=0;
		int cellEnd=cellsPerBlock;
		
		//fgActLQ.end-fgActLQ.begin:4, for every block we use [fgActRange.start,fgActEnd)
		//int fgActEnd=fgActBegin.start()+this.num_seq_dims();
		//RangeView fgActEnd=new RangeView(new IndexRange(fgActBegin.start()+this.num_seq_dims(),fgActBegin.end()),this.forgetGateActs);
		
		
		//fgActEnd.setMem(new IndexRange(fgActBegin.start()+this.num_seq_dims(),fgActBegin.end()), this.forgetGateActs);
		indexRange1.setRange(fgActBegin.start()+this.num_seq_dims(),fgActBegin.end());
		fgActEnd.setMem(indexRange1, this.forgetGateActs);
		
		int oldfgActStart=fgActEnd.start();
		int oldfgActEnd=fgActEnd.end();
		
		int inActItIndex=0;
		IndexRange os;
		
		for(int b=0;b<numBlocks;b++){
			//RangeView fgActs=new RangeView(new IndexRange(fgActBegin.start(),fgActEnd.start()),this.forgetGateActs);
			
			//fgActs.setMem(new IndexRange(fgActBegin.start(),fgActEnd.start()), this.forgetGateActs);
			indexRange2.setRange(fgActBegin.start(),fgActEnd.start());
			fgActs.setMem(indexRange2, this.forgetGateActs);
			
			for(int oi=0;oi<oldStates.size();oi++){
				os=oldStates.get(oi);
				//weights as input :too big?
				//LOG.info("lq LstmLayer before b:"+b+" cellStart:"+cellStart+" cellEnd:"+cellEnd+" os:"+states.fetchData(new IndexRange(os.start+cellStart,os.start+cellEnd))+" peepWtIt:"+WeightContainer.weights[peepWtIt.first]+" (peepWtIt+1):"+WeightContainer.weights[peepWtIt.first+1]+" inActIt:"+inActIt.getOffset(inActItIndex));
				if(os.begin()){
				   Matrix.dot(states, os.start+cellStart, os.start+cellEnd, WeightContainer.weights, peepWtIt.first+peepWtItIndex, inActIt.seqBuffer, inActIt.start()+inActItIndex, inActIt.start()+inActItIndex+1);
				}
				//LOG.info("lq LstmLayer after b:"+b+" cellStart:"+cellStart+" cellEnd:"+cellEnd+" os:"+states.fetchData(new IndexRange(os.start+cellStart,os.start+cellEnd))+" peepWtIt:"+WeightContainer.weights[peepWtIt.first]+" (peepWtIt+1):"+WeightContainer.weights[peepWtIt.first+1]+" inActIt:"+inActIt.getOffset(inActItIndex));

			}
			
			peepWtItIndex+=cellsPerBlock;
			 //forma (4.3)
			//LOG.info("lq LstmLayer inGateAct coords:"+coords+" b:"+b+" inActIt:"+inActIt.getOffset(inActItIndex));
			double inGateAct=this.g.fn(inActIt.getOffset(inActItIndex));	
			//LOG.info("lq LstmLayer inGateAct coords:"+coords+" b:"+b+" inGateAct:"+inGateAct);
			inGateActBegin.setOffset(b, inGateAct);		
			++inActItIndex;
			
			//forget gates
			//extra inputs from peepholes (from old states)	
			for(int d=0;d<this.num_seq_dims();d++){
				os=oldStates.get(d);
				if(os.begin())
				{
					 Matrix.dot(states,os.start+cellStart, os.start+cellEnd, WeightContainer.weights, peepWtIt.first+peepWtItIndex, inActIt.seqBuffer, inActIt.start()+inActItIndex, inActIt.start()+inActItIndex+1);
				}
				peepWtItIndex+= cellsPerBlock;
				fgActs.setOffset(d, this.g.fn(inActIt.getOffset(inActItIndex)));
				//LOG.info("lq LstmLayer forget gates d:"+d+" fgActs:"+fgActs.getOffset(d));
				++inActItIndex;
			}
			
			//pre-gate cell states
			//preGateStateBegin g(a(c,P))
			Matrix.transform(inActIt.seqBuffer, inActIt.start()+inActItIndex, inActIt.start()+inActItIndex+cellsPerBlock, preGateStateBegin.seqBuffer,preGateStateBegin.start()+cellStart, this.ci);
			inActItIndex+=cellsPerBlock;
			
			//cell states
			//stateBegin is local variable relative to every block?
			//the state vector is composed of different outputs of different blocks
			//actually for each block, we only need one element of state or preOutGateAct,why we 
			//calculate the whole vector?
			for(int c=cellStart;c<cellEnd;c++){
				
				double state=inGateAct*preGateStateBegin.getOffset(c);
				//LOG.info("lq LstmLayer cell states c:"+c+" state:"+state+" inGateAct:"+inGateAct+" preGateStateBegin:"+preGateStateBegin.getOffset(c));
				
				for(int d=0;d<this.num_seq_dims();d++){
					os=oldStates.get(d);
					if(os.begin()){
						state+=fgActs.getOffset(d)*states.valueOf(os.start+c);
					}
				}
				
				stateBegin.setOffset(c, state);
				preOutGateActBegin.setOffset(c, this.co.fn(state));			
			}
			
			//output gate
			//extra input from peephole (from current state)
			Matrix.dot(stateBegin.seqBuffer, stateBegin.start()+cellStart, stateBegin.start()+cellEnd, WeightContainer.weights, peepWtIt.first+peepWtItIndex, inActIt.seqBuffer, inActIt.start()+inActItIndex, inActIt.start()+inActItIndex+1);			
			peepWtItIndex+=cellsPerBlock;
			
			double outGateAct=this.g.fn(inActIt.getOffset(inActItIndex));
			outGateActBegin.setOffset(b, outGateAct);
			inActItIndex++;
			
			//output activations
			Matrix.transformMultiply(preOutGateActBegin.seqBuffer, preOutGateActBegin.start()+cellStart, preOutGateActBegin.start()+cellEnd, actBegin.seqBuffer, actBegin.start()+cellStart, outGateAct);
			cellStart = cellEnd;
			cellEnd += cellsPerBlock;
			
			//fgActLQ.end-fgActLQ.begin:4
			////fgActBegin = fgActEnd;
			//fgActBegin.setMem(fgActEnd.getIndexRange(), fgActEnd.getSeqBuffer());
			oldfgActEndindexRange.setRange(fgActEnd.getIndexRange().start, fgActEnd.getIndexRange().end);
			fgActBegin.setMem(oldfgActEndindexRange, fgActEnd.getSeqBuffer());
			oldfgActStart=fgActEnd.start();
			
			////fgActEnd=new RangeView(new IndexRange(oldfgActStart+this.num_seq_dims(),oldfgActEnd),this.forgetGateActs);
		    
			//fgActEnd.setMem(new IndexRange(oldfgActStart+this.num_seq_dims(),oldfgActEnd), this.forgetGateActs);
			indexRange3.setRange(oldfgActStart+this.num_seq_dims(),oldfgActEnd);
			fgActEnd.setMem(indexRange3, this.forgetGateActs);
			
		}
		
		//LOG.info("lq LstmLayer feed_forward name:"+this.name+" output coords:"+coords+" actBegin.end-actBegin.begin:"+(actBegin.end()-actBegin.start())+" actBegin:"+actBegin.getRangeData());
	}
	
	
	public void feed_back(Vector<Integer> coords){
		
		
		//activations
		//RangeView inGateActBegin=new RangeView(inGateActs.getView(coords),inGateActs);
		//inGateActBegin.setMem(inGateActs.getView(coords), inGateActs);
		//ir//IndexRange tempInGateActBeginIR=new IndexRange(0,0);
		inGateActs.getView(coords,tempInGateActBeginIR);
		inGateActBegin.setMem(tempInGateActBeginIR, inGateActs);
		
		//RangeView forgetGateActBegin=new RangeView(forgetGateActs.getView(coords),forgetGateActs);
		//forgetGateActBegin.setMem(forgetGateActs.getView(coords), forgetGateActs);
		//ir//IndexRange tempForgetGateActBeginIR=new IndexRange(0,0);
		forgetGateActs.getView(coords,tempForgetGateActBeginIR);
		forgetGateActBegin.setMem(tempForgetGateActBeginIR, forgetGateActs);
		
		//RangeView outGateActBegin=new RangeView(outGateActs.getView(coords),outGateActs);
		//outGateActBegin.setMem(outGateActs.getView(coords), outGateActs);
		//ir//IndexRange tempOutGateActBeginIR=new IndexRange(0,0);
		outGateActs.getView(coords,tempOutGateActBeginIR);
		outGateActBegin.setMem(tempOutGateActBeginIR, outGateActs);
		
		//RangeView preGateStateBegin=new RangeView(preGateStates.getView(coords),preGateStates);
		//preGateStateBegin.setMem(preGateStates.getView(coords), preGateStates);
		//ir//IndexRange tempPreGateStateBeginIR=new IndexRange(0,0);
		preGateStates.getView(coords,tempPreGateStateBeginIR);
		preGateStateBegin.setMem(tempPreGateStateBeginIR, preGateStates);
		
		//RangeView preOutGateActBegin=new RangeView(preOutGateActs.getView(coords),preOutGateActs);
		//preOutGateActBegin.setMem(preOutGateActs.getView(coords), preOutGateActs);
		//ir//IndexRange tempPreOutGateActBeginIR=new IndexRange(0,0);
		preOutGateActs.getView(coords,tempPreOutGateActBeginIR);
		preOutGateActBegin.setMem(tempPreOutGateActBeginIR, preOutGateActs);
		
		//bug20//LOG.info("lq LstmLayer feed_back inputViews coords:"+coords+" inGateActStr:"+inGateActBegin.indexRange+" inGateActLQ:"+inGateActBegin.getRangeData());
		//bug20//LOG.info("lq LstmLayer feed_back inputViews coords:"+coords+" forgetGateActStr:"+forgetGateActBegin.indexRange+" forgetGateActLQ:"+forgetGateActBegin.getRangeData());
		//bug20//LOG.info("lq LstmLayer feed_back inputViews coords:"+coords+" outGateActStr:"+outGateActBegin.indexRange+" outGateActLQ:"+outGateActBegin.getRangeData());
		//bug20//LOG.info("lq LstmLayer feed_back inputViews coords:"+coords+" ipreGateStateStr:"+preGateStateBegin.indexRange+" preGateStateLQ:"+preGateStateBegin.getRangeData());
		//bug20//LOG.info("lq LstmLayer feed_back inputViews coords:"+coords+" preOutGateActStr:"+preOutGateActBegin.indexRange+" preOutGateActLQ:"+preOutGateActBegin.getRangeData());

		
		//errors
		//RangeView inErrs=new RangeView(this.inputErrors.getView(coords),this.inputErrors);
		//inErrs.setMem(this.inputErrors.getView(coords), this.inputErrors);
		//ir//IndexRange tempInErrsIR=new IndexRange(0,0);
		this.inputErrors.getView(coords,tempInErrsIR);
		inErrs.setMem(tempInErrsIR, this.inputErrors);
		
		//real_t* cellErrorBegin = cellErrors[coords].begin();
		//RangeView cellErrorBegin=new RangeView(this.cellErrors.getView(coords),this.cellErrors);
		//cellErrorBegin.setMem(this.cellErrors.getView(coords), this.cellErrors);
		//ir//IndexRange tempCellErrorBeginIR=new IndexRange(0,0);
		this.cellErrors.getView(coords,tempCellErrorBeginIR);
		cellErrorBegin.setMem(tempCellErrorBeginIR, this.cellErrors);
		
		//RangeView outputErrorBegin=new RangeView(this.outputErrors.getView(coords),this.outputErrors);
		//outputErrorBegin.setMem(this.outputErrors.getView(coords), this.outputErrors);
		//ir//IndexRange tempOutputErrorBeginIR=new IndexRange(0,0);
		this.outputErrors.getView(coords,tempOutputErrorBeginIR);
		outputErrorBegin.setMem(tempOutputErrorBeginIR, this.outputErrors);
		
		//bug20//LOG.info("lq LstmLayer feed_back inputViews coords:"+coords+" outputErrorStr:"+outputErrorBegin.indexRange+" outputErrorLQ:"+outputErrorBegin.getRangeData());

		
		double[] W=WeightContainer.weights;
		
		//real_t* errorIt = inErrs.begin();
		
		Pair errorIt=new Pair(inErrs.start(),inErrs.end());
		int errorItIndex=0;
		
		Pair peepWtIt=peepRange;
		int peepWtItIndex=0;
		
		for(int d=0;d<this.num_seq_dims();d++){
			Helpers.range_plus(delayedCoords, coords, stateDelays.get(d));
			
			//oldStates.set(d, states.at(delayedCoords));
			//ir//IndexRange tempOldStatesIR=new IndexRange(0,0);
			IndexRange tempOldStatesIR=tempOldStatesIRList.get(d);
			states.at(delayedCoords,tempOldStatesIR);
			oldStates.set(d, tempOldStatesIR);
			
			Helpers.range_minus(delayedCoords, coords, stateDelays.get(d));
			
			//nextErrors.set(d, this.inputErrors.at(delayedCoords));
			//ir//IndexRange tempNextErrorsIR=new IndexRange(0,0);
			IndexRange tempNextErrorsIR=tempNextErrorsIRList.get(d);
			this.inputErrors.at(delayedCoords,tempNextErrorsIR);
			nextErrors.set(d, tempNextErrorsIR);
			
			//nextFgActs.set(d, forgetGateActs.at(delayedCoords));
			//IndexRange tempNextFgActsIR=new IndexRange(0,0);
			IndexRange tempNextFgActsIR=tempNextFgActsIRList.get(d);
			forgetGateActs.at(delayedCoords,tempNextFgActsIR);
			nextFgActs.set(d, tempNextFgActsIR);
			
			//nextCellErrors.set(d, cellErrors.at(delayedCoords));
			//IndexRange tempNextCellErrorsIR=new IndexRange(0,0);
			IndexRange tempNextCellErrorsIR=tempNextCellErrorsIRList.get(d);
			cellErrors.at(delayedCoords,tempNextCellErrorsIR);
			nextCellErrors.set(d, tempNextCellErrorsIR);
			
		}
		
		int cellStart = 0;
		int cellEnd = cellsPerBlock;
		int fgStart = 0;
		int gateStart = 0;
		
		for(int b=0;b<numBlocks;b++){
			
			double inGateAct=inGateActBegin.getOffset(b);
			double outGateAct=outGateActBegin.getOffset(b);
			
			//output gate error
			
			double outGateError=g.deriv(outGateAct)*Matrix.inner_product(preOutGateActBegin.seqBuffer, preOutGateActBegin.start()+cellStart, preOutGateActBegin.start()+cellEnd, outputErrorBegin.seqBuffer, outputErrorBegin.start()+cellStart, 0.0);
			
			//cell pds (dE/dState)		
			for(int c=cellStart;c<cellEnd;c++){
				
				double deriv=co.deriv(preOutGateActBegin.getOffset(c))*outGateAct*outputErrorBegin.getOffset(c);
				int cOffset = c - cellStart;
				
				double igPeepWt = W[peepWtIt.first+peepWtItIndex+cOffset];
				
				double ogPeepWt= W[peepWtIt.first+peepWtItIndex+(peepsPerBlock - cellsPerBlock + cOffset)];
				
				deriv+=outGateError * ogPeepWt;
				
				for(int d=0;d<this.num_seq_dims();d++){
					
					double  fgPeepWt=W[peepWtIt.first+peepWtItIndex+(cellsPerBlock * (d + 1))];
					
					//RangeView nextErrs=new RangeView(nextErrors.get(d),this.inputErrors);
					nextErrs.setMem(nextErrors.get(d),this.inputErrors);
					
					//RangeView nextFgActsD=new RangeView(nextFgActs.get(d),forgetGateActs);
					nextFgActsD.setMem(nextFgActs.get(d), forgetGateActs);
					
					//RangeView nextCellErrorsD=new RangeView(nextCellErrors.get(d),cellErrors);
					nextCellErrorsD.setMem(nextCellErrors.get(d), cellErrors);
					
					if(nextErrs.begin()){
						deriv+=((nextErrs.getOffset(gateStart + 1 + d) * fgPeepWt)+(nextErrs.getOffset(gateStart) * igPeepWt));
						deriv+=(nextFgActsD.getOffset(fgStart + d) * nextCellErrorsD.getOffset(c));
					}	
				}
				
				cellErrorBegin.setOffset(c, deriv);
			}
			
			//input gate error
			inErrs.setOffset(errorItIndex, g.deriv(inGateAct)*Matrix.inner_product(cellErrorBegin.seqBuffer, cellErrorBegin.start()+cellStart, cellErrorBegin.start()+cellEnd, preGateStateBegin.seqBuffer, preGateStateBegin.start()+cellStart, 0.0));
			
			++errorItIndex;
			
			//forget gate error
			for(int d=0;d<this.num_seq_dims();d++){
				
				//RangeView os=new RangeView(oldStates.get(d),states);
				os.setMem(oldStates.get(d), states);
				
				if(os.begin()){
					inErrs.setOffset(errorItIndex, g.deriv(forgetGateActBegin.getOffset(fgStart+d))*Matrix.inner_product(cellErrorBegin.seqBuffer, cellErrorBegin.start()+cellStart, cellErrorBegin.start()+cellEnd, os.seqBuffer, os.start()+cellStart, 0.0));
				}else{
					inErrs.setOffset(errorItIndex,0.0);
				}
				
				errorItIndex++;
			}
			
			//cell errors
			for(int c=cellStart;c<cellEnd;c++){
				inErrs.setOffset(errorItIndex, inGateAct *(ci.deriv(preGateStateBegin.getOffset(c)))*(cellErrorBegin.getOffset(c)));
				errorItIndex++;
			}
			
			inErrs.setOffset(errorItIndex,outGateError);
			errorItIndex++;
			
			peepWtItIndex+=peepsPerBlock;
			
			cellStart += cellsPerBlock;
			cellEnd += cellsPerBlock;
			fgStart += this.num_seq_dims();
			gateStart += unitsPerBlock;
				
		}
		
		//bug20//LOG.info("lq LstmLayer feed_back inErrs name:"+this.name+" coords:"+coords+" inErrs:"+inErrs.indexRange+" inErrs:"+inErrs.getRangeData());
		
		//RangeView statesViewLQ=new RangeView(states.getView(coords),states);
		//statesViewLQ.setMem(states.getView(coords), states);
		//ir//IndexRange tempStatesViewLQIR=new IndexRange(0,0);
		states.getView(coords,tempStatesViewLQIR);
		statesViewLQ.setMem(tempStatesViewLQIR, states);
		
		//LOG.info("lq LstmLayer feed_back statesViewLQ name:"+this.name+" coords:"+coords+" statesViewLQ.size:"+statesViewLQ.size()+" statesViewLQ:"+statesViewLQ.getRangeData());
		
		//constrain errors to be in [-1,1] for stability
		if(GradientCheck.runningGradTest){
			//to check the correctness of it
			Helpers.bound_range(inErrs, -1.0, 1.0);
		}
		
	}
	

	public void update_derivs(Vector<Integer> coords){
		
		//RangeView stateBegin=new RangeView(states.getView(coords),states);
		//stateBegin.setMem(states.getView(coords), states);
		//ir//IndexRange tempStateBeginIR=new IndexRange(0,0);
		states.getView(coords,tempStateBeginIR);
		stateBegin.setMem(tempStateBeginIR, states);
		
		//RangeView errorBegin=new RangeView(this.inputErrors.getView(coords),this.inputErrors);
		//errorBegin.setMem(this.inputErrors.getView(coords), this.inputErrors);
		//ir//IndexRange tempErrorBeginIR=new IndexRange(0,0);
		this.inputErrors.getView(coords,tempErrorBeginIR);
		errorBegin.setMem(tempErrorBeginIR, this.inputErrors);
		
		Pair pdIt=peepRange;
		int pdItIndex=0;
		
		double[] derivatives=WeightContainer.derivatives;
		
		for(int d=0;d<this.num_seq_dims();d++){
			Helpers.range_plus(delayedCoords, coords, stateDelays.get(d));
				
			//oldStates.set(d, states.at(delayedCoords));
			//ir//IndexRange tempStatesIR=new IndexRange(0,0);
			IndexRange tempStatesIR=tempStatesIRList.get(d);
			states.at(delayedCoords,tempStatesIR);
			oldStates.set(d, tempStatesIR);
		}
		
		for(int b=0;b<numBlocks;b++){
			int cellStart = b * cellsPerBlock;
			int cellEnd = cellStart + cellsPerBlock;
			int errorOffset = b * unitsPerBlock;
			double inGateError=errorBegin.getOffset(errorOffset);
			
			for(int d=0;d<this.num_seq_dims();d++){
				
				//RangeView os=new RangeView(oldStates.get(d),states);
				os.setMem(oldStates.get(d), states);
				
				if(os.begin()){
					
					for(int c=cellStart;c<cellEnd;c++){
						derivatives[pdIt.first+pdItIndex+(c - cellStart)]+=(inGateError*os.getOffset(c));
					}
					
					double forgGateError=errorBegin.getOffset(errorOffset + d + 1);
					
					for(int c=cellStart;c<cellEnd;c++){
						derivatives[pdIt.first+pdItIndex+((c - cellStart) + ((d + 1) * cellsPerBlock))]+=(forgGateError*os.getOffset(c));
					}	
					//LOG.info("lq LstmLayer update_derivs views d:"+d+" ospair:"+os.indexRange+" os:"+os.getRangeData()+" errorBeginStr:"+errorBegin.indexRange+" errorBegin:"+errorBegin.getRangeData()+" pdIt:"+pdIt+" pdItdata:"+WeightContainer.sliceDerivs(pdIt)+" cellStart:"+cellStart+" cellEnd:"+cellEnd+" errorOffset:"+errorOffset+" inGateError:"+inGateError+" forgGateError:"+forgGateError);
				}
				
			}
			
			double outGateError=errorBegin.getOffset(errorOffset + unitsPerBlock - 1);
			
			for(int c=cellStart;c<cellEnd;c++){
				derivatives[pdIt.first+pdItIndex+((c - cellStart) + peepsPerBlock - cellsPerBlock)]+=outGateError*stateBegin.getOffset(c);
			}
			
			pdItIndex+=peepsPerBlock;	
		}
		
		
		//LOG.info("lq LstmLayer update_derivs derivs name:"+this.name+" coords:"+coords+" derivs.end-derivs.begin:"+(peepRange.second-peepRange.first)+" derivs:"+WeightContainer.sliceDerivs(peepRange));
			
	}
	

}
