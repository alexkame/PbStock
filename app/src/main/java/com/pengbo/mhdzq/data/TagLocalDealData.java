package com.pengbo.mhdzq.data;

public class TagLocalDealData {
	
	public	static final int  MAX_NUM_DETAILDATA_ARRAY	= 12;    //显示最多的明细数据

	public int		date;			//日期YYYYMMDD
	public int		time;			//时间HHMM
	public int		now;			//现价，放大PriceRate倍
	public double		ccl;			//持仓量，单位股
	public double		volume;			//成交量，单位股
	public byte		inoutflag;		//内盘盘标志：1 外盘、2内盘、0不清3平盘 注意：其他无效
	
	public TagLocalDealData() {}
	
	public TagLocalDealData(int time, int now, int volume, byte flag)
	{
		this.time = time;
		this.now = now;
		this.volume = volume;
		this.inoutflag = flag;
	}
}
