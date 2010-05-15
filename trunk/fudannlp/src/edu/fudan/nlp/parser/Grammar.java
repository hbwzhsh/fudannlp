package edu.fudan.nlp.parser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseArray;
import edu.fudan.ml.types.SparseHashArray;
public class Grammar implements Serializable {
	private static final long serialVersionUID = 5672825321267352227L;
	int wordThre = 5;
	int numOfPOS = 0;
	SparseHashArray<String> stateList;
	SparseHashArray<String> wordList;
	SparseArray<Boolean> isPOSTag;
	SparseHashArray<Rule> ruleList;
	HashMap<Integer, List<UnaryRule>> unaryRuleMap;
	HashMap<Integer, List<BinaryRule>> binaryRuleMap;
	RuleCounter ruleCounter;
	LexicalCounter lexCounter;
	public Grammar(InstanceSet corpus) {
		stateList = new SparseHashArray<String>();
		wordList = new SparseHashArray<String>();
		for (int i = 0; i < corpus.size(); i++) {
			Tree<String> sample = (Tree<String>) corpus.getInstance(i)
					.getData();
			Iterator<Tree<String>> ite = sample.iterator();
			while (ite.hasNext()) {
				Tree<String> cur = ite.next();
				if (cur.isLeaf()) {
					wordList.put(cur.getLabel());
				} else if (cur.isPreTerminal()) {
					stateList.put(cur.getLabel());
				} else {
					stateList.put(cur.getLabel());
				}
			}
		}
		isPOSTag = new SparseArray<Boolean>();
		ruleList = new SparseHashArray<Rule>();
		ruleCounter = new RuleCounter();
		lexCounter = new LexicalCounter();
		unaryRuleMap = new HashMap<Integer, List<UnaryRule>>();
		binaryRuleMap = new HashMap<Integer, List<BinaryRule>>();
		for (int i = 0; i < corpus.size(); i++) {
			Tree<String> sample = (Tree<String>) corpus.getInstance(i)
					.getData();
			Iterator<Tree<String>> ite = sample.iterator();
			while (ite.hasNext()) {
				Tree<String> cur = ite.next();
				if (cur.isPreTerminal()) {
					addLexicalRule(cur);
				} else if (!cur.isLeaf()) {
					switch (cur.getChildren().size()) {
					case 1:
						addUnaryRule(cur);
						break;
					case 2:
						addBinaryRule(cur);
						break;
					}
				}
			}
		}
		numOfPOS = isPOSTag.size();
		initializeGrammar();
		ruleCounter = null;
		System.gc();
	}
	private void initializeGrammar() {
		for(int i = 0; i < stateList.size(); i++) {
			String pStateStr = stateList.get(i);
			if (isPOSTag.get(i) != null)
				continue;
			int totalCount = 0;
			List<UnaryRule> unaryRules = getUnaryRulesByParent(i);
			for (UnaryRule r : unaryRules) {
				totalCount += ruleCounter.getCount(r);
			}
			List<BinaryRule> binaryRules = getBinaryRulesByParent(i);
			for (BinaryRule r : binaryRules) {
				totalCount += ruleCounter.getCount(r);
			}
			for (UnaryRule r : unaryRules) {
				double prob = ruleCounter.getCount(r) / totalCount;
				r.setScore(Math.log(prob));
			}
			for (BinaryRule r : binaryRules) {
				double prob = ruleCounter.getCount(r) / totalCount;
				r.setScore(Math.log(prob));
			}
		}
	}
	private void addLexicalRule(Tree<String> cur) {
		lexCounter.increment(cur.getChild(0).getLabel());
		lexCounter.increment(cur.getLabel());
		int idx = stateList.fetch(cur.getLabel());
		isPOSTag.put(idx, true);
		StringBuffer buf = new StringBuffer();
		buf.append(cur.getLabel());
		buf.append("\\");
		buf.append(cur.getChild(0).getLabel());
		lexCounter.increment(buf.toString().intern());
		buf = null;
	}
	private void addBinaryRule(Tree<String> cur) {
		int pState = stateList.fetch(cur.getLabel());
		int lState = stateList.fetch(cur.getChild(0).getLabel());
		int rState = stateList.fetch(cur.getChild(1).getLabel());
		BinaryRule r = new BinaryRule(pState, lState, rState);
		if (!binaryRuleMap.containsKey(pState))
			binaryRuleMap.put(pState, new ArrayList<BinaryRule>());
		List<BinaryRule> l = binaryRuleMap.get(pState);
		if (!l.contains(r))
			l.add(r);
		ruleList.put(r);
		ruleCounter.increment(r);
	}
	private void addUnaryRule(Tree<String> cur) {
		int pState = stateList.fetch(cur.getLabel());
		int cState = stateList.fetch(cur.getChild(0).getLabel());
		UnaryRule r = new UnaryRule(pState, cState);
		if (!unaryRuleMap.containsKey(pState))
			unaryRuleMap.put(pState, new ArrayList<UnaryRule>());
		List<UnaryRule> l = unaryRuleMap.get(pState);
		if (!l.contains(r))
			l.add(r);
		ruleList.put(r);
		ruleCounter.increment(r);
	}
	public boolean isPOSTag(int idx) {
		Boolean ret = isPOSTag.get(idx);
		if (ret == null)
			return false;
		return true;
	}
	public SparseHashArray<String> getStatesList() {
		return stateList;
	}
	public int getUnaryRule(int pState, int cState) {
		return ruleList.fetch(new UnaryRule(pState, cState));
	}
	public int getBinaryRule(int pState, int lState, int rState) {
		return ruleList.fetch(new BinaryRule(pState, lState, rState));
	}
	public List<UnaryRule> getUnaryRulesByParent(int pState) {
		if (!unaryRuleMap.containsKey(pState))
			return Collections.emptyList();
		return unaryRuleMap.get(pState);
	}
	public List<BinaryRule> getBinaryRulesByParent(int pState) {
		if (!binaryRuleMap.containsKey(pState))
			return Collections.emptyList();
		return binaryRuleMap.get(pState);
	}
	public double getLexicalScore(String word, String tag) {
		double wCnt = lexCounter.getCount(word);
		StringBuffer buf = new StringBuffer(tag);
		buf.append("\\");
		buf.append(word);
		double wtCnt = lexCounter.getCount(buf.toString());
		if (wtCnt < wordThre) {
			wtCnt = 0;
		}
		buf = null;
		return (wtCnt + 1) / (wCnt + numOfPOS);
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(stateList);
		out.writeObject(wordList);
		out.writeObject(isPOSTag);
		out.writeObject(ruleList);
		out.writeObject(binaryRuleMap);
		out.writeObject(unaryRuleMap);
		out.writeObject(lexCounter);
		out.writeInt(wordThre);
		out.writeInt(numOfPOS);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		stateList = (SparseHashArray<String>) in.readObject();
		wordList = (SparseHashArray<String>) in.readObject();
		isPOSTag = (SparseArray<Boolean>) in.readObject();
		ruleList = (SparseHashArray<Rule>) in.readObject();
		binaryRuleMap = (HashMap<Integer, List<BinaryRule>>) in.readObject();
		unaryRuleMap = (HashMap<Integer, List<UnaryRule>>) in.readObject();
		lexCounter = (LexicalCounter) in.readObject();
		wordThre = in.readInt();
		numOfPOS = in.readInt();
	}
}
