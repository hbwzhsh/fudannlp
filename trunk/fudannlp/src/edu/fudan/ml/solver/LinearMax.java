package edu.fudan.ml.solver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
public class LinearMax extends MaxSolver {
	private Alphabet alphabet;
	int numThread;
	ExecutorService pool;
	public LinearMax(Generator featureGen, Alphabet alphabet, int n) {
		super(featureGen);
		this.alphabet = alphabet;
		numThread = n;
		pool = Executors.newFixedThreadPool(numThread);
	}
	public Object getBest(Instance inst, int n, Object[] params) {
		SparseVector weight = (SparseVector) params[0];
		Integer target = null;
		if(params.length>1){
			target = (Integer) params[1];
		}
		Future[] f=new Future[alphabet.size()];
		for (int i = 0; i < alphabet.size(); i++) {
			SparseVector fv = featureGen.getVector(inst, i);
			f[i] = pool.submit(new Multiplesolve(fv, weight));
		}
		Results res = new Results(n);
		if(target!=null){
			res.buildOracle();
		}
		for (int i = 0; i < alphabet.size(); i++){ 
			try {
				double score  = (Double) f[i].get();
				if(target!=null&&target==i){
					res.addOracle(score,i);
				}else{
					res.addPred(score,i);
				}
			} catch (Exception e) {
				pool.shutdownNow();
				e.printStackTrace();
				return null;
			}
		}
		return res;
	}
	public double getScore(Object o)	{
		return 0;
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
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(alphabet);
		out.writeInt(numThread);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		alphabet = (Alphabet) in.readObject();
		numThread = in.readInt();
		pool = Executors.newFixedThreadPool(numThread);
	}
	protected void finalize()	{
		pool.shutdown();
		if (!pool.isShutdown())
			pool.shutdownNow();
		pool = null;
	}
}
