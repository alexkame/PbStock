package com.pengbo.mhdzq.zq_activity;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.fragment.ZhengQuanFragment;
import com.pengbo.mhdzq.main_activity.SplashActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class ZhengQuanActivity extends FragmentActivity {
	public static final String TAG = "ZhengQuanActivity";
	private MyApp mMyApp;
	private Fragment mFrag;
	private int mId;

	public void onCreate(Bundle savedInstanceState) {
		mMyApp = (MyApp) this.getApplication();

		boolean isRestart = false;
		if (mMyApp.mCodeTableMarketNum <= 0) {
			isRestart = true;
		}
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zhengquan);

		mId = getIntent().getIntExtra("id", 0);
		initFragment();

		if (isRestart) {
			onAppRestore();
		} else {
			AppActivityManager.getAppManager().addActivity(this);
		}
	}

	public void onAppRestore() {
		AppActivityManager.getAppManager().finishAllActivity();
		Intent i = new Intent(this, SplashActivity.class);
		this.startActivity(i);
		this.finish();
	}

	private void initFragment() {

		FragmentManager fm = getSupportFragmentManager();
		// 开始事务
		FragmentTransaction transaction = fm.beginTransaction();
		mFrag = (Fragment) new ZhengQuanFragment();
		Bundle data = new Bundle();
		data.putInt("mId", mId);
		mFrag.setArguments(data);
		transaction.replace(R.id.fl_zhengquan, mFrag, TAG);
		transaction.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		AppActivityManager.getAppManager().removeActivity(this);
		super.onDestroy();
	}

}
