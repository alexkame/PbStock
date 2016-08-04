package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqCCAdapter.ViewHolder;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.SearchDataItem;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ZqWTAdapter extends BaseAdapter {

	private PBSTEP mListData;
	private PBSTEP mLIstDataCJ;
	private Context mContext;
	private boolean mIsLishi;
	private ViewHolder viewHolder = null;
	private MyApp mMyApp;

	private Handler mHandler;
	private int mCurrentCheckedIndex = -1;

	public ZqWTAdapter(PBSTEP datas, Context mCon, Handler handler, MyApp app,
			boolean isLishi) {
		this.mListData = datas;
		this.mContext = mCon;
		this.mHandler = handler;
		this.mMyApp = app;
		this.mIsLishi = isLishi;

	}

	public void setCJData(PBSTEP datas) {
		mLIstDataCJ = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListData.GetRecNum();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		mListData.GotoRecNo(position);
		return mListData;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(mContext);
				convertView = mInflater.inflate(
						R.layout.zq_trade_entrust_listitem, null);

				viewHolder.buysell = (ImageView) convertView
						.findViewById(R.id.zq_buy_idex);

				viewHolder.name = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_name);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_time);

				viewHolder.weituojia = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_weituojia);
				viewHolder.junjia = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_junjia);

				viewHolder.weituoliang = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_weituoliang);
				viewHolder.chengjiaoliang = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_chengjiaoliang);
				viewHolder.zhuangtai = (TextView) convertView
						.findViewById(R.id.zq_entrust_item_zhuangtai);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position < this.getCount()) {
			mListData.GotoRecNo(position);

			String mmlb = mListData.GetFieldValueString(STEP_Define.STEP_MMLB);

			if (mmlb.equals("0")) {
				viewHolder.buysell.setBackgroundDrawable(mContext
						.getResources().getDrawable(R.drawable.zq_buy_idx));
			} else if (mmlb.equals("1")) {
				viewHolder.buysell.setBackgroundDrawable(mContext
						.getResources().getDrawable((R.drawable.zq_sell_idx)));
			}

			String optionName = mListData
					.GetFieldValueString(STEP_Define.STEP_HYDMMC);

			if (optionName == null || optionName.length() == 0) {
				SearchDataItem item;

				String market = mListData
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				String code = mListData
						.GetFieldValueString(STEP_Define.STEP_HYDM);

				int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
				String nHqCode = mMyApp.mTradeData.GetHQCodeFromTradeCode(code);

				ArrayList<SearchDataItem> searchArray = mMyApp
						.getSearchTradeCodeArray();

				for (int i = 0; i < searchArray.size(); i++) {
					item = searchArray.get(i);
					if (item.market == nMarket && item.code.endsWith(nHqCode))

						optionName = item.name;
				}
			}

			String weituotime;
			if (mIsLishi) {
				weituotime = mListData
						.GetFieldValueString(STEP_Define.STEP_WTRQ);
			} else {
				weituotime = mListData
						.GetFieldValueString(STEP_Define.STEP_WTSJ);
			}

			String weituojia;
			
			char sjType = mListData.GetFieldValueCHAR(STEP_Define.STEP_SJWTLB);
		    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
		    {
		    	//weituojia = mListData.GetFieldValueString(STEP_Define.STEP_SJWTLBMC);
		    	weituojia = "市价";
		    }else
		    {
		    	weituojia = mListData.GetFieldValueString(STEP_Define.STEP_WTJG);
		    }
		    

			String weituoliang = mListData
					.GetFieldValueString(STEP_Define.STEP_WTSL);
			String chengjiaoliang = mListData
					.GetFieldValueString(STEP_Define.STEP_CJSL);
			String zhuangtai = mListData
					.GetFieldValueString(STEP_Define.STEP_WTZTMC);
			String wtzt = mListData.GetFieldValueString(STEP_Define.STEP_WTZT);

			String wtbh = mListData.GetFieldValueString(STEP_Define.STEP_WTBH);
			String cjsl, cjjg;
			float jgAmout = (float) 0.0;
			float slAmout = 0;
			float average = 0;
			String sJunjia = "0";

			int dotlen = getDotlen();

			if (this.mIsLishi) {
				sJunjia = mListData
						.GetFieldValueString(STEP_Define.STEP_CJJG);
			} else if (mLIstDataCJ != null) {

				for (int j = 0; j < mLIstDataCJ.GetRecNum(); j++) {
					mLIstDataCJ.GotoRecNo(j);
					String wtbh2 = mLIstDataCJ
							.GetFieldValueString(STEP_Define.STEP_WTBH);
					if (wtbh.equalsIgnoreCase(wtbh2)) {
						cjsl = mLIstDataCJ
								.GetFieldValueString(STEP_Define.STEP_CJSL);
						cjjg = mLIstDataCJ
								.GetFieldValueString(STEP_Define.STEP_CJJG);

						jgAmout = Float.valueOf(cjsl) * Float.valueOf(cjjg)
								+ jgAmout;
						slAmout = slAmout + Float.valueOf(cjsl);
					}
				}

				if (slAmout > 0.1) {
					average = jgAmout / slAmout;

					sJunjia = ViewTools.getStringByFloatPrice(average, 0,
							dotlen);
				}
			}

			viewHolder.name.setText(optionName);
			viewHolder.time.setText(weituotime);
			
		    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
		    {
				viewHolder.weituojia.setText(weituojia);
		    }else
		    {
				viewHolder.weituojia.setText(ViewTools.getStringByFloatPrice(
						Float.valueOf(weituojia), 0, dotlen));
		    }
		    
			viewHolder.junjia.setText(sJunjia);
			viewHolder.weituoliang.setText(weituoliang);
			viewHolder.chengjiaoliang.setText(chengjiaoliang);
			viewHolder.zhuangtai.setText(zhuangtai);

			if (DataTools.isTraded(wtzt)) {
				viewHolder.zhuangtai.setTextColor(ColorConstant.COLOR_ZQ_BLUE);
			} else {
				viewHolder.zhuangtai.setTextColor(ColorConstant.COLOR_ZQ_GRAY);
			}

		}

		return convertView;
	}

	private int getDotlen() {
		int dotlen = 2;

		String jymarket = mListData.GetFieldValueString(STEP_Define.STEP_SCDM);
		String jycode = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);

		String hqcode = mMyApp.mTradeData.GetHQCodeFromTradeCode(jycode);

		short hqmarket = (short) TradeData.GetHQMarketFromTradeMarket(jymarket);

		TagLocalStockData optionData = new TagLocalStockData();
		if (!mMyApp.mHQData_ZQ.getData(optionData, hqmarket, hqcode, false)) {

			ArrayList<CCodeTableItem> codeTableList = null;
			for (int j = 0; j < mMyApp.mCodeTableMarketNum
					&& j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
				if (hqmarket == mMyApp.mCodeTable[j].mMarketId) {
					codeTableList = mMyApp.mCodeTable[j].mCodeTableList;
					break;
				}
			}

			if (codeTableList != null) {
				for (int m = 0; m < codeTableList.size(); m++) {
					CCodeTableItem item = codeTableList.get(m);
					if (hqmarket == item.market
							&& hqcode.equalsIgnoreCase(item.code)) {
						dotlen = item.PriceDecimal;
					}

				}
			}
		} else {
			dotlen = optionData.PriceDecimal;
		}

		return dotlen;
	}

	class ViewHolder {
		ImageView buysell;
		TextView name;
		TextView time;

		TextView weituojia;
		TextView junjia;

		TextView weituoliang;
		TextView chengjiaoliang;

		TextView zhuangtai;

	}

}
