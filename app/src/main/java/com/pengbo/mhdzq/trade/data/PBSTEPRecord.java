package com.pengbo.mhdzq.trade.data;

import com.pengbo.mhdzq.tools.STD;



public class PBSTEPRecord {

	protected	TListBuffer	m_data;
		
	public PBSTEPRecord()
	{
		m_data = new TListBuffer();
	}
		
	public 	void Init()
	{
		m_data.Empty();
	}
		
		//默认移动第一条记录
	public 	void	SetData(char[] data, int offset,int size)
	{
		m_data.Empty();
		m_data.Add(data,offset, size);
	}
		
	public 	int		GetFieldValueINT(int id)
	{
		int nRet = 0;
		String strRet = GetFieldValueString(id);
		if(strRet == null || strRet.length() == 0)
		{
			return 0;
		}
		 try
		 {
			 nRet = STD.StringToInt(strRet);
		 }
		catch(Exception e){
			e.printStackTrace();
		}
		return nRet;
	}
		
	//返回false表示对应ID返回空值
	public String	GetFieldValueString(int id)
	{
		int[] nRet = new int[2];
		return GetFieldInfoWithID(id,nRet);
	}
	//返回false表示对应ID返回空值
	public int	GetFieldValueString(int id, char[] out, int size)
	{
		int[] nRet = new int[2];
		String strRet = GetFieldInfoWithID(id,nRet);
		if(strRet != null && strRet.length() > 0)
		{
	        char[] buffer = strRet.toCharArray();
	        int nCount = Math.min(size, strRet.length());
	        System.arraycopy(buffer, 0, out, 0, nCount);
	        return nCount;
		}

        return 0;
	}

	//获取ID的信息，start返回起始位置,len返回整个数据对的长度（包括ID）,out存储ID返回的值
	//未找到ID时返回false,否则返回true
	public String	GetFieldInfoWithID(int id, int[] nRet)
	{		
		int start = 0;
		int len = 0;
		nRet[0] = start;
		nRet[1] = len;
		
		StringBuffer strKey = new StringBuffer();
		strKey = strKey.append(id).append('=');

		String pdata = m_data.GetContentString();
		if(pdata == null)
		{
			return null;
		}
		int datasize = (int)m_data.GetSize();
		StringBuffer strOut = new StringBuffer();
		
		while (true)
		{
			char p; 
			int nFindIndex = pdata.indexOf(strKey.toString()); //strstr(pdata, key);
			if (nFindIndex < 0)
			{
				return "";
			}

			boolean keyflag = false;
			if (nFindIndex != 0)	//
			{
				char c = pdata.charAt(nFindIndex-1);//*(p-1);
				if (c == '\n' || c == '&')			//前一字节是行分隔符或数据对分隔符，表明是字段名
					keyflag = true;
			}
			else	//是第一个字符，表明是字段名
				keyflag = true;

			if (!keyflag)
			{
				pdata	= pdata.substring(nFindIndex+strKey.length()); //p + keylen;
				continue;
			}

			start = nFindIndex;
			//查找数据结束位置
			int i = start + strKey.length();
			for (; i < datasize; i++)
			{
				p = pdata.charAt(i);
				char c = p;
				if ( c == '\\' )
				{
					if (i+1 < datasize)
					{
						i++;
						p = pdata.charAt(i);
						char c1 = p;
						if (c1 == '\\')
							c = '\\';
						else if (c1 == 'a')
							c = '=';
						else if (c1 == 'b')
							c = '&';
						else if (c1 == 'n')
							c = '\n';
					}
				}
				else if (c == '&' || c == '\n')	//数据对结束了
				{
					break;
				}

				strOut.append(c);
			}
			len = i - start;

			nRet[0] = start;
			nRet[1] = len;
			return strOut.toString();
		}
	}

	public int	GetDataSize()
	{
		if (m_data != null)
		{
			return (int)(m_data.GetSize());
		}else
		{
			return 0;
		}
	}

