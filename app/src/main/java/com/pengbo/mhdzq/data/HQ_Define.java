package com.pengbo.mhdzq.data;

public class HQ_Define {

	public	static final String	    HQ_MARKET_NULL = 	"";			//证券交易所未知
	public	static final String		HQ_MARKET_SH	=	"SHSE";		//上海证券
	public	static final String		HQ_MARKET_SZ	=	"SZSE";		//深圳证券
	public	static final String		HQ_MARKET_SHQQ	=	"SHSEFZ";	//上海个股期权
	public	static final String		HQ_MARKET_SZQQ	=	"SZSEFZ";	//深圳个股期权
	public  static final String     HQ_MARKET_BHSP_TJ = "BOCE";//天津渤海商品 渤海商品

	public	static final int HQ_MARKET_SH_INT	=	1000;		//上海证券
	public	static final int HQ_MARKET_SZ_INT	=	1001;		//深圳证券
	public	static final int MARKET_SHQQ = 1020; //上海个股期权
	public	static final int MARKET_SZQQ = 1021; //深圳个股期权
	public	static final int MARKET_SHQQFZ = 1090; //上海个股期权仿真
	public	static final int MARKET_SZQQFZ = 1091; //深圳个股期权仿真
	
	public  static final int MARKETID_BHSP_TJ = 31004;//天津渤海商品 渤海商品
	
	public  static final int MARKETID_CZCE0 = 21003;//郑州商品交易所
	public  static final int MARKETID_CZCE1 = 2200;//郑州商品交易所
	public  static final int MARKETID_CZCE2 = 21007;//郑州商品交易所
	public  static final int MARKETID_CZCE3 = 2201;//郑州商品交易所
	
	public  static final int MARKETID_CFFEX0 = 21004;//中国金融期货交易所2300
	public  static final int MARKETID_CFFEX1 = 2300;//中国金融期货交易所2300
	
	public  static final int MARKETID_SHFE = 21005;//上海期货交易所
	public  static final int MARKETID_DCE = 21006;//大连商品交易所
	public  static final int MARKETID_GT_E = 3000;
	public  static final int MARKETID_GT_S = 12000;
	
	//分类属性
	public  static final int GRPATTR_INDEX = 10,   	//指数
							 GRPATTR_STOCK = 20,//股票
							 GRPATTR_ETFFUND = 30,//ETF基金
							 GRPATTR_CLSFUND = 31,//封闭基金
							 GRPATTR_OPNFUND = 32,//开放基金
							 GRPATTR_LOFTFUND = 33,//LOFT基金
							 GRPATTR_BLKIDX = 40,//板块指数
							 GRPATTR_WARRNT = 50,//权证
							 GRPATTR_GDSFUT = 100,//国内商品期货
							 GRPATTR_IDXFUT = 101,//国内指数期货
							 GRPATTR_DEBTFUT = 102,//国内国债期货
							 GRPATTR_FUTOPTN = 110,//国内期货期权
							 GRPATTR_STKOPTN = 111,//股票期权，有持仓和结算价，所以放在期货大分类
							 GRPATTR_IDXOPTN = 112,//股指期权
							 GRPATTR_GDSFUTIDX = 120,//国内商品期货指数
							 GRPATTR_EXFUT1 = 130, //国外商品1      以收盘价做涨跌
							 GRPATTR_EXFUT2 = 131,//国外商品2     以昨结计算涨跌
							 GRPATTR_STKFUT = 140,//股票期货
							 GRPATTR_EXCH = 200,//外汇
							 GRPATTR_METALSPOT = 201,//贵金属现货
							 GRPATTR_BOND = 202,//债券
							 GRPATTR_CVBOND = 203,//转债
							 GRPATTR_SPOT = 204,//现货
							 GRPATTR_RATE = 205;//利率
}
