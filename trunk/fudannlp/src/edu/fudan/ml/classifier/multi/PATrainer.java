package edu.fudan.ml.classifier.multi;
import java.util.ArrayList;
import java.util.HashSet;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
import edu.fudan.nlp.tc.Mean;
public class PATrainer implements MultiTrainer {
	private SparseVector[] weights;
	private PA classifier;
	private MaxSolver msolver;
	private Generator featureGen;
	private Loss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-10;
	private Tree tree;
	private double c;
	public PATrainer(MaxSolver msolver,Generator featureGen,Loss loss,
			int maxIter,double c,Tree tr){
		this.msolver =msolver;
		this.featureGen =featureGen;
		this.loss = loss;
		this.maxIter = maxIter;
		tree = tr;
		this.c = c;
	}
	public PA getClassifier() {
		return classifier;
	}
	public PA train(InstanceSet trainingList) {
		System.out.println("Sample Size："+trainingList.size());
		int numClass = trainingList.getLabelAlphabet().size();
		weights = Mean.mean(trainingList, tree);
		int loops = 0;
		double oldErrorRate = Double.MAX_VALUE;
		System.out.println("Begin Training...");
		long beginTime = System.currentTimeMillis();
		while (loops++ < maxIter) {
			System.out.print("Loop: "+loops);
			double totalerror = 0.0;
			trainingList.shuffle();
			long beginTimeInner = System.currentTimeMillis();
			for (int ii = 0; ii < trainingList.size(); ii++) {
				Instance inst = trainingList.getInstance(ii);
				Integer maxE;
				Integer maxC = (Integer) inst.getTarget();
				HashSet<Integer> t = new HashSet<Integer>();
				t.add(maxC);
				Results pred = (Results) msolver.getBest(inst, 1, new Object[]{weights,t});
				int error;
				if(tree==null){
					error = ((Integer) pred.predList.get(0)==maxC)?0:1;
				}else{
					error = tree.dist((Integer) pred.predList.get(0),maxC);
				}
				double loss = error - ((Double) pred.oracleScore.get(0) - ((Double) pred.predScore.get(0)));
				maxE = (Integer) pred.predList.get(0);
				if (loss>0) {
//					System.out.print("ID: "+ ii +" Error: "+error + " Loss: "+ loss+ " ||| ");
//					System.out.println(pred[0].get(0) +" -->> "+inst.getTarget());
					totalerror +=1;
					double phi = featureGen.getVector(inst).l2Norm2();
					double alpha = Math.min(c, loss/(phi*error));
					if(tree!=null){
						ArrayList<Integer> anc = tree.getPath(maxC);
						for(int j=0;j<anc.size();j++){
							weights[anc.get(j)].add(featureGen.getVector(inst),alpha);
						}
						anc = tree.getPath(maxE);
						for(int j=0;j<anc.size();j++){
							weights[anc.get(j)].add(featureGen.getVector(inst),-alpha);
						}
					}else{
						weights[maxC].add(featureGen.getVector(inst), alpha);
						weights[maxE].add(featureGen.getVector(inst), -alpha);
					}
				}
			}
			double errorRate = totalerror/trainingList.size();
			System.out.print("\t Error rate："+errorRate);
			System.out.println("\t Time(s):"+(System.currentTimeMillis()-beginTimeInner)/1000);
			if(errorRate==0 && Math.abs(oldErrorRate-errorRate)/oldErrorRate<eps)
				break;
		}
		System.out.println("Training End");
		System.out.println("Training Time(s):"+(System.currentTimeMillis()-beginTime)/1000);
		Pipe dataPipe = trainingList.getPipes();
		classifier = new PA(weights, msolver, featureGen, dataPipe,trainingList.getLabelAlphabet());
		return classifier;
	}
}
