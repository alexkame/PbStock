package com.pengbo.mhdzq.trade.data;

import java.util.ArrayList;

import com.pengbo.mhdzq.data.SSLEncrypt;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;

/*
服务端：
1、处理请求
SetPackage
GetFieldValueString

2、应答
Init
SetTotalRecNum
...

while(...)
{
AppendRecord
AddFieldValueString
...
}
MakeData(TListBuffer&)


客户端：
1、处理请求
Init
AppendRecord
AddFieldValueString
...

MakeData(TListBuffer&)

  2、应答
  SetPackage
  do{
	GetFieldValueString()
	...
  }while(GotoNext())
*/
public class PBSTEP {
	private final static String TAG = "PBSTEP";
	public final static int 	MAX_CALC_FIELD_NUM = 10;//设置最大可计算及排序的列
	
	class tagPBSTEPSortFieldPara
	{
		public int						id;
		public boolean					sortupdownflag;		//排序方向，true表示从高到低时，false表示从低到高

		public tagPBSTEPSortFieldPara()
		{
			Init();
		}
		public void		Init()
		{
			id				= -1;
			sortupdownflag	= false;
		}
		public void		Set(int id, boolean flag)
		{
			this.id	= id;
			this.sortupdownflag	= flag;
		}
	};
	
	int					m_iRecordNum;		//记录个数
	int					m_iTotalRecNum;		//客户端收取数据时保存的记录总数
	int					m_iRecno;			//当前记录序号，从0开始

	ArrayList<PBSTEPRecord> m_RecordPtr; 	//PBSTEPRecord*	0表示第0表示记录
	PBSTEPRecord		m_pFirstRecord;		//第一条记录
	PBSTEPRecord		m_pCurrRecord;

	int					m_CalcFieldNum;
	PBSTEPField[]		m_pCalcFieldData;	//需计算的字段及数据
	tagPBSTEPSortFieldPara	[]m_SortFunc;

	public PBSTEP()
	{
		m_RecordPtr = new ArrayList<PBSTEPRecord>();
		m_pFirstRecord	= new PBSTEPRecord();
		m_CalcFieldNum	= 0;
		m_pCalcFieldData = new PBSTEPField[MAX_CALC_FIELD_NUM];
		for (int i = 0; i < MAX_CALC_FIELD_NUM; i++)
		{
			m_pCalcFieldData[i]	= null;
		}
		
		m_SortFunc = new tagPBSTEPSortFieldPara[2];
		for (int i = 0; i < 2; i++)
		{
			m_SortFunc[i] = new tagPBSTEPSortFieldPara();
			m_SortFunc[i].Init();
		}

		Init();
	}

	public void	Init()
	{
		m_iRecordNum		= 0;
		m_iTotalRecNum		= 0;
		m_iRecno			= -1;	//默认没有记录
		
		if (null == m_pFirstRecord)
		{
			m_pFirstRecord	= new PBSTEPRecord();
		}
		m_pFirstRecord.Init();
		m_pCurrRecord		= null;
		m_RecordPtr.clear();
	}	

	public  void	Free()
	{
		m_iRecordNum		= 0;
		m_iTotalRecNum		= 0;
		m_iRecno			= -1;	//默认没有记录
		
		m_pFirstRecord		= null;
		m_pCurrRecord		= null;
		m_RecordPtr.clear();
		
		InitCalcField();
	}

