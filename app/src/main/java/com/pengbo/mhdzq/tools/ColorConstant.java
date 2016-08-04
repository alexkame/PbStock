package com.pengbo.mhdzq.tools;

import android.graphics.Color;

/**
 * 颜色类
 * 
 * @author pobo
 * 
 */
public class ColorConstant {

	// public static final int PRICE_UP = Color.rgb(180, 6, 27), // 涨,#b4061b
	// PRICE_DOWN = Color.rgb(123, 159, 88), // 跌,#7b9f58 没T时的红绿值

	// PRICE_UP = Color.rgb(238, 69, 80), // 涨,#b4061b
	// PRICE_DOWN = Color.rgb(48, 178, 75), // 跌,#30b24b

	// PRICE_UP = Color.rgb(234, 68, 80), // 涨,#ea4450 234 68 80
	// PRICE_DOWN = Color.rgb(48, 178, 75), // 跌,#30b24b 48 178 75
	public static final int PRICE_UP = Color.rgb(234, 68, 80), // 涨,#ea4450 234
																// 68 80
			PRICE_DOWN = Color.rgb(48, 178, 75), // 跌,#30b24b 48 178 75
			PRICE_EQUAL = Color.BLACK, // 平
			KLINE_UP = Color.rgb(180, 6, 27), // 阳线
			KLINE_DOWN = Color.CYAN, // 阴线
			DATA_NULL = Color.WHITE, // 无数据
			COLOR_ALL_RED = 0xFFD42626,// 所有红色
			COLOR_ALL_GREEN = 0xFF23A823,// 所有绿色
			COLOR_ALL_BLUE = 0xFF0D7AFF,// 所有蓝色
			COLOR_YELLOW = 0xFFEEEC81, // 黄色
			COLOR_BLUE = 0xFF38BBFF, // 蓝色
			COLOR_VOL = COLOR_YELLOW, // 量
			COLOR_AMT = COLOR_YELLOW, // 额
			COLOR_TREND = Color.argb(255, 36, 126, 234), // 走势线颜色
			COLOR_TREND_ZQ = Color.argb(255, 36, 144, 252), // 走势线颜色
			COLOR_TREND_STOCK = 0xFFCCCCCC, // 正股走势线
			COLOR_AVG = Color.rgb(255, 166, 0), // 均线颜色
			COLOR_AVG_ZQ = Color.rgb(255, 255, 0), // 均线颜色
			COLOR_TIME = Color.rgb(255, 126, 0), // 时间框背景色
			COLOR_LB = Color.rgb(255, 200, 0), // 量比
			COLOR_CCL = Color.rgb(255, 200, 0), // 持仓量
			COLOR_CCL_ZQ = Color.rgb(255, 255, 0), // 持仓量
			COLOR_TREND_ZUOBIAO = Color.rgb(208, 213, 221),// 走势图坐标线
			COLOR_TREND_ZD = Color.rgb(144, 196, 126),// 走势图0涨跌线
			COLOR_TREND_VOLUME = 0xff247eea,// 走势图量线
			COLOR_TREND_VOLUME_ZQ = 0xFFCCCCCC,// 走势图量线
			COLOR_KEYBOARD_PRICE = 0xFF999999, // 自定义键盘价格颜色

			COLOR_LINE_MA5 = Color.rgb(255, 229, 149),
			COLOR_LINE_MA10 = Color.rgb(171, 208, 252),
			COLOR_LINE_MA20 = Color.rgb(229, 171, 253),
			COLOR_LINE_MA60 = 0XFF00FF00,
			COLOR_LINE_MA120 = 0XFF696A69,
			COLOR_LINE_MA250 = 0XFFFC0000,

			COLOR_GAIN_RED = 0xFFFB494C,
			COLOR_GAIN_GREEN = 0xFF3DC33E,

			COLOR_INFOBG = Color.argb(200, 195, 62, 88), // 信息栏背景色 #1C3E58

			COLOR_TECH0 = Color.WHITE, // 白
			COLOR_TECH1 = COLOR_YELLOW, // 黄
			COLOR_TECH2 = Color.MAGENTA, // 紫

