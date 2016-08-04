package com.pengbo.mhdcx.bean;
/***
 * 盈亏分析 界面中  把下面的 listview 条目中的四个 字段 看作是  GainLossList 的属性  
 * @author pobo
 *
 */
public class GainLossList {
	public String mPrice;//价格 
	public String mIncome;//收益 
	public String mYield;//收益率 
	public String mProbability;//发生概率 
	public String getmPrice() {
		return mPrice;
	}
	public void setmPrice(String mPrice) {
		this.mPrice = mPrice;
	}
	public String getmIncome() {
		return mIncome;
	}
	public void setmIncome(String mIncome) {
		this.mIncome = mIncome;
	}
	public String getmYield() {
		return mYield;
	}
	public void setmYield(String mYield) {
		this.mYield = mYield;
	}
	public String getmProbability() {
		return mProbability;
	}
	public void setmProbability(String mProbability) {
		this.mProbability = mProbability;
	}
	
	
}
