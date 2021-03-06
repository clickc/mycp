package org.click.media.rnn;

import java.util.Vector;

public class Helpers {
	
	private static ResizeUtils ru=new ResizeUtils();

	
	public static int bound(int v,int minVal,int maxVal){
		return Math.min(Math.max(minVal, v), maxVal);
	}
	
	public static Vector<Integer> list_of(int numElements,int init){
		
		Vector<Integer> vec=new Vector<>();
		
		for(int i=0;i<numElements;i++){
			vec.add(init);
		}
		
		return vec;
		
	}
	
	public static void fill(Vector<Integer> vec,int num,int val){
		for(int i=0;i<num;i++){
			vec.add(val);
		}
	}
	
	
	
	public static Vector<Integer> flip(Vector<Integer> vec){
	
		Vector<Integer> rvec=new Vector<>(vec.size());
		
		for(int i=vec.size()-1;i>=0;i--){
			rvec.add(vec.get(i));
		}
		
		return rvec;
	}
	

	public static Vector<Integer> createVector(int[] array){
		
		Vector<Integer> vec=new Vector<>();
		for(int i=0;i<array.length;i++){
			vec.add(array[i]);
		}
		
		return vec;
	}
	
	public static Vector<Integer> createVector(int size,int init){
		
		Vector<Integer> vec=new Vector<>();
		for(int i=0;i<size;i++){
			vec.add(init);
		}
		
		return vec;
	}
	
	public static Vector<Double> createDoubleVector(int size,double init){
		
		Vector<Double> vec=new Vector<>();
		for(int i=0;i<size;i++){
			vec.add(init);
		}
		
		return vec;
	}
	
    public static double[] createDoubleArray(int size,double init){
		
		double[] array=new double[size];
		
		for(int i=0;i<size;i++){
			array[i]=init;
		}
		
		return array;
	}
	
	
	public static Vector<Boolean> createBooleanVector(int size,boolean init){
		
		Vector<Boolean> vec=new Vector<>();
		for(int i=0;i<size;i++){
			vec.add(init);
		}
		
		return vec;
	}
	
	public static int[] createArray(Vector<Integer> vec){
		int[] array=new int[vec.size()];
		
		for(int i=0;i<array.length;i++){
			array[i]=vec.get(i);
		}
		
		return array;
	}
	
	public static Vector<Vector<Integer>> createEmbedVector(int size){
		
		Vector<Vector<Integer>> embedVec=new Vector<Vector<Integer>>();
		
		for(int i=0;i<size;i++){
			embedVec.add(new Vector<Integer>());
		}
		
		return embedVec;
	}
	
	public static Vector<IndexRange> createIndexRanges(int size){
		
		Vector<IndexRange> vec=new Vector<>();
		
		for(int i=0;i<size;i++){
			//because createIndexRanges will not be called in a loop so we need not 
			//change it to a global variable
			vec.add(new IndexRange(0,0));
		}
		
		return vec;
		
	}
	
	
	public static void transformMinus(Vector<Integer> a,int opnum,Vector<Integer> b,Vector<Integer> c){
		for(int i=0;i<opnum;i++){
			c.set(i, a.get(i)+b.get(i));
		}
	}
	
	public static boolean in(Vector<Integer> vec,int val){
		boolean inVec=false;
		
		for(int i=0;i<vec.size();i++){
			if(vec.get(i)==val){
				inVec=true;
			}
		}
		
		return inVec;
	}
	
	
	public static String int_to_sortable_string(int num,int max){
		//to check
		return num+"";
	}
	
	public static int index(Vector<String> targets,String lab){
		int ind=-1;
		
		for(int i=0;i<targets.size();i++){
			if(targets.get(i).equals(lab)){
				ind=i;
				break;
			}
		}
		
		
		return ind;
	}
	
	public static Vector<String> make_target_labels(Vector<String> labs){
		
		Vector<String> targetLabels = labs;
		targetLabels.add("blank");
		return targetLabels;
		
	}
	
	public static double[] flood(double[] r,int size,double v){
		//ResizeUtils ru=new ResizeUtils();
		
		//return ResizeUtils.resize(r, size, v);
		return ru.resize(r, size, v);
	}
	
	public static Vector<Integer> copyVector(Vector<Integer> source){
		
		Vector<Integer> dest=new Vector<>();
		
		for(int i=0;i<source.size();i++){
			dest.add(source.get(i));
		}
		
		return dest;
		
		
	}
	
	
	public static void range_multiply(Vector<Integer> a,Vector<Integer> b,Vector<Integer> c){
		
		int minSize=Math.min(b.size(), c.size());
		
		for(int i=0;i<minSize;i++){
			a.set(i, b.get(i)*c.get(i));
		}
		
		
	}
	
	//a=b+c
	public static void range_plus(Vector<Integer> a,Vector<Integer> b,Vector<Integer> c){
		
		int minSize=Math.min(b.size(), c.size());
		
		for(int i=0;i<minSize;i++){
			a.set(i, b.get(i)+c.get(i));
		}
	}
	
	//a=b-c
	public static void range_minus(Vector<Integer> a,Vector<Integer> b,Vector<Integer> c){
		
		int minSize=Math.min(b.size(), c.size());
		
		for(int i=0;i<minSize;i++){
			a.set(i, b.get(i)-c.get(i));
		}
	}
	
	
	//a=a+b right?
	public static void range_plus_equals(Vector<Integer> a,Vector<Integer> b){
		 range_plus(a,a,b);
	}
	
	public static void bound_range(RangeView r,double minVal,double maxVal){
		
		double oval=-1,bval=-1;
		
		for(int i=0;i<r.size();i++){
			oval=r.getOffset(i);
			bval=bound(oval,minVal,maxVal);
			r.setOffset(i, bval);
		}
		
	}
	
	public static double bound(double v,double minVal,double maxVal){
		
		return Math.min(Math.max(minVal, v), maxVal);
	}
	
	public static double get_runtime(){
		
		return System.currentTimeMillis()/(double)1000;
	}
	


}
