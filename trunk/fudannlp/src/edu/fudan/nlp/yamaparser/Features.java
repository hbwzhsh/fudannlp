package edu.fudan.nlp.yamaparser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
public class Features extends Generator implements Serializable {
	public Features()	{
		super();
	}
	public Features(Alphabet alphabet)	{
		this.alphabet = alphabet;
	}
	@Override
	public FeatureVector getVector(Instance inst, Object label) {
		Object data = inst.getData();
		Iterator ite = null;
		if (data instanceof List)
			ite = ((List<String>)data).iterator();
		else if (data instanceof HashMap)
			ite = ((HashMap<String, Integer>)data).keySet().iterator();
		FeatureVector fv = new FeatureVector(alphabet);
		StringBuffer buff = new StringBuffer();
		while(ite.hasNext())	{
			buff.delete(0, buff.length());
			buff.append((String)ite.next());
			buff.append('@');
			buff.append(label);
			fv.addFeature(buff.toString());
		}
		buff = null;
		return fv;
	}
	public Alphabet getAlphabet()	{
		return alphabet;
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(alphabet);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		alphabet = (Alphabet) in.readObject();
	}
}
