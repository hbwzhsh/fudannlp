package edu.fudan.nlp.parser;
import edu.fudan.ml.types.SparseHashArray;
public abstract class Counter<T> extends SparseHashArray<T> {
	protected double[] counter = new double[0];
	public double getCount(T t) {
		int cur = super.fetch(t);
		if (cur < 0)
			return 0;
		return counter[cur];
	}
	public void put(int idx, Rule r){
		throw new UnsupportedOperationException();
	}
	public void put(Rule r)	{
		throw new UnsupportedOperationException();
	}
}
