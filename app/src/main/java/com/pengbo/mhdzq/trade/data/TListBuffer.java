package com.pengbo.mhdzq.trade.data;

import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;


public class TListBuffer {
	char[]			m_pszData;
	int				m_iUseSize;
	int				m_iMaxSize;
	
	public TListBuffer() 
	{
		m_pszData	= null;
		m_iUseSize	= 0;
		m_iMaxSize	= 0;
	}
	public	int	GetSize()	    { return m_iUseSize; }
	public	char[]	GetPtr()	{ return m_pszData; }
	public	String GetContentString()
	{
		if(m_pszData == null || m_iUseSize == 0)
		{
			return null;
		}
		return new String(m_pszData);
	}
	public	void	Empty()
	{
		if (m_iUseSize > 0)
		{
			STD.memset(m_pszData, 0, m_iUseSize);
			m_iUseSize	= 0;
		}
	}
	public	void	Free()
	{
		m_pszData	= null;
		m_iUseSize	= 0;
		m_iMaxSize	= 0;
	}
	
	public	int	AllocMem(int newsize)	//重新分配内存
	{
		if (newsize > m_iMaxSize)
		{
			L.d("Trade", "AllocMemL:"+newsize);
			//newsize		= ((newsize + 1023) >> 10) << 10;
			newsize = ((newsize+127)>>7)<<7;
			L.d("Trade", "AllocMem:"+newsize);
			//newsize = 100;
			char[] p 	= new char[newsize];

			if (p != null)
			{
				if (m_iUseSize > 0)
				{
					System.arraycopy(m_pszData, 0, p, 0, m_iUseSize);
				}
				m_pszData	= p;
				m_iMaxSize	= newsize;
			}
		}

		return m_iMaxSize;
	}
	
	public	boolean	Add(int size)
	{
		int newsize = size + m_iUseSize;
		if (AllocMem(newsize +1) >= newsize)
		{
			m_iUseSize	= newsize;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public	boolean	AddString(String buff, int size)
	{
		return Add(buff.toCharArray(),0,size);
	}
	public	boolean	Add(char[] buff,int offset, int size)
	{
		if(size <= 0) {
			return false;
		}
		int newsize = size + m_iUseSize;
		if (AllocMem(newsize+1) >= newsize)
		{
			System.arraycopy(buff, offset, m_pszData, m_iUseSize, size);
			//memcpy(m_pszData + m_iUseSize, buff, size);
			m_iUseSize	= newsize;
			return true;
		}
		else
		{
			return false;
		}
	}

	public int  Insert(int nPos, char[] pBuffer, int size)
	{
		if(size <= 0) {
			return GetSize();
		}
		int newsize = size + m_iUseSize;
		if (AllocMem(newsize+1) >= newsize)
		{
			int nMoveSize = m_iUseSize - nPos;
		    System.arraycopy(m_pszData, nPos, m_pszData, nPos+size, nMoveSize);
		    
		    System.arraycopy(pBuffer, 0, m_pszData, nPos, size);
		    m_iUseSize	= newsize;
		}
	    return this.GetSize();
	}

	public int  Delete(int nPos, int size)
	{
	    if(nPos >= m_iUseSize)
	        return this.GetSize();

	    int nMoveSize = m_iUseSize - nPos - size;
	    if(nMoveSize > 0)
	    {
	    	System.arraycopy(m_pszData, nPos+size, m_pszData, nPos, nMoveSize);
	    }
	    m_iUseSize -= size;
		STD.memset(m_pszData, m_iUseSize, size);
			
	    return this.GetSize();
	}
}
