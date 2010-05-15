package edu.fudan.ml.classifier.multi;
import java.util.*;
import edu.fudan.ml.cluster.Kmeans;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
import edu.fudan.nlp.tc.Mean;
public class LatentPATrainer implements MultiTrainer {
	private SparseVector[] weights;
	private PA classifier;
	private MaxSolver msolver;
	private Generator featureGen;
	private Loss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-10;
	private Tree tree;
	private double c;
	int numLatent = 2;
	public LatentPATrainer(MaxSolver msolver,Generator featureGen,Loss loss,
			int maxIter,double c,Tree tr,int numLatent){
		this.msolver =msolver;
		this.featureGen =featureGen;
		this.loss = loss;
		this.maxIter = maxIter;
		tree = tr;
		this.c = c;
		this.numLatent = numLatent;
	}
	public PA getClassifier() {
		return classifier;
	}
	public PA train(InstanceSet trainingList) {
		System.out.println("Sample Size: "+trainingList.size());
		int numClass = trainingList.getLabelAlphabet().size();
		if(tree==null){
			return null;
		}
		int numNodeswithLatent = tree.size*numLatent;
		weights = new SparseVector[numNodeswithLatent];
		ArrayList<Instance>[] nodes = new ArrayList[tree.size];
		for(int i=0;i<nodes.length;i++){
			nodes[i] = new ArrayList<Instance>();
		}
		for(int i=0;i<trainingList.size();i++){
			ArrayList<Integer> anc = tree.getPath((Integer) trainingList.getInstance(i).getTarget());
			for(int j=0;j<anc.size();j++){
				nodes[anc.get(j)].add(trainingList.getInstance(i));
			}			
		}
		boolean way = true;
		if(way){
			SparseVector[] weights1 = Mean.mean(trainingList, tree);
			weights = new SparseVector[weights1.length*numLatent];
			for(int i=0;i<weights1.length;i++)
				for(int j=0;j<numLatent;j++)
					weights[i*numLatent+j] = weights1[i].clone();
		}else{
			for(int i=0;i<tree.size;i++){
				Kmeans km = new Kmeans(numLatent);
				km.cluster(nodes[tree.getNode(i)]);
				for(int j=0;j<numLatent;j++){
					weights[2*tree.getNode(i)+j] = km.centroids[j];
				}
			}
		}
		int loops = 0;
		double oldErrorRate = Double.MAX_VALUE;
		System.out.println("Begin Training...");
		long beginTime = System.currentTimeMillis();
		while (loops++ < maxIter) {
			System.out.print("Loop:"+loops);
			double totalerror = 0.0;
			trainingList.shuffle();
			long beginTimeInner = System.currentTimeMillis();
			for (int ii = 0; ii < trainingList.size(); ii++) {
				Instance inst = trainingList.getInstance(ii);
				Integer maxEY;
				Integer maxCY;
				Integer[] maxlatent;
				maxCY= (Integer) inst.getTarget();
				HashSet<Integer> t = new HashSet<Integer>();
				t.add(maxCY);
				Object[] pred = (Object[]) msolver.getBest(inst, 1, new Object[]{weights,t});
				int error;
				maxEY = ((List<Integer>) pred[0]).get(0);
				maxlatent = (Integer[]) pred[4];	
				error = tree.dist(maxEY,maxCY);
				double loss = error - (((List<Double>) pred[3]).get(0) - ((List<Double>) pred[1]).get(0));
				if (loss>0) {
					//					System.out.print("ID: "+ ii +" Error: "+error + " Loss: "+ loss+ " ||| ");
					//					System.out.println(pred[0].get(0) +" -->> "+inst.getTarget());
					totalerror +=1;
					double phi = featureGen.getVector(inst).l2Norm2();
					double alpha = Math.min(c, loss/(phi*error));
					ArrayList<Integer> anc = tree.getPath(maxCY);
					for(int j=0;j<anc.size();j++){
						weights[anc.get(j)*numLatent+maxlatent[anc.get(j)]].add(featureGen.getVector(inst),alpha);
					}
					anc = tree.getPath(maxEY);
					for(int j=0;j<anc.size();j++){
						weights[anc.get(j)*numLatent+maxlatent[anc.get(j)]].add(featureGen.getVector(inst),-alpha);
					}
				}
			}
			double errorRate = totalerror/trainingList.size();
			System.out.print("\t Error rate:"+errorRate);
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
