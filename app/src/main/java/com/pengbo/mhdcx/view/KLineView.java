package com.pengbo.mhdcx.view;

import java.util.ArrayList;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalKLineData;
import com.pengbo.mhdzq.data.TagLocalKLineData.TagKAverageInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.AnalyseFunc;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * KLine
 * 
 * @author pobo
 * 
 */
public class KLineView extends FrameLayout {
	public static final String TAG = KLineView.class.getSimpleName();
	//
	private static final int DEFAULT_KLINE_WIDTH = 15; // kline width as default
	private static final int	MAX_KLINE_WIDTH = 25;		//最大K线宽度
	
	private Context mContext;
	private TagLocalStockData mOptionData;
	private MyApp mMyApp;
	
	private View mPopInfo;
	public boolean mPopinfoFlag;
	private int mPopupViewWidth = 0;
	private final static int POSITION_LEFT	=	1;
	private final static int POSITION_RIGHT	=	2;
	private int flag_position_showing = 0; //popinfo显示在右边还是左边
	
	private	int	m_iIndex; //当前点中的KLine索引
	private KLine mKLine;
	private DisplayMetrics mScreenSize;
	private boolean mbOptionOrStock = true; // true-option;false-stock

	private ArrayList<TagLocalKLineData> mKLineDataArray;

	public KLineView(Context context, boolean bOption) {
		super(context);

		mContext = context;
		mbOptionOrStock = bOption;
		initData(context);
		initView(context);
	}

	private void initData(Context context) {
		mMyApp = (MyApp) context.getApplicationContext();
		mScreenSize = ViewTools.getScreenSize(context);
		if (mbOptionOrStock)
		{
			mKLineDataArray = mMyApp.getKLineDataArray();
		}else
		{
			mKLineDataArray = mMyApp.getStockKLineDataArray();
		}
		
		mPopupViewWidth = (int) context.getResources().getDimension(R.dimen.popupinfo_width);
	}

	private void initView(Context context) {
		FrameLayout frame = new FrameLayout(context);

		LinearLayout layouthorn = new LinearLayout(context);
		layouthorn.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams lp1 = new LayoutParams(mScreenSize.widthPixels,
				mScreenSize.heightPixels/3);//mScreenSize.heightPixels/3
		mKLine = new KLine(context);
		layouthorn.addView(mKLine, lp1);

		frame.addView(layouthorn);
		addView(frame);
	}

	public void updateData(TagLocalStockData optionData) {
		this.mOptionData = optionData;
	}
	
	public void resetKLineParam() {
		L.i("KLineView", "resetKLineParam");
		mKLine.resetParam();
		mKLine.dataInit();
		DismissInfo();
	}
	
	//
    public void onTouchLine(MotionEvent event) {
    	mKLine.onTouchLine(event);
    }
    //
    public void onMoveLine(MotionEvent event) {
    	mKLine.onMoveLine(event);
    }
    //
    public void onScrollLine(float y, float dx, float dy) {
    	mKLine.onScrollLine(y, dx, dy);
    }
    //
    public void onScaleLine(float dy) {
    	mKLine.onScaleLine(dy);
    }

	// update view
	public void updateAllData() {
		if (mKLine != null)
			mKLine.updateAllData();
		
		if(mPopinfoFlag == true) {
			PopupInfo();
		}
	}
	
	public void PopupInfo() {
    	
        if(m_iIndex < 0 || m_iIndex >= mKLineDataArray.size()) {
	    	L.d(TAG, "PopupInfo--->DismissInfo");
        	DismissInfo();
        	return;
        }

    	if(mPopInfo == null) {
    		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
    		mPopInfo = layoutInflater.inflate(R.layout.pop_klineinfo, null); 
    		
    		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, Gravity.LEFT);
			int top = mKLine.getLineTop();
			
			//判断popupinfo弹出位置
	    	int halfwidth = mKLine.getClientHalfWidth();   

	    	if (flag_position_showing == POSITION_RIGHT)
	    	{
	    		lp.setMargins(halfwidth*2 - mPopupViewWidth,//flag_detail_showing ? halfwidth*2 - mPopupViewWidth : halfwidth*3 - mPopupViewWidth, 
	    				top, 
	    				0, 
	    				0);
	    	}
	    	else
	    	{
	    		lp.setMargins(0, top, 0, 0);
	    	}	
			addView(mPopInfo, lp);
    	}
    	
