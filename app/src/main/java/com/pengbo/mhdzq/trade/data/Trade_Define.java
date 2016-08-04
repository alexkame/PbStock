package com.pengbo.mhdzq.trade.data;

public class Trade_Define {

	public	static final int  GDDM_LEN	= 10;	//股东代码长度
	
	public	static final String	    ENum_MARKET_NULL 	=	"";				//证券交易所未知
	public	static final String		ENum_MARKET_SHA		=	"SHSE-A";		//上海A
	public	static final String		ENum_MARKET_SZA		=	"SZSE-A";		//深圳
	public	static final String		ENum_MARKET_SHB		=	"SHSE-B";		//上海B
	public	static final String		ENum_MARKET_SZB		=	"SZSE-B";		//深圳B
	public	static final String		ENum_MARKET_SHQQA	=	"SHQQ-A";		//上海A个股期权
	public	static final String		ENum_MARKET_SZQQA	=	"SZQQ-A";		//深圳A个股期权
	public	static final String		ENum_MARKET_BOCE	=	"BOCE";		//渤海商品
	
	public	static final String		ENum_MARKET_GTG	=	"G";		//黄金交易
	public	static final String		ENum_MARKET_SHFE	=	"SHFE";		//上海期货交易所
	public	static final String		ENum_MARKET_CZCE	=	"CZCE";		//郑州商品交易所
	public	static final String		ENum_MARKET_DCE	=	"DCE";		//大连商品交易所
	public	static final String		ENum_MARKET_CFFEX	=	"CFFEX";		//中国金融期货交易所
	public  static final String     ENum_MARKET_GT_S     = "GT-S"; //12000黄金现货市场
	public  static final String     ENum_MARKET_GT_E    = "GT-E"; //3000对应延期市场
	
	
	//币种
	public	static final String	    MType_NULL 	= "x";				//未知
	public	static final String	    MType_RMB 	= "0";        		//人民币
	public	static final String	    MType_USD 	= "1";				//美元
	public	static final String	    MType_HKD 	= "2";				//港币
	public	static final String	    MType_EUR 	= "3";				//欧元
	public	static final String	    MType_AUD 	= "4";				//澳元
	public	static final String	    MType_JPY 	= "5";				//日元
	public	static final String	    MType_TWY 	= "6";				//台湾币
	
	//委托价格类别
	public static final int WTPRICEMODE_INPUT = -1; //键盘输入
	public static final int WTPRICEMODE_DSJ = 0; //对手价
	public static final int WTPRICEMODE_NOW = 1; //最新价
	public static final int WTPRICEMODE_GDJ = 2; //挂单价
	public static final int WTPRICEMODE_UP = 3; //涨停价
	public static final int WTPRICEMODE_DOWN = 4; //跌停价
	public static final int WTPRICEMODE_SJSYZXJ = 5; //市价剩余转限价
	public static final int WTPRICEMODE_SJSYCX = 6; //市价剩余撤销
	public static final int WTPRICEMODE_QBCJHCX = 7; //全额成交或撤销
	
	public static final int WTPRICEMODE_DSJCY = 8; // 对手价超一
	public static final int WTPRICEMODE_SJ = 9; // 市价
    
	public static final int WTPIRCEMODE_DSFZY_SZ = 15; //对手方最优
	public static final int WTPIRCEMODE_BFZY_SZ = 16; //本方最优
	public static final int WTPIRCEMODE_JSCJSYCX_SZ = 17; //即时成交剩余撤销
	public static final int WTPIRCEMODE_ZYWDJSCJ_SZ = 18; //最优五档即时成交
	public static final int WTPIRCEMODE_QBCJHCX_SZ = 19; //全额成交或撤销
	
	public static final int WTPIRCEMODE_ZYWDJSCJSYCX = 20;//最优五档即时成交剩余撤销
	public static final int WTPIRCEMODE_ZYWDJSCJSYXJ = 21;//最优五档即时成交剩余转限价
	public static final int WTPIRCEMODE_JSCJSYCXWT = 22;//即时成交剩余撤销委托
	public static final int WTPIRCEMODE_QECJHCXWT = 23;//全额成交或撤消委托
	public static final int WTPIRCEMODE_DSZYJWT =24;//对手方最优价格委托
	public static final int WTPIRCEMODE_BFZYJWT = 25;//本方最优价格委托
	public static final int WTPIRCEMODE_SJRYJ = 26;//市价 任意价


	
	//交易模式
	public static final int TRADE_MODE_PUTONG = 0; //普通模式
	public static final int TRADE_MODE_GAOJI = 1; //高级模式
	
	public static final String TRADE_QS_ZHTYPE_KEHU = "a";
	public static final String TRADE_QS_ZHTYPE_ZJ = "0";
	
	public static final String TRADE_HYLB_GOU = "C";
	public static final String TRADE_HYLB_GU = "P";
	
	//交易 功能号
	public static final int
		Func_HeartBeat		= 0,	//心跳包
		Func_Connect		= 1,	//连接
		Func_Login			= 6011,	//交易登录
		Func_Money			= 6012,	//资金查询
		Func_DRCJ			= 6013,	//当日成交
		Func_HOLDSTOCK		= 6014,	//持仓查询
		Func_HYLB			= 6018,	//查询合约列表
		Func_DRWT			= 6019,	//当日委托查询
		
		Func_WT				= 6021,	//委托下单
		Func_WTCD			= 6022,	//委托撤单
		Func_XGMM			= 6023,	//修改密码
		Func_GDZH			= 6040,	//查询股东账号
		Func_KMSL			= 6044,	//查询可买卖数量
		
		Func_LSWT           = 6052, //查询历史委托
		Func_LSCJ           = 6053, //查询历史成交
		
		Func_QRJZD          = 6071, //确认结账单
		
		Func_LSZJLS         = 6093,//历史资金流水
		
		Func_XQ				= 6100, //行权执行与放弃
		Func_XQCD			= 6101, //行权撤销
		Func_KXQSL			= 6102, //查询可行权数量
		Func_FJYWTCX		= 6103, //非交易委托单查询
		Func_BDKDJJDSL      = 6104, //备兑可冻结解冻数量
		Func_BDDJJD         = 6105, //备兑冻结解冻
		Func_BDZQCCCX       = 6106, //备兑证券持仓查询
		Func_XQZP			= 6107, //行权指派查询
		Func_LSXQZP			= 6108, //历史行权指派查询
		
		//银证转账
		Func_YHZH			= 6200,	//查询银行账号
		Func_ZQZYH			= 6201,	//证券转银行
		Func_YHZZQ			= 6202,	//银行转证券
		Func_YHYE			= 6203,	//查询银行余额
		Func_ZZLS			= 6205,	//查询银证转账流水
		
		Func_ZCSJHM         = 6403, //注册手机号码
		Func_YZSJZCM        = 6404, //验证手机注册码
		
		Func_Push_DRCJ		= 56013,//当日成交推送
		Func_Push_HOLDSTOCK	= 56014,//持仓推送
		Func_Push_DRWT		= 56019,//当日委托推送
		Func_Push_HBBZ		= 56002,//推送委托回报，成交回报，撤单回报
		Func_CJHB			= 56006,			
		Func_MAX			= 10000;
}
