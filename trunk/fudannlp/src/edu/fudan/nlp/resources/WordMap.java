package edu.fudan.nlp.resources;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import edu.fudan.ml.similarity.EditDistance;
import edu.fudan.ml.similarity.EditDistanceWithSemantic;
import edu.fudan.ml.similarity.ISimilarity;
public class WordMap {
	private Map<String, String> nameMap;
	private String fileName;
	private String serFileName;
	ISimilarity is;
	public WordMap(String filename){
		fileName = filename;
		buildNameMap();
	}
	private void buildNameMap() {
		nameMap = Collections.synchronizedMap(new HashMap<String, String>()); 
		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream(fileName),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			String info = bin.readLine();
			while(info!=null&&info.length()>0){
				String[] toks = info.split("\\s+");
				for(int i=0;i<toks.length;i++){
					nameMap.put(toks[i], toks[0]);
				}
				info = bin.readLine();
			}
		}catch(Exception e){
		}
	}
	public String getMap(String word){
		if(nameMap==null||!nameMap.containsKey(word))
			return word;
		else
			return nameMap.get(word);
	}
	public String getLooseMap (String str) throws Exception {
		if(is==null)
			is = new EditDistanceWithSemantic();
		String resName = str;
		if(str==null||str.trim().length()==0)
			return resName;
		for(Iterator it = nameMap.keySet().iterator();it.hasNext();){
			str = (String) it.next();
			if(is.calc(str,resName)==0){
				resName = nameMap.get(str);
				System.out.println("匹配："+str+"<"+resName);
				break;
			}
		}
		return resName;
	}
}
