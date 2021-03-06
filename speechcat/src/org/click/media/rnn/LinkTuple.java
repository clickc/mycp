package org.click.media.rnn;

public class LinkTuple {

	public String fromName;
	public String connName;
	public int paramBegin;
	public int paramEnd;
	
	public LinkTuple(String fromName,String connName,int paramBegin,int paramEnd){
		
		this.fromName=fromName;
		this.connName=connName;
		this.paramBegin=paramBegin;
		this.paramEnd=paramEnd;
		
	}
	
}
