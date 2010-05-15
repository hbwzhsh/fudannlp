package edu.fudan.nlp.tag;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class MySEG {
	Tagger tagger;
	public MySEG() {
		InputStream is=this.getClass().getResourceAsStream("/model/peopledaily.seg.model.gz");   
		tagger = new Tagger();
		try {
			tagger.readModel(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public MySEG(String str) {
		tagger = new Tagger();
		try {
			tagger.readModel(str);
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
			if(label.equals("E") || label.equals("S")) {
				sb.append("  ");
			}
		}
		return new String(sb);
	}
}
