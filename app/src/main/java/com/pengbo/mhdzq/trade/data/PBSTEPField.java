package com.pengbo.mhdzq.trade.data;



public class PBSTEPField extends TListBuffer{
	
	//设置需计算的列时会用到的字段数据类别
	public final static int 	FIELD_TYPE_INT64		= 1;		//64位整形
	public final static int 	FIELD_TYPE_DOUBLE		= 2;		//double
	public final static int 	FIELD_TYPE_STRING		= 3;		//字符串，还需指明长度，长度包含结束符
	
	public	int						m_FieldId;		//字段ID
	public	int		m_FieldType;		//字段数据类别
	//字段宽度用基类的m_lItemSize
	public	PBSTEPField()
	{
		m_FieldId	= 0;
		m_FieldType	= FIELD_TYPE_STRING;
	}
	public	PBSTEPField(long lBufferItem, long lItemSize, long lAddNum)
	{
		m_FieldId	= 0;
		m_FieldType	= FIELD_TYPE_STRING;
	}
	public	void		Init()
	{
		m_FieldId	= 0;
		m_FieldType	= FIELD_TYPE_STRING;
	}
	public void		Set(int id, int type, int length)
	{
		m_FieldId	= id;
		m_FieldType	= type;
	}
}
