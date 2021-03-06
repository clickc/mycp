package org.click.media.rnn;
/**
 * [start,end)
 * @author lq
 */
public class IndexRange {

	public int start;

	public int end;
	


	public IndexRange(int start,int end){
		this.start=start;
		this.end=end;
	}
	
	/**
	 * right?
	 * @return
	 */
	public boolean begin(){
		
		if(start<end){
			return true;
		}
		
		return false;
	}
	
	public String toString(){
		return "["+start+","+end+"]";
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public void setRange(int start,int end){
		setStart(start);
		setEnd(end);
	}
	
}
