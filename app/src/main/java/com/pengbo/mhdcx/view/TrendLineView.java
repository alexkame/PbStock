package com.pengbo.mhdcx.view;

import java.util.ArrayList;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 走势图
 * 
 * @author pobo
 * 
 */
public class TrendLineView extends FrameLayout {
	public static final String TAG = TrendLineView.class.getSimpleName();

	public MyApp mMyApp;

	private TagLocalStockData mOptionData;
	private TagLocalStockData mStockData;
	private ArrayList<TagLocalTrendData> mTrendDataArray;
	private ArrayList<TagLocalTrendData> mTrendStockDataArray;
	private ArrayList<TagLocalDealData> mDealDataArray;

	private TrendView mTrendView;
	private Ctrl_Trend_RightPanel mRightView;
	private DisplayMetrics mScreenSize;
	private boolean mbShowRight = true;

	public TrendLineView(Context context, boolean bShowRight) {
		super(context);
		mbShowRight = bShowRight;
		initData(context);
		initView(context);
	}
	
	private void initData(Context context) {
		mMyApp = (MyApp) context.getApplicationContext();
		mScreenSize = ViewTools.getScreenSize(context);

		mDealDataArray = mMyApp.getDealDataArray();
		mTrendDataArray = mMyApp.getTrendDataArray();
		mTrendStockDataArray=mMyApp.getTrendStockDataArray();
	}
	
	private void initView(Context context) {
		FrameLayout frame = new FrameLayout(context);

		LinearLayout layouthorn = new LinearLayout(context);
		layouthorn.setOrientation(LinearLayout.HORIZONTAL);
		//trend line layout

		LinearLayout layouthorn1 = new LinearLayout(context);//trend view layout include title
		layouthorn1.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout layoutTrendText = new LinearLayout(context);//trend view layout include title
		layoutTrendText.setOrientation(LinearLayout.HORIZONTAL);
		layoutTrendText.setGravity(Gravity.CENTER_VERTICAL);
		layoutTrendText.setPadding(5, 0, 0, 0);
		
		float fontSize = getResources().getDimension(R.dimen.font_screen_F);
		fontSize = ViewTools.px2dip(context, fontSize);
		ImageView img1 = new ImageView(context);
		img1.setBackgroundResource(R.drawable.point01);
		img1.setPadding(5, 2, 2, 2);
		layoutTrendText.addView(img1);
		
		TextView tv1 = new TextView(context);
		tv1.setTextColor(Color.GRAY);
		tv1.setText("期权走势");
		tv1.setTextSize(fontSize);
		tv1.setPadding(0, 0, 10, 0);
		layoutTrendText.addView(tv1);
		
		ImageView img2 = new ImageView(context);
		img2.setBackgroundResource(R.drawable.point02);
		img2.setPadding(2, 2, 2, 2);
		layoutTrendText.addView(img2);
		
		TextView tv2 = new TextView(context);
		tv2.setTextColor(Color.GRAY);
		tv2.setTextSize(fontSize);
		tv2.setText("正股走势");
		layoutTrendText.addView(tv2);
		
		layouthorn1.addView(layoutTrendText);
		
		mTrendView = new TrendView(context);
		layouthorn1.addView(mTrendView);
		//right panel for five price and detail
		if (mbShowRight)
		{
			
			//mTrendView = new TrendView(context);
			//layouthorn1.addView(mTrendView);
			
			
			LayoutParams lp1 = new LayoutParams(mScreenSize.widthPixels * 2 / 3,
					mScreenSize.heightPixels/3);
			layouthorn.addView(layouthorn1, lp1);
			
			LayoutParams lp2 = new LayoutParams(mScreenSize.widthPixels *1/3, 
					mScreenSize.heightPixels/3);
			mRightView = new Ctrl_Trend_RightPanel(context);
			layouthorn.addView(mRightView, lp2);
		}else
		{
			
			LayoutParams lp1 = new LayoutParams(LayoutParams.FILL_PARENT,
					mScreenSize.heightPixels/3);
			
			layouthorn.addView(layouthorn1, lp1);
		}
        
		frame.addView(layouthorn);
		addView(frame);
	}
	
