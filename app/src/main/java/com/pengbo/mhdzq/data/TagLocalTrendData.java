package com.pengbo.mhdzq.data;


public class TagLocalTrendData {
	
	public	static final int  MAX_TREND_NUM	= 1500;    //一天最大走势数据 
	
	public int		date;			//日期YYYYMMDD
	public int		time;			//时间HHMM
	public int		now;			//现价，放大PriceRate倍
	public double	volume;			//到当前为止的总成交量，单位股
	public double	amount;			//成交金额，单位分
	public double	volSell;		//外盘量(委卖总量)单位股
	public double	ccl;			//持仓量，单位股
	public int		average;		//均价
	public int		open;        //分钟开盘价，放大PriceRate倍
	public int		high;        //分钟最高价，放大PriceRate倍
	public int		low;        //分钟最低价，放大PriceRate倍

	public TagLocalTrendData() {}

	public TagLocalTrendData(int nDate, int nTime,int nNow, long lVolume,int mAverage
			, int nOpen, int nHigh, int nLow)
	{
		this.date	= nDate;
		this.time	= nTime;
		this.now	= nNow;
		this.volume	= lVolume;
		this.average= mAverage;
		this.open = nOpen;
		this.high = nHigh;
		this.low = nLow;
	}
	
	public void Clear()
	{
		this.date= 0;
		this.time	= 0;
		this.now	= 0;
		this.amount	= 0;
		this.volume		= 0;
		this.volSell	= 0;
		this.ccl		= 0;
		this.average=0;
		this.open = 0;
		this.high = 0;
		this.low = 0;
	}
	
	public void Copy(TagLocalTrendData data)
	{
		this.now	= data.now;
		this.date= data.date;
		this.time	= data.time;
		this.amount	= data.amount;
		this.volume		= data.volume;
		this.volSell	= data.volSell;
		this.ccl		= data.ccl;
		this.average=data.average;
		this.open = data.open;
		this.high = data.high;
		this.low = data.low;
	}
}
