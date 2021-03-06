package org.click.media.rnn;

import java.io.FileWriter;
import java.io.PrintWriter;

public class WeightsUniform {

	public static void main(String[] args) throws Exception{
		
		int totalWeights=159369;
		
		PrintWriter pw=new PrintWriter(new FileWriter("logs/uniweights.txt"));
		
		for(int i=0;i<totalWeights;i++){
			pw.println(i+"\t"+Math.random());
		}
		
		pw.close();
		
		
	}
	
}
