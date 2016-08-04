package com.pengbo.mhdzq.data;

/**
 * 定义 全局变量   的类  
 * @author pobo
 *
 */
public class Global_Define {
	public static final  String TAG=Global_Define.class.getSimpleName();// 拿到简写类名 
	
	public	static final int  HQ_CODE_LEN	= 9;    //代码长度  
	public	static final int  HQ_NAME_LEN	= 21;	//指8个字符，16个字节
	public	static final int  HQ_TRADE_TIME_NUM	= 8;
	
	public	static final int VOLUME_STRING_LENGHT = 6;//量显示的长度
	
	public static final  String STRING_VALUE_EMPTY = "--";// 空值

	//K线周期
	public static final int 	
		HISTORY_TYPE_DAY	=	0x01,
		HISTORY_TYPE_WEEK	=	0x02,
		HISTORY_TYPE_MONTH	=	0x03,
		HISTORY_TYPE_5MIN	=	0x04,
		HISTORY_TYPE_60MIN	=	0x05,
		HISTORY_TYPE_15MIN	=	0x06,
		HISTORY_TYPE_1MIN	=	0x07,
		HISTORY_TYPE_3MIN	=	0x08,
		HISTORY_TYPE_30MIN	=	0x09,
		HISTORY_TYPE_240MIN	=	0x0A,
		HISTORY_TYPE_120MIN	=	0x0B;
		
	//这里定义所有请求统一的固定的字段
	public	static final int  
		HFD_All_Market		= 50;		//市场
	
	/*********************行情字段定义********************************************/
	public	static final int
	//基本
	FIELD_HQ_YESTERDAY		= 1,		//昨收价
	FIELD_HQ_OPEN			= 2,		//开盘价
	FIELD_HQ_HIGH			= 3,		//最高价
	FIELD_HQ_LOW			= 4,		//最低价
	FIELD_HQ_NOW			= 5,		//最新价
	FIELD_HQ_VOLUME			= 6,		//总量
	FIELD_HQ_AMOUNT			= 7,		//总额
	FIELD_HQ_CURVOL			= 8,		//现量
	FIELD_HQ_MARKET			= 9,		//市场
	FIELD_HQ_CODE			= 10,		//代码
	FIELD_HQ_STOCKTYPE		= 11,		//类别
	FIELD_HQ_NAME			= 12,		//名称
	FIELD_HQ_FLAG			= 13,		//雷达标志，第0位
	FIELD_HQ_LB				= 14,		//量比
	FIELD_HQ_HSL			= 15,		//换手率
	FIELD_HQ_SYL			= 16,		//市盈率
	FIELD_HQ_ZD				= 17,		//涨跌，放大PriceRate倍，由服务器算好，如果客户端没有要求这个字段，则应该自己计算，在早上竞价阶段，价格涨跌根据买一卖一价来计算
	FIELD_HQ_AVERAGE		= 18,		//均价，放大PriceRate倍，客户端直接使用这个均价，而不需自己计算
	FIELD_HQ_UNITANDDOT		= 19,		//每股手数和价格位数
	FIELD_HQ_TRADETIMENUM	= 20,		//交易段数及时间
	FIELD_HQ_HQTIME			= 21,		//行情时间
	FIELD_HQ_NAME_ANSI		= 22,		//股票名称
	FIELD_HQ_ZDF			= 23,       //涨跌幅
    FIELD_HQ_ZDF_SIGN		= 24,       //带+-符号的涨跌幅
    
	FIELD_HQ_ZHENFU			= 25,		//振幅	放大PriceRate倍
	FIELD_HQ_SJL			= 26,		//市净率	放大PriceRate倍
    
	FIELD_HQ_5ZF			= 27,		//五分钟涨幅 放大PriceRate倍
	
	FIELD_HQ_MMPFLAG		= 28,		//内外盘标志 第0、1位表示内外盘标志，0 外盘、1内盘、2平盘，注意：其他无效
	FIELD_HQ_CJBS			= 29,		//成交笔数
	
