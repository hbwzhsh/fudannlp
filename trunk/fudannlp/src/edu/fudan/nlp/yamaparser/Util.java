package edu.fudan.nlp.yamaparser;
public class Util {
	public static int[] stringsToInts(String[] stringreps) {
		int[] nums = new int[stringreps.length];
		for (int i = 0; i < stringreps.length; i++)
			nums[i] = Integer.parseInt(stringreps[i]);
		return nums;
	}
	public static double[] stringsToDoubles(String[] stringreps) {
		double[] vals = new double[stringreps.length];
		for (int i = 0; i < vals.length; i++)
			vals[i] = Double.parseDouble(stringreps[i]);
		return vals;
	}
	public static String[] intsToStrings(int[] intreps) {
		String[] stringreps = new String[intreps.length];
		for (int i = 0; i < intreps.length; i++)
			stringreps[i] = new Integer(intreps[i]).toString();
		return stringreps;
	}
	public static String join(String[] a, char sep) {
		StringBuffer sb = new StringBuffer();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++)
			sb.append(sep).append(a[i]);
		return sb.toString();
	}
	public static String join(int[] a, char sep) {
		StringBuffer sb = new StringBuffer();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++)
			sb.append(sep).append(a[i]);
		return sb.toString();
	}
}
