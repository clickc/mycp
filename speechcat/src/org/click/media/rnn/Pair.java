package org.click.media.rnn;

public class Pair {

	public int first;
	public int second;
	
	public Pair(int first,int second){
		this.first=first;
		this.second=second;
	}
	
	public void plus(Pair p){
		this.first+=p.first;
		this.second+=p.second;
	}
	
	public String toString(){
		return "["+first+","+second+"]";
	}
	
}
