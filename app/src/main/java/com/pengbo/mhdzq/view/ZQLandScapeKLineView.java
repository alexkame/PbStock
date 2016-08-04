package com.pengbo.mhdzq.view;

import java.util.ArrayList;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalKLineData;
import com.pengbo.mhdzq.data.TagLocalKLineData.TagKAverageInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.AnalyseFunc;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;

/**
 * KLine
 * 
 * @author pobo
 * 
 */
public class ZQLandScapeKLineView extends FrameLayout {

	public static final String TAG = ZQLandScapeKLineView.class.getSimpleName();
	//
	private static final int DEFAULT_KLINE_WIDTH = 15; // kline width as default
	private static final int MAX_KLINE_WIDTH = 25; // 最大K线宽度
	private static final int	AVERAGE_NUM = 5;
	
	public static final int		TECH_VOLUME	= 1;
	public static final int		TECH_MACD	= 2;
	public static final int		TECH_KDJ	= 3;
	public static final int		TECH_RSI	= 4;
	public static final int     TECH_WR     = 5;
	public static final int     TECH_BIAS   = 6;
	
	public static final int     KLINE_MA    = 10;
	public static final int     KLINE_BOLL  = 11;
	
	private int	mCycle = Global_Define.HISTORY_TYPE_DAY;
	private int	mTechType = TECH_VOLUME;
	private int mKLineType = KLINE_MA;

	private Context mContext;
	private TagLocalStockData mOptionData;
	private MyApp mMyApp;

	public boolean mPopinfoFlag ;

	private int m_iIndex; // 当前点中的KLine索引
	private int m_iYPos;//走势线询价模式Y坐标
	
	private KLine mKLine;
	private DisplayMetrics mScreenSize;
	private boolean mbOptionOrStock = true; // true-option;false-stock
	private int mKLineTop;//kline在父窗口中y坐标

	private ArrayList<TagLocalKLineData> mKLineDataArray;
	private boolean mbShowRight = true;//是否显示右边 
	private ZQ_LandScape_Ctrl_Kline_RightPanel mRightView;

	public ZQLandScapeKLineView(Context context, boolean bOption, boolean bShowRight) {
		super(context);
		mbShowRight = bShowRight;
		mContext = context;		
		mbOptionOrStock = bOption;
		initData(context);
		initView(context);
	}

	private void initData(Context context) {
		mMyApp = (MyApp) context.getApplicationContext();
		mScreenSize = ViewTools.getScreenSize(context);
		if (mbOptionOrStock) {
			mKLineDataArray = mMyApp.getLandKLineDataArray();
		}
	}

	private void initView(Context context) {
		FrameLayout frame = new FrameLayout(context);

		LinearLayout layouthorn = new LinearLayout(context);
		layouthorn.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout layouthorn1 = new LinearLayout(context);// trend view layout include title
		layouthorn1.setOrientation(LinearLayout.VERTICAL);
		
		mKLine = new KLine(context);
		layouthorn1.addView(mKLine);
		
		if(mbShowRight){
			LayoutParams lp1 = new LayoutParams(mScreenSize.widthPixels *5/ 6, LayoutParams.FILL_PARENT);
			layouthorn.addView(layouthorn1,lp1);
			
			LayoutParams lp2 = new LayoutParams(mScreenSize.widthPixels * 1 / 6, LayoutParams.FILL_PARENT);
			mRightView = new ZQ_LandScape_Ctrl_Kline_RightPanel(context, mKLine);
			layouthorn.addView(mRightView, lp2);
			
		}else{
			LayoutParams lp1 = new LayoutParams(mScreenSize.widthPixels, LayoutParams.FILL_PARENT);// mScreenSize.heightPixels/3
			layouthorn.addView(layouthorn1,lp1);
		}	
		frame.addView(layouthorn);
		addView(frame);
	}
	
	public void setKLineTop(int top)
	{
		this.mKLineTop = top;
	}

	public void updateData(TagLocalStockData optionData) {
		this.mOptionData = optionData;
	}

	public void resetKLineParam() {
		L.i("KLineView", "resetKLineParam");
		mKLine.resetParam();
		mKLine.dataInit();
		DismissTitle(false);
	}
	
	public void resetKLineData()
	{
		mKLineDataArray = new ArrayList<TagLocalKLineData>();
	}
	
	public void SetCycle(int cycle) {
    	mCycle = cycle;
    	if(mCycle == Global_Define.HISTORY_TYPE_DAY)
		{
			mKLineDataArray = mMyApp.getLandKLineDataArray();
		}
		else if(mCycle == Global_Define.HISTORY_TYPE_WEEK)
		{
			mKLineDataArray = mMyApp.getLandKLineWeekArray();
		}
		else if(mCycle == Global_Define.HISTORY_TYPE_MONTH)
		{
			mKLineDataArray = mMyApp.getLandKLineMonthArray();
		}else if(mCycle == Global_Define.HISTORY_TYPE_3MIN
				|| mCycle == Global_Define.HISTORY_TYPE_15MIN
				|| mCycle == Global_Define.HISTORY_TYPE_30MIN
				|| mCycle == Global_Define.HISTORY_TYPE_120MIN
				|| mCycle == Global_Define.HISTORY_TYPE_240MIN)
		{
			mKLineDataArray = mMyApp.getLandKLineMinArray();
		}else
		{
			mKLineDataArray = mMyApp.getLandKLineDataArray();
		}
    }
	
	public int GetCycle() {
    	return mCycle;
    }
	
	public void setTechType(int type) {
		mTechType = type;
	}
	
	public int getTechType() {
		return mTechType;
	}

	//
	public void onTouchLine(MotionEvent event) {
		mKLine.onTouchLine(event);
	}
	
