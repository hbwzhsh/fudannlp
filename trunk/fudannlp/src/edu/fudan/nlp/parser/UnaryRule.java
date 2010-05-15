package edu.fudan.nlp.parser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
public class UnaryRule extends Rule implements Serializable {
	int cState = -1;
	double score;
	public UnaryRule(int pState, int cState) {
		this.pState = pState;
		this.cState = cState;
	}
	public UnaryRule(int pState, int cState, double score)	{
		this(pState, cState);
		this.score = score;
	}
	public int hashCode() {
		return (pState << 8) ^ cState;
	}
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof UnaryRule) {
			UnaryRule ur = (UnaryRule) o;
			if (pState == ur.pState && cState == ur.cState) {
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
		buf.append(cState);
		return buf.toString();
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeInt(pState);
		out.writeInt(cState);
		out.writeDouble(score);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		pState = in.readInt();
		cState = in.readInt();
		score = in.readDouble();
	}
}
