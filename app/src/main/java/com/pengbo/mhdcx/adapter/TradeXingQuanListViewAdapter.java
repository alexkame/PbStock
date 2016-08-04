package com.pengbo.mhdcx.adapter;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 交易持仓中的listView 适配器 
 * @author pobo
 *
 */
public class TradeXingQuanListViewAdapter extends BaseAdapter {
	
	private MyApp mMyApp;
	private PBSTEP mListData;
	private Context context;


	public TradeXingQuanListViewAdapter(Context context, PBSTEP datas) {
		this.context = context;
		mMyApp = (MyApp) this.context.getApplicationContext();
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
		
		if(null == convertView){
			synchronized (this) {

				viewHolder = new ViewHolder();
				LayoutInflater mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(
						R.layout.trade_xingquan_listview_item, null);

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

				viewHolder.image_xingquan_qi = (ImageView) convertView
						.findViewById(R.id.image_xingquan_qi);
				
				viewHolder.tv_xingquan_date = (TextView) convertView
						.findViewById(R.id.tv_xingquan_date);
				
				viewHolder.tv_xingquan_lastdays = (TextView) convertView
						.findViewById(R.id.tv_xingquan_lastdays);

				viewHolder.image_xingquan_bao = (ImageView) convertView
						.findViewById(R.id.image_xingquan_bao);
				
				viewHolder.tv_xingquan_bao = (TextView) convertView
						.findViewById(R.id.tv_xingquan_bao);
				
				viewHolder.tv_xingquan_zhuangtai=(TextView) convertView.findViewById(R.id.tv_xingquan_zhuangtai);
				
				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String optionName = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);

			float fMMBZ = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_MMLB));
			boolean bBuy = (fMMBZ == 0f ? true : false);
			int id = R.drawable.position_quanli;
			if (bBuy) {
				id = R.drawable.position_quanli;
			} else {
				id = R.drawable.position_yiwu;
			}
			
			viewHolder.tv_position_name.setText(optionName);
			Drawable drawable = context.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
			viewHolder.tv_position_name.setCompoundDrawables(drawable, null, null, null);

			String currentNum = mListData.GetFieldValueString(STEP_Define.STEP_DQSL);
			viewHolder.tv_position.setText(STD.IntToString((int)STD.StringToValue(currentNum)));

			String junPrice = mListData.GetFieldValueString(STEP_Define.STEP_MRJJ);
			viewHolder.tv_average.setText(junPrice);// setMinprice
			
			TagLocalStockData optionData = new TagLocalStockData();
			String code = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			String market = mListData
					.GetFieldValueString(STEP_Define.STEP_SCDM);
			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
			mMyApp.mHQData.getData(optionData, (short) nMarket, code, false);

			String nowPrice = ViewTools.getStringByFieldID(optionData,
					Global_Define.FIELD_HQ_NOW);
			mListData.SetFieldValueString(STEP_Define.STEP_ZXJ, nowPrice);
			viewHolder.tv_nowprice.setText(nowPrice);

			//行权价值计算
			double dInValue = 0.0;
			TagLocalStockData stockData = new TagLocalStockData(); //标的
			mMyApp.mStockConfigData.search(stockData, optionData.optionData.StockMarket, optionData.optionData.StockCode);
			
