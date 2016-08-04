package com.pengbo.mhdcx.adapter;

import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * 历史委托list中的listView 适配器 
 * @author pobo
 *
 */
public class TradeLSWTListAdapter extends BaseAdapter {
	
	private PBSTEP mListData;
	private Context context;
	private DisplayMetrics mScreenSize;


	public TradeLSWTListAdapter(Context context, PBSTEP datas) {
		this.context = context;
		this.mListData = datas;
		mScreenSize = ViewTools.getScreenSize(context);
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
				convertView = mInflater.inflate(R.layout.hv_lswt_item_data, null);

				viewHolder.tv_item1 = (TextView) convertView.findViewById(R.id.hv_lscx_item1);
				LayoutParams lp = viewHolder.tv_item1.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item1.setLayoutParams(lp);
				
				viewHolder.tv_item2 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item2);
				lp = viewHolder.tv_item2.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item2.setLayoutParams(lp);
				
				viewHolder.tv_item3 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item3);
				lp = viewHolder.tv_item3.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item3.setLayoutParams(lp);
				
				viewHolder.tv_item4 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item4);
				lp = viewHolder.tv_item4.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item4.setLayoutParams(lp);
				
				viewHolder.tv_item5 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item5);
				lp = viewHolder.tv_item5.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item5.setLayoutParams(lp);
				
				viewHolder.tv_item6 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item6);
				lp = viewHolder.tv_item6.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item6.setLayoutParams(lp);
				
				viewHolder.tv_item7 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item7);
				lp = viewHolder.tv_item7.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item7.setLayoutParams(lp);
				
				viewHolder.tv_item8 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item8);
				lp = viewHolder.tv_item8.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item8.setLayoutParams(lp);
				
				viewHolder.tv_item9 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item9);
				lp = viewHolder.tv_item9.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item9.setLayoutParams(lp);
				
				viewHolder.tv_item10 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item10);
				lp = viewHolder.tv_item10.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item10.setLayoutParams(lp);
				
				viewHolder.tv_item11 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item11);
				lp = viewHolder.tv_item11.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item11.setLayoutParams(lp);
				
				viewHolder.tv_item12 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item12);
				lp = viewHolder.tv_item12.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item12.setLayoutParams(lp);
				
				viewHolder.tv_item13 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item13);
				lp = viewHolder.tv_item13.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item13.setLayoutParams(lp);
				
				
				viewHolder.tv_item14 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item14);
				lp = viewHolder.tv_item14.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item14.setLayoutParams(lp);
				
				viewHolder.tv_item15 = (TextView) convertView
						.findViewById(R.id.hv_lscx_item15);
				lp = viewHolder.tv_item15.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item15.setLayoutParams(lp);
				
				
				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String strTemp = "";
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_WTRQ);
			viewHolder.tv_item1.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_WTSJ);
			viewHolder.tv_item2.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_WTBH);
			viewHolder.tv_item3.setText(strTemp);
			
			//合约编码
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			viewHolder.tv_item4.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			viewHolder.tv_item5.setText(strTemp);
			
			//买卖
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_MMLBMC);
			viewHolder.tv_item6.setText(strTemp);
			
			//开平
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_KPBZMC);
			viewHolder.tv_item7.setText(strTemp);
			
			//委托数量
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_WTSL);
			viewHolder.tv_item8.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_WTJG);
			viewHolder.tv_item9.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJSL);
			viewHolder.tv_item10.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CDSL);
			viewHolder.tv_item11.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_WTZTMC);
			viewHolder.tv_item12.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_BDBZMC);
			viewHolder.tv_item13.setText(strTemp);
			
			String hylb = mListData.GetFieldValueString(STEP_Define.STEP_HYLB);
			if (hylb.equalsIgnoreCase(Trade_Define.TRADE_HYLB_GOU) == true)
			{
				viewHolder.tv_item14.setText("认购");
				viewHolder.tv_item14.setTextColor(ViewTools.getColor(1.0f));
			}else if (hylb.equalsIgnoreCase(Trade_Define.TRADE_HYLB_GU) == true)
			{
				viewHolder.tv_item14.setText("认沽");
				viewHolder.tv_item14.setTextColor(ViewTools.getColor(-1.0f));
			}else
			{
				viewHolder.tv_item13.setText("");
			}
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_MMLBSM);
			viewHolder.tv_item15.setText(strTemp);
		}
		
		return convertView;
	}
	
	public static class ViewHolder {

		TextView tv_item1;	//期权名称	
		TextView tv_item2; //持仓
		TextView tv_item3; // 可用
		TextView tv_item4;
		TextView tv_item5;
		TextView tv_item6;
		TextView tv_item7;
		TextView tv_item8;
		TextView tv_item9;
		TextView tv_item10;
		TextView tv_item11;
		TextView tv_item12;
		TextView tv_item13;
		TextView tv_item14;
		TextView tv_item15;
	}
	
	
	

}
