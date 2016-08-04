package com.pengbo.mhdzq.data;

import java.io.Serializable;
import java.util.ArrayList;

public class CMarketInfoItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CMarketInfoItem() {
		Name = new String();
		Code = new String();
		MarketId = 0;
		mGroupList = new ArrayList<mktGroupInfo>();
	}

	public void addGroup(mktGroupInfo aGroup) {
		mGroupList.add(aGroup);
	}

	public short MarketId; // 交易所ID
	public byte MarketAttr; // 交易所属性
	public String Name; // 交易所简称
	public String Code; // 交易所代码
	public short TimeZone; // 时区
	public short MaxNumber; // 最大允许商品数
	public short GroupNumber;// 分类数
	public short StartTime; // 开盘时间
	public short EndTime; // 收盘时间
	public ArrayList<mktGroupInfo> mGroupList;

	public static class mktGroupInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public byte Flag; // 指数或一般品种
		public String Name; // 类别名称
		public String Code;
		public byte TradeFields; // 交易区段
		public short[] Start; // 区段起始 hhss
		public short[] End; // 区段结束 hhss
		public byte FlagAskBid; // 买卖盘的档数 0x01 1档，0x03 3档, 0x05 5档 ,0x10 档

		public mktGroupInfo() {
			// Name = new String();
			TradeFields = 0;
			Start = new short[4];
			End = new short[4];
		}
	}
}