	//设置数据，包括字段信息、记录等
	//返回处理的字节数，当数据不够时，返回-1，其它错误时返回其它负数
	public int	SetPackage(char[] data, int size)
	{
		Init();
		if (null == m_pFirstRecord)
		{
			return -1;
		}
		int p = 0;
		int i;
		int pstart = 0;
		int num = 0;
		for (i = 0; i < size; i++, p++)
		{
			if (data[p] == '\0' || data[p] == '\n' || i+1 == size)
			{
				num++;
				if (num == 1)
				{
					m_pFirstRecord.SetData(data, pstart, p - pstart);
				}
				else
				{
					PBSTEPRecord rec = new PBSTEPRecord();
					rec.SetData(data, pstart, p - pstart);
					m_RecordPtr.add(rec);
				}
				if (data[p] == '\0')
					break;
				pstart = p + 1;
			}
		}

		{
			m_iTotalRecNum	= m_pFirstRecord.GetFieldValueINT(7);
			m_iRecordNum	= m_pFirstRecord.GetFieldValueINT(6);

			if (m_iRecordNum > m_RecordPtr.size())
			{
				m_iRecordNum	= m_RecordPtr.size();
			}
		}

		GotoFirst();

		return size;
	}

	//从aStep里复制数据
	public void   Copy(PBSTEP aStep)
	{
	    Init();
	    if (null == aStep.m_pFirstRecord)
		{
	    	m_pFirstRecord.SetData(null, 0, 0);
		}else
		{
			m_pFirstRecord.SetData(aStep.m_pFirstRecord.GetDataPtr(),0, aStep.m_pFirstRecord.GetDataSize());
		}
	    for (int i = 0; i < aStep.GetRecNum(); i++) {
	        
			PBSTEPRecord rec = aStep.m_RecordPtr.get(i);			
			PBSTEPRecord newRecord = new PBSTEPRecord();
			newRecord.SetData(rec.GetDataPtr(),0, rec.GetDataSize());
			m_RecordPtr.add(newRecord);
	    }
	    m_iTotalRecNum = aStep.GetTotalRecNum();	    
	    m_iRecordNum	= (int)m_RecordPtr.size();

		GotoFirst();
	}
	
	//打印aStep数据
	public void   logContent()
	{
		L.d(TAG, "Start logContent");
	    for (int i = 0; i < GetRecNum(); i++) {
			PBSTEPRecord rec = m_RecordPtr.get(i);
			if (rec.GetDataPtr() != null)
			{
				L.d(TAG, "Record[" + i + "]:" + new String(rec.GetDataPtr(),0, rec.GetDataSize()));
			}else
			{
				L.d(TAG, "Record[" + i + "]:" + "rec.GetDataPtr()=null");
			}
	    }
	    L.d(TAG, "End logContent");
	}
	
	//合并aStep中的数据，如果存在相同记录(nSearchId数据相同,如果nId2>0,nId2数据也要相同)就替换成新数据，如果没有相同记录则添加新记录
	//如果nSearchId <= 0 ,则直接添加记录
	public void   updateList(PBSTEP aStep,int nSearchId,int nId2)
	{
	    for (int i = 0; i < aStep.GetRecNum(); i++) {
	        
	        boolean bNeedAdd = true;
	        PBSTEPRecord aRecord1 = aStep.m_RecordPtr.get(i);

	        if (nSearchId > 0) {
	            char[] aField1 = new char[1024];
	            aRecord1.GetFieldValueString(nSearchId, aField1, 1024);
	            if (aField1[0] == '\0') {
	                continue;
	            }
	            
	            for (int n = 0; n < m_iRecordNum; n++) {
	                
	            	PBSTEPRecord aRecord2 =  m_RecordPtr.get(n);
	                
	            	char[] aField2 = new char[1024];
	                aRecord2.GetFieldValueString(nSearchId, aField2, 1024);
	                
	                if (STD.strcmp(aField1, aField2) == 0) {
	                    
	                    if (nId2 > 0) {
	                        char[] secondField1 = new char[1024];
	                        aRecord1.GetFieldValueString(nId2, secondField1, 1024);
	                        
	                        char[] secondField2 = new char[1024];
	                        aRecord2.GetFieldValueString(nId2, secondField2, 1024);
	                        
	                        if (STD.strcmp(secondField1, secondField2) != 0)continue;
	                    }
	                    aRecord2.SetData(aRecord1.GetDataPtr(), 0,aRecord1.GetDataSize());
	                    bNeedAdd = false;
	                    break;
	                }
	            }
	        }
	        
	        if (bNeedAdd == true)
	        {
	        	PBSTEPRecord newRecord = new PBSTEPRecord();
	            newRecord.SetData(aRecord1.GetDataPtr(),0, aRecord1.GetDataSize());
	            m_RecordPtr.add(newRecord);
	            
	            m_iRecordNum	= (int)m_RecordPtr.size();
	            m_iRecno		= m_iRecordNum-1;
	            m_pCurrRecord	= newRecord;
	        }
	    }
	}
	
