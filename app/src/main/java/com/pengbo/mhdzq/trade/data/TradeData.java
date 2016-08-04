package com.pengbo.mhdzq.trade.data;

import java.util.ArrayList;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.tools.DataTools;



public class TradeData {
		
	public String	zjzh;	   //资金账号
	public String	session;   //会话号
	
	public 	int 			mTradeLockTimeout;
	public 	String			mTradeVersion;
	public 	String			mTradeAccount;
	public	String			mTradePassword;
	public	boolean			mTradeLoginFlag;
	public  int             mHQType;
	public  boolean     mReconnected;
	
    private PBSTEP		m_stepGDZH;
    private PBSTEP		m_stepMoney;//资金
    private PBSTEP		m_stepHoldStock;//持仓数据
    private PBSTEP		m_stepDRWT;//当日委托
    private PBSTEP		m_stepDRCJ;//当日成交
    private PBSTEP      m_stepLSWT;//历史委托
    private PBSTEP      m_stepLSCJ;//历史成交
    private PBSTEP      m_stepWT;//委托下单
    private PBSTEP		m_stepXQWT;//行权委托
    private PBSTEP		m_stepKXQSL;//可行权数量
    private PBSTEP		m_stepFJYWT;//非交易委托
    private PBSTEP      m_stepXQZP;//行权指派查询
    private PBSTEP      m_stepXQLSZP;//行权历史指派查询
    public PBSTEP		m_AccountInfo;
    public PBSTEP		m_stepOptionList;//期权列表
    public PBSTEP       m_stepKMSL;//可买卖数量
    public PBSTEP		m_stepYZZZ;//银证转账相关
    private PBSTEP      m_stepBDCC; //备兑持仓查询
    private PBSTEP      m_stepBDKDJSL; //备兑可冻结数量
    private PBSTEP      m_stepBDKJDSL; //备兑可解冻数量
    private PBSTEP      m_stepLSZJLS;//历史资金流水
    
    public PBSTEP       m_stepCJHB;//成交回报
	public int			m_wtIntervalTime;//拆单时多笔委托下单的间隔时间，单位毫秒    public ArrayList<TradeZJRecord> m_ZJDataList;//存储持仓显示的资金属性列表
    
    
    public ArrayList<TradeZJRecord> m_ZJDataList;//存储持仓显示的资金属性列表

	public TradeData(){

		zjzh = new String();	
		session = new String();
		
		//测试
		mTradeVersion	= "1.0";//new String();
		mTradeAccount 	= "" ;//new String();
		mTradePassword 	= "";//new String();
		mTradeLoginFlag	= false;
		mTradeLockTimeout = 5;//锁定时间
		mHQType = AppConstants.HQTYPE_NULL;
		
		m_stepGDZH = new PBSTEP();
		m_stepMoney = new PBSTEP();
		m_stepHoldStock = new PBSTEP();
		m_stepDRWT = new PBSTEP();
		m_stepWT = new PBSTEP();
		m_stepDRCJ = new PBSTEP();
		m_AccountInfo = new PBSTEP();
		m_stepOptionList = new PBSTEP();
		m_stepKMSL = new PBSTEP();
		m_stepYZZZ = new PBSTEP();
		m_stepXQWT = new PBSTEP();
		m_stepKXQSL = new PBSTEP();
		m_stepFJYWT = new PBSTEP();
		m_stepXQZP = new PBSTEP();
		m_stepXQLSZP = new PBSTEP();
		m_stepLSWT = new PBSTEP();
		m_stepLSCJ = new PBSTEP();
		m_stepCJHB = new PBSTEP();
	    m_stepBDCC = new PBSTEP();
		m_stepBDKDJSL = new PBSTEP();
		m_stepBDKJDSL = new PBSTEP();
		m_stepLSZJLS = new PBSTEP();
		
		m_ZJDataList = new ArrayList<TradeZJRecord>();
	}
	
	public void quitTrade()
	{
		MyApp.getInstance().mTradeNet.closeConnect();
		clearStepData();
	}
	
