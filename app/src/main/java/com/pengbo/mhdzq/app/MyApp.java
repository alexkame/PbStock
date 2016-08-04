package com.pengbo.mhdzq.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.util.EncodingUtils;
import org.xmlpull.v1.XmlPullParser;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Xml;

import com.pengbo.mhdcx.bean.News;
import com.pengbo.mhdcx.fragment.TradeOrderFragment;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.ui.main_activity.MyStockActivity;
import com.pengbo.mhdcx.ui.main_activity.ScreenActivity;
import com.pengbo.mhdcx.ui.main_activity.TradeLoginActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeDetailActivity;
import com.pengbo.mhdzq.trade.data.TradeAccountInfo;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.CGlobalParam;
import com.pengbo.mhdzq.data.CHQData;
import com.pengbo.mhdzq.data.CMarketInfo;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.data.CPBMarket;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.data.PublicData.Stock;
import com.pengbo.mhdzq.data.PublicData.StockInfo;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.DBZQHomeActivityMarket;
import com.pengbo.mhdzq.data.Rule;
import com.pengbo.mhdzq.data.SSLEncrypt;
import com.pengbo.mhdzq.data.SearchDataItem;
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalHQRecord;
import com.pengbo.mhdzq.data.TagLocalKLineData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.KeyTool;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PinYin;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.TradeQSInfo;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_trade_activity.ZqTradeDetailActivity;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author Pobo
 * 
 */
public class MyApp extends Application {
	private static final String TAG = "MyApp";
	private static MyApp instance;

	/****************** 1 网络 相关 ***********************/
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	/****************** 行情登录状态 ********************/
	/** 未登录 */
	public static final int HQLoginStatus_NO = 0x00;
	/** 登录中 */
	public static final int HQLoginStatus_Loging = 0x01;
	/** 查询登录 */
	public static final int HQLoginStatus_Query = 0x02;
	/** 推送登录 */
	public static final int HQLoginStatus_Push = 0x04;
	/** 查询和推送都已登录 */
	public static final int HQLoginStatus_All = 0x06;

	private static final String HQ_DEFAULT_KEY = "pv7nnasfdfghkldk";
	/************* 配置文件 ***************/
	/** main.cfg **/
	public static final String MAIN_CONFIGPATH = "main.cfg";
	/** trade_server.ini **/
	public static final String TRADE_CONFIGPATH = "trade_server.ini";
	/** trade_addr.ini **/
	public static final String TRADE_ADDRPATH = "trade_addr.ini";
	/** downloadList.ini **/
	public static final String DOWNLOAD_CONFIGPATH = "downloadList.ini";
	/** downloadListTemp.ini **/
	public static final String DOWNLOAD_CONFIGPATH_TEMP = "downloadListTemp.ini";
	/** hq_addr.ini **/
	public static final String HQ_CONFIGPATH = "hq_addr.ini";
	/** softversion.ini **/
	public static final String APP_VERSIONFILE = "softversion.ini";
	/** hq_zhishu.ini **/
	public static final String HQ_ZHISHUCONFIG = "hq_zhishu.ini";
	/** MbUserMarket.xml **/
	public static final String PB_USER_MARKETFILE = "MbUserMarket.xml";
	/** defaultConfig.ini **/
	public static final String DEFAULT_CONFIGPATH = "defaultConfig.ini";
	/** yhzl.ini **/
	public static final String YHZL_CONFIGFILE = "yhzl.ini";
	/** ../block/1000.ini **/
	public static final String FILE_CONFIG_ZLHY_REQ = "../block/1000.ini";
	/** 1000.ini **/
	public static final String FILE_CONFIG_ZLHY = "1000.ini";
	/** monijiaoyi.ini **/
	public static final String MNJYHY_CONFIGFILE = "monijiaoyi.ini";
	/** CustomColumn.ini **/
	public static final String SHUILV_CONFIGFILE = "CustomColumn.ini";

	/** 默认分页大小 */
	public static final int PAGE_SIZE = 20;
	/** 读取main.cfg文件 */
	public MIniFile mMainIniFile;
	/** 登录状态 */
	private int mHQLoginStatus = HQLoginStatus_NO;
	/** 行情服务器分配给客户的识别ID */
	public int mLoginIdentID;

	public int mCurrentHQType = AppConstants.HQTYPE_QQ;
	/** 存储期权行情列表数据 */
	public CHQData mHQData;
	/** 存储证券行情列表数据 */
	public CHQData mHQData_ZQ;
	/** 存储标的行情数据 */
	public CHQData mStockConfigData;
	/** 存储指数行情列表数据 */
	public CHQData mZhiShuData;
	/** 指数列表 在文件：hq_zhishu.ini */
	public ArrayList<TagCodeInfo> mZhiShuCodeInfos;
	/** 证券指数列表 在文件：hq_zhishu.ini */
	public ArrayList<TagCodeInfo> mZQ_ZhiShuCodeInfos;
	/** 全局参数 */
	public CGlobalParam mGlobalParam;
	/** 市场配置信息 文件名为：pb_marketinfo_xh.cfg */
	public CMarketInfo mMarketInfo;
	/** 存储码表信息 */
	public CDataCodeTable[] mCodeTable;

	public ArrayList<News> mNews;

	/****************** 自选股相关 ***************/
	/** 自选股-期权 */
	private ArrayList<TagCodeInfo> mMyStockList;
	/** 自选股-证券 */
	private ArrayList<TagCodeInfo> mMyStockList_ZQ;
	/** 码表市场 证券+期权市场，期权目前有2个，0-上海市场，1-深圳市场 */
	public int[] mCodeTableMarket;
	/** 码表市场的数量 */
	public int mCodeTableMarketNum;
	/** 行情查询连接，认证服务器相关 */
	public GlobalNetConnect mCertifyNet;
	public String mHQAddress_Certify[];
	public int mAddrNum_Certify;
	/** HQ推送连接 */
	public GlobalNetConnect mHQPushNet;
	public String mHQPushAddress;

	/******************** 新的 自选股 路径 *********************/
	/** 期权 */
	public static final String MYSTOCK_PATH = "mystock.dat";
	/** 证券 */
	public static final String MYSTOCK_ZQ_PATH = "mystockzq.dat";
	public static final String TRADE_ACCOUNT_PATH = "tradeaccount.dat";
	public static final String TRADE_ACCOUNT_ZQ_PATH = "tradeaccountzq.dat";
	private ArrayList<TradeAccountInfo> mTradeAccountList;
	private ArrayList<TradeAccountInfo> mTradeAccountList_ZQ;
	/** 首页市场列表 文件路径 */
	public static final String HOMEMARKET_PATH = "DBZQhomemarket.dat";
	/** 本地市场所属用户,空为游客 */
	public String mSaveUser_home = "";
	/** 本地数据保存时间 */
	public long mSaveTime_home;
	/***** maxnum_MARKET ******/
	public static final int MAXNUM_HOMEMARKET = 100;

	/************* 2 自选股 最大数 走势 K线 ***************/
	/******** maxnum_stock **********/
	public static final int MAXNUM_STOCK = 1000;
	public static final int MAXNUM_TREND = 400;
	public static final int MAXNUM_KLINE = 400;
	public static final int MAXNUM_TRADEACCOUNT = 100;

	/******************** 升级 ******************/
	/*** APP最新版本 */
	public String mNewestVersion = "";
	/*** 新版本apk下载url */
	public String mUpdateURL = "";
	/** 新版本更新信息 */
	public String mUpdateMSG = "";
	public String mNewestVersionName = "";
	/** 版本升级提示框是否已经显示 */
	public boolean mbIsShowed = false;

	// public int mProduct = 0; // 产品号: 0/1--标准版、0x100--券商版
	// public int mPlatform = 0; // 平台号: 5--Android手机
	/************************** 用户相关的 。。 ************************************/
	/******* 用户相关 ***********/
	public boolean mLoginFlag = true;
	/** apk version code */
	public String mVersionCode = "1";
	/** 短信验证功能, 0-不需要，1需要 */
	public int mSMSQualify = 0;
	/** 短信认证的手机号码 */
	public String mSMSPhone;
	/** 行情登录用户名 */
	public String mUser;
	/** 行情登录机构ID */
	public int mJGId;
	/** 行情登录密码 */
	public String mPassWord;
	public String mVersion;
	public String mPhoneNum;
	/** 是否启动server做后台程序检查，activity劫持检测 */
	public boolean isCheckServerStart = false;
	/** 是否行情刷新 */
	public boolean isSchedule = true;
	/** 是否有认证口令 */
	public boolean isRenZhengKouLin = false;
	/** 是否 */
	public boolean isYanZhengMa = true;
	/** 是否短信验证 */
	public boolean isSMSVerify = false;
	/** 配置文件里所有市场列表 MbUserMarket.xml */
	public ArrayList<CPBMarket> mPBMarketArray;
	/** 定制板块里所有市场列表 */
	public ArrayList<CPBMarket> mPBMarketArray_DZBK_ALL;
	/** 定制板块自定义列表 */
	public ArrayList<CPBMarket> mPBMarketArray_DZBK;
	public static final String DZBKMARKET_PATH = "dzbkmarket.dat";
	/** 市场配置文件MbUserMarket.xml文件的版本号 */
	public String mPBMarketDZBKVersion = "1.0";
	/** maxnum_MARKET */
	public static final int MAXNUM_DZBKMARKET = 100;

	public int mCurrentTapMarketIndex = 0;
	/** yhzl.ini里合约信息（code和market） */
	public ArrayList<StockInfo> mYHZLArray;
	/** 主力合约 */
	public ArrayList<StockInfo> mZLHYArray;
	/** 模拟交易合约 monijiaoyi.ini（code和market ） */
	public ArrayList<StockInfo> mMNJYHY;
	/** 渤商所加税价对应各品种的税率文件 */
	public MIniFile mSHUILVIni;
	public boolean m_bZLHYDownload = false;
	/** 走势数据(合约) */
	private ArrayList<TagLocalTrendData> mTrendDataArray;
	/** 走势数据 （标的 */
	private ArrayList<TagLocalTrendData> mTrendStockDataArray;
	/** 标的K线数据 */
	private ArrayList<TagLocalKLineData> mStockKLineDataArray;
	/** 明细数据 */
	private ArrayList<TagLocalDealData> mDealDataArray;
	/** K线数据 */
	private ArrayList<TagLocalKLineData> mKLineDataArray;
	private ArrayList<TagLocalKLineData> mKLineWeekArray;
	private ArrayList<TagLocalKLineData> mKLineMonthArray;
	private ArrayList<TagLocalKLineData> mKLineMinArray;

	/************************** 另外存一份数据 ********************/
	/** 走势数据（横屏的 ） */
	private ArrayList<TagLocalTrendData> mLandTrendDataArray;
	/** K线数据（横屏的 ） */
	private ArrayList<TagLocalKLineData> mLandKLineDataArray;
	/** 周线数据 （横屏的 ） */
	private ArrayList<TagLocalKLineData> mLandKLineWeekArray;
	/** 月线数据 （横屏的） */
	private ArrayList<TagLocalKLineData> mLandKLineMonthArray;
	/** 分钟线 （横屏的 ） */
	private ArrayList<TagLocalKLineData> mLandKLineMinArray;

	/************************** 交易相关 ************************************/
	/***** 交易服务器地址 ****/
	public TradeNetConnect mTradeNet;
	public String mTrade_Address[];
	public String mTrade_reconnectAddress[];
	public int mTrade_AddrNum;
	public int mTrade_reconnectAddrNum;
	/** 期权交易相关数据 */
	public TradeData mTradeData;
	/** 是否直接进入下单页面 */
	public boolean mbDirectInOrderPage = false;
	public int mIndexOfOrderBtn = TradeOrderFragment.Btn_WT_All;
	/** 交易站点选择索引 */
	public int mTradeQSIndex = -1;
	/** 交易账号类别 */
	public String mTradeQSZHType = "";
	/** 交易站点列表 */
	private ArrayList<TradeQSInfo> mTradeQSList;
	/** 交易站点选择索引 */
	public int mTradeQSIndex_ZQ = -1;
	/** 交易账号类别 */
	public String mTradeQSZHType_ZQ = "";
	/** 交易站点列表 */
	private ArrayList<TradeQSInfo> mTradeQSList_ZQ;
	/** 当前期权合约 */
	private TagCodeInfo mCurrentOption;
	/** 当前合约列表，支持上下滑动切换品种 */
	public ArrayList<CCodeTableItem> mCurrentStockArray;

	/************************** Activity 类 自选 筛选 相关的类 及 集合 ************************************/
	public boolean mIsMainActivity = false;
	public MainTabActivity mMainTabActivity;
	public TradeDetailActivity mTradeDetailActivity;
	public TradeLoginActivity mTradeLoginActivity;
	public ZqTradeDetailActivity mTradeZqDetailActivity;
	public MyStockActivity mMyStockActivity;
	public ScreenActivity mscreenActivity;

