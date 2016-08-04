package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;

import com.pengbo.mhdcx.adapter.CustomOptionListAdapter;
import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdcx.ui.activity.CorrectHotOptionActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.activity.ScreenDetailActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.view.CHScrollView;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
/**
 * 筛选页 1。首先第一次安装时就应该创建一个文件 或者 xml 文件初始化一个 筛选页的条件修改的详情内容 2。筛选页读取文件显示详情内容
 * 3。条件修改页也要读取内容显示在每个地方
 * 
 * @author pobo
 * 
 */
public class ScreenActivity extends HdActivity implements OnClickListener,
		OnItemClickListener, OnCheckedChangeListener {
	@SuppressWarnings("unused")
	private static final String TAG = ScreenActivity.class.getSimpleName();
	public static final int REQUEST_CODE_EDIT = 10000;
	private static final int UPDATE_UI = 1;
	public TextView head_tv_option,// 中间的 筛选 文本
			head_btn_edit,// 左边的修改 按钮;
			
			header_right_search;// 右边的期权列表要隐藏
	public ImageView mBack;
	public ImageView head_imageview_refresh;// 右边的 刷新 列表

	public TextView tv_screen_detail; // 筛选条件修改的 详细
	
	private ListView mListView1;
	public static View mHead;

	private CustomOptionListAdapter mAdapter;

	private TagLocalStockData option;
	private HeadListDataService hdService;

	private ScreenCondition sc;
	private ArrayList<TagLocalStockData> mOptionDatas;//筛选出来的所有合约
	private MyApp mMyApp;
	public float option_hq_zdf = 0f; // 涨跌幅度

	public int option_LeverId = 0;; // 杠杆倍数
	public int option_Vitality = 0; // 活跃度
	
	public int 	mRequestCode[];//请求标记
	
	private RadioGroup mMainRG;

	/**
	 * 消息处理
	 */

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			
			switch (msg.what) {
			case UPDATE_UI:

				break;
				
			case GlobalNetConnect.MSG_UPDATE_DATA:
			{
				if(nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA)
				{
					loadData();
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
		setContentView(R.layout.activity_screen);
		mRequestCode = new int[1];
		this.initView();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_EDIT) {
			if (resultCode == CorrectHotOptionActivity.RESULT_CODE_EDIT) {
				
			}
		}
	}

	@Override
	protected void onResume() {
		updateView();
		loadData();
		if (mOptionDatas == null || mOptionDatas.size() <=0 )
		{
			Toast.makeText(this, "当前筛选条件暂无合约", Toast.LENGTH_SHORT).show();
		}
		queryHQPushInfo();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
		
	}
	
	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt){
		for(CHScrollView scrollView : mHScrollViews) {
			//防止重复滑动
			if(mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mMyApp = (MyApp) getApplication();
		/************** 中间的 筛选 文本 head_tv_option *****************/
		head_tv_option = (TextView) this
				.findViewById(R.id.header_middle_textview);
		head_tv_option.setText("筛选 ");
		/***************** head_tv_searchoptionlist 右边的期权列表要隐藏 ********************/
		header_right_search = (TextView) this
				.findViewById(R.id.header_right_search);
		header_right_search.setVisibility(View.VISIBLE);
		header_right_search.setText("修改条件");
		header_right_search.setOnClickListener(this);
		
		mBack=(ImageView) this.findViewById(R.id.header_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);

		/*********** 左边的修改 按钮 head_btn_edit ***************/
		head_btn_edit = (TextView) this.findViewById(R.id.header_left_edit);//   unused
		//head_btn_edit.setVisibility(View.GONE);
		//head_btn_edit.setText("修改条件");

		/******************* 右边的 刷新 列表 head_imageview_refresh ********************/
		head_imageview_refresh = (ImageView) this
				.findViewById(R.id.header_right_refresh);//   unused
		//head_imageview_refresh.setVisibility(View.VISIBLE);// 右面显示 刷新按钮

	//	head_btn_edit.setOnClickListener(this);// 点击修改条件 跳转到修改 界面
	//	head_imageview_refresh.setOnClickListener(this);// 点击刷新 按钮 手动刷新 请求 更新界面

		/******************* 筛选条件修改的 详细 tv_screen_detail ********************/
		/** !!!!!! !!!!!! 这里应该先读文件 看上次保存的修改条件 !!!!!! **/
		tv_screen_detail = (TextView) this.findViewById(R.id.tv_screen_detail);
		
		/************ 集合初始化 ***********/
		hdService = new HeadListDataService(ScreenActivity.this);

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
		
		mListView1 = (ListView) findViewById(R.id.listView1);
		mListView1.setOnItemClickListener(this);
		mListView1.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {//list停止滚动时加载图片  
	                //加载数据 
					queryHQPushInfo();
	            }else if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
	            {
	            	mMyApp.setHQPushNetHandler(null);
	            }
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		
		mOptionDatas = new ArrayList<TagLocalStockData> ();
		mAdapter = new CustomOptionListAdapter(ScreenActivity.this, mOptionDatas, mHead);
		mListView1.setAdapter(mAdapter);
		
		mMainRG = (RadioGroup) findViewById(R.id.main_tabs_rg);
		mMainRG.setOnCheckedChangeListener(this);
		mMainRG.check(R.id.main_tab_rb_b);
	}
	
	@Override
	public void addHViews(final CHScrollView hScrollView) {
		if(!mHScrollViews.isEmpty()) {
			int size = mHScrollViews.size();
			CHScrollView scrollView = mHScrollViews.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			//第一次满屏后，向下滑动，有一条数据在开始时未加入
			if(scrollX != 0) {
				mListView1.post(new Runnable() {
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
	
	private void updateView()
	{
		readDetailScreen();// 先读取安装默认的筛选条件
		referView();
	}

	public void referView() {
		String gridname = null;
		String levername = null;
		String Vitality = null;
		if (sc.getGridViewId() == 0) {
			gridname = "短期看涨(一个月左右 )";
		} else if (sc.getGridViewId() == 1) {
			gridname = "中期看涨(1~3个月)";
		} else if (sc.getGridViewId() == 2) {
			gridname = "长期看涨(3~12个月)";
		} else if (sc.getGridViewId() == 3) {
			gridname = "短期看跌(一个月左右)";
		} else if (sc.getGridViewId() == 4) {
			gridname = "中期看跌(1~3个月)";
		} else if (sc.getGridViewId() == 5) {
			gridname = "长期看跌(3~12个月)";
		}

		if (sc.getLeverId() == 0) {
			levername = "<10";
		} else if (sc.getLeverId() == 1) {
			levername = "10~20";
		} else if (sc.getLeverId() == 2) {
			levername = ">20";
		}else if (sc.getLeverId() == 3) {
			levername = "全部";
		}
		
		if (sc.getVitality() == 0) {
			Vitality = "不活跃";
		} else if (sc.getVitality() == 1) {
			Vitality = "一般";
		} else if (sc.getVitality() == 2) {
			Vitality = " 活跃 ";
		}
		tv_screen_detail.setText(sc == null ? "错" : sc.getName() + "+"
				+ gridname + "+" + ((int) (sc.getField_hq_zdf() * 100) + "%")
				+ "+" + levername + "+" + Vitality);
	}

	public void readDetailScreen() {

		String mGetSave = PreferenceEngine.getInstance().getHQQueryCondition();

		sc = new ScreenCondition();
		sc.setCode(STD.GetValue(mGetSave, 1, '|'));
		sc.setMarket((short) STD.StringToInt(STD.GetValue(mGetSave, 2, '|')));
		sc.setName(STD.GetValue(mGetSave, 3, '|'));
		sc.setGridViewId(STD.StringToInt(STD.GetValue(mGetSave, 4, '|')));
		sc.setField_hq_zdf(STD.StringToValue(STD.GetValue(mGetSave, 5, '|')));
		sc.setLeverId(STD.StringToInt(STD.GetValue(mGetSave, 6, '|')));
		sc.setVitality(STD.StringToInt(STD.GetValue(mGetSave, 7, '|')));
	}

	/**
	 * 子线程请求数据 数据应该是先请求网络 解析后 返回一个 集合 然后再 对应显示出来
	 */
	public void loadData() {
		//new Thread() {
		//	public void run() {
				try {
					L.e("loaddatas----",
							sc.getCode() + "......." + sc.getName());
					mOptionDatas = hdService.getScreenTagCodeInfos(sc, mMyApp.mStockConfigData);
					mAdapter.setDatas(mOptionDatas);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				mAdapter.notifyDataSetChanged();
		//	};
		//}.start();

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_right_search:
			//new Thread() {
			//	public void run() {
					Intent intent = new Intent();
					intent.setClass(
							ScreenActivity.this,
							com.pengbo.mhdcx.ui.activity.CorrectHotOptionActivity.class);

					startActivityForResult(intent, REQUEST_CODE_EDIT);
				//};
			//}.start();
			break;
		case R.id.header_back:// 点击刷新 按钮 手动刷新 请求 更新界面
			//new Thread() {
			//	public void run() {
			//		loadData();
			//	};
			//}.start();

			this.finish();
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		option = mOptionDatas.get(position);
		Intent intent = new Intent();
		intent.setClass(ScreenActivity.this, ScreenDetailActivity.class);
		
		TagCodeInfo stockCodeInfo = new TagCodeInfo(option.optionData.StockMarket, option.optionData.StockCode, option.group, option.name);
		TagCodeInfo optionCodeInfo = new TagCodeInfo(option.optionData.market, option.optionData.code, option.group, option.name);
		ScreenDetailActivity.mOptionCodeInfo = optionCodeInfo;
		ScreenDetailActivity.mStockCodeInfo = stockCodeInfo;
		
		//Bundle mBundle=new Bundle();
		
		//mBundle.putString(HeadOptionListActivity.INTENT_SERIALIZABLE_CODE,option.code);
		//mBundle.putShort(HeadOptionListActivity.INTENT_SERIALIZABLE_Market,option.market);
		//intent.putExtras(mBundle);
		
		startActivity(intent);
	}

	/**
	 * 当读取文件时 对象是空的话 给添加一个对象数据
	 */
	private void init() {
		sc = new ScreenCondition();
		sc.setCode(ScreenCondition.SCREEN_ALL_STOCK_CODE);
		sc.setMarket((short) 0);
		sc.setName("全部标的");
		sc.setGridViewId(2);
		sc.setField_hq_zdf(0.05f);
		sc.setLeverId(3);
		sc.setVitality(2);
	}
	
	// 获取期权行情信息
	private void queryHQPushInfo() {
		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		for (int i = 0; i < mOptionDatas.size(); i++) {
			TagCodeInfo optionCodeInfo = new TagCodeInfo(mOptionDatas.get(i).HQData.market, mOptionDatas.get(i).HQData.code,
					mOptionDatas.get(i).group, mOptionDatas.get(i).name);
			codelist.add(optionCodeInfo);
		}

		ArrayList<TagCodeInfo> stocklist = mMyApp.mHQData
				.getStockListByOptionList(codelist);
		codelist.addAll(stocklist);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	public void gotoMainPage(int page)
	{
		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();

		if (act != null) {
			act.setChangePage(page);
		}
		intent.setClass(ScreenActivity.this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
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
