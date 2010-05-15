package edu.fudan.nlp.parser;
public class ParserEval extends TreeLoss {
	double correctCnt = 0;
	double goldCnt = 0;
	double guessCnt = 0;
	public double eval(Tree<String> guess, Tree<String> gold)	{
		calc(guess, gold);
		correctCnt += correctSet.size();
		goldCnt += goldSet.size();
		guessCnt += guessSet.size();
		double precision = correctSet.size()*1.0/guessSet.size();
		double recall = correctSet.size()*1.0/goldSet.size();
		return 2*precision*recall/(precision+recall);
	}
	public double display()	{
		double precision = correctCnt*1.0/guessCnt;
		double recall = correctCnt*1.0/goldCnt;
		return 2*precision*recall/(precision+recall);
	}
}
