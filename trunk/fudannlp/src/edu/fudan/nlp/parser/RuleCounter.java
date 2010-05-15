package edu.fudan.nlp.parser;
import java.util.Arrays;
public class RuleCounter extends Counter<Rule> {
	int increSize = 8;
	protected void grow()	{
		int nSize = index.length+increSize;
		Rule[] nData = new Rule[nSize];
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
	public void increment(Rule r)	{
		int cur = super.fetch(r);
		if (cur < 0)	{
			if (length == index.length)
				grow();
			cur = -cur-1;
			System.arraycopy(data, cur, data, cur + 1, length - cur);
			System.arraycopy(index, cur, index, cur + 1, length - cur);
			data[cur] = r;
			index[cur] = r.hashCode();
			counter[cur] = 1;
			length++;
		}else	{
			counter[cur]++;
		}
	}
}
