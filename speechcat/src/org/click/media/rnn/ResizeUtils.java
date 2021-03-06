package org.click.media.rnn;

import java.util.Vector;

public class ResizeUtils {

	public Vector<Integer> globalnself=new Vector<Integer>();
	/*
	public Vector<Integer> resize(Vector<Integer> self, int n, int val) {

		Vector<Integer> nself = new Vector<Integer>();
		if (self == null) {
			for (int i = 0; i < n; i++) {
				nself.add(val);
			}
		} else if (self.size() <= n) {

			for (int i = 0; i < self.size(); i++) {
				nself.add(self.get(i));
			}

			for (int i = self.size(); i < n; i++) {
				nself.add(val);
			}
		} else {
			for (int i = 0; i < n; i++) {
				nself.add(self.get(i));
			}
		}

		return nself;
	}
	*/
	
	public Vector<Integer> resize(Vector<Integer> self, int n, int val) {

		//Vector<Integer> nself = new Vector<Integer>();
		globalnself.clear();
		if (self == null) {
			for (int i = 0; i < n; i++) {
				globalnself.add(val);
			}
		} else if (self.size() <= n) {

			for (int i = 0; i < self.size(); i++) {
				globalnself.add(self.get(i));
			}

			for (int i = self.size(); i < n; i++) {
				globalnself.add(val);
			}
		} else {
			for (int i = 0; i < n; i++) {
				globalnself.add(self.get(i));
			}
		}

		return globalnself;
	}

	public double[] resize(double[] self, int n, double val) {

		double[] nself = new double[n];
		if (self == null) {
			for (int i = 0; i < n; i++) {
				nself[i] = val;
			}
		} else if (self.length <= n) {

			for (int i = 0; i < self.length; i++) {
				nself[i] = self[i];
			}

			for (int i = self.length; i < n; i++) {
				nself[i] = val;
			}
		} else {
			for (int i = 0; i < n; i++) {
				nself[i] = self[i];
			}
		}

		return nself;
	}

	public Vector<Boolean> resizeBoolean(Vector<Boolean> self, int n, boolean val) {

		Vector<Boolean> nself = new Vector<Boolean>();
		if (self == null) {
			for (int i = 0; i < n; i++) {
				nself.add(val);
			}
		} else if (self.size() <= n) {

			for (int i = 0; i < self.size(); i++) {
				nself.add(self.get(i));
			}

			for (int i = self.size(); i < n; i++) {
				nself.add(val);
			}
		} else {
			for (int i = 0; i < n; i++) {
				nself.add(self.get(i));
			}
		}

		return nself;
	}

	public Vector<Integer> plus(Vector<Integer> self, int val) {

		Vector<Integer> nself = new Vector<Integer>();

		if (self != null) {
			for (int i = 0; i < self.size(); i++) {
				nself.add(self.get(i));
			}
		}

		nself.add(val);

		return nself;
	}

}
