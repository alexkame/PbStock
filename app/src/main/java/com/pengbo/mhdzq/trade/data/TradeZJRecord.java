package com.pengbo.mhdzq.trade.data;

public class TradeZJRecord {
	public String mTitle;
	public int[] mStepVaules;//目前只支持2个字段备选
	
	public TradeZJRecord(){
		mTitle = "";
		mStepVaules = new int[2]; 
	}
}