			// COLOR_KLINE_RED = 0xFFFF0000,//KLINE RED
			// COLOR_KLINE_GREEN = 0xFF00FCFC,//KLINE GREEN

			COLOR_KLINE_RED = Color.rgb(234, 68, 80),// KLINE RED
			COLOR_KLINE_GREEN = Color.rgb(48, 178, 75),// KLINE GREEN

			COLOR_SHADE = Color.rgb(16, 38, 55), // 走势阴影：#102637
			COLOR_END = Color.BLACK, // ：#030e13

			RADAR_DATE = Color.rgb(232, 117, 2), // 雷达 日期颜色#e87502

			COLOR_UNSELECT = Color.rgb(250, 250, 250),
			COLOR_SELECT = Color.rgb(33, 37, 44),
			COLOR_YELLOW_X = Color.rgb(237, 138, 36),
			COLOR_BLACK_X = Color.rgb(34, 34, 34),
			COLOR_KLINE_BOUND = 0xff3c4852, COLOR_KLINE_PRICE = Color.rgb(153,
					153, 153), COLOR_CC_ITEM_BCG = Color.rgb(66, 70, 76),// 42464c
			COLOR_CC_ITEM_DAN = Color.rgb(245, 245, 250),// //#f5f5fa
			COLOR_CC_ITEM_SHUANG = Color.rgb(240, 240, 245),// //#f0f0f5
			COLOR_CC_TEXTVIEW = Color.rgb(94, 94, 99),// 5e5e63
			COLOR_YELLOW_POINT = Color.rgb(242, 165, 35),// #f2a523
			COLOR_8d9095 = Color.rgb(141, 144, 149),// #8d9095

			COLOR_ZQ_GRAY = Color.rgb(65, 65, 65),// hqgray 414141
			COLOR_ZQ_BLACK = Color.rgb(81, 81, 81),// hqblack
			COLOR_ZQ_RED = Color.rgb(212, 38, 38),// hqred
			COLOR_ZQ_GREEN = Color.rgb(29, 191, 96),// hqgreen

			COLOR_ZQ_BLUE = Color.rgb(13, 122, 255),// hqblue 0d7aff

			ZQ_COLOR_TREND=Color.rgb(253,114,12),//证券中分时图的走势线的颜色    #fe8022  改为  #fd720c(253,114,12)
	        ZQ_COLOR_TREND_FILLED=Color.rgb(255, 221, 191),//证券中分时填充色 #ffddbf
	        ZQ_COLOR_AVG=Color.rgb(173,215,154),//证券中分时图的均线的颜色    #c1c19d(193, 193, 157) 改为 # add79a(173,215,154)
	        ZQ_PRICE_UP = Color.rgb(214, 50, 50), // 涨,#d73434   (215, 52, 52)改为  d63232（214，50，50 ）
	        ZQ_PRICE_DOWN = Color.rgb(34, 191, 99), //跌,#22bf63  
	        ZQ_COLOR_KLINE_RED =Color.rgb(214, 46, 46),//KLINE RED  #d62e2e
	        ZQ_COLOR_KLINE_GREEN =Color.rgb(37, 192, 101),//所有绿色
	        
	        
	        ZQ_COLOR_LINE_MA5 =  Color.rgb(249, 239, 62),//#f9ef3e  黄 
	        ZQ_COLOR_LINE_MA10 = Color.rgb(90, 181, 245),//#5ab5f5 蓝 
	        ZQ_COLOR_LINE_MA20 = Color.rgb(250, 101, 194),//#fa65c2  玫红 
	        ZQ_COLOR_LINE_MA60 = Color.rgb(110, 103, 255),//6e67ff  紫色 
	        ZQ_COLOR_LINE_MA120 = Color.rgb(251, 121, 86),// #fb7956暂时没有 120 的均线  只有 5根   橘色 
	        ZQ_COLOR_LINE_MA250 = Color.rgb(251, 121, 86),//#fb7956  橘色  
	        
