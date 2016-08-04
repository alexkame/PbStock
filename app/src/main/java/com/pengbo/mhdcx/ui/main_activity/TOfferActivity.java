package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pengbo.mhdzq.R;
import android.view.ViewGroup.LayoutParams;

import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdcx.adapter.MyDialogAdapter;
import com.pengbo.mhdcx.adapter.OurListViewMiddleAdapter;
import com.pengbo.mhdcx.adapter.TOfferListAdapter;
import com.pengbo.mhdcx.adapter.TOfferMyDialogAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdcx.fragment.TradeOrderFragment;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdcx.ui.activity.ScreenDetailActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.view.MyTHScrollView;
import com.pengbo.mhdcx.view.MyTListView;
import com.pengbo.mhdcx.view.MyHScrollView.OnScrollChangedListener;
import com.pengbo.mhdcx.widget.MyListDialog;
import com.pengbo.mhdcx.widget.MyListDialog.Dialogcallback;


/**
 * 期权 T型报价 行权价添加 A-M  字母 
 * 
 * @author pobo
 * @date   2015-11-24 下午3:24:01
 * @className TOfferActivity.java
 * @verson 1.0.0
 */
public class TOfferActivity extends HdActivity implements OnClickListener,
		OnCheckedChangeListener {
	private static final String TAG = "TOfferActivity";
	private static final int UPDATE_UI = 1;

	public static final int MENU_ITEM_BUYOPEN = 0;
	public static final int MENU_ITEM_SELLOPEN = 1;
	public static final int MENU_ITEM_BUYCLOSE = 2;
	public static final int MENU_ITEM_SELLCLOSE = 3;

	private View mViewDetail, mBack;
	private ImageView mRefresh;
	private View mPullDownView;// 点击下拉改变期权名称
	private View mTitleHeadView, mGouGuView;
	private TextView mMiddleText;// 点击下拉时 左边的文本内容改变控件
	private TextView mMiddleText_BiaoDi;// 标的价格
	PopupWindow mWindow;
	private ListView mPopLv;
	private TextView mCancel;

	private TextView mMonth;
	private MyListDialog myDialog;// 自定义对话框

	private TOfferMyDialogAdapter adapter;

	private HeadListDataService hService;

	private View mHead1;
	private View mHead2;
	private View mHead0;

	private MyTListView mListView1;// 左边显示看涨list
	private MyTListView mListView2;// 右边显示看跌list
	private MyTListView mListView0;// 中间显示行权价list
	private int mListWith = 0;

	private MyTHScrollView mTHScrollView1;
	private MyTHScrollView mTHScrollView2;
	// private View mHead_Left, mHead_Right; //左边和右边listview的表头

	private ArrayList<TagLocalStockData> mUpDatas;//
	private ArrayList<TagLocalStockData> mDownDatas; //
	private ArrayList<TagCodeInfo> mUpOptionList;//
	private ArrayList<TagCodeInfo> mDownOptionList;
	private TagCodeInfo mCurrentSelectOption;
	private List<TagCodeInfo> mStockList; // 标的列表
	private List<String> mDateList;// 合约到期列表
	private ArrayList<String> mListXingQuanJia;
	private TOfferListAdapter mListAdapter_Up; // 看涨
	private TOfferListAdapter mListAdapter_Down; // 看跌
	private OurListViewMiddleAdapter mListAdapter_XQJ;

	private MyApp mMyApp;
	private Context mContext;
	// 当前标的的code和market
	private TagLocalStockData mCurrentStockData = null;
	private String mCurrentStockCode = null;
	private short mCurrentStockMarket = 0;
	private int mCurrentDate = 0;// 当前到期年月
	private int mCurrentBiaoDiIndex = 0;

	public int mRequestCode[];// 请求标记 0:query push

	private int mCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
	private int mScreenItemNum = 5;
	private PopupWindow mMenuWindow;
	private View tempView;
	private RadioGroup mMainRG;

	private Button mBtnBuyKai, mBtnBuyPing, mBtnSellKai, mBtnSellPing,
			mJiaZiXuan;
	private boolean mIsMyStock = false;

	/**
	 * 消息处理
	 */

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			case UPDATE_UI:

				mTHScrollView1.resetToDefaultPos();
				mTHScrollView2.resetToDefaultPos();
				break;

			case GlobalNetConnect.MSG_UPDATE_DATA: {
				if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
					refreshListData();
					
					if (mCurrentStockData != null && mMiddleText_BiaoDi != null)
					{
						mMyApp.mStockConfigData.getData(mCurrentStockData, mCurrentStockMarket,
								mCurrentStockCode, false);
						// 现价幅度等
						String nowPrice = ViewTools.getStringByFieldID(mCurrentStockData,
								Global_Define.FIELD_HQ_NOW);
						String fd = ViewTools.getStringByFieldID(mCurrentStockData,
								Global_Define.FIELD_HQ_ZDF_SIGN);
						mMiddleText_BiaoDi.setText(String.format("%s %s", nowPrice, fd));
						mMiddleText_BiaoDi.setTextColor(ViewTools.getColorByFieldID(
								mCurrentStockData, Global_Define.FIELD_HQ_NOW));
					}
					if (mListAdapter_Up == null) {
						mListAdapter_Up = new TOfferListAdapter(mMyApp,
								getApplicationContext(), mUpDatas, true,
								mScreenItemNum);
						mListView1.setAdapter(mListAdapter_Up);

					} else {
						mListAdapter_Up.notifyDataSetChanged();
					}

					if (mListAdapter_Down == null) {
						mListAdapter_Down = new TOfferListAdapter(mMyApp,
								getApplicationContext(), mDownDatas, false,
								mScreenItemNum);
						mListView2.setAdapter(mListAdapter_Down);

					} else {
						mListAdapter_Down.notifyDataSetChanged();
					}

					if (mListAdapter_XQJ == null) {
						mListAdapter_XQJ = new OurListViewMiddleAdapter(
								getApplicationContext(), mListXingQuanJia);

						mListView0.setAdapter(mListAdapter_XQJ);
					} else {
						mListAdapter_XQJ.notifyDataSetChanged();
					}
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
		mContext = this;

		mCurrentOrientation = getResources().getConfiguration().orientation;

		// 横屏模式全屏显示T型报价
		if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
			this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		}
		setContentView(R.layout.activity_t_offer);

		final int[] data = (int[]) getLastNonConfigurationInstance();

		if (data != null) {

			this.mCurrentBiaoDiIndex = data[0];
			this.mCurrentDate = data[1];
		}

		initData();
		initView();
	}

	private void initData() {
		mMyApp = (MyApp) this.getApplication();
		mRequestCode = new int[2];

		if (mUpDatas == null) {
			mUpDatas = new ArrayList<TagLocalStockData>();
		}
		if (mDownDatas == null) {
			mDownDatas = new ArrayList<TagLocalStockData>();
		}
		if (mUpOptionList == null) {
			mUpOptionList = new ArrayList<TagCodeInfo>();
		}
		if (mDownOptionList == null) {
			mDownOptionList = new ArrayList<TagCodeInfo>();
		}
		if (mListXingQuanJia == null) {
			mListXingQuanJia = new ArrayList<String>();
		}
		if (mCurrentSelectOption == null) {
			mCurrentSelectOption = new TagCodeInfo();
		}

		hService = new HeadListDataService(this);

		mStockList = getTargetTagCodeInfo();
		if (mCurrentBiaoDiIndex >= 0 && mCurrentBiaoDiIndex < mStockList.size()) {
			mCurrentStockCode = mStockList.get(mCurrentBiaoDiIndex).code;
			mCurrentStockMarket = mStockList.get(mCurrentBiaoDiIndex).market;
		} else {
			mCurrentStockCode = "";
			mCurrentStockMarket = 0;
		}
		mDateList = getDatelists(mCurrentStockCode, mCurrentStockMarket);
		if (!mDateList.isEmpty() && mCurrentDate <= 0) {
			mCurrentDate = STD.StringToInt(mDateList.get(0));
		}

		mCurrentStockData = new TagLocalStockData();
		mMyApp.mStockConfigData.getData(mCurrentStockData, mCurrentStockMarket,
				mCurrentStockCode, false);
	}

	// init the view
	private void initView() {

		mViewDetail = this.findViewById(R.id.middle_linearlayout1);
		mViewDetail.setVisibility(View.GONE);

		mBack = this.findViewById(R.id.linearlayout_out_of_back);
		mBack.setOnClickListener(this);

		mRefresh = (ImageView) this
				.findViewById(R.id.screen_detail_header_right_refresh);
		mRefresh.setOnClickListener(this);
		mMonth = (TextView) this.findViewById(R.id.textview_month);
		String temp = STD.IntToString(mCurrentDate);
		if (temp.length() >= 2) {
			temp = temp.substring(temp.length() - 2);
		}
		mMonth.setText(String.format("%d月", STD.StringToInt(temp)));
		mMonth.setOnClickListener(this);

		mPullDownView = this.findViewById(R.id.middle_t_offer);
		mPullDownView.setVisibility(View.VISIBLE);
		mPullDownView.setOnClickListener(this);

		mTitleHeadView = this.findViewById(R.id.t_offer_title_view);
		mGouGuView = this.findViewById(R.id.red_green_layout);

		mMiddleText = (TextView) this
				.findViewById(R.id.t_offer_middle_textview);
		mMiddleText_BiaoDi = (TextView) this.findViewById(R.id.t_offer_fudong);
		mMiddleText_BiaoDi.setText(Global_Define.STRING_VALUE_EMPTY);

		if (mStockList.isEmpty()) {
			mMiddleText.setText(Global_Define.STRING_VALUE_EMPTY);
		} else {
			mMiddleText.setText(mStockList.get(mCurrentBiaoDiIndex).name);
		}
		// 现价幅度等
		String nowPrice = ViewTools.getStringByFieldID(mCurrentStockData,
				Global_Define.FIELD_HQ_NOW);
		String fd = ViewTools.getStringByFieldID(mCurrentStockData,
				Global_Define.FIELD_HQ_ZDF_SIGN);

		mMiddleText_BiaoDi.setText(String.format("%s %s", nowPrice, fd));
		mMiddleText_BiaoDi.setTextColor(ViewTools.getColorByFieldID(
				mCurrentStockData, Global_Define.FIELD_HQ_NOW));

		mHead1 = (View) findViewById(R.id.head_leftlist);
		mHead2 = (View) findViewById(R.id.head_rightlist);
		mHead0 = (View) findViewById(R.id.head_midllelist);

		mMainRG = (RadioGroup) findViewById(R.id.main_tabs_rg);
		mMainRG.setOnCheckedChangeListener(this);
		mMainRG.check(R.id.main_tab_rb_b);

		if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			// If current screen is portrait
			mScreenItemNum = 5;
			mMainRG.setVisibility(View.VISIBLE);

		} else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			// If current screen is landscape
			mScreenItemNum = 9;
			mMainRG.setVisibility(View.GONE);
			LinearLayout layoutHList = (LinearLayout) findViewById(R.id.llayout_hlist);
			RelativeLayout.LayoutParams lplayoutHList = (android.widget.RelativeLayout.LayoutParams) layoutHList
					.getLayoutParams();
			if (lplayoutHList != null) {
				lplayoutHList.bottomMargin = 0;
				layoutHList.setLayoutParams(lplayoutHList);
			}
		}

		LayoutParams lp = null;// AppConstants.LIST_HEADER_ITEMS
		int listWidth = 0;
		int iScreenWidth = ViewTools.getScreenSize(this).widthPixels;
		for (int i = 0; i < AppConstants.TLIST_HEADER_ITEMS; i++) {
			String strid = String.format("hv_head_item%d", i + 1);
			View tmp = mHead1.findViewById(this.getResources().getIdentifier(
					strid, "id", this.getPackageName()));
			lp = tmp.getLayoutParams();
			if (i < AppConstants.TLIST_HEADER_ITEMS - 5) {
				lp.width = iScreenWidth / (mScreenItemNum - 1);
			} else {
				lp.width = iScreenWidth / mScreenItemNum;
			}
			listWidth += lp.width + 1;
			tmp.setLayoutParams(lp);

			tmp = mHead2.findViewById(this.getResources().getIdentifier(strid,
					"id", this.getPackageName()));
			lp = tmp.getLayoutParams();
			if (i >= 5) {
				lp.width = iScreenWidth / (mScreenItemNum - 1);
			} else {
				lp.width = iScreenWidth / mScreenItemNum;
			}
			listWidth += lp.width + 1;
			tmp.setLayoutParams(lp);

			// tmp = mHead0.findViewById(R.id.tv_toffer_xq_price);
			// lp = tmp.getLayoutParams();

			// listWidth += lp.width + 1;
			// tmp.setLayoutParams(lp);
		}

		mListWith = listWidth;

		ViewGroup rlayout1 = (ViewGroup) findViewById(R.id.horizontalOfferScrollView1);
		lp = rlayout1.getLayoutParams();
		lp.width = (iScreenWidth / mScreenItemNum) * (mScreenItemNum / 2);
		rlayout1.setLayoutParams(lp);

		ViewGroup rlayout2 = (ViewGroup) findViewById(R.id.horizontalOfferScrollView0);
		lp = rlayout2.getLayoutParams();
		lp.width = (iScreenWidth / mScreenItemNum);
		rlayout2.setLayoutParams(lp);

		ViewGroup rlayout3 = (ViewGroup) findViewById(R.id.horizontalOfferScrollView2);
		lp = rlayout3.getLayoutParams();
		lp.width = (iScreenWidth / mScreenItemNum) * (mScreenItemNum / 2);
		rlayout3.setLayoutParams(lp);

		if (mListView1 == null) {
			mListView1 = (MyTListView) findViewById(R.id.listView1);
			mListView1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					{
						if (mUpDatas == null || mUpDatas.isEmpty()
								|| mListView1.mbNegation) {
							return;
						}
						TagLocalStockData option = mUpDatas.get(position);
						Intent mIntent = new Intent();
						mIntent.setClass(TOfferActivity.this,
								ScreenDetailActivity.class);

						TagCodeInfo stockCodeInfo = new TagCodeInfo(
								option.optionData.StockMarket,
								option.optionData.StockCode, option.group,
								option.name);
						TagCodeInfo optionCodeInfo = new TagCodeInfo(
								option.HQData.market, option.HQData.code,
								option.group, option.name);
						ScreenDetailActivity.mOptionCodeInfo = optionCodeInfo;
						ScreenDetailActivity.mStockCodeInfo = stockCodeInfo;

						startActivity(mIntent);

					}
				}

			});

			mListView1
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							if (mUpDatas == null || mUpDatas.isEmpty()
									|| mListView1.mbNegation) {
								return false;
							}
							mCurrentSelectOption.code = mUpDatas.get(position).HQData.code;
							mCurrentSelectOption.name = mUpDatas.get(position).name;
							mCurrentSelectOption.market = mUpDatas
									.get(position).HQData.market;
							mCurrentSelectOption.group = mUpDatas.get(position).group;

							showPopupWindow(view, view, true, position);
							return true;
						}

					});

			mListView1.setWidth(listWidth);

			mListView1.setScreenItemNum(mScreenItemNum / 2);
			mListView1.setLeftToRight(false);

			mListAdapter_Up = new TOfferListAdapter(mMyApp,
					getApplicationContext(), mUpDatas, true, mScreenItemNum);
			mListView1.setAdapter(mListAdapter_Up);

		}
		if (mListView2 == null) {
			mListView2 = (MyTListView) findViewById(R.id.listView2);
			mListView2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					{
						if (mUpDatas == null || mUpDatas.isEmpty()
								|| mListView2.mbNegation) {
							return;
						}

						TagLocalStockData option = mDownDatas.get(position);
						Intent mIntent = new Intent();
						mIntent.setClass(TOfferActivity.this,
								ScreenDetailActivity.class);

						TagCodeInfo stockCodeInfo = new TagCodeInfo(
								option.optionData.StockMarket,
								option.optionData.StockCode, option.group,
								option.name);
						TagCodeInfo optionCodeInfo = new TagCodeInfo(
								option.HQData.market, option.HQData.code,
								option.group, option.name);
						ScreenDetailActivity.mOptionCodeInfo = optionCodeInfo;
						ScreenDetailActivity.mStockCodeInfo = stockCodeInfo;

						startActivity(mIntent);

					}
				}

			});

			mListView2
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							if (mDownDatas == null || mDownDatas.isEmpty()
									|| mListView2.mbNegation) {
								return false;
							}
							mCurrentSelectOption.code = mDownDatas
									.get(position).HQData.code;
							mCurrentSelectOption.name = mDownDatas
									.get(position).name;
							mCurrentSelectOption.market = mDownDatas
									.get(position).HQData.market;
							mCurrentSelectOption.group = mDownDatas
									.get(position).group;
							showPopupWindow(view, view, false, position);
							return true;
						}

					});

			mListView2.setWidth(listWidth);

			mListView2.setScreenItemNum(mScreenItemNum / 2);
			mListView2.setLeftToRight(true);

			mListAdapter_Down = new TOfferListAdapter(mMyApp,
					getApplicationContext(), mDownDatas, false, mScreenItemNum);
			mListView2.setAdapter(mListAdapter_Down);

		}
		if (mListView0 == null) {
			mListView0 = (MyTListView) findViewById(R.id.listView0);

			mListAdapter_XQJ = new OurListViewMiddleAdapter(
					getApplicationContext(), mListXingQuanJia);

			mListView0.setAdapter(mListAdapter_XQJ);

		}

		if (mTHScrollView1 == null) {
			mTHScrollView1 = (MyTHScrollView) findViewById(R.id.horizontalOfferScrollView1);
			mTHScrollView1.mListHead = mListView1.mListHead;
		}

		if (mTHScrollView2 == null) {
			mTHScrollView2 = (MyTHScrollView) findViewById(R.id.horizontalOfferScrollView2);

			mTHScrollView2.mListHead = mListView2.mListHead;

		}

		mTHScrollView2.setWidth(listWidth);
		mTHScrollView1.setWidth(listWidth);

	}

	private void TInit() {
		mTHScrollView1.setRelatedScrollView(mTHScrollView2);
		mTHScrollView2.setRelatedScrollView(mTHScrollView1);

		mTHScrollView1.setLeftToRight(false);
		mTHScrollView2.setLeftToRight(true);

		mListView1.RemoveAllRelatedListView();
		mListView2.RemoveAllRelatedListView();
		mListView0.RemoveAllRelatedListView();
		
		mListView1.AddRelatedListView(mListView0);
		mListView1.AddRelatedListView(mListView2);

		mListView2.AddRelatedListView(mListView0);
		mListView2.AddRelatedListView(mListView1);

		mListView0.AddRelatedListView(mListView1);
		mListView0.AddRelatedListView(mListView2);

		mTHScrollView1.resetToDefaultPos();
		mTHScrollView2.resetToDefaultPos();
	}

	private void RelatedHScrollViewInit() {

		mTHScrollView1.RemoveAllOnScrollChangedListener();
		mTHScrollView2.RemoveAllOnScrollChangedListener();

		final int leftpos = mListWith
				* (AppConstants.TLIST_HEADER_ITEMS - (mScreenItemNum / 2))
				/ AppConstants.TLIST_HEADER_ITEMS;

		final int rightpos = 0;

		mTHScrollView1
				.AddOnScrollChangedListener(new OnScrollChangedListener() {
					int pos = leftpos;
					int total = 0;

					@Override
					public void onScrollChanged(int l, int t, int oldl, int oldt) {
						// TODO Auto-generated method stub
						if (mTHScrollView1.sleftouched && (l != pos)) {
							mTHScrollView2.scrollBy(oldl - l, t - oldt);
							pos = l;
						}

						total = total + l - oldl;
						L.d("scoll", "sc1 moved:" + total);
					}
				});

		mTHScrollView2
				.AddOnScrollChangedListener(new OnScrollChangedListener() {
					int pos = rightpos;
					int total = 0;

					@Override
					public void onScrollChanged(int l, int t, int oldl, int oldt) {
						// TODO Auto-generated method stub
						if (mTHScrollView2.sleftouched && (l != pos)) {
							mTHScrollView1.scrollBy((oldl - l), t - oldt);
							pos = l;
						}
						total = total + oldl - l;
						L.d("scoll", "sc2 moved:" + total);
					}
				});
	}

	private void ListRelationInit() {

		if (!ViewTools.getHandSetInfoSpecial())
			return;

		mListView1.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				View subView = view.getChildAt(0);
				if (subView != null && !mListView0.isMoveSelf()) {
					mListView0.setSelectionFromTop(firstVisibleItem,
							subView.getTop());
				}

				if (subView != null && !mListView2.isMoveSelf()) {
					mListView2.setSelectionFromTop(firstVisibleItem,
							subView.getTop());
				}
			}
		});

		mListView2.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				final View subView = view.getChildAt(0);
				final int item = firstVisibleItem;
				if (subView != null && !mListView0.isMoveSelf()) {
					mListView0.setSelectionFromTop(firstVisibleItem,
							subView.getTop());
				}

				if (subView != null && !mListView1.isMoveSelf()) {
					mListView1.setSelectionFromTop(firstVisibleItem,
							subView.getTop());
				}
			}
		});

		mListView0.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				View subView = view.getChildAt(0);
				if (subView != null && !mListView1.isMoveSelf()) {
					mListView1.setSelectionFromTop(firstVisibleItem,
							subView.getTop());
				}

				if (subView != null && !mListView2.isMoveSelf()) {
					mListView2.setSelectionFromTop(firstVisibleItem,
							subView.getTop());
				}
			}
		});

	}

	private void loadListData() {
		byte fx = 0;// 认购
		mUpOptionList.clear();
		mUpOptionList = hService.getTagCodeInfos(mCurrentStockCode,
				mCurrentStockMarket, mCurrentDate, fx);
		fx = 1;// 认沽
		mDownOptionList.clear();
		mDownOptionList = hService.getTagCodeInfos(mCurrentStockCode,
				mCurrentStockMarket, mCurrentDate, fx);

		mUpDatas.clear();
		mDownDatas.clear();
		mListXingQuanJia.clear();
		TagCodeInfo taginfo = null;

		float nValue = -1.0f;
		int position = -1;
		for (int i = 0; i < mUpOptionList.size(); i++) {
			taginfo = mUpOptionList.get(i);
			TagLocalStockData optionData = new TagLocalStockData();
			mMyApp.mHQData.getData(optionData, taginfo.market, taginfo.code,
					false);

			boolean bAdded = false;
			for (int m = 0; m < mUpDatas.size(); m++) {
				if (optionData.optionData.StrikePrice <= mUpDatas.get(m).optionData.StrikePrice) {
					mUpDatas.add(m, optionData);
					
					String mStrName = ViewTools.getStringByFieldID(
							optionData, Global_Define.FIELD_HQ_NAME_ANSI);
					String mStrDistingguish = DataTools.distinguishStockName(mStrName);
					String mStrxqj = ViewTools.getStringByFieldID(
							optionData, Global_Define.FIELD_HQ_XQJ);
					mListXingQuanJia.add(m, mStrxqj+mStrDistingguish);
					
//					mListXingQuanJia.add(m, ViewTools.getStringByFieldID(
//							optionData, Global_Define.FIELD_HQ_XQJ));
					bAdded = true;

					// 计算标记现价与行权价最近的位置
					float temp = Math.abs(optionData.optionData.StrikePrice
							- ViewTools.getPriceByFieldNo(
									Global_Define.FIELD_HQ_NOW,
									mCurrentStockData));
					if (nValue < 0) {
						nValue = temp;
						position = m;
					} else {
						if (temp < nValue) {
							nValue = temp;
							position = m;
						} else {
							if (m <= position) {
								position++;
							}
						}
					}
					break;
				}
			}
			if (!bAdded) {
				mUpDatas.add(optionData);
				
				String mStrName = ViewTools.getStringByFieldID(
						optionData, Global_Define.FIELD_HQ_NAME_ANSI);
				String mStrDistingguish = DataTools.distinguishStockName(mStrName);
				String mStrxqj = ViewTools.getStringByFieldID(
						optionData, Global_Define.FIELD_HQ_XQJ);
				mListXingQuanJia.add(mStrxqj+mStrDistingguish);
			
//				mListXingQuanJia.add(ViewTools.getStringByFieldID(optionData,
//						Global_Define.FIELD_HQ_XQJ));

				float temp = Math.abs(optionData.optionData.StrikePrice
						- ViewTools.getPriceByFieldNo(
								Global_Define.FIELD_HQ_NOW, mCurrentStockData));
				if (nValue < 0) {
					nValue = temp;
					position = mUpDatas.size() - 1;
				} else {
					if (temp < nValue) {
						nValue = temp;
						position = mUpDatas.size() - 1;
					}
				}
			}

		}

		if (mListAdapter_Up != null) {
			mListAdapter_Up.setmTPosition(position);
		}
		if (mListAdapter_Down != null) {
			mListAdapter_Down.setmTPosition(position);
		}
		if (mListAdapter_XQJ != null) {
			mListAdapter_XQJ.setmPosition(position);
		}

		for (int i = 0; i < mUpDatas.size(); i++) {
			for (int m = 0; m < mDownOptionList.size(); m++) {
				taginfo = mDownOptionList.get(m);
				TagLocalStockData optionData = new TagLocalStockData();
				mMyApp.mHQData.getData(optionData, taginfo.market,
						taginfo.code, false);

				if (optionData.optionData.StrikePrice == mUpDatas.get(i).optionData.StrikePrice) {
					mDownDatas.add(optionData);
					break;
				}
			}
		}
		
		queryHQPushInfo();
	}

	private void refreshListData() {
		mMyApp.mStockConfigData.getData(mCurrentStockData, mCurrentStockMarket,
				mCurrentStockCode, false);
		// 现价幅度等
		String nowPrice = ViewTools.getStringByFieldID(mCurrentStockData,
				Global_Define.FIELD_HQ_NOW);
		String fd = ViewTools.getStringByFieldID(mCurrentStockData,
				Global_Define.FIELD_HQ_ZDF_SIGN);

		mMiddleText_BiaoDi.setText(String.format("%s %s", nowPrice, fd));
		mMiddleText_BiaoDi.setTextColor(ViewTools.getColorByFieldID(
				mCurrentStockData, Global_Define.FIELD_HQ_NOW));

		ArrayList<TagLocalStockData> tempList = new ArrayList<TagLocalStockData>();
		tempList.addAll(mUpDatas);

		mUpDatas.clear();

		TagLocalStockData data = null;

		float nValue = -1.0f;
		int position = -1;
		for (int i = 0; i < tempList.size(); i++) {
			data = tempList.get(i);
			TagLocalStockData optionData = new TagLocalStockData();
			mMyApp.mHQData.getData(optionData, data.market, data.code, false);

			mUpDatas.add(optionData);

			// 计算标记现价与行权价最近的位置
			float temp = Math.abs(optionData.optionData.StrikePrice
					- ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW,
							mCurrentStockData));
			if (nValue < 0) {
				nValue = temp;
				position = i;
			} else {
				if (temp < nValue) {
					nValue = temp;
					position = i;
				}
			}
		}

		if (mListAdapter_Up != null) {
			mListAdapter_Up.setmTPosition(position);
		}
		if (mListAdapter_Down != null) {
			mListAdapter_Down.setmTPosition(position);
		}
		if (mListAdapter_XQJ != null) {
			mListAdapter_XQJ.setmPosition(position);
		}

		tempList.clear();
		tempList.addAll(mDownDatas);
		mDownDatas.clear();
		for (int i = 0; i < tempList.size(); i++) {
			data = tempList.get(i);
			TagLocalStockData optionData = new TagLocalStockData();
			mMyApp.mHQData.getData(optionData, data.market, data.code, false);
			mDownDatas.add(optionData);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		TInit();
		RelatedHScrollViewInit();

		loadListData();
		if (mListAdapter_Up == null) {
			mListAdapter_Up = new TOfferListAdapter(mMyApp,
					getApplicationContext(), mUpDatas, true, mScreenItemNum);
			mListView1.setAdapter(mListAdapter_Up);

		} else {
			mListAdapter_Up.notifyDataSetChanged();
		}

		if (mListAdapter_Down == null) {
			mListAdapter_Down = new TOfferListAdapter(mMyApp,
					getApplicationContext(), mDownDatas, false, mScreenItemNum);

			mListView2.setAdapter(mListAdapter_Down);

		} else {
			mListAdapter_Down.notifyDataSetChanged();
		}

		if (mListAdapter_XQJ == null) {
			mListAdapter_XQJ = new OurListViewMiddleAdapter(
					getApplicationContext(), mListXingQuanJia);

			mListView0.setAdapter(mListAdapter_XQJ);

		} else {
			mListAdapter_XQJ.notifyDataSetChanged();
		}
		new Thread() {
			public void run() {
				try {
					sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(UPDATE_UI);
			}
		}.start();

		ListRelationInit();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linearlayout_out_of_back:
			this.finish();
			break;
		case R.id.screen_detail_header_right_refresh:

			break;
		case R.id.middle_t_offer:
			showPopupWindow(mPullDownView, mTitleHeadView, mGouGuView);
			break;

		case R.id.textview_month:
			myDialog = new MyListDialog(TOfferActivity.this).builder();
			MyDialogAdapter adapter_date = new MyDialogAdapter(
					TOfferActivity.this, mDateList);
			myDialog.setContent(adapter_date);
			myDialog.setDialogCallback(dialogcallback2);
			myDialog.setCancelable(true);
			myDialog.setCanceledOnTouchOutside(true);
			myDialog.show();
			break;
		case R.id.txt_mydialog_cancel:
			mWindow.dismiss();
			break;
		case R.id.buykai:
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
			intent.setClass(TOfferActivity.this, MainTabActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buyping:

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
			intent2.setClass(TOfferActivity.this, MainTabActivity.class);
			startActivity(intent2);
			finish();
			break;
		case R.id.sellkai:
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
			intent1.setClass(TOfferActivity.this, MainTabActivity.class);
			startActivity(intent1);
			finish();
			break;
		case R.id.sellping:

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
			intent3.setClass(TOfferActivity.this, MainTabActivity.class);
			startActivity(intent3);
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 日期 对话框 回调函数
	 */
	Dialogcallback dialogcallback2 = new Dialogcallback() {
		@Override
		public void dialogdo(int pos) {

			String temp = mDateList.get(pos);
			if (temp.length() >= 2) {
				temp = temp.substring(temp.length() - 2);
			}
			mMonth.setText(String.format("%d月", STD.StringToInt(temp)));
			mCurrentDate = STD.StringToInt(mDateList.get(pos));
			loadListData();
			mListAdapter_Up.notifyDataSetChanged();
			mListAdapter_Down.notifyDataSetChanged();
			mListAdapter_XQJ.notifyDataSetChanged();
		}
	};

	/**
	 * 日期数据接口
	 * 
	 * @return
	 */

	private List<String> getDatelists(String stockCode, short stockMarket) {
		List<String> mStrDates = new ArrayList<String>(); // 这里必须要初始化日期数据 包括 网络
															// //
															// 默认的数据;//要多一条默认数据日期的集合
		List<String> mNetStrDates = null;// 网络请求过来的日期集合
		mNetStrDates = hService.getAllDateArray(stockCode, stockMarket);
		// mStrDates.add("全部日期");
		for (int i = 0; i < mNetStrDates.size(); i++) {
			mStrDates.add(mNetStrDates.get(i));
		}
		return mStrDates;
	}

	private void showPopupWindow(View mLayout, View mTileHead, View mGouGuView) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view_window = inflater.inflate(
				R.layout.toffer_biaodi_name_mydialog, null);
		mWindow = new PopupWindow(view_window);
		// 设置 window 的layout 以及其宽高
		// int width = mLayout.getWidth();// 条目的宽
		int width = mLayout.getWidth();
		int height = mLayout.getHeight() * 5;// 条目的高
		mWindow.setWidth(width);
		mWindow.setHeight(height);
		mWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.pop_bckg));
		mWindow.setFocusable(true);// 设置焦距 让点其他条目或后退键时条目消失
		// window.update();
		// window.setOutsideTouchable(true);
		// 设置window弹出来的位置
		int[] location = new int[2];
		// 获取一个空间在手机屏幕上的位置
		mLayout.getLocationOnScreen(location);
		int x = location[0];

		mGouGuView.getLocationOnScreen(location);
		int y = location[1];

		// 显示window_item
		// mWindow.showAtLocation(mLayout, Gravity.LEFT | Gravity.TOP, x - 20, y
		// + 109);

		// mWindow.showAtLocation(mTileHead, Gravity.LEFT | Gravity.TOP, x,
		// y+mTileHead.getHeight()-10);
		mWindow.showAtLocation(mGouGuView, Gravity.LEFT | Gravity.TOP, x, y);
		mCancel = (TextView) view_window.findViewById(R.id.txt_mydialog_cancel);
		mCancel.setOnClickListener(this);

		mPopLv = (ListView) view_window.findViewById(R.id.device_list);
		adapter = new TOfferMyDialogAdapter(mMyApp, mStockList);
		mPopLv.setAdapter(adapter);
		adapter.setSeclection(mCurrentBiaoDiIndex);
		mPopLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentBiaoDiIndex = position;
				mMiddleText.setText(mStockList.get(position).name);
				mWindow.dismiss();
				adapter.setSeclection(position);
				mCurrentStockCode = mStockList.get(mCurrentBiaoDiIndex).code;
				mCurrentStockMarket = mStockList.get(mCurrentBiaoDiIndex).market;
				mMyApp.mStockConfigData.getData(mCurrentStockData,
						mCurrentStockMarket, mCurrentStockCode, false);
				// 现价幅度等
				String nowPrice = ViewTools.getStringByFieldID(
						mCurrentStockData, Global_Define.FIELD_HQ_NOW);
				String fd = ViewTools.getStringByFieldID(mCurrentStockData,
						Global_Define.FIELD_HQ_ZDF_SIGN);

				mMiddleText_BiaoDi.setText(String.format("%s %s", nowPrice, fd));
				mMiddleText_BiaoDi.setTextColor(ViewTools.getColorByFieldID(
						mCurrentStockData, Global_Define.FIELD_HQ_NOW));
				mDateList = getDatelists(mCurrentStockCode, mCurrentStockMarket);
				if (!mDateList.isEmpty()) {
					mCurrentDate = STD.StringToInt(mDateList.get(0));
					String temp = STD.IntToString(mCurrentDate);
					if (temp.length() >= 2) {
						temp = temp.substring(temp.length() - 2);
					}
					mMonth.setText(String.format("%d月", STD.StringToInt(temp)));
				}
				loadListData();
				mListAdapter_Up.notifyDataSetChanged();
				mListAdapter_Down.notifyDataSetChanged();
				mListAdapter_XQJ.notifyDataSetChanged();
			}
		});

	}

	/**
	 * 期权标的 数据接口 获得服务端过滤的期权标的列表
	 * 
	 * @return
	 */
	public List<TagCodeInfo> getTargetTagCodeInfo() {
		List<TagCodeInfo> mTagCodeInfo = new ArrayList<TagCodeInfo>();
		List<TagCodeInfo> mNetTagCodeInfo = null;
		mNetTagCodeInfo = hService.getTagCodeInfoRemoveNull();

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

	// 获取期权行情信息
	private void queryHQPushInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();

		codelist.addAll(mUpOptionList);
		codelist.addAll(mDownOptionList);

		ArrayList<TagCodeInfo> stocklist = mMyApp.mHQData
				.getStockListByOptionList(codelist);
		codelist.addAll(stocklist);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final int data[] = new int[2];
		data[0] = this.mCurrentBiaoDiIndex;
		data[1] = this.mCurrentDate;
		return data;
	}

	private void showPopupWindow(View mLayout, View mPop,
			final boolean mboolean, final int mPosition) {
		int trade_mode = PreferenceEngine.getInstance().getTradeMode();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view_window = inflater.inflate(R.layout.pop_t_offer_order, null);

		mMenuWindow = new PopupWindow(this);
		mMenuWindow.setContentView(view_window);
		mMenuWindow.setWidth(LayoutParams.WRAP_CONTENT);
		mMenuWindow.setHeight(LayoutParams.WRAP_CONTENT);

		if (mboolean == true) {
			mMenuWindow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.zx));
		} else {
			mMenuWindow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.zx1));
		}

		// mMenuWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionsheet_single_pressed));
		mMenuWindow.setFocusable(true);// 设置焦距 让点其他条目或后退键时条目消失

		// 设置window弹出来的位置
		int[] location = new int[2];
		// 获取一个空间在手机屏幕上的位置
		mLayout.getLocationOnScreen(location);
		int x = location[0];

		mLayout.getLocationOnScreen(location);
		int y = location[1];

		// mMenuWindow.showAtLocation(mLayout, Gravity.LEFT | Gravity.TOP, x, y
		// - mLayout.getHeight());

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;

		int screenHeigh = dm.heightPixels;

		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		// int width = display.getWidth();
		//
		// int height = display.getHeight();

		if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
			if (trade_mode == 0) {// 普通模式下
				mMenuWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY,
						screenWidth * 3 / 10, y - mLayout.getHeight());
			} else {

				mMenuWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY,
						screenWidth * 1 / 8, y - mLayout.getHeight());
			}
		} else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {// heng
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
		mJiaZiXuan = (Button) view_window.findViewById(R.id.jiazixuan);

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
		TagCodeInfo mOptionCodeInfo = null;
		HeadListDataService mHService = new HeadListDataService(
				TOfferActivity.this);
		TagLocalStockData mOptionData;
		if (mboolean == true) {
			mOptionCodeInfo = mUpOptionList.get(mPosition);
		} else {
			mOptionCodeInfo = mDownOptionList.get(mPosition);
		}
		mOptionData = mHService.getTagLocalStockData(mOptionCodeInfo.market,
				mOptionCodeInfo.code);

		if (mMyApp.IsStockExist(mOptionData.code, mOptionData.market, AppConstants.HQTYPE_QQ)) {
			mIsMyStock = true;
			mJiaZiXuan.setText("删除自选");
		} else {
			mIsMyStock = false;
			mJiaZiXuan.setText("加自选");
		}

		mJiaZiXuan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TagCodeInfo mOptionCodeInfo = null;
				HeadListDataService mHService = new HeadListDataService(
						TOfferActivity.this);
				TagLocalStockData mOptionData;
				if (mboolean == true) {
					mOptionCodeInfo = mUpOptionList.get(mPosition);
				} else {
					mOptionCodeInfo = mDownOptionList.get(mPosition);
				}
				mOptionData = mHService.getTagLocalStockData(
						mOptionCodeInfo.market, mOptionCodeInfo.code);
				if (mIsMyStock) {// delete from my stock

					int position = -1;
					int size = mMyApp.getMyStockList(AppConstants.HQTYPE_QQ).size();
					for (int i = 0; i < size; i++) {
						if (mOptionData.code.equals(mMyApp.getMyStockList(AppConstants.HQTYPE_QQ)
								.get(i).code)
								&& mOptionData.market == mMyApp
										.getMyStockList(AppConstants.HQTYPE_QQ).get(i).market) {
							position = i;
							break;
						}
					}
					int ret = mMyApp.RemoveFromMyStock(position, AppConstants.HQTYPE_QQ);
					if (ret == 0) {
						mIsMyStock = false;
						mJiaZiXuan.setText("加自选");
						Toast.makeText(TOfferActivity.this, "该自选股已删除！",
								Toast.LENGTH_LONG).show();
					}

				} else {// add to my stock
					int ret = mMyApp.AddtoMyStock(mOptionData, AppConstants.HQTYPE_QQ);
					if (ret == 0) {
						mIsMyStock = true;
						mJiaZiXuan.setText("删除自选");

						Toast.makeText(TOfferActivity.this, "已添加到自选股！",
								Toast.LENGTH_LONG).show();
					} else if (ret == -1) {
						Toast.makeText(TOfferActivity.this, "自选股已存在！",
								Toast.LENGTH_LONG).show();
					} else if (ret == -2) {
						Toast.makeText(TOfferActivity.this, "自选股超过最大限制！",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});

	}

	public void gotoMainPage(int page) {
		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager()
				.getMainTabActivity();

		if (act != null) {
			act.setChangePage(page);
		}
		intent.setClass(TOfferActivity.this, MainTabActivity.class);
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
