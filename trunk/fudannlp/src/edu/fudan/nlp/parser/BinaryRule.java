package edu.fudan.nlp.parser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
public class BinaryRule extends Rule implements Serializable	{
	int lState = -1;
	int rState = -1;
	double score;
	public BinaryRule(int pState, int lState, int rState) {
		this.pState = pState;
		this.lState = lState;
		this.rState = rState;
	}
	public BinaryRule(int pState, int lState, int rState, double score)	{
		this(pState, lState, rState);
		this.score = score;
	}
	public int hashCode() {
		return (pState << 16) ^ (lState << 8) ^ rState;
	}
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof BinaryRule) {
			BinaryRule br = (BinaryRule) o;
			if (pState == br.pState && lState == br.lState && rState == br.rState) {
				return true;
			}
		}
		return false;
	}
	public double getScore()	{
		return score;
	}
	public void setScore(double score){
		this.score = score;
	}
	public String toString()	{
		StringBuffer buf = new StringBuffer();
		buf.append(pState);
		buf.append(" -> ");
		buf.append(lState);
		buf.append(' ');
		buf.append(rState);
		return buf.toString();
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeInt(pState);
		out.writeInt(lState);
		out.writeInt(rState);
		out.writeDouble(score);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		pState = in.readInt();
		lState = in.readInt();
		rState = in.readInt();
		score = in.readDouble();
	}
}