	public synchronized void clearStepData()
	{
		mTradeVersion	= "1.0";//new String();
		mTradeAccount 	= "" ;//new String();
		mTradePassword 	= "";//new String();
		mTradeLoginFlag	= false;
		mTradeLockTimeout = 5;//锁定时间
		mHQType = AppConstants.HQTYPE_NULL;
		
		m_stepGDZH.Free();
		m_stepMoney.Free();
		m_stepHoldStock.Free();
		m_stepDRWT.Free();
		m_stepWT.Free();
		m_stepDRCJ.Free();
		m_AccountInfo.Free();
		m_stepOptionList.Free();
		m_stepKMSL.Free();
		m_stepYZZZ.Free();
		m_stepXQWT.Free();
		m_stepKXQSL.Free();
		m_stepFJYWT.Free();
		m_stepXQZP.Free();
		m_stepXQLSZP.Free();
		m_stepLSWT.Free();
		m_stepLSCJ.Free();
		m_stepCJHB.Free();
		m_stepBDCC.Free();
		m_stepBDKDJSL.Free();
		m_stepBDKJDSL.Free();
		m_stepLSZJLS.Free();
	}
	
	public synchronized void clearJustStepData()
	{		
		m_stepGDZH.Free();
		m_stepMoney.Free();
		m_stepHoldStock.Free();
		m_stepDRWT.Free();
		m_stepWT.Free();
		m_stepDRCJ.Free();
		m_AccountInfo.Free();
		m_stepOptionList.Free();
		m_stepKMSL.Free();
		m_stepYZZZ.Free();
		m_stepXQWT.Free();
		m_stepKXQSL.Free();
		m_stepFJYWT.Free();
		m_stepXQZP.Free();
		m_stepXQLSZP.Free();
		m_stepLSWT.Free();
		m_stepLSCJ.Free();
		m_stepCJHB.Free();
		m_stepBDCC.Free();
		m_stepBDKDJSL.Free();
		m_stepBDKJDSL.Free();
		m_stepLSZJLS.Free();
	}
	
	
	public synchronized int GetCJHB(PBSTEP aStep)
	{
		aStep.Copy(m_stepCJHB);
		return aStep.GetRecNum();
	}
	
	
	public synchronized int GetYZZZ(PBSTEP aStep)
	{
		aStep.Copy(m_stepYZZZ);
		return aStep.GetRecNum();
	}
	public synchronized int GetKMSL(PBSTEP aStep)
	{
		aStep.Copy(m_stepKMSL);
		return aStep.GetRecNum();
	}
	public synchronized int	GetGDZH(PBSTEP aStep)
	{
		aStep.Copy(m_stepGDZH);
		return aStep.GetRecNum();
	}
	public synchronized int	GetMoney(PBSTEP aStep)
	{
        aStep.Copy(m_stepMoney);
		return aStep.GetRecNum();
	}
	public synchronized int	GetHoldStock(PBSTEP aStep)
	{
        aStep.Copy(m_stepHoldStock);
		return aStep.GetRecNum();
	}
	public synchronized int	GetLSWT(PBSTEP aStep)
	{
        aStep.Copy(m_stepLSWT);
		return aStep.GetRecNum();
	}
	public synchronized int	GetLSCJ(PBSTEP aStep)
	{
        aStep.Copy(m_stepLSCJ);
		return aStep.GetRecNum();
	}
	public synchronized int	GetDRWT(PBSTEP aStep)
	{
        aStep.Copy(m_stepDRWT);
		return aStep.GetRecNum();
	}
	public synchronized int GetWT(PBSTEP aStep)
	{
		aStep.Copy(m_stepWT);
		return aStep.GetRecNum();
	}
	
	public synchronized int	GetDRWT_CD(PBSTEP aStep)//当日委托中可撤单的列表
	{
        aStep.Copy(m_stepDRWT);
        
        int nIndex = 0;
        while (aStep.GetRecNum() > 0) {
            
            aStep.GotoRecNo(nIndex);
            String wtzt		= aStep.GetFieldValueString(STEP_Define.STEP_WTZT);
            if (!DataTools.isCDStatusEnabled(wtzt)) {
                
                nIndex = aStep.DeleteCurrRecord();
            }
            else
            {
                nIndex++;
            }
            
            if (nIndex == -1 || nIndex >= aStep.GetRecNum()) {
                break;
            }
        }
		return aStep.GetRecNum();
	}
	public synchronized int	GetDRWT_CJ(PBSTEP aStep)//当日委托中已成交的列表
	{
        aStep.Copy(m_stepDRWT);
        
        int nIndex = 0;
        while (aStep.GetRecNum() > 0) {
            
            aStep.GotoRecNo(nIndex);
            String wtzt		= aStep.GetFieldValueString(STEP_Define.STEP_WTZT);
            if (!DataTools.isTradeSucceed(wtzt))
            { 
                nIndex = aStep.DeleteCurrRecord();
            }
            else
            {
                nIndex++;
            }
            
            if (nIndex == -1 || nIndex >= aStep.GetRecNum()) {
                break;
            }
        }
		return aStep.GetRecNum();
	}
	
