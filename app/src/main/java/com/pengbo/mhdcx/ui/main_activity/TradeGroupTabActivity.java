package com.pengbo.mhdcx.ui.main_activity;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdcx.ui.trade_activity.TradeDetailActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_trade_activity.ZqTradeDetailActivity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("deprecation")
public class TradeGroupTabActivity extends ActivityGroup {
	private static final String TAG = "TradeGroupTabActivity";

	/**
	 * 定义一个 静态的ActivityGroup 变量 ，用于管理 本Group中的 Activity
	 */
	public static ActivityGroup group;
	private MyApp mMyApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		group = this;
		mMyApp = (MyApp) this.getApplication();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	/**
	 * 重写 onResume()
	 */
	@Override
	protected void onResume() {
		// 把界面切换到 onResume 方法中是因为，从其他选项卡换回来的时候 调用的是 OnResume方法
		// 要跳转的页面
		ViewTools.isShouldForegraund = true;

		if (mMyApp.mTradeData.mHQType != mMyApp.getCurrentHQType()
				|| !mMyApp.mTradeData.mTradeLoginFlag) {

			Activity activity = group.getLocalActivityManager()
					.getCurrentActivity();
			if (activity != null
					&& (activity instanceof TradeDetailActivity && mMyApp.mTradeData.mHQType == AppConstants.HQTYPE_NULL)) {
				gotoTradeDetailView(false);
			} else {
				loadLoginView();
			}
		} else {
			gotoTradeDetailView(true);
		}
		super.onResume();
	}

	public void loadLoginView() {
		L.i(TAG, "loadLoginView");
		// mMyApp.mTradeData.mTradeLoginFlag = false;
		MIniFile iniFile = new MIniFile();
		iniFile.setFilePath(getApplicationContext(), MyApp.TRADE_ADDRPATH);

		// smsverify=false
		String smsverify = iniFile.ReadString("base", "smsverify", "false");
		if (smsverify.equalsIgnoreCase("true")) {
			mMyApp.isSMSVerify = true;
		} else {
			mMyApp.isSMSVerify = false;
		}

		Activity activity = group.getLocalActivityManager()
				.getCurrentActivity();

		String strPhone = PreferenceEngine.getInstance().getPhoneForVerify();
		if (mMyApp.isSMSVerify && strPhone.isEmpty()) {
			if (activity != null
					&& (activity instanceof AccountRegisterActivity)) {
			} else {
				group.getLocalActivityManager().removeAllActivities();
				Intent intent = new Intent(this, AccountRegisterActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// 把一个Activity转换成一个 View
				View view = group.getLocalActivityManager()
						.startActivity("AccountRegisterActivity", intent)
						.getDecorView();

				group.setContentView(view);
			}
		} else {
			if (activity != null && (activity instanceof TradeLoginActivity)) {

			} else {
				group.getLocalActivityManager().removeAllActivities();
				Intent intent = new Intent(this, TradeLoginActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// 把一个Activity转换成一个 View
				View view = group.getLocalActivityManager()
						.startActivity("TradeLoginActivity", intent)
						.getDecorView();

				group.setContentView(view);
			}
		}

	}

	public void gotoTradeDetailView(boolean check) {
		L.i(TAG, "gotoTradeDetailView");
		if (!mMyApp.mTradeData.mTradeLoginFlag && check) {
			L.i(TAG, "gotoTradeDetailView-haven't login,goto login");
			loadLoginView();
			return;
		}
		L.i(TAG, "gotoTradeDetailView-switch to trade detail view");

		Intent intent = new Intent(this, TradeDetailActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (mMyApp.mbDirectInOrderPage) {
			Bundle mBundle = new Bundle();
			mBundle.putInt(TradeDetailActivity.INTENT_SERIALIZABLE_CURRENTPAGE,
					TradeDetailActivity.TRADE_ORDER);
			intent.putExtras(mBundle);
		}

		Activity activity = group.getLocalActivityManager()
				.getCurrentActivity();
		if (activity != null && !(activity instanceof TradeDetailActivity)) {
			group.getLocalActivityManager().removeAllActivities();
		}
		// 把一个Activity转换成一个 View
		View view = group.getLocalActivityManager()
				.startActivity("TradeDetailActivity", intent).getDecorView();
		group.setContentView(view);
	}

}
