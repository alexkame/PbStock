package com.pengbo.mhdzq.data;

import java.util.Random;

import com.pengbo.mhdzq.net.MyByteBuffer;


/**
 * 数据 加密 类 
 * @author pobo
 *
 */
public class CDataEncrypt {
	//产生随机字符串
	public static void	CreateRandData(char[] out, int len)
	{
		final char[] randstring = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

		Random	rand = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < len; i++)
		{
			int temp = rand.nextInt();
			if(temp < 0) 
				temp = -temp;
			int no	= temp % randstring.length;
			out[i]	= randstring[no];
		}
		rand	= null;
	}
	
	//从随机字符串里获取密钥
	public static void	GetKeyFromRandData(byte[] data, int data_len, byte[] key, int key_len)
	{
		if (data_len == 0)
		{
			return;
		}
		int value = (int)(GetValueFromRandData(data, data_len) % data_len);
		for (int i = 0; i < key_len; i++)
		{
			key[i]	= data[value];
			value	= value * 19 % data_len;
		}
	}

	static long GetValueFromRandData(byte[] data, int data_len)
	{
		long value = 0;
		int offset = 0;
		while (data_len >= 4)
		{
			value	+= ((long)MyByteBuffer.getInt(data, offset))&0xffffffff;
			offset	+= 4;
			data_len -= 4;
		}

		if (value == 0)
		{
			return 1;
		}

		return value;
	}
}
