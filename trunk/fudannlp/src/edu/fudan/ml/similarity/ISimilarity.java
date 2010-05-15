package edu.fudan.ml.similarity;
public interface ISimilarity {
	public <E> float calc(E item1,E item2) throws Exception;
}
