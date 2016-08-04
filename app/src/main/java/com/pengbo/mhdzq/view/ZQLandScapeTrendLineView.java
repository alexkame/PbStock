package com.pengbo.mhdzq.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;


/**
 *  横屏的走势 
 * //对于走势均线的问题，目前的处理规则如下：
 * 对于当日走势的请求里的均线数据，客户端不再自己计算处理，直接使用服务器返回均价字段
 * 对于推送请求里返回的均价，有两种不同处理
 * 如果当前是指数，直接使用均价字段
 * 如果非指数，客户端采用金额/量的方式计算
 * 
 * 在画均线的时候，如果均价所有数据为零，则不画均价线；
 * @author pobo
 * @date   2015-11-25 上午10:16:31
 * @className ZQLandScapeTrendLineView.java
 * @verson 1.0.0
 */

public class ZQLandScapeTrendLineView extends FrameLayout {

	public static final String TAG = ZQLandScapeTrendLineView.class.getSimpleName();

	public MyApp mMyApp;

	private TagLocalStockData mOptionData;
	private ArrayList<TagLocalTrendData> mTrendDataArray;
	private ArrayList<TagLocalDealData> mDealDataArray;
	private int mTrandNum = 241;//默认241分钟

	private TrendView mTrendView;
	private ZQ_Ctrl_Trend_RightPanel mRightView;
	private boolean mbShowRight = true;
	private DisplayMetrics mScreenSize;
	private int mTrendLineTop;//trendline在父窗口中y坐标
//	private int flag_position_showing = 0; // popinfo显示在右边还是左边
//	private final static int POSITION_LEFT = 1;
//	private final static int POSITION_RIGHT = 2;
	private	int	m_iIndex;//走势线索引
	private int m_iYPos;//走势线询价模式Y坐标

	public boolean	mPopinfoFlag;
	private Context mContext;
	
	public ZQLandScapeTrendLineView(Context context, boolean bShowRight) {
		super(context);
		mbShowRight = bShowRight;
		initData(context);
		initView(context);
	}

	private void initData(Context context) {
		mMyApp = (MyApp) context.getApplicationContext();
		mScreenSize = ViewTools.getScreenSize(context);

		mDealDataArray = mMyApp.getDealDataArray();
		mTrendDataArray = mMyApp.getLandTrendDataArray();
	}

	private void initView(Context context) {
		FrameLayout frame = new FrameLayout(context);

		LinearLayout layouthorn = new LinearLayout(context);
		layouthorn.setOrientation(LinearLayout.HORIZONTAL);
		// trend line layout

		LinearLayout layouthorn1 = new LinearLayout(context);// trend view layout include title
		layouthorn1.setOrientation(LinearLayout.VERTICAL);

		mTrendView = new TrendView(context);
		layouthorn1.addView(mTrendView);
		// right panel for five price and detail
		if (mbShowRight) {
			LayoutParams lp1 = new LayoutParams(mScreenSize.widthPixels * 4 /5, LayoutParams.MATCH_PARENT);
			layouthorn.addView(layouthorn1, lp1);

			LayoutParams lp2 = new LayoutParams(mScreenSize.widthPixels * 1 /5, LayoutParams.MATCH_PARENT);
			mRightView = new ZQ_Ctrl_Trend_RightPanel(context);
			layouthorn.addView(mRightView, lp2);
		} else {

			LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layouthorn.addView(layouthorn1, lp1);
		}

		frame.addView(layouthorn);
		addView(frame);
	}
	
	
	public void setTrendLineTop(int top)
	{
		this.mTrendLineTop = top;
	}

	public void updateData(TagLocalStockData optionData, TagLocalStockData stockData) {
		this.mOptionData = optionData;
		if(mOptionData != null)
		{
			mTrandNum = 0;
			for(int i = 0; i < mOptionData.TradeFields; i++)
			{
				int minute = STD.getMinutes(mOptionData.Start[i], mOptionData.End[i]);
				mTrandNum += minute;
			}
		}
		if(mTrandNum <= 0)
		{
			mTrandNum = 241;
		}else
		{
			mTrandNum += 1;
		}
	}

	public void onTouchLine(MotionEvent event) {
		mTrendView.onTouchLine(event);
	}
	
	public void onLongPressLine(MotionEvent event) {
		mTrendView.onLongPressLine(event);
	}

	//
	public void onMoveLine(MotionEvent event) {
		mTrendView.onMoveLine(event);
	}
	

