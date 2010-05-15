package edu.fudan.nlp.resources;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
public class CiLin {
	public static HashSet buildSynonymSet(String fileName){
		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream(fileName),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			HashSet<String> synSet = new HashSet<String>();
			int c=0;
			String str = bin.readLine();
			while(str!=null&&str.length()==0){
				String[] strs = str.trim().split(" ");
				if(strs[0].endsWith("=")){
					int wordNum = Integer.parseInt(strs[1]);
					for(int i=2;i<2+wordNum-1;i++){
						for(int j=i+1;j<2+wordNum;j++){
							String combine1 = strs[i]+"|"+strs[j];
							System.out.println(combine1 + c);
							synSet.add(combine1);
							String combine2 = strs[j]+"|"+strs[i];
							synSet.add(combine2);
							c++;
						}
					}
				}else{
				}
				str = bin.readLine();
			}
			return synSet;
		}catch(Exception e){
			return null;
		}
	}
	public static void main(String[] argv){
		HashSet<String> synSet = buildSynonymSet("\\\\10.11.7.3\\f$\\对于共享版《同义词词林》的改进\\improvedThesaurus.data");
	}
}
