package edu.fudan.ml.data;
import java.net.URI;
import java.util.Iterator;
import edu.fudan.ml.types.Instance;
public class StringReader extends Reader
{
	String[] data;
	int index;
	public StringReader (String[] data)
	{
		this.data = data;
		this.index = 0;
	}
	public Instance next ()
	{
		return new Instance (data[index++], null);
	}
	public boolean hasNext ()	{	return index < data.length;	}
}
