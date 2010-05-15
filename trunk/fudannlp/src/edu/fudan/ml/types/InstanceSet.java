package edu.fudan.ml.types;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import edu.fudan.ml.data.Reader;
import edu.fudan.ml.data.SequenceReader;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.pipe.SeriesPipes;
public class InstanceSet {
	private List<Instance> data;
	private Pipe pipes = null;
	private Alphabet labelAlphabet = null;
	public int numFeatures = 0;
	public InstanceSet()	{
		data = new LinkedList<Instance>();
	}
	public InstanceSet(Pipe pipes) {
		this.pipes = pipes;
		data = new LinkedList<Instance>();
	}
	public InstanceSet(Pipe pipes,	Alphabet labelalphabet) {
		this.pipes = pipes;
		data = new LinkedList<Instance>();
		labelAlphabet = labelalphabet;
	}
	public InstanceSet[] split(int i, int n, InstanceSet set) {
		InstanceSet shuffled = set;
		int length = shuffled.size();
		// System.out.println(set.featureAlphabet.size()+" "+(int)(proportions[0]*length));
		InstanceSet[] ne = new InstanceSet[2];
		ne[0] = new InstanceSet(set.pipes,
				set.labelAlphabet);
		ne[1] = new InstanceSet(set.pipes,
				set.labelAlphabet);
		ne[1].data = new LinkedList(shuffled.data.subList(i * length / n,
				(1 + i) * length / n));
		if (i > 0) {
			ne[0].data = new LinkedList(shuffled.data.subList(0, i * length / n));
			for (int j = (1 + i) * length / n; j < length; j++) {
				ne[0].data.add(shuffled.data.get(j));
			}
		} else {
			ne[0].data = new LinkedList(shuffled.data.subList((i + 1) * length
					/ n, length));
		}
		System.out.println("train:" + ne[0].size());
		System.out.println("test:" + ne[1].size());
		// System.out.println("train:"+ne[0].size());
		// System.out.println("test:"+ne[1].size());
		return ne;
	}
	public void loadThruPipes(Reader reader) throws IOException {
		while(reader.hasNext()){
			Instance inst = reader.next();
			if(pipes!=null)
				pipes.addThruPipe(inst);
			data.add(inst);
		}
	}
	public void loadThruStagePipes(SequenceReader reader) {
		SeriesPipes p = (SeriesPipes) pipes;
		Pipe p1 = p.getPipe(0);
		while(reader.hasNext()){
			Instance inst = reader.next();
			if(p1!=null)
				p1.addThruPipe(inst);
			data.add(inst);
		}
		for(int i = 1; i < p.size(); i++)
			p.getPipe(i).process(this);		
	}
	public void shuffle() {
		Collections.shuffle(data);
	}
	public Pipe getPipes() {
		return pipes;
	}
	public int size() {
		return data.size();
	}
	public Instance getInstance(int idx) {
		if (idx < 0 || idx > data.size())
			return null;
		return data.get(idx);
	}
	public Alphabet getLabelAlphabet() {
		return labelAlphabet;
	}
	public void addAll(InstanceSet subset)	{
		data.addAll(subset.data);
	}
	public void add(Instance inst)	{
		data.add(inst);
	}
	public void setPipes(Pipe pipes)	{
		this.pipes = pipes;
	}
	public void setLabelAlphabet(Alphabet alphabet) {
		labelAlphabet = alphabet;
	}
}
