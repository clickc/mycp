package org.click.media.rnn;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class SeqBuffer<T> extends MultiArray<T>{

	int depth;
	
	static Logger LOG =  Logger.getLogger(SeqBuffer.class.getName());
	
	private ResizeUtils ru=new ResizeUtils();

	public SeqBuffer(int depth){
		this.depth=depth;
		reshape(new Vector<Integer>());
	}
	
	public SeqBuffer(Vector<Integer> shape,int depth){
		this.depth=depth;
		reshape(shape);
	}
	

	public IndexRange getView(int coord){
		//to do
		int start=coord*shape.lastElement();
		int end=start+shape.lastElement();
		return new IndexRange(start,end);
	}
	
	public int seq_offset(Vector<Integer> coords){
		return this.offset(coords)/this.shape.lastElement();
	}
	
	public IndexRange at(int coord){
		
		if((coord>=0)&&((coord * this.shape.lastElement()) < RangeUtils.product(this.shape))){
			return getView(coord);
		}
		
		return new IndexRange(0,0);
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
		
		LOG.info("lq SeqBegin:"+this.shape+" directions:"+directions);
		return new CoordIterator(this.seq_shape(),directions,false);
	}
	
	public CoordIterator rbegin(Vector<Integer> directions){
		
		return new CoordIterator(this.seq_shape(),directions,true);
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
	
	public void reshape(SeqBuffer<T> buff){
		reshape(buff.seq_shape());
	}
	
	public void reshape(SeqBuffer<T> buff,T fillVal){
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
	
	public void reshape(Vector<Integer> newSeqShape,T fillVal){
		reshape(newSeqShape);
		this.fill_data(fillVal);
	}
	
	public void reshape_with_depth(Vector<Integer> newSeqShape,int depth){
		this.depth=depth;
		reshape(newSeqShape);
	}
	
	public void reshape_with_depth(Vector<Integer> newSeqShape,int depth,T fillVal){
		reshape_with_depth(newSeqShape,depth);
		this.fill_data(fillVal);
	}
	
	public int product(int coord){
		
		IndexRange indexRange=getView(coord);
		
		int prod=1;
		for(int i=indexRange.start;i<indexRange.end;i++){
			prod*=(int)data.get(i);
		}
		
		return prod;
		
	}
	
	public Vector<T> getRangeVector(IndexRange indexRange){
		
		@SuppressWarnings("unchecked")
		Vector<T> rangeVector=(Vector<T>)(new Vector<>()); 
		
		for(int i=indexRange.start;i<indexRange.end;i++){
			rangeVector.add(data.get(i));
		}
		
		return rangeVector;
	}
	
	public boolean load_to_seq_buffer(NetcdfFile nc,SeqBuffer<T> dest,Vector<Integer> shape,String name,boolean required,int offset,int count)
	{
	    dest.reshape(shape);
		
		return load_array(nc,name,dest.data,required,offset,count);
	}
	
	public boolean load_array(NetcdfFile nc,String name,List<T> dest,boolean required,int offset,int count){
		
		return load_nc_array(nc,name,dest,required,offset,count);
	}
	

	
	@SuppressWarnings("unchecked")
	public boolean load_nc_array(NetcdfFile nc,String name,List<T> dest,boolean required,int offset,int count){
		
		Variable v=nc.findVariable(name);
		int dim=v.getDimensions().size();
		
		Vector<Integer> offsets=Helpers.list_of(1, offset);
		Helpers.fill(offsets, dim-1, 0);
		Vector<Integer> counts=Helpers.createVector(dim, 0);		
		Vector<Integer> shape=Helpers.createVector(v.getShape());
		
		Helpers.transformMinus(shape, dim, offsets, counts);
		if(count>0){
			counts.set(0, count);
		
		
		LOG.info("lq SeqBuffer offsets:"+offsets+" shape:"+shape+" counts:"+counts+" count:"+count);}
		
		try{
			
            Array destA=v.read(Helpers.createArray(offsets), Helpers.createArray(counts));
            destA.copyTo1DJavaArray();
            float[][] destcopy=( float[][])destA.copyToNDJavaArray();
            //float[] destcopy=(float[])destA.copyTo1DJavaArray();
            LOG.info("destcopy.len:"+destcopy.length+" destcopy[0].len:"+destcopy[0].length);
            dest.clear();
            for(int i=0;i<destcopy.length;i++){
            	for(int j=0;j<destcopy[i].length;j++)
            	{
            	  dest.add((T)(destcopy[i][j]+""));
            	}
            }
         

            
            //System.err.println("dest:"+dest);
            
            return true;
            
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void fill(IndexRange range,T a){
		
	      for(int i=range.start;i<range.end;i++){
	    	  this.data.set(i, a);
	      }
	      
	}
	
	public void fill(int start,int end,T a){
		
	      for(int i=start;i<end;i++){
	    	  this.data.set(i, a);
	      }
	      
	}
	
	public List<T> fetchData(IndexRange range){
		
		List<T> list=new ArrayList<>();
		
		for(int i=range.start;i<range.end;i++){
			list.add(this.data.get(i));
		}
		
		return list;
	}
	
	
	public List<T> fetchData(int rangeStart,int rangeEnd){
		
		List<T> list=new ArrayList<>();
		
		for(int i=rangeStart;i<rangeEnd;i++){
			list.add(this.data.get(i));
		}
		
		return list;
	}
	
	
	public void fill(int start,int end,List<T> list){
		
		for(int i=0;i<list.size();i++){
			this.data.set(start+i, list.get(i));
		}
		
	}
	
	
	public T valueOf(int index){
		//System.err.println("index:"+index+" data:"+this.data.get(index));
		return this.data.get(index);
	}
	
	public void setValue(int index,T v){
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
