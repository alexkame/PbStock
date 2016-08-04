package com.pengbo.mhdzq.constant;

public class AppConstants {
	
	public static final String APP_PREFERENCE = "com.mhdzq.preference.application";
	public static final String APP_VERSION_INFO = "v0.9.1 B001(20151202dbzqfz)";
	
	public static final String THIRD_APP_PACKAGE = "com.cairh.khapp.nesc";//东北证券开户APK包名
	public static final String THIRD_APP_DOWNLOAD_PATH = "http://218.62.40.58:8086/nesc-sjkh.apk";
	public static final String HOMAPAGE_ADS = "http://222.168.95.186/reg/";  //主页面上的广告链接地址
	//行情类别
	public static final int HQTYPE_NULL = 0;
	public static final int HQTYPE_QQ = 1; //期权
	public static final int HQTYPE_ZQ = 2; //证券
	//行情市场代码
	public	static final int		HQ_MARKET_SHQQ_INT	=	1090;	//上海个股期权
	public	static final int		HQ_MARKET_SZQQ_INT	=	1091;	//深圳个股期权
	public	static final int		HQ_MARKET_BOCE_INT	=	31004;	//渤商所
	public	static final int		HQ_MARKET_CFFEX_INT	=	2300;	//中金所
	public	static final int		HQ_MARKET_CZCE_INT	=	2200;	//郑商所
	public	static final int		HQ_MARKET_GT_E_INT	=	3000;	//
	public	static final int		HQ_MARKET_GT_S_INT	=	12000;	//
	public	static final int	    HQ_MARKET_NULL_INT 	=	0;			//证券交易所未知
	//更新服务器地址
	public static final String SERVER_PATH_UPDATEFILE = "http://221.231.139.137/mobile/update/pbstock/dongbeizq/fz/ver0/";//文件更新http服务器地址
	public static final String SERVER_PATH_UPDATEAPP = "http://221.231.139.137/mobile/apkupdate/pbstock/dongbeizq/fz/";//版本信息文件
	//交易设置
	public static final String PREF_KEY_TRADE_ONLINE_TIME = "trade_online_time";
	public static final String PREF_KEY_TRADE_ORDER_CONFIRM = "trade_order_confirm";//期权的下单无需确认 文件名 
	public static final String ZQ_PREF_KEY_TRADE_ORDER_CONFIRM = "zq_trade_order_confirm";//证券的下单无需确认名  
	public static final String PREF_KEY_TRADE_MODE = "trade_mode";
	public static final String PREF_KEY_TRADE_ORDER_INCREASE_NUM = "trade_order_increase_num";
	public static final String PREF_KEY_TRADE_ALL_PINGCANG_WTPRICE = "trade_all_pingcang_wtprice";
	public static final String PREF_KEY_TRADE_KJFS_WTPRICE = "trade_kjfs_wtprice";
	public static final String PREF_KEY_TRADE_KJFS_AUTO_CD_TIME = "trade_kjfs_auto_cd_time";
	public static final String PREF_KEY_TRADE_SENIOR_MODE_WT_BTN = "trade_senior_mode_wt_btn";
	public static final String PREF_KEY_TRADE_ACCOUNT_SAVED = "trade_account_saved";
	public static final String PREF_KEY_TRADE_ACCOUNT_SAVED_ZQ = "trade_account_saved_zq";
	public static final String PREF_KEY_TRADE_MRCD_NUM = "trade_order_mrcd_num";//默认拆单数量
	//券商选择
	public static final String PREF_KEY_TRADE_QS_SELECTION = "trade_qs_select_index";
	public static final String PREF_KEY_TRADE_PHONE_VERIFY = "trade_phone_verify";
	public static final String PREF_KEY_TRADE_PHONE_TEMP = "trade_phone_temp";

	//行情筛选条件设置
	public static final String PREF_KEY_HQ_QUERY_CONDITION = "hq_query_condition";

	//行情列表分页每页显示的条目数
	public static final int LIST_PAGE_ITEMS_NUM = 20;
	
	public static final int LIST_HEADER_ITEMS_BSS = 10;//list 标题头的列数
	public static final int LIST_HEADER_ITEMS = 19;//list 标题头的列数
	public static final int TLIST_HEADER_ITEMS = 17;//T型报价list 标题头的列数
	
	public static final int HOME_PAGE_ITEM_SIZE = 8;//首页gridview每页显示个数
	public static final int	MAXNUM_KLINE = 400;
	
	
	//现货交易设置相关 
	public static final String PREF_KEY_MA = "set_ma";
	public static final String PREF_KEY_MACD = "set_macd";
	public static final String PREF_KEY_KDJ = "set_kdj";
	public static final String PREF_KEY_RSJ= "set_rsj";
	public static final String PREF_KEY_WR= "set_wr";
	public static final String PREF_KEY_BIAS = "set_bias";
	
	public static final String PREF_KEY_ZDJSBJJG = "set_jdjsbjjg";//涨跌计算比较价格
	public static final String PREF_KEY_XSBDGDJ = "set_xsbdgdj";//显示波段高低价
	
	public static final String PREF_KEY_QDSFMRJRZX = "set_qdsfmrjrzx";//启动是否默认进入自选 
	
	public static final String PREF_KEY_QDZNJP = "set_qdznjp";//启动智能键盘
	public static final String PREF_KEY_YXPMZDJRXM = "set_yxpmzdjrxm";//允许屏幕自动休眠  
	public static final String PREF_KEY_CJHBZDTX = "set_cjhbzdtx";//成交回报自动提醒
	public static final String PREF_KEY_VERSIONCODE = "set_versioncode";//version code
	
	public static final String PREF_KEY_ZLHYLENGTH = "set_zlhylength";//1000.ini length
	public static final String PREF_KEY_ZLHYDATE = "set_zlhydate";//1000.ini date
	public static final String PREF_KEY_ZLHYTIME = "set_zlhytime";//1000.ini time
	public static final String PREF_KEY_SHUILVLENGTH = "set_shuilvlength";//CustomColumn.ini length
	public static final String PREF_KEY_SHUILVDATE = "set_shuilvdate";//CustomColumn.ini date
	public static final String PREF_KEY_SHUILVTIME = "set_shuilvtime";//CustomColumn.ini time
	public static final String PREF_KEY_KEEPTRADELIVETIME = "set_tradelivetime";//Trade keep live time
	
	public static final boolean IS_NEED_WARNING_SAVE_ACCOUNT = false;
	public static final boolean IS_NEED_ABOUT_SHOW_IN_MAINPAGER = true;
	public static final boolean IS_TEXTVIEW_REQUEIRED = false;
}
