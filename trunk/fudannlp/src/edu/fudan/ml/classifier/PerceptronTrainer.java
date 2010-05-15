package edu.fudan.ml.classifier;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import gnu.trove.TIntObjectProcedure;
import gnu.trove.TObjectIntProcedure;
public class PerceptronTrainer implements Trainer {
	protected SparseVector weight;
	protected Perceptron classifier;
	protected MaxSolver msolver;
	protected Generator featureGen;
	protected Loss loss;
	protected int maxIter = Integer.MAX_VALUE;
	protected double eps = 1e-10;
	public PerceptronTrainer(MaxSolver msolver, Generator featureGen,
			Loss loss, int maxIter, double eps) {
		this.msolver = msolver;
		this.featureGen = featureGen;
		this.loss = loss;
		this.maxIter = maxIter;
		this.eps = eps;
		this.weight = new SparseVector();
	}
	public PerceptronTrainer(Perceptron perceptron, Loss loss, int maxIter, double eps)	{
		this.msolver = perceptron.msolver;
		this.featureGen = perceptron.gen;
		this.loss = loss;
		this.maxIter = maxIter;
		this.eps = eps;
		this.weight = perceptron.weight;
	}
	public Perceptron getClassifier() {
		return classifier;
	}
	public Perceptron train(InstanceSet trainingList) {
		System.out.println("总样本数：" + trainingList.size());
		int loops = 0;
		double oldErrorRate = Double.MAX_VALUE;
		int _count = trainingList.size();
		if (_count > 80)
			_count /= 80;
		System.out.println("开始训练");
		long beginTime = System.currentTimeMillis();
		while (loops++ < maxIter) {
			System.out.printf("迭代：%d ", loops);
			double totalerror = 0.0;
			trainingList.shuffle();
			long beginTimeInner = System.currentTimeMillis();
			for (int ii = 0; ii < trainingList.size();) {
				Instance inst = trainingList.getInstance(ii++);
				Results pred = (Results) msolver.getBest(inst, 1,
						new Object[] { weight });
				double error = loss.calc(pred.predList.get(0), inst
						.getTarget());
				if (error > 0) {
					totalerror += error;
					SparseVector fvi = featureGen.getVector(inst);
					weight.add(fvi);
					fvi = featureGen.getVector(inst, pred.predList.get(0));
					weight.minus(fvi);
				}
				if (ii % _count == 0)
					System.out.print(".");
			}
			System.out.println();
			double errorRate = totalerror / trainingList.size();
			System.out.print("\t 累计错误率：" + errorRate);
			System.out.print("\t w非零个数" + weight.size());
			System.out.println("\t 时间(s):"
					+ (System.currentTimeMillis() - beginTimeInner) / 1000);
			if (errorRate == 0
					|| Math.abs(oldErrorRate - errorRate) / oldErrorRate < eps)
				break;
		}
		System.out.println("结束训练");
		System.out.println("总时间(s):" + (System.currentTimeMillis() - beginTime)
				/ 1000);
		Pipe dataPipe = trainingList.getPipes();
		trimAlphabet();
		classifier = new Perceptron(weight, msolver, featureGen, dataPipe,
				trainingList.getLabelAlphabet());
		return classifier;
	}
	protected void trimAlphabet() {
		System.out.print("压缩特征字典:\t");
		System.out.print("字典大小：" + featureGen.alphabet.data.size());
		boolean retainEntries = featureGen.alphabet.data
				.retainEntries(new TIntObjectProcedure() {
					public boolean execute(int arg0, Object arg1) {
						if (weight.vector.containsKey(arg0-1))
							return true;
						else
							return false;
					}
				});
		featureGen.alphabet.index.retainEntries(new TObjectIntProcedure() {
			public boolean execute(Object arg0, int arg1) {
				if (weight.vector.containsKey(arg1-1))
					return true;
				else
					return false;
			}
		});
		featureGen.alphabet.data.compact();
		System.out.println("\t->\t" + featureGen.alphabet.data.size());
	}
}
