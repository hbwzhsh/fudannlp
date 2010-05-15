package edu.fudan.ml.similarity;
public class EditDistance {
	public float calc(String cSeq1,
			String cSeq2) {
		int xsLength = cSeq1.length() + 1;
		int ysLength = cSeq2.length() + 1;
		float[] lastSlice = new float[ysLength];
		float[] currentSlice = new float[ysLength];
		currentSlice[0]=0;
		for (int y = 1; y < ysLength; ++y)
			currentSlice[y] = currentSlice[y-1] + costIns(cSeq2.charAt(y-1));
		for (int x = 1; x < xsLength; ++x) {
			char cX = cSeq1.charAt(x-1);
			float[] lastSliceTmp = lastSlice;
			lastSlice = currentSlice;
			currentSlice = lastSliceTmp;
			currentSlice[0] = lastSlice[0]+costDel(cSeq1.charAt(x-1));
			for (int y = 1; y < ysLength; ++y) {
				int yMinus1 = y - 1;
				char cY = cSeq2.charAt(yMinus1);
				currentSlice[y] = Math.min(cX == cY
						? lastSlice[yMinus1]
						            : costReplace(cX,cY) + lastSlice[yMinus1],
						             Math.min(costDel(cX)+lastSlice[y],
						            		costIns(cY)+currentSlice[yMinus1]));
			}
		}
		return currentSlice[currentSlice.length-1];
	}
	static String noCostChars = "的 最和";
	static String maxCostChars = "不";
	protected static float costIns(char c) {
		if(noCostChars.indexOf(c)!=-1)
			return 0;
		if(maxCostChars.indexOf(c)!=-1)
			return 5;
		return 1;
	}
	protected static float costDel(char c) {
		if(noCostChars.indexOf(c)!=-1)
			return 0;
		if(maxCostChars.indexOf(c)!=-1)
			return 5;
		return 1;
	}
	protected static float costReplace(char x, char y) {
		return 1;
	}
	public double sim(String str1, String str2) {   
		float ld = calc(str1, str2);   
		return 1 - ld / Math.max(str1.length(), str2.length());    
	}   
}
