package edu.fudan.nlp.parser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import edu.fudan.ml.classifier.PATrainer;
import edu.fudan.ml.classifier.Perceptron;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class CKYParserTrainer {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		String path = "../corpus/ctb";
		Treebank.Type type = Treebank.Type.CTB;
		int maxIter = 10;
		boolean viterbi = false;
		String modelPath = "wsj.pa.model";
		double C = 0.001;
		Treebank tb = new Treebank(path, type);
		InstanceSet trainSet = tb.getTrainSet();
		for (int i = 0; i < trainSet.size(); i++) {
			Instance tree = trainSet.getInstance(i);
			TreeTransformer.transformTree((Tree<String>) tree.getData());
		}
		Grammar grammar = new Grammar(trainSet);
		Tree2Vector featureGenerator = new Tree2Vector(grammar);
		CKYParser parser = new CKYParser(grammar, viterbi);
		TreeLoss loss = new TreeLoss();
		PATrainer trainer = new PATrainer(parser, featureGenerator, loss, 20,
				1e-10);
		Perceptron model = trainer.train(trainSet);
		saveBinModel(model, modelPath);
	}
	private static void saveBinModel(Perceptron model, String modelPath)
			throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(modelPath + ".cz"));
		out.writeObject(model);
		out.close();
	}
}