	public void onLongPressLine(MotionEvent event) {
		mKLine.onLongPressLine(event);
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
	
	public void onZoomStop()
	{
		mKLine.onZoomStop();
	}

	// update view
	public void updateAllData() {
		if (mKLine != null) mKLine.updateAllData();
		
		if (mbShowRight && mRightView != null) {
			mRightView.updateData(mOptionData, mScreenSize.heightPixels / 3);
		}

		if (mPopinfoFlag == true) {
			ShowPopTitle();
		}
	}
	
	public ArrayList<TagLocalKLineData> getKLineData()
	{
		return mKLineDataArray;
	}
	
	public int getCurrentSelectIndex()
	{
		return m_iIndex;
	}

	public void ShowPopTitle() {
		
		if (m_iIndex < 0 || m_iIndex >= mKLineDataArray.size()) {
			DismissTitle(false);
			return;
		}

		if(mOptionData == null)	{
			DismissTitle(false);
			return;
		}

		mPopinfoFlag = true;

	}
	
	
	public void DismissTitle(boolean bNeedRedraw) {	
		
		mPopinfoFlag = false;
		if (bNeedRedraw && mKLine != null) {
			mKLine.invalidate();
		}
	}

	public class KLine extends View {

		Rect mClientRect; // display area
		Paint mPaint;
		Paint linePaint;

		// private int mFontH_M = 0; // 报价栏字体
		private int mFontH = 0;
		private float mFontSize = 10; // 坐标字体大小

		private int mLeft = 0;
		private int mRight = 0;

		private int mStockPanelY = 0; // 股票报价栏Y坐标

		private int mLineLeft = 0;
		private int mLineRight = 0;

		private int mKlineTopY = 0; // Y轴上坐标

		private int mKlineBottomY = 0; // Y轴下坐标
		private int mKlineMiddleY =0;  //k线中间值 
		private int mTechTopY = 0;
		private int mTechBottomY = 0;

		private double mLineSpace = 0.0; // Y轴间距

		private int m_iItemWidth; // the width of each kline, min=1
		private int m_iItemWidth_Base; //zoom in/out 基准宽度，结束后值等于m_iItemWidth
		private int m_iSeparate; // separate width between two kline

		private int m_iStart; // the position of the first kline from left，
								// －1 = display from the last
		private int m_iScreenNum; // kline numbers can be displayed in one page
		private int m_iShowNum; // current numbers in the page

		private int mKLineNum = 0; // total numbers of kline
		private int mMaxPrice;
		private int mMinPrice;
		
		private int mMaxPriceWithAva;
		private int mMinPriceWithAva;

		private TagKAverageInfo[] mKAverage; //均线
		private String [] mMAs;

		private boolean m_bScrolling = false; // 滑动K线
	

		public KLine(Context context) {
			super(context);

			mClientRect = new Rect();
			mPaint = new Paint();
			linePaint = new Paint();
			mPaint.setAntiAlias(true);

			mFontSize = getResources().getDimension(R.dimen.font_screen_F);
			// mFontH_M = ViewTools.getFontHeight(ViewTools.TEXTSIZE_M);
			mFontH = ViewTools.getFontHeight(mFontSize);

			m_iStart = 0;// the position of the first kline from left，
							// －1 = display from the last
			m_iSeparate = 2; // separate of kline
			m_iItemWidth = DEFAULT_KLINE_WIDTH; // width of kline
			m_iItemWidth_Base = DEFAULT_KLINE_WIDTH;

			final int para[] = {5, 10, 20, 60, 120, 250};
			String maPara = PreferenceEngine.getInstance().getMA();
			mMAs = maPara.split(",");
	        final int color[] = {ColorConstant.ZQ_COLOR_LINE_MA5, ColorConstant.ZQ_COLOR_LINE_MA10,  ColorConstant.ZQ_COLOR_LINE_MA20, ColorConstant.ZQ_COLOR_LINE_MA60, 
	        		ColorConstant.ZQ_COLOR_LINE_MA120, ColorConstant.ZQ_COLOR_LINE_MA250};
			mKAverage = new TagKAverageInfo [AVERAGE_NUM];
	        for(int i = 0; i < mMAs.length; i++) {
	        	
	        	mKAverage[i] = new TagKAverageInfo();
	        	mKAverage[i].para = STD.StringToInt(mMAs[i]);
	        	mKAverage[i].color = color[i];
	        }
		}
		
		public void drawTechLine(int techType)
		{
    		if(techType >= TECH_VOLUME && techType <= TECH_RSI && techType != mTechType)
    		{
    			mTechType = techType;
    			invalidate();
    		}
		}
		
		public int getLineTop() {
			return mKlineTopY - mFontH / 2;
		}

		public int getClientHalfWidth() {
			// 判断popupinfo弹出位置
			int halfwidth = (mClientRect.right - mClientRect.left) / 2;
			return halfwidth;
		}

		// init some parameters for kline draw
		private void drawInit() {
			mLeft = mClientRect.left;
			mRight = mClientRect.right;
			mStockPanelY = 0;
			
			mPaint.setTextSize(mFontSize);
			int width = (int) mPaint.measureText("10000.000");
			

			mLineLeft = mLeft + 15 + width;
			mLineRight = mClientRect.right - 15;

			mKlineTopY = mStockPanelY + mFontH;
			
			mTechBottomY = mClientRect.bottom - 10;
			mLineSpace = (double) (mTechBottomY - mKlineTopY - mFontH*2) / 8.0;
			mTechTopY = (int) (mTechBottomY - mLineSpace * 2);			
			mKlineBottomY = mTechTopY - mFontH*2;
			mKlineMiddleY =(int) ( mKlineBottomY - mLineSpace *3);
		}

		public void resetParam() {
			m_iStart = 0;
			m_iItemWidth = DEFAULT_KLINE_WIDTH;
			m_iItemWidth_Base = DEFAULT_KLINE_WIDTH;
			m_iIndex = 0;
		}

		private void dataInit() {

			L.i(TAG, "dataInit");

			mKLineNum = mKLineDataArray.size();

			// 定位当前为最新
			if (mPopinfoFlag == false && mKLineNum > 0) {
				m_iIndex = mKLineNum - 1;
				L.d("KLineView", "dataInit--->m_iIndex = " + m_iIndex + ", mKLineNum = " + mKLineNum);
			}

			for(int i = 0; i < AVERAGE_NUM; i++) {
	        	
	        	STD.memset(mKAverage[i].data);
	        	
	        	if(mKLineNum < mKAverage[i].para || mKAverage[i].para <= 0)
	        		continue;
	        	
	        	for(int ii = 0; ii < mKLineNum; ii++) {
	        		mKAverage[i].data[ii] = mKLineDataArray.get(ii).close;
	        	}

	        	AnalyseFunc.MA(mKAverage[i].data, mKLineNum, mKAverage[i].para);
	        	
	        	for(int n = 0; n < mKAverage[i].para-1; n++) {
	        		mKAverage[i].data[n] = 0;
	        	}
	        }

			mMaxPrice = 0;
			mMinPrice = 0;
			
			mMaxPriceWithAva = 0;
			mMinPriceWithAva = 0;

			getShowNum();
		}

		// update view
		public void updateAllData() {
			// init some parameters for draw trend line
			dataInit();
			this.invalidate();
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			//
			super.onLayout(changed, left, top, right, bottom);

			if (changed) {
				mClientRect.set(left, top, right, bottom);
				L.d(TAG, "onLayout--->top = " + mClientRect.top + ", bottom = " + mClientRect.bottom + ", left = " + mClientRect.left + ", right = " + mClientRect.right);
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
			 if (mKLineType == KLINE_BOLL)
			 {
				 drawKLine_Boll(canvas);
			 }else
			 {
				 drawKLine(canvas);
			 }
			 
			if (mTechType == TECH_VOLUME)
				drawVolume(canvas);
			else if (mTechType == TECH_MACD)
				drawMACD(canvas);
			else if (mTechType == TECH_KDJ)
				drawKDJ(canvas);
			else if (mTechType == TECH_RSI)
				drawRSI(canvas);
			//新需求中 要求  不要   WR   BIAS  
//			else if (mTechType == TECH_WR)
//				drawWR(canvas);
//			else if (mTechType == TECH_BIAS)
//				drawBIAS(canvas);

			 drawRule(canvas);
		}

		// draw background
		protected void drawBackground(Canvas canvas) {
			
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setColor(ColorConstant.ZQ_COLOR_KLINE_BOUND);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(2.0f);
			
			// kline coordinate line X
			canvas.drawLine(mLineLeft, mKlineTopY, mLineRight, mKlineTopY, linePaint);
			canvas.drawLine(mLineLeft, mKlineBottomY, mLineRight, mKlineBottomY, linePaint);

			// kline coordinate line Y
			canvas.drawLine(mLineLeft, mKlineTopY, mLineLeft, mKlineBottomY, linePaint);
			canvas.drawLine(mLineRight, mKlineTopY, mLineRight, mKlineBottomY, linePaint);

			// tech coordinate line X
			canvas.drawLine(mLineLeft, mTechTopY, mLineRight, mTechTopY, linePaint);
			// canvas.drawLine(mLineLeft, (int)(mTechTopY + mLineSpace), mLineRight, (int)(mTechTopY + mLineSpace),
			// linePaint);
			canvas.drawLine(mLineLeft, mTechBottomY, mLineRight, mTechBottomY, linePaint);

			// tech coordinate line Y
			canvas.drawLine(mLineLeft, mTechTopY, mLineLeft, mTechBottomY, linePaint);
			canvas.drawLine(mLineRight, mTechTopY, mLineRight, mTechBottomY, linePaint);
			
			//画虚点 
			linePaint.setColor(ColorConstant.ZQ_DATA_XUDIAN);
			linePaint.setStrokeWidth(0.8f);
			linePaint.setStyle(Paint.Style.STROKE);
			Path path1=new Path();
			path1.moveTo(mLineLeft, mKlineMiddleY);
			path1.lineTo(mLineRight, mKlineMiddleY);
			
			PathEffect effects = new DashPathEffect(new float[] {
					5, 5, 5, 5
			}, 1);
			linePaint.setPathEffect(effects);
			canvas.drawPath(path1, linePaint);
		}

		// KLine
		protected void drawKLine(Canvas canvas) {
			
			mKLineNum = mKLineDataArray.size();
			if (mKLineNum == 0 || mOptionData == null) return;
			
			int maxvalue = 0;
			int minvalue = 0;
			if (m_iStart >= 0 && m_iStart < mKLineNum) {
				// calc max&min
				mMaxPrice = mKLineDataArray.get(m_iStart).high;
				mMinPrice = mKLineDataArray.get(m_iStart).low;
				maxvalue = mMaxPrice;
				minvalue = mMinPrice;
				for (int i = 0; i < m_iShowNum && (m_iStart + i) < mKLineDataArray.size(); i++) {
					mMaxPrice = (mMaxPrice < mKLineDataArray.get(m_iStart + i).high) ? mKLineDataArray.get(m_iStart + i).high : mMaxPrice;
					mMinPrice = (mMinPrice > mKLineDataArray.get(m_iStart + i).low) ? mKLineDataArray.get(m_iStart + i).low : mMinPrice;

					//均价
		    		for(int j=0; j<AVERAGE_NUM; j++) {
		    			
		    			int value = mKAverage[j].data[m_iStart+i];
		    			if(value == 0) {
		    				continue;
		    			}
		    			maxvalue = (maxvalue < value) ? value : maxvalue;
		    			minvalue = (minvalue > value) ? value : minvalue;
		    		}
				}
			}
			if (mMaxPrice <= mMinPrice) {
				mMaxPrice = mMinPrice + 6000;
			}
			if(maxvalue < mMaxPrice)
			{
				maxvalue = mMaxPrice;
			}
			if(minvalue > mMinPrice)
			{
				minvalue = mMinPrice;
			}
			
			mMaxPriceWithAva = maxvalue;
			mMinPriceWithAva = minvalue;
			
			linePaint.setAntiAlias(false);
			linePaint.setPathEffect(null);
			linePaint.setStrokeWidth(2.0f);
			linePaint.setStyle(Paint.Style.FILL);
			double divscale = (double) (mKlineBottomY - mKlineTopY - 2*mFontH) / (maxvalue - minvalue);
			int x = mLineLeft + 1;
			int mid = x;
			int oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
			boolean bDrawn_High = false;
			boolean bDrawn_Low = false;
			for (int j = 0; j < m_iShowNum  && (m_iStart + j) < mKLineDataArray.size(); j++, x += (m_iItemWidth + m_iSeparate)) {
				TagLocalKLineData data = mKLineDataArray.get(m_iStart + j);
				if (data.open <= 0) data.open = data.close;
				if (data.high <= 0) data.high = data.close;
				if (data.low <= 0) data.low = data.close;
				
				int y_open = mKlineBottomY - mFontH - (int) ((data.open - minvalue) * divscale + 0.5);
				int y_high = mKlineBottomY - mFontH - (int) ((data.high - minvalue) * divscale + 0.5);
				int y_low = mKlineBottomY - mFontH - (int) ((data.low - minvalue) * divscale + 0.5);
				int y_close = mKlineBottomY - mFontH - (int) ((data.close - minvalue) * divscale + 0.5);
				
				y_open = Math.max(y_open, mKlineTopY);
				y_high = Math.max(y_high, mKlineTopY);
				y_low = Math.min(y_low, mKlineBottomY);
				y_close = Math.min(y_close, mKlineBottomY);

				mid = x + (m_iItemWidth - 1) / 2;
				if(data.high == mMaxPrice)
				{
					if(!bDrawn_High)
					{
						//画最高价
						mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
						mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
						mPaint.setTextSize(mFontSize);
						mPaint.setTextAlign(Paint.Align.LEFT);
						int width = (int) mPaint.measureText(ViewTools.getStringByPrice(mMaxPrice, 1, mOptionData.PriceDecimal, mOptionData.PriceRate));
						if((x + width) > mLineRight)
						{
							ViewTools.DrawPrice(canvas, mLineRight - width - 2, y_high - mFontH, mMaxPrice, 1, mMaxPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}else
						{
							ViewTools.DrawPrice(canvas, x, y_high - mFontH, mMaxPrice, 1, mMaxPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}
						bDrawn_High = true;
					}
					
				}
				if(data.low == mMinPrice)
				{
					if(!bDrawn_Low)
					{
						//画最低价
						mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
						mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
						mPaint.setTextSize(mFontSize);
						mPaint.setTextAlign(Paint.Align.LEFT);
						int width = (int) mPaint.measureText(ViewTools.getStringByPrice(mMinPrice, 1, mOptionData.PriceDecimal, mOptionData.PriceRate));
						if((x + width) > mLineRight)
						{
							ViewTools.DrawPrice(canvas, mLineRight - width - 2, y_low, mMinPrice, 1, mMinPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}else
						{
							ViewTools.DrawPrice(canvas, x, y_low, mMinPrice, 1, mMinPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}
						bDrawn_Low = true;
					}
				}

				if (data.close > data.open) {
					// 阳线
					linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
					linePaint.setStyle(Paint.Style.FILL);
					oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;

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
					linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
					linePaint.setStyle(Paint.Style.FILL);
					oldColor = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;

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
						if (mKLineDataArray.get(index).close > mKLineDataArray.get(index - 1).close) {
							linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
							oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
						} else if (mKLineDataArray.get(index).close < mKLineDataArray.get(index - 1).close) {
							linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
							oldColor = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;
						} else {
							linePaint.setColor(oldColor);
						}
					} else {
						linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
						oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
					}

					canvas.drawLine(x, y_open, x + m_iItemWidth, y_open, linePaint);
					canvas.drawLine(mid, y_high, mid, y_low, linePaint);
				}
			}
			
			//均线
	    	//显示均线值
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	int left = mLineLeft;
	    	String str = "";
	        final String avg_str[] = {"MA", "  ", "  ", "  ", "  ", "  "};	
	    	for(int i = 0; i < mMAs.length && i < avg_str.length; i++) {
	    		left += mPaint.measureText(str);
	        	mPaint.setColor(mKAverage[i].color);
	        	str = avg_str[i] + mMAs[i] +": " + ViewTools.getStringByPrice(mKAverage[i].data[m_iIndex], 0, mOptionData.PriceDecimal, mOptionData.PriceRate);
	        	ViewTools.DrawText(canvas, str, left, 0, mStockPanelY, 0, mPaint);
	    	}

			linePaint.setAntiAlias(true);
			linePaint.setStyle(Paint.Style.STROKE);
			PathEffect effect = new CornerPathEffect(3);
			linePaint.setPathEffect(effect);
			
			for(int i=0; i<AVERAGE_NUM; i++)
			{
				// 均线显示起始位置
				int start = ((mKAverage[i].para - 1 - m_iStart) > 0) ? (mKAverage[i].para - 1 - m_iStart) : 0;
				int index = start + m_iStart; // 对应K线起始位置的索引
	
				int avgX = mLineLeft + 1 + start * (m_iItemWidth + m_iSeparate) + m_iItemWidth / 2;
				int avgY = mKlineBottomY - mFontH - (int) ((mKAverage[i].data[index] - minvalue) * divscale + 0.5);
	
				Path path = new Path();
				path.moveTo(avgX, avgY);
	
				for (int j = 1; j < m_iShowNum - start; j++) {
	
					avgX += m_iItemWidth + m_iSeparate;
					avgY = mKlineBottomY - mFontH - (int) ((mKAverage[i].data[index + j] - minvalue) * divscale + 0.5);
					if(avgY >= mKlineTopY && avgY <= mKlineBottomY)
					{
						path.lineTo(avgX, avgY);
					}
				}
	
				linePaint.setColor(mKAverage[i].color);
				canvas.drawPath(path, linePaint);
			}
			
			
			
			
			//画左下角的时间 
			// 时间
			
			String  time_left = "----";
			if (mCycle == Global_Define.HISTORY_TYPE_DAY
					|| mCycle == Global_Define.HISTORY_TYPE_WEEK
					|| mCycle == Global_Define.HISTORY_TYPE_MONTH) {
				time_left = STD.getDateSringyyyymmdd(mKLineDataArray
						.get(m_iStart).date);
			} else {
				String date=STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart).date);
				String sub_date=date;
				if(date.length()>=4){
					sub_date=date.substring(date.length()-4, date.length());
				}
				time_left = sub_date+" "+STD.getTimeSringhhmm(mKLineDataArray.get(m_iStart).time / 100);
			}
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float fontSize = getResources().getDimension(R.dimen.font_screen_F);
			mPaint.setTextSize(fontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			//canvas, time, mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0, mPaint
			
			int width = (int) mPaint.measureText(time_left);
			ViewTools.DrawText(canvas, time_left, mLineLeft+2, mLineLeft+2+width, mKlineBottomY, mKlineBottomY+mFontH, mPaint);
			
			
			
			
			
			//画右下角的时间 
			String  time_right = "----";
			if (mCycle == Global_Define.HISTORY_TYPE_DAY
					|| mCycle == Global_Define.HISTORY_TYPE_WEEK
					|| mCycle == Global_Define.HISTORY_TYPE_MONTH) {
				if((m_iStart + m_iShowNum-1 )>= 0 && (m_iStart + m_iShowNum-1) < mKLineDataArray.size()){
					time_right = STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart + m_iShowNum-1).date);
				}
				
			} else {
				if((m_iStart + m_iShowNum-1 )>= 0 && (m_iStart + m_iShowNum-1) < mKLineDataArray.size()){
					String date=STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart +  m_iShowNum-1).date);
					String sub_date=date;
					if(date.length()>=4){
						sub_date=date.substring(date.length()-4, date.length());
					}
					time_right = sub_date+" "+STD.getTimeSringhhmm(mKLineDataArray.get(m_iStart +  m_iShowNum-1).time / 100);
				}
				
			
			}
			
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float font_size = getResources().getDimension(R.dimen.font_screen_F);
			mPaint.setTextSize(font_size);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			//canvas, time, mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0, mPaint
			
			int width_font = (int) mPaint.measureText(time_right);
			
			ViewTools.DrawText(canvas, time_right, mLineRight-2-width_font, mLineRight-2, mKlineBottomY, mKlineBottomY+mFontH, mPaint);
			
			
			//画价格
			int Draw_Price=0; 
			int mLineSpacePrice=(maxvalue - minvalue)/2;
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);	
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			Draw_Price=minvalue;
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, mKlineBottomY-mFontH,Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
			
			
			//中间一条价格 
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_MIDDLE);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);			
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			Draw_Price=minvalue+mLineSpacePrice;
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, (int)(mKlineBottomY-mLineSpace*2.5-mFontH),Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
			
			//K线 最上面一条 价格 
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);			
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			Draw_Price=maxvalue;
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, mStockPanelY+mFontH,Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
			
		
			
		}
		
		// KLine-布林线
		//BOLL:MA(CLOSE,N);
		//UB:BOLL+2*STD(CLOSE,N);
		//LB:BOLL-2*STD(CLOSE,N);
		protected void drawKLine_Boll(Canvas canvas) {
			mKLineNum = mKLineDataArray.size();
			if (mKLineNum <= 0 || mOptionData == null) return;
			
			if (mKLineNum > 0 && m_iStart < mKLineNum) {
				// calc max&min
				mMaxPrice = mKLineDataArray.get(m_iStart).high;
				mMinPrice = mKLineDataArray.get(m_iStart).low;
				for (int i = 0; i < m_iShowNum  && (m_iStart + i) < mKLineDataArray.size(); i++) {
					mMaxPrice = (mMaxPrice < mKLineDataArray.get(m_iStart + i).high) ? mKLineDataArray.get(m_iStart + i).high : mMaxPrice;
					mMinPrice = (mMinPrice > mKLineDataArray.get(m_iStart + i).low) ? mKLineDataArray.get(m_iStart + i).low : mMinPrice;
				}
			}
			if (mMaxPrice <= mMinPrice) {
				mMaxPrice = mMinPrice + 6000;
			}
			
			long maxvalue = mMaxPrice;
			long minvalue = mMinPrice;
			//计算boll线
			final int para = 20;
			//0-BOLL;1-UB;2-LB
			long[][] dBOLL = new long[3][AppConstants.MAXNUM_KLINE];
			if(mKLineNum >= para)
			{
				long[] CLOSE = new long[AppConstants.MAXNUM_KLINE];
				long[] std = new long[AppConstants.MAXNUM_KLINE];
				for (int i = 0; i < mKLineNum; i++)
				{
					int close = mKLineDataArray.get(i).close;
					CLOSE[i]	= Math.max(close, 0);		//涨跌
				}
				// calc boll value
				System.arraycopy(CLOSE, 0, dBOLL[0], 0, mKLineNum);
				System.arraycopy(CLOSE, 0, std, 0, mKLineNum);
				AnalyseFunc.MA(dBOLL[0], mKLineNum, para);
				AnalyseFunc.STD(std, dBOLL[0], mKLineNum, para);
				
				//UB:BOLL+2*STD(CLOSE,N);
				dBOLL[1][0]	= 0;
				for (int j = 1; j < mKLineNum; j++) {
					if (dBOLL[0][j] > 0) {
						dBOLL[1][j]	= dBOLL[0][j] + 2*std[j];
					} else {
						dBOLL[1][j]	= dBOLL[1][j-1];
					}
				}
				//LB:BOLL-2*STD(CLOSE,N);
				dBOLL[2][0]	= 0;
				for (int j = 1; j < mKLineNum; j++) {
					if (dBOLL[0][j] > 0) {
						dBOLL[2][j]	= dBOLL[0][j] - 2*std[j];
					} else {
						dBOLL[2][j]	= dBOLL[2][j-1];
					}
				}
				for(int k = 0; k < 3; k++)
				{
					for(int n = 0; n < para-1; n++) {
						dBOLL[k][n] = 0;
		        	}
				}
				
				//最大最小值
				maxvalue = dBOLL[1][m_iStart+1];
				{
					for (int j = 1; j < m_iShowNum; j++) {
						long value = dBOLL[1][m_iStart+j];
						maxvalue	= Math.max(maxvalue, value);
					}
				}
				maxvalue = Math.max(maxvalue, mMaxPrice);
				
				minvalue = maxvalue;
				for (int j = 1; j < m_iShowNum; j++) {
					long value = dBOLL[2][m_iStart+j];
					if(value <= 0)
						continue;
					
					minvalue = Math.min(minvalue, value);
				}
				minvalue = Math.min(minvalue, mMinPrice);
				if (maxvalue <= minvalue) {
					maxvalue = minvalue + 6000;
				}
			}
			
			mMaxPriceWithAva = (int) maxvalue;
			mMinPriceWithAva = (int) minvalue;

			// draw high and low
//			mPaint.setColor(Color.WHITE);
//			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//			mPaint.setTextSize(mFontSize);
//			mPaint.setTextAlign(Paint.Align.LEFT);
//			ViewTools.DrawPrice(canvas, mLineLeft + 2, mKlineTopY, mMaxPrice, 1, mMaxPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
//			ViewTools.DrawPrice(canvas, mLineLeft + 2, mKlineBottomY - mFontH, mMinPrice, 1, mMinPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);

			linePaint.setAntiAlias(false);
			linePaint.setPathEffect(null);
			linePaint.setStrokeWidth(2.0f);
			linePaint.setStyle(Paint.Style.FILL);
			double divscale = (double) (mKlineBottomY - mKlineTopY - 2*mFontH) / (maxvalue - minvalue);
			int x = mLineLeft + 1;
			int mid = x;
			int oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
			boolean bDrawn_High = false;
			boolean bDrawn_Low = false;
			for (int j = 0; j < m_iShowNum && (m_iStart + j) < mKLineDataArray.size(); j++, x += (m_iItemWidth + m_iSeparate)) {
				TagLocalKLineData data = mKLineDataArray.get(m_iStart + j);
				if (data.open <= 0) data.open = data.close;
				if (data.high <= 0) data.high = data.close;
				if (data.low <= 0) data.low = data.close;
				
				int y_open = mKlineBottomY - mFontH - (int) ((data.open - minvalue) * divscale + 0.5);
				int y_high = mKlineBottomY - mFontH - (int) ((data.high - minvalue) * divscale + 0.5);
				int y_low = mKlineBottomY - mFontH - (int) ((data.low - minvalue) * divscale + 0.5);
				int y_close = mKlineBottomY - mFontH - (int) ((data.close - minvalue) * divscale + 0.5);

				y_open = Math.max(y_open, mKlineTopY);
				y_high = Math.max(y_high, mKlineTopY);
				y_low = Math.min(y_low, mKlineBottomY);
				y_close = Math.min(y_close, mKlineBottomY);
				
				mid = x + (m_iItemWidth - 1) / 2;
				if(data.high == mMaxPrice)
				{
					if(!bDrawn_High)
					{
						//画最高价
						mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
						mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
						mPaint.setTextSize(mFontSize);
						mPaint.setTextAlign(Paint.Align.LEFT);
						int width = (int) mPaint.measureText(ViewTools.getStringByPrice(mMaxPrice, 1, mOptionData.PriceDecimal, mOptionData.PriceRate));
						if((x + width) > mLineRight)
						{
							ViewTools.DrawPrice(canvas, mLineRight - width - 2, y_high - mFontH, mMaxPrice, 1, mMaxPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}else
						{
							ViewTools.DrawPrice(canvas, x, y_high - mFontH, mMaxPrice, 1, mMaxPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}
						bDrawn_High = true;
					}
					
				}
				if(data.low == mMinPrice)
				{
					if(!bDrawn_Low)
					{
						//画最低价
						mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
						mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
						mPaint.setTextSize(mFontSize);
						mPaint.setTextAlign(Paint.Align.LEFT);
						int width = (int) mPaint.measureText(ViewTools.getStringByPrice(mMinPrice, 1, mOptionData.PriceDecimal, mOptionData.PriceRate));
						if((x + width) > mLineRight)
						{
							ViewTools.DrawPrice(canvas, mLineRight - width - 2, y_low, mMinPrice, 1, mMinPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}else
						{
							ViewTools.DrawPrice(canvas, x, y_low, mMinPrice, 1, mMinPrice, mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, true);
						}
						bDrawn_Low = true;
					}
				}

				if (data.close > data.open) {
					// 阳线
					linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
					linePaint.setStyle(Paint.Style.FILL);
					oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;

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
					linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
					linePaint.setStyle(Paint.Style.FILL);
					oldColor = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;

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
						if (mKLineDataArray.get(index).close > mKLineDataArray.get(index - 1).close) {
							linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
							oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
						} else if (mKLineDataArray.get(index).close < mKLineDataArray.get(index - 1).close) {
							linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
							oldColor = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;
						} else {
							linePaint.setColor(oldColor);
						}
					} else {
						linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
						oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
					}

					canvas.drawLine(x, y_open, x + m_iItemWidth, y_open, linePaint);
					canvas.drawLine(mid, y_high, mid, y_low, linePaint);
				}
			}
			
			//boll线
	    	//显示boll值
			String sText = "";
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_BOLL_A);
	    	int left = mLineLeft;
			sText = "BOLL-M("+para+",2)  BOLL: " + STD.DataToString(dBOLL[0][m_iIndex], mOptionData.PriceDecimal, mOptionData.PriceRate);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mStockPanelY, 0, mPaint);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_BOLL_B);
			left += (int)mPaint.measureText(sText);
	    	sText = "  UB: " + STD.DataToString(dBOLL[1][m_iIndex], mOptionData.PriceDecimal, mOptionData.PriceRate);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mStockPanelY, 0, mPaint);
			
			mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_BOLL_C);
			left += (int)mPaint.measureText(sText);
	    	sText = "  LB: " + STD.DataToString(dBOLL[2][m_iIndex], mOptionData.PriceDecimal, mOptionData.PriceRate);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mStockPanelY, 0, mPaint);

			//画线
			linePaint.setAntiAlias(true);
	    	linePaint.setStyle(Paint.Style.STROKE);
			final int color[] = {ColorConstant.ZQ_COLOR_LINE_BOLL_A, ColorConstant.ZQ_COLOR_LINE_BOLL_B, ColorConstant.ZQ_COLOR_LINE_BOLL_C};
	    	for(int i=0; i<3; i++) {	

	    		// 显示起始位置
				int start = ((para - 1 - m_iStart) > 0) ? (para - 1 - m_iStart) : 0;
				int index = start + m_iStart; // 对应K线起始位置的索引
	
				int techX = mLineLeft + 1 + start * (m_iItemWidth + m_iSeparate) + m_iItemWidth / 2;
				int techY = mKlineBottomY - mFontH - (int) ((dBOLL[i][index] - minvalue) * divscale + 0.5);
	
				Path path = new Path();
				path.moveTo(techX, techY);
	
				for (int j = 1; j < m_iShowNum - start; j++) {
	
					techX += m_iItemWidth + m_iSeparate;
					techY = mKlineBottomY - mFontH - (int)((dBOLL[i][index+j] - minvalue) * divscale + 0.5);
					if(techY >= mKlineTopY && techY <= mKlineBottomY)
					{
						path.lineTo(techX, techY);
					}
				}
				linePaint.setColor(color[i]);
		    	PathEffect effect = new CornerPathEffect(3);
		    	linePaint.setPathEffect(effect);
				canvas.drawPath(path, linePaint);
			}
	    	
	    	
	    	
	    	//画左下角的时间 
			// 时间
			String  time_left = "----";
			if (mCycle == Global_Define.HISTORY_TYPE_DAY
					|| mCycle == Global_Define.HISTORY_TYPE_WEEK
					|| mCycle == Global_Define.HISTORY_TYPE_MONTH) {
				time_left = STD.getDateSringyyyymmdd(mKLineDataArray
						.get(m_iStart).date);
			} else {
				String date=STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart).date);
				String sub_date=date;
				if(date.length()>=4){
					sub_date=date.substring(date.length()-4, date.length());
				}
				time_left = sub_date+" "+STD.getTimeSringhhmm(mKLineDataArray.get(m_iStart).time / 100);
			}
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float fontSize = getResources().getDimension(R.dimen.font_screen_F);
			mPaint.setTextSize(fontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			//canvas, time, mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0, mPaint
			
			int width = (int) mPaint.measureText(time_left);
			ViewTools.DrawText(canvas, time_left, mLineLeft+2, mLineLeft+2+width, mKlineBottomY, mKlineBottomY+mFontH, mPaint);
			
			
			
			
			
			//花右下角的时间 
			String  time_right = "----";
			if (mCycle == Global_Define.HISTORY_TYPE_DAY
					|| mCycle == Global_Define.HISTORY_TYPE_WEEK
					|| mCycle == Global_Define.HISTORY_TYPE_MONTH) {
				if((m_iStart + m_iShowNum-1 )>= 0 && (m_iStart + m_iShowNum-1) < mKLineDataArray.size()){
					time_right = STD.getDateSringyyyymmdd(mKLineDataArray
							.get(m_iStart + m_iShowNum-1).date);
				}
				
			} else {
				if((m_iStart + m_iShowNum-1 )>= 0 && (m_iStart + m_iShowNum-1) < mKLineDataArray.size()){
					String date=STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iStart +  m_iShowNum-1).date);
					String sub_date=date;
					if(date.length()>=4){
						sub_date=date.substring(date.length()-4, date.length());
					}
					time_right = sub_date+" "+STD.getTimeSringhhmm(mKLineDataArray.get(m_iStart +  m_iShowNum-1).time / 100);
				}
				
			}
			
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float font_size = getResources().getDimension(R.dimen.font_screen_F);
			mPaint.setTextSize(font_size);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			//canvas, time, mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0, mPaint
			
			int width_font = (int) mPaint.measureText(time_right);
			
			ViewTools.DrawText(canvas, time_right, mLineRight-2-width_font, mLineRight-2, mKlineBottomY, mKlineBottomY+mFontH, mPaint);
			
			
			
			//画价格
			int Draw_Price=0; 
			int mLineSpacePrice=(int) ((maxvalue - minvalue)/2);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);	
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			Draw_Price=(int) minvalue;
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, mKlineBottomY-mFontH,Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
			
			
			//中间一条价格 
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_MIDDLE);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);			
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			Draw_Price=(int) (minvalue+mLineSpacePrice);
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, (int)(mKlineBottomY-mLineSpace*2.5-mFontH),Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
			
			//K线 最上面一条 价格 
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);			
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			Draw_Price=(int) maxvalue;
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, mStockPanelY+mFontH,Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
			
			
		}

		// 显示标尺
		protected void drawRule(Canvas canvas) {
			if (mKLineDataArray.size() <= 0) return;

			if (mPopinfoFlag == true) {
				//
				int indexX = mLineLeft + 2 + (m_iIndex - m_iStart) * (m_iItemWidth + m_iSeparate) + m_iItemWidth / 2;
				
				//
				linePaint.setAntiAlias(true);
				linePaint.setStyle(Paint.Style.STROKE);
				linePaint.setColor(ColorConstant.ZQ_COLOR_TIME);
				PathEffect effect = new DashPathEffect(new float[] {
						3, 2
				}, 0);
				linePaint.setPathEffect(effect);
				Path path = new Path();
				path.moveTo(indexX, mKlineTopY);
				path.lineTo(indexX, mKlineBottomY);
				canvas.drawPath(path, linePaint);
				path.moveTo(indexX, mTechTopY);
				path.lineTo(indexX, mTechBottomY);
				canvas.drawPath(path, linePaint);
				
				//draw 横轴查价线
				int indexY = m_iYPos;
				
				int Draw_Price = mMinPriceWithAva;
				if (indexY >= (mKlineBottomY - mFontH))
				{
					indexY = mKlineBottomY - mFontH;
				}
				if (indexY <= (mKlineTopY + mFontH))
				{
					indexY = mKlineTopY + mFontH;
				}
				
				if (indexY > mKlineTopY && indexY < mKlineBottomY)
				{
					//
					path.moveTo(mLineLeft, indexY);
					path.lineTo(mLineRight, indexY);
					canvas.drawPath(path, linePaint);
					
					// 价格显示
					mPaint.setColor(ColorConstant.ZQ_COLOR_TIME);
					mPaint.setAlpha(228);
					mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
					mPaint.setTextSize(mFontSize);
					mPaint.setTextAlign(Paint.Align.CENTER);
					
					String strprice = "----";

					int priceOffset = (int) ((mMaxPriceWithAva - mMinPriceWithAva)/(mKlineBottomY - mKlineTopY));
					
					if (indexY == (mKlineTopY + mFontH))
					{
						Draw_Price = mMaxPriceWithAva;
					}else
					{
						Draw_Price = mMinPriceWithAva + priceOffset*(mKlineBottomY - mFontH - indexY);
					}
					//ViewTools.DrawPrice(canvas, mLineLeft+2, mKlineBottomY-mFontH,Draw_Price,1,Draw_Price, mOptionData.PriceDecimal, mOptionData.PriceRate,mPaint,true);
				
					
					//int nPrice = (int) (mOptionData.HQData.getnLastClear() - priceOffset * (m_iYPos - mKlineMidY)/mLineSpace);
					strprice = ViewTools.getStringByPrice(Draw_Price, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
					
					int nWidth = (int) mPaint.measureText(strprice) + 10;
					int nHeight = mFontH + 10;

					RectF r = new RectF();
					//r.set(mLineLeft, indexY - nHeight/2, mLineLeft + nWidth, indexY + nHeight/2);
					
					r.set(mLineLeft - nWidth, indexY - nHeight/2, mLineLeft, indexY + nHeight/2);
					canvas.drawRoundRect(r, 5, 5, mPaint);

					mPaint.setColor(Color.WHITE);
					mPaint.setTextSize(mFontSize);
					ViewTools.DrawText(canvas, strprice, (int) r.left, (int) r.right, (int) r.top, (int) r.bottom, mPaint);
				}
				
				// 时间显示
				mPaint.setColor(ColorConstant.ZQ_COLOR_TIME);
				mPaint.setAlpha(228);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setTextSize(mFontSize);
				mPaint.setTextAlign(Paint.Align.CENTER);

				int width = (int) mPaint.measureText("20100101") + 10;
				if (mCycle == Global_Define.HISTORY_TYPE_DAY
						|| mCycle == Global_Define.HISTORY_TYPE_WEEK
						|| mCycle == Global_Define.HISTORY_TYPE_MONTH) {
					width = (int) mPaint.measureText("20100101") + 10;
				}else
				{
					width = (int) mPaint.measureText("0101 00:00") + 10;
				}
				RectF r = new RectF();
				if (indexX + width / 2 < mRight)
					r.set(indexX - width / 2, mKlineBottomY + 1, indexX + width / 2, mTechTopY - 1);
				else
					r.set(mRight - 1 - width, mKlineBottomY + 1, mRight - 1, mTechTopY - 1);
				canvas.drawRoundRect(r, 5, 5, mPaint);

				mPaint.setColor(Color.WHITE);
				mPaint.setTextSize(mFontSize);
				String  time = "";
				if (mCycle == Global_Define.HISTORY_TYPE_DAY
						|| mCycle == Global_Define.HISTORY_TYPE_WEEK
						|| mCycle == Global_Define.HISTORY_TYPE_MONTH) {
					time = STD.getDateSringyyyymmdd(mKLineDataArray
							.get(m_iIndex).date);
				} else {
					
					String date=STD.getDateSringyyyymmdd(mKLineDataArray.get(m_iIndex).date);
					String sub_date=date;
					if(date.length()>=4){
						sub_date=date.substring(date.length()-4, date.length());
					}
					time = sub_date+" "+STD.getTimeSringhhmm(mKLineDataArray.get(m_iIndex).time / 100);
				}
				ViewTools.DrawText(canvas, time, (int) r.left, (int) r.right, (int) r.top, (int) r.bottom, mPaint);
			}
		}

		protected void drawVolume(Canvas canvas) {
			if (mKLineNum == 0  || mOptionData == null) return;
			// calc max value
			long max = 0;
			if (mKLineNum > 0) {
				max = mKLineDataArray.get(m_iStart).volume;
				for (int i = 0; i < m_iShowNum; i++) {

					long value = mKLineDataArray.get(m_iStart + i).volume;
					max = (max < value) ? value : max;
				}
			}

			//显示坐标
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
	    	String curValue = "VOL: ";
	    	long vol = 0;
	    	if(m_iIndex>=0 && m_iIndex<mKLineNum)
	    		vol = mKLineDataArray.get(m_iIndex).volume;

			curValue += ViewTools.getStringByVolume(vol, mOptionData.market, mOptionData.VolUnit, 6, false);
	    	ViewTools.DrawText(canvas, curValue, mLineLeft, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	
			// calc y scale
			double YScales = 0.0;
			if (max > 0) {
				YScales = (double) (mTechBottomY - mTechTopY - 2) / max;
			}
			mPaint.setStyle(Paint.Style.FILL);
			int x = mLineLeft + 1;
			int oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
			for (int j = 0; j < m_iShowNum; j++, x += (m_iItemWidth + m_iSeparate)) {

				TagLocalKLineData data = mKLineDataArray.get(m_iStart + j);
				int y = 0;
				long value = 0;
				value = data.volume;

				//if (value == 0) continue;
				y = mTechBottomY - 1 - (int) (value * YScales + 0.5);

				if (data.close > data.open) {
					mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
					mPaint.setStyle(Paint.Style.FILL);//-------------yjh
					oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
				} else if (data.close < data.open) {
					mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
					mPaint.setStyle(Paint.Style.FILL);
					oldColor = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;
				} else {

					int index = m_iStart + j;
					if (index > 0) {
						if (mKLineDataArray.get(index).close > mKLineDataArray.get(index - 1).close) {
							mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
							mPaint.setStyle(Paint.Style.FILL);//-------------yjh
							oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
						} else if (mKLineDataArray.get(index).close < mKLineDataArray.get(index - 1).close) {
							mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
							mPaint.setStyle(Paint.Style.FILL);
							oldColor = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;
						} else {
							if (oldColor == ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED)
							{
								mPaint.setStyle(Paint.Style.FILL);//-------------yjh
							}else
							{
								mPaint.setStyle(Paint.Style.FILL);
							}
							mPaint.setColor(oldColor);
						}
					} else {
						mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
						mPaint.setStyle(Paint.Style.FILL);//-------------yjh
						oldColor = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
					}
				}

				Rect rect = new Rect();
				rect.set(x, y, x + m_iItemWidth, mTechBottomY);
				canvas.drawRect(rect, mPaint);
			}
		}
		
		/* MACD
		 * DIF:EMA(CLOSE,SHORT)-EMA(CLOSE,LONG);
		 * DEA:EMA(DIF,MID);
		 * MACD:(DIF-DEA)*2;
		 */
		protected void drawMACD(Canvas canvas)
		{
			if (mKLineDataArray.size()<=0 || mOptionData == null)
				return;
			
			String str = PreferenceEngine.getInstance().getMACD();
			String[] paras = str.split(",");
			if(paras == null || paras.length != 3)
				return;
			//计算
			final int para1	= STD.StringToInt(paras[0]);//SHORT
			final int para2	= STD.StringToInt(paras[1]);//LONG
			final int para3	= STD.StringToInt(paras[2]);//MID
			final int maxpara = Math.max(para1, para2);
			
			int[] EMA1, EMA2, DIF, MACD;
			EMA1 = new int[AppConstants.MAXNUM_KLINE];
			EMA2 = new int[AppConstants.MAXNUM_KLINE];
			DIF = new int[AppConstants.MAXNUM_KLINE];
			MACD = new int[AppConstants.MAXNUM_KLINE];
			//
			for(int i=0; i < mKLineNum; i++) {
				EMA1[i] = mKLineDataArray.get(i).close;
				EMA2[i] = mKLineDataArray.get(i).close;
			}
			//计算12日EMA值
			AnalyseFunc.EMA(EMA1, mKLineNum, para1);
			//计算26日EMA值
			AnalyseFunc.EMA(EMA2, mKLineNum, para2);
			//dif=ema12-ema26
			for (int i = 0; i < mKLineNum; i++) {
				DIF[i]	= EMA1[i] - EMA2[i];
			}
			//计算dif的9日ema值
			
			System.arraycopy(DIF, 0, MACD, 0, mKLineNum);
			AnalyseFunc.EMA(MACD, mKLineNum, para3);
			
			//最大最小值
			int start = 0;//Math.max(m_iStart, 0);
			int	maxvalue = 0;
			int minvalue = DIF[m_iStart+start];
			for(int i=start; i<m_iShowNum; i++) {
				int value	= DIF[m_iStart+i];
				maxvalue	= Math.max(maxvalue, value);
				minvalue	= Math.min(minvalue, value);
			}
			start = Math.max(para3-m_iStart, 0);
			for(int i=start; i<m_iShowNum; i++) {
				int value	= MACD[m_iStart+i];
				maxvalue	= Math.max(maxvalue, value);
				minvalue	= Math.min(minvalue, value);
				value	= (DIF[m_iStart+i]-MACD[m_iStart+i])*2;
				maxvalue	= Math.max(maxvalue, value);
				minvalue	= Math.min(minvalue, value);
			}
			if (maxvalue <= minvalue) {
				maxvalue	= minvalue + 100;
			}

	    	//显示坐标
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.RIGHT);
	    	mPaint.setColor(Color.WHITE);
	    	//int fontH = ViewTools.getFontHeight(mFontSize);
	    	//int tempY = (int)(mTechTopY + mLineSpace - fontH/3);
	    	//String sText = STD.DataToString((maxvalue+minvalue)/2, 2, 0);
	    	//ViewTools.DrawText(canvas, sText, 0, mLineLeft-2, tempY, 0, mPaint);
	    	//显示当前值
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_MACD_A);
	    	int left = mLineLeft;
	    	String sText = "MACD("+para1+","+para2+","+para3+")  DIF: "+ STD.DataToString(DIF[m_iIndex], 2, mOptionData.PriceRate);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_MACD_B);
			left += (int)mPaint.measureText(sText);
	    	sText = "  DEA: " + STD.DataToString(MACD[m_iIndex], 2, mOptionData.PriceRate);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_MACD_C);
			left += (int)mPaint.measureText(sText);
	    	sText = "  MACD: " + STD.DataToString((DIF[m_iIndex]-MACD[m_iIndex])*2, 2, mOptionData.PriceRate);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	
	    	double divscale = (double)(mTechBottomY - mTechTopY - 1)/(maxvalue - minvalue);
	    	//
	    	int midY = mTechBottomY - (int)((0 - minvalue) * divscale + 0.5);
	    	if(midY > mTechTopY && midY < mTechBottomY) {
	        	linePaint.setAntiAlias(false);
	    		linePaint.setColor(Color.GRAY);
	        	PathEffect effect = new DashPathEffect(new float[] {1, 1}, 0);
	        	linePaint.setPathEffect(effect);
	        	Path path = new Path();
	        	path.moveTo(mLineLeft, midY);
	        	path.lineTo(mLineRight, midY);
	        	canvas.drawPath(path, linePaint);
	    	}
			//画线起始位置
	    	start = 0;//Math.max(maxpara-m_iStart, 0);
			int indexS	= start + m_iStart;	//
			int techX	= mLineLeft + 1 + start * (m_iItemWidth+m_iSeparate) + m_iItemWidth/2;
			int techY	= mTechBottomY-1 - (int)((DIF[indexS] - minvalue) * divscale + 0.5);
			Path	path	= new Path();
			if(techY < mTechTopY)
			{
				techY = mTechTopY;
			}else if(techY > mTechBottomY)
			{
				techY = mTechBottomY;
			}
	    	path.moveTo(techX, techY);
			for(int ii=1; ii<m_iShowNum-start; ii++) {
				
				techX	+= m_iItemWidth+m_iSeparate;
				techY	= mTechBottomY-1 - (int)((DIF[indexS+ii] - minvalue) * divscale + 0.5);
				if(techY >= mTechTopY && techY <= mTechBottomY)
				{
					path.lineTo(techX, techY);
				}
			}
	    	linePaint.setAntiAlias(true);
			linePaint.setColor(ColorConstant.ZQ_COLOR_LINE_MACD_A);
	    	PathEffect effect = new CornerPathEffect(3);
	    	linePaint.setPathEffect(effect);
			canvas.drawPath(path, linePaint);

			//
	    	start = 0;//Math.max(maxpara+para3-m_iStart, 0);
			indexS	= start + m_iStart;	//
			techX	= mLineLeft + 1 + start * (m_iItemWidth+m_iSeparate) + m_iItemWidth/2;
			techY	= mTechBottomY-1 - (int)((MACD[indexS] - minvalue) * divscale + 0.5);
			path	= new Path();
			if(techY < mTechTopY)
			{
				techY = mTechTopY;
			}else if(techY > mTechBottomY)
			{
				techY = mTechBottomY;
			}
	    	path.moveTo(techX, techY);
			for(int ii=1; ii<m_iShowNum-start; ii++) {
				
				techX	+= m_iItemWidth+m_iSeparate;
				techY	= mTechBottomY-1 - (int)((MACD[indexS+ii] - minvalue) * divscale + 0.5);
				if(techY >= mTechTopY && techY <= mTechBottomY)
				{
					path.lineTo(techX, techY);
				}
			}
			linePaint.setColor(ColorConstant.ZQ_COLOR_LINE_MACD_B);
			canvas.drawPath(path, linePaint);
			//
	    	linePaint.setAntiAlias(false);
			int tempValue = 0;
			techX	= mLineLeft + 1 + start * (m_iItemWidth+m_iSeparate) + m_iItemWidth/2;
			for(int ii=0; ii<m_iShowNum-start; ii++) {
				
				tempValue = (DIF[indexS+ii]-MACD[indexS+ii])*2;
				techY	= mTechBottomY-1 - (int)((tempValue - minvalue) * divscale + 0.5);
				
				if(tempValue > 0)
					linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
				else
					linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);

				canvas.drawLine(techX, midY, techX, techY, linePaint);
				
				techX	+= m_iItemWidth+m_iSeparate;
			}
		}
		
		/* KDJ 算法
		 * RSV:=(CLOSE-LLV(LOW,N))/(HHV(HIGH,N)-LLV(LOW,N))*100;
		 * K:SMA(RSV,M1,1);
		 * D:SMA(K,M2,1);
		 * J:3*K-2*D;
		 */
		protected void drawKDJ(Canvas canvas)
		{
			if (mKLineDataArray.size()<=0 || mOptionData == null)
				return;
			
			String str = PreferenceEngine.getInstance().getKDJ();
			String[] paras = str.split(",");
			if(paras == null || paras.length != 3)
				return;

			//计算
	    	final int N = STD.StringToInt(paras[0]);
	    	final int M1 = STD.StringToInt(paras[1]);
	    	final int M2 = STD.StringToInt(paras[2]);

	    	long[] RSV, K, D, J;
			RSV = new long[AppConstants.MAXNUM_KLINE];
			K = new long[AppConstants.MAXNUM_KLINE];
			D = new long[AppConstants.MAXNUM_KLINE];
			J = new long[AppConstants.MAXNUM_KLINE];

	    	//计算RSV值
	    	for (int i = 0; i < mKLineNum; i++) {
	    		int high = mKLineDataArray.get(i).high, low = mKLineDataArray.get(i).low;

	    		int t = Math.min(i, N-1);
	    		for (int j = 0; j < t; j++)
	    		{
	    			high = Math.max(high, mKLineDataArray.get(i-j-1).high);
	    			low = Math.min(low, mKLineDataArray.get(i-j-1).low);
	    		}
	    		int value = high - low;
	    		if (value > 0)
	    		{
	    			RSV[i]	= ((mKLineDataArray.get(i).close - low) * 100L * 10000L) / value;
	    		}
	    		else
	    		{
	    			RSV[i]	= 100L * 10000L;//50;
	    		}
	    		
//	    		L.d("KLine", "RSV[" + i + "] = " + RSV[i]);
	    	}
	    	//K
	    	K[0] = RSV[0];
	    	for (int i = 1; i < mKLineNum; i++) {
	    		K[i] = (K[i-1] * (M1-1) + RSV[i] + 1) / M1;
	    	}
	    	//D
	    	D[0] = K[0];
	    	for (int i = 1; i < mKLineNum; i++) {
	    		D[i] = (D[i-1] * (M2-1) + K[i] + 1) / M2;
	    	}
	    	//J
	    	for (int i = 0; i < mKLineNum; i++) {
	    		J[i] = K[i] * 3 - D[i] * 2;
	    	}
			//最大最小值
	    	long	maxvalue = 0;

			L.d("KLineView", "m_iStart = " + m_iStart + ", K.length = " + K.length);
			long minvalue = K[m_iStart];
			for(int i=0; i<m_iShowNum; i++) {
				maxvalue	= Math.max(maxvalue, K[m_iStart+i]);
				maxvalue	= Math.max(maxvalue, D[m_iStart+i]);
				maxvalue	= Math.max(maxvalue, J[m_iStart+i]);
				minvalue	= Math.min(minvalue, K[m_iStart+i]);
				minvalue	= Math.min(minvalue, D[m_iStart+i]);
				minvalue	= Math.min(minvalue, J[m_iStart+i]);
			}
	    	//显示坐标
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.RIGHT);
	    	mPaint.setColor(Color.WHITE);
