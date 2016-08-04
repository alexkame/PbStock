package com.pengbo.mhdcx.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdzq.main_activity.MainActivity;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;

public class WelcomeGuideActivity extends HdActivity {

	private Button guideButton;
	private LinearLayout layout_general,layout_advanced;
	private ImageView imageview_general,imageview_advanced;
	private int mTradeMode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_guide);
		
		mTradeMode = PreferenceEngine.getInstance().getTradeMode();
		// 初始化view
		initView();
		saveObjectToShared();
	}

	/**
	 * 保存一个默认的筛选条件 
	 */
	private void saveObjectToShared() {
		ScreenCondition sc=new ScreenCondition();
		sc.setCode(ScreenCondition.SCREEN_ALL_STOCK_CODE);
		sc.setMarket((short) 0);
		sc.setName("全部标的");
		sc.setGridViewId(4);
		sc.setField_hq_zdf(0.05f);
		sc.setLeverId(3);
		sc.setVitality(1);

		String mTiaoJianInfo = sc.getCode() + "|" + sc.getMarket() + "|" + sc.getName() + "|" + sc.getGridViewId() + "|" + sc.getField_hq_zdf() + "|" + sc.getLeverId() + "|"
				+ sc.getVitality();
		PreferenceEngine.getInstance().saveHQQueryCondition(mTiaoJianInfo);
	}

	private void initView()
	{
		guideButton = (Button) findViewById(R.id.btn_before_guide);
		layout_general=(LinearLayout) this.findViewById(R.id.before_guide_general_layout);
		layout_advanced=(LinearLayout) this.findViewById(R.id.before_guide_advanced_layout1);
		
		imageview_general=(ImageView) this.findViewById(R.id.berore_guide_general_imageview);
		imageview_advanced=(ImageView) this.findViewById(R.id.berore_guide_advanced_imageview);

		if (mTradeMode == Trade_Define.TRADE_MODE_GAOJI) {
			imageview_general.setVisibility(View.GONE);
			imageview_advanced.setVisibility(View.VISIBLE);
		}else if (mTradeMode == Trade_Define.TRADE_MODE_PUTONG)
		{
			imageview_general.setVisibility(View.VISIBLE);
			imageview_advanced.setVisibility(View.GONE);
		}
		layout_general.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imageview_general.setVisibility(View.VISIBLE);
				imageview_advanced.setVisibility(View.GONE);
				mTradeMode = Trade_Define.TRADE_MODE_PUTONG;
			}
		});
		layout_advanced.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imageview_advanced.setVisibility(View.VISIBLE);
				imageview_general.setVisibility(View.GONE);
				mTradeMode = Trade_Define.TRADE_MODE_GAOJI;
			}
		});
		
		guideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PreferenceEngine.getInstance().saveTradeMode(mTradeMode);
				int mId = getIntent().getIntExtra("mId", MainTabActivity.PAGE_MYSTOCK);
				Intent intent = new Intent(WelcomeGuideActivity.this, MainTabActivity.class);
				intent.putExtra("mId", mId);
				startActivity(intent);

				WelcomeGuideActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		saveObjectToShared();
		super.onDestroy();
	}
}
