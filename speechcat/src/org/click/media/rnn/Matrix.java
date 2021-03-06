package org.click.media.rnn;

import java.util.List;

import org.click.media.rnn.activationFunctions.Func;

public class Matrix {

	
   	public static double[] weights=null;
	
	public static void dot(SeqBufferDouble in, int inBegin, int inEnd, SeqBufferDouble M, int mstart,
			SeqBufferDouble out, int outBegin, int outEnd) {

		int k = mstart;
		double ov;
		for (int i = outBegin; i < outEnd; ++i) {
			double sum = 0;

			for (int j = inBegin; j < inEnd; ++j, ++k) {
				sum += M.valueOf(k) * in.valueOf(j);
			}

			ov = out.valueOf(i);
			out.setValue(i, ov + sum);
		}

	}

	public static void dot(SeqBufferDouble in, int inBegin, int inEnd, double[] M, int mstart, SeqBufferDouble out,
			int outBegin, int outEnd) {

		int k = mstart;
		double ov;
		for (int i = outBegin; i < outEnd; ++i) {
			double sum = 0;

			for (int j = inBegin; j < inEnd; ++j, ++k) {
				sum += M[k] * in.valueOf(j);
			}

			ov = out.valueOf(i);
			out.setValue(i, ov + sum);
		}

	}
	
	
	public static void outer(SeqBufferDouble a,IndexRange aRange,int mstart,SeqBufferDouble b,IndexRange bRange){
		outer(a,aRange.start,aRange.end,mstart,b,bRange.start,bRange.end);
	}
	
	public static void dot(SeqBufferDouble in, IndexRange inRange,  int mstart, SeqBufferDouble out,
			IndexRange outRange) {
		dot(in,inRange.start,inRange.end,mstart,out,outRange.start,outRange.end);
	}
	
	public static void dot_transpose(SeqBufferDouble in, IndexRange inRange,  int mstart, SeqBufferDouble out,
			IndexRange outRange) {
		dot_transpose(in,inRange.start,inRange.end,mstart,out,outRange.start,outRange.end);
	}
	
	
	/**
	 * a*b+preVal
	 * @param a
	 * @param aStart
	 * @param aEnd
	 * @param b
	 * @param bStart
	 * @param preVal
	 */
	public static double inner_product(SeqBufferDouble a,int aStart,int aEnd,SeqBufferDouble b,int bStart,double preVal)
	{
		double innerSum=0;
		
		int bIndex=bStart;
		
		for(int i=aStart;i<aEnd;i++){
			
			innerSum+=(a.data.get(i))*(b.data.get(bIndex));
			bIndex++;
		}
		
		return innerSum;
	}
	
	public static void outer(SeqBufferDouble a,int aBegin,int aEnd,int mstart,SeqBufferDouble b,int bBegin,int bEnd){
		int k=mstart;
		double ov;
		double[] derivs=WeightContainer.derivatives;
		
		for(int i=bBegin;i<bEnd;i++){			
			double input=b.valueOf(i);
			for(int j=aBegin;j<aEnd;++j,++k){
				ov=derivs[k];
				//derivs[k]=ov+a.valueOf(j)*input;
				derivs[k]=ov+(a.valueOf(j))*input;
			}
			
		}	
	}
	
	public static void dot(SeqBufferDouble in, int inBegin, int inEnd,  int mstart, SeqBufferDouble out,
			int outBegin, int outEnd) {

		double[] weights=WeightContainer.weights;
		int k = mstart;
		double ov;
		for (int i = outBegin; i < outEnd; ++i) {
			double sum = 0;

			for (int j = inBegin; j < inEnd; ++j, ++k) {
				//System.err.println(weights[k]);
				//System.err.println(in.valueOf(j));
				//////sum += weights[k] * (Double.parseDouble(in.valueOf(j)+""));
				sum += weights[k] * (in.valueOf(j));
				//sum += weights[k] * (0.5);
			}

			ov = out.valueOf(i);
			out.setValue(i, ov + sum);
		}

	}
	
	public static void dot_transpose(SeqBufferDouble in, int inBegin, int inEnd,  int mstart, SeqBufferDouble out,
			int outBegin, int outEnd) {
		
		double[] weights=WeightContainer.weights;
		int k = mstart;
		double ov;
		
		for(int i=inBegin;i<inEnd;i++){
			//double input=in.valueOf(i);
			double input=in.valueOf(i);
			for(int j=outBegin;j<outEnd;++j,++k){
				//ov=out.valueOf(j);
				ov=out.valueOf(j);
				out.setValue(j, ov+weights[k]*input);
			}
		}	
		
	}
	
	
	public static void dot(SeqBufferDouble in, IndexRange inRange, double[] M, int mstart, SeqBufferDouble out,
			IndexRange outRange) {
		dot(in,inRange.start,inRange.end,M,mstart,out,outRange.start,outRange.end);
	}
	
	
	
	
	public static void transform(SeqBufferDouble in, int inBegin, int inEnd,SeqBufferDouble out,int outBegin,Func func){
		
		int outIndex=outBegin;
		
		for(int i=inBegin;i<inEnd;i++){
			out.setValue(outIndex, func.fn(in.valueOf(i)));
			outIndex++;
		}
		
	}

	
	public static void transformMultiply(SeqBufferDouble in, int inBegin, int inEnd,SeqBufferDouble out,int outBegin,double multiply){
		
		int outIndex=outBegin;
		
		for(int i=inBegin;i<inEnd;i++){
			out.setValue(outIndex, multiply*(in.valueOf(i)));
			outIndex++;
		}
		
	}
	
}
