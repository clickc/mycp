package org.click.media.rnn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.click.media.rnn.activationFunctions.Identity;
import org.click.media.rnn.activationFunctions.Logistic;
import org.click.media.rnn.activationFunctions.Softsign;
import org.click.media.rnn.activationFunctions.Tanh;

import edu.stanford.nlp.util.CollectionUtils;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.concurrent.MulticoreWrapper;
import edu.stanford.nlp.util.concurrent.ThreadsafeProcessor;

/**
 * size_t int real_t double
 * 
 * @author lq
 */
public class Mdrnn {

	static Logger LOG = Logger.getLogger(Mdrnn.class.getName());

	Map<String, List<Connection>> connections = new HashMap<>();
	Vector<Layer> hiddenLayers = new Vector<>();
	Vector<Vector<Layer>> hiddenLevels = new Vector<>();
	InputLayer inputLayer;

	Vector<NetworkOutput> outputs = new Vector<>();
	Vector<Layer> outputLayers = new Vector<>();
	Vector<Boolean> bidirectional;
	Vector<Boolean> symmetry;
	Vector<Integer> inputBlock;
	Layer inputBlockLayer;
	BiasLayer bias = new BiasLayer();
	Vector<Layer> recurrentLayers;

	Map<String, Double> errors = new HashMap<>();
	Map<String, Double> normFactors = new HashMap<>();
	Vector<String> criteria = new Vector<>();
	// private final MulticoreWrapper<Pair<Collection<Vector<Integer>>,
	// FeedforwardParams>, Cost2> jobHandler;

	public Mdrnn(ConfigFile conf, DataHeader data) {

		inputLayer = new InputLayer("input", data.numDims, data.inputSize, data.inputLabels);
		bidirectional = conf.getBooleanVector("bidirectional", true, data.numDims);
		symmetry = conf.getBooleanVector("symmetry", false, data.numDims);
		inputBlock = conf.getIntVector("inputBlock", 0, data.numDims);

		System.err.println("inputBlock:" + inputBlock);
		if (Helpers.in(inputBlock, 0)) {
			System.err.println("zero is in inputBlock");
			inputBlockLayer = null;
		} else {
			// null should be blocklayer to do
			inputBlockLayer = add_layer(new BlockLayer(inputLayer, inputBlock), false, false);
		}

		// jobHandler = new MulticoreWrapper<>(4, new CostFunction2(), false);

		// LOG.info("construct
		// inputLayer.outputActivations.shape:"+inputLayer.outputActivations.shape);
	}

	public class Cost2 {

	}

	private class CostFunction2
			implements ThreadsafeProcessor<Pair<Collection<Vector<Integer>>, FeedforwardParams>, Cost2> {

		@Override
		public ThreadsafeProcessor<Pair<Collection<Vector<Integer>>, FeedforwardParams>, Cost2> newInstance() {
			// TODO Auto-generated method stub
			return new CostFunction2();
		}

		@Override
		public Cost2 process(Pair<Collection<Vector<Integer>>, FeedforwardParams> input) {
			// TODO Auto-generated method stub
			Collection<Vector<Integer>> examples = input.first();
			FeedforwardParams params = input.second();
			List<Connection> connRange = params.getConnRange();
			Layer layer = params.getLayer();

			Iterator<Vector<Integer>> it = examples.iterator();

			while (it.hasNext()) {

				Vector<Integer> coord = it.next();
				if (connRange != null) {
					for (Connection c : connRange) {
						c.feed_forward(coord);
					}
				}

				layer.feed_forward(coord);

			}

			return new Cost2();
		}

	}

	/**
	 * default : addBias false recurrent false
	 * 
	 * @param layer
	 * @param addBias
	 * @param recurrent
	 * @return
	 */
	public Layer add_layer(Layer layer, boolean addBias, boolean recurrent) {
		// to do
		hiddenLayers.add(layer);

		System.err.println("layer " + layer.name + " is_mirror:" + is_mirror(layer));

		if (!is_mirror(layer)) {

			if (addBias) {
				add_bias(layer);
			}

			if (recurrent) {
				make_layer_recurrent(layer);
			}

		}

		return layer;
	}

