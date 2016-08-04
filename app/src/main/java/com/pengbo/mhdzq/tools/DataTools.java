package com.pengbo.mhdzq.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.trade.data.PTK_Define;


public class DataTools {
	
	//判断是否为指数
	public static boolean isStockIndex(TagLocalStockData stockData)
	{
		if(stockData == null) return false;
		int groupFlag = stockData.GroupFlag;
	    if (groupFlag == HQ_Define.GRPATTR_INDEX || groupFlag == HQ_Define.GRPATTR_BLKIDX || groupFlag == HQ_Define.GRPATTR_IDXFUT) {
	        return true;
	    }
	    return false;
	}
	//是否是证券
	public static boolean isStockZQ(TagLocalStockData stockData)//是否是证券
	{
		if(stockData == null) return false;
	    int nMarketType = stockData.HQData.market;
	    if (nMarketType == HQ_Define.HQ_MARKET_SH_INT || nMarketType == HQ_Define.HQ_MARKET_SZ_INT) {
	        return true;
	    }
	    return false;
	}
	
	//买卖类别是否是买入
	public static boolean isMMLB_BUY(int mmlb)
	{
		return false;
	}
	
	//买卖类别是否是卖出
	public static boolean isMMLB_SELL(int mmlb)
	{
		return false;
	}
	
	//此委托状态是否可以撤单
	public static boolean isCDStatusEnabled(String status)
	{
		boolean bRet = true;
	    if(status.equals(String.format("%c", PTK_Define.PTK_OST_AllTraded)) ||
	    		status.equals(String.format("%c", PTK_Define.PTK_OST_PartTradedCanceled)) ||
	    		status.equals(String.format("%c", PTK_Define.PTK_OST_Canceled)) ||
	    		status.equals(String.format("%c", PTK_Define.PTK_OST_Error)))
	    {
	    	bRet = false;
	    }

	    return bRet;
	}
	
	public static boolean isTraded(String status)
	{
		boolean bRet = false;
		if(status.equals(String.format("%c", PTK_Define.PTK_OST_PartTraded)) ||
				status.equals(String.format("%c", PTK_Define.PTK_OST_AllTraded)) ||
				status.equals(String.format("%c", PTK_Define.PTK_OST_PartTradedCanceled)) ||
				status.equals(String.format("%c", PTK_Define.PTK_OST_PartTradedCancelling)))
		{
				bRet = true;
		}
		return bRet;
	}
	//此委托状态是否已成交
	public static boolean isTradeSucceed(String status)
	{
		boolean bRet = false;
		if( status.equals(String.format("%c", PTK_Define.PTK_OST_AllTraded)) )
	    {
	    	bRet = true;
	    }
		
	    return bRet;
	}
	
	// 非交易委托是否是执行或者放弃
	public static boolean isFJYWTEnabled(String status) {
		boolean bRet = false;
		if (status.equals(String.format("%c", PTK_Define.PTK_FJYLB_ZX)) 
				|| status.equals(String.format("%c", PTK_Define.PTK_FJYLB_FQ))) 
		{
			bRet = true;
		}

		return bRet;
	}
	
	// 非交易委托是否是锁定或者解锁
	public static boolean isFJYWTSDJS(String status) {
		boolean bRet = false;
		if (status.equals(String.format("%c", PTK_Define.PTK_FJYLB_SD)) 
				|| status.equals(String.format("%c", PTK_Define.PTK_FJYLB_JS))) 
		{
			bRet = true;
		}

		return bRet;
	}
	
	 /** 
     * 检查手机上是否安装了指定的软件 
     * @param context 
     * @param packageName：应用包名 
     * @return 
     */  
	public static boolean isAPKAvalible(Context context, String packageName){   
        //获取packagemanager   
        final PackageManager packageManager = context.getPackageManager();  
      //获取所有已安装程序的包信息   
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);  
      //用于存储所有已安装程序的包名   
        List<String> packageNames = new ArrayList<String>();  
        //从pinfo中将包名字逐一取出，压入pName list中   
        if(packageInfos != null){   
            for(int i = 0; i < packageInfos.size(); i++){   
                String packName = packageInfos.get(i).packageName;   
                packageNames.add(packName);   
            }   
        }   
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE   
        return packageNames.contains(packageName);  
  } 
	
	/**
	 *  根据合约的名称中是否包含A到M的字母   给行权价添加 A到M的字母 
	 * @param mTagLocalStockData
	 * @return
	 */
	public static String distinguishStockName(String strName){
		if (strName == null || strName.isEmpty())
		{
			return "";
		}
		
		String str = "";
		int pos = -1;
		pos = strName.indexOf("月");
		if(pos < 0){
			return str;
		}
		for (int i = pos + 2; i < strName.length(); i++){
			char ch = strName.charAt(i);
			if(ch >= 'A'&& ch <= 'M'){
				str = String.valueOf(ch);
			}
		}
		return str;
		
	}
}
