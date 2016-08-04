package com.pengbo.mhdzq.trade.data;

import java.util.ArrayList;

public class TradeQSInfo {

	public String mName;
	public String mID;
	public ArrayList<String> mAccoutType;
	public ArrayList<String> mIPAdd;
	
	public TradeQSInfo() 
	{
		mName = "";
		mID = "";
		mAccoutType = new ArrayList<String> ();
		mIPAdd = new ArrayList<String> ();
	}
}
