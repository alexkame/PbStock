package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pengbo.mhdcx.adapter.CustomOptionListAdapter;
import com.pengbo.mhdcx.ui.activity.ScreenDetailActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.CHScrollView;
import com.pengbo.mhdzq.zq_activity.HdActivity;

public class HotOptionActivity extends HdActivity implements OnClickListener,
		OnItemClickListener, OnCheckedChangeListener {
	/** 页面头部居中的“热炒合约”文字 */
	private View mViewHot;
	/** 页面头部居中的LinearLayout */
	private View mViewDetail;
	/** 页面头部左边的“返回”按钮 */
	private View mBack;
	/** 页面头部右边的“刷新”按钮 */
	private ImageView mRefresh;

	private static final String TAG = "HotOptionActivity";
	/** 页面主体横向滚动部分中数据展示部分的ListView */
	private ListView mListView;
	/** 横滚部分包含大量TextView的LinearLayout，除去第1列固定列 **/
	public static View mHead;
	/** 向ListView中添加的数据集 **/
	public ArrayList<TagLocalStockData> mDatas;// option data list for
												// listadapter
	/** 热炒合约 list */
	public ArrayList<TagLocalStockData> mHotOptionList;
	private CustomOptionListAdapter mListAdapter;
	private MyApp mMyApp;
	private Timer mTimerGetHotOption = null;
	/** 请求标记 0:query push */
	public int mRequestCode[];
	/** 页面最下面的四个选项按钮的组容器 */
	private RadioGroup mMainRG;

	/** 消息处理 */
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {

			case GlobalNetConnect.MSG_UPDATE_DATA: {
				if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
					L.d(TAG, "HotOptionActivity receive push data");
					// 更新数据（最新数据已经保存在mMyApp.mHQData里）
					updateHotStockList();
				}
			}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_option);
		initData();
		initView();
	}

	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (CHScrollView scrollView : mHScrollViews) {
			// 防止重复滑动
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}

	private void initData() {
		mMyApp = (MyApp) this.getApplication();
		mRequestCode = new int[2];
		mHotOptionList = new ArrayList<TagLocalStockData>();

		if (mHScrollViews == null) {
			mHScrollViews = new ArrayList<CHScrollView>();
		}
		mHScrollViews.clear();
	}

	private int getScreenWidth(Context context) {
		return ViewTools.getScreenSize(context).widthPixels;
	}

	/** init the view */
	private void initView() {
		mViewDetail = this.findViewById(R.id.middle_linearlayout1);
		mViewHot = this.findViewById(R.id.middle_rechaoheyue);
		mViewDetail.setVisibility(View.GONE);
		mViewHot.setVisibility(View.VISIBLE);
		mBack = this.findViewById(R.id.linearlayout_out_of_back);
		mRefresh = (ImageView) this
				.findViewById(R.id.screen_detail_header_right_refresh);
		mRefresh.setVisibility(View.GONE);
		mBack.setOnClickListener(this);

		/** 页面横滚主体的标题 */
		CHScrollView headerScroll = (CHScrollView) findViewById(R.id.horizontalScrollView1);

		mHead = (View) findViewById(R.id.hv_head);
		LayoutParams lp = null;
		/** 屏幕宽度 */
		int iScreenWidth = getScreenWidth(this);

		/** 滚动部分详情子元素的第一列固定列 */
		View tmp = findViewById(R.id.item1);
		lp = tmp.getLayoutParams();
		lp.width = iScreenWidth * 3 / 10;
		tmp.setLayoutParams(lp);
		/** 遍历除去第1列固定列的其它所有滚动列,并设置它们的宽度 */
		for (int i = 1; i < AppConstants.LIST_HEADER_ITEMS; i++) {
			String strid = String.format("item%d", i + 1);
			tmp = mHead.findViewById(this.getResources().getIdentifier(strid,
					"id", this.getPackageName()));
			lp = tmp.getLayoutParams();
			if (i == 1 || i == 2 || i == 3) {
				lp.width = iScreenWidth * 7 / 30;
			} else {
				lp.width = iScreenWidth * 3 / 10;
			}

			tmp.setLayoutParams(lp);
		}

		// 添加头滑动事件
		mHScrollViews.add(headerScroll);

		if (mListView == null) {
			mListView = (ListView) findViewById(R.id.hotoption_listView1);
			mListView.setOnItemClickListener(this);

			mDatas = new ArrayList<TagLocalStockData>();// 初始化集合
			mListAdapter = new CustomOptionListAdapter(HotOptionActivity.this,
					mDatas, mHead);
			mListView.setAdapter(mListAdapter);
		}

		mMainRG = (RadioGroup) findViewById(R.id.main_tabs_rg);
		mMainRG.setOnCheckedChangeListener(this);
		mMainRG.check(R.id.main_tab_rb_b);
	}

	@Override
	public void addHViews(final CHScrollView hScrollView) {
		if (!mHScrollViews.isEmpty()) {
			int size = mHScrollViews.size();
			CHScrollView scrollView = mHScrollViews.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			// 第一次满屏后，向下滑动，有一条数据在开始时未加入
			if (scrollX != 0) {
				mListView.post(new Runnable() {
					@Override
					public void run() {
						// 当listView刷新完成之后，把该条移动到最终位置
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		mHScrollViews.add(hScrollView);
	}

	private void updateHotStockList() {
		mDatas.clear();
		for (int i = 0; i < mHotOptionList.size(); i++) {
			TagLocalStockData optionData = new TagLocalStockData();
			mMyApp.mHQData.getData(optionData,
					mHotOptionList.get(i).HQData.market,
					mHotOptionList.get(i).HQData.code, false);
			mDatas.add(optionData);
		}
		mListAdapter.notifyDataSetChanged();
	}

	/** 初始化热炒合约ArrayList<TagLocalStockData> mDatas的数据 *****/
	private void initHotOptionList() {

		mDatas.clear();
		mHotOptionList = mMyApp.mHQData.getHotOptionList(20);
		mDatas.addAll(mHotOptionList);
		// this.mListAdapter.setDatas(mDatas);
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		initHotOptionList();
		queryHQPushInfo();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null,
				0, 0);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		TagLocalStockData option = mDatas.get(position);
		Intent mIntent = new Intent();
		mIntent.setClass(this, ScreenDetailActivity.class);

		TagCodeInfo optionCodeInfo = new TagCodeInfo(option.HQData.market,
				option.HQData.code, option.group, option.name);

		TagCodeInfo stockCodeInfo = new TagCodeInfo(
				option.optionData.StockMarket, option.optionData.StockCode,
				option.group, option.name);
		ScreenDetailActivity.mOptionCodeInfo = optionCodeInfo;
		ScreenDetailActivity.mStockCodeInfo = stockCodeInfo;

		startActivity(mIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linearlayout_out_of_back:
			this.finish();
			break;
		case R.id.screen_detail_header_right_refresh:

			break;
		default:
			break;
		}
	}

	private void startGetHotOptionTimer(long timer) {

		stopGetHotOptionTimer();
		{
			mTimerGetHotOption = new Timer();
			mTimerGetHotOption.schedule(new TimerTask() {
				//
				public void run() {
				}
			}, timer, timer);
		}
	}

	private void stopGetHotOptionTimer() {
		if (mTimerGetHotOption != null) {
			mTimerGetHotOption.cancel();
		}
		mTimerGetHotOption = null;
	}

	/** 获取期权行情信息 */
	private void queryHQPushInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		for (int i = 0; i < mDatas.size(); i++) {
			TagCodeInfo codeinfo = new TagCodeInfo(mDatas.get(i).HQData.market,
					mDatas.get(i).HQData.code, mDatas.get(i).group,
					mDatas.get(i).name);
			codelist.add(codeinfo);
		}

		ArrayList<TagCodeInfo> stocklist = mMyApp.mHQData
				.getStockListByOptionList(codelist);
		codelist.addAll(stocklist);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	public void gotoMainPage(int page) {
		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager()
				.getMainTabActivity();

		if (act != null) {
			act.setChangePage(page);
		}
		intent.setClass(HotOptionActivity.this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.main_tab_rb_a:
			L.d(TAG, "onCheckedChanged tab_rb_a");
			gotoMainPage(MainTabActivity.PAGE_MYSTOCK);

			break;
		case R.id.main_tab_rb_b:
			L.d(TAG, "onCheckedChanged tab_rb_b");

			break;
		case R.id.main_tab_rb_c:
			L.d(TAG, "onCheckedChanged tab_rb_c");
			gotoMainPage(MainTabActivity.PAGE_TRADE);

			break;
		case R.id.main_tab_rb_e:
			L.d(TAG, "onCheckedChanged tab_rb_e");
			gotoMainPage(MainTabActivity.PAGE_SETTING);

			break;
		}

	}
}
