package edu.fudan.ml.pipe;
import java.util.ArrayList;
import edu.fudan.ml.types.Instance;
public class String2NgramToken extends Pipe{
	int[] gramSizes = null;
	public String2NgramToken(int[] sizes) {
		this.gramSizes = sizes;
	}
	public void addThruPipe(Instance instance) {
		String data = (String) instance.getData();
		ArrayList<String> list = new ArrayList<String>();
		for (int j = 0; j < gramSizes.length; j++) {
			int len = gramSizes[j];
			if (len <= 0 || len > data.length())
				continue;
			for (int i = 0; i < data.length() - len; i++) {
				list.add(data.substring(i, i + len));
			}
		}
		instance.setData(list);
	}
}
