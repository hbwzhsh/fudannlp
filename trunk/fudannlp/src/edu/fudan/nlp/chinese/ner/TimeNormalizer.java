package edu.fudan.nlp.chinese.ner;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
public class TimeNormalizer {
	private String timeBase;
	private String oldTimeBase;
	private static String rules = null;
	private String target;
	private TimeUnit[] timeToken = new TimeUnit[0];
	public TimeNormalizer(){
		rules=null;
		String _ruleunit=null,mark=null;
		try {		
			InputStreamReader  read = new InputStreamReader(this.getClass().getResourceAsStream("/model/model.ner.TimeExp-Rules.txt"),"utf-8");
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
			System.out.println("正则表达式文件未找到！");
		}
	}
	public TimeNormalizer(String path){
		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream(path),"utf-8");
			//InputStreamReader  read = new InputStreamReader(this.getClass().getResourceAsStream("/model/peopledaily.ner.model.gz"),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			String _ruleunit = bin.readLine();
			while (_ruleunit!=null)
			{
				if (!_ruleunit.startsWith("-"))
				{
					rules=rules+"|("+_ruleunit+")";
				}	
				_ruleunit=bin.readLine();
			}
		}catch(Exception e){
			System.out.println("正则表达式文件未找到！");
		}
	}
	public void parse(String target,String timeBase){
		this.target = target;
		this.timeBase = timeBase;
		this.oldTimeBase = timeBase;
		preHandling();
		timeToken = TimeEx(this.target,timeBase);
	}
	public void parse(String target){
		this.target = target;
		this.timeBase = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
		this.oldTimeBase = timeBase;
		preHandling();
		timeToken = TimeEx(this.target,timeBase);
	}
	public String getTimeBase(){
		return timeBase;
	}
	public String getOldTimeBase(){
		return oldTimeBase;
	}
	public void setTimeBase(String s){
		timeBase = s;
	}
	public void resetTimeBase(){
		timeBase = oldTimeBase;
	}
	public TimeUnit[] getTimeUnit(){
		return timeToken;
	}
	private void preHandling(){
		target = stringPreHandlingModule.delKeyword(target, "\\s+"); //清理空白符
		target = stringPreHandlingModule.delKeyword(target, "[的]+"); //清理语气助词
		target = stringPreHandlingModule.numberTranslator(target);
	}
	private TimeUnit[] TimeEx(String tar,String timebase)
	{
		Pattern patterns;
		Matcher match;
		int startline=-1,endline=-1;
		String [] temp = new String[99];
		int rpointer=0;
		TimeUnit[] Time_Result = null;
		patterns=Pattern.compile(rules);
		match=patterns.matcher(tar);	
		boolean startmark=true;
		while(match.find())
		{
			startline=match.start();
			if (endline==startline) 
			{
				rpointer--;
				temp[rpointer]=temp[rpointer]+match.group();
			}
			else
			{
				if(!startmark)
				{
					rpointer--;
					rpointer++;	
				}	
				startmark=false;
				temp[rpointer]=match.group();
			}
			endline=match.end();
			rpointer++;
		}
		if(rpointer>0)
		{
			rpointer--;
			rpointer++;
		}
		Time_Result=new TimeUnit[rpointer];
		//	System.out.println("Basic Data is " + timebase); 
		for(int j=0;j<rpointer;j++)
		{
			Time_Result[j]=new TimeUnit(temp[j],this);
		}
		return Time_Result;
	}
}
