package edu.fudan.nlp.chinese.ner;
public class Demo_NumberTranslator {
	public static void main(String[] args){
		String target = "七千零五十一万零三百零五";
		String s  = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
		target = "一千六加一五八零";
		s = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
		target = "周三十三点";
		s = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
		target = "三十三点";
		s = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
	}
}