	        ZQ_COLOR_LINE_BOLL_A = Color.rgb(249, 239, 62),//#f9ef3e  黄 
	        ZQ_COLOR_LINE_BOLL_B = Color.rgb(90, 181, 245),//#5ab5f5 蓝 
	        ZQ_COLOR_LINE_BOLL_C = Color.rgb(250, 101, 194),//#fa65c2  玫红 
	        
	        
	        ZQ_COLOR_LINE_MACD_A = Color.rgb(90, 181, 245),//#5ab5f5 蓝 
	    	ZQ_COLOR_LINE_MACD_B = Color.rgb(249, 239, 62),//#f9ef3e  黄 
	    	ZQ_COLOR_LINE_MACD_C = Color.rgb(250, 101, 194),//#fa65c2  玫红 
	    	
	    	
			ZQ_COLOR_LINE_KDJ_A = Color.rgb(250, 101, 194),//#fa65c2  玫红  
	    	ZQ_COLOR_LINE_KDJ_B =  Color.rgb(90, 181, 245),//#5ab5f5 蓝 
	    	ZQ_COLOR_LINE_KDJ_C = Color.rgb(249, 239, 62),//#f9ef3e  黄  
	    	
	    	
			ZQ_COLOR_LINE_RSI_A = Color.rgb(249, 239, 62),//#f9ef3e  黄  
	    	ZQ_COLOR_LINE_RSI_B =  Color.rgb(250, 101, 194),//#fa65c2  玫红 
	    	ZQ_COLOR_LINE_RSI_C = Color.rgb(90, 181, 245),//#5ab5f5 蓝   
	    	        
	        
			ZQ_COLOR_KLINE_BOUND = Color.rgb(206,206,206),//221,221,221--dddddd   206,206,206 cecece

			ZQ_DETAIL_DOWN = Color.rgb(29,191,96),//1dbf60
			ZQ_DETAIL_UP = Color.rgb(239,81,56),//ef5138
			ZQ_DATA_NULL = Color.rgb(90,90,90), // 无数据5a5a5a
			
			ZQ_DETAIL_NULL = Color.rgb(90,90,90), // 无数据5a5a5a
			ZQ_DATA_XUDIAN = Color.rgb(250, 101, 194),//ffcac4(255,202,196)---改为 eeebe9(238,235,233) 因为eeebe9 给的不行 自己 改为了 Color.rgb(250, 101, 194),//#fa65c2  玫红 
			ZQ_DETAIL_PRICE_TOP = Color.rgb(118,118,118),//#76767  分时图左边价格  上面和 底部的 颜色 
			ZQ_DETAIL_PRICE_MIDDLE = Color.rgb(159,159,159),//#9f9f9f 分时图左边价格  中间  的 颜色
			ZQ_DETAIL_PRICE_UP_ALL_RED = Color.rgb(212, 38, 38), // 涨,#d42626 212, 38, 38
			ZQ_DETAIL_PRICE_DOWN_ALL_GREEN = Color.rgb(41, 180, 98), //跌,   #29b462 41, 180, 98
			
			ZQ_DETAIL_COLOR_TREND_VOLUME =Color.rgb(90, 181, 245),//#5ab5f5 蓝 十字光标 
			
		    ZQ_COLOR_TIME = Color.rgb(13, 122, 255) // 时间框背景色 # 0d7aff
		    
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196),
//			ZQ_DATA_XUDIAN = Color.rgb(255,202,196)
			;
			
			
			/**
			 *   <color name="zq_767676">#767676</color>
     <color name="zq_29b462">#29b462</color> 
     <color name="zq_0d7aff">#0d7aff</color>
     <color name="zq_6faefb">#6faefb</color>
     <color name="zq_fd720c">#fd720c</color>
     <color name="zq_add79a">#add79a</color>
     <color name="zq_eeebe9">#eeebe9</color>
     <color name="zq_f9ef3e">#f9ef3e</color>
     <color name="zq_5ab5f5">#5ab5f5</color>
     <color name="zq_fa65c2">#fa65c2</color>
     <color name="zq_6e67ff">#6e67ff</color>
			 */
	//0d7aff
}
