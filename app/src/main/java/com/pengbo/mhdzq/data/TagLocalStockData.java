package com.pengbo.mhdzq.data;

import java.io.Serializable;

/**
 * 
 * @author pobo
 * 
 */
public class TagLocalStockData implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 市场，为本地类型
	 */
	public short market;
	/**
	 * 代码
	 */
	public String code;
	/**
	 * 类别，为本地类型
	 */
	public short group;
	/**
	 * 名称
	 */
	public String name;
	/**
	 * 从服务器直接获取的HQ数据
	 */
	public TagLocalHQRecord HQData;
	/**
	 * 期权数据
	 */
	public TagLocalOptionRecord optionData;

	/***************** 对应LOCAL_CODETABLE_RECORD数据 ******************/
	/**
	 * 分类号
	 */
	public short GroupOffset;
	/**
	 * 显示价格小数位
	 * 
	 */
	public short PriceDecimal;
	/**
	 * 价格放大倍率
	 */
	public int PriceRate;
	/**
	 * 每手股数
	 */
	public short VolUnit;
	/**
	 * 合约乘数，计划均价是用到
	 */
	public int Multiplier;

	/************************ 对应ST_MKT_GROUP_INFO_UNICODE数据 *****************/
	public String groupCode;
	/**
	 * 交易所代码
	 */
	public String extCode;
	/**
	 * 交易区段数量
	 */
	public byte TradeFields;
	/**
	 * 区段起始 hhss
	 */
	public short[] Start;
	/**
	 * 区段结束 hhss
	 */
	public short[] End;
	/**
	 * 指数或一般品种
	 */
	public short GroupFlag;

	/******************** 本地计算或保存的数据 ******************/
	/**
	 * 时间，用于保存
	 */
	public long tickcount;
	/**
	 * 渤商所最新含税计算用到的税率
	 */
	public float fShuiLv;

	public TagLocalStockData() {
		code = new String();
		name = new String();
		extCode = new String();
		groupCode = new String();

		Start = new short[4];
		End = new short[4];

		HQData = new TagLocalHQRecord();
		optionData = new TagLocalOptionRecord();
		fShuiLv = 0.0f;
	}

	public void copyData(TagLocalStockData aStockData) {
		market = aStockData.market;
		code = new String(aStockData.code);
		group = aStockData.group;
		name = new String(aStockData.name);

		HQData.copyData(aStockData.HQData);
		optionData.copyData(aStockData.optionData);

		GroupOffset = aStockData.GroupOffset;
		PriceDecimal = aStockData.PriceDecimal;
		PriceRate = aStockData.PriceRate;
		VolUnit = aStockData.VolUnit;
		Multiplier = aStockData.Multiplier;
		extCode = new String(aStockData.extCode);
		groupCode = new String(aStockData.groupCode);
		tickcount = aStockData.tickcount;
		TradeFields = aStockData.TradeFields;
		GroupFlag = aStockData.GroupFlag;
		for (int i = 0; i < 4; i++) {
			Start[i] = aStockData.Start[i];
			End[i] = aStockData.End[i];
		}
		fShuiLv = aStockData.fShuiLv;
	}

}
