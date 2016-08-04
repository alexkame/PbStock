package com.pengbo.mhdzq.data;


public class CPbDataField {

	public CPbDataField()
	{
		m_DataType = DF_UNKOWN;
		bVal = 0;
		wVal = 0;
		nVal = 0;
		fVal = 0;
		dVal = 0;
		strVal = "";
		m_szFieldName = "";
		m_szFieldDesc = "";
		m_FieldID = 0;
		m_bValid = false;

	}
	public CPbDataField(CPbDataField aDataField)
	    {
	        m_DataType = aDataField.m_DataType;
	        dVal = aDataField.dVal;
	        wVal = aDataField.wVal;
	        bVal = aDataField.bVal;
	        fVal = aDataField.fVal;
	        nVal = aDataField.nVal;

	        strVal = aDataField.strVal;
	        m_szFieldName = aDataField.m_szFieldName;
	        m_szFieldDesc = aDataField.m_szFieldDesc;
	        m_FieldID = aDataField.m_FieldID;
	        m_bValid = aDataField.m_bValid;
	    }
	    public	static final int
			DF_UNKOWN = 0,
			DF_INT8 = 1,
			DF_INT16 = 2,
			DF_INT32 = 3,
			DF_FLOAT = 4,
			DF_DOUBLE = 5,
			DF_STRING = 6,
			DF_WSTRING = 7;
		public	 int m_DataType;

		public int m_FieldID;
		public byte bVal;
		public int wVal;
		public int nVal;
		public float fVal;
		public double dVal;
		public String strVal;
		public boolean m_bValid;

		public String m_szFieldName;
		public String m_szFieldDesc;

		public boolean IsValid() {return m_bValid;}

		public void clearData()
		{
			bVal = 0;
			wVal = 0;
			nVal = 0;
			fVal = 0;
			dVal = 0;
			strVal = "";
		}
		public void SetInt8(byte bval) {bVal = bval; m_bValid = true;}
		public void SetInt16(int wval) {wVal = wval; m_bValid = true;}
		public void SetInt32(int nval) {nVal = nval; m_bValid = true;}
		public void SetFloat(float fval) {fVal = fval; m_bValid = true;}
		public void SetDouble(double dval) {dVal = dval; m_bValid = true;}
		public void SetString(String strval) {strVal = strval; m_bValid = true;}

		public byte GetInt8() {if(m_DataType != DF_INT8) return 0; return bVal;}
		public int GetInt16() {if(m_DataType != DF_INT16) return 0; return wVal;}
		public int GetInt32() {if(m_DataType != DF_INT32) return 0; return nVal;}
		public float GetFloat() {if(m_DataType != DF_FLOAT) return 0; return fVal;}
		public double GetDouble() {if(m_DataType != DF_DOUBLE) return 0; return dVal;}
		public String GetString() {if(m_DataType != DF_STRING && m_DataType != DF_WSTRING) return ""; if(strVal.length()>0)return strVal;return "";}

}
