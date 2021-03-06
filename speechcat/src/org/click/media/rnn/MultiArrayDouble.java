package org.click.media.rnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class MultiArrayDouble {

	//data
	List<Double> data;
	Vector<Integer> shape;
	Vector<Integer> strides;
	

	//functions
	MultiArrayDouble(){
		init();	
	}
	
	MultiArrayDouble(Vector<Integer> s){
		init();
		reshape(s);
	}
	
	public MultiArrayDouble(Vector<Integer> s,double fillval){
		init();
		reshape(s,fillval);
	}
	
	@SuppressWarnings("unchecked")
	public void init(){
		
		data=new ArrayList<>();
		shape=new Vector<>();
		strides=new Vector<>();
		
	}
	
	@SuppressWarnings("unchecked")
	public void resize_data(){
		//data.resize(product(shape))
		//vector is necessary to be resized
		////*******old***********
		//int dataResize=RangeUtils.product(shape);
		//for(int i=0;i<dataResize;i++){
		//	data.add((T)"0");
		//}
		
		int dataResize=RangeUtils.product(shape);
		//int oldDataSize=data.size();
		data.clear();
		for(int i=0;i<dataResize;i++){
			data.add(0.0);
		}
		
		strides.clear();
		for(int i=0;i<shape.size();i++){
			strides.add(1);
		}
		
		for(int i=shape.size()-2;i>=0;--i){
			strides.set(i, strides.get(i+1)*shape.get(i+1));
		}
		
	}
	
	public void reshape(Vector<Integer> newShape)
	{
		this.shape=newShape;
		resize_data();
	}
	
	public void fill_data(double fillVal){
		
		for(int i=0;i<data.size();i++){
			data.set(i, fillVal);
		}
		
	}
	
	public void reshape(Vector<Integer> dims,double fillVal){
		reshape(dims);
		fill_data(fillVal);
	}
	
	public boolean in_range(Vector<Integer> coords){
		
		if(coords.size()>shape.size()){
			return false;
		}
		
		int c;
		for(int i=0;i<coords.size();i++){
			c=coords.get(i);
			if(c<0||c>=shape.get(i)){
				return false;
			}
		}
		
		return true;
	}
	
	
	public int offset(Vector<Integer> coords){
		
		if(coords.size()!=strides.size()){
			//System.err.println("coords.size must be equal to strides.size");
		}
		
		//LOG.info("offset: coords:"+coords+" strides:"+strides);
		
		int innerProduct=0;
		
		for(int i=0;i<coords.size();i++){
			innerProduct+=coords.get(i)*strides.get(i);
		}
		
		return innerProduct;
	}

	/**
	 * consider depth and strides
	 * @param coords
	 * @return
	 */
	/*
	public IndexRange getView(Vector<Integer> coords){
		
		if(coords.isEmpty()){
			return new IndexRange(0,data.size());
		}
		
		//needed to be adjusted
		//System.err.println("shape:"+shape+" strides:"+strides+" coords:"+coords);
		int start=offset(coords);
		int end=start+strides.get(coords.size()-1);
		//LOG.info("getView start:"+offset(coords)+" end:"+(start+strides.get(coords.size()-1)));
		return new IndexRange(start,end);
	}
	*/
	
	
	public void getView(Vector<Integer> coords,IndexRange result){
		
		if(coords.isEmpty()){
			//return new IndexRange(0,data.size());
			result.setRange(0,data.size());
			return;
		}
		
		//needed to be adjusted
		//System.err.println("shape:"+shape+" strides:"+strides+" coords:"+coords);
		int start=offset(coords);
		int end=start+strides.get(coords.size()-1);
		//LOG.info("getView start:"+offset(coords)+" end:"+(start+strides.get(coords.size()-1)));
		//return new IndexRange(start,end);
		result.setRange(start, end);
	}
	
	
	/**
	 * consider depth and strides
	 * @param coords
	 * @return
	 */
	/*
	public IndexRange at(Vector<Integer> coords){
		
		//LOG.info("at coords:"+coords+" shape:"+shape);
		
		if(in_range(coords)){
			return getView(coords);
		}
		
		return new IndexRange(0,0);
	}
	*/
	
	
	public void at(Vector<Integer> coords,IndexRange result){
		
		//LOG.info("at coords:"+coords+" shape:"+shape);
		
		if(in_range(coords)){
			getView(coords,result);
			return;
		}
		
		result.setRange(0,0);
		//return new IndexRange(0,0);
	}
	
	public void assign(MultiArray<Double> a){
		reshape(a.shape);
		data=new ArrayList<>();
		for(int i=0;i<a.data.size();i++){
			data.set(i, a.data.get(0));
		}
	}
	
	
}
