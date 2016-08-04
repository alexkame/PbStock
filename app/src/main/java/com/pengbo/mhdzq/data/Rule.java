package com.pengbo.mhdzq.data;

public class Rule {

	public String mCategory;
	public String mMarketId;
	public String mCode;
	public String mGroupCode;

	public Rule() {
		mGroupCode = "";
		mMarketId = "";
		mCode = "";
		mCategory = KeyDefine.CATEGORY_MARKET;
	}
}