	/*********************** 合约搜索相关 *******************/
	/** 用于搜索的所有码表数据 */
	public ArrayList<SearchDataItem> mSearchCodeArray;
	/** 用于搜索的所有股票码表数据 */
	public ArrayList<SearchDataItem> mSearchTradeCodeArray;
	/** 用于存放倒计时时间 */
	public static Map<String, Long> map;
	/** 首页中间件模块解析及初始化 */
	public ArrayList<DBZQHomeActivityMarket> mDBZQHomeMenuMarkets;
	public ArrayList<DBZQHomeActivityMarket> mDBZQMarketArray;
	/** 市场配置文件版本号 */
	public String mDBZQMarketVersion = "1.0";

	public String mTradeAccount = "";
	public String mTradePassword = "";

	public String mTradeVersion = "";
	public int mTradeLockTimeout;
	public int mHQType = AppConstants.HQTYPE_NULL;

	public String mQQTradeAccount = "";
	public String mQQTradePassword = "";
	public int mQQHQType;

	public String mQQTradeVersion = "";
	public int mQQTradeLockTimeout;
	public String mQQType = "";

	public static final String DBZQ_USER_MARKETFILE = "DBZQHomeActivityMarket.xml";

	static {
		System.loadLibrary("SSLEncrypt");
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public boolean getSignature() {
		/** 通过包管理器获得指定包名包含签名的包信息 **/
		PackageInfo packInfo = getPackageInfo();
		/******* 通过返回的包信息获得签名数组 *******/
		Signature[] signatures = packInfo.signatures;

		KeyTool tool = new KeyTool();
		String md5 = "", sha1 = "", sha256 = "";
		try {
			// md5 = tool.getCertFingerPrint("MD5",
			// signatures[0].toByteArray());
			sha1 = tool.getCertFingerPrint("SHA1", signatures[0].toByteArray());
			sha256 = tool.getCertFingerPrint("SHA-256",
					signatures[0].toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean bOffical = SSLEncrypt.IsOffical(md5, sha1, sha256);
		return bOffical;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;
		// 注册App异常崩溃处理器
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());

		mVersionCode = STD.IntToString(getPackageInfo().versionCode);
		String saveVersion = PreferenceEngine.getInstance().getVersionCode();
		if (saveVersion.isEmpty()) {
			saveVersion = mVersionCode;
		} else {
			if (STD.StringToInt(saveVersion) < STD.StringToInt(mVersionCode)) {
				saveVersion = mVersionCode;
			}
		}
		PreferenceEngine.getInstance().saveVersionCode(saveVersion);
		// 用户初始化
		mJGId = 0;
		mUser = new String();
		mPassWord = new String();
		mHQData = new CHQData();
		mHQData_ZQ = new CHQData();

		mStockConfigData = new CHQData();
		mZhiShuData = new CHQData();

		mGlobalParam = new CGlobalParam();

		// 创建码表保存对象，初始化动作比较耗时，放在启动页进行
		mCodeTable = new CDataCodeTable[CDataCodeTable.MAX_SAVE_MARKET_COUNT];
		mCodeTableMarket = new int[CDataCodeTable.MAX_SAVE_MARKET_COUNT];
		// 认证服务器地址
		/*************************************************************** 加载前 读取配置 文件 ********************************/

		mCertifyNet = new GlobalNetConnect(this);
		mHQPushNet = new GlobalNetConnect(this);
		mHQAddress_Certify = new String[10];
		mAddrNum_Certify = 0;

		// 如果程序是第一次启动或者升级后第一次启动，清除files目录下文件
		String versionName = getPackageInfo().versionName;
		if (!PreferenceEngine.getInstance().getAPKVersion(versionName)) {
			FileService file = new FileService(this.getApplicationContext());
			file.deleteFile(TRADE_CONFIGPATH);
			file.deleteFile(CMarketInfo.MarketInfo_FileName);
		}

		// 读取main.cfg文件
		mMainIniFile = new MIniFile(getApplicationContext(), MAIN_CONFIGPATH);
		// 读取main.cfg文件中pb对应的值
		if (mMainIniFile.ReadInt("pb", "pb", 0) != 0) {
			isCheckServerStart = true;
		}
		// 读取行情登录用户和密码
		mUser = mMainIniFile.ReadString("hq", "loginname", "pobo");
		mPassWord = mMainIniFile.ReadString("hq", "loginpwd", "pobo");
		mJGId = mMainIniFile.ReadInt("hq", "jgid", 0);
		// 认证口令
		int rzkl = mMainIniFile.ReadInt("trade", "rzkl", 0);
		this.isRenZhengKouLin = (rzkl == 1) ? true : false;
		// 验证码
		int yzm = mMainIniFile.ReadInt("trade", "yzm", 0);
		this.isYanZhengMa = (yzm == 1) ? true : false;

		// copy some ini config files to data/../files for using and upgrade.
		initHQAddrFile();// copy hq_addr.ini to files/
		initDownloadListFile(); // copy downloadlist.ini to data/../files/
								// directory
		initTradeConfigFile(); // read trade config
		initTradeAddrFile(); // copy trade_addr.ini to data directory
		initDefaultConfigFile();// copy defaultconfig.ini to data directory
		// initBHBJConfig();// copy yhzl.ini to data/../files/ directory
		// initMNJYConfig();// copy monijiaoyi.ini to data/../files/ directory
		// initZLHYConfig();// copy 1000.ini to data/../files/ directory
		// initSHUILVConfig();// copy CustomColumn.ini to data/../files/
		// directory

		// 初始化市场信息
		mMarketInfo = new CMarketInfo();
		mMarketInfo.readFromFile();

		// 初始化指数列表，在hq_zhishu.ini文件中
		mZhiShuCodeInfos = new ArrayList<TagCodeInfo>();
		mZQ_ZhiShuCodeInfos = new ArrayList<TagCodeInfo>();
		MIniFile iniZhiShu = new MIniFile(getApplicationContext(),
				HQ_ZHISHUCONFIG);

		for (int i = 0; i < 5; i++) {
			String key = String.format("指数%d", i);

			String strName = iniZhiShu.ReadString(key, "指数名称", "");
			String strMarket = iniZhiShu.ReadString(key, "market", "");
			String strCode = iniZhiShu.ReadString(key, "code", "");

			if (!strName.isEmpty() && !strMarket.isEmpty()
					&& !strCode.isEmpty()) {
				TagCodeInfo codeInfo = new TagCodeInfo();
				codeInfo.market = (short) STD.StringToInt(strMarket);
				codeInfo.code = strCode;
				codeInfo.name = strName;

				mZhiShuCodeInfos.add(codeInfo);
			} else {
				break;
			}
		}

		for (int i = 0; i < 5; i++) {
			String key = String.format("证券%d", i);

			String strName = iniZhiShu.ReadString(key, "证券名称", "");
			String strMarket = iniZhiShu.ReadString(key, "market", "");
			String strCode = iniZhiShu.ReadString(key, "code", "");

			if (!strName.isEmpty() && !strMarket.isEmpty()
					&& !strCode.isEmpty()) {
				TagCodeInfo codeInfo = new TagCodeInfo();
				codeInfo.market = (short) STD.StringToInt(strMarket);
				codeInfo.code = strCode;
				codeInfo.name = strName;

				mZQ_ZhiShuCodeInfos.add(codeInfo);
			} else {
				break;
			}
		}

		mPBMarketArray_DZBK = new ArrayList<CPBMarket>();
		anlaysePBMarketXml();
		// 定制板块配置文件不存在，使用市场配置文件里default=1
		if (ReadDZBKMarketData_Ex() < 0) {
			for (int itemp = 0; itemp < mPBMarketArray_DZBK_ALL.size(); itemp++) {
				if (mPBMarketArray_DZBK_ALL.get(itemp).IsDefault) {
					mPBMarketArray_DZBK.add(mPBMarketArray_DZBK_ALL.get(itemp));
				}
			}
			startSaveDZBKMarketImmediately();
		}

		// 首页中间件模块解析及初始化
		mDBZQHomeMenuMarkets = new ArrayList<DBZQHomeActivityMarket>();
		anlayseDBZQMarketXml();
		// 首页配置文件不存在，使用市场配置文件里default=1
		if (ReadHomeMarketData_Ex() < 0) {
			for (int itemp = 0; itemp < mDBZQMarketArray.size(); itemp++) {
				if (mDBZQMarketArray.get(itemp).IsDefault) {
					mDBZQHomeMenuMarkets.add(mDBZQMarketArray.get(itemp));
				}
			}
			startSaveDBZQHomeMarketImmediately();
		}

		// 初始化自选股-期权
		mMyStockList = new ArrayList<TagCodeInfo>();
		// 初始化自选股-证券
		mMyStockList_ZQ = new ArrayList<TagCodeInfo>();

		/**
		 * ReadMyStockData_Ex 读取自选股-期权“mystock.dat”
		 */
		if (ReadMyStockData_Ex(AppConstants.HQTYPE_QQ) < 0) {
			L.i(TAG, "mystock.dat is not exist");
		}

		/**
		 * ReadMyStockData_Ex 读取自选股-证券“mystockzq.dat”
		 */
		if (ReadMyStockData_Ex(AppConstants.HQTYPE_ZQ) < 0) {
			L.i(TAG, "mystockzq.dat is not exist");
		}

		mCurrentStockArray = new ArrayList<CCodeTableItem>();
		mTrendDataArray = new ArrayList<TagLocalTrendData>();
		mTrendStockDataArray = new ArrayList<TagLocalTrendData>();

		mStockKLineDataArray = new ArrayList<TagLocalKLineData>();

		mKLineDataArray = new ArrayList<TagLocalKLineData>();
		mKLineWeekArray = new ArrayList<TagLocalKLineData>();
		mKLineMonthArray = new ArrayList<TagLocalKLineData>();
		mDealDataArray = new ArrayList<TagLocalDealData>();
		mKLineMinArray = new ArrayList<TagLocalKLineData>();

		// 横屏的数据
		mLandTrendDataArray = new ArrayList<TagLocalTrendData>();
		mLandKLineDataArray = new ArrayList<TagLocalKLineData>();
		mLandKLineWeekArray = new ArrayList<TagLocalKLineData>();
		mLandKLineMonthArray = new ArrayList<TagLocalKLineData>();
		mLandKLineMinArray = new ArrayList<TagLocalKLineData>();

		// 加载行情协议pbtransclnt.xml
		CPbDataDecode.LoadPackageTemplate(getApplicationContext(),
				"pbtransclnt.xml");

		SSLEncrypt.DesSetKey(HQ_DEFAULT_KEY, SSLEncrypt.SSLKEY_INDEX_HQ);

		// 从hq_addr.ini（该文件如果有更新会从server下载）添加行情地址信息
		String t[] = new String[10];
		int tnum = 10;
		int start = (int) (System.currentTimeMillis() & 0xff);
		for (int i = 0; i < 10; i++) {
			String key = "ip" + (i + 1);
			String addr = mMainIniFile.ReadString("addr", key, "");
			if (addr.length() > 0) {
				t[i] = addr;
			} else {
				tnum = i;
				break;
			}
		}

		MIniFile iniFile = new MIniFile();
		iniFile.setFilePath(getApplicationContext(), MyApp.HQ_CONFIGPATH);

		for (int i = 0; i < 10 - tnum; i++) {
			String key = "ip" + (i + 1);
			String addr = iniFile.ReadString("addr", key, "");
			if (addr.length() > 0) {
				t[tnum + i] = addr;
			} else {
				tnum += i;
				break;
			}
		}
		// 随机添加行情服务器地址
		for (int i = 0; i < tnum; i++, start++) {
			start %= tnum;
			addAddress_Certify(t[start]);
		}
		mCertifyNet.setAddr(mHQAddress_Certify, mAddrNum_Certify);
		/************************************************************************************************/

		// 交易相关
		// 初始化保存的交易账号
		mTradeAccountList = new ArrayList<TradeAccountInfo>();
		ReadMyTradeAccountData_Ex();

		mTradeAccountList_ZQ = new ArrayList<TradeAccountInfo>();
		ReadMyTradeAccountData_ZQ();

		mTradeNet = new TradeNetConnect(this);
		mTrade_Address = new String[10];
		mTrade_AddrNum = 0;
		mTrade_reconnectAddress = new String[10];
		mTrade_reconnectAddrNum = 0;
		mTradeData = new TradeData();
		mTradeData.mReconnected = false;

		start = (int) (System.currentTimeMillis() & 0xff);
		tnum = 10;
		String tradeAddr[] = new String[10];
		for (int i = 0; i < 10; i++) {
			String key = "ip" + (i + 1);
			String addr = mMainIniFile.ReadString("trade", key, "");
			if (addr.length() > 0) {
				tradeAddr[i] = addr;
			} else {
				tnum = i;
				break;
			}
		}
		for (int i = 0; i < tnum; i++, start++) {
			start %= tnum;
			addAddress_Trade(tradeAddr[start]);
		}

		mTradeQSList = new ArrayList<TradeQSInfo>();
		mTradeQSList_ZQ = new ArrayList<TradeQSInfo>();
	}

	/**
	 * 解析市场列表配置文件DBZQHomeActivityMarket.xml（得到首页中中间模块的展示数据）,
	 * 向mDBZQMarketArray中写数据
	 */
	private void anlayseDBZQMarketXml() {

		InputStream inStream = null;
		try {
			inStream = this.getApplicationContext().getResources().getAssets()
					.open(DBZQ_USER_MARKETFILE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		XmlPullParser parser = Xml.newPullParser();
		DBZQHomeActivityMarket mMarket = null;
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = parser.getName();

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: // 文档开始事件,可以进行数据初始化处理
					if (mDBZQMarketArray != null) {
						mDBZQMarketArray = null;
					}
					mDBZQMarketArray = new ArrayList<DBZQHomeActivityMarket>();
					break;
				case XmlPullParser.START_TAG: // 开始元素事件
					if (parser.getName().equalsIgnoreCase("dbzqmarkets")) {
						this.mDBZQMarketVersion = parser.getAttributeValue(
								null, "version");
					} else if (parser.getName().equalsIgnoreCase("dbzqmarket")) {
						mMarket = new DBZQHomeActivityMarket();
						mMarket.Name = parser.getAttributeValue(null, "name");
						mMarket.Id = parser.getAttributeValue(null, "id");
						int fixed = STD.StringToInt(parser.getAttributeValue(
								null, "fixed"));
						if (fixed == 0) {
							mMarket.IsFixed = false;
						} else {
							mMarket.IsFixed = true;
						}
						int IsDefault = STD.StringToInt(parser
								.getAttributeValue(null, "default"));
						if (IsDefault == 0) {
							mMarket.IsDefault = false;
						} else {
							mMarket.IsDefault = true;
						}
					} else if (mMarket != null) {
						if (name.equalsIgnoreCase("image")) {
							mMarket.NormalIcon = parser.getAttributeValue(null,
									"normal");
							if (mMarket.NormalIcon.length() > 4) {
								mMarket.NormalIcon = mMarket.NormalIcon
										.substring(0,
												mMarket.NormalIcon.length() - 4);
							}
							mMarket.PressIcon = parser.getAttributeValue(null,
									"focus");
							if (mMarket.PressIcon.length() > 4) {
								mMarket.PressIcon = mMarket.PressIcon
										.substring(0,
												mMarket.PressIcon.length() - 4);
							}
						} else if (name.equalsIgnoreCase("link")) {
							mMarket.url = "";
							mMarket.url = parser.getAttributeValue(null, "url");
						}
					}

					break;

				case XmlPullParser.END_TAG:// 结束元素事件
					if (parser.getName().equalsIgnoreCase("dbzqmarket")
							&& mMarket != null) {
						if (mDBZQMarketArray == null) {
							mDBZQMarketArray = new ArrayList<DBZQHomeActivityMarket>();
						}
						mDBZQMarketArray.add(mMarket);

						mMarket = null;
					}
					break;
				}
				eventType = parser.next();
			}
			inStream.close();
			L.i("info_anlayseDBZQMarketXml success");
		} catch (Exception e) {
			e.printStackTrace();
			L.e("ERROR_anlayseDBZQMarketXml", e.toString());
		}

	}

	/**
	 * 解析市场列表配置文件MbUserMarket.xml文件， 存储在mPBMarketArray与mPBMarketArray_DZBK_ALL中
	 */
	private void anlaysePBMarketXml() {
		InputStream inStream = null;
		try {
			inStream = getApplicationContext().getResources().getAssets()
					.open(PB_USER_MARKETFILE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		XmlPullParser parser = Xml.newPullParser();
		CPBMarket pbmarket = null;

		Rule pbrule = null;
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				// 文档开始事件,可以进行数据初始化处理
				case XmlPullParser.START_DOCUMENT:
					if (mPBMarketArray == null) {
						mPBMarketArray = new ArrayList<CPBMarket>();
					}
					mPBMarketArray.clear();

					if (mPBMarketArray_DZBK_ALL == null) {
						mPBMarketArray_DZBK_ALL = new ArrayList<CPBMarket>();
					}
					mPBMarketArray_DZBK_ALL.clear();
					break;
				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if (name.equalsIgnoreCase("pbmarkets")) {
						this.mPBMarketDZBKVersion = parser.getAttributeValue(
								null, "version");
					} else if (name.equalsIgnoreCase("pbmarket")) {
						pbmarket = new CPBMarket();

						// pbmarket.MarketIds = parser.getAttributeValue(null,
						// "market");
						pbmarket.Name = parser.getAttributeValue(null, "name");
						pbmarket.Id = parser.getAttributeValue(null, "id");
						pbmarket.ParentId = parser.getAttributeValue(null,
								"parentid");
						int fixed = STD.StringToInt(parser.getAttributeValue(
								null, "fixed"));
						if (fixed == 0) {
							pbmarket.IsFixed = false;
						} else {
							pbmarket.IsFixed = true;
						}

						int IsDefault = STD.StringToInt(parser
								.getAttributeValue(null, "default"));
						if (IsDefault == 0) {
							pbmarket.IsDefault = false;
						} else {
							pbmarket.IsDefault = true;
						}

					} else if (pbmarket != null) {

						if (name.equalsIgnoreCase("image")) {
							pbmarket.NormalIcon = parser.getAttributeValue(
									null, "normal");
							if (pbmarket.NormalIcon.length() > 4) {
								pbmarket.NormalIcon = pbmarket.NormalIcon
										.substring(
												0,
												pbmarket.NormalIcon.length() - 4);
							}
							pbmarket.PressIcon = parser.getAttributeValue(null,
									"focus");
							if (pbmarket.PressIcon.length() > 4) {
								pbmarket.PressIcon = pbmarket.PressIcon
										.substring(0,
												pbmarket.PressIcon.length() - 4);
							}
						} else if (name.equalsIgnoreCase("Rule")) {
							pbrule = new Rule();
							pbrule.mCategory = parser.getAttributeValue(null,
									"Category");
							pbrule.mMarketId = parser.getAttributeValue(null,
									"MarketID");
							pbrule.mCode = parser.getAttributeValue(null,
									"Code");
							pbrule.mGroupCode = parser.getAttributeValue(null,
									"GroupCode");
						}
					}
					break;

				case XmlPullParser.END_TAG:// 结束元素事件
					if (parser.getName().equalsIgnoreCase("pbmarket")
							&& pbmarket != null) {
						if (pbmarket.ParentId != null
								&& STD.StringToInt(pbmarket.ParentId) == KeyDefine.KEY_MARKET_DZBK) {
							if (mPBMarketArray_DZBK_ALL == null) {
								mPBMarketArray_DZBK_ALL = new ArrayList<CPBMarket>();
							}
							mPBMarketArray_DZBK_ALL.add(pbmarket);
						}
						if (mPBMarketArray == null) {
							mPBMarketArray = new ArrayList<CPBMarket>();
						}
						mPBMarketArray.add(pbmarket);
						pbmarket = null;
					} else if (parser.getName().equalsIgnoreCase("Rule")
							&& pbmarket != null && pbrule != null) {
						if (pbmarket.mRules == null) {
							pbmarket.mRules = new ArrayList<Rule>();
						}
						pbmarket.mRules.add(pbrule);

						pbrule = null;
					}
					break;
				}

				eventType = parser.next();
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			L.e("ERROR_anlaysePBMarketXml", e.toString());
		}
	}

	// 渤商所税率配置文件
	public void initSHUILVConfig() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(SHUILV_CONFIGFILE);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(SHUILV_CONFIGFILE);
				file.copyFile(input, SHUILV_CONFIGFILE);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// yhzl.ini
	public void initBHBJConfig() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(YHZL_CONFIGFILE);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(YHZL_CONFIGFILE);
				file.copyFile(input, YHZL_CONFIGFILE);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mYHZLArray = new ArrayList<StockInfo>();
		mYHZLArray.clear();
	}

