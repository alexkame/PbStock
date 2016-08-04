package com.pengbo.mhdzq.data;

import java.text.DecimalFormat;

import com.pengbo.mhdzq.tools.STD;

public class CHQStep {

	final char STEP_SOP1 = '=';
	final char STEP_SOP2 = '|';
	final char STEP_SOP3 = '\3';

	protected tagListBuffer m_RecData = new tagListBuffer();

	public CHQStep() {
	};

	public byte[] GetData() {
		return m_RecData.m_pszData;
	}

	public int GetData(byte[] out, int offset, int size) {
		if (out != null && size > 0) {
			STD.memset(out, offset, size);
		}
		if (m_RecData.m_iUseSize > 0) {
			int copysize = Math.min(size - 1, m_RecData.m_iUseSize);
			STD.memcpy(out, m_RecData.m_pszData, offset, copysize);
			return copysize;
		}
		return 0;
	};

	public int GetDataSize() {
		return m_RecData.m_iUseSize;
	};

	public void Init() {
		m_RecData.Empty();
	}

	public void AddData(byte[] data, int size) {
		m_RecData.Add(data, size);
	}

	/**
	 * 添加一个字段信息，非汉字,仅包含可见字符
	 * 
	 * @param key
	 * @param value
	 */
	public void AddFieldString(int key, String value) {
		byte[] data = value.getBytes();

		StringBuffer str = new StringBuffer();
		int size = 0;
		if (m_RecData.m_iUseSize > 0) {
			str = str.append(STEP_SOP2).append(key).append(STEP_SOP1);
		} else {
			str = str.append(key).append(STEP_SOP1);
		}
		m_RecData.Add(str.toString().getBytes(), str.length());
		m_RecData.Add(data, STD.strlen(data));
	}

	public void AddFieldInt(int key, int value) {
		StringBuffer str = new StringBuffer();
		int size = 0;
		if (m_RecData.m_iUseSize > 0) {
			str = str.append(STEP_SOP2).append(key).append(STEP_SOP1)
					.append(value);
		} else {
			str = str.append(key).append(STEP_SOP1).append(value);
		}
		m_RecData.Add(str.toString().getBytes(), str.length());
	}

	public void AddFieldInt(int key, long value) {
		StringBuffer str = new StringBuffer();
		int size = 0;
		if (m_RecData.m_iUseSize > 0) {
			str = str.append(STEP_SOP2).append(key).append(STEP_SOP1)
					.append(value);
		} else {
			str = str.append(key).append(STEP_SOP1).append(value);
		}
		m_RecData.Add(str.toString().getBytes(), str.length());
	}

	// date:yyyymmdd
	// time:hhmmss
	public void AddFieldDateTime(int key, int date, int time) {
		StringBuffer str = new StringBuffer();

		DecimalFormat df1 = new DecimalFormat("00000000");
		String str_date = df1.format(date);
		DecimalFormat df2 = new DecimalFormat("000000");
		String str_time = df2.format(time);

		int size = 0;
		if (m_RecData.m_iUseSize > 0) {
			str = str.append(STEP_SOP2).append(key).append(STEP_SOP1)
					.append(str_date).append(' ').append(str_time);
		} else {
			str = str.append(key).append(STEP_SOP1).append(str_date)
					.append(' ').append(str_time);
		}
		m_RecData.Add(str.toString().getBytes(), str.length());
	}

	public void AddFieldDateTime(int key, int year, int month, int day,
			int hour, int minute, int second) {
		AddFieldDateTime(key, year * 10000 + month * 100 + day, hour * 10000
				+ minute * 100 + second);
	}

	// 如果未想到相关key，则返回false,否则返回true
	public String GetFieldString(int key) {
		StringBuffer out = new StringBuffer();
		if (m_RecData.m_iUseSize <= 0) {
			return null;
		}

		// 找到key
		byte[] src = m_RecData.GetPtr();
		StringBuffer data = new StringBuffer();
		data = data.append(key).append(STEP_SOP1);
		int len = data.length();
		byte[] p = m_RecData.GetPtr();

		int iFind = 0;
		byte[] temp_key = data.toString().getBytes();
		if (STD.strncmp(p, temp_key, len) != 0) {
			data.delete(0, data.length());// 清空
			data = data.append(STEP_SOP2).append(key).append(STEP_SOP1);
			len = data.length();
			// p = strstr(m_RecData.GetPtr(), data);
			byte[] search_key = data.toString().getBytes();
			iFind = STD.bytechr(src, 0, search_key, p.length);
			if (iFind < 0) {
				return null;
			}
		}

		int i = iFind + len;
		int j = 0;
		for (; src[i] != '\0'; i++, j++) {
			char ch = (char) src[i];
			if (ch == STEP_SOP2 || ch == STEP_SOP3) {
				break;
			}
			out.append(ch);
		}
		return out.toString();
	}

	public int GetFieldInt(int key) {
		String value = GetFieldString(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long GetFieldInt64(int key) {
		String value = GetFieldString(key);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int GetFieldTime(int key) {
		String value = GetFieldString(key);
		value = value.substring(9);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int GetFieldDate(int key) {
		String value = GetFieldString(key);
		value = value.substring(0, 8);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