	public synchronized int	GetDRCJ(PBSTEP aStep)
	{
        aStep.Copy(m_stepDRCJ);
		return aStep.GetRecNum();
	}
	
	public synchronized int	GetXQWT(PBSTEP aStep)
	{
        aStep.Copy(m_stepXQWT);
		return aStep.GetRecNum();
	}
	
	public synchronized int	GetKXQSL(PBSTEP aStep)
	{
        aStep.Copy(m_stepKXQSL);
		return aStep.GetRecNum();
	}
	
	//bSDJS - true: 3 锁定 4 解锁; false-1 执行 2 放弃
	public synchronized int	GetFJYWT(PBSTEP aStep, boolean bSDJS)
	{
        aStep.Copy(m_stepFJYWT);
        int nIndex = 0;
        while (aStep.GetRecNum() > 0) {
            
            aStep.GotoRecNo(nIndex);
            String wtlb		= aStep.GetFieldValueString(STEP_Define.STEP_FJYSQLB);
            if(bSDJS)
            {
            	//锁定解锁委托查询，是执行或放弃状态的删掉
            	if (!DataTools.isFJYWTSDJS(wtlb)) {
	                
	                nIndex = aStep.DeleteCurrRecord();
	            }
	            else
	            {
	                nIndex++;
	            }
            }else
            {
            	//执行放弃委托查询，是锁定解锁状态的删掉
	            if (!DataTools.isFJYWTEnabled(wtlb)) {
	                
	                nIndex = aStep.DeleteCurrRecord();
	            }
	            else
	            {
	                nIndex++;
	            }
            }
            
            if (nIndex == -1 || nIndex >= aStep.GetRecNum()) {
                break;
            }
        }
		return aStep.GetRecNum();
	}
	
	public synchronized int GetXQZP(PBSTEP aStep)
	{
		aStep.Copy(m_stepXQZP);
		return aStep.GetRecNum();
	}
	
	public synchronized int GetXQLSZP(PBSTEP aStep)
	{
		aStep.Copy(m_stepXQLSZP);
		return aStep.GetRecNum();
	}
	
	public synchronized int GetBDCC(PBSTEP aStep)
	{
		aStep.Copy(m_stepBDCC);
		return aStep.GetRecNum();
	}
	public synchronized int GetBDKDJSL(PBSTEP aStep)
	{
		aStep.Copy(m_stepBDKDJSL);
		return aStep.GetRecNum();
	}
	public synchronized int GetBDKJDSL(PBSTEP aStep)
	{
		aStep.Copy(m_stepBDKJDSL);
		return aStep.GetRecNum();
	}

	public synchronized int GetLSZJLS(PBSTEP aStep)
	{
		aStep.Copy(m_stepLSZJLS);
		return aStep.GetRecNum();
	}

	public synchronized void SetCJHB(PBSTEP aStep)
	{
		m_stepCJHB.Copy(aStep);
	}
	
