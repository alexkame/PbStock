package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqWTAdapter.ViewHolder;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
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

public class ZqZJAdapter extends BaseAdapter {

	private PBSTEP mListData;
	private Context mContext;
	private Handler mHandler;
	private MyApp mMyApp;

	private ViewHolder viewHolder = null;

	public ZqZJAdapter(PBSTEP datas, Context mCon, Handler handler, MyApp app) {
		this.mListData = datas;
		this.mContext = mCon;
		this.mHandler = handler;
		this.mMyApp = app;
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
						R.layout.zq_trade_zjls_listitem, null);

				viewHolder.fsdate = (TextView) convertView
						.findViewById(R.id.zq_zj_fsrq);

				viewHolder.ywbs = (TextView) convertView
						.findViewById(R.id.zq_zj_ywbs);
				viewHolder.ywje = (TextView) convertView
						.findViewById(R.id.zq_zj_ywje);

				viewHolder.zjdqye = (TextView) convertView
						.findViewById(R.id.zq_zj_zjdqye);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position < this.getCount()) {
			mListData.GotoRecNo(position);

			int dotlen = 2;

			String fsdate = mListData
					.GetFieldValueString(STEP_Define.STEP_FSRQ);
			String ywbs = mListData
					.GetFieldValueString(STEP_Define.STEP_YZYWSM);

			String ywje = mListData.GetFieldValueString(STEP_Define.STEP_YWJE);
			String zjdqye = mListData
					.GetFieldValueString(STEP_Define.STEP_ZJYE);


			viewHolder.fsdate.setText(fsdate);
			viewHolder.ywbs.setText(ywbs);
			viewHolder.ywje.setText(ywje);
			viewHolder.zjdqye.setText(String.valueOf(zjdqye));
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
		TextView fsdate;
		TextView ywbs;

		TextView ywje;

		TextView zjdqye;

	}

}
