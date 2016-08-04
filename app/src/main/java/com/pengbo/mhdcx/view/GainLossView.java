package com.pengbo.mhdcx.view;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.adapter.GainLossListViewAdapter;
import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagProfitRecord;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.L;
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
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.app.Activity;
import android.app.Dialog;

/**
 * 盈亏分析
 * 
 * @author pobo
 * 
 */
public class GainLossView extends FrameLayout implements OnCheckedChangeListener {
	public static final String TAG = GainLossView.class.getSimpleName();
	public static final int UI_UPDATE = 1;

	public static final int AnalyseView_Profit_Num = 9; //水平9条线，显示收益,必须是基数
	public static final int AnalyseView_Price_Num = 7; //垂直7条线，显示标的价格,必须是基数
	public static float s_fLostValueWidth = 0.25f;//总长度的四分之一
	public static float s_fLostValueHeight = 0.25f;//总高度的四分之一
	public float profitYbl = 0f;
	public MyApp mMyApp;
	private Context mContext;
	public Dialog mProgress;

	private TagLocalStockData mOptionData;
	private TagLocalStockData mStockData;// 合约对应的标的
	private HeadListDataService hService;
	
	private float mProfit[];
	private float mPrice[];

	private GLView mGLView;
	private DisplayMetrics mScreenSize;
	
	private LayoutInflater mInflater;
	private View mView;
	private RadioGroup mRgThree;//平缓 正常 剧烈
	private ListView mBottom_lv;
	private GainLossListViewAdapter mAdapter;
	private List<TagProfitRecord> mTagProfitRecords = null;// 不用初始化 业务类中有初始化
	private TagProfitRecord mPjsyRecord;
	
	private float bdyqbl = 1.0f;//默认是正常

	/************** 中间 总手 等 的数据 ********************************/
	private TextView field_hq_xqj, field_hq_nzjz, field_hq_sjjz, field_hq_ggl,
			field_hq_zsggl, field_hq_delta, field_hq_gamma, field_hq_theta,
			field_hq_rho, field_hq_vega;
	
	private LinearLayout mLLayout_PJSY;
	private TextView field_pjsy, field_pjsyl, field_pjrate;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case UI_UPDATE:
				closeProgress();
				if (mAdapter == null) {
					mAdapter = new GainLossListViewAdapter(mContext, mTagProfitRecords);
					mBottom_lv.setAdapter(mAdapter);
				} else {
					mAdapter.setLists(mTagProfitRecords);
					mAdapter.notifyDataSetChanged();
				}
				updatePJSYView();
				break;

