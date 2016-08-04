package com.pengbo.mhdzq.data;

import com.pengbo.mhdzq.app.MyApp;


public class TagLocalKLineData {
	
	public	static final int  MAX_KLINE_NUM	= 400;    //K线最大数据 
	
	public int       date;			//日期YYYYMMDD
	public int       time;			//时间HHMM
	public int       open;			//今日开盘价		放大PriceRate倍
	public int		high;			//今日最高价		放大PriceRate倍
	public int		low;			//今日最低价		放大PriceRate倍
	public int		close;			//今日收盘价		放大PriceRate倍
	public long		volume;			//今日成交量		单位股
	public long		amount;			//成交金额，单位分
	public double		ccl;			//持仓量，单位股
	public int       clearPrice;
    public double		volSell;		//外盘量(委卖总量)单位股
    public int       raiseNum;       //上涨家数
    public int       fallNum;        //下跌家数

	public TagLocalKLineData() {}
	
	public void Clear()
	{
		this.time	= 0;
		this.date	= 0;
		this.open	= 0;
		this.high	= 0;
		this.low	= 0;
		this.close	= 0;
		this.volume	= 0;
		this.amount	= 0;	
		this.ccl	= 0;
		this.clearPrice	= 0;
		this.volSell	= 0;
		this.raiseNum	= 0;
		this.fallNum	= 0;
	}
	
	public void Copy(TagLocalKLineData data)
	{
		this.time	= data.time;
		this.date	= data.date;
		this.open	= data.open;
		this.high	= data.high;
		this.low	= data.low;
		this.close	= data.close;
		this.volume	= data.volume;
		this.amount	= data.amount;
		this.ccl	= data.ccl;
		this.clearPrice	= data.clearPrice;
		this.volSell	= data.volSell;
		this.raiseNum	= data.raiseNum;
		this.fallNum	= data.fallNum;
	}
	
	public static class TagKAverageInfo {

		public int[] data;
		public int para;
		public int color; //line color

		public TagKAverageInfo() {
			data = new int[MyApp.MAXNUM_KLINE];
		}
	}
}
