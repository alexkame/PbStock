package com.pengbo.mhdzq.data;

import java.util.ArrayList;


public class CPbDataPackage {
	
	public CPbDataPackage()
		{
			m_wPackageID = 0;
			m_bNeedMaskField = true;
			m_nItemSize = 0;
			m_DataItems = new ArrayList<CPbDataItem>();
		}
	public CPbDataPackage(CPbDataPackage aDataPackage)
	    {
	        m_wPackageID=aDataPackage.m_wPackageID;
	        m_szPackageName=aDataPackage.m_szPackageName;
	        m_szPackageVersion = aDataPackage.m_szPackageVersion;
	        m_bNeedMaskField=aDataPackage.m_bNeedMaskField;
	        m_nItemSize=aDataPackage.m_nItemSize;
	        m_DataItems=aDataPackage.m_DataItems;
	    }
	    
		public int m_wPackageID;
		public String m_szPackageName;
		public String m_szPackageVersion;
		public boolean m_bNeedMaskField;
		
		public int m_nItemSize;
		public ArrayList<CPbDataItem> m_DataItems;

		public CPbDataField GetNormalField(int index)
		{
			if(index >= m_nItemSize || m_DataItems.get(index).m_ItemType != CPbDataItem.DIT_NORMAL)
				return null;

			return m_DataItems.get(index).m_NormalField;

		}
		public CPbDataField GetArrayField(int index, int row, int col)
		{
			if(index >= m_nItemSize || m_DataItems.get(index).m_ItemType != CPbDataItem.DIT_ARRAY)
				return null;

			if(row >= m_DataItems.get(index).nSubFields || col >= m_DataItems.get(index).nArraySize)
				return null;

			return m_DataItems.get(index).m_ArrayValue.get(col*m_DataItems.get(index).nSubFields+row);	

		}
		public CPbDataField GetNormalFieldByID(int fieldid)
		{
			int i = 0;
			for( ; i < m_nItemSize; ++i)
			{
				if(m_DataItems.get(i).m_ItemType == CPbDataItem.DIT_NORMAL)
				{
					if(m_DataItems.get(i).m_NormalField.m_FieldID == fieldid)
						return m_DataItems.get(i).m_NormalField;
				}
			}

			return null;
		}
		public CPbDataField GetArrayFieldByID(int fieldid, int nArrayIndex/*Package里第nArrayIndex个数组*/)
		{
			int i = 0;
			for( ; i < m_nItemSize; ++i)
			{
				if(m_DataItems.get(i).m_ItemType == CPbDataItem.DIT_ARRAY)
				{
					int j = 0;
					for( ; j < m_DataItems.get(i).nSubFields; ++j)
					{
						if(m_DataItems.get(i).m_ArrayField.get(j).m_FieldID == fieldid)
							return m_DataItems.get(i).m_ArrayValue.get(nArrayIndex*m_DataItems.get(i).nSubFields+j);
					}
				}
			}

			return null;
		}
	    
//		public CPbDataField GetSelArrayFieldByID(int fieldid, int col ,int nArrayIndex/*Package里第nArrayIndex个数组*/)
//		{
//			
//		}
		public int GetArraySize(int nArrayIndex/*Package里第nArrayIndex个数组*/)
		{
		    int nCount = 0;
		    int nIndex = 0;
		    for(int i = 0 ; i < m_nItemSize; ++i)
			{
				if(m_DataItems.get(i).m_ItemType == CPbDataItem.DIT_ARRAY)
				{
		            if (nIndex == nArrayIndex) {
		                nCount = m_DataItems.get(i).nArraySize;
		                break;
		            }
					nIndex++;
				}
			}
		    return nCount;

		}
}
