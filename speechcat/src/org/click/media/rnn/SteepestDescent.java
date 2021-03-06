package org.click.media.rnn;

import java.io.FileWriter;
import java.io.PrintWriter;

public class SteepestDescent extends Optimiser {

	double[] deltas;
	double learnRate;
	double momentum;

	public SteepestDescent(String name, double[] weights, double[] derivatives, double lr, double mom) {
		super(weights, derivatives);

		this.learnRate = lr;
		this.momentum = mom;
		build();
	}

	public void update_weights() {
		//System.err.println("momentum:" + momentum + " learnRate:" + learnRate);

		
		//try {
			//PrintWriter pw = new PrintWriter(new FileWriter("logs/firstupdateWeights.txt"));
            
			for (int i = 0; i < wts.length; i++) {
		//		pw.print("i:"+i+" bwts:"+wts[i]+" bderivs:"+derivs[i]+" bdeltas:"+deltas[i]+"\t");
				double delta = (momentum * deltas[i]) - (learnRate * derivs[i]);
				deltas[i] = delta;
				wts[i] += delta;
		//		pw.println("i:"+i+" awts:"+wts[i]+" aderivs:"+derivs[i]+" adeltas:"+deltas[i]+"\t");
			}
		//	pw.close();
		//} catch (Exception e) {
		//	e.printStackTrace();
		//}
		

	}

	public void build() {
		deltas = new double[wts.length];
	}

}
