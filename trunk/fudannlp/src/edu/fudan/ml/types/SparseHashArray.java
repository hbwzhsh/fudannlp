package edu.fudan.ml.types;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
public class SparseHashArray<T> extends SparseArray<T> implements Serializable {
	private int increSize = 8;
	public int fetch(T str) {
		assert (str != null);
		int hashVal = str.hashCode();
		int cur = Arrays.binarySearch(index, hashVal);
		if (cur >= 0) {
			int i = cur;
			boolean found = false;
			for (; i < length; i++) {
				if (str.equals(data[i]))	{
					cur = i;
					found = true;
					break;
				}
				if (index[i] != hashVal) {
					break;
				}
			}
			if (!found)	{
				for(i = cur-1; i >= 0; i--)	{
					if (str.equals(data[i]))	{
						cur = i;
						found = true;
						break;
					}
					if (index[i] != hashVal)
						break;
				}
			}
			if (!found)	{
//				System.out.println("crash!");
				cur = -cur-1;
			}
		}
		return cur;
	}
	public void put(T value) {
		int cur = fetch(value);
		if (cur < 0) {
			if (length == data.length)
				grow();
			cur = -cur-1;
			System.arraycopy(data, cur, data, cur + 1, length - cur);
			System.arraycopy(index, cur, index, cur + 1, length - cur);
			data[cur] = value;
			index[cur] = value.hashCode();
			length++;
		}
	}
	public T get(int idx)	{
		if (idx < 0 || idx >= length)
			return null;
		return data[idx];
	}
	public void clear() {
		Arrays.fill(data, null);
		Arrays.fill(index, Integer.MAX_VALUE);
		length = 0;
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
