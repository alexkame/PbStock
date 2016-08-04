package com.pengbo.mhdzq.data;

import java.io.Serializable;

public class CCodeTableItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public short market; // 市场，为本地类型
	public String code; // 代码
	public short group; // 类别，为本地类型
	public String name; // 名称
    
    //对应LOCAL_CODETABLE_RECORD数据
	public short    GroupOffset;     //分类号
	public short	PriceDecimal;  //显示价格小数位
	public int		PriceRate; //价格放大倍率
	public short	VolUnit;   //每手股数
	public int    Multiplier; //合约乘数，计划均价是用到
	
	public String groupCode;
    
    //对应ST_MKT_GROUP_INFO_UNICODE数据
	public String	extCode;	   //交易所代码
	public short	ContractCRC;     //商品信息校验和
	public short	ContractUpdate;  //商品代码状态 0-更新，1-新增，2-删除
	public short GroupFlag; //指数或一般品种

	public CCodeTableItem(){
		code = new String();
		name = new String();
		extCode = new String();
		groupCode = new String();
	}
	
	public void copyData(CCodeTableItem aStockData)
	{
		market = aStockData.market;
		code = new String(aStockData.code);
		group = aStockData.group;
		name = new String(aStockData.name);

		GroupOffset = aStockData.GroupOffset;
	    PriceDecimal = aStockData.PriceDecimal;
	    PriceRate = aStockData.PriceRate;
	    VolUnit = aStockData.VolUnit;
	    extCode = new String(aStockData.extCode);
	    groupCode = new String(aStockData.groupCode);
	    ContractCRC = aStockData.ContractCRC;
	    ContractUpdate = aStockData.ContractUpdate;
	    GroupFlag = aStockData.GroupFlag;
	    Multiplier = aStockData.Multiplier;
	}
	
}
