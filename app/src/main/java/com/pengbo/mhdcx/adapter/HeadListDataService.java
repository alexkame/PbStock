package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdzq.data.CHQData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagProfitRecord;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.tools.ViewTools;

/**
 * 期权列表数据业务类
 * 
 * @author pobo
 * 
 */
public class HeadListDataService {
	public MyApp myApp;

	/**
	 * 构造函数
	 * 
	 * @param mActivity
	 */
	public HeadListDataService(Activity mActivity) {
		super();
		myApp = (MyApp) mActivity.getApplication();
	}

	/**
	 * 拿取 所有数据行情
	 * 搜索页要用到的传入 代码 市场   日期   和   认购或 认沽  
	 * @param stockCode
	 * @param stockMarket
	 * @param date
	 * @param fx
	 * @return
	 */
	public List<TagLocalStockData> getTagLocalStockDatas(String stockCode,
			short stockMarket, int date, byte fx) {
		List<TagCodeInfo> mTagCodeInfos = null;
		List<TagLocalStockData> mTagLocalStockDatas = new ArrayList<TagLocalStockData>();
		TagCodeInfo aCodeInfo;// 包含市场 代码 和 分组的 类
		mTagCodeInfos = myApp.mHQData.getOptionList(stockCode, stockMarket,
				date, fx);
		for (int i = 0; i < mTagCodeInfos.size(); i++) {
			aCodeInfo = mTagCodeInfos.get(i);
			TagLocalStockData aStockData = new TagLocalStockData();
			myApp.mHQData.getData(aStockData, aCodeInfo.market, aCodeInfo.code,
					false);
			mTagLocalStockDatas.add(aStockData);
		}
		return mTagLocalStockDatas;
	}
	
	/**
	 * 
	 * 期权的详细页 
	 * 
	 * 查单只股票的合约信息 
	 * @return
	 */
	public TagLocalStockData getTagLocalStockData(short stockMarket,String stockCode){
		TagLocalStockData tStockData=new TagLocalStockData();
		myApp.mHQData.search(tStockData, stockMarket, stockCode);
		return tStockData;		
	}
	
	
	/**
	 * 期权的详细页 
	 * 
	 * 单只股票  合约对应的标的信息 
	 * 
	 * 通过市场 和   代码  查询 
	 * @param stockCode  合约代码 
	 * @param stockMarket 合约市场
	 * @return
	 */
	public TagLocalStockData getTagLocalStockDataAll(short DataMarket,String DataCode){
		// aStockInfo 合约对应的标的信息
		TagLocalStockData sStockInfo=new TagLocalStockData();		
		myApp.mStockConfigData.search(sStockInfo, DataMarket, DataCode);  // 标的的信息 		
		return sStockInfo;		
	}
	
	
	

	public ArrayList<TagCodeInfo> getTagCodeInfos(String stockCode,
			short stockMarket, int date, byte fx) {
		//这里不做初始化 方法中有初始 
		ArrayList<TagCodeInfo> tTagCodeInfos = null;
		tTagCodeInfos = myApp.mHQData.getOptionList(stockCode, stockMarket,
				date, fx);
		return tTagCodeInfos;
	}

	/**
	 * 拿取 日期 集合
	 * 
	 * @param stockCode
	 * @param stockMarket
	 * @return
	 */
	public List<String> getAllDateArray(String stockCode, short stockMarket) {
		List<String> mStrDates = null;// 日期集合
		mStrDates = myApp.mHQData.getDateArray(stockCode, stockMarket);
		return mStrDates;
	}

	/**
	 * 获取标的的集合 传的是 TagCodeInfo 对象过去
	 * 
	 * @return
	 */
	public List<TagCodeInfo> getTagCodeInfoRemoveNull() {
		List<TagCodeInfo> mTagCodeInfos = null;
		List<TagCodeInfo> mTagCodeInfoRemoveNull = new ArrayList<TagCodeInfo>();
		mTagCodeInfos = myApp.mStockConfigData.getStockList();
		for (int i = 0; i < mTagCodeInfos.size(); i++) {
			if (mTagCodeInfos.get(i).name == null
					|| "".equals(mTagCodeInfos.get(i).name.trim().toString())) {
				continue;
			} else {
				mTagCodeInfoRemoveNull.add(mTagCodeInfos.get(i));
			}
		}
		return mTagCodeInfoRemoveNull;
	}
	
	
	
	
	/**
	 * 传入筛选条件对象   筛选对应的 合约 信息 
	 * @param sc
	 * @param stockConfigData
	 * @return
	 */
	
	public ArrayList<TagLocalStockData> getScreenTagCodeInfos(ScreenCondition sc,CHQData stockConfigData){
		ArrayList<TagCodeInfo> sTagCodeInfos=null;
		ArrayList<TagLocalStockData> mLocalStockDatas = new ArrayList<TagLocalStockData>();
		TagCodeInfo aCodeInfo;// 包含市场 代码 和 分组的 类

		sTagCodeInfos=myApp.mHQData.getOptionList(sc, stockConfigData);
		
		for (int i = 0; i < sTagCodeInfos.size(); i++) {
			aCodeInfo = sTagCodeInfos.get(i);
			TagLocalStockData aStockData = new TagLocalStockData();
			myApp.mHQData.getData(aStockData, aCodeInfo.market, aCodeInfo.code,
					false);
			mLocalStockDatas.add(aStockData);
		}
		return mLocalStockDatas;		
	}
	
	/**
	 * 盈亏分析 数据 
	 * @param bdyqbl   传入 平缓   正常  剧烈  
	 * @param stockData
	 * @param stockInfo
	 * @return
	 */
	//public 
	//getProfitRecordList(TagProfitRecord[]profitRecord,float bdyqbl, TagLocalStockData stockData,TagLocalStockData stockInfo)
	//ViewTools.getProfitRecordList(tempProfitRecord, bdyqbl, sStockData, sStockInfo);
	public List<TagProfitRecord> getProfitRecordList(float bdyqbl, TagLocalStockData stockData,TagLocalStockData stockInfo, TagProfitRecord pjsy){
		List<TagProfitRecord> tagProfitRecords=null;
		tagProfitRecords=ViewTools.getProfitRecordList(bdyqbl, stockData, stockInfo, pjsy);		
		return tagProfitRecords;		
	}
}
