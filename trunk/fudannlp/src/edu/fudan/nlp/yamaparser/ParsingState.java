package edu.fudan.nlp.yamaparser;
import java.util.*;
public class ParsingState {
	enum Action {
		SHIFT, LEFT, RIGHT
	};
	Sentence instance;
	ArrayList<Tree> subtrees;
	int leftFocus;
	private double[] probsOfBuild;
	private Action[] actionsOfBuild;
	private boolean isUpdated = false;
	private boolean isFinal = false;
	public ParsingState(Sentence instance) {
		subtrees = new ArrayList<Tree>();
		for (int i = 0; i < instance.length(); i++) {
			Tree tree = new Tree(i);
			subtrees.add(tree);
		}
		this.instance = instance;
		probsOfBuild = new double[subtrees.size() - 1];
		actionsOfBuild = new Action[subtrees.size() - 1];
	}
	public boolean isFinalState() {
		return subtrees.size() == 1 || isFinal;
	}
	public int[] getFocusIndices() {
		assert (!isFinalState());
		int[] indices = new int[2];
		indices[0] = subtrees.get(leftFocus).root;
		indices[1] = subtrees.get(leftFocus + 1).root;
		return indices;
	}
	public HashMap<String, Integer> getFeatures() {
		if (isFinalState())
			return null;
		int rightFocus = leftFocus + 1;
		HashMap<String, Integer> features = new HashMap<String, Integer>();
		int l = 2;
		int r = 2;
		for (int i = 0; i <= l; i++) {
			String posFeature = "-" + new Integer(i).toString() + "/pos/";
			String lexFeature = "-" + new Integer(i).toString() + "/lex/";
			String lcLexFeature = "-" + new Integer(i).toString() + "/ch-L-lex/";
			String lcPosFeature = "-" + new Integer(i).toString() + "/ch-L-pos/";
			String rcLexFeature = "-" + new Integer(i).toString() + "/ch-R-lex/";
			String rcPosFeature = "-" + new Integer(i).toString() + "/ch-R-pos/";
			if (leftFocus - i < 0) {
				features.put(posFeature + "START"
						+ new Integer(i - leftFocus).toString(), 1);
				features.put(lexFeature + "START"
						+ new Integer(i - leftFocus).toString(), 1);
			} else {
				features
						.put(
								posFeature
										+ instance.postags[subtrees
												.get(leftFocus - i).root], 1);
				features.put(lexFeature
						+ instance.forms[subtrees.get(leftFocus - i).root], 1);
				if (subtrees.get(leftFocus - i).leftChildren.size() == 0) {
					// features.put(lcLexFeature + "None", 1);
					// features.put(lcPosFeature + "None", 1);
				} else {
					for (int j = 0; j < subtrees.get(leftFocus - i).leftChildren
							.size(); j++) {
						int leftChildIndex = subtrees.get(leftFocus - i).leftChildren
								.get(j).root;
						features.put(lcLexFeature
								+ instance.forms[leftChildIndex], 1);
						features.put(lcPosFeature
								+ instance.postags[leftChildIndex], 1);
					}
				}
				if (subtrees.get(leftFocus - i).rightChildren.size() == 0) {
					// features.put(rcLexFeature + "None", 1);
					// features.put(rcPosFeature + "None", 1);
				} else {
					for (int j = 0; j < subtrees.get(leftFocus - i).rightChildren
							.size(); j++) {
						int rightChildIndex = subtrees.get(leftFocus - i).rightChildren
								.get(j).root;
						features.put(rcLexFeature
								+ instance.forms[rightChildIndex], 1);
						features.put(rcPosFeature
								+ instance.postags[rightChildIndex], 1);
					}
				}
			}
		}
		for (int i = 0; i <= r; i++) {
			String posFeature = "+" + new Integer(i).toString() + "/pos/";
			String lexFeature = "+" + new Integer(i).toString() + "/lex/";
			String lcLexFeature = "+" + new Integer(i).toString() + "/ch-L-lex/";
			String lcPosFeature = "+" + new Integer(i).toString() + "/ch-L-pos/";
			String rcLexFeature = "+" + new Integer(i).toString() + "/ch-R-lex/";
			String rcPosFeature = "+" + new Integer(i).toString() + "/ch-R-pos/";
			if (rightFocus + i >= subtrees.size()) {
				features.put(posFeature
						+ "END"
						+ new Integer(rightFocus + i - subtrees.size() + 1)
								.toString(), 1);
				features.put(lexFeature
						+ "END"
						+ new Integer(rightFocus + i - subtrees.size() + 1)
								.toString(), 1);
			} else {
				features.put(posFeature
						+ instance.postags[subtrees.get(rightFocus + i).root],
						1);
				features.put(lexFeature
						+ instance.forms[subtrees.get(rightFocus + i).root], 1);
				if (subtrees.get(rightFocus + i).leftChildren.size() == 0) {
					// features.put(lcLexFeature + "None", 1);
					// features.put(lcPosFeature + "None", 1);
				} else {
					for (int j = 0; j < subtrees.get(rightFocus + i).leftChildren
							.size(); j++) {
						int leftChildIndex = subtrees.get(rightFocus + i).leftChildren
								.get(j).root;
						features.put(lcLexFeature
								+ instance.forms[leftChildIndex], 1);
						features.put(lcPosFeature
								+ instance.postags[leftChildIndex], 1);
					}
				}
				if (subtrees.get(rightFocus + i).rightChildren.size() == 0) {
					// features.put(rcLexFeature + "None", 1);
					// features.put(rcPosFeature + "None", 1);
				} else {
					for (int j = 0; j < subtrees.get(rightFocus + i).rightChildren
							.size(); j++) {
						int rightChildIndex = subtrees.get(rightFocus + i).rightChildren
								.get(j).root;
						features.put(rcLexFeature
								+ instance.forms[rightChildIndex], 1);
						features.put(rcPosFeature
								+ instance.postags[rightChildIndex], 1);
					}
				}
			}
		}
		return features;
	}
	public void next(Action action, double prob) {
		probsOfBuild[leftFocus] = prob;
		actionsOfBuild[leftFocus] = action;
		leftFocus++;
		if (leftFocus >= subtrees.size() - 1) {
			if (!isUpdated) {
				int maxIndex = 0;
				double maxValue = 0;
				for (int i = 0; i < probsOfBuild.length; i++)
					if (probsOfBuild[i] > maxValue) {
						maxValue = probsOfBuild[i];
						maxIndex = i;
					}
				leftFocus = maxIndex;
				next(actionsOfBuild[leftFocus]);
			}
			back();
		}
	}
	private void back() {
		isUpdated = false;
		leftFocus = 0;
		probsOfBuild = new double[subtrees.size() - 1];
		actionsOfBuild = new Action[subtrees.size() - 1];
	}
	public void next(Action action) {
		// assert (action.equalsIgnoreCase("left")
		// || action.equalsIgnoreCase("right") || action
		// .equalsIgnoreCase("shift"));
		assert (!isFinalState());
		int lNode = subtrees.get(leftFocus).root;
		int rNode = subtrees.get(leftFocus + 1).root;
		switch (action) {
		case LEFT:
			subtrees.get(leftFocus).addRightChild(subtrees.get(leftFocus + 1));
			subtrees.remove(leftFocus + 1);
			isUpdated = true;
			break;
		case RIGHT:
			subtrees.get(leftFocus + 1).addLeftChild(subtrees.get(leftFocus));
			subtrees.remove(leftFocus);
			isUpdated = true;
			break;
		default:
			leftFocus++;
		}
		if (leftFocus >= subtrees.size() - 1) {
			if (!isUpdated) {
				isFinal = true;
			}
			back();
		}
	}
	public void saveRelation() {
		for (int i = 0; i < subtrees.size(); i++) {
			saveRelation(subtrees.get(i));
		}
	}
	private void saveRelation(Tree t) {
		for (int i = 0; i < t.leftChildren.size(); i++) {
			instance.heads[t.leftChildren.get(i).root] = t.root;
			saveRelation(t.leftChildren.get(i));
		}
		for (int i = 0; i < t.rightChildren.size(); i++) {
			instance.heads[t.rightChildren.get(i).root] = t.root;
			saveRelation(t.rightChildren.get(i));
		}
	}
}
class Tree {
	int root;
	ArrayList<Tree> leftChildren;
	ArrayList<Tree> rightChildren;
	public Tree(int root) {
		this.root = root;
		leftChildren = new ArrayList<Tree>();
		rightChildren = new ArrayList<Tree>();
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(new Integer(root).toString());
		sb.append(" ");
		for (int i = 0; i < leftChildren.size(); i++) {
			sb.append(leftChildren.get(i).toString());
		}
		sb.append("-");
		for (int i = 0; i < rightChildren.size(); i++) {
			sb.append(rightChildren.get(i).toString());
		}
		sb.append("]");
		return sb.toString();
	}
	public void addLeftChild(Tree lc) {
		leftChildren.add(0, lc);
	}
	public void addRightChild(Tree rc) {
		rightChildren.add(rc);
	}
}
