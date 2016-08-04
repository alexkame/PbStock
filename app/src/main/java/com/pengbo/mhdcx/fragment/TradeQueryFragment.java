package com.pengbo.mhdcx.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pengbo.mhdcx.adapter.TradeQueryAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeLocalRecord;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdcx.ui.trade_activity.OnTradeFragmentListener;
import com.pengbo.mhdzq.R;


/**
 * 交易中查询界面 
 * @author pobo
 *
 */
public class TradeQueryFragment extends Fragment implements
		OnCheckedChangeListener {

	private MyApp mMyApp;
	private View mView;
	private RadioGroup mRadioGroup;
	private int mAllCancelDealIndex = 1; // 0全部,1可撤,2已成交
	private TextView mTVState, mTVDeal;
	private ListView mListView;

	private PBSTEP mListData1;//data for adapter
	private PBSTEP mListDRWT;

	private TradeQueryAdapter mAdapter;

	private TradeLocalRecord mCheDanRecord = null; // 撤单
	private boolean mIsViewReady = false;
	private OnTradeFragmentListener mListener;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case TradeNetConnect.MSG_ADAPTER_CD_BUTTON_CLICK: {
					proc_MSG_ADAPTER_CD_BUTTON_CLICK(msg);
				}
					break;
			}
			super.handleMessage(msg);
		};
	};

	public TradeQueryFragment(MyApp myApp) {
		mMyApp = myApp;
		if (mCheDanRecord == null) {
			mCheDanRecord = new TradeLocalRecord();
		}
	}

	public void updateWTData() {
		if (!mIsViewReady)
			return;

		switch (mAllCancelDealIndex) {
		case 0:

			mTVState.setVisibility(View.VISIBLE);
			mTVDeal.setVisibility(View.GONE);
			if (this.mListData1 == null) {
				mListData1 = new PBSTEP();
			}
			mMyApp.mTradeData.GetDRWT(mListData1);
			break;

		case 1:
			mTVState.setVisibility(View.VISIBLE);
			mTVDeal.setVisibility(View.GONE);
			if (this.mListData1 == null) {
				mListData1 = new PBSTEP();
			}
			mMyApp.mTradeData.GetDRWT_CD(mListData1);
			break;
		case 2:
			mTVState.setVisibility(View.GONE);
			mTVDeal.setVisibility(View.VISIBLE);
			if (this.mListData1 == null) {
				mListData1 = new PBSTEP();
			}
			if (this.mListDRWT == null) {
				mListDRWT = new PBSTEP();
			}
			mMyApp.mTradeData.GetDRCJ(mListData1);
			mMyApp.mTradeData.GetDRWT(mListDRWT);
			mAdapter.setmLSDRWT(mListDRWT);

			break;

		default:
			break;
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mListener = (OnTradeFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.trade_detail_query_fragment, null);
		initViews();
		initListDatas();
		mIsViewReady = true;
		return mView;
	}

	private void initViews() {
		// 包含头部的 Radiogroup
		mRadioGroup = (RadioGroup) mView.findViewById(R.id.tradequery_radiogroup);
		mRadioGroup.setOnCheckedChangeListener(this);

		mTVState = (TextView) mView.findViewById(R.id.trade_detail_head_title_state);// list上面的 状态
		mTVDeal = (TextView) mView.findViewById(R.id.trade_detail_head_title_alreadydeal);// list上面的 成交价

		if (mListView == null) {
			mListView = (ListView) mView.findViewById(R.id.trade_detail_query_lv);
			if (mListData1 == null) {
				mListData1 = new PBSTEP();
			}
			mAdapter = new TradeQueryAdapter(getActivity(), mListData1, mHandler);
			mListView.setAdapter(mAdapter);
		}

	}

	private void initListDatas() {
		if (this.mListData1 == null) {
			mListData1 = new PBSTEP();

		}
		if (this.mListDRWT == null) {
			mListDRWT = new PBSTEP();
		}
		mMyApp.mTradeData.GetDRWT_CD(mListData1); //默认显示可撤页面
	}
	
	@Override
	public void onResume() {
		if (!this.isHidden()) {
			mMyApp.setHQPushNetHandler(null);
			updateWTData();
		}
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if(!hidden)
		{
			mMyApp.setHQPushNetHandler(null);
			updateWTData();
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {

			case R.id.tradequery_rb_all:
				mAdapter.setTag(true);
				changeAllCancelDealView(0);
				break;

			case R.id.tradequery_rb_cancel:
				mAdapter.setTag(true);
				changeAllCancelDealView(1);

				break;
			case R.id.tradequery_rb_deal:
				if (mListener != null)
				{
					mListener.requestDRWT();
					mListener.requestDRCJ();
				}
				mAdapter.setTag(false);
				changeAllCancelDealView(2);
				break;

			default:
				break;
		}

	}

	private void changeAllCancelDealView(int index) {
		if (mAllCancelDealIndex != index) {
			mAllCancelDealIndex = index;

			switch (mAllCancelDealIndex) {
				case 0:

					mTVState.setVisibility(View.VISIBLE);
					mTVDeal.setVisibility(View.GONE);
					if (this.mListData1 == null) {
						mListData1 = new PBSTEP();
					}
					mMyApp.mTradeData.GetDRWT(mListData1);
					break;

				case 1:
					mTVState.setVisibility(View.VISIBLE);
					mTVDeal.setVisibility(View.GONE);
					if (this.mListData1 == null) {
						mListData1 = new PBSTEP();
					}
					mMyApp.mTradeData.GetDRWT_CD(mListData1);
					break;
				case 2:
					mTVState.setVisibility(View.GONE);
					mTVDeal.setVisibility(View.VISIBLE);
					if (this.mListData1 == null) {
						mListData1 = new PBSTEP();
					}
					if (this.mListDRWT == null) {
						mListDRWT = new PBSTEP();
					}
					mMyApp.mTradeData.GetDRCJ(mListData1);
					mMyApp.mTradeData.GetDRWT(mListDRWT);
					mAdapter.setmLSDRWT(mListDRWT);

					break;

				default:
					break;
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	private void proc_MSG_ADAPTER_CD_BUTTON_CLICK(Message msg) {
		// 撤单
		int pos = msg.arg1;
		if (mListData1 != null) {
			int num = mListData1.GetRecNum();
			if (pos < num) {
				mListData1.GotoRecNo(pos);
				mCheDanRecord.mWTBH = mListData1.GetFieldValueString(STEP_Define.STEP_WTBH);
				mCheDanRecord.mWTSHJ = mListData1.GetFieldValueString(STEP_Define.STEP_WTRQ);
				mCheDanRecord.mGDZH = mListData1.GetFieldValueString(STEP_Define.STEP_GDH);
				mCheDanRecord.mMarketCode = mListData1.GetFieldValueString(STEP_Define.STEP_SCDM);
				mCheDanRecord.mXWH = mListData1.GetFieldValueString(STEP_Define.STEP_XWH);
				mCheDanRecord.mXDXW = mListData1.GetFieldValueString(STEP_Define.STEP_XDXW);
				mCheDanRecord.mWTZT = mListData1.GetFieldValueString(STEP_Define.STEP_WTZT);
				mCheDanRecord.mWTZTMC = mListData1.GetFieldValueString(STEP_Define.STEP_WTZTMC);
				mCheDanRecord.mBiaodiCode = mListData1.GetFieldValueString(STEP_Define.STEP_BDDM);
				mCheDanRecord.mBiaodiMC = mListData1.GetFieldValueString(STEP_Define.STEP_BDMC);
				mCheDanRecord.mWTPrice = mListData1.GetFieldValueString(STEP_Define.STEP_WTJG);
				mCheDanRecord.mWTSL = mListData1.GetFieldValueString(STEP_Define.STEP_WTSL);

				MyApp.getInstance().mTradeDetailActivity.requestWTCDFromCheDanView(mCheDanRecord);
			}
		}
	}

}
