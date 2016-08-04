package com.pengbo.mhdzq.zq_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.main_activity.SplashActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.CHScrollView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

public class HdActivity extends Activity {
	public MyApp mMyApp;
	/**
	 * CHScrollView extends HorizontalScrollView
	 */
	public CHScrollView mTouchView;
	protected List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (PreferenceEngine.getInstance().getYXPMZDJRXMG()) {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			// 让屏幕保持不暗不关闭
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		mMyApp = (MyApp) getApplication();
		boolean isRestart = false;
		if (!(this instanceof SplashActivity)
				&& mMyApp.mCodeTableMarketNum <= 0) {
			isRestart = true;
		}
		super.onCreate(savedInstanceState);
		if (isRestart) {
			// 重启软件
			onAppRestore();
		} else {
			AppActivityManager.getAppManager().addActivity(this);
		}
	}

	@Override
	protected void onResume() {
		if (PreferenceEngine.getInstance().getYXPMZDJRXMG()) {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		ViewTools.isShouldForegraund = true;
		super.onResume();
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {

	}

	/**
	 * 
	 * @param hscrollView
	 *            数据横滚部分的除去第1列固定列后的其它数据，用CHScrollView包含
	 */
	public void addHViews(CHScrollView hscrollView) {

	}

	/**
	 * 重启软件
	 */
	public void onAppRestore() {
		// 关闭所有的Activity
		AppActivityManager.getAppManager().finishAllActivity();

		Intent i = new Intent(this, SplashActivity.class);
		this.startActivity(i);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		AppActivityManager.getAppManager().removeActivity(this);
		super.onDestroy();
	}
}
