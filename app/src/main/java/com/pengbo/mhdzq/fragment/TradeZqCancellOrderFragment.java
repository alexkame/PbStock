package com.pengbo.mhdzq.fragment;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqCCAdapter;
import com.pengbo.mhdzq.adapter.ZqCDAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeLocalRecord;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.zq_trade_activity.OnTradeFragmentListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class TradeZqCancellOrderFragment extends Fragment {

	private MyApp mMyApp = null;
	private Activity mActivity = null;
	private boolean mIsViewReady = false;

	public View mView;

	private PBSTEP mListData_kc;// 持仓
	private PBSTEP mListData_cj;// 成交
	
	private OnTradeFragmentListener mListener;

	public ZqCDAdapter mKeCheAdapterKC;
	private TradeLocalRecord mCheDanRecord;
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
				 proc_MSG_ADAPTER_CD_BUTTON_CLICK(msg);
			}
				break;
			}

			super.handleMessage(msg);
		}

	};
	
	public static TradeZqCancellOrderFragment newInstance()
	{
		TradeZqCancellOrderFragment f = new TradeZqCancellOrderFragment();
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
		mView = inflater.inflate(R.layout.zq_trade_cancell_frame, null);

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
		
		if (mCheDanRecord == null) {
			mCheDanRecord = new TradeLocalRecord();
		}
	}

	private void initCCData() {
		mListData_kc = new PBSTEP();
		mListData_cj = new PBSTEP();
	}

	public void initView() {
		mListView = (ListView) mView
				.findViewById(R.id.trade_order_listview_cancell);

		mKeCheAdapterKC = new ZqCDAdapter(mListData_kc, getActivity(), mHandler,mMyApp);
		mListView.setAdapter(mKeCheAdapterKC);
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

		mMyApp.mTradeData.GetDRWT_CD(mListData_kc);
		mMyApp.mTradeData.GetDRCJ(mListData_cj);
		
		mKeCheAdapterKC.setCJData(mListData_cj);

		// mMyApp.mTradeData.GetDRWT(mListData_WEITUO);

		mKeCheAdapterKC.notifyDataSetChanged();

	}
	
	private void proc_MSG_ADAPTER_CD_BUTTON_CLICK(Message msg) {
		int pos = msg.arg1;
		TradeLocalRecord cheDanRecord = null;
		if (pos >= 0 && pos < mListData_kc.GetRecNum()) {
			mListData_kc.GotoRecNo(pos);

			cheDanRecord = new TradeLocalRecord();
			cheDanRecord.mWTBH = mListData_kc.GetFieldValueString(STEP_Define.STEP_WTBH);
			cheDanRecord.mWTSHJ = mListData_kc.GetFieldValueString(STEP_Define.STEP_WTRQ);
			cheDanRecord.mGDZH = mListData_kc.GetFieldValueString(STEP_Define.STEP_GDH);
			cheDanRecord.mMarketCode = mListData_kc.GetFieldValueString(STEP_Define.STEP_SCDM);
			cheDanRecord.mXWH = mListData_kc.GetFieldValueString(STEP_Define.STEP_XWH);
			cheDanRecord.mXDXW = mListData_kc.GetFieldValueString(STEP_Define.STEP_XDXW);
			cheDanRecord.mWTZT = mListData_kc.GetFieldValueString(STEP_Define.STEP_WTZT);
			cheDanRecord.mWTZTMC = mListData_kc.GetFieldValueString(STEP_Define.STEP_WTZTMC);
			cheDanRecord.mBiaodiCode = mListData_kc.GetFieldValueString(STEP_Define.STEP_BDDM);
			cheDanRecord.mBiaodiMC = mListData_kc.GetFieldValueString(STEP_Define.STEP_BDMC);
			cheDanRecord.mWTPrice = mListData_kc.GetFieldValueString(STEP_Define.STEP_WTJG);
			cheDanRecord.mWTSL = mListData_kc.GetFieldValueString(STEP_Define.STEP_WTSL);

		}

		if (cheDanRecord == null || !DataTools.isCDStatusEnabled(cheDanRecord.mWTZT)) {
			Toast.makeText(getActivity(), "此委托无法撤单", Toast.LENGTH_SHORT).show();
		}

		mCheDanRecord.copy(cheDanRecord);
		String strMsg = String.format("委托编号：%s\r\n委托日期：%s\r\n委托状态：%s\r\n委托价格：%s\r\n委托数量：%s\r\n\r\n您确认撤单吗？", cheDanRecord.mWTBH, cheDanRecord.mWTSHJ, cheDanRecord.mWTZTMC,
				cheDanRecord.mWTPrice, cheDanRecord.mWTSL);
		
		if(PreferenceEngine.getInstance().getOrderWithoutConfirm()){//撤单无需确认 
			if (mListener != null) {
				mListener.requestWTCD(mCheDanRecord);
			}
		}else{
			new com.pengbo.mhdzq.widget.AlertDialog(getActivity()).builder().setTitle("撤单").setMsg(strMsg).setCancelable(false).setCanceledOnTouchOutside(false)
			.setPositiveButton("确认", new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null){
						mListener.requestWTCD(mCheDanRecord);
					}
				}
			}).setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			}).show();
		}

	
	}
}
