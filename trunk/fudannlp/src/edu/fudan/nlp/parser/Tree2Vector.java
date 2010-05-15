package edu.fudan.nlp.parser;
import java.util.Iterator;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class Tree2Vector extends Generator {
	Grammar grammar;
	public Tree2Vector(Grammar grammar)	{
		this.grammar = grammar;
	}
	private SparseVector getVector2(Tree<String> tree)	{
		SparseVector vector = new SparseVector();
		Iterator<Tree<String>> ite = tree.iterator();
		SparseVector tmp = new SparseVector();
		while(ite.hasNext())	{
			Tree<String> cur = ite.next();
			getVector(cur, tmp);
			vector.add(tmp);
			tmp.clear();
		}
		return vector;
	}
	private SparseVector getVector(Tree<String> tree)	{
		SparseVector vector = new SparseVector();
		getVector(tree, vector);
		return vector;
	}
	private void getVector(Tree<String> tree, SparseVector vector)	{
		Iterator<Tree<String>> ite = tree.iterator();
		while(ite.hasNext())	{
			Tree<String> cur = ite.next();
			int pState = grammar.stateList.fetch(cur.getLabel());
			if (cur.isLeaf() || cur.isPreTerminal())	{
				continue;
			}
			List<Tree<String>> children = cur.getChildren();
			int idx = -1;
			if (children.size() == 1)	{
				int cState = grammar.stateList.fetch(children.get(0).getLabel());
				idx = grammar.getUnaryRule(pState, cState);
			}else	{
				int lState = grammar.stateList.fetch(children.get(0).getLabel());
				int rState = grammar.stateList.fetch(children.get(1).getLabel());
				idx = grammar.getBinaryRule(pState, lState, rState);
			}
			if (idx != -1)	{
				double val = vector.elementAt(idx);
				vector.put(idx, val+1);
			}
		}
	}
	public SparseVector getVector(Instance inst) {
		Tree<String> tree = (Tree<String>) inst.getData();
		return getVector2(tree);
	}
	@Override
	public SparseVector getVector(Instance inst, Object object) {
		if (object instanceof Tree)
			return getVector2((Tree<String>)object);
		return null;
	}
}
