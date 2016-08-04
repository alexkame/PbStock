package com.pengbo.mhdzq.view;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 价格五
 * 详细页中的  -----五档价格 ----
 * @author pobo
 * 
 */
public class ZQ_Ctrl_Trend_FivePrice extends LinearLayout{
	
	public final static String TAG = ZQ_Ctrl_Trend_FivePrice.class.getSimpleName();

	protected Context mContext;
	
	private TagLocalStockData mOptionData;
	
	private	AutoScaleTextView[] mMaicPrice, mMaicAmount ;
	
	private	AutoScaleTextView[] mMaijPrice, mMaijAmount ;
	
	public ZQ_Ctrl_Trend_FivePrice(Context context) {
		super(context);
		mContext = context;
	}
	
	public ZQ_Ctrl_Trend_FivePrice(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate()
    {
		super.onFinishInflate();
		initView();
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	public void initView()
	{
		L.i(TAG, "initView");
		setFocusable(true);
        setFocusableInTouchMode(true);
		initCtrls();
	}

	public void initCtrls()
	{
		AutoScaleTextView[] mMaicPrice1 = {
				(AutoScaleTextView)findViewById(R.id.maic1_price),
				(AutoScaleTextView)findViewById(R.id.maic2_price),
				(AutoScaleTextView)findViewById(R.id.maic3_price),
				(AutoScaleTextView)findViewById(R.id.maic4_price),
				(AutoScaleTextView)findViewById(R.id.maic5_price)},
				   mMaicAmount1 = {
				(AutoScaleTextView)findViewById(R.id.maic1_amount),
				(AutoScaleTextView)findViewById(R.id.maic2_amount),
				(AutoScaleTextView)findViewById(R.id.maic3_amount),
				(AutoScaleTextView)findViewById(R.id.maic4_amount),
				(AutoScaleTextView)findViewById(R.id.maic5_amount)
												};
		
		AutoScaleTextView[] mMaijPrice1 = {
				(AutoScaleTextView)findViewById(R.id.maij1_price),
				(AutoScaleTextView)findViewById(R.id.maij2_price),
				(AutoScaleTextView)findViewById(R.id.maij3_price),
				(AutoScaleTextView)findViewById(R.id.maij4_price),
				(AutoScaleTextView)findViewById(R.id.maij5_price)},
				  mMaijAmount1 = {
				(AutoScaleTextView)findViewById(R.id.maij1_amount),
				(AutoScaleTextView)findViewById(R.id.maij2_amount),
				(AutoScaleTextView)findViewById(R.id.maij3_amount),
				(AutoScaleTextView)findViewById(R.id.maij4_amount),
				(AutoScaleTextView)findViewById(R.id.maij5_amount)
												};	
		
		this.mMaicPrice = mMaicPrice1;
		this.mMaicAmount = mMaicAmount1;
		
		this.mMaijPrice = mMaijPrice1;
		this.mMaijAmount = mMaijAmount1;
	}
	
	public void updateData(TagLocalStockData data)
	{
		mOptionData = data;
		updateCtrls();
	}
	
	public void updateCtrls()
	{
		if (mOptionData == null)
		{
			L.e(TAG, "updateCtrls--->mOptionData == null");
			return;
		}
		String price_temp = new String();
		for(int i=0; i< mOptionData.HQData.sellPrice.length ; i++)
		{
			price_temp = ViewTools.getStringByPrice(mOptionData.HQData.sellPrice[i],0,mOptionData.PriceDecimal, mOptionData.PriceRate);
			mMaicPrice[i].setText(price_temp);
			mMaicPrice[i].setTextColor(ViewTools.getColor(mOptionData.HQData.sellPrice[i], mOptionData.HQData.nLastClear));
			
			price_temp = ViewTools.getStringByVolume(mOptionData.HQData.sellVolume[i], mOptionData.market, mOptionData.VolUnit, 6, false);
			mMaicAmount[i].setText(price_temp);
			mMaicAmount[i].setTextColor(Color.BLACK);
		}
		
		for(int i=0; i< mOptionData.HQData.buyPrice.length ; i++){
			price_temp = ViewTools.getStringByPrice(mOptionData.HQData.buyPrice[i],0,mOptionData.PriceDecimal, mOptionData.PriceRate);
			mMaijPrice[i].setText(price_temp);
			mMaijPrice[i].setTextColor(ViewTools.getColor(mOptionData.HQData.buyPrice[i], mOptionData.HQData.nLastClear));
    		
			price_temp = ViewTools.getStringByVolume(mOptionData.HQData.buyVolume[i], mOptionData.market, mOptionData.VolUnit, 6, false);
			mMaijAmount[i].setText(price_temp);
			mMaijAmount[i].setTextColor(Color.BLACK);
		}
	}	
}