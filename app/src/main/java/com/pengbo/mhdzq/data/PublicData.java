package com.pengbo.mhdzq.data;

import java.io.Serializable;

/**
 * PublicData 类名 提供 公用 数据 的 类
 * 
 * @author pobo
 * 
 */
public class PublicData {

	/**
	 * Option 期权实体 类 期权 基本信息 name 名称 code 代码 market 市场 期权中 现用的暂叫 代码 是 8位 实际是 合约
	 * 编码 是 18位的 简用的 8 位
	 * 
	 * @author pobo
	 * 
	 */
	public static class Stock {
		/** 期权 名称 */
		public String name;
		/** 期权代码 */
		public String code;
		/** 期权 市场 代码 */
		public short market;
		/** 类别 分组 */
		public short group;
		/** 交易市场代码 */
		public String tradeMarket;
		/** 可锁定数量 */
		public String ksdsl;
		/** 可解锁数量 */
		public String kjssl;

		public Stock() {
			code = new String();
			name = new String();
			tradeMarket = new String();
			ksdsl = new String();
			kjssl = new String();

		}
	}

	public static class StockInfo {
		public String code;
		public short market;

		public StockInfo() {
			code = new String();
		}
	}

	public static class TopRankField {
		public String name;
		public short id;
		public int count;
		public boolean isExpand = false;

		public TopRankField() {
			name = "";
		}
	}

	public static class TagCodeInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean equals(Object o) {
			TagCodeInfo tempObject = (TagCodeInfo) o;
			if (this.code.equalsIgnoreCase(tempObject.code)
					&& this.market == tempObject.market
					&& this.group == tempObject.group) {
				return true;
			}

			return false;
		}

		public TagCodeInfo() {
			code = new String();
			name = new String();
		}

		public TagCodeInfo(short aMarket, String aCode) {
			market = aMarket;
			code = new String(aCode);
		}

		public TagCodeInfo(short aMarket, String aCode, short aGroup) {
			market = aMarket;
			code = new String(aCode);
			group = aGroup;
		}

		public TagCodeInfo(short aMarket, String aCode, short aGroup,
				String aName) {
			market = aMarket;
			code = new String(aCode);
			if (aName != null) {
				name = new String(aName);
			}
			group = aGroup;
		}

		/** 市场，为本地类型 */
		public short market;
		/** 代码 */
		public String code;
		/** 代码 */
		public String name;
		/** 类别，为本地类型 */
		public short group;
	}
}
