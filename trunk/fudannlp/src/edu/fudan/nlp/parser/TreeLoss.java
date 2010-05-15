package edu.fudan.nlp.parser;
import java.util.HashSet;
import java.util.Set;
import edu.fudan.ml.loss.Loss;
public class TreeLoss implements Loss {
	Set<Constituent> goldSet = new HashSet<Constituent>();
	Set<Constituent> guessSet = new HashSet<Constituent>();
	Set<Constituent> correctSet = new HashSet<Constituent>();
	public double calc(Object o1, Object o2) {
		if (!(o1 instanceof Tree && o2 instanceof Tree))
			throw new IllegalArgumentException();
		int diff = 0;
		goldSet.clear();
		guessSet.clear();
		correctSet.clear();
		addConstituents((Tree<String>) o1, guessSet, 0);
		addConstituents((Tree<String>) o2, goldSet, 0);
		correctSet.addAll(goldSet);
		correctSet.retainAll(guessSet);
		diff = goldSet.size() + guessSet.size() - 2 * correctSet.size();
		return diff;
	}
	private int addConstituents(Tree<String> tree, Set<Constituent> set,
			int start) {
		if (tree == null)
			return 0;
		if (tree.isLeaf()) {
			return 1;
		}
		int end = start;
		for (Tree<String> child : tree.getChildren()) {
			int span = addConstituents(child, set, end);
			end += span;
		}
		if (!tree.isPreTerminal())
			set.add(new Constituent(tree, start, end));
		return end - start;
	}
	private class Constituent {
		Tree<String> tree;
		int from;
		int to;
		private Constituent(Tree<String> tree, int from, int to) {
			this.tree = tree;
			this.from = from;
			this.to = to;
		}
		public String toString() {
			StringBuffer buf = new StringBuffer(tree.getLabel());
			buf.append('[');
			buf.append(from);
			buf.append(',');
			buf.append(to);
			buf.append(']');
			return buf.toString();
		}
		public int hashCode() {
			String buf = toString();
			return buf.hashCode();
		}
		public boolean equals(Object o) {
			if (!(o instanceof Constituent))
				return false;
			if (this.hashCode() == o.hashCode())
				return true;
			return false;
		}
	}
}
