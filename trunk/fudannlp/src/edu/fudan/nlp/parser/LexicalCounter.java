package edu.fudan.nlp.parser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
public class LexicalCounter extends Counter<String> implements Serializable {
	private static final long serialVersionUID = 4158638213109679290L;
	int increSize = 8;
	protected void grow()	{
		int nSize = index.length+increSize;
		String[] nData = new String[nSize];
		Arrays.fill(nData, null);
		System.arraycopy(data, 0, nData, 0, length);
		int[] nIndex = new int[nSize];
		Arrays.fill(nIndex, Integer.MAX_VALUE);
		System.arraycopy(index, 0, nIndex, 0, length);
		data = null; index = null;
		data = nData;
		index = nIndex;
		double[] nCounter = new double[index.length];
		System.arraycopy(counter, 0, nCounter, 0, counter.length);
		counter = null;
		counter = nCounter;
	}
	public void increment(String str)	{
		int cur = super.fetch(str);
		if (cur < 0)	{
			if (length == index.length)
				grow();
			cur = -cur-1;
			System.arraycopy(data, cur, data, cur + 1, length - cur);
			System.arraycopy(index, cur, index, cur + 1, length - cur);
			data[cur] = str;
			index[cur] = str.hashCode();
			counter[cur] = 1;
			length++;
		}else	{
			counter[cur]++;
		}
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(data);
		out.writeObject(index);
		out.writeObject(counter);
		out.writeInt(length);
		out.writeInt(increSize);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		data = (String[])in.readObject();
		index = (int[])in.readObject();
		counter = (double[])in.readObject();
		length = in.readInt();
		increSize = in.readInt();
	}
}
