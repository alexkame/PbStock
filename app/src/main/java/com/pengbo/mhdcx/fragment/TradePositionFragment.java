package com.pengbo.mhdcx.fragment;

import java.util.Date;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeZJRecord;
import com.pengbo.mhdcx.ui.trade_activity.OnTradeFragmentListener;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.view.DownPullToRefreshView;
import com.pengbo.mhdcx.view.DownPullToRefreshView.OnFooterDownRefreshListener;
import com.pengbo.mhdcx.view.DownPullToRefreshView.OnHeaderDownRefreshListener;
import com.pengbo.mhdcx.view.TradePositionChiCangView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 持仓页
 * 
 * @author pobo
 * 
 */
public class TradePositionFragment extends Fragment {

	public final static int ZJ_PROPERTY_NUM = 6;//只支持显示6个资金属性
	
	private MyApp mMyApp;

	private OnTradeFragmentListener mListener;
	private Activity mActivity;
	private PBSTEP mHoldList;
	private PBSTEP mMoney;
	private View mView;
	private TradePositionChiCangView mChiCangView;
	private TextView[] mTV_ZJValues;
	private TextView[] mTV_ZJTitles;
	//private TextView mTV_kqzj, mTV_fudong_yk, mTV_chicang_shizhi, mTV_fxd, mTV_kyzj, mTV_zqy;
	private boolean mIsViewReady = false;
	private LinearLayout mlLayout_Position;
	private DownPullToRefreshView mPullToRefreshView;
	
	public TradePositionFragment(MyApp myApp)
	{
		mMyApp = myApp;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.mActivity = activity;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.trade_position_fragment, container,
				false);
		
