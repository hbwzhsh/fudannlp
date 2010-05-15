package edu.fudan.ml.types;
import java.util.Iterator;
public abstract class FeatureArray<T> extends SparseArray<T> {
	protected int increSize = 8;
	public abstract void increment(int idx);
	public abstract Iterator iterator();
}