	public void updateData(TagLocalStockData optionData, TagLocalStockData stockData) {
		this.mOptionData = optionData;
		this.mStockData = stockData;
	}

	public void onTouchLine(MotionEvent event) {
		//L.i(TAG, "onTouchLine");
		mTrendView.onTouchLine(event);
	}

	//update view
	public void updateAllView() {
		if (mTrendView != null)
		{
			mTrendView.updateAllView();
		}
		
		if (mbShowRight && mRightView != null)
		{
			mRightView.updateData(mOptionData, mDealDataArray, mScreenSize.heightPixels/3);
		}
	}
	
	public void setShowRight(boolean bShow)
	{
		this.mbShowRight = bShow;
	}

	//View for draw trend line
	public class TrendView extends View {

		Rect mClientRect; //display area
		Paint mPaint;
		Paint linePaint;
		Paint linePaint_stock;

		private int mHeight_Title = 0;
		private int mFontH_L = 0;
		private int mLeft = 0;
		private int mRight = 0;
		private int mStockPanelY = 0;

		private int mLineLeft = 0;
		private int mLineRight = 0;
		private int mTlineTopY = 0; //trend line y top
		private int mTlineMidY = 0; //trend line y middle
		private int mTlineBottomY = 0; //trend line y bottom
		private double mLineSpace = 0.0; //trend line y space for each row

		private int mTechTopY = 0;
		private int mTechBottomY = 0;

		private double mXScales = 0.0; //x scale(for time)
		private double mYScales = 0.0; //y scale(for price)
		private double mYScales_Stock = 0.0; // y scale stock(for price)

		private int mPriceOffset; //y option price offset of each row
		private int mDataNum; //trend data number
		private int mStockDataNum;// trend data of stock number
		
		private int mPriceOffset_Stock; //y stock price offset of each row

		public TrendView(Context context) {
			super(context);

			mClientRect = new Rect();
			mPaint = new Paint();
			linePaint = new Paint();
			linePaint_stock = new Paint();

			float fontSize = getResources().getDimension(R.dimen.font_screen_F);
			mHeight_Title = ViewTools.getFontHeight(fontSize);
			mFontH_L = ViewTools.getFontHeight(ViewTools.TEXTSIZE_L);
		}