	//初始化当前行(最后需要调用UpdateCurrRecord())
	public void	InitRecord()
	{
		if (m_pCurrRecord != null)
			m_pCurrRecord.Init();
	}

	//添加一行
	public int	AppendRecord()
	{
		PBSTEPRecord rec = new PBSTEPRecord();
		m_RecordPtr.add(rec);
		m_iRecordNum	= m_RecordPtr.size();
		m_iRecno		= m_iRecordNum-1;
		m_pCurrRecord	= rec;

		return m_iRecordNum;
	}

	//删除当前行，并重新初始化当前行，如当前行不存在，则返回-1，如删除后已越界，返回-1
	public int	DeleteCurrRecord()
	{
		if (m_iRecno < 0 || m_iRecno >= m_iRecordNum)
		{
			return -1;
		}

		m_RecordPtr.remove(m_iRecno);
		m_iRecordNum	= m_RecordPtr.size();
		if (m_iRecno >= m_iRecordNum)
		{
			m_iRecno	= m_iRecordNum - 1;
		}

		if (m_iRecno >= 0)
		{
			m_pCurrRecord = m_RecordPtr.get(m_iRecno);
		}

		return m_iRecno;
	}

	public boolean	GotoFirst()
	{
		if (m_iRecordNum < 1)
		{
			m_iRecno		= -1;
			m_pCurrRecord	= null;
			return false;
		}

		
		m_iRecno = 0;
		m_pCurrRecord = m_RecordPtr.get(0);
		return true;
	}

	public boolean	GotoNext()
	{
		if (m_iRecno+1 == m_iRecordNum)
		{
			return false;
		}

		m_iRecno++;
		
		m_pCurrRecord = m_RecordPtr.get(m_iRecno);
		return true;
	}
	public boolean	GotoRecNo(int recno)
	{
	    if (recno == m_iRecordNum)
		{
			return false;
		}
		m_iRecno = recno;
		
		m_pCurrRecord = m_RecordPtr.get(m_iRecno);
		return true;
	}
	public int	GetCurrRecNo()
	{
		return m_iRecno;
	}

	public int	GetRecNum()
	{
		return m_iRecordNum;
	}
	public int	GetTotalRecNum()
	{
		return m_iTotalRecNum;
	}
	public void	SetTotalRecNum(int num)
	{
		m_iTotalRecNum	= num;
	}


	//字段内容信息(Set系列会查找是否有相同字段，Add系列直接在行尾添加内容)
	public void	SetFieldValueINT(int id, int value)	//原样设置值
	{
		String str = String.valueOf(value);
		SetFieldValueString(id, str.toCharArray());
	}
	public void	SetFieldValueString(int id, char[] value)	//原样设置值
	{
		if (m_pCurrRecord != null)
			m_pCurrRecord.SetFieldValueString(id, value);
	}
	public void	SetFieldValueString(int id, String value)	//原样设置值
	{
		if(value == null || value.length() == 0)return;
		SetFieldValueString(id, value.toCharArray());
	}
	public void	SetFieldValue_Encrypt(int id, char[] value,int desKeyIndex)//将value加密
	{
	    char []temp = EncryptValueString(value, desKeyIndex);
	    
	    SetFieldValueString(id, temp);
	}
	public void	SetFieldValue_ChangeToUTF8(int id, char[] value)	//将value转成utf8格式
	{
//		if (m_pCurrRecord != null)
//		{
//			char out[64*1024];
//			STD::AnsiToUtf8(out, sizeof(out), value);
//			m_pCurrRecord.SetFieldValueString(id, out);
//		}
	}

