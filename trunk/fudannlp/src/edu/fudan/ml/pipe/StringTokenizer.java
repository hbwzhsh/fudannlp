package edu.fudan.ml.pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class StringTokenizer extends Pipe {
	public void addThruPipe(Instance instance) {
		List data0 = (List) instance.getData();
		List data1 = new ArrayList();
		for(int i=0; i<data0.size(); i++) {
			String s = (String) data0.get(i);
			String[] arr = s.split("\\s+");
			data1.add(arr);
		}
		instance.setData(data1);
	}
}
