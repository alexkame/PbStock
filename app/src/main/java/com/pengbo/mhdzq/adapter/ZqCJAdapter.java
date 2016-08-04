package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqWTAdapter.ViewHolder;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.SearchDataItem;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
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

public class ZqCJAdapter extends BaseAdapter {

	private PBSTEP mListData;
	private Context mContext;
	private Handler mHandler;
	private MyApp mMyApp;
	private boolean mIsLiShi;

	private ViewHolder viewHolder = null;

	public ZqCJAdapter(PBSTEP datas, Context mCon, Handler handler, MyApp app,boolean isLishi) {
		this.mListData = datas;
		this.mContext = mCon;
		this.mHandler = handler;
		this.mMyApp = app;
		this.mIsLiShi = isLishi;
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
						R.layout.zq_trade_drcj_listitem, null);

				viewHolder.buysell = (ImageView) convertView
						.findViewById(R.id.zq_buy_idex);

				viewHolder.name = (TextView) convertView
						.findViewById(R.id.zq_cj_item_name);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.zq_cj_item_time);

				viewHolder.chenjiaojia = (TextView) convertView
						.findViewById(R.id.zq_cj_item_chenjiaojia);
				viewHolder.chenjiaoliang = (TextView) convertView
						.findViewById(R.id.zq_cj_item_chenjiaoliang);

				viewHolder.chenjiaojine = (TextView) convertView
						.findViewById(R.id.zq_cj_item_chengjiaojine);

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

			int dotlen = 2;

			String market = mListData
					.GetFieldValueString(STEP_Define.STEP_SCDM);

			String optionName = mListData
					.GetFieldValueString(STEP_Define.STEP_HYDMMC);

			if (optionName == null || optionName.length() == 0) {
				SearchDataItem item;

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

			String time;
			if(mIsLiShi)
			{
				time = mListData.GetFieldValueString(STEP_Define.STEP_CJRQ);
			}
			else
			{
				time = mListData.GetFieldValueString(STEP_Define.STEP_CJSJ);	
			}
			
			String sprice = mListData
					.GetFieldValueString(STEP_Define.STEP_CJJG);

			float price = STD.StringToValue(sprice);
			int amount = mListData.GetFieldValueInt(STEP_Define.STEP_CJSL);

			float tolmoney = price * amount;

			dotlen = getDotlen();

			viewHolder.name.setText(optionName);
			viewHolder.time.setText(time);
			viewHolder.chenjiaojia.setText(ViewTools.getStringByFloatPrice(
					price, 0, dotlen));
			viewHolder.chenjiaoliang.setText(String.valueOf(amount));
			viewHolder.chenjiaojine.setText(ViewTools.getStringByFloatPrice(
					tolmoney, 0, dotlen));
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

		TextView chenjiaojia;

		TextView chenjiaoliang;

		TextView chenjiaojine;

	}

}
