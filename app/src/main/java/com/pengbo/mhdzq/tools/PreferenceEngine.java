package com.pengbo.mhdzq.tools;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.trade.data.Trade_Define;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceEngine {

	private static PreferenceEngine instance;
	private Context appContext = MyApp.getInstance().getApplicationContext();

	public synchronized static PreferenceEngine getInstance() {

		if (instance == null) {
			instance = new PreferenceEngine();
		}
		return instance;
	}

	private PreferenceEngine() {
	}

	// 保存该APK versionName=true
	public void saveAPKVersion(String versionName) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(versionName, true);
		editor.commit();
	}

	/**
	 * 获取apk对应的versionName是否为true来判断apk是否第一次安装
	 * 
	 * @param versionName
	 * @return
	 */
	public boolean getAPKVersion(String versionName) {
		boolean bret = getDefaultSharePreference().getBoolean(versionName,
				false);
		return bret;
	}

	// 交易模式，0-普通；1-高级
	public void saveTradeMode(int iTradeMode) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_MODE, iTradeMode);
		editor.commit();
	}

	public int getTradeMode() {
		int i = getDefaultSharePreference()
				.getInt(AppConstants.PREF_KEY_TRADE_MODE,
						Trade_Define.TRADE_MODE_GAOJI);
		return i;
	}

	// trade online time(1/3/5/10/15 minutes)
	public void saveTradeOnlineTime(int time) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_ONLINE_TIME, time);
		editor.commit();
	}

	public int getTradeOnlineTime() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_ONLINE_TIME, 15);

		return i;
	}

	// 下单无需确认，true-无需确认；false-需要确认 ------用于期权
	public void saveOrderWithoutConfirm(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_TRADE_ORDER_CONFIRM,
				isNoNeedConfirm);
		editor.commit();
	}

	// 下单无需确认 期权的
	public boolean getOrderWithoutConfirm() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_TRADE_ORDER_CONFIRM, false);
		return b;
	}

	// 下单无需确认，true-无需确认；false-需要确认 ------用于证券的
	public void saveZQOrderWithoutConfirm(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.ZQ_PREF_KEY_TRADE_ORDER_CONFIRM,
				isNoNeedConfirm);
		editor.commit();
	}

	// 下单无需确认 证券 的
	public boolean getZQOrderWithoutConfirm() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.ZQ_PREF_KEY_TRADE_ORDER_CONFIRM, false);
		return b;
	}

	// 交易数量默认加量(1/5/10/50)
	public void saveTradeOrderIncreaseNum(int num) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_ORDER_INCREASE_NUM, num);
		editor.commit();
	}

	public int getTradeOrderIncreaseNum() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_ORDER_INCREASE_NUM, 1);

		return i;
	}

	// 默认拆单数量
	public void saveTradeMRCDNum(int num) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_MRCD_NUM, num);
		editor.commit();
	}

	public int getTradeMRCDNum() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_MRCD_NUM, 0);

		return i;
	}

	// 全部平仓默认价格（对手价0，最新价1，挂单价2，涨停价3，跌停价4）
	public void saveTradeAllPingCangPrice(int priceType) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_ALL_PINGCANG_WTPRICE,
				priceType);
		editor.commit();
	}

	public int getTradeAllPingCangPrice() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_ALL_PINGCANG_WTPRICE,
				Trade_Define.WTPRICEMODE_DSJ);

		return i;
	}

	// 快捷反手默认委托价格（对手价0，最新价1，挂单价2，涨停价3，跌停价4）
	public void saveTradeKJFSWTPrice(int priceType) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_KJFS_WTPRICE, priceType);
		editor.commit();
	}

	public int getTradeKJFSWTPrice() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_KJFS_WTPRICE,
				Trade_Define.WTPRICEMODE_DSJ);

		return i;
	}

	// 快捷反手自动撤单时间（10、20、30秒）
	public void saveTradeKJFSAutoCDTime(int secs) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_KJFS_AUTO_CD_TIME, secs);
		editor.commit();
	}

	public int getTradeKJFSAutoCDTime() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_KJFS_AUTO_CD_TIME, 10);

		return i;
	}

	// 高级交易模式委托按钮顺序 （目前2种-0、1）
	public void saveTradeSeniorModeWTBtn(int btnOrder) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_TRADE_SENIOR_MODE_WT_BTN, btnOrder);
		editor.commit();
	}

	public int getTradeSeniorModeWTBtn() {
		int i = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_TRADE_SENIOR_MODE_WT_BTN, 0);

		return i;
	}

	// 行情筛选条件
	public void saveHQQueryCondition(String condition) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_HQ_QUERY_CONDITION, condition);
		editor.commit();
	}

	public String getHQQueryCondition() {
		String s = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_HQ_QUERY_CONDITION, "");

		return s;
	}

	// 券商选择 (券商id|账户类型|交易账号)
	public void saveTradeQSInfo(String qsinfo) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_TRADE_QS_SELECTION, qsinfo);
		editor.commit();
	}

	public String getTradeQSInfo() {
		String s = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_TRADE_QS_SELECTION, "");

		return s;
	}

	// 是否保存账号true-保存
	public void saveIsTradeAccountSaved(boolean bSaved) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_TRADE_ACCOUNT_SAVED, bSaved);
		editor.commit();
	}

	public boolean getIsTradeAccountSaved() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_TRADE_ACCOUNT_SAVED, false);
		return b;
	}

	// 是否保存账号_ZQ true-保存
	public void saveIsTradeAccountSaved_ZQ(boolean bSaved) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_TRADE_ACCOUNT_SAVED_ZQ, bSaved);
		editor.commit();
	}

	/** 是否保存帐号 **/
	public boolean getIsTradeAccountSaved_ZQ() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_TRADE_ACCOUNT_SAVED_ZQ, false);
		return b;
	}

	private SharedPreferences getDefaultSharePreference() {
		return appContext.getSharedPreferences(AppConstants.APP_PREFERENCE,
				Context.MODE_PRIVATE);
	}

	public void savePhoneForVerify(String strPhone) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_TRADE_PHONE_VERIFY, strPhone);
		editor.commit();
	}

	public String getPhoneForVerify() {
		String s = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_TRADE_PHONE_VERIFY, "");

		return s;
	}

	// 暂时性保存手机号
	public void saveTempPhoneForVerify(String tempPhone) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_TRADE_PHONE_TEMP, tempPhone);
		editor.commit();
	}

	public String getTempPhoneForVerify() {
		String s = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_TRADE_PHONE_TEMP, "");

		return s;
	}

	// ---------------------------------------------现货交易相关
	// 均线 默认几条的天数
	public void saveMA(String str) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_MA, str);
		editor.commit();
	}

	public String getMA() {
		String str = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_MA, "5,10,20,60,250");

		return str;
	}

	// MACD默认几条的天数
	public void saveMACD(String str) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_MACD, str);
		editor.commit();
	}

	public String getMACD() {
		String str = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_MACD, "12,26,9");

		return str;
	}

	// KDJ默认几条的天数
	public void saveKDJ(String str) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_KDJ, str);
		editor.commit();
	}

	public String getKDJ() {
		String str = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_KDJ, "9,3,3");

		return str;
	}

	// RSJ默认几条的天数
	public void saveRSI(String str) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_RSJ, str);
		editor.commit();
	}

	public String getRSI() {
		String str = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_RSJ, "7,14");

		return str;
	}

	// WR默认几条的天数
	public void saveWR(String str) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_WR, str);
		editor.commit();
	}

	public String getWR() {
		String str = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_WR, "14");

		return str;
	}

	// RSJ默认几条的天数
	public void saveBIAS(String str) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_BIAS, str);
		editor.commit();
	}

	public String getBIAS() {
		String str = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_BIAS, "6,12,24");

		return str;
	}

	// 保存涨跌计算比较价格 ，true-昨收 ；false-昨结
	public void saveZDJSBJJG(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_ZDJSBJJG, isNoNeedConfirm);
		editor.commit();
	}

	public boolean getZDJSBJJG() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_ZDJSBJJG, false);
		return b;
	}

	// 显示波段高低价 ，true- ；false-
	public void saveXSBDGDJ(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_XSBDGDJ, isNoNeedConfirm);
		editor.commit();
	}

	public boolean getXSBDGDJ() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_XSBDGDJ, true);
		return b;
	}

	// 启动是否默认进入自选 ，true-；false-
	public void saveQDSFMRJRZX(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_QDSFMRJRZX, isNoNeedConfirm);
		editor.commit();
	}

	public boolean getQDSFMRJRZX() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_QDSFMRJRZX, false);
		return b;
	}

	// 允许屏幕自动休眠 ，true-；false-
	public void saveYXPMZDJRXM(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_YXPMZDJRXM, isNoNeedConfirm);
		editor.commit();
	}

	/**
	 * 从SharedPreference()中得到“set_yxpmzdjrxm”所对应的值
	 * 
	 * @return
	 */
	public boolean getYXPMZDJRXMG() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_YXPMZDJRXM, true);
		return b;
	}

	// 启动智能键盘 ，true-；false-
	public void saveQDZNJP(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_QDZNJP, isNoNeedConfirm);
		editor.commit();
	}

	// 默认智能键盘为打开
	public boolean getQDZNJP() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_QDZNJP, true);
		return b;
	}

	// 成交回报自动提醒 ，true- ；false-
	public void saveCJHBZDTX(boolean isNoNeedConfirm) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(AppConstants.PREF_KEY_CJHBZDTX, isNoNeedConfirm);
		editor.commit();
	}

	public boolean getCJHBZDTX() {
		boolean b = getDefaultSharePreference().getBoolean(
				AppConstants.PREF_KEY_CJHBZDTX, false);
		return b;
	}

	// 当前版本信息保存（versioncode）
	public void saveVersionCode(String versionCode) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(AppConstants.PREF_KEY_VERSIONCODE, versionCode);
		editor.commit();
	}

	public String getVersionCode() {
		String versionCode = getDefaultSharePreference().getString(
				AppConstants.PREF_KEY_VERSIONCODE, "");
		return versionCode;
	}

	// 1000.ini 文件日期、时间、长度
	public void saveLengthZLHY(int nLength) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_ZLHYLENGTH, nLength);
		editor.commit();
	}

	public int getLengthZLHY() {
		int len = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_ZLHYLENGTH, 0);
		return len;
	}

	public void saveDateZLHY(int nDate) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_ZLHYDATE, nDate);
		editor.commit();
	}

	public int getDateZLHY() {
		int date = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_ZLHYDATE, 0);
		return date;
	}

	public void saveTimeZLHY(int nTime) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_ZLHYTIME, nTime);
		editor.commit();
	}

	public int getTimeZLHY() {
		int time = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_ZLHYTIME, 0);
		return time;
	}

	// CustomColumn.ini 文件日期、时间、长度
	public void saveLengthSHUILV(int nLength) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_SHUILVLENGTH, nLength);
		editor.commit();
	}

	public int getLengthSHUILV() {
		int len = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_SHUILVLENGTH, 0);
		return len;
	}

	public void saveDateSHUILV(int nDate) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_SHUILVDATE, nDate);
		editor.commit();
	}

	public int getDateSHUILV() {
		int date = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_SHUILVDATE, 0);
		return date;
	}

	public void saveTimeSHUILV(int nTime) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_SHUILVTIME, nTime);
		editor.commit();
	}

	public int getTimeSHUILV() {
		int time = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_SHUILVTIME, 0);
		return time;
	}

	public void saveKeepLiveTime(int minute) {
		SharedPreferences preference = getDefaultSharePreference();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(AppConstants.PREF_KEY_KEEPTRADELIVETIME, minute);
		editor.commit();
	}

	public int getSaveKeepLiveTime() {
		int time = getDefaultSharePreference().getInt(
				AppConstants.PREF_KEY_KEEPTRADELIVETIME, 0);
		return time;
	}
}
