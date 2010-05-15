package edu.fudan.nlp.yamaparser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
public class ParserTester {
	Parser parser;
	public ParserTester(String modelPath)	{
		parser = new Parser(modelPath);
	}
	public void test(String testFile, String resultFile, String charset)
			throws Exception {
		int error = 0;
		int total = 0;
		System.out.println("Beginning the test ...");
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(testFile), charset));
		BufferedWriter outputWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(resultFile), charset));
		Sentence instance = Sentence.readSentence(inputReader);
		while (instance != null) {
			int[] golds = new int[instance.heads.length];
			System.arraycopy(instance.heads, 0, golds, 0, golds.length);
			parser.getBestParse(instance);
			int[] preds = instance.heads;
			error += diff(golds, preds);
			total += golds.length;
			golds = null;
			instance.writeInstance(outputWriter);
			instance = Sentence.readSentence(inputReader);
		}
		outputWriter.close();
		inputReader.close();
		parser = null;
		System.gc();
		System.out.printf("errate: %.6f\n", 1.0*error/total);
	}
	private int diff(int[] golds, int[] preds)	{
		int ret = 0;
		int[] ref = golds;
		if (golds.length > preds.length)
			ref = preds;
		for(int i = 0; i < ref.length; i++)
			if (golds[i] != preds[i])
				ret++;
		return ret;
	}
	public static void main(String[] args) throws Exception	{
//		ParserTester tester = new ParserTester("./Data/ctb_v6_d2_tuned/");
		ParserTester tester = new ParserTester("data/dp/model");
		tester.test("data/dp/chtb.v5.mst.test.dat", "data/dp/chtb.v5.pa.bin.result", "GB18030");
	}
}
