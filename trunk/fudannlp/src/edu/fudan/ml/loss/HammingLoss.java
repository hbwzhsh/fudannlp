package edu.fudan.ml.loss;
import java.util.List;
public class HammingLoss implements Loss {
	public double calc(Object o1, Object o2) {
		if ((o1 instanceof List) && 
				(o2 instanceof List)) {		
			int ne = 0;
			List l1 = (List)o1;
			List l2 = (List)o2;
			for(int i=0; i<l1.size(); i++) {
				if (!l1.get(i).equals(l2.get(i)))
					ne++;
			}
			return ne;
		} else if ((o1 instanceof int[]) &&
				(o2 instanceof int[])) {
			int ne = 0;
			int[] l1 = (int[])o1;
			int[] l2 = (int[])o2;
			for(int i=0; i<l1.length; i++) {
				if (l1[i] != l2[i])
					ne++;
			}
			return ne;
		}else
			return 0;
	}
}