		//init some parameters for trend line draw
		private void drawInit() {
			mLeft = mClientRect.left;
			mRight = mClientRect.right;

			mTechBottomY = mClientRect.height() - 5;
			mStockPanelY = 0;

			L.d(TAG, "drawInit--->mClientRect--->top = " + mClientRect.top
					+ ", bottom = " + mClientRect.bottom);

			mTlineTopY = mStockPanelY + 5;
			mLineSpace = (double) (mTechBottomY - mTlineTopY - mFontH_L) / 6.0;
			mTechTopY = (int) (mTechBottomY - mLineSpace * 2);

			mTlineMidY = (int) (mTlineTopY + mLineSpace * 2);
			mTlineBottomY = mTechTopY - mFontH_L;

			mPaint.setTextSize(ViewTools.TEXTSIZE_L);
			mLineLeft = mLeft + 10;
			mPaint.setTextSize(ViewTools.TEXTSIZE_L);
			mLineRight = mRight - 5;

			//calc x scale for time
			int trendnum = 241;//now using 241 minutes for temporary, will change this value base on the data

			mXScales = (double) (mLineRight - mLineLeft) / trendnum;
			
			mPriceOffset = 0;
			mDataNum = mTrendDataArray.size();
			if (mDataNum <= 0)
				return;

			// calc y scale for option
			if (mOptionData == null)
			{
				return;
			}
			int maxprice = (mOptionData.HQData.nHighPrice != 0) ? mOptionData.HQData.nHighPrice
					: mOptionData.HQData.getnLastClear();
			int minprice = (mOptionData.HQData.nLowPrice != 0) ? mOptionData.HQData.nLowPrice
					: mOptionData.HQData.getnLastClear();
			for (int i = 0; i < mDataNum; i++) {
				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData.now != 0) {
					maxprice = Math.max(trendData.now, maxprice);
					minprice = Math.min(trendData.now, minprice);
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
				int times[] = { 10000, 1000, 100, 10, 1 };
				if (mOptionData.PriceDecimal >= 0
						&& mOptionData.PriceDecimal < times.length) {
					maxPriceOffset = 4 * times[mOptionData.PriceDecimal];
				}
			}

			if (maxPriceOffset > 0) {
				mYScales = (double) (mTlineBottomY - mTlineMidY)
						/ maxPriceOffset;
			}

			mPriceOffset = maxPriceOffset / 2;// Price has 2 rows for each side
			
			// calc y scale for stock
			mStockDataNum = mTrendStockDataArray.size();
			if (mStockDataNum <= 0)
				return;
			
			if (mStockData == null)
			{
				return;
			}
			int maxprice_stock = (mStockData.HQData.nHighPrice != 0) ? mStockData.HQData.nHighPrice
					: mStockData.HQData.getnLastClose();
			int minprice_stock = (mStockData.HQData.nLowPrice != 0) ? mStockData.HQData.nLowPrice
					: mStockData.HQData.getnLastClose();
			for (int i = 0; i < this.mStockDataNum; i++) {
				TagLocalTrendData trendData_stock = mTrendStockDataArray.get(i);
				if (trendData_stock.now != 0) {
					maxprice_stock = Math.max(trendData_stock.now, maxprice_stock);
					minprice_stock = Math.min(trendData_stock.now, minprice_stock);
				}
			}

			if (maxprice_stock > 0) {
				maxprice_stock = maxprice_stock - mStockData.HQData.getnLastClose();
				if (maxprice_stock < 0) {
					maxprice_stock = -maxprice_stock;
				}
			}
			if (minprice_stock > 0) {
				minprice_stock = mStockData.HQData.getnLastClose() - minprice_stock;
				if (minprice_stock < 0) {
					minprice_stock = -minprice_stock;
				}
			}

			int maxPriceOffset_stock = Math.max(minprice_stock, maxprice_stock);
			if (maxPriceOffset_stock == 0) {
				int times[] = { 10000, 1000, 100, 10, 1 };
				if (mStockData.PriceDecimal >= 0
						&& mStockData.PriceDecimal < times.length) {
					maxPriceOffset_stock = 4 * times[mStockData.PriceDecimal];
				}
			}

			if (maxPriceOffset_stock > 0) {
				mYScales_Stock = (double) (mTlineBottomY - mTlineMidY)
						/ maxPriceOffset_stock;
			}

			mPriceOffset_Stock = maxPriceOffset_stock / 2;// Price has 3 rows for each side
		}

		//update view
		public void updateAllView() {
			//init some parameters for draw trend line
			drawInit();
			this.invalidate();
		}

		private void drawNow(Canvas canvas) {
			L.i("ScreenDetailActivity", "Received push data1 drawNow");
			updateTrend(canvas);
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
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			drawNow(canvas);
		}

		//update trend area
		public void updateTrend(Canvas canvas) {
			//draw background such as coordinate line
			if (mOptionData == null)
			{
				return;
			}
			drawBackground(canvas);
			drawTrendLine(canvas);
			drawVolume(canvas);

		}

		//draw background such as coordinate line
		protected void drawBackground(Canvas canvas) {
			mPaint.setColor(ColorConstant.COLOR_TREND_ZD);  
			//mPaint.setStrokeWidth(1.5f);  
			PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5}, 1);  
			mPaint.setPathEffect(effects);  
			canvas.drawLine(mLineLeft, mTlineMidY, mLineRight, mTlineMidY, mPaint);
			
