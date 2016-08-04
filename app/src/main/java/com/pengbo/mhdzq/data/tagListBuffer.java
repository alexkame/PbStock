package com.pengbo.mhdzq.data;

import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;

public class tagListBuffer {

	byte[] m_pszData;
	int m_iUseSize;
	int m_iMaxSize;

	public tagListBuffer() {
		m_pszData = null;
		m_iUseSize = 0;
		m_iMaxSize = 0;
	}

	public int GetSize() {
		return m_iUseSize;
	}

	public byte[] GetPtr() {
		return m_pszData;
	}

	public void Empty() {
		if (m_iUseSize > 0) {
			STD.memset(m_pszData, 0, m_iUseSize);
			m_iUseSize = 0;
		}
	}

	public void Free() {
		m_pszData = null;
		m_iUseSize = 0;
		m_iMaxSize = 0;
	}

	public int AllocMem(int newsize) // 重新分配内存
	{
		if (newsize > m_iMaxSize) {
			newsize = ((newsize + 1023) >> 10) << 10;
			byte[] p = new byte[newsize];

			if (p != null) {
				if (m_iUseSize > 0) {
					System.arraycopy(m_pszData, 0, p, 0, m_iUseSize);
				}
				m_pszData = p;
				m_iMaxSize = newsize;
			}
		}

		return m_iMaxSize;
	}

	public boolean Add(int size) {
		int newsize = size + m_iUseSize;
		if (AllocMem(newsize + 1) >= newsize) {
			m_iUseSize = newsize;
			return true;
		} else {
			return false;
		}
	}

	public boolean Add(byte[] buff, int size) {
		return Add(buff, 0, size);
	}

	public boolean Add(byte[] buff, int offset, int size) {
		if (size <= 0) {
			return false;
		}
		int newsize = size + m_iUseSize;
		if (AllocMem(newsize + 1) >= newsize) {
			System.arraycopy(buff, offset, m_pszData, m_iUseSize, size);
			// memcpy(m_pszData + m_iUseSize, buff, size);
			m_iUseSize = newsize;
			return true;
		} else {
			return false;
		}
	}

	public tagListBuffer clone() {
		L.d("tagListBuffer", "start clone");
		tagListBuffer buf = new tagListBuffer();
		buf.m_iMaxSize = this.m_iMaxSize;
		buf.m_iUseSize = this.m_iUseSize;
		int size = 0;
		if (m_pszData != null) {
			size = this.m_pszData.length;
		}

		if (size > 0) {
			L.d("tagListBuffer", "m_pszData size = " + size);
			buf.m_pszData = new byte[size];
			System.arraycopy(this.m_pszData, 0, buf.m_pszData, 0, size);
		}
		L.d("tagListBuffer", "end clone");
		return buf;
	}
}