	FIELD_HQ_HQTIME_HAVE_SECOND	= 30,	//行情时间，带秒 低4字节：YYYYMMDD 高4字段：HHMMSS
    
	//指数
	FIELD_HQ_INDEX_WB		= 40,		//委比
	FIELD_HQ_INDEX_WC		= 41,		//委差
	FIELD_HQ_INDEX_UP		= 42,		//上涨家数
	FIELD_HQ_INDEX_SAME		= 43,		//平盘家数
	FIELD_HQ_INDEX_DOWN		= 44,		//下跌家数
	
	FIELD_HQ_INDEX_RATE_UP	= 45,		//上涨家数比 上涨家数/总家数 放大PriceRate倍
	FIELD_HQ_INDEX_QRD		= 46,		//强弱度 放大1000倍
	FIELD_HQ_INDEX_HLZ		= 47,		//大盘红绿柱
    
	//个股
	FIELD_HQ_BVOLUME1		= 60,		//买一量	单位股
	FIELD_HQ_SVOLUME1		= 61,		//卖一量	单位股
	FIELD_HQ_INVOLUME		= 62,		//内盘总量	单位股
	
	FIELD_HQ_UPPRICE		= 70,		//涨跌停
	FIELD_HQ_DOWNPRICE		= 71,		//涨跌停
	FIELD_HQ_BUYPRICE		= 72,		//委买价
	FIELD_HQ_SELLPRICE		= 73,		//委卖价
	FIELD_HQ_BPRICE1		= FIELD_HQ_BUYPRICE,	//委买价
	FIELD_HQ_SPRICE1		= FIELD_HQ_SELLPRICE,	//委卖价
	FIELD_HQ_BS5			= 74,		//委买盘 BuySell5
	FIELD_HQ_OUTVOLUME		= 75,		//外盘总量
		
	//财务
	FIELD_HQ_LTGB			= 80,		//流通A股
	FIELD_HQ_YCMGSY			= 81,		//预测每股收益，放大PriceRate倍
	FIELD_HQ_AVGVOL5		= 82,		//五日均量
	FIELD_HQ_ZGB			= 83,		//总股本
	
	FIELD_HQ_LTSZ			= 84,		//流通市值
	FIELD_HQ_ZSZ			= 85,		//总市值
	
	FIELD_HQ_LTBG			= 86,		//INT64	流通B股	单位股
	FIELD_HQ_MGZZC			= 87,		//INT32	每股净资产	放大PriceRate倍
	FIELD_HQ_ZZCSYL			= 88,		//INT32	净资产收益率	放大PriceRate倍:每股收益/每股净资产*100%
	FIELD_HQ_ZCFZL			= 89,		//INT32	资产负债率	放大PriceRate倍
	FIELD_HQ_ZYSR			= 90,		//INT64	主营收入	单位分，即放大100倍
	FIELD_HQ_ZLR			= 91,		//INT64	净利润	单位分，即放大100倍
	FIELD_HQ_ZZC			= 92,		//INT64	总资产	单位分，即放大100倍
	FIELD_HQ_ZXMGSY			= 93,		//INT32	最新每股收益	放大PriceRate倍
	
	FIELD_HQ_JRCCS					= 160,	//今日持仓+今日结算价+昨日持仓量+持仓性质
	FIELD_HQ_KPC					= 161,	//开仓+平仓
	FIELD_HQ_XKP					= 162,	//现开+平开
	FIELD_HQ_ZRJSJ					= 163,	//昨日结算价
    
	FIELD_HQ_MAINCC			= 200,	//用于技术指标中的主力持仓指标
    
