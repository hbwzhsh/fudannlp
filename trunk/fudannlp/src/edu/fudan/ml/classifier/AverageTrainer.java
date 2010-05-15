package edu.fudan.ml.classifier;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class AverageTrainer extends PerceptronTrainer {
	public AverageTrainer(MaxSolver msolver,Generator featureGen,Loss loss,
			int maxIter,double eps){
		super(msolver, featureGen, loss, maxIter, eps);
	}
	public AverageTrainer(Perceptron perceptron, Loss loss, int maxIter, double eps)	{
		super(perceptron, loss, maxIter, eps);
	}
	public Perceptron train(InstanceSet trainingList) {
		System.out.println("总样本数：" + trainingList.size());
		SparseVector tmpWeight = weight.clone();
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
				Object[] pred = (Object[]) msolver.getBest(inst, 1,
						new Object[] { tmpWeight });
				double error = loss.calc(((List) pred[0]).get(0), inst
						.getTarget());
				if (error > 0) {
					totalerror += error;
					SparseVector fvi = featureGen.getVector(inst);
					tmpWeight.add(fvi);
					fvi = featureGen.getVector(inst, ((List) pred[0]).get(0));
					tmpWeight.minus(fvi);
				}
				if (ii % _count == 0)
					System.out.print(".");
			}
			weight.add(tmpWeight);
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
		weight.scalarDivide(loops);
		trimAlphabet();
		classifier = new Perceptron(weight, msolver, featureGen, dataPipe,
				trainingList.getLabelAlphabet());
		return classifier;
	}
}
