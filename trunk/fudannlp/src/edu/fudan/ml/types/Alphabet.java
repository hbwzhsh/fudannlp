package edu.fudan.ml.types;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import gnu.trove.TLongDoubleIterator;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Alphabet implements Serializable	{
	public TIntObjectHashMap data;
	public TObjectIntHashMap index;
	int size = 0;
	boolean frozen = false;
	public Set<Integer> toSet(){
		Set<Integer> set = new HashSet<Integer>();
		TObjectIntIterator it = index.iterator();
		for (int i = index.size(); i-- > 0;) 
		{
			it.advance();
			set.add(it.value()-1);
		}
		return set;		
	}
	public boolean isStopIncrement() {
		return frozen;
	}
	public void setStopIncrement(boolean stopIncrement) {
		this.frozen = stopIncrement;
	}
	public Alphabet() {
		data = new TIntObjectHashMap();
		index = new TObjectIntHashMap();
	}
	public int lookupIndex(String s) {
		return lookupIndex(s, 1);
	}
	public int lookupIndex(String s, int step) {
		int idx = index.get(s);
		if (0==idx && !frozen) {
			idx = size+1;
			data.put(idx, s);
			index.put(s, idx);
			size += step;
		}
		return idx-1;
	}
	public String lookupString(int index) {
		return (String) data.get(index+1);
	}
	public int size()	{
		return this.size;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<index.size(); i++) {
			sb.append(data.get(i+1));
			sb.append('@');
			sb.append(i);
			sb.append('\n');
		}
		return sb.toString();
	}
}
