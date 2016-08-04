package com.pengbo.mhdcx.adapter;



import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 行权下单list中的listView 适配器 
 * @author pobo
 *
 */
public class TradeXingQuanCDListAdapter extends BaseAdapter {
	
	private MyApp mMyApp;
	private PBSTEP mListData;
	private Context context;
	private Handler mHandler;


	public TradeXingQuanCDListAdapter(Context context, PBSTEP datas, Handler handler) {
		this.context = context;
		mMyApp = (MyApp) this.context.getApplicationContext();
		this.mListData = datas;
		this.mHandler = handler;
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
		
		if(null == convertView){
			synchronized (this) {

				viewHolder = new ViewHolder();
				LayoutInflater mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(
						R.layout.trade_xq_chedan_listview_item, null);

				viewHolder.tv_position_name = (TextView) convertView
						.findViewById(R.id.tv_xqcd_optionname);
				
				viewHolder.tv_xqtime = (TextView) convertView
						.findViewById(R.id.tv_xqcd_time);
				
				viewHolder.tv_xqsl = (TextView) convertView
						.findViewById(R.id.tv_xqcd_num);
				
				viewHolder.tv_xqstate1 = (TextView) convertView
						.findViewById(R.id.tv_xqcd_state1);
				
				viewHolder.tv_xqstate2 = (TextView) convertView
						.findViewById(R.id.tv_xqcd_state2);
				
				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String WTSJ = mListData.GetFieldValueString(STEP_Define.STEP_WTSJ);
			viewHolder.tv_xqtime.setText(WTSJ);
			
			TagLocalStockData stockData = new TagLocalStockData();
			String code = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			String market = mListData.GetFieldValueString(STEP_Define.STEP_SCDM);
			
			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
			mMyApp.mHQData.getData(stockData, (short) nMarket, code, false);
			
			viewHolder.tv_position_name.setText(stockData.name);

			String XQSL = mListData.GetFieldValueString(STEP_Define.STEP_WTSL);
			viewHolder.tv_xqsl.setText(STD.IntToString((int)STD.StringToValue(XQSL)));

			String WTZTMC = mListData.GetFieldValueString(STEP_Define.STEP_WTZTMC);
			viewHolder.tv_xqstate1.setText(WTZTMC);
			
			//viewHolder.tv_xqstate2.setOnClickListener(new XQCDClickListener(position, viewHolder));

			String WTZT = mListData.GetFieldValueString(STEP_Define.STEP_WTZT);

			if(DataTools.isCDStatusEnabled(WTZT))
			{
				viewHolder.tv_xqstate2.setVisibility(View.VISIBLE);
			}else
			{
				viewHolder.tv_xqstate2.setVisibility(View.GONE);
			}
			
		}
		
		return convertView;
	}
	
	class XQCDClickListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public XQCDClickListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.tv_xqstate2) {
				// 撤单
				if (mHandler == null)
					return;

				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_ADAPTER_CD_BUTTON_CLICK;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);
			}
		}
	}
	
	public static class ViewHolder {

		TextView tv_position_name;	//期权名称	
		TextView tv_xqtime; //持仓
		TextView tv_xqsl; // 可用
		TextView tv_xqstate1;
		TextView tv_xqstate2;
	}
	
	
	

}
