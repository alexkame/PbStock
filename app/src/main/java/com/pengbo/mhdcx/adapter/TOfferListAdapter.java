package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TOfferListAdapter extends BaseAdapter {

	private ArrayList<TagLocalStockData> datas;

	private LayoutInflater mInflater;
	private MyApp mMyApp;
	private boolean mbNegation = false;
	private int mScreenItemNum = 5;
	private Context context;
	private DisplayMetrics mScreenSize;

	private TagLocalStockData aStockData;
	
	private int mTPosition;
	

	public int getmTPosition() {
		return mTPosition;
	}

	public void setmTPosition(int mTPosition) {
		this.mTPosition = mTPosition;
	}

	public TOfferListAdapter(MyApp myApp, Context con,
			ArrayList<TagLocalStockData> datas, boolean bNegation, int itemNum) {
		super();
		this.datas = datas;
		this.mbNegation = bNegation;
		this.mInflater = LayoutInflater.from(con);
		this.context = con;
		mScreenSize = ViewTools.getScreenSize(context);
		mMyApp = myApp;
		mScreenItemNum = itemNum;
	}

	public ArrayList<TagLocalStockData> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<TagLocalStockData> datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.hv_item_data, null);

				if (mbNegation) {
					viewHolder.tv1 = (TextView) convertView
							.findViewById(R.id.hv_item17);
					LayoutParams lp = viewHolder.tv1.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv1.setLayoutParams(lp);

					viewHolder.tv2 = (TextView) convertView
							.findViewById(R.id.hv_item16);
					lp = viewHolder.tv2.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv2.setLayoutParams(lp);

					viewHolder.tv3 = (TextView) convertView
							.findViewById(R.id.hv_item15);
					lp = viewHolder.tv3.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv3.setLayoutParams(lp);

					viewHolder.tv4 = (TextView) convertView
							.findViewById(R.id.hv_item14);
					lp = viewHolder.tv4.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv4.setLayoutParams(lp);

					viewHolder.tv5 = (TextView) convertView
							.findViewById(R.id.hv_item13);
					lp = viewHolder.tv5.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv5.setLayoutParams(lp);

					viewHolder.tv6 = (TextView) convertView
							.findViewById(R.id.hv_item12);
					lp = viewHolder.tv6.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv6.setLayoutParams(lp);

					viewHolder.tv7 = (TextView) convertView
							.findViewById(R.id.hv_item11);
					lp = viewHolder.tv7.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv7.setLayoutParams(lp);

					viewHolder.tv8 = (TextView) convertView
							.findViewById(R.id.hv_item10);
					lp = viewHolder.tv8.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv8.setLayoutParams(lp);

					viewHolder.tv9 = (TextView) convertView
							.findViewById(R.id.hv_item9);
					lp = viewHolder.tv9.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv9.setLayoutParams(lp);
					
					viewHolder.tv10 = (TextView) convertView
							.findViewById(R.id.hv_item8);
					lp = viewHolder.tv10.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv10.setLayoutParams(lp);
					
					viewHolder.tv11 = (TextView) convertView
							.findViewById(R.id.hv_item7);
					lp = viewHolder.tv11.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv11.setLayoutParams(lp);
					
					viewHolder.tv12 = (TextView) convertView
							.findViewById(R.id.hv_item6);
					lp = viewHolder.tv12.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv12.setLayoutParams(lp);
					
					viewHolder.tv13 = (TextView) convertView
							.findViewById(R.id.hv_item5);
					lp = viewHolder.tv13.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv13.setLayoutParams(lp);
					
					viewHolder.tv14 = (TextView) convertView
							.findViewById(R.id.hv_item4);
					lp = viewHolder.tv14.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv14.setLayoutParams(lp);
					
					viewHolder.tv15 = (TextView) convertView
							.findViewById(R.id.hv_item3);
					lp = viewHolder.tv15.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv15.setLayoutParams(lp);
					
					viewHolder.tv16 = (TextView) convertView
							.findViewById(R.id.hv_item2);
					lp = viewHolder.tv16.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv16.setLayoutParams(lp);
					
					viewHolder.tv17 = (TextView) convertView
							.findViewById(R.id.hv_item1);
					lp = viewHolder.tv17.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv17.setLayoutParams(lp);
				} else {
					viewHolder.tv1 = (TextView) convertView
							.findViewById(R.id.hv_item1);
					LayoutParams lp = viewHolder.tv1.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv1.setLayoutParams(lp);

					viewHolder.tv2 = (TextView) convertView
							.findViewById(R.id.hv_item2);
					lp = viewHolder.tv2.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv2.setLayoutParams(lp);

					viewHolder.tv3 = (TextView) convertView
							.findViewById(R.id.hv_item3);
					lp = viewHolder.tv3.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv3.setLayoutParams(lp);

					viewHolder.tv4 = (TextView) convertView
							.findViewById(R.id.hv_item4);
					lp = viewHolder.tv4.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv4.setLayoutParams(lp);

					viewHolder.tv5 = (TextView) convertView
							.findViewById(R.id.hv_item5);
					lp = viewHolder.tv5.getLayoutParams();
					lp.width = mScreenSize.widthPixels / mScreenItemNum;
					viewHolder.tv5.setLayoutParams(lp);

					viewHolder.tv6 = (TextView) convertView
							.findViewById(R.id.hv_item6);
					lp = viewHolder.tv6.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv6.setLayoutParams(lp);

					viewHolder.tv7 = (TextView) convertView
							.findViewById(R.id.hv_item7);
					lp = viewHolder.tv7.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv7.setLayoutParams(lp);

					viewHolder.tv8 = (TextView) convertView
							.findViewById(R.id.hv_item8);
					lp = viewHolder.tv8.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv8.setLayoutParams(lp);

					viewHolder.tv9 = (TextView) convertView
							.findViewById(R.id.hv_item9);
					lp = viewHolder.tv9.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv9.setLayoutParams(lp);
					
					viewHolder.tv10 = (TextView) convertView
							.findViewById(R.id.hv_item10);
					lp = viewHolder.tv10.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv10.setLayoutParams(lp);
					
					viewHolder.tv11 = (TextView) convertView
							.findViewById(R.id.hv_item11);
					lp = viewHolder.tv11.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv11.setLayoutParams(lp);
					
					viewHolder.tv12 = (TextView) convertView
							.findViewById(R.id.hv_item12);
					lp = viewHolder.tv12.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv12.setLayoutParams(lp);
					
					viewHolder.tv13 = (TextView) convertView
							.findViewById(R.id.hv_item13);
					lp = viewHolder.tv13.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv13.setLayoutParams(lp);
					
					viewHolder.tv14 = (TextView) convertView
							.findViewById(R.id.hv_item14);
					lp = viewHolder.tv14.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv14.setLayoutParams(lp);
					
					viewHolder.tv15 = (TextView) convertView
							.findViewById(R.id.hv_item15);
					lp = viewHolder.tv15.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv15.setLayoutParams(lp);
					
					viewHolder.tv16 = (TextView) convertView
							.findViewById(R.id.hv_item16);
					lp = viewHolder.tv16.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv16.setLayoutParams(lp);
					
					viewHolder.tv17 = (TextView) convertView
							.findViewById(R.id.hv_item17);
					lp = viewHolder.tv17.getLayoutParams();
					lp.width = mScreenSize.widthPixels / (mScreenItemNum - 1);
					viewHolder.tv17.setLayoutParams(lp);
				}
				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set item values to the viewHolder:

		// aStockData 合约信息
		aStockData = datas.get(position);

		// aStockInfo 合约对应的标的信息
		TagLocalStockData aStockInfo = new TagLocalStockData();

		mMyApp.mStockConfigData.search(aStockInfo,
				aStockData.optionData.StockMarket,
				aStockData.optionData.StockCode);
		viewHolder.tv1.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_NOW));
		viewHolder.tv1.setTextColor(ViewTools.getColorByFieldID(aStockData,
				Global_Define.FIELD_HQ_NOW));

		viewHolder.tv2.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_ZD));
		viewHolder.tv2.setTextColor(ViewTools.getColorByFieldID(aStockData,
				Global_Define.FIELD_HQ_NOW));

		viewHolder.tv3.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_ZDF));
		viewHolder.tv3.setTextColor(ViewTools.getColorByFieldID(aStockData,
				Global_Define.FIELD_HQ_NOW));

		viewHolder.tv4.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_BUYPRICE, aStockInfo));

		viewHolder.tv5.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_SELLPRICE));

		viewHolder.tv6.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_VOLUME));
		viewHolder.tv7.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_CCL));
		viewHolder.tv8.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_YHBDL, aStockInfo));
		viewHolder.tv9.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_TheoryPrice, aStockInfo));
		
		viewHolder.tv10.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_GGL, aStockInfo));
		
		viewHolder.tv11.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_ZSGGL, aStockInfo));
		
		viewHolder.tv12.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_YJL, aStockInfo));
		
		viewHolder.tv13.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_Delta, aStockInfo));
		
		viewHolder.tv14.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_Gamma, aStockInfo));
		
		viewHolder.tv15.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_Rho, aStockInfo));
		
		viewHolder.tv16.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_Theta, aStockInfo));
		
		viewHolder.tv17.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_Vega, aStockInfo));
		
		
		if(mbNegation){
			if(position>=0 && position<datas.size()){
				if (position<mTPosition) {
					convertView.setBackgroundResource(R.color.t_offer_f7eeee);
				}else if(position==mTPosition){
					convertView.setBackgroundResource(R.drawable.t_offer_beside_xingquanjia_up_down);
				} else {
					convertView.setBackgroundResource(R.color.white);
				}
			}
		}else{
			if(position>=0 && position<datas.size()){
				if (position<mTPosition) {
					convertView.setBackgroundResource(R.color.white);
				} else if(position==mTPosition){
					convertView.setBackgroundResource(R.drawable.t_offer_beside_xingquanjia_up_down);
				}else {
					convertView.setBackgroundResource(R.color.t_offer_f7eeee);
				}
			}
		}

		

		return convertView;
	}

	class ViewHolder {
		TextView tv1;// 现价
		TextView tv2;// 涨跌额
		TextView tv3;// 涨跌幅
		TextView tv4;// 买价
		TextView tv5;// 卖价
		TextView tv6;// 总量
		TextView tv7;// 持仓量
		TextView tv8;// 隐含波动率
		TextView tv9;// 理论家
		TextView tv10;// 杠杆率
		TextView tv11;// 真实杠杆率
		TextView tv12;//溢价率
		TextView tv13;// Delta
		TextView tv14;// Gamma
		TextView tv15;// Rho
		TextView tv16;// Theta
		TextView tv17;// Vega

	}
}
