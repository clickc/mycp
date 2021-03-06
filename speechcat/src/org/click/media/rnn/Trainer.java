package org.click.media.rnn;

import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

public class Trainer extends DataExporter {

	// data
	public Mdrnn net;
	public Optimiser optimiser;
	public ConfigFile config;

	double[] wts;
	double[] derivs;
	int epoch;

	Vector<String> criteria;
	Map<String, Double> netErrors;
	Map<String, Double> netNormFactors;

	double[] distortedWeights;
	String task;
	double dataFraction;
	int seqsPerWeightUpdate;
	boolean batchLearn;

	DataList trainFiles;
	DataList testFiles;
	DataList valFiles;

	DatasetErrors trainErrors = new DatasetErrors();
	DatasetErrors testErrors = new DatasetErrors();
	DatasetErrors valErrors = new DatasetErrors();

	double inputNoiseDev;
	double weightDistortion;

	boolean testDistortions;
	double l1, l2;
	double invTrainSeqs;

	// MDL parameters
	boolean mdl;
	double mdlWeight;
	double mdlInitStdDev;
	double[] mdlStdDevs;
	double[] mdlStdDevDerivs;
	double[] weightCosts;

	int mdlSamples;
	boolean mdlSymmetricSampling;
	double[] mdlSeqDerivs;
	double[] mdlOldDerivs;

	double mdlPriorMean;
	double mdlPriorStdDev;
	double mdlPriorVariance;

	Optimiser mdlOptimiser;

	// we add log constraint implied
	double[] mdlMlErrors;
	Map<String, Double> mdlSeqErrors;

	int matrixOps;

	static Logger LOG = Logger.getLogger(Trainer.class.getName());

	/**
	 * default name:trainer
	 * 
	 * @param n
	 * @param conf
	 * @param name
	 */
	public Trainer(Mdrnn n, ConfigFile conf, String name) {
		super(name);
		this.net = n;
		this.optimiser = null;
		this.config = conf;
		this.wts = WeightContainer.weights;
		this.derivs = WeightContainer.derivatives;
		this.epoch = 0;
		this.criteria = net.criteria;
		this.netErrors = net.errors;
		this.netNormFactors = net.normFactors;
		this.task = conf.getString("task", "");
		this.dataFraction = conf.getDouble("dataFraction", "1");
		this.seqsPerWeightUpdate = conf.getInt("seqsPerWeightUpdate", "1");

		boolean tempBool = (conf.getString("optimiser", "steepest").equals("rprop")) && (seqsPerWeightUpdate == 1);

		this.batchLearn = config.getBoolean("batchLearn", tempBool + "");
		this.trainFiles = new DataList(conf.getStringVector("trainFile", ""), task, !batchLearn, dataFraction);
		this.testFiles = new DataList(conf.getStringVector("testFile", ""), task, false, dataFraction);
		this.valFiles = new DataList(conf.getStringVector("valFile", ""), task, false, dataFraction);
		this.inputNoiseDev = conf.getDouble("inputNoiseDev", "0.0");
		this.weightDistortion = conf.getDouble("weightDistortion", "0.0");

		this.testDistortions = conf.getBoolean("testDistortions", "false");
		this.l1 = conf.getDouble("l1", "0.0");
		this.l2 = conf.getDouble("l2", "0.0");

		this.invTrainSeqs = trainFiles.numSequences != 0 ? 1.0 / trainFiles.numSequences : 0;

		// MDL parameters
		this.mdl = conf.getBoolean("mdl", "false");
		this.mdlWeight = mdl ? conf.getDouble("mdlWeight", "1") : 1;
		this.mdlInitStdDev = mdl ? conf.getDouble("mdlInitStdDev", "0.075") : 0;
		this.mdlStdDevs = Helpers.createDoubleArray(mdl ? wts.length : 0, this.mdlInitStdDev);
		this.mdlStdDevDerivs = Helpers.createDoubleArray(this.mdlStdDevs.length, 0.0);
		this.weightCosts = Helpers.createDoubleArray(this.mdlStdDevs.length, 0.0);
		this.mdlSamples = mdl ? conf.getInt("mdlSamples", "1") : 0;
		this.mdlSymmetricSampling = mdl ? conf.getBoolean("mdlSymmetricSampling", "false") : false;
		this.mdlPriorMean = 0;
		this.mdlInitStdDev = 0;
		this.mdlPriorVariance = 0;
		this.mdlOptimiser = null;

		String optType = conf.getString("optimiser", "steepest");
		String optName = "weight_optimiser";

		double learnRate = config.getDouble("learnRate", "1e-4");
		double momentum = config.getDouble("momentum", "0.9");

		if (optType.equals("rprop")) {
			optimiser = new Rprop(optName, wts, derivs, false);
		} else {
			optimiser = new SteepestDescent(optName, wts, derivs, learnRate, momentum);
		}

		if (mdl) {

			String mdlOptType = conf.getString("mdlOptimiser", optType);
			String mdlOptName = "mdl_dev_optimiser";

			if (mdlOptType.equals("rprop")) {
				mdlOptimiser = new Rprop(mdlOptName, mdlStdDevs, mdlStdDevDerivs, false);
			} else {
				mdlOptimiser = new SteepestDescent(mdlOptName, mdlStdDevs, mdlStdDevDerivs,
						conf.getDouble("mdlLearnRate", learnRate + ""), conf.getDouble("mdlMomentum", momentum + ""));
			}

			// to do
			// SAVE(mdlPriorMean);
			// SAVE(mdlPriorVariance);
			// WeightContainer::instance().save_by_conns(mdlStdDevs,
			// "_mdl_devs");
			// WeightContainer::instance().save_by_conns(weightCosts,
			// "_mdl_weight_costs");

		}

		// SAVE(epoch);
	}

