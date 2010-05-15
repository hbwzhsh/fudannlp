package edu.fudan.ml.pipe;
import java.util.ArrayList;
import edu.fudan.ml.types.Instance;
public class String2BES extends Pipe{
	boolean hasLabel = true;
	public String2BES(boolean b){
		hasLabel = b;
	}
	@Override
	public void addThruPipe(Instance inst) {
		String str = (String) inst.getData();
		ArrayList data = new ArrayList();
		ArrayList label = new ArrayList();
		char[] sent = str.toCharArray();
		for(int i=0;i<str.length();i++){
			if (sent[i]!=' ') {
				data.add(sent[i]);
				if(hasLabel){
					if(i==0){
						label.add('B');
					}else{
						if(sent[i-1]==' '){
							if(i+1==sent.length||sent[i+1]==' ')
								label.add('S');
							else
								label.add('B');
						}else
							label.add('E');
					}
				}
			}
		}
		inst.setData(data);
		inst.setTarget(label);
	}
}
