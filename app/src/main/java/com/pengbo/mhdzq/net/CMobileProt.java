package com.pengbo.mhdzq.net;

import com.pengbo.mhdzq.tools.L;




/**
 * 请求网路数据 解压缩包 
 * @author pobo
 *
 */
public class CMobileProt {
	/**********获得类的简写名称*************/
	public static final String TAG=CMobileProt.class.getSimpleName();
	
	
	/**********提供无参构造函数************/
	public CMobileProt() {
	}
	
	/**************  生成 加密 请求包 *************/
	// 生成一个加密请求包
	// 主要是设置包头
	// datasize为包的长度，含包头和包体
	// encrypt可取0和1，分别表示不加密和加密
	//int MainType, int ChildType,int PageNo, int SessionID, int RequestCode, byte[] data,int offset, int datasize, int encrypt
	public static int MakeEncryptPackage(int MainType, int ChildType,int PageNo, 
			int SessionID, int RequestCode, byte[] data,int offset, 
			int datasize, int encrypt){
		L.i(TAG, "send -- MainType: " + MainType + " ChildType: "
				+ ChildType);
		return MakeEncryptPackage((byte) MainType, (byte) ChildType,
				(byte) PageNo, SessionID, RequestCode, data, offset, datasize,
				(byte) encrypt);
		
	}
	
	public static int MakeEncryptPackage(byte MainType, byte ChildType,
			byte PageNo, int SessionID, int RequestCode, byte[] data,
			int offset, int datasize, byte encrypt){
		return 0;
		
	}
	
	/************ 一个 包 请求   Make  Package  *********/
	public static int MakePackage(int MainType, int ChildType, int PageNo,
			int SessionID, int RequestCode, byte[] data, int offset,
			int datasize, int encrypt){
		return 0;
		
	}
	
	/********   多 包  请求  ***********/
	public static int MakeMultiPackage(){
		return 0;
		
	}
	
	/***********  数据  检查  *********/
	public static int CheckData(){
		return 0;
		
	}
	
	/***********单个 行情数据的请求 ************/	
	public static  int MakeRequest_Single_10(){
		return 0;		
	}
	
	
	
	/**********解析 单个  行情数据************/
	
	public static int Analy_Single_10(){
		return 0;		
	}
	
	/**********请求代码表 **********/
	
	public static int MakeRequest_Code_11(){
		return 0;		
	}
	
	
	/***********解析代码表数据  ****************/
	
	public static int Analy_Code_11(){
		return 0;
		
	}
	
	/***********多个行情数据的请求 (有推送  )************/	
	//多行情的参数		   	 int SessionID, int RequestCode,
	//	int pageNo, STOCKINFO[] stock, int num, int fix_num, byte[] fix,
	//	int push_num, byte[] push, byte[] data, int offset
	public static int MakeRequest_Multi_12(){
		return 0;		
	}
	
	/************解析多行情数据********************/
	public static int Analy_Multi_12(){
		return 0;
		
	}

	/***************多行情数据的请求    （无   推送   ）*************/
	public static int MakeRequest_Multi_36(){
		return 0;
		
	}
	
	
	/****************解析  多行情数据  （无 推送  ）****************/
	
	public static int Analy_Multi_36(){
		return 0;
		
	}
	
	
	/**
	 * 走势   数据 的请求  	 
	 * @param SessionID
	 * @param RequestCode
	 * @param pageNo
	 * @param market
	 * @param code
	 * @param firsttime  请求时间点
	 * @param field_num   字段个数
	 * @param fieldid   字段 ID  
	 * @param data      
	 * @param offset   缓冲偏移位置 
	 * @return
	 */
	public static int MakeRequest_Trend_33(int SessionID, int RequestCode,
			int pageNo, int market, String code, int firsttime, int field_num,
			byte[] fieldid, byte[] data, int offset){
		return 0;
		
	}
	

	
	/***********   解析  走势  行情数据      ----33  ***************/
	
	public static int Analy_Trend_33(){
		return 0;		
	}
	
	
	/************  解析 走势  行情 数据  -----13***************/
	
	public static  int Analy_Trend_13(){
		return 0;
		
	}
	
	
	
	/**************  明细  数据 请求   *********************/
	
	public static int MakeRequest_Detail_14(){
		return 0;
		
	}
	
	
	/**************  解析  明细  数据    **********************/
	public static int Analy_Detail_14(){
		return 0;		
	}
	
	
	
	
	/**************  K  线  数据请求 (无  推送  )*************/
	//int SessionID, int RequestCode,int pageNo, int market, String code, int style, int period,int startdate, int lastdate, int num, int field_num,byte[] fieldid, byte[] data, int offset
	public static int MakeRequest_K_31(){
		return 0;		
	}
	
	/***************   解析   K 线  数据 （无推送 ）******************/
	public static int Analy_K_31(){
		return 0;		
	}

	/***************** K  线 数据  请求   （ 有   推送  ）***************/
	//int SessionID, int RequestCode,int pageNo, int market, String code, int style, int period,int startdate, int lastdate, int num, int field_num,byte[] fieldid, int push_num, byte[] pushid, byte[] data, int offset
	public static int MakeRequest_K_32(){
		return 0;
		
	}
	
	/**********   解析   K 线  数据  （有推送 ）*****/

	public static int Analy_K_32(){
		return 0;		
	}

	
	
	/***************    请求 排行   数据  *************/
	public static int MakeRequest_SortData_17(){
		return 0;
		
	}
	
	/*************     解析 排行 数据   *******************/

	public static  int Analy_SortData_17(){
		return 0;
		
	}
	/*************   修改   走势  数据   ****************/
	
	public static void UpdateTrendData(){
		
	}
	
	/************    修改  明细   数据  ******************/
	
	public static void UpdataDetailData(){
		
	}
	
	
	/************   上传    自选股    请求   ***********/
	//int SessionID, int RequestCode,int pageNo, int ver, int type, String user, String version,int mtype, STOCKINFO[] stock, int num, byte[] data, int offset,int packageNum, int packageNo
	public static int MakeRequest_UpOptionalStock_51(){
		return 0;
		
	}

	/*************   解析  上传自选股  数据   ***************/
	public static int Analy_UpOptionalStock_51(){
		return 0;
		
	}
	

	/************   下载   自选股    请求   ***********/
	//int SessionID, int RequestCode,int pageNo, int ver, int type, long time, String user,String version, int mtype, byte[] data, int offset
	public static int MakeRequest_DownOptionalStock_52(){
		return 0;
		
	}

	/*************   解析  下载自选股  数据   ***************/
	public static int Analy_DownOptionalStock_52(){
		return 0;
		
	}
	
	/**************  自选股  同步跟新   *************/
	
	public  static void Analy_synchronization_53(){ 
		
	}
	
	
	/**************   请求 大盘指数  行情  推送  ***************/
	
	public static int MakeRequest_MarketIndex_22(){
		return 0;
		
	}
	
	/**************   解析    大盘指数 行情 推送的  数据 *********/
	public static int Analy_MarketIndex_22(){
		return 0;
		
	}
	
	/**************  请求推送大盘行情数据(增加涨跌家数) *******/
	
	public static int MakeRequest_MarketIndex_23(){
		return 0;
		
	}
	
	/***************  解析 大盘行情数据(增加涨跌家数)**********/
	public static int Analy_MarketIndex_23(){
		return 0;
		
	}
	
	/*****************************/
	public static int MakeRequest_24(){
		return 0;
		
	}
	
	public static int Analy_24(){
		return 0;
		
	}
	
	
	
}
