package edu.fudan.ml.pipe;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
import gnu.trove.TIntDoubleIterator;
public class TFIDF extends Pipe {
	int[] idf;
	private int docNum;
	public TFIDF(int[] idf, int docNum) {
		this.idf = idf;
		this.docNum = docNum;
	}
	@Override
	public void addThruPipe(Instance inst) {
		SparseVector data = (SparseVector) inst.getData();
		TIntDoubleIterator it = data.vector.iterator();
		for (int i = data.vector.size(); i-- > 0;) {
			it.advance();
			if(idf[it.key()]>0)
				data.put(it.key(), it.value()*Math.log(docNum/idf[it.key()]));
		}
	}
}
