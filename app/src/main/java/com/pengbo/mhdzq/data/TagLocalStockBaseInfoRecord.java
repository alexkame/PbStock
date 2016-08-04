package com.pengbo.mhdzq.data;

import java.io.Serializable;

/**
 * 股票基础数据类
 */
public class TagLocalStockBaseInfoRecord implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String	code;//代码
    public short	market;			//市场，为本地类型
    public byte     StopFlag; //全天停牌标记
    public double   Avg5Volume; //5日均量
    public double   TotalCapital; //总股本
    public double   FlowCapital; //流通股本
    public double   AvgNetAssets; //每股净资产
    public double   LastAvgProfit; //上年度每股收益
    public double   ForecastAvgProfit; //预计本年度每股收益
    public double   NetProfit; //净利润
    public double   TotalAssets; //总资产
    public double   TotalDebt; //总负债
	
	public TagLocalStockBaseInfoRecord()
	{
		code = new String();
	}
	
	public void copyData(TagLocalStockBaseInfoRecord aStockBaseRecord)
	{
		code = new String(aStockBaseRecord.code);
		market = aStockBaseRecord.market;
		StopFlag = aStockBaseRecord.StopFlag;
		Avg5Volume = aStockBaseRecord.Avg5Volume;
		TotalCapital = aStockBaseRecord.TotalCapital;
		FlowCapital = aStockBaseRecord.FlowCapital;
		AvgNetAssets = aStockBaseRecord.AvgNetAssets;
		LastAvgProfit = aStockBaseRecord.LastAvgProfit;
		ForecastAvgProfit = aStockBaseRecord.ForecastAvgProfit;
		NetProfit = aStockBaseRecord.NetProfit;
		TotalAssets = aStockBaseRecord.TotalAssets;
		TotalDebt = aStockBaseRecord.TotalDebt;
	}
}
