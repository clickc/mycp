package org.click.media.rnn;

import java.util.Vector;

import org.apache.log4j.Logger;

public class CoordIterator {

	Vector<Integer> shape;
	Vector<Integer> directions;
	Vector<Integer> pt;
	boolean end;
	static Logger LOG =  Logger.getLogger(CoordIterator.class.getName());
	private ResizeUtils ru=new ResizeUtils();
	
	public CoordIterator(){
		//initial constructor
		pt=new Vector<>();
	}
	
    /**
     * default:
     * directions:empty list
     * reverse:false	
     * @param shape
     * @param directions
     * @param reverse
    */
	public CoordIterator(Vector<Integer> shape,Vector<Integer> directions,boolean reverse){
		
		this.shape=shape;
		this.directions=directions;
		pt=new Vector<>();
		for(int i=0;i<shape.size();i++){
			pt.add(0);
		}
		
		//this.directions=ResizeUtils.resize(directions, shape.size(), 1);
		this.directions=ru.resize(directions, shape.size(), 1);
		//System.err.println("CoordIterator construct directions :"+this.directions);
		if(reverse){
			RangeUtils.multiply_val(this.directions, -1);
		}
		
		//LOG.info("after construct start point:"+point());
		begin();
		//LOG.info("after construct start point2:"+point());
	}
	
	public void reset(Vector<Integer> shape,Vector<Integer> directions,boolean reverse){
		this.shape=shape;
		this.directions=directions;
		//pt=new Vector<>();
		pt.clear();
		for(int i=0;i<shape.size();i++){
			pt.add(0);
		}
		
		//this.directions=ResizeUtils.resize(directions, shape.size(), 1);
		this.directions=ru.resize(directions, shape.size(), 1);
		//System.err.println("CoordIterator construct directions :"+this.directions);
		if(reverse){
			RangeUtils.multiply_val(this.directions, -1);
		}
		
		//LOG.info("after construct start point:"+point());
		begin();
		//LOG.info("after construct start point2:"+point());
	}
	
	public void step(int d){
		
		if (directions.get(d) > 0)
		{
			if (pt.get(d) == shape.get(d) - 1)
			{
				pt.set(d, 0);
				if (d!=0)
				{
					step(d-1);
				}
				else
				{
					end = true;
				}
			}
			else
			{
				int oval=pt.get(d);
				pt.set(d, oval+1);
			}
		}
		else
		{
			if (pt.get(d) == 0)
			{
				pt.set(d, shape.get(d)-1);
				if (d!=0)
				{
					step(d-1);
				}
				else
				{
					end = true;
				}
			}
			else
			{
				int oval=pt.get(d);
				pt.set(d, oval-1);
			}
		}
	}
	
	public CoordIterator increment(){
		
		if (shape.size()!=0)
		{
			step(shape.size()-1);
		}
		else
		{
			end = true;
		}
		
		return this;
	}
	
	public Vector<Integer> point(){
		return pt;
	}
	
	public void begin(){
		
		//LOG.info("begin shape:"+shape+" directions:"+directions);
		for(int i=0;i<shape.size();i++){
			pt.set(i, ((directions.get(i)>0)?0:shape.get(i)-1));
		}
		end=false;
	}
	
	public int size(){
		return RangeUtils.product(shape);
	}
	
	public static void main(String[] args){
		/*
		Vector<Integer> shape=new Vector<>();
		shape.add(3);
		shape.add(4);
		Vector<Integer> directions=new Vector<>();
		directions.add(1);
		directions.add(1);
		
		CoordIterator ci=new CoordIterator(shape,directions,false);
		
		System.err.println("first pass");
		for(int i=0;i<ci.size()-5;i++){
			System.err.println(ci.point());
			ci.increment();
		}
		
		ci.begin();
		
		System.err.println("second pass");
		for(int i=0;i<ci.size();i++){
			System.err.println(ci.point());
			ci.increment();
		}*/
		
		CoordIterator ci=new CoordIterator();
		
		Vector<Integer> shape=new Vector<>();
		shape.add(60);
		shape.add(19);
		Vector<Integer> directions=new Vector<>();
		directions.add(-1);
		directions.add(-1);
		
		//CoordIterator ci=new CoordIterator(shape,directions,false);
		ci.reset(shape, directions, false);
		
		System.err.println("first pass");
		for(int i=0;i<ci.size()-5;i++){
			System.err.println(ci.point());
			ci.increment();
		}
		
		ci.begin();
		
		System.err.println("second pass");
		for(int i=0;i<ci.size();i++){
			System.err.println(ci.point());
			ci.increment();
		}
		
		Vector<Integer> shape2=new Vector<>();
		shape2.add(3);
		shape2.add(4);
		Vector<Integer> directions2=new Vector<>();
		directions2.add(-1);
		directions2.add(-1);
		
		ci.reset(shape2, directions2, false);
		
        ci.begin();
		
		System.err.println("reset pass");
		for(int i=0;i<ci.size();i++){
			System.err.println(ci.point());
			ci.increment();
		}
		
	}
	
	
	
}
