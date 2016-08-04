package com.pengbo.mhdcx.fragment;

import java.util.ArrayList;
import java.util.List;
import com.pengbo.mhdcx.adapter.TradeSearchOptionListAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TradeMyStock extends Fragment implements OnItemClickListener{
	
	protected static final int UPDATE_UI = 0;
	
	private ListView mListView1;
	public static View mHead;
	public List<TagLocalStockData> datas;
	
	private TagLocalStockData option;
	public ArrayList<TagCodeInfo> mTagCodeInfos;
	private TradeSearchOptionListAdapter mAdapter;
	private MyApp mMyApp;

	public int 		mRequestCode[];//请求标记
	
	public Activity mActivity;
	
	
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			
			switch (msg.what) {
			case UPDATE_UI:
				if (mAdapter == null) {
					mAdapter = new TradeSearchOptionListAdapter(mMyApp,getActivity(), datas, mTagCodeInfos);
					mListView1.setAdapter(mAdapter);

				} else {
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalNetConnect.MSG_UPDATE_DATA:
			{
				if(nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA && mRequestCode[0] == nRequestCode)
				{
					L.e("MyApp", "MyStockActivity receive push data");
					//更新数据（最新数据已经保存在mMyApp.mHQData里）
				//	initData();
				//	mAdapter.notifyDataSetChanged();
				}
			}
				break;
			default:
				break;
			}
		};
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity=activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initView();
		mRequestCode = new int[2];
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.trade_mystock_fragment, null);
	}
	
	
	
	
	@Override
	public void onResume() {
		super.onResume();		
		mTagCodeInfos = mMyApp.getMyStockList(AppConstants.HQTYPE_QQ);
		L.i("i", mTagCodeInfos.size() + "=======mTagCodeInfos=========");
		L.i("i", mMyApp.getMyStockList(AppConstants.HQTYPE_QQ).size()
				+ "=====mMyApp.mMyStockList===========");
		this.initData();
		queryHQPushInfo();
	}
	
	
	
	private void initView() {
		mMyApp = (MyApp) getActivity().getApplication();
		
		datas = new ArrayList<TagLocalStockData>();// 初始化集合
	
		mListView1 = (ListView) getActivity(). findViewById(R.id.listView1);
		mListView1.setOnItemClickListener(this);
	}
	
	
	
	
	private void initData() {

		new Thread() {
			public void run() {
				datas.clear();
				TagCodeInfo taginfo = null;
				for (int i = 0; i < mTagCodeInfos.size(); i++) {
					taginfo = mTagCodeInfos.get(i);
					option = new TagLocalStockData();
					boolean ret = mMyApp.mHQData.getData(option, taginfo.market,
							taginfo.code, false);
					if (ret)
					{//get data success
					    datas.add(option);
					}else
					{//get data failed, this record is bad, need remove from mystock list
						mMyApp.RemoveFromMyStock(i, AppConstants.HQTYPE_QQ);
					}
				}

				mHandler.sendEmptyMessage(UPDATE_UI);

			};
		}.start();
	}

	
	//获取期权行情信息
		private void queryHQPushInfo() {
			//comment out these code, because not need push data now.
//			ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
//			codelist.addAll(mTagCodeInfos);
//			
//			ArrayList<TagCodeInfo> stocklist = mMyApp.mHQData.getStockListByOptionList(mTagCodeInfos);
//			codelist.addAll(stocklist);
//		    
//			mMyApp.setHQPushNetHandler(mHandler);
//			mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet,codelist,0,codelist.size());
		}	

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			mMyApp.setCurrentOption(mTagCodeInfos.get(position));
			mMyApp.mbDirectInOrderPage = true; // enable this flag for enter order page.
			
			Intent intent = new Intent();
			MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
			
			if (act != null) {
				act.setChangePage(MainTabActivity.PAGE_TRADE);
			}

			intent.setClass(getActivity(), MainTabActivity.class);
			startActivity(intent);
		}

}
