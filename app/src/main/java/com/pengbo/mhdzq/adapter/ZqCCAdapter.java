package com.pengbo.mhdzq.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.SearchDataItem;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ZqCCAdapter extends BaseAdapter {
	private PBSTEP mListData;
	private Context mContext;
	private MyApp mMyApp;
	private Handler mHandler;
	private boolean bNeedMenu = false;// 是否需要点击条目扩展显示menu，默认不需要
	private int mCurrentCheckedIndex = -1;

	private ViewHolder viewHolder = null;

	public ZqCCAdapter(PBSTEP datas, Context mCon, Handler handler) {
		this.mListData = datas;
		this.mContext = mCon;
		this.mHandler = handler;
		mMyApp = (MyApp) this.mContext.getApplicationContext();
	}

	public void setNeedMenu(boolean bNeed) {
		this.bNeedMenu = bNeed;
	}

	public void setCheckedIndex(int pos) {
		mCurrentCheckedIndex = pos;
	}

	public int getCheckedIndex() {
		return mCurrentCheckedIndex;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(mContext);
				convertView = mInflater
						.inflate(R.layout.zq_trade_cc_item, null);

				viewHolder.name = (TextView) convertView
						.findViewById(R.id.zq_cc_name);
				viewHolder.shizhi = (TextView) convertView
						.findViewById(R.id.zq_cc_code);

				viewHolder.yingkui = (TextView) convertView
						.findViewById(R.id.zq_cc_yk);
				viewHolder.yingkuibili = (TextView) convertView
						.findViewById(R.id.zq_cc_ykbl);

				viewHolder.chicang = (TextView) convertView
						.findViewById(R.id.zq_cc_cc);
				viewHolder.keyong = (TextView) convertView
						.findViewById(R.id.zq_cc_ky);

				viewHolder.chengben = (TextView) convertView
						.findViewById(R.id.zq_cc_cb);
				viewHolder.xianjia = (TextView) convertView
						.findViewById(R.id.zq_cc_xj);

				viewHolder.rlayout_menu = (RelativeLayout) convertView
						.findViewById(R.id.rlayout_chicang_list_menu);
				viewHolder.viewItem = convertView
						.findViewById(R.id.zq_trade_cc_layout);
				viewHolder.mBuyRu = (TextView) convertView
						.findViewById(R.id.tv_menu_buy);
				viewHolder.mSellChu = (TextView) convertView
						.findViewById(R.id.tv_menu_sell);
				viewHolder.mHangQing = (TextView) convertView
						.findViewById(R.id.tv_menu_hangqing);

				convertView.setTag(viewHolder);

			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position < this.getCount()) {

			int dotlen = 3;

			mListData.GotoRecNo(position);

			String optionName = mListData
					.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			String shizhi = mListData
					.GetFieldValueString(STEP_Define.STEP_ZQSZ);

			String chicang = mListData
					.GetFieldValueString(STEP_Define.STEP_DQSL);
			String keyong = mListData
					.GetFieldValueString(STEP_Define.STEP_KYSL);

			String chengbenjia = mListData
					.GetFieldValueString(STEP_Define.STEP_CBJ);

			TagLocalStockData stockData = new TagLocalStockData();

			String market = mListData
					.GetFieldValueString(STEP_Define.STEP_SCDM);

			String jycode = mListData
					.GetFieldValueString(STEP_Define.STEP_HYDM);

			String hqCode = mMyApp.mTradeData.GetHQCodeFromTradeCode(jycode);

			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);

			mMyApp.mHQData_ZQ
					.getData(stockData, (short) nMarket, hqCode, false);

			Float cbfloat = Float.parseFloat(chengbenjia);

			BigDecimal b = new BigDecimal(cbfloat);
			float f1 = b.setScale(dotlen, BigDecimal.ROUND_HALF_UP)
					.floatValue();

			viewHolder.chengben.setText(ViewTools.getStringByFloatPrice(f1, 0,
					dotlen));

			float fPrice = ViewTools.getPriceByFieldNo(
					Global_Define.FIELD_HQ_NOW, stockData);

			String xjf = "%." + dotlen + "f";

			viewHolder.xianjia.setText(String.format(xjf, fPrice));

			ArrayList<SearchDataItem> searchArray = mMyApp.getSearchCodeArray();
			for (int i = 0; i < searchArray.size(); i++) {
				if (searchArray.get(i).market == nMarket
						&& searchArray.get(i).extcode.equals(hqCode)) {
					optionName = searchArray.get(i).name;
					break;
				}
			}

			if (!viewHolder.name.getText().toString().equals(optionName)) {
				viewHolder.name.setText(optionName);
			}

			if (!viewHolder.shizhi.getText().toString().equals(shizhi)) {
				viewHolder.shizhi.setText(shizhi);
			}

			float FDYK;
			String yingkui = "";

			if (chicang.equals("0") || chicang.length() == 0) {
				yingkui = mListData.GetFieldValueString(STEP_Define.STEP_FDYK);

			} else {
				float fCBJ = 0.0f;
				fCBJ = STD.StringToValue(mListData
						.GetFieldValueString(STEP_Define.STEP_CBJ));

				float fDQSL = 0f;
				fDQSL = STD.StringToValue(mListData
						.GetFieldValueString(STEP_Define.STEP_DQSL));

				FDYK = (fPrice - fCBJ) * fDQSL;
				yingkui = String.format("%.2f", FDYK);

			}

			viewHolder.yingkui.setText(yingkui);

			BigDecimal cb = new BigDecimal(chengbenjia)
					.multiply(new BigDecimal(1000));
			BigDecimal xz = new BigDecimal(fPrice)
					.multiply(new BigDecimal(1000));

			BigDecimal gap = xz.subtract(cb);

			if (!cb.equals(BigDecimal.ZERO)) {
				try {
					BigDecimal bl = gap.divide(cb, dotlen + 2,
							BigDecimal.ROUND_HALF_UP).multiply(
							new BigDecimal(100));
					viewHolder.yingkuibili.setText(String.format("%.2f", bl)
							+ "%");
				} catch (Exception ex) {
					viewHolder.yingkuibili.setText("--" + "%");
				}

			} else {
				viewHolder.yingkuibili.setText("--" + "%");
			}

			if (!viewHolder.chicang.getText().toString().equals(chicang)) {
				viewHolder.chicang.setText(chicang);
			}

			if (!viewHolder.keyong.getText().toString().equals(keyong)) {
				viewHolder.keyong.setText(keyong);
			}

			viewHolder.viewItem.setOnClickListener(new CCOnClickListener(
					position));

			if (STD.StringToValue(yingkui) < 0) {
				viewHolder.name.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.shizhi.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.yingkui.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.yingkuibili
						.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.chicang.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.keyong.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.chengben.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
				viewHolder.xianjia.setTextColor(ColorConstant.COLOR_ZQ_GREEN);
			} else if (STD.StringToValue(yingkui) > 0) {
				viewHolder.name.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.shizhi.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.yingkui.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.yingkuibili.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.chicang.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.keyong.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.chengben.setTextColor(ColorConstant.COLOR_ZQ_RED);
				viewHolder.xianjia.setTextColor(ColorConstant.COLOR_ZQ_RED);
			} else {
				viewHolder.yingkui.setTextColor(ColorConstant.COLOR_ZQ_BLACK);

				viewHolder.name.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.shizhi.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.yingkui.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.yingkuibili
						.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.chicang.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.keyong.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.chengben.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
				viewHolder.xianjia.setTextColor(ColorConstant.COLOR_ZQ_BLACK);
			}

			if (mCurrentCheckedIndex == position && bNeedMenu) {
				viewHolder.rlayout_menu.setVisibility(View.VISIBLE);
				viewHolder.mBuyRu.setOnClickListener(new BtnZhanKaiListener(
						position, viewHolder));
				viewHolder.mSellChu.setOnClickListener(new BtnZhanKaiListener(
						position, viewHolder));
				viewHolder.mHangQing.setOnClickListener(new BtnZhanKaiListener(
						position, viewHolder));
				
			} else {
				viewHolder.rlayout_menu.setVisibility(View.GONE);
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

	class BtnZhanKaiListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public BtnZhanKaiListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.mBuyRu) {
				Toast.makeText(mContext, "买入 ", Toast.LENGTH_SHORT).show();
				if (mHandler == null) {
					return;
				}
				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_ADAPTER_CC_BUY_BUTTON_CLICK;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);

			} else if (v == mViewHolder.mSellChu) {
				Toast.makeText(mContext, "卖出  ", Toast.LENGTH_SHORT).show();
				if (mHandler == null) {
					return;
				}
				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_ADAPTER_CC_SELL_BUTTON_CLICK;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);
			} else if (v == mViewHolder.mHangQing) {
				Toast.makeText(mContext, "行情  ", Toast.LENGTH_SHORT).show();
				if (mHandler == null) {
					return;
				}
				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_ADAPTER_CC_HANGQING_BUTTON_CLICK;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);
			}

		}
	}

	class CCOnClickListener implements OnClickListener {
		private int mPosition;

		public CCOnClickListener(int pos) {
			mPosition = pos;
		}

		@Override
		public void onClick(View arg0) {
			if (mCurrentCheckedIndex != -1 && mCurrentCheckedIndex == mPosition) {
				mCurrentCheckedIndex = -1;
			} else {
				setCheckedIndex(mPosition);
			}
			if (bNeedMenu) {
				notifyDataSetChanged();
			}

			if (mHandler != null) {
				// 发送点击消息给UI页面处理，如需要。

				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_UPDATE_DATA;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);
			}
		}
	}

	class ViewHolder {
		TextView name;
		TextView shizhi;

		TextView yingkui;
		TextView yingkuibili;

		TextView chicang;
		TextView keyong;

		TextView chengben;
		TextView xianjia;

		RelativeLayout rlayout_menu;// 点击显示菜单条
		View viewItem;// 数据条目

		TextView mBuyRu, mSellChu, mHangQing;
	}

}
