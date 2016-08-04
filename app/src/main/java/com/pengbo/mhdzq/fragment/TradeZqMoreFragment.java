package com.pengbo.mhdzq.fragment;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.zq_activity.ZQDRCJActivity;
import com.pengbo.mhdzq.zq_activity.ZQLSCJActivity;
import com.pengbo.mhdzq.zq_activity.ZQLSWTActivity;
import com.pengbo.mhdzq.zq_activity.ZQZJLSActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZQBankToSecutityActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZQEditTradePassWordActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TradeZqMoreFragment extends Fragment implements OnClickListener {
	private MyApp mMyApp = null;
	private Activity mActivity = null;

	public View mView;
	private Button btn_logout;
	private RelativeLayout mDRCJlay,mLSWTlay,mLSCJlay,mLSZJLSlay,mYHZZ,mXGJYMM;  //银行转账   修改交易密码 
;

	private boolean mIsViewReady = false;


	public static TradeZqMoreFragment newInstance()
	{
		TradeZqMoreFragment f = new TradeZqMoreFragment();
	    return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.zq_trade_more_frame, null);

		initView();

		mIsViewReady = true;

		return mView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		mMyApp = (MyApp) this.mActivity.getApplication();
	}

	public void initData() {

	}

	public void initView() {
		btn_logout = (Button) mView.findViewById(R.id.logout);
		btn_logout.setOnClickListener(this);

		mLSWTlay = (RelativeLayout) mView.findViewById(R.id.setting_zq_layout1);
		mLSWTlay.setOnClickListener(this);
		
		mDRCJlay = (RelativeLayout) mView.findViewById(R.id.setting_zq_layout2);
		mDRCJlay.setOnClickListener(this);
		
		mLSCJlay = (RelativeLayout) mView.findViewById(R.id.setting_zq_layout3);
		mLSCJlay.setOnClickListener(this);
		
		mLSZJLSlay = (RelativeLayout) mView.findViewById(R.id.setting_zq_layout5);
		mLSZJLSlay.setOnClickListener(this);
		
		
		mYHZZ = (RelativeLayout) mView.findViewById(R.id.setting_zq_layout4);
		mYHZZ.setOnClickListener(this);
		
		mXGJYMM = (RelativeLayout) mView.findViewById(R.id.setting_zq_layout7);
		mXGJYMM.setOnClickListener(this);
		

		
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.logout: {
			mMyApp.setHQPushNetHandler(null);
			GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
			mMyApp.setTradeHandler(null);
			mMyApp.mTradeNet.closeConnect();

			mMyApp.mTradeData.clearStepData();
			mMyApp.mTradeData.mTradeLoginFlag = false;
			Toast.makeText(mActivity, "退出账户", Toast.LENGTH_SHORT).show();

			Intent intent = new Intent();
			intent.setClass(mActivity, ZhengQuanActivity.class);
			if (AppActivityManager.getAppManager().getZQActivity() != null) {
				AppActivityManager.getAppManager().finishActivity(
						AppActivityManager.getAppManager().getZQActivity());
			}

			intent.putExtra("ZQ_VIEW_INDEX", ZhengQuanFragment.ZQ_VIEW_JIAOYI);
			startActivity(intent);
			mActivity.finish();
		}
			break;


		case R.id.setting_zq_layout1: {
			Intent intent = new Intent();
			intent.setClass(mActivity, ZQLSWTActivity.class);
			startActivity(intent);
		}
			break;

		case R.id.setting_zq_layout2: {

			Intent intent = new Intent();
			intent.setClass(mActivity, ZQDRCJActivity.class);
			startActivity(intent);
		}
			break;
			
		case R.id.setting_zq_layout3:
		{
			Intent intent = new Intent();
			intent.setClass(mActivity, ZQLSCJActivity.class);
			startActivity(intent);

			break;
		}
		
		case R.id.setting_zq_layout5:
		{
			Intent intent = new Intent();
			intent.setClass(mActivity, ZQZJLSActivity.class);
			startActivity(intent);

			break;
		}
		
		
		case R.id.setting_zq_layout4:
		{
			Intent intent = new Intent();
			intent.setClass(mActivity, ZQBankToSecutityActivity.class);
			startActivity(intent);

			break;
		}
		
		case R.id.setting_zq_layout7:
		{
			Intent intent = new Intent();
			intent.setClass(mActivity, ZQEditTradePassWordActivity.class);
			startActivity(intent);

			break;
		}
		

		}
	}
}
