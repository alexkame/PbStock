package com.pengbo.mhdzq.net;

import java.util.ArrayList;

import com.pengbo.mhdzq.common.StringUtils;
import com.pengbo.mhdzq.data.CHQStep;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.Stock;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.SSLEncrypt;
import com.pengbo.mhdzq.data.ZLibTools;
import com.pengbo.mhdzq.tools.L;

/**
 * 个 模块请求 类
 * 
 * @author pobo
 * 
 */
public class GlobalNetProgress {
	/**
	 * 获取类的简写 名称
	 */
	public final static String TAG = GlobalNetProgress.class.getSimpleName();
	/**
	 * 请求号
	 */
	private static int mRequestCode = 0;

	/**
	 * @return mRequestCode（请求号）
	 */
	public static int MakeRequestCode() {
		mRequestCode++;
		mRequestCode &= 0x3fff;
		if (mRequestCode == 0) {
			mRequestCode = 1;
		}
		return mRequestCode;
	}

	/**
	 * 提供一个 无参的构造 函数
	 */

	public GlobalNetProgress() {
	}

	/**
	 * 行情登录接口
	 * 
	 * @param version
	 *            软件内部版本号
	 * @param account
	 *            账号
	 * @param pwd
	 *            密码
	 * @param loginId
	 *            认证服务器分配给客户的识别ID，当 bPushConnnect=true时有效
	 * @param bPushConnnect
	 *            是否是推送连接
	 * @return 请求号
	 */
	public static int RequestMake_Login(GlobalNetConnect netClass,
			String version, String account, String pwd, int loginId,
			boolean bPushConnnect, byte[] outdata, int size) {
		return 0;
	}

