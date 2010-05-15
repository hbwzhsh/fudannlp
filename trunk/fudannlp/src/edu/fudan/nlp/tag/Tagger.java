package edu.fudan.nlp.tag;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import edu.fudan.ml.classifier.seq.Perceptron;
import edu.fudan.ml.classifier.seq.PerceptronTrainer;
import edu.fudan.ml.data.SequenceReader;
import edu.fudan.ml.feature.generator.Templet;
import edu.fudan.ml.loss.HammingLoss;
import edu.fudan.ml.loss.Loss;
import edu.fudan.ml.pipe.*;
import edu.fudan.ml.solver.Viterbi;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class Tagger   {
	List<Templet> templets;
	Alphabet features;
	Alphabet labels;
	double[] weights;
	Pipe featurePipe;
	Viterbi inference;
	public void test(InstanceSet testSet) {
		int err = 0;
		int total = 0;
		for(int i=0; i<testSet.size(); i++) {
			int[] target;
			int[] predict;
			Instance inst = testSet.getInstance(i);
			target = (int[]) inst.getTarget();
			List result = (List) inference.getBest(inst, 1, new Object[]{weights});
			predict = (int[]) result.get(0);
			for (int j=0; j<target.length; j++) {
				if(predict[j] != target[j]) ++err;
				++total;
			}
		}
		System.out.print(total-err);
		System.out.print('/');
		System.out.print(total);
		System.out.print(' ');
		System.out.println((total-err)*1.0/total);
		System.out.println();
	}
	public void tag(Instance inst) {
		featurePipe.addThruPipe(inst);
		List pred = (List) inference.getBest(inst, 1, new Object[]{weights});
		int[] target = (int[]) pred.get(0);
		String[] re = new String[target.length];
		for(int i=0; i<target.length; i++)
			re[i] = labels.lookupString(target[i]);
		inst.setTarget(re);
	}
	public void readModel(InputStream is) throws IOException{
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (is)));
		readModel(in);
	}
	public void readModel(String file) throws IOException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (new FileInputStream(file))));
		readModel(in);
	}
	public void readModel(ObjectInputStream in){
		try {
			int nTemplets = in.readInt();
			templets = new ArrayList(nTemplets);
			Templet.gid = 0;
			for(int i=0; i<nTemplets; i++) {
				String templ = (String) in.readObject();
				templets.add(new Templet(templ));
			}
			labels = (Alphabet) in.readObject();
			features = (Alphabet) in.readObject();
			features.setStopIncrement(true);
			weights = (double[]) in.readObject();
			featurePipe = new Sequence2FeatureSequence(templets, features, labels);
			inference = new Viterbi(null, features, labels, templets);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void saveModel(String file) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream (
				new GZIPOutputStream (new FileOutputStream(file))));
		out.writeInt(templets.size());
		for(int i=0; i<templets.size(); i++)
			out.writeObject(templets.get(i).toString());
		out.writeObject(labels);
		out.writeObject(features);
		out.writeObject(weights);
		out.close();
	}
	public static void main(String[] args) throws IOException {
		List<Templet> templets = new ArrayList(2);
		//		templets.add(new Templet("%x[-2,0]%y[0]"));
		templets.add(new Templet("%x[-1,0]%y[0]"));
		templets.add(new Templet("%x[0,0]%y[0]"));
		templets.add(new Templet("%x[1,0]%y[0]"));
		//		templets.add(new Templet("%x[2,0]%y[0]"));
		//		templets.add(new Templet("%x[0,1]%y[0]"));
		//		templets.add(new Templet("%x[-2,0]%x[-1,0]%y[0]"));
		templets.add(new Templet("%x[-1,0]%x[0,0]%y[0]"));
		templets.add(new Templet("%x[0,0]%x[1,0]%y[0]"));
		//		templets.add(new Templet("%x[1,0]%x[2,0]%y[0]"));
		templets.add(new Templet("%x[-1,0]%x[1,0]%y[0]"));
		templets.add(new Templet("%y[-1]%y[0]"));
		Alphabet labels = new Alphabet();
		Pipe prePipe = new SeriesPipes(new Pipe[] {
				new StringTokenizer(),
				new SplitDataAndTarget(), new Target2Label(labels)});
		Alphabet features = new Alphabet();
		Pipe featurePipe = new Sequence2FeatureSequence(templets, features, labels);
		Pipe pipe = new SeriesPipes(new Pipe[]{prePipe,featurePipe});
		InstanceSet trainSet = new InstanceSet(pipe);
		trainSet.loadThruStagePipes(new SequenceReader("data/cws/pku_training.1.txt", "gbk"));
		Viterbi inference = new Viterbi(null, features, labels, templets);
		Loss loss = new HammingLoss();
		features.setStopIncrement(true);
		InstanceSet testSet = new InstanceSet(pipe);
		testSet.loadThruStagePipes(new SequenceReader("data/cws/pku_test.1.txt", "gbk"));
		System.out.println(testSet.size());
		System.out.println(labels.size());
		System.out.println(features.size());
		Tagger tagger = new Tagger();
		tagger.templets = templets;
		tagger.features = features;
		tagger.labels = labels;
		tagger.inference = inference;
		tagger.featurePipe = featurePipe;
		PerceptronTrainer trainer = new PerceptronTrainer(inference, loss, 10,features,labels,templets);
		Perceptron p = trainer.train(trainSet);
		tagger.weights = p.getWeight();
		//		FileIO.writeObject("model/cws.m", p);
		tagger.test(testSet);
	}
}
