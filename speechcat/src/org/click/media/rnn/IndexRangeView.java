package org.click.media.rnn;

import java.util.List;

public class IndexRangeView<T> {
	
	public IndexRange indexRange=null;
	public SeqBuffer<T> seqBuffer=null;
	
	public IndexRangeView(IndexRange indexRange,SeqBuffer<T> seqBuffer)
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
	
	public T current(){
		return seqBuffer.data.get(start());
	}
	
	public void setOffset(int offset,T v){
		this.seqBuffer.data.set(start()+offset, v);
	}
	
	public T getOffset(int offset){
		return this.seqBuffer.data.get(start()+offset);
	}
	
	public List<T> getRangeData(){
		return seqBuffer.fetchData(indexRange);
	}
	
	public int size(){
		return end()-start();
	}
	
	public T back(){
		return this.seqBuffer.data.get(end()-1);
	}
	
	public T nth_last(int n){
		return this.seqBuffer.data.get(end()-n);
	}
	
	public void setBack(T v){
		this.seqBuffer.data.set(end()-1, v);
	}
	
	public void set_nth_last(int n,T v){
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
	
	
	/*
	public T minMaxMean(){
		
		T min,max,mean=null;
		
		Double a;
		mean=(min+max)/2;
		return mean;
	}
	*/
	
}
