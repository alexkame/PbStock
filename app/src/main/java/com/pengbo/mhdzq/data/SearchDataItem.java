package com.pengbo.mhdzq.data;

public class SearchDataItem {
	public short market; // 市场，为本地类型
	public String code; // 代码
	public String extcode;
	public String name; // 代码
	public String jianpin;//名称简拼
	public short group; // 类别，为本地类型
	
	public SearchDataItem()
	{
		code = new String();
		extcode = new String();
		name = new String();
		jianpin = new String();
	}
}
