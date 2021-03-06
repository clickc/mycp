package org.click.media.rnn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class StringAlignment {
	
	//data
	Map<Integer,Map<Integer,Integer>> subsMap=new HashMap<Integer,Map<Integer,Integer>>();
	
	Map<Integer,Integer> delsMap=new HashMap<>();
	
	Map<Integer,Integer> insMap=new HashMap<>();
	
	Vector<Vector<Integer>> matrix=new Vector<>();

	int substitutions;
	int deletions;
	int insertions;
	int distance;
	int subPenalty;
	int delPenalty;
	int insPenalty;
	int n,m;
	
	/**
	 * default:
	 * trackErrors: false
	 * backtrace: true
	 * sp: 1
	 * dp: 1
	 * ip: 1
	 * @param reference_sequence
	 * @param test_sequence
	 * @param trackErrors
	 * @param backtrace
	 * @param sp
	 * @param dp
	 * @param ip
	 */
	public StringAlignment(Vector<Integer> reference_sequence,Vector<Integer> test_sequence,boolean trackErrors,boolean backtrace, int sp,int dp,int ip){
		
		subPenalty=sp;
		delPenalty=dp;
		insPenalty=ip;
		n=reference_sequence.size();		
		m=test_sequence.size();
		
		if(n==0){
			substitutions = 0;
			deletions = 0;
			insertions = m;
			distance = m;
		}else if(m==0){
			substitutions = 0;
			deletions = n;
			insertions = 0;
			distance = n;
		}else{
			
			//initialise the matrix
			for(int i=0;i<n+1;i++){
				matrix.add(new Vector<>());
			}
			
			for(int i=0;i<matrix.size();i++){
				for(int j=0;j<m+1;j++){
					matrix.get(i).add(0);
				}
			}
			
			for(int i=0;i<n+1;i++){
				matrix.get(i).set(0, i);
			}
			
			for(int j=0;j<m+1;j++){
				matrix.get(0).set(j, j);
			}
			
			//calculate the insertions, substitutions and deletions
			for(int i=1;i<n+1;i++){
				int s_i=reference_sequence.get(i-1);
				
				for(int j=1;j<m+1;j++){
					int t_j=test_sequence.get(j-1);
					int cost=((s_i == t_j) ? 0 : 1);
					int above = matrix.get(i-1).get(j);
					int left = matrix.get(i).get(j-1);
					int diag = matrix.get(i-1).get(j-1);
					int cell = Math.min(above + 1,			// deletion
										 Math.min(left + 1,			// insertion
											 diag + cost));		// substitution
					
					matrix.get(i).set(j, cell);
					
				}			
			}
			
			//N.B sub,ins and del penalties are all set to 1 if backtrace is ignored
			if (backtrace)
			{
				int i = n;
				int j = m;
				substitutions = 0;
				deletions = 0;
				insertions = 0;
				
				// Backtracking
				while (i != 0 && j != 0) 
				{
					if (matrix.get(i).get(j) == matrix.get(i-1).get(j-1)) 
					{
						--i;
						--j;
					}
					else if (matrix.get(i).get(j) == (matrix.get(i-1).get(j-1) + 1))
					{
						if (trackErrors)
						{
							if(!(subsMap.containsKey(reference_sequence.get(i)))){
								subsMap.put(reference_sequence.get(i), new HashMap<Integer,Integer>());
							}else if(!(subsMap.get(reference_sequence.get(i)).containsKey(test_sequence.get(j)))){
								subsMap.get(reference_sequence.get(i)).put(test_sequence.get(j),0);
							}
							subsMap.get(reference_sequence.get(i)).put(test_sequence.get(j),subsMap.get(reference_sequence.get(i)).get(test_sequence.get(j))+1);
						}
						++substitutions;
						--i;
						--j;
					}
					else if (matrix.get(i).get(j) == (matrix.get(i-1).get(j) + 1))
					{
						if (trackErrors)
						{
							if(!(delsMap.containsKey(reference_sequence.get(i)))){
								delsMap.put(reference_sequence.get(i), 0);
							}
							
							delsMap.put(reference_sequence.get(i), delsMap.get(reference_sequence.get(i))+1);
							//++delsMap[reference_sequence[i]];
						}
						++deletions;
						--i;
					}
					else 
					{
						if (trackErrors)
						{
							if(!(insMap.containsKey(test_sequence.get(j)))){
								insMap.put(test_sequence.get(j), 0);
							}
							
							insMap.put(test_sequence.get(j), insMap.get(test_sequence.get(j))+1);
						}
						++insertions;
						--j;
					}
				}
				
				
				while (i != 0)
				{
					if (trackErrors)
					{
						if(!(delsMap.containsKey(reference_sequence.get(i)))){
							delsMap.put(reference_sequence.get(i), 0);
						}
						
						delsMap.put(reference_sequence.get(i), delsMap.get(reference_sequence.get(i))+1);
						//++delsMap[reference_sequence[i]];
					}
					++deletions;
					--i;
				}
				while (j != 0) 
				{
					if (trackErrors)
					{
						if(!(insMap.containsKey(test_sequence.get(j)))){
							insMap.put(test_sequence.get(j), 0);
						}
						
						insMap.put(test_sequence.get(j), insMap.get(test_sequence.get(j))+1);
						//++insMap[test_sequence[j]];
					}
					++insertions;
					--j;
				}
				
				//scale individual errors by penalties
				distance = (subPenalty*substitutions) + (delPenalty*deletions) + (insPenalty*insertions);
			}else
			{
				distance = matrix.get(n).get(m);
			}
					
		}
		
		
	}
	
	
	public static void main(String[] args){
		
	    //how to test?
		
		Vector<Integer> targetLabelSeq=new Vector<>();
		targetLabelSeq.add(1);
		targetLabelSeq.add(2);
		targetLabelSeq.add(3);
		targetLabelSeq.add(3);
		
		Vector<Integer> outputLabelSeq=new Vector<>();
		outputLabelSeq.add(1);
		outputLabelSeq.add(3);
		outputLabelSeq.add(2);
		//outputLabelSeq.add(3);
		
		StringAlignment alignment=new StringAlignment(targetLabelSeq,outputLabelSeq,false,true,1,1,1);

		double labelError = alignment.distance;
		double substitutions = alignment.substitutions;
		double deletions = alignment.deletions;
		double insertions = alignment.insertions;
		
		System.err.println("labelError:"+labelError+" substitutions:"+substitutions+" deletions:"+deletions+" insertions:"+insertions);
		
		
		
		
	}
	
	
	
}
