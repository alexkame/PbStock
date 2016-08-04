package com.pengbo.mhdzq.trade.data;

/**
 *
 * @author pobo
 *
 */
public class PTK_Define {
	public static final  String TAG = PTK_Define.class.getSimpleName();
	
	// ///////////////////////////////////////////////////////////////////////
	// /报单状态
	// ///////////////////////////////////////////////////////////////////////
	// /正在申报
	public static final char PTK_OST_Inserting = '0';
	// /已报
	public static final char PTK_OST_Inserted = '1';
	// /部成
	public static final char PTK_OST_PartTraded = '2';
	// /已成
	public static final char PTK_OST_AllTraded = '3';
	// /已撤
	public static final char PTK_OST_Canceled = '4';
	// /部成部撤
	public static final char PTK_OST_PartTradedCanceled = '5';
	// /待撤
	public static final char PTK_OST_Cancelling = '6';
	// /部成待撤
	public static final char PTK_OST_PartTradedCancelling = '7';
	// /待改
	public static final char PTK_OST_Modifying = '8';
	// /废单
	public static final char PTK_OST_Error = 'e';
	// /已挂起
	public static final char PTK_OST_Halted = 'h';
	// /本地开盘触发
	public static final char PTK_OST_Pending = 'p';
	// /未知
	public static final char PTK_OST_Unknown = 'x';
/////////////////////////////////////////////////////////////////////////
///买卖方向
/////////////////////////////////////////////////////////////////////////
    ///买
	public static final char PTK_D_Buy = '0';
	public static final char PTK_D_Sell = '1';
	
/////////////////////////////////////////////////////////////////////////
///备兑
/////////////////////////////////////////////////////////////////////////
///买
	public static final char PTK_D_FBD = '0';//非备兑
	public static final char PTK_D_BD = '1';//备兑

/////////////////////////////////////////////////////////////////////////
///开平标志
/////////////////////////////////////////////////////////////////////////
    ///开仓	
	public static final char PTK_OF_Open = '0';
	///平仓
	public static final char PTK_OF_Close = '1';
	///平今
	public static final char PTK_OF_CloseToday = '2';
	///自动（仅下单时适用）
	public static final char PTK_OF_Auto = '3';
	
/////////////////////////////////////////////////////////////////////////
///报单价格条件
/////////////////////////////////////////////////////////////////////////
	///限价
	public static final char PTK_OPT_LimitPrice = '0';
	///市价
	public static final char PTK_OPT_AnyPrice = '1';

	///最优五档立即成交剩余撤单
	public static final char PTK_OPT_5FAK = 'a';
	///最优五档立即成交剩余转限价
	public static final char PTK_OPT_5FAL = 'b';
	///立即成交剩余撤单
	public static final char PTK_OPT_FAK = 'c';
	///全部成交否则撤单
	public static final char PTK_OPT_FOK = 'd';
	///对方最优价格
	public static final char PTK_OPT_DBestPrice = 'e';
	///本方最优价格
	public static final char PTK_OPT_WBestPrice = 'f';
	
	//个股期权市价委托定义
	//市价剩余转限价:
	//投资者无须设定价格，仅按照当时市场上可执行的最优报价成交（最优价为买一或卖一价）。市价订单未成交部分转限价（按成交价格申报）。
	public static final char PTK_QQ_OPT_FAL = 'o';
	//市价剩余撤销:
	//投资者无须设定价格，仅按照当时市场上可执行的最优报价成交（最优价为买一或卖一价）。市价订单未成交部分自动撤销。
	public static final char PTK_QQ_OPT_FAK	= 'p';
	//限价FOK,即全部成交否则全部撤单:
	//立即全部成交否则自动撤销指令
	public static final char PTK_QQ_OPT_FOK_XJ = 'q';
	//市价FOK,即市价全部成交否则全部撤单:
	//立即全部成交否则自动撤销指令
	public static final char PTK_QQ_OPT_FOK = 'r';
	
	//对手方最优
	public static final char PTK_QQ_OPT_DBestPrice = 's';
	//本方最优
	public static final char PTK_QQ_OPT_WBestPrice = 't';
	//即时成交剩余撤销
	public static final char PTK_QQ_OPT_FAK_SZ = 'u';
	//最优五档即时成交剩余撤销
	public static final char PTK_QQ_OPT_5FAK_SZ = 'v';
	//全额成交或撤销(深圳)
	public static final char PTK_QQ_OPT_FOK_SZ = 'w';
	
	// ///////////////////////////////////////////////////////////////////////
	// /非交易申请类别
	// ///////////////////////////////////////////////////////////////////////
	// /
	public static final char PTK_FJYLB_ZX = '1';//执行
	public static final char PTK_FJYLB_FQ = '2';//放弃
	public static final char PTK_FJYLB_SD = '3';//锁定
	public static final char PTK_FJYLB_JS = '4';//解锁
	
}
