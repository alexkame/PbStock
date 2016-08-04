package com.pengbo.mhdzq.trade.data;

public class TradeLocalRecord {

	public String mMarketCode;
	public String mStockCode;
	public String mWTPrice;
	public String mWTSL;
	public String mWTSHJ;
	public char mMMLB;
	public char mKPBZ;
	public String mGDZH;
	public String mWTBH;
	public char mSJType;
	public String mXWH;
	public String mXDXW;
	public String mWTZT;
	public String mWTZTMC;
	public String mBiaodiMC;
	public String mBiaodiCode;
	public int mBDFlag;//
	
	public TradeLocalRecord() 
	{
		this.mMarketCode = "";
		this.mStockCode = "";
		this.mWTPrice = "";
		this.mWTSL = "";
		this.mMMLB = '\0';
		this.mKPBZ = '\0';
		this.mGDZH = "";
		this.mWTBH = "";
		this.mSJType = '\0';
		this.mXWH = "";
		this.mWTSHJ = "";
		this.mXDXW = "";
		this.mWTZT = "";
		this.mWTZTMC = "";
		this.mBiaodiMC = "";
		this.mBiaodiCode = "";
		this.mBDFlag = 0;
	}
	
	public TradeLocalRecord(String marketCode, String stockCode,
			String wtPrice, String wtsl, char mmlb, char kpbz, String gdzh,
			String wtbh, char sjType, String xwh, String wtshj, String xdxw, String wtzt
			, String wtztmc, String biaodiMC, String biaodiCode, int bdFlag) {
		this.mMarketCode = marketCode;
		this.mStockCode = stockCode;
		this.mWTPrice = wtPrice;
		this.mWTSL = wtsl;
		this.mMMLB = mmlb;
		this.mKPBZ = kpbz;
		this.mGDZH = gdzh;
		this.mWTBH = wtbh;
		this.mSJType = sjType;
		this.mXWH = xwh;
		this.mWTSHJ = wtshj;
		this.mXDXW = xdxw;
		this.mWTZT = wtzt;
		this.mWTZTMC = wtztmc;
		this.mBiaodiMC = biaodiMC;
		this.mBiaodiCode = biaodiCode;
		this.mBDFlag = bdFlag;
	}
	
	public void clear()
	{
		this.mMarketCode = "";
		this.mStockCode = "";
		this.mWTPrice = "";
		this.mWTSL = "";
		this.mMMLB = '\0';
		this.mKPBZ = '\0';
		this.mGDZH = "";
		this.mWTBH = "";
		this.mSJType = '\0';
		this.mXWH = "";
		this.mWTSHJ = "";
		this.mXDXW = "";
		this.mWTZT = "";
		this.mWTZTMC = "";
		this.mBiaodiMC = "";
		this.mBiaodiCode = "";
		this.mBDFlag = 0;
	}
	
	public void copy(TradeLocalRecord record) 
	{
		this.mMarketCode = record.mMarketCode;
		this.mStockCode = record.mStockCode;
		this.mWTPrice = record.mWTPrice;
		this.mWTSL = record.mWTSL;
		this.mMMLB = record.mMMLB;
		this.mKPBZ = record.mKPBZ;
		this.mGDZH = record.mGDZH;
		this.mWTBH = record.mWTBH;
		this.mSJType = record.mSJType;
		this.mXWH = record.mXWH;
		this.mWTSHJ = record.mWTSHJ;
		this.mXDXW = record.mXDXW;
		this.mWTZT = record.mWTZT;
		this.mWTZTMC = record.mWTZTMC;
		this.mBiaodiMC = record.mBiaodiMC;
		this.mBiaodiCode = record.mBiaodiCode;
		this.mBDFlag = record.mBDFlag;
	}
}
