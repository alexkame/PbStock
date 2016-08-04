package com.pengbo.mhdcx.adapter;


import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeLocalRecord;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 下单页中 持仓页面  的listView 适配器 
 * @author pobo
 *
 */
public class OrderListViewCDAdapter extends BaseAdapter {
	
	public final static int TYPE_CHICANG = 0;
	public final static int TYPE_CHEDAN = 1;

	private PBSTEP mListData;
	private TradeLocalRecord mCheDanRecord = null; //撤单
	public Context context;
	private Handler mHandler;
	private LayoutInflater mInflater;

	public OrderListViewCDAdapter(Context context, PBSTEP data, Handler handler) {
		super();
		this.context = context;
		mCheDanRecord = new TradeLocalRecord();
		this.mListData = data;
		this.mHandler = handler;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	public PBSTEP getDatas() {
		return mListData;
	}

	public void setDatas(PBSTEP datas) {
		this.mListData = datas;
	}
	
	@Override
	public int getCount() {
		return mListData.GetRecNum();
	}

	@Override
	public Object getItem(int position) {
		mListData.GotoRecNo(position);
		return mListData;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (null == convertView) {
			convertView = mInflater.inflate(
					R.layout.trade_order_chedan_listview_item, null);
			viewHolder = new ViewHolder();

			viewHolder.mOptionName = (TextView) convertView
					.findViewById(R.id.tv_chedan_optionname);

			viewHolder.mType1 = (TextView) convertView
					.findViewById(R.id.tv_chedan_type1);

			//viewHolder.mType2 = (TextView) convertView
			//		.findViewById(R.id.tv_chedan_type2);

			viewHolder.mWeituojia = (TextView) convertView
					.findViewById(R.id.tv_chedan_weituojia);

			viewHolder.mNum = (TextView) convertView
					.findViewById(R.id.tv_chedan_num1);

			viewHolder.mNum2 = (TextView) convertView
					.findViewById(R.id.tv_chedan_num2);

			viewHolder.mState1 = (TextView) convertView
					.findViewById(R.id.tv_chedan_state1);

			viewHolder.mState2 = (TextView) convertView
					.findViewById(R.id.tv_chedan_state2);

			convertView.setTag(viewHolder);
		}else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String optionName = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			viewHolder.mOptionName.setText(optionName);
			
			char chBDBZ = mListData.GetFieldValueCHAR(STEP_Define.STEP_BDBZ);
			boolean bBD = (chBDBZ == PTK_Define.PTK_D_BD ? true : false);
			
			float fMMBZ = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_MMLB));
			boolean bBuy = (fMMBZ == 0f ? true : false);
			//mListData.GetFieldValueString(STEP_Define.STEP_MMLBMC);

			String MMLBMC = "";
			if(bBuy)
			{
				MMLBMC = "买";
			}else
			{
				MMLBMC = "卖";
			}
			
			float fKPBZ = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_KPBZ));
			boolean bKai = (fKPBZ == 0f ? true : false);
			String KPCMC = "";
			if(bKai)
			{
				KPCMC = "开";
			}else
			{
				KPCMC = "平";
			}
			String strTemp = "";
			if(bBD)
			{
				strTemp = MMLBMC + KPCMC + "(备)";
			}else
			{
				strTemp = MMLBMC + KPCMC;
			}
			viewHolder.mType1.setText(strTemp);

			//String KPCMC = mListData.GetFieldValueString(STEP_Define.STEP_KPBZMC);
			//viewHolder.mType2.setText(KPCMC);

			char sjType = mListData.GetFieldValueCHAR(STEP_Define.STEP_SJWTLB);
			String WTJG = "";
		    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
		    {
		    	WTJG = mListData.GetFieldValueString(STEP_Define.STEP_SJWTLBMC);
		    }else
		    {
		    	WTJG = mListData.GetFieldValueString(STEP_Define.STEP_WTJG);
		    }
			viewHolder.mWeituojia.setText(WTJG);

			String CJSL = mListData.GetFieldValueString(STEP_Define.STEP_CJSL);
			viewHolder.mNum.setText(CJSL);

			String WTSL = mListData.GetFieldValueString(STEP_Define.STEP_WTSL);
			viewHolder.mNum2.setText(WTSL);

			String WTZTMC = mListData.GetFieldValueString(STEP_Define.STEP_WTZTMC);
			viewHolder.mState1.setText(WTZTMC);
			
			viewHolder.mState2.setOnClickListener(new CheDanClickListener(position, viewHolder));

			String WTZT = mListData.GetFieldValueString(STEP_Define.STEP_WTZT);

			if(DataTools.isCDStatusEnabled(WTZT))
			{
				viewHolder.mState2.setVisibility(View.VISIBLE);
			}else
			{
				viewHolder.mState2.setVisibility(View.GONE);
			}
			
		}
		
		return convertView;
	}
	
	class CheDanClickListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public CheDanClickListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.mState2) {
				//撤单
				if (mListData != null) {
					int num = mListData.GetRecNum();
					if (this.mPosition < num)
					{
						mListData.GotoRecNo(this.mPosition);
						mCheDanRecord.mWTBH = mListData.GetFieldValueString(STEP_Define.STEP_WTBH);
						mCheDanRecord.mWTSHJ = mListData.GetFieldValueString(STEP_Define.STEP_WTRQ);
						mCheDanRecord.mGDZH = mListData.GetFieldValueString(STEP_Define.STEP_GDH);
						mCheDanRecord.mMarketCode = mListData.GetFieldValueString(STEP_Define.STEP_SCDM);
						mCheDanRecord.mXWH = mListData.GetFieldValueString(STEP_Define.STEP_XWH);
						mCheDanRecord.mXDXW = mListData.GetFieldValueString(STEP_Define.STEP_XDXW);
						mCheDanRecord.mWTZT = mListData.GetFieldValueString(STEP_Define.STEP_WTZT);
						mCheDanRecord.mWTZTMC = mListData.GetFieldValueString(STEP_Define.STEP_WTZTMC);
						mCheDanRecord.mBiaodiCode = mListData.GetFieldValueString(STEP_Define.STEP_BDDM);
						mCheDanRecord.mBiaodiMC = mListData.GetFieldValueString(STEP_Define.STEP_BDMC);
						mCheDanRecord.mWTPrice = mListData.GetFieldValueString(STEP_Define.STEP_WTJG);
						mCheDanRecord.mWTSL = mListData.GetFieldValueString(STEP_Define.STEP_WTSL);
						
						MyApp.getInstance().mTradeDetailActivity.requestWTCDFromCheDanView(mCheDanRecord);
					}
				}
				
			} 
		}
	}
	
	class ViewHolder {
		TextView mOptionName;
		TextView mType1;
		TextView mType2;
		TextView mWeituojia;
		TextView mNum;
		TextView mNum2;
		TextView mState1;
		TextView mState2;
	}
}
