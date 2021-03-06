package org.click.media.speechcat.facility;


import java.io.IOException;  
import java.util.ArrayList;  
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;  
import ucar.nc2.NetcdfFileWriteable;  

/**
 * regularize inputs:
   the inputs may be:
   wav file
   png file
   
  regularized format:
   NcFile(.nc suffix)
    
 * @author blue
 */

public class NetcdfFacility {

	
	
	/**
	 * python version (digit_offline.py)
	 * @param pngListFile
	 * @param outputFile
	 */
	public static void createNcFileFromPngList(String pngListFile,String outputFile){
	
		
		
		
	}
	
	
	
	
	
	
	public static void createNcFileFromWavList(String wavListFile,String outputFile){
		
	}
	
	
	

	
	@SuppressWarnings("deprecation")
	public static void create3DNetCDFTest() throws Exception{
		
        String filename = "rnn_exmpales/test/test3D.nc";  
        NetcdfFileWriteable ncfile = NetcdfFileWriteable.createNew(filename,true); // add  
        Dimension timeDim = ncfile.addDimension("time",2);  
        Dimension latDim = ncfile.addDimension("lat", 3);  
        Dimension lonDim = ncfile.addDimension("lon", 3); // define  
        ArrayList dims = new ArrayList();  
        dims.add(timeDim);  
        dims.add(latDim);  
        dims.add(lonDim);  
        ncfile.addVariable("temperature", DataType.DOUBLE, dims);  
        ncfile.addVariableAttribute("temperature", "units", "K"); // add a  
        Array data = Array.factory(int.class, new int[] { 3 }, new int[] { 1,2,3 });  
        ncfile.addVariableAttribute("temperature", "scale", data);  
        try {  
            ncfile.create();  
        } catch (IOException e) {  
            System.err.println("ERROR creating file " + ncfile.getLocation()+ "\n" + e);  
        }  
        
	}
	
	@SuppressWarnings("deprecation")
	public static void write3DNetCDFTest() throws Exception{
		
		NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting("rnn_exmpales/test/test3D.nc", true);  
        Dimension timeDim = ncfile.getDimensions().get(0);  
        Dimension latDim = ncfile.getDimensions().get(1);  
        Dimension lonDim = ncfile.getDimensions().get(2);  
        ArrayDouble A = new ArrayDouble.D3(timeDim.getLength(),latDim.getLength(), lonDim.getLength());  
        int k,i, j;  
        Index ima = A.getIndex();  
        for(k = 0; k < timeDim.getLength(); k++){  
            for (i = 0; i < latDim.getLength(); i++) {  
                for (j = 0; j < lonDim.getLength(); j++) {  
                    A.setDouble(ima.set(k,i,j), (double) (k+i+j));  
                }  
            }  
        }  
        int[] origin = new int[3];  
        try {  
            ncfile.write("temperature", origin, A);  
            ncfile.close();  
        } catch (IOException e) {  
            System.err.println("ERROR writing file");  
        } catch (InvalidRangeException e) {  
            e.printStackTrace();  
        }  
	}
	
	
	public static void main(String[] args) throws Exception{
		
		//create3DNetCDFTest();
		write3DNetCDFTest();
		
		
	}
	
	

}
