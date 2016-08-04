package com.pengbo.mhdzq.fragment;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqCDAdapter;
import com.pengbo.mhdzq.adapter.ZqWTAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.zq_trade_activity.OnTradeFragmentListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TradeZqEntrustFragment extends Fragment {
	private MyApp mMyApp = null;
	private Activity mActivity = null;
	private boolean mIsViewReady = false;

	public View mView;

	private PBSTEP mListData_wt;// 持仓
	private PBSTEP mListData_cj;// 成交
	private OnTradeFragmentListener mListener;

	public ZqWTAdapter mWTAdapter;
	private ListView mListView;

	protected Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// process incoming messages here
			switch (msg.what) {
			case TradeNetConnect.MSG_ADAPTER_KJFS_BUTTON_CLICK: {
				// proc_MSG_ADAPTER_KJFS_BUTTON_CLICK(msg);
			}
				break;
			case TradeNetConnect.MSG_ADAPTER_CC_SELECT_CLICK: {
				// proc_MSG_ADAPTER_CC_SELECT_CLICK(msg);
			}
				break;
			case TradeNetConnect.MSG_ADAPTER_CD_BUTTON_CLICK: {
				// proc_MSG_ADAPTER_CD_BUTTON_CLICK(msg);
			}
				break;
			}

			super.handleMessage(msg);
		}

	};

	public static TradeZqEntrustFragment newInstance()
	{
		TradeZqEntrustFragment f = new TradeZqEntrustFragment();
	    return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.zq_trade_entrust_frame, null);

		initView();

		mIsViewReady = true;

		return mView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		mMyApp = (MyApp) this.mActivity.getApplication();

		try {
			mListener = (OnTradeFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onResume() {
		if (!this.isHidden()) {
			requestDRWT();
		}
		super.onResume();
	}

	public void initData() {
		initCCData();
	}

	private void initCCData() {
		mListData_wt = new PBSTEP();
		mListData_cj = new PBSTEP();
	}

	public void initView() {
		mListView = (ListView) mView
				.findViewById(R.id.trade_order_listview_entrust);

		mWTAdapter = new ZqWTAdapter(mListData_wt, getActivity(), mHandler,
				mMyApp,false);
		mListView.setAdapter(mWTAdapter);
	}

	private void requestDRWT() {

		if (mListener != null) {
			mListener.requestDRWT();
		}
		// setInitPriceAndVolume();// 下单后重置委托价格和数量

	}

	public void updateTradeData(boolean isPushRefresh) {
		if (!mIsViewReady)
			return;

		mMyApp.mTradeData.GetDRWT(mListData_wt);
		mMyApp.mTradeData.GetDRCJ(mListData_cj);
		
		mWTAdapter.setCJData(mListData_cj);

		// mMyApp.mTradeData.GetDRWT(mListData_WEITUO);

		mWTAdapter.notifyDataSetChanged();

	}
}