	public void	AddFieldValueINT(int id, int value)		//原样添加值
	{
		String str = String.valueOf(value);
		AddFieldValueString(id, str.toCharArray());
	}


	public void	AddFieldValueDouble(int id, double value)		//原样添加值
	{
		String str = String.valueOf(value);
		AddFieldValueString(id, str.toCharArray());
	}

	//只有添加数据到当前记录，如果有重复字段，这里不判断，由调用者自行决定
	public void	AddFieldValueString(int id, char[] value)		//原样添加值
	{
		if (m_pCurrRecord != null)
			m_pCurrRecord.AddFieldValueString(id, value);
	}
	//只有添加数据到当前记录，如果有重复字段，这里不判断，由调用者自行决定
	public void	AddFieldValueString(int id, String value)		//原样添加值
	{
		if(value == null || value.length() == 0)return;
		AddFieldValueString(id, value.toCharArray());
	}
	//只有添加数据到当前记录，如果有重复字段，这里不判断，由调用者自行决定
	public void	AddFieldValueString(int id, char value)		//原样添加值
	{
		char[] szTemp = new char[2];
		szTemp[0] = value;
		AddFieldValueString(id, szTemp);
	}
	public void	AddFieldValueString_Encrypt(int id, char[] value,int desKeyIndex)	//将value加密
	{
	    char []temp = EncryptValueString(value, desKeyIndex);
	    AddFieldValueString(id, temp);
	}
	public void	AddFieldValueString_Encrypt(int id, String value,int desKeyIndex)	//将value加密
	{
		if(value == null || value.length() == 0)return;
	    char []temp = EncryptValueString(value.toCharArray() , desKeyIndex);
	    AddFieldValueString(id, temp);
	}
	public void	AddFieldValueString_ChangeToUTF8(int id, char[] value)	//将value转成utf8
	{
//		if (m_pCurrRecord != null)
//		{
//			char out[64*1024];
//			STD::AnsiToUtf8(out, sizeof(out), value);
//			m_pCurrRecord.AddFieldValueString(id, out);
//		}
	}

	//返回false表示对应ID返回空值
	public int	GetFieldValueString(int id, char[] out, int size)
	{
		if (m_pCurrRecord != null)
			return m_pCurrRecord.GetFieldValueString(id, out, size);
		
		return 0;
	}
	
	public char GetFieldValueCHAR(int nId)
	{
	    String temp = "";
	    temp = GetFieldValueString(nId);
	    if (temp.isEmpty())
	    {
	    	return '\0';
	    }else
	    {
	    	return temp.charAt(0);
	    }
	}
	
	public String	GetFieldValueString(int id)
	{
		if (m_pCurrRecord != null)
		{
		    int nLen = 4*1024;
		    char []temp = new char[nLen];
		    nLen = m_pCurrRecord.GetFieldValueString(id, temp, nLen);
			return new String(temp,0,nLen);
		}
		return "";
	}
	
