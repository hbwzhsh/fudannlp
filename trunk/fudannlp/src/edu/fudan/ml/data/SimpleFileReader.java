package edu.fudan.ml.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import edu.fudan.ml.types.Instance;
public class SimpleFileReader extends Reader {
	String content = null;
	BufferedReader reader;
	public SimpleFileReader(String file){
		try {
			File f = new File(file);
			FileInputStream in = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public boolean hasNext() {
		try {
			content = reader.readLine();
			if(content==null){
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
		String[] toks = content.split("\\s");
		return new Instance (toks[1], toks[0]);
	}
}
