package edu.fudan.ml.feature.generator;
import java.io.Serializable;
import java.util.List;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Labelwise implements Serializable{
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
	public Labelwise(double[] weights, double[] averageWeights,
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
		double[] diff = new double[data.length];
		double[] diffW = new double[data.length];
		int[] delta = new int[data.length];
		int len = 0;
		int[][] index = new int[data.length][2*numTemplets];
		int[][] value = new int[data.length][2*numTemplets];
		for(int l=-maxOrder-1, r=0; r<data.length; l++, r++) {
			len = 0;
			tS = tS*numLabels%numStates+target[r];
			pS = pS*numLabels%numStates+predict[r];
			if(predict[r] != target[r]) ne++;
			if(l>=0 && (predict[l] != target[l])) ne--;
			if(ne>0) {
				delta[r] = 1;
				for(int t=0; t<numTemplets; t++) {
					if(data[r][t] == -1) continue;
					int tI = data[r][t]+offset[t][tS];
					int pI = data[r][t]+offset[t][pS];
					if(tI != pI) {
						int k;
						for(k=0; k<len; k++)
							if(index[r][k] == tI){
								value[r][k]++; break;
							}
						if(k == len) {
							index[r][len] = tI;
							value[r][len] = 1;
							++len;
						}
						for(k=0; k<len; k++)
							if(index[r][k] == pI){
								value[r][k]--; break;
							}
						if(k == len) {
							index[r][len] = pI;
							value[r][len] = -1;
							++len;
						}
						diffW[r] += weights[tI] - weights[pI];
					}
				}
			}
			for(int k=0; k<len; k++) {
				diff[r] += value[r][k]*value[r][k];
			}
			diffW[r] = 1-diffW[r];
		}
		int N = data.length;
		Matrix m1 = new Matrix(N, N);
		for(int r=0; r<data.length; r++) {
			for(int c=r; c<data.length; c++) {
				double dot = 0;
				for(int a=0; a<numTemplets; a++ ) {
					for(int b=0; b<numTemplets; b++) {
						if(index[r][a] == index[c][b]) {
							dot += value[r][a]*value[c][b]; 
						}
					}
				}
				m1.set(r, c, dot);
				m1.set(c, r, dot);
			}
		}
		SingularValueDecomposition de = new SingularValueDecomposition(m1);
		Matrix S = de.getS();
		Matrix U = de.getU();
		Matrix V = de.getV();
		int R = de.rank();
		S = S.getMatrix(0, R-1, 0, R-1);
		U = U.getMatrix(0, N-1, 0, R-1);
		V = V.getMatrix(0, N-1, 0, R-1);
		Matrix xx = V.times(S.inverse()).times(U.transpose()).times(new Matrix(diffW, N));
		double[] x = xx.getColumnPackedCopy();
		tS = 0; pS = 0;
		ne = 0;
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
					if(tI != pI) {
					if (diffW[r] <= delta[r]) {
						double alpha = x[r];
					weights[tI] += alpha;
					weights[pI] -= alpha;
					averageWeights[tI] += alpha*times;
					averageWeights[pI] -= alpha*times;
					}
					}
				}
			}
		}
//			System.err.print(":( ");
		return null;
	}
}
