package edu.fudan.ml.classifier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class Perceptron implements Classifier, Serializable {
	private static final long serialVersionUID = 1L;
	protected Pipe pipes;
	protected SparseVector weight;
	protected MaxSolver msolver;
	protected Generator gen;
	protected Alphabet labelAlphabet;
	public Perceptron() {
	}
	public Perceptron(SparseVector sv, MaxSolver msolver, 
			Generator gen, Pipe pipes, Alphabet labelAlphabet) {
		this.pipes = pipes;
		this.weight = sv;
		this.gen = gen;
		gen.setStopIncrement(true);
		this.msolver = msolver;
		this.labelAlphabet = labelAlphabet;
		labelAlphabet.setStopIncrement(true);
	}
	public Object classify(Instance instance) {
		Object pred = msolver.getBest(instance, 1, new Object[]{weight});
		return pred;
	}
	public Object classify(Instance instance, int n)	{
		return msolver.getBest(instance, n, new Object[]{weight});
	}
	public String getLabel(Instance instance){
		Results pred = (Results) msolver.getBest(instance, 1, new Object[]{weight});
		return  labelAlphabet.lookupString((Integer) pred.predList.get(0));
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(pipes);
		out.writeObject(weight);
		out.writeObject(gen);
		out.writeObject(msolver);
		out.writeObject(labelAlphabet);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		pipes = (Pipe) in.readObject();
		weight = (SparseVector) in.readObject();
		gen = (Generator) in.readObject();
		msolver = (MaxSolver) in.readObject();
		labelAlphabet = (Alphabet) in.readObject();
	}
	public Alphabet getLabelAlphabet() {
		return labelAlphabet;
	}
}