	public static int HQRequest_Login(GlobalNetConnect netClass,
			String version, String account, String pwd, int loginId, int orgId,
			boolean bPushConnnect) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];

		CHQStep lineStep = new CHQStep();
		if (version == null || version.isEmpty()) {
			version = "1";
		}
		lineStep.AddFieldString(Global_Define.HFD_Login_SoftVersion, version);
		lineStep.AddFieldString(Global_Define.HFD_Login_Account, account);
		String szTemp = StringUtils.MD5Encode(pwd);
		lineStep.AddFieldString(Global_Define.HFD_Login_Password, szTemp);
		lineStep.AddFieldInt(Global_Define.HFD_Login_ProtocolVer, 1511);
		lineStep.AddFieldInt(Global_Define.HFD_Login_OrganizationId, orgId);

		if (bPushConnnect) {
			lineStep.AddFieldInt(Global_Define.HFD_Login_LoginType, 1);
			lineStep.AddFieldInt(Global_Define.HFD_Login_PushId, loginId);
		} else {
			lineStep.AddFieldInt(Global_Define.HFD_Login_LoginType, 0);
			lineStep.AddFieldInt(Global_Define.HFD_Login_PushId, 0);
		}

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		L.e("MyApp", "HQRequest_Login lineStep[" + size + "]:"
				+ new String(data, headsize, size));
		if (size >= 0) {
			// [180 MFT_MOBILE_LOGIN_APPLY]
			int nEncrypt = 0;
			if (bPushConnnect == false)
				nEncrypt = 1;
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_LOGIN_APPLY, nRequestCode,
					data, 0, size + headsize, nEncrypt, 0);
		}
		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 请求自选期权列表
	 * 
	 * @param netclass
	 * @param optionlist
	 *            传入要查询的期权的集合
	 * @param start
	 * @param num
	 *            查询自选中期权的 个数
	 * @return
	 */
	public static int RequestOptionalOptionList(GlobalNetConnect netclass,
			ArrayList<Stock> optionlist, int start, int num) {
		// 多行情数据 有请求
		return num;
	}

	/**
	 * 查询一个市场的期权信息 MFT_MOBILE_DATA_APPLY RequestType=5
	 * 
	 * @param netclass
	 * @param nMarket
	 *            查询整个市场
	 * @param nMarket2
	 *            支持第二个市场
	 * @return
	 */
	public static int HQRequest_OptionInfo(GlobalNetConnect netClass,
			int nMarket, int nMarket2) {

		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_RequestType, 5);

		int iCount = 1;
		if (nMarket2 > 0) {
			iCount = 2;
		}
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_ReqCount, iCount);
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_MarketId, nMarket);
		if (nMarket2 > 0) {
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_MarketId, nMarket2);
		}

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;

	}

	/**
	 * 查询给定标的信息 MFT_MOBILE_DATA_APPLY RequestType=1
	 * 
	 * @param netclass
	 * @param codelist
	 *            传入要查询的期权的集合
	 * @param start
	 * @param nCodeCount
	 *            查询期权的 个数
	 * @return
	 */
	public static int HQRequest_StockInfo(GlobalNetConnect netClass,
			ArrayList<TagCodeInfo> codelist, int start, int nCodeCount) {

		if (nCodeCount <= 0)
			return -1;

		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_RequestType, 1);
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_ReqCount, nCodeCount);
		for (int i = 0; i < nCodeCount; i++) {
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_MarketId,
					codelist.get(start + i).market);
			lineStep.AddFieldString(Global_Define.HFD_APPLY_5_Code,
					codelist.get(start + i).code);
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_GroupId,
					codelist.get(start + i).group);
		}

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 查询股票基础数据 MFT_MOBILE_DATA_APPLY RequestType=2
	 * 
	 * @param netclass
	 * @param codelist
	 *            传入要查询的期权的集合
	 * @param start
	 * @param nCodeCount
	 *            查询期权的 个数
	 * @return
	 */
	public static int HQRequest_ZQStockInfo(GlobalNetConnect netClass,
			ArrayList<TagCodeInfo> codelist, int start, int nCodeCount) {

		if (nCodeCount <= 0)
			return -1;

		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_RequestType, 2);
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_ReqCount, nCodeCount);
		for (int i = 0; i < nCodeCount; i++) {
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_MarketId,
					codelist.get(start + i).market);
			lineStep.AddFieldString(Global_Define.HFD_APPLY_5_Code,
					codelist.get(start + i).code);
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_5_GroupId,
					codelist.get(start + i).group);
		}

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 查询市场信息
	 * 
	 * @param netClass
	 *            网络连接
	 * @param date
	 *            市场文件修改日期 int yyyymmdd
	 * @param time
	 *            市场文件修改时间 int hhmmss
	 * @return 请求号
	 */
	public static int HQRequest_MarketInfo(GlobalNetConnect netClass, int date,
			int time, int protocolVer) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_MarketQuery_Right, 1);
		lineStep.AddFieldInt(Global_Define.HFD_MarketQuery_ModifyDate, date);
		lineStep.AddFieldInt(Global_Define.HFD_MarketQuery_ModifyTime, time);
		lineStep.AddFieldInt(Global_Define.HFD_MarketQuery_ProtocolVer,
				protocolVer);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [181 MFT_MOBILE_MARKET_INFO_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_MARKET_INFO_APPLY,
					nRequestCode, data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 查询全局信息
	 * 
	 * @param netClass
	 *            网络连接
	 * @return 请求号
	 */
	public static int HQRequest_GlobalParamInfo(GlobalNetConnect netClass) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(1, 999);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183 MFT_MOBILE_DATA_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 查询码表
	 * 
	 * @param netClass
	 *            网络连接
	 * @param date
	 *            客户端已有码表日期 int yyyymmdd
	 * @param time
	 *            客户端已有码表时间 int hhmmss
	 * @param marketId
	 *            请求市场编号 short
	 * @param startNO
	 *            请求起始序号 short
	 * @param reqCount
	 *            请求品种个数 short 填0表示取市场所有代码
	 * @param groupCount
	 *            市场品种个数 short 用户本地无码表时可填0
	 * @param tableCRC
	 *            码表信息校验和
	 * @param updateFlag
	 *            0-全量，缺省 1-增量
	 * @return 请求号
	 */
	public static int HQRequest_CodeTable(GlobalNetConnect netClass, int date,
			int time, short marketId, short startNO, short reqCount,
			short groupCount, short tableCRC, short updateFlag) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_RequestType, 15);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_LocalDate, date);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_LocalTime, time);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_MarketId, marketId);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_StartNO, startNO);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_ReqCount, reqCount);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_GroupCount, groupCount);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_TableCRC, tableCRC);
		lineStep.AddFieldInt(Global_Define.HFD_CodeTable_UpdateFlag, updateFlag);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183 MFT_MOBILE_DATA_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 查询多只股票行情信息(快照模式)，快照查询， cFrameType = MFT_MOBILE_DATA_APPLY RequestType=10
	 * 
	 * @param netclass
	 * @param codelist
	 *            传入要查询的期权的集合
	 * @param start
	 * @param nCodeCount
	 *            查询期权的 个数
	 * @param nMarket
	 *            若nMarket大于0，查询整个市场
	 * @param nMarket2
	 *            第二个市场，为了支持深圳市场
	 * @return
	 */
	public static int HQRequest_MultiCodeInfo(GlobalNetConnect netClass,
			ArrayList<TagCodeInfo> codelist, int start, int nCodeCount,
			int nMarket, int nMarket2) {

		int nTotalCount = nCodeCount;
		if (nMarket > 0) {
			nTotalCount += 1;
		}
		if (nMarket2 > 0) {
			nTotalCount += 1;
		}
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_10_RequestType, 10);
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_10_ReqCount, nTotalCount);
		if (nMarket > 0) {
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_10_MarketId, nMarket);
		}
		if (nMarket2 > 0) {
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_10_MarketId, nMarket2);
		}
		for (int i = 0; i < nCodeCount; i++) {
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_10_MarketId,
					codelist.get(start + i).market);
			lineStep.AddFieldString(Global_Define.HFD_APPLY_10_Code,
					codelist.get(start + i).code);
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_10_GroupId,
					codelist.get(start + i).group);
		}

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;

	}

	/**
	 * 查询多只股票行情信息(推送模式) cFrameType = MFT_MOBILE_PUSH_DATA
	 * 
	 * @param netclass
	 * @param codelist
	 *            传入要查询的期权的集合
	 * @param start
	 * @param nCodeCount
	 *            查询期权的 个数
	 * @return
	 */
	public static int HQRequest_MultiCodeInfoPush(GlobalNetConnect netClass,
			ArrayList<TagCodeInfo> codelist, int start, int nCodeCount) {

		// if(nCodeCount == 0 || codelist.size() == 0)return -1;

		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();
		lineStep.AddFieldInt(Global_Define.HFD_Push_ReqCount, nCodeCount);

		for (int i = 0; i < nCodeCount && codelist != null
				&& i < codelist.size(); i++) {
			lineStep.AddFieldInt(Global_Define.HFD_Push_MarketId,
					codelist.get(start + i).market);
			lineStep.AddFieldString(Global_Define.HFD_Push_Code,
					codelist.get(start + i).code);
			lineStep.AddFieldInt(Global_Define.HFD_Push_GroupId,
					codelist.get(start + i).group);
		}

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [184 MFT_MOBILE_PUSH_DATA]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_PUSH_DATA, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, true);
		return nRequestCode;

	}

	/**
	 * 查询走势 cFrameType = MFT_MOBILE_DATA_APPLY RequestType=11
	 * 
	 * @param netclass
	 * @param code
	 *            //品种代码 string
	 * @param marketId
	 *            //市场编号 short
	 * @param date
	 *            //查询日期 int yyyymmdd，0为查询当日
	 * @param dateType
	 *            //查询类型 unsigned char 0表示查询日期当天的数据，1表示查询日期之前最近有数据的一天数据
	 * @param startMinute
	 *            //起始分钟 short hhmm，查询该分钟开始的走势，包括该分钟
	 * @return
	 */
	public static int HQRequest_TrendLine(GlobalNetConnect netClass,
			String code, short marketId, int date, short dateType,
			short startMinute) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();

		lineStep.AddFieldInt(Global_Define.HFD_Trend_RequestType, 11);
		lineStep.AddFieldInt(Global_Define.HFD_Trend_Date, date);
		lineStep.AddFieldInt(Global_Define.HFD_Trend_DateType, dateType);
		lineStep.AddFieldInt(Global_Define.HFD_Trend_MarketId, marketId);
		lineStep.AddFieldString(Global_Define.HFD_Trend_Code, code);
		lineStep.AddFieldInt(Global_Define.HFD_Trend_StartMinute, startMinute);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183 MFT_MOBILE_DATA_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;

	}

	/**
	 * 查询k线 cFrameType = MFT_MOBILE_DATA_APPLY RequestType=13
	 * 
	 * @param netclass
	 * @param KLineType
	 *            //K线类型 unsigned char 0-日线，1-1分钟线，2-5分钟线，3-60分钟线
	 * @param code
	 *            //品种代码 string
	 * @param marketId
	 *            //市场编号 short
	 * @param startDate
	 *            //起始日期 int yyyymmdd，包括当日
	 * @param startTime
	 *            //起始时间 int hhmmss
	 * @param endDate
	 *            //终止日期 int yyyymmdd，包括当日
	 * @param endTime
	 *            //终止时间 int hhmmss
	 * @param reqCount
	 *            //请求根数 short
	 *            起止时间均不为0时无效，否则以有效的那个时间为基准请求指定根数K线，若两个日期均为0，以当前时间为基准请求指定根数K线
	 * @param fqType
	 *            //复权类型 unsigned char 0-不复权，1-前复权，2-后复权
	 * @return
	 */
	public static int HQRequest_KLine(GlobalNetConnect netClass,
			short KLineType, String code, short marketId, int startDate,
			int startTime, int endDate, int endTime, int reqCount, int fqType) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();

		lineStep.AddFieldInt(Global_Define.HFD_KLineRequestType, 13);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_KLineType, KLineType);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_MarketId, marketId);
		lineStep.AddFieldString(Global_Define.HFD_KLine_Code, code);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_StartDate, startDate);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_StartTime, startTime);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_EndDate, endDate);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_EndTime, endTime);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_ReqCount, reqCount);
		lineStep.AddFieldInt(Global_Define.HFD_KLine_FQType, fqType);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183 MFT_MOBILE_DATA_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;

	}

	/**
	 * 查询明细 cFrameType = MFT_MOBILE_DATA_APPLY RequestType=12
	 * 
	 * @param netclass
	 * @param code
	 *            //品种代码 string
	 * @param marketId
	 *            //市场编号 short
	 * @param startNO
	 *            //起始笔数 int -1表示从最新的一笔往前查
	 * @param reqCount
	 *            //查询总笔数 int 0表示查询当日所有的，最大600笔
	 * @return
	 */
	public static int HQRequest_Detail(GlobalNetConnect netClass, String code,
			short marketId, int startNO, int reqCount) {

		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();

		lineStep.AddFieldInt(Global_Define.HFD_Detail_RequestType, 12);
		lineStep.AddFieldInt(Global_Define.HFD_Detail_MarketId, marketId);
		lineStep.AddFieldString(Global_Define.HFD_Detail_Code, code);
		lineStep.AddFieldInt(Global_Define.HFD_Detail_StartNO, startNO);
		lineStep.AddFieldInt(Global_Define.HFD_Detail_ReqCount, reqCount);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183 MFT_MOBILE_DATA_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	// 手机端系统配置下载 MFT_MOBILE_SYSCONFIG_APPLY
	public static int HQRequest_SYSCONFIG(GlobalNetConnect netClass,
			String fileName, int fileSize, int date, int time, int jgId) {
		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();

		lineStep.AddFieldString(Global_Define.HFD_UpdateConfig_FileName,
				fileName);
		lineStep.AddFieldInt(Global_Define.HFD_UpdateConfig_FileSize, fileSize);
		lineStep.AddFieldInt(Global_Define.HFD_UpdateConfig_Date, date);
		lineStep.AddFieldInt(Global_Define.HFD_UpdateConfig_time, time);
		lineStep.AddFieldInt(Global_Define.HFD_UpdateConfig_OrganizationId,
				jgId);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [182 MFT_MOBILE_SYSCONFIG_APPLY]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_SYSCONFIG_APPLY,
					nRequestCode, data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 排名查询， cFrameType = MFT_MOBILE_DATA_APPLY RequestType=18
	 * 
	 * @param netclass
	 * @param range
	 *            排名范围 （ 80 - 沪深A股 81 - 沪A股82 - 沪B股83 - 深A股84 - 深B股85 - 沪债86 -
	 *            深债87 - 中小板88 - 沪深B股89 - 沪深权证90 - 创业板91 - 个股期权缺省 - 沪深A）
	 * @param fieldCount
	 *            排名字段数
	 * @param filed
	 *            [] 排名字段 （0 - 当日涨幅1 - 5分钟涨幅2 - 当日委比涨3 - 当日振幅4 - 量比5 - 总金额101 -
	 *            当日跌幅102 - 5分钟跌幅103 - 当日委比跌）
	 * @param count
	 *            请求排名个数
	 * @return
	 */
	public static int HQRequest_PaiMing(GlobalNetConnect netClass, short range,
			short fieldCount, short[] field, int count) {

		byte[] data = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE];
		CHQStep lineStep = new CHQStep();

		lineStep.AddFieldInt(Global_Define.HFD_APPLY_18_RequestType, 18);
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_18_Range, range);
		lineStep.AddFieldInt(Global_Define.HFD_APPLY_18_FieldCount, fieldCount);
		for (int i = 0; i < field.length; i++) {
			if (field[i] == -1)
				continue;
			lineStep.AddFieldInt(Global_Define.HFD_APPLY_18_Field, field[i]);
		}

		lineStep.AddFieldInt(Global_Define.HFD_APPLY_18_Count, count);

		int nRequestCode = MakeRequestCode();
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		int size = lineStep.GetData(data, headsize, data.length - headsize);
		if (size >= 0) {
			// [183]
			size = MakeEncryptPackage(netClass.mSessionID,
					(byte) Global_Define.MFT_MOBILE_DATA_APPLY, nRequestCode,
					data, 0, size + headsize, 0, 0);
		}

		netClass.addSendData(data, 0, size, false);
		return nRequestCode;
	}

	/**
	 * 创建心跳包 cFrameType = MFT_KEEP_ALIVE
	 * 
	 * @return 心跳包大小
	 */
	public static int RequestMake_HeartBeat(byte[] data, int size) {
		int headsize = GlobalNetConnect.MC_FrameHead_LEN;
		if (size < headsize) {
			return 0;
		}
		size = MakeEncryptPackage(0, (byte) Global_Define.MFT_KEEP_ALIVE, 0,
				data, 0, headsize, 0, 0);

		return size;
	}

	/********** 请求 走势图 数据 *************/

	public static int RequestTrendData() {

		return 0;
	}

	/********* 请求 K 线 数据 **************/

	public static int RequestKLineData() {

		return 0;

	}

	/************* 明细请求 ******************/
	public static int RequestDetailData() {
		return 0;

	}

	/**
	 * 请求 代码 表 param:GlobalNetConnect netClass, String code
	 */

	public static int RequestOptionList() {

		return 0;
	}

	/************** 生成 加密 请求包 *************/
	// 生成一个加密请求包
	// 主要是设置包头
	// datasize为包的长度，含包头和包体
	// encrypt可取0和1，分别表示不加密和加密
	// int MainType, int ChildType,int PageNo, int SessionID, int RequestCode,
	// byte[] data,int offset, int datasize, int encrypt
	public static int MakeEncryptPackage(int SessionID, byte MainType,
			int RequestCode, byte[] data, int offset, int datasize,
			int encrypt, int compress) {
		byte cFlagZlibCompress = 0;

		int nBufferSize = datasize - GlobalNetConnect.MC_FrameHead_LEN;
		if (nBufferSize > 0) {
			byte[] szSrc = new byte[datasize + 50];
			// 拷贝数据。包头数据不压缩不加密
			System.arraycopy(data, GlobalNetConnect.MC_FrameHead_LEN, szSrc, 0,
					nBufferSize);
			L.e("MyApp", "MakeEncryptPackage srcData[" + nBufferSize + "]:"
					+ new String(szSrc, 0, nBufferSize));
			byte[] szDst = new byte[datasize + 50];
			if (compress > 0 && datasize > 256) // 需要压缩
			{
				nBufferSize = ZLibTools.compress(szSrc, nBufferSize, szDst);
				cFlagZlibCompress = 1;
				System.arraycopy(szDst, 0, szSrc, 0, szSrc.length);
			}

			if (data.length - GlobalNetConnect.MC_FrameHead_LEN < nBufferSize) {
				return -1;
			}

			if (encrypt > 0) {
				encrypt = 1;
				int nSize = (int) SSLEncrypt.DesNcbcEncrypt(
						SSLEncrypt.SSLKEY_INDEX_HQ, szSrc, szDst, nBufferSize,
						1);
				L.e("MyApp", "MakeEncryptPackage EncryptedData[" + nSize + "]:"
						+ new String(szDst, 0, nSize));
				// Des加密后必须在MAIN_FRAME_HEAD后面紧跟着原长度(unsigned short)
				MyByteBuffer.putShort(data, GlobalNetConnect.MC_FrameHead_LEN,
						(short) nBufferSize);
				System.arraycopy(szDst, 0, data,
						GlobalNetConnect.MC_FrameHead_LEN + 2, nSize);
				nBufferSize = nSize + 2;
			} else {
				System.arraycopy(szSrc, 0, data,
						GlobalNetConnect.MC_FrameHead_LEN, nBufferSize);
			}
			L.e("MyApp", "MakeEncryptPackage DstData["
					+ nBufferSize
					+ "]:"
					+ new String(data, GlobalNetConnect.MC_FrameHead_LEN,
							nBufferSize));
		}
		datasize = nBufferSize + GlobalNetConnect.MC_FrameHead_LEN;

		/*
		 * typedef struct { char cHeadID; // ‘@’ unsigned char cFrameType; //
		 * 帧类型 ,MAIN_FRAME_TYPE 定义 unsigned short wFrameCount; // 连续帧数量 unsigned
		 * short wFrameNo; // 帧号 unsigned short wDataSize; // 帧长度, 不包括帧头长度
		 * unsigned short wPackageNo; // 包号0-65535循环使用,静态包由客户端填写,动态数据由服务器端填写
		 * PKG_FLAG cFlag; unsigned char cErrorCode; // 返回码 } MAIN_FRAME_HEAD;
		 * typedef struct{ unsigned char Reserved : 4; //4位保留填0 unsigned char
		 * cFlagZlibCompress : 1; //Zlib压缩 unsigned char cFlagDesEncode : 1;
		 * //Des加密，加密后必须在MAIN_FRAME_HEAD后面紧跟着原长度DES_FRAME_HEAD unsigned char
		 * cFlagDynamic : 1; //动态包标示 1动态 0静态 unsigned char cFlagCompress : 1;
		 * //压缩状态 1压缩 0不压缩} PKG_FLAG;
		 */
		data[offset] = '@'; // cHeadID
		data[offset + 1] = MainType;
		MyByteBuffer.putShort(data, offset + 2, (short) 1);// wFrameCount
		MyByteBuffer.putShort(data, offset + 4, (short) 0);// wFrameNo
		short PackageSize = (short) (datasize - GlobalNetConnect.MC_FrameHead_LEN);
		MyByteBuffer.putShort(data, offset + 6, (short) PackageSize);
		MyByteBuffer.putShort(data, offset + 8, (short) RequestCode);

		data[offset + 10] = (byte) ((byte) (((int) encrypt << 5) & 0xf0) + (byte) (((int) cFlagZlibCompress << 4) & 0xf0));// PKG_FLAG
		data[offset + 11] = 0;// cErrorCode

		L.e("MyApp", "MakeEncryptPackage outData[" + datasize + "]:"
				+ new String(data, 0, datasize));
		return datasize;
	}

	/**
	 * 检测数据是否正确
	 * 
	 * @param data
	 * @param size
	 * @param head
	 *            ，返回的头
	 * @return：大于0时表示是个完整包，小于0时数据有错误，0时还不是完整包
	 */
	public static int CheckData(byte[] data, int size, HQFrameHead head) {
		if (size <= 0 || head == null) {
			return 0;
		}

		// typedef struct
		// {
		// unsigned char Reserved : 4; //4位保留填0
		// unsigned char cFlagZlibCompress : 1; //Zlib压缩
		// unsigned char cFlagDesEncode : 1;
		// //Des加密，加密后必须在MAIN_FRAME_HEAD后面紧跟着原长度DES_FRAME_HEAD
		// unsigned char cFlagDynamic : 1; //动态包标示 1动态 0静态
		// unsigned char cFlagCompress : 1; //压缩状态 1压缩 0不压缩
		// } PKG_FLAG;
		//
		// typedef struct
		// {
		// char cHeadID; /* @ */
		// unsigned char cFrameType; /* 帧类型 ,MAIN_FRAME_TYPE 定义 */
		// unsigned short wFrameCount; /* 连续帧数量 */
		// unsigned short wFrameNo; /* 帧号 */
		// unsigned short wDataSize; /* 帧长度, 不包括帧头长度 */
		// unsigned short wPackageNo; /* 包号0-65535循环使用,静态包由客户端填写,动态数据由服务器端填写*/
		// PKG_FLAG cFlag;
		// unsigned char cErrorCode; /* 返回码 */
		// } MAIN_FRAME_HEAD;
		head.cHeadID = (char) MyByteBuffer.getByte(data, 0);
		if (head.cHeadID != '@') {
			return -1;
		}

		if (size < GlobalNetConnect.MC_FrameHead_LEN) // 还不是一个完整包
		{
			return -2;
		}

		{
			head.cFrameType = (short) MyByteBuffer.getUnsignedByte(data, 1);
			head.wFrameCount = MyByteBuffer.getShort(data, 2);
			head.wFrameNo = MyByteBuffer.getShort(data, 4);
			head.wDataSize = MyByteBuffer.getUnsignedShort(data, 6);
			head.RequestCode = MyByteBuffer.getUnsignedShort(data, 8);

			byte cFlag = MyByteBuffer.getByte(data, 10);
			head.cFlagZlibCompress = (byte) ((cFlag >> 4) & 0x01);
			head.cFlagDesEncode = (byte) ((cFlag >> 5) & 0x01);
			head.cFlagDynamic = (byte) ((cFlag >> 6) & 0x01);
			head.cFlagCompress = (byte) ((cFlag >> 7) & 0x01);

			head.cErrorCode = (short) MyByteBuffer.getUnsignedByte(data, 11);
		}

		int packsize = head.wDataSize + GlobalNetConnect.MC_FrameHead_LEN;

		if (size < packsize) // 未满一个包
		{
			return 0;
		}

		return 1;
	}

	public static void Request_DownloadListFile(GlobalNetConnect netClass) {
		netClass.RequestDownloadList();
	}

	public static void Request_APPVersionFile(GlobalNetConnect netClass) {
		netClass.RequestAPPVersionFile();
	}
}
