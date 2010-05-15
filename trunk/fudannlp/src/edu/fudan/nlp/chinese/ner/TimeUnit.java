package edu.fudan.nlp.chinese.ner;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TimeUnit
{
	public String Time_Expression=null;
	public String Time_Norm="";
	public int[] time_full;
	public int[] time_origin;
	TimeNormalizer normalizer = null;
	public TimePoint _tp=new TimePoint();
	public TimePoint _tp_origin=new TimePoint();
	public class TimePoint
	{
		int [] tunit={-1,-1,-1,-1,-1,-1};
	}
	public TimeUnit(String exp_time, TimeNormalizer n)
	{
		Time_Expression=exp_time;
		normalizer = n;
		Time_Normalization();
	}
	public void norm_setyear()
	{
		String rule="[0-9]{2}(?=年)";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[0]=Integer.parseInt(match.group());
			if(_tp.tunit[0] >= 0 && _tp.tunit[0] < 100){
				if(_tp.tunit[0]<30)
					_tp.tunit[0] += 2000;
				else
					_tp.tunit[0] += 1900;
			}
		}
		rule="[0-9]?[0-9]{3}(?=年)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[0]=Integer.parseInt(match.group());
		}
	}
	public void norm_setmonth()
	{
		String rule="((10)|(11)|(12)|([1-9]))(?=月)";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[1]=Integer.parseInt(match.group());
		}	
	}
	public void norm_setday()
	{
		String rule="((?<!\\d))([0-3][0-9]|[1-9])(?=(日|号))";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[2]=Integer.parseInt(match.group());
		}	
	}
	public void norm_sethour()
	{
		String rule="(?<!(周|星期))([0-2]?[0-9])(?=(点|时))";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[3]=Integer.parseInt(match.group());
		}	
		rule = "(中午)|(午间)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
				_tp.tunit[3] += 12;
		}
		rule = "(下午)|(午后)|(pm)|(PM)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
		}
		rule = "晚";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if(_tp.tunit[3] == 12)
				_tp.tunit[3] = 0;
		}
	}
	public void norm_setminute()
	{
		String rule="([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!小)[点时]))[0-5]?[0-9](?!刻))";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			if(match.group().equals("")){
			}
			else
			_tp.tunit[4]=Integer.parseInt(match.group());
		}
		rule = "(?<=[点时])[1一]刻(?!钟)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			_tp.tunit[4] = 15;
		}
		rule = "(?<=[点时])半";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			_tp.tunit[4] = 30;
		}
		rule = "(?<=[点时])[3三]刻(?!钟)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			_tp.tunit[4] = 45;
		}
	}
	public void norm_setsecond()
	{
		String rule="([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[5]=Integer.parseInt(match.group());
		}	
	}
	public void norm_setTotal()
	{
		String rule;
		Pattern pattern;
		Matcher match;
		String[] tmp_parser;
		String tmp_target;
		rule="(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split(":");
			_tp.tunit[3]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[4]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[5]=Integer.parseInt(tmp_parser[2]);
		}
		else{
			rule="(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]";
			pattern=Pattern.compile(rule);
			match=pattern.matcher(Time_Expression);
			if(match.find())
			{
				tmp_parser=new String[2];
				tmp_target=match.group();
				tmp_parser=tmp_target.split(":");
				_tp.tunit[3]=Integer.parseInt(tmp_parser[0]);
				_tp.tunit[4]=Integer.parseInt(tmp_parser[1]);
			}
		}
		rule = "(中午)|(午间)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
				_tp.tunit[3] += 12;
		}
		rule = "(下午)|(午后)|(pm)|(PM)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
		}
		rule = "晚";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if(_tp.tunit[3] == 12)
				_tp.tunit[3] = 0;
		}
		rule="[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split("-");
			_tp.tunit[0]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[1]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[2]=Integer.parseInt(tmp_parser[2]);
		}
		rule="((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split("/");
			_tp.tunit[1]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[2]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[0]=Integer.parseInt(tmp_parser[2]);
		}
		rule="[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split("\\.");
			_tp.tunit[0]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[1]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[2]=Integer.parseInt(tmp_parser[2]);
		}
	}
	public void norm_setBaseRelated(){
		String [] time_grid=new String[6];
		time_grid=normalizer.getTimeBase().split("-");
		int[] ini = new int[6];
		for(int i = 0 ; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(ini[0], ini[1]-1, ini[2], ini[3], ini[4], ini[5]);
		boolean[] flag = {false,false,false};
		String rule="\\d+(?=天[以之]?前)";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int day = Integer.parseInt(match.group());
			calendar.add(Calendar.DATE, -day);
		}
		rule="\\d+(?=天[以之]?后)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int day = Integer.parseInt(match.group());
			calendar.add(Calendar.DATE, day);
		}
		rule="\\d+(?=(个)?月[以之]?前)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			int month = Integer.parseInt(match.group());
			calendar.add(Calendar.MONTH, -month);
		}
		rule="\\d+(?=(个)?月[以之]?后)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			int month = Integer.parseInt(match.group());
			calendar.add(Calendar.MONTH, month);
		}
		rule="\\d+(?=年[以之]?前)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			int year = Integer.parseInt(match.group());
			calendar.add(Calendar.YEAR, -year);
		}
		rule="\\d+(?=年[以之]?后)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			int year = Integer.parseInt(match.group());
			calendar.add(Calendar.YEAR, year);
		}
		String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
		String[] time_fin = s.split("-");
		if(flag[0]||flag[1]||flag[2]){
			_tp.tunit[0] = Integer.parseInt(time_fin[0]);
		}
		if(flag[1]||flag[2])
			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
		if(flag[2])
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);
	}
	public void norm_setCurRelated(){
		String [] time_grid=new String[6];
		time_grid=normalizer.getOldTimeBase().split("-");
		int[] ini = new int[6];
		for(int i = 0 ; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(ini[0], ini[1]-1, ini[2], ini[3], ini[4], ini[5]);
		boolean[] flag = {false,false,false};
		String rule="前年";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, -2);
		}
		rule="去年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, -1);
		}
		rule="今年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, 0);
		}
		rule="明年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, 1);
		}	
		rule="后年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, 2);
		}	
		rule="上(个)?月";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			calendar.add(Calendar.MONTH, -1);
		}
		rule="(本|这个)月";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			calendar.add(Calendar.MONTH, 0);
		}
		rule="下(个)?月";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			calendar.add(Calendar.MONTH, 1);
		}
		rule="大前天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, -3);
		}
		rule="(?<!大)前天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, -2);
		}
		rule="昨";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, -1);
		}
		rule="今(?!年)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 0);
		}
		rule="明(?!年)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 1);
		}
		rule="(?<!大)后天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 2);
		}
		rule="大后天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 3);
		}
		rule="(?<=(上上(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, -2);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		rule="(?<=((?<!上)上(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		rule="(?<=((?<!下)下(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 1);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		rule="(?<=(下下(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 2);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		rule="(?<=((?<!(上|下))(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 0);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
		String[] time_fin = s.split("-");
		if(flag[0]||flag[1]||flag[2]){
			_tp.tunit[0] = Integer.parseInt(time_fin[0]);
		}
		if(flag[1]||flag[2])
			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
		if(flag[2])
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);
	}
	public void modifyTimeBase(){
		String [] time_grid=new String[6];
		time_grid=normalizer.getTimeBase().split("-");
		String s = "";
		if(_tp.tunit[0] != -1)
			s += Integer.toString(_tp.tunit[0]);
		else
			s += time_grid[0];
		for(int i = 1; i < 6; i++){
			s += "-";
			if(_tp.tunit[i] != -1)
				s += Integer.toString(_tp.tunit[i]);
			else
				s += time_grid[i];
		}
		normalizer.setTimeBase(s);
	}
	public void Time_Normalization()
	{
		norm_setyear();
		norm_setmonth();
		norm_setday();
		norm_sethour();
		norm_setminute();
		norm_setsecond();
		norm_setTotal();
		norm_setBaseRelated();
		norm_setCurRelated();
		modifyTimeBase();
		_tp_origin.tunit = _tp.tunit.clone();
		String [] time_grid=new String[6];
		time_grid=normalizer.getTimeBase().split("-");
		int tunitpointer=5;
		while (tunitpointer>=0 && _tp.tunit[tunitpointer]<0)
		{
			tunitpointer--;
		}
		for (int i=0;i<tunitpointer;i++)
		{
			if (_tp.tunit[i]<0)
				_tp.tunit[i]=Integer.parseInt(time_grid[i]);
		}
		String[] _result_tmp=new String[6];
		_result_tmp[0]=String.valueOf(_tp.tunit[0]);
		if (_tp.tunit[0]>=10 &&_tp.tunit[0]<100)
		{
			_result_tmp[0]="19"+String.valueOf(_tp.tunit[0]);
		}
		if (_tp.tunit[0]>0 &&_tp.tunit[0]<10)
		{
			_result_tmp[0]="200"+String.valueOf(_tp.tunit[0]);
		}
		for(int i=1;i<6;i++)
			_result_tmp[i]=String.valueOf(_tp.tunit[i]);
		for(int i=0;i<5;i++)
			Time_Norm+=_result_tmp[i]+"^";
		Time_Norm+=_result_tmp[5];
		time_full = _tp.tunit.clone();
		time_origin = _tp_origin.tunit.clone();
	}
}
