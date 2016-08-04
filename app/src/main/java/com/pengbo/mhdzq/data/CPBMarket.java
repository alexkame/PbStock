package com.pengbo.mhdzq.data;

import java.util.ArrayList;

public class CPBMarket {
	// public String MarketIds; //市场ID,可以多余1个“2000|2001|21001|21005”
	/**
	 * 市场简称
	 */
	public String Name;
	public String Id;
	public String ParentId;
	/**
	 * 位置是否固定
	 */
	public boolean IsFixed;
	public boolean IsDefault;
	/**
	 * 市场图标正常状态下
	 */
	public String NormalIcon;
	/**
	 * 市场图标点按状态
	 */
	public String PressIcon;
	public ArrayList<Rule> mRules;

	public CPBMarket() {
		// MarketIds = "";
		mRules = new ArrayList<Rule>();
		Name = "";
		Id = "";
		ParentId = "";
		IsDefault = false;
		IsFixed = false;
		NormalIcon = "";
		PressIcon = "";
	}
}
