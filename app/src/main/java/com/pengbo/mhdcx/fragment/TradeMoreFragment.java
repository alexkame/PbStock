package com.pengbo.mhdcx.fragment;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.ui.trade_activity.BankSecuritiesTransferActivity;
import com.pengbo.mhdcx.ui.trade_activity.EditIdentifyPassword;
import com.pengbo.mhdcx.ui.trade_activity.EditPassWordActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeBeiDuiSuoDingActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeLiShiCJActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeLiShiWTActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
/**
 * 交易中更多页
 * @author pobo
 *
 */
public class TradeMoreFragment extends Fragment implements OnClickListener {
	
	private MyApp mMyApp;
	public View mView;
	public RelativeLayout mRel1;
	public RelativeLayout mRel2;
	public RelativeLayout mRel3;
	public RelativeLayout mRel4;
	public RelativeLayout mRel5;
	public RelativeLayout mRel6;
	private View mViewSepRZKL;
	
	public Activity mActivity;
	Intent intent;
	
	
	public Button btn_exit;
	
	public TradeMoreFragment(MyApp myApp)
	{
		mMyApp = myApp;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		this.mActivity = activity;
		super.onAttach(activity);
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		mView=inflater.inflate(R.layout.trade_more_fragment, null);
		initView();
		return mView;
	}
	
	@Override
	public void onResume() {
		if (!this.isHidden()) {
			mMyApp.setHQPushNetHandler(null);
		}
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if(!hidden)
		{
			mMyApp.setHQPushNetHandler(null);
		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mRel1=(RelativeLayout) mView.findViewById(R.id.re_1);
		mRel1.setOnClickListener(this);
		
		mRel2=(RelativeLayout) mView.findViewById(R.id.re_2);
		mRel2.setOnClickListener(this);
		
		mRel3=(RelativeLayout) mView.findViewById(R.id.re_3);
		mRel3.setOnClickListener(this);
		
		mRel4=(RelativeLayout) mView.findViewById(R.id.re_4);
		mRel4.setOnClickListener(this);
		
		mRel5=(RelativeLayout) mView.findViewById(R.id.re_5);
		mRel5.setOnClickListener(this);
		
		mRel6=(RelativeLayout) mView.findViewById(R.id.re_6);
		mRel6.setOnClickListener(this);
		
		btn_exit=(Button) mView.findViewById(R.id.exit_account);
		btn_exit.setOnClickListener(this);
		
		mViewSepRZKL = (View) mView.findViewById(R.id.view_re3);
		
		if(mMyApp.isRenZhengKouLin)
		{
			this.mRel3.setVisibility(View.VISIBLE);
			mViewSepRZKL.setVisibility(View.VISIBLE);
		}else
		{
			this.mRel3.setVisibility(View.GONE);
			mViewSepRZKL.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re_1:
			intent=new Intent();
			intent.setClass(mActivity, BankSecuritiesTransferActivity.class);
			startActivity(intent);
			break;
			
		case R.id.re_2:
			intent=new Intent();
			intent.setClass(mActivity, EditPassWordActivity.class);
			startActivity(intent);
			break;
		case R.id.re_3:
			intent=new Intent();
			intent.setClass(mActivity,EditIdentifyPassword.class );
			startActivity(intent);
			break;
		case R.id.re_4:
			intent=new Intent();
			intent.setClass(mActivity,TradeLiShiWTActivity.class );
			startActivity(intent);
			break;
		case R.id.re_5:
			intent=new Intent();
			intent.setClass(mActivity,TradeLiShiCJActivity.class );
			startActivity(intent);
			break;
		case R.id.re_6:
			intent=new Intent();
			intent.setClass(mActivity,TradeBeiDuiSuoDingActivity.class );
			startActivity(intent);
			break;
		case R.id.exit_account:
			mMyApp.setHQPushNetHandler(null);
			GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
			mMyApp.setCurrentOption(null);
			mMyApp.mTradeData.quitTrade();
			Toast.makeText(mActivity, "退出账户", Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent();
			MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
			
			if (act != null) {
				act.setChangePage(MainTabActivity.PAGE_TRADE);
			}
			intent.setClass(mActivity, MainTabActivity.class);
			startActivity(intent);

			break;
		default:
			break;
		}
		
	}
	
	
}
