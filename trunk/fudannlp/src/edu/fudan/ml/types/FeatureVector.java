package edu.fudan.ml.types;
public class FeatureVector extends SparseVector{
	private static final long serialVersionUID = 1430382793902727454L;
	private Alphabet alphabet=null;
	public FeatureVector(Alphabet alphabet)	{
		super();
		this.alphabet = alphabet;
	}
	public long addFeature(String feature)	{
		return addFeature(feature, 1.0);
	}
	public int addFeature(String feature, double weight)	{
		int index = alphabet.lookupIndex(feature);
		if(index!=-1)
		vector.put(index, weight);
		return index;
	}
	public int size()	{
		return vector.size();
	}
	public double lookup(int idx)	{
		if (vector.containsKey(idx))
			return vector.get(idx);
		else
			return 0;
	}
	public int[] locations()	{
		int[] indices = vector.keys();
		return indices;
	}
	public Alphabet getAlphabet()	{
		return alphabet;
	}
	public void setAlphabet(Alphabet featureAlphabet) {
		this.alphabet=featureAlphabet;
	}
}
