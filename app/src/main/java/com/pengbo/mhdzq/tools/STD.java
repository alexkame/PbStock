package com.pengbo.mhdzq.tools;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Date;

import android.text.format.Time;

import com.pengbo.mhdzq.net.MyByteBuffer;

public class STD {

	// 从一个字符串中查找参数，格式：name1:1111|name2:2222|...
	public final static String GetPara(String src, String para, char s, char e) {
		if (src == null) {
			return "";
		}
		int len = para.length();

		int start = src.indexOf(para);
		if (start >= 0) {
			if (start > 0) // 非第一个字符
			{
				if (src.charAt(start - 1) != e) // 假设没有空格
				{
					return "";
				}
			}

			start += len;
			if (src.charAt(start) != s) // 不是属性名
			{
				return "";
			}

			start++;
			int end = start;
			int srclen = src.length();
			while (end < srclen) {
				if (src.charAt(end) == e) {
					break;
				}
				end++;
			}
			if (end > start) {
				return src.substring(start, end);
			}
		}

		return "";
	}

	public final static int GetParaInt(String src, String para, char s, char e) {
		int value = 0;
		try {
			value = Integer.parseInt(GetPara(src, para, s, e));
		} catch (NumberFormatException err) {
		}
		return value;
	}

	// 从一个字符串中查找子字串，以ch为分隔符，查找第num个子字串，从1开始
	// 如：||股票买卖|5800|200|55555555|000001|深发展A|29.00|200|
	// 是以'|'分隔符
	public final static String GetValue(String src, int num, char ch) {
		return GetValue(src, num, ch, '\0');
	}

	public final static String GetValue(String src, int num, char ch,
			char EndChar) {
		// int tt = num;
		int srclen = src.length();
		int start = 0;

		// 查找起始位置
		if (num > 1) {
			while (start < srclen) {
				char c = src.charAt(start);
				start++;
				if (c == '\n' || c == EndChar) {
					break;
				}
				if (c == ch) {
					num--;
					if (num == 1) {
						break;
					}
				}
			}
		}

		// 查找结束位置
		int end = start;
		while (end < srclen)
		// while (*src!=0 && *src!='\n' && *src!=ch && size > 1)
		{
			char c = src.charAt(end);
			if (c == '\n' || c == EndChar || c == ch) {
				break;
			}
			end++;
		}

		while (start < end) {
			if (src.charAt(start) != ' ') {
				break;
			}
			start++;
		}

		end--;
		while (end > start) {
			if (src.charAt(end) != ' ') {
				break;
			}
			end--;
		}

		end++;
		if (start < end) {
			return src.substring(start, end);
		} else {
			return "";
		}
	}

	public final static int GetValueInt(String src, int num, char ch) {
		return GetValueInt(src, num, ch, '\0');
	}

	public final static int GetValueInt(String src, int num, char ch,
			char EndChar) {
		int value = 0;
		try {
			value = Integer.parseInt(GetValue(src, num, ch, EndChar));
		} catch (NumberFormatException e) {
		}
		return value;
	}

	public final static int GetValueInt(String src, int num, char ch, int def) {
		return GetValueInt(src, num, ch, '\0', def);
	}

	public final static int GetValueInt(String src, int num, char ch,
			char EndChar, int def) {
		int value = def;
		try {
			value = Integer.parseInt(GetValue(src, num, ch, EndChar));
		} catch (NumberFormatException e) {
		}
		return value;
	}

	// time 包含h&m&s,返回：s
	public static final String getTimeSringss(int time) // 格式%02d:%02d:%02d
	{
		StringBuffer str = new StringBuffer();

		str.append(':');

		int s = time % 100;
		str.append((s == 0) ? "00" : s);

		return str.toString();
	}

	public static final String getTimeSringhhmmss(int time) // 格式%02d:%02d:%02d
	{
		StringBuffer str = new StringBuffer();
		return getTimeSringhhmmss(str, time / 10000, time / 100 % 100,
				time % 100);
	}

	public static final String getTimeSringhhmmss(StringBuffer str, int h,
			int m, int s) // 格式%02d:%02d:%02d
	{
		str.delete(0, str.length());
		str.append(h + 100);
		str.deleteCharAt(0);
		str.append(m + 100);
		str.setCharAt(2, ':');
		str.append(s + 100);
		str.setCharAt(5, ':');
		return str.toString();
	}

