package edu.fudan.ml.pipe;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class Normalize extends Pipe {
	@Override
	public void addThruPipe(Instance instance) {
		SparseVector data = (SparseVector) instance.getData();
		data.normalize();
	}
}
