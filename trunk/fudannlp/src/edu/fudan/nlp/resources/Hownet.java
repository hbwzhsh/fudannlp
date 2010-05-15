package edu.fudan.nlp.resources;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.map.MultiValueMap;
public class Hownet {
	public static void buildSynonymSet(String fileName){
		MultiValueMap map = new MultiValueMap();
		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream(fileName),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			int c=0;
			String str;
			while((str = bin.readLine())!=null && str.length()!=0){
				if(str.contains("W_C=")){
					String word = str.substring(4, str.length());
					str = bin.readLine();
					int idx1 = str.indexOf("[");
					int indx2 = str.indexOf("]");
					String pinyin = str.substring(idx1+1, indx2);
					if(pinyin.contains("?")){
						continue;
					}
					pinyin = pinyin.replaceAll("[0-9\\s]", ""); //去掉声调，空格
					Collection yin =  map.getCollection(word);
					if(yin==null){
						map.put(word,pinyin);
					}else if(!yin.contains(pinyin))
						map.put(word,pinyin);
				}
			}
		}catch(Exception e){
		}
////		fio.startWrite("./data/spell.word.txt");
//				//					fio.writeLine(word+" "+pinyin);
	}
	public static void main(String[] argv){
		buildSynonymSet("/volumes/f$/Corpus/HowNet_Full_Version_2007_08 Folder/Data/HowNet.txt");
	}
}
