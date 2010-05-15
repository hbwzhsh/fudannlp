package edu.fudan.nlp.chinese.ner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class stringPreHandlingModule {
	public static String delKeyword(String target, String rules){
		Pattern p = Pattern.compile(rules); 
		Matcher m = p.matcher(target); 
		StringBuffer sb = new StringBuffer(); 
		boolean result = m.find(); 
		while(result) { 
			m.appendReplacement(sb, ""); 
			result = m.find(); 
		}
		m.appendTail(sb);
		String s = sb.toString();
		//System.out.println("字符串："+target+" 的处理后字符串为：" +sb);
		return s;
	}
	public static String numberTranslator(String target){
		Pattern p = Pattern.compile("[一二两三四五六七八九123456789]万[一二两三四五六七八九123456789](?!(千|百|十))"); 
		Matcher m = p.matcher(target); 
		StringBuffer sb = new StringBuffer(); 
		boolean result = m.find(); 
		while(result) { 
			String group = m.group();
			String[] s = group.split("万");
			int num = 0;
			if(s.length == 2){
				num += wordToNumber(s[0])*10000 + wordToNumber(s[1])*1000;
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("[一二两三四五六七八九123456789]千[一二两三四五六七八九123456789](?!(百|十))"); 
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			String group = m.group();
			String[] s = group.split("千");
			int num = 0;
			if(s.length == 2){
				num += wordToNumber(s[0])*1000 + wordToNumber(s[1])*100;
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("[一二两三四五六七八九123456789]百[一二两三四五六七八九123456789](?!十)"); 
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			String group = m.group();
			String[] s = group.split("百");
			int num = 0;
			if(s.length == 2){
				num += wordToNumber(s[0])*100 + wordToNumber(s[1])*10;
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("[零一二两三四五六七八九]"); 
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			m.appendReplacement(sb, Integer.toString(wordToNumber(m.group()))); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("(?<=(周|星期))[天日]"); 
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			m.appendReplacement(sb, Integer.toString(wordToNumber(m.group()))); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("(?<!(周|星期))0?[0-9]?十[0-9]?"); 
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("十");
			int num = 0;
			if(s.length == 0){
				num += 10;
			}
			else if(s.length == 1){
				int ten = Integer.parseInt(s[0]);
				if(ten == 0)
					num += 10;
				else num += ten*10;
			}
			else if(s.length == 2){
				if(s[0].equals(""))
					num += 10;
				else{
					int ten = Integer.parseInt(s[0]);
					if(ten == 0)
						num += 10;
					else num += ten*10;
				}
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("0?[1-9]百[0-9]?[0-9]?"); 
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("百");
			int num = 0;
			if(s.length == 1){
				int hundred = Integer.parseInt(s[0]);
				num += hundred*100;
			}
			else if(s.length == 2){
				int hundred = Integer.parseInt(s[0]);
				num += hundred*100;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("0?[1-9]千[0-9]?[0-9]?[0-9]?"); 
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("千");
			int num = 0;
			if(s.length == 1){
				int thousand = Integer.parseInt(s[0]);
				num += thousand*1000;
			}
			else if(s.length == 2){
				int thousand = Integer.parseInt(s[0]);
				num += thousand*1000;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		p = Pattern.compile("[0-9]+万[0-9]?[0-9]?[0-9]?[0-9]?"); 
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("万");
			int num = 0;
			if(s.length == 1){
				int tenthousand = Integer.parseInt(s[0]);
				num += tenthousand*10000;
			}
			else if(s.length == 2){
				int tenthousand = Integer.parseInt(s[0]);
				num += tenthousand*10000;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		return target;
	}
	private static int wordToNumber(String s){
		if(s.equals("零")||s.equals("0"))
			return 0;
		else if(s.equals("一")||s.equals("1"))
			return 1;
		else if(s.equals("二")||s.equals("两")||s.equals("2"))
			return 2;
		else if(s.equals("三")||s.equals("3"))
			return 3;
		else if(s.equals("四")||s.equals("4"))
			return 4;
		else if(s.equals("五")||s.equals("5"))
			return 5;
		else if(s.equals("六")||s.equals("6"))
			return 6;
		else if(s.equals("七")||s.equals("天")||s.equals("日")||s.equals("7"))
			return 7;
		else if(s.equals("八")||s.equals("8"))
			return 8;
		else if(s.equals("九")||s.equals("9"))
			return 9;
		else return -1;
	}
}
