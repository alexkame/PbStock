package com.pengbo.mhdzq.trade.data;

/** 券商信息 */
public class TradeAccountInfo {
	/** 券商name **/
	public String mName;
	/** 券商id **/
	public String mID;
	/** 账号类别-客户号或资金号 **/
	public String mAccoutType;
	/** 账号 **/
	public String mAccout;

	public TradeAccountInfo() {
		mName = "";
		mID = "";
		mAccoutType = "";
		mAccout = "";
	}
}
