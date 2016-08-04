package com.pengbo.mhdcx.adapter;


import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 下单页中 持仓页面  的listView 适配器 
 * @author pobo
 *
 */
public class OrderListViewCCAdapter extends BaseAdapter {
	
	public final static int TYPE_CHICANG = 0;
	public final static int TYPE_CHEDAN = 1;

	private MyApp mMyApp;
	private PBSTEP mListData;
	public Context context;
	private Handler mHandler;
	private LayoutInflater mInflater;

	public OrderListViewCCAdapter(Context context, PBSTEP data, Handler handler) {
		super();
		this.context = context;
		mMyApp = (MyApp) this.context.getApplicationContext();
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
			viewHolder = new ViewHolder();

			convertView = mInflater.inflate(
					R.layout.trade_order_position_listview_item, null);

			viewHolder.tv_position_name = (TextView) convertView
					.findViewById(R.id.tv_position_name);

			viewHolder.tv_position = (TextView) convertView
					.findViewById(R.id.tv_position);

			viewHolder.tv_canuse = (TextView) convertView
					.findViewById(R.id.tv_canuse);

			viewHolder.tv_average = (TextView) convertView
					.findViewById(R.id.tv_average);

			viewHolder.tv_nowprice = (TextView) convertView
					.findViewById(R.id.tv_nowprice);

			viewHolder.tv_fudongyingkui_up = (TextView) convertView
					.findViewById(R.id.tv_fudongyingkui_up);

			viewHolder.tv_fudongyingkui_down = (TextView) convertView
					.findViewById(R.id.tv_fudongyingkui_down);

			viewHolder.image_fanshou = (ImageView) convertView
					.findViewById(R.id.order_position_btn);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String optionName = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);

			char chBDBZ = mListData.GetFieldValueCHAR(STEP_Define.STEP_BDBZ);
			boolean bBD = (chBDBZ == PTK_Define.PTK_D_BD ? true : false);
			
			float fMMBZ = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_MMLB));
			boolean bBuy = (fMMBZ == 0f ? true : false);
			int id = R.drawable.position_quanli;
			
			if (bBD) {
				id = R.drawable.beidui;
			} else {
				if (bBuy) {
					id = R.drawable.position_quanli;
				} else {
					id = R.drawable.position_yiwu;
				}
			}
			
			viewHolder.tv_position_name.setText(optionName);
			Drawable drawable = context.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
			viewHolder.tv_position_name.setCompoundDrawables(drawable, null, null, null);

			String currentNum = mListData.GetFieldValueString(STEP_Define.STEP_DQSL);
			viewHolder.tv_position.setText(STD.IntToString((int)STD.StringToValue(currentNum)));
			
			String junPrice = mListData.GetFieldValueString(STEP_Define.STEP_MRJJ);
			viewHolder.tv_average.setText(junPrice);// setMinprice
			

			TagLocalStockData stockData = new TagLocalStockData();
			String code = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			String market = mListData
					.GetFieldValueString(STEP_Define.STEP_SCDM);
			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
			mMyApp.mHQData.getData(stockData, (short) nMarket, code, false);

			String nowPrice = ViewTools.getStringByFieldID(stockData,
					Global_Define.FIELD_HQ_NOW);
			mListData.SetFieldValueString(STEP_Define.STEP_ZXJ, nowPrice);
			viewHolder.tv_nowprice.setText(nowPrice);

			float fCBJ = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_MRJJ));
			float fDQSL = STD.StringToValue(currentNum);
			float fPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData);
	        
			float FDYK = 0;// 浮动盈亏
			if (bBuy) {
				FDYK = (fPrice - fCBJ) * stockData.optionData.StrikeUnit
						* fDQSL;
			} else {
				FDYK = (fCBJ - fPrice) * stockData.optionData.StrikeUnit
						* fDQSL;
			}

			viewHolder.tv_fudongyingkui_up.setText(String.format("%.2f", FDYK));
			int fudongcolor = ViewTools.getColor(STD.StringToValue(String.format("%.2f", FDYK)));
			viewHolder.tv_fudongyingkui_up.setTextColor(fudongcolor);
			viewHolder.tv_fudongyingkui_down.setText("");
			viewHolder.tv_fudongyingkui_down.setTextColor(fudongcolor);
			
			String keyongNum = mListData.GetFieldValueString(STEP_Define.STEP_KYSL);
			if (keyongNum.equalsIgnoreCase("-99999999"))
			{
				int nBDBZ = mListData.GetFieldValueInt(STEP_Define.STEP_BDBZ);
                int nDJSL = MyApp.getInstance().mTradeData.GetDJSL(code, market, bBuy,nBDBZ);
                int nCCSL = mListData.GetFieldValueInt(STEP_Define.STEP_DQSL);
                int nKYSL = nCCSL-nDJSL;
                if (nKYSL < 0) {
                    nKYSL = 0;
                }
                keyongNum = STD.IntToString(nKYSL);
			}
			viewHolder.tv_canuse.setText(STD.IntToString((int)STD.StringToValue(keyongNum)));

	
			viewHolder.image_fanshou.setOnClickListener(new BtnFanShouListener(position, viewHolder));
			if (PreferenceEngine.getInstance().getTradeMode() == 0)
			{
				viewHolder.image_fanshou.setVisibility(View.GONE);
			}else
			{
				viewHolder.image_fanshou.setVisibility(View.VISIBLE);
			}
		}
		
		return convertView;
	}
	
	class BtnFanShouListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public BtnFanShouListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.image_fanshou) {
				if (mHandler == null)
					return;
				
				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_ADAPTER_KJFS_BUTTON_CLICK;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);
				
			}
		}
	}
	
	class ViewHolder{
		
		ImageView image_position_quanliyiwu;
		TextView tv_position_name;		
		TextView tv_position;
		TextView tv_canuse;
		TextView tv_average;
		TextView tv_nowprice;
		TextView tv_fudongyingkui_up;
		TextView tv_fudongyingkui_down;
		ImageView image_fanshou;
	}

}
