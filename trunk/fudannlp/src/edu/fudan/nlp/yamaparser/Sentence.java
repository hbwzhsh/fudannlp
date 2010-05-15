package edu.fudan.nlp.yamaparser;
import java.io.*;
import java.util.*;
public class Sentence {
	String[] forms;
	String[] postags;
	int[] heads;
	public HashSet[] modifiersOfAll;
 	public Sentence(String[] forms, String[] postags, int[] heads) {
 		this.forms = forms;
 		this.postags = postags;
 		this.heads = heads;
 		modifiersOfAll = new HashSet[heads.length];
 		for(int i = 0; i < heads.length; i++) {
 			modifiersOfAll[i] = new HashSet();
 		}
 		for(int i = 0; i < heads.length; i++) {
 			if (heads[i] != -1)
 				modifiersOfAll[heads[i]].add(i);
 		}
 	}
 	public int length() {
 		return forms.length;
 	}
 	static public Sentence readSentence (BufferedReader inputReader) throws Exception {
//				new FileInputStream(file), "UTF8"));
 		String line = inputReader.readLine();
 		String pos_line = inputReader.readLine();
 		String heads_line = inputReader.readLine();
 		inputReader.readLine();
 		if(line == null) {
 		    inputReader.close();
 		    return null;
 		}
 		String[] forms = line.split("\t");
 		String[] postags = pos_line.split("\t");
 		int[] heads = Util.stringsToInts(heads_line.split("\t"));
 		for (int i = 0; i < heads.length; i++)
 			heads[i]--;
 		return new Sentence(forms, postags, heads);
 	}
 	public void writeInstance(BufferedWriter outputWriter) throws Exception {
 		outputWriter.write(Util.join(forms, '\t') + "\n");
 		outputWriter.write(Util.join(postags, '\t') + "\n");
 		String[] strHeads = new String[heads.length];
 		for (int i = 0; i < heads.length; i++)
 			strHeads[i] = new Integer(heads[i] + 1).toString();
 		outputWriter.write(Util.join(strHeads, '\t') + "\n");
 		outputWriter.write("\n");
 		outputWriter.flush();
 	}
 	public void clearDependency() {
 		for (int i = 0; i < heads.length; i++) {
 			heads[i] = -1;
 		}
 	}
}
