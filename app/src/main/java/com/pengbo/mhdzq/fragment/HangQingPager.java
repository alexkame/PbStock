package com.pengbo.mhdzq.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.data.CPBMarket;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.TopRankData;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ExpandableListAdapter;
import com.pengbo.mhdzq.adapter.ExpandableListAdapter_DZBK;
import com.pengbo.mhdzq.adapter.HangQingAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.base.BasePager;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TopRankField;
import com.pengbo.mhdzq.data.Rule;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.main_activity.HomeEditActivity;
import com.pengbo.mhdzq.main_activity.ZiXuanFastSearchActivity;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.AutoScaleTextView;
import com.pengbo.mhdzq.zq_activity.DZBKEditActivity;
import com.pengbo.mhdzq.zq_activity.MyStockEditActivity;
import com.pengbo.mhdzq.zq_activity.ZQMarketDetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 证券-行情页面
 * 
 * @author pobo
 */
public class HangQingPager extends BasePager implements OnClickListener {
	private static final String TAG = "HangQingPager";
	private static final int TIMER_REQUEST = 1;

	private static final int VIEW_HSGG = 0;
	private static final int VIEW_QQZS = 1;
	private static final int VIEW_GJS = 2;
	private static final int VIEW_DZBK = 3;

	private static final int ZHISHU_NUM = 3;// 目前显示3个指数
	private static final int REFRESH_ITEM_NUM = 15; // 每次推送刷新item的个数
	private static final int DEFOULT_IN = 0;

	private int mId = DEFOULT_IN;

	private View view;

	private int mCurrentView = VIEW_HSGG;

	private RadioGroup rg_hangqing;
	private RadioButton rb_hushen, rb_quanqiu, rb_guijinshu, rb_dingzhi;

	private View layout_auto_textview_shangzhengzhishu;
	private View layout_auto_textview_shenzhengchengzhi;
	private View layout_auto_textview_chuangyebanzhi;

	private TextView tv_Names[] = new TextView[3];

	private AutoScaleTextView tv_Price[] = new AutoScaleTextView[3],
			tv_ZDF[] = new AutoScaleTextView[3];

	private LinearLayout llayout_zhishu1, llayout_zhishu2, llayout_zhishu3;

	private View layout_hangqing_others;
	private View layout_hangqing_dingzhi;
	private View layout_hangqing_quanqiuzhishu;
	private ExpandableListView expandableListView;
	private ArrayList<TopRankField> groupData;
	private HashMap<Short, ArrayList<TopRankData>> childDatas;
	private ExpandableListAdapter exListAdapter;

	// for 定制板块
	private ExpandableListView expandableListView_dzbk;
	private ArrayList<CPBMarket> groupData_dzbk;
	private HashMap<String, ArrayList<CCodeTableItem>> childDatas_dzbk;
	private ExpandableListAdapter_DZBK exListAdapter_dzbk;
	private int mTotalListItemNum_DZBK = 0;// 定制版块list里条目总数

	private int mTotalListItemNum = 0;// list里条目总数
	private int mStartIndex = 0;// ListView当前显示的第一条item索引
	private int mEndIndex = 20; // listview 当前屏幕显示的最后一条item索引
	private ArrayList<CCodeTableItem> mDatas;
	private ArrayList<CPBMarket> mPBMarkets; // 存储市场

	// 全球指数
	private ListView mLv_Quanqiuzhishu;
	private HangQingAdapter mListAdapter;

	private Activity activity;
	private Context mContext;

	private Timer mTimerRequestPaiMing = null;

	private ArrayList<TagCodeInfo> mZhiShuCodeInfos;// zhishu list
	private int mRequestCode[];// 请求标记0-指数信息请求，1-指数信息推送,2-排名请求
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			case TIMER_REQUEST: {
				requestPaiMing();
			}
				break;

			case GlobalNetConnect.MSG_UPDATE_DATA: {
				CMessageObject aMsgObject = null;
				if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
					aMsgObject = (CMessageObject) msg.obj;
				} else {
					return;
				}

