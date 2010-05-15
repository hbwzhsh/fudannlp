package edu.fudan.nlp.yamaparser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import edu.fudan.ml.classifier.PATrainer;
import edu.fudan.ml.classifier.Perceptron;
import edu.fudan.ml.classifier.PerceptronTrainer;
import edu.fudan.ml.loss.ZeroOneLoss;
import edu.fudan.ml.solver.LinearMax;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class ParserTrainer {
	String dataPath;
	Charset charset;
	List<String> datalist;
	String tmpfile;
	public ParserTrainer(String data) throws Exception {
		this(data, "GB18030");
	}
	public ParserTrainer(String dataPath, String charset) throws Exception {
		this.dataPath = dataPath;
		this.charset = Charset.forName(charset);
	}
	private void buildInstanceList(String file) throws Exception {
		System.out.print("generating training instances ");
		int count = 0;
		if (datalist == null) {
			datalist = new ArrayList<String>();
		}
		BufferedReader instanceReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), charset));
		tmpfile = dataPath + "/train.features";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(tmpfile), charset));
		Sentence instance = Sentence.readSentence(instanceReader);
		while (instance != null) {
			count++;
			if (count % 1000 == 0) {
				System.out.print('.');
			}
			ParsingState state = new ParsingState(instance);
			while (!state.isFinalState()) {
				int[] lr = state.getFocusIndices();
				String lpos = instance.postags[lr[0]];
				if (!datalist.contains(lpos)) {
					datalist.add(lpos);
				}
				HashMap<String, Integer> features = state.getFeatures();
				ParsingState.Action action = getAction(lr[0], lr[1],
						instance.heads);
				switch (action) {
				case LEFT:
					bw.write("1");
					break;
				case RIGHT:
					bw.write("2");
					break;
				default:
					bw.write("0");
				}
				bw.write(" ");
				bw.write(instance.postags[lr[0]]);
				for (Iterator<Map.Entry<String, Integer>> iter = features
						.entrySet().iterator(); iter.hasNext();) {
					Map.Entry<String, Integer> element = iter.next();
					bw.write(" ");
					bw.write(element.getKey());
				}
				// bw.write(new Integer(indices.get(i)).toString() + ":1 ");
				bw.write("\n");
				state.next(action);
				if (action == ParsingState.Action.LEFT)
					instance.heads[lr[1]] = -1;
				if (action == ParsingState.Action.RIGHT)
					instance.heads[lr[0]] = -1;
			}
			bw.write('\n');
			bw.flush();
			instance = Sentence.readSentence(instanceReader);
		}
		bw.close();
		instanceReader.close();
		System.out.println(" finished");
		System.out.printf("%d instances have benn loaded.\n\n", count);
	}
	public void train(String dataFile, int maxite, double eps) throws Exception {
		buildInstanceList(dataFile);
		Iterator ite = datalist.iterator();
		while (ite.hasNext()) {
			String pos = (String) ite.next();
			InstanceSet instset = readInstanceSet(pos);
			Alphabet alphabet = instset.getLabelAlphabet();
			System.out.printf("Training with data: %s\n", pos);
			System.out.printf("number of labels: %d\n", alphabet.size());
			Features generator = new Features();
			LinearMax solver = new LinearMax(generator, alphabet,2);
			ZeroOneLoss loss = new ZeroOneLoss();
			PerceptronTrainer trainer = new PerceptronTrainer(solver,
					generator, loss, maxite, eps);
			Perceptron model = trainer.train(instset);
			saveBinModel(model, pos);
			instset = null;
			generator = null;
			solver = null;
			loss = null;
			trainer = null;
			System.out.println();
		}
		System.gc();
	}
	private InstanceSet readInstanceSet(String pos) throws Exception {
		InstanceSet instset = new InstanceSet();
		instset.setLabelAlphabet(new Alphabet());
		Alphabet alphabet = instset.getLabelAlphabet();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(tmpfile), charset));
		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.matches("^$"))
				continue;
			List<String> tokens = Arrays.asList(line.split("\\s+"));
			if (pos.equals(tokens.get(1))) {
				edu.fudan.ml.types.Instance inst = new edu.fudan.ml.types.Instance(
						tokens.subList(2, tokens.size()));
				inst.setTarget(alphabet.lookupIndex(tokens.get(0)));
				instset.add(inst);
			}
		}
		in.close();
		return instset;
	}
	private void saveBinModel(Perceptron model, String pos)
			throws Exception {
		ObjectOutputStream outstream = new ObjectOutputStream(
				new FileOutputStream(dataPath + '/' + pos + ".model.cz"));
		outstream.writeObject(model);
		outstream.close();
	}
//				new FileOutputStream(dataPath + '/' + pos + ".model")));
	private ParsingState.Action getAction(int l, int r, int[] heads) {
		if (heads[l] == r && modifierNumOf(l, heads) == 0)
			return ParsingState.Action.RIGHT;
		else if (heads[r] == l && modifierNumOf(r, heads) == 0)
			return ParsingState.Action.LEFT;
		else
			return ParsingState.Action.SHIFT;
	}
	private int modifierNumOf(int h, int[] heads) {
		int n = 0;
		for (int i = 0; i < heads.length; i++)
			if (heads[i] == h)
				n++;
		return n;
	}
	public static void main(String[] args) throws Exception {
		ParserTrainer trainer = new ParserTrainer("data/dp/model");
		trainer.train("data/dp/chtb.v5.mst.train.dat", 20, 1e-4);
	}
}