	public static final String getTimeSringddhhmm(int date, int time) // 格式%02d%02d%02d
	{
		StringBuffer str = new StringBuffer();
		str.append(time / 60 + 100);
		str.deleteCharAt(0);
		str.append(time % 60 + 100);
		str.deleteCharAt(2);
		str.insert(0, date % 100 + 100);
		str.deleteCharAt(0);
		return str.toString();
	}

	public static final String getTimeSringhhmm(int time) // 格式%02d:%02d
	{
		StringBuffer str = new StringBuffer();
		int h = time / 100;
		if (h < 10) {
			str.append(0);
		} else if (h >= 24) {
			h = h - 24;
			if (h < 10) {
				str.append(0);
			}
		}
		str.append(h);
		str.append(':');
		int minute = time % 100;
		if (minute < 10) {
			str.append(0);
		}
		str.append(minute);

		return str.toString();
	}

	public static final String getTimeSringyyyymmdd(int date) // 格式%04d/%02d/%02d
	{
		StringBuffer str = new StringBuffer();
		str.append(date / 10000);
		getTimeSringyyyymmdd(str, date);
		return str.toString();
	}

	public static final void getTimeSringyyyymmdd(StringBuffer str, int date) // 格式%04d/%02d/%02d
	{
		str.append(date / 10000);
		int len = str.length();
		str.append(date / 100 % 100 + 100);
		str.setCharAt(len, '/');
		len = str.length();
		str.append(date % 100 + 100);
		str.setCharAt(len, '/');
	}

	public static final String getDateSringmmdd(int date) // 格式%02d%02d
	{
		StringBuffer str = new StringBuffer();
		str.append(date % 10000 + 10000);
		str.deleteCharAt(0);
		return str.toString();
	}

	public static long my_int_times(long value, int times) {
		if (times <= 1) {
			return value;
		}

		if (value < 0) {
			return (value - times / 2) / times;
		} else {
			return (value + times / 2) / times;
		}
	}

	/**
	 * String to Unicode Bytes
	 */
	public static byte[] str2unicode(String src) {
		int len = src.length();
		byte[] unicode = new byte[len << 1];
		int offset = 0;
		char[] ss = src.toCharArray();
		for (int i = 0; i < ss.length; i++) {
			MyByteBuffer.putChar(unicode, offset, ss[i]);
			offset += 2;
		}
		return unicode;
	}

	/**
	 * 拷贝byte数据到char
	 * 
	 * @param start
	 *            dst的起始位置
	 * @param len
	 *            src的长度
	 */
	public static int strcpy(char[] dst, int start, byte[] src, int len) {
		len = Math.min(dst.length - start, len);
		for (int i = 0; i < len; i++) {
			char c = (char) src[i];
			dst[start + i] = c;
			if (c == 0) {
				return i;
			}
		}
		return len;
	}

	// 拷贝byte数据到char,byte为unicode格式
	public static int strcpy(char[] dst, int start, byte[] src, int offset,
			int len) {
		len = Math.min(dst.length - start, len);
		for (int i = 0; i < len; i++) {
			char c = MyByteBuffer.getChar(src, offset + (i << 1));
			dst[start + i] = c;
			if (c == 0) {
				return i;
			}
		}
		return len;
	}

	public static String strcpy(byte[] src, int offset, int len) {
		char str[] = new char[len];
		len = strcpy(str, 0, src, offset, len);
		return new String(str, 0, len);
	}

	public static int strcpy(char[] dst, char[] src) {
		int num = Math.min(dst.length - 1, src.length);
		for (int i = 0; i < num; i++) {
			dst[i] = src[i];
			if (src[i] == 0) {
				return i;
			}
		}
		dst[num] = 0;
		return num;
	}

	public static int strcpy(char[] dst, int start, char[] src, int len) {
		len = Math.min(dst.length - start, len);
		for (int i = 0; i < len; i++) {
			dst[start + i] = src[i];
			if (src[i] == 0) {
				return i;
			}
		}
		return len;
	}

	public static int strcpy(char[] dst, String src) {
		char[] str = src.toCharArray();
		return strcpy(dst, str);
	}

	public static int bytecpy(byte[] dst, int offset, char[] src) {
		int num = Math.min(dst.length - offset - 1, src.length);
		for (int i = 0; i < num; i++) {
			dst[offset + i] = (byte) src[i];
			if (src[i] == 0) {
				return i;
			}
		}
		dst[offset + num] = 0;
		return num;
	}

