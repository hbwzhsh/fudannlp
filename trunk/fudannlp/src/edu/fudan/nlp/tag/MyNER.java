package edu.fudan.nlp.tag;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class MyNER {
	Tagger tagger;
	public MyNER() {
		InputStream is=this.getClass().getResourceAsStream("/model/peopledaily.ner.model.gz");   
		tagger = new Tagger();
		try {
			tagger.readModel(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public MyNER(String file){
		tagger = new Tagger();
		try {
			tagger.readModel(file);
			//			tagger.saveModel(file+".gz");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public String tag2(String src) {
		return tag2(src).toString();
	}
	public HashMap<String,String> tag(String src) {
		List data = new ArrayList(src.length());
		for(int i=0; i<src.length(); i++)
			data.add(new String[]{src.substring(i, i+1)});
		Instance inst = new Instance(data);
		tagger.tag(inst);
		String[] target = (String[])inst.getTarget();
		HashMap<String,String> map = analysis(src,target);
		return map;
	}
	private HashMap<String,String> analysis(String src, String[] target) {
		HashMap<String,String> map = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		String label=null;
		for(int i=0; i<src.length(); i++) {
			if(target[i].equals("O")){
				if(sb.length()>0){
					map.put(sb.toString(), label.substring(2));
					sb = new StringBuilder();
					label=null;
				}
				continue;
			}
			label = target[i];
			sb.append(src.charAt(i));
		}
		return map;
	}
}