	public void update_weights() {
		optimiser.update_weights();
		WeightContainer.reset_derivs();
	}

	public double differentiate(DataSequence seq) {

		double error = 0;

		if (mdl) {

			mdlSeqErrors.clear();
			mdlMlErrors = null;

			if ((mdlSamples > 1) || (seqsPerWeightUpdate > 1)) {
				// flood(mdlSeqDerivs, derivs.size(), 0); the resize of array is
				// carefully considered
				mdlSeqDerivs = Helpers.flood(mdlSeqDerivs, derivs.length, 0);
				mdlOldDerivs = derivs;
			}

			// to do
		} else {
			seq = apply_distortions(seq);
			error = net.train(seq);
			revert_distortions();
		}

		return error;
	}

	public DataSequence apply_distortions(DataSequence seq) {

		// to do

		return seq;
	}

	public void revert_distortions() {
		// to do
	}

	DatasetErrors calculate_errors(DataList data, DatasetErrors errors) {
		errors.clear();
		
		LOG.info("begin to use testset data.size:"+data.dataset.size()+" testFile:"+data.dataset.fileName);
		
		for (DataSequence seq = data.start(); seq != null; seq = data.next_sequence()) {
			net.calculate_errors(seq);
			//errors.add_seq_errors(netErrors, netNormFactors);
			errors.add_seq_errors(net.errors, net.normFactors);
			
		}
		errors.normalise();
		
		LOG.info("-------------------------------------------");
		LOG.info("lq Test epoch:" + epoch);
		LOG.info("testErrors.size:"+errors.getSeqNum());
		for (Map.Entry<String, Double> err : errors.errors.entrySet()) {
			LOG.info(err.getKey() + ":" + err.getValue());
		}
		LOG.info("-------------------------------------------");
		
		return errors;
	}

	public void train(String savename) {

		int totalEpochs = config.getInt("totalEpochs", "-1");
		int maxTestsNoBest = config.getInt("maxTestsNoBest", "20");

		// init filenames
		String bestSaveRoot = savename + ".best";
		String lastSaveFile = savename + ".last.save";

		int numWeights = wts.length;

		Map<String, PairError> bestTestErrors;
		Map<String, PairError> bestValErrors;
		Map<String, PairError> bestTrainErrors;

		int initEpoch = epoch;
		int seqsSinceWeightUpdate = 0;
		boolean stoppingCriteriaReached = false;
		totalEpochs = 2000;
		
		//debug calculate_errors
		if(testFiles!=null&&testFiles.dataset!=null&&testFiles.dataset.size()>0){
			calculate_errors(testFiles,testErrors);
		}	
		
		while (!stoppingCriteriaReached && (epoch < totalEpochs || totalEpochs < 0)) {

			double rtStart = Helpers.get_runtime();

			trainErrors.clear();
			matrixOps = 0;

			System.err.println("epoch:" + epoch);
			int selIndex = 0;
			for (DataSequence seq = trainFiles.start(); seq != null; seq = trainFiles.next_sequence()) {
				// System.err.println("epoch:"+epoch+"\tselIndex:"+selIndex);
				// DataSequence seq=trainFiles.start();
				selIndex++;
				if (selIndex != 1) {
					// continue;
				}
				differentiate(seq);
				update_weights();

				// add error
				// trainErrors.
				trainErrors.add_seq_errors(net.errors, net.normFactors);

				if (selIndex % 10 == 0) {
					try {
						System.gc();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			trainErrors.normalise();
			epoch++;
			LOG.info("-------------------------------------------");
			LOG.info("lq Trainer epoch:" + epoch);
			LOG.info("trainErrors.size:"+trainErrors.getSeqNum());
			for (Map.Entry<String, Double> err : trainErrors.errors.entrySet()) {
				LOG.info(err.getKey() + ":" + err.getValue());
			}
			LOG.info("-------------------------------------------");
			double rtEnd = Helpers.get_runtime();
			LOG.info("lq Trainer epoch:" + epoch + " totalTime:" + " time: " + (rtEnd - rtStart) + " s");
			System.err.println("lq Trainer epoch:" + epoch + " totalTime:" + " time: " + (rtEnd - rtStart) + " s");
			try {
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//use the test set
			if(epoch%2==0){
				
				if(testFiles!=null&&testFiles.dataset!=null&&testFiles.dataset.size()>0){
					calculate_errors(testFiles,testErrors);
				}			
			}
			
		} // end while

	}

}
