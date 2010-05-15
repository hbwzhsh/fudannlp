package edu.fudan.nlp.parser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import edu.fudan.ml.classifier.Perceptron;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class CKYParserTester {
	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		String path = "../corpus/ctb";
		Treebank.Type type = Treebank.Type.CTB;
		boolean viterbi = true;
		String modelPath = "wsj.pa.model.cz";
		ObjectInputStream instream = new ObjectInputStream(new FileInputStream(
				modelPath));
		Perceptron model = (Perceptron) instream.readObject();
		instream.close();
		ParserEval eval = new ParserEval();
		Treebank tb = new Treebank(path, type);
		InstanceSet testSet = tb.getTestSet();
		for (int i = 0; i < testSet.size(); i++) {
			Instance tree = testSet.getInstance(i);
			TreeTransformer.transformTree((Tree<String>) tree.getData());
			Object ret = (Object)model.classify(tree);
			Tree<String> guess = (Tree<String>) ((Object[])ret)[0];
		}
	}
}