	/**
	 * 字节拷贝
	 * 
	 * @param dst
	 *            目标数据
	 * @param str
	 *            源数据
	 * @param offset
	 *            原数据偏移
	 * @param len
	 *            长度
	 * @return
	 */
	public static int bytecpy(byte[] dst, int start, byte[] str, int offset,
			int len) {
		len = Math.min(str.length - offset, dst.length - start);
		for (int i = 0; i < len; i++) {
			dst[start + i] = str[offset + i];
			if (str[offset + i] == 0) {
				len = i;
				break;
			}
		}
		return len;
	}

	// 比较字符串
	// 分别返回-1,0,1
	public static int strcmp(char[] str1, char[] str2) {
		int num = Math.min(str1.length, str2.length);
		for (int i = 0; i < num; i++) {
			int s = str1[i] - str2[i];
			if (s < 0) {
				return -1;
			} else if (s > 0) {
				return 1;
			} else {
				if (str1[i] == 0) {
					return 0;
				}
			}
		}
		if (num < str1.length) {
			if (str1[num] != 0) {
				return 1;
			}
		}
		if (num < str2.length) {
			if (str2[num] != 0) {
				return -1;
			}
		}
		return 0;
	}

	public static int strncmp(byte[] str1, byte[] str2, int len) {
		int num = len;// Math.min(str1.length, str2.length);
		for (int i = 0; i < num; i++) {
			int s = str1[i] - str2[i];
			if (s < 0) {
				return -1;
			} else if (s > 0) {
				return 1;
			} else {
				if (str1[i] == 0) {
					return 0;
				}
			}
		}

		return 0;
	}

	public static int strcmp(String str1, String str2, int start, int len) {
		int num = Math.min(str1.length(), len);
		for (int i = 0; i < num; i++) {
			int s = str1.charAt(i) - str2.charAt(start + i);
			if (s < 0) {
				return -1;
			} else if (s > 0) {
				return 1;
			}
		}
		if (num < str1.length()) {
			return 1;
		}
		if (num < len) {
			return -1;
		}
		return 0;
	}

	// 比较字符串
	// 分别返回-1,0,1
	public static int strcmp(char[] str1, int start1, byte[] str2, int start2,
			int num) {
		// int num = Math.min(str1.length, str2.length);
		int len1 = str1.length;
		int len2 = str2.length;
		for (int i = 0; i < num && start1 < len1 && start2 < len2; i++, start1++, start2++) {
			// byte t = (byte)str1[start1];
			int s = str1[start1] - str2[start2];
			// mobilestock.writelog(t + "," + str2[start2] + "," + s);
			if (s < 0) {
				return -1;
			} else if (s > 0) {
				return 1;
			} else {
				if (str1[start1] == 0) {
					return 0;
				}
			}
		}
		if (start1 < len1) {
			if (str1[start1] != 0) {
				return 1;
			}
		}
		if (start2 < len1) {
			if (str2[start2] != 0) {
				return -1;
			}
		}
		return 0;
	}

	public static int strlen(char[] str) {
		int num = str.length;
		for (int i = 0; i < num; i++) {
			if (str[i] == 0) {
				return i;
			}
		}
		return num;
	}

	public static int strlen(byte[] str) {
		return strlen(str, 0, str.length);
	}

	public static int strlen(byte[] str, int offset, int len) {
		int num = Math.min(str.length - offset, len);

		for (int i = 0; i < num; i++) {
			// System.out.println("strlen offset: " + offset + " [" + i + "]=" +
			// str[i+offset]);
			if (str[i + offset] == 0) {
				return i;
			}
		}
		return num;
	}

	public static void memcpy(int[] data, int[] src, int len) {
		memset(data);
		for (int i = 0; i < len; i++) {
			data[i] = src[i];
		}
	}

	public static void memcpy(short[] data, short[] src, int len) {
		memset(data);
		for (int i = 0; i < len; i++) {
			data[i] = src[i];
		}
	}

	public static void memcpy(byte[] data, byte[] src, int offset, int len) {
		memset(data, offset, data.length);
		for (int i = 0; i < len; i++) {
			data[i + offset] = src[i];
		}
	}

	public static void memcpy(byte[] data, byte[] src, int len) {
		memset(data);
		for (int i = 0; i < len; i++) {
			data[i] = src[i];
		}
	}

	public static void memset(char[] str) {
		int num = str.length;
		for (int i = 0; i < num; i++) {
			str[i] = 0;
		}
	}

	public static void memset(char[] str, int start, int len) {
		int num = Math.min(str.length - start, len);
		for (int i = 0; i < num; i++) {
			str[start + i] = 0;
		}
	}

	public static void memset(short[] str) {
		int num = str.length;
		for (int i = 0; i < num; i++) {
			str[i] = 0;
		}
	}

