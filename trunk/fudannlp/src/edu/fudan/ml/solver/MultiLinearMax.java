package edu.fudan.ml.solver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
public class MultiLinearMax extends MaxSolver {
	private Alphabet alphabet;
	private Tree tree;
	int numThread;
	ExecutorService pool;;
	public MultiLinearMax(Generator featureGen, Alphabet alphabet, Tree tree,int n) {
		super(featureGen);
		this.alphabet = alphabet;
		numThread = n;
		this.tree = tree;
		pool = Executors.newFixedThreadPool(numThread);
	}
	public Object getBest(Instance inst, int n, Object[] params) {
		SparseVector[] weights = (SparseVector[]) params[0];
		Set<Integer> target = null;
		if(params.length>1){
			target = (Set<Integer>) params[1];
		}
		SparseVector fv = featureGen.getVector(inst);
		Callable[] c= new Callable [alphabet.size()];
		for (int i = 0; i < alphabet.size(); i++) {
			c[i] = new Multiplesolve(fv,weights[i]);
		}
		double[] sw = new double[alphabet.size()];
		for (int i = 0; i < alphabet.size(); i++){ 
			Future f = pool.submit(c[i]);
			try {
				sw[i] = (Double) f.get();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		Results res = new Results(n);
		if(target!=null){
			res.buildOracle();
		}
		Set<Integer> leafs =null;
		if(tree==null){
			leafs = alphabet.toSet();
		}else
			leafs= tree.getLeafs();
		Iterator<Integer> it = leafs.iterator();
		while(it.hasNext()){
			double score=0.0;
			Integer i = it.next();
			if(tree!=null){
				ArrayList<Integer> anc = tree.getPath(i);
				for(int j=0;j<anc.size();j++){
					score += sw[anc.get(j)];
				}
			}else{
				score = sw[i];
			}
			if(target!=null&&target.contains(i)){
				res.addOracle(score,i);
			}else{
				res.addPred(score,i);
			}
		}		
		return res;
	}
	class Multiplesolve implements Callable {
		SparseVector weight;
		SparseVector fv;
		public  Multiplesolve(SparseVector fv,SparseVector weight) {
			this.fv = fv;
			this.weight = weight;
		}
		public Double call() {
			double score = fv.dotProduct(weight);
			return score;
		}
	}
}
