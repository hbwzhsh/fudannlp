package edu.fudan.ml.data;
import java.util.Iterator;
import edu.fudan.ml.types.Instance;
public abstract class Reader implements Iterator<Instance> {
	public void remove () {
		throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
	}
}
