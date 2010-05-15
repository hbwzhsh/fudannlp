package edu.fudan.ml.pipe;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import gnu.trove.TIntDoubleIterator;
public class TF2IDF extends Pipe {
	public int[] idf;
	public TF2IDF(InstanceSet train, InstanceSet test) {
		int numFeatures = 0;
		for(int i=0;i<train.size();i++){
			int len = ((SparseVector)train.getInstance(i).getData()).len;
			if(len > numFeatures)
				numFeatures = len;
		}
		for(int i=0;i<test.size();i++){
			int len = ((SparseVector)test.getInstance(i).getData()).len;
			if(len > numFeatures)
				numFeatures = len;
		}
		idf = new int[numFeatures+1];
	}
	@Override
	public void addThruPipe(Instance inst) {
		SparseVector data = (SparseVector) inst.getData();
		TIntDoubleIterator it = data.vector.iterator();
		for (int i = data.vector.size(); i-- > 0;) {
			it.advance();
			idf[it.key()]++;
		}
	}
}
