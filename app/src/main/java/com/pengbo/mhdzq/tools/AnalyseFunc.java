package com.pengbo.mhdzq.tools;


/**
 * 分析类
 * 
 * @author pobo
 * 
 */
public final class AnalyseFunc {

	/** MA 简单移动平均线
	 * @param buffer 要计算的原值以及存储计算后的值
	 * @param n 需要计算的值得个数
	 * @param para MA的天数
	 */
	public final static void MA(int[] buffer, int n, int para) {
		int i;
		long total = 0;
		int t_value;

		int para_half = para / 2;

		if (n < 2)
			return;
		if (para < 2)
			return;

		if (para < n) {
			total = 0;

			int posf = n - 1;
			int pos = n - 1;
			for (i = 0; i < para; i++, posf--) {
				total += buffer[posf];
			}

			for (i = n - para; i > 0; i--, posf--, pos--) {
				t_value = buffer[pos];
				buffer[pos] = (int) ((total + para_half) / para);
				total -= t_value;
				total += buffer[posf];
			}
		}
		//
		int firstnum = Math.min(n, para);
		{
			total = 0;
			for (i = 0; i < firstnum; i++) {
				total += buffer[i];
				buffer[i] = (int) ((total + (i + 1) / 2) / (i + 1));
			}
		}
	}
	
	public static void MA(long[] buffer, int n, int para) {
		int i;
		long total = 0; // 存储多日值之和
		long t_value; // 临时计算时所用

		int para_half = para / 2;

		if (n < 2)
			return;
		if (para < 2)
			return;

		if (para < n) {
			total = 0;
			// 第一步，计算
			int posf = n - 1;
			int pos = n - 1;
			for (i = 0; i < para; i++, posf--) {
				total += buffer[posf];
			}

			for (i = n - para; i > 0; i--, posf--, pos--) {
				t_value = buffer[pos];
				buffer[pos] = ((total + para_half) / para);
				total -= t_value;
				total += buffer[posf];
			}
		}
		//
		int firstnum = Math.min(n, para);
		{
			total = 0;
			for (i = 0; i < firstnum; i++) {
				total += buffer[i];
				buffer[i] = ((total + (i + 1) / 2) / (i + 1));
			}
		}
	}

	// 加权移动平均线
	public static void SMA(long[] buffer, int num, int n, int m) {
		// SMA(n,m) = SMA(n-1,m) * (n-m)/n + value * m / n;
		// 举例:m=2, n = 6
		// SMA（6,2）=前一日SMA（6,2）×4/6＋今日收盘价×2/6
		// m=1,n=6
		// SMA(6,1)=前一日SMA（6,1）×5/6＋今日收盘价×1/6

		if (m >= n) {
			return;
		}
		long d_total = buffer[0];

		for (int i = 1; i < num; i++) {
			d_total = (d_total * (n - m) + (buffer[i]) * m + n / 2) / n;
			buffer[i] = d_total;// + ((d_total>0)?0.5:-0.5);
		}
	}
	
	// 指数移动平均线
	// buffer为存储的数据，同时计算后的数据
	// n为数据个数
	// m为移动天数
	public static void EMA(int[] buffer, int n, int m) {
		if (m < 1) {
			return;
		}
		// 放大m*1000再计算
		// 举例:m=12
		// EMA（12）=前一日EMA（12）×11/13＋今日收盘价×2/13
		// 前12日用算术平均
		long d_total = 0;
		{
			int start = Math.min(m, n);
			long total = buffer[0];
			for (int i = 1; i < start; i++) {
				total += buffer[i];
				buffer[i] = (int) ((total + (i + 1) / 2) / (i + 1));
			}

			d_total = total * 1000;
		}

		long t1 = 2000 * m;
		long t2 = 1000 * m;
		for (int i = m; i < n; i++) {
			d_total = (d_total * (m - 1) + (buffer[i]) * t1) / (m + 1);
			buffer[i] = (int) ((d_total) / (t2));
		}
	}
	
//	// 除复权计算
//	// 注意这里面价格单位都放大了10000倍，且注意需转成INT64，否则溢出。红利也需要乘以10000，因为传输来的红利单位为元
//	public static int CalcBeforeWeightPrice(int AfterWeightPrice,
//			final tagWeightData p) {
//		return (int) ((AfterWeightPrice * (100000L + p.SGS + p.ZZGS + p.PDS)
//				- (long) p.PGJ * p.PDS + p.HL * 10000L + 50000) / 100000);
//	}
//
//	public static int CalcAfterWeightPrice(int BeforeWeightPrice,
//			final tagWeightData p) {
//		return (int) ((BeforeWeightPrice * 100000L + (long) p.PGJ * p.PDS
//				- p.HL * 10000L + 50000) / (100000 + p.SGS + p.ZZGS + p.PDS));
//	}

	/**
	 * N日平均绝对偏差
	 * @param buffer	原值，及存储计算后的值
	 * @param n			需要计算原值的个数
	 * @param N			平均绝对偏差的天数
	 */
	public final static void AVEDEV( int[] buffer, int n, int N ) {
		
		long total = 0, t_total = 0;
		
		if(n<2)    	return; 
		if(N<2) 	return;
		
		int pos = n - 1;
		// 计算
		for (int i = n - N; i >= 0; i--, pos--) {
			int j = 0, t = pos;
			// N日平均值
			t_total = 0;
			for (j = 0, t = pos; j < N; j++, t--) {
				t_total += buffer[t];
			}
			long avg = (t_total + N/2) / N;
			total = 0;
			for (j = 0, t = pos; j < N; j++, t--) {
				total += Math.abs(buffer[t] - avg);
			}
			buffer[pos] = (int) ((total + N/2) / N);
		}
		
		int firstnum = Math.min(n, N);
		for (int i = 0; i < firstnum-1; i++) {
			buffer[i] = 0;
		}
	}
	public final static void AVEDEV( long[] buffer, int n, int N ) {
		
		long total = 0, t_total = 0;
		
		if(n<2)    	return; 
		if(N<2) 	return;
		
		int pos = n - 1;
		// 计算
		for (int i = n - N; i >= 0; i--, pos--) {
			int j = 0, t = pos;
			// N日平均值
			t_total = 0;
			for (j = 0, t = pos; j < N; j++, t--) {
				t_total += buffer[t];
			}
			long avg = (t_total + N/2) / N;
			total = 0;
			for (j = 0, t = pos; j < N; j++, t--) {
				total += Math.abs(buffer[t] - avg);
			}
			buffer[pos] = (total + N/2) / N;
		}
		
		int firstnum = Math.min(n, N);
		for (int i = 0; i < firstnum-1; i++) {
			buffer[i] = 0;
		}
	}
	
	/**
	 * N日估算标准差
	 * @param buffer	原值，及存储计算后的值
	 * @param ma		buffer的N日简单移动平均
	 * @param n			需要计算原值的个数
	 * @param N			估算标准差的天数
	 */
	public final static void STD( long[] buffer, long[] ma, int n, int N ) {
		
		int pos = n - 1;
		// 计算
		for (int i = n - N; i >= 0; i--, pos--) {
			
			long total = 0;
			for (int j = 0, t = pos; j < N; j++, t--) {
				
				total += Math.pow(buffer[t]-ma[t], 2);
				buffer[pos] = (int)(Math.sqrt(total/N)+0.5);
			}
		}
	}
}
