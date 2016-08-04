package com.pengbo.mhdcx.ui.activity;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.tools.PreferenceEngine;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SetModelActivity extends BaseActivity {
	public static final int RESULT_CODE_MODEL=10001;
	
	/***** 点击 交易模式后 弹出 的 高级 及 普通 模式 的选择 及 其中 按钮的 点击 及 保存 ****/
	private Button guideButton;
	private LinearLayout layout_general, layout_advanced;
	private ImageView imageview_general, imageview_advanced;
	private int mTradeMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_guide);
		setUpView();
	}

	private void setUpView() {

		guideButton = (Button) findViewById(R.id.btn_before_guide);
		layout_general = (LinearLayout) this
				.findViewById(R.id.before_guide_general_layout);
		layout_advanced = (LinearLayout) this
				.findViewById(R.id.before_guide_advanced_layout1);

		imageview_general = (ImageView) this
				.findViewById(R.id.berore_guide_general_imageview);
		imageview_advanced = (ImageView) this
				.findViewById(R.id.berore_guide_advanced_imageview);
		
		Intent intent=getIntent();
		mTradeMode = intent.getIntExtra("TradeMode", Trade_Define.TRADE_MODE_PUTONG);
		
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
				    mTradeMode = Trade_Define.TRADE_MODE_PUTONG;
					imageview_general.setVisibility(View.VISIBLE);
					imageview_advanced.setVisibility(View.GONE);
			}
		});
		
		layout_advanced.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTradeMode = Trade_Define.TRADE_MODE_GAOJI;
				imageview_advanced.setVisibility(View.VISIBLE);
				imageview_general.setVisibility(View.GONE);
			}
		});

		guideButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
	
				PreferenceEngine.getInstance().saveTradeMode(mTradeMode);
				setResult(RESULT_CODE_MODEL, intent);
				SetModelActivity.this.finish();
			}
		});

	}
	
	
	
	/**
	 * 监听 back  监听暂时有误
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
		}
		return super.onKeyDown(keyCode, event);
	}
}
