package edu.fudan.nlp.chinese.ner;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TimeExpressionDetectionNormalization {
	public static String setrules_auto() throws IOException
	{
		String rules=null,_ruleunit=null,mark=null;
		//BufferedReader _rule_reader=new BufferedReader(new FileReader("../Data/model.ner.TimeExp-Rules.txt"));
		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream("../FudanNLP/model/model.ner.TimeExp-Rules.txt"),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			_ruleunit=bin.readLine();
			while (_ruleunit!=null)
			{
				if (!_ruleunit.startsWith("-"))
				{
					rules=rules+"|("+_ruleunit+")";
				}	
				_ruleunit=bin.readLine();
			}
		}catch(Exception e){
		}
		return rules;
	}
	public static String setrules()
	{
		String rules;
		String ruledate = null,ruletime=null,rulevocabulary=null,rulenumbervocabulary=null;
		String [] ruledates = new String[5];
		ruledates[0]="[12][09][0-9]{2}-[01][0-9]-[0-3][0-9]";
		ruledates[1]="[12][09][0-9]{2}(年)";
		ruledates[2]="((10)|(11)|(12)|([1-9])|元)月";
		ruledates[3]="([0-3][0-9]|[1-9])(日|号)";
		ruledates[4]="[0-3][0-9]/[01][0-9]/[12][09][0-9]{2}";
		for (int i=0;i<ruledates.length;i++)		
		{
			ruledate=ruledate+"|("+ruledates[i]+")";
		}
		String [] ruletimes=new String[2]; 
		ruletimes[0]="\\d+:\\d+:\\d+.\\d+";
		ruletimes[1]="(\\d+天)+(\\d+点)?(\\d+分)?(\\d+秒)?|(\\d+[点时])+(\\d+分)?(\\d+秒)?(左右)?|(\\d+分)+(\\d+秒)?|(\\d+秒)+";
		for (int i=0;i<ruletimes.length;i++)		
		{
			ruletime=ruletime+"|("+ruletimes[i]+")";
		}
		//	rulevocabulary="|(一|二|三|四|五|六|七|八|九|十|百|千|万|几|多)+(天|日|周|月|年)(后|前|)";
		rulevocabulary="当天|前天|昨天|今天|明天|后天"+"|[春夏秋冬](天|季)" +
		"|未来|近来|目前" +
		"|上午|中午|下午|早晨|傍晚|黄昏|午夜|凌晨|午后"+
		"|前年|去年|今年|明年|后年|新年" +"|[鼠牛虎兔龙蛇马羊猴鸡狗猪]年"+
		"|(一|二|三|四|五|六|七|八|九|十|百|千|万|几|多)+(天|日|周|月|年)(后|前|)"+
		"|(星期|周)(一|二|三|四|五|六|七)"+
		"|(元宵)节"+
		"|最近|稍后|农历|阴历|阳历|长期|生前|生后|(新)?世纪";
		rulenumbervocabulary="[12][0-9]世纪|\\d+年|\\d+大寿|\\d+[:：]\\d+(分|)|\\d+(天|日|周|月|年)(后|前|)";
		rules="("+ruledate+")|("+rulevocabulary+")|("+rulenumbervocabulary+")|("+ruletime+")";
		return rules;
	}
	public static TimeUnit[] TimeEx(String target,String timebase)
	{
		TimeNormalizer normalizer = new TimeNormalizer();
		normalizer.parse(target,timebase);
		return normalizer.getTimeUnit();
	}
	public static TimeUnit[] TimeEx(String target)
	{
		TimeNormalizer normalizer = new TimeNormalizer(target);
		return normalizer.getTimeUnit();
	}
	public static void main(String[] args) throws IOException
	{
		TimeUnit [] timeToken;
		String targetline;
		targetline = "1个钟头03刻钟";
		//targetline = "去年9月12号，未来，同年11月，今天，今年6月15日";
		//targetline = "周 3 的 0 7 点 的 1 5 PM";
		//targetline = "上星期 2 1:45 pm ";
//		targetline = "上周3,上周三,上星期三,上星期3,上周天,上周日,上星期天,上星期日,上周,上星期," +
//				"这周3,这周三,这星期三,这星期3,这周天,这周日,这星期天,这星期日,这周,这星期," +
//				"本周3,本周三,本星期三,本星期3,本周天,本周日,本星期天,本星期日,本周,本星期," +
//				"下周3,下周三,下星期三,下星期3,下周天,下周日,下星期天,下星期日,下周,下星期," +
//				"周3,周三,星期三,星期3,周天,周日,星期天,星期日,周,星期";
//		targetline = "大前天,前天,昨天,昨日,今天,今日,明天,明日,后天," +
//				"大后天,上个月,上月,本月,下个月,下月,前年,去年,今年," +
//				"明年,后年,早上,凌晨,早晨,清晨,上午,中午,午间,下午," +
//				"午后,晚上,傍晚,晚间,今早,今晚,明早,明晚";
		//targetline = "(2/12/2008新华社2008-2-12,2008.2.12)08年5月12日14时28分125秒左右，四川汶川发生8.0级特大地震...下午2:45，上海...19日全国哀悼日...1978年的唐山地震...";
		//String timebase="2008-5-12-16-28-56";
		timeToken=TimeEx(targetline);
		for(int i=0;i<timeToken.length;i++)
		{
			System.out.println(i+"	:	"+timeToken[i].Time_Expression);
			System.out.println(timeToken[i].Time_Norm);
		}
	}
}
