package edu.fudan.ml.pipe;
import java.util.ArrayList;
import edu.fudan.ml.types.Instance;
public class SeriesPipes extends Pipe{
	private ArrayList<Pipe> pipes = null;
	public int size(){
		return pipes.size();
	}
	public SeriesPipes(Pipe[] pipes)	{
		this.pipes = new ArrayList<Pipe>(pipes.length);
		for(int i = 0; i < pipes.length; i++)
			this.pipes.add(pipes[i]);
	}
	public ArrayList<Pipe> getPipes()	{
		return pipes;
	}
	@Override
	public void addThruPipe(Instance carrier) {
		for(int i = 0; i < pipes.size(); i++)
			pipes.get(i).addThruPipe(carrier);
	}
	public Pipe getPipe(int id)	{
		if (id < 0 | id > pipes.size())
			return null;
		return pipes.get(id);
	}
}
