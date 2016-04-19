
# FudanNLP Web Services #
采用REST架构，直接通过URL直接调用。通过后台丰富的知识库支持，可提供更好的处理性能。

调用格式为：

http://api2.lingclouds.com/{seg|ner|pos|key|time|tree}/{input}

如：

http://api2.lingclouds.com/seg/他说的确实在理

# 已知问题 #
**受URL长度限制，目前最大的输入长度限制为700汉字。**

# 封装SDK #

## Java SKD ##

```
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;


/**
 * FudanNLP Web Services使用示例 
 * @author 赵嘉亿
 */
public class Demo {
	
	static String u = "http://api2.lingclouds.com/";
	
	public static String nlp(String func, String input) throws IOException {	
		// must encode url!! if we write FudannlpResource.seg(String) this way
		input = URLEncoder.encode(input, "utf-8"); //utf-8 重要!
		URL url = new URL( u + func + "/" + input);
		
		StringBuffer sb = new StringBuffer();
		BufferedReader out = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8")); //utf-8 重要!
		String line;
		while ((line = out.readLine()) != null) 
			sb.append(line);
		out.close();
		return sb.toString();
	}
	
	public static String welcome() throws IOException {
		URL url = new URL(u);
		StringBuffer sb = new StringBuffer();
		BufferedReader out = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		String line;
		while ((line = out.readLine()) != null) 
			sb.append(line);
		out.close();
		return sb.toString();
	}
	
	public static String nlp(String func) throws IOException {
		URL url = new URL(u + func);
		StringBuffer sb = new StringBuffer();
		BufferedReader out = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		String line;
		while ((line = out.readLine()) != null) 
			sb.append(line);
		out.close();
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		// 也可直接用IE访问 http://api2.lingclouds.com/seg/开源中文自然语言处理工具包 FudanNLP
		System.out.println(welcome());
		System.out.println(nlp("key"));
		System.out.println(nlp("seg", "开源中文自然语言处理工具包 FudanNLP"));
		System.out.println(nlp("ner", "开源中文自然语言处理工具包 FudanNLP"));
		System.out.println(nlp("time", "2010-10-10"));
	}
}

```

## 其他语言SDK ##
欢迎大家共享其他语言的SDK。