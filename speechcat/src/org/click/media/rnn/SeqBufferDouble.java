package org.click.media.rnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class SeqBufferDouble extends MultiArrayDouble{


	static Logger LOG =  Logger.getLogger(SeqBufferDouble.class.getName()); 
	
	int depth;
	
	private IndexRange indexRange=new IndexRange(0,0);
	
	private CoordIterator beginCoordIt=null;
	
	private CoordIterator rbeginCoordIt=null;
	
	private ResizeUtils ru=new ResizeUtils();

	public SeqBufferDouble(int depth){
		this.depth=depth;
		reshape(new Vector<Integer>());
		
		//global iterator
		initIts();
	}
	
	public SeqBufferDouble(Vector<Integer> shape,int depth){
		this.depth=depth;
		reshape(shape);
		
		//global iterator
		initIts();
	}
	
	public void initIts(){
		
		//beginCoordIt=new CoordIterator(this.seq_shape(),directions,false);
		//rbeginCoordIt= new CoordIterator(this.seq_shape(),directions,true);
		beginCoordIt=new CoordIterator();
		rbeginCoordIt= new CoordIterator();
	}

	/*
	public IndexRange getView(int coord){
		//to do
		int start=coord*shape.lastElement();
		int end=start+shape.lastElement();
		return new IndexRange(start,end);
	}
	*/
	
	public void getView(int coord,IndexRange result){
		//to do
		int start=coord*shape.lastElement();
		int end=start+shape.lastElement();
		//return new IndexRange(start,end);
		result.setRange(start, end);
	}
	
	public int seq_offset(Vector<Integer> coords){
		return this.offset(coords)/this.shape.lastElement();
	}
	
	/*
	public IndexRange at(int coord){
		
		if((coord>=0)&&((coord * this.shape.lastElement()) < RangeUtils.product(this.shape))){
			return getView(coord);
		}
		
		return new IndexRange(0,0);
	}
	*/
	
	public void at(int coord,IndexRange result){
		
		if((coord>=0)&&((coord * this.shape.lastElement()) < RangeUtils.product(this.shape))){
			//return getView(coord);
			getView(coord,result);
			return;
		}
		
		//return new IndexRange(0,0);
		result.setRange(0, 0);
	}
	
	
	public IndexRange front(Vector<Integer> directions){
		
		return null;
	}
	
	/*
	public IndexRange back(Vector<Integer> directions){
		
		return null;
		//return rbegin(directions);
	}
	*/
	
	public CoordIterator begin(Vector<Integer> directions){
		
		//return new CoordIterator(this.seq_shape(),directions,false);
		beginCoordIt.reset(this.seq_shape(),directions,false);		
		return beginCoordIt;
	}
	
	public CoordIterator rbegin(Vector<Integer> directions){
		
		//return new CoordIterator(this.seq_shape(),directions,true);
		rbeginCoordIt.reset(this.seq_shape(),directions,true);
		return rbeginCoordIt;
	}
	
	public Vector<Integer> seq_shape(){
		
		//LOG.info("seq_shape shape:"+this.shape);
		
		Vector<Integer> newShape=new Vector<>();
		
		for(int i=0;i<shape.size()-1;i++){
			newShape.add(shape.get(i));
		}
		
		return newShape;
	}
	
	
	public Vector<Double> seq_means(){
		//to do
		return null;
	}
	
	public int seq_size(){
		return RangeUtils.product(seq_shape());
	}
	
	public int num_seq_dims(){
		return this.shape.size()-1;
	}
	
	public void reshape(SeqBufferDouble buff){
		reshape(buff.seq_shape());
	}
	
	public void reshape(SeqBufferDouble buff,double fillVal){
		reshape(buff.seq_shape(),fillVal);
	}
	
	public void reshape(Vector<Integer> newSeqShape){
		if(depth!=0){
			this.shape=newSeqShape;
			//this.shape=ResizeUtils.plus(this.shape, depth);
			this.shape=ru.plus(this.shape, depth);
			this.resize_data();
		}
	}
	
	public void reshape(Vector<Integer> newSeqShape,double fillVal){
		reshape(newSeqShape);
		this.fill_data(fillVal);
	}
	
	public void reshape_with_depth(Vector<Integer> newSeqShape,int depth){
		this.depth=depth;
		reshape(newSeqShape);
	}
	
	public void reshape_with_depth(Vector<Integer> newSeqShape,int depth,double fillVal){
		reshape_with_depth(newSeqShape,depth);
		this.fill_data(fillVal);
	}
	
	public int product(int coord){
		
		//IndexRange indexRange=getView(coord);
		//ir//IndexRange indexRange=new IndexRange(0,0);
		getView(coord,indexRange);
		
		int prod=1;
		for(int i=indexRange.start;i<indexRange.end;i++){
			prod*=data.get(i).intValue();
		}
		
		return prod;
		
	}
	
	public Vector<Double> getRangeVector(IndexRange indexRange){
		
		Vector<Double> rangeVector=new Vector<>(); 
		
		for(int i=indexRange.start;i<indexRange.end;i++){
			rangeVector.add(data.get(i));
		}
		
		return rangeVector;
	}
	
	public boolean load_to_seq_buffer(NetcdfFile nc,SeqBufferDouble dest,Vector<Integer> shape,String name,boolean required,int offset,int count)
	{
	    dest.reshape(shape);
		
		return load_array(nc,name,dest.data,required,offset,count);
	}
	
	public boolean load_array(NetcdfFile nc,String name,List<Double> dest,boolean required,int offset,int count){
		
		return load_nc_array(nc,name,dest,required,offset,count);
	}
	

	
	@SuppressWarnings("unchecked")
	public boolean load_nc_array(NetcdfFile nc,String name,List<Double> dest,boolean required,int offset,int count){
		
		Variable v=nc.findVariable(name);
		int dim=v.getDimensions().size();
		
		Vector<Integer> offsets=Helpers.list_of(1, offset);
		Helpers.fill(offsets, dim-1, 0);
		Vector<Integer> counts=Helpers.createVector(dim, 0);		
		Vector<Integer> shape=Helpers.createVector(v.getShape());
		
		Helpers.transformMinus(shape, dim, offsets, counts);
		if(count>0){
			counts.set(0, count);		
		}
		
		try{
			
            Array destA=v.read(Helpers.createArray(offsets), Helpers.createArray(counts));
            destA.copyTo1DJavaArray();
            float[][] destcopy=( float[][])destA.copyToNDJavaArray();
            //float[] destcopy=(float[])destA.copyTo1DJavaArray();
            dest.clear();
            for(int i=0;i<destcopy.length;i++){
            	for(int j=0;j<destcopy[i].length;j++)
            	{
            	  dest.add((double)destcopy[i][j]);
            	}
            }
         
            
            LOG.info("dest:"+dest);
            //System.err.println("dest:"+dest);
            
            return true;
            
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void fill(IndexRange range,double a){
		
	      for(int i=range.start;i<range.end;i++){
	    	  this.data.set(i, a);
	      }
	      
	}
	
	public void fill(int start,int end,double a){
		
	      for(int i=start;i<end;i++){
	    	  this.data.set(i, a);
	      }
	      
	}
	
	public List<Double> fetchData(IndexRange range){
		
		List<Double> list=new ArrayList<>();
		
		for(int i=range.start;i<range.end;i++){
			list.add(this.data.get(i));
		}
		
		return list;
	}
	
	
	public List<Double> fetchData(int rangeStart,int rangeEnd){
		
		List<Double> list=new ArrayList<>();
		
		for(int i=rangeStart;i<rangeEnd;i++){
			list.add(this.data.get(i));
		}
		
		return list;
	}
	
	
	public void fill(int start,int end,List<Double> list){
		
		for(int i=0;i<list.size();i++){
			this.data.set(start+i, list.get(i));
		}
		
	}
	
	
	public double valueOf(int index){
		//System.err.println("index:"+index+" data:"+this.data.get(index));
		return this.data.get(index);
	}
	
	public void setValue(int index,double v){
		this.data.set(index, v);
	}
	
	
	public String trackDes(){
		String des="";
		
		des+=(this.getClass().getSimpleName()+" ");
		des+=("shape:"+shape+" ");
		des+=("strides:"+strides+" ");
		des+=("seq_shape:"+seq_shape()+" ");
		
		return des;
	}
	
}
