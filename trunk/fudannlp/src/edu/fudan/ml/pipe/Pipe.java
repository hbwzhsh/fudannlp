package edu.fudan.ml.pipe;
import java.io.Serializable;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public abstract class Pipe implements Serializable{
	public abstract void addThruPipe(Instance inst);
	public void process(InstanceSet instList) {
		for(int i=0;i<instList.size();i++){
			Instance inst = instList.getInstance(i);
			addThruPipe(inst);
		}
	}
}