			mPaint.setAntiAlias(true);
			mPaint.setColor(ColorConstant.COLOR_TREND_ZUOBIAO);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setPathEffect(null);
			mPaint.setShader(null);

			//separation line(row) price
			int tempY = mTlineMidY;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);

			mPaint.setColor(ColorConstant.COLOR_TREND_ZUOBIAO);
			tempY -= mLineSpace;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);
			//tempY -= mLineSpace;
			//canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);

			tempY = mTlineTopY;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);
			
			tempY = mTlineMidY + (int) mLineSpace;
			mPaint.setColor(ColorConstant.COLOR_TREND_ZUOBIAO);
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);
			//tempY += mLineSpace;
			//canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);

			canvas.drawLine(mLineLeft, mTlineBottomY, mLineRight,
					mTlineBottomY, mPaint);
			
			//Y line for time
			int tradeFields = mOptionData.TradeFields; //时间段
			if (tradeFields <= 0)
			{
				tradeFields = 1;
			}
			int xspace = (mLineRight - mLineLeft)/tradeFields;
			for (int i = 1; i < tradeFields; i++)
			{
				canvas.drawLine(mLineLeft + i*xspace, mTlineTopY, mLineLeft + i*xspace, mTlineBottomY,
						mPaint);
			}

			//boundary of left&right
			canvas.drawLine(mLineLeft, mTlineTopY, mLineLeft, mTlineBottomY,
					mPaint);
			canvas.drawLine(mLineRight, mTlineTopY, mLineRight, mTlineBottomY,
					mPaint);

			mPaint.setColor(ColorConstant.COLOR_TREND_ZUOBIAO);
			//separation line(row) volume
			tempY = mTechTopY;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);
			tempY += mLineSpace;
			tempY += mLineSpace;
			canvas.drawLine(mLineLeft, tempY, mLineRight, tempY, mPaint);
			//boundary of left&right
			canvas.drawLine(mLineLeft, mTechTopY, mLineLeft, mTechBottomY,
					mPaint);
			canvas.drawLine(mLineRight, mTechTopY, mLineRight, mTechBottomY,
					mPaint);
		}

		//draw trend line(price)
		protected void drawTrendLine(Canvas canvas) {
			
			if(null == mOptionData)
			{
				L.e(TAG, "mOptionData == null");
				return;
			}
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			float fontSize = getResources().getDimension(R.dimen.font_screen_F);
			//x coordinate for time
			int tradenum = 241;//now using 241 minutes for default. In future,it will be changed by the data
			if (tradenum > 1) {
				int tradeFields = mOptionData.TradeFields;
				String temp;
				float left = mLineLeft;
				for (int i = 0; i < tradeFields; i++)
				{
					mPaint.setTextSize(fontSize);
					mPaint.setColor(Color.GRAY);
					mPaint.setTextAlign(Paint.Align.CENTER);
					temp = STD.getTimeSringhhmm(mOptionData.Start[i]);
					float t_width = mPaint.measureText(temp);
					left += (t_width * i) + mPaint.measureText("/");
					ViewTools.DrawText(canvas, temp, (int) left,
							(int) (left + t_width), mTlineBottomY, 0, mPaint);
					
					temp = STD.getTimeSringhhmm(mOptionData.End[i]);
					if(i != tradeFields-1)
					{
						temp += "/";
					}
					t_width = mPaint.measureText(temp);
					left = left + (mLineRight - mLineLeft) / tradeFields 
							- t_width - 1;
					ViewTools.DrawText(canvas, temp, (int) left,
							(int) (left + t_width), mTlineBottomY, 0, mPaint);
				}
			}
			
			//y coordinate for price
			
			
			mPaint.setTextSize(fontSize);//ViewTools.TEXTSIZE_L
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(ColorConstant.PRICE_EQUAL);
			int tempY = mTlineMidY;

			ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,
					mOptionData.HQData.getnLastClear(), 1,
					mOptionData.HQData.getnLastClear(),
					mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			
			mPaint.setColor(ColorConstant.PRICE_UP);
			tempY -= mLineSpace;
			//ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,
			////		mOptionData.HQData.getnLastClear() + mPriceOffset, 1,
			//		mOptionData.HQData.getnLastClear(),
			//		mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			tempY -= mLineSpace;
			ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,
					mOptionData.HQData.getnLastClear() + mPriceOffset * 2, 1,
					mOptionData.HQData.getnLastClear(),
					mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			
			mPaint.setColor(ColorConstant.PRICE_DOWN);
			tempY = mTlineMidY + (int) mLineSpace -  mHeight_Title;
			int t_width = (int)mPaint.measureText("0.00");
//			if (mOptionData.HQData.getnLastClear() < mPriceOffset) {
//				ViewTools.DrawText(canvas, "0.00", mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0,
//						mPaint);
//			} else {
//				ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,
//						mOptionData.HQData.getnLastClear() - mPriceOffset, 1,
//						mOptionData.HQData.getnLastClear(),
//						mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
//			}
			tempY += mLineSpace;
			if (mOptionData.HQData.getnLastClear() < mPriceOffset * 2) {
				ViewTools.DrawText(canvas, "0.00", mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0,
						mPaint);
			} else {
				ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,
						mOptionData.HQData.getnLastClear() - mPriceOffset * 2,
						1, mOptionData.HQData.getnLastClear(),
						mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
			}
//			tempY += mLineSpace;
//			if (mOptionData.HQData.getnLastClear() < mPriceOffset * 3) {
//				ViewTools.DrawText(canvas, "0.00", mLineLeft + 2, mLineLeft + 2 + t_width, tempY, 0,
//						mPaint);
//			} else {
//				ViewTools.DrawPrice(canvas, mLineLeft + 2, tempY,
//						mOptionData.HQData.getnLastClear() - mPriceOffset * 3,
//						1, mOptionData.HQData.getnLastClear(),
//						mOptionData.PriceDecimal, mOptionData.PriceRate, mPaint, false);
//			}
			
			//draw ZDF
			mPaint.setTextSize(fontSize);
			mPaint.setColor(ColorConstant.PRICE_EQUAL);
			tempY = mTlineMidY;
			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY, 0, 1,
					mOptionData.HQData.getnLastClear(), true, true, mPaint,
					false);
			
			mPaint.setColor(ColorConstant.PRICE_UP);
			tempY -= mLineSpace;