	// update view
	public void updateAllView() {
		if (mTrendView != null) {
			mTrendView.updateAllView();
		}

		if (mbShowRight && mRightView != null) {
			mRightView.updateData(mOptionData, mDealDataArray, mScreenSize.heightPixels / 3);
		}
		
		if (mPopinfoFlag == true) {
			PopupInfo();
		}
	}

	public void DismissInfo(boolean bNeedRedraw) {

		mPopinfoFlag = false;
		if (bNeedRedraw && mTrendView != null) {
			mTrendView.invalidate();
		}
	}
	  
	public int getCurrentSelectIndex()
	{
		return m_iIndex;
	}
	
	public void PopupInfo() {

		if (m_iIndex < 0 || m_iIndex >= mTrendDataArray.size()) {
			L.d(TAG, "PopupInfo--->DismissInfo");
			DismissInfo(false);
			return;
		}

		if(mOptionData == null)
		{
			DismissInfo(false);
			return;
		}

		mPopinfoFlag = true;
	}

	public void setShowRight(boolean bShow) {
		this.mbShowRight = bShow;
	}

	// View for draw trend line
	public class TrendView extends View {

		Rect mClientRect; // display area
		Paint mPaint;
		Paint linePaint;
		Paint linePaint_stock;

		private int mFontH = 0;
		private float mFontSize = 10; // 坐标字体大小
		
		private int mLeft = 0;
		private int mRight = 0;
		private int mStockPanelY = 0;

		private int mLineLeft = 0;
		private int mLineRight = 0;
		private int mTlineTopY = 0; // trend line y top
		private int mTlineMidY = 0; // trend line y middle
		private int mTlineBottomY = 0; // trend line y bottom
		private double mLineSpace = 0.0; // trend line y space for each row
		private int mMinStep = 0;//trend line x minute step (just used when tradeFields==1)

		private int mTechTopY = 0;
		private int mTechBottomY = 0;

		private double mXScales = 0.0; // x scale(for time)
		private double mYScales = 0.0; // y scale(for price)

		private int mPriceOffset; // y option price offset of each row
		private int mDataNum; // trend data number

		public TrendView(Context context) {
			super(context);
			mContext=context;
			mClientRect = new Rect();
			mPaint = new Paint();
			linePaint = new Paint();
			linePaint_stock = new Paint();

			mFontSize = getResources().getDimension(R.dimen.font_screen_F);
			mFontH = ViewTools.getFontHeight(mFontSize);
		}
		
		public void resetParam() {
			m_iIndex = 0;
		}

		// init some parameters for trend line draw
		private void drawInit() {
			mLeft = mClientRect.left;
			mRight = mClientRect.right;
			
			mPaint.setTextSize(mFontSize);
			int width = (int) mPaint.measureText("10000.000");
			

			mTechBottomY = mClientRect.height() - 5;
			mStockPanelY = 0;

			L.d(TAG, "drawInit--->mClientRect--->top = " + mClientRect.top + ", bottom = " + mClientRect.bottom);

			mTlineTopY = mStockPanelY + 5;
			mLineSpace = (double) (mTechBottomY - mTlineTopY - mFontH) / 6.0;
			mTechTopY = (int) (mTechBottomY - mLineSpace * 2);

			mTlineMidY = (int) (mTlineTopY + mLineSpace * 2);
			mTlineBottomY = mTechTopY - mFontH;

			mPaint.setTextSize(mFontSize);
			mLineLeft = mLeft + 5 + width;
			mPaint.setTextSize(mFontSize);
			mLineRight = mRight - 5 - width;

			// calc x scale for time
			int trendnum = mTrandNum;// now using 241 minutes for temporary, will change this value base on the data

			mXScales = (double) (mLineRight - mLineLeft) / trendnum;

			mPriceOffset = 0;
			mDataNum = mTrendDataArray.size();
			if (mDataNum <= 0) return;

			// calc y scale for option
			if (mOptionData == null) {
				return;
			}
			int maxprice = (mOptionData.HQData.nHighPrice != 0) ? mOptionData.HQData.nHighPrice : mOptionData.HQData.getnLastClear();
			int minprice = (mOptionData.HQData.nLowPrice != 0) ? mOptionData.HQData.nLowPrice : mOptionData.HQData.getnLastClear();
			
			
		
			for (int i = 0; i < mDataNum; i++) {
				TagLocalTrendData trendData = mTrendDataArray.get(i);
				
				if (trendData.now != 0) {
					maxprice = Math.max(trendData.now, maxprice);
					minprice = Math.min(trendData.now, minprice);
				}
				
				if (trendData.average!=0)
	        	{
	        		maxprice	= Math.max(trendData.average, maxprice);
	        		minprice	= Math.min(trendData.average, minprice);
	        	}
				
				
			}

			if (maxprice > 0) {
				maxprice = maxprice - mOptionData.HQData.getnLastClear();
				if (maxprice < 0) {
					maxprice = -maxprice;
				}
			}
			if (minprice > 0) {
				minprice = mOptionData.HQData.getnLastClear() - minprice;
				if (minprice < 0) {
					minprice = -minprice;
				}
			}

			int maxPriceOffset = Math.max(minprice, maxprice);
			if (maxPriceOffset == 0) {
				int times[] = {
						10000, 1000, 100, 10, 1
				};
				if (mOptionData.PriceDecimal >= 0 && mOptionData.PriceDecimal < times.length) {
					maxPriceOffset = 4 * times[mOptionData.PriceDecimal];
				}
			}

			if (maxPriceOffset > 0) {
				mYScales = (double) (mTlineBottomY - mTlineMidY) / maxPriceOffset;
			}

			mPriceOffset = maxPriceOffset / 2;// Price has 2 rows for each side
		}

