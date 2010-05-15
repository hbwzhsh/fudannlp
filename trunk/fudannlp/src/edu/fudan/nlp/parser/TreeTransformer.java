package edu.fudan.nlp.parser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
public class TreeTransformer {
	public final static String emptyNode = "-NONE-";
	public final static String rootNode = "ROOT";
	final static Stack<Tree<String>> treeStack = new Stack<Tree<String>>();
	public static Tree<String> transformTree(Tree<String> tree) {
		removeEmptyNode(tree);
		removeXXEdge(tree);
		Binarizer.binarize(tree);
		return tree;
	}
	public static Tree<String> transformTree2(Tree<String> tree) {
		removeEmptyNode(tree);
		removeXXEdge(tree);
		return tree;
	}
	public static Tree<String> restoreTree(Tree<String> tree)	{
		Binarizer.unbinarize(tree);
		return tree;
	}
	private static void removeUnaryChain(Tree<String> tree) {
	}
	private static void removeXXEdge(Tree<String> tree) {
		postTraversal(tree);
		while (!treeStack.isEmpty()) {
			Tree<String> cur = treeStack.pop();
			if (cur.getChildren().size() != 1)
				continue;
			String xlabel = cur.getChild(0).getLabel();
			if (xlabel.equals(cur.getLabel())) {
				cur.setChildren(cur.getChild(0).getChildren());
			}
		}
	}
	private static void removeEmptyNode(Tree<String> tree) {
		postTraversal(tree);
		while (!treeStack.isEmpty()) {
			Tree<String> cur = treeStack.pop();
			for (int i = cur.getChildren().size() - 1; i >= 0; i--) {
				Tree<String> child = cur.getChild(i);
				if (child.getLabel().equals(emptyNode))
					cur.removeChild(i);
			}
			if (cur.isTerminal())
				cur.setLabel(emptyNode.intern());
		}
		if (tree.isTerminal())
			tree.setLabel(rootNode);
	}
	private static void postTraversal(Tree<String> tree) {
		if (tree.isTerminal() || tree.isPreTerminal())
			return;
		treeStack.add(tree);
		for (Tree<String> child : tree.getChildren()) {
			postTraversal(child);
		}
	}
	private static class Binarizer {
		public static Tree<String> binarize(Tree<String> tree) {
			doRightBinarization(tree);
			forgetLabel(tree);
			return tree;
		}
		public static Tree<String> unbinarize(Tree<String> tree) {
			for(int i = 0; i < tree.getChildren().size(); i++)
				doUnbinarization(tree, tree.getChild(i), i);
			return tree;
		}
		private static void doRightBinarization(Tree<String> tree) {
			if (tree.isPreTerminal() || tree.isTerminal())
				return;
			int size = tree.getChildren().size();
			if (size > 2) {
				String internLabel = tree.getLabel();
				if (!internLabel.startsWith("@"))
					internLabel = "@" + internLabel + "->_";
				else
					internLabel = internLabel + "_";
				Tree<String> lastChild = tree.getLastChild();
				String rightLabel = lastChild.getLabel();
				Tree<String> internNode = new Tree<String>(internLabel
						+ rightLabel);
				internNode.setChildren(tree.getChildren().subList(0, size - 1));
				List<Tree<String>> children = new ArrayList<Tree<String>>(2);
				children.add(internNode);
				children.add(lastChild);
				tree.setChildren(children);
				size = tree.getChildren().size();
			}
			for (int i = 0; i < size; i++)
				doRightBinarization(tree.getChild(i));
		}
		private void doLeftBinarization(Tree<String> tree) {
			if (tree.isPreTerminal() || tree.isTerminal())
				return;
			int size = tree.getChildren().size();
			if (size > 2) {
				String internLabel = tree.getLabel();
				if (!internLabel.startsWith("@"))
					internLabel = "@" + internLabel + "->_";
				else
					internLabel = internLabel + "_";
				Tree<String> firstChild = tree.getFirstChild();
				String leftLabel = firstChild.getLabel();
				Tree<String> internNode = new Tree<String>(internLabel
						+ leftLabel);
				internNode.setChildren(tree.getChildren().subList(1, size));
				List<Tree<String>> children = new ArrayList<Tree<String>>(2);
				children.add(firstChild);
				children.add(internNode);
				tree.setChildren(children);
				size = tree.getChildren().size();
			}
			for (int i = 0; i < size; i++)
				doLeftBinarization(tree.getChild(i));
		}
		private static void doUnbinarization(Tree<String> tree,
				Tree<String> child, int p) {
			if (tree.isPreTerminal() || tree.isTerminal())
				return;
			int size = child.getChildren().size();
			for (int i = size-1; i >= 0; i--)
				doUnbinarization(child, child.getChild(i), i);
			String childLabel = child.getLabel();
			if (childLabel.startsWith("@"))	{
				tree.removeChild(p);
				for(int i = child.getChildren().size()-1; i >= 0 ; i--)	{
					tree.addChild(p, child.getChild(i));
				}
			}
		}
		private static void forgetLabel(Tree<String> tree) {
			Iterator<Tree<String>> ite = tree.iterator();
			while (ite.hasNext()) {
				Tree<String> cur = ite.next();
				String label = cur.getLabel();
				if (label.startsWith("@")) {
					label = label.substring(0, label.indexOf('-'));
					cur.setLabel(label.intern());
				}
			}
		}
	}
}
