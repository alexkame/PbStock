package com.pengbo.mhdzq.base;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * basepager for homapager zixuanpager etc 读取了zq_page_base.xml配置文件
 * 
 * @author pobo
 */
public abstract class BasePager {

	public Activity mActivity;
	public MyApp mMyApp;
	/** 指向zq_page_base布局文件 **/
	public View mRootView;
	/** 指向zq_dbzq_public_head_black布局文件 **/
	public View title_bar;
	/** zq_dbzq_public_head_black布局文件中的中间标题“东北证券” **/
	public TextView tvTitle;
	/** mLlayUpDown中包含的主标题文本“中国重工” **/
	public TextView tvTitleUp;
	/** mLlayUpDown中包含的副标题文本“......” **/
	public TextView tvTitleDown;
	/** 标题栏中右边的查询图片 ***/
	public ImageView imgRightSearch;
	/** 标题栏中右边的刷新图片 ***/
	public ImageView imgRightRefresh;
	/** 该页面的主体内容Fragment ****/
	public FrameLayout flContent;
	/** zq_dbzq_public_head_black布局文件中的中间另一个标题“中国重工” ***/
	public LinearLayout mLlayUpDown;

	public Dialog mProgress;
	/** 标题栏左边的编辑按钮 ****/
	public ImageView imgLeftNote;
	public boolean bPagerReady = false;

	public BasePager(Activity activity) {
		mActivity = activity;
		mMyApp = (MyApp) mActivity.getApplication();
		initView();
	}

	/** 读取了zq_page_base.xml配置文件，并初始化了里面的控件 **********/
	public void initView() {
		mRootView = View.inflate(mActivity, R.layout.zq_page_base, null);
		title_bar = (View) mRootView.findViewById(R.id.title_bar);
		tvTitle = (TextView) mRootView
				.findViewById(R.id.tv_public_black_head_title_middle_name);

		mLlayUpDown = (LinearLayout) mRootView
				.findViewById(R.id.llayout_middle_tv);
		tvTitleUp = (TextView) mRootView
				.findViewById(R.id.llayout_middle_tv_up);
		tvTitleDown = (TextView) mRootView
				.findViewById(R.id.llayout_middle_tv_down);

		imgRightSearch = (ImageView) mRootView
				.findViewById(R.id.img_public_black_head_title_right_blue_search);
		imgRightRefresh = (ImageView) mRootView
				.findViewById(R.id.img_public_black_head_title_right_blue_refresh);
		imgLeftNote = (ImageView) mRootView
				.findViewById(R.id.img_public_black_head_title_left_note);
		flContent = (FrameLayout) mRootView.findViewById(R.id.fl_content);
	}

	public abstract void initDetailView();

	public abstract void visibleOnScreen();

	public abstract void invisibleOnScreen();

	protected void showProgress(String msg) {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(mActivity, R.style.ProgressDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText(msg);
			mProgress.setCancelable(true);
		}
		mProgress.show();
	}

	protected void closeProgress() {
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}

	/**
	 * 隐藏系统键盘
	 * 
	 * @param editText
	 */
	public void hideSoftInputMethod(EditText editText) {
		if (editText == null) {
			return;
		}
		InputMethodManager imm = ((InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE));

		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}
}
