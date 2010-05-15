package edu.fudan.ml.feature.generator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Templet implements Serializable {
	public static int gid = 0; 
	static Pattern parser = 
		Pattern.compile("(?:%(x|y)\\[(-?\\d+)(?:,(\\d+))?\\])");
	String templet;
	transient Matcher matcher;
	int[] vars;
	int order;
	int id;
	public Templet(String templet) {
		this.id = gid++;
		this.templet = templet;
		this.matcher = parser.matcher(this.templet);
		List<String> l = new ArrayList<String>();
		while (matcher.find()) {
			if (matcher.group(1).equals("y")) {
				l.add(matcher.group(2));
			}
		}
		vars = new int[l.size()];
		for(int j=0; j<l.size(); j++) {
			vars[j] = Integer.parseInt(l.get(j));
		}
		Arrays.sort(vars);
		order = vars[vars.length-1]-vars[0];
		matcher.reset();
	}
	public int[] getVars() { return this.vars; }
	public int getOrder() { return this.order; }
	public void generateAt(Instance instance, int pos, 
			int[] fv, Alphabet features, int numLabels) {
		List data = (List) instance.getData();
		int i,t,j,k;
		Matcher m = this.matcher;
		m.reset();
		StringBuffer sb = new StringBuffer();
		sb.append(id); sb.append(':');
		while (m.find()) {
			String rp = "";
			if (m.group(1).equals("x")) {
				j = Integer.parseInt(m.group(2));
				k = Integer.parseInt(m.group(3));
				if(pos+j<0 || pos+j>=data.size()) {
					if(pos+j<0) rp="//S"+String.valueOf(pos+j)+"//";
					if(pos+j>=data.size()) rp="//E+"+String.valueOf(pos+j-data.size()+1)+"//";
				}else
				rp = "//"+((String[]) data.get(pos+j))[k]+"//";
			} else if (m.group(1).equals("y")) {
				j = Integer.parseInt(m.group(2));
				if(pos+j<0 || pos+j>=data.size()) return;
				rp = "";
			}
			if (-1 != rp.indexOf('$')) rp = rp.replaceAll("\\$", "\\\\\\$");
			m.appendReplacement(sb, rp);
		}
		m.appendTail(sb);
		int index = features.lookupIndex(sb.toString(), (int)Math.pow(numLabels, order+1));
		fv[this.id] = index;
	}
	public String toString() {
		return this.templet;
	}
}
