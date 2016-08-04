package com.pengbo.mhdzq.tools;

import java.util.Calendar;
import java.util.Date;

import com.pengbo.mhdzq.data.TagLocalStockData;

public class KdateTools {

	
	
	/*public static LONDATE transfer_date_ymd(int date,LONDATE lonDate)
	{
		lonDate.yy = 0;
	    lonDate.mm = 0;
		lonDate.dd = 0;
		if( date>=100000000l || date<10000l )
			return null;
		lonDate.yy = (short)(date/10000l);
		lonDate.mm = (short)((date/100l)%100l);
		lonDate.dd = (short)(date%100);
		if( lonDate.mm<=0 || lonDate.dd<=0 || lonDate.mm>12 || lonDate.dd>31 )
			return null;
		return lonDate;
	}*/
	
	public static int getYY(int date)
	{
		if( date>=100000000l || date<10000l )
			return -1;
		
		int yy = (int) (date/10000l);
		
		return yy;
	}
	
	public static int getMM(int date)
	{
		if( date>=100000000l || date<10000l )
			return -1;
		
		int mm = (int) ((date/100l)%100l);
		
		if (mm < 0 || mm > 12)
			return -1;
		
		return mm;
	}
	
	public static int getDD(int date)
	{
		if( date>=100000000l || date<10000l )
			return -1;
		
		int dd = (short)(date%100);
		
		if (dd < 0 || dd > 31)
			return -1;
		
		return dd;
	}
	
	public static int lon_weekday(int date)
	{
		int yy,mm,dd,temp;

	    yy = getYY(date);
	    mm = getMM(date);
	    dd = getDD(date);
		mm -= 2;
		if( mm<=0 )	{ mm += 12;	yy--; }
		temp = yy/100;
		yy %= 100;
		return (((13*mm-1)/5+dd+yy+yy/4+temp/4-temp-temp)%7+7)%7;
	}
	
	public static boolean same_week(int day1,int day2)
	{
	    int d1 = lastday_of_week(day1);
	    int d2 = lastday_of_week(day2);
	    
	    boolean ret = false;
	    if(getYY(d1) == getYY(d2) && getMM(d1) == getMM(d2) && getDD(d1) == getDD(d2)) 
	    ret = true;
		
	    return ret;
	}
	
	public static boolean same_month(int d1,int d2)
	{
		if(getYY(d1) == getYY(d2) && getMM(d1) == getMM(d2))
			return true;
		
		return false;
	}
	
	// 判断是否为同一天
	public static boolean same_day(int d1,int d2)
	{
		if(getYY(d1) == getYY(d2) && getMM(d1) == getMM(d2) && getDD(d1) == getDD(d2))
			return true;
		
		return false;
	}
	
	// 判断time2是否在time1 一分钟内
	public static boolean in_minute(int time1,int time2)
	{
		
		if(time1 - time2 > 0)
		{
			return true;
		}
		return false;
	}
	
	//time - hhmm
	public static int getNextTimeWithMinStep(int time,int step)
	{
		int minute = time%100;
		int hh = time/100;
		minute += step;
		
		hh += minute/60;
		minute = minute%60;
		
		return hh*100 + minute;
	}
	
	
	public static int lon_nextday(int date)
	{
		int ld[]={ 31,28,31,30,31,30,31,31,30,31,30,31 };

	    if( ( getYY(date) % 400 == 0 ) || ( ( getYY(date) % 100 != 0 ) && ( getYY(date) % 4 == 0 ) ) ) // 闰年
			ld[1]++;
	    
	    int day = getDD(date);
	    int month = getMM(date);
	    int year = getYY(date);

	    day++;
		if( day<=0 || day>ld[month-1] )
		{
			day = 1;  month++;
			if( month>12 )
			{
	            month = 1; year++;
			}
		}
		
		int lonDay = year*10000+month*100+day;
		return lonDay;
	}
	
	public static int lastday_of_week(int date)
	{
		int  wkd = lon_weekday(date);
	    for( int i=0;i<6-wkd;i++ )
	        date = lon_nextday(date);
		return date;
	}
	
    /*public static int  LastDay(int date)
    {
    	
        int month = getMM(date);
        int day = getDD(date);
        int year = getYY(date);
        
    	if( month > 12 || month < 1|| day > 31 || day < 1 ) return -1;
        if( day == 1 )
        {
            if( month == 1 ) { -- year; month = 12; day = 31; }
            else
            {
                -- month;
                UINT uDaysOfMonth[12] = { 31U, 28U, 31U, 30U, 31U, 30U, 31U, 31U, 30U, 31U, 30U, 31U };
                
                UINT uCurMonthDayCount = uDaysOfMonth[Month - 1];
                if( Month == 2U && ( ( Year % 400U == 0U ) || ( ( Year % 100U != 0U ) && ( Year % 4U == 0U ) ) ) ) ++ uCurMonthDayCount;
                Day = uCurMonthDayCount;
            }
        }
        else -- Day;
    }*/
	