	//获取ID对应的值，如果为空则获取backid对应的值
	public String	GetFieldValueStringWithBackup(int id, int backid)
	{
		String retStr = "";
		if (m_pCurrRecord != null)
		{
		    int nLen = 4*1024;
		    char []temp = new char[nLen];
		    nLen = m_pCurrRecord.GetFieldValueString(Math.abs(id), temp, nLen);
		   
		    if (nLen > 0)//该字段有值
		    {
		    	retStr = String.copyValueOf(temp, 0, nLen);
		    	if (id < 0)
		    	{
		    		boolean bPercent = false;//是否带百分号,有百分号的取倒数之后仍然加上百分号

		    		String tempStr = retStr;
		    		tempStr = tempStr.replace("%", "");
		            
		            if (retStr.equalsIgnoreCase(tempStr)) {
		                bPercent = false;
		            }
		            else
		            {
		                bPercent = true;
		            }
		            float fValue = STD.StringToValue(tempStr);
		            if (fValue == 0f) {
		            	retStr = "-";
		            }
		            else
		            {
		                if(bPercent)
		                {
		                    fValue = fValue/100;
		                    retStr = String.format("%.2f%%", 100.0/fValue);
		                }
		                else
		                {
		                	retStr = String.format("%.2f", 1.0/fValue);
		                }
		            }
		    	}
		    }
		    else//该字段没值，用备用字段获取
		    {
		    	nLen = 4*1024;
		    	nLen = m_pCurrRecord.GetFieldValueString(Math.abs(backid), temp, nLen);
		    	
		    	if(nLen > 0)
		    	{
		    		retStr = String.copyValueOf(temp, 0, nLen);
			    	if (backid < 0)
			    	{
			    		boolean bPercent = false;//是否带百分号,有百分号的取倒数之后仍然加上百分号
			    		
	
			    		String tempStr = retStr;
			    		tempStr = tempStr.replace("%", "");
			            
			            if (retStr.equalsIgnoreCase(tempStr)) {
			                bPercent = false;
			            }
			            else
			            {
			                bPercent = true;
			            }
			            float fValue = STD.StringToValue(tempStr);
			            if (fValue == 0f) {
			            	retStr = "-";
			            }
			            else
			            {
			                if(bPercent)
			                {
			                    fValue = fValue/100;
			                    retStr = String.format("%.2f%%", 100.0/fValue);
			                }
			                else
			                {
			                	retStr = String.format("%.2f", 1.0/fValue);
			                }
			            }
			    	}
		    	}
		    }
		}
		return retStr;
	}

	public boolean	GetFieldValueString_ChangeUTF8ToAnsi(int id, char[] out, int size)
	{
//		char temp[64*1024];
//		boolean ret = GetFieldValueString(id, temp, sizeof(temp));
//		if (ret)
//			STD::Utf8ToAnsi(out, size, temp, strlen(temp));
//
//		return ret;
		return false;
	}
	public char[] GetFieldValueString_Encrypted(int id,int desKeyIndex)//获取加密字段内容
	{
		int nLen = 4*1024;
	    char []temp = new char[nLen];
	    GetFieldValueString(id, temp,nLen);
	    
	   // size = nLen;
	    return DecryptValueString(temp, desKeyIndex);
	}
	public void	SetBaseRecFieldValue_Encrypt(int id, char[] value,int desKeyIndex)	//将value加密
	{
	    char []temp = EncryptValueString(value,desKeyIndex);
	    SetBaseRecFieldValueString(id, temp);
	}
	
	public void	SetBaseRecFieldValue_Encrypt(int id, String value,int desKeyIndex)	//将value加密
	{
		if(value == null || value.length() == 0)return;
	    char []temp = EncryptValueString(value.toCharArray(), desKeyIndex);
	    SetBaseRecFieldValueString(id, temp);
	}

	//第一条记录的字段信息
	public void		SetBaseRecFieldValueString(int id, char[] value)	//原样设置值
	{
		if (m_pFirstRecord != null)
			m_pFirstRecord.SetFieldValueString(id, value);
	}
	//第一条记录的字段信息
	public void		SetBaseRecFieldValueString(int id, String value)	//原样设置值
	{
		if(value == null || value.length() == 0)return;
		SetBaseRecFieldValueString(id,value.toCharArray());
	}
	public void		SetBaseRecFieldValueINT(int id, int value)
	{
		if (m_pFirstRecord != null)
		{
	        String str = String.valueOf(value);
			m_pFirstRecord.SetFieldValueString(id, str.toCharArray());
		}
	}
	public void		SetBaseRecFieldValue_ChangeToUTF8(int id, char[] value)	//将value转成utf8格式
	{
//		if (m_pFirstRecord != null)
//		{
//			char out[64*1024];
//			STD::AnsiToUtf8(out, sizeof(out), value);
//			m_pFirstRecord.SetFieldValueString(id, out);
//		}
	}