	public synchronized void SetYZZZ(PBSTEP aStep)
	{
		m_stepYZZZ.Copy(aStep);
	}
	public synchronized void SetKMSL(PBSTEP aStep)
	{
		m_stepKMSL.Copy(aStep);
	}
	public synchronized void SetDRWT(PBSTEP aStep)
    {
        m_stepDRWT.Copy(aStep);
    }
	public synchronized void SetDRWTWithPackage(char[] data, int size)
    {
        m_stepDRWT.SetPackage(data, size);
    }
	public synchronized void SetDRCJ(PBSTEP aStep)
    {
        m_stepDRCJ.Copy(aStep);
    }
	public synchronized void SetDRCJWithPackage(char[] data, int size)
    {
        m_stepDRCJ.SetPackage(data, size);
    }
	public synchronized void SetLSWT(PBSTEP aStep)
    {
        m_stepLSWT.Copy(aStep);
    }
	public synchronized void SetLSWTWithPackage(char[] data, int size)
    {
        m_stepLSWT.SetPackage(data, size);
    }
	public synchronized void SetLSCJ(PBSTEP aStep)
    {
        m_stepLSCJ.Copy(aStep);
    }
	public synchronized void SetLSCJWithPackage(char[] data, int size)
    {
        m_stepLSCJ.SetPackage(data, size);
    }
	public synchronized void SetMoney(PBSTEP aStep)
    {
        m_stepMoney.Copy(aStep);
    }
	public synchronized void SetMoneyWithPackage(char[] data, int size)
    {
        m_stepMoney.SetPackage(data, size);
    }
	public synchronized void SetHoldStock(PBSTEP aStep)
    {
		m_stepHoldStock.Copy(aStep);
    }
	public synchronized void SetHoldStockWithPackage(char[] data, int size)
    {
		m_stepHoldStock.SetPackage(data, size);
    }
	public synchronized void SetWT(PBSTEP aStep)
    {
		m_stepWT.Copy(aStep);
    }
	public synchronized void SetWTWithPackage(char[] data, int size)
    {
		m_stepWT.SetPackage(data, size);
    }
	public synchronized void SetGDZH(PBSTEP aStep)
    {
		m_stepGDZH.Copy(aStep);
    }
	public synchronized void SetGDZHWithPackage(char[] data, int size)
    {
		m_stepGDZH.SetPackage(data, size);
    }
	public synchronized void SetAccountInfo(PBSTEP aStep)
    {
		m_AccountInfo.Copy(aStep);
    }
	public synchronized void SetAccountInfoWithPackage(char[] data, int size)
    {
		m_AccountInfo.SetPackage(data, size);
    }
	public synchronized void SetOptionList(PBSTEP aStep)
    {
		m_stepOptionList.Copy(aStep);
		for(int i = 0; i < m_stepOptionList.GetRecNum(); i++)
		{
			m_stepOptionList.GotoRecNo(i);
			String code = m_stepOptionList.GetFieldValueString(STEP_Define.STEP_HYDM);
			code = code.replace("(", "");
			code = code.replace(")", "");
			m_stepOptionList.AddFieldValueString(STEP_Define.STEP_BD_HYDM, code);
		}
    }

	public synchronized void SetOptionListWithPackage(char[] data, int size)
    {
		m_stepOptionList.SetPackage(data, size);
    }
	
	public synchronized void SetXQWT(PBSTEP aStep)
    {
		m_stepXQWT.Copy(aStep);
    }
	public synchronized void SetXQWTWithPackage(char[] data, int size)
    {
		m_stepXQWT.SetPackage(data, size);
    }
	
	public synchronized void SetKXQSL(PBSTEP aStep)
    {
		m_stepKXQSL.Copy(aStep);
    }
	public synchronized void SetKXQSLWithPackage(char[] data, int size)
    {
		m_stepKXQSL.SetPackage(data, size);
    }
	
	public synchronized void SetFJYWT(PBSTEP aStep)
    {
		m_stepFJYWT.Copy(aStep);
    }
	public synchronized void SetFJYWTWithPackage(char[] data, int size)
    {
		m_stepFJYWT.SetPackage(data, size);
    }
	
	public synchronized void SetXQZP(PBSTEP aStep)
    {
		m_stepXQZP.Copy(aStep);
    }
	public synchronized void SetXQZPWithPackage(char[] data, int size)
    {
		m_stepXQZP.SetPackage(data, size);
    }
	
	public synchronized void SetXQLSZP(PBSTEP aStep)
    {
		m_stepXQLSZP.Copy(aStep);
    }
	public synchronized void SetXQLSZPWithPackage(char[] data, int size)
    {
		m_stepXQLSZP.SetPackage(data, size);
    }
	
