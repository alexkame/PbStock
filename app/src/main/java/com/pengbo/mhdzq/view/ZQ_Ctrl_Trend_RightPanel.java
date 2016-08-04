package com.pengbo.mhdzq.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ScrollLayout;
import com.pengbo.mhdzq.tools.ScrollLayout.onScrollChangedListener;

/**
 * 横屏   竖屏  都有 
 * 走势图右边控件，显示价格五或详细
 * 
 * @author pobo
 * 
 */
public class ZQ_Ctrl_Trend_RightPanel extends RelativeLayout implements OnCheckedChangeListener
{
	public final static String TAG	= ZQ_Ctrl_Trend_RightPanel.class.getSimpleName();
	
	private final static int SCREEN_FIVE = 0;
	private final static int SCREEN_DETAIL = SCREEN_FIVE + 1;
	
	protected Context		mContext;
	protected View			mView;
	
	private TagLocalStockData		mOptionData;
	private ArrayList<TagLocalDealData> mDealDataArray;
	
	private RadioGroup mRgFiveDetail;
	private ZQ_Ctrl_Trend_Detail mDetailView;
	private ZQ_Ctrl_Trend_FivePrice mFivePriceView;
	private ScrollLayout mScrollLayout;
	
	
	public ZQ_Ctrl_Trend_RightPanel(Context context) {
		super(context);
		
		mContext = context;
		initView();
	}
	
	public ZQ_Ctrl_Trend_RightPanel(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		initView();
	}
	
	
	public View getView()
	{
		return mView;
	}

	public void initView()
	{
		L.i(TAG, "initView");
		setFocusable(true);
        setFocusableInTouchMode(true);
        
		if (mView==null)
		{
			mView = LayoutInflater.from(mContext).inflate(R.layout.zq_stockinfo_rightpanel, null);
			addView(mView);
			
			ViewGroup.LayoutParams params = mView.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			mView.setLayoutParams(params);
		}
		
		initCtrls();
	}

	public void initCtrls()
	{
		mRgFiveDetail = (RadioGroup) this
				.findViewById(R.id.five_detail_radiogroup);
		mRgFiveDetail.setOnCheckedChangeListener(this);
		
		if (mDetailView==null)
		{
			mDetailView = (ZQ_Ctrl_Trend_Detail) this.findViewById(R.id.detail_panel).findViewById(R.id.layout_detail);
		}
		if (mFivePriceView==null)
		{
			mFivePriceView = (ZQ_Ctrl_Trend_FivePrice) this.findViewById(R.id.fiveprice_panel).findViewById(R.id.layout_fiveprice);
		}
		
		mScrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
		mScrollLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (mScrollLayout.getCurrentScreenIndex() == SCREEN_FIVE)
					{
						mScrollLayout.snapToScreen(SCREEN_DETAIL);
					}else if (mScrollLayout.getCurrentScreenIndex() == SCREEN_DETAIL)
					{
						mScrollLayout.snapToScreen(SCREEN_FIVE);
					}else
					{
						mScrollLayout.snapToScreen(SCREEN_FIVE);
					}
			}
			
		});
		mScrollLayout.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (mScrollLayout.getCurScreen() == SCREEN_FIVE)
					{
						mScrollLayout.snapToScreen(SCREEN_DETAIL);
					}else if (mScrollLayout.getCurScreen() == SCREEN_DETAIL)
					{
						mScrollLayout.snapToScreen(SCREEN_FIVE);
					}else
					{
						mScrollLayout.snapToScreen(SCREEN_FIVE);
					}
				}
				return false;
			}
			
		});
		mScrollLayout.onChangeListener = new onScrollChangedListener() {
			@Override
			public void onChangeEvent(int viewid)
			{
                if (viewid == SCREEN_FIVE)
                {
                	((RadioButton) mRgFiveDetail.findViewById(R.id.radiobutton_five)).setChecked(true);
                }else if (viewid == SCREEN_DETAIL)
                {
                	((RadioButton) mRgFiveDetail.findViewById(R.id.radiobutton_detail)).setChecked(true);
                	//mRgFiveDetail.check(R.id.radiobutton_detail);
                }
			}
		};
	}
	
	public void updateData(TagLocalStockData data, ArrayList<TagLocalDealData> dealdata, int height)
	{
		mOptionData = data;
		mDealDataArray = dealdata;
		
		updateCtrls(height);
	}
	
	public void updateCtrls(int height)
	{
		if (mOptionData == null)
		{
			L.e(TAG, "updateCtrls--->mOptionData == null");
			return;
		}
		
		if (mFivePriceView!=null)
		{
			mFivePriceView.updateData(mOptionData);
		}
		if (mDetailView!=null)
		{
			mDetailView.updateData(mOptionData, mDealDataArray);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radiobutton_five:
			mScrollLayout.snapToScreen(SCREEN_FIVE);
			break;
		case R.id.radiobutton_detail:
			mScrollLayout.snapToScreen(SCREEN_DETAIL);
			break;
		default:
			break;
		}
	}
	
}
