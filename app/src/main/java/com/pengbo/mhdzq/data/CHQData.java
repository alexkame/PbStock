package com.pengbo.mhdzq.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;

import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;

public class CHQData {
	public static final int MAX_LOCAL_STOCKHQ_COUNT = 1000;
	private ArrayList<TagLocalStockData> m_codeList;

	public CHQData() {
		m_codeList = new ArrayList<TagLocalStockData>();
	}

	public ArrayList<TagLocalStockData> getDataList() {
		return m_codeList;
	}

	// 获取数量
	public long getNum() {
		return m_codeList.size();
	}

	public TagLocalStockData getItem(int nIndex) {
		if (nIndex >= m_codeList.size()) {
			return null;
		}
		return m_codeList.get(nIndex);
	}

	public void clear() {
		m_codeList.clear();
	}

	public void clearwithMarket(int nMarketId) {
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == nMarketId) {
				m_codeList.remove(i);
			}
		}
	}

	public void writelog() {
		// //WRITELOG(100, "%5s %6s %10s %8s", "market", "code", "tickcount",
		// "now");
		// tagLocalStockData* p = (tagLocalStockData*)m_codeList.GetPtr();
		// for (int i = 0; i < m_codeList.size(); i++, p++)
		// {
		// //WRITELOG(100, "%5d %6s %10d %8d", p.market, p.code, p.tickcount,
		// p.now);
		// }
	}

	// 根据代码获取行情，返回1表示成功，0失败
	public boolean getData(TagLocalStockData stock, short market, String code,
			boolean resetNewFlag) {
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == market
					&& code.equalsIgnoreCase(p.HQData.code)) {
				stock.copyData(p);
				if (resetNewFlag == true) {
					p.HQData.bNewUpdated = false;
				}
				return true;
			}
		}
		stock.HQData.market = market;
		stock.HQData.code = code;
		return false;
	}

	// 根据代码获取行情，返回true表示成功，false失败
	public boolean search(TagLocalStockData stock, short market, String code) {
		if (market == 0 || code == null || code.length() == 0) {
			return false;
		}
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == market
					&& code.equalsIgnoreCase(p.HQData.code)) {
				if (stock != null) {
					stock.copyData(p);
				}
				return true;
			}
		}
		return false;
	}

	// 根据代码获取行情，返回true表示成功，false失败
	public boolean add(short market, String code, boolean bAddDirectly) {
		if (market == 0 || code == null || code.length() == 0) {
			return false;
		}
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == market
					&& code.equalsIgnoreCase(p.HQData.code)) {
				return false;
			}
		}
		TagLocalStockData tempRecord = new TagLocalStockData();
		tempRecord.code = code;
		tempRecord.market = market;
		tempRecord.HQData.code = code;
		tempRecord.HQData.market = market;
		addRecord(tempRecord, bAddDirectly);
		return true;
	}

	public void copyHQData(TagLocalHQRecord outRecord, TagLocalHQRecord inRecord) {
		if (inRecord.nTradeDate != 0)
			outRecord.nTradeDate = inRecord.nTradeDate;
		if (inRecord.nUpdateDate != 0)
			outRecord.nUpdateDate = inRecord.nUpdateDate;
		if (inRecord.nUpdateTime != 0)
			outRecord.nUpdateTime = inRecord.nUpdateTime;
		if (inRecord.nLastClose != 0)
			outRecord.nLastClose = inRecord.nLastClose;
		if (inRecord.nLastClear != 0)
			outRecord.nLastClear = inRecord.nLastClear;
		if (inRecord.dLastOpenInterest != 0)
			outRecord.dLastOpenInterest = inRecord.dLastOpenInterest;
		if (inRecord.nOpenPrice != 0)
			outRecord.nOpenPrice = inRecord.nOpenPrice;
		if (inRecord.nHighPrice != 0)
			outRecord.nHighPrice = inRecord.nHighPrice;
		if (inRecord.nLowPrice != 0)
			outRecord.nLowPrice = inRecord.nLowPrice;
		if (inRecord.nLastPrice != 0) {
			outRecord.nLastPrice = inRecord.nLastPrice;
			outRecord.nLastPriceForCalc = inRecord.nLastPrice;
		} else {
			if (inRecord.nLastClose != 0) {
				outRecord.nLastPriceForCalc = inRecord.nLastClose;
			} else {
				outRecord.nLastPriceForCalc = inRecord.nLastClear;
			}
		}
		if (inRecord.nUpperLimit != 0)
			outRecord.nUpperLimit = inRecord.nUpperLimit;
		if (inRecord.nLowerLimit != 0)
			outRecord.nLowerLimit = inRecord.nLowerLimit;
		if (inRecord.nAveragePrice != 0)
			outRecord.nAveragePrice = inRecord.nAveragePrice;
		if (inRecord.nClearPrice != 0)
			outRecord.nClearPrice = inRecord.nClearPrice;
		if (inRecord.nClosePrice != 0)
			outRecord.nClosePrice = inRecord.nClosePrice;
		if (inRecord.volume != 0)
			outRecord.volume = inRecord.volume;
		if (inRecord.amount != 0)
			outRecord.amount = inRecord.amount;
		if (inRecord.dOpenInterest != 0)
			outRecord.dOpenInterest = inRecord.dOpenInterest;
		if (inRecord.nTradeTicks != 0)
			outRecord.nTradeTicks = inRecord.nTradeTicks;
		if (inRecord.nTradeDirect != 0)
			outRecord.nTradeDirect = inRecord.nTradeDirect;
		if (outRecord.sellPrice[0] > 0.0001
				&& inRecord.nLastPrice >= outRecord.sellPrice[0]) {
			// 主动性买盘
			outRecord.nTradeDirect = 1;
		} else if (outRecord.buyPrice[0] > 0.0001
				&& inRecord.nLastPrice <= outRecord.buyPrice[0]) {
			// 主动性卖盘
			outRecord.nTradeDirect = 2;
		} else if (outRecord.sellPrice[0] == 0 && inRecord.nLastPrice > 0) {
			// 张停, 主动卖
			outRecord.nTradeDirect = 2;
		} else if (outRecord.buyPrice[0] == 0 && inRecord.nLastPrice > 0) {
			// 跌停, 主动买
			outRecord.nTradeDirect = 1;
		}
		// 默认推送时买卖五档数据都是有更新的
		System.arraycopy(inRecord.buyPrice, 0, outRecord.buyPrice, 0, 5);
		System.arraycopy(inRecord.buyVolume, 0, outRecord.buyVolume, 0, 5);
		System.arraycopy(inRecord.sellPrice, 0, outRecord.sellPrice, 0, 5);
		System.arraycopy(inRecord.sellVolume, 0, outRecord.sellVolume, 0, 5);

		if (inRecord.currentVolume != 0)
			outRecord.currentVolume = inRecord.currentVolume;
		if (inRecord.dLiquidity != 0)
			outRecord.dLiquidity = inRecord.dLiquidity;
		if (inRecord.dWPL != 0)
			outRecord.dWPL = inRecord.dWPL;
	}

	public boolean updateHQData(TagLocalHQRecord pRecord, boolean bNeedAdd) {
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == pRecord.market
					&& pRecord.code.equalsIgnoreCase(p.HQData.code)) {
				double oldVolume = p.HQData.volume;
				double oldAmount = p.HQData.amount;
				copyHQData(p.HQData, pRecord);
				p.HQData.bNewUpdated = true;
				p.HQData.currentCJ = p.HQData.volume - oldVolume;
				if (p.HQData.volume != oldVolume && oldVolume != 0) {
					p.HQData.currentVolume = p.HQData.volume - oldVolume;
				}
				double currentAmount = p.HQData.amount - oldAmount;
				if (p.HQData.currentCJ != 0 && currentAmount != 0
						&& p.Multiplier != 0) {
					p.HQData.currentCJAveragePrice = currentAmount
							/ p.HQData.currentCJ / p.Multiplier;
				} else {
					p.HQData.currentCJAveragePrice = 0;
				}
				return true;
			}
		}
		if (bNeedAdd) {
			TagLocalStockData tempRecord = new TagLocalStockData();
			tempRecord.HQData.copyData(pRecord);
			m_codeList.add(tempRecord);
		}
		return false;
	}

	// bAddDirectly : true-添加记录，不管是否炒股上限；false-超过上限不添加
	public boolean updateData(TagLocalStockData pRecord, boolean bAddDirectly) {
		if (pRecord.HQData.market == 0 || pRecord.HQData.code == null
				|| pRecord.HQData.code.length() == 0) {
			return false;
		}
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == pRecord.HQData.market
					&& pRecord.HQData.code.equalsIgnoreCase(p.HQData.code)) {
				p.copyData(pRecord);
				return true;
			}
		}
		addRecord(pRecord, bAddDirectly);
		return false;
	}

	public void addRecord(TagLocalStockData pRecord, boolean bAddDirectly) {
		if (bAddDirectly) {
			m_codeList.add(pRecord);
			return;
		}

		if (m_codeList.size() > MAX_LOCAL_STOCKHQ_COUNT) {
			for (int i = 0; i < MAX_LOCAL_STOCKHQ_COUNT / 10; i++)
				m_codeList.remove(0);
		}
		m_codeList.add(pRecord);
	}

	public boolean updateOptionData(TagLocalOptionRecord pRecord) {
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.HQData.market == pRecord.market
					&& pRecord.code.equalsIgnoreCase(p.HQData.code)) {
				p.optionData.copyData(pRecord);
				return true;
			}
		}
		return false;
	}

	// stockMarket:""添加all
	// code:"" 不过滤code
	// groupcode:""不过滤groupcode
	public ArrayList<TagCodeInfo> getOptionList(String stockMarket,
			String code, String groupCode) {
		ArrayList<TagCodeInfo> aArray = new ArrayList<TagCodeInfo>();
		TagLocalStockData p;
		boolean bNeedAdd = false;
		boolean bCheckCode = false;
		boolean bCheckGroup = false;
		boolean bWildcards = false;// 通配符
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			bNeedAdd = false;
			{

				if (stockMarket == null || stockMarket.isEmpty()) {
					bNeedAdd = true;
				} else {
					int nMarket = STD.StringToInt(stockMarket);

					if (code != null && !code.isEmpty()) {
						bCheckCode = true;
						if (code.endsWith("*")) {
							bWildcards = true;
							code = code.substring(0, code.length() - 1);
						}
					}
					if (groupCode != null && !groupCode.isEmpty()) {
						bCheckGroup = true;
					}

					if (p.market == nMarket) {
						if (bCheckCode) {
							if (bWildcards) {
								if (!p.code.contains(code)) {
									break;
								}
							} else {
								if (!code.equalsIgnoreCase(p.code)) {
									break;
								}
							}
						}

						if (bCheckGroup) {
							if (!groupCode.equals(p.groupCode)) {
								break;
							}
						}
						bNeedAdd = true;
						break;
					}
				}
			}

			if (true == bNeedAdd) {
				TagCodeInfo aCodeRecord = new TagCodeInfo(p.HQData.market,
						p.HQData.code, (short) p.GroupOffset, p.name);
				aArray.add(aCodeRecord);
			}
		}

		return aArray;
	}

	// 获取满足搜索条件的期权列表，返回列表ArrayList<TagCodeInfo>
	// stockMarket: 标的市场，如果为空，搜索所有合约，可能会有多个市场代码如2000|2001|2002
	// date: 期权到期日 如果为0，则搜索所有日期
	public ArrayList<TagCodeInfo> getOptionList(String stockMarket, int date,
			byte fx) {
		ArrayList<TagCodeInfo> aArray = new ArrayList<TagCodeInfo>();
		TagLocalStockData p;
		boolean bNeedAdd = false;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			bNeedAdd = false;
			if (date == 0 || p.optionData.StrikeDateNoDay == date) {

				if (stockMarket == null || stockMarket.isEmpty()) {
					bNeedAdd = true;
				} else {
					for (int j = 1; j < 100; j++) {
						String market = STD.GetValue(stockMarket, j, '|');
						if (market.isEmpty()) {
							break;
						}
						int nMarket = STD.StringToInt(market);
						if (p.market == nMarket) {
							bNeedAdd = true;
							break;
						}
					}
				}
			}

			if (true == bNeedAdd) {
				TagCodeInfo aCodeRecord = new TagCodeInfo(p.HQData.market,
						p.HQData.code, (short) p.GroupOffset, p.name);
				aArray.add(aCodeRecord);
			}
		}

		return aArray;
	}

	// 获取满足搜索条件的期权列表，返回列表ArrayList<TagCodeInfo>
	// stockCode: 标的代码，如果为空，则搜索所有期权
	// stockMarket: 标的市场，如果code为空，忽略market
	// date: 期权到期日 如果为0，则搜索所有日期
	// fx: 期权投资方向 0.看涨(认购) 1.看跌(认沽)
	public ArrayList<TagCodeInfo> getOptionList(String stockCode,
			short stockMarket, int date, byte fx) {
		ArrayList<TagCodeInfo> aArray = new ArrayList<TagCodeInfo>();
		TagLocalStockData p;
		boolean bNeedAdd = false;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			bNeedAdd = false;
			if (date == 0 || p.optionData.StrikeDateNoDay == date) {
				if (p.optionData.OptionCP == fx) {

					if (stockCode == null
							|| stockCode.length() == 0
							|| stockCode
									.equalsIgnoreCase(ScreenCondition.SCREEN_ALL_STOCK_CODE)) {
						bNeedAdd = true;
					} else {
						if (p.optionData.StockMarket == stockMarket
								&& stockCode.equals(p.optionData.StockCode)) {
							bNeedAdd = true;
						}
					}
				}
			}

			if (true == bNeedAdd) {
				TagCodeInfo aCodeRecord = new TagCodeInfo(p.HQData.market,
						p.HQData.code, (short) p.GroupOffset, p.name);
				aArray.add(aCodeRecord);
			}
		}

		return aArray;
	}

	// 获取满足setting筛选条件的期权列表
	public ArrayList<TagCodeInfo> getOptionList(ScreenCondition setting,
			CHQData stockConfigData) {
		ArrayList<TagCodeInfo> aArray = new ArrayList<TagCodeInfo>();
		boolean bNeedAdd = false;

		TagLocalStockData p;
		for (int m = 0; m < m_codeList.size(); m++) {
			p = m_codeList.get(m);
			bNeedAdd = false;
			// 过滤:标的选择，看涨看跌
			if (setting.code
					.equalsIgnoreCase(ScreenCondition.SCREEN_ALL_STOCK_CODE)
					|| (setting.code.equals(p.optionData.StockCode) && p.optionData.StockMarket == setting.market)) {
				if (p.optionData.OptionCP == 0 && setting.gridViewId < 3) {

					bNeedAdd = true;
				} else if (p.optionData.OptionCP == 1 && setting.gridViewId > 3) {
					bNeedAdd = true;
				}
			}
			// 过滤:短期看涨，中期看涨，长期看涨等
			if (true == bNeedAdd) {
				bNeedAdd = false;
				int nDays = ViewTools
						.getDaysDruationFromToday(p.optionData.StrikeDate);
				if (nDays < 30) // 短期(一个月左右)
				{
					if (setting.gridViewId == 0 || setting.gridViewId == 3) {
						bNeedAdd = true;
					}
				} else if (nDays > 90)// 长期(3~12个月)
				{
					if (setting.gridViewId == 2 || setting.gridViewId == 5) {
						bNeedAdd = true;
					}
				} else // 中期期(1~3个月)
				{
					if (setting.gridViewId == 1 || setting.gridViewId == 4) {
						bNeedAdd = true;
					}
				}
			}
			// 过滤:看涨跌幅度
			// 看涨(认购):行权价+期权价 <= 当前标的价格*(1+幅度%)
			// 看跌(认沽):行权价-期权价 >= 当前标的价格*(1-幅度%)
			TagLocalStockData stockInfo = new TagLocalStockData();
			boolean bSearchRet = stockConfigData.search(stockInfo,
					p.optionData.StockMarket, p.optionData.StockCode);
			if (true == bNeedAdd) {
				bNeedAdd = false;
				if (bSearchRet == true) {
					float fStockPrice = ViewTools.getFloatPriceByFieldID(
							stockInfo, Global_Define.FIELD_HQ_NOW);
					float fOptionPrice = 0;
					if (p.optionData.OptionCP == 0) {// 看涨(认购)
						fStockPrice += fStockPrice * setting.field_hq_zdf;
						fOptionPrice = p.optionData.StrikePrice
								+ ViewTools.getFloatPriceByFieldID(p,
										Global_Define.FIELD_HQ_NOW);
						if (fOptionPrice <= fStockPrice) {
							bNeedAdd = true;
						}
					} else if (p.optionData.OptionCP == 1)// 看跌(认沽)
					{
						fStockPrice -= fStockPrice * setting.field_hq_zdf;
						fOptionPrice = p.optionData.StrikePrice
								- ViewTools.getFloatPriceByFieldID(p,
										Global_Define.FIELD_HQ_NOW);
						if (fOptionPrice >= fStockPrice) {
							bNeedAdd = true;
						}
					}
				}

			}
			// 过滤:杠杆比率
			if (true == bNeedAdd) {
				bNeedAdd = false;
				if (bSearchRet == true) {
					if (setting.leverId == 3)// 全部
					{
						bNeedAdd = true;
					} else {
						float fStockPrice = ViewTools.getFloatPriceByFieldID(
								stockInfo, Global_Define.FIELD_HQ_NOW);
						float fOptionPrice = ViewTools.getFloatPriceByFieldID(
								p, Global_Define.FIELD_HQ_NOW);
						float bl = 0;
						if (fOptionPrice != 0) {
							bl = fStockPrice / fOptionPrice;
							if (bl < 10.0 && setting.leverId == 0) // <10倍
							{
								bNeedAdd = true;
							} else if (bl > 30.0 && setting.leverId == 2)// 30倍以上
							{
								bNeedAdd = true;
							} else if (bl >= 10.0 && bl <= 30.0
									&& setting.leverId == 1)// 10~30倍
							{
								bNeedAdd = true;
							}
						}
					}
				}
			}
			// 过滤:活跃度
			if (true == bNeedAdd) {
				// bNeedAdd = false;
				// Deal later
			}
			// 过滤:未来波动
			// if (true == bNeedAdd) {
			// bNeedAdd = false;
			// //暂定低于正常波动率30%为平缓，高于正常波动率50%为剧烈
			// float fBDL = [DataTools::getStringByFieldNo(FIELD_HQ_YHBDL, *p)
			// floatValue];
			// if (fBDL < p.optionData.dHistoryVolatility*0.7 &&
			// setting.nSelWLBD == 0) //平缓
			// {
			// bNeedAdd = true;
			// }
			// else if(fBDL >= p.optionData.dHistoryVolatility*0.7 && fBDL <=
			// p.optionData.dHistoryVolatility*1.5 && setting.nSelWLBD == 1)//正常
			// {
			// bNeedAdd = true;
			// }
			// else if(fBDL > p.optionData.dHistoryVolatility*1.5 &&
			// setting.nSelWLBD == 2)//剧烈
			// {
			// bNeedAdd = true;
			// }
			// }
			if (true == bNeedAdd) {
				TagCodeInfo aCodeRecord = new TagCodeInfo(p.HQData.market,
						p.HQData.code, (short) p.group, p.name);
				aArray.add(aCodeRecord);
			}
		}
		return aArray;
	}

	// 获取到期日列表
	// code: 标的代码，如果为空，则搜索所有期权
	// short market: 标的市场，如果code为空，忽略market
	public ArrayList<String> getDateArray(String stockCode, short stockMarket) {
		ArrayList<String> aArray = new ArrayList<String>();
		boolean bStockCode = stockCode.isEmpty();
		boolean bEqual = true;
		TagLocalStockData p;
		for (int i = 0; i < m_codeList.size(); i++) {
			p = m_codeList.get(i);
			if (p.optionData.StrikeDateNoDay == 0)
				continue;

			String szDate = String.valueOf(p.optionData.StrikeDateNoDay);

			if (!bStockCode) {
				if (stockCode.equalsIgnoreCase(p.optionData.StockCode)
						&& stockMarket == p.optionData.StockMarket) {
					bEqual = true;
				} else {
					bEqual = false;
				}
			}

			if (bEqual && szDate != null && false == aArray.contains(szDate)) {
				boolean bAdded = false;
				for (int j = 0; j < aArray.size(); j++) {
					if (STD.StringToInt(szDate) <= STD.StringToInt(aArray
							.get(j))) {
						aArray.add(j, szDate);
						bAdded = true;
						break;
					}
				}
				if (!bAdded) {
					aArray.add(szDate);
				}
			}
		}

		return aArray;
	}

	// 获取多个期权对应的标的列表，返回实际存在的股票个数
	public ArrayList<TagCodeInfo> getStockListByOptionList(
			ArrayList<TagCodeInfo> inList) {
		ArrayList<TagCodeInfo> aArray = new ArrayList<TagCodeInfo>();

		for (int i = 0; i < inList.size(); i++) {

			TagLocalStockData p;
			for (int m = 0; m < m_codeList.size(); m++) {
				p = m_codeList.get(m);
				if (p.HQData.market == inList.get(i).market
						&& p.HQData.code.equalsIgnoreCase(inList.get(i).code)) {
					TagCodeInfo aCodeRecord = new TagCodeInfo(
							p.optionData.StockMarket, p.optionData.StockCode,
							p.group, p.name);
					if (aCodeRecord != null
							&& false == aArray.contains(aCodeRecord)) {
						aArray.add(aCodeRecord);
					}
					break;
				}
			}
		}
		return aArray;
	}

	// 获取所有股票的列表（只当保存标的时使用）
	public ArrayList<TagCodeInfo> getStockList() {
		ArrayList<TagCodeInfo> aArray = new ArrayList<TagCodeInfo>();
		TagLocalStockData p;
		for (int m = 0; m < m_codeList.size(); m++) {
			p = m_codeList.get(m);
			TagCodeInfo aCodeRecord = new TagCodeInfo(p.HQData.market,
					p.HQData.code, (short) p.group, p.name);
			if (aCodeRecord != null && false == aArray.contains(aCodeRecord)) {
				aArray.add(aCodeRecord);
			}
		}
		return aArray;
	}

	/**
	 * 获取热炒合约列表 规则（持仓量+成交量）
	 * 
	 * @param num
	 *            热炒合约个数
	 * @return
	 */
	public ArrayList<TagLocalStockData> getHotOptionList(int num) {
		ArrayList<TagLocalStockData> aArray = new ArrayList<TagLocalStockData>();
		for (int m = 0; m < m_codeList.size(); m++) {
			TagLocalStockData p = new TagLocalStockData();
			p = m_codeList.get(m);
			int iCount = aArray.size();
			int i = 0;
			for (i = 0; i < iCount; i++) {
				if ((p.HQData.volume + p.HQData.dOpenInterest) > (aArray.get(i).HQData.volume + aArray
						.get(i).HQData.dOpenInterest)) {
					while (aArray.size() >= num) {
						aArray.remove(aArray.size() - 1);
					}
					aArray.add(i, p);

					break;
				}
			}
			if (i == iCount) {
				if (aArray.size() < num) {
					aArray.add(p);
				}
			}
		}
		return aArray;
	}

	public void sort() {
		if (m_codeList.size() > 1) {
			try {
				Comparator<TagLocalStockData> comp = new SortComparator();
				Collections.sort(m_codeList, comp);

				Comparator<TagLocalStockData> compbyStrikeDate = new SortComparatorByStrikeDate();
				Collections.sort(m_codeList, compbyStrikeDate);
			} catch (Exception e) {
				L.e("CHQData", "sort option list exception");
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public class SortComparator implements Comparator {
		@Override
		public int compare(Object obj1, Object obj2) {
			TagLocalStockData a = (TagLocalStockData) obj1;
			TagLocalStockData b = (TagLocalStockData) obj2;

			return a.name.compareTo(b.name);
		}
	}

	@SuppressWarnings("rawtypes")
	public class SortComparatorByStrikeDate implements Comparator {
		@Override
		public int compare(Object obj1, Object obj2) {
			TagLocalStockData a = (TagLocalStockData) obj1;
			TagLocalStockData b = (TagLocalStockData) obj2;

			int cmp = Integer.valueOf(a.market).compareTo(
					Integer.valueOf(b.market));
			if (cmp == 0) {
				cmp = a.optionData.StockCode.compareTo(b.optionData.StockCode);
				if (cmp == 0) {
					cmp = Integer
							.valueOf(a.optionData.StrikeDateNoDay)
							.compareTo(
									Integer.valueOf(b.optionData.StrikeDateNoDay));

					if (cmp == 0) {
						cmp = Float
								.valueOf(a.optionData.StrikePrice)
								.compareTo(
										Float.valueOf(b.optionData.StrikePrice));
					}
				}
			}

			return cmp;
		}
	}
}