	public static void memset(byte[] str) {
		memset(str, 0, str.length);
	}

	public static void memset(byte[] str, int start, int len) {
		int num = Math.min(str.length - start, len);
		for (int i = 0; i < num; i++) {
			str[start + i] = 0;
		}
	}

	public static void memset(int[] data) {
		int num = data.length;
		for (int i = 0; i < num; i++) {
			data[i] = 0;
		}
	}

	public static void memset(int[][] data) {
		int num = data.length;
		for (int i = 0; i < num; i++) {
			int num2 = data[i].length;
			for (int j = 0; j < num2; j++) {
				data[i][j] = 0;
			}
		}
	}

	public static int strchr(StringBuffer str, char c) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (str.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}

	public static int strchr(String str, int start, int len, char c) {
		for (int i = 0; i < len; i++) {
			if (str.charAt(start + i) == c) {
				return i + start;
			}
		}
		return -1;
	}

	//
	public static int getByteStringLen(byte[] str, int offset, int len) {
		for (int i = 0; i < len; i++) {
			if (str[offset + i] == 0) {
				len = i;
				break;
			}
		}
		return len;
	}

	/**
	 * 字节匹配查找
	 */
	public static int bytechr(byte[] str, int start, byte[] dst, int len) {
		int d_len = Math.min(STD.strlen(dst), len);
		int i = start, s_Index = 0, d_Index = 0, ll = len;
		while (str[i] != 0 && ll > 0) {
			if (len + start - i < d_len)
				break;
			if (str[i] == dst[0]) {
				s_Index = i + 1;
				d_Index = 1;
				while (d_Index < d_len) {
					if (str[s_Index] != dst[d_Index]) {
						break;
					}
					s_Index++;
					d_Index++;
				}
				if (d_Index == d_len) {
					return i;
				}
			}
			i++;
			ll--;
			if (i >= str.length) {
				break;
			}
		}
		return -1;
	}

	// public static final int NUMBER_POUND[] = {10, 100, 1000, 10000, 100000,
	// 1000000, 10000000, 100000000, 1000000000};
	public static final long NUMBER_POUND_long[] = { 10, 100, 1000, 10000,
			100000, 1000000, 10000000, 100000000, 1000000000l, 10000000000l,
			100000000000l, 1000000000000l, 10000000000000l, 100000000000000l,
			1000000000000000l, 10000000000000000l, 100000000000000000l,
			1000000000000000000l };

	public static final long getNumberPound(int num) {
		if (num <= 0) {
			return 1;
		} else if (num <= NUMBER_POUND_long.length) {
			return NUMBER_POUND_long[num - 1];
		} else {
			return NUMBER_POUND_long[NUMBER_POUND_long.length - 1];
		}
	}

	public static final int getDataLength(long data) {
		if (data < 0) {
			data = -data;
		}
		for (int i = 0; i < NUMBER_POUND_long.length; i++) {
			if (data < NUMBER_POUND_long[i]) {
				return i + 1;
			}
		}
		return NUMBER_POUND_long.length;
	}

	// 数字转换成指定小数点位数的字符串, data放大rate倍(如果rate == 0, 默认10000)
	public static String DataToString(long data, int dotlen, int rate) {
		StringBuffer str = new StringBuffer();
		DataToString(str, data, dotlen, rate);
		return str.toString();
	}

	public static void DataToString(StringBuffer str, long data, int dotlen,
			int rate) {
		if (data < 0) {
			str.append('-');
			DataToString(str, -data, dotlen, rate);
			return;
		}

		if (rate == 0) {
			rate = 10000;
		}

		if (dotlen <= 0) {
			str.append((data + rate / 2) / rate);
			return;
		}

		str.append(data / rate);

		long times = getNumberPound(dotlen);
		if (times > rate)
			times = rate;
		long temp = rate / times;

		long newdata = data % rate;
		int len = str.length();
		str.append((newdata + temp / 2) / temp + times);
		str.setCharAt(len, '.');
	}

	//
	public static final String getDateSringyyyymmdd(int date) // 格式yyyymmdd
	{
		StringBuffer str = new StringBuffer();
		str.append(date);
		return str.toString();
	}

	//
	public static final String getStringDateMinmmddhhmm(int date, int min) // 格式mmdd-hhmm
	{
		StringBuffer str = new StringBuffer();
		if (date < 1000)
			str.append('0');
		str.append(date);
		str.append('-');

		int h = min / 100;
		if (h < 10) {
			str.append(0);
		} else if (h >= 24) {
			h = h - 24;
			if (h < 10) {
				str.append(0);
			}
		}
		str.append(h);
		str.append(':');
		int minute = min % 100;
		if (minute < 10) {
			str.append(0);
		}
		str.append(minute);

		return str.toString();
	}

