package com.pengbo.mhdzq.net;

public class HQFrameHead {

	public	char			cHeadID;		// @ 
	public	short			cFrameType;		//帧类型 ,MAIN_FRAME_TYPE 定义
	public	short			wFrameCount;	//连续帧数量
	public	short			wFrameNo;		//帧号 
	public	int				wDataSize;		//帧长度, 不包括帧头长度
	public	int				RequestCode;	// 包号0-65535循环使用,静态包由客户端填写,动态数据由服务器端填写
	public	short			cErrorCode;		//返回码
	
	public	byte			Reserved;		//保留填0
	public	byte			cFlagZlibCompress;	//Zlib压缩
	public	byte			cFlagDesEncode;		//Des加密，加密后必须在MAIN_FRAME_HEAD后面紧跟着原长度DES_FRAME_HEAD
	public	byte			cFlagDynamic;		//动态包标示  1动态  0静态
	public	byte			cFlagCompress;		//压缩状态     1压缩 0不压缩
//	typedef struct
//	{
//		unsigned char Reserved : 4;			//4位保留填0
//		unsigned char cFlagZlibCompress : 1;	//Zlib压缩
//		unsigned char cFlagDesEncode : 1;	//Des加密，加密后必须在MAIN_FRAME_HEAD后面紧跟着原长度DES_FRAME_HEAD
//		unsigned char cFlagDynamic : 1;		//动态包标示  1动态  0静态
//		unsigned char cFlagCompress : 1;   //压缩状态     1压缩 0不压缩
//	}  PKG_FLAG; 
//
//	typedef struct
//	{
//		char			cHeadID;		/* @ */
//		unsigned char	cFrameType;		/* 帧类型 ,MAIN_FRAME_TYPE 定义	*/
//		unsigned short	wFrameCount;	/* 连续帧数量 */
//		unsigned short	wFrameNo;		/* 帧号 */
//		unsigned short	wDataSize;		/* 帧长度, 不包括帧头长度	*/
//		unsigned short	wPackageNo;		/* 包号0-65535循环使用,静态包由客户端填写,动态数据由服务器端填写*/
//		PKG_FLAG		cFlag;
//		unsigned char	cErrorCode;		/* 返回码 */
//	}	MAIN_FRAME_HEAD;
}