    	mPopinfoFlag = true;
    	
        TagLocalKLineData KData	= mKLineDataArray.get(m_iIndex);
		if(KData == null)
			return;
		
		TagLocalKLineData prevData;
		if(m_iIndex > 0) {
			prevData = mKLineDataArray.get(m_iIndex-1);
		} else {
			prevData = KData; 
		}
		//开盘
		String value = ViewTools.getStringByPrice(KData.open, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
		TextView fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field0);
		fieldView.setTextColor(ViewTools.getColor(KData.open, prevData.close));
		fieldView.setText(value);
		//最高
		value = ViewTools.getStringByPrice(KData.high, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
		fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field1);
		fieldView.setTextColor(ViewTools.getColor(KData.high, prevData.close));
		fieldView.setText(value);
		//最低
		value = ViewTools.getStringByPrice(KData.low, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
		fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field2);
		fieldView.setTextColor(ViewTools.getColor(KData.low, prevData.close));
		fieldView.setText(value);
		//收盘
		value = ViewTools.getStringByPrice(KData.close,  mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
		fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field3);
		fieldView.setTextColor(ViewTools.getColor(KData.close, prevData.close));
		fieldView.setText(value);
		//幅度
		value = ViewTools.getZDF(KData.close-prevData.close, prevData.close, 1, true, true);
		fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field4);
		fieldView.setTextColor(ViewTools.getColor(KData.close, prevData.close));
		fieldView.setText(value);
		
		//成交量
		L.d(TAG, "PopupInfo--->"+ KData.volume + ", " + mOptionData.VolUnit);
		value = ViewTools.getStringByVolume(KData.volume, mOptionData.market, 1, 6, false);
		fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field5);
		fieldView.setTextColor(value.startsWith("--")?Color.WHITE:ColorConstant.COLOR_YELLOW);
		fieldView.setText(value);
		
		//成交额
		value = ViewTools.getStringByVolume(KData.amount, mOptionData.market, 1, 6, false);
		fieldView = (TextView)mPopInfo.findViewById(R.id.kline_popinfo_field6);
		fieldView.setTextColor(value.startsWith("--")?Color.WHITE:ColorConstant.COLOR_YELLOW);
		fieldView.setText(value);
		
		////////////////////////////
		View view_ccl = mPopInfo.findViewById(R.id.layout_gzqh_ccl);
		View view_jsj = mPopInfo.findViewById(R.id.layout_gzqh_jsj);
		View view_hsl = mPopInfo.findViewById(R.id.layout_hsl);
		
		view_ccl.setVisibility(View.GONE);
		view_jsj.setVisibility(View.GONE);
		view_hsl.setVisibility(View.GONE);
		
    }
    
    //
    public void DismissInfo() {
    	
    	if(mPopInfo == null) {
    		return;
    	}
    	
    	removeView(mPopInfo);
    	mPopInfo = null;
    	mPopinfoFlag = false;
    }

	public class KLine extends View {

		Rect mClientRect; // display area
		Paint mPaint;
		Paint linePaint;

		//private int mFontH_M = 0; // 报价栏字体
		private int mFontH = 0;
		private float mFontSize = 10; // 坐标字体大小

		private int mLeft = 0;
		private int mRight = 0;

		private int mStockPanelY = 0; // 股票报价栏Y坐标

		private int mLineLeft = 0;
		private int mLineRight = 0;

		private int mKlineTopY = 0; // Y轴上坐标
		private int mKlineBottomY = 0; // Y轴下坐标
		private int mTechTopY = 0;
		private int mTechBottomY = 0;

		private double mLineSpace = 0.0; // Y轴间距

		private int m_iItemWidth; // the width of each kline, min=1
		private int m_iSeparate; // separate width between two kline

		private int m_iStart; // the position of the first kline from left，
								// －1 = display from the last
		private int m_iScreenNum; // kline numbers can be displayed in one page
		private int m_iShowNum; // current numbers in the page

		private int mKLineNum = 0; // total numbers of kline
		private int mMaxPrice;
		private int mMinPrice;
		
		private TagKAverageInfo mKAverage; //now just draw one k average line

		private boolean m_bScrolling = false; //滑动K线
		
		public KLine(Context context) {
			super(context);

			mClientRect = new Rect();
			mPaint = new Paint();
			linePaint = new Paint();
			mPaint.setAntiAlias(true);

			mFontSize = getResources().getDimension(R.dimen.font_screen_F);
			//mFontH_M = ViewTools.getFontHeight(ViewTools.TEXTSIZE_M);
			mFontH = ViewTools.getFontHeight(mFontSize);

			m_iStart = 0;// the position of the first kline from left，
			               // －1 = display from the last
			m_iSeparate = 2; // separate of kline
			m_iItemWidth = DEFAULT_KLINE_WIDTH; // width of kline
			
			mKAverage = new TagKAverageInfo();
			mKAverage.color = ColorConstant.COLOR_LINE_MA5;
			mKAverage.para = 5;
		}

		public int getLineTop() {
			return mKlineTopY - mFontH/2;
		}
		
		public int getClientHalfWidth()
		{
			//判断popupinfo弹出位置
	    	int halfwidth = (mClientRect.right - mClientRect.left)/2;
	    	return halfwidth;
		}
		
		// init some parameters for kline draw
		private void drawInit() {
			mLeft = mClientRect.left;
			mRight = mClientRect.right;

			mStockPanelY = 0;

			mLineLeft = mLeft + 15;
			mLineRight = mClientRect.right - 15;

			mKlineTopY = mStockPanelY + mFontH / 2;

			mTechBottomY = mClientRect.bottom - 10;
			mLineSpace = (double) (mTechBottomY - mKlineTopY - mFontH) / 6.0;
			mTechTopY = (int) (mTechBottomY - mLineSpace * 2);
			mKlineBottomY = mTechTopY - mFontH;
		}
		
		public void resetParam() {
			m_iStart 	= 0;
			m_iItemWidth = DEFAULT_KLINE_WIDTH;
			
			m_iIndex	= 0;
		}

		private void dataInit() {

			L.i(TAG, "dataInit");

			mKLineNum = mKLineDataArray.size();
			
			//定位当前为最新
	    	if(mPopinfoFlag == false && mKLineNum > 0) {
	    		m_iIndex = mKLineNum - 1;
	    		L.d("KLineView", "dataInit--->m_iIndex = " + m_iIndex + ", mKLineNum = " + mKLineNum);
	    	}

			STD.memset(mKAverage.data);

			if (mKLineNum >= mKAverage.para) {

				for (int i = 0; i < mKLineNum; i++) {
					mKAverage.data[i] = mKLineDataArray.get(i).close;
				}

				AnalyseFunc.MA(mKAverage.data, mKLineNum, mKAverage.para);
				for (int n = 0; n < mKAverage.para - 1; n++) {
					mKAverage.data[n] = 0;
				}
			}

			mMaxPrice = 0;
			mMinPrice = 0;

			getShowNum();
		}

		// update view
		public void updateAllData() {
			// init some parameters for draw trend line
			dataInit();
			this.invalidate();
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			//
			super.onLayout(changed, left, top, right, bottom);

			if (changed) {
				mClientRect.set(left, top, right, bottom);
				L.d(TAG, "onLayout--->top = " + mClientRect.top + ", bottom = "
						+ mClientRect.bottom + ", left = " + mClientRect.left
						+ ", right = " + mClientRect.right);
				drawInit();
				dataInit();
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			drawNow(canvas);
		}

		private void drawNow(Canvas canvas) {
			updateKLine(canvas);
		}

		public void updateKLine(Canvas canvas) {

			drawBackground(canvas);
			drawKLine(canvas);
			drawVolume(canvas);
			drawRule(canvas);
		}

		// draw background
		protected void drawBackground(Canvas canvas) {
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setColor(ColorConstant.COLOR_TREND_ZUOBIAO);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(1);

			//kline coordinate line X
			canvas.drawLine(mLineLeft, mKlineTopY, mLineRight,
					mKlineTopY, linePaint);
			
			canvas.drawLine(mLineLeft, (int)(mKlineTopY + mLineSpace), mLineRight,
					(int)(mKlineTopY + mLineSpace), linePaint);
			canvas.drawLine(mLineLeft, (int)(mKlineTopY + mLineSpace*2), mLineRight,
					(int)(mKlineTopY + mLineSpace*2), linePaint);
			canvas.drawLine(mLineLeft, (int)(mKlineTopY + mLineSpace*3), mLineRight,
					(int)(mKlineTopY + mLineSpace*3), linePaint);
			//canvas.drawLine(mLineLeft, (int)(mKlineTopY + mLineSpace*4), mLineRight,
			//		(int)(mKlineTopY + mLineSpace*4), linePaint);
			//canvas.drawLine(mLineLeft, (int)(mKlineTopY + mLineSpace*5), mLineRight,
			//		(int)(mKlineTopY + mLineSpace*5), linePaint);
			
			canvas.drawLine(mLineLeft, mKlineBottomY, mLineRight,
					mKlineBottomY, linePaint);
			
			
			//kline coordinate line Y
			canvas.drawLine(mLineLeft, mKlineTopY, mLineLeft, mKlineBottomY,
					linePaint);
			canvas.drawLine(mLineRight, mKlineTopY, mLineRight, mKlineBottomY,
					linePaint);
			
			
			//tech coordinate line X
			canvas.drawLine(mLineLeft, mTechTopY, mLineRight, mTechTopY,
					linePaint);
			//canvas.drawLine(mLineLeft, (int)(mTechTopY + mLineSpace), mLineRight, (int)(mTechTopY + mLineSpace),
			//		linePaint);
			canvas.drawLine(mLineLeft, mTechBottomY, mLineRight, mTechBottomY,
					linePaint);
			
			//tech coordinate line Y
			canvas.drawLine(mLineLeft, mTechTopY, mLineLeft, mTechBottomY,
					linePaint);
			canvas.drawLine(mLineRight, mTechTopY, mLineRight, mTechBottomY,
					linePaint);
		}

		// KLine
		protected void drawKLine(Canvas canvas) {
			if (m_iStart >= 0 && m_iStart < mKLineNum) {
				// calc max&min
				mMaxPrice = mKLineDataArray.get(m_iStart).high;
				mMinPrice = mKLineDataArray.get(m_iStart).low;
				for (int i = 0; i < m_iShowNum  && (m_iStart + i) < mKLineDataArray.size(); i++) {
					mMaxPrice = (mMaxPrice < mKLineDataArray.get(m_iStart + i).high) ? mKLineDataArray
							.get(m_iStart + i).high : mMaxPrice;
					mMinPrice = (mMinPrice > mKLineDataArray.get(m_iStart + i).low) ? mKLineDataArray
							.get(m_iStart + i).low : mMinPrice;

					int value = mKAverage.data[m_iStart + i];
					if (value == 0) {
						continue;
					}
					mMaxPrice = (mMaxPrice < value) ? value : mMaxPrice;
					mMinPrice = (mMinPrice > value) ? value : mMinPrice;
				}
			}
			if (mMaxPrice <= mMinPrice) {
				mMaxPrice = mMinPrice + 6000;
			}

			if (mKLineNum == 0)
				return;

			//draw high and low
			mPaint.setColor(Color.LTGRAY);
        	mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        	mPaint.setTextSize(mFontSize);
        	mPaint.setTextAlign(Paint.Align.LEFT);
        	ViewTools.DrawPrice(canvas, mLineLeft + 2, mKlineTopY,
					mMaxPrice, 1,
					mMaxPrice,
					mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
        	ViewTools.DrawPrice(canvas, mLineLeft + 2, mKlineBottomY - mFontH,
					mMinPrice, 1,
					mMinPrice,
					mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);

			
			
			linePaint.setAntiAlias(false);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.FILL);
			double divscale = (double) (mKlineBottomY - mKlineTopY)
					/ (mMaxPrice - mMinPrice);
			int x = mLineLeft + 1;
			int mid = x;
			int oldColor = ColorConstant.COLOR_ALL_RED;
			for (int j = 0; j < m_iShowNum && (m_iStart + j) < mKLineDataArray.size(); j++, x += (m_iItemWidth + m_iSeparate))
			{
				TagLocalKLineData data = mKLineDataArray.get(m_iStart + j);
				int y_open = mKlineBottomY
						- (int) ((data.open - mMinPrice) * divscale + 0.5);
				int y_high = mKlineBottomY
						- (int) ((data.high - mMinPrice) * divscale + 0.5);
				int y_low = mKlineBottomY
						- (int) ((data.low - mMinPrice) * divscale + 0.5);
				int y_close = mKlineBottomY
						- (int) ((data.close - mMinPrice) * divscale + 0.5);

				mid = x + (m_iItemWidth - 1) / 2;

				if (data.close > data.open) {
					// 阳线
					linePaint.setColor(ColorConstant.COLOR_ALL_RED);
					oldColor = ColorConstant.COLOR_ALL_RED;

					Rect rect = new Rect();
					if (y_open == y_close)
						rect.set(x, y_close - 1, x + m_iItemWidth, y_open);
					else
						rect.set(x, y_close, x + m_iItemWidth, y_open);
					canvas.drawRect(rect, linePaint);

					canvas.drawLine(mid, y_close, mid, y_high, linePaint);
					canvas.drawLine(mid, y_open, mid, y_low, linePaint);
				} else if (data.close < data.open) {
					// 阴线
					linePaint.setColor(ColorConstant.COLOR_ALL_GREEN);
					oldColor = ColorConstant.COLOR_ALL_GREEN;

					Rect rect = new Rect();
					if (y_open == y_close)
						rect.set(x, y_open - 1, x + m_iItemWidth, y_close);
					else
						rect.set(x, y_open, x + m_iItemWidth, y_close);
					canvas.drawRect(rect, linePaint);

					canvas.drawLine(mid, y_close, mid, y_low, linePaint);
					canvas.drawLine(mid, y_open, mid, y_high, linePaint);

				} else {
					// 十字星线
					int index = m_iStart + j;
					if (index > 0) {
						if (mKLineDataArray.get(index).close > mKLineDataArray
								.get(index - 1).close) {
							linePaint.setColor(ColorConstant.COLOR_ALL_RED);
							oldColor = ColorConstant.COLOR_ALL_RED;
						} else if (mKLineDataArray.get(index).close < mKLineDataArray
								.get(index - 1).close) {
							linePaint.setColor(ColorConstant.COLOR_ALL_GREEN);
							oldColor = ColorConstant.COLOR_ALL_GREEN;
						} else {
							linePaint.setColor(oldColor);
						}
					} else {
						linePaint.setColor(ColorConstant.COLOR_ALL_RED);
						oldColor = ColorConstant.COLOR_ALL_RED;
					}

					canvas.drawLine(x, y_open, x + m_iItemWidth, y_open,
							linePaint);
					canvas.drawLine(mid, y_high, mid, y_low, linePaint);
				}
			}
			// 均线
			linePaint.setAntiAlias(true);
			linePaint.setStyle(Paint.Style.STROKE);
			PathEffect effect = new CornerPathEffect(3);
			linePaint.setPathEffect(effect);
			// 均线显示起始位置
			int start = ((mKAverage.para - 1 - m_iStart) > 0) ? (mKAverage.para - 1 - m_iStart)
					: 0;
			int index = start + m_iStart; // 对应K线起始位置的索引

			int avgX = mLineLeft + 1 + start * (m_iItemWidth + m_iSeparate)
					+ m_iItemWidth / 2;
			int avgY = mKlineBottomY
					- (int) ((mKAverage.data[index] - mMinPrice) * divscale + 0.5);

			Path path = new Path();
			path.moveTo(avgX, avgY);

			for (int j = 1; j < m_iShowNum - start; j++) {

				avgX += m_iItemWidth + m_iSeparate;
				avgY = mKlineBottomY
						- (int) ((mKAverage.data[index + j] - mMinPrice)
								* divscale + 0.5);
				path.lineTo(avgX, avgY);
			}

			linePaint.setColor(mKAverage.color);
			canvas.drawPath(path, linePaint);
			
			
			

			//画左下角的时间 
			// 时间
			
			String  time_left = "----";
			
			time_left = STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart).date);		
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float fontSize = getResources().getDimension(R.dimen.font_screen_F);
			mPaint.setTextSize(fontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			
			int width = (int) mPaint.measureText(time_left);
			ViewTools.DrawText(canvas, time_left, mLineLeft+2, mLineLeft+2+width, mKlineBottomY, mKlineBottomY+mFontH, mPaint);

			//画右下角的时间 
			String time_right = "----";

			if ((m_iStart + m_iShowNum-1 ) >= 0 && (m_iStart + m_iShowNum-1) < mKLineDataArray.size()) {
				time_right = STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart + m_iShowNum - 1).date);
			}

			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float font_size = getResources()
					.getDimension(R.dimen.font_screen_F);
			mPaint.setTextSize(font_size);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);

			int width_font = (int) mPaint.measureText(time_right);
			
			ViewTools.DrawText(canvas, time_right, mLineRight-2-width_font, mLineRight-2, mKlineBottomY, mKlineBottomY+mFontH, mPaint);
		}
		
		//显示标尺
		protected void drawRule(Canvas canvas) {
			if (mKLineDataArray.size() <= 0)
				return;
			
	        if(mPopinfoFlag == true) {
	        	//
	        	int indexX = mLineLeft + 2 + (m_iIndex-m_iStart) * (m_iItemWidth + m_iSeparate) + m_iItemWidth/2;
	        	//
	        	linePaint.setAntiAlias(true);
	        	linePaint.setStyle(Paint.Style.STROKE);
	        	linePaint.setColor(Color.GRAY);
	        	PathEffect effect = new DashPathEffect(new float[] {3, 2}, 0);
	        	linePaint.setPathEffect(effect);
	        	Path path = new Path();
	        	path.moveTo(indexX, mKlineTopY);
	        	path.lineTo(indexX, mKlineBottomY);
	        	canvas.drawPath(path, linePaint);
	        	path.moveTo(indexX, mTechTopY);
	        	path.lineTo(indexX, mTechBottomY);
	        	canvas.drawPath(path, linePaint);
//	        	canvas.drawLine(indexX, mKlineTopY, indexX, mKlineBottomY, mPaint);

	    		//时间显示
	        	mPaint.setColor(ColorConstant.COLOR_TIME);
	        	mPaint.setAlpha(228);
	        	mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        	mPaint.setTextSize(mFontSize);
	        	mPaint.setTextAlign(Paint.Align.CENTER);
	        	
	        	int width = (int)mPaint.measureText("20100101")+10;
	        	RectF r = new RectF();
	        	if(indexX+width/2 < mRight)
	        		r.set(indexX-width/2, mKlineBottomY+1, indexX+width/2, mTechTopY-1);
	        	else
	        		r.set(mRight-1-width, mKlineBottomY+1, mRight-1, mTechTopY-1);
	        	canvas.drawRoundRect(r, 5, 5, mPaint);
	        	
	        	mPaint.setColor(Color.WHITE);
	        	mPaint.setTextSize(mFontSize);
	        	String time = STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iIndex).date);
	        	ViewTools.DrawText(canvas, time, (int)r.left, (int)r.right, (int)r.top, (int)r.bottom, mPaint);
	        }
		}

		protected void drawVolume(Canvas canvas) {
			// calc max value
			long max = 0;
			if (mKLineNum > 0) {
				max = mKLineDataArray.get(m_iStart).volume;
				for (int i = 0; i < m_iShowNum; i++) {

					long value = mKLineDataArray.get(m_iStart + i).volume;
					max = (max < value) ? value : max;
				}
			}

			//
			if (mKLineNum == 0)
				return;

			// calc y scale
			double YScales = 0.0;
			if (max > 0) {
				YScales = (double) (mTechBottomY - mTechTopY - 2) / max;
			}
			mPaint.setStyle(Paint.Style.FILL);
			int x = mLineLeft + 1;
			int oldColor = ColorConstant.COLOR_ALL_RED;
			for (int j = 0; j < m_iShowNum; j++, x += (m_iItemWidth + m_iSeparate)) {

				TagLocalKLineData data = mKLineDataArray.get(m_iStart + j);
				int y = 0;
				long value = 0;
				value = data.volume;

				if (value == 0)
					continue;
				y = mTechBottomY - 1 - (int) (value * YScales + 0.5);

				if (data.close > data.open) {
					mPaint.setColor(ColorConstant.COLOR_ALL_RED);
					oldColor = ColorConstant.COLOR_ALL_RED;
				} else if (data.close < data.open) {
					mPaint.setColor(ColorConstant.COLOR_ALL_GREEN);
					oldColor = ColorConstant.COLOR_ALL_GREEN;
				} else {

					int index = m_iStart + j;
					if (index > 0) {
						if (mKLineDataArray.get(index).close > mKLineDataArray
								.get(index - 1).close) {
							mPaint.setColor(ColorConstant.COLOR_ALL_RED);
							oldColor = ColorConstant.COLOR_ALL_RED;
						} else if (mKLineDataArray.get(index).close < mKLineDataArray
								.get(index - 1).close) {
							mPaint.setColor(ColorConstant.COLOR_ALL_GREEN);
							oldColor = ColorConstant.COLOR_ALL_GREEN;
						} else {
							mPaint.setColor(oldColor);
						}
					} else {
						mPaint.setColor(ColorConstant.COLOR_ALL_RED);
						oldColor = ColorConstant.COLOR_ALL_RED;
					}
				}

				Rect rect = new Rect();
				rect.set(x, y, x + m_iItemWidth, mTechBottomY);
				canvas.drawRect(rect, mPaint);
			}
		}

		// calc the display numbers
		private void getShowNum() {
			if (mKLineNum <= 0)
				return;

			m_iScreenNum = (mLineRight - mLineLeft - 2)
					/ (m_iItemWidth + m_iSeparate);
			
			if (m_iScreenNum <= 0)
				return;
			
			//if (m_iStart == -1) {
				m_iStart = mKLineNum - m_iScreenNum;
			//}

			if (m_iStart < 0) {
				m_iStart = 0;
			}

			m_iShowNum = mKLineNum - m_iStart;
			if (m_iShowNum > m_iScreenNum) {
				m_iShowNum = m_iScreenNum;
			}
		}

		private int getCurIndexByX(int x) {
			
			int index = (x-mLineLeft-1)/(m_iItemWidth+m_iSeparate)+m_iStart;
			
			if (index < m_iStart ) {
				index	= m_iStart;
			}
			else if (index >= m_iStart+m_iShowNum) {
				index	= m_iStart + m_iShowNum - 1;
			}
			
			return index;
		}
		
		//
	    public void onTouchLine(MotionEvent event)
	    {
	    	L.d(TAG, "------------------onTouchLine------------------" + event.getAction());
	    	int x = (int)event.getX();
	    	int y = (int)event.getY();

			//
			if( event.getAction() == MotionEvent.ACTION_UP ) {
				
				//
				if (m_bScrolling)
				{
					m_bScrolling = false;
					return;
				}
				
				//判断popupinfo弹出位置
		    	int halfwidth = (mClientRect.right - mClientRect.left)/2;
		    	int oldflat = flag_position_showing;
		    	if (x <= halfwidth)
		    	{
		    		flag_position_showing = POSITION_RIGHT;
		    	}
		    	else
		    	{
		    		flag_position_showing = POSITION_LEFT;
		    	}
		    	
		    	if( (x > mLineLeft) && (x < mLineRight)
		    			&& (y > mKlineTopY) && (y < mKlineBottomY) ) 
		    	{
		    		
		    		m_iIndex = getCurIndexByX(x);

		    		if (oldflat != flag_position_showing)
		    		{
		    			DismissInfo();
		    		}
		    		
		    		PopupInfo();
		        	invalidate();
		    	}
		    	else {
		    		DismissInfo();
		    		invalidate();
		    	}
			}
	    }
	    
	    //
		private int getXByIndex(int index) {
			return mLineLeft + m_iSeparate + (index-m_iStart) * (m_iItemWidth + m_iSeparate) + m_iItemWidth/2;
		}
		
		//
	    public void onMoveLine(MotionEvent event)
	    {
	    	int x = (int)event.getX();
	    	int y = (int)event.getY();
	    	
	    	
	    	
	    	int midX = (mLineLeft + mLineRight)/2;
	    	int oldX = getXByIndex(m_iIndex);
			//
			if( event.getAction() == MotionEvent.ACTION_MOVE ) 
			{
				//判断popupinfo弹出位置
		    	int halfwidth = (mClientRect.right - mClientRect.left)/2;
		    	int oldflat = flag_position_showing;
		    	if (x <= halfwidth)
		    	{
		    		flag_position_showing = POSITION_RIGHT;
		    	}
		    	else
		    	{
		    		flag_position_showing = POSITION_LEFT;
		    	}
		    	

				 if( (x > mLineLeft) && (x < mLineRight)
		    			&& (y > mKlineTopY ) && (y < mKlineBottomY) )
				 {
		    		
		    		m_iIndex = getCurIndexByX(x);
		    		//
		    		if((oldX < midX && x >= midX) 
		        			|| (oldX >= midX && x < midX)) 
		    		{
		        		DismissInfo();
		        	}

		    		if (oldflat != flag_position_showing)
		    		{
		    			DismissInfo();
		    		}
		    		
		    		PopupInfo();
		        	invalidate();
		    	} 
		    	else if((x > mLineLeft) && (x < mLineRight)
		    			&& (y > mTechTopY) && (y < mTechBottomY))
		    	{
		    		m_iIndex = getCurIndexByX(x);

		    		if((oldX < midX && x >= midX) 
		        			|| (oldX >= midX && x < midX))
		    		{
		        		DismissInfo();
		        	}

		    		if (oldflat != flag_position_showing)
		    			DismissInfo();
		    		
		    		PopupInfo();
		        	invalidate();
		    	}
		    	else
		    	{
		    		DismissInfo();
		    		invalidate();
		    	}
			}
	    }
	    
	    //
	    public void onScrollLine(float y, float dx, float dy) {
	    	if((y < mKlineTopY) || (y > mKlineBottomY))
	    		return;
	    	
	    	int width = m_iItemWidth+m_iSeparate;
	    	
	    	int abs_x = Math.abs((int)dx);
	    	//左右移动K线
	    	if(abs_x > width) {
	    		m_bScrolling = true;
	    		
	    		DismissInfo();
	    		
	    		int offset = abs_x/width;
	    		if(dx < 0) {
	    			m_iStart -= offset;
	    		} else {
	    			m_iStart += offset;
	    		}
	    		
	    		if(m_iStart > mKLineNum - m_iScreenNum)
	    			m_iStart = mKLineNum - m_iScreenNum;
	    		
	    		if (m_iStart < 0)
	    			m_iStart = 0;

	    		getShowNum();
	    		invalidate();
	    	}
	    }   
	    //K线缩放
	    public void onScaleLine(float dy) {
	    	
	    	if(Math.abs(dy) < mLineSpace*1.5)
	    		return;
	    	
	    	DismissInfo();
	    	m_bScrolling = true;
	    	//
	    	if(dy > 0) {
	    		if(m_iItemWidth >= MAX_KLINE_WIDTH)
	    		{
	    			return;
	    		}
	    		m_iItemWidth += 2;
	    	}
	    	else {
	    		if(m_iItemWidth <= 1)
	    		{
	        		return;
	    		}
	    		m_iItemWidth -= 2;
	    	}
	    	
	    	//
	    	getShowNum();
	    	invalidate();
	    }
	}
}
