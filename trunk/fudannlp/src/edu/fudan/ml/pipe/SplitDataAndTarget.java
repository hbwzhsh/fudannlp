package edu.fudan.ml.pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class SplitDataAndTarget extends Pipe {
	public void addThruPipe(Instance instance) {
		List seq = (List)instance.getData();
		List data = new ArrayList();
		List target = new ArrayList();
		for(int i=0; i<seq.size(); i++) {
			String[] arr = (String[])seq.get(i);
			data.add(Arrays.copyOfRange(arr, 0, arr.length-1));
			target.add(arr[arr.length-1]);
		}
		instance.setData(data);
		instance.setTarget(target);
	}
}