	public void initMNJYConfig() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(MNJYHY_CONFIGFILE);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(MNJYHY_CONFIGFILE);
				file.copyFile(input, MNJYHY_CONFIGFILE);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mMNJYHY = new ArrayList<StockInfo>();
		mMNJYHY.clear();
	}

	public void initZLHYConfig() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(FILE_CONFIG_ZLHY);
		// 文件不存在
		if (size < 0) {
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(FILE_CONFIG_ZLHY);
				file.copyFile(input, FILE_CONFIG_ZLHY);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mZLHYArray = new ArrayList<StockInfo>();
		mZLHYArray.clear();
	}

	/**
	 * 初始化hq_addr.ini文件，如果文件不在files目录下，copy
	 */
	public void initHQAddrFile() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(HQ_CONFIGPATH);

		if (size < 0) {
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(HQ_CONFIGPATH);
				file.copyFile(input, HQ_CONFIGPATH);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化downloadList.ini文件，如果文件不在files目录下，copy
	 */
	public void initDownloadListFile() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(DOWNLOAD_CONFIGPATH);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(DOWNLOAD_CONFIGPATH);
				file.copyFile(input, DOWNLOAD_CONFIGPATH);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化trade_server.ini文件，如果文件不在files目录下，copy
	 */
	public void initTradeConfigFile() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(TRADE_CONFIGPATH);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(TRADE_CONFIGPATH);
				file.copyFile(input, TRADE_CONFIGPATH);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化trade_addr.ini文件，如果文件不在files目录下，copy
	 */
	public void initTradeAddrFile() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(TRADE_ADDRPATH);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(TRADE_ADDRPATH);
				file.copyFile(input, TRADE_ADDRPATH);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化defaultConfig.ini文件，如果文件不在files目录下，copy
	 */
	public void initDefaultConfigFile() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(DEFAULT_CONFIGPATH);

		if (size < 0)// 文件不存在
		{
			try {
				InputStream input = this.getApplicationContext().getAssets()
						.open(DEFAULT_CONFIGPATH);
				file.copyFile(input, DEFAULT_CONFIGPATH);
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static MyApp getInstance() {
		return instance;
	}

	/**
	 * 获取当前市场类别，期权或者证券
	 * 
	 * @return
	 */
	public int getCurrentHQType() {
		return mCurrentHQType;
	}

	/**
	 * 设置当前市场类别，期权或证券
	 * 
	 * @param type
	 */
	public void setCurrentHQType(int type) {
		mCurrentHQType = type;
	}

	// HQ服务器
	/****************************************************************************************************/
	public void resetAddrNum_Certify() {
		mAddrNum_Certify = 0;
	}

	public void addAddress_Certify(String addr) {
		for (int i = 0; i < mAddrNum_Certify; i++) {
			if (mHQAddress_Certify[i].equals(addr)) {
				return;
			}
		}
		if (mAddrNum_Certify >= mHQAddress_Certify.length) {
			return;
		}
		mHQAddress_Certify[mAddrNum_Certify] = addr;
		mAddrNum_Certify++;
	}

	public int getAddrNum_Certify() {
		return mAddrNum_Certify;
	}

	public void setCertifyNetHandler(Handler handler) {
		mCertifyNet.setMainHandler(handler);
	}

	public Handler getCertifyNetHandler() {
		return mCertifyNet.getHandler();
	}

	public void setHQPushNetHandler(Handler handler) {
		mHQPushNet.setMainHandler(handler);
	}

	public Handler getHQPushNetHandler() {
		return mHQPushNet.getHandler();
	}

	public void setTradeHandler(Handler handler) {
		mTradeNet.setMainHandler(handler);
	}

	public Handler getTradeHandler() {
		return mTradeNet.getHandler();
	}

	// 获取交易服务器地址
	public void resetAddrNum_Trade() {
		mTrade_AddrNum = 0;
	}

	// 获取交易服务器地址
	public void resetAddrNum_Trade(int num) {
		mTrade_AddrNum = num;
	}

	public void addAddress_Trade(String addr) {
		for (int i = 0; i < mTrade_AddrNum; i++) {
			if (mTrade_Address[i] == addr) {
				return;
			}
		}
		if (mTrade_AddrNum >= mTrade_Address.length) {
			return;
		}
		mTrade_Address[mTrade_AddrNum] = addr;
		mTrade_AddrNum++;
	}

	// 获取交易服务器地址
	public void resetreconnectAddrNum_Trade() {
		mTrade_reconnectAddrNum = 0;
	}

	public void addreconnectAddress_Trade(String addr) {
		for (int i = 0; i < mTrade_reconnectAddrNum; i++) {
			if (mTrade_reconnectAddress[i] == addr) {
				return;
			}
		}
		if (mTrade_reconnectAddrNum >= mTrade_reconnectAddress.length) {
			return;
		}
		mTrade_reconnectAddress[mTrade_reconnectAddrNum] = addr;
		mTrade_reconnectAddrNum++;
	}

	public int getAddrCount_Trade() {
		return mTrade_AddrNum;
	}

	public void UpdateHQPushNetAddress() {
		String[] szAddr = new String[1];
		szAddr[0] = mCertifyNet.getSuccessAddr();
		mHQPushNet.setAddr(szAddr, 1);
	}

	// 解析合约行情数据
	public int parseAllOptionHQInfoData(byte[] szData, int nSize) {
		L.i(TAG, "Start parseAllOptionHQInfoData");
		int nCount = 0;
		int offset = 0;
		while (nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			if (wPackSize < 0) {
				L.e(TAG, "ERROR: parseAllOptionHQInfoData wPackSize = "
						+ wPackSize);
				break;
			}
			offset += 4;
			if (wPackSize > nSize - offset)
				break;

			CPbDataPackage pack = new CPbDataPackage();
			wPackSize = CPbDataDecode.DecodeOnePackage(szData, offset,
					wPackSize, pack);
			offset += wPackSize;
			if (pack.m_wPackageID == 10) {

				TagLocalHQRecord aRecord = new TagLocalHQRecord();
				aRecord.code = pack.GetNormalFieldByID(10).GetString();
				aRecord.market = (short) pack.GetNormalFieldByID(11).GetInt16();

				aRecord.nTradeDate = pack.GetNormalFieldByID(20).GetInt32();
				aRecord.nUpdateDate = pack.GetNormalFieldByID(21).GetInt32();
				aRecord.nUpdateTime = pack.GetNormalFieldByID(22).GetInt32();
				aRecord.nLastClose = pack.GetNormalFieldByID(23).GetInt32();
				aRecord.nLastClear = pack.GetNormalFieldByID(24).GetInt32();
				if (aRecord.nLastClear == 0) {
					aRecord.nLastClear = aRecord.nLastClose;
				}
				aRecord.dLastOpenInterest = pack.GetNormalFieldByID(25)
						.GetDouble();
				aRecord.nOpenPrice = pack.GetNormalFieldByID(26).GetInt32();
				aRecord.nHighPrice = pack.GetNormalFieldByID(27).GetInt32();
				aRecord.nLowPrice = pack.GetNormalFieldByID(28).GetInt32();
				aRecord.nLastPrice = pack.GetNormalFieldByID(29).GetInt32();
				if (aRecord.nLastPrice != 0) {
					aRecord.nLastPriceForCalc = aRecord.nLastPrice;
				} else {
					if (aRecord.nLastClose != 0) {
						aRecord.nLastPriceForCalc = aRecord.nLastClose;
					} else {
						aRecord.nLastPriceForCalc = aRecord.nLastClear;
					}// 如果没有现价用昨收价
				}

				aRecord.nUpperLimit = pack.GetNormalFieldByID(30).GetInt32();
				aRecord.nLowerLimit = pack.GetNormalFieldByID(31).GetInt32();
				aRecord.nAveragePrice = pack.GetNormalFieldByID(32).GetInt32();
				aRecord.nClearPrice = pack.GetNormalFieldByID(33).GetInt32();
				aRecord.nClosePrice = pack.GetNormalFieldByID(34).GetInt32();
				aRecord.volume = pack.GetNormalFieldByID(35).GetDouble();
				aRecord.amount = pack.GetNormalFieldByID(36).GetDouble();
				aRecord.dOpenInterest = pack.GetNormalFieldByID(39).GetDouble();
				aRecord.nTradeTicks = pack.GetNormalFieldByID(44).GetInt32();
				aRecord.nTradeDirect = pack.GetNormalFieldByID(45).GetInt8();
				aRecord.currentVolume = 0;
				aRecord.currentCJ = 0;
				aRecord.dLiquidity = pack.GetNormalFieldByID(48).GetFloat();
				if (pack.GetNormalFieldByID(47) != null
						&& pack.GetNormalFieldByID(47).IsValid()) {
					aRecord.currentVolume = pack.GetNormalFieldByID(47)
							.GetDouble();
				}

				if (pack.GetNormalFieldByID(37) != null
						&& pack.GetNormalFieldByID(37).IsValid()) {
					aRecord.dWPL = pack.GetNormalFieldByID(37).GetDouble();
				}
				int nArraySize = pack.GetArraySize(0);
				int i;
				for (i = 0; i < nArraySize && i < 5; i++) {
					aRecord.buyPrice[i] = pack.GetArrayFieldByID(40, i)
							.GetInt32();
					aRecord.buyVolume[i] = pack.GetArrayFieldByID(41, i)
							.GetInt32();
				}
				nArraySize = pack.GetArraySize(1);
				for (i = 0; i < nArraySize && i < 5; i++) {
					aRecord.sellPrice[i] = pack.GetArrayFieldByID(42, i)
							.GetInt32();
					aRecord.sellVolume[i] = pack.GetArrayFieldByID(43, i)
							.GetInt32();
				}

				nCount++;
				switch (mCurrentHQType) {
				case AppConstants.HQTYPE_QQ: {
					if (true == mZhiShuData.search(null, aRecord.market,
							aRecord.code)) {
						mZhiShuData.updateHQData(aRecord, true);
					} else if (true == mStockConfigData.search(null,
							aRecord.market, aRecord.code)) {
						mStockConfigData.updateHQData(aRecord, true);
					} else if (mHQData.updateHQData(aRecord, true) == false) {
						L.e(TAG, "ERROR: CDataHQ::updateHQData failed");
					}
				}
					break;
				case AppConstants.HQTYPE_ZQ: {
					if (mHQData_ZQ.updateHQData(aRecord, false) == false) {
						L.e(TAG, "ERROR: CDataHQ_ZQ::updateHQData failed");
						TagLocalStockData tempRecord = new TagLocalStockData();
						tempRecord.HQData.copyData(aRecord);

						ArrayList<CCodeTableItem> codeTableList = null;
						for (int j = 0; j < mCodeTableMarketNum
								&& j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
							if (tempRecord.HQData.market == mCodeTable[j].mMarketId) {
								codeTableList = mCodeTable[j].mCodeTableList;
								break;
							}
						}

						if (codeTableList != null) {
							for (int m = 0; m < codeTableList.size(); m++) {
								CCodeTableItem item = codeTableList.get(m);
								if (tempRecord.HQData.market == item.market
										&& tempRecord.HQData.code
												.equalsIgnoreCase(item.code)) {
									tempRecord.PriceDecimal = item.PriceDecimal;
									tempRecord.PriceRate = item.PriceRate;
									tempRecord.VolUnit = item.VolUnit;
									tempRecord.Multiplier = item.Multiplier;
									tempRecord.name = item.name;
									tempRecord.code = item.code;
									tempRecord.market = item.market;
									tempRecord.groupCode = item.groupCode;
									tempRecord.group = item.group;
									tempRecord.extCode = item.extCode;
									tempRecord.GroupOffset = item.GroupOffset;
									tempRecord.GroupFlag = item.GroupFlag;
									break;
								}
							}
						}

						mktGroupInfo groupRecord;
						groupRecord = mMarketInfo.searchMarketGroupInfo(
								tempRecord.HQData.market, null,
								(short) tempRecord.GroupOffset);
						if (groupRecord != null) {
							tempRecord.TradeFields = groupRecord.TradeFields;
							STD.memcpy(tempRecord.Start, groupRecord.Start, 4);
							STD.memcpy(tempRecord.End, groupRecord.End, 4);
							tempRecord.GroupFlag = groupRecord.Flag;
							// L.e(TAG," GroupInfoData marketId="+
							// aStockData.HQData.market + ",TradeFields="+
							// aStockData.TradeFields + ",Start[0]=" +
							// aStockData.Start[0] + ",End[0]=" +
							// aStockData.End[0]);
						} else {
							L.e(TAG, "ERROR: MarketInfo.search failed"
									+ ",marketId=" + tempRecord.HQData.market
									+ ",code=" + tempRecord.HQData.code);
						}
						if (tempRecord.fShuiLv <= 0.0
								&& tempRecord.market == AppConstants.HQ_MARKET_BOCE_INT) {
							float fsl = STD.StringToValue(mSHUILVIni
									.ReadString("TAXRATE", tempRecord.extCode,
											"1"));
							tempRecord.fShuiLv = fsl;
						}

						mHQData_ZQ.addRecord(tempRecord, false);
					}
				}
					break;
				default:
					break;
				}
			}
		}// while (nSize - offset >= 4)

		L.i(TAG, "End parsePushData,nCount=" + nCount);
		return nCount;
	}

	public void setHQLoginStatus(int nHQLoginStatus) {
		mHQLoginStatus = nHQLoginStatus;
	}

	public int getHQLoginStatus() {
		return mHQLoginStatus;
	}

	// get TradeQS list
	public ArrayList<TradeQSInfo> getTradeQSArray() {
		return mTradeQSList;
	}

	public int getTradeQSNum() {
		return mTradeQSList.size();
	}

	public TradeQSInfo getTradeQSInfo(int pos) {
		if (pos < 0 || pos >= getTradeQSNum())
			return null;

		return mTradeQSList.get(pos);
	}

	public void resetTradeQSArray() {
		mTradeQSList.clear();
	}

	// ////
	// get TradeQS list
	public ArrayList<TradeQSInfo> getTradeQSArray_ZQ() {
		return mTradeQSList_ZQ;
	}

	public int getTradeQSNum_ZQ() {
		return mTradeQSList_ZQ.size();
	}

	public TradeQSInfo getTradeQSInfo_ZQ(int pos) {
		if (pos < 0 || pos >= getTradeQSNum_ZQ())
			return null;

		return mTradeQSList_ZQ.get(pos);
	}

	public void resetTradeQSArray_ZQ() {
		mTradeQSList_ZQ.clear();
	}

	// ////

	// get trend line data
	public ArrayList<TagLocalTrendData> getTrendDataArray() {
		return mTrendDataArray;
	}

	// get trend line data ---------------------------------横屏
	public ArrayList<TagLocalTrendData> getLandTrendDataArray() {
		return mLandTrendDataArray;
	}

	// get trend of stock line data
	public ArrayList<TagLocalTrendData> getTrendStockDataArray() {
		return mTrendStockDataArray;
	}

	// get trend data numbers
	public int getTrendNum() {
		return mTrendDataArray.size();
	}

	// get trend data numbers --------------------------横屏
	public int getLandTrendNum() {
		return mLandTrendDataArray.size();
	}

	// get trend of stock line data
	public int getTrendStockNum() {
		return mTrendStockDataArray.size();
	}

	// get trend data with position
	public TagLocalTrendData getTrendData(int pos) {
		if (pos < 0 || pos >= getTrendNum())
			return null;

		return mTrendDataArray.get(pos);
	}

	// get trend data with position ------------------- 横屏
	public TagLocalTrendData getLandTrendData(int pos) {
		if (pos < 0 || pos >= getLandTrendNum())
			return null;

		return mLandTrendDataArray.get(pos);
	}

	// get trend stock data with position
	public TagLocalTrendData getTrendStockData(int pos) {
		if (pos < 0 || pos >= getTrendStockNum()) {
			return null;
		}
		return mTrendStockDataArray.get(pos);
	}

	// clear trend data arrays
	public void resetTrendDataArray() {
		mTrendDataArray.clear();
	}

	// clear trend data arrays ----------------------横屏
	public void resetLandTrendDataArray() {
		mLandTrendDataArray.clear();
	}

	// clear trend stock data arrays
	public void resetTrendStockDataArray() {
		mTrendStockDataArray.clear();
	}

	// get KLine data array
	public ArrayList<TagLocalKLineData> getKLineDataArray() {
		return mKLineDataArray;
	}

	// get KLine data array ----------------------横屏
	public ArrayList<TagLocalKLineData> getLandKLineDataArray() {
		return mLandKLineDataArray;
	}

	public ArrayList<TagLocalKLineData> getKLineMinArray() {
		return mKLineMinArray;
	}

	// -----------------------------横屏
	public ArrayList<TagLocalKLineData> getLandKLineMinArray() {
		return mLandKLineMinArray;
	}

	public ArrayList<TagLocalKLineData> getKLineWeekArray() {
		return mKLineWeekArray;
	}

	// ----------------------------------横屏
	public ArrayList<TagLocalKLineData> getLandKLineWeekArray() {
		return mLandKLineWeekArray;
	}

	public ArrayList<TagLocalKLineData> getKLineMonthArray() {
		return mKLineMonthArray;
	}

	// ------------------------------------横屏
	public ArrayList<TagLocalKLineData> getLandKLineMonthArray() {
		return mLandKLineMonthArray;
	}

	public int getKLineNum() {
		return mKLineDataArray.size();
	}

	// -------------------------------横屏
	public int getLandKLineNum() {
		return mLandKLineDataArray.size();
	}

	public TagLocalKLineData getKLineData(int pos) {
		if (pos < 0 || pos >= getKLineNum())
			return null;

		return mKLineDataArray.get(pos);
	}

	// -------------------------------横屏
	public TagLocalKLineData getLandKLineData(int pos) {
		if (pos < 0 || pos >= getLandKLineNum())
			return null;

		return mLandKLineDataArray.get(pos);
	}

	public void resetKLineDataArray() {
		mKLineDataArray.clear();
	}

	// -------------------------------横屏
	public void resetLandKLineDataArray() {
		mLandKLineDataArray.clear();
	}

	public void resetKLineMinArray() {
		mKLineMinArray.clear();
	}

	// -------------------------横屏
	public void resetLandKLineMinArray() {
		mLandKLineMinArray.clear();
	}

	public void resetKLineWeekArray() {
		mKLineWeekArray.clear();
	}

	// ----------------------横屏
	public void resetLandKLineWeekArray() {
		mLandKLineWeekArray.clear();
	}

	public void resetKLineMonthArray() {
		mKLineMonthArray.clear();
	}

	// ----------------------------横屏
	public void resetLandKLineMonthArray() {
		mLandKLineMonthArray.clear();
	}

	// get stock KLine data array
	public ArrayList<TagLocalKLineData> getStockKLineDataArray() {
		return mStockKLineDataArray;
	}

	public int getStockKLineNum() {
		return mStockKLineDataArray.size();
	}

	public TagLocalKLineData getStockKLineData(int pos) {
		if (pos < 0 || pos >= getStockKLineNum())
			return null;

		return mStockKLineDataArray.get(pos);
	}

	public void resetStockKLineDataArray() {
		mStockKLineDataArray.clear();
	}

	// get detail data array
	public ArrayList<TagLocalDealData> getDealDataArray() {
		return mDealDataArray;
	}

	public int getDealNum() {
		return mDealDataArray.size();
	}

	public TagLocalDealData getDealData(int pos) {
		if (pos < 0 || pos >= getDealNum())
			return null;

		return mDealDataArray.get(pos);
	}

	public void resetDealDataArray() {
		mDealDataArray.clear();
	}

	// set current stock array from option detail screen
	public void setCurrentStockArray(ArrayList<CCodeTableItem> stockArray) {
		mCurrentStockArray = stockArray;
	}

	// get current stock array from option detail screen
	public ArrayList<CCodeTableItem> getCurrentStockArray() {
		return mCurrentStockArray;
	}

	// set current option info from option detail screen
	public void setCurrentOption(TagCodeInfo option) {
		mCurrentOption = option;
	}

	// get current option info from option detail screen
	public TagCodeInfo getCurrentOption() {
		return mCurrentOption;
	}

	/**
	 * 获取自选股列表
	 * 
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized ArrayList<TagCodeInfo> getMyStockList(int hqType) {

		L.i(TAG, "getMyStockList-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ:
			return mMyStockList;
		case AppConstants.HQTYPE_ZQ:
			return mMyStockList_ZQ;
		default:
			return null;
		}
	}

	/**
	 * 自选股数
	 * 
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized int getMyStockNum(int hqType) {
		L.i(TAG, "getMyStockNum-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ:
			return mMyStockList.size();
		case AppConstants.HQTYPE_ZQ:
			return mMyStockList_ZQ.size();
		default:
			return 0;
		}
	}

	/**
	 * 拿到自选股 对象
	 * 
	 * @param index
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public TagCodeInfo getMyStock(int index, int hqType) {
		L.i(TAG, "getMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ:
			return mMyStockList.get(index);
		case AppConstants.HQTYPE_ZQ:
			return mMyStockList_ZQ.get(index);
		default:
			return null;
		}
	}

	/**
	 * 清空自选股
	 * 
	 * @param hqType
	 *            - 期权or证券
	 */
	public synchronized void clearMyStock(int hqType) {
		L.i(TAG, "clearMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ:
			mMyStockList.clear();
			break;
		case AppConstants.HQTYPE_ZQ:
			mMyStockList_ZQ.clear();
			break;
		default:
			break;
		}
	}

	/**
	 * 修改自选股列表
	 * 
	 * @param stockList
	 * @param hqType
	 *            - 期权or证券
	 * 
	 */
	public synchronized int UpdateAllMyStockList(
			ArrayList<TagCodeInfo> stockList, int hqType) {
		L.i(TAG, "UpdateAllMyStockList-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			clearMyStock(AppConstants.HQTYPE_QQ);
			for (int i = 0; i < stockList.size(); i++) {
				TagCodeInfo stock = new TagCodeInfo();
				stock.market = stockList.get(i).market;
				stock.group = stockList.get(i).group;
				stock.code = stockList.get(i).code;
				stock.name = stockList.get(i).name;
				mMyStockList.add(stock);
			}
			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			clearMyStock(AppConstants.HQTYPE_ZQ);
			for (int i = 0; i < stockList.size(); i++) {
				TagCodeInfo stock = new TagCodeInfo();
				stock.market = stockList.get(i).market;
				stock.group = stockList.get(i).group;
				stock.code = stockList.get(i).code;
				stock.name = stockList.get(i).name;
				mMyStockList_ZQ.add(stock);
			}
			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}
		return 0;
	}

	/**
	 * 修改自选股
	 * 
	 * @param index
	 * @param data
	 * @param hqType
	 *            - 期权or证券
	 */
	public synchronized void UpdateMyStock(int index, TagLocalStockData data,
			int hqType) {

		L.i(TAG, "UpdateMyStock-hqtype = " + hqType);
		if (index < 0 || index >= getMyStockNum(hqType))
			return;

		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			TagCodeInfo stock = mMyStockList.get(index);

			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			TagCodeInfo stock = mMyStockList_ZQ.get(index);

			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
		}
			break;
		default:
			break;
		}
	}

	/**
	 * 添加 成功返回0 已存在返回-1 超过最大限制 返回-2
	 * 
	 * @param data
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized int AddtoMyStock(TagLocalStockData data, int hqType) {

		L.i(TAG, "AddtoMyStock-hqtype = " + hqType);

		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			if (mMyStockList.size() >= MAXNUM_STOCK) {
				return -2;
			}
			//
			if (IsStockExist(data.code, data.market, AppConstants.HQTYPE_QQ) == true) {
				return -1;
			}

			TagCodeInfo stock = new TagCodeInfo();
			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
			mMyStockList.add(stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			if (mMyStockList_ZQ.size() >= MAXNUM_STOCK) {
				return -2;
			}
			//
			if (IsStockExist(data.code, data.market, AppConstants.HQTYPE_ZQ) == true) {
				return -1;
			}

			TagCodeInfo stock = new TagCodeInfo();
			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
			mMyStockList_ZQ.add(stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}
		return 0;
	}

	/**
	 * 添加自选股
	 * 
	 * @param data
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized int AddtoMyStock(TagCodeInfo data, int hqType) {
		L.i(TAG, "AddtoMyStock-hqtype = " + hqType);

		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			if (mMyStockList.size() >= MAXNUM_STOCK) {
				return -2;
			}
			//
			if (IsStockExist(data.code, data.market, AppConstants.HQTYPE_QQ) == true) {
				return -1;
			}
			TagCodeInfo stock = new TagCodeInfo();
			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
			mMyStockList.add(stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			if (mMyStockList_ZQ.size() >= MAXNUM_STOCK) {
				return -2;
			}
			//
			if (IsStockExist(data.code, data.market, AppConstants.HQTYPE_ZQ) == true) {
				return -1;
			}
			TagCodeInfo stock = new TagCodeInfo();
			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
			mMyStockList_ZQ.add(stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}

		return 0;
	}

	/**
	 * 往 自选股列表 添加 要保存的自选股
	 * 
	 * @param index
	 * @param data
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */

	public synchronized int InsertToMyStock(int index, TagCodeInfo data,
			int hqType) {
		L.i(TAG, "InsertToMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			if (mMyStockList.size() >= MAXNUM_STOCK) {
				return -2;
			}
			//
			if (IsStockExist(data.code, data.market, AppConstants.HQTYPE_ZQ) == true) {
				return -1;
			}
			TagCodeInfo stock = new TagCodeInfo();
			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
			mMyStockList.add(index, stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			if (mMyStockList_ZQ.size() >= MAXNUM_STOCK) {
				return -2;
			}
			//
			if (IsStockExist(data.code, data.market, AppConstants.HQTYPE_ZQ) == true) {
				return -1;
			}
			TagCodeInfo stock = new TagCodeInfo();
			stock.market = data.market;
			stock.group = data.group;
			stock.code = data.code;
			stock.name = data.name;
			mMyStockList_ZQ.add(index, stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}

		return 0;
	}

	/**
	 * 根据 传入的 股票 从 自选股列表中 删除
	 * 
	 * @param data
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized int RemoveFromMyStock(Stock data, int hqType) {
		L.i(TAG, "RemoveFromMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			int ind = -1;
			for (int i = 0; i < mMyStockList.size(); i++) {
				if (mMyStockList.get(i).code.compareTo(data.code) == 0
						&& mMyStockList.get(i).market == data.market) {
					ind = i;
					break;
				}
			}
			mMyStockList.remove(ind);
			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			int ind = -1;
			for (int i = 0; i < mMyStockList_ZQ.size(); i++) {
				if (mMyStockList_ZQ.get(i).code.compareTo(data.code) == 0
						&& mMyStockList_ZQ.get(i).market == data.market) {
					ind = i;
					break;
				}
			}
			mMyStockList_ZQ.remove(ind);
			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}

		return 0;
	}

	/**
	 * 删除全部
	 * 
	 * @param hqType
	 *            - 期权or证券
	 * 
	 * @return
	 */
	public synchronized int ClearMyStock(int hqType) {
		L.i(TAG, "ClearMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			mMyStockList.clear();
			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			mMyStockList_ZQ.clear();
			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}

		return 0;
	}

	/**
	 * 根据 传入的参数 删除
	 * 
	 * @param index
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized int RemoveFromMyStock(int index, int hqType) {
		L.i(TAG, "RemoveFromMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			mMyStockList.remove(index);
			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			mMyStockList_ZQ.remove(index);
			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}

		return 0;
	}

	/**
	 * 自选股 是否存在
	 * 
	 * @param code
	 * @param market
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public synchronized boolean IsStockExist(String code, int market, int hqType) {
		L.i(TAG, "IsStockExist-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			for (int i = 0; i < mMyStockList.size(); i++) {
				if (mMyStockList.get(i).code.compareTo(code) == 0
						&& mMyStockList.get(i).market == market)
					return true;
			}
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			for (int i = 0; i < mMyStockList_ZQ.size(); i++) {
				if (mMyStockList_ZQ.get(i).code.compareTo(code) == 0
						&& mMyStockList_ZQ.get(i).market == market)
					return true;
			}
		}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * 编辑 移动 自选股
	 * 
	 * @param from
	 * @param to
	 * @param hqType
	 *            - 期权or证券
	 */
	public synchronized void MoveMyStock(int from, int to, int hqType) {
		L.i(TAG, "MoveMyStock-hqtype = " + hqType);
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			TagCodeInfo stock = mMyStockList.get(from);
			mMyStockList.remove(from);
			mMyStockList.add(to, stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_QQ);
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			TagCodeInfo stock = mMyStockList_ZQ.get(from);
			mMyStockList_ZQ.remove(from);
			mMyStockList_ZQ.add(to, stock);

			startSaveMyStockImmediately(AppConstants.HQTYPE_ZQ);
		}
			break;
		default:
			break;
		}

	}

	/**
	 * 自选股同步 -- 自选股保存、读取 -- 文件名mystock.dat
	 * 
	 * @param time
	 * @param hqType
	 *            - 期权or证券
	 */
	public synchronized void SaveMyStockData_Ex(long time, int hqType) {
		L.i(TAG, "SaveMyStockData_Ex-hqtype = " + hqType);
		int num = 0;
		ArrayList<TagCodeInfo> tempArray = null;
		String saveFile = "";
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			num = mMyStockList.size();
			tempArray = mMyStockList;
			saveFile = MYSTOCK_PATH;
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			num = mMyStockList_ZQ.size();
			tempArray = mMyStockList_ZQ;
			saveFile = MYSTOCK_ZQ_PATH;
		}
			break;
		default:
			break;
		}

		if (tempArray == null)
			num = 0;
		L.i(TAG, "SaveMyStockData_Ex--->num = " + num);
		byte[] data = new byte[40 + num * 40];
		int offset = 0;
		// 用户名
		MyByteBuffer
				.putBytes(data, offset, mUser.getBytes(), 0, mUser.length());
		offset += 30;

		// 保存时的服务器时间
		MyByteBuffer.putLong(data, offset, time);
		offset += 8;

		// 个数
		MyByteBuffer.putShort(data, offset, (short) num);
		offset += 2;

		//
		for (int i = 0; i < num; i++) {
			//
			MyByteBuffer
					.putShort(data, offset, (short) tempArray.get(i).market);
			offset += 2;

			//
			byte[] temp = tempArray.get(i).code.getBytes();
			int len = Math.min(temp.length, Global_Define.HQ_CODE_LEN);
			MyByteBuffer.putBytes(data, offset, temp, 0, len);
			offset += Global_Define.HQ_CODE_LEN;

			temp = tempArray.get(i).name.getBytes();
			int namelen = temp.length;
			//
			MyByteBuffer.putShort(data, offset, (short) namelen);
			offset += 2;

			MyByteBuffer.putBytes(data, offset, temp, 0, namelen);
			offset += namelen;
		}
		//
		FileService file = new FileService(this.getApplicationContext());
		try {
			//
			file.saveToFile(saveFile, data, offset);
			// 保存成功
			L.i(TAG, "SaveMyStockData_Ex Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e(TAG, "SaveMyStockData_Ex Error!");
		}
	}

	/**
	 * 没有新自选股时读取老的
	 * 
	 * @param hqType
	 *            - 期权or证券，期权时读mystock.dat,证券时读mystockzq.dat
	 * @return
	 */
	public synchronized int ReadMyStockData_Ex(int hqType) {
		L.i(TAG, "ReadMyStockData_Ex-hqtype = " + hqType);

		String readFile = "";
		switch (hqType) {
		case AppConstants.HQTYPE_QQ: {
			readFile = MYSTOCK_PATH;
		}
			break;
		case AppConstants.HQTYPE_ZQ: {
			readFile = MYSTOCK_ZQ_PATH;
		}
			break;
		default:
			break;
		}

		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(readFile);
		byte[] data = new byte[size + 1];
		L.i(TAG, "ReadMyStockData_Ex -- size: " + size);

		int ret = file.readFile(readFile, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明自选股文件不存在
			return -1;
		}

		// 解析添加自选股数据
		int offset = 0;
		// 用户名
		int user_len = STD.getByteStringLen(data, offset, 30);
		String saveUser = new String(data, offset, user_len);
		offset += 30;
		// 保存时的服务器时间
		long saveTime = MyByteBuffer.getLong(data, offset);
		offset += 8;

		L.d(TAG, "ReadMyStockData_Ex -- user: " + saveUser + " time: "
				+ saveTime);
		// 个数
		int num = MyByteBuffer.getShort(data, offset);
		offset += 2;

		for (int i = 0; i < num; i++) {
			short market = MyByteBuffer.getShort(data, offset);
			offset += 2;

			int len = STD.strlen(data, offset, Global_Define.HQ_CODE_LEN);
			String code = new String(data, offset, len);
			offset += Global_Define.HQ_CODE_LEN;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] user = new byte[len];
			MyByteBuffer.getBytes(data, offset, user, 0, len);
			String name = EncodingUtils.getString(user, "UTF-8");
			offset += len;// Global_Define.HQ_NAME_LEN<<1;

			TagCodeInfo stock = new TagCodeInfo();
			stock.market = market;
			stock.code = code;
			stock.name = name;

			switch (hqType) {
			case AppConstants.HQTYPE_QQ: {
				mMyStockList.add(stock);
			}
				break;
			case AppConstants.HQTYPE_ZQ: {
				mMyStockList_ZQ.add(stock);
			}
				break;
			default:
				break;
			}
		}
		return 0;
	}

	// //////////////////////////////////

	/**
	 * 清空
	 */
	public synchronized void clearDBZQHomeMarket() {
		mDBZQHomeMenuMarkets.clear();
	}

	/**
	 * 修改首页列表
	 * 
	 * @param stockList
	 * 
	 */
	public synchronized int UpdateAllDBZQHomeMarketList(
			ArrayList<DBZQHomeActivityMarket> marketList) {
		clearDBZQHomeMarket();
		for (int i = 0; i < marketList.size(); i++) {
			DBZQHomeActivityMarket mMarket = new DBZQHomeActivityMarket();
			mMarket.Name = marketList.get(i).Name;
			mMarket.Id = marketList.get(i).Id;
			// if (pbmarket.mRules == null)
			// pbmarket.mRules = new ArrayList<Rule>();

			// pbmarket.mRules = marketList.get(i).mRules;
			mMarket.NormalIcon = marketList.get(i).NormalIcon;
			mMarket.PressIcon = marketList.get(i).PressIcon;
			mMarket.IsFixed = marketList.get(i).IsFixed;
			mMarket.IsDefault = marketList.get(i).IsDefault;
			mMarket.url = marketList.get(i).url;
			mDBZQHomeMenuMarkets.add(mMarket);
		}

		startSaveDBZQHomeMarketImmediately();
		return 0;
	}

	/**
	 * 首页市场列表 -- 保存、读取 -- 文件名homemarket.dat
	 * 
	 * @param time
	 */
	public synchronized void SaveHomeMarketData_Ex(long time) {

		int num = mDBZQHomeMenuMarkets.size();
		L.i(TAG, "SaveDBZQHomeMarketData_Ex--->num = " + num);
		byte[] data = new byte[40 + num * 200];
		int offset = 0;
		// 版本号
		MyByteBuffer.putBytes(data, offset, mDBZQMarketVersion.getBytes(), 0,
				mDBZQMarketVersion.length());
		offset += 30;

		// 保存时的服务器时间
		MyByteBuffer.putLong(data, offset, time);
		offset += 8;

		// 个数
		MyByteBuffer.putShort(data, offset, (short) num);
		offset += 2;

		//
		for (int i = 0; i < num; i++) {

			//
			byte[] temp = mDBZQHomeMenuMarkets.get(i).Name.getBytes();
			int len = temp.length;
			MyByteBuffer.putShort(data, offset, (short) len);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, len);
			offset += len;

			temp = mDBZQHomeMenuMarkets.get(i).Id.getBytes();
			int idlen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) idlen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, idlen);
			offset += idlen;

			temp = mDBZQHomeMenuMarkets.get(i).NormalIcon.getBytes();
			int norIconlen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) norIconlen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, norIconlen);
			offset += norIconlen;

			temp = mDBZQHomeMenuMarkets.get(i).PressIcon.getBytes();
			int pressIconlen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) pressIconlen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, pressIconlen);
			offset += pressIconlen;

			boolean btemp = mDBZQHomeMenuMarkets.get(i).IsFixed;
			byte value = (byte) (btemp ? 1 : 0);
			MyByteBuffer.putByte(data, offset, value);
			offset += 1;

			btemp = mDBZQHomeMenuMarkets.get(i).IsDefault;
			value = (byte) (btemp ? 1 : 0);
			MyByteBuffer.putByte(data, offset, value);
			offset += 1;

			temp = mDBZQHomeMenuMarkets.get(i).url.getBytes();
			int urllen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) urllen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, urllen);
			offset += urllen;
		}
		//
		FileService file = new FileService(this.getApplicationContext());
		try {
			//
			file.saveToFile(HOMEMARKET_PATH, data, offset);
			// 保存成功
			L.i(TAG, "SaveHomeMarketData_Ex Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e(TAG, "SaveHomeMarketData_Ex Error!");
		}
	}

	/**
	 * 读取首页保存是市场模块“homemarket.dat”
	 * 
	 * @return
	 */
	public synchronized int ReadHomeMarketData_Ex() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(HOMEMARKET_PATH);
		byte[] data = new byte[size + 1];
		L.e(TAG, "ReadHomeMarketData_Ex -- size: " + size);
		int ret = file.readFile(HOMEMARKET_PATH, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明文件不存在
			return -1;
		}

		// 解析添加数据
		int offset = 0;
		// 版本号
		int version_len = STD.getByteStringLen(data, offset, 30);
		String version = new String(data, offset, version_len);
		offset += 30;
		if (!version.equalsIgnoreCase(this.mDBZQMarketVersion)) {
			return -1;// 市场配置文件版本号不相同，说明配置文件有更改，需要重置home items
		}
		// 保存时的服务器时间
		mSaveTime_home = MyByteBuffer.getLong(data, offset);
		offset += 8;
		L.d(TAG, "ReadHomeMarketData_Ex -- user: " + mSaveUser_home + " time: "
				+ mSaveTime_home);
		// 个数
		int num = MyByteBuffer.getShort(data, offset);
		offset += 2;

		for (int i = 0; i < num; i++) {

			int len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			// name
			byte[] bytename = new byte[len];
			MyByteBuffer.getBytes(data, offset, bytename, 0, len);
			String Name = EncodingUtils.getString(bytename, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			// id
			byte[] byteid = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteid, 0, len);
			String Id = EncodingUtils.getString(byteid, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			// icon
			byte[] byteNorIcon = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteNorIcon, 0, len);
			String NormalIcon = EncodingUtils.getString(byteNorIcon, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			// press icon
			byte[] bytePressIcon = new byte[len];
			MyByteBuffer.getBytes(data, offset, bytePressIcon, 0, len);
			String PressIcon = EncodingUtils.getString(bytePressIcon, "UTF-8");
			offset += len;

			// IsFixed
			byte isFixed = MyByteBuffer.getByte(data, offset);
			offset += 1;
			boolean bFixed = (isFixed == 1) ? true : false;

			// IsDefault
			byte isDefault = MyByteBuffer.getByte(data, offset);
			offset += 1;
			boolean bDefault = (isDefault == 1) ? true : false;

			// url
			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] byteurl = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteurl, 0, len);
			String url = EncodingUtils.getString(byteurl, "UTF-8");
			offset += len;

			DBZQHomeActivityMarket mDBZQmarket = new DBZQHomeActivityMarket();
			mDBZQmarket.Id = Id;
			mDBZQmarket.Name = Name;
			mDBZQmarket.NormalIcon = NormalIcon;
			mDBZQmarket.PressIcon = PressIcon;
			mDBZQmarket.IsFixed = bFixed;
			mDBZQmarket.IsDefault = bDefault;
			mDBZQmarket.url = url;
			mDBZQHomeMenuMarkets.add(mDBZQmarket);
		}
		return 0;
	}

	/**
	 * 立即保存到本地文件
	 * 
	 * @return
	 */
	public int startSaveDBZQHomeMarketImmediately() {
		L.e(TAG, "startSaveHomeMarketImmediately");

		long time = getServerTime();
		// 保存本地
		SaveHomeMarketData_Ex(time);
		// 更新保存时间
		mSaveTime_home = time;
		// 延迟10毫秒
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 立即保存到本地文件
	 * 
	 * @param hqType
	 *            - 期权or证券
	 * @return
	 */
	public int startSaveMyStockImmediately(int hqType) {
		L.i(TAG, "startSaveMyStockImmediately-hqType = " + hqType);

		long time = getServerTime();
		// 保存本地
		SaveMyStockData_Ex(time, hqType);
		// 延迟10毫秒
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取服务器时间,目前暂时获取当前时间
	 * 
	 * @return
	 */
	public long getServerTime() {
		return STD.getCurDataTime();
	}

	// ///////////////////////////////////////////////////////

	// //////////////////////////////////
	// 定制板块相关
	public synchronized int getDZBKMarketNum() {
		return mPBMarketArray_DZBK.size();
	}

	public CPBMarket getDZBKMarket(int index) {
		return mPBMarketArray_DZBK.get(index);
	}

	public synchronized int getAllDZBKMarketNum() {
		return mPBMarketArray_DZBK_ALL.size();
	}

	public synchronized CPBMarket getDZBKMarketFromAll(int index) {
		return mPBMarketArray_DZBK_ALL.get(index);
	}

	/**
	 * 清空
	 */
	public synchronized void clearDZBKMarket() {
		mPBMarketArray_DZBK.clear();
	}

	/**
	 * 修改自选股列表
	 * 
	 * @param stockList
	 * 
	 */
	public synchronized int UpdateAllDZBKMarketList(
			ArrayList<CPBMarket> marketList) {
		clearDZBKMarket();
		for (int i = 0; i < marketList.size(); i++) {
			CPBMarket pbmarket = new CPBMarket();
			pbmarket.Name = marketList.get(i).Name;
			pbmarket.Id = marketList.get(i).Id;
			if (pbmarket.mRules == null)
				pbmarket.mRules = new ArrayList<Rule>();

			pbmarket.mRules = marketList.get(i).mRules;
			pbmarket.NormalIcon = marketList.get(i).NormalIcon;
			pbmarket.PressIcon = marketList.get(i).PressIcon;
			pbmarket.IsFixed = marketList.get(i).IsFixed;
			pbmarket.IsDefault = marketList.get(i).IsDefault;
			mPBMarketArray_DZBK.add(pbmarket);
		}

		startSaveDZBKMarketImmediately();
		return 0;
	}

	/**
	 * 添加
	 * 
	 * @param data
	 * @return
	 */
	public synchronized int AddtoDZBKMarket(CPBMarket data) {
		//
		if (mPBMarketArray_DZBK.size() >= MAXNUM_DZBKMARKET) {
			return -2;
		}
		//
		if (IsDZBKMarketExist(data.Id, data.Name) == true) {
			return -1;
		}
		CPBMarket pbmarket = new CPBMarket();
		pbmarket.Name = data.Name;
		pbmarket.Id = data.Id;
		pbmarket.mRules = data.mRules;
		pbmarket.NormalIcon = data.NormalIcon;
		pbmarket.PressIcon = data.PressIcon;
		pbmarket.IsFixed = data.IsFixed;
		pbmarket.IsDefault = data.IsDefault;
		mPBMarketArray_DZBK.add(pbmarket);

		startSaveDZBKMarketImmediately();
		return 0;
	}

	/**
	 * 是否存在
	 * 
	 * @param code
	 * @param market
	 * @return
	 */
	public synchronized boolean IsDZBKMarketExist(String id, String name) {
		for (int i = 0; i < mPBMarketArray_DZBK.size(); i++) {
			if (mPBMarketArray_DZBK.get(i).Id.compareTo(id) == 0
					&& mPBMarketArray_DZBK.get(i).Name == name)
				return true;
		}

		return false;
	}

	/**
	 * 往 列表 添加 要保存的市场
	 * 
	 * @param index
	 * @param data
	 * @return
	 */

	public synchronized int InsertToDZBKMarket(int index, CPBMarket data) {
		//
		if (mPBMarketArray_DZBK.size() >= MAXNUM_DZBKMARKET) {
			return -2;
		}
		//
		if (IsDZBKMarketExist(data.Id, data.Name) == true) {
			return -1;
		}
		CPBMarket pbmarket = new CPBMarket();
		pbmarket.Name = data.Name;
		pbmarket.Id = data.Id;
		pbmarket.mRules = data.mRules;
		pbmarket.NormalIcon = data.NormalIcon;
		pbmarket.PressIcon = data.PressIcon;
		pbmarket.IsFixed = data.IsFixed;
		pbmarket.IsDefault = data.IsDefault;
		mPBMarketArray_DZBK.add(index, pbmarket);

		startSaveDZBKMarketImmediately();
		return 0;
	}

	/**
	 * 根据 传入的市场信息 从 定制板块列表中 删除
	 * 
	 * @param data
	 * @return
	 */
	public synchronized int RemoveFromDZBKMarket(CPBMarket data) {

		int ind = -1;
		for (int i = 0; i < mPBMarketArray_DZBK.size(); i++) {
			if (mPBMarketArray_DZBK.get(i).Name.compareTo(data.Name) == 0
					&& mPBMarketArray_DZBK.get(i).Id == data.Id) {
				ind = i;
				break;
			}
		}
		mPBMarketArray_DZBK.remove(ind);

		startSaveDZBKMarketImmediately();
		return 0;
	}

	/**
	 * 定制板块市场列表 -- 保存、读取 -- 文件名dzbkmarket.dat
	 * 
	 * @param time
	 */
	public synchronized void SaveDZBKMarketData_Ex(long time) {

		int num = mPBMarketArray_DZBK.size();
		L.i("MyApp", "SaveDZBKMarketData_Ex--->num = " + num);
		byte[] data = new byte[40 + num * 200];
		int offset = 0;
		// 版本号
		MyByteBuffer.putBytes(data, offset, mPBMarketDZBKVersion.getBytes(), 0,
				mPBMarketDZBKVersion.getBytes().length);
		offset += 30;

		// 保存时的服务器时间
		MyByteBuffer.putLong(data, offset, time);
		offset += 8;

		// 个数
		MyByteBuffer.putShort(data, offset, (short) num);
		offset += 2;

		for (int i = 0; i < num; i++) {
			byte[] temp = mPBMarketArray_DZBK.get(i).Name.getBytes();
			int len = temp.length;
			MyByteBuffer.putShort(data, offset, (short) len);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, len);
			offset += len;

			temp = mPBMarketArray_DZBK.get(i).Id.getBytes();
			int idlen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) idlen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, idlen);
			offset += idlen;

			temp = mPBMarketArray_DZBK.get(i).NormalIcon.getBytes();
			int norIconlen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) norIconlen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, norIconlen);
			offset += norIconlen;

			temp = mPBMarketArray_DZBK.get(i).PressIcon.getBytes();
			int pressIconlen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) pressIconlen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, pressIconlen);
			offset += pressIconlen;

			boolean btemp = mPBMarketArray_DZBK.get(i).IsFixed;
			byte value = (byte) (btemp ? 1 : 0);
			MyByteBuffer.putByte(data, offset, value);
			offset += 1;

			btemp = mPBMarketArray_DZBK.get(i).IsDefault;
			value = (byte) (btemp ? 1 : 0);
			MyByteBuffer.putByte(data, offset, value);
			offset += 1;
		}
		FileService file = new FileService(this.getApplicationContext());
		try {
			file.saveToFile(DZBKMARKET_PATH, data, offset);
			// 保存成功
			L.i("MyApp", "SaveDZBKMarketData_Ex Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e("MyApp", "SaveDZBKMarketData_Ex Error!");
		}
	}

	/**
	 * 没有新自选股时读取老的，读取dzbkmarket.dat
	 * 
	 * @return
	 */
	public synchronized int ReadDZBKMarketData_Ex() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(DZBKMARKET_PATH);
		byte[] data = new byte[size + 1];
		L.e("MyApp", "ReadDZBKMarketData_Ex -- size: " + size);
		int ret = file.readFile(DZBKMARKET_PATH, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明文件不存在
			return -1;
		}

		// 解析添加数据
		int offset = 0;
		// 版本号
		int version_len = STD.getByteStringLen(data, offset, 30);
		String version = new String(data, offset, version_len);
		offset += 30;
		if (!version.equalsIgnoreCase(this.mPBMarketDZBKVersion)) {
			return -1;// 市场配置文件版本号不相同，说明配置文件有更改，需要重置home items
		}
		// 保存时的服务器时间
		MyByteBuffer.getLong(data, offset);
		offset += 8;

		// 个数
		int num = MyByteBuffer.getShort(data, offset);
		offset += 2;

		for (int i = 0; i < num; i++) {

			int len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] bytename = new byte[len];
			MyByteBuffer.getBytes(data, offset, bytename, 0, len);
			String Name = EncodingUtils.getString(bytename, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] byteid = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteid, 0, len);
			String Id = EncodingUtils.getString(byteid, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] byteNorIcon = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteNorIcon, 0, len);
			String NormalIcon = EncodingUtils.getString(byteNorIcon, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] bytePressIcon = new byte[len];
			MyByteBuffer.getBytes(data, offset, bytePressIcon, 0, len);
			String PressIcon = EncodingUtils.getString(bytePressIcon, "UTF-8");
			offset += len;

			byte isFixed = MyByteBuffer.getByte(data, offset);
			offset += 1;
			boolean bFixed = (isFixed == 1) ? true : false;

			byte isDefault = MyByteBuffer.getByte(data, offset);
			offset += 1;
			boolean bDefault = (isDefault == 1) ? true : false;

			CPBMarket pbmarket = new CPBMarket();
			pbmarket.Id = Id;
			pbmarket.Name = Name;
			// pbmarket.MarketIds = MarketIds;
			pbmarket.NormalIcon = NormalIcon;
			pbmarket.PressIcon = PressIcon;
			pbmarket.IsFixed = bFixed;
			pbmarket.IsDefault = bDefault;
			mPBMarketArray_DZBK.add(pbmarket);
		}
		return 0;
	}

	/**
	 * 立即保存到本地文件
	 * 
	 * @return
	 */
	public int startSaveDZBKMarketImmediately() {
		L.e("MyApp", "startSaveHomeMarketImmediately");

		long time = getServerTime();
		// 保存本地
		SaveDZBKMarketData_Ex(time);

		// 延迟10毫秒
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// ////////////////////////////

	public ArrayList<TradeAccountInfo> getTradeAccountList() {
		return mTradeAccountList;
	}

	/**
	 * 添加交易账号
	 * 
	 * @param data
	 * @return
	 */
	public synchronized int AddtoMyTradeAccount(TradeAccountInfo data, int index) {
		//
		if (mTradeAccountList.size() >= MAXNUM_TRADEACCOUNT) {
			return -2;
		}
		//
		int indexExist = GetAccountExistIndex(data.mID, data.mAccout);
		if (indexExist == 0) {
			return -1;
		}
		TradeAccountInfo accountInfo = new TradeAccountInfo();
		accountInfo.mName = data.mName;
		accountInfo.mAccout = data.mAccout;
		accountInfo.mID = data.mID;
		accountInfo.mAccoutType = data.mAccoutType;
		if (indexExist > 0 && indexExist < mTradeAccountList.size()) {
			mTradeAccountList.remove(indexExist);
		}
		if (index >= 0 && index < mTradeAccountList.size()) {
			mTradeAccountList.add(index, accountInfo);
		} else {
			mTradeAccountList.add(accountInfo);
		}

		startSaveMyTradeAccountImmediately();
		return 0;
	}

	/**
	 * 根据 传入的 股票 从 自选股列表中 删除
	 * 
	 * @param data
	 * @return
	 */
	public synchronized int RemoveFromMyTradeAccount(TradeAccountInfo data) {

		int index = -1;
		for (int i = 0; i < mTradeAccountList.size(); i++) {
			if (mTradeAccountList.get(i).mID.equals(data.mID)
					&& mTradeAccountList.get(i).mAccout.equals(data.mAccout)) {
				index = i;
				break;
			}
		}
		mTradeAccountList.remove(index);

		startSaveMyTradeAccountImmediately();
		return 0;
	}

	/**
	 * 删除全部
	 * 
	 * @return
	 */
	public synchronized int ClearMyTradeAccount() {
		mTradeAccountList.clear();

		startSaveMyTradeAccountImmediately();
		return 0;
	}

	/**
	 * 根据 传入的参数 删除
	 * 
	 * @param index
	 * @return
	 */
	public synchronized int RemoveFromMyTradeAccount(int index) {

		if (index >= 0 && index < mTradeAccountList.size()) {
			mTradeAccountList.remove(index);
			startSaveMyTradeAccountImmediately();
		}
		return 0;
	}

	/**
	 * 账号是否已经保存
	 * 
	 * @param id
	 * @param account
	 * @return
	 */
	public synchronized int GetAccountExistIndex(String id, String account) {
		for (int i = 0; i < mTradeAccountList.size(); i++) {
			if (mTradeAccountList.get(i).mID.equals(id)
					&& mTradeAccountList.get(i).mAccout.equals(account)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 立即保存到本地文件
	 * 
	 * @return
	 */
	public int startSaveMyTradeAccountImmediately() {
		L.e(TAG, "startSaveMyTradeAccountImmediately");

		// 保存本地
		SaveMyTradeAccountData_Ex();
		// 延迟10毫秒
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 保存交易账号 -- 文件名tradeaccount.dat
	 * 
	 * 
	 */
	public synchronized void SaveMyTradeAccountData_Ex() {

		int num = mTradeAccountList.size();
		L.d(TAG, "SaveMyTradeAccountData_Ex--->num = " + num);
		byte[] data = new byte[2 + num * 128];
		int offset = 0;

		// 个数
		MyByteBuffer.putShort(data, offset, (short) num);
		offset += 2;

		//
		for (int i = 0; i < num; i++) {
			//
			byte[] temp = mTradeAccountList.get(i).mName.getBytes();
			int templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;

			temp = mTradeAccountList.get(i).mID.getBytes();
			templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;

			temp = mTradeAccountList.get(i).mAccout.getBytes();
			templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;

			temp = mTradeAccountList.get(i).mAccoutType.getBytes();
			templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;
		}
		//
		FileService file = new FileService(this.getApplicationContext());
		try {
			//
			file.saveToFile(TRADE_ACCOUNT_PATH, data, offset);
			// 保存成功
			L.d(TAG, "SaveMyTradeAccountData_Ex Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e(TAG, "SaveMyTradeAccountData_Ex Error!");
		}
	}

	/**
	 * 读取交易账号保存文件tradeaccount.dat
	 * 
	 * @return
	 */
	public synchronized int ReadMyTradeAccountData_Ex() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(TRADE_ACCOUNT_PATH);
		byte[] data = new byte[size + 1];
		L.d(TAG, "ReadMyTradeAccountData_Ex -- size: " + size);
		int ret = file.readFile(TRADE_ACCOUNT_PATH, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明文件不存在
			return -1;
		}

		// 解析添加数据
		int offset = 0;
		// 个数
		int num = MyByteBuffer.getShort(data, offset);
		offset += 2;
		L.d(TAG, "ReadMyTradeAccountData_Ex -- Num: " + num);

		for (int i = 0; i < num; i++) {
			int len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] byteName = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteName, 0, len);
			String name = EncodingUtils.getString(byteName, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;
			byte[] byteId = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteId, 0, len);
			String id = EncodingUtils.getString(byteId, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;
			byte[] byteAccount = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteAccount, 0, len);
			String account = EncodingUtils.getString(byteAccount, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;
			byte[] byteAccountType = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteAccountType, 0, len);
			String accountType = EncodingUtils.getString(byteAccountType,
					"UTF-8");
			offset += len;

			TradeAccountInfo info = new TradeAccountInfo();
			info.mName = name;
			info.mID = id;
			info.mAccout = account;
			info.mAccoutType = accountType;
			mTradeAccountList.add(info);
		}
		return 0;
	}

	public ArrayList<TradeAccountInfo> getTradeAccountList_ZQ() {
		return mTradeAccountList_ZQ;
	}

	/**
	 * 添加交易账号
	 * 
	 * @param data
	 * @return
	 */
	public synchronized int AddtoMyTradeAccount_ZQ(TradeAccountInfo data,
			int index) {
		//
		if (mTradeAccountList_ZQ.size() >= MAXNUM_TRADEACCOUNT) {
			return -2;
		}
		//
		int indexExist = GetAccountExistIndex_ZQ(data.mID, data.mAccout);
		if (indexExist == 0) {
			return -1;
		}
		TradeAccountInfo accountInfo = new TradeAccountInfo();
		accountInfo.mName = data.mName;
		accountInfo.mAccout = data.mAccout;
		accountInfo.mID = data.mID;
		accountInfo.mAccoutType = data.mAccoutType;
		if (indexExist > 0 && indexExist < mTradeAccountList_ZQ.size()) {
			mTradeAccountList_ZQ.remove(indexExist);
		}
		if (index >= 0 && index < mTradeAccountList_ZQ.size()) {
			mTradeAccountList_ZQ.add(index, accountInfo);
		} else {
			mTradeAccountList_ZQ.add(accountInfo);
		}

		startSaveMyTradeAccountImmediately_ZQ();
		return 0;
	}

	/**
	 * 根据 传入的 账号从保存列表里删除
	 * 
	 * @param data
	 * @return
	 */
	public synchronized int RemoveFromMyTradeAccount_ZQ(TradeAccountInfo data) {

		int index = -1;
		for (int i = 0; i < mTradeAccountList_ZQ.size(); i++) {
			if (mTradeAccountList_ZQ.get(i).mID.equals(data.mID)
					&& mTradeAccountList_ZQ.get(i).mAccout.equals(data.mAccout)) {
				index = i;
				break;
			}
		}
		mTradeAccountList_ZQ.remove(index);

		startSaveMyTradeAccountImmediately_ZQ();
		return 0;
	}

	/**
	 * 删除全部
	 * 
	 * @return
	 */
	public synchronized int ClearMyTradeAccount_ZQ() {
		mTradeAccountList_ZQ.clear();

		startSaveMyTradeAccountImmediately_ZQ();
		return 0;
	}

	/**
	 * 根据 传入的参数 删除
	 * 
	 * @param index
	 * @return
	 */
	public synchronized int RemoveFromMyTradeAccount_ZQ(int index) {

		if (index >= 0 && index < mTradeAccountList_ZQ.size()) {
			mTradeAccountList_ZQ.remove(index);
			startSaveMyTradeAccountImmediately_ZQ();
		}
		return 0;
	}

	/**
	 * 账号是否已经保存
	 * 
	 * @param id
	 * @param account
	 * @return
	 */
	public synchronized int GetAccountExistIndex_ZQ(String id, String account) {
		for (int i = 0; i < mTradeAccountList_ZQ.size(); i++) {
			if (mTradeAccountList_ZQ.get(i).mID.equals(id)
					&& mTradeAccountList_ZQ.get(i).mAccout.equals(account)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 立即保存到本地文件
	 * 
	 * @return
	 */
	public int startSaveMyTradeAccountImmediately_ZQ() {
		L.e(TAG, "startSaveMyTradeAccountImmediately_ZQ");

		// 保存本地
		SaveMyTradeAccountData_ZQ();
		// 延迟10毫秒
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 保存交易账号 -- 文件名tradeaccountzq.dat
	 * 
	 * 
	 */
	public synchronized void SaveMyTradeAccountData_ZQ() {

		int num = mTradeAccountList_ZQ.size();
		L.d(TAG, "SaveMyTradeAccountData_ZQ--->num = " + num);
		byte[] data = new byte[2 + num * 128];
		int offset = 0;

		// 个数
		MyByteBuffer.putShort(data, offset, (short) num);
		offset += 2;

		//
		for (int i = 0; i < num; i++) {
			//
			byte[] temp = mTradeAccountList_ZQ.get(i).mName.getBytes();
			int templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;

			temp = mTradeAccountList_ZQ.get(i).mID.getBytes();
			templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;

			temp = mTradeAccountList_ZQ.get(i).mAccout.getBytes();
			templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;

			temp = mTradeAccountList_ZQ.get(i).mAccoutType.getBytes();
			templen = temp.length;
			MyByteBuffer.putShort(data, offset, (short) templen);
			offset += 2;
			MyByteBuffer.putBytes(data, offset, temp, 0, templen);
			offset += templen;
		}
		//
		FileService file = new FileService(this.getApplicationContext());
		try {
			//
			file.saveToFile(TRADE_ACCOUNT_ZQ_PATH, data, offset);
			// 保存成功
			L.d(TAG, "SaveMyTradeAccountData_ZQ Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e(TAG, "SaveMyTradeAccountData_ZQ Error!");
		}
	}

	/**
	 * 读取交易账号保存文件tradeaccountzq.dat
	 * 
	 * @return
	 */
	public synchronized int ReadMyTradeAccountData_ZQ() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(TRADE_ACCOUNT_ZQ_PATH);
		byte[] data = new byte[size + 1];
		L.d(TAG, "ReadMyTradeAccountData_ZQ -- size: " + size);
		int ret = file.readFile(TRADE_ACCOUNT_ZQ_PATH, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明文件不存在
			return -1;
		}

		// 解析添加数据
		int offset = 0;
		// 个数
		int num = MyByteBuffer.getShort(data, offset);
		offset += 2;
		L.d(TAG, "ReadMyTradeAccountData_ZQ -- Num: " + num);

		for (int i = 0; i < num; i++) {
			int len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] byteName = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteName, 0, len);
			String name = EncodingUtils.getString(byteName, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;
			byte[] byteId = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteId, 0, len);
			String id = EncodingUtils.getString(byteId, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;
			byte[] byteAccount = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteAccount, 0, len);
			String account = EncodingUtils.getString(byteAccount, "UTF-8");
			offset += len;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;
			byte[] byteAccountType = new byte[len];
			MyByteBuffer.getBytes(data, offset, byteAccountType, 0, len);
			String accountType = EncodingUtils.getString(byteAccountType,
					"UTF-8");
			offset += len;

			TradeAccountInfo info = new TradeAccountInfo();
			info.mName = name;
			info.mID = id;
			info.mAccout = account;
			info.mAccoutType = accountType;
			mTradeAccountList_ZQ.add(info);
		}
		return 0;
	}

	// 取得mac地址
	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String ret = "";
		ret = info.getMacAddress();
		if (ret == null) {
			ret = "";
		}
		try {
			ret = ret.replaceAll(":", "");
		} catch (Exception e) {
			ret = info.getMacAddress();
		}
		return ret;
	}

	// 取得IP地址
	public String getLocalIpAddress() {
		boolean useIPv4 = true;
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} // for now eat exceptions
		return null;
	}

	public ArrayList<SearchDataItem> getSearchCodeArray() {
		if (mSearchCodeArray == null) {
			updateSearchCodeArray();
		}
		return mSearchCodeArray;
	}

	public ArrayList<SearchDataItem> getSearchTradeCodeArray() {
		if (mSearchTradeCodeArray == null) {
			updateSearchTradeCodeArray();
		}
		return mSearchTradeCodeArray;
	}

	public void updateSearchCodeArray() {
		if (mSearchCodeArray == null) {
			mSearchCodeArray = new ArrayList<SearchDataItem>();
		}
		mSearchCodeArray.clear();

		for (int i = 0; i < this.mCodeTableMarketNum
				&& i < CDataCodeTable.MAX_SAVE_MARKET_COUNT; i++) {
			int nMarket = this.mCodeTableMarket[i];

			if (nMarket != HQ_Define.HQ_MARKET_SH_INT
					&& nMarket != HQ_Define.HQ_MARKET_SZ_INT) {
				continue;
			}

			ArrayList<CCodeTableItem> codeTables = this.mCodeTable[i]
					.getData(nMarket);
			if (codeTables != null) {
				for (int j = 0; j < codeTables.size(); j++) {
					CCodeTableItem item = codeTables.get(j);
					String strName = item.name;
					String strJP = "";
					for (int k = 0; k < strName.length(); k++) {
						char ch = strName.charAt(k);
						if (ch >= '0' && ch <= '9') {
							strJP += ch;
						} else if (ch >= 'a' && ch <= 'z') {
							strJP += ch;
						} else if (ch >= 'A' && ch <= 'Z') {
							strJP += ch;
						} else if (ch == '+') {

						} else {
							char firstStr = (char) (PinYin
									.pinyinFirstLetter(ch) - 32);
							strJP += firstStr;
						}
					}

					SearchDataItem searchItem = new SearchDataItem();
					searchItem.name = item.name;
					searchItem.code = item.code;
					searchItem.group = item.group;
					searchItem.market = item.market;
					searchItem.jianpin = strJP;
					searchItem.extcode = item.extCode;
					mSearchCodeArray.add(searchItem);
				}
			}
		}
	}

	public void updateSearchTradeCodeArray() {
		if (mSearchTradeCodeArray == null) {
			mSearchTradeCodeArray = new ArrayList<SearchDataItem>();
		}
		mSearchTradeCodeArray.clear();

		for (int i = 0; i < this.mCodeTableMarketNum
				&& i < CDataCodeTable.MAX_SAVE_MARKET_COUNT; i++) {
			int nMarket = this.mCodeTableMarket[i];

			if (nMarket != HQ_Define.HQ_MARKET_SH_INT
					&& nMarket != HQ_Define.HQ_MARKET_SZ_INT)
				continue;
			ArrayList<CCodeTableItem> codeTables = this.mCodeTable[i]
					.getData(nMarket);
			if (codeTables != null) {
				for (int j = 0; j < codeTables.size(); j++) {
					CCodeTableItem item = codeTables.get(j);

					if (item.GroupFlag == HQ_Define.GRPATTR_INDEX
							|| item.GroupFlag == HQ_Define.GRPATTR_BLKIDX
							|| item.GroupFlag == HQ_Define.GRPATTR_IDXFUT)
						continue;
					String strName = item.name;
					String strJP = "";
					for (int k = 0; k < strName.length(); k++) {
						char ch = strName.charAt(k);
						if (ch >= '0' && ch <= '9') {
							strJP += ch;
						} else if (ch >= 'a' && ch <= 'z') {
							strJP += ch;
						} else if (ch >= 'A' && ch <= 'Z') {
							strJP += ch;
						} else if (ch == '+') {

						} else {
							char firstStr = (char) (PinYin
									.pinyinFirstLetter(ch) - 32);
							strJP += firstStr;
						}
					}

					SearchDataItem searchItem = new SearchDataItem();
					searchItem.name = item.name;
					searchItem.code = item.code;
					searchItem.group = item.group;
					searchItem.market = item.market;
					searchItem.jianpin = strJP;
					searchItem.extcode = item.extCode;
					mSearchTradeCodeArray.add(searchItem);
				}
			}
		}
	}

	public String getIMEI() {
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		String ret = tm.getDeviceId();
		if (ret == null) {
			ret = "";
		}
		return ret;
	}

	public boolean IsAPPNeedUpdate() {
		boolean ret = false;
		String nowVersion = PreferenceEngine.getInstance().getVersionCode();
		String lastVersion = this.mNewestVersion;
		int nVer_Now = STD.StringToInt(nowVersion);
		int nVer_Last = STD.StringToInt(lastVersion);
		if (nVer_Now < nVer_Last) {
			ret = true;
		}
		return ret;
	}

	public void doStartApplicationWithPackageName(Context context,
			String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

}