		// update view
		public void updateAllView() {
			// init some parameters for draw trend line
			drawInit();
			this.invalidate();
		}

		private void drawNow(Canvas canvas) {
			L.i("ScreenDetailActivity", "Received push data1 drawNow");
			updateTrend(canvas);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			//
			super.onLayout(changed, left, top, right, bottom);

			if (changed) {
				mClientRect.set(left, top, right, bottom);
				L.d(TAG, "onLayout--->top = " + mClientRect.top + ", bottom = " + mClientRect.bottom + ", left = " + mClientRect.left + ", right = " + mClientRect.right);
				drawInit();
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			drawNow(canvas);
		}

		// update trend area
		public void updateTrend(Canvas canvas) {
			// draw background such as coordinate line
			if (mOptionData == null) {
				return;
			}
			drawBackground(canvas);
			drawTrendLine(canvas);
			drawVolume(canvas);
			if(!DataTools.isStockZQ(mOptionData))
			{
				drawCCL(canvas);
			}
			drawRule(canvas);
			
		}
		
		
		// 显示标尺
	protected void drawRule(Canvas canvas) {
		if (mTrendDataArray.size() <= 0) return;

		if (mPopinfoFlag == true) {
			//
			int indexX = (int) (mLineLeft + 1 + mXScales * m_iIndex);
			//
			linePaint.setAntiAlias(true);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setColor(ColorConstant.ZQ_COLOR_TIME);
			PathEffect effect = new DashPathEffect(new float[] {
					3, 2
			}, 0);
			linePaint.setPathEffect(effect);
			Path path = new Path();
			path.moveTo(indexX, mTlineTopY);
			path.lineTo(indexX, mTlineBottomY);
			canvas.drawPath(path, linePaint);
			path.moveTo(indexX, mTechTopY);
			path.lineTo(indexX, mTechBottomY);
			canvas.drawPath(path, linePaint);
			
			//draw 横轴查价线
			int indexY = m_iYPos;
			//if (indexY >= mTlineTopY && indexY <= mTlineBottomY)
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

				
				int nPrice = mTrendDataArray.get(m_iIndex).now;
						//(int) (mOptionData.HQData.getnLastClear() - mPriceOffset * (m_iYPos - mTlineMidY)/mLineSpace);
				strprice = ViewTools.getStringByPrice(nPrice, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
				
				int nWidth = (int) mPaint.measureText(strprice) + 10;
				int nHeight = mFontH + 10;

				RectF r = new RectF();
				
				//修改左边的差价在边框外面
				if ((indexY - nHeight/2) < mTlineTopY)
				{
					r.set(mLineLeft - nWidth, mTlineTopY, mLineLeft, mTlineTopY + nHeight);
					
				}else
				{
					r.set(mLineLeft- nWidth, indexY - nHeight/2, mLineLeft , indexY + nHeight/2);
				}
				canvas.drawRoundRect(r, 5, 5, mPaint);

				mPaint.setColor(Color.WHITE);
				mPaint.setTextSize(mFontSize);
				ViewTools.DrawText(canvas, strprice, (int) r.left, (int) r.right, (int) r.top, (int) r.bottom, mPaint);
				
				// 涨跌幅显示
				mPaint.setColor(ColorConstant.ZQ_COLOR_TIME);
				mPaint.setAlpha(228);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setTextSize(mFontSize);
				mPaint.setTextAlign(Paint.Align.CENTER);
				
				String strzdf = "----";
				strzdf = ViewTools.getZDF(nPrice - mOptionData.HQData.getnLastClear(), mOptionData.HQData.getnLastClear(), nPrice, true, true);
				nWidth = (int) mPaint.measureText(strzdf) + 10;

				//修改右边的涨跌幅差价在边框外面
				
				if ((indexY - nHeight/2 )< mTlineTopY)
				{
					r.set(mLineRight, mTlineTopY, mLineRight + nWidth, mTlineTopY + nHeight);
				}else
				{
					r.set(mLineRight , indexY - nHeight/2, mLineRight + nWidth, indexY + nHeight/2);
				}
				
			
				canvas.drawRoundRect(r, 5, 5, mPaint);
				
				mPaint.setColor(Color.WHITE);
				mPaint.setTextSize(mFontSize);
				ViewTools.DrawText(canvas, strzdf, (int) r.left, (int) r.right, (int) r.top, (int) r.bottom, mPaint);
				
			}
			
			// 时间显示
			mPaint.setColor(ColorConstant.ZQ_COLOR_TIME);
			mPaint.setAlpha(228);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setTextSize(mFontSize);
			mPaint.setTextAlign(Paint.Align.CENTER);

			int width = (int) mPaint.measureText("0101 00:23") + 10;
			RectF r = new RectF();
			if (indexX + width / 2 < mRight)
				r.set(indexX - width / 2, mTlineBottomY + 1, indexX + width / 2, mTechTopY - 1);
			else
				r.set(mRight - 1 - width, mTlineBottomY + 1, mRight - 1, mTechTopY - 1);
			canvas.drawRoundRect(r, 5, 5, mPaint);

			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(mFontSize);

			String date=STD.getDateSringyyyymmdd(mTrendDataArray.get(m_iIndex).date);
			String sub_date=date;
			if(date.length()>=4){
				sub_date=date.substring(date.length()-4, date.length());
			}
			String time2 = STD.getTimeSringhhmm(mTrendDataArray.get(m_iIndex).time);
				
			ViewTools.DrawText(canvas, sub_date+" "+time2, (int) r.left, (int) r.right, (int) r.top, (int) r.bottom, mPaint);
		
        }
	}

		// draw background such as coordinate line
		protected void drawBackground(Canvas canvas) {
			//
			//画边框 
			mPaint.setAntiAlias(true);
			mPaint.setColor(ColorConstant.ZQ_COLOR_KLINE_BOUND);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(2.0f);
			mPaint.setPathEffect(null);
			mPaint.setShader(null);
			
			int tempY = mTlineMidY;
			//  trend view  最上面一条 			
			canvas.drawLine(mLineLeft, mTlineTopY, mLineRight, mTlineTopY, mPaint);

			//  trend view  最下面一条 
			canvas.drawLine(mLineLeft, mTlineBottomY, mLineRight, mTlineBottomY, mPaint);

			// boundary of left&right

			canvas.drawLine(mLineLeft, mTlineTopY, mLineLeft, mTlineBottomY, mPaint);
			canvas.drawLine(mLineRight, mTlineTopY, mLineRight, mTlineBottomY, mPaint);

			mPaint.setColor(ColorConstant.ZQ_COLOR_KLINE_BOUND);

			// separation line(row) volume
			tempY = mTechTopY;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);

			tempY += mLineSpace;
			tempY += mLineSpace;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);
			// boundary of left&right

