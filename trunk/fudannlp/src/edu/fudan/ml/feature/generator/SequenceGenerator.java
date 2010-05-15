package edu.fudan.ml.feature.generator;
import java.io.Serializable;
import java.util.List;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class SequenceGenerator implements Serializable{
	List<Templet> templets;
	int numTemplets;
	int numLabels;
	int[] orders;
	int maxOrder;
	double[] weights;
	double[] averageWeights;
	int[] base;
	int numStates;
	int[][] offset;
	public SequenceGenerator(double[] weights, double[] averageWeights,
			List<Templet> templets, Alphabet labels) {
		this.weights = weights;
		this.averageWeights = averageWeights;
		this.templets = templets;
		this.numTemplets = templets.size();
		this.numLabels = labels.size();
		this.orders = new int[numTemplets];
		for(int j=0; j<numTemplets; j++) {
			Templet t = (Templet) templets.get(j);
			this.orders[j] = t.getOrder();
			if (orders[j] > maxOrder)
				maxOrder = orders[j];
		}
		base = new int[maxOrder+2];
		base[0]=1;
		for(int i=1; i<base.length; i++) {
			base[i]=base[i-1]*numLabels;
		}
		this.numStates = (int) Math.pow(numLabels, maxOrder+1);
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
	public int[] get(Instance inst, Object label, int times) {
		int[][] data = (int[][])inst.getData();
		int[] target = (int[]) inst.getTarget();
		int[] predict = (int[]) label;
		int ne=0;
		int tS=0, pS=0;
		double diff = 0;
		double diffW = 0;
		double delta = 0;
		int len = 0;
		int[] index = new int[2*data.length*numTemplets];
		int[] value = new int[2*data.length*numTemplets];
		for(int l=-maxOrder-1, r=0; r<data.length; l++, r++) {
			tS = tS*numLabels%numStates+target[r];
			pS = pS*numLabels%numStates+predict[r];
			if(predict[r] != target[r]) ne++;
			if(l>=0 && (predict[l] != target[l])) ne--;
			if(ne>0) { 
				delta++;
				for(int t=0; t<numTemplets; t++) {
					if(data[r][t] == -1) continue;
					int tI = data[r][t]+offset[t][tS];
					int pI = data[r][t]+offset[t][pS];
					if(tI != pI) {
						int k;
						for(k=0; k<len; k++)
							if(index[k] == tI){
								value[k]++; break;
							}
						if(k == len) {
							index[len] = tI;
							value[len] = 1;
							++len;
						}
						for(k=0; k<len; k++)
							if(index[k] == pI){
								value[k]--; break;
							}
						if(k == len) {
							index[len] = pI;
							value[len] = -1;
							++len;
						}
						diffW += weights[tI] - weights[pI];
					}
				}
			}
		}
		for(int k=0; k<len; k++) {
			diff += value[k]*value[k];
		}
		double alpha;
		if(diffW <= delta) {
		tS = 0; pS = 0;
		ne = 0;
		alpha = Math.min(1.0, (delta - diffW) / diff); 
		for(int l=-maxOrder-1, r=0; r<data.length; l++, r++) {
			tS = tS*numLabels%numStates+target[r];
			pS = pS*numLabels%numStates+predict[r];
			if(predict[r] != target[r]) ne++;
			if(l>=0 && (predict[l] != target[l])) ne--;
			if(ne>0) {
				for(int t=0; t<numTemplets; t++) {
					if(data[r][t] == -1) continue;
					int tI = data[r][t]+offset[t][tS];
					int pI = data[r][t]+offset[t][pS];
					weights[tI] += alpha;
					weights[pI] -= alpha;
					averageWeights[tI] += alpha*times;
					averageWeights[pI] -= alpha*times;
				}
			}
		}
		} else {
			System.err.print(":( ");
		}
		return null;
	}
}