	public Layer add_layer(String type, String name, int size, Vector<Integer> directions, boolean addBias,
			boolean recurrent) {
		// to do
		Layer layer = null;
		System.err.println("type:" + type);
		if (type.equals("tanh")) {
			layer = new NeuronLayer<Tanh>(name, directions, size, new Tanh());
		} else if (type.equals("softsign")) {
			layer = new NeuronLayer<Softsign>(name, directions, size, new Softsign());
		} else if (type.equals("logistic")) {
			layer = new NeuronLayer<Logistic>(name, directions, size, new Logistic());
		} else if (type.equals("identity")) {
			layer = new IdentityLayer(name, directions, size);
		} else if (type.equals("lstm")) {
			System.out.println("add layer lstm name:"+name+" directions:"+directions+" size:"+size);
			layer = new LstmLayer<Tanh, Tanh, Logistic>(name, directions, size, 1, null, new Tanh(), new Tanh(),
					new Logistic());
		} else if (type.equals("linear_lstm")) {
			layer = new LstmLayer<Tanh, Identity, Logistic>(name, directions, size, 1, null, new Tanh(), new Identity(),
					new Logistic());
		} else if (type.equals("softsign_lstm")) {
			layer = new LstmLayer<Softsign, Softsign, Logistic>(name, directions, size, 1, null, new Softsign(),
					new Softsign(), new Logistic());
		} else {
			System.err.println("layer type:" + type + " unknown");
		}

		return add_layer(layer, addBias, recurrent);
	}

	public Layer get_input_layer() {

		System.err.println("inputBlockLayer:" + inputBlockLayer);
		return inputBlockLayer != null ? inputBlockLayer : inputLayer;

	}

	public int add_hidden_level(String type, int size, boolean recurrent, String name, boolean addBias) {

		int levelNum = hiddenLevels.size();
		// vector not need to significantly resized
		//
		// hiddenLevels.resize(levelNum + 1);
		hiddenLevels.add(new Vector<Layer>());
		System.out.println("add layer name:"+name+" type:"+type+" size:"+size+" num_seq_dims:"+num_seq_dims());
		add_hidden_layers_to_level(type, size, recurrent, name, 0, levelNum, Helpers.createVector(num_seq_dims(), 1),
				addBias);

		return levelNum;
	}

	public Layer add_hidden_layers_to_level(String type, int size, boolean recurrent, String name, int dim,
			int levelNum, Vector<Integer> directions, boolean addBias) {
		System.out.println("add_hidden_layers_to_level dim:" + dim + " num_seq_dims:" + num_seq_dims() + " levelNum:" + levelNum
				+ " directions:" + directions+" size:"+size);
		if (dim == num_seq_dims()) {
			System.err.println("dim:" + dim + " num_seq_dims:" + num_seq_dims() + " levelNum:" + levelNum
					+ " directions:" + directions);
			String fullName = name + "_" + hiddenLevels.get(levelNum).size();

			Layer layer = add_layer(type, fullName, size, directions, addBias, recurrent);
			hiddenLevels.get(levelNum).add(layer);
			return layer;
		} else {

			if (bidirectional.get(dim)) {
				// System.err.println("lq add_hidden_layers_to_level dim:"+dim+"
				// bidirectional true");
				directions.set(dim, -1);
				add_hidden_layers_to_level(type, size, recurrent, name, dim + 1, levelNum, directions, addBias);
			}

			directions.set(dim, 1);
			return add_hidden_layers_to_level(type, size, recurrent, name, dim + 1, levelNum, directions, addBias);

		}

	}

	public int num_seq_dims() {
		return bidirectional.size();
	}

	public boolean is_mirror(Layer layer) {

		for (int i = 0; i < symmetry.size(); i++) {
			if (symmetry.get(i) && layer.directions.get(i) < 0) {
				return true;
			}
		}

		return false;

	}

	public FullConnection add_bias(Layer layer) {

		Vector<Integer> noDelay = new Vector<>();

		return connect_layers(bias, layer, noDelay);

	}

	public FullConnection connect_layers(Layer from, Layer to, Vector<Integer> delay) {

		FullConnection conn = new FullConnection(make_name(from, to, delay), from, to, delay, null);
		add_connection(conn);

		return conn;
	}

	public Connection add_connection(Connection conn) {

		if (!(connections.containsKey(conn.to.name))) {
			connections.put(conn.to.name, new ArrayList<Connection>());
		}

		connections.get(conn.to.name).add(conn);

		return conn;
	}

	public void make_layer_recurrent(Layer layer) {

		
		Vector<Integer> delay = Helpers.createVector(layer.num_seq_dims(), 0);
		System.out.println("make_layer_recurrent delay:"+delay);
		for (int i = 0; i < delay.size(); i++) {
			delay.set(i, -layer.directions.get(i));
			connect_layers(layer, layer, delay);
			delay.set(i, 0);
		}

	}

	public void connect_to_hidden_level(Layer from, int levelNum) {

		for (int i = 0; i < hiddenLevels.get(levelNum).size(); i++) {
			Layer to = hiddenLevels.get(levelNum).get(i);
			if (!is_mirror(to)) {
				Vector<Integer> noDelay = new Vector<>();
				connect_layers(from, to, noDelay);
			}
		}

	}

