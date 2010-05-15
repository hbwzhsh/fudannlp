package edu.fudan.ml.types;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
public class SparseArray<T> implements Vector, Serializable {
	protected T[] data = (T[])new Object[0];
	protected int[] index = new int[0];
	protected int length;
	private int increSize = 8;
	public T get(int idx) {
		int cur = Arrays.binarySearch(index, idx);
		if (cur >= 0)
			return data[cur];
		return null;
	}
	public void put(int idx, T value) {
		int cur = Arrays.binarySearch(index, idx);
		if (cur < 0)	{
			if (length == data.length)
				grow();
			int p = -cur-1;
			System.arraycopy(data, p, data, p+1, length-p);
			System.arraycopy(index, p, index, p+1, length-p);
			data[p] = value;
			index[p] = idx;
			length++;
		}else	{
			data[cur] = value;
		}
	}
	protected void grow() {
		int nSize = data.length+increSize;
		T[] nData = (T[])new Object[nSize];
		Arrays.fill(nData, null);
		System.arraycopy(data, 0, nData, 0, length);
		int[] nIndex = new int[nSize];
		Arrays.fill(nIndex, Integer.MAX_VALUE);
		System.arraycopy(index, 0, nIndex, 0, length);
		data = null; index = null;
		data = nData;
		index = nIndex;
	}
	public int capacity()	{
		return data.length;
	}
	public void compact()	{
		T[] nData = (T[])new Object[length];
		System.arraycopy(data, 0, nData, 0, length);
		int[] nIndex = new int[length];
		System.arraycopy(index, 0, nIndex, 0, length);
		data = null; index = null;
		data = nData;
		index = nIndex;
	}
	public int size()	{
		return length;
	}
	public boolean containsKey(int idx)	{
		int cur = Arrays.binarySearch(index, idx);
		if (cur < 0)
			return false;
		else
			return true;
	}
	public Iterator iterator()	{
		return new IndexIterator();
	}
	protected class IndexIterator implements Iterator<Integer>	{
		int cur = 0;
		public boolean hasNext() {
			return (cur < length);
		}
		public Integer next() {
			return index[cur++];
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(data);
		out.writeObject(index);
		out.writeInt(length);
		out.writeInt(increSize);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException	{
		data = (T[])in.readObject();
		index = (int[])in.readObject();
		length = in.readInt();
		increSize = in.readInt();
	}
}
