package com.pengbo.mhdcx.fragment;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdcx.adapter.MyDialogAdapter;
import com.pengbo.mhdcx.adapter.MyTargetDialogAdapter;
import com.pengbo.mhdcx.adapter.TradeSearchOptionListAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.view.PullToRefreshView;
import com.pengbo.mhdcx.view.PullToRefreshView.OnFooterRefreshListener;
import com.pengbo.mhdcx.view.PullToRefreshView.OnHeaderRefreshListener;
import com.pengbo.mhdcx.widget.MyListDialog;
import com.pengbo.mhdcx.widget.MyTargetListDialog;
import com.pengbo.mhdcx.widget.MyListDialog.Dialogcallback;
import com.pengbo.mhdcx.widget.MyTargetListDialog.DialogcallbackTarget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TradeListAll extends Fragment implements
		OnItemClickListener, OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener{

	protected static final int UPDATE_UI = 1;

	public TextView tv_name;// 期权名称。。。。
	public TextView tv_lasttime;// 期权到期日

	private LinearLayout mLayoutName, mLayoutLastTime;

	Context context;

	private View mView;
	private ListView mListView1;
	public static View mHead;
	
	private PullToRefreshView mPullToRefreshView;
	private int mTotalListItemNum = 0;//list里条目总数
	private int mTotalListPage = 0;//list分页的页数，每页个数=LIST_PAGE_ITEMS_NUM
	private int mCurrentListPage = 0;//list分页当前显示页

	private TradeSearchOptionListAdapter mListAdapter;
	public List<TagLocalStockData> mDatas;// 默认时储存的集合 参数都是默认的 全部标的 全部日期 认购

	public ArrayList<TagCodeInfo> mTagCodeInfos; // 拿到合约所有的TagCodeInfos集合

	private RadioGroup rgroup;

	private TagLocalStockData tTagLocalStockData;
	private HeadListDataService hService;

	private MyListDialog myDialog;// 自定义对话框
	private MyTargetListDialog myTargetDialog;
	private MyTargetDialogAdapter adapter2;

	private String stockCode = null;
	private short stockMarket = 0;
	private int date = 0;
	private byte fx = 0;

	public int mRequestCode[];// 请求标记

	public Activity mActivity;
	public MyApp mMyApp;

	/**
	 * 消息处理
	 */

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			
			switch (msg.what) {
			case UPDATE_UI:

				String pageInfo = "";
				pageInfo = String.format("%d/%d", mCurrentListPage + 1, mTotalListPage);
				mPullToRefreshView.setHeaderPageInfo(pageInfo);
				mPullToRefreshView.setFooterPageInfo(pageInfo);
				
				mListAdapter.notifyDataSetChanged();

				break;
				
			case GlobalNetConnect.MSG_UPDATE_DATA:
			{
				if(nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA)
				{
					refreshListData();
					mListAdapter.notifyDataSetChanged();
				}
			}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView =  inflater.inflate(R.layout.trade_list_all_fragment, null);
		this.context = getActivity();
		initTitleView();
		initViewBiaoDi();
		initViewLastTime();
		initListView();
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		new Thread() {
			public void run() {
				loadListData();
				mHandler.sendEmptyMessage(UPDATE_UI);
			}
		}.start();
		
	}


	/**
	 * 初始化 抬头 上面的控件 关闭 按钮 和 全部 标的 和自选 标的 两个 按钮
	 */
	private void initTitleView() {

		mRequestCode = new int[2];
		// 是否添加到自选集和中 及点击事件
		mMyApp = (MyApp) mActivity.getApplication();

		mTagCodeInfos = new ArrayList<TagCodeInfo>();

		/******* 把 对象 new出来 传入 HeadOptionListActivity.this ********/
		hService = new HeadListDataService(getActivity());

		rgroup = (RadioGroup) mView.findViewById(
				R.id.head_option_list_actiity_radiogroup2);
		rgroup.setOnCheckedChangeListener(new OnCheckedChangeListeners());

	}


	private void initListView() {
		// 默认 给这几个属性一个值 即进来查询所有的时候的值
		if(mListView1 == null)
		{
			mListView1 = (ListView) mView.findViewById(R.id.listView1);

			// 条目点击事件
			mListView1.setOnItemClickListener(this);
			
    		mDatas = new ArrayList<TagLocalStockData>();// 初始化集合
			mListAdapter = new TradeSearchOptionListAdapter(mMyApp, context, mDatas, mTagCodeInfos);

			mListView1.setAdapter(mListAdapter);
		}
		
		mPullToRefreshView = (PullToRefreshView) mView.findViewById(R.id.trade_optionlist_pull_refreshview);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
	}

	/**
	 * 初始化期权标的的控件
	 */
	private void initViewBiaoDi() {

		tv_name = (TextView) mView.findViewById(
				R.id.head_option_list_activity_tv1);

		// tv_name.setText(Html.fromHtml("<font color=#000000>全部标的</font>\t\t"
		// + "<font color=#FF0000>10%</font>\t\t"
		// + "<font color=#FF0000>-10%</font>"));
		// 点击整个条目 ，弹出对话框 ------
		mLayoutName = (LinearLayout) mView.findViewById(
				R.id.head_option_list_activity_layout1);

		mLayoutName.setOnClickListener(this);

	}

	/**
	 * 初始化期权到期日期
	 */
	private void initViewLastTime() {

		tv_lasttime = (TextView) mView.findViewById(
				R.id.head_option_list_activity_tv4);
		// 点击 到期 整个 条目 改变 中间的 到期日
		mLayoutLastTime = (LinearLayout) mView.findViewById(
				R.id.head_option_list_activity_layout2);
		mLayoutLastTime.setOnClickListener(this);
	}

	/**
	 * 期权标的 数据接口 获得服务端过滤的期权标的列表
	 * 
	 * @return
	 */
	private List<TagCodeInfo> getTargetTagCodeInfo() {
		List<TagCodeInfo> mTagCodeInfo = new ArrayList<TagCodeInfo>();
		List<TagCodeInfo> mNetTagCodeInfo = null;
		mNetTagCodeInfo = hService.getTagCodeInfoRemoveNull();
		TagCodeInfo info = new TagCodeInfo();
		info.name = "全部标的";
		info.code = null;
		info.group = 0;
		info.market = 0;
		info.group = 0;
		mTagCodeInfo.add(info);
		for (int i = 0; i < mNetTagCodeInfo.size(); i++) {
			mTagCodeInfo.add(mNetTagCodeInfo.get(i));
		}
		
		for (int i = 0; i < mTagCodeInfo.size(); i++) {
			for (int j = mTagCodeInfo.size()-1; j > i; j--) {
				String s1 =mTagCodeInfo.get(j).name;
				String s2 =mTagCodeInfo.get(i).name;
				if (s1.equals(s2)) {
					mTagCodeInfo.get(j).name = s1 +"("+mTagCodeInfo.get(j).code+")";
					mTagCodeInfo.get(i).name = s2 +"("+mTagCodeInfo.get(i).code+")";
				}
			}
		}
		return mTagCodeInfo;
	}

	/**
	 * 日期数据接口
	 * 
	 * @return
	 */

	private List<String> getDatelists() {
		List<String> mStrDates = new ArrayList<String>(); // 这里必须要初始化日期数据 包括 网络
															// 和
															// 默认的数据;//要多一条默认数据日期的集合
		List<String> mNetStrDates = null;// 网络请求过来的日期集合
		mNetStrDates = hService.getAllDateArray("", (short) 0);
		mStrDates.add("全部日期");
		for (int i = 0; i < mNetStrDates.size(); i++) {
			mStrDates.add(mNetStrDates.get(i));
		}
		return mStrDates;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_option_list_activity_layout1:// 点击 标的 条目时 数据要进行筛选
													// 从新调用数据函数
			myTargetDialog = new MyTargetListDialog(context);

			adapter2 = new MyTargetDialogAdapter(context,
					getTargetTagCodeInfo());
			myTargetDialog.setContent(adapter2);
			myTargetDialog.setDialogCallback(dialogcallback);
			myTargetDialog.show();

			break;
		case R.id.head_option_list_activity_layout2:// 点击 日期条目 时 要 传入此时的 日期参数
													// 还要获得 其他参数的形式
			myDialog = new MyListDialog(context).builder();
			MyDialogAdapter adapter_date = new MyDialogAdapter(context,
					getDatelists());
			myDialog.setContent(adapter_date);
			myDialog.setDialogCallback(dialogcallback2);
			myDialog.setCancelable(true);
			myDialog.setCanceledOnTouchOutside(true);
			myDialog.show();
		default:
			break;
		}
	}

	/**
	 * 标的的回调函数
	 */
	DialogcallbackTarget dialogcallback = new DialogcallbackTarget() {
		@Override
		public void dialogdo(final int index) {
			// getData().get(string) #FFFF0000
			// Html.fromHtml("<font color=#000000>" +
			// getTargetNames().get(string) + "</font>\t\t"+
			// "<font color=#FF0000>10%</font>\t\t"+
			// "<font color=#FF0000>-10%</font>")
			tv_name.setText(getTargetTagCodeInfo().get(index).name);

			stockCode = getTargetTagCodeInfo().get(index).code;
			stockMarket = getTargetTagCodeInfo().get(index).market;

			L.e("============= dialogcallback  标的 ==============",
					"----------------");
			L.e("stockCode,stockMarket,date,fx", stockCode + "," + stockMarket
					+ "," + date + "," + fx);
			L.e("============= dialogcallcack  标的  ==============",
					"----------------");

			mCurrentListPage = 0;
			loadListData();
			mListAdapter.notifyDataSetChanged();
			String pageInfo = "";
			pageInfo = String.format("%d/%d", mCurrentListPage + 1,
					mTotalListPage);
			mPullToRefreshView.setHeaderPageInfo(pageInfo);
			mPullToRefreshView.setFooterPageInfo(pageInfo);

			L.e("mDatas============dialogcallback   标的==========",
					mDatas.size() + "  --");
			L.e("mTagCodeInfos====dialogcallback   标的==========",
					mTagCodeInfos.size() + "  --");

		}
	};

	/**
	 * 日期 对话框 回调函数
	 */
	Dialogcallback dialogcallback2 = new Dialogcallback() {
		@Override
		public void dialogdo(int string) {
			tv_lasttime.setText(getDatelists().get(string));
			if (getDatelists().get(string).equals("全部日期")) {
				date = 0;
			} else {
				date = STD.StringToInt(getDatelists().get(string));
			}

			L.e("============= dialogcallback2  日期 ==============",
					"----------------");
			L.e("stockCode,stockMarket,date,fx", stockCode + ","
					+ stockMarket + "," + date + "," + fx);
			L.e("============= dialogcallback2 日期  ==============",
					"----------------");

			mCurrentListPage = 0;
			loadListData();
			mListAdapter.notifyDataSetChanged();
			String pageInfo = "";
			pageInfo = String.format("%d/%d", mCurrentListPage + 1, mTotalListPage);
			mPullToRefreshView.setHeaderPageInfo(pageInfo);
			mPullToRefreshView.setFooterPageInfo(pageInfo);
		}
	};
	
	
	
	
	/**
	 * 实现 RadioGroup的 切换选择事件
	 * 
	 * @author pobo
	 * 
	 */
	public class OnCheckedChangeListeners implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			String pageInfo = "";
			switch (checkedId) {
			case R.id.rb_head_option_list_activity_3:
				fx = 0;
				L.e("============= Radiobutton 认购 ==============",
						"----------------");
				L.e("stockCode,stockMarket,date,fx", stockCode + ","
						+ stockMarket + "," + date + "," + fx);
				L.e("============= Radiobutton 认购 ==============",
						"----------------");
				mCurrentListPage = 0;
				loadListData();
				mListAdapter.notifyDataSetChanged();
				
				pageInfo = String.format("%d/%d", mCurrentListPage + 1, mTotalListPage);
				mPullToRefreshView.setHeaderPageInfo(pageInfo);
				mPullToRefreshView.setFooterPageInfo(pageInfo);
				
				L.e("mDatas============认购==========", mDatas.size() + "  --");
				L.e("mTagCodeInfos====认购==========", mTagCodeInfos.size()
						+ "  --");
				break;

			case R.id.rb_head_option_list_activity_4:
				fx = 1;

				L.e("============= Radiobutton 认沽 ==============",
						"----------------");
				L.e("stockCode,stockMarket,date,fx", stockCode + ","
						+ stockMarket + "," + date + "," + fx);
				L.e("============= Radiobutton 认沽 ==============",
						"----------------");
				mCurrentListPage = 0;
				loadListData();
				mListAdapter.notifyDataSetChanged();
				pageInfo = String.format("%d/%d", mCurrentListPage + 1, mTotalListPage);
				mPullToRefreshView.setHeaderPageInfo(pageInfo);
				mPullToRefreshView.setFooterPageInfo(pageInfo);
				
				L.e("mDatas============认沽==========", mDatas.size() + "  --");
				L.e("mTagCodeInfos====认沽==========", mTagCodeInfos.size()
						+ "  --");
			default:
				break;
			}
		}

	}


	/**
	 * 条目点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		tTagLocalStockData = mDatas.get(position);

		TagCodeInfo optionCodeInfo = new TagCodeInfo(tTagLocalStockData.HQData.market, tTagLocalStockData.HQData.code, tTagLocalStockData.group, tTagLocalStockData.name);
		
		mMyApp.setCurrentOption(optionCodeInfo);
		mMyApp.mbDirectInOrderPage = true; // enable this flag for enter order page.
		
		
		
		Intent intent = new Intent();
		
		MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
		
		if (act != null) {
			act.setChangePage(MainTabActivity.PAGE_TRADE);
		}
		intent.setClass(context, MainTabActivity.class);
		startActivity(intent);

	}
	
	private void loadListData()
	{
		mTagCodeInfos = hService.getTagCodeInfos(stockCode, stockMarket, date,
				fx);
		mTotalListItemNum = mTagCodeInfos.size();
		if (mTotalListItemNum > 0) {
			mTotalListPage = mTotalListItemNum
					/ AppConstants.LIST_PAGE_ITEMS_NUM
					+ (((mTotalListItemNum % AppConstants.LIST_PAGE_ITEMS_NUM) > 0) ? 1
							: 0);
		} else {
			mTotalListPage = 0;
		}

		mDatas.clear();
		TagCodeInfo taginfo = null;
		if (mTotalListPage > 0 && mCurrentListPage >= mTotalListPage) {
			mCurrentListPage = mTotalListPage - 1;
		}
		for (int i = mCurrentListPage * AppConstants.LIST_PAGE_ITEMS_NUM; i < (mCurrentListPage + 1)
				* AppConstants.LIST_PAGE_ITEMS_NUM
				&& i < mTagCodeInfos.size(); i++) {
			taginfo = mTagCodeInfos.get(i);
			TagLocalStockData optionData = new TagLocalStockData();
			mMyApp.mHQData.getData(optionData, taginfo.market, taginfo.code,
					false);
			mDatas.add(optionData);
		}
		queryHQPushInfo();
	}
	
	private void refreshListData()
	{
		mTotalListItemNum = mTagCodeInfos.size();
		if (mTotalListItemNum > 0) {
			mTotalListPage = mTotalListItemNum
					/ AppConstants.LIST_PAGE_ITEMS_NUM
					+ (((mTotalListItemNum % AppConstants.LIST_PAGE_ITEMS_NUM) > 0) ? 1
							: 0);
		} else {
			mTotalListPage = 0;
		}

		mDatas.clear();
		TagCodeInfo taginfo = null;
		if (mTotalListPage > 0 && mCurrentListPage >= mTotalListPage) {
			mCurrentListPage = mTotalListPage - 1;
		}
		for (int i = mCurrentListPage * AppConstants.LIST_PAGE_ITEMS_NUM; i < (mCurrentListPage + 1)
				* AppConstants.LIST_PAGE_ITEMS_NUM
				&& i < mTagCodeInfos.size(); i++) {
			taginfo = mTagCodeInfos.get(i);
			TagLocalStockData optionData = new TagLocalStockData();
			mMyApp.mHQData.getData(optionData, taginfo.market, taginfo.code,
					false);
			mDatas.add(optionData);
		}
	}

	// 获取期权行情信息
	private void queryHQPushInfo() {
		//comment out these code, because not need push data now.
//		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
//		for (int i = 0; i < mDatas.size(); i++) {
//			codelist.add(mTagCodeInfos.get(mCurrentListPage
//					* AppConstants.LIST_PAGE_ITEMS_NUM + i));
//		}
//
//		ArrayList<TagCodeInfo> stocklist = mMyApp.mHQData
//				.getStockListByOptionList(codelist);
//		codelist.addAll(stocklist);
//
//		mMyApp.setHQPushNetHandler(mHandler);
//		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
//				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMyApp.setHQPushNetHandler(null);
	}

	// bnext: true-跳转下一页，false-上一页
	private void gotoListPage(boolean bnext) {
		if (bnext) {
			if (mCurrentListPage < mTotalListPage - 1) {
				mCurrentListPage++;

				loadListData();
				mListAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this.context, "当前已经是最后一页", Toast.LENGTH_SHORT).show();
			}
		} else {
			if (mCurrentListPage > 0) {
				mCurrentListPage--;

				loadListData();
				mListAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this.context, "当前已经是第一页", Toast.LENGTH_SHORT).show();
			}
		}
		String pageInfo = "";
		pageInfo = String.format("%d/%d", mCurrentListPage + 1, mTotalListPage);
		mPullToRefreshView.setHeaderPageInfo(pageInfo);
		mPullToRefreshView.setFooterPageInfo(pageInfo);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {

		// list跳转到下一页
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
		// list跳转到上一页
		gotoListPage(false);
		mPullToRefreshView.postDelayed(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// 设置更新时间
				// mPullToRefreshView.onHeaderRefreshComplete("最近更新:"+new
				// Date().toLocaleString());
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 10);

	}

}