			canvas.drawLine(mLineLeft, mTechTopY, mLineLeft, mTechBottomY, mPaint);
			canvas.drawLine(mLineRight, mTechTopY, mLineRight, mTechBottomY, mPaint);

		}

		// draw trend line(price)
		protected void drawTrendLine(Canvas canvas) {

			if (null == mOptionData) {
				L.e(TAG, "mOptionData == null");
				return;
			}
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			// x coordinate for time
			int tradenum = mTrandNum;// now using 241 minutes for default. In future,it will be changed by the data
			if (tradenum > 1) {
				int tradeFields = mOptionData.TradeFields;
				String temp;
				float left = mLineLeft;
				for (int i = 0; i < tradeFields; i++) {
					mPaint.setTextSize(mFontSize);
					mPaint.setColor(Color.GRAY);
					mPaint.setStrokeWidth(1.0f);
					mPaint.setTextAlign(Paint.Align.CENTER);
					temp = STD.getTimeSringhhmm(mOptionData.Start[i]);
					
					int minute = STD.getMinutes(mOptionData.Start[i], mOptionData.End[i]);
					
					float t_width = mPaint.measureText(temp);
					if (i == 0)
					{
						left += mPaint.measureText("/");
					}else
					{
						left += (t_width) + mPaint.measureText("/");
					}
					
					ViewTools.DrawText(canvas, temp, (int) left, (int) (left + t_width), mTlineBottomY, 0, mPaint);

					temp = STD.getTimeSringhhmm(mOptionData.End[i]);
					if (i != tradeFields - 1) {
						temp += "/";
					}
					t_width = mPaint.measureText(temp);
					//xspace += (int) (minute*this.mXScales);(mLineRight - mLineLeft) / tradeFields
					
					left = left +  (float)(minute*this.mXScales) - t_width - 1;
					ViewTools.DrawText(canvas, temp, (int) left, (int) (left + t_width), mTlineBottomY, 0, mPaint);
				}
				
				if(tradeFields == 1 && mMinStep > 0)
				{
					left = mLineLeft;
					for (int i = 1; i <= 2; i++) {
						mPaint.setTextSize(mFontSize);
						mPaint.setColor(Color.GRAY);
						mPaint.setStrokeWidth(1.0f);
						mPaint.setTextAlign(Paint.Align.CENTER);
						int time = STD.getTimeWithAdd(mOptionData.Start[0], mMinStep*i);
						temp = STD.getTimeSringhhmm(time);
						
						int minute = mMinStep;
						
						float t_width = mPaint.measureText(temp);
						
						left = left +  (float)(minute*this.mXScales) - 1;
						ViewTools.DrawText(canvas, temp, (int) (left - t_width/2), (int) (left + t_width/2), mTlineBottomY, 0, mPaint);
					}
				}
			}
			
			//---------------------------------------------------------------

			mDataNum = mTrendDataArray.size();
			if (mDataNum <= 0) {
				L.e(TAG, "mDataNum <= 0");
				return;
			}

			
			double last = mLineLeft;//左边
			Path path_now = new Path();
			Path path_now_filled = new Path();
			int oldPrice = mOptionData.HQData.getnLastClear();  //作天结算价 
			int lastPrice = oldPrice;
			for (int i = 0; i < mDataNum; i++) {
				double x = last;//  X坐标 

				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}

				int now = 0;
				if (trendData.now == 0) {
					now = lastPrice;
				} else {
					now = trendData.now;
				}

				double y_now = (double) (mTlineMidY - (now - oldPrice) * mYScales);

				if (i == 0) {
					path_now.moveTo((float) x, (float) y_now);
					path_now_filled.moveTo(mLineLeft, mTlineMidY);
				} else {
					x += mXScales;
					path_now.lineTo((float) x, (float) y_now);
					path_now_filled.lineTo((float) x, (float) y_now);
				}
				last = x;
				lastPrice = now;
			}
			
