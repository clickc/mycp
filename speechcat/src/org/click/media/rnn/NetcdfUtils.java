package org.click.media.rnn;

import java.util.ArrayList;
import java.util.List;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class NetcdfUtils {

	public static boolean load_nc_array(NetcdfFile ncf, String name, List<Double> dest, boolean required, int offset,
			int count) {

		Variable v = ncf.findVariable(name);

		if (v != null) {

		}

		return true;
	}

	public static int product(int[] array) {

		int duct = 1;

		for (int i = 0; i < array.length; i++) {
			duct *= array[i];
		}

		return duct;

	}

	public static List<String> load_string_array(NetcdfFile ncf, String name) {

		List<String> taList=new ArrayList<>();
		try {
			Variable seqTarget = ncf.findVariable(name);
			Array seqTargetA = seqTarget.read();
			char[][] targets = (char[][]) seqTargetA.copyToNDJavaArray();
			String target = "";
			for (int i = 0; i < targets.length; i++) {
				target = new String(targets[i]);
				taList.add(target);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taList;
	}

}
