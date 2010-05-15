package edu.fudan.ml.solver;
import java.io.Serializable;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.Tree;
import edu.fudan.ml.types.SparseVector;
public abstract class MaxSolver implements Serializable{
	Generator featureGen;
	public MaxSolver(Generator featureGen) {
		this.featureGen = featureGen;
	}
	public abstract Object getBest(Instance inst, int n, Object[] params);
}