//			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY, mPriceOffset,
//					1, mOptionData.HQData.getnLastClear(), true, true, mPaint,
//					false);
			tempY -= mLineSpace;
			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY,
					mPriceOffset * 2, 1, mOptionData.HQData.getnLastClear(),
					true, true, mPaint, false);
//			tempY -= mLineSpace;
//			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY,
//					mPriceOffset * 3, 1, mOptionData.HQData.getnLastClear(),
//					true, true, mPaint, false);
			
			mPaint.setColor(ColorConstant.PRICE_DOWN);
			tempY = mTlineMidY - mHeight_Title + (int) mLineSpace;
//			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY, -mPriceOffset,
//					1, mOptionData.HQData.getnLastClear(), true, true, mPaint,
//					false);
			tempY += mLineSpace;
			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY,
					-mPriceOffset * 2, 1, mOptionData.HQData.getnLastClear(),
					true, true, mPaint, false);
//			tempY += mLineSpace;
//			ViewTools.DrawZDF(canvas, mLineRight - 2, tempY,
//					-mPriceOffset * 3, 1, mOptionData.HQData.getnLastClear(),
//					true, true, mPaint, false);

			mDataNum = mTrendDataArray.size();
			if (mDataNum <= 0) {
				L.e(TAG, "mDataNum <= 0");
				return;
			}

			
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(2.0f);
			linePaint.setColor(ColorConstant.COLOR_TREND);
			
			double last = mLineLeft;
			Path path_now = new Path();
			int oldPrice = mOptionData.HQData.getnLastClear();
			int lastPrice = oldPrice;
			for (int i = 0; i < mDataNum; i++) {
				double x = last;

				TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null) {
					continue;
				}
				
				int now = 0;
				if (trendData.now == 0)
				{
					now = lastPrice;
				}else
				{
					now = trendData.now;
				}
				
				double y_now = (double) (mTlineMidY - (now - oldPrice)
						* mYScales);

				if (i == 0) {
					path_now.moveTo((float) x, (float) y_now);
				} else {
					x += mXScales;
					path_now.lineTo((float) x, (float) y_now);
				}
				last = x;
				lastPrice = now;
			}
			canvas.drawPath(path_now, linePaint);
			
			
			
			// draw trend stock line path
			mStockDataNum = mTrendStockDataArray.size();
			if (mStockDataNum <= 0) {
				L.e(TAG, "mStockDataNum <= 0");
				return;
			}
			
			if(mStockData == null)
			{
				return;
			}

			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(2.0f);
			linePaint.setColor(ColorConstant.COLOR_TREND_STOCK);

			double lasts_stock = mLineLeft;
			Path path_nows_stock = new Path();
			int oldPrices_stock = mStockData.HQData.getnLastClose();
			int lastPrices_stock = oldPrices_stock;
			for (int i = 0; i < mStockDataNum; i++) {
				double x_stock = lasts_stock;

				TagLocalTrendData trendData_stock = mTrendStockDataArray.get(i);
				if (trendData_stock == null) {
					continue;
				}

				int now_stock = 0;
				if (trendData_stock.now == 0) {
					now_stock = lastPrices_stock;
				} else {
					now_stock = trendData_stock.now;
				}

				double y_now_stock = (double) (mTlineMidY - (now_stock - oldPrices_stock) * mYScales_Stock);

				if (i == 0) {
					path_nows_stock.moveTo((float) x_stock, (float) y_now_stock);
				} else {
					x_stock += mXScales;
					path_nows_stock.lineTo((float) x_stock, (float) y_now_stock);
				}
				lasts_stock = x_stock;
				lastPrices_stock = now_stock;
			}
			canvas.drawPath(path_nows_stock, linePaint);
		}

		//draw volume
		protected void drawVolume(Canvas canvas) {
			if(mDataNum <= 0)
			{
				L.e(TAG, "mDataNum <= 0");
				return;
			}
			
			long max = 0;
	        //calc max volume
	        for (int i = 0; i < mDataNum; i++) {
	        	TagLocalTrendData trendData = mTrendDataArray.get(i);
	        	if(trendData == null)
	        	{
	        		continue;
	        	}
	        	max = (long)Math.max(trendData.volume, max);
	        }

	        //calc y scale
	        double	YScales	= 0.0;
	        if (max > 0) {
	        	YScales	= (double) (mLineSpace * 2) / max;
	        }
	    	//display volume
	    	linePaint.setAntiAlias(true);
	    	linePaint.setStyle(Paint.Style.STROKE);
	    	linePaint.setPathEffect(null);
	    	linePaint.setStrokeWidth(1);
	    	linePaint.setColor(ColorConstant.COLOR_TREND_VOLUME);
	    	double last = mLineLeft + 1;
	    	for (int i = 0; i < mDataNum; i++)
	    	{
	    		float x	= (float)last;
	    		TagLocalTrendData trendData = mTrendDataArray.get(i);
				if (trendData == null)
				{
					continue;
				}
				
	    		float y	= 0;
	    		y = (float)(mTechBottomY - trendData.volume * YScales);
	    		
	    		if (y >= mTechTopY && y < mTechBottomY) {
	    			canvas.drawLine(x, y, x, mTechBottomY-1, linePaint);
	    		}
	    		last += mXScales;
	    	}
		}

		public void onTouchLine(MotionEvent event) {

		}
	}
}
