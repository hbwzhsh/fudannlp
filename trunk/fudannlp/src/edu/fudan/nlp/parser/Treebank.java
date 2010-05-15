package edu.fudan.nlp.parser;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import edu.fudan.ml.data.TreebankReader;
import edu.fudan.ml.types.InstanceSet;
public class Treebank {
	String path;
	Type type;
	public enum Section {
		TRAIN, DEV, TEST, VALID
	}
	public enum Type {
		WSJ, CTB, SINGLE
	}
	public Treebank(String path, Type type) {
		this.path = path;
		this.type = type;
	}
	public InstanceSet getTrainSet() {
		System.out.println("loading training data ...");
		return loadTreebank(Section.TRAIN);
	}
	public InstanceSet getDevSet() {
		System.out.println("loading develop data ...");
		return loadTreebank(Section.DEV);
	}
	public InstanceSet getTestSet() {
		System.out.println("loading testing data ...");
		return loadTreebank(Section.TEST);
	}
	public InstanceSet getValidSet() {
		System.out.println("loading validation data ...");
		return loadTreebank(Section.VALID);
	}
	private InstanceSet loadTreebank(Section section) {
		InstanceSet dataSet = null;
		if (type == Type.WSJ)
			dataSet = loadWSJ(section);
		if (type == Type.CTB)
			dataSet = loadCTB(section);
		if (type == Type.SINGLE)
			dataSet = loadSingle(section);
		System.out.printf("%d trees have been loaded.\n", dataSet.size());
		return dataSet;
	}
	private InstanceSet loadCTB(Section section) {
		InstanceSet dataSet = new InstanceSet();
		String suffix = "fid";
		System.out.print("Loading Chinese Treebank ... ");
		try {
			if (section == Section.TRAIN) {
				dataSet.addAll(TreebankReader.readTrees(path, 26, 270, suffix, Charset
						.forName("GB18030")));
				dataSet.addAll(TreebankReader.readTrees(path, 400, 1151, suffix, Charset
						.forName("GB18030")));
			} else if (section == Section.DEV) {
				dataSet.addAll(TreebankReader.readTrees(path, 26, 270, suffix, Charset
						.forName("GB18030")));
			} else if (section == Section.TEST) {
				dataSet.addAll(TreebankReader.readTrees(path, 271, 300, suffix, Charset
						.forName("GB18030")));
			} else if (section == Section.VALID) {
				dataSet.addAll(TreebankReader.readTrees(path, 301, 325, suffix, Charset
						.forName("GB18030")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		return dataSet;
	}
	private InstanceSet loadWSJ(Section section) {
		InstanceSet dataSet = new InstanceSet();
		String suffix = "MRG";
		System.out.print("Loading English Treebank ... ");
		try {
			if (section == Section.TRAIN) {
				dataSet.addAll(TreebankReader.readTrees(path, 200, 2199, suffix, Charset
						.forName("UTF-8")));
			} else if (section == Section.DEV) {
				dataSet.addAll(TreebankReader.readTrees(path, 2200, 2299, suffix, Charset
						.forName("UTF-8")));
			} else if (section == Section.TEST) {
				dataSet.addAll(TreebankReader.readTrees(path, 2300, 2399, suffix, Charset
						.forName("UTF-8")));
			} else if (section == Section.VALID) {
				dataSet.addAll(TreebankReader.readTrees(path, 2100, 2199, suffix, Charset
						.forName("UTF-8")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		return dataSet;
	}
	private InstanceSet loadSingle(Section section) {
		InstanceSet dataSet = new InstanceSet();
		System.out.print("Loading Treebank ... ");
		try {
			if (section == Section.TRAIN) {
				dataSet.addAll(TreebankReader.readTrees(path, "train", Charset
						.defaultCharset()));
			} else if (section == Section.DEV) {
				dataSet.addAll(TreebankReader.readTrees(path, "dev", Charset
						.defaultCharset()));
			} else if (section == Section.TEST) {
				dataSet.addAll(TreebankReader.readTrees(path, "test", Charset
						.defaultCharset()));
			} else if (section == Section.VALID) {
				dataSet.addAll(TreebankReader.readTrees(path, "valid", Charset
						.defaultCharset()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		return dataSet;
	}
}