	public synchronized void SetBDCC(PBSTEP aStep)
    {
		m_stepBDCC.Copy(aStep);
    }
	public synchronized void SetBDCCWithPackage(char[] data, int size)
    {
		m_stepBDCC.SetPackage(data, size);
    }
	public synchronized void SetBDKDJSL(PBSTEP aStep)
    {
		m_stepBDKDJSL.Copy(aStep);
    }
	public synchronized void SetBDKDJSLWithPackage(char[] data, int size)
    {
		m_stepBDKDJSL.SetPackage(data, size);
    }
	public synchronized void SetBDKJDSL(PBSTEP aStep)
    {
		m_stepBDKJDSL.Copy(aStep);
    }
	public synchronized void SetBDKJDSLWithPackage(char[] data, int size)
    {
		m_stepBDKJDSL.SetPackage(data, size);
    }
	public synchronized void SetLSZJLS(PBSTEP aStep)
	{
		m_stepLSZJLS.Copy(aStep);
	}
	public synchronized void SetLSZJLSWithPackage(char[] data, int size)
    {
		m_stepLSZJLS.SetPackage(data, size);
    }
	
	//code-trade code
	public synchronized int GetDJSL(String code, String market,boolean bQuanli, int bdbz)
	{
	    int nDJSL = 0;
	    
	    for (int i = 0; i < m_stepDRWT.GetRecNum(); i++) {
	        
	        m_stepDRWT.GotoRecNo(i);
	        String wtzt		= m_stepDRWT.GetFieldValueString(STEP_Define.STEP_WTZT);
	        char KPPZ = m_stepDRWT.GetFieldValueCHAR(STEP_Define.STEP_KPBZ);
	        if (KPPZ == PTK_Define.PTK_OF_Close && DataTools.isCDStatusEnabled(wtzt))
	        {
	        	
	            String tempCode		= m_stepDRWT.GetFieldValueString(STEP_Define.STEP_HYDM);
	            String tempMarket	= m_stepDRWT.GetFieldValueString(STEP_Define.STEP_SCDM);
	            int nBDBZ = m_stepDRWT.GetFieldValueInt(STEP_Define.STEP_BDBZ);
	            int nMMLB = m_stepDRWT.GetFieldValueInt(STEP_Define.STEP_MMLB);
	            boolean bBuy = (nMMLB == 0?true:false);
	            
	            if (code.equalsIgnoreCase(tempCode) && market.equalsIgnoreCase(tempMarket) && bBuy != bQuanli && nBDBZ == bdbz)
	            {
	                nDJSL += m_stepDRWT.GetFieldValueInt(STEP_Define.STEP_WTSL);
	            }
	        }
	    }
	    return nDJSL;
	}
	
	//由市场获得对应股东帐号
	public synchronized String GetGDZHFromMarket(String jysc)
	{
	    String gdzh = "";
		for (int i = 0; i < m_stepGDZH.GetRecNum(); i++)
		{
			m_stepGDZH.GotoRecNo(i);
			String market = m_stepGDZH.GetFieldValueString(STEP_Define.STEP_SCDM);
			if (market != null && jysc.equalsIgnoreCase(market) == true)
			{
				gdzh = m_stepGDZH.GetFieldValueString(STEP_Define.STEP_GDH);
				break;
			}
		}
	    return gdzh;
	}
	
	public synchronized ArrayList<String> GetStockGDZHFromMarket(String jysc)
	{
	    ArrayList<String> gdlist = new ArrayList<String>();
	    
		for (int i = 0; i < m_stepGDZH.GetRecNum(); i++)
		{
			m_stepGDZH.GotoRecNo(i);
			String market = m_stepGDZH.GetFieldValueString(STEP_Define.STEP_SCDM);
			if (market != null && jysc.equalsIgnoreCase(market) == true)
			{
			    String gdzh;
				gdzh = m_stepGDZH.GetFieldValueString(STEP_Define.STEP_GDH);
				gdlist.add(gdzh);
			}
		}
	    return gdlist;
	}
	
