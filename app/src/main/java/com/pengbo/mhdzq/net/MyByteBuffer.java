package com.pengbo.mhdzq.net;

public class MyByteBuffer {

	private MyByteBuffer() {
	}

	public static byte getByte(byte[] data, int offset) {
		if (offset >= data.length) {
			return (Byte) null;
		}
		return data[offset];
	}

	public static int getUnsignedByte(byte[] data, int offset) {
		if (offset >= data.length) {
			return 0;
		}
		return data[offset] & 0xff;
	}

	public static void getBytes(byte[] data, int offset, byte[] out,
			int out_offset, int len) {
		if (offset + len >= data.length) {
			return;
		}
		System.arraycopy(data, offset, out, out_offset, len);
	}

	public static short getShort(byte[] data, int offset) {
		if (offset >= (data.length - 1)) {
			return 0;
		}
		return (short) (((data[offset]) & 0xff) + ((data[offset + 1] << 8) & 0xff00));
	}

	public static int getUnsignedShort(byte[] data, int offset) {
		if (offset >= (data.length - 1)) {
			return 0;
		}
		return (int) (((data[offset]) & 0xff) + ((data[offset + 1] << 8) & 0xff00));
	}

	public static int getInt(byte[] data, int offset) {
		if (offset >= (data.length - 3)) {
			return 0;
		}
		return ((data[offset]) & 0xff) + ((data[offset + 1] << 8) & 0xff00)
				+ ((data[offset + 2] << 16) & 0xff0000)
				+ ((data[offset + 3] << 24) & 0xff000000);
	}

	public static float getFloat(byte[] data, int offset) {
		// byte []dstData = new byte[4];
		// System.arraycopy(data, offset, dstData, 0, 4);
		// float fVal = Float.parseFloat(new String(dstData));
		// return fVal;
		if (offset + 4 >= data.length) {
			return 0f;
		}
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = offset; i < (offset + len); i++) {
			tmp[cnt] = data[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
			accum |= ((int) (tmp[i] & 0xff)) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}

	public static double getDouble(byte[] data, int offset) {
		// byte []dstData = new byte[8];
		// System.arraycopy(data, offset, dstData, 0, 8);
		// double dVal = Double.parseDouble(new String(dstData));
		// return dVal;
		if (offset + 8 >= data.length) {
			return 0f;
		}
		int i = 0;
		int len = 8;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = offset; i < (offset + len); i++) {
			tmp[cnt] = data[i];
			cnt++;
		}
		long accum = 0;
		i = 0;
		for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
			accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
			i++;
		}
		return Double.longBitsToDouble(accum);
	}

	public static long getLong(byte[] data, int offset) {
		if (offset >= (data.length - 7)) {
			return 0;
		}
		return ((data[offset]) & 0xff)
				+ (((long) data[offset + 1] << 8) & 0xff00)
				+ (((long) data[offset + 2] << 16) & 0xff0000)
				+ (((long) data[offset + 3] << 24) & 0xff000000L)
				+ (((long) data[offset + 4] << 32) & 0xff00000000L)
				+ (((long) data[offset + 5] << 40) & 0xff0000000000L)
				+ (((long) data[offset + 6] << 48) & 0xff000000000000L)
				+ (((long) data[offset + 7] << 56) & 0xff00000000000000L);
	}

	public static char getChar(byte[] data, int offset) {
		if (offset >= (data.length - 1)) {
			return 0;
		}
		return (char) (((data[offset]) & 0xff) + ((data[offset + 1] << 8) & 0xff00));
	}

	public static void putByte(byte[] data, int offset, byte value) {
		if (offset >= data.length) {
			return;
		}
		data[offset] = value;
	}

	public static void putBytes(byte[] dst, int dst_offset, byte[] src,
			int src_offset, int len) {
		if ((dst_offset + len) < dst.length) {
			System.arraycopy(src, src_offset, dst, dst_offset, len);
		}
	}

	public static void putShort(byte[] data, int offset, short value) {
		if (offset >= (data.length - 1)) {
			return;
		}
		data[offset] = (byte) (value & 0xff);
		data[offset + 1] = (byte) ((value >> 8) & 0xff);
	}

	public static void putChar(byte[] data, int offset, char value) {
		if (offset >= (data.length - 1)) {
			return;
		}
		data[offset] = (byte) (value & 0xff);
		data[offset + 1] = (byte) ((value >> 8) & 0xff);
	}

	public static void putInt(byte[] data, int offset, int value) {
		if (offset >= (data.length - 3)) {
			return;
		}
		data[offset] = (byte) (value & 0xff);
		data[offset + 1] = (byte) ((value >> 8) & 0xff);
		data[offset + 2] = (byte) ((value >> 16) & 0xff);
		data[offset + 3] = (byte) ((value >> 24) & 0xff);
	}

	public static void putLong(byte[] data, int offset, long value) {
		if (offset >= (data.length - 7)) {
			return;
		}
		data[offset] = (byte) (value & 0xff);
		data[offset + 1] = (byte) ((value >> 8) & 0xff);
		data[offset + 2] = (byte) ((value >> 16) & 0xff);
		data[offset + 3] = (byte) ((value >> 24) & 0xff);
		data[offset + 4] = (byte) ((value >> 32) & 0xff);
		data[offset + 5] = (byte) ((value >> 40) & 0xff);
		data[offset + 6] = (byte) ((value >> 48) & 0xff);
		data[offset + 7] = (byte) ((value >> 56) & 0xff);
	}
}