    //期权
	FIELD_HQ_GGL			= 300,		//杠杆率
	FIELD_HQ_ZSGGL			= 301,		//真实杠杆率
    FIELD_HQ_NZJZ			= 302,		//内在价值
	FIELD_HQ_SJJZ			= 303,		//时间价值
    FIELD_HQ_YJL			= 304,		//溢价率
    FIELD_HQ_CCL			= 305,		//持仓量
    FIELD_HQ_CC             = 306,      //仓差
    FIELD_HQ_XQJ            = 307,      //行权价
    FIELD_HQ_EXPIRE_DATE    = 308,      //到期日
    FIELD_HQ_SXZ            = 309,      //实虚值
    FIELD_HQ_HYD            = 310,      //活跃度
    FIELD_HQ_DZCB           = 311,      //单张成本
    FIELD_HQ_YLGL           = 312,      //盈利概率
    FIELD_HQ_GSSYB          = 313,      //估算损益比
    FIELD_HQ_Delta          = 314,      //Delta
    FIELD_HQ_Gamma          = 315,      //Gamma
    FIELD_HQ_Theta          = 316,      //Theta
    FIELD_HQ_Rho            = 317,      //Rho
    FIELD_HQ_Vega           = 318,      //Vega
    FIELD_HQ_TheoryPrice    = 319,      //理论价格
    FIELD_HQ_YHBDL          = 320,      //隐含波动率
	FIELD_HQ_NOWSHUI			= 501,		//最新含税
	FIELD_HQ_AVERAGESHUI			= 502;		//平均含税
    /*******End********行情字段定义********************************************/
	
	
	//以下为服务器定义的MAIN_FRAME_TYPE
	public	static final int
		MFT_APPLY_CANCEL = 0,			/* 取消以前的所有请求 */
		MFT_KEEP_ALIVE = 1,

		MFT_MOBILE_LOGIN_APPLY = 180,	//手机端登录，双连接
		MFT_MOBILE_MARKET_INFO_APPLY = 181,	//手机端市场信息请求
		MFT_MOBILE_SYSCONFIG_APPLY = 182,		//手机端系统配置下载
		MFT_MOBILE_DATA_APPLY = 183,			//手机端数据请求，请求命令使用文本格式
		MFT_MOBILE_PUSH_DATA = 184,			//手机端行情订阅和推送
		MFT_MOBILE_SPECDATA_APPLY = 185,		//手机端请求全市场（分类）的某项数据
	
		MFT_ERROR_DESCRIBE = 202,				/* Server 端返回的错误描述 =2###*/
		MFT_MOBILE_ERROR_DESCRIBE = 203,		//手机端返回错误描述
		MFT_MOBILE_APP_UPGRADE = 204;

	// 股票市场
	public	static final int  
		MARKET_NONE		= 0,		//未知
		MARKET_SH		= 1,		//上海
		MARKET_SZ		= 2,		//深圳
		MARKET_HK		= 3;		//香港
	
	//MFT_MOBILE_LOGIN_APPLY = 180,	用户登录请求字段定义
	public static final int 	
		HFD_Login_LoginType		= 1,		//登录类型 unsigned char   0-查询连接，1-推送连接
	    HFD_Login_Account		= 2,		//用户名  string          长度不超过16
	    HFD_Login_Password		= 3,		//密码    string          32位md5码加密
	    HFD_Login_AppType		= 4,		//客户端类型 unsigned char
	    HFD_Login_UserType		= 5,		//用户类型  unsigned char   由系统统一制定
	    HFD_Login_PushId		= 6,		//推送登录识别号 unsigned int 查询连接登陆时返回的识别号
	    HFD_Login_MacAddress    = 7,        //绑定网卡地址 string
	    HFD_Login_HardWare		= 8,		//其他硬件绑定 string 预留，待定
	    HFD_Login_SoftVersion	= 9,        //软件版本 short
	    HFD_Login_ProtocolVer	= 10,       //协议版本 short
		HFD_Login_OrganizationId = 11;      //机构Id
	
