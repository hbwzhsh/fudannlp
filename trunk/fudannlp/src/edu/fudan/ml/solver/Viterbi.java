package edu.fudan.ml.solver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.feature.generator.Templet;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Viterbi extends MaxSolver {
	int maxOrder;
	int numLabels;
	int numStates;
	int numTemplets;
	int[] orders;
	int[][] offset;
	double[] weights;
	int[] base;
	public Viterbi(Generator generator, Alphabet features, Alphabet labels, List<Templet> templets) {
		super(generator);
		this.numLabels = labels.size();
		this.numTemplets = templets.size();
		this.orders = new int[numTemplets];
		for(int j=0; j<numTemplets; j++) {
			Templet t = templets.get(j);
			this.orders[j] = t.getOrder();
			if (orders[j] > maxOrder)
				maxOrder = orders[j];
		}
		base = new int[maxOrder+2];
		base[0]=1;
		for(int i=1; i<base.length; i++) {
			base[i]=base[i-1]*numLabels;
		}
		this.numStates = base[maxOrder+1];
		offset = new int[numTemplets][numStates];
		for(int t=0; t<numTemplets; t++) {
			Templet tpl = templets.get(t);
			int[] vars = tpl.getVars();
			int[] bits = new int[maxOrder+1];
			int v;
			for(int s=0; s<numStates; s++) {
				int d = s;
				for(int i=0; i<maxOrder+1; i++) {
					bits[i] = d%numLabels;
					d = d/numLabels;
				}
				v = 0;						
				for(int i=0; i<vars.length; i++) {
					v = v*numLabels + bits[-vars[i]];
				}
				offset[t][s] = v;
			}
		}
	}
	public Object getBest(Instance instance, int nbest, Object[] params) {
		weights = (double[]) params[0];
		int[][] data;
		int[] target;
		Node[][] lattice;
		int[] phi;
		data = (int[][]) instance.getData();
		lattice = new Node[data.length][numStates];
		for(int ip=0; ip<data.length; ip++)
			for(int s=0; s<numStates; s++)
				lattice[ip][s] = new Node();
		phi = new int[data.length];
		List paths = new ArrayList(nbest);
		paths.add(phi);
		for(int ip=0; ip<data.length; ip++) {
			for(int s=0; s<numStates; s++) {
				for(int t=0; t<numTemplets; t++) {
					if (data[ip][t] == -1) continue;
					lattice[ip][s].weight += weights[data[ip][t]+ offset[t][s]];
				}
			}
		}
		for(int s=0; s<numStates; s++) {
			lattice[0][s].best = lattice[0][s].weight;
		}
		for(int ip=1; ip<data.length; ip++) {
			for(int s=0; s<numStates; s++) {
				for(int k=0; k<numLabels; k++) {
					int sp = (k*numStates+s)/numLabels;
					double best = lattice[ip-1][sp].best+lattice[ip][s].weight;
					if (best > lattice[ip][s].best) {
						lattice[ip][s].best = best;
						lattice[ip][s].prev = sp;
					}
				}
			}
		}
		double maxW = Double.NEGATIVE_INFINITY;
		int maxS = -1;
		int last = data.length-1;
		for(int s=0; s<numStates; s++) {
			if (lattice[last][s].best > maxW) {
				maxW = lattice[last][s].best; maxS = s;
			}
		}
		int p = last;
		int s = maxS;
		for(int d=s, i=0; i<maxOrder && p>=0; i++, p--) {
			phi[p] = d%numLabels;
			d = d/numLabels;
		}
		for(; p>=0; p--) {
			phi[p] = s/base[maxOrder];
			s = lattice[p+maxOrder][s].prev;
		}
		return paths;
	}
	public final class Node implements Serializable {
		public Node(){
		}
		double weight = 0.0;
		double best = Double.NEGATIVE_INFINITY;
		int prev = -1;
		public String toString() {
			return String.format("%f %f %d", weight, best, prev);
		}
	}
}
