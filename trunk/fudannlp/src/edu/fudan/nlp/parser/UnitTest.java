package edu.fudan.nlp.parser;
import java.util.Iterator;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class UnitTest {
	public static void main(String[] args)	{
		double c = 0.01;
		Treebank tb = new Treebank("../corpus/ctb", Treebank.Type.CTB);
		InstanceSet trainSet = tb.getTrainSet();
		for(int i = 0; i < trainSet.size(); i++)	{
			Instance tree = trainSet.getInstance(i);
			TreeTransformer.transformTree((Tree<String>)tree.getData());
		}
		Grammar grammar = new Grammar(trainSet);
		CKYParser parser = new CKYParser(grammar, true);
		TreeLoss loss = new TreeLoss();
		SparseVector weight = new SparseVector();
		Tree2Vector featureGenerator = new Tree2Vector(grammar);
		trainSet.shuffle();
		for(int i = 0; i < trainSet.size(); i++)	{
			System.out.print(i+": ");
			Instance inst = trainSet.getInstance(i);
			Tree<String> gold = (Tree<String>) inst.getData();
			Object[] pred = (Object[]) parser.getBest(inst, 1, new Object[]{weight});
			Tree<String> guess = (Tree<String>) pred[0];
			double diff = loss.calc(gold, guess);
			if (guess.isTerminal())
				continue;
			if (diff > 0)	{
				System.out.print(diff+" -> ");
				double goldScore = parser.getScore(gold);
				double guessScore = parser.getScore(guess);
				diff -= (goldScore-guessScore);
				SparseVector sv = featureGenerator.getVector(new Instance(gold));
				SparseVector sv2 = featureGenerator.getVector(new Instance(guess));
				sv.minus(sv2);
				diff /= sv.l2Norm2();
				if (Double.isNaN(diff))
					continue;
				sv.scalarMultiply(diff);
				weight.add(sv);
				pred = (Object[]) parser.getBest(inst, 1, new Object[]{weight});
				diff = loss.calc(pred[0], inst.getTarget());
				System.out.print(diff);
			}
			System.out.println();
		}
	}
}
