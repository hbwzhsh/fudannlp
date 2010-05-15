package edu.fudan.ml.feature.generator;
import java.io.Serializable;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
public abstract class Generator implements Serializable {
	public Alphabet alphabet=null;
	public Generator(){
		alphabet = new Alphabet();
	}
	public void setStopIncrement(boolean stopIncrement){
		alphabet.setStopIncrement(stopIncrement);
	}
	public Alphabet getAlphabet() {
		return this.alphabet;
	}
	public int[] get(Instance inst){
		return get(inst,inst.getTarget());
	}
	public  int[] get(Instance inst,Object label){
		return null;
	}
	public SparseVector getVector(Instance inst){
		return getVector(inst,inst.getTarget());
	}
	public  abstract SparseVector getVector(Instance inst, Object object);
}