			float fStockPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData );
			float fOptionExecutePrice = optionData.optionData.StrikePrice;
			
			//0->看涨(认购)  1->看跌(认沽)
	        if (optionData.optionData.OptionCP == 0) {
	        	dInValue = fStockPrice - fOptionExecutePrice;
	        }
	        else if (optionData.optionData.OptionCP == 1)
	        {
	        	dInValue = fOptionExecutePrice - fStockPrice ;
	        }
	        
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

	        
	        double xqjz = dInValue*optionData.optionData.StrikeUnit*STD.StringToValue(currentNum);
	        viewHolder.tv_fudongyingkui_up.setText(String.format("%.2f", xqjz));
	       
	        int fudongcolor = ViewTools.getColor((float)xqjz);
			viewHolder.tv_fudongyingkui_up.setTextColor(fudongcolor);
			
			
			viewHolder.tv_fudongyingkui_down.setText("");
			
			viewHolder.image_xingquan_qi.setBackgroundResource(R.drawable.position_qi);

			String daoqiri = ViewTools.getStringByFieldID(optionData, Global_Define.FIELD_HQ_EXPIRE_DATE);
			viewHolder.tv_xingquan_date.setText(daoqiri);
			
			int nDays = ViewTools.getDaysDruationFromToday(optionData.optionData.StrikeDate);

			String strTemp = "";
			if (nDays > 0) {
				strTemp = String.format("剩余%d天", nDays);
				if(nDays<=7){
					viewHolder.tv_xingquan_lastdays.setTextColor(ColorConstant.COLOR_ALL_RED);
				}else{
					viewHolder.tv_xingquan_lastdays.setTextColor(ColorConstant.COLOR_ALL_BLUE);
				}
			} else if (nDays == 0) {
				if (bBuy) {
					// "期权到期,如需行权请使用PC版操作"
					strTemp = "剩余0天";
				} else {
					// 等待权利方行权
					strTemp = "等待权利方行权";
				}
			} else {
				strTemp = "";
			}
			viewHolder.tv_xingquan_lastdays.setText(strTemp);

			float fBDBZ = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_BDBZ));
			boolean bBD = (fBDBZ == 1.0f ? true : false);
			int imgId = 0;
			if (bBD) {
				imgId = R.drawable.position_bei;
			} else {
				imgId = R.drawable.position_bao;
			}

			String bzj = mListData.GetFieldValueString(STEP_Define.STEP_BZJ);
			if (bBD)
			{
				bzj = "";
			}
			
			if (!bBuy)//买是权利，不需要保证金
			{
				viewHolder.image_xingquan_bao.setVisibility(View.VISIBLE);
				viewHolder.tv_xingquan_bao.setVisibility(View.VISIBLE);
				viewHolder.image_xingquan_bao.setBackgroundResource(imgId);
				viewHolder.tv_xingquan_bao.setText(bzj);
			}else
			{
				viewHolder.image_xingquan_bao.setVisibility(View.GONE);
				viewHolder.tv_xingquan_bao.setVisibility(View.GONE);
			}
			
			if (nDays <=30)
			{
				//convertView.setOnClickListener(new XQClickListener(position, viewHolder));
				viewHolder.tv_xingquan_zhuangtai.setVisibility(View.VISIBLE);
				viewHolder.tv_xingquan_zhuangtai.setText("点击行权");
				//viewHolder.tv_xingquan_zhuangtai.setTextColor(ColorConstant.COLOR_BLUE);
				//viewHolder.tv_xingquan_zhuangtai.setOnClickListener(new XQClickListener(position, viewHolder));
			}else
			{
				viewHolder.tv_xingquan_zhuangtai.setVisibility(View.GONE);
			}
			
		}
		
		return convertView;
	}
	
	class XQClickListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public XQClickListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.tv_xingquan_zhuangtai) {
				//撤单
				if (mListData != null) {
					int num = mListData.GetRecNum();
					if (this.mPosition < num)
					{
						mListData.GotoRecNo(this.mPosition);
						Toast.makeText(context, "Invalid LTKContext", Toast.LENGTH_SHORT)
						.show();
					}
				}
				
			} 
		}
	}
	
	public static class ViewHolder {
		
		ImageView image_position_quanliyiwu; // 权利义务图标
		TextView tv_position_name;	//期权名称	
		TextView tv_position; //持仓
		TextView tv_canuse; // 可用
		TextView tv_average; //均价
		TextView tv_nowprice; //现价
		TextView tv_fudongyingkui_up; // 浮动盈亏--换成行权价值
		TextView tv_fudongyingkui_down; //浮动盈亏比例
		ImageView image_xingquan_qi; //到期日图标
		TextView tv_xingquan_date; //到期日日期
		TextView tv_xingquan_lastdays; //到期日剩余天数
		ImageView image_xingquan_bao; //保证金/备 图标
		TextView tv_xingquan_bao; //保证金数目
		
		TextView tv_xingquan_zhuangtai;//=========行权状态
	}
	
	
	

}