	public Layer add_output_layer(NetworkOutput output, boolean addBias) {

		Layer layer = (Layer) output;
		outputLayers.add(layer);

		if (addBias) {
			add_bias(layer);
		}

		outputs.add(output);

		return layer;
	}

	public Layer collapse_layer(Layer src, Layer dest, Vector<Boolean> activeDims) {

		Layer layer = add_layer(new CollapseLayer(src, dest, activeDims), false, false);
		add_connection(new CopyConnection(layer, dest));

		return layer;
	}

	public void connect_from_hidden_level(int levelNum, Layer to) {

		for (int i = 0; i < hiddenLevels.get(levelNum).size(); i++) {
			Layer from = hiddenLevels.get(levelNum).get(i);
			Vector<Integer> noDelay = new Vector<>();
			connect_layers(from, to, noDelay);
		}

	}

	public void build() {
		// to do
	}

	public void feed_forward(DataSequence seq) {

		errors.clear();
		inputLayer.copy_inputs(seq.inputs);

		Layer layer;

		// LOG.info("lq Mdrnn feed_forward
		// hiddenLayers.size:"+hiddenLayers.size());
		int hlIndex = 0;
		for (int i = 0; i < hiddenLayers.size(); i++) {
			layer = hiddenLayers.get(i);
			// LOG.info("lq Mdrnn feed_forward hidden layer:"+layer.name+"
			// hlIndex:"+hlIndex);
			feed_forward_layer(layer);
			hlIndex++;
		}

		// LOG.info("lq Mdrnn feed_forward
		// outputLayers.size:"+outputLayers.size());
		int olIndex = 0;
		for (int i = 0; i < outputLayers.size(); i++) {
			layer = outputLayers.get(i);
			// LOG.info("lq Mdrnn feed_forward output layer:"+layer.name+"
			// olIndex:"+olIndex);
			feed_forward_layer(layer);
			olIndex++;
		}

	}

	private static class FeedforwardParams {

		/**
		 * Size of the entire mini-batch (not just the chunk that might be
		 * fed-forward at this moment).
		 */
		private Layer layer;

		private List<Connection> connRange;

		private FeedforwardParams(Layer layer, List<Connection> connRange) {
			this.layer = layer;
			this.connRange = connRange;
		}

		public Layer getLayer() {
			return layer;
		}

		public List<Connection> getConnRange() {
			return connRange;
		}

	}

	public void feed_forward_layer(Layer layer) {

		double rtStart = Helpers.get_runtime();
		// System.err.println("feed_forward layer:"+layer+" outputActivations
		// shape:"+layer.outputActivations.shape+"
		// inputActivations.shape:"+layer.inputActivations.shape);

		layer.start_sequence();
		// LOG.info("lq Mdrnn feed_forward_layer data "+layer.dataSizeInfo());
		// LOG.info("lq Mdrnn feed_forward_layer shape "+layer.shapeSizeInfo());
		// LOG.info("lq Mdrnn feed_forward_layer depth "+layer.depthSizeInfo());
		List<Connection> connRange = connections.get(layer.name);

		CoordIterator ci = layer.input_seq_begin();

		// what is cilen,product(shape)?
		// the shape should not include depth
		// LOG.info("feed_forward layer:"+layer.name+" shape:"+ci.shape+"
		// directions:"+ci.directions+" start.point:"+ci.point());
		/*
		 * List<Vector<Integer>> coords=new ArrayList<>(); FeedforwardParams
		 * params = new FeedforwardParams(layer, connRange);
		 * 
		 * 
		 * for(int i=0;i<RangeUtils.product(ci.shape);i++){
		 * coords.add(ci.point()); ci.increment(); }
		 * 
		 * List<List<Vector<Integer>>> chunks =
		 * CollectionUtils.partitionIntoFolds(coords, 4); for
		 * (Collection<Vector<Integer>> chunk : chunks) jobHandler.put(new
		 * Pair<>(chunk, params)); jobHandler.join(false);
		 */

		int ciSize = RangeUtils.product(ci.shape);
		// for(int i=0;i<RangeUtils.product(ci.shape);i++){
		for (int i = 0; i < ciSize; i++) {
			if (connRange != null) {
				for (Connection c : connRange) {
					c.feed_forward(ci.point());
				}
			}

			layer.feed_forward(ci.point());

			ci.increment();

		}

		double rtEnd = Helpers.get_runtime();
		// LOG.info("lq Mdrnn feed_forward_layer totalTime layer:"+layer.name+"
		// time: "+(rtEnd-rtStart)+" s");

	}

