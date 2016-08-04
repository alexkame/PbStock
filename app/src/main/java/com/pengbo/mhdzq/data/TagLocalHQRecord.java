package com.pengbo.mhdzq.data;

import java.io.Serializable;

public class TagLocalHQRecord implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//行情成交量返回的单位是手
	public boolean     bNewUpdated;            //代码   最新更新
	public String		code;		//代码   id=10
	public short	  market;			//市场，为本地类型  id=11
	public int       nTradeDate;           //日期YYYYMMDD  id=20
	public int       nUpdateDate;           //日期YYYYMMDD  id=21
	public int       nUpdateTime;           //时间HHMMSS  id=22
	public int		nLastClose;		//昨日收盘价*10000  id=23
	public int		nLastClear;		//昨日结算价  id=24
	public double      dLastOpenInterest; // 昨日持仓量id=25
	public int		nOpenPrice;			//今日开盘价
	public int		nHighPrice;			//最高价
	public int		nLowPrice;			//最低价
	public int 		nLastPrice;			//现价
	public int 		nLastPriceForCalc;	//现价(用于计算，如果为0，则取昨收价或昨结价)
	public int		nUpperLimit;		//涨停价格
	public int		nLowerLimit;		//跌停价格
	public int		nAveragePrice;		//均价
	public int		nClearPrice;		//id=33
    public int		nClosePrice;		//id=34
    public double		volume;			//总成交量 单位(手)
    public double		amount;			//总成交金额 单位(元)
    public double		dOpenInterest;	//持仓量
    //买卖5档
    public int []buyPrice;
    public int []buyVolume;
    public int []sellPrice;
    public int []sellVolume;
    
    public int nTradeTicks; //成交笔数 id=44
    public int  nTradeDirect; //成交方向 id=45 1 外盘、2内盘、3平盘
    public double		currentVolume;			//现手(现量) 单位(手) 有成交时与当前成交一样，没成交时为上一次的值
    public double		currentCJ;			//当前成交 单位(手)
    public double		currentCJAveragePrice;	//当前成交的均价 = 当前成交额/当前成交量 (注意：此价格没有做放大处理)
    public double      dLiquidity;         //流动性
    public double      dWPL;//外盘量
    
	public TagLocalHQRecord()
	{
		code = new String();
	    buyPrice = new int[5];
	    buyVolume = new int[5];
	    sellPrice = new int[5];
	    sellVolume = new int[5];
	}
	public void copyData(TagLocalHQRecord aHQRecord)
	{
		bNewUpdated = aHQRecord.bNewUpdated;
		code = new String(aHQRecord.code);
		market = aHQRecord.market;
		nTradeDate = aHQRecord.nTradeDate;
		nUpdateDate = aHQRecord.nUpdateDate;
		nUpdateTime = aHQRecord.nUpdateTime;
		nLastClose = aHQRecord.nLastClose;
		nLastClear = aHQRecord.nLastClear;
		dLastOpenInterest = aHQRecord.dLastOpenInterest;
		nOpenPrice = aHQRecord.nOpenPrice;
		nHighPrice = aHQRecord.nHighPrice;
		nLowPrice = aHQRecord.nLowPrice;
		nLastPrice = aHQRecord.nLastPrice;
		nLastPriceForCalc = aHQRecord.nLastPriceForCalc;
		if (nLastPriceForCalc <= 0)
		{
			if (aHQRecord.nLastPrice != 0)
	        {
	            nLastPriceForCalc = aHQRecord.nLastPrice;
	        }else
	        {
				if (aHQRecord.nLastClose != 0)
	        	{
	        		nLastPriceForCalc = aHQRecord.nLastClose;
	        	}else
	        	{
	        		nLastPriceForCalc = aHQRecord.nLastClear;
	        	}
	        }
		}
	    nUpperLimit = aHQRecord.nUpperLimit;
	    nLowerLimit = aHQRecord.nLowerLimit;
	    nAveragePrice = aHQRecord.nAveragePrice;
	    nClearPrice = aHQRecord.nClearPrice;
	    nClosePrice = aHQRecord.nClosePrice;
	    volume = aHQRecord.volume;
	    amount = aHQRecord.amount;
	    dOpenInterest = aHQRecord.dOpenInterest;
	    nTradeTicks = aHQRecord.nTradeTicks;
	    nTradeDirect = aHQRecord.nTradeDirect;
	    currentVolume = aHQRecord.currentVolume;
	    currentCJ = aHQRecord.currentCJ;
	    currentCJAveragePrice = aHQRecord.currentCJAveragePrice;
	    dLiquidity = aHQRecord.dLiquidity;
	    dWPL = aHQRecord.dWPL;
	    System.arraycopy(aHQRecord.buyPrice, 0, buyPrice, 0, 5);
	    System.arraycopy(aHQRecord.buyVolume, 0, buyVolume, 0, 5);
	    System.arraycopy(aHQRecord.sellPrice, 0, sellPrice, 0, 5);
	    System.arraycopy(aHQRecord.sellVolume, 0, sellVolume, 0, 5);
	}
	public boolean isbNewUpdated() {
		return bNewUpdated;
	}
	public void setbNewUpdated(boolean bNewUpdated) {
		this.bNewUpdated = bNewUpdated;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public short getMarket() {
		return market;
	}
	public void setMarket(short market) {
		this.market = market;
	}
	public int getnTradeDate() {
		return nTradeDate;
	}
	public void setnTradeDate(int nTradeDate) {
		this.nTradeDate = nTradeDate;
	}
	public int getnUpdateDate() {
		return nUpdateDate;
	}
	public void setnUpdateDate(int nUpdateDate) {
		this.nUpdateDate = nUpdateDate;
	}
	public int getnUpdateTime() {
		return nUpdateTime;
	}
	public void setnUpdateTime(int nUpdateTime) {
		this.nUpdateTime = nUpdateTime;
	}
	public int getnLastClose() {
		return nLastClose;
	}
	public void setnLastClose(int nLastClose) {
		this.nLastClose = nLastClose;
	}
	public int getnLastClear() {
		return nLastClear;
	}
	public void setnLastClear(int nLastClear) {
		this.nLastClear = nLastClear;
	}
	public double getdLastOpenInterest() {
		return dLastOpenInterest;
	}
	public void setdLastOpenInterest(double dLastOpenInterest) {
		this.dLastOpenInterest = dLastOpenInterest;
	}
	public int getnOpenPrice() {
		return nOpenPrice;
	}
	public void setnOpenPrice(int nOpenPrice) {
		this.nOpenPrice = nOpenPrice;
	}
	public int getnHighPrice() {
		return nHighPrice;
	}
	public void setnHighPrice(int nHighPrice) {
		this.nHighPrice = nHighPrice;
	}
	public int getnLowPrice() {
		return nLowPrice;
	}
	public void setnLowPrice(int nLowPrice) {
		this.nLowPrice = nLowPrice;
	}
	public int getnLastPrice() {
		return nLastPrice;
	}
	public void setnLastPrice(int nLastPrice) {
		this.nLastPrice = nLastPrice;
	}
	public int getnLastPriceForCalc() {
		return nLastPriceForCalc;
	}
	public void setnLastPriceForCalc(int nLastPriceForCalc) {
		this.nLastPriceForCalc = nLastPriceForCalc;
	}
	public int getnUpperLimit() {
		return nUpperLimit;
	}
	public void setnUpperLimit(int nUpperLimit) {
		this.nUpperLimit = nUpperLimit;
	}
	public int getnLowerLimit() {
		return nLowerLimit;
	}
	public void setnLowerLimit(int nLowerLimit) {
		this.nLowerLimit = nLowerLimit;
	}
	public int getnAveragePrice() {
		return nAveragePrice;
	}
	public void setnAveragePrice(int nAveragePrice) {
		this.nAveragePrice = nAveragePrice;
	}
	public int getnClearPrice() {
		return nClearPrice;
	}
	public void setnClearPrice(int nClearPrice) {
		this.nClearPrice = nClearPrice;
	}
	public int getnClosePrice() {
		return nClosePrice;
	}
	public void setnClosePrice(int nClosePrice) {
		this.nClosePrice = nClosePrice;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getdOpenInterest() {
		return dOpenInterest;
	}
	public void setdOpenInterest(double dOpenInterest) {
		this.dOpenInterest = dOpenInterest;
	}
	public int getnTradeTicks() {
		return nTradeTicks;
	}
	public void setnTradeTicks(int nTradeTicks) {
		this.nTradeTicks = nTradeTicks;
	}
	public int getnTradeDirect() {
		return nTradeDirect;
	}
	public void setnTradeDirect(int nTradeDirect) {
		this.nTradeDirect = nTradeDirect;
	}
	public double getCurrentVolume() {
		return currentVolume;
	}
	public void setCurrentVolume(double currentVolume) {
		this.currentVolume = currentVolume;
	}
	public double getCurrentCJ() {
		return currentCJ;
	}
	public void setCurrentCJ(double currentCJ) {
		this.currentCJ = currentCJ;
	}
	public double getCurrentCJAveragePrice() {
		return currentCJAveragePrice;
	}
	public void setCurrentCJAveragePrice(double currentCJAveragePrice) {
		this.currentCJAveragePrice = currentCJAveragePrice;
	}
	public double getdLiquidity() {
		return dLiquidity;
	}
	public void setdLiquidity(double dLiquidity) {
		this.dLiquidity = dLiquidity;
	}
	public double getdWPL() {
		return dWPL;
	}
	public void setdWPL(double dWPL) {
		this.dWPL = dWPL;
	}
	
	
	
	
	
	
}
