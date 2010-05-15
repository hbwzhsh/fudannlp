package edu.fudan.nlp.yamaparser;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import edu.fudan.ml.classifier.Perceptron;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.LinearMax;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
import libsvm.*;
public class Parser {
	String dataPath;
	HashMap<String, HashMap<String, Integer>> featureAlphabetByPos;
	HashMap<String, Perceptron> modelByPos;
	String suffix = ".model.cz";
	Parser(String dataPath) {
		this.dataPath = dataPath;
		featureAlphabetByPos = new HashMap<String, HashMap<String, Integer>>();
		modelByPos = new HashMap<String, Perceptron>();
	}
	public double getBestParse(String[] words, String[] postags, int[] heads)
			throws Exception {
		Sentence instance = new Sentence(words, postags, heads);
		return getBestParse(instance);
	}
	public double getBestParse(Sentence instance) throws Exception {
		instance.clearDependency();
		double score = 0;
		ParsingState state = new ParsingState(instance);
		while (!state.isFinalState()) {
			int[] lr = state.getFocusIndices();
			HashMap<String, Integer> features = state.getFeatures();
			Instance inst = new Instance(
					features);
			String lpos = instance.postags[lr[0]];
			if (!modelByPos.containsKey(lpos)) {
//				 + ".train.model"));
				modelByPos.put(lpos, loadBinModel(dataPath + '/' + lpos
						+ ".model.cz"));
			}
			double[][] estimates = estimateActions(modelByPos.get(lpos), inst);
			if ((int) estimates[0][0] == 1)
				state.next(ParsingState.Action.LEFT);
			else if ((int) estimates[0][0] == 2)
				state.next(ParsingState.Action.RIGHT);
			else if ((int) estimates[0][1] == 1)
				state.next(ParsingState.Action.LEFT, estimates[1][1]);
			else
				state.next(ParsingState.Action.RIGHT, estimates[1][1]);
			if (estimates[0][0] != 0)
				score += Math.log10(estimates[1][0]);
			else
				score += Math.log10(estimates[1][1]);
		}
		state.saveRelation();
		return Math.exp(score);
	}
	private double[][] estimateActions(
			HashMap<String, Integer> featureAlphabet, svm_model model,
			HashMap<String, Integer> features) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (Iterator<Map.Entry<String, Integer>> iter = features.entrySet()
				.iterator(); iter.hasNext();) {
			Map.Entry<String, Integer> element = iter.next();
			String f = element.getKey();
			if (featureAlphabet.containsKey(f)) {
				int index = featureAlphabet.get(f);
				indices.add(index);
			}
		}
		Collections.sort(indices);
		svm_node[] x = new svm_node[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			x[i] = new svm_node();
			x[i].index = indices.get(i);
			x[i].value = 1;
		}
		int nr_class = svm.svm_get_nr_class(model);
		int[] labels = new int[nr_class];
		svm.svm_get_labels(model, labels);
		double[] prob_estimates = new double[nr_class];
		svm.svm_predict_probability(model, x, prob_estimates);
		ArrayList resultIndices = new ArrayList();
		for (int i = 0; i < prob_estimates.length; i++)
			resultIndices.add(i);
		class IndexComparor implements Comparator<Integer> {
			public IndexComparor(double[] content) {
				this.content = content;
			}
			public int compare(Integer o1, Integer o2) {
				if (content[o1] < content[o2])
					return 1;
				else if (content[o1] > content[o2])
					return -1;
				else
					return 0;
			}
			private double[] content;
		}
		Collections.sort(resultIndices, new IndexComparor(prob_estimates));
		double[][] result = new double[2][3];
		int i = 0;
		for (; i < prob_estimates.length; i++) {
			result[0][i] = labels[(Integer) resultIndices.get(i)];
			result[1][i] = prob_estimates[(Integer) resultIndices.get(i)];
		}
		indices = null;
		x = null;
		labels = null;
		prob_estimates = null;
		resultIndices = null;
		return result;
	}
	private double[][] estimateActions(Perceptron model,
			edu.fudan.ml.types.Instance inst) {
		Alphabet actionList = model.getLabelAlphabet();
		int numOfClasses = actionList.size();
		double[][] result = new double[2][numOfClasses];
		Object ret = (Object) model.classify(inst, numOfClasses);
		List guess = (List) ((Object[])ret)[0];
		List scores = (List) ((Object[])ret)[1];
		double total = 0;
		for (int i = 0; i < guess.size(); i++) {
			result[0][i] = Double.parseDouble(actionList.lookupString((Integer)guess.get(i)));
			result[1][i] = Math.exp((Double) scores.get(i));
			total += result[1][i];
		}
		for (int i = 0; i < guess.size(); i++) {
			result[1][i] = result[1][i] / total;
		}
		return result;
	}
	private HashMap<String, Integer> loadFeatureAlphabetOfPos(String pos)
			throws Exception {
		HashMap<String, Integer> featureAlphabet = new HashMap<String, Integer>();
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataPath + '/' + pos + suffix), Charset.defaultCharset()));
		String line = inputReader.readLine();
		while (line != null) {
			String[] feat_value = line.split(" ");
			featureAlphabet.put(feat_value[0], Integer.valueOf(feat_value[1]));
			line = inputReader.readLine();
		}
		inputReader.close();
		return featureAlphabet;
	}
	public Perceptron loadBinModel(String modelPath) throws Exception {
		ObjectInputStream instream = new ObjectInputStream(new FileInputStream(
				modelPath));
		Perceptron model = (Perceptron) instream.readObject();
		instream.close();
		return model;
	}
	private Perceptron loadTxtModel(String modelPath) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(modelPath)));
		String line = null;
		Alphabet labelAlphabet = new Alphabet();
		while ((line = in.readLine()) != null) {
			if (!line.matches("^$"))
				labelAlphabet.lookupIndex(line);
			else
				break;
		}
		Alphabet featureAlphabet = new Alphabet();
		SparseVector weight = new SparseVector();
		while ((line = in.readLine()) != null) {
			if (!line.matches("^$")) {
				String[] keyval = line.split("\\t+");
				featureAlphabet.lookupIndex(keyval[1]);
				weight.set(Integer.parseInt(keyval[0]), Double
						.parseDouble(keyval[2]));
			}
		}
		featureAlphabet.setStopIncrement(true);
		Features generator = new Features(featureAlphabet);
		LinearMax solver = new LinearMax(generator, labelAlphabet,2);
		return new Perceptron(weight, solver, generator, null, labelAlphabet);
	}
	public static void main(String[] args) throws Exception	{
		Parser parser = new Parser("Data/ctb_v6_d2_tuned");
		String[] words = new String[]{"维阿里", "也", "是", "属于", "少年得志", "。"};
		String[] pos = new String[]{"NR", "AD", "VC", "VV", "VV", "PU"};
		int[] heads = new int[words.length];
		parser.loadBinModel("data/dp/model/NR.model.cz");
		parser.getBestParse(words, pos, heads);
		for(int i = 0; i < heads.length; i++)
			System.out.printf("%s\t%s\t%d\n", words[i], pos[i], heads[i]);
	}
}
