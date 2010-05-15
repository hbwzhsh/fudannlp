package edu.fudan.ml.feature.generator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
public class TokenGenerator extends Generator implements Serializable{
	public TokenGenerator(){
		super();
	}
	@Override
	public FeatureVector getVector(Instance inst, Object label) {
		List data = (List<String>) inst.getData();
		FeatureVector fv = new FeatureVector(alphabet);
		Iterator it = data.iterator();
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()){
			sb.delete(0, sb.length());
			Object token = it.next();
			sb.append(token);
			sb.append("@");
			sb.append(label);
			fv.addFeature(sb.toString());
		}
		fv.addFeature("1@"+label);
		return fv;
	}
}