		initData();
		initView();
		mIsViewReady = true;
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		if (!this.isHidden()) {
			updateAllData();
			updateAllView();
			requestHQPushData();
		}
		super.onResume();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if(!hidden)
		{
			updateAllData();
			updateAllView();
			requestHQPushData();
		}
		super.onHiddenChanged(hidden);
	}
	
	private void initData() {
		if(mHoldList == null)
		{
			mHoldList = new PBSTEP();
		}
		if(mMoney == null)
		{
			mMoney = new PBSTEP();
		}
	}
	
	private void initView() {
		TextView[] textViews = {
				(TextView) mView.findViewById(R.id.tv_zj_name0), 
				(TextView) mView.findViewById(R.id.tv_zj_name1),
				(TextView) mView.findViewById(R.id.tv_zj_name2),
				(TextView) mView.findViewById(R.id.tv_zj_name3),
				(TextView) mView.findViewById(R.id.tv_zj_name4),
				(TextView) mView.findViewById(R.id.tv_zj_name5),
		};
		mTV_ZJTitles = textViews;
		textViews = null;
		
		TextView[] textViews1 = {
				(TextView) mView.findViewById(R.id.tv_zj_0), 
				(TextView) mView.findViewById(R.id.tv_zj_1),
				(TextView) mView.findViewById(R.id.tv_zj_2),
				(TextView) mView.findViewById(R.id.tv_zj_3),
				(TextView) mView.findViewById(R.id.tv_zj_4),
				(TextView) mView.findViewById(R.id.tv_zj_5),
		};
		mTV_ZJValues = textViews1;
		textViews1 = null;
		
		for(int i = 0; i < mMyApp.mTradeData.m_ZJDataList.size() && i < mTV_ZJTitles.length; i++)
		{
			String temp = mMyApp.mTradeData.m_ZJDataList.get(i).mTitle;
			mTV_ZJTitles[i].setText(temp);
		}
		
		if(mChiCangView == null)
		{
			mChiCangView = (TradePositionChiCangView) mView.findViewById(R.id.trade_chicang_panel).findViewById(R.id.layout_positionchicang);
		}
		
		mlLayout_Position = (LinearLayout) mView.findViewById(R.id.llayout_position);
		mlLayout_Position.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				return true;
			}
		});
		
		mPullToRefreshView = (DownPullToRefreshView) mView.findViewById(R.id.position_refreshview);
		mPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderDownRefreshListener(){

			@Override
			public void onHeaderRefresh(DownPullToRefreshView view) {
				if(mListener != null)
				{
					mListener.requestZJ();
					mListener.requestHoldStock();
				}
				mPullToRefreshView.postDelayed(new Runnable() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						//设置更新时间
						mPullToRefreshView.onHeaderRefreshComplete("更新时间:"+new Date().toLocaleString());
						mPullToRefreshView.onHeaderRefreshComplete();
					}
				},20);
				
			}
			
		});
		mPullToRefreshView.setOnFooterRefreshListener(new OnFooterDownRefreshListener(){

			@Override
			public void onFooterRefresh(DownPullToRefreshView view) {
				mPullToRefreshView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mPullToRefreshView.onFooterRefreshComplete();
					}
				}, 20);
				
			}
			
		});
	}
	
	
	public void updateAllData() {
		updateZJData();
		updateChiCangData();
	}
	
	public void updateZJData() {
		mMyApp.mTradeData.GetMoney(mMoney);
	}
	

	public void updateChiCangData() {
		mMyApp.mTradeData.GetHoldStock(mHoldList);
    }
	
	public void updateAllView() {
		updateZJView();
		updateChiCangView();
	}
	
	public void updateChiCangView() {
		if (mChiCangView != null) {
			mChiCangView.updateData(mMyApp, mHoldList);
		}
	}
	
	public void updateZJView() {
		if (!mIsViewReady)
			return;
		
		int retNum = 0;
		if(mMoney == null)
		{
			mMoney = new PBSTEP();
			mMyApp.mTradeData.GetMoney(mMoney);
		}
		
		retNum = mMoney.GetRecNum();
		if (retNum > 0)
		{
			for(int i = 0; i < mMyApp.mTradeData.m_ZJDataList.size() && i < mTV_ZJValues.length; i++)
			{
				String temp = "";
				TradeZJRecord record = mMyApp.mTradeData.m_ZJDataList.get(i);
				if (record.mStepVaules[0] == STEP_Define.STEP_FDYK)
				{
					PBSTEP holdStock = new PBSTEP();
				    mMyApp.mTradeData.GetHoldStock(holdStock);
				    double CCSZ = 0;//持仓市值
				    double FDYK = 0;//浮动盈亏
				    for (int j = 0; j < holdStock.GetRecNum(); j++) {
				        holdStock.GotoRecNo(j);
	
				        String strMarket = holdStock.GetFieldValueString(STEP_Define.STEP_SCDM);
				        String strCode = holdStock.GetFieldValueString(STEP_Define.STEP_HYDM);
				        
				        int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
				        
				        TagLocalStockData stockData = new TagLocalStockData();
				        mMyApp.mHQData.getData(stockData, (short)nMarket, strCode, false);
	
				        float fPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData);
				        
				        float fCBJ = 0.0f;
				        fCBJ = STD.StringToValue(holdStock.GetFieldValueString(STEP_Define.STEP_MRJJ));
				        		
				        float fDQSL = 0f;
				        fDQSL = STD.StringToValue(holdStock.GetFieldValueString(STEP_Define.STEP_DQSL));
				        
				        float fMMBZ = 0f;
				        fMMBZ = STD.StringToValue(holdStock.GetFieldValueString(STEP_Define.STEP_MMLB));
				        
				        boolean bBuy = (fMMBZ == 0f ? true : false);
				        
				        if (bBuy) {
				            CCSZ += fPrice * stockData.optionData.StrikeUnit * fDQSL;
				            FDYK += (fPrice - fCBJ) * stockData.optionData.StrikeUnit * fDQSL;
				        }
				        else
				        {
				            CCSZ -= fPrice * stockData.optionData.StrikeUnit * fDQSL;
				            FDYK += (fCBJ - fPrice) * stockData.optionData.StrikeUnit * fDQSL;
				        }
				    }
				    
				    //浮动盈亏
				    mTV_ZJValues[i].setText(String.format("%.2f", FDYK));
				    mTV_ZJValues[i].setTextColor(ViewTools.getColor((float)FDYK));
				}else
				{
					temp = mMoney.GetFieldValueStringWithBackup(record.mStepVaules[0], record.mStepVaules[1]);
					if(temp == null || temp.isEmpty())
					{
						temp = Global_Define.STRING_VALUE_EMPTY;
					}
					mTV_ZJValues[i].setText(temp);
				}
			}
			//可用资金
//			String temp = mMoney.GetFieldValueString(STEP_Define.STEP_KYZJ);
//			mTV_kyzj.setText(temp);
//			
//			//可取资金
//			temp = mMoney.GetFieldValueString(STEP_Define.STEP_KQZJ);
//			mTV_kqzj.setText(temp);
//			
//			//风险度
//			temp = mMoney.GetFieldValueString(STEP_Define.STEP_FXD);
//			if (temp.isEmpty())
//			{
//				temp = mMoney.GetFieldValueString(STEP_Define.STEP_JYSFXD);
//			}
//			
//			if (temp.isEmpty())
//			{
//				temp = Global_Define.STRING_VALUE_EMPTY;
//			}
//			mTV_fxd.setText(temp);
//
//			//总资产
//			temp = mMoney.GetFieldValueString(STEP_Define.STEP_ZZC);
//			mTV_zqy.setText(temp);
//			
//			PBSTEP holdStock = new PBSTEP();
//		    mMyApp.mTradeData.GetHoldStock(holdStock);
//		    double CCSZ = 0;//持仓市值
//		    double FDYK = 0;//浮动盈亏
//		    for (int i = 0; i < holdStock.GetRecNum(); i++) {
//		        holdStock.GotoRecNo(i);
//
//		        String strMarket = holdStock.GetFieldValueString(STEP_Define.STEP_SCDM);
//		        String strCode = holdStock.GetFieldValueString(STEP_Define.STEP_HYDM);
//		        
//		        int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
//		        
//		        TagLocalStockData stockData = new TagLocalStockData();
//		        mMyApp.mHQData.getData(stockData, (short)nMarket, strCode, false);
//
//		        float fPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData);
//		        
//		        float fCBJ = 0.0f;
//		        fCBJ = STD.StringToValue(holdStock.GetFieldValueString(STEP_Define.STEP_MRJJ));
//		        		
//		        float fDQSL = 0f;
//		        fDQSL = STD.StringToValue(holdStock.GetFieldValueString(STEP_Define.STEP_DQSL));
//		        
//		        float fMMBZ = 0f;
//		        fMMBZ = STD.StringToValue(holdStock.GetFieldValueString(STEP_Define.STEP_MMLB));
//		        
//		        boolean bBuy = (fMMBZ == 0f ? true : false);
//		        
//		        if (bBuy) {
//		            CCSZ += fPrice * stockData.optionData.StrikeUnit * fDQSL;
//		            FDYK += (fPrice - fCBJ) * stockData.optionData.StrikeUnit * fDQSL;
//		        }
//		        else
//		        {
//		            CCSZ -= fPrice * stockData.optionData.StrikeUnit * fDQSL;
//		            FDYK += (fCBJ - fPrice) * stockData.optionData.StrikeUnit * fDQSL;
//		        }
//		    }
//		    
//		    //持仓市值
//		    String strCCSZ = mMoney.GetFieldValueStringWithBackup(STEP_Define.STEP_ZSZ, STEP_Define.STEP_ZQSZ);
//		    mTV_chicang_shizhi.setText(strCCSZ);
//		    //浮动盈亏
//		    mTV_fudong_yk.setText(String.format("%.2f", FDYK));
//		    mTV_fudong_yk.setTextColor(ViewTools.getColor((float)FDYK));
		}else
		{
			for(int i = 0; i < mMyApp.mTradeData.m_ZJDataList.size() && i < mTV_ZJValues.length; i++)
			{
				mTV_ZJValues[i].setText(Global_Define.STRING_VALUE_EMPTY);
			}
//			mTV_kyzj.setText("--");
//			mTV_fxd.setText("--");
//            mTV_fudong_yk.setText("--");
//            mTV_kqzj.setText("--");
//			mTV_chicang_shizhi.setText("--");
//			mTV_zqy.setText("--");
		}
	}
	
	private void requestHQPushData()
	{
		mListener.requestHQPushData(null);
	}
}
