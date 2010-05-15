package edu.fudan.ml.feature.generator;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class SimpleGenerator extends Generator{
	public SparseVector getVector(Instance inst) {
		return (SparseVector) inst.getData();
	}
	public SparseVector getVector(Instance inst, Object object)	{
		return null;
	}
}
