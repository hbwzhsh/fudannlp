package edu.fudan.nlp.tc;
import java.io.IOException;
import edu.fudan.ml.classifier.multi.*;
import edu.fudan.ml.data.*;
import edu.fudan.ml.feature.generator.*;
import edu.fudan.ml.loss.*;
import edu.fudan.ml.pipe.*;
import edu.fudan.ml.solver.*;
import edu.fudan.ml.types.*;
public class Experiemnt {
	static InstanceSet train;
	static InstanceSet test;
	static Alphabet alphabet = new Alphabet();
	static String path =null;
	private static boolean hier;
	static Tree tree= null;
	public static void main(String[] args) throws Exception{		
		long start = System.currentTimeMillis();		
		if(args.length>0)
			path = args[0];
		if(args.length>1 && args[1].compareToIgnoreCase("tree")==0){
			hier = true;
		}
		loadlshtc();
		System.out.println("Number of class: "+ alphabet.size());
		Tree t = null;
		if(hier){
			t=tree;
		}
		int numThreads = 4;
		Generator featureGen = new SimpleGenerator();
		ZeroOneLoss loss = new ZeroOneLoss();
		MaxSolver msolver = new MultiLinearMax(featureGen, alphabet,t,numThreads);		
		PATrainer trainer = new PATrainer(msolver, featureGen, loss, 10, 1.0, t);
		PA cl = trainer.train(train);
		int[] pred = new int[test.size()];
		int[] golden = new int[test.size()];
		System.out.println("Begin Test...");
		for(int i=0;i<test.size();i++){
			pred[i]= (Integer) cl.classify(test.getInstance(i));
			golden[i] = (Integer) test.getInstance(i).getTarget();
		}
		System.out.println("Test End");
		Evaluation res = new Evaluation();
		res.eval(golden, pred, alphabet.size(),tree);
		long end=System.currentTimeMillis();
		System.out.println("Total Time: "+(end-start));
		System.out.println("End!");
		System.exit(0);
	}
	private static void loadwipo() throws IOException {
		if(path==null)
			path = "D:/Datasets/wipo";
		tree = new Tree();
		tree.loadFromFileWithEdge(path+"/e.txt",alphabet);
		Pipe pipe = new SeriesPipes(new Pipe[]{new Target2Label(alphabet),new Normalize()});
		train = new InstanceSet(pipe,alphabet);
		test = new InstanceSet(pipe,alphabet);
		svmFileReaderforwipo reader = new svmFileReaderforwipo(path+"/x_t.txt" , path+"/y_tr.txt");
		train.loadThruPipes(reader);
		reader = new svmFileReaderforwipo(path+"/x_ts.txt" , path+"/y_ts.txt");
		test.loadThruPipes(reader);
	}
	private static void loadlshtc() throws IOException {
		if(path==null)
			path = "D:/Datasets/dry-run_lshtc_dataset";
		tree = new Tree();
		tree.loadFromFileWithPath(path+"/cat_hier.txt",alphabet);
		Pipe pipe = new Target2Label(alphabet);
		train = new InstanceSet(pipe,alphabet);
		test = new InstanceSet(pipe,alphabet);
		train.loadThruPipes(new svmFileReader(path+ "/Task1_Train_CrawlData_Test_CrawlData/train.txt"));
		train.loadThruPipes(new svmFileReader(path+ "/Task1_Train_CrawlData_Test_CrawlData/validation.txt"));
		test.loadThruPipes(new svmFileReader(path+ "/Task1_Train_CrawlData_Test_CrawlData/test.txt"));
		TF2IDF pipeIDF = new TF2IDF(train,test);
		pipeIDF.process(train);
		pipeIDF.process(test);
		pipe = new SeriesPipes(new Pipe[]{new TFIDF(pipeIDF.idf,train.size()+test.size()),new Normalize()});
		pipe.process(train);
		pipe.process(test);
	}
}