	public static final String getStringDateyymmddhhmmss(long date) // 格式yyyy-mm-dd
																	// hh:mm:ss
	{
		int d = STD.getCurDate(date);
		int t = STD.getCurTime(date);
		return String.format("%04d-%02d-%02d %02d:%02d:%02d", d / 10000,
				d / 100 % 100, d % 100, t / 10000, t / 100 % 100, t % 100);
	}

	//
	public static final String getDateSring(int date) // 格式yymmdd
	{
		StringBuffer str = new StringBuffer();
		str.append(date % 1000000);
		return str.toString();
	}

	//
	public static final String getStringDateMin(int date, int min) // 格式ddhhmm
	{
		StringBuffer str = new StringBuffer();
		// if(date < 1000)
		// str.append('0');
		str.append(date % 100);
		// str.append('-');
		if (min < 1000)
			str.append('0');
		str.append(min);
		return str.toString();
	}

	public static final String getNumString(int num) {
		StringBuffer str = new StringBuffer();

		if (num < 10)
			str.append("0").append(num);
		else
			str.append(num);

		return str.toString();
	}

	// 空字符串数值校正, dotlen：小数点位数
	public static final String ValueString(String text, int dotlen) {
		StringBuffer str = new StringBuffer();
		if (text.length() <= 0) {
			if (dotlen == 0) {
				str.append("0");
				return str.toString();
			}
			str.append("0.");
			for (int i = 0; i < dotlen; i++) {
				str.append("0");
			}
			return str.toString();
		} else {
			return str.append(text).toString();
		}
	}

	public static final String LongtoString(long text) {
		StringBuffer str = new StringBuffer();
		return str.append(text).toString();
	}

	public static final String IntToString(int n) {
		String ret = String.format("%d", n);

		return ret;
	}

	public static final int StringToInt(String text) {
		if (text == null || text.length() <= 0)
			text = "0";

		try {
			return Integer.parseInt(text);
		} catch (Exception e) {
			return 0;
		}
	}

	//
	public static final float StringToValue(String text) {
		if (text == null || text.length() <= 0)
			text = "0.00";

		try {
			return Float.parseFloat(text);
		} catch (Exception e) {
			return 0.00f;
		}
	}

	//
	public static final double StringToDouble(String text) {
		if (text == null || text.length() <= 0)
			text = "0.00";

		try {
			return Double.parseDouble(text);
		} catch (Exception e) {
			return 0.00f;
		}
	}

	public static final String DoubleToString(double data) {
		return String.valueOf(data);
	}

	// 通过ch字符，把str解析成string的数组
	public static final String[] getStringBufferArray(String str, String ch) {
		String temp = str.replaceAll(ch, "");
		int l = str.length();
		int start = 0;
		int param = 0;
		int length = l - temp.length() + 1;

		if (0 >= length) {
			return new String[0];
		}

		String[] sbf = new String[length];

		for (int i = 0; i < l - 1; i++) {
			if (ch.charAt(0) == str.charAt(i)) {
				sbf[param++] = (String) str.substring(start, i);
				start = i + 1;
			}
		}
		sbf[param] = (String) str.substring(start);
		return sbf;
	}

	public static final String[] getStringBufferArray(String str) {
		return getStringBufferArray(str, " ");
	}

	public static final String[] getStringBufferArray(String str, String ch,
			int length) {
		int l = str.length();
		int start = 0;
		int param = 0;
		String[] sbf = new String[length];
		for (int i = 0; i < l - 1; i++) {
			if (ch.charAt(0) == str.charAt(i)) {
				sbf[param++] = (String) str.substring(start, i);
				start = i + 1;
				if (param == length)
					return sbf;
			}
		}
		sbf[param] = (String) str.substring(start);
		return sbf;
	}

	public static final String[] getStringBufferArray(String str, int length) {
		return getStringBufferArray(str, " ", length);
	}

	public static final long getCurDataTime(Date d) {

		int date = d.getYear() * 10000 + d.getMonth() * 100 + d.getDate();
		int time = d.getHours() * 10000 + d.getMinutes() * 100 + d.getSeconds();

		return (((long) date << 32) & 0xffffffff00000000L) + time;
	}

