package com.pengbo.mhdzq.data;

/**
 * 定义 CashMarket   的类  
 * @author pobo
 * Copyright (c) 2015年 pobo. All rights reserved.
 */
public class KeyDefine {
	
	public static final String CATEGORY_MARKET = "Market";
	public static final String CATEGORY_MARKET_GROUP = "MarketAndGroup";
	public static final String CATEGORY_MARKET_CODE = "MarketAndCode";
	//定义主页的市场配置对应的id key
	//600~800是一些特殊板块
	public	static final int
	
	        KEY_MARKET_SELF = 600,          //自选股
			KEY_MARKET_BK_DEBIT = 601,      //国债板块
			KEY_MARKET_BK_ZL = 602,         //主力板块
			KEY_MARKET_BHBJ = 603,          //本行报价
			KEY_MARKET_YHZL = 604,          //银行专栏
			KEY_MARKET_MNJY = 605,          //模拟交易
			KEY_MARKET_DZBK = 606,			//定制板块
			KEY_MARKET_ZQJY = 607,          //证券交易
			KEY_MARKET_QQJY = 608,          //期权交易
			KEY_MARKET_TJZQ = 609,          //投教专区，东证之衍
			KEY_MARKET_SDKH = 610,          //闪电开户
			KEY_MARKET_QQSP = 611,          //全球商品
				    
			//800之上是一些市场配置组合
			KEY_MARKET_STOCK_SHSZ = 800,    //沪深个股
			KEY_MARKET_STOCK_SH = 801,      //上证所
			KEY_MARKET_STOCK_SZ = 802,      //深证所
			KEY_MARKET_STOCK_HK = 803,      //香港证券交易所
			KEY_MARKET_GLOD_SH = 804,       //上海黄金交易所
			KEY_MARKET_FUTURE_ZJS = 805,    //中金所
			KEY_MARKET_FUTURE_SH = 806,     //上海期货交易所
			KEY_MARKET_FUTURE_DL = 807,     //大连商品交易所
			KEY_MARKET_FUTURE_ZZ = 808,     //郑州商品交易所
			KEY_MARKET_EX_MYHL = 809,       //全球外汇
			KEY_MARKET_EX_STOCK_ZS = 810,   //全球指数
			KEY_MARKET_EX_FUTURE_LD = 811,  //国际贵金属
			KEY_MARKET_FUTURE_LME = 812,    //LME
			KEY_MARKET_FUTURE_ICE = 813,    //ICE
			KEY_MARKET_FUTURE_CME = 814,    //CME
			KEY_MARKET_FUTURE_RiBen = 815,  //日本期货
			KEY_MARKET_FUTURE_MaLai = 816,  //马来期货
			KEY_MARKET_DEBIT = 817,         //国债利率
			KEY_MARKET_BOSHANGSUO = 818;    //渤商所
}