			default:
				break;
			}
		};
	};
	
	public GainLossView(Context context, TagLocalStockData optionData, TagLocalStockData stockData) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		mContext = context;
		this.mOptionData = optionData;
		this.mStockData = stockData;
		initData(mContext);
		initView(mContext);
		
		field_pjsy = (TextView) ((Activity)mContext).findViewById(R.id.gain_loss_anilysis_pjsy);
		field_pjsyl = (TextView) ((Activity)mContext).findViewById(R.id.gain_loss_anilysis_pjsyl);
	    field_pjrate = (TextView) ((Activity)mContext).findViewById(R.id.gain_loss_anilysis_pjrate);
	    field_pjrate.setVisibility(View.INVISIBLE);
	}
	
	private void updatePJSYView()
	{
		TagProfitRecord record = null;
		record = getPJSYRecord();
		if (record != null) {
			field_pjsy.setText(String.format("%.2f", record.sy));
			field_pjsyl.setText(String.format("%.2f%s", record.syl * 100, "%"));
			field_pjrate.setText(String
					.format("%.2f%s", record.rate * 100, "%"));
		} else {
			field_pjsy.setText(Global_Define.STRING_VALUE_EMPTY);
			field_pjsyl.setText(Global_Define.STRING_VALUE_EMPTY);
			field_pjrate.setText(Global_Define.STRING_VALUE_EMPTY);
		}
	}
	
	private void loadData() {
		new Thread() {
			public void run() {

				mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
						mOptionData, mStockData, mPjsyRecord);
				mHandler.sendEmptyMessage(UI_UPDATE);
			};
		}.start();
		
	}
	
	private void initData(Context context) {
		mMyApp = (MyApp) context.getApplicationContext();
		mScreenSize = ViewTools.getScreenSize(context);
		mPjsyRecord = new TagProfitRecord();
		hService = new HeadListDataService( (Activity) context);
		
		mProfit = new float[9];
		mPrice = new float[7];
	}
	
	private void initView(Context context) {
		mView = mInflater.inflate(R.layout.gain_loss_analysis_view, null);
		
		mRgThree=(RadioGroup) mView.findViewById(R.id.detail_gainloss_rg);
		mRgThree.setOnCheckedChangeListener(this);
		
		// 底部的listview
		mBottom_lv = (ListView) mView.findViewById(R.id.gain_loss_listview);
		mTagProfitRecords = new ArrayList<TagProfitRecord>();

		if (mAdapter == null) 
		{
			mAdapter = new GainLossListViewAdapter(mContext, mTagProfitRecords);
			mBottom_lv.setAdapter(mAdapter);
		}
		
		field_hq_xqj = (TextView) mView.findViewById(R.id.field_hq_xqj);
		field_hq_nzjz = (TextView) mView.findViewById(R.id.field_hq_nzjz);
		field_hq_sjjz = (TextView) mView.findViewById(R.id.field_hq_sjjz);
		field_hq_ggl = (TextView) mView.findViewById(R.id.field_hq_ggl);
		field_hq_zsggl = (TextView) mView.findViewById(R.id.field_hq_zsggl);
		field_hq_delta = (TextView) mView.findViewById(R.id.field_hq_delta);
		field_hq_gamma = (TextView) mView.findViewById(R.id.field_hq_gamma);
		field_hq_theta = (TextView) mView.findViewById(R.id.field_hq_theta);
		field_hq_rho = (TextView) mView.findViewById(R.id.field_hq_rho);
		field_hq_vega = (TextView) mView.findViewById(R.id.field_hq_vega);
		
		FrameLayout frame = (FrameLayout) mView.findViewById(R.id.gain_loss_analysis_draw);

		LinearLayout layouthorn = new LinearLayout(context);
		layouthorn.setOrientation(LinearLayout.HORIZONTAL);
		//trend line layout
		LayoutParams lp1 = new LayoutParams(mScreenSize.widthPixels,
				mScreenSize.heightPixels * 1/3);
		mGLView = new GLView(context);
		layouthorn.addView(mGLView, lp1);
        
		frame.addView(layouthorn);
		addView(mView);
		loadData();
		setData();
	}
	
	private void setData() {
		if (mOptionData == null || mStockData == null)
		{
			return;
		}
		field_hq_xqj.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_XQJ, mStockData));
		field_hq_nzjz.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_NZJZ, mStockData));

		field_hq_sjjz.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_SJJZ, mStockData));

		field_hq_ggl.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_GGL, mStockData));

		field_hq_zsggl.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_ZSGGL, mStockData));

		field_hq_delta.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Delta, mStockData));

		field_hq_gamma.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Gamma, mStockData));

		field_hq_theta.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Theta, mStockData));

		field_hq_rho.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Rho, mStockData));

		field_hq_vega.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Vega, mStockData));
	}
	
	public TagProfitRecord getPJSYRecord()
	{
		return this.mPjsyRecord;
	}
	
	public void updateData(TagLocalStockData optionData, TagLocalStockData stockData) {
		this.mOptionData = optionData;
		this.mStockData = stockData;

		setData();
		updateAllData();
	}

	//update view
	public void updateAllData() {
		
		if (mGLView != null)
		{
			mGLView.updateAllData();
		}
	}

	//View for draw profit
	public class GLView extends View {

		Rect mClientRect; //display area
		Paint mPaint;
		Paint linePaint;
		
		private float mStartXCoor;//坐标点X坐标
		private float mStartYCoor;//坐标点Y坐标
		private float mDrawWidth;//画图区域宽度
		private float mDrawHeight;//画图区域高度
		
		private float mFontSize; //坐标字体大小
		private int mHeight_Font;
		private int mWidth_Font;
		private float mMargin; //上下左右空隙

		public GLView(Context context) {
			super(context);

			mClientRect = new Rect();
			mPaint = new Paint();
			linePaint = new Paint();
			mFontSize = getResources().getDimension(R.dimen.font_gainloss_B);
			mMargin = getResources().getDimension(R.dimen.gainloss_margin_leftright);
			mHeight_Font = ViewTools.getFontHeight(mFontSize);
		}

		//init some parameters for trend line draw
		private void drawInit() {
			String strProfit = ViewTools.getStringByFloatPrice(mProfit[0], 0, mOptionData.PriceDecimal);
			mPaint.setTextSize(mFontSize);
			mWidth_Font = (int) mPaint.measureText(strProfit);
			mStartXCoor = mClientRect.left + mWidth_Font + mMargin;
	        mDrawWidth = mClientRect.width() - mStartXCoor - mMargin;
	        mStartYCoor = mClientRect.top + mMargin;
	        mDrawHeight = mClientRect.height() - mStartYCoor - mHeight_Font - mMargin;
		}

		private void updateProfitData()
		{
			TagLocalStockData stockData = new TagLocalStockData(); //标的
			mMyApp.mStockConfigData.search(stockData, mOptionData.optionData.StockMarket, mOptionData.optionData.StockCode);
			
			float fStockPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData );
			float fOptionExecutePrice = mOptionData.optionData.StrikePrice;
			float fOptionNowPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, mOptionData);
			float fStockTargetPrice = 0; //标的平本价
			
			//0->看涨(认购)  1->看跌(认沽)
		    if (mOptionData.optionData.OptionCP == 0) {
		        fStockTargetPrice = fOptionExecutePrice + fOptionNowPrice;
		    }
		    else if (mOptionData.optionData.OptionCP == 1)
		    {
		        fStockTargetPrice = fOptionExecutePrice - fOptionNowPrice;
		    }
		    
		    float YPriceScale = fOptionNowPrice/s_fLostValueHeight/(AnalyseView_Profit_Num-1);//"收益"每挡之间的价格差
		    float XPriceScale = fOptionNowPrice/s_fLostValueWidth/(AnalyseView_Price_Num-1);//"价格"每挡之间的价格差
		    
		    if((XPriceScale*3+fStockTargetPrice) <= fStockPrice )
		    {
		        XPriceScale = ((1.05f*fStockPrice - fStockTargetPrice)/3.0f);
		    }
		    else if ((fStockTargetPrice-XPriceScale*3) > fStockPrice)
		    {
		        XPriceScale = ((fStockPrice - 1.05f*fStockTargetPrice)/3.0f);
		    }
		    
		    if (XPriceScale < 0)
		    {
		    	XPriceScale = -XPriceScale;
		    }
		    
		    if (fOptionNowPrice < 0.03f) {
		        YPriceScale = 0.03f;
		    }
		    else if (fOptionNowPrice < 0.5f)
		    {
		        YPriceScale = fOptionNowPrice;
		    }
		    profitYbl = fOptionNowPrice/(YPriceScale*8);
		    for (int i=0; i<AnalyseView_Profit_Num; i++) {
		        mProfit[i] = (i-AnalyseView_Profit_Num/2)*YPriceScale;
			}
		    for (int i=0; i<AnalyseView_Price_Num; i++) {
		        mPrice[i] = fStockTargetPrice + (i-AnalyseView_Price_Num/2)*XPriceScale;
			}
		}
		
		//update view
		public void updateAllData() {
			updateProfitData();
			drawInit();
			this.invalidate();
		}

		private void drawNow(Canvas canvas) {
			L.i("ScreenDetailActivity", "Received push data1 drawNow");
			updateProfit(canvas);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			//
			super.onLayout(changed, left, top, right, bottom);

			if (changed) {
				mClientRect.set(left, top, right, bottom);
				drawInit();
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			drawNow(canvas);
		}

		//update GL area
		public void updateProfit(Canvas canvas) {
			//draw background such as coordinate line
			drawBackground(canvas);
			drawScale(canvas);
			drawProfitLine(canvas);

		}

		//draw background such as coordinate line
		protected void drawBackground(Canvas canvas) {
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.DKGRAY);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(1); 
			mPaint.setPathEffect(null);
			mPaint.setShader(null);

			
			//draw profit
			for (int i = 0; i < AnalyseView_Profit_Num; i++) {
				if (i == AnalyseView_Profit_Num/2)
					continue;

				canvas.drawLine(mStartXCoor, mStartYCoor + mDrawHeight - i*mDrawHeight/(AnalyseView_Profit_Num - 1), mStartXCoor
						+ mDrawWidth, mStartYCoor + mDrawHeight - i*mDrawHeight/(AnalyseView_Profit_Num - 1), mPaint);
			}
			//draw price
			for (int i = 0; i < AnalyseView_Price_Num; i++) {
				if (i == AnalyseView_Price_Num/2)
				{
					canvas.drawLine(mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num - 1), mStartYCoor, 
							mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num - 1), mStartYCoor + mDrawHeight*2/(AnalyseView_Profit_Num - 1), mPaint);
					canvas.drawLine(mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num - 1), mStartYCoor + mDrawHeight*6/(AnalyseView_Profit_Num - 1), 
							mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num - 1), mStartYCoor + mDrawHeight, mPaint);
				}else
				{
					canvas.drawLine(mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num - 1), mStartYCoor, 
						mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num - 1), mStartYCoor + mDrawHeight, mPaint);
				}
			}

			//draw 收益 and 价格 text
			mPaint.setTextSize(mFontSize);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setColor(Color.GRAY);
			mPaint.setTextAlign(Paint.Align.RIGHT);
			String temp = "收益";
			float t_width = mPaint.measureText(temp);
			float left = mStartXCoor - t_width - 2;
			ViewTools.DrawText(canvas, temp, (int) left,
					(int) (left + t_width), (int) mStartYCoor, 0, mPaint);
			
			temp = "价格";
			left = mStartXCoor + mDrawWidth - t_width + 3;
			ViewTools.DrawText(canvas, temp, (int) left,
					(int) (left + t_width), (int) (mStartYCoor + mDrawHeight), 0, mPaint);
		}
		
		protected void drawScale(Canvas canvas) {
			mPaint.setAntiAlias(true);
			mPaint.setColor(ViewTools.getColor(1));
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setPathEffect(null);
			mPaint.setShader(null);
			
			//收益刻度
			for (int i = 0; i < AnalyseView_Profit_Num - 1; i++)
			{
				if (i > AnalyseView_Profit_Num/2)
				{
					mPaint.setColor(ViewTools.getColor(1));
				}else if (i == AnalyseView_Profit_Num/2)
				{
					mPaint.setColor(ViewTools.getColor(0));
				}else
				{
					mPaint.setColor(ViewTools.getColor(-1));
				}
				
				String strProfit = ViewTools.getStringByFloatPrice(mProfit[i], 0, mOptionData.PriceDecimal);
				float t_width = mPaint.measureText(strProfit);
				ViewTools.DrawText(canvas, strProfit, (int) (mStartXCoor - t_width - 3),
						(int) (mStartXCoor), (int) (mStartYCoor + mDrawHeight - mDrawHeight*i/(AnalyseView_Profit_Num-1) - mHeight_Font*2/3), 0, mPaint);
			}
			//价格刻度
			mPaint.setColor(Color.GRAY);
			for (int i = 0; i < AnalyseView_Price_Num - 1; i++)
			{
				String strPrice = ViewTools.getStringByFloatPrice(mPrice[i], 0, mOptionData.PriceDecimal);
				float t_width = mPaint.measureText(strPrice);

				float left = mStartXCoor + i*mDrawWidth/(AnalyseView_Price_Num-1)-t_width/2;
				ViewTools.DrawText(canvas, strPrice, (int) left,
						(int) (left + t_width), (int) (mStartYCoor + mDrawHeight + 3), 0, mPaint);
			}

			mPaint.setColor(Color.GRAY); 
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(2.0f); 
			PathEffect effects = new DashPathEffect(new float[] {3, 2}, 0);
			mPaint.setPathEffect(effects);  
			//PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5}, 1);
			Path path = new Path();
        	path.moveTo(mStartXCoor + mDrawWidth/2, mStartYCoor+mDrawHeight/4);
        	path.lineTo(mStartXCoor + mDrawWidth/2, mStartYCoor+mDrawHeight*3/4);
        	canvas.drawPath(path, mPaint);
        	
        	path.moveTo(mStartXCoor, mStartYCoor+mDrawHeight/2);
        	path.lineTo(mStartXCoor + mDrawWidth, mStartYCoor+mDrawHeight/2);
        	canvas.drawPath(path, mPaint);

		}
		
		protected void drawProfitLine(Canvas canvas) {
			updateProfitData();
			
			linePaint.setAntiAlias(true);
			linePaint.setPathEffect(null);
			linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
			linePaint.setStrokeWidth(1);
			linePaint.setColor(ColorConstant.COLOR_GAIN_RED);
			
		    Path pathGain = new Path();//定义一条路径   
		    Path pathLoss = new Path();
		    
		    if (mOptionData.optionData.OptionCP == 0) {//看涨
		    	linePaint.setColor(ColorConstant.COLOR_GAIN_RED);
		    	pathGain.moveTo(mStartXCoor+mDrawWidth/2, (float) (mStartYCoor+mDrawHeight/2-0.5));
		    	pathGain.lineTo(mStartXCoor+mDrawWidth, mStartYCoor);
		    	pathGain.lineTo(mStartXCoor+mDrawWidth, (float) (mStartYCoor+mDrawHeight/2-0.5));
		    	pathGain.lineTo(mStartXCoor+mDrawWidth/2, (float) (mStartYCoor+mDrawHeight/2-0.5));
		    	
		    	canvas.drawPath(pathGain, linePaint);
		        
		    	linePaint.setColor(ColorConstant.COLOR_GAIN_GREEN);
		    	pathLoss.moveTo(mStartXCoor, (float) (mStartYCoor+mDrawHeight/2+0.5));
		    	pathLoss.lineTo(mStartXCoor+mDrawWidth/2, (float) (mStartYCoor+mDrawHeight/2+0.5));
		    	pathLoss.lineTo(mStartXCoor+mDrawWidth/2-mDrawWidth*profitYbl, mStartYCoor+mDrawHeight*1/2+mDrawHeight*profitYbl);
		    	pathLoss.lineTo(mStartXCoor, mStartYCoor+mDrawHeight*1/2+mDrawHeight*profitYbl);
		    	pathLoss.lineTo(mStartXCoor, (float) (mStartYCoor+mDrawHeight/2+0.5));
		    	
		    	canvas.drawPath(pathLoss, linePaint);
		        
		    	
		    }else if (mOptionData.optionData.OptionCP == 1) {//看跌
		    	linePaint.setColor(ColorConstant.COLOR_GAIN_RED);
		    	pathGain.moveTo(mStartXCoor, mStartYCoor);
		    	pathGain.lineTo(mStartXCoor+mDrawWidth/2, (float) (mStartYCoor+mDrawHeight/2-0.5));
		    	pathGain.lineTo(mStartXCoor, (float) (mStartYCoor+mDrawHeight/2-0.5));
		    	pathGain.lineTo(mStartXCoor, mStartYCoor);
		    	
		    	canvas.drawPath(pathGain, linePaint);
		        
		    	linePaint.setColor(ColorConstant.COLOR_GAIN_GREEN);
		    	pathLoss.moveTo(mStartXCoor+mDrawWidth/2, (float) (mStartYCoor+mDrawHeight/2+0.5));
		    	pathLoss.lineTo(mStartXCoor+mDrawWidth, (float) (mStartYCoor+mDrawHeight/2+0.5));
		    	pathLoss.lineTo(mStartXCoor+mDrawWidth, mStartYCoor+mDrawHeight*1/2+mDrawHeight*profitYbl);
		    	pathLoss.lineTo(mStartXCoor+mDrawWidth*1/2+mDrawWidth*profitYbl, mStartYCoor+mDrawHeight*1/2+mDrawHeight*profitYbl);
		    	pathLoss.lineTo(mStartXCoor+mDrawWidth/2, (float) (mStartYCoor+mDrawHeight/2+0.5));
		    	
		    	canvas.drawPath(pathLoss, linePaint);
		    }
		    
		    //画边界线
		    linePaint.setColor(Color.argb(255, 250, 235, 0));
		    linePaint.setStrokeWidth(3);
		    //0->看涨(认购)  1->看跌(认沽)
		    if (mOptionData.optionData.OptionCP == 0) {
		        
				canvas.drawLine(mStartXCoor, mStartYCoor + mDrawHeight*1/2 + mDrawHeight*profitYbl, 
						mStartXCoor + mDrawWidth*1/2 - mDrawWidth * profitYbl, 
						mStartYCoor + mDrawHeight*1/2 + mDrawHeight * profitYbl, linePaint);
				canvas.drawLine(mStartXCoor + mDrawWidth*1/2 - mDrawWidth*profitYbl, 
						mStartYCoor + mDrawHeight*1/2 + mDrawHeight * profitYbl,
						mStartXCoor + mDrawWidth,
						mStartYCoor, linePaint);
		    }
		    else if (mOptionData.optionData.OptionCP == 1)
		    {
		    	canvas.drawLine(mStartXCoor, mStartYCoor, 
		    			mStartXCoor + mDrawWidth*1/2 + mDrawWidth*profitYbl,
		    			mStartYCoor + mDrawHeight*1/2 + mDrawHeight*profitYbl, linePaint);
		        
		    	canvas.drawLine(mStartXCoor + mDrawWidth*1/2 + mDrawWidth*profitYbl,
		    			mStartYCoor + mDrawHeight*1/2 + mDrawHeight*profitYbl, 
		    			mStartXCoor + mDrawWidth,
		    			mStartYCoor + mDrawHeight*1/2 + mDrawHeight*profitYbl, linePaint);
		    }
		    //draw 标的当前价格位置
		    if (mPrice[AnalyseView_Price_Num-1] - mPrice[0] != 0)
		    {
		        TagLocalStockData stockData = new TagLocalStockData();//标的行情
		        mMyApp.mStockConfigData.search(stockData, mOptionData.optionData.StockMarket, mOptionData.optionData.StockCode);
		        float fStockPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData);
		        
		        linePaint.setStrokeWidth(1.5f);
		        linePaint.setColor(Color.BLACK);
		        float nXPos = mStartXCoor + (fStockPrice - mPrice[0])*mDrawWidth/(mPrice[AnalyseView_Price_Num-1]-mPrice[0]);
		        canvas.drawLine(nXPos, mStartYCoor, nXPos, mStartYCoor+mDrawHeight, linePaint);
		        
		        linePaint.setAntiAlias(true);
		        linePaint.setColor(Color.BLACK);
		        linePaint.setTextSize(mFontSize);

		        String price = ViewTools.getStringByFloatPrice(fStockPrice, 0, stockData.PriceDecimal);
		        float t_width = linePaint.measureText(price);
		        int t_height = ViewTools.getFontHeight(mFontSize);
		        canvas.drawRect(nXPos - t_width/2 - 1, mStartYCoor+mDrawHeight-t_height, nXPos + t_width/2 - 1, mStartYCoor+mDrawHeight , linePaint);
		        
		        linePaint.setStrokeWidth(0);
		        linePaint.setColor(Color.WHITE);
		        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		        canvas.drawText(price, nXPos - t_width/2 - 1, mStartYCoor+mDrawHeight-3, linePaint);
		    }    
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.detail_rb_pinghuan:// 平缓
			bdyqbl = 0.7f;
			showProgress();

			new Thread() {
				public void run() {
					mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
							mOptionData, mStockData, mPjsyRecord);
					
					mAdapter.setLists(mTagProfitRecords);
					mHandler.sendEmptyMessage(UI_UPDATE);
				};
			}.start();
			
		
			break;
		case R.id.detail_rb_zhengchang:// 正常
			bdyqbl = 1.0f;

			showProgress();
			new Thread() {
				public void run() {
					mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
							mOptionData, mStockData, mPjsyRecord);

					mAdapter.setLists(mTagProfitRecords);
					mHandler.sendEmptyMessage(UI_UPDATE);
				};
			}.start();
			
			break;
		case R.id.detail_rb_julie:// 剧烈
			bdyqbl = 1.5f;

			showProgress();
			new Thread() {
				public void run() {
					L.e("bdyqbl__1.5f", mOptionData.code + "..." + mStockData.market+"****"+bdyqbl+"****");
					mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
							mOptionData, mStockData, mPjsyRecord);

					mAdapter.setLists(mTagProfitRecords);
					mHandler.sendEmptyMessage(UI_UPDATE);
				};
			}.start();
			break;

		default:
			break;
		}
	}
	
	private void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(mContext, R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			mProgress.setCancelable(true);
		}
		mProgress.show();
	}

	private void closeProgress() {

		if (mProgress != null && mProgress.isShowing()) {
			L.i("BaseActivity", "closeProgress--->" + this.toString());
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}
}
