package com.pengbo.mhdzq.tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.DisplayMetrics;

import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockBaseInfoRecord;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagProfitRecord;

//@SuppressLint("DefaultLocale")
public class ViewTools {

	public final static int VIEW_TRENDLINE = 2;
	public final static int VIEW_KLINE_DAY = 3;
	public final static int VIEW_KLINE_WEEK = 4;
	public final static int VIEW_KLINE_MONTH = 5;
	public final static int VIEW_KLINE_1M = 6;
	public final static int VIEW_KLINE_3M = 7;
	public final static int VIEW_KLINE_5M = 8;
	public final static int VIEW_KLINE_15M = 9;
	public final static int VIEW_KLINE_30M = 10;
	public final static int VIEW_KLINE_60M = 11;
	public final static int VIEW_KLINE_120M = 12;
	public final static int VIEW_KLINE_240M = 13;

	public static double m_dRateWithoutRisk = 0.004;
	/** 字体 定义 **/
	public static final int TEXTSIZE_L = 20, // 字体大小--大
			TEXTSIZE_M = 14, // 字体大小--中
			TEXTSIZE_S = 10; // 字体大小--小
	public static boolean isShouldForegraund = false;

	/** 获取屏幕分辨率 ***/
	public static DisplayMetrics getScreenSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		return dm;
	}

	/** dip/px像素单位转换 **/
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/** dip/px像素单位转换 **/
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	//
	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) (Math.ceil(fm.descent - fm.top) + 2);
	}

	// 从文件读取图片
	public static Bitmap getBitmapFromFile(Context context, String fileName) {

		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(fileName);
		} catch (Exception e) {
			L.e("qlmobile", "Icon File Not Found...");
			return null;
		}
		Bitmap bm = BitmapFactory.decodeStream(is, null, null).extractAlpha();
		return bm;
	}

	public static Bitmap getBitmapFromFile_NoAlpha(Context context,
			String fileName) {

		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(fileName);
		} catch (Exception e) {
			L.e("qlmobile", "Icon File Not Found...");
			return null;
		}
		Bitmap bm = BitmapFactory.decodeStream(is, null, null);
		return bm;
	}

	public static void DrawTextRect(Canvas canvas, String text, int left,
			int top, int right, int bottom, Paint paint, Paint.Align align,
			int color) {
		paint.setColor(color);
		paint.setTextAlign(align);
		if (align == Paint.Align.LEFT) {
			canvas.drawText(text, left, bottom, paint);
		} else if (align == Paint.Align.RIGHT) {
			canvas.drawText(text, right, bottom, paint);
		} else if (align == Paint.Align.CENTER) {
			canvas.drawText(text, (left + right) >> 1, (bottom + top) >> 1,
					paint);
		}

	}

	// 在指定位置，以指定的颜色、对齐方式显示一串文本，返回显示高度
	public static void DrawText(Canvas canvas, String text, int left,
			int right, int top, int bottom, Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		int y = top + (int) (bottom - top - fm.ascent) / 2;
		if (bottom <= top) {
			int h = (int) (Math.ceil(fm.descent - fm.top) + 2);
			y = top + (int) (h - fm.ascent) / 2;
		}

		Paint.Align align = paint.getTextAlign();
		if (align == Paint.Align.LEFT) {
			canvas.drawText(text, left, y, paint);
		} else if (align == Paint.Align.RIGHT) {
			canvas.drawText(text, right, y, paint);
		} else if (align == Paint.Align.CENTER) {
			canvas.drawText(text, (left + right) >> 1, y, paint);
		}
	}

	public static void DrawText(Canvas canvas, String text, int left, int top,
			Paint paint, Paint.Align align, int color) {
		paint.setColor(color);
		paint.setTextAlign(align);
		canvas.drawText(text, left, top, paint);
	}

	// 在指定位置，以指定的对齐方式、指定小数位数，显示价格
	// flag: true--使用paint设置
	public static void DrawPrice(Canvas canvas, int x, int y, int price,
			int now, int yesterday, int dotlen, int pricerate, Paint paint,
			boolean flag) {
		DrawPrice(canvas, x, y, 0, price, now, yesterday, dotlen, pricerate,
				paint, flag);
	}

	public static void DrawPrice(Canvas canvas, int x, int top, int bottom,
			int price, int now, int yesterday, int dotlen, int pricerate,
			Paint paint, boolean flag) {
		String str = getStringByPrice(price, now, dotlen, pricerate);
		if (flag == false) {
			paint.setColor(getColor(price, yesterday));
			paint.setTextAlign(Paint.Align.LEFT);
		}
		int t_width = (int) paint.measureText(str);
		DrawText(canvas, str, x, x + t_width, top, bottom, paint);
	}

	// 在指定位置，以指定的对齐方式、指定小数位数，显示价格
	// flag: true--使用paint设置
	public static void DrawPrice_ZQ(Canvas canvas, int x, int y, int price,
			int now, int yesterday, int dotlen, int pricerate, Paint paint,
			boolean flag) {
		DrawPrice_ZQ(canvas, x, y, 0, price, now, yesterday, dotlen, pricerate,
				paint, flag);
	}

	/**
	 * 在指定位置，以指定的对齐方式、指定小数位数，显示价格 // flag: true--使用paint设置 走势画价格 涨跌在边框外面
	 * 
	 * @param canvas
	 * @param x
	 * @param y
	 * @param price
	 * @param now
	 * @param yesterday
	 * @param dotlen
	 * @param pricerate
	 * @param paint
	 * @param flag
	 */
	public static void DrawPrice_ZQ_Out(Canvas canvas, int x, int y, int price,
			int now, int yesterday, int dotlen, int pricerate, Paint paint,
			boolean flag) {
		DrawPrice_ZQ_Out(canvas, x, y, 0, price, now, yesterday, dotlen,
				pricerate, paint, flag);
	}

	/**
	 * 证券里面画图 中 价格 涨幅 颜色值 在外面画的地方传
	 * 
	 * @param canvas
	 * @param x
	 * @param top
	 * @param bottom
	 * @param price
	 * @param now
	 * @param yesterday
	 * @param dotlen
	 * @param pricerate
	 * @param paint
	 * @param flag
	 */
	public static void DrawPrice_ZQ(Canvas canvas, int x, int top, int bottom,
			int price, int now, int yesterday, int dotlen, int pricerate,
			Paint paint, boolean flag) {
		String str = getStringByPrice(price, now, dotlen, pricerate);
		if (flag == false) {
			// paint.setColor(getColorWhite(price, yesterday));
			paint.setTextAlign(Paint.Align.LEFT);
		}
		int t_width = (int) paint.measureText(str);
		DrawText(canvas, str, x, x + t_width, top, bottom, paint);
	}

	/**
	 * 走势画价格 涨跌在边框外面
	 * 
	 * @param canvas
	 * @param x
	 * @param top
	 * @param bottom
	 * @param price
	 * @param now
	 * @param yesterday
	 * @param dotlen
	 * @param pricerate
	 * @param paint
	 * @param flag
	 */
	public static void DrawPrice_ZQ_Out(Canvas canvas, int x, int top,
			int bottom, int price, int now, int yesterday, int dotlen,
			int pricerate, Paint paint, boolean flag) {
		String str = getStringByPrice(price, now, dotlen, pricerate);
		if (flag == false) {
			// paint.setColor(getColorWhite(price, yesterday));
			paint.setTextAlign(Paint.Align.LEFT);
		}
		int t_width = (int) paint.measureText(str);
		DrawText(canvas, str, x - t_width, x, top, bottom, paint);
	}

	// 在指定位置，以黄色、右对齐方式、指定宽度，显示量，单位手
	public static void DrawVolume(Canvas canvas, int x, int y, long volume,
			int market, int unit, Paint paint) {
		DrawVolume(canvas, x, y, 0, volume, market, unit, paint);
	}

	public static void DrawVolume(Canvas canvas, int x, int top, int bottom,
			long volume, int market, int unit, Paint paint) {
		String str = getStringByVolume(volume, market, unit, 6, false);
		paint.setTextAlign(Paint.Align.RIGHT);
		if (volume == 0) {
			paint.setColor(ColorConstant.DATA_NULL);
		} else {
			paint.setColor(ColorConstant.COLOR_VOL);
		}
		DrawText(canvas, str, 0, x, top, bottom, paint);
	}

	// 在指定位置，以白色、右对齐方式、指定宽度，显示金额
	public static void DrawAmount(Canvas canvas, int x, int y, long amount,
			int market, Paint paint) {
		DrawAmount(canvas, x, y, 0, amount, market, paint);
	}

	public static void DrawAmount(Canvas canvas, int x, int top, int bottom,
			long amount, int market, Paint paint) {
		String str = getStringByVolume(amount, market, 100, 6, false);
		paint.setTextAlign(Paint.Align.RIGHT);
		if (amount == 0) {
			paint.setColor(ColorConstant.DATA_NULL);
		} else {
			paint.setColor(ColorConstant.COLOR_VOL);
		}
		DrawText(canvas, str, 0, x, top, bottom, paint);
	}

	// 在指定位置，显示涨跌幅
	// flag:是否显示百分号
	// isSign:是否显示符号，只有负数才显示符号
	public static void DrawZDF(Canvas canvas, int x, int y, int zd, int now,
			int yesterday, boolean flag, boolean isSign, Paint paint,
			boolean pFlag) {
		String str = getZDF(zd, yesterday, now, flag, isSign);
		if (pFlag == false) {
			paint.setColor(getColor(zd));
			paint.setTextAlign(Paint.Align.RIGHT);
		}
		DrawText(canvas, str, 0, x, y, 0, paint);
	}

	// 在指定位置，显示涨跌幅
	// flag:是否显示百分号
	// isSign:是否显示符号，只有负数才显示符号
	public static void DrawZDF_ZQ(Canvas canvas, int x, int y, int zd, int now,
			int yesterday, boolean flag, boolean isSign, Paint paint,
			boolean pFlag) {
		String str = getZDF(zd, yesterday, now, flag, isSign);
		if (pFlag == false) {
			// paint.setColor(getColorWhite(zd));
			paint.setTextAlign(Paint.Align.RIGHT);
		}
		DrawText(canvas, str, 0, x, y, 0, paint);
	}

	// 在指定位置，显示涨跌幅
	// flag:是否显示百分号
	// isSign:是否显示符号，只有负数才显示符号
	public static void DrawZDF_ZQ_Out(Canvas canvas, int x, int y, int zd,
			int now, int yesterday, boolean flag, boolean isSign, Paint paint,
			boolean pFlag) {
		String str = getZDF(zd, yesterday, now, flag, isSign);
		if (pFlag == false) {
			// paint.setColor(getColorWhite(zd));
			paint.setTextAlign(Paint.Align.RIGHT);
		}
		int t_width = (int) paint.measureText(str);
		// DrawText(canvas, str, 0, x, y, 0, paint);

		DrawText(canvas, str, x, x + t_width, y, 0, paint);
	}

	// 在指定位置，显示数值
	public static void DrawInt(Canvas canvas, int x, int y, int data,
			int width, int color, Paint paint) {
		paint.setColor(color);
		paint.setTextAlign(Paint.Align.RIGHT);

		StringBuffer str = new StringBuffer();
		str.append(data);

		canvas.drawText(str.toString(), x, y, paint);
	}

	// 根据最小价格增加幅度进行加减，返回结果
	// price-原始价格
	// minPriceStep-最小幅度
	// bAdd-true:增加，false：减少
	// nPriceDotLen-价格小数点个数
	public static String getPriceByStep(String price, float minPriceStep,
			boolean bAdd, int nPriceDotLen) {
		float fPrice = STD.StringToValue(price);
		if (bAdd) {
			fPrice += minPriceStep;
		} else {
			fPrice -= minPriceStep;
		}

		if (fPrice < 0) {
			fPrice = 0.0f;
		}
		String format = String.format("%%.%df", nPriceDotLen);

		String retPrice = String.format(format, fPrice);
		return retPrice;
	}

	// 根据小数点位数、放大倍数返回实际价格
	public static float getFloatPriceByInt(int data, int dotlen, int unit) {
		if (unit == 0) {
			unit = 10000;
		}

		float fRet = (float) data / unit;
		return fRet;
	}

	// 根据小数点位数、放大倍数返回数值的字符串
	// flag: data为0时是否显示
	public static String getStringByInt(int data, int dotlen, int unit,
			boolean flag) {

		if (data == 0 && flag == false) {
			return "----";
		}

		if (unit == 0) {
			unit = 10000;
		}

		StringBuffer str = new StringBuffer();
		if (dotlen == 0) {
			int add = (data > 0) ? unit / 2 : (-unit / 2);
			str.append((data + add) / unit);
			return str.toString();
		}

		int len = STD.getDataLength(unit);
		long rate = 10000;
		if (len > 0) {
			rate = STD.getNumberPound(len - 1);
		}
		STD.DataToString(str, (long) data * rate / unit, dotlen, (int) rate);

		return str.toString();
	}

	public static String getStringByFloatPrice(float price, int now, int dotlen) {
		String format = String.format("%%.%df", dotlen);
		String strRet = String.format(format, price);
		return strRet;
	}

	// 根据小数点位数返回价格的字符串
	public static String getStringByPrice(int price, int now, int dotlen,
			int pricerate) {
		if (price == 0) {
			return "----";
		}

		if (pricerate == 0) {
			pricerate = 10000;
		}

		int maxdec = 5;
		if (price >= 1000000000) // 5.1
		{
			maxdec = 1;
		} else if (price >= 100000000) // 4.2
		{
			maxdec = 2;
		} else if (price >= 10000000) // 3.3
		{
			maxdec = 3;
		} else if (price >= 1000000) // 3.3
		{
			maxdec = 4;
		}

		if (dotlen > maxdec) {
			dotlen = maxdec;
		}

		if (dotlen < 0) {
			dotlen = 2;
		} else if (dotlen == 0) {
			STD.LongtoString(price);
		}

		return STD.DataToString(price, dotlen, pricerate);
	}

	public static String getRateLongHu(int rate, boolean flag, boolean hasOper) {
		if (rate == 0) {
			return "0.00%";
		}
		StringBuffer str = new StringBuffer();
		STD.DataToString(str, (long) rate * 100, 2, 0);

		if (flag) {
			str.append("%");
		}

		if (hasOper && rate > 0)
			str.insert(0, "+");

		return str.toString();
	}

	// 单位：万元，放大100倍
	public static String getStringByLongHu(long data) {
		if (data == 0) {
			return "0.00";
		}

		StringBuffer out = new StringBuffer();
		if (data < 0) {
			out.insert(0, "-");
			data *= -1;
		}

		data /= 100;
		data += 50;
		data /= 100;

		out.append(data / 100);
		out.append('.');
		out.append(data % 100);

		return out.toString();
	}

	// 根据需要显示的宽度返回量的字符串
	// flag: data为0时是否显示
	// width >=3，否则会堆栈溢出
	public static String getStringByVolume(long data, int market, int unit,
			int width, boolean flag) {
		if (flag && data == 0) {
			return "0";
		}
		StringBuffer out = new StringBuffer();
		if (market == Global_Define.MARKET_HK) {
			IntToWidth_HK(data, width, out);
		} else {
			if (unit <= 0)
				unit = 100;
			data = (data + unit / 2) / unit;

			IntToWidth(data, width, out);
		}

		if (data < 0)
			out.insert(0, "-");

		return out.toString();
	}

	public static String getStringByAmount(long data, int market, int unit,
			int width, boolean flag) {
		if (flag && data == 0) {
			return "0";
		}
		if (market == Global_Define.MARKET_HK) {
			if (unit <= 0)
				unit = 100;
			data = (data + unit / 2) / unit;
		}

		return getStringByVolume(data, market, unit, width, flag);
	}

	public static String getStringByGu(long data, int market, int unit,
			int width, boolean flag) {
		if (flag && data == 0) {
			return "0";
		}
		StringBuffer out = new StringBuffer();
		if (unit <= 0)
			unit = 100;
		data = (data + unit / 2) / unit;

		{
			IntToWidth_Gu(data, width, out);
		}

		if (data < 0)
			out.insert(0, "-");

		return out.toString();
	}

	public static String getStringByGu_unprocess(long data, int market,
			int unit, int width, boolean flag) {
		if (flag && data == 0) {
			return "0";
		}
		StringBuffer out = new StringBuffer();

		out.append(data);

		return out.toString();
	}

	// 根据字段号直接获取价格
	public static float getPriceByFieldNo(int nFieldNo, TagLocalStockData stock) {
		float fPrice = 0;
		String strRet = "";
		switch (nFieldNo) {
		case Global_Define.FIELD_HQ_YESTERDAY:
			strRet = ViewTools.getStringByPrice(stock.HQData.nLastClose,
					stock.HQData.nLastPriceForCalc, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_OPEN:
			strRet = ViewTools.getStringByPrice(stock.HQData.nOpenPrice,
					stock.HQData.nLastPriceForCalc, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_HIGH:
			strRet = ViewTools.getStringByPrice(stock.HQData.nHighPrice,
					stock.HQData.nLastPriceForCalc, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_LOW:
			strRet = ViewTools.getStringByPrice(stock.HQData.nLowPrice,
					stock.HQData.nLastPriceForCalc, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_NOW:
			strRet = ViewTools.getStringByPrice(stock.HQData.nLastPriceForCalc,
					stock.HQData.nLastPriceForCalc, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_ZD:// 涨跌
			strRet = ViewTools.getStringByPrice(stock.HQData.nLastPriceForCalc
					- stock.HQData.nLastClear, stock.HQData.nLastPriceForCalc,
					stock.PriceDecimal, stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_UPPRICE:// 涨停
			strRet = ViewTools.getStringByPrice(stock.HQData.nUpperLimit,
					stock.HQData.nUpperLimit, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_DOWNPRICE:// 跌停
			strRet = ViewTools.getStringByPrice(stock.HQData.nLowerLimit,
					stock.HQData.nLowerLimit, stock.PriceDecimal,
					stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_BUYPRICE:// 委买价
			strRet = ViewTools.getStringByPrice(stock.HQData.buyPrice[0], 0,
					stock.PriceDecimal, stock.PriceRate);
			break;
		case Global_Define.FIELD_HQ_SELLPRICE:// 委卖价
			strRet = ViewTools.getStringByPrice(stock.HQData.sellPrice[0], 0,
					stock.PriceDecimal, stock.PriceRate);
			break;
		default:
			break;
		}
		fPrice = STD.StringToValue(strRet);
		return fPrice;
	}

	// 不转换单位
	public static String getVolume(long data, int market, int unit, boolean flag) {
		// 港股显示单位
		if (market == Global_Define.MARKET_HK) {

			return getStringByVolume(data * unit, market, unit, 6, flag);
		}

		if (flag && data == 0) {
			return "0";
		} else if (data == 0) {
			return "----";
		}
		if (unit <= 0)
			unit = 100;
		data = (data + unit / 2) / unit;

		StringBuffer str = new StringBuffer();
		str.append(data);

		return str.toString();
	}

	// 转换字符串
	public static int WidthString(int flag, int width, double data,
			StringBuffer out) {
		out.delete(0, out.length());
		STD.DataToString(out, (long) (data * 10000), 2, 0);
		// out.append( String.format("%.2f", data) );
		int len = out.length();
		if (width >= (len - 3) || flag >= 2) // 可显示
		{
			int left = width - len + 3;

			if (left < 1) {
				return WidthString(flag + 1, width, data / 10000.0, out);
			} else if (left == 1) {
				out.delete(0, out.length());
				out.append((long) (data + 0.5));
			} else {
				left--;
				if (left < 0) {
					left = 0;
				} else if (left > 2) {
					left = 2;
				}
				out.delete(0, out.length());
				STD.DataToString(out, (long) (data * 10000), left, 0);
				// String temp = String.format("%%.%df", left);
				// out.append( String.format(temp, data) );
			}
			return flag;
		}

		if (flag == 0) {
			width -= 1;
		}

		return WidthString(flag + 1, width, data / 10000.0, out);
	}

	public static int IntToWidth_Gu(long data, int width, StringBuffer out) {
		if (data == 0) {
			out.append("----");
			return out.length();
		}

		if (data < 0) {
			// out.append("-");
			return IntToWidth(-data, width - 1, out);
		}
		out.append(data);
		int len = out.length();
		if (width >= len && data < 100) // 可显示
		{
			return 0;
		}

		int flag = WidthString(1, width - 1, data / 100.0, out);

		if (flag == 1)
			out.append("万");
		else if (flag == 2)
			out.append("亿");

		return flag;
	}

	public static int IntToWidth(long data, int width, StringBuffer out) {
		if (data == 0) {
			out.append("----");
			return out.length();
		}

		if (data < 0) {
			// out.append("-");
			return IntToWidth(-data, width - 1, out);
			// return IntToWidth(-data, width, out);
		}
		out.append(data);
		int len = out.length();
		if (width >= len && data < 10000) // 可显示
		{
			return 0;
		}

		int flag = WidthString(1, width - 1, data / 10000.0, out);

		if (flag == 1)
			out.append("万");
		else if (flag == 2)
			out.append("亿");
		else if (flag == 3)
			out.append("万亿");

		return flag;
	}

	public static int WidthString_HK(int flag, int width, double data,
			StringBuffer out) {
		int len = STD.getDataLength((long) data);
		if (width >= len && data < 1000) // 可显示
		{
			int left = width - len;
			if (flag > 1 && left <= 2)
				STD.DataToString(out, (long) (data * 10000), 1, 0);
			else if (flag > 1 && left > 2)
				STD.DataToString(out, (long) (data * 10000), 2, 0);
			else
				out.append((long) data);
			return flag;
		}

		if (flag == 0) {
			width -= 1;
		}
		if (flag >= 3) {
			return flag;
		}

		return WidthString_HK(flag + 1, width, data / 1000.0, out);
	}

	public static int IntToWidth_HK(long data, int width, StringBuffer out) {
		if (data == 0) {
			out.append("----");
			return out.length();
		}

		if (data < 0) {
			// out.append("-");
			return IntToWidth_HK(-data, width - 1, out);
		}

		if (width > STD.getDataLength(data) && data < 10000) // 可显示
		{
			out.append(data);
			return 0;
		}

		int flag = WidthString_HK(1, width - 1, data / 1000.0, out);

		if (flag == 1)
			out.append("K");
		else if (flag == 2)
			out.append("M");
		else if (flag == 3)
			out.append("B");

		return flag;
	}

	// 由涨跌获取涨跌幅
	public static int getZDF(int zd, int yesterday) {
		if (yesterday == 0) {
			return 0;
		}
		double add = (zd > 0) ? 0.5 : (-0.5);
		return (int) (zd * 10000.0 / yesterday + add);
	}

	// 由涨跌获取涨跌幅 字符串
	// flag:是否显示百分号
	// isSign:是否显示符号，只有负数才显示符号
	public static String getZDF(int zd, int yesterday, int now, boolean flag,
			boolean isSign) {
		int zdf = getZDF(zd, yesterday);

		if (zdf == 0 && now == 0) {
			return "----";
		}
		// 不显示符号
		if (isSign == false && zdf < 0) {
			zdf = -zdf;
		}

		StringBuffer str = new StringBuffer();
		if (zdf < 1000 || zdf > -1000)
			STD.DataToString(str, (long) zdf * 100, 2, 0);
		else
			STD.DataToString(str, (long) zdf * 100, 1, 0);

		if (flag) {
			str.append("%");
		}
		return str.toString();
	}

	// 由成交量获取换手率
	public static double getHSL(double volume, double ltgb) {
		if (ltgb == 0) {
			return 0;
		}
		return volume / (ltgb * 10000.0);
	}

	// flag:是否显示百分号
	public static String getHSL(double volume, double ltgb, boolean flag) {
		double hsl = getHSL(volume, ltgb);

		return getHSL(hsl, flag);
	}

	public static String getHSL(double hsl, boolean flag) {
		if (hsl == 0.0) {
			return "----";
		}
		StringBuffer str = new StringBuffer();
		str.append(String.format("%.2f", hsl * 100));

		if (flag) {
			str.append("%");
		}
		return str.toString();
	}

	// 由每股收益算市盈率
	public static String getSYL(double now, double mgsy) {
		if (mgsy == 0.0) {
			return "----";
		}
		double syl = (now / mgsy);

		return getSYL(syl);
	}

	public static String getSYL(double syl) {
		if (syl <= 0.0) {
			return "----";
		}
		StringBuffer str = new StringBuffer();
		str.append(String.format("%.2f", syl));

		return str.toString();
	}

	// by ngj add
	public static String getZHENFU(int zhenfu, boolean flag) {
		if (zhenfu == 0) {
			return "----";
		}
		StringBuffer str = new StringBuffer();
		STD.DataToString(str, (long) zhenfu * 100, 2, 0);

		if (flag) {
			str.append("%");
		}
		return str.toString();
	}

	/**
	 * 比率 flag:是否显示百分号
	 */
	public static String getRate(int rate, boolean flag) {
		if (rate == 0) {
			return "----";
		}
		StringBuffer str = new StringBuffer();
		STD.DataToString(str, (long) rate * 100, 2, 0);

		if (flag) {
			str.append("%");
		}
		return str.toString();
	}

	// 显示颜色
	public static int getColor(float data) {
		return getColor(data, 0.0f);
	}

	public static int getColor(float data, float base) {
		int color = ColorConstant.PRICE_EQUAL;
		if (data == 0.0f && base != 0.0f) {
			color = ColorConstant.DATA_NULL;
		} else if (data > base) {
			color = ColorConstant.PRICE_UP;
		} else if (data < base) {
			color = ColorConstant.PRICE_DOWN;
		} else {
			// L.e("ViewTools", "data = " + data + ", base = " + base);
		}
		return color;
	}

	public static int getColor(long data) {
		return getColor(data, 0);
	}

	public static int getColor(long data, long base) {
		int color = ColorConstant.PRICE_EQUAL;
		if (data == 0 && base != 0) {
			color = ColorConstant.DATA_NULL;
		} else if (data > base) {
			color = ColorConstant.PRICE_UP;
		} else if (data < base) {
			color = ColorConstant.PRICE_DOWN;
		} else {
			// L.e("ViewTools", "data = " + data + ", base = " + base);
		}
		return color;
	}

	public static int getColor(int data) {
		return getColor(data, 0);
	}

	public static int getColorWhite(int data) {
		return getColorWhite(data, 0);
	}

	public static int getColorWhite(int data, int base) {

		// int color = ColorConstant.PRICE_EQUAL;
		// #ecedef（236，237，239）
		int color = Color.rgb(236, 237, 239); // 不涨不跌
		if (data == 0 && base != 0) {
			color = ColorConstant.DATA_NULL;
		} else if (data > base) {
			color = ColorConstant.PRICE_UP;
		} else if (data < base) {
			color = ColorConstant.PRICE_DOWN;
		} else {
			// L.e("ViewTools", "data = " + data + ", base = " + base);
		}
		return color;
	}

	public static int getColor(int data, int base) {
		// int color=Color.rgb(124, 124, 125); //不涨不跌
		int color = ColorConstant.PRICE_EQUAL;
		if (data == 0 && base != 0) {
			color = ColorConstant.DATA_NULL;
		} else if (data > base) {
			color = ColorConstant.PRICE_UP;
		} else if (data < base) {
			color = ColorConstant.PRICE_DOWN;
		} else {
			// L.e("ViewTools", "data = " + data + ", base = " + base);
		}
		return color;
	}

	public static int getTradeColor(int data, int base) {
		int color = Color.rgb(0x00, 0x49, 0xA4);
		if (data == 0 && base != 0) {
			color = Color.rgb(0x00, 0x49, 0xA4);
		} else if (data > base) {
			color = ColorConstant.PRICE_UP;
		} else if (data < base) {
			color = ColorConstant.PRICE_DOWN;
		}
		return color;
	}

	public static int getTradeColor(String strdata, String strbase) {
		int color = Color.rgb(0x00, 0x49, 0xA4);
		if (strdata == null || strdata.length() <= 0 || strbase == null
				|| strbase.length() <= 0)
			return color;

		try {
			double data = Double.valueOf(strdata);
			double base = Double.valueOf(strbase);
			if (data == 0.0 && base != 0.0) {
				color = Color.rgb(0x00, 0x49, 0xA4);
			} else if (data > base) {
				color = ColorConstant.PRICE_UP;
			} else if (data < base) {
				color = ColorConstant.PRICE_DOWN;
			}
		} catch (Exception e) {
			e.printStackTrace();
			color = Color.rgb(0x00, 0x49, 0xA4);
		}
		return color;
	}

	// 买卖气颜色
	public static int getMMQColor(double mmq) {
		int color = Color.WHITE;
		if (mmq == 0) // 0
			color = Color.rgb(50, 50, 50); // 0x323232
		else if (mmq > 0 && mmq <= 20) // (0, 20)
			color = Color.rgb(90, 0, 0); // 0x5A0000
		else if (mmq > 20 && mmq <= 40) // (20, 40)
			color = Color.rgb(180, 0, 0); // 0xB40000
		else if (mmq > 40 && mmq <= 70) // (40, 70)
			color = Color.rgb(255, 0, 0); // 0xFF0000
		else if (mmq > 70 && mmq <= 100) // (70, 100)
			color = Color.rgb(255, 0, 255); // 0xFF00FF
		else if (mmq >= -20 && mmq < 0) // (0, -20)
			color = Color.rgb(0, 90, 0); // 0x005A00
		else if (mmq >= -40 && mmq < -20) // (-20, -40)
			color = Color.rgb(0, 180, 0); // 0x00B400
		else if (mmq >= -70 && mmq < -40) // (-40, -70)
			color = Color.rgb(0, 255, 0); // 0x00FF00
		else if (mmq >= -100 && mmq < -70) // (-70, -100)
			color = Color.rgb(0, 255, 255); // 0x00FFFF

		return color;
	}

	public static String formatDate(String date) {
		if (date.length() < 8) {
			return date;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(date.substring(0, 4)).append("-");
		sb.append(date.substring(4, 6)).append("-");
		sb.append(date.substring(6));
		return sb.toString();
	}

	public static String formatDateFromINT(int date) {
		StringBuffer sb = new StringBuffer();
		sb.append(date / 10000).append("-");
		sb.append(date / 100 % 100).append("-");
		sb.append(date % 100);
		return sb.toString();
	}

	public static String formatTime(String time) {
		if (time.length() < 5) {
			return time;
		}
		StringBuffer sb = new StringBuffer();
		if (time.length() == 5) {
			time = "0" + time;
		}
		sb.append(time.substring(0, 2)).append(":");
		sb.append(time.substring(2, 4));
		// sb.append(time.substring(4));
		return sb.toString();
	}

	public static double getYearsDruationFromToday(int toData) {
		double dYearsDruation = 0;

		int y, m, d;
		Calendar cal = Calendar.getInstance();
		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH) + 1;
		d = cal.get(Calendar.DATE);

		dYearsDruation = FinanceModel.TimeDruation2Years(y, m, d,
				toData / 10000, toData / 100 % 100, toData % 100);
		return dYearsDruation;
	}

	public static int getDaysDruationFromToday(int toData) {
		int nDaysDruation = 0;

		int y, m, d;
		Calendar cal = Calendar.getInstance();
		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH) + 1;
		d = cal.get(Calendar.DATE);

		nDaysDruation = FinanceModel.TimeDruation2Days(y, m, d, toData / 10000,
				toData / 100 % 100, toData % 100);
		return nDaysDruation;
	}

	// 行权日是否是当月并且行权日未到
	public static boolean isStrikeDateInMonth(int strikeDate) {
		int y, m, d;
		Calendar cal = Calendar.getInstance();
		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH) + 1;
		d = cal.get(Calendar.DATE);

		int y2 = strikeDate / 10000;
		int m2 = strikeDate / 100 % 100;
		int d2 = strikeDate % 100;
		if (y == y2 && m == m2 && d <= d2) {
			return true;
		}

		return false;
	}

	// 价位
	public final static int j[] = { 0, 2500, 5000, 100000, 200000, 1000000,
			2000000, 5000000, 10000000, 20000000, 50000000, 99950000 };
	public final static int w[] = { 0, 10, 50, 100, 200, 500, 1000, 2000, 5000,
			10000, 20000, 50000 };

	// 计算香港价位，输入的价格参数为整型，放大了一百倍。返回整型价位，放大一百倍
	public static int getHKPriceUnit(int price) {
		for (int i = 0; i < j.length; i++) {
			if (price < j[i]) {
				return w[i];
			}
		}
		return 0;
	}

	// 根据加减，计算新的价格
	public static int getHKPriceByStep(int price, boolean up) {
		int jw = 0;
		if (up) {
			for (int i = 0; i < j.length; i++) {
				if (price < j[i]) {
					jw = w[i];
					price += jw;
					break;
				}
			}
		} else {
			for (int i = 0; i < j.length; i++) {
				if (price <= j[i]) {
					jw = w[i];
					price -= jw;
					break;
				}
			}
		}

		if (jw > 0) {
			price = price / jw * jw;
		}
		return price;
	}

	public static long getAverage(long num1, long num2) {
		long ret = 0;
		long max = (num1 >= num2) ? num1 : num2;
		long min = (num1 >= num2) ? num2 : num1;
		if (max > 0 && min < 0) {
			if (Math.abs(max) > Math.abs(min))
				ret = (max - min) / 2;
			else
				ret = (-max + min) / 2;
		} else
			ret = (max + min) / 2;

		return ret;
	}

	/**
	 * 根据字段号直接获取实际价格
	 * 
	 * @param stockData
	 *            合约信息
	 * @param nField
	 *            字段号
	 * @return 实际价格
	 */
	public static float getFloatPriceByFieldID(TagLocalStockData stockData,
			int nField) {
		float fPrice = 0;
		switch (nField) {
		case Global_Define.FIELD_HQ_YESTERDAY:
			fPrice = getFloatPriceByInt(stockData.HQData.nLastClose,
					stockData.PriceDecimal, stockData.PriceRate);
			break;
		case Global_Define.FIELD_HQ_OPEN:
			fPrice = getFloatPriceByInt(stockData.HQData.nOpenPrice,
					stockData.PriceDecimal, stockData.PriceRate);
			break;
		case Global_Define.FIELD_HQ_HIGH:
			fPrice = getFloatPriceByInt(stockData.HQData.nHighPrice,
					stockData.PriceDecimal, stockData.PriceRate);
			break;
		case Global_Define.FIELD_HQ_LOW:
			fPrice = getFloatPriceByInt(stockData.HQData.nLowPrice,
					stockData.PriceDecimal, stockData.PriceRate);
			break;
		case Global_Define.FIELD_HQ_NOW:
			fPrice = getFloatPriceByInt(stockData.HQData.nLastPrice,
					stockData.PriceDecimal, stockData.PriceRate);
			break;
		default:
			break;
		}
		return fPrice;
	}

	/**
	 * 根据字段号直接获取String
	 * 
	 * @param stockData
	 *            合约信息
	 * @param stockBaseInfo
	 *            股票基础数据
	 * @param nField
	 *            字段号
	 * @return 显示用的String
	 */
	public static String getStringByFieldID(TagLocalStockData stockData,
			TagLocalStockBaseInfoRecord stockBaseInfo, int nField) {
		String strRet = "";
		if (stockData == null) {
			return "----";
		}

		switch (nField) {
		case Global_Define.FIELD_HQ_HSL:// 换手率
		{
			if (stockBaseInfo == null) {
				strRet = "----";
			} else {
				strRet = getHSL(stockData.HQData.volume,
						stockBaseInfo.FlowCapital, true);
			}
		}
			break;
		case Global_Define.FIELD_HQ_LTSZ:// 流通市值
		{
			if (stockBaseInfo == null || DataTools.isStockIndex(stockData)) {
				strRet = "----";
			} else {
				float now = getFloatPriceByFieldID(stockData,
						Global_Define.FIELD_HQ_NOW);
				strRet = getStringByAmount((long) (now
						* stockBaseInfo.FlowCapital * 10000),
						stockData.HQData.market, 1,
						Global_Define.VOLUME_STRING_LENGHT, false);
			}
		}
			break;
		case Global_Define.FIELD_HQ_ZSZ:// 总市值
		{
			if (stockBaseInfo == null || DataTools.isStockIndex(stockData)) {
				strRet = "----";
			} else {
				float now = getFloatPriceByFieldID(stockData,
						Global_Define.FIELD_HQ_NOW);
				strRet = getStringByAmount((long) (now
						* stockBaseInfo.TotalCapital * 10000),
						stockData.HQData.market, 1,
						Global_Define.VOLUME_STRING_LENGHT, false);
			}
		}
			break;
		case Global_Define.FIELD_HQ_SYL:// 市盈率
		{
			if (stockBaseInfo == null || DataTools.isStockIndex(stockData)) {
				strRet = "----";
			} else {
				float now = getFloatPriceByFieldID(stockData,
						Global_Define.FIELD_HQ_NOW);
				strRet = getSYL((double) now, stockBaseInfo.ForecastAvgProfit);
			}
		}
			break;
		case Global_Define.FIELD_HQ_ZHENFU:// 振幅
		{
			int zf = getZDF(
					(stockData.HQData.nHighPrice - stockData.HQData.nLastClear),
					stockData.HQData.nLastClear);
			int df = getZDF(
					(stockData.HQData.nLowPrice - stockData.HQData.nLastClear),
					stockData.HQData.nLastClear);
			int zhenfu = zf - df;
			strRet = getZHENFU(zhenfu, true);
		}
			break;
		default:
			strRet = getStringByFieldID(stockData, nField, null);
			break;
		}
		return strRet;
	}

	/**
	 * 根据字段号直接获取String
	 * 
	 * @param stockData
	 *            合约信息
	 * @param nField
	 *            字段号
	 * @return 显示用的String
	 */
	public static String getStringByFieldID(TagLocalStockData stockData,
			int nField) {
		return getStringByFieldID(stockData, nField, null);
	}

	/**
	 * 根据字段号直接获取String
	 * 
	 * @param stockData
	 *            合约信息
	 * @param nField
	 *            字段号
	 * @param stockInfo
	 *            合约对应的标的信息，某些字段计算的时候需要
	 * @return 显示用的String
	 */
	public static String getStringByFieldID(TagLocalStockData stockData,
			int nField, TagLocalStockData stockInfo) {
		String strRet = "";
		switch (nField) {
		case Global_Define.FIELD_HQ_INVOLUME:
			if (DataTools.isStockIndex(stockData)) {
				strRet = "----";
			} else {
				strRet = getStringByVolume(
						(long) (stockData.HQData.volume - stockData.HQData.dWPL),
						stockData.HQData.market, stockData.VolUnit,
						Global_Define.VOLUME_STRING_LENGHT, false);
			}
			break;
		case Global_Define.FIELD_HQ_OUTVOLUME:
			if (DataTools.isStockIndex(stockData)) {
				strRet = "----";
			} else {
				strRet = getStringByVolume((long) stockData.HQData.dWPL,
						stockData.HQData.market, stockData.VolUnit,
						Global_Define.VOLUME_STRING_LENGHT, false);
			}
			break;
		case Global_Define.FIELD_HQ_YESTERDAY:
			strRet = getStringByInt(stockData.HQData.nLastClose,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_ZRJSJ:
			strRet = getStringByInt(stockData.HQData.nLastClear,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_OPEN:
			strRet = getStringByInt(stockData.HQData.nOpenPrice,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_HIGH:
			strRet = getStringByInt(stockData.HQData.nHighPrice,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_LOW:
			strRet = getStringByInt(stockData.HQData.nLowPrice,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_NOW:
			strRet = getStringByInt(stockData.HQData.nLastPrice,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_NOWSHUI:
			if (stockData.fShuiLv <= 0.0)
				stockData.fShuiLv = 1.0f;
			strRet = getStringByInt(
					(int) (stockData.HQData.nLastPrice * stockData.fShuiLv),
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_VOLUME:// 总量
			strRet = getStringByVolume((long) stockData.HQData.volume,
					stockData.HQData.market, stockData.VolUnit,
					Global_Define.VOLUME_STRING_LENGHT, false);
			break;
		case Global_Define.FIELD_HQ_AMOUNT:// 总额
			strRet = getStringByAmount((long) stockData.HQData.amount,
					stockData.HQData.market, 1,
					Global_Define.VOLUME_STRING_LENGHT, false);
			// DataTools::getAmountColor()
			break;
		case Global_Define.FIELD_HQ_CURVOL:// 现量
			strRet = getStringByVolume((long) stockData.HQData.currentVolume,
					stockData.HQData.market, stockData.VolUnit,
					Global_Define.VOLUME_STRING_LENGHT, false);
			break;
		case Global_Define.FIELD_HQ_MARKET:
			strRet = String.valueOf(stockData.HQData.market);
			break;
		case Global_Define.FIELD_HQ_CODE:
			strRet = stockData.HQData.code;
			break;
		case Global_Define.FIELD_HQ_NAME:

		case Global_Define.FIELD_HQ_NAME_ANSI:
			strRet = stockData.name;
			break;
		case Global_Define.FIELD_HQ_LB:// 量比
			// strRet = DataTools::getStringByInt10000(stockData.HQData.lb);
			break;
		case Global_Define.FIELD_HQ_ZD:// 涨跌
			if (stockData.HQData.nLastPriceForCalc == 0) {
				if (stockData.HQData.nLastPrice != 0) {
					stockData.HQData.nLastPriceForCalc = stockData.HQData.nLastPrice;
				} else {
					stockData.HQData.nLastPriceForCalc = stockData.HQData.nLastClear;
				}
			}
			strRet = getStringByInt(
					(stockData.HQData.nLastPriceForCalc - stockData.HQData.nLastClear),
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_AVERAGE:// 均价 放大PriceRate倍
			strRet = getStringByInt(stockData.HQData.nAveragePrice,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_AVERAGESHUI:// 均价含税
			if (stockData.fShuiLv <= 0.0)
				stockData.fShuiLv = 1.0f;
			strRet = getStringByInt(
					(int) (stockData.HQData.nClearPrice * stockData.fShuiLv),
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_ZDF:// 涨跌幅
			if (stockData.HQData.nLastPriceForCalc == 0) {
				if (stockData.HQData.nLastPrice != 0) {
					stockData.HQData.nLastPriceForCalc = stockData.HQData.nLastPrice;
				} else {
					stockData.HQData.nLastPriceForCalc = stockData.HQData.nLastClear;
				}
			}
			strRet = getZDF(
					(stockData.HQData.nLastPriceForCalc - stockData.HQData.nLastClear),
					stockData.HQData.nLastClear,
					stockData.HQData.nLastPriceForCalc, true, true);
			break;
		case Global_Define.FIELD_HQ_ZDF_SIGN:// 带+-符号的涨跌幅
		{
			if (stockData.HQData.nLastPriceForCalc == 0) {
				if (stockData.HQData.nLastPrice != 0) {
					stockData.HQData.nLastPriceForCalc = stockData.HQData.nLastPrice;
				} else {
					stockData.HQData.nLastPriceForCalc = stockData.HQData.nLastClear;
				}
			}
			int nZD = (stockData.HQData.nLastPriceForCalc - stockData.HQData.nLastClear);
			String tempString = getZDF(nZD, stockData.HQData.nLastClear,
					stockData.HQData.nLastPriceForCalc, true, true);
			if (nZD > 0) {
				strRet = "+" + tempString;
			} else {
				strRet = tempString;
			}
		}
			break;
		case Global_Define.FIELD_HQ_ZHENFU:// 振幅
			// strRet = DataTools::getStringByRateEx(stockData.HQData.swing, 0,
			// false, false);
			strRet = "----";
			break;
		case Global_Define.FIELD_HQ_UPPRICE:// 涨停
			strRet = getStringByInt(stockData.HQData.nUpperLimit,
					stockData.PriceDecimal, stockData.PriceRate, false);
			// DataTools::getColorByPrice(1)
			break;
		case Global_Define.FIELD_HQ_DOWNPRICE:// 跌停
			strRet = getStringByInt(stockData.HQData.nLowerLimit,
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_BUYPRICE:// 委买价
			strRet = getStringByInt(stockData.HQData.buyPrice[0],
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_SELLPRICE:// 委卖价
			strRet = getStringByInt(stockData.HQData.sellPrice[0],
					stockData.PriceDecimal, stockData.PriceRate, false);
			break;
		case Global_Define.FIELD_HQ_BVOLUME1:// 买量
			strRet = getStringByVolume(stockData.HQData.buyVolume[0],
					stockData.HQData.market, stockData.VolUnit,
					Global_Define.VOLUME_STRING_LENGHT, false);
			break;
		case Global_Define.FIELD_HQ_SVOLUME1:// 卖量
			strRet = getStringByVolume(stockData.HQData.sellVolume[0],
					stockData.HQData.market, stockData.VolUnit,
					Global_Define.VOLUME_STRING_LENGHT, false);
			break;
		// 期权
		case Global_Define.FIELD_HQ_GGL:// 杠杆率
		{
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionNowPrice = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			float bl = 0;
			if (fOptionNowPrice != 0) {
				bl = fStockPrice / fOptionNowPrice;
			}
			if (bl == 0.0) {
				strRet = Global_Define.STRING_VALUE_EMPTY;
			} else {
				strRet = String.format("%.2f", bl);
			}
		}
			break;
		case Global_Define.FIELD_HQ_ZSGGL:// 真实杠杆率
		{
			// float fGGL =
			// Float.parseFloat(getStringByFieldID(stockData,Global_Define.FIELD_HQ_GGL,
			// stockInfo));
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionNowPrice = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			float fGGL = 0;
			if (fOptionNowPrice != 0) {
				fGGL = fStockPrice / fOptionNowPrice;
			}
			float fDelta = stockData.optionData.Delta;
			strRet = String.format("%.2f", fGGL * fDelta);
		}
			break;
		case Global_Define.FIELD_HQ_NZJZ:// 内在价值
		{
			strRet = "-";
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			float fNZJZ = 0;
			// 0->看涨(认购) 1->看跌(认沽)
			if (stockData.optionData.OptionCP == 0) {
				fNZJZ = fStockPrice - fOptionExecutePrice;
			} else if (stockData.optionData.OptionCP == 1) {
				fNZJZ = fOptionExecutePrice - fStockPrice;
			}
			if (fNZJZ > 0) {
				strRet = String.format("%.4f", fNZJZ);
			}
		}
			break;
		case Global_Define.FIELD_HQ_SJJZ:// 时间价值
		{
			strRet = "0";
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			float fNZJZ = 0;
			// 0->看涨(认购) 1->看跌(认沽)
			if (stockData.optionData.OptionCP == 0) {
				fNZJZ = fStockPrice - fOptionExecutePrice;
			} else if (stockData.optionData.OptionCP == 1) {
				fNZJZ = fOptionExecutePrice - fStockPrice;
			}
			if (fNZJZ < 0) {
				fNZJZ = 0;
			}
			float fSJJZ = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW) - fNZJZ;
			if (fSJJZ > 0) {
				strRet = String.format("%.4f", fSJJZ);
			}
		}
			break;
		case Global_Define.FIELD_HQ_YJL:// 溢价率
		{
			strRet = Global_Define.STRING_VALUE_EMPTY;
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			if (fStockPrice == 0) {
				break;
			}
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			float nowPrice = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			float fYJL = 0;
			// 0->看涨(认购) 1->看跌(认沽)
			if (stockData.optionData.OptionCP == 0) {
				fYJL = (nowPrice + fOptionExecutePrice) / fStockPrice - 1;
			} else if (stockData.optionData.OptionCP == 1) {
				fYJL = 1 - (fOptionExecutePrice - nowPrice) / fStockPrice;
			}
			strRet = String.format("%.2f", fYJL * 100);
		}
			break;
		case Global_Define.FIELD_HQ_CCL:// 持仓量
			strRet = getStringByVolume((long) stockData.HQData.dOpenInterest,
					stockData.HQData.market, stockData.VolUnit,
					Global_Define.VOLUME_STRING_LENGHT, false);
			break;
		case Global_Define.FIELD_HQ_CC:// 仓差
			if (stockData.HQData.dOpenInterest
					- stockData.HQData.dLastOpenInterest == 0) {
				strRet = "0";
			} else {
				strRet = getStringByVolume(
						(long) (stockData.HQData.dOpenInterest - stockData.HQData.dLastOpenInterest),
						stockData.HQData.market, stockData.VolUnit,
						Global_Define.VOLUME_STRING_LENGHT, false);
			}
			break;
		case Global_Define.FIELD_HQ_XQJ:// 行权价
			strRet = String.format("%.4f", stockData.optionData.StrikePrice);
			break;
		case Global_Define.FIELD_HQ_EXPIRE_DATE:// 到期日
			strRet = formatDateFromINT(stockData.optionData.StrikeDate);
			break;
		case Global_Define.FIELD_HQ_SXZ:// 实虚值
		{
			strRet = Global_Define.STRING_VALUE_EMPTY;

			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			float fSXZ = 0;
			// 0->看涨(认购) 1->看跌(认沽)
			if (stockData.optionData.OptionCP == 0) {
				fSXZ = fStockPrice - fOptionExecutePrice;
			} else if (stockData.optionData.OptionCP == 1) {
				fSXZ = fOptionExecutePrice - fStockPrice;
			}
			strRet = String.format("%.2f", fSXZ);
		}
			break;
		case Global_Define.FIELD_HQ_HYD:// 活跃度
			if (stockData.HQData.dLiquidity == 0) {
				strRet = "--";
			} else {
				strRet = String.format("%.2f", stockData.HQData.dLiquidity);
			}
			break;
		case Global_Define.FIELD_HQ_DZCB:// 单张成本
		{
			float nowPrice = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			strRet = String.format("%.2f", nowPrice
					* stockData.optionData.StrikeUnit);
		}
			break;
		case Global_Define.FIELD_HQ_YLGL:// 盈利概率
		{
			strRet = Global_Define.STRING_VALUE_EMPTY;
			if (stockData.optionData.StrikeUnit == 0) {
				break;
			}
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionNowPrice = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			double dYearsDruation = getYearsDruationFromToday(stockData.optionData.StrikeDate);
			float fStockTargetPrice = 0;// 标的目标价
			double dYLGL = 0;
			// 0->看涨(认购) 1->看跌(认沽)
			if (stockData.optionData.OptionCP == 0) {
				fStockTargetPrice = fOptionExecutePrice + fOptionNowPrice;
				dYLGL = FinanceModel.GetPIRProbability(0, fStockPrice,
						FinanceModel.MAX_PRICE_PROBABILITY, fStockTargetPrice,
						stockData.optionData.dHistoryVolatility,
						m_dRateWithoutRisk, dYearsDruation);
				// fYLGL = GetPOLProbability(stockData.optionData.OptionCP, 0,
				// fStockPrice, fStockTargetPrice, fOptionExecutePrice,
				// stockData.optionData.dHistoryVolatility, m_dRateWithoutRisk,
				// dYearsDruation) ;
			} else if (stockData.optionData.OptionCP == 1) {
				fStockTargetPrice = fOptionExecutePrice - fOptionNowPrice;
				dYLGL = FinanceModel.GetPIRProbability(0, fStockPrice,
						fStockTargetPrice, FinanceModel.MIN_PRICE_PROBABILITY,
						stockData.optionData.dHistoryVolatility,
						m_dRateWithoutRisk, dYearsDruation);
				// fYLGL = GetPOLProbability(stockData.optionData.OptionCP, 0,
				// fStockPrice, fStockTargetPrice, fOptionExecutePrice,
				// stockData.optionData.dHistoryVolatility, m_dRateWithoutRisk,
				// dYearsDruation) ;
			}
			strRet = String.format("%.2f", dYLGL * 100);
		}
			break;
		case Global_Define.FIELD_HQ_GSSYB:// 估算损益比
			strRet = Global_Define.STRING_VALUE_EMPTY;
			break;
		case Global_Define.FIELD_HQ_Delta:// Delta
		{
			strRet = String.format("%.4f", stockData.optionData.Delta);
		}
			break;
		case Global_Define.FIELD_HQ_Gamma:// Gamma
		{
			strRet = String.format("%.4f", stockData.optionData.Gamma);
		}
			break;
		case Global_Define.FIELD_HQ_Theta:// Theta
		{
			strRet = String.format("%.4f", stockData.optionData.Theta);
		}
			break;
		case Global_Define.FIELD_HQ_Rho:// Rho
		{
			strRet = String.format("%.4f", stockData.optionData.Rho);
		}
			break;
		case Global_Define.FIELD_HQ_Vega:// Vega
		{
			strRet = String.format("%.4f", stockData.optionData.Vega);
		}
			break;
		case Global_Define.FIELD_HQ_TheoryPrice:// 理论价格
		{
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			double dYearsDruation = getYearsDruationFromToday(stockData.optionData.StrikeDate);
			double dTheoryPrice = FinanceModel.GetOptionBSPrice(
					stockData.optionData.OptionCP, 0, fStockPrice,
					fOptionExecutePrice,
					stockData.optionData.dHistoryVolatility,
					m_dRateWithoutRisk, dYearsDruation);

			strRet = String.format("%.4f", dTheoryPrice);
		}
			break;
		case Global_Define.FIELD_HQ_YHBDL:// 隐含波动率
		{
			float fStockPrice = getFloatPriceByFieldID(stockInfo,
					Global_Define.FIELD_HQ_NOW);
			float fOptionNowPrice = getFloatPriceByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			float fOptionExecutePrice = stockData.optionData.StrikePrice;
			double dYearsDruation = getYearsDruationFromToday(stockData.optionData.StrikeDate);

			double dYHBDL = FinanceModel.GetImpliedVolatility(
					stockData.optionData.OptionCP, 0, fStockPrice,
					fOptionExecutePrice,
					stockData.optionData.dHistoryVolatility,
					m_dRateWithoutRisk, dYearsDruation, fOptionNowPrice);

			strRet = String.format("%.4f", dYHBDL);
		}
			break;
		default:
			break;
		}

		return strRet;
	}

	/**
	 * 根据字段号直接获取颜色
	 * 
	 * @param stockData
	 *            合约信息
	 * @param nField
	 *            字段号
	 * @return 显示用的颜色值
	 */
	public static int getColorByFieldID(TagLocalStockData stockData, int nField) {
		int nColor = Color.BLACK;
		// 如果不变颜色值用
		// int nColor=Color.rgb(124, 124, 125);
		switch (nField) {
		case Global_Define.FIELD_HQ_YESTERDAY:
			nColor = Color.BLACK;
			break;
		case Global_Define.FIELD_HQ_OPEN:
			nColor = getColor(stockData.HQData.nOpenPrice
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_HIGH:
			nColor = getColor(stockData.HQData.nHighPrice
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_LOW:
			nColor = getColor(stockData.HQData.nLowPrice
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_NOW:
			nColor = getColor(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_NOWSHUI:
			if (stockData.fShuiLv <= 0.0)
				stockData.fShuiLv = 1.0f;
			nColor = getColor((int) (stockData.HQData.nLastPriceForCalc * stockData.fShuiLv)
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_AVERAGESHUI:
			if (stockData.fShuiLv <= 0.0)
				stockData.fShuiLv = 1.0f;
			nColor = getColor((int) (stockData.HQData.nClearPrice * stockData.fShuiLv)
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_VOLUME:// 总量
			break;
		case Global_Define.FIELD_HQ_AMOUNT:// 总额
			break;
		case Global_Define.FIELD_HQ_CURVOL:// 现量
			break;
		case Global_Define.FIELD_HQ_MARKET:
			break;
		case Global_Define.FIELD_HQ_CODE:
			break;
		case Global_Define.FIELD_HQ_NAME:
		case Global_Define.FIELD_HQ_NAME_ANSI:
			break;
		case Global_Define.FIELD_HQ_LB:// 量比
			break;
		case Global_Define.FIELD_HQ_HSL:// 换手率
			break;
		case Global_Define.FIELD_HQ_ZD:// 涨跌
			break;
		case Global_Define.FIELD_HQ_AVERAGE:// 均价 放大PriceRate倍
			break;
		case Global_Define.FIELD_HQ_ZDF:// 涨跌幅
			nColor = getColor(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_ZDF_SIGN:// 带+-符号的涨跌幅
			nColor = getColor(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_ZHENFU:// 振幅

			break;
		case Global_Define.FIELD_HQ_UPPRICE:// 涨停
			nColor = getColor(1);
			break;
		case Global_Define.FIELD_HQ_DOWNPRICE:// 跌停
			nColor = getColor(-1);
			break;
		case Global_Define.FIELD_HQ_BUYPRICE:// 委买价
			nColor = getColor(stockData.HQData.buyPrice[0]
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_SELLPRICE:// 委卖价
			nColor = getColor(stockData.HQData.sellPrice[0]
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_BVOLUME1:// 买量
			break;
		case Global_Define.FIELD_HQ_SVOLUME1:// 卖量
			break;
		case Global_Define.FIELD_HQ_GGL:// 杠杆率
			break;
		case Global_Define.FIELD_HQ_ZSGGL:// 真实杠杆率
			break;
		case Global_Define.FIELD_HQ_NZJZ:// 内在价值
			break;
		case Global_Define.FIELD_HQ_SJJZ:// 时间价值
			break;
		case Global_Define.FIELD_HQ_YJL:// 溢价率
			break;
		case Global_Define.FIELD_HQ_CCL:// 持仓量
			break;
		case Global_Define.FIELD_HQ_CC:// 仓差
			break;
		case Global_Define.FIELD_HQ_XQJ:// 行权价
			break;
		case Global_Define.FIELD_HQ_EXPIRE_DATE:// 到期日
			break;
		case Global_Define.FIELD_HQ_SXZ:// 实虚值
			break;
		case Global_Define.FIELD_HQ_HYD:// 活跃度
			break;
		case Global_Define.FIELD_HQ_DZCB:// 单张成本
			break;
		case Global_Define.FIELD_HQ_YLGL:// 盈利概率
			break;
		case Global_Define.FIELD_HQ_GSSYB:// 估算损益比
			break;
		case Global_Define.FIELD_HQ_Delta:// Delta
			break;
		case Global_Define.FIELD_HQ_Gamma:// Gamma

			break;
		case Global_Define.FIELD_HQ_Theta:// Theta

			break;
		case Global_Define.FIELD_HQ_Rho:// Rho
			break;
		case Global_Define.FIELD_HQ_Vega:// Vega

			break;
		case Global_Define.FIELD_HQ_TheoryPrice:// 理论价格

			break;
		case Global_Define.FIELD_HQ_YHBDL:// 隐含波动率

			break;
		default:
			break;
		}

		return nColor;
	}

	/**
	 * 当是平的时候是 白色的
	 * 
	 * @param stockData
	 * @param nField
	 * @return
	 */
	public static int getColorByFieldIDOther(TagLocalStockData stockData,
			int nField) {
		// int nColor = Color.BLACK;

		// int color=Color.rgb(236,237,239);
		int nColor = Color.rgb(236, 237, 239);
		// 如果不变颜色值用
		// int nColor=Color.rgb(124, 124, 125);
		switch (nField) {
		case Global_Define.FIELD_HQ_YESTERDAY:
			nColor = Color.rgb(236, 237, 239);
			break;
		case Global_Define.FIELD_HQ_OPEN:
			nColor = getColorWhite(stockData.HQData.nOpenPrice
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_HIGH:
			nColor = getColorWhite(stockData.HQData.nHighPrice
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_LOW:
			nColor = getColorWhite(stockData.HQData.nLowPrice
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_NOW:
			nColor = getColorWhite(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_VOLUME:// 总量
			break;
		case Global_Define.FIELD_HQ_AMOUNT:// 总额
			break;
		case Global_Define.FIELD_HQ_CURVOL:// 现量
			break;
		case Global_Define.FIELD_HQ_MARKET:
			break;
		case Global_Define.FIELD_HQ_CODE:
			break;
		case Global_Define.FIELD_HQ_NAME:
		case Global_Define.FIELD_HQ_NAME_ANSI:
			break;
		case Global_Define.FIELD_HQ_LB:// 量比
			break;
		case Global_Define.FIELD_HQ_HSL:// 换手率
			break;
		case Global_Define.FIELD_HQ_ZD:// 涨跌
			break;
		case Global_Define.FIELD_HQ_AVERAGE:// 均价 放大PriceRate倍
			break;
		case Global_Define.FIELD_HQ_ZDF:// 涨跌幅
			nColor = getColorWhite(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_ZDF_SIGN:// 带+-符号的涨跌幅
			nColor = getColorWhite(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_ZHENFU:// 振幅
			break;
		case Global_Define.FIELD_HQ_UPPRICE:// 涨停
			nColor = getColor(1);
			break;
		case Global_Define.FIELD_HQ_DOWNPRICE:// 跌停
			nColor = getColor(-1);
			break;
		case Global_Define.FIELD_HQ_BUYPRICE:// 委买价
			nColor = getColorWhite(stockData.HQData.buyPrice[0]
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_SELLPRICE:// 委卖价
			nColor = getColorWhite(stockData.HQData.sellPrice[0]
					- stockData.HQData.nLastClear);
			break;
		case Global_Define.FIELD_HQ_BVOLUME1:// 买量
			break;
		case Global_Define.FIELD_HQ_SVOLUME1:// 卖量
			break;
		case Global_Define.FIELD_HQ_GGL:// 杠杆率
			break;
		case Global_Define.FIELD_HQ_ZSGGL:// 真实杠杆率
			break;
		case Global_Define.FIELD_HQ_NZJZ:// 内在价值
			break;
		case Global_Define.FIELD_HQ_SJJZ:// 时间价值
			break;
		case Global_Define.FIELD_HQ_YJL:// 溢价率
			break;
		case Global_Define.FIELD_HQ_CCL:// 持仓量
			break;
		case Global_Define.FIELD_HQ_CC:// 仓差
			break;
		case Global_Define.FIELD_HQ_XQJ:// 行权价
			break;
		case Global_Define.FIELD_HQ_EXPIRE_DATE:// 到期日
			break;
		case Global_Define.FIELD_HQ_SXZ:// 实虚值
			break;
		case Global_Define.FIELD_HQ_HYD:// 活跃度
			break;
		case Global_Define.FIELD_HQ_DZCB:// 单张成本
			break;
		case Global_Define.FIELD_HQ_YLGL:// 盈利概率
			break;
		case Global_Define.FIELD_HQ_GSSYB:// 估算损益比
			break;
		case Global_Define.FIELD_HQ_Delta:// Delta
			break;
		case Global_Define.FIELD_HQ_Gamma:// Gamma

			break;
		case Global_Define.FIELD_HQ_Theta:// Theta

			break;
		case Global_Define.FIELD_HQ_Rho:// Rho
			break;
		case Global_Define.FIELD_HQ_Vega:// Vega

			break;
		case Global_Define.FIELD_HQ_TheoryPrice:// 理论价格

			break;
		case Global_Define.FIELD_HQ_YHBDL:// 隐含波动率

			break;
		default:
			break;
		}

		return nColor;
	}

	/**
	 * 获取盈亏分析列表
	 * 
	 * @return 返回计算之后的列表数据
	 * @param bdyqbl
	 *            波动预期比例（平缓=0.7,正常=1.0 ,剧烈=1.5）
	 * @param stockData
	 *            合约信息
	 * @param stockInfo
	 *            合约对应的标的信息，某些字段计算的时候需要
	 * @param pjsy
	 *            用来保存计算的平均收益，收益率以及概率
	 */
	public static ArrayList<TagProfitRecord> getProfitRecordList(float bdyqbl,
			TagLocalStockData stockData, TagLocalStockData stockInfo,
			TagProfitRecord pjsy) {
		ArrayList<TagProfitRecord> aArray = new ArrayList<TagProfitRecord>();
		for (int i = 0; i < TagProfitRecord.ProfitAnalyse_TableRowCount; i++) {
			TagProfitRecord aTempProfitRecord = new TagProfitRecord();
			aArray.add(aTempProfitRecord);
		}

		if (stockData == null || stockInfo == null) {
			return aArray;
		}

		float fStockPrice = getFloatPriceByFieldID(stockInfo,
				Global_Define.FIELD_HQ_NOW);
		float fOptionNowPrice = getFloatPriceByFieldID(stockData,
				Global_Define.FIELD_HQ_NOW);
		float fOptionExecutePrice = stockData.optionData.StrikePrice;
		double dYearsDruation = getYearsDruationFromToday(stockData.optionData.StrikeDate);

		// 用临时列表保存，最后首尾两个的去掉
		TagProfitRecord[] tempProfitRecord = new TagProfitRecord[TagProfitRecord.ProfitAnalyse_TableRowCount + 2];
		for (int i = 0; i < TagProfitRecord.ProfitAnalyse_TableRowCount + 2; i++) {
			TagProfitRecord aTempProfitRecord = new TagProfitRecord();
			tempProfitRecord[i] = aTempProfitRecord;
		}
		int nMiddleIndex = (TagProfitRecord.ProfitAnalyse_TableRowCount + 2) / 2;
		tempProfitRecord[nMiddleIndex].price = fStockPrice;

		for (int i = 1; i <= nMiddleIndex; i++) {

			tempProfitRecord[nMiddleIndex - i].price = (1 - TagProfitRecord.ProfitAnalyse_TablePriceRate
					* i)
					* fStockPrice;
			tempProfitRecord[nMiddleIndex + i].price = (1.0f / (1 - TagProfitRecord.ProfitAnalyse_TablePriceRate
					* i))
					* fStockPrice;
		}

		pjsy.rate = 0;
		pjsy.sy = 0;
		pjsy.syl = 0f;
		double total = 0.0;

		for (int i = 1; i < TagProfitRecord.ProfitAnalyse_TableRowCount; i++) {

			tempProfitRecord[i].rate = (float) FinanceModel.GetPIRProbability(
					0, fStockPrice, tempProfitRecord[i + 1].price,
					tempProfitRecord[i - 1].price,
					stockData.optionData.dHistoryVolatility * bdyqbl,
					m_dRateWithoutRisk, dYearsDruation);
			pjsy.rate += tempProfitRecord[i].rate;
		}
		// pjsy.rate /= TagProfitRecord.ProfitAnalyse_TableRowCount;

		for (int i = 0; i < TagProfitRecord.ProfitAnalyse_TableRowCount; i++) {
			aArray.get(i).price = tempProfitRecord[i + 1].price;
			aArray.get(i).rate = tempProfitRecord[i + 1].rate;
			float fTempProfit = 0;
			// 0->看涨(认购) 1->看跌(认沽)
			if (stockData.optionData.OptionCP == 0) {

				fTempProfit = aArray.get(i).price
						- (fOptionExecutePrice + fOptionNowPrice);
			} else if (stockData.optionData.OptionCP == 1) {
				fTempProfit = (fOptionExecutePrice - fOptionNowPrice)
						- aArray.get(i).price;
			}

			if (fTempProfit < 0 && Math.abs(fTempProfit) > fOptionNowPrice) {
				// 最大亏损为保证金
				fTempProfit = -1 * fOptionNowPrice;
			}
			aArray.get(i).syl = (fTempProfit) / fOptionNowPrice;
			aArray.get(i).sy = fTempProfit * stockData.optionData.StrikeUnit;

			total += aArray.get(i).sy * aArray.get(i).rate;

			// pjsy.sy += aArray.get(i).sy;
			pjsy.syl += aArray.get(i).syl * aArray.get(i).rate;
		}

		if (pjsy.rate > 0.000001) {
			pjsy.sy = total / pjsy.rate;
			pjsy.syl = pjsy.syl / pjsy.rate;
		} else {
			pjsy.sy = 0;
			pjsy.syl = 0;
		}
		pjsy.rate /= TagProfitRecord.ProfitAnalyse_TableRowCount;
		// pjsy.sy /= TagProfitRecord.ProfitAnalyse_TableRowCount;
		// pjsy.syl /= TagProfitRecord.ProfitAnalyse_TableRowCount;

		return aArray;
	}

	public static boolean getHandSetInfoSpecial() {
		String handSetInfo = android.os.Build.MANUFACTURER;
		if (handSetInfo != null && handSetInfo.equals("Xiaomi")) {
			return true;
		} else {
			return false;
		}
	}

	public static HashSet<String> getActivities(Context context) {
		HashSet<String> returnClassList = new HashSet<String>();

		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (packageInfo.activities != null) {
			for (ActivityInfo ai : packageInfo.activities) {
				returnClassList.add(ai.name);
				// Maybe isAssignableFrom is unnecessary
				/*
				 * if(Activity.class.isAssignableFrom(c)){
				 * returnClassList.add(c); }
				 */
			}
		}
		return returnClassList;
	}
}
