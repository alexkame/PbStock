package com.pengbo.mhdzq.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.view.ZQLandScapeKLineView.KLine;

/**
 *  K 线图右边显示 指标按钮----只有竖屏的时候有 
 * 
 * @author pobo
 * @date   2015-11-9 上午10:50:19
 * @className ZQ_LandScape_Ctrl_Kline_RightPanel.java
 * @verson 1.0.0
 */
public class ZQ_LandScape_Ctrl_Kline_RightPanel extends LinearLayout{
	public final static String TAG = ZQ_LandScape_Ctrl_Kline_RightPanel.class.getSimpleName();
	protected Context mContext;
	protected View mView;

	private TagLocalStockData mOptionData;
	
	private RadioGroup mRGFQ,mRGZB;//  复权   指标 
	private KLine mKLine;
	private int mTechType = ZQKLineView.TECH_VOLUME;
	
	public ZQ_LandScape_Ctrl_Kline_RightPanel(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 构造函数 
	 * @param context
	 */
	public ZQ_LandScape_Ctrl_Kline_RightPanel(Context context, KLine kline) {
		super(context);
		mContext = context;
		mKLine = kline;
		initView();
	}

	
	/**
	 * 构造函数 
	 * @param context
	 */
	public ZQ_LandScape_Ctrl_Kline_RightPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	public View getView() {
		return mView;
	}

	

	public void initView() {
		L.i(TAG, "initView");
		setFocusable(true);
		setFocusableInTouchMode(true);

		if (mView == null) {
			mView = LayoutInflater.from(mContext).inflate(
					R.layout.zq_kline_radiobutton_right_panel, null);
			addView(mView);

			ViewGroup.LayoutParams params = mView.getLayoutParams();
			params.width = LayoutParams.FILL_PARENT;
			params.height = LayoutParams.FILL_PARENT;
			mView.setLayoutParams(params);
		}
		
		mRGFQ=(RadioGroup) mView.findViewById(R.id.rg_zq_kline_right_panel_fq);
		mRGZB=(RadioGroup) mView.findViewById(R.id.rg_zq_kline_right_panel_zb);
		mRGFQ.setOnCheckedChangeListener(new checkedChangeListenerFQ());
		mRGZB.setOnCheckedChangeListener(new checkedChangeListenerZB());
	}

	public void updateData(TagLocalStockData data, int height) {
		mOptionData = data;
		updateCtrls(height);
	}

	public void updateCtrls(int height) {
		if (mOptionData == null) {
			L.e(TAG, "updateCtrls--->mOptionData == null");
			return;
		}
	}
	
	public void setTechType(int techType)
	{
		if (techType >= ZQKLineView.TECH_VOLUME && techType <= ZQKLineView.TECH_BIAS)
		{
			mTechType = techType;
			switch(mTechType)
			{
			case ZQKLineView.TECH_VOLUME:
				mRGZB.check(R.id.zq_kline_right_panel_rb_cjl);
				break;
			case ZQKLineView.TECH_MACD:
				mRGZB.check(R.id.zq_kline_right_panel_rb_macd);
				break;
			case ZQKLineView.TECH_KDJ:
				mRGZB.check(R.id.zq_kline_right_panel_rb_kdj);
				break;
			case ZQKLineView.TECH_RSI:
				mRGZB.check(R.id.zq_kline_right_panel_rb_rsi);
				break;
			case ZQKLineView.TECH_WR:
				mRGZB.check(R.id.zq_kline_right_panel_rb_wr);
				break;
			case ZQKLineView.TECH_BIAS:
				mRGZB.check(R.id.zq_kline_right_panel_rb_bias);
				break;
			}
		}
	}
	
	
	private  class checkedChangeListenerFQ implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkId) {
			switch (checkId) {
			case R.id.zq_kline_right_panel_rb_bfq:
			
				break;
			case R.id.zq_kline_right_panel_rb_qfq:
				
				break;
			case R.id.zq_kline_right_panel_rb_hfq:
	
				break;

			default:
				break;
			}
		}
		
	}
	
	
	
	private  class checkedChangeListenerZB implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkId) {
			switch (checkId) {
			case R.id.zq_kline_right_panel_rb_cjl:
			{
				if (mKLine != null)
				{
					mKLine.drawTechLine(ZQKLineView.TECH_VOLUME);
				}
			}
				break;
			case R.id.zq_kline_right_panel_rb_macd:
			{
				if (mKLine != null)
				{
					mKLine.drawTechLine(ZQKLineView.TECH_MACD);
				}
			}
				break;
			case R.id.zq_kline_right_panel_rb_kdj:
			{
				if (mKLine != null)
				{
					mKLine.drawTechLine(ZQKLineView.TECH_KDJ);
				}
			}
				break;
			case R.id.zq_kline_right_panel_rb_rsi:
			{
				if (mKLine != null)
				{
					mKLine.drawTechLine(ZQKLineView.TECH_RSI);
				}
			}
				break;
			case R.id.zq_kline_right_panel_rb_wr:
			{
				if (mKLine != null)
				{
					mKLine.drawTechLine(ZQKLineView.TECH_WR);
				}
			}
				break;
			case R.id.zq_kline_right_panel_rb_bias:
			{
				if (mKLine != null)
				{
					mKLine.drawTechLine(ZQKLineView.TECH_BIAS);
				}
			}
				break;

			default:
				break;
			}
		}
		
	}
	
	
	

}
