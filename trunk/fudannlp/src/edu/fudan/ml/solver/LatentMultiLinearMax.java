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
public class LatentMultiLinearMax extends MaxSolver {
	private Alphabet alphabet;
	private Tree tree;
	int numThread;
	ExecutorService pool;
	private int numLatent;
	private int[] indictor;
	public LatentMultiLinearMax(Generator featureGen, Alphabet alphabet, Tree tree,int n,int numLatent) {
		super(featureGen);
		this.alphabet = alphabet;
		numThread = n;
		this.tree = tree;
		this.numLatent = numLatent;
		pool = Executors.newFixedThreadPool(numThread);
		indictor = new int[tree.size];
	}
	public Object getBest(Instance inst, int n, Object[] params) {
		SparseVector[] weights = (SparseVector[]) params[0];
		Set<Integer> target = null;
		if(params.length>1){
			target = (Set<Integer>) params[1];
		}
		SparseVector fv = featureGen.getVector(inst);
		Callable[] c= new Callable [tree.size];
		for (int i = 0; i < tree.size; i++) {
			c[i] = new Multiplesolve(fv,weights,tree.getNode(i));
		}
		double[] sw = new double[tree.size];
		Integer[] maxlatent = new Integer[tree.size];
		for (int i = 0; i < tree.size; i++){ 
			Future f = pool.submit(c[i]);
			try {
				Object[] res = (Object[]) f.get();
				sw[tree.getNode(i)] = (Double) res[0];
				maxlatent[tree.getNode(i)] = (Integer) res[1];
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
		leafs= tree.getLeafs();
		Iterator<Integer> it = leafs.iterator();
		while(it.hasNext()){
			double score=0.0;
			Integer i = it.next();			
			ArrayList<Integer> anc = tree.getPath(i);
			for(int j=0;j<anc.size();j++){
				score += sw[anc.get(j)];
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
		SparseVector[] weights;
		SparseVector fv;
		int idx;
		public  Multiplesolve(SparseVector fv,SparseVector[] weights, int i) {
			this.fv = fv;
			this.weights = weights;
			this.idx= i;
		}
		public Object[] call() {
			Double score = 0.0;
			Integer maxi=0;
			for(int i=0;i<numLatent;i++){				
				double s = fv.dotProduct(weights[idx*numLatent+i]);
				if(s>score){
					score =s;
					maxi = i;
				}
			}
			return new Object[]{score,maxi};
		}
	}
}