	//市场配置查询
	public static final int 	
		HFD_MarketQuery_Right       = 1,	//权限分类号 int 0x01
	    HFD_MarketQuery_ModifyDate  = 2,	//市场文件修改日期 int yyyymmdd
	    HFD_MarketQuery_ModifyTime	= 3,	//市场文件修改时间 int  hhmmss
	    HFD_MarketQuery_ProtocolVer = 4;
	
	//配置文件更新
	public static final int 	
	    HFD_UpdateConfig_FileName   = 1,	//文件名 string
		HFD_UpdateConfig_FileSize   = 2,	//文件大小 int
	    HFD_UpdateConfig_Date       = 3,    //文件日期 int yyyymmdd
	    HFD_UpdateConfig_time       = 4,    //文件时间 int hhmmss
	    HFD_UpdateConfig_StartSize	= 5,	//文件起始位置 int  客户端已有新文件长度，用于断点续传
	    HFD_UpdateConfig_ContentSize	= 6,	//发送内容长度,本次请求返回的文件内容最大长度，用于断点续传
	    HFD_UpdateConfig_OrganizationId = 7;      //机构Id申请公有时用-1(，申请私有时用客户所属机构ID，缺省值为-1

	
	//码表查询请求字段定义
	public static final int 	
	    HFD_CodeTable_RequestType   = 1,	//请求类型 与应答模板号一致，值=1
	    HFD_CodeTable_LocalDate     = 2,    //客户端已有码表日期 int yyyymmdd
	    HFD_CodeTable_LocalTime     = 3,	//客户端已有码表时间 int  hhmmss
	    HFD_CodeTable_MarketId      = 4,	//请求市场编号 short
	    HFD_CodeTable_StartNO       = 5,	//请求起始序号 short
	    HFD_CodeTable_ReqCount      = 6,	//请求品种个数 short 填0表示取市场所有代码
	    HFD_CodeTable_GroupCount	= 7,	//市场品种个数 short 用户本地无码表时可填0
		HFD_CodeTable_TableCRC      = 8,	//码表校验和
		HFD_CodeTable_UpdateFlag	= 9;	//码表更新策略，0-全量，缺省1-增量

	
	//推送订阅请求字段定义
	public static final int 	
		HFD_Push_ReqCount	= 1,		//订阅品种数 short   重复出现2、3、4项
	    HFD_Push_MarketId	= 2,		//品种市场编号 short 若未指定代码和分类，订阅全市场；若未指定代码，订阅指定分类
	    HFD_Push_Code       = 3,		//品种代码   string 订阅指定代码商品
	    HFD_Push_GroupId    = 4;		//品种分类号 unsigned char 订阅指定分类商品

	
	//快照查询 1=10
	public static final int 	
	    HFD_APPLY_10_RequestType = 1,		//请求类型 unsigned short   快照查询 1=10
		HFD_APPLY_10_ReqCount	 = 2,		//订阅品种数 short   重复出现2、3、4项
	    HFD_APPLY_10_MarketId	 = 3,		//品种市场编号 short 若未指定代码和分类，订阅全市场；若未指定代码，订阅指定分类
	    HFD_APPLY_10_Code        = 4,		//品种代码   string 订阅指定代码商品
	    HFD_APPLY_10_GroupId     = 5;		//品种分类号 unsigned char 订阅指定分类商品

	
	//走势查询请求字段定义
	public static final int 	
		HFD_Trend_RequestType	= 1,		//请求类型 unsigned short   走势、历史回忆查询，对应模板id=11
	    HFD_Trend_MarketId		= 2,		//市场编号 short
	    HFD_Trend_Code          = 3,		//品种代码   string
	    HFD_Trend_Date          = 4,		//查询日期 int yyyymmdd，0为查询当日
	    //HFD_Trend_DateMoved     = 5,		//日期偏移  short 查询日期之前第几天的走势
	    HFD_Trend_DateType      = 5,         //查询类型  unsigned char 0表示查询日期当天的数据，1表示查询日期之前最近有数据的一天数据
	    HFD_Trend_StartMinute	= 6;		//起始分钟 short  hhmm，查询该分钟开始的走势，包括该分钟

