package org.click.media.rnn;

import java.util.Vector;

public class RangeUtils {

	public static void multiply_val(Vector<Integer> vec,int val){
		int oval;
		for(int i=0;i<vec.size();i++){
			oval=vec.get(i);
			vec.set(i, oval*val);
		}
	}
	
	public static int product(Vector<Integer> vec){
		
		int prod=1;
		
		for(int i=0;i<vec.size();i++){
			prod*=vec.get(i);
		}
		
		return prod;
	}
	
	public static String delimStr(Vector<String> vec,char deli){
		String deliStr="";
		
		for(int i=0;i<vec.size();i++){
			if(i!=(vec.size()-1))
			{
			   deliStr+=(vec.get(i)+deli);
			}
			else{
				 deliStr+=vec.get(i);
			}
		}
		
		return deliStr;
	}

	
}