	// 获取当前数据时间， 格式：低四字节为时间(hhmmss)，高四字节为日期(yyyymmdd)
	public static final long getCurDataTime() {

		Time t = new Time("GMT+8");
		t.setToNow(); // 取得系统时间

		int date = t.year * 10000 + t.month * 100 + t.monthDay;
		int time = t.hour * 10000 + t.minute * 100 + t.second;

		return (((long) date << 32) & 0xffffffff00000000L) + time;
	}

	public static final long getDataTime(int date, int time) {

		// Time t = new Time("GMT+8");
		// t.setToNow(); // 取得系统时间
		//
		// int date = t.year * 10000 + t.month * 100 + t.monthDay;
		// int time = t.hour * 10000 + t.minute * 100 + t.second;

		return (((long) date << 32) & 0xffffffff00000000L) + time;
	}

	//
	public static final int getCurDate(long time) {
		int date = (int) (time >> 32);
		return date;
	}

	//
	public static final int getCurTime(long time) {
		int ret = (int) (time & 0xffffffff);
		return ret;
	}

	public static byte[] getBytesFromChars(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);

		return bb.array();
	}

	public static char[] getCharsFromBytes(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	public static int getUnicodeBytesStringLen(byte[] data, int offset, int len) {
		int num = len / 2 + 2;
		len = 0;
		for (int i = 0; i < num; i++) {
			char c = MyByteBuffer.getChar(data, offset);
			if (c == 0) {
				break;
			}
			offset += 2;
			len++;
		}
		return len;
	}

	public static char[] getCharsFromUnicodeBytes(byte[] data, int offset,
			int len) {
		int num = len / 2;
		char[] str = new char[num + 1];
		for (int i = 0; i < num; i++) {
			char c = MyByteBuffer.getChar(data, offset);

			if (c == 0) {
				break;
			}
			offset += 2;
			str[i] = c;
		}
		str[num] = 0;
		return str;
	}

	public static String getStringFromUnicodeBytes(byte[] data, int offset,
			int len) {
		int num = len / 2;
		char[] str = new char[num + 1];
		len = 0;
		for (int i = 0; i < num; i++) {
			char c = MyByteBuffer.getChar(data, offset);

			if (c == 0) {
				break;
			}
			offset += 2;
			str[i] = c;
			len++;
		}
		str[num] = 0;
		return new String(str, 0, len);
	}

	public static String getStringFromBytes(byte[] data, int offset, int len) {

		int nLength = strlen(data, offset, len);
		return new String(data, offset, nLength);
	}

	static public StringBuffer wcharToString(byte[] data, int offset, int len) {
		int num = len / 2 + 2;
		StringBuffer str = new StringBuffer(num);
		for (int i = 0; i < num; i++) {
			char c = MyByteBuffer.getChar(data, offset);

			if (c == 0) {
				break;
			}
			offset += 2;
			str.append(c);
		}

		return str;
	}

	public static int Base2Chr(short n, byte[] out, int offset) {
		n &= 0x3F;
		if (n < 26) {
			out[offset] = (byte) (n + 'A');
			return 1;
		} else if (n < 52) {
			out[offset] = (byte) (n - 26 + 'a');
			return 1;
		} else if (n < 62) {
			out[offset] = (byte) (n - 52 + '0');
			return 1;
		} else if (n == 62) {
			out[offset++] = '%';
			out[offset++] = '2';
			out[offset] = 'B';
			return 3;
		} else {
			out[offset] = '/';
			return 1;
		}

	}

	public static byte[] Base64Filter(byte[] str) // 过滤掉错误字符
	{
		if (str == null) {
			return null;
		}
		StringBuffer strOut = new StringBuffer();

		byte c = str[0];
		for (int i = 0; i < str.length && c != 0; i++) {
			c = str[i];
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || (c == '%') || (c == '/')
					|| (c == '+') || (c == '=')) {
				strOut.append(c);
			}
		}
		return strOut.toString().getBytes();
	}

	// ---------------------------------------------------------------------------
	public static byte Chr2Base(byte c) {
		if (c >= 'A' && c <= 'Z')
			return (byte) (c - 'A');
		else if (c >= 'a' && c <= 'z')
			return (byte) (c - 'a' + 26);
		else if (c >= '0' && c <= '9')
			return (byte) (c - '0' + 52);
		else if (c == '+')
			return 62;
		else
			return 63;
	}

	public static byte[] Base64Encode(byte[] out, int len, byte[] str,
			int str_len) {
		memset(out, 0, len);

		short c, t = 0;

		int pos = 0;
		int n = str_len;
		len--;

		for (int i = 0; i < n; i++) {
			c = (short) (str[i] & 0xff);
			if (i % 3 == 0) {
				if (pos + 4 >= len) {
					break;
				}
				pos += Base2Chr((short) (c >> 2), out, pos);
				t = (short) ((c << 4) & 0x3F);
			} else if (i % 3 == 1) {
				if (pos + 4 >= len) {
					break;
				}
				pos += Base2Chr((short) (t | (c >> 4)), out, pos);
				t = (short) ((c << 2) & 0x3F);
			} else {
				if (pos + 6 >= len) {
					break;
				}
				pos += Base2Chr((short) (t | (c >> 6)), out, pos);
				pos += Base2Chr((short) (c & 0x3F), out, pos);
			}
		}
		if (n % 3 != 0 && pos + 5 < len) {
			pos += Base2Chr(t, out, pos);
			out[pos] = '=';
			pos++;
			if (n % 3 == 1) {
				out[pos] = '=';
				pos++;
			}
		}
		return out;
	}

	public static int Base2Chr_WithAdd(short n, byte[] out, int offset) {
		n &= 0x3F;
		if (n < 26) {
			out[offset] = (byte) (n + 'A');
			return 1;
		} else if (n < 52) {
			out[offset] = (byte) (n - 26 + 'a');
			return 1;
		} else if (n < 62) {
			out[offset] = (byte) (n - 52 + '0');
			return 1;
		} else if (n == 62) {
			out[offset] = '+';
			return 1;
		} else {
			out[offset] = '/';
			return 1;
		}

	}

	public static byte[] Base64Encode_WithAdd(byte[] out, int len, byte[] str,
			int str_len) {
		memset(out, 0, len);
		short c, t = 0;

		int pos = 0;
		int n = str_len;
		len--;

		for (int i = 0; i < n; i++) {
			c = (short) (str[i] & 0xff);
			if (i % 3 == 0) {
				if (pos + 4 >= len) {
					break;
				}
				pos += Base2Chr_WithAdd((short) (c >> 2), out, pos);
				t = (short) ((c << 4) & 0x3F);
			} else if (i % 3 == 1) {
				if (pos + 4 >= len) {
					break;
				}
				pos += Base2Chr_WithAdd((short) (t | (c >> 4)), out, pos);
				t = (short) ((c << 2) & 0x3F);
			} else {
				if (pos + 6 >= len) {
					break;
				}
				pos += Base2Chr_WithAdd((short) (t | (c >> 6)), out, pos);
				pos += Base2Chr_WithAdd((short) (c & 0x3F), out, pos);
			}
		}
		if (n % 3 != 0 && pos + 5 < len) {
			pos += Base2Chr_WithAdd(t, out, pos);
			out[pos] = '=';
			pos++;
			if (n % 3 == 1) {
				out[pos] = '=';
				pos++;
			}
		}
		return out;
	}

	public static int Base64Decode(byte[] in, byte[] out, int out_len) {
		Base64Filter(in);

		out[0] = 0;
		int x, y, z;
		int i, j;
		byte[] bufa = new byte[4];
		byte[] bufb = new byte[3];

		out_len--;
		/*
		 * 由主调函数确保形参有效性
		 */
		int len = strlen(in);
		if (len < 4) {
			out[0] = 0;
			return 0;
		}

		x = (len - 4) / 4;
		i = 0;
		j = 0;
		for (z = 0; z < x; z++) {
			for (y = 0; y < 4; y++) {
				bufa[y] = Chr2Base(in[j + y]);
			} /* end of for */

			if (i + 3 > out_len) {
				break;
			}
			out[i] = (byte) (bufa[0] << 2 | (bufa[1] & 0x30) >> 4);
			out[i + 1] = (byte) ((bufa[1] & 0x0F) << 4 | (bufa[2] & 0x3C) >> 2);
			out[i + 2] = (byte) ((bufa[2] & 0x03) << 6 | (bufa[3] & 0x3F));
			i += 3;
			j += 4;
		} /* end of for */
		for (z = 0; z < 4; z++) {
			bufa[z] = Chr2Base(in[j + z]);
		} /* end of for */
		/*
		 * 编码算法确保了结尾最多有两个'='
		 */
		if ('=' == in[len - 2]) {
			y = 2;
		} else if ('=' == in[len - 1]) {
			y = 1;
		} else {
			y = 0;
		}
		/*
		 * BASE64算法所需填充字节个数是自识别的
		 */
		for (z = 0; z < y; z++) {
			bufa[4 - z - 1] = 0x00;
		} /* end of for */
		bufb[0] = (byte) (bufa[0] << 2 | (bufa[1] & 0x30) >> 4);
		bufb[1] = (byte) ((bufa[1] & 0x0F) << 4 | (bufa[2] & 0x3C) >> 2);
		bufb[2] = (byte) ((bufa[2] & 0x03) << 6 | (bufa[3] & 0x3F));
		/*
		 * y必然小于3
		 */
		if (i + 3 - y <= out_len) {
			for (z = 0; z < 3 - y; z++) {
				out[i + z] = (byte) bufb[z];
			} /* end of for */
			/*
			 * 离开for循环的时候已经z++了
			 */
			i += z;
		}
		out[i] = 0;
		out_len = i;
		return (i);

	}

	// 判断一个String 是否在String数组里
	// 如果在数组里，返回在数组的index，如果不在，返回-1
	public static int IsHave(String[] strArray, String str) {
		int retIndex = -1;

		if (strArray != null && str != null) {
			for (int i = 0; i < strArray.length; i++) {
				if (str.equals(strArray[i])) {
					retIndex = i;
					break;
				}
			}
		}

		return retIndex;
	}

	// 判断一个int 是否在int数组里
	// 如果在数组里，返回在数组的index，如果不在，返回-1
	public static int IsHaveInt(int[] intArray, int data) {
		int retIndex = -1;

		if (intArray != null) {
			for (int i = 0; i < intArray.length; i++) {
				if (data == intArray[i]) {
					retIndex = i;
					break;
				}
			}
		}
		return retIndex;
	}

	// 半角转全角
	public static String ToSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = 12288; // 采用十六进制,相当于十进制的12288

			} else if (c[i] < 127) { // 采用八进制,相当于十进制的127
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	// 全角转半角
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static int getMinutes(int start, int end) {
		int ret = 0;

		int hstart = start / 100;
		int hend = end / 100;
		int h = hend - hstart;

		int minuteStart = start % 100;
		int minuteEnd = end % 100;
		int minute = minuteEnd - minuteStart;

		ret = h * 60 + minute;
		return ret;
	}

	// 根据时间和增加的值（分钟）返回时间
	// time-hhmm
	public static int getTimeWithAdd(int time, int minute) {
		int ret = 0;

		int h1 = time / 100;
		int m1 = time % 100;

		int min = m1 + minute;
		int hAdd = min / 60;
		min = min % 60;

		ret = (h1 + hAdd) * 100 + min;
		return ret;
	}

	public static String unicodeToUtf8(byte[] data, int len)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		int offset = 0;
		for (int i = 0; i < len; i++) {
			// String s = str.substring(i, i + 1);
			char c = MyByteBuffer.getChar(data, offset);
			offset += 2;
			if (c > 0x80) {
				byte[] bytes = String.valueOf(c).getBytes("Unicode");
				String binaryStr = "";
				for (int j = 2; j < bytes.length; j += 2) {
					// the first byte
					String hexStr = getHexString(bytes[j + 1]);
					String binStr = getBinaryString(Integer.valueOf(hexStr, 16));
					binaryStr += binStr;
					// the second byte
					hexStr = getHexString(bytes[j]);
					binStr = getBinaryString(Integer.valueOf(hexStr, 16));
					binaryStr += binStr;
				}
				// convert unicode to utf-8
				String s1 = "1110" + binaryStr.substring(0, 4);
				String s2 = "10" + binaryStr.substring(4, 10);
				String s3 = "10" + binaryStr.substring(10, 16);
				byte[] bs = new byte[3];
				bs[0] = Integer.valueOf(s1, 2).byteValue();
				bs[1] = Integer.valueOf(s2, 2).byteValue();
				bs[2] = Integer.valueOf(s3, 2).byteValue();
				String ss = new String(bs, "UTF-8");
				sb.append(ss);
			} else {
				sb.append(String.valueOf(c));
			}
		}
		return sb.toString();
	}

	public static String getHexString(byte b) {
		String hexStr = Integer.toHexString(b);
		int m = hexStr.length();
		if (m < 2) {
			hexStr = "0" + hexStr;
		} else {
			hexStr = hexStr.substring(m - 2);
		}
		return hexStr;
	}

	public static String getBinaryString(int i) {
		String binaryStr = Integer.toBinaryString(i);
		int length = binaryStr.length();
		for (int l = 0; l < 8 - length; l++) {
			binaryStr = "0" + binaryStr;
		}
		return binaryStr;
	}

	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