	//明细查询请求字段定义
	public static final int 	
		HFD_Detail_RequestType	= 1,		//请求类型 unsigned short   走势、历史回忆查询，对应模板id=12
	    HFD_Detail_MarketId		= 2,		//市场编号 short
	    HFD_Detail_Code         = 3,		//品种代码   string
	    HFD_Detail_StartNO      = 4,        //起始笔数 int -1表示从最新的一笔往前查
	    HFD_Detail_ReqCount     = 5;		//查询总笔数  int 0表示查询当日所有的，最大600笔
	
	//K线查询请求字段定义
	public static final int 	
		HFD_KLineRequestType	= 1,		//请求类型 unsigned short   走势、历史回忆查询，对应模板id=13
	    HFD_KLine_KLineType     = 2,		//K线类型 unsigned char 0-日线，1-1分钟线，2-5分钟线，3-60分钟线
	    HFD_KLine_MarketId		= 3,		//市场编号 short
	    HFD_KLine_Code          = 4,		//品种代码   string
	    HFD_KLine_StartDate		= 5,		//起始日期 int yyyymmdd，包括当日
	    HFD_KLine_StartTime		= 6,		//起始时间 int hhmmss
	    HFD_KLine_EndDate		= 7,		//终止日期 int yyyymmdd，包括当日
	    HFD_KLine_EndTime       = 8,        //终止时间 int hhmmss
	    HFD_KLine_ReqCount		= 9,		//请求根数 short 起止时间均不为0时无效，否则以有效的那个时间为基准请求指定根数K线，若两个日期均为0，以当前时间为基准请求指定根数K线
	    HFD_KLine_FQType        = 10;       //复权类型 unsigned char 0-不复权，1-前复权，2-后复权

	
	//快照查询 1=5
	public static final int 	
	    HFD_APPLY_5_RequestType = 1,		//请求类型 unsigned short   快照查询 1=5
		HFD_APPLY_5_ReqCount	= 2,		//订阅品种数 short   重复出现2、3、4项
	    HFD_APPLY_5_MarketId	= 3,		//品种市场编号 short 若未指定代码和分类，订阅全市场；若未指定代码，订阅指定分类
	    HFD_APPLY_5_Code        = 4,		//品种代码   string 订阅指定代码商品
	    HFD_APPLY_5_GroupId     = 5;		//品种分类号 unsigned char 订阅指定分类商品
	
	//快照查询 1=18
	public static final int 	
	    HFD_APPLY_18_RequestType = 1,		//请求类型 unsigned short   快照查询 1=18
		HFD_APPLY_18_Range	= 2,		//查询排名范围 unsigned char
	    HFD_APPLY_18_FieldCount	= 3,		//查询排名字段数 short 重复出现4项
	    HFD_APPLY_18_Field        = 4,		//排名字段 unsigned char
	    HFD_APPLY_18_Count     = 5;		//请求排名个数 Unsigned int 0-全部，非0-返回指定个数，不填为缺省前10

	//排名字段定义
	public static final int
		PBF_RANK_PRICE_UP = 0,			//当日涨幅 
		PBF_RANK_5MINPRICE_UP = 1,		//5分钟涨幅涨幅 
		PBF_RANK_ORDER_UP = 2,			//当日委比涨
		PBF_RANK_AMPLITUDE_UP = 3,		//当日振幅 
		PBF_RANK_VOLUMERATE_UP = 4,		//量比	 
		PBF_RANK_TURNOVER_UP = 5,		//总金额
		PBF_RANK_CHANGING_UP = 6,		//换手率
		PBF_RANK_PRICE_DOWN = 101,		//当日跌幅
		PBF_RANK_5MINPRICE_DOWN = 102,	//5分钟跌幅
		PBF_RANK_ORDER_DOWN = 103;		//委比跌 	 
}