				if (nFrameType == Global_Define.MFT_MOBILE_DATA_APPLY) {
					if (aMsgObject.nErrorCode != 0) {
						L.e(TAG, "RequestCode:" + nRequestCode);
						return;
					}

					if (mRequestCode[2] == nRequestCode)//
					{
						parseTopRankData(aMsgObject.getData(),
								aMsgObject.getDataLength());
						((ExpandableListAdapter) expandableListView
								.getExpandableListAdapter())
								.notifyDataSetChanged();
					}

				} else if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
					L.i(TAG, "Received push data");
					closeProgress();
					switch (mCurrentView) {
					case VIEW_HSGG:
						updateZhiShuView();
						break;
					case VIEW_QQZS:
					case VIEW_GJS:
						refreshListData();
						break;
					case VIEW_DZBK:
						((ExpandableListAdapter_DZBK) expandableListView_dzbk
								.getExpandableListAdapter())
								.notifyDataSetChanged();
						break;
					}

				}
			}
				break;

			default:
				break;
			}
		};
	};

	public HangQingPager(Activity activity) {
		super(activity);
		this.activity = activity;
		mContext = activity;
		mId = DEFOULT_IN;
	}

	public HangQingPager(Activity activity, int mId) {
		super(activity);
		this.activity = activity;
		mContext = activity;
		this.mId = mId;
	}

	// 初始化整个页面
	@Override
	public void initDetailView() {
		tvTitle.setText("证券-行情");
		tvTitle.setVisibility(View.VISIBLE);

		view = View.inflate(mActivity, R.layout.zq_page_hangqing, null);

		findView();

		mZhiShuCodeInfos = mMyApp.mZQ_ZhiShuCodeInfos;
		mRequestCode = new int[3];

		rg_hangqing.setOnCheckedChangeListener(new CheckChangedLinstener());

		imgRightSearch.setVisibility(View.VISIBLE);
		imgRightSearch.setOnClickListener(this);

		mPBMarkets = new ArrayList<CPBMarket>();
		getTargetMarkets(mPBMarkets);

		initExpandLv();
		initExpandLv_dzbk();

		initFirstEntrance();
		flContent.addView(view);
		bPagerReady = true;

		updateZhiShuView();
	}

	// 初始化页面
	private void initFirstEntrance() {

		layout_hangqing_others.setVisibility(View.VISIBLE);
		layout_hangqing_dingzhi.setVisibility(View.GONE);
		switch (mId) {
		case KeyDefine.KEY_MARKET_STOCK_SHSZ:
			rb_hushen.setChecked(true);
			break;
		case KeyDefine.KEY_MARKET_EX_STOCK_ZS:
			rb_quanqiu.setChecked(true);
			break;
		case KeyDefine.KEY_MARKET_EX_FUTURE_LD:
			rb_guijinshu.setChecked(true);
			break;
		}
	}

	// 初始化expandableListview
	private void initExpandLv() {
		if (groupData == null) {
			groupData = new ArrayList<TopRankField>();
		} else {
			groupData.clear();
		}

		if (childDatas == null) {
			childDatas = new HashMap<Short, ArrayList<TopRankData>>();
		} else {
			childDatas.clear();
		}

		initModle();
		setListener();
	}

	// 初始化expandableListview
	private void initExpandLv_dzbk() {
		if (groupData_dzbk == null) {
			groupData_dzbk = new ArrayList<CPBMarket>();
		} else {
			groupData_dzbk.clear();
		}

		if (childDatas_dzbk == null) {
			childDatas_dzbk = new HashMap<String, ArrayList<CCodeTableItem>>();
		} else {
			childDatas_dzbk.clear();
		}

		exListAdapter_dzbk = new ExpandableListAdapter_DZBK(mActivity,
				groupData_dzbk, childDatas_dzbk);
		expandableListView_dzbk.setAdapter(exListAdapter_dzbk);

		// expandableListView_dzbk.setOnGroupClickListener(new
		// OnGroupClickListener() {
		//
		// @Override
		// public boolean onGroupClick(ExpandableListView parent, View v,
		// int groupPosition, long id) {
		// return false;
		// }
		// });
	}

	private void loadDZBKData() {
		groupData_dzbk.clear();

		CPBMarket pbmarket = null;
		for (int i = 0; i < mMyApp.mPBMarketArray_DZBK.size(); i++) {
			pbmarket = mMyApp.mPBMarketArray_DZBK.get(i);
			if (pbmarket == null)
				continue;

			groupData_dzbk.add(pbmarket);
		}

		childDatas_dzbk.clear();
		for (int i = 0; i < groupData_dzbk.size(); i++) {
			ArrayList<CCodeTableItem> data = new ArrayList<CCodeTableItem>();
			childDatas_dzbk.put(groupData_dzbk.get(i).Id, data);
		}

		mTotalListItemNum_DZBK = 0;
		pbmarket = null;
		for (int i = 0; i < mMyApp.mPBMarketArray_DZBK.size(); i++) {
			pbmarket = mMyApp.mPBMarketArray_DZBK.get(i);

			if (pbmarket == null)
				continue;

			if (pbmarket.mRules == null || pbmarket.mRules.size() == 0) {
				for (int j = 0; j < mMyApp.mPBMarketArray_DZBK_ALL.size(); j++) {
					if (pbmarket.Id
							.equalsIgnoreCase(mMyApp.mPBMarketArray_DZBK_ALL
									.get(j).Id)) {
						pbmarket.mRules = mMyApp.mPBMarketArray_DZBK_ALL.get(j).mRules;
						break;
					}
				}
			}

			ArrayList<Rule> rules = pbmarket.mRules;
			if (rules == null)
				return;

			for (int m = 0; m < rules.size()
					&& m < CDataCodeTable.MAX_SAVE_MARKET_COUNT; m++) {
				Rule rule = rules.get(m);
				int nMarket = STD.StringToInt(rule.mMarketId);
				if (nMarket > 0) {
					int codeTableIndex = -1;
					for (int j = 0; j < mMyApp.mCodeTableMarketNum
							&& j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
						if (nMarket == mMyApp.mCodeTable[j].mMarketId) {
							codeTableIndex = j;
							break;
						}
					}
					if (codeTableIndex >= 0
							&& codeTableIndex < mMyApp.mCodeTableMarketNum) {
						String category = rule.mCategory;
						if (category
								.equalsIgnoreCase(KeyDefine.CATEGORY_MARKET_CODE)) {
							ArrayList<CCodeTableItem> tempArray = new ArrayList<CCodeTableItem>();
							String code = rule.mCode;
							tempArray = mMyApp.mCodeTable[codeTableIndex]
									.getDataByCode(nMarket, code);
							childDatas_dzbk.get(pbmarket.Id).addAll(tempArray);
						} else if (category
								.equalsIgnoreCase(KeyDefine.CATEGORY_MARKET_GROUP)) {
							ArrayList<CCodeTableItem> tempArray = new ArrayList<CCodeTableItem>();
							String groupCode = rule.mGroupCode;
							tempArray = mMyApp.mCodeTable[codeTableIndex]
									.getDataByGroup(nMarket, groupCode);
							childDatas_dzbk.get(pbmarket.Id).addAll(tempArray);
						} else if (category
								.equalsIgnoreCase(KeyDefine.CATEGORY_MARKET)) {
							ArrayList<CCodeTableItem> tempArray = new ArrayList<CCodeTableItem>();
							tempArray = mMyApp.mCodeTable[codeTableIndex]
									.getData(nMarket);
							childDatas_dzbk.get(pbmarket.Id).addAll(tempArray);
						}
					}
				}
			}
			mTotalListItemNum_DZBK += childDatas_dzbk.get(pbmarket.Id).size();
		}// for(int i = 0; i < mMyApp.mPBMarketArray_DZBK.size(); i++)
		exListAdapter_dzbk.setData(groupData_dzbk, childDatas_dzbk);
		if (groupData_dzbk.size() > 0) {
			expandableListView_dzbk.expandGroup(0);
		}

		((ExpandableListAdapter_DZBK) expandableListView_dzbk
				.getExpandableListAdapter()).notifyDataSetChanged();
		queryHQPushInfo();
	}

	// 初始化expandablelistview里面的数据与lv捆绑
	private void initModle() {
		readTopRankFieldFromConfig();
		for (int i = 0; i < groupData.size(); i++) {
			ArrayList<TopRankData> data = new ArrayList<TopRankData>();
			childDatas.put(groupData.get(i).id, data);
		}

		exListAdapter = new ExpandableListAdapter(mActivity, groupData,
				childDatas);
		expandableListView.setAdapter(exListAdapter);
	}

	// 从defaultConfig.ini读取涨幅排名字段.
	// [stocksort]
	// //排名字段名称,字段id,请求个数|排名字段名称,字段id,请求个数|...
	// tablemenu=涨幅,0,6|跌幅,101,8|5分钟涨幅,1,2|5分钟跌幅,102,4
	private void readTopRankFieldFromConfig() {
		MIniFile defaultIni = new MIniFile();
		defaultIni.setFilePath(mActivity.getApplicationContext(),
				MyApp.DEFAULT_CONFIGPATH);
		String key = "stocksort";

		if (groupData == null) {
			groupData = new ArrayList<TopRankField>();
		}
		groupData.clear();

		String strTemp = defaultIni.ReadString(key, "tablemenu", "");
		if (strTemp.isEmpty()) {
			return;
		}

		String value = "";
		for (int i = 0; i < 20; i++) {
			value = STD.GetValue(strTemp, i + 1, '|');
			if (value.isEmpty()) {
				break;
			}
			String name = STD.GetValue(value, 1, ',');
			String id = STD.GetValue(value, 2, ',');
			String count = STD.GetValue(value, 3, ',');

			TopRankField field = new TopRankField();
			field.name = name;
			field.id = (short) STD.StringToInt(id);
			field.count = STD.StringToInt(count);

			groupData.add(field);
		}
	}

	// set listener for expandableListView
	private void setListener() {
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (parent.isGroupExpanded(groupPosition)) {
					groupData.get(groupPosition).isExpand = false;
				} else {
					groupData.get(groupPosition).isExpand = true;
				}
				requestPaiMing();
				return false;
			}
		});

		expandableListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// showPopupWindow(view);
						return false;
					}
				});

		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TopRankData data = (TopRankData) exListAdapter.getChild(
						groupPosition, childPosition);

				Intent mIntent = new Intent();
				mIntent.setClass(activity, ZQMarketDetailActivity.class);

				TagCodeInfo optionCodeInfo = new TagCodeInfo(data.market,
						data.code, (short) 0, data.name);
				ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;

				mContext.startActivity(mIntent);
				return false;
			}
		});
	}

	// set up the popWindow
	protected void showPopupWindow(View v) {
		View popView = LayoutInflater.from(activity).inflate(
				R.layout.zq_popwindow_hangqing, null);

		Button btn_buy = (Button) popView.findViewById(R.id.btn_buy);
		Button btn_sell = (Button) popView.findViewById(R.id.btn_sell);
		Button btn_min = (Button) popView.findViewById(R.id.btn_min);
		Button btn_kline = (Button) popView.findViewById(R.id.btn_kline);
		Button btn_addself = (Button) popView.findViewById(R.id.btn_addself);

		btn_buy.setOnClickListener(this);
		btn_sell.setOnClickListener(this);
		btn_min.setOnClickListener(this);
		btn_kline.setOnClickListener(this);
		btn_addself.setOnClickListener(this);

		final PopupWindow popupWindow = new PopupWindow(popView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		popupWindow.setTouchable(true);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// Log.i("mengdd", "onTouch : ");
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(
				R.drawable.dingyue));

		// 设置好参数之后再show
		popupWindow.showAsDropDown(v);
	}

	// 沪深个股 全球指数 贵金属 定制板块的展示与撤离 和 数据的更改
	class CheckChangedLinstener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb_hushen:
				mCurrentView = VIEW_HSGG;
				layout_hangqing_others.setVisibility(View.VISIBLE);
				layout_hangqing_dingzhi.setVisibility(View.GONE);
				imgRightSearch.setVisibility(View.VISIBLE);
				imgLeftNote.setVisibility(View.GONE);

				updateZhiShuView();
				requestPaiMing();
				startRequestPMTimer(30);
				queryHQPushInfo();

				break;
			case R.id.rb_quanqiu: {
				mCurrentView = VIEW_QQZS;
				layout_hangqing_quanqiuzhishu.setVisibility(View.VISIBLE);
				layout_hangqing_others.setVisibility(View.GONE);
				layout_hangqing_dingzhi.setVisibility(View.GONE);
				imgRightSearch.setVisibility(View.VISIBLE);
				imgLeftNote.setVisibility(View.GONE);

				mStartIndex = 0;
				stopRequestPMTimer();

				loadListData(KeyDefine.KEY_MARKET_EX_STOCK_ZS);
				mLv_Quanqiuzhishu.setSelection(0);
				queryHQPushInfo();
			}

				break;
			case R.id.rb_guijinshu:
				mCurrentView = VIEW_GJS;
				layout_hangqing_quanqiuzhishu.setVisibility(View.VISIBLE);
				layout_hangqing_others.setVisibility(View.GONE);
				layout_hangqing_dingzhi.setVisibility(View.GONE);
				imgRightSearch.setVisibility(View.VISIBLE);
				imgLeftNote.setVisibility(View.GONE);

				mStartIndex = 0;
				stopRequestPMTimer();

				loadListData(KeyDefine.KEY_MARKET_EX_FUTURE_LD);
				mLv_Quanqiuzhishu.setSelection(0);
				queryHQPushInfo();

				break;
			case R.id.rb_dingzhi:
				mCurrentView = VIEW_DZBK;
				layout_hangqing_others.setVisibility(View.GONE);
				layout_hangqing_dingzhi.setVisibility(View.VISIBLE);
				layout_hangqing_quanqiuzhishu.setVisibility(View.GONE);
				imgRightSearch.setVisibility(View.VISIBLE);
				imgLeftNote.setVisibility(View.VISIBLE);
				imgLeftNote.setOnClickListener(HangQingPager.this);

				mStartIndex = 0;
				stopRequestPMTimer();

				loadDZBKData();
				expandableListView_dzbk.setSelection(0);
				queryHQPushInfo();

				break;
			}
		}

	}

	private void findView() {

		tv_Names[0] = (TextView) view.findViewById(R.id.tv_name0);
		tv_Names[1] = (TextView) view.findViewById(R.id.tv_name1);
		tv_Names[2] = (TextView) view.findViewById(R.id.tv_name2);

		rg_hangqing = (RadioGroup) view.findViewById(R.id.rg_hangqing);

		rb_hushen = (RadioButton) view.findViewById(R.id.rb_hushen);
		rb_quanqiu = (RadioButton) view.findViewById(R.id.rb_quanqiu);
		rb_guijinshu = (RadioButton) view.findViewById(R.id.rb_guijinshu);
		rb_dingzhi = (RadioButton) view.findViewById(R.id.rb_dingzhi);

		llayout_zhishu1 = (LinearLayout) view
				.findViewById(R.id.llayout_zhishu1);
		llayout_zhishu1.setOnClickListener(this);
		llayout_zhishu2 = (LinearLayout) view
				.findViewById(R.id.llayout_zhishu2);
		llayout_zhishu2.setOnClickListener(this);
		llayout_zhishu3 = (LinearLayout) view
				.findViewById(R.id.llayout_zhishu3);
		llayout_zhishu3.setOnClickListener(this);

		layout_hangqing_others = view.findViewById(R.id.layout_hangqing_others);
		layout_hangqing_dingzhi = view
				.findViewById(R.id.layout_hangqing_dingzhi);
		layout_hangqing_quanqiuzhishu = view
				.findViewById(R.id.layout_hangqing_quanqiuzhishu);

		layout_auto_textview_shangzhengzhishu = view
				.findViewById(R.id.layout_auto_textview_shangzhengzhishu);
		tv_Price[0] = (AutoScaleTextView) layout_auto_textview_shangzhengzhishu
				.findViewById(R.id.tv_detail_now_price);
		tv_ZDF[0] = (AutoScaleTextView) layout_auto_textview_shangzhengzhishu
				.findViewById(R.id.tv_detail_zd);

		layout_auto_textview_shenzhengchengzhi = view
				.findViewById(R.id.layout_auto_textview_shenzhengchengzhi);
		tv_Price[1] = (AutoScaleTextView) layout_auto_textview_shenzhengchengzhi
				.findViewById(R.id.tv_detail_now_price);
		tv_ZDF[1] = (AutoScaleTextView) layout_auto_textview_shenzhengchengzhi
				.findViewById(R.id.tv_detail_zd);

		layout_auto_textview_chuangyebanzhi = view
				.findViewById(R.id.layout_auto_textview_chuangyebanzhi);
		tv_Price[2] = (AutoScaleTextView) layout_auto_textview_chuangyebanzhi
				.findViewById(R.id.tv_detail_now_price);
		tv_ZDF[2] = (AutoScaleTextView) layout_auto_textview_chuangyebanzhi
				.findViewById(R.id.tv_detail_zd);

		expandableListView = (ExpandableListView) view
				.findViewById(R.id.expandableListView);

		expandableListView_dzbk = (ExpandableListView) view
				.findViewById(R.id.expandlv_dzbk);
		expandableListView_dzbk
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						queryHQPushInfo();
					}

				});

		expandableListView_dzbk.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {// list停止滚动时加载图片
					// 加载数据
					queryHQPushInfo();
				} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					mMyApp.setHQPushNetHandler(null);
					GlobalNetProgress.HQRequest_MultiCodeInfoPush(
							mMyApp.mHQPushNet, null, 0, 0);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				// 设置当前屏幕显示的起始index和结束index
				mStartIndex = firstVisibleItem;
				mEndIndex = firstVisibleItem + visibleItemCount;
				if (mEndIndex >= mTotalListItemNum_DZBK) {
					mEndIndex = mTotalListItemNum_DZBK - 1;
				}
			}
		});

		mLv_Quanqiuzhishu = (ListView) layout_hangqing_quanqiuzhishu
				.findViewById(R.id.lv_quanqiuzhishu);
		if (mDatas == null) {
			mDatas = new ArrayList<CCodeTableItem>();// 初始化集合
		}
		if (mListAdapter == null) {
			mListAdapter = new HangQingAdapter(mActivity, mDatas);
		}
		mLv_Quanqiuzhishu.setAdapter(mListAdapter);
		mLv_Quanqiuzhishu.setOnItemClickListener(new qqItemClickListener());

		mLv_Quanqiuzhishu.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {// list停止滚动时加载图片
					// 加载数据
					queryHQPushInfo();
				} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					mMyApp.setHQPushNetHandler(null);
					GlobalNetProgress.HQRequest_MultiCodeInfoPush(
							mMyApp.mHQPushNet, null, 0, 0);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				// 设置当前屏幕显示的起始index和结束index
				mStartIndex = firstVisibleItem;
				mEndIndex = firstVisibleItem + visibleItemCount;
				if (mEndIndex >= mTotalListItemNum) {
					mEndIndex = mTotalListItemNum - 1;
				}
			}
		});
	}

	/**
	 * 全球指数 及 贵金属 共用的Adapter 的item的点击事件
	 * 
	 * @author pobo
	 * @date 2015-10-30 下午5:16:06
	 * @className HangQingPager.java
	 * @verson 1.0.0
	 */
	class qqItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CCodeTableItem codeItem = mDatas.get(position);
			if (codeItem.market != HQ_Define.HQ_MARKET_SH_INT
					&& codeItem.market != HQ_Define.HQ_MARKET_SZ_INT) {
				return;
			}
			Intent mIntent = new Intent();
			mIntent.putExtra("STOCKINDEX", position);
			mIntent.setClass(activity, ZQMarketDetailActivity.class);

			TagCodeInfo optionCodeInfo = new TagCodeInfo(codeItem.market,
					codeItem.code, codeItem.group, codeItem.name);
			ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;
			mMyApp.setCurrentOption(optionCodeInfo);
			mContext.startActivity(mIntent);
		}

	}

	private void refreshListData() {
		mListAdapter.notifyDataSetChanged();
	}

	private void loadListData(int id) {
		CPBMarket pbmarket = null;
		for (int i = 0; i < mPBMarkets.size(); i++) {
			if (id == STD.StringToInt(mPBMarkets.get(i).Id)) {
				pbmarket = mPBMarkets.get(i);
				break;
			}
		}
		if (pbmarket == null) {
			return;
		}
		mDatas.clear();

		ArrayList<Rule> rules = pbmarket.mRules;
		if (rules == null)
			return;

		for (int m = 0; m < rules.size()
				&& m < CDataCodeTable.MAX_SAVE_MARKET_COUNT; m++) {
			Rule rule = rules.get(m);
			int nMarket = STD.StringToInt(rule.mMarketId);
			if (nMarket > 0) {
				int codeTableIndex = -1;
				for (int j = 0; j < mMyApp.mCodeTableMarketNum
						&& j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
					if (nMarket == mMyApp.mCodeTable[j].mMarketId) {
						codeTableIndex = j;
						break;
					}
				}
				if (codeTableIndex >= 0
						&& codeTableIndex < mMyApp.mCodeTableMarketNum) {
					String category = rule.mCategory;
					if (category
							.equalsIgnoreCase(KeyDefine.CATEGORY_MARKET_CODE)) {
						ArrayList<CCodeTableItem> tempArray = new ArrayList<CCodeTableItem>();
						String code = rule.mCode;
						tempArray = mMyApp.mCodeTable[codeTableIndex]
								.getDataByCode(nMarket, code);
						mDatas.addAll(tempArray);
					} else if (category
							.equalsIgnoreCase(KeyDefine.CATEGORY_MARKET_GROUP)) {
						ArrayList<CCodeTableItem> tempArray = new ArrayList<CCodeTableItem>();
						String groupCode = rule.mGroupCode;
						tempArray = mMyApp.mCodeTable[codeTableIndex]
								.getDataByGroup(nMarket, groupCode);
						mDatas.addAll(tempArray);
					} else if (category
							.equalsIgnoreCase(KeyDefine.CATEGORY_MARKET)) {
						ArrayList<CCodeTableItem> tempArray = new ArrayList<CCodeTableItem>();
						tempArray = mMyApp.mCodeTable[codeTableIndex]
								.getData(nMarket);
						mDatas.addAll(tempArray);
					}
				}

			}
		}

		mTotalListItemNum = mDatas.size();
		mListAdapter.notifyDataSetChanged();
	}

	private void getTargetMarkets(ArrayList<CPBMarket> pbMarkets) {
		if (pbMarkets == null) {
			return;
		}
		pbMarkets.clear();

		int iCount = mMyApp.mPBMarketArray.size();
		for (int i = 0; i < iCount; i++) {
			CPBMarket info = new CPBMarket();
			info.Name = mMyApp.mPBMarketArray.get(i).Name;
			info.Id = mMyApp.mPBMarketArray.get(i).Id;

			info.mRules = mMyApp.mPBMarketArray.get(i).mRules;

			info.IsDefault = mMyApp.mPBMarketArray.get(i).IsDefault;
			info.IsFixed = mMyApp.mPBMarketArray.get(i).IsFixed;
			info.NormalIcon = mMyApp.mPBMarketArray.get(i).NormalIcon;
			pbMarkets.add(info);
		}
	}

	private void updateZhiShuView() {
		for (int i = 0; i < ZHISHU_NUM && i < mZhiShuCodeInfos.size(); i++) {
			tv_Names[i].setText(mZhiShuCodeInfos.get(i).name);
			TagLocalStockData aStockInfo = new TagLocalStockData();
			if (mMyApp.mHQData_ZQ.search(aStockInfo,
					mZhiShuCodeInfos.get(i).market,
					mZhiShuCodeInfos.get(i).code)) {
				// 现价幅度等
				String nowPrice = ViewTools.getStringByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_NOW);
				String zd = ViewTools.getStringByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_ZD);
				String fd = ViewTools.getStringByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_ZDF_SIGN);

				tv_Price[i].setText(nowPrice);
				tv_Price[i].setTextColor(ViewTools.getColorByFieldID(
						aStockInfo, Global_Define.FIELD_HQ_NOW));

				tv_ZDF[i].setText(String.format("%s %s", zd, fd));
				tv_ZDF[i].setTextColor(ViewTools.getColorByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_NOW));
			} else {
				tv_Price[i].setText(Global_Define.STRING_VALUE_EMPTY);
				tv_ZDF[i].setText(Global_Define.STRING_VALUE_EMPTY + " "
						+ Global_Define.STRING_VALUE_EMPTY);
			}
		}
	}

	// 获取指数行情推送信息
	private void queryHQPushInfo() {

		switch (mCurrentView) {
		case VIEW_HSGG: {
			mMyApp.setHQPushNetHandler(mHandler);
			mRequestCode[1] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
					mMyApp.mHQPushNet, mZhiShuCodeInfos, 0,
					mZhiShuCodeInfos.size());
		}
			break;
		case VIEW_QQZS:
		case VIEW_GJS: {
			mMyApp.setHQPushNetHandler(null);
			if (mDatas == null || mDatas.size() <= 0)
				return;

			ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
			CCodeTableItem item = null;
			if (mEndIndex < (mStartIndex + REFRESH_ITEM_NUM)) {
				mEndIndex = mStartIndex + REFRESH_ITEM_NUM;
			}
			for (int i = mStartIndex; i < mDatas.size() && i < mEndIndex; i++) {
				item = mDatas.get(i);
				TagCodeInfo codeinfo = new TagCodeInfo(item.market, item.code,
						item.group);
				codelist.add(codeinfo);
			}

			mMyApp.setHQPushNetHandler(mHandler);
			if (codelist.size() > 0) {
				showProgress("");
			}
			mRequestCode[1] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
					mMyApp.mHQPushNet, codelist, 0, codelist.size());
		}
			break;
		case VIEW_DZBK: {
			mMyApp.setHQPushNetHandler(null);
			if (groupData_dzbk == null || groupData_dzbk.size() <= 0)
				return;

			ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
			CCodeTableItem item = null;
			if (mEndIndex < (mStartIndex + REFRESH_ITEM_NUM)) {
				mEndIndex = mStartIndex + REFRESH_ITEM_NUM;
			}

			int index = 0;
			for (int i = 0; i < groupData_dzbk.size(); i++) {
				if (expandableListView_dzbk.isGroupExpanded(i)) {
					ArrayList<CCodeTableItem> items = childDatas_dzbk
							.get(groupData_dzbk.get(i).Id);
					for (int k = 0; k < items.size() && index < mEndIndex; k++, index++) {
						if (index < mStartIndex) {
							continue;
						}
						item = items.get(k);
						TagCodeInfo codeinfo = new TagCodeInfo(item.market,
								item.code, item.group);
						codelist.add(codeinfo);
					}
				} else {
					index++;
				}
			}

			mMyApp.setHQPushNetHandler(mHandler);
			if (codelist.size() > 0) {
				showProgress("");
			}
			mRequestCode[1] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
					mMyApp.mHQPushNet, codelist, 0, codelist.size());
		}
			break;
		default:
			break;
		}
	}

	// parse top rank data and store it into ArrayList<TagLocalTrendData>
	private void parseTopRankData(byte[] szData, int nSize) {
		L.i(TAG, "******************Start parseTopRankData************");
		int nCount = 0;
		int offset = 0;
		while (nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			if (wPackSize < 0) {
				L.e(TAG, "ERROR: parseTopRankData wPackSize = " + wPackSize);
				break;
			}
			offset += 4;
			if (wPackSize > nSize - offset)
				break;

			CPbDataPackage pack = new CPbDataPackage();
			wPackSize = CPbDataDecode.DecodeOnePackage(szData, offset,
					wPackSize, pack);

			offset += wPackSize;
			if (offset < 0) {
				L.e(TAG, "ERROR: CPbDataDecode.DecodeOnePackage failed");
			}

			if (pack.m_wPackageID == 18) {

				byte RankRegion = (byte) pack.GetNormalFieldByID(800).GetInt8();
				short RankField = (short) pack.GetNormalFieldByID(801)
						.GetInt8();

				if (childDatas.get(RankField) == null) {
					ArrayList<TopRankData> datas = new ArrayList<TopRankData>();
					childDatas.put(RankField, datas);
				}
				childDatas.get(RankField).clear();

				int nArraySize = pack.GetArraySize(0);
				for (int i = 0; i < nArraySize; i++) {
					TopRankData record = new TopRankData();
					record.sortField = RankField;
					record.code = pack.GetArrayFieldByID(10, i).GetString();
					record.market = (short) pack.GetArrayFieldByID(11, i)
							.GetInt16();
					record.fSortValue = pack.GetArrayFieldByID(802, i)
							.GetFloat();
					record.nLastClear = pack.GetArrayFieldByID(24, i)
							.GetInt32();
					record.nLastClose = pack.GetArrayFieldByID(23, i)
							.GetInt32();
					record.nLastPrice = pack.GetArrayFieldByID(29, i)
							.GetInt32();

					childDatas.get(record.sortField).add(record);
				}

			}
		}

		L.i(TAG, "*******************End parseTopRankData,nRecordCount="
				+ nCount);
	}

	// 设置点击侦听事件给每一个popwindow里面的button
	// 最后一个给titlebar的button使用
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llayout_zhishu1: {
			if (mZhiShuCodeInfos != null && mZhiShuCodeInfos.size() > 0) {
				Intent mIntent = new Intent();
				mIntent.setClass(activity, ZQMarketDetailActivity.class);

				TagCodeInfo optionCodeInfo = new TagCodeInfo(
						mZhiShuCodeInfos.get(0).market,
						mZhiShuCodeInfos.get(0).code,
						mZhiShuCodeInfos.get(0).group,
						mZhiShuCodeInfos.get(0).name);
				ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;

				mContext.startActivity(mIntent);
			}
		}
			break;
		case R.id.llayout_zhishu2: {
			if (mZhiShuCodeInfos != null && mZhiShuCodeInfos.size() > 1) {
				Intent mIntent = new Intent();
				mIntent.setClass(activity, ZQMarketDetailActivity.class);

				TagCodeInfo optionCodeInfo = new TagCodeInfo(
						mZhiShuCodeInfos.get(1).market,
						mZhiShuCodeInfos.get(1).code,
						mZhiShuCodeInfos.get(1).group,
						mZhiShuCodeInfos.get(1).name);
				ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;

				mContext.startActivity(mIntent);
			}
		}
			break;
		case R.id.llayout_zhishu3: {
			if (mZhiShuCodeInfos != null && mZhiShuCodeInfos.size() > 2) {
				Intent mIntent = new Intent();
				mIntent.setClass(activity, ZQMarketDetailActivity.class);

				TagCodeInfo optionCodeInfo = new TagCodeInfo(
						mZhiShuCodeInfos.get(2).market,
						mZhiShuCodeInfos.get(2).code,
						mZhiShuCodeInfos.get(2).group,
						mZhiShuCodeInfos.get(2).name);
				ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;

				mContext.startActivity(mIntent);
			}
		}
			break;
		case R.id.btn_buy:
			Toast.makeText(mActivity, "btn_buy", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_sell:
			Toast.makeText(mActivity, "btn_sell", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_min:
			Toast.makeText(mActivity, "btn_min", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_kline:
			Toast.makeText(mActivity, "btn_kline", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_addself:
			Toast.makeText(mActivity, "btn_addself", Toast.LENGTH_SHORT).show();
			break;
		case R.id.img_public_black_head_title_right_blue_search: {
			Intent intent = new Intent(mActivity,
					ZiXuanFastSearchActivity.class);
			mActivity.startActivity(intent);
		}
			break;
		case R.id.img_public_black_head_title_left_note: {

			Intent intent = new Intent(mActivity, DZBKEditActivity.class);
			mActivity.startActivity(intent);
			mActivity.overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_bottom_out);
		}
			break;
		default:
			break;
		}

	}

	// fileds:排名字段，可以多个
	// reqCount:请求个数
	private void requestPaiMing() {
		int count = groupData.size();
		int reqCount = 6;
		short[] fields = null;
		int fieldCount = 0;
		if (count > 0) {
			fields = new short[count];
			for (int i = 0; i < count; i++) {
				if (groupData.get(i).isExpand) {
					fields[i] = (short) groupData.get(i).id;
					reqCount = groupData.get(i).count;
					fieldCount++;
				} else {
					fields[i] = -1;
				}
			}
		}
		// range 排名范围 （ 80 - 沪深A股 81 - 沪A股82 - 沪B股83 - 深A股84 - 深B股85 - 沪债86 -
		// 深债87 - 中小板88 - 沪深B股89 - 沪深权证90 - 创业板91 - 个股期权缺省 - 沪深A）
		if (fields == null || fields.length <= 0 || fieldCount <= 0) {
			return;
		}
		short range = 80;
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[2] = GlobalNetProgress
				.HQRequest_PaiMing(mMyApp.mCertifyNet, range,
						(short) fieldCount, fields, reqCount);
	}

	private void startRequestPMTimer(int time)// "快捷反手"开仓撤单
	{
		stopRequestPMTimer();

		mTimerRequestPaiMing = new Timer();
		mTimerRequestPaiMing.schedule(new TimerTask() {
			//
			public void run() {
				if (mHandler != null) {
					Message msg = mHandler.obtainMessage();
					msg.what = TIMER_REQUEST;
					mHandler.sendMessage(msg);
				}
			}
		}, time * 1000, time * 1000);
	}

	private void stopRequestPMTimer() {
		if (mTimerRequestPaiMing != null) {
			mTimerRequestPaiMing.cancel();
		}
		mTimerRequestPaiMing = null;
	}

	@Override
	public void visibleOnScreen() {
		if (bPagerReady) {
			switch (mCurrentView) {
			case VIEW_HSGG: {
				if (!rb_hushen.isChecked()) {
					rb_hushen.setChecked(true);
				} else {
					updateZhiShuView();
					requestPaiMing();
					startRequestPMTimer(30);
				}

			}
				break;
			case VIEW_QQZS:
			case VIEW_GJS:
				break;
			case VIEW_DZBK: {
				loadDZBKData();
				expandableListView_dzbk.setSelection(0);
			}
				break;
			default:
				break;
			}

			queryHQPushInfo();
		}
	}

	@Override
	public void invisibleOnScreen() {
		stopRequestPMTimer();
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null,
				0, 0);
	}
}
