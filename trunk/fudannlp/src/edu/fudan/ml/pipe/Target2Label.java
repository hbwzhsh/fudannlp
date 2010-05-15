package edu.fudan.ml.pipe;
import java.util.List;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Target2Label extends Pipe{
	private Alphabet labelAlphabet;
	public Target2Label(Alphabet labelAlphabet){
		this.labelAlphabet = labelAlphabet;
	}
	@Override
	public void addThruPipe(Instance instance) {
		Object t = instance.getTarget();
		if (t==null) return;
		if (t instanceof List) {
			List l = (List) t;
			int[] newTarget = new int[l.size()];
			for (int i=0; i<l.size(); i++) {
				int index = (int) labelAlphabet.lookupIndex((String) l.get(i));
				newTarget[i] = index;
			}
			instance.setTarget(newTarget);
		}else {
			instance.setTarget(labelAlphabet.lookupIndex((String) t));
		}
	}
}
