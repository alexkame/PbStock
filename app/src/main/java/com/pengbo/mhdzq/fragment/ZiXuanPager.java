package com.pengbo.mhdzq.fragment;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.HangQingAdapter;
import com.pengbo.mhdzq.base.BasePager;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.TopRankData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.main_activity.ZiXuanFastSearchActivity;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.zq_activity.MyStockEditActivity;
import com.pengbo.mhdzq.zq_activity.ZQMarketDetailActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * 自选页
 * 
 * @author pobo
 * 
 */
public class ZiXuanPager extends BasePager implements OnClickListener {
	private static final String TAG = "ZiXuanPager";
	/** 每次推送刷新item的个数 **/
	private static final int REFRESH_ITEM_NUM = 15;

	private View view;
	private HangQingAdapter mListAdapter;
	/*** list里条目总数 **/
	private int mTotalListItemNum = 0;
	/** ListView当前显示的第一条item索引 **/
	private int mStartIndex = 0;
	/** listview 当前屏幕显示的最后一条item索引 **/
	private int mEndIndex = 20;
	/** 添加自选 **/
	private Button btn_Add;

	// //////////////////////////

	private ListView mListView;//

	public ArrayList<CCodeTableItem> mDatas;
	public ArrayList<TagCodeInfo> mMyStockCodeInfos;// my stock list
	/** 请求标记 0:query push **/
	public int mRequestCode[];

	/** 消息处理 */
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			CMessageObject aMsgOject = null;
			if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
				aMsgOject = (CMessageObject) msg.obj;
			}

			switch (msg.what) {

			case GlobalNetConnect.MSG_UPDATE_DATA: {
				if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
					// 更新数据（最新数据已经保存在mMyApp.mHQData里）
					closeProgress();
					refreshMyStockList();
				}
			}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};

	public ZiXuanPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initDetailView() {
		mRequestCode = new int[1];

		mMyStockCodeInfos = mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ);
		mTotalListItemNum = mMyStockCodeInfos.size();

		initPagerView();

		flContent.addView(view);
		bPagerReady = true;

		ZhengQuanFragment frag = (ZhengQuanFragment) ((FragmentActivity) mActivity)
				.getSupportFragmentManager().findFragmentByTag(
						ZhengQuanActivity.TAG);
		if (frag != null
				&& frag.mCurrentView == ZhengQuanFragment.ZQ_VIEW_ZIXUAN) {
			visibleOnScreen();
		}
	}

	/**
	 * 初始化控件
	 */
	private void initPagerView() {

		view = View.inflate(mActivity, R.layout.zq_page_optional, null);

		tvTitle.setText("证券-自选");
		tvTitle.setVisibility(View.VISIBLE);
		/********** 自选股 编辑 **************/
		imgLeftNote.setVisibility(View.VISIBLE);
		imgLeftNote.setOnClickListener(this);

		/********** 期权列表的搜索界面 *********************/
		imgRightSearch.setVisibility(View.VISIBLE);
		imgRightSearch.setOnClickListener(this);

		/*********** 期权列表按钮的监听 *************************/

		btn_Add = (Button) view.findViewById(R.id.btn_zq_myoption_add);
		btn_Add.setOnClickListener(this);

		mListView = (ListView) view.findViewById(R.id.lv_zq_mystock);
		mDatas = new ArrayList<CCodeTableItem>();// 初始化集合
		mListAdapter = new HangQingAdapter(mActivity, mDatas, true);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CCodeTableItem codeItem = mDatas.get(position);
				if (codeItem.market != HQ_Define.HQ_MARKET_SH_INT
						&& codeItem.market != HQ_Define.HQ_MARKET_SZ_INT) {
					return;
				}
				Intent mIntent = new Intent();
				mIntent.setClass(mActivity, ZQMarketDetailActivity.class);

				TagCodeInfo optionCodeInfo = new TagCodeInfo(codeItem.market,
						codeItem.code, codeItem.group, codeItem.name);
				ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;

				mActivity.startActivity(mIntent);
			}

		});

		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {// list停止滚动时加载图片
					// 加载数据
					refreshListData();
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

		if (mTotalListItemNum <= 0) {
			btn_Add.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			btn_Add.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private void updateMyStockList() {
		initMyStockList();
		if (mTotalListItemNum <= 0) {
			btn_Add.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			btn_Add.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private void refreshMyStockList() {
		mTotalListItemNum = mDatas.size();
		mListAdapter.notifyDataSetChanged();
	}

	private void initMyStockList() {
		mMyStockCodeInfos = mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ);
		mTotalListItemNum = mMyStockCodeInfos.size();

		mDatas.clear();

		TagCodeInfo taginfo = null;

		for (int i = 0; i < mTotalListItemNum; i++) {
			taginfo = mMyStockCodeInfos.get(i);

			CCodeTableItem item = new CCodeTableItem();
			item.market = taginfo.market;
			item.code = taginfo.code;
			item.name = taginfo.name;
			item.group = taginfo.group;
			mDatas.add(item);
		}

		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_public_black_head_title_left_note: {

			Intent intent = new Intent(mActivity, MyStockEditActivity.class);
			mActivity.startActivity(intent);
		}
			break;

		case R.id.img_public_black_head_title_right_blue_search: {
			Intent intent = new Intent(mActivity,
					ZiXuanFastSearchActivity.class);
			mActivity.startActivity(intent);
		}
			break;

		case R.id.btn_zq_myoption_add: {
			Intent intent = new Intent(mActivity,
					ZiXuanFastSearchActivity.class);
			mActivity.startActivity(intent);
		}
			break;

		default:
			break;
		}
	}

	private void refreshListData() {
		queryHQPushInfo();
	}

	// 获取期权行情信息
	private void queryHQPushInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		if (mEndIndex < (mStartIndex + REFRESH_ITEM_NUM)) {
			mEndIndex = mStartIndex + REFRESH_ITEM_NUM;
		}
		for (int i = mStartIndex; i < mDatas.size() && i < mEndIndex; i++) {
			codelist.add(mMyStockCodeInfos.get(i));
		}

		mMyApp.setHQPushNetHandler(mHandler);
		if (codelist.size() > 0) {
			showProgress("");
		}
		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	@Override
	public void visibleOnScreen() {
		if (bPagerReady) {
			updateMyStockList();
			queryHQPushInfo();
		}

	}

	@Override
	public void invisibleOnScreen() {
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null,
				0, 0);
	}

}
