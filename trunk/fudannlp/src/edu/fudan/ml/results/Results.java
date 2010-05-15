package edu.fudan.ml.results;
import java.util.LinkedList;
public class Results {
	int n;
	public LinkedList<Double> predScore;
	public LinkedList<Integer> predList;
	public LinkedList<Double> oracleScore;
	public LinkedList<Integer> oracleList;
	public Results(int n){
		this.n= n;
		predScore = new LinkedList<Double>();
		predList = new LinkedList<Integer>();
		oracleScore =null;
		oracleList =null;
	}
	public void buildOracle(){
		oracleScore = new LinkedList<Double>();
		oracleList = new LinkedList<Integer>();
	}
	public void addPred(double score,int i){
		int idx=0;
		for(;idx<predScore.size();idx++){
			if(predScore.get(idx)<score)
				break;
		}
		if(idx>=predScore.size()&&idx<n){
			predScore.add(score);
			predList.add(i);
		}else{
			predScore.add(idx,score);
			predList.add(idx,i);
		}
		if(predScore.size()>n){
			predScore.removeLast();
			predList.removeLast();
		}
	}
	public void addOracle(double score,int i){
		int idx=0;
		for(;idx<oracleScore.size();idx++){
			if(oracleScore.get(idx)<score)
				break;
		}
		if(idx>=oracleScore.size()&&idx<n){
			oracleScore.add(score);
			oracleList.add(i);
		}else{
			oracleScore.add(idx,score);
			oracleList.add(idx,i);
		}
		if(oracleScore.size()>n){
			oracleScore.removeLast();
			oracleList.removeLast();
		}
	}
}
