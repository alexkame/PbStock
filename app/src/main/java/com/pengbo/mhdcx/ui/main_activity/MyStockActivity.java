package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pengbo.mhdcx.adapter.CustomOptionListAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdcx.fragment.TradeOrderFragment;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdcx.ui.activity.HeadEditActivity;
import com.pengbo.mhdcx.ui.activity.HeadSearchOptionListActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.activity.ScreenDetailActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.view.CHScrollView;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.view.PullToRefreshView;
import com.pengbo.mhdcx.view.PullToRefreshView.OnFooterRefreshListener;
import com.pengbo.mhdcx.view.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 自选页
 * 
 * @author pobo
 * 
 */
public class MyStockActivity extends HdActivity implements OnClickListener,
		OnItemClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {
	private static final String TAG = "MyStockActivity";

	private PullToRefreshView mPullToRefreshView;
	private int mTotalListItemNum = 0;//list里条目总数
	private int mTotalListPage = 0;//list分页的页数，每页个数=LIST_PAGE_ITEMS_NUM
	private int mCurrentListPage = 0;//list分页当前显示页
	/** 左边的编辑按钮 & 右边的搜索期权列表 **/
	private TextView head_tv_option, head_btn_edit, head_btn_searchlist;
	private TextView mTV_Add;//添加自选
	
	private ListView mListView;
	public static View mHead;
	public ArrayList<TagLocalStockData> mDatas;// option data list for
												// listadapter
	public ArrayList<TagCodeInfo> mMyStockCodeInfos;// my stock list
	private CustomOptionListAdapter mListAdapter;
	private MyApp mMyApp;
	private PopupWindow mMenuWindow;
	private Button mBtnBuyKai, mBtnBuyPing, mBtnSellKai, mBtnSellPing, mJiaZiXuan;
	private TagCodeInfo mCurrentSelectOption;
	
	public int mRequestCode[];// 请求标记 0:query push

	/**
	 * 消息处理
	 */

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			
			case GlobalNetConnect.MSG_UPDATE_DATA: 
			{
				if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA)
				{
					L.d(TAG, "MyStockActivity receive push data");
					// 更新数据（最新数据已经保存在mMyApp.mHQData里）
					updateMyStockList();
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
		setContentView(R.layout.activity_optional);
		
		initData();
		initView();
	}
	
	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt){
		for(CHScrollView scrollView : mHScrollViews) {
			//防止重复滑动
			if(mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}
	
	private void initData()
	{
		mMyApp = (MyApp) this.getApplication();
		mRequestCode = new int[2];
		
		if (mCurrentSelectOption == null) {
			mCurrentSelectOption = new TagCodeInfo();
		}
		
		mMyStockCodeInfos = mMyApp.getMyStockList(AppConstants.HQTYPE_QQ);
		mTotalListItemNum = mMyStockCodeInfos.size();
		if (mTotalListItemNum > 0)
		{
			mTotalListPage = mTotalListItemNum/AppConstants.LIST_PAGE_ITEMS_NUM
						+ ( ((mTotalListItemNum%AppConstants.LIST_PAGE_ITEMS_NUM) > 0) ? 1:0 );
		}else
		{
			mTotalListPage = 0;
		}
		mCurrentListPage = 0;
		
		if (mHScrollViews == null)
		{
			mHScrollViews = new ArrayList<CHScrollView>();
		}
		mHScrollViews.clear();
	}
	
	/**
	 * 初始化控件
	 */
	private void initView() {
		/********** 自选股 编辑 **************/
		head_btn_edit = (TextView) this.findViewById(R.id.header_left_edit);
		/********** 期权列表的搜索界面 *********************/
		head_btn_searchlist = (TextView) this
				.findViewById(R.id.header_right_search);
		/********* 抬头标题中 文本显示是 自选界面 **************/
		head_tv_option = (TextView) this
				.findViewById(R.id.header_middle_textview);
		head_tv_option.setText("自选");
		head_btn_edit.setVisibility(View.VISIBLE);

		/************ 自选股编辑按钮的监听 ****************/
		head_btn_edit.setOnClickListener(this);
		/*********** 期权列表按钮的监听 *************************/
		head_btn_searchlist.setOnClickListener(this);
		
		mTV_Add = (TextView) this.findViewById(R.id.tv_myoption_add);
		mTV_Add.setOnClickListener(this);
		
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.mystock_pull_refreshview);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		//mPullToRefreshView.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		
		if (mTotalListItemNum <= 0)
		{
			mTV_Add.setVisibility(View.VISIBLE);
			mPullToRefreshView.setVisibility(View.GONE);
		}else
		{
			mTV_Add.setVisibility(View.GONE);
			mPullToRefreshView.setVisibility(View.VISIBLE);
		}
		
		CHScrollView headerScroll = (CHScrollView) findViewById(R.id.horizontalScrollView1);
		
		mHead = (View) findViewById(R.id.hv_head);
		LayoutParams lp = null;
		int iScreenWidth = ViewTools.getScreenSize(this).widthPixels;
		
		//第0列
		View tmp = findViewById(R.id.item1);
		lp = tmp.getLayoutParams();
		lp.width = iScreenWidth*3/10;
		tmp.setLayoutParams(lp);
		
		for (int i = 1; i < AppConstants.LIST_HEADER_ITEMS; i++)
		{
			String strid = String.format("item%d", i+1);
			tmp = mHead.findViewById(this.getResources().getIdentifier(strid, "id", this.getPackageName()));
			lp = tmp.getLayoutParams();
			if (i == 1 || i == 2 || i == 3)
			{
				lp.width = iScreenWidth*7/30;
			}else
			{
				lp.width = iScreenWidth*3/10;
			}	
			
			tmp.setLayoutParams(lp);
		}
		//添加头滑动事件 
		mHScrollViews.add(headerScroll);
		
		if(mListView == null)
		{
			mListView = (ListView) findViewById(R.id.listView1);
			mListView.setOnItemClickListener(this);
			
			mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					if (mDatas == null || mDatas.isEmpty()) {
						return false;
					}
					mCurrentSelectOption.code = mDatas.get(position).HQData.code;
					mCurrentSelectOption.name = mDatas.get(position).name;
					mCurrentSelectOption.market = mDatas.get(position).HQData.market;
					mCurrentSelectOption.group = mDatas.get(position).group;

					showPopupWindow(view, view, true, position);
					return true;
				}

			});
			
			mDatas = new ArrayList<TagLocalStockData>();// 初始化集合
			mListAdapter = new CustomOptionListAdapter(
					MyStockActivity.this, mDatas, mHead);
			mListView.setAdapter(mListAdapter);
		}
	}
	
	@Override
	public void addHViews(final CHScrollView hScrollView) {
		if(!mHScrollViews.isEmpty()) {
			int size = mHScrollViews.size();
			CHScrollView scrollView = mHScrollViews.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			//第一次满屏后，向下滑动，有一条数据在开始时未加入
			if(scrollX != 0) {
				mListView.post(new Runnable() {
					@Override
					public void run() {
						//当listView刷新完成之后，把该条移动到最终位置
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		mHScrollViews.add(hScrollView);
	}
	
	//bnext: true-跳转下一页，false-上一页
	private void gotoListPage(boolean bnext)
	{
		if (bnext)
		{
			if (mCurrentListPage < mTotalListPage - 1)
			{
				mCurrentListPage++;
				updateMyStockList();
				queryHQPushInfo();
			}else
			{
				Toast.makeText(this, "当前已经是最后一页", Toast.LENGTH_SHORT).show();
			}
		}else
		{
			if (mCurrentListPage > 0)
			{
				mCurrentListPage--;
				updateMyStockList();
				queryHQPushInfo();
			}else
			{
				Toast.makeText(this, "当前已经是第一页", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void updateMyStockList()
	{
		initMyStockList();
		if (mTotalListItemNum <= 0)
		{
			mTV_Add.setVisibility(View.VISIBLE);
			mPullToRefreshView.setVisibility(View.GONE);
		}else
		{
			mTV_Add.setVisibility(View.GONE);
			mPullToRefreshView.setVisibility(View.VISIBLE);
		}
	}

	private void initMyStockList() {
		mMyStockCodeInfos = mMyApp.getMyStockList(AppConstants.HQTYPE_QQ);
		mTotalListItemNum = mMyStockCodeInfos.size();
		if (mTotalListItemNum > 0)
		{
			mTotalListPage = mTotalListItemNum/AppConstants.LIST_PAGE_ITEMS_NUM
						+ ( ((mTotalListItemNum%AppConstants.LIST_PAGE_ITEMS_NUM) > 0) ? 1:0 );
		}else
		{
			mTotalListPage = 0;
		}
		mDatas.clear();
		TagCodeInfo taginfo = null;
		if (mTotalListPage > 0 && mCurrentListPage >= mTotalListPage)
		{
			mCurrentListPage = mTotalListPage - 1;
		}
		for (int i = mCurrentListPage*AppConstants.LIST_PAGE_ITEMS_NUM; 
				i < (mCurrentListPage+1)*AppConstants.LIST_PAGE_ITEMS_NUM && i < mMyStockCodeInfos.size(); i++) {
			taginfo = mMyStockCodeInfos.get(i);
			TagLocalStockData optionData = new TagLocalStockData();
			boolean ret = mMyApp.mHQData.getData(optionData, taginfo.market,
					taginfo.code, false);
			if (ret) {// get data success
				mDatas.add(optionData);
			} else 
			{// get data failed, this record is bad, need remove from
					// mystock list
				mMyApp.RemoveFromMyStock(i, AppConstants.HQTYPE_QQ);
				mTotalListItemNum--;
			}
		}

		mListAdapter.notifyDataSetChanged();
		String pageInfo = "";
		pageInfo = String.format("%d/%d", mCurrentListPage+1, mTotalListPage);
		mPullToRefreshView.setHeaderPageInfo(pageInfo);
		mPullToRefreshView.setFooterPageInfo(pageInfo);
	}
	

	@Override
	protected void onResume() {
		updateMyStockList();
		queryHQPushInfo();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
		if (mMenuWindow != null)
		{
			mMenuWindow.dismiss();
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		TagLocalStockData option = mDatas.get(position);
		Intent mIntent = new Intent();
		mIntent.setClass(MyStockActivity.this, ScreenDetailActivity.class);

		TagCodeInfo stockCodeInfo = new TagCodeInfo(
				option.optionData.StockMarket, option.optionData.StockCode,
				option.group, option.name);
		ScreenDetailActivity.mOptionCodeInfo = mMyStockCodeInfos.get(position + mCurrentListPage*AppConstants.LIST_PAGE_ITEMS_NUM);
		ScreenDetailActivity.mStockCodeInfo = stockCodeInfo;

		startActivity(mIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left_edit: {
	
			Intent intent = new Intent(MyStockActivity.this,
					HeadEditActivity.class);
			startActivity(intent);
		}
			break;
			
		case R.id.header_right_search: {
			Intent intent = new Intent(MyStockActivity.this,
					HeadSearchOptionListActivity.class);
			startActivity(intent);
		}
		    break;
		    
		case R.id.tv_myoption_add: {
			Intent intent = new Intent(MyStockActivity.this,
					HeadSearchOptionListActivity.class);
			startActivity(intent);
		}
		    break;
		    
		case R.id.buykai:
			if (mMenuWindow != null)
			{
				mMenuWindow.dismiss();
			}
			mMyApp.setCurrentOption(mCurrentSelectOption);
			mMyApp.mbDirectInOrderPage = true; // enable this flag for enter
												// order page.
			mMyApp.mIndexOfOrderBtn = TradeOrderFragment.Btn_WT_BuyOpen;

			Intent intent = new Intent();
			MainTabActivity act = AppActivityManager.getAppManager()
					.getMainTabActivity();

			if (act != null) {
				act.setChangePage(MainTabActivity.PAGE_TRADE);
			}
			intent.setClass(MyStockActivity.this, MainTabActivity.class);
			startActivity(intent);
			break;
		case R.id.buyping:
			if (mMenuWindow != null)
			{
				mMenuWindow.dismiss();
			}
			mMyApp.setCurrentOption(mCurrentSelectOption);
			mMyApp.mbDirectInOrderPage = true; // enable this flag for enter
												// order page.
			mMyApp.mIndexOfOrderBtn = TradeOrderFragment.Btn_WT_BuyClose;
			Intent intent2 = new Intent();
			MainTabActivity act2 = AppActivityManager.getAppManager()
					.getMainTabActivity();

			if (act2 != null) {
				act2.setChangePage(MainTabActivity.PAGE_TRADE);
			}
			intent2.setClass(MyStockActivity.this, MainTabActivity.class);
			startActivity(intent2);
			break;
		case R.id.sellkai:
			if (mMenuWindow != null)
			{
				mMenuWindow.dismiss();
			}
			mMyApp.setCurrentOption(mCurrentSelectOption);
			mMyApp.mbDirectInOrderPage = true; // enable this flag for enter
												// order page.
			mMyApp.mIndexOfOrderBtn = TradeOrderFragment.Btn_WT_SellOpen;
			Intent intent1 = new Intent();
			MainTabActivity act1 = AppActivityManager.getAppManager()
					.getMainTabActivity();

			if (act1 != null) {
				act1.setChangePage(MainTabActivity.PAGE_TRADE);
			}
			intent1.setClass(MyStockActivity.this, MainTabActivity.class);
			startActivity(intent1);
			break;
		case R.id.sellping:
			if (mMenuWindow != null)
			{
				mMenuWindow.dismiss();
			}
			mMyApp.setCurrentOption(mCurrentSelectOption);
			mMyApp.mbDirectInOrderPage = true; // enable this flag for enter
												// order page.
			mMyApp.mIndexOfOrderBtn = TradeOrderFragment.Btn_WT_SellClose;
			Intent intent3 = new Intent();
			MainTabActivity act3 = AppActivityManager.getAppManager()
					.getMainTabActivity();

			if (act3 != null) {
				act3.setChangePage(MainTabActivity.PAGE_TRADE);
			}
			intent3.setClass(MyStockActivity.this, MainTabActivity.class);
			startActivity(intent3);
			break;
		default:
			break;
		}
	}

	// 获取期权行情信息
	private void queryHQPushInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		for(int i = 0; i < mDatas.size(); i++)
		{
			codelist.add(mMyStockCodeInfos.get(mCurrentListPage*AppConstants.LIST_PAGE_ITEMS_NUM + i));
		}

		ArrayList<TagCodeInfo> stocklist = mMyApp.mHQData
				.getStockListByOptionList(codelist);
		codelist.addAll(stocklist);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}
	
	private void showPopupWindow(View mLayout, View mPop,
			final boolean mboolean, final int mPosition) {
		int trade_mode = PreferenceEngine.getInstance().getTradeMode();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view_window = inflater.inflate(R.layout.pop_t_offer_order, null);

		mJiaZiXuan = (Button) view_window.findViewById(R.id.jiazixuan);
		mJiaZiXuan.setVisibility(View.GONE);
		
		mMenuWindow = new PopupWindow(this);
		mMenuWindow.setContentView(view_window);
		mMenuWindow.setWidth(LayoutParams.WRAP_CONTENT);
		mMenuWindow.setHeight(LayoutParams.WRAP_CONTENT);

		if (mboolean == true) {
			mMenuWindow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.zx_small));
		} else {
			mMenuWindow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.zx1));
		}

		mMenuWindow.setFocusable(true);// 设置焦距 让点其他条目或后退键时条目消失

		// 设置window弹出来的位置
		int[] location = new int[2];
		// 获取一个空间在手机屏幕上的位置
		mLayout.getLocationOnScreen(location);
		int x = location[0];

		mLayout.getLocationOnScreen(location);
		int y = location[1];

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;

		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		int orientation = display.getOrientation();

		if (orientation == 0) {// 竖屏
			if (trade_mode == 0) {// 普通模式下
				mMenuWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY,
						screenWidth * 3 / 10, y - mLayout.getHeight()/2);
			} else {

				mMenuWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY,
						screenWidth * 1 / 8, y - mLayout.getHeight()/2);
			}
		} else if (orientation == 1) {// heng
			if (trade_mode == 0) {
				mMenuWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY,
						screenWidth * 17 / 50, y - mLayout.getHeight());
			} else {
				mMenuWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY,
						screenWidth * 26 / 100, y - mLayout.getHeight());
			}
		}

		mBtnBuyKai = (Button) view_window.findViewById(R.id.buykai);
		mBtnBuyPing = (Button) view_window.findViewById(R.id.buyping);
		mBtnSellKai = (Button) view_window.findViewById(R.id.sellkai);
		mBtnSellPing = (Button) view_window.findViewById(R.id.sellping);

		if (trade_mode == 0) {
			mBtnBuyKai.setVisibility(View.VISIBLE);
			mBtnSellPing.setVisibility(View.VISIBLE);
			mBtnBuyPing.setVisibility(View.GONE);
			mBtnSellKai.setVisibility(View.GONE);
		} else {
			mBtnBuyKai.setVisibility(View.VISIBLE);
			mBtnBuyPing.setVisibility(View.VISIBLE);
			mBtnSellKai.setVisibility(View.VISIBLE);
			mBtnSellPing.setVisibility(View.VISIBLE);
		}

		mBtnBuyKai.setOnClickListener(this);
		mBtnBuyPing.setOnClickListener(this);
		mBtnSellKai.setOnClickListener(this);
		mBtnSellPing.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		
		//list跳转到下一页
		gotoListPage(true);
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 10);
	}
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		//list跳转到上一页
		gotoListPage(false);
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				//设置更新时间
				//mPullToRefreshView.onHeaderRefreshComplete("最近更新:"+new Date().toLocaleString());
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		},10);
		
	}
}