//	    	int fontH = ViewTools.getFontHeight(mFontSize);
//	    	int tempY = (int)(mTechTopY + mLineSpace - fontH/3);
//	    	String sText = STD.DataToString((maxvalue+minvalue)/2, 2, 0);
//	    	ViewTools.DrawText(canvas, sText, 0, mLineLeft-2, tempY, 0, mPaint);
	    	//显示当前值
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_KDJ_A);
	    	int left = mLineLeft;
//	    	L.d("KLineView", "K[m_iIndex] = " + K[m_iIndex]);
	    	String sText = "KDJ("+N+","+M1+","+M2+")  K: " + STD.DataToString(K[m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_KDJ_B);
			left += (int)mPaint.measureText(sText);
	    	sText = "  D: " + STD.DataToString(D[m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_KDJ_C);
			left += (int)mPaint.measureText(sText);
	    	sText = "  J: " + STD.DataToString(J[m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	
			//画线
	    	double divscale = (double)(mTechBottomY - mTechTopY - 1)/(maxvalue - minvalue);
			int techX	= mLineLeft + 1 + m_iItemWidth/2;
			int techY	= mTechBottomY-1 - (int)((K[m_iStart] - minvalue) * divscale + 0.5);
			Path	path	= new Path();
	    	path.moveTo(techX, techY);
			for(int ii=1; ii<m_iShowNum; ii++) {
				
				techX	+= m_iItemWidth+m_iSeparate;
				techY	= mTechBottomY-1 - (int)((K[m_iStart+ii] - minvalue) * divscale + 0.5);
				path.lineTo(techX, techY);
			}
	    	linePaint.setAntiAlias(true);
			linePaint.setColor(ColorConstant.ZQ_COLOR_LINE_KDJ_A);
	    	PathEffect effect = new CornerPathEffect(3);
	    	linePaint.setPathEffect(effect);
			canvas.drawPath(path, linePaint);
			//
			techX	= mLineLeft + 1 + m_iItemWidth/2;
			techY	= mTechBottomY-1 - (int)((D[m_iStart] - minvalue) * divscale + 0.5);
			path	= new Path();
	    	path.moveTo(techX, techY);
			for(int ii=1; ii<m_iShowNum; ii++) {
				
				techX	+= m_iItemWidth+m_iSeparate;
				techY	= mTechBottomY-1 - (int)((D[m_iStart+ii] - minvalue) * divscale + 0.5);
				path.lineTo(techX, techY);
			}
			linePaint.setColor(ColorConstant.ZQ_COLOR_LINE_KDJ_B);
			canvas.drawPath(path, linePaint);
			//
			techX	= mLineLeft + 1 + m_iItemWidth/2;
			techY	= mTechBottomY-1 - (int)((J[m_iStart] - minvalue) * divscale + 0.5);
			path	= new Path();
	    	path.moveTo(techX, techY);
			for(int ii=1; ii<m_iShowNum; ii++) {
				
				techX	+= m_iItemWidth+m_iSeparate;
				techY	= mTechBottomY-1 - (int)((J[m_iStart+ii] - minvalue) * divscale + 0.5);
				path.lineTo(techX, techY);
			}
			linePaint.setColor(ColorConstant.ZQ_COLOR_LINE_KDJ_C);
			canvas.drawPath(path, linePaint);
		}
		/* RSI
		 * LC:=REF(CLOSE,1);昨收
		 * RSI1:SMA(MAX(CLOSE-LC,0),N1,1)/SMA(ABS(CLOSE-LC),N1,1)*100;
		 * RSI2:SMA(MAX(CLOSE-LC,0),N2,1)/SMA(ABS(CLOSE-LC),N2,1)*100;
		 * RSI3:SMA(MAX(CLOSE-LC,0),N3,1)/SMA(ABS(CLOSE-LC),N3,1)*100;
		 */
		protected void drawRSI(Canvas canvas)
		{
			if (mKLineDataArray.size()<=0 || mOptionData == null)
				return;
			
			String str = PreferenceEngine.getInstance().getRSI();
			String[] paras = str.split(",");
			if(paras == null || paras.length != 2)
				return;

			//计算
	    	int n1 = STD.StringToInt(paras[0]);
	    	int n2 = STD.StringToInt(paras[1]);
	    	//计算
			final int 	para[] = {n1, n2};
	        double[][] fRSI = new double[2][AppConstants.MAXNUM_KLINE];
	        long[] zd = new long[AppConstants.MAXNUM_KLINE];
	        long[] abs_zd = new long[AppConstants.MAXNUM_KLINE];
			long[][] temp	= new long[2][AppConstants.MAXNUM_KLINE];
			//计算涨跌
	    	int last	= mKLineDataArray.get(0).close;
			for (int i = 0; i < mKLineNum; i++)
			{
				int close = mKLineDataArray.get(i).close;
				long value	= (close - last)*10000L;	//由于没有采用浮点计算，为了减少精度损失，这里再放大10000倍
				last		= close;
				zd[i]	= Math.max(value, 0);		//涨跌
				//abs_zd[i]	= Math.abs(value);		//
				abs_zd[i]	= (value < 0) ? -value : value;	
			}
			//计算RSI值
			for (int i = 0; i < 2; i++) {
				System.arraycopy(zd, 0, temp[0], 0, mKLineNum);
				System.arraycopy(abs_zd, 0, temp[1], 0, mKLineNum);
				AnalyseFunc.SMA(temp[0], mKLineNum, para[i], 1);
				AnalyseFunc.SMA(temp[1], mKLineNum, para[i], 1);
				fRSI[i][0]	= 0;
				for (int j = 1; j < mKLineNum; j++) {
					if (temp[1][j] > 0) {
						fRSI[i][j]	= (int)((temp[0][j] * 1000000L + temp[1][j]/2)/ temp[1][j]);
					} else {
						fRSI[i][j]	= fRSI[i][j-1];
					}
				}
			}
			//最大最小值
			double	maxvalue = fRSI[0][m_iStart+1];
			double  minvalue = maxvalue;
			for (int i = 0; i < 2; i++) {
				for (int j = 1; j < m_iShowNum; j++) {
					double value = fRSI[i][m_iStart+j];
					maxvalue	= Math.max(maxvalue, value);
					minvalue	= Math.min(minvalue, value);
				}
			}
			if (maxvalue <= minvalue) {
				maxvalue = minvalue + 10000;
			}
			if (maxvalue < 800000) {
				maxvalue = 800000;
			}
			if (minvalue > 200000) {
				minvalue = 200000;
			}    	
			//显示坐标
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.RIGHT);
	    	mPaint.setColor(Color.WHITE);
//	    	int fontH = ViewTools.getFontHeight(mFontSize);
//	    	int tempY = (int)(mTechTopY + mLineSpace - fontH/3);
//	    	String sText = STD.DataToString((long)(maxvalue+minvalue)/2, 2, 0);
//	    	ViewTools.DrawText(canvas, sText, 0, mLineLeft-2, tempY, 0, mPaint);
	    	//显示当前值
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_RSI_A);
	    	int left = mLineLeft;
	    	String sText = "RSI("+para[0]+","+para[1]+")  RSI1: " + STD.DataToString((long)fRSI[0][m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	mPaint.setColor(ColorConstant.ZQ_COLOR_LINE_RSI_B);
			left += (int)mPaint.measureText(sText);
	    	sText = "  RSI2: " + STD.DataToString((long)fRSI[1][m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
			//画线
			final int color[] = {ColorConstant.ZQ_COLOR_LINE_RSI_A, ColorConstant.ZQ_COLOR_LINE_RSI_B};
	    	double divscale = (double)(mTechBottomY - mTechTopY - 1)/(maxvalue - minvalue);
			for(int i=0; i<2; i++) {	

				int techX	= mLineLeft + 1 + m_iItemWidth/2;
				int techY	= 0;//mTechBottomY-1 - (int)((fRSI[i][m_iStart] - minvalue) * divscale + 0.5);
				Path	path	= new Path();
				boolean bfirst = true;
				for(int ii=0; ii<m_iShowNum; ii++) {
					
					techY	= mTechBottomY-1 - (int)((fRSI[i][m_iStart+ii] - minvalue) * divscale + 0.5);
					if(techY >= mTechTopY && techY <= mTechBottomY) {
						if( bfirst==true ) {
							bfirst = false;
							path.moveTo(techX, techY);
						} 
						else {
							path.lineTo(techX, techY);
						}
					}
					techX	+= m_iItemWidth+m_iSeparate;
				}
		    	linePaint.setAntiAlias(true);
				linePaint.setColor(color[i]);
		    	PathEffect effect = new CornerPathEffect(3);
		    	linePaint.setPathEffect(effect);
				canvas.drawPath(path, linePaint);
			}
		}
		
		/* WR (N,N1) (10,6)
		 * WR1:100*(HHV(HIGH,N)-CLOSE)/(HHV(HIGH,N)-LLV(LOW,N));
		 * WR2:100*(HHV(HIGH,N1)-CLOSE)/(HHV(HIGH,N1)-LLV(LOW,N1));
		 */
		protected void drawWR(Canvas canvas)
		{
			if (mKLineDataArray.size()<=0 || mOptionData == null)
				return;
			
			String str = PreferenceEngine.getInstance().getWR();
			String[] paras = str.split(",");
			if(paras == null || paras.length != 1)
				return;

			//计算
	    	int n1 = STD.StringToInt(paras[0]);
			//计算
			final int 	para[] = {n1};
	        long[][] fWR = new long[1][AppConstants.MAXNUM_KLINE];

			
			// 计算WR值
			for (int k = 0; k < para.length; k++) {
				for (int i = 0; i < mKLineNum; i++) {
					int high = mKLineDataArray.get(i).high, low = mKLineDataArray
							.get(i).low;

					int t = Math.min(i, para[k] - 1);
					for (int j = 0; j < t; j++) {
						high = Math.max(high,
								mKLineDataArray.get(i - j - 1).high);
						low = Math.min(low, mKLineDataArray.get(i - j - 1).low);
					}
					int value = high - low;
					if (value > 0) {
						fWR[k][i] = ((high - mKLineDataArray.get(i).close) * 100L * 10000L)
								/ value;
					} else {
						fWR[k][i] = 100L * 10000L;// 50;
					}
				}
			}
			
			//最大最小值
			double	maxvalue = fWR[0][m_iStart+1];
			double  minvalue = maxvalue;
			for (int i = 0; i < para.length; i++) {
				for (int j = 1; j < m_iShowNum; j++) {
					double value = fWR[i][m_iStart+j];
					maxvalue	= Math.max(maxvalue, value);
					minvalue	= Math.min(minvalue, value);
				}
			}
			if (maxvalue <= minvalue) {
				maxvalue = minvalue + 10000;
			}
			if (maxvalue < 800000) {
				maxvalue = 800000;
			}
			if (minvalue > 200000) {
				minvalue = 200000;
			}    	
			//显示坐标
	    	mPaint.setTextSize(mFontSize);
	    	mPaint.setTextAlign(Paint.Align.RIGHT);
	    	mPaint.setColor(Color.WHITE);
//	    	int fontH = ViewTools.getFontHeight(mFontSize);
//	    	int tempY = (int)(mTechTopY + mLineSpace - fontH/3);
//	    	String sText = STD.DataToString((long)(maxvalue+minvalue)/2, 2, 0);
//	    	ViewTools.DrawText(canvas, sText, 0, mLineLeft-2, tempY, 0, mPaint);
	    	//显示当前值
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.COLOR_TECH0);
	    	int left = mLineLeft;
	    	String sText = "WR("+para[0]+")  WR1: " + STD.DataToString((long)fWR[0][m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	//mPaint.setColor(ColorConstant.COLOR_TECH1);
			//left += (int)mPaint.measureText(sText);
	    	//sText = "  WR2: " + STD.DataToString((long)fWR[1][m_iIndex], 2, 0);
			//ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY, 0, mPaint);
			//画线
			final int color[] = {ColorConstant.COLOR_TECH0, ColorConstant.COLOR_TECH1};
	    	double divscale = (double)(mTechBottomY - mTechTopY - 1)/(maxvalue - minvalue);
			for(int i=0; i<para.length; i++) {	

				int techX	= mLineLeft + 1 + m_iItemWidth/2;
				int techY	= 0;//mTechBottomY-1 - (int)((fWR[i][m_iStart] - minvalue) * divscale + 0.5);
				Path	path	= new Path();
				boolean bfirst = true;
				for(int ii=0; ii<m_iShowNum; ii++) {
					
					techY	= mTechBottomY-1 - (int)((fWR[i][m_iStart+ii] - minvalue) * divscale + 0.5);
					if(techY >= mTechTopY && techY <= mTechBottomY) {
						if( bfirst==true ) {
							bfirst = false;
							path.moveTo(techX, techY);
						} 
						else {
							path.lineTo(techX, techY);
						}
					}
					techX	+= m_iItemWidth+m_iSeparate;
				}
		    	linePaint.setAntiAlias(true);
				linePaint.setColor(color[i]);
		    	PathEffect effect = new CornerPathEffect(3);
		    	linePaint.setPathEffect(effect);
				canvas.drawPath(path, linePaint);
			}
		}
		
		/* BIAS 乘离率 (N1,N2,N3) (6,12,24)
		 * BIAS1 :(CLOSE-MA(CLOSE,N1))/MA(CLOSE,N1)*100;
		 * BIAS2 :(CLOSE-MA(CLOSE,N2))/MA(CLOSE,N2)*100;
		 * BIAS3 :(CLOSE-MA(CLOSE,N3))/MA(CLOSE,N3)*100;
		 */
		protected void drawBIAS(Canvas canvas)
		{
			if (mKLineDataArray.size()<=0 || mOptionData == null)
				return;
			
			String str = PreferenceEngine.getInstance().getBIAS();
			String[] paras = str.split(",");
			if(paras == null || paras.length != 3)
				return;

			//计算
	    	int n1 = STD.StringToInt(paras[0]);
	    	int n2 = STD.StringToInt(paras[1]);
	    	int n3 = STD.StringToInt(paras[2]);
	    	//计算
			final int 	para[] = {n1, n2, n3};
	        double[][] fBIAS = new double[3][AppConstants.MAXNUM_KLINE];
	        long[] zd = new long[AppConstants.MAXNUM_KLINE];
			long[][] temp	= new long[1][AppConstants.MAXNUM_KLINE];
			//计算涨跌
			for (int i = 0; i < mKLineNum; i++)
			{
				int close = mKLineDataArray.get(i).close;
				long value	= close * 10000L;	//由于没有采用浮点计算，为了减少精度损失，这里再放大10000倍
				zd[i]	= Math.max(value, 0);		//涨跌
			}
			//计算BIAS值
			for (int i = 0; i < 3; i++) {
				System.arraycopy(zd, 0, temp[0], 0, mKLineNum);
				AnalyseFunc.MA(temp[0], mKLineNum, para[i]);
				fBIAS[i][0]	= 0;
				for (int j = 1; j < mKLineNum; j++) {
					if (temp[0][j] > 0) {
						fBIAS[i][j]	= ((mKLineDataArray.get(j).close * 10000L - temp[0][j]) * 100 * 10000L)/ temp[0][j];
					} else {
						fBIAS[i][j]	= fBIAS[i][j-1];
					}
				}
			}
			//最大最小值
			double	maxvalue = fBIAS[0][m_iStart+1];
			double  minvalue = maxvalue;
			for (int i = 0; i < 3; i++) {
				for (int j = 1; j < m_iShowNum; j++) {
					double value = fBIAS[i][m_iStart+j];
					maxvalue	= Math.max(maxvalue, value);
					minvalue	= Math.min(minvalue, value);
				}
			}
			if (maxvalue <= minvalue) {
				maxvalue = minvalue + 10000;
			} 	
			//显示坐标
	    	mPaint.setTextSize(mFontSize);

	    	String sText = STD.DataToString((long)(maxvalue+minvalue)/2, 2, 0);
	    	//显示当前值
	    	mPaint.setTextAlign(Paint.Align.LEFT);
	    	mPaint.setColor(ColorConstant.COLOR_TECH0);
	    	int left = mLineLeft;
			sText = "BIAS("+para[0]+","+para[1]+","+para[2]+")  BIAS1: " + STD.DataToString((long)fBIAS[0][m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
	    	mPaint.setColor(ColorConstant.COLOR_TECH1);
			left += (int)mPaint.measureText(sText);
	    	sText = "  BIAS2: " + STD.DataToString((long)fBIAS[1][m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
			
			mPaint.setColor(ColorConstant.COLOR_TECH2);
			left += (int)mPaint.measureText(sText);
	    	sText = "  BIAS3: " + STD.DataToString((long)fBIAS[2][m_iIndex], 2, 0);
			ViewTools.DrawText(canvas, sText, left, mLineRight, mKlineBottomY+mFontH, 0, mPaint);
			//画线
			final int color[] = {ColorConstant.COLOR_TECH0, ColorConstant.COLOR_TECH1, ColorConstant.COLOR_TECH2};
	    	double divscale = (double)(mTechBottomY - mTechTopY - 1)/(maxvalue - minvalue);
			for(int i=0; i<3; i++) {	

				int techX	= mLineLeft + 1 + m_iItemWidth/2;
				int techY	= 0;//mTechBottomY-1 - (int)((fBIAS[i][m_iStart] - minvalue) * divscale + 0.5);
				Path	path	= new Path();
				boolean bfirst = true;
				for(int ii=0; ii<m_iShowNum; ii++) {
					
					techY	= mTechBottomY-1 - (int)((fBIAS[i][m_iStart+ii] - minvalue) * divscale + 0.5);
					if(techY >= mTechTopY && techY <= mTechBottomY) {
						if( bfirst==true ) {
							bfirst = false;
							path.moveTo(techX, techY);
						} 
						else {
							path.lineTo(techX, techY);
						}
					}
					techX	+= m_iItemWidth+m_iSeparate;
				}
		    	linePaint.setAntiAlias(true);
				linePaint.setColor(color[i]);
		    	PathEffect effect = new CornerPathEffect(3);
		    	linePaint.setPathEffect(effect);
				canvas.drawPath(path, linePaint);
			}
		}

		// calc the display numbers
		private void getShowNum() {
			if (mKLineNum <= 0) return;

			m_iScreenNum = (mLineRight - mLineLeft - 2) / (m_iItemWidth + m_iSeparate);

			if (m_iScreenNum <= 0) return;

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

			int index = (x - mLineLeft - 1) / (m_iItemWidth + m_iSeparate) + m_iStart;

			if (index < m_iStart) {
				index = m_iStart;
			} else if (index >= m_iStart + m_iShowNum) {
				index = m_iStart + m_iShowNum - 1;
			}

			return index;
		}
		
		public void onLongPressLine(MotionEvent event) {
			
			L.d(TAG, "------------------onLongPressLine------------------" + event.getAction());
			int x = (int) event.getX();
			int y = (int) event.getY();
			//y = y - mKLineTop;
			m_iYPos = y;

			//
			{
				//
				if (m_bScrolling) {
					m_bScrolling = false;
					return;
				}

//				// 判断popupinfo弹出位置
//				int halfwidth = (mClientRect.right - mClientRect.left) / 2;
//				int oldflat = flag_position_showing;
//				if (x <= halfwidth) {
//					flag_position_showing = POSITION_RIGHT;
//				} else {
//					flag_position_showing = POSITION_LEFT;
//				}

				if ((x > mLineLeft) && (x < mLineRight)) { // && (y > mKlineTopY) && (y < mKlineBottomY)) 

					m_iIndex = getCurIndexByX(x);

//					if (oldflat != flag_position_showing) {
//						DismissInfo();
//					}

					ShowPopTitle();
					invalidate();
				} 
				else if((x > mLineLeft) && (x < mLineRight)
		    			&& (y > mTechTopY) && (y < mTechBottomY))  {
		    		//
		    		mTechType ++;
		    		if(mTechType > TECH_RSI)
		    			mTechType = TECH_VOLUME;
		    		
		    		if(mRightView != null)
		    		{
		    			mRightView.setTechType(mTechType);
		    		}
		    		//
		    		invalidate();
		    	}
				else {
					DismissTitle(false);
					invalidate();
				}
			}
		}

		//
		public void onTouchLine(MotionEvent event) {
			L.d(TAG, "------------------onTouchLine------------------" + event.getAction());
			
			int x = (int) event.getX();
			int y = (int) event.getY();
			//y = y - mKLineTop;
			if (event.getAction() == MotionEvent.ACTION_UP) {

				if (m_bScrolling) {
					m_bScrolling = false;
					L.d(TAG, "------------------onTouchLine------------------" + m_bScrolling);
					
					return;
				}

				// 判断popupinfo弹出位置
//				int halfwidth = (mClientRect.right - mClientRect.left) / 2;
//				int oldflat = flag_position_showing;
//				if (x <= halfwidth) {
//					flag_position_showing = POSITION_RIGHT;
//				} else {
//					flag_position_showing = POSITION_LEFT;
//				}

				if ((x > mLineLeft) && (x < mLineRight) && (y > mKlineTopY) && (y < mKlineBottomY)) {
		    		
					if(mPopinfoFlag)
					{
						DismissTitle(false);
					}else
					{
						mKLineType ++;
			    		if(mKLineType > KLINE_BOLL)
			    			mKLineType = KLINE_MA;
					}
					L.d(TAG, "------------------onTouchLine------------------" + mKLineType);
					
					invalidate();
				} else if((x > mLineLeft) && (x < mLineRight)&& (y > mTechTopY) && (y < mTechBottomY))  {
		    		
					if(mPopinfoFlag){
						DismissTitle(false);
					}else
					{
						mTechType ++;
						if(mTechType > TECH_RSI)
							mTechType = TECH_VOLUME;
						
						if(mRightView != null)
			    		{
			    			mRightView.setTechType(mTechType);
			    		}
					}
		    		
		    		invalidate();
		    	}
				else {
					DismissTitle(false);
					invalidate();
				}
			}
		}

		private int getXByIndex(int index) {
			return mLineLeft + m_iSeparate + (index - m_iStart) * (m_iItemWidth + m_iSeparate) + m_iItemWidth / 2;
		}

		//
		public void onMoveLine(MotionEvent event) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			//y = y - mKLineTop;
			m_iYPos = y;

			//int midX = (mLineLeft + mLineRight) / 2;
			//int oldX = getXByIndex(m_iIndex);
			//
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				// 判断popupinfo弹出位置
//				int halfwidth = (mClientRect.right - mClientRect.left) / 2;
//				int oldflat = flag_position_showing;
//				if (x <= halfwidth) {
//					flag_position_showing = POSITION_RIGHT;
//				} else {
//					flag_position_showing = POSITION_LEFT;
//				}

				if ((x > mLeft) && (x < mRight) ) {//&& (y > mKlineTopY) && (y < mTechBottomY)

					m_iIndex = getCurIndexByX(x);
					//
					//if ((oldX < midX && x >= midX) || (oldX >= midX && x < midX)) {
					///	DismissTitle(false);
					//}

//					if (oldflat != flag_position_showing) {
//						DismissInfo();
//					}

					ShowPopTitle();
					invalidate();
				}  
				else {
					DismissTitle(false);
					invalidate();
				}
			}
		}

		//
		public void onScrollLine(float y, float dx, float dy) {
			//y = y - mKLineTop;
			
			if ((y < mKlineTopY) || (y > mKlineBottomY)) return;

			int width = m_iItemWidth + m_iSeparate;

			int abs_x = Math.abs((int) dx);
			// 左右移动K线
			if (abs_x > width) {
				//m_bScrolling = true;

				DismissTitle(false);

				int offset = abs_x / width;
				if (dx < 0) {
					m_iStart -= offset;
				} else {
					m_iStart += offset;
				}

				if (m_iStart > mKLineNum - m_iScreenNum) m_iStart = mKLineNum - m_iScreenNum;

				if (m_iStart < 0) m_iStart = 0;

				getShowNum();
				invalidate();
			}
		}

		int step = (int) ((mScreenSize.widthPixels / MAX_KLINE_WIDTH)*1) ;
		// K线缩放
		public void onScaleLine(float dy) {

			DismissTitle(false);
			m_bScrolling = true;
			
			
			//
			if (dy > 0 ) {
				{
					//if(m_iItemWidth == MAX_KLINE_WIDTH)
					//	return;
					
					m_iItemWidth =  (m_iItemWidth_Base + ((int)(dy))/step);
					if(m_iItemWidth > MAX_KLINE_WIDTH)
						m_iItemWidth = MAX_KLINE_WIDTH;
				}
			} else {//dy<=1
				{
					//if(m_iItemWidth == 1)
					//	return;
					
					m_iItemWidth = (m_iItemWidth_Base + ((int)(dy))/step);
					if(m_iItemWidth < 1)
						m_iItemWidth = 1;
				}
				
			}

			//
			getShowNum();
			invalidate();
		}
		
		public void onZoomStop()
		{
			m_iItemWidth_Base = m_iItemWidth;
			m_bScrolling = false;
		}
	}
}
