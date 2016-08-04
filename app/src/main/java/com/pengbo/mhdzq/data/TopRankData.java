package com.pengbo.mhdzq.data;

public class TopRankData {

    /**
     * 排名数据
	 * auth:pobo
	 */

	public String   name; //名称
	public String	code;//代码 id = 10
    public short	market;			//市场 id = 11
    public short    sortField;      //排名字段 id=801
    public float	fSortValue;     //排名数值  id=802
    public int		nLastClose;		//昨日收盘价*10000  id=23
    public int		nLastClear;		//昨日结算价  id=24
    public int 		nLastPrice;		//现价 id = 29
   
	
	public TopRankData()
	{
		name = new String();
		code = new String();
	}
	public void copyData(TopRankData aRecord)
	{
		code = new String(aRecord.code);
		market = aRecord.market;
		sortField = aRecord.sortField;
		fSortValue = aRecord.fSortValue;
		nLastClose = aRecord.nLastClose;
		nLastClear = aRecord.nLastClear;
		nLastPrice = aRecord.nLastPrice;
	}
}
