package com.pengbo.mhdcx.view;

import java.util.ArrayList;

import com.pengbo.mhdcx.adapter.TradePositionListViewAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdcx.bean.Option;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdcx.widget.ActionSheetDialog;
import com.pengbo.mhdcx.widget.AlertDialog;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Trade position chicang view
 * 
 * @author pobo
 * 
 */
public class TradePositionChiCangView extends LinearLayout implements
			OnItemClickListener {
	public static final String TAG = "TradePositionChiCangView";
	
	private MyApp mMyApp;
	protected Context mContext;
	private ListView mListView;
	private TradePositionListViewAdapter mListAdapter;
	private ArrayList<Option> mDatas;
	private PBSTEP mHoldList;

	public TradePositionChiCangView(Context context) {
		super(context);
		mContext = context;
		initData(context);
	}
	
	public TradePositionChiCangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
		initData(context);
	}
	
	@Override
	protected void onFinishInflate()
    {
		super.onFinishInflate();
		initView();
    }

	private void initData(Context context) {
		if (mHoldList == null)
		{
			mHoldList = new PBSTEP();
		}
	}
	
	private void initView() {
		L.i(TAG, "initView");
		setFocusable(true);
        setFocusableInTouchMode(true);
		
        if(mListView == null)
        {
        	mListView = (ListView) this.findViewById(R.id.trade_position_listview);
        	mListView.setOnItemClickListener(this);
        	mDatas = new ArrayList<Option>();
        	mListAdapter = new TradePositionListViewAdapter(mContext, mDatas);
        	mListView.setAdapter(mListAdapter);
        }
	}
	
	private void loadListDatas() {
		initListDatas();
		mListAdapter.notifyDataSetChanged();
	}
	
	private void initListDatas() {

		mDatas.clear();
		if (mHoldList == null) {
			mHoldList = new PBSTEP();
			mMyApp.mTradeData.GetHoldStock(mHoldList);
		}
		int num = mHoldList.GetRecNum();
		for (int i = 0; i < num; i++) {
			Option oo = new Option();

			mHoldList.GotoRecNo(i);
			String optionName = mHoldList
					.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			oo.setMname(optionName);
			
			float fBDBZ = STD.StringToValue(mHoldList
					.GetFieldValueString(STEP_Define.STEP_BDBZ));
			boolean bBD = (fBDBZ == 1.0f ? true : false);
			if (bBD) {
				oo.setImage_three(R.drawable.position_bei);
				
			} else {
				oo.setImage_three(R.drawable.position_bao);
			}
			oo.setBDBZ(bBD);

			float fMMBZ = STD.StringToValue(mHoldList
					.GetFieldValueString(STEP_Define.STEP_MMLB));
			boolean bBuy = (fMMBZ == 0f ? true : false);
			
			if (bBD)
			{
				oo.setImage_one(R.drawable.beidui);
			}else
			{
				if (bBuy) {
					oo.setImage_one(R.drawable.position_quanli);
				} else {
					oo.setImage_one(R.drawable.position_yiwu);
				}
			}
			oo.setMMBZ(bBuy);

			String currentNum = mHoldList
					.GetFieldValueString(STEP_Define.STEP_DQSL);
			oo.setMchicang(STD.IntToString((int)STD.StringToValue(currentNum)));

			String junPrice = mHoldList
					.GetFieldValueString(STEP_Define.STEP_MRJJ);
			oo.setAverateprice(junPrice);

			TagLocalStockData stockData = new TagLocalStockData();
			String code = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);
			String market = mHoldList
					.GetFieldValueString(STEP_Define.STEP_SCDM);
			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
			mMyApp.mHQData.getData(stockData, (short) nMarket, code, false);

			float fPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData);
	        
			String nowPrice = ViewTools.getStringByFieldID(stockData,Global_Define.FIELD_HQ_NOW);
			mHoldList.SetFieldValueString(STEP_Define.STEP_ZXJ, nowPrice);
			
			oo.setMnownum(nowPrice);

			float fCBJ = STD.StringToValue(mHoldList
					.GetFieldValueString(STEP_Define.STEP_MRJJ));
			float nDQSL = STD.StringToValue(currentNum);

			float FDYK = 0;// 浮动盈亏
			if (bBuy) {
				FDYK = (fPrice - fCBJ) * stockData.optionData.StrikeUnit
						* nDQSL;
			} else {
				FDYK = (fCBJ - fPrice) * stockData.optionData.StrikeUnit
						* nDQSL;
			}

			oo.setFudongyk(String.format("%.2f", FDYK));
			oo.setMtruegangganlv("");
			oo.setMgangganlv("");

			oo.setImage_two(R.drawable.position_qi);

			String daoqiri = ViewTools.getStringByFieldID(stockData,
					Global_Define.FIELD_HQ_EXPIRE_DATE);
			oo.setmDueTime(daoqiri);
			
			String keyongNum = mHoldList.GetFieldValueString(STEP_Define.STEP_KYSL);
			
			if (keyongNum.equalsIgnoreCase("-99999999"))
			{
				int nBDBZ = mHoldList.GetFieldValueInt(STEP_Define.STEP_BDBZ);
                int nDJSL = MyApp.getInstance().mTradeData.GetDJSL(code, market, bBuy,nBDBZ);
                int nCCSL = mHoldList.GetFieldValueInt(STEP_Define.STEP_DQSL);
                int nKYSL = nCCSL-nDJSL;
                if (nKYSL < 0) {
                    nKYSL = 0;
                }
                keyongNum = STD.IntToString(nKYSL);
			}
			oo.setMcangcha(STD.IntToString((int)STD.StringToValue(keyongNum)));

			String bzj = mHoldList.GetFieldValueString(STEP_Define.STEP_BZJ);
			int nDays = ViewTools
					.getDaysDruationFromToday(stockData.optionData.StrikeDate);

			oo.setDays(nDays);
			if (nDays > 0) {
				oo.setMoldtime(String.format("剩余%d天", nDays));
			} else if (nDays == 0) {
				if (bBuy) {
					// "期权到期,如需行权请使用PC版操作"
					oo.setMoldtime("剩余0天");
				} else {
					// 等待权利方行权
					oo.setMoldtime("等待权利方行权");
				}
			} else {
				oo.setMoldtime("");
			}

			if (!bBD) {
				oo.setBaoZJ(bzj);
			} else {
				oo.setBaoZJ("");
			}

			mDatas.add(oo);
		}
	}

	public void updateData(MyApp myApp, PBSTEP holdList) {
		this.mMyApp = myApp;
		this.mHoldList = holdList;
		loadListDatas();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (this.mHoldList != null) {
			int num = this.mHoldList.GetRecNum();
			if (position < num) {

				TagLocalStockData optionData = new TagLocalStockData();
				mHoldList.GotoRecNo(position);

				String code = mHoldList
						.GetFieldValueString(STEP_Define.STEP_HYDM);
				String market = mHoldList
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
				mMyApp.mHQData.getData(optionData, (short) nMarket, code, false);
				final String keyongNum = mHoldList.GetFieldValueString(STEP_Define.STEP_KYSL);
				
				TagCodeInfo optionCodeInfo = new TagCodeInfo();
				optionCodeInfo.code = optionData.code;
				optionCodeInfo.market = optionData.market;
				optionCodeInfo.group = optionData.group;
				optionCodeInfo.name = optionData.name;
				mMyApp.setCurrentOption(optionCodeInfo);
				
				if(PreferenceEngine.getInstance().getTradeMode() == 0)
				{
					float fMMBZ = STD.StringToValue(mHoldList.GetFieldValueString(STEP_Define.STEP_MMLB));
					boolean bBuy = (fMMBZ == 0f ? true : false);
					if (!bBuy)// 义务仓并且是普通交易模式，弹提示
					{
						new AlertDialog(mMyApp.mTradeDetailActivity.getParent()).builder()
						               .setTitle(mContext.getString(R.string.IDS_TiShi))
						               .setMsg(mContext.getString(R.string.IDS_YiWuCangTiShi))
						.setCancelable(false).setCanceledOnTouchOutside(false)
						.setPositiveButton(mContext.getString(R.string.IDS_ZhiDaoLe), new OnClickListener() {
							@Override
							public void onClick(View v) {
								mMyApp.mTradeDetailActivity.updateTradeOrderOptionData(keyongNum);
							}
						}).show();
						return;
					}
				}
				mMyApp.mTradeDetailActivity.updateTradeOrderOptionData(keyongNum);
			}
		}
	}

}
