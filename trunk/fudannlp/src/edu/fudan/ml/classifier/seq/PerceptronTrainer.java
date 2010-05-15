package edu.fudan.ml.classifier.seq;
import java.util.List;
import edu.fudan.ml.feature.generator.SequenceGenerator;
import edu.fudan.ml.feature.generator.Templet;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.nlp.tag.Tagger;
public class PerceptronTrainer {
	private double[] weights;
	double[] averageWeights;
	private Perceptron classifier;
	private MaxSolver msolver;
	private SequenceGenerator featureGen;
	private Loss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-6;
	public int count;
	public PerceptronTrainer(MaxSolver msolver, Loss loss,	int maxIter, Alphabet features, Alphabet labels, List<Templet> templets){
		this.msolver =msolver;
		this.loss = loss;
		this.maxIter = maxIter;
		weights = new double[features.size()];
		averageWeights = new double[features.size()];
		featureGen = new SequenceGenerator(weights, averageWeights, templets, labels);
	}
	public Perceptron getClassifier() {
		return classifier;
	}
	public Perceptron train(InstanceSet trainingList) {
		int numSamples = trainingList.size();
		count = 0;
		for (int ii = 0; ii < trainingList.size(); ii++) {
			Instance inst = trainingList.getInstance(ii);
			count += ((int[]) inst.getTarget()).length;
		}
		System.out.println("Training Size: "+trainingList.size());	// 样本总数
//		System.out.println("Label Number: " +trainingList.getLabelAlphabet().size());		// label个数
//		System.out.println("Feature Number: "+ trainingList.getPipes().size());	// 特征总数
		System.out.println("Chars Number: " +count);				
		int iter = 0;
		double oldErrorRate = Double.MAX_VALUE;
		long beginTime, endTime;
		long beginTimeIter, endTimeIter;
		beginTime = System.currentTimeMillis();
		double pE = 0;
		while (iter++ < maxIter) {
			double err = 0;
			beginTimeIter = System.currentTimeMillis();
			for (int ii = 0; ii < trainingList.size(); ii++) {
				Instance inst = trainingList.getInstance(ii);
				List pred = (List) msolver.getBest(inst, 1, new Object[]{weights});
				double l = loss.calc(pred.get(0),inst.getTarget());
					err += l;
					featureGen.get(inst,pred.get(0), numSamples-ii);
			}
			for(int iw=0; iw<weights.length; iw++)
				weights[iw] = averageWeights[iw]/numSamples;
			endTimeIter = System.currentTimeMillis();
			System.out.print("iter:");
			System.out.print(iter);
			System.out.print("\terr:");
			System.out.print(err/count);
			System.out.println("\ttime:"+(endTimeIter-beginTimeIter));
		}
		endTime = System.currentTimeMillis();
		System.out.println("done!");
		System.out.println("time escape:"+(endTime-beginTime));
		Perceptron p = new Perceptron(averageWeights,msolver,trainingList.getLabelAlphabet(),trainingList.getPipes());
		return p;
	}
}