	public double calculate_output_errors(DataSequence seq) {
		double error = 0;
		errors.clear();
		// System.err.println("outputs.size:"+outputs.size());
		if (outputs.size() == 1) {
			NetworkOutput l = outputs.firstElement();
			error = l.calculate_errors(seq);
			errors = l.errorMap;
			normFactors = l.normFactors;
		} else {
			normFactors.clear();
			for (NetworkOutput l : outputs) {
				error += l.calculate_errors(seq);
				String layerPrefix = l.getName() + "_";

				Map<String, Double> tempErrorMap = l.getErrorMap();

				for (Map.Entry<String, Double> entry : tempErrorMap.entrySet()) {

					if (!(errors.containsKey(entry.getKey()))) {
						errors.put(entry.getKey(), entry.getValue());
					} else {
						errors.put(entry.getKey(), errors.get(entry.getKey()) + entry.getValue());
					}

					errors.put(layerPrefix + entry.getKey(), entry.getValue());

				}

				Map<String, Double> tempNormFactors = l.getNormFactors();

				for (Map.Entry<String, Double> entry : tempNormFactors.entrySet()) {

					if (!(normFactors.containsKey(entry.getKey()))) {
						normFactors.put(entry.getKey(), entry.getValue());
					} else {
						normFactors.put(entry.getKey(), normFactors.get(entry.getKey()) + entry.getValue());
					}

					normFactors.put(layerPrefix + entry.getKey(), entry.getValue());

				}
			}

		}

		return error;
	}

	public double calculate_errors(DataSequence seq) {
		feed_forward(seq);
		return calculate_output_errors(seq);
	}

	public boolean is_recurrent(Layer layer) {

		List<Connection> connRange = connections.get(layer.name);

		if (connRange == null) {
			return false;
		}

		// System.err.println("layer.name:"+layer.name);
		for (Connection c : connRange) {
			if (c.from.name.equals(c.to.name)) {
				return true;
			}
		}

		return false;
	}

	public void feed_back_layer(Layer layer) {
		double rtStart = Helpers.get_runtime();
		// System.err.println("feed_back layer:"+layer+" outputActivations
		// shape:"+layer.outputActivations.shape+"
		// inputActivations.shape:"+layer.inputActivations.shape);

		List<Connection> connRange = connections.get(layer.name);

		CoordIterator ci = layer.input_seq_rbegin();

		for (int i = 0; i < ci.size(); i++) {

			layer.feed_back(ci.point());
			if (connRange != null) {
				for (Connection c : connRange) {
					c.feed_back(ci.point());
				}
			}

			ci.increment();
		}

		CoordIterator ci2 = layer.input_seq_rbegin();
		for (int i = 0; i < ci2.size(); i++) {

			layer.update_derivs(ci2.point());
			if (connRange != null) {
				for (Connection c : connRange) {
					c.update_derivs(ci2.point());
				}
			}

			ci2.increment();
		}

		double rtEnd = Helpers.get_runtime();
		// LOG.info("lq Mdrnn feed_back_layer totalTime layer:"+layer.name+"
		// time: "+(rtEnd-rtStart)+" s");

	}

	public void feed_back() {

		Layer layer;

		for (int i = outputLayers.size() - 1; i >= 0; i--) {
			layer = outputLayers.get(i);
			feed_back_layer(layer);
		}

		for (int i = hiddenLayers.size() - 1; i >= 0; i--) {
			layer = hiddenLayers.get(i);
			feed_back_layer(layer);
		}

	}

	public double train(DataSequence seq) {

		double error = calculate_errors(seq);

		// System.err.println("in Mdrnn train");
		feed_back();
		return error;
	}

	public String make_name(Layer f, Layer t, Vector<Integer> d) {

		String name;
		name = f.name + "_to_" + t.name;

		if (d.size() > 0) {
			name += "_delay_";
			for (int i = 0; i < d.size(); i++) {
				if (i != (d.size() - 1)) {
					name += (d.get(i) + "_");
				} else {
					name += (d.get(i));
				}
			}
		}

		return name;

	}

	/**
	 * print the network
	 */
	public void print() {

		prt_line();
		out(hiddenLayers.size() + 2 + " layers");
		out(inputLayer);

		Layer layer;
		for (int i = 0; i < hiddenLayers.size(); i++) {
			layer = hiddenLayers.get(i);
			if (is_recurrent(layer)) {
				outIn("(R) ");
			}
			out(layer);
		}

		for (int i = 0; i < outputLayers.size(); i++) {
			layer = outputLayers.get(i);
			out(layer);
		}

		prt_line();

		out(connections.size() + " connections:");

		List<Connection> conns = null;

		for (Map.Entry<String, List<Connection>> entry : connections.entrySet()) {
			conns = entry.getValue();
			for (Connection con : conns) {
				out(con);
			}
		}

	}

	public void out(Object s) {
		LOG.info(s.toString());
	}

	public void outIn(Object s) {
		LOG.info(s.toString());
	}

	public void prt_line() {
		LOG.info("------------------------------");
	}

}