	public int		GetBaseRecFieldValueString(int id, char[] out, int size)
	{
		if (m_pFirstRecord != null)
			return m_pFirstRecord.GetFieldValueString(id, out, size);

		return 0;
	}
	public String		GetBaseRecFieldValueString(int id)
	{
		if (m_pFirstRecord != null)
		{
		    int nLen = 4*1024;
		    char []temp = new char[nLen];
		    nLen = m_pFirstRecord.GetFieldValueString(id, temp, nLen);
			return new String(temp,0,nLen);
		}
		return "";
	}
	
	int			GetBaseRecFieldValueINT(int id)
	{
		int nRet = 0;
		char[] temp = new char[100];
		int nLength = GetBaseRecFieldValueString(id, temp, 100);
		if(nLength == 0)
		{
			return 0;
		}
		 try
		 {
			 nRet = STD.StringToInt(new String(temp,0,nLength));
		 }
		catch(Exception e){
    		e.printStackTrace();
    	}
		 return nRet;
	}


	public int	GetFieldValueInt(int id)
	{
		int nRet = 0;
		char[] temp = new char[100];
		int nLength = GetFieldValueString(id, temp, 100);
		if(nLength == 0)
		{
			return 0;
		}
		 try
		 {
			 nRet = STD.StringToInt(new String(temp,0,nLength));
		 }
		catch(Exception e){
			e.printStackTrace();
		}
		 return nRet;
	}


//	public boolean		GetBaseRecFieldValueString_ChangeUTF8ToAnsi(int id, char* out, int size)
//	{
//		char temp[64*1024];
//		boolean ret = GetBaseRecFieldValueString(id, temp, sizeof(temp));
//		if (ret)
//			STD::Utf8ToAnsi(out, size, temp, strlen(temp));
//
//		return ret;
//		return false;
//	}
	 public char[]	GetBaseRecFieldValueString_Encrypted(int id,int desKeyIndex)//获取加密字段内容
	{
		int nLen = 4*1024;
		char []temp = new char[nLen];
	    GetBaseRecFieldValueString(id, temp,nLen);

	    return DecryptValueString(temp, desKeyIndex);
	}

	//组包，返回记录数
	//StartRecord表示从第几条记录开始(序号从0开始)，num表示返回多少条
	int MakeData(TListBuffer data, int StartRecord, int num)
	{
		if (StartRecord < 0)
		{
			StartRecord = 0;
		}

		if (StartRecord >= m_iRecordNum)
		{
			num = 0;
		}
		else
		{
			if (StartRecord + num  > m_iRecordNum)
			{
				num = m_iRecordNum - StartRecord;
			}
		}

		SetBaseRecFieldValueINT(6, num);
		SetBaseRecFieldValueINT(7, m_iTotalRecNum);

		data.Add(m_pFirstRecord.GetDataPtr(),0, m_pFirstRecord.GetDataSize());

		data.AddString("\n",1);

		for (int i = 0; i < num; i++)
		{
			PBSTEPRecord rec = m_RecordPtr.get(i+StartRecord);
			if (rec != null)
			{
				data.Add(rec.GetDataPtr(),0, rec.GetDataSize());
			}
			data.AddString("\n", 1);
		}

		return num;
	}

//	//按序号获取字段信息返回内容和ID
//	int		GetBaseRecFieldByPos(int pos, char[] out, int out_len)	//如果返回<=0，表示没有字段了
//	{
//		if (m_pFirstRecord != null)
//			return m_pFirstRecord.GetFieldByPos(pos, out, out_len);
//		else
//		{
//			memset(out, 0, out_len);
//			return -1;
//		}
//	}
//
//	int		GetFieldByPos(int pos, char[] out, int out_len)	//如果返回<=0，表示没有字段了
//	{
//		if (m_pCurrRecord != null)
//			return m_pCurrRecord.GetFieldByPos(pos, out, out_len);
//		else
//		{
//			memset(out, 0, out_len);
//			return -1;
//		}
//	}
	char[]  EncryptValueString(char[] value,int desKeyIndex)
	{
		byte []szSrc = STD.getBytesFromChars(value);
	    int nSrcSize = STD.strlen(szSrc);
		
	    byte []szDst = new byte[nSrcSize+50];
	    
		int nSize = (int)SSLEncrypt.DesNcbcEncrypt(desKeyIndex,szSrc, szDst, nSrcSize, 1);
        L.e(TAG, "MakeEncryptPackage EncryptedData["+ nSize + "]:"+new String(szDst,0,nSize));

	    int nLen = nSize + 50;
	    byte []szOut = new byte[nLen];
	    STD.Base64Encode_WithAdd(szOut, nLen, szDst, nSize);
	    
	    return new String(szOut).toCharArray();
	}
	char[]  DecryptValueString(char[] value, int desKeyIndex)
	{
		byte []szSrc = STD.getBytesFromChars(value);
		int nSrcSize = STD.strlen(szSrc);
	    int nLen = nSrcSize+50;
		byte []szDst = new byte[nLen];
		
		int nDestSize =STD.Base64Decode(szSrc, szDst, nLen);
		
		byte []szOut = new byte[nLen*2];
	    //解密
		int nSize = (int)SSLEncrypt.DesNcbcEncrypt(desKeyIndex,szDst, szOut, nDestSize, 0);
        L.e(TAG, "MakeEncryptPackage EncryptedData["+ nSize + "]:"+new String(szOut,0,nSize));

	    return new String(szOut).toCharArray();
	}