	public char[] GetDataPtr()
	{
		if (m_data != null)
		{
			return m_data.GetPtr();
		}else
		{
			return null;
		}
	}

	public void	to_zhuangyi_string(char[] value,StringBuffer out,int nMaxSize)
	{
		int len = 0;
		int nSrcLen = Math.min(value.length, STD.strlen(value));
		for (int i = 0 ; i < nSrcLen && len < nMaxSize ; i++)
		{
			char c = value[i];
			switch (c)
			{
			case '\\':
				if (len+1 < nMaxSize)
				{
					out.append('\\');
					out.append('\\');
				}
				else
				{
					//out.append('\0');
					return;
				}
				continue;
			case '=':
				if (len+1 < nMaxSize)
				{
					out.append('\\');
					out.append('a');
				}
				else
				{
					//out.append('\0');
					return;
				}
				continue;
			case '&':
				if (len+1 < nMaxSize)
				{
					out.append('\\');
					out.append('b');
				}
				else
				{
					//out.append('\0');
					return;
				}
				continue;
			case '\n':
				if (len+1 < nMaxSize)
				{
					out.append('\\');
					out.append('n');
				}
				else
				{
					//out.append('\0');
					return;
				}
				continue;
			}
			out.append(c);
			len++;
		}

		//out.append('\0');
	}
	public void	to_zhuangyi_string(char[] value, char[] out,int offset, int size)
	{
		int len = 0;
		int nSrcLen = Math.min(value.length, STD.strlen(value));
		size--;
		for (int i = 0 ; i < nSrcLen && len < size ; i++)
		{
			char c = value[i];
			switch (c)
			{
			case '\\':
				if (len+1 < size)
				{
					out[offset+len] = '\\';
					len++;
					out[offset+len] = '\\';
					len++;
				}
				else
				{
					out[offset+len] = 0;
					return;
				}
				continue;
			case '=':
				if (len+1 < size)
				{
					out[offset+len] = '\\';
					len++;
					out[offset+len] = 'a';
					len++;
				}
				else
				{
					out[offset+len] = 0;
					return;
				}
				continue;
			case '&':
				if (len+1 < size)
				{
					out[offset+len] = '\\';
					len++;
					out[offset+len] = 'b';
					len++;
				}
				else
				{
					out[offset+len] = 0;
					return;
				}
				continue;
			case '\n':
				if (len+1 < size)
				{
					out[offset+len] = '\\';
					len++;
					out[offset+len] = 'n';
					len++;
				}
				else
				{
					out[offset+len] = 0;
					return;
				}
				continue;
			}
			out[offset+len]	= c;
			len++;
		}

		out[offset+len] = 0;
	}

	//字段内容信息(Set系列会查找是否有相同字段，Add系列直接在行尾添加内容)
	public void	SetFieldValueString(int id, char[] value)	//原样设置值
	{
		if (value == null || value[0] == 0)
		{
			return;
		}
		int nMaxSize = 64*1024;
		StringBuffer str = new StringBuffer();
		str = str.append(id).append('=');
		to_zhuangyi_string(value,str,nMaxSize);
		
		int[] nRet = new int[2];
		GetFieldInfoWithID(id,nRet);
		int start = nRet[0];
		int len = nRet[1];
		if (len > 0)
		{
			//str.append('\0');
			m_data.Delete(start, len);
			m_data.Insert(start, str.toString().toCharArray(), str.toString().length());
		}
		else
		{
			str.append('&');
			//str.append('\0');
			m_data.Add(str.toString().toCharArray(),0, str.toString().length());
		}
	}

	public void	AddFieldValueString(int id, char[] value)	//原样添加值，添加到末尾，不判断是否有重复
	{	
		int nMaxSize = 64*1024;
		StringBuffer str = new StringBuffer();
		str = str.append(id).append('=');
		to_zhuangyi_string(value,str,nMaxSize);
		str.append('&');
		//str.append('\0');
		m_data.Add(str.toString().toCharArray(),0, str.toString().length());
	}
}
