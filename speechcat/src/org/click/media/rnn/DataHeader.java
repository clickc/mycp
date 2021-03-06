package org.click.media.rnn;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;
import org.click.media.speechcat.facility.SSO;
import org.click.media.speechcat.facility.SpeechRawDataset.SpeechItem;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * 
 * size_t==int? we use 'Vector' only for configure information
 * 
 * @author blue
 */
public class DataHeader {

	static Logger LOG = Logger.getLogger(DataHeader.class.getName());

	// data
	int numDims;
	Vector<String> inputLabels = new Vector<>();
	Map<String, Integer> inputLabelCounts = new HashMap<>();
	Vector<String> targetLabels = new Vector<>();
	Map<String, Integer> targetIndexs = new HashMap<>();
	Map<String, Integer> targetLabelCounts = new HashMap<>();
	int inputSize;
	int outputSize;
	int numSequences;
	int numTimesteps;
	int totalTargetStringLength;

	/**
	 * source type: ncfile rawwavdir
	 * 
	 * 
	 * @param datafile
	 * @param task
	 * @param dataFraction
	 * @param sourceType
	 */
	public DataHeader(String datasource, String task, double dataFraction, String sourceType) {

		sourceType = sourceType.trim();

		if (sourceType.equals("ncfile")) {
			InitDataHeaderFromNcfile(datasource, task, dataFraction);
		} else if (sourceType.equals("rawwavdir")) {
			// InitDataHeaderFromRawWavdir(datasource, task, dataFraction);
		}

	}

	public DataHeader(SpeechItem[] sourceItems, String task, double dataFraction, String sourceType) {

		sourceType = sourceType.trim();

		if (sourceType.equals("ncfile")) {
			// InitDataHeaderFromNcfile(datasource, task, dataFraction);
		} else if (sourceType.equals("rawwavdir")) {
			InitDataHeaderFromRawWavdir(sourceItems, task, dataFraction);
		}

	}

	public void InitDataHeaderFromRawWavdir(SpeechItem[] sourceItems, String task, double dataFraction) {

		// the dimension of input data, for raw wav file, it actually is one
		// dimension
		numDims = 1;

		// wheter the pic or wav, the input is flattened into one dimension
		// array(corresponds to inputPattSize)
		inputSize = 1;

		// actually count the number of speech items
		int maxSeqs = sourceItems.length;

		// numSequences is approximately dataFraction*maxSeqs
		numSequences = Helpers.bound((int) (dataFraction * maxSeqs), 1, maxSeqs);

		// in order to get seqDims, we must read wav files
        //next we calculate numTimesteps
		
		
		
		
	   
	}

	public void InitDataHeaderFromNcfile(String datafile, String task, double dataFraction) {

		try {

			NetcdfFile nc = new NetcdfFile(datafile);

			LOG.info("=======datafile header info:" + datafile + "==============");

			numDims = nc.findDimension("numDims").getLength();
			if (numDims == 0) {
				numDims = 1;
			}

			LOG.info("numDims:" + numDims);

			inputSize = nc.findDimension("inputPattSize").getLength();
			LOG.info("inputSize:" + inputSize);

			int maxSeqs = nc.findDimension("numSeqs").getLength();
			LOG.info("maxSeqs:" + maxSeqs);

			numSequences = Helpers.bound((int) (dataFraction * maxSeqs), 1, maxSeqs);
			System.err.println("numSequences:" + numSequences);
			LOG.info("numSequences:" + numSequences);

			Variable seqV = nc.findVariable("seqDims");
			Array seqA = seqV.read();
			int[][] dimscopy = (int[][]) seqA.copyToNDJavaArray();

			int[] seqDims = null;
			for (int i = 0; i < numSequences; i++) {
				seqDims = dimscopy[i];
				LOG.info("seq[" + i + "]=" + SSO.strOf(seqDims));
				numTimesteps += NetcdfUtils.product(seqDims);
			}
			LOG.info("numTimesteps:" + numTimesteps);

			if (task.equals("transcription")) {
				outputSize = nc.findDimension("numLabels").getLength();
			}
			LOG.info("outputSize:" + outputSize);

			Variable seqLabel = nc.findVariable("labels");
			Array seqLabelA = seqLabel.read();
			char[][] labels = (char[][]) seqLabelA.copyToNDJavaArray();
			String label = "";
			for (int i = 0; i < outputSize; i++) {
				label = new String(labels[i]);
				label = label.trim();
				targetLabels.add(label);
				targetIndexs.put(label, i);
				targetLabelCounts.put(label, 0);
			}

			LOG.info("targetLabels:" + SSO.strOf(targetLabels));

			if (task.equals("transcription")) {

				Variable seqTarget = nc.findVariable("targetStrings");
				Array seqTargetA = seqTarget.read();
				char[][] targets = (char[][]) seqTargetA.copyToNDJavaArray();
				String target = "", lab = "";
				String[] labs = null;

				for (int s = 0; s < numSequences; s++) {
					target = new String(targets[s]);
					LOG.info("target[" + s + "]=" + target);
					labs = target.split("\\s+");
					for (int k = 0; k < labs.length; k++) {
						lab = labs[k];
						if (targetLabelCounts.containsKey(lab)) {
							targetLabelCounts.put(lab, targetLabelCounts.get(lab) + 1);
						}

						totalTargetStringLength++;
					}
				}

			}

			LOG.info("===============================================");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Vector<Integer> str_to_label_seq(String labelSeqString) {

		Vector<Integer> label_seq = new Vector<>();

		String label = "";

		String[] tokens = null;
		labelSeqString = labelSeqString.trim();
		tokens = labelSeqString.split("\\s+");

		for (int i = 0; i < tokens.length; i++) {
			label = tokens[i];
			label_seq.add(targetIndexs.get(label.trim()));
		}

		return label_seq;

	}

}
