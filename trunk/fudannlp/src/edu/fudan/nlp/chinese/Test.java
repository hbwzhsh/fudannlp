package edu.fudan.nlp.chinese;
import java.util.HashSet;
public class Test {
	public static void main(String[] args) {
//		int res = EditDistance.calc("最的我生  日","生日们");
//		String string = "'&";
//		string = string.replaceAll("(\\(.*\\)|(（.*）)|(【.*】))", "");
//		string = string.replaceAll(CharSets.allRegexPunc, "");
		HashSet<String> s =new HashSet<String>();
		s.add("1");
		s.add("2");
		s.add("1");
		System.out.println(s.toArray());
		System.out.println(s.toArray().toString());
		System.out.println(s.toString());
	}
}
