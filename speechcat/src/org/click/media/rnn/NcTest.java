package org.click.media.rnn;

import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.Index2D;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class NcTest {

	public static void main(String[] args){
		
		try{
		   NetcdfFile nc = new NetcdfFile("rnn_exmpales/arabic_offline_handwriting/arabic_offline.nc"); 
		   
		   int numDims=nc.findDimension("numDims").getLength();
		   System.err.println("numDims:"+numDims);
		   
		   List<Dimension> dims=nc.getDimensions();
		   
		   Dimension di=null;
		   for(int i=0;i<dims.size();i++){
			   di=dims.get(i);
			   System.err.println(di.getName());;
		   }
		   
		   
		   List<Variable> vi = nc.getVariables();
		    for(int i=0;i<vi.size();i++){
			  Variable var = vi.get(i);
			
			  System.out.println(var.getName() + " ...");
			  // just throw away the data, read it in for timing and tuning
		    }
		    
		  Variable inputV=nc.findVariable("inputs");
		  System.err.println("inputs dims:"+inputV.getDimensions().size());
		
		  int[] shapeV=inputV.getShape();
		  for(int i=0;i<shapeV.length;i++){
			  System.err.println("i="+i+"\t"+shapeV[i]);
		  }
		  
		  
		  Variable seqV=nc.findVariable("seqDims");
		  Array seqA=seqV.read();
		  int[][] dimscopy=( int[][])seqA.copyToNDJavaArray();
		
		  for(int i=0;i<dimscopy.length;i++){
			  for(int j=0;j<dimscopy[i].length;j++){
		
				    System.err.println("["+i+"]["+j+"]="+dimscopy[i][j]);
				  
			  }
		  }
		  
		  
		  int[] seqShape=seqV.getShape();
		  for(int i=0;i<seqShape.length;i++){
			  System.err.println("i="+i+"\t"+seqShape[i]);
		  }
		  
		  Variable seqLabel=nc.findVariable("labels");
		  Array seqLabelA=seqLabel.read();
		  char[][] labels=(char[][])seqLabelA.copyToNDJavaArray();
		  for(int i=0;i<labels.length;i++){
			  for(int j=0;j<labels[i].length;j++)
			  {
			     System.err.print("["+i+"]["+j+"]\t"+labels[i][j]+"\t");
			  }
			  System.err.println();
			  String l=new String(labels[i]); 
			  System.err.println("l="+l);
		  }
		  
		  //Variable seqTarget=nc.findVariable("targetStrings");
		  Variable seqTarget=nc.findVariable("seqTags");
		  Array seqTargetA=seqTarget.read();
		  char[][] targets=(char[][])seqTargetA.copyToNDJavaArray();
		  String target="";
		  for(int i=0;i<targets.length;i++){
			  for(int j=0;j<targets[i].length;j++){
				  System.err.print("["+i+"]["+j+"]\t"+targets[i][j]);
			  }
			  System.err.println();
			  target=new String(targets[i]);
			  System.err.println("target="+target);
		  }
		  
		  System.err.println("wordTargetStrings:"+nc.findVariable("wordTargetStrings"));
		  
		  
		  
		  
		  
		  
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