	//初始化需计算的列，包括设置的需排序列
	public void	InitCalcField()
	{
		m_CalcFieldNum	= 0;
		for (int i = 0; i < MAX_CALC_FIELD_NUM; i++)
		{
			m_pCalcFieldData[i]	= null;
		}
		
		for (int i = 0; i < 2; i++)
		{
			m_SortFunc[i].Init();
		}

	}

	//注意需计算的列和排序的列加起来不超过10(MAX_CALC_FIELD_NUM)
	//设置需计算的列
	//为字符串类别时，需指明长度，长度包含字符串结束符
	//64整形和double都是8字节
	public boolean	AddCalcField(int id, int type, int length)
	{
		PBSTEPField p = null;
		int i;
		for (i = 0; i < m_CalcFieldNum; i++)
		{
			p = m_pCalcFieldData[i];
			if (p.m_FieldId == id)
			{
				if (p.m_FieldType == type)	//字段一样
				{
					return true;
				}
				//字段不一样时，需要重新设置数据
				p.Set(id, type, length);
				break;
			}
		}

		if (i == m_CalcFieldNum)	//还没有设置字段信息
		{
			if (m_CalcFieldNum >= MAX_CALC_FIELD_NUM)	//字段信息满了
			{
				return false;
			}
		
			p = new PBSTEPField();

			p.Set(id, type, length);
			m_pCalcFieldData[m_CalcFieldNum]	= p;

			m_CalcFieldNum++;
		}

		//获取已经有的数据

		return true;
	}

	public boolean	DelCalcField(int id)
	{
		for (int i = 0; i < m_CalcFieldNum; i++)
		{
			if (m_pCalcFieldData[i].m_FieldId == id)
			{
				m_pCalcFieldData[i] = null;
				m_CalcFieldNum--;
				if (i < m_CalcFieldNum)	//非最后一个
				{
					m_pCalcFieldData[i]	= m_pCalcFieldData[m_CalcFieldNum];
				}
				m_pCalcFieldData[m_CalcFieldNum]	= null;
				return true;
			}
		}
		return false;
	}

	//设置需排序的列，最多设置两列，不需要第二列时，id2设置为-1即可
	//排序的列需先加到需计算的列里
	public boolean	SetSortField(int id1, boolean updownflag1,
							 int id2, boolean updownflag2
							 )
	{
		m_SortFunc[0].Set(id1, updownflag1);
		m_SortFunc[1].Set(id2, updownflag2);
		return true;
	}
}
