package com.pengbo.mhdcx.ui.activity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdcx.fragment.TradeListAll;
import com.pengbo.mhdcx.fragment.TradeMyStock;
import com.pengbo.mhdcx.fragment.TradeOptionListAll;
import com.pengbo.mhdcx.fragment.TradeOptionListMyStock;
import com.pengbo.mhdzq.main_activity.SplashActivity;
import com.pengbo.mhdzq.tools.ViewTools;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 期权列表 页
 * 
 * @author pobo
 * 
 */
public class TradeHeadOptionListActivity extends FragmentActivity implements OnClickListener,OnCheckedChangeListener{

	private TextView btn_back;
	private RadioGroup mRadioGroup;//  RadioButton
	private FragmentManager fm;
	private MyApp mMyApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mMyApp = (MyApp) this.getApplication();
		
		boolean isRestart = false;
		if(mMyApp.mCodeTableMarketNum <= 0) {
			isRestart = true;
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_header_option_list_activity);
		
		initView();

		if(isRestart) {
			onAppRestore();
		}
	}
	
	public void onAppRestore() {
		AppActivityManager.getAppManager().finishAllActivity();
		Intent i = new Intent(this, SplashActivity.class);
		this.startActivity(i);
		this.finish();
	}
	
	

	/**
	 * 初始化控件 
	 */
	private void initView() {
		btn_back=(TextView) this.findViewById(R.id.trade_option_list_activity_close);
		btn_back.setOnClickListener(this);
		
		//包含头部的 Radiogroup
		mRadioGroup=(RadioGroup) this.findViewById(R.id.trade_option_list_actiity_radiogroup);		
		mRadioGroup.setOnCheckedChangeListener(this);
		
		fm=getSupportFragmentManager();
		if (mMyApp.getMyStockNum(AppConstants.HQTYPE_QQ) > 0)
		{
			//mRadioGroup.check(R.id.rb_mystock);
			fm.beginTransaction().add(R.id.trade_option_list_framelayout, new TradeMyStock()).commit();
			((RadioButton) mRadioGroup.findViewById(R.id.rb_mystock)).setChecked(true);
		}else
		{
			//mRadioGroup.check(R.id.rb_all);
			fm.beginTransaction().add(R.id.trade_option_list_framelayout, new TradeListAll()).commit();
			((RadioButton) mRadioGroup.findViewById(R.id.rb_all)).setChecked(true);
		}
		//		
		
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.trade_option_list_activity_close:
			TradeHeadOptionListActivity.this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.rb_all:
			fm.beginTransaction().replace(R.id.trade_option_list_framelayout, new TradeListAll()).commit();
			break;
		case R.id.rb_mystock:
			fm.beginTransaction().replace(R.id.trade_option_list_framelayout, new TradeMyStock()).commit();
			break;
		}
	}
	
	@Override
	protected void onResume()
	{
		ViewTools.isShouldForegraund = true;
		super.onResume();
	}

	

}
