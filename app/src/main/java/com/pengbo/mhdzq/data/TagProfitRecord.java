package com.pengbo.mhdzq.data;

public class TagProfitRecord {
	
	public	static final int   ProfitAnalyse_TableRowCount	= 11;    //价格变动档位总数，相当于显示当前价格以及上下各5档
	public	static final float ProfitAnalyse_TablePriceRate	= 0.05f;  //每挡价格变动百分比，0.05相当于5%

	public	float price;    //价格
	public	float syl;      //收益率
	public	double sy;      //收益
	public	float rate;     //发生概率
}