	public static int lastday_of_month(int date)
	{
		int uDaysOfMonth[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		
		int month = getMM(date);
		int day = getDD(date);
		int year = getYY(date);
		int lastday = -1;
		
		if( month > 12 || month < 1|| day > 31 || day < 1 ) return -1;
		
		int currentDayCount = uDaysOfMonth[month-1];
		
		if( month == 2 && ( ( year % 400 == 0 ) || ( ( year % 100 != 0 ) && ( year % 4 == 0 ) ) ) ) ++ currentDayCount;
		lastday = currentDayCount;
		
		return lastday;
	}
	
	//根据行情数据返回交易总时间（分钟）
	public static int GetWorkTime( TagLocalStockData stockData )
	{
	    if( stockData == null )
	    {
	        return 0;
	    }
	    int workTime = 0;
	    for( int i = 0; i < stockData.TradeFields; i++ )
	    {
	        workTime += STD.getMinutes(stockData.Start[i], stockData.End[i]);
	    }
	    return workTime;
	}
	
	public static boolean IsInTradeTime( int time, TagLocalStockData stockData )
	{
	    if( stockData == null )
	    {
	        return false;
	    }
	    boolean ret = false;
	    for( int i = 0; i < stockData.TradeFields; ++ i )
	    {
	    	if ((time >= stockData.Start[i] && time <= stockData.End[i])
	    			|| ((time + 2400) >= stockData.Start[i] && (time + 2400) <= stockData.End[i]))
	    	{
	    		ret = true;
	    		break;
	    	}
	    }
	    return ret;
	}
	
	// 将时间点映射到当天的交易所真实时间上 0 -> 09:30, 239 -> 14:59
	// 这个函数用的时候要注意了
	public static int PointToTime( int point, TagLocalStockData stockData )
	{
	    if( stockData == null )
	    {
	        return 0;
	    }
	    int workTime = GetWorkTime(stockData);
	    //point -= 1;
	    if( point > workTime )
	    {
	        point = workTime;// - 1;// 这里原来 - 1U; 的。 不知道为什么，先去掉 知道为什么了要写注释
	    }

	    for( int i = 0; i < stockData.TradeFields; ++ i )
	    {
	        if( point < STD.getMinutes( stockData.Start[i], stockData.End[i]))
	        {
	            return (int)STD.getTimeWithAdd(stockData.Start[i], point);
	        }
	        point -= STD.getMinutes( stockData.Start[i], stockData.End[i] );
	    }
	    return (int)stockData.End[stockData.TradeFields - 1];
	}
	
	//时间转成点，比如9：30 -- 0
	//time - hhmmss
	public static int TimeToPoint(int time, TagLocalStockData stockData ) // min = hours * 60 + minutes
	{
		if(stockData == null)
		{
			return 0;
		}
	    int uResult = 0;
	    int hhmm = time/100;
	    if(stockData.TradeFields > 0 && stockData.End[stockData.TradeFields-1] > 2400)
	    {
	    	hhmm += 2400;
	    }
	    int ss = time%100;
	    int workTime = GetWorkTime(stockData);
	    for( int i = 0; i < stockData.TradeFields; ++ i )
	    {
	        if( hhmm < stockData.Start[i] || (hhmm == stockData.Start[i] && ss == 0))
	        {
	            return 0;
	        }
	        if( hhmm < stockData.End[i])
	        {
	            uResult += STD.getMinutes(stockData.Start[i], hhmm);
	            break;
	        }
	        // time >= Attr.Group[group].End[i]
	        if( i + 1 < stockData.TradeFields && hhmm < ( ( stockData.End[i] + stockData.Start[i+1] ) >> 1 ) )
	        {
	            uResult += STD.getMinutes(stockData.Start[i], stockData.End[i]);
	            break;
	        }
	        uResult += STD.getMinutes(stockData.Start[i], stockData.End[i]);
	    }
	    if (ss > 0)
        	uResult += 1;
	    return ( uResult < workTime ? uResult : ( workTime ) );
	}
	
	

	public static int getHour(Date date) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		return calendar.get(Calendar.HOUR_OF_DAY);

	}
	
	

	public static int getMinute(Date date) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		return calendar.get(Calendar.MINUTE);

	}



}
