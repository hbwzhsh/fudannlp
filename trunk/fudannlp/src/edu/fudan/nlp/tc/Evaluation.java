package edu.fudan.nlp.tc;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import edu.fudan.ml.types.Tree;
public class Evaluation  {
	double Accuracy;
	double MarcoF;
	double MacroPrecision;
	double MacroRecall;
	double Treeloss;
	public void eval(int[] golden, int[] pred, int numofclass, Tree tree) {
		int totnum=golden.length;
		if(tree!=null){
			numofclass=tree.size;
		}
		double leafcor=0;
		double loss=0;
		double[] ttcon=new double[10];
		double[] truePositive=new double[numofclass];
		double[] falseNegative=new double[numofclass];
		double[] falsePositive=new double[numofclass];
		for(int i=0;i<totnum;i++){
			if(golden[i]==pred[i]){
				leafcor++;
				truePositive[golden[i]]++;
			}
			else{	
				falsePositive[pred[i]]++;
				falseNegative[golden[i]]++;
				if(tree!=null){
					loss+=tree.dist(golden[i], pred[i]);
				}
			}
		}
		BufferedWriter bout=null;
		try{
			FileOutputStream fos =new FileOutputStream("testresult0111.txt");
			bout = new BufferedWriter(new OutputStreamWriter(fos,"UTF-8"));
			Treeloss=loss/totnum;
			Accuracy=leafcor/totnum;
			double count1=0;
			double count2=0;
			for(int i=0;i<numofclass;i++){
				double base = truePositive[i]+falsePositive[i]; 
				if(base>0)
					MacroPrecision+= truePositive[i]/base;
				else{
					count1++;	
				}
				base = truePositive[i]+falseNegative[i]; 
				if(base>0)
					MacroRecall+=truePositive[i]/base;
				else{
					count2++;
				}
			}
			bout.write("Accuracy    MarcoF   MacroPrecision   MacroRecall   Treeloss\n");
			MacroPrecision/=(numofclass-count1);
			MacroRecall/=(numofclass-count2);
			MarcoF=2*MacroPrecision*MacroRecall/(MacroPrecision+MacroRecall);
			int i=0;
			DecimalFormat df = new DecimalFormat("##.00");
			Accuracy = Double.parseDouble(df.format(Accuracy*100));
			MarcoF = Double.parseDouble(df.format(MarcoF*100));
			MacroPrecision = Double.parseDouble(df.format(MacroPrecision*100));
			MacroRecall = Double.parseDouble(df.format(MacroRecall*100));
			Treeloss = Double.parseDouble(df.format(Treeloss));
			bout.write(Accuracy+" "+ MarcoF+" "+ MacroPrecision+" "+ MacroRecall+" "+ Treeloss+"\n");
			while(ttcon[i]!=0){
				ttcon[i] = Double.parseDouble(df.format(ttcon[i]*100));
				bout.write(""+i+"th level accurary: "+(double)ttcon[i]/totnum+"\n");
				i++;
			}
			bout.close();
		}catch(Exception e){
		}
	}
}
