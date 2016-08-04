package com.pengbo.mhdzq.data;

import java.util.ArrayList;

public class CPbDataItem {
		public CPbDataItem()
		{
			m_ItemType = DIT_NORMAL;
			nSubFields = 0;
			nArraySize = 0;
			m_bNeedMaskField = true;
		}
	    CPbDataItem(CPbDataItem aDataItem)
	    {
	        m_ItemType = aDataItem.m_ItemType;
	        m_NormalField = aDataItem.m_NormalField;
	        nSubFields = aDataItem.nSubFields;
	        nArraySize = aDataItem.nArraySize;
	        m_bNeedMaskField = aDataItem.m_bNeedMaskField;
	        m_ArrayField = aDataItem.m_ArrayField;
	        m_ArrayValue = aDataItem.m_ArrayValue;
	    }
	    public	static final int
			DIT_NORMAL = 0,
			DIT_ARRAY = 1;
		public int m_ItemType;

		public CPbDataField m_NormalField;
		public int nSubFields;
		public ArrayList<CPbDataField> m_ArrayField;
		public int nArraySize;
		public ArrayList<CPbDataField> m_ArrayValue;
		public boolean m_bNeedMaskField;

}
