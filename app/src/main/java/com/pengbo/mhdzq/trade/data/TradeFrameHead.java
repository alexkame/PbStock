package com.pengbo.mhdzq.trade.data;

public class TradeFrameHead {

	public short	sign;		//协议标识，目前定义为66
	public byte		crypt;		//0表示未加密，1表示数据加密了
	public byte		unused;		//未使用
	public short	PackageNum;	//通讯包总数
	public short	PackageNo;	//当前通讯包序号，从0开始，当为PackageNum-1时表示是最后一个通讯包
	public int		CheckCode;	//校验码（对通讯包体进行CRC16校验，在加密压缩后）
	public int		PackageSize;//包体的长度，加密压缩后
	
	public byte     zip;		//0表示未压缩，1表示zlib压缩
	public byte		zipunused;	//未使用
	public int		zipdatasize;//数据压缩后的长度
}

//typedef struct
//{
//	BYTE                           zip:2;			//0表示未压缩，1表示zlib压缩
//	BYTE                           unused:6;        //未使用
//	UINT16						   zipdatasize;     //数据压缩后的长度
//}PACKED PB_ZipHead;   //3byte
//
//typedef struct
//{
//	BYTE							sign;			//协议标识，目前定义为66
//	BYTE							crypt:2;		//0表示未加密，1表示数据加密了
//	BYTE							unused:6;		//未使用
//	BYTE							PackageNum;		//通讯包总数
//	BYTE							PackageNo;		//当前通讯包序号，从0开始，当为PackageNum-1时表示是最后一个通讯包
//	UINT16							CheckCode;		//校验码（对通讯包体进行CRC16校验，在加密压缩后）
//	UINT16							PackageSize;	//包体的长度，加密压缩后
//}PACKED PB_FrameHead;	//8byte