package edu.fudan.nlp.tag;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class MyPOS {
	Tagger tagger;
	public MyPOS() {
		InputStream is=this.getClass().getResourceAsStream("/model/peopledaily.pos.model.gz");   
		tagger = new Tagger();
		try {
			tagger.readModel(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public MyPOS(String str) {
		tagger = new Tagger();
		try {
			tagger.readModel(str);
//			tagger.saveModel(str+".gz");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String tag(String src) {
		List data = new ArrayList(src.length());
		for(int i=0; i<src.length(); i++)
			data.add(new String[]{src.substring(i, i+1)});
		Instance inst = new Instance(data);
		tagger.tag(inst);
		String[] target = (String[])inst.getTarget();
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<src.length(); i++) {
			String label = target[i];
			sb.append(src.charAt(i));
			if(label.startsWith("E") || label.startsWith("S")) {
				sb.append('/');
				sb.append(label.substring(2));
				sb.append(' ');
			}
		}
		return new String(sb);
	}
	public String tag1(String src) {
		src = src.replaceAll("。", "。 ");
		StringBuilder sb = new StringBuilder();
		String[] s = src.split("\\s+");
		for(int i=0;i<s.length;i++){
			sb.append(tag(s[i]));
		}
		return sb.toString();
	}
}
