package com.pengbo.mhdcx.bean;

import java.io.Serializable;

/**
 * 筛选条件 把所有的 筛选出来的内容 看作是一个对象
 * 
 * @author pobo
 * 
 */
public class ScreenCondition implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String SCREEN_ALL_STOCK_CODE = "All";

	public String name;
	public String code;
	public short market;
	/**
	 * 长短期
	 */
	public int gridViewId;
	/**
	 * 杠杆倍数 -默认是全部，0-<10;1-10~20;2->20;3-全部
	 */
	public int leverId;
	/**
	 * 活跃度
	 */
	public int vitality;
	/**
	 * 涨跌幅度
	 */
	public float field_hq_zdf;

	public String increaseDecrease;

	public float getField_hq_zdf() {
		return field_hq_zdf;
	}

	public void setField_hq_zdf(float field_hq_zdf) {
		this.field_hq_zdf = field_hq_zdf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGridViewId() {
		return gridViewId;
	}

	public void setGridViewId(int gridViewId) {
		this.gridViewId = gridViewId;
	}

	public String getIncreaseDecrease() {
		return increaseDecrease;
	}

	public void setIncreaseDecrease(String increaseDecrease) {
		this.increaseDecrease = increaseDecrease;
	}

	public int getLeverId() {
		return leverId;
	}

	public void setLeverId(int leverId) {
		this.leverId = leverId;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public static String getScreenAllStockCode() {
		return SCREEN_ALL_STOCK_CODE;
	}

	@Override
	public String toString() {
		return "ScreenCondition [name=" + name + ", code=" + code + ", market="
				+ market + ", gridViewId=" + gridViewId + ", leverId="
				+ leverId + ", vitality=" + vitality + ", field_hq_zdf="
				+ field_hq_zdf + ", increaseDecrease=" + increaseDecrease + "]";
	}

}
