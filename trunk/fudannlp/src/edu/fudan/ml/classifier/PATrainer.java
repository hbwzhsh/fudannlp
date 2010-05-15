package edu.fudan.ml.classifier;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.nlp.parser.Tree;
import gnu.trove.TIntObjectProcedure;
import gnu.trove.TObjectIntProcedure;
public class PATrainer extends PerceptronTrainer {
	public PATrainer(MaxSolver msolver,Generator featureGen,Loss loss,
			int maxIter,double eps){
		super(msolver, featureGen, loss, maxIter, eps);
	}
	public PATrainer(Perceptron perceptron, Loss loss, int maxIter, double eps)	{
		super(perceptron, loss, maxIter, eps);
	}
	public Perceptron train(InstanceSet trainingList) {
		System.out.println("总样本数："+trainingList.size());
		SparseVector tmpWeight = weight.clone();
		int loops = 0;
		double oldErrorRate = Double.MAX_VALUE;
		int _count = trainingList.size();
		if (_count > 100)
			_count /= 100;
		System.out.println("开始训练 ...");
		long beginTime = System.currentTimeMillis();
		while (loops++ < maxIter) {
			System.out.printf("iteration：%d ", loops);
			double totalerror = 0.0;
			trainingList.shuffle();
			for (int ii = 1; ii < trainingList.size();) {
				Instance inst = trainingList.getInstance(ii++);
				System.out.println(ii);
				Object[] pred = (Object[]) msolver.getBest(inst, 1, new Object[]{tmpWeight});
				if (pred.length == 0)
					continue;
				double error = loss.calc(((List) pred[0]).get(0), inst.getTarget());
				if (error>0) {
					totalerror ++;
					SparseVector goldVector = featureGen.getVector(inst);
					SparseVector guessVector = featureGen.getVector(inst, ((List) pred[0]).get(0));
					goldVector.minus(guessVector);
					double diff = error-tmpWeight.dotProduct(goldVector);
					diff = diff/goldVector.l2Norm2();
					if (Double.isNaN(diff) || Double.isInfinite(diff))
						continue;
					goldVector.scalarMultiply(diff);
					tmpWeight.add(goldVector);
				}
				if (ii % _count == 0)
					System.out.print(".");
			}
			System.out.println();
			weight.add(tmpWeight);
			double errorRate = totalerror/trainingList.size();
			if(errorRate==0 || Math.abs(oldErrorRate-errorRate)/oldErrorRate<eps)
				break;
		}
		System.out.println("训练结束");
		System.out.println("总耗时(s):"+(System.currentTimeMillis()-beginTime)/1000);
		weight.scalarDivide(loops);
		Pipe dataPipe = trainingList.getPipes();
		trimAlphabet();
		classifier = new Perceptron(weight, msolver, featureGen, dataPipe,trainingList.getLabelAlphabet());
		return classifier;
	}
}