			path_now_filled.lineTo((float)last, mTlineMidY);//mTlineBottomY
			
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(2.5f);
			linePaint.setColor(ColorConstant.ZQ_COLOR_TREND);//---------------yjh 分时的颜色 
			canvas.drawPath(path_now, linePaint);
			
			
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.FILL);
			linePaint.setStrokeWidth(2.5f);
			linePaint.setColor(ColorConstant.ZQ_COLOR_TREND_FILLED);//---------------yjh 分时填充的颜色 	
			canvas.drawPath(path_now_filled, linePaint);
			
			
			//画中间的 虚点 
			mPaint.setColor(ColorConstant.ZQ_DATA_XUDIAN);
			mPaint.setStrokeWidth(0.8f);
			mPaint.setStyle(Paint.Style.STROKE);
			Path path=new Path();
			path.moveTo(mLineLeft, mTlineMidY);
			path.lineTo(mLineRight, mTlineMidY);
			
			PathEffect effects = new DashPathEffect(new float[] {
					5, 5, 5, 5
			}, 1);
			mPaint.setPathEffect(effects);
			canvas.drawPath(path, mPaint);
			
			// Y line for time
			int tradeFields = mOptionData.TradeFields; // 时间段
			if (tradeFields <= 0) {
				tradeFields = 1;
			}
			int xspace = 0;// (mLineRight - mLineLeft) / tradeFields;
			for (int i = 1; i < tradeFields; i++) {
				int minute = STD.getMinutes(mOptionData.Start[i - 1],
						mOptionData.End[i - 1]);
				xspace += (int) (minute * this.mXScales);
				
				path.moveTo(mLineLeft + xspace, mTlineTopY);
				path.lineTo(mLineLeft + xspace, mTlineBottomY);
				canvas.drawPath(path, mPaint);
				
				path.moveTo(mLineLeft + xspace, mTechTopY);
				path.lineTo(mLineLeft + xspace, mTechBottomY);
				canvas.drawPath(path, mPaint);
			}
			if (tradeFields == 1) {
				int minute = STD.getMinutes(mOptionData.Start[0],
						mOptionData.End[0]);
				int minStep = minute / 3;
				if (minStep % 30 != 0) {
					minStep -= (minStep % 30);
				}
				mMinStep = minStep;
				xspace = 0;
				for (int i = 0; i < 2; i++) {
					xspace += (int) (minStep * this.mXScales);
					
					path.moveTo(mLineLeft + xspace, mTlineTopY);
					path.lineTo(mLineLeft + xspace, mTlineBottomY);
					canvas.drawPath(path, mPaint);
					
					path.moveTo(mLineLeft + xspace, mTechTopY);
					path.lineTo(mLineLeft + xspace, mTechBottomY);
					canvas.drawPath(path, mPaint);
				}
			}
			
			mDataNum = mTrendDataArray.size();
			if (mDataNum <= 0) {
				return;
			}
			boolean bDrawAverageLine = false;//是否画均线，证券里很多合约没有均价，这时候不画均线
		    for (int i = 0; i < mDataNum - 1; i++) {
		        if (mTrendDataArray.get(i).average > 0) {
		            bDrawAverageLine = true;
		            break;
		        }
		    }
		    if (!bDrawAverageLine)
		    {
		    	return;
		    }
		    
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(2.0f);
			linePaint.setColor(ColorConstant.ZQ_COLOR_AVG);

			double last1  = mLineLeft;
			Path path_avg = new Path();
			
			int oldPrice1 = mOptionData.HQData.getnLastClear();
			int lastPrice1 = oldPrice1;
			for (int i = 0; i < mDataNum; i++) {
				double x = last1;

				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}

				int average = 0;
				if (trendData.average == 0) {
					average = lastPrice1;
				} else {					
						average = trendData.average;					
				}

				double y_average = (double) (mTlineMidY - (average - oldPrice1) * mYScales);

				if (i == 0) {
					path_avg.moveTo((float) x, (float) y_average);
				} else {
					x += mXScales;
					path_avg.lineTo((float) x, (float) y_average);
				}
				last1 = x;
				lastPrice1 = average;
			}
			canvas.drawPath(path_avg, linePaint);
			
	
			
			
			// y coordinate for price
			mPaint.setTextSize(mFontSize);// ViewTools.TEXTSIZE_L
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setPathEffect(null);
			mPaint.setShader(null);
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setStrokeWidth(0.5f);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_MIDDLE);//ColorConstant.PRICE_EQUAL
			int tempY = mTlineMidY;

			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, tempY, mOptionData.HQData.getnLastClear(), 1, mOptionData.HQData.getnLastClear(), mOptionData.PriceDecimal,mOptionData.PriceRate, mPaint, false);
		
			
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			tempY -= mLineSpace;
			tempY -= mLineSpace;			
			ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, tempY, mOptionData.HQData.getnLastClear() + mPriceOffset * 2, 1, mOptionData.HQData.getnLastClear(),mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			
			
			
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			tempY = mTlineMidY + (int) mLineSpace - mFontH;
			int t_width = (int) mPaint.measureText("0.00");
			
			 if (mOptionData.HQData.getnLastClear() < mPriceOffset) {
			// ViewTools.DrawText(canvas, "0.00", mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0, mPaint);
			 } else {
			// ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,mOptionData.HQData.getnLastClear() - mPriceOffset, 1,mOptionData.HQData.getnLastClear(), mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			 }
			
			
			
			 //___________________________________________
			tempY += mLineSpace;
			if (mOptionData.HQData.getnLastClear() < mPriceOffset * 2) {
				ViewTools.DrawText(canvas, "0.00", mLineLeft - 2 -  t_width, mLineLeft - 2, tempY, 0, mPaint);
			} else {
				ViewTools.DrawPrice_ZQ_Out(canvas, mLineLeft - 2, tempY, mOptionData.HQData.getnLastClear() - mPriceOffset * 2, 1, mOptionData.HQData.getnLastClear(),
						mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			}
			
			// draw ZDF
			mPaint.setTextSize(mFontSize);
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_MIDDLE);
			tempY = mTlineMidY;
			ViewTools.DrawZDF_ZQ_Out(canvas, mLineRight + 2, tempY, 0, 1, mOptionData.HQData.getnLastClear(), true, true, mPaint, false);

			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			tempY -= mLineSpace;
			tempY -= mLineSpace;
			ViewTools.DrawZDF_ZQ_Out(canvas, mLineRight + 2, tempY, mPriceOffset * 2, 1, mOptionData.HQData.getnLastClear(), true, true, mPaint, false);
			
			//===========2
			 tempY -= mLineSpace;
			//===========2
			mPaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_TOP);
			tempY = mTlineMidY - mFontH + (int) mLineSpace;

			//===========3
			tempY += mLineSpace;
			ViewTools.DrawZDF_ZQ_Out(canvas, mLineRight + 2, tempY, -mPriceOffset * 2, 1, mOptionData.HQData.getnLastClear(), true, true, mPaint, false);

			
		//----------------------------

			
			
			
		}

		// draw volume
		protected void drawVolume(Canvas canvas) {
			if (mDataNum <= 0 || mOptionData == null) {
				L.e(TAG, "mDataNum <= 0");
				return;
			}

			long max = 0;
			// calc max volume
			for (int i = 0; i < mDataNum; i++) {
				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}
				max = (long) Math.max(trendData.volume, max);
			}

			// calc y scale
			double YScales = 0.0;
			if (max > 0) {
				YScales = (double) (mLineSpace * 2) / max;
			}
			// display volume
			linePaint.setAntiAlias(true);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setPathEffect(null);
			
			double last = mLineLeft + 1;
			for (int i = 0; i < mDataNum; i++) {
				if (i == 0) {
		            if (mTrendDataArray.get(0).now > mOptionData.HQData.nOpenPrice) {
		            	linePaint.setStrokeWidth(1);
		    			linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
		            }else if (mTrendDataArray.get(0).now < mOptionData.HQData.nOpenPrice){
		            	linePaint.setStrokeWidth(1);
		    			linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
		            }else{
		            	linePaint.setStrokeWidth(1);
		    			linePaint.setColor(ColorConstant.ZQ_DETAIL_COLOR_TREND_VOLUME);
		            }
		            
		            
		        }else{
		            if (mTrendDataArray.get(i).now >mTrendDataArray.get(i-1).now) {
		            	linePaint.setStrokeWidth(1);
		    			linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED);
		            }else if (mTrendDataArray.get(i).now < mTrendDataArray.get(i-1).now){
		            	linePaint.setStrokeWidth(1);
		    			linePaint.setColor(ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN);
		            } else{
		            	linePaint.setStrokeWidth(1);
		    			linePaint.setColor(ColorConstant.ZQ_DETAIL_COLOR_TREND_VOLUME);
		            }
			    }
				float x = (float) last;
				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}

				float y = 0;
				y = (float) (mTechBottomY - 1 - trendData.volume * YScales);
				if(y < mTechTopY)
				{
					y = mTechTopY;
				}

				if (y >= mTechTopY && y < mTechBottomY) {
					canvas.drawLine(x, y, x, mTechBottomY - 1, linePaint);
				}
				last += mXScales;
		    }
		}
		
		// draw CCL,在成交量区域上半区域画
		protected void drawCCL(Canvas canvas) {
			if (mDataNum <= 0 || mOptionData == null) {
				L.e(TAG, "mDataNum <= 0");
				return;
			}

			long max = 0;
			long min = 0;
			// calc max ccl
			for (int i = 0; i < mDataNum; i++) {
				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}
				max = (long) Math.max(trendData.ccl, max);
				if (trendData.ccl > 0)
				{
					if (min > 0)
					{
						min = (long) Math.min(trendData.ccl, min);
					}else
					{
						min = (long) trendData.ccl;
					}
				}
			}
			
			if (max <= min) {
				max	= min + 100;
			}

			// calc y scale
			double YScales = 0.0;
			if (max > 0) {
				YScales = (double) (mLineSpace) / (max-min);
			}
			// display volume
			linePaint.setAntiAlias(true);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setPathEffect(null);
			linePaint.setStrokeWidth(2.0f);
			linePaint.setColor(ColorConstant.COLOR_CCL);
			
			double last = mLineLeft + 1;
			Path path_ccl = new Path();
			
			double lastccl = 0;
			for (int i = 0; i < mDataNum; i++) {

				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}
				
				double ccl = 0;
				if (trendData.ccl <= 0)
				{
					ccl = lastccl;
				}else
				{
					ccl = trendData.ccl;
					lastccl = ccl;
				}
				float y_ccl = (float) (mTechBottomY - mLineSpace);
				if (ccl > 0)
				{
					y_ccl = (float) (mTechBottomY - mLineSpace - (ccl-min)*YScales);
				}		

				float x = (float) last;
				
				if (i == 0) {
					path_ccl.moveTo((float) x, (float) y_ccl);
				} else {
					path_ccl.lineTo((float) x, (float) y_ccl);
				}
				last += mXScales;
			}
			canvas.drawPath(path_ccl, linePaint);
			
		}
		
		public void onLongPressLine(MotionEvent event) {

			int x = (int) event.getX();
			int y = (int) event.getY();
			//y = y - mTrendLineTop;
			m_iYPos = y;

	    	//判断popupinfo弹出位置
//	    	int halfwidth = (mClientRect.right - mClientRect.left)/2;
//	    	int oldflat = flag_position_showing;
//	    	if (x <= halfwidth) {
//				flag_position_showing = POSITION_RIGHT;
//			} else {
//				flag_position_showing = POSITION_LEFT;
//			}
	    	
	    	if( (x > mLineLeft) && (x < mLineRight) ) {//&& (y > mTlineTopY) && (y < (mTlineBottomY)

	    		double index = (x - mLineLeft)/mXScales;
	    		if(index >= 0 && index < mDataNum) {
	    			m_iIndex = (int)index;
	    		}
	    		else {
	    			m_iIndex = mDataNum - 1;
	    		}
//	    		if (oldflat != flag_position_showing)
//	    			DismissInfo(false);
	    		
	    		
	    		if (mTrendDataArray != null && m_iIndex >= 0 && m_iIndex < mTrendDataArray.size())
	    		{
	    			TagLocalTrendData trendData = mTrendDataArray.get(m_iIndex);
					if (trendData != null) {
						int oldPrice = mOptionData.HQData.getnLastClear();
						
						int now = 0;
						if (trendData.now == 0) {
							now = oldPrice;
						} else {
							now = trendData.now;
						}

						m_iYPos = (int) (mTlineMidY - (now - oldPrice) * mYScales);
					}
					
	    		}
	    		
	    		
	    		PopupInfo();
	        	
	        	this.invalidate();
	    	} 
	    	else if( event.getAction() == MotionEvent.ACTION_UP ) {	    		
		    		DismissInfo(false);
		    		this.invalidate();
	    	}
		}

		public void onMoveLine(MotionEvent event) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			//y = y - mTrendLineTop;
			m_iYPos = y;

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
		
				if( (x > mLeft) && (x < mRight) ) {//&& (y > mTlineTopY) && (y < (mTechBottomY)

		    		double index = (x - mLineLeft)/mXScales;
		    		if(index >= 0 && index < mDataNum) {
		    			m_iIndex = (int)index;
		    		}
		    		else if (index < 0)
		    		{
		    			m_iIndex = 0;
		    		}else {
		    			m_iIndex = mDataNum - 1;
		    		}
//		    		if (oldflat != flag_position_showing)
//		    			DismissInfo(false);
		    		if (mTrendDataArray != null && m_iIndex >= 0 && m_iIndex < mTrendDataArray.size())
		    		{
		    			TagLocalTrendData trendData = mTrendDataArray.get(m_iIndex);
						if (trendData != null) {
							int oldPrice = mOptionData.HQData.getnLastClear();
							
							int now = 0;
							if (trendData.now == 0) {
								now = oldPrice;
							} else {
								now = trendData.now;
							}

							m_iYPos = (int) (mTlineMidY - (now - oldPrice) * mYScales);
						}
						
		    		}
		    		PopupInfo();
		        	
		        	this.invalidate();
		    	}
				else {
					DismissInfo(false);
					invalidate();
				}
			}
		}

		public void onTouchLine(MotionEvent event) {
			
			L.d(TAG, "------------------onTouchLine------------------" + event.getAction());
			int x = (int) event.getX();
			int y = (int) event.getY();
			y = y - mTrendLineTop;
			//
			if (event.getAction() == MotionEvent.ACTION_UP) {

				// 判断popupinfo弹出位置
//				int halfwidth = (mClientRect.right - mClientRect.left) / 2;
//				int oldflat = flag_position_showing;
//				if (x <= halfwidth) {
//					flag_position_showing = POSITION_RIGHT;
//				} else {
//					flag_position_showing = POSITION_LEFT;
//				}

				if ((x > mLineLeft) && (x < mLineRight) && (y > mTlineTopY) && (y < mTlineBottomY)) {
		    		
					if(mPopinfoFlag)
					{
						DismissInfo(false);
					}
					invalidate();
				} 
				else if((x > mLineLeft) && (x < mLineRight)
		    			&& (y > mTechTopY) && (y < mTechBottomY))  {
		    		//
					if(mPopinfoFlag)
					{
						DismissInfo(false);
					}
		    		//
		    		invalidate();
		    	}
				else {
					DismissInfo(false);
					invalidate();
				}
			}

		}

		public int getLineTop() {
			return mTlineTopY - mFontH / 2;
		}
		
		public int getClientHalfWidth()
		{
	    	int halfwidth = (mClientRect.right - mClientRect.left)/2;
	    	return halfwidth;
		}	    
	}
}
