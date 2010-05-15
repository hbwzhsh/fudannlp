package edu.fudan.nlp.parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseHashArray;
import edu.fudan.ml.types.SparseVector;
public class CKYParser extends MaxSolver {
	private static final long serialVersionUID = 1L;
	Grammar grammar;
	SparseHashArray<String> stateList;
	boolean useGoldPOS = true;
	int numStates;
	short length;
	double[][][] insideScore;
	double[][][] maxScore;
	Object[][][] trace;
	int startNonTerm;
	List<String> words;
	List<String> pos;
	boolean viterbi;
	SparseVector weight;
	public CKYParser(Grammar gr, boolean viterbi) {
		super(null);
		this.grammar = gr;
		this.stateList = gr.stateList;
		numStates = stateList.size();
		startNonTerm = stateList.fetch("ROOT");
		this.viterbi = viterbi;
	}
	public Tree<String> getBestParse(List<String> words, List<String> pos) {
		initializeChart(words, pos);
		doMaxScores();
		if (Double.NEGATIVE_INFINITY != maxScore[0][length][startNonTerm])
			return buildTree(0, length, startNonTerm);
		else
			return new Tree<String>(stateList.get(startNonTerm));
	}
	private Tree<String> buildTree(int start, int end, int state) {
		double bestScore = maxScore[start][end][state];
		String label = stateList.get(state);
		if ((end - start) == 1) {
			if (grammar.isPOSTag(state)) {
				List<Tree<String>> children = new ArrayList<Tree<String>>(1);
				children.add(new Tree<String>(words.get(start)));
				return new Tree<String>(label, children);
			} else {
				double maybeScore = Double.NEGATIVE_INFINITY;
				int maybeIndex = -1;
				List<UnaryRule> unaryRules = grammar
						.getUnaryRulesByParent(state);
				for (int i = 0; i < unaryRules.size(); i++) {
					UnaryRule r = unaryRules.get(i);
					if (r.cState == state || !grammar.isPOSTag(r.cState))
						continue;
					int idx = grammar.getUnaryRule(r.pState, r.cState);
					double score = maxScore[start][end][r.cState]
							+ weight.elementAt(idx);
					if (score > maybeScore
							&& Math.abs(score - bestScore) < 1e-10) {
						maybeScore = score;
						maybeIndex = r.cState;
					}
				}
				List<Tree<String>> children = new ArrayList<Tree<String>>();
				children.add(new Tree<String>(words.get(start)));
				if (maybeIndex == -1)
					maybeIndex = stateList.fetch(pos.get(start));
				Tree<String> internal = new Tree<String>(stateList
						.get(maybeIndex));
				internal.setChildren(children);
				children = new ArrayList<Tree<String>>();
				children.add(internal);
				return new Tree<String>(label, children);
			}
		} else {
			Object[] tr = (Object[]) trace[start][end][state];
			if (tr.length == 2) {
				BinaryRule rule = (BinaryRule) tr[0];
				int split = (Integer) tr[1];
				List<Tree<String>> children = new ArrayList<Tree<String>>(2);
				Tree<String> lchild = buildTree(start, split, rule.lState);
				Tree<String> rchild = buildTree(split, end, rule.rState);
				if (lchild != null)
					children.add(lchild);
				if (rchild != null)
					children.add(rchild);
				return new Tree<String>(label, children);
			} else if (tr.length == 1) {
				UnaryRule ur = (UnaryRule) tr[0];
				Tree<String> child = buildTree(start, end, ur.cState);
				List<Tree<String>> children = new ArrayList<Tree<String>>(1);
				children.add(child);
				return new Tree<String>(label, children);
			} else {
				throw new IllegalStateException();
			}
		}
	}
	protected boolean matches(double x, double y) {
		return (Math.abs(x - y) / (Math.abs(x) + Math.abs(y) + 1e-10) < 1e-5);
	}
	public double getScore(Tree<String> tree) {
		double score = 0;
		Iterator<Tree<String>> ite = tree.iterator();
		while (ite.hasNext()) {
			Tree<String> cur = ite.next();
			if (cur.isTerminal())
				continue;
			String label = cur.getLabel();
			int pState = stateList.fetch(label);
			if (!cur.isPreTerminal()) {
				if (cur.getChildren().size() == 1) {
					int cState = stateList.fetch(cur.getChild(0).getLabel());
					int id = grammar.getUnaryRule(pState, cState);
					score += weight.elementAt(id);
				} else if (cur.getChildren().size() == 2) {
					int lState = stateList.fetch(cur.getChild(0).getLabel());
					int rState = stateList.fetch(cur.getChild(1).getLabel());
					int id = grammar.getBinaryRule(pState, lState, rState);
					score += weight.elementAt(id);
				}
			} else {
				String tag = cur.getChild(0).getLabel();
				double lexScore = grammar.getLexicalScore(label, tag);
				score += lexScore;
			}
		}
		return score;
	}
	private void doMaxScores() {
		for (int span = 1; span <= length; span++) {
			for (int start = 0; start < (length - span + 1); start++) {
				int end = start + span;
				for (int pState = 0; pState < numStates; pState++) {
					List<BinaryRule> binaryRules = grammar
							.getBinaryRulesByParent(pState);
					for (int j = 0; j < binaryRules.size(); j++) {
						BinaryRule rule = binaryRules.get(j);
						int lState = rule.lState;
						int rState = rule.rState;
						int idx = grammar.getBinaryRule(pState, lState, rState);
						double ruleScore = weight.elementAt(idx);
						double oldScore = maxScore[start][end][pState];
						double bestScore = oldScore;
						int bestSplit = start;
						for (int split = start + 1; split < end; split++) {
							double lscore = maxScore[start][split][lState];
							if (Double.isInfinite(lscore))
								continue;
							double rscore = maxScore[split][end][rState];
							if (Double.NEGATIVE_INFINITY == rscore)
								continue;
							double score = lscore + rscore + ruleScore;
							if (score >= bestScore) {
								bestScore = score;
								bestSplit = split;
							}
						}
						if (bestScore > oldScore) {
							maxScore[start][end][pState] = bestScore;
							trace[start][end][pState] = new Object[] { rule,
									bestSplit };
						}
					}
				}
				for (int pState = 0; pState < numStates; pState++) {
					List<UnaryRule> unaryRules = grammar
							.getUnaryRulesByParent(pState);
					double oldScore = maxScore[start][end][pState];
					double bestScore = oldScore;
					UnaryRule bestRule = null;
					for (int j = 0; j < unaryRules.size(); j++) {
						UnaryRule rule = unaryRules.get(j);
						int cState = rule.cState;
						double cScore = maxScore[start][end][cState];
						if (Double.NEGATIVE_INFINITY == cScore)
							continue;
						int idx = grammar.getUnaryRule(pState, cState);
						double ruleScore = weight.elementAt(idx);
						double score = cScore + ruleScore;
						if (score >= bestScore) {
							Object[] track = (Object[]) trace[start][end][cState];
							if (track != null && track.length == 1)
								continue;
							bestScore = score;
							bestRule = rule;
						}
					}
					if (bestScore > oldScore) {
						maxScore[start][end][pState] = bestScore;
						trace[start][end][pState] = new Object[] { bestRule };
					}
				}
			}
		}
	}
	private void initializeChart(List<String> words, List<String> pos) {
		this.words = words;
		this.pos = pos;
		createChart();
		int begin = 0;
		int end = begin + 1;
		for (String word : words) {
			end = begin + 1;
			for (short tag = 0; tag < stateList.size(); tag++) {
				if (!grammar.isPOSTag(tag))
					continue;
				if (!useGoldPOS) {
					double lexScores = lexicalScore(word, tag);
					if (lexScores != 0) {
						if (!viterbi)
							insideScore[begin][end][tag] = lexScores;
						maxScore[begin][end][tag] = lexScores;
					}
				} else {
					if (stateList.get(tag).equals(pos.get(begin))) {
						double lexScores = lexicalScore(word, tag);
						if (lexScores != 0) {
							if (!viterbi)
								insideScore[begin][end][tag] = lexScores;
							maxScore[begin][end][tag] = lexScores;
						}
					}
				}
			}
			begin++;
		}
	}
	private double lexicalScore(String word, int tag) {
		return grammar.getLexicalScore(word, stateList.get(tag));
	}
	private void createChart() {
		destroyChart();
		length = (short) words.size();
		if (!viterbi)
			insideScore = new double[length][length + 1][];
		trace = new Object[length][length + 1][];
		maxScore = new double[length][length + 1][];
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length + 1; j++) {
				if (!viterbi) {
					insideScore[i][j] = new double[numStates];
					Arrays.fill(insideScore[i][j], 0);
				}
				trace[i][j] = new Object[numStates];
				maxScore[i][j] = new double[numStates];
				Arrays.fill(maxScore[i][j], Double.NEGATIVE_INFINITY);
			}
		}
	}
	private void destroyChart() {
		insideScore = maxScore = null;
		trace = null;
	}
	@Override
	public Object getBest(Instance inst, int n, Object[] params) {
		Object data = inst.getData();
		weight = (SparseVector) params[0];
		List<String> words = null;
		List<String> tags = null;
		if (data instanceof Tree) {
			Tree<String> sentence = (Tree<String>) inst.getData();
			words = sentence.getTerminals();
			tags = sentence.getPreTerminals();
		} else if (data instanceof List[]) {
			words = ((List[]) data)[0];
			tags = ((List[]) data)[1];
		} else {
			throw new UnsupportedOperationException();
		}
		List<Tree<String>> retList = new ArrayList<Tree<String>>(n);
		Tree<String> bestTree = getBestParse(words, tags);
		if (bestTree.isTerminal())
			return new Object[] {};
		retList.add(bestTree);
		return new Object[] { bestTree };
	}
}
