package edu.fudan.ml.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class svmFileReader extends Reader {
	String content = null;
	BufferedReader reader;
	int type = 1;
	public svmFileReader(String file) {
		try {
			File f = new File(file);
			FileInputStream in = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public svmFileReader(String file,int type) {
		this(file);
		this.type = 1;
	}
	public boolean hasNext() {
		try {
			content = reader.readLine();
			if (content == null) {
				reader.close();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public Instance next() {
		String[] tokens = content.split("\\t+|\\s+");
		SparseVector sv = new SparseVector();
		for (int i = 1; i < tokens.length; i++) {
			String[] taken = tokens[i].split(":");
			if (taken.length > 1) {
				double value = Double.parseDouble(taken[1]);
				int idx = Integer.parseInt(taken[0]);
				sv.put(idx, value);
			}
		}
		return new Instance(sv, tokens[0]);
	}
}
