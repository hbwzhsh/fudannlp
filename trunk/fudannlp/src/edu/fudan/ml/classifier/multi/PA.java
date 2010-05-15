package edu.fudan.ml.classifier.multi;
import java.io.Serializable;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class PA implements MultiClassifier, Serializable {
	public Pipe pipes;
	SparseVector[] weight;
	private MaxSolver msolver;
	private Generator gen;
	private Alphabet labelAlphabet;
	public PA() {
	}
	public PA(SparseVector[] weights, MaxSolver msolver, 
			Generator gen, Pipe pipes, Alphabet labelAlphabet) {
		this.pipes = pipes;
		this.weight = weights.clone();
		this.gen = gen;
		gen.setStopIncrement(true);
		this.msolver = msolver;
		this.labelAlphabet = labelAlphabet;
		labelAlphabet.setStopIncrement(true);
	}
	public Object classify(Instance instance) {
		Object pred = msolver.getBest(instance, 1, new Object[]{weight});		
		return ((Results)pred).predList.get(0);
	}
	public String getLabel(Instance instance){
		Results pred = (Results) msolver.getBest(instance, 1, new Object[]{weight});
		return (String) labelAlphabet.lookupString((Integer) (pred.predList).get(0));
	}
}
