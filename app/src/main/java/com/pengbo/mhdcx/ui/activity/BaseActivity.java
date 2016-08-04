package com.pengbo.mhdcx.ui.activity;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
/**
 * 应用程序  Activity的基类   
 * 里面 还得做 登陆的处理   
 * @author Administrator
 *
 */
public class BaseActivity extends HdActivity {
	public Context mContext;
	public Dialog mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext =this;//上下文环境 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this, R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			mProgress.setCancelable(true);
		}
		L.i("BaseActivity", "showProgress--->" + this.toString());
		mProgress.show();
	}

	protected void closeProgress() {

		if (mProgress != null && mProgress.isShowing()) {
			L.i("BaseActivity", "closeProgress--->" + this.toString());
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}
}
