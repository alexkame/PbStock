package com.pengbo.mhdzq.data;

import java.io.Serializable;

public class TagLocalOptionRecord implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String	code;//代码
    public short	market;			//市场，为本地类型
    public String	TargetSymbol; //标的合约
    public String	StockCode;//标的代码
    public short	StockMarket; //标的市场，为本地类型
    public String	StockMarketCode;//标的市场代码
    public byte    OptionAttr; //期权类型
    public byte	OptionType; //行权方式
    public float	StrikePrice; //行权价
    public int		StrikeDate; //行权到期日
    public int		StrikeDateNoDay; //行权到期日,只有年月
    public int		StrikeUnit;   //行权比例
    public byte	OptionCP;     //期权方向 0->看涨(认购)  1->看跌(认沽)
    public byte	OptionAdjust; //合约调整
    public byte	OptionLife;   //合约存续
	public byte	OpenLimit;    //限制开仓
	public double  dHistoryVolatility;    //标的物历史波动率
	public float	Delta; //Delta
	public float	Gamma; //Gamma
	public float	Rho; //Rho
	public float	Theta; //Theta
	public float	Vega; //Vega
	
	public TagLocalOptionRecord()
	{
		code = new String();
		TargetSymbol = new String();
		StockCode = new String();
		StockMarketCode = new String();
		OptionCP = -1;
	}
	public void copyData(TagLocalOptionRecord aOptionRecord)
	{
		code = new String(aOptionRecord.code);
		market = aOptionRecord.market;
		TargetSymbol = new String(aOptionRecord.TargetSymbol);
		StockCode = new String(aOptionRecord.StockCode);
		StockMarket = aOptionRecord.StockMarket;
		StockMarketCode = new String(aOptionRecord.StockMarketCode);
		OptionAttr = aOptionRecord.OptionAttr;
		OptionType = aOptionRecord.OptionType;
		StrikePrice = aOptionRecord.StrikePrice;
		StrikeDate = aOptionRecord.StrikeDate;
		StrikeDateNoDay = aOptionRecord.StrikeDateNoDay;
		StrikeUnit = aOptionRecord.StrikeUnit;
		OptionCP = aOptionRecord.OptionCP;
		OptionAdjust = aOptionRecord.OptionAdjust;
		OptionLife = aOptionRecord.OptionLife;
		OpenLimit = aOptionRecord.OpenLimit;
		dHistoryVolatility = aOptionRecord.dHistoryVolatility;
		Delta = aOptionRecord.Delta;
		Gamma = aOptionRecord.Gamma;
		Rho = aOptionRecord.Rho;
		Theta = aOptionRecord.Theta;
		Vega = aOptionRecord.Vega;
	}
}