	//由市场获得对应席位号
	public synchronized String GetXWHFromMarket(String jysc)
	{
		String xwh = "";
	    for (int i = 0; i < m_stepGDZH.GetRecNum(); i++)
	    {
	        m_stepGDZH.GotoRecNo(i);
	        String market = m_stepGDZH.GetFieldValueString(STEP_Define.STEP_SCDM);
	        if (market != null && jysc.equalsIgnoreCase(market) == true)
	        {
	        	xwh = m_stepGDZH.GetFieldValueString(STEP_Define.STEP_XWH);
	            break;
	        }
	    }
	    return xwh;
	}
	//由行情市场获得对应的交易市场
	public static String GetTradeMarketFromHQMarket(int nHQMarket,int nGroup)
	{
		String szMarket = "";
	    if (nHQMarket == HQ_Define.MARKET_SHQQ || nHQMarket == HQ_Define.MARKET_SHQQFZ) {

	        szMarket = Trade_Define.ENum_MARKET_SHQQA; 
	    }
	    else if (nHQMarket == HQ_Define.MARKET_SZQQ || nHQMarket == HQ_Define.MARKET_SZQQFZ)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_SZQQA;
	    }
	    else if (nHQMarket == HQ_Define.HQ_MARKET_SH_INT) {

	        szMarket = Trade_Define.ENum_MARKET_SHA; 
	    }
	    else if(nHQMarket == HQ_Define.HQ_MARKET_SZ_INT)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_SZA; 
	    }
	    else if (nHQMarket == HQ_Define.MARKETID_BHSP_TJ)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_BOCE;
	    }
	    else if (nHQMarket == HQ_Define.MARKETID_CFFEX0 || nHQMarket == HQ_Define.MARKETID_CFFEX1)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_CFFEX;
	    }
	    else if (nHQMarket == HQ_Define.MARKETID_CZCE0 || nHQMarket == HQ_Define.MARKETID_CZCE1
	    		|| nHQMarket == HQ_Define.MARKETID_CZCE2 || nHQMarket == HQ_Define.MARKETID_CZCE3)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_CZCE;
	    }
	    else if (nHQMarket == HQ_Define.MARKETID_GT_E)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_GT_E;
	    }
	    else if (nHQMarket == HQ_Define.MARKETID_GT_S)
	    {
	    	szMarket = Trade_Define.ENum_MARKET_GT_S;
	    }
	    else
	    {
	    	szMarket = Trade_Define.ENum_MARKET_NULL;
	    }
	   

	    
	    return szMarket;
	}
	//由交易市场获得对应的行情市场
	public static int GetHQMarketFromTradeMarket(String tradeMarket)
	{
	    int nHQMarket = AppConstants.HQ_MARKET_NULL_INT;
	    if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_SHQQA) == true) {
	        nHQMarket = AppConstants.HQ_MARKET_SHQQ_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_SZQQA) == true)
	    {
	    	nHQMarket = AppConstants.HQ_MARKET_SZQQ_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_BOCE) == true)
	    {
	    	nHQMarket = AppConstants.HQ_MARKET_BOCE_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_CFFEX) == true)
	    {
	    	nHQMarket = AppConstants.HQ_MARKET_CFFEX_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_CZCE) == true)
	    {
	    	nHQMarket = AppConstants.HQ_MARKET_CZCE_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_GT_S) == true)
	    {
	    	nHQMarket = AppConstants.HQ_MARKET_GT_S_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_GT_E) == true)
	    {
	    	nHQMarket = AppConstants.HQ_MARKET_GT_E_INT;
	    }
	    else if (tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_SHA) == true)
	    {
	    	nHQMarket = HQ_Define.HQ_MARKET_SH_INT;
	    }
	    else if(tradeMarket != null && tradeMarket.equalsIgnoreCase(Trade_Define.ENum_MARKET_SZA) == true)
	    {
	    	nHQMarket = HQ_Define.HQ_MARKET_SZ_INT;
	    }
	    
	    return nHQMarket;
	}
	
	////由行情code获得对应的交易code
	public String GetTradeCodeFromHQCode(String strHQCode, int nHQMarket)
	{
	    String tradeCode = "";
	    String tradeMarket = GetTradeMarketFromHQMarket(nHQMarket, 0);
	    for(int i = 0; i < m_stepOptionList.GetRecNum(); i++)
		{
			m_stepOptionList.GotoRecNo(i);
			String code = m_stepOptionList.GetFieldValueString(STEP_Define.STEP_BD_HYDM);
			String market = m_stepOptionList.GetFieldValueString(STEP_Define.STEP_SCDM);
			if(tradeMarket.equals(market) && strHQCode.equals(code))
			{
				tradeCode = m_stepOptionList.GetFieldValueString(STEP_Define.STEP_HYDM);
			}
		}
	    
	    return tradeCode;
	}
	//由交易code获得对应的行情code
	public String GetHQCodeFromTradeCode(String tradeCode)
	{
	    String hqCode = "";
	    hqCode = tradeCode;
	    hqCode = hqCode.replace("(", "");
	    hqCode = hqCode.replace(")", "");
	    return hqCode;
	}
}
