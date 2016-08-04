package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;

import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.trade.data.TradeLocalRecord;

public interface OnTradeFragmentListener {
	public void requestWT(TradeLocalRecord record);
	public void requestDRWT();
	public void requestDRCJ();
	public void requestZJ();
	public void requestHoldStock();

	public void requestWTWithFlag(TradeLocalRecord record, boolean bOnlyPC);

	public void requestWTCD(TradeLocalRecord record);

	public void requestKMSL(String price, TagLocalStockData optionData,
			char sjtype,boolean buy);

	// 请求当前下单页面选择的合约以及标的推送信息
	public void requestHQPushData(ArrayList<TagCodeInfo> codeList);

	public void requestHQTrendLine(TagLocalStockData optionData,
			TagLocalStockData stockData);
	
	// 行权
	public void requestXQ(String marketCode, String stockCode, String xqsl, String gdzh, String xwh, String sqType);
	// 查可行权数量
	public void requestKXQSL(String stockCode, String marketCode, String gdzh, String xwh);
	// 撤销行权
	public void requestXQCD(String marketCode, String wtbh, String gdzh, String xwh, String xdxw);
	// 查行权委托单
	public void requestXQWT();
	// 查行权指派
	public void requestXQZP(String marketCode, String stockCode, String gdzh);
	
	// 查历史行权指派
	public void requestXQLSZP(String startDate, String endDate);
	
	// 设置快捷反手是否结束
	public void setKJFSRuning(boolean bRuning);
	
	public void requestHQPushData(TagCodeInfo optionInfo, ArrayList<TagCodeInfo> codeList);



}
