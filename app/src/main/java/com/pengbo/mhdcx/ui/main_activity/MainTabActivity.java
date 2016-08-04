package com.pengbo.mhdcx.ui.main_activity;

import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

/**
 * MainTabActivity 主界面 在主界面要实现 网络的链接请求 及进来时 自选股 或 筛选股 界面第一次进来时的界面展示 要用的数据 请求
 * 
 * @author pobo
 * 
 */
@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements
		OnCheckedChangeListener {

	private final static String TAG = MainTabActivity.class.getSimpleName();
	/** 自选选项卡 */
	public static final int PAGE_MYSTOCK = 0;
	/** 行情选项卡 */
	public static final int PAGE_SCREEN = 1;
	/** 交易选项卡 */
	public static final int PAGE_TRADE = 2;
	/** 设置选项卡 */
	public static final int PAGE_SETTING = 3;

	private MyApp mMyApp;
	private TabHost mHost;
	/** 最小页的五个选项所在的选项组 */
	private RadioGroup radioderGroup;
	private int mCurrentPage = PAGE_MYSTOCK;
	/** 最下面的“交易”选项卡 */
	private RadioButton tab_rb_c;
	/** 最下面的“自选”选项卡 */
	private RadioButton tab_rb_a;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tab);
		mMyApp = (MyApp) this.getApplication();
		mMyApp.mMainTabActivity = this;
		AppActivityManager.getAppManager().addActivity(this);
		setHost();

		radioderGroup = (RadioGroup) findViewById(R.id.tabs_rg);
		tab_rb_c = (RadioButton) radioderGroup.findViewById(R.id.tab_rb_c);
		tab_rb_a = (RadioButton) radioderGroup.findViewById(R.id.tab_rb_a);
		radioderGroup.setOnCheckedChangeListener(this);

		// int mId = getIntent().getIntExtra("mId",PAGE_MYSTOCK);

		int mId = getIntent().getIntExtra("mId", PAGE_MYSTOCK);
		if (mId == KeyDefine.KEY_MARKET_QQJY) {
			mCurrentPage = PAGE_TRADE;
			tab_rb_c.setChecked(true);
		} else {
			mCurrentPage = PAGE_MYSTOCK;
			tab_rb_a.setChecked(true);
		}
	}

	private void setHost() {
		// 实例化TabHost
		mHost = this.getTabHost();

		/******************** 添加选项卡 *******************/
		// 添加证券自选
		mHost.addTab(mHost.newTabSpec("optional").setIndicator("optional")
				.setContent(new Intent(this, MyStockActivity.class)));
		// 添加沪深个股
		mHost.addTab(mHost.newTabSpec("screen").setIndicator("screen")
				.setContent(new Intent(this, MarketInfoActivity.class)));
		// 添加交易页
		mHost.addTab(mHost.newTabSpec("tradetab").setIndicator("tradetab")
				.setContent(new Intent(this, TradeGroupTabActivity.class)));
		// mHost.addTab(mHost.newTabSpec("info").setIndicator("info")
		// .setContent(new Intent(this,InfoActivity.class)));
		// 交易设置页
		mHost.addTab(mHost.newTabSpec("set").setIndicator("set")
				.setContent(new Intent(this, SettingActivity.class)));
	}

	public void setChangePage(int index) {
		if (mCurrentPage != index) {
			mCurrentPage = index;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		// 解决com.pengbo.mhdcx.ui.main_activity.SettingActivity.onResume时候的NullPointerException。
		if (mHost == null) {
			mHost.clearAllTabs();
			setHost();
		}

		switch (checkedId) {
		case R.id.tab_rb_a:
			L.d(TAG, "onCheckedChanged tab_rb_a");
			setChangePage(MainTabActivity.PAGE_MYSTOCK);
			mHost.setCurrentTabByTag("optional");
			break;
		case R.id.tab_rb_b:
			L.d(TAG, "onCheckedChanged tab_rb_b");
			setChangePage(MainTabActivity.PAGE_SCREEN);
			mHost.setCurrentTabByTag("screen");
			break;
		case R.id.tab_rb_c:
			L.d(TAG, "onCheckedChanged tab_rb_c");
			setChangePage(MainTabActivity.PAGE_TRADE);
			mHost.setCurrentTabByTag("tradetab");
			break;
		case R.id.tab_rb_d:// 首页
			L.d(TAG, "onCheckedChanged tab_rb_d");
			this.finish();
			break;
		case R.id.tab_rb_e:
			L.d(TAG, "onCheckedChanged tab_rb_e");
			setChangePage(MainTabActivity.PAGE_SETTING);
			mHost.setCurrentTabByTag("set");
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		switch (mCurrentPage) {
		case PAGE_MYSTOCK:
			L.d(TAG, "onNewIntent PAGE_MYSTOCK");
			// radioderGroup.check(R.id.tab_rb_a);
			tab_rb_a.setChecked(true);
			// mHost.setCurrentTabByTag("optional");
			break;
		case PAGE_SCREEN:
			L.d(TAG, "onNewIntent PAGE_SCREEN");
			// radioderGroup.check(R.id.tab_rb_b);
			((RadioButton) radioderGroup.findViewById(R.id.tab_rb_b))
					.setChecked(true);
			// mHost.setCurrentTabByTag("screen");
			break;
		case PAGE_TRADE:
			L.d(TAG, "onNewIntent PAGE_TRADE");
			// radioderGroup.check(R.id.tab_rb_c);
			tab_rb_c.setChecked(true);
			// mHost.setCurrentTabByTag("tradetab");
			break;
		case PAGE_SETTING:
			L.d(TAG, "onNewIntent PAGE_SETTING");
			// radioderGroup.check(R.id.tab_rb_e);
			((RadioButton) radioderGroup.findViewById(R.id.tab_rb_e))
					.setChecked(true);
			// mHost.setCurrentTabByTag("set");
			break;
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		ViewTools.isShouldForegraund = true;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		AppActivityManager.getAppManager().removeActivity(this);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if(keyCode == KeyEvent.KEYCODE_BACK) {
		// //退出确认对话框
		// AppActivityManager.getAppManager().AppExit(true);
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}
}
