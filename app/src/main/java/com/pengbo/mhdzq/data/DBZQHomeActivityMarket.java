package com.pengbo.mhdzq.data;

import java.util.ArrayList;

public class DBZQHomeActivityMarket {
	/**
	 * 市场简称
	 */
	public String Name;
	public String Id;
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
	public String url;

	public DBZQHomeActivityMarket() {
		Name = "";
		Id = "";
		IsDefault = false;
		IsFixed = false;
		NormalIcon = "";
		PressIcon = "";
		url = "";
	}

	@Override
	public String toString() {
		return "DBZQHomeActivityMarket [Name=" + Name + ", url=" + url + "]";
	}

}
