package com.pengbo.mhdzq.data;

import com.pengbo.mhdzq.trade.data.TradeData;

public class CPbGDZHData {

	public String mMarket;
	public String mGdzh;
	public int mHqMarket;
	
	public CPbGDZHData(String market,String gdzh)
	{
		mMarket = market;
		mGdzh = gdzh;
		mHqMarket = TradeData.GetHQMarketFromTradeMarket(market);
	}
}
