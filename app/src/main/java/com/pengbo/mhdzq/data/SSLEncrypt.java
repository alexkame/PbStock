package com.pengbo.mhdzq.data;

public class SSLEncrypt {
	
	public	static final int  SSLKEY_INDEX_HQ		 = 0;    //用于行情加解密的key的索引
	public	static final int  SSLKEY_INDEX_TRADE	 = 1;    //用于交易加解密的key的索引
	public	static final int  SSLKEY_INDEX_TRADE_PWD = 2;    //用于交易里密码字段加解密的key的索引
	
	static public native void DesSetKey(String inKey, int nKeyIndex);
	static public native long DesNcbcEncrypt(int nKeyIndex,byte[] input, byte[] output,long length,int enc/*1:encrypt 0:decrypt*/);
	
	static public native int RSAPublicEncrypt(byte[] input, byte[] output,int length,int padding);
	
	static public native boolean IsOffical(String md5, String sha1, String sha256);
	
	static public native String GetPbBarray();
}
