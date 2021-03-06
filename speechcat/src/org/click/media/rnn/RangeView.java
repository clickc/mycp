package org.click.media.rnn;

import java.util.List;

public class RangeView {
	

	public IndexRange indexRange=null;

	public SeqBufferDouble seqBuffer=null;
	

	public RangeView(IndexRange indexRange,SeqBufferDouble seqBuffer)
	{
		this.indexRange=indexRange;
		this.seqBuffer=seqBuffer;
	
    }
	
	public int start(){
		return indexRange.start;
	}
	
	public int end(){
		return indexRange.end;
	}
	
	public double current(){
		return seqBuffer.data.get(start());
	}
	
	public void setOffset(int offset,double v){
		this.seqBuffer.data.set(start()+offset, v);
	}
	
	public double getOffset(int offset){
		return this.seqBuffer.data.get(start()+offset);
	}
	
	public List<Double> getRangeData(){
		return seqBuffer.fetchData(indexRange);
	}
	
	public int size(){
		return end()-start();
	}
	
	public double back(){
		return this.seqBuffer.data.get(end()-1);
	}
	
	public double nth_last(int n){
		return this.seqBuffer.data.get(end()-n);
	}
	
	public void setBack(double v){
		this.seqBuffer.data.set(end()-1, v);
	}
	
	public void set_nth_last(int n,double v){
		this.seqBuffer.data.set(end()-n, v);
	}
	
	/**
	 * right?
	 * @return
	 */
	public boolean begin(){
		
		if(indexRange.start<indexRange.end){
			return true;
		}
		
		return false;
	}

	public IndexRange getIndexRange() {
		return indexRange;
	}

	public void setIndexRange(IndexRange indexRange) {
		this.indexRange = indexRange;
	}
	
	public SeqBufferDouble getSeqBuffer() {
		return seqBuffer;
	}

	public void setSeqBuffer(SeqBufferDouble seqBuffer) {
		this.seqBuffer = seqBuffer;
	}
	
	public void setMem(IndexRange indexRange,SeqBufferDouble seqBuffer){
		setIndexRange(indexRange);
		setSeqBuffer(seqBuffer);
	}
	
}
