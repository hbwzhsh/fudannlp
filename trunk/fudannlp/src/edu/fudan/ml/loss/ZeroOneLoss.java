package edu.fudan.ml.loss;
import java.util.List;
import edu.fudan.ml.types.Tree;
public class ZeroOneLoss implements Loss {
	public double calc(Object o1, Object o2) {
		if ((o1 instanceof List) && 
				(o2 instanceof List)) {
			boolean eq = true;
			List l1 = (List)o1;
			List l2 = (List)o2;
			for(int i=0; i<l1.size(); i++) {
				if (!l1.get(i).equals(l2.get(i))){
					eq = false;
					break;
				}
			}
			return eq?0:1;
		} else if ((o1 instanceof int[]) &&
				(o2 instanceof int[])) {
			boolean eq = true;
			int[] l1 = (int[])o1;
			int[] l2 = (int[])o2;
			for(int i=0; i<l1.length; i++) {
				if (l1[i] != l2[i]){
					eq = false;
					break;
				}
			}
			return eq?0:1;
		} else {
			return o1.equals(o2)?0:1;
		}
	}
}
