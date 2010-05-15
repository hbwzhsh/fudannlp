package edu.fudan.ml.classifier.seq;
import java.io.Serializable;
import java.util.List;
import edu.fudan.ml.classifier.Classifier;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Perceptron implements Classifier, Serializable {
	double[] weights;
	private MaxSolver msolver;
	private Alphabet labelAlphabet;
	public Pipe pipe;
	public Perceptron() {
	}
	public Perceptron(double[] sv, MaxSolver msolver, Alphabet labelAlphabet,Pipe pipe) {
		this.weights = sv;
		this.msolver = msolver;
		this.labelAlphabet = labelAlphabet;
		this.pipe = pipe;
	}
	public Object classify(Instance instance) {
		List pred = (List) msolver.getBest(instance, 1, new Object[]{weights});
		return pred.get(0);
	}
	public String[] predict(Instance instance) {
		List pred = (List) msolver.getBest(instance, 1, new Object[]{weights});
		int[] label =  (int[]) pred.get(0);
		String[] tag = new String[label.length];
		for(int i=0;i<label.length;i++){
			tag[i] = labelAlphabet.lookupString(label[i]);
		}
		return tag;
	}
	public double[] getWeight() {
		return weights;
	}
}
