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
 * 历史成交list中的listView 适配器 
 * @author pobo
 *
 */
public class TradeLSCJListAdapter extends BaseAdapter {
	
	private PBSTEP mListData;
	private Context context;
	private DisplayMetrics mScreenSize;


	public TradeLSCJListAdapter(Context context, PBSTEP datas) {
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
				convertView = mInflater.inflate(R.layout.hv_lscj_item_data, null);

				viewHolder.tv_item1 = (TextView) convertView.findViewById(R.id.hv_lscj_item1);
				LayoutParams lp = viewHolder.tv_item1.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item1.setLayoutParams(lp);
				
				viewHolder.tv_item2 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item2);
				lp = viewHolder.tv_item2.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item2.setLayoutParams(lp);
				
				viewHolder.tv_item3 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item3);
				lp = viewHolder.tv_item3.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item3.setLayoutParams(lp);
				
				viewHolder.tv_item4 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item4);
				lp = viewHolder.tv_item4.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item4.setLayoutParams(lp);
				
				viewHolder.tv_item5 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item5);
				lp = viewHolder.tv_item5.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item5.setLayoutParams(lp);
				
				viewHolder.tv_item6 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item6);
				lp = viewHolder.tv_item6.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item6.setLayoutParams(lp);
				
				viewHolder.tv_item7 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item7);
				lp = viewHolder.tv_item7.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item7.setLayoutParams(lp);
				
				viewHolder.tv_item8 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item8);
				lp = viewHolder.tv_item8.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item8.setLayoutParams(lp);
				
				viewHolder.tv_item9 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item9);
				lp = viewHolder.tv_item9.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item9.setLayoutParams(lp);
				
				viewHolder.tv_item10 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item10);
				lp = viewHolder.tv_item10.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item10.setLayoutParams(lp);
				
				viewHolder.tv_item11 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item11);
				lp = viewHolder.tv_item11.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item11.setLayoutParams(lp);
				
				viewHolder.tv_item12 = (TextView) convertView
						.findViewById(R.id.hv_lscj_item12);
				lp = viewHolder.tv_item12.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item12.setLayoutParams(lp);
				
				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String strTemp = "";
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJRQ);
			viewHolder.tv_item1.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJSJ);
			viewHolder.tv_item2.setText(strTemp);
			//合约编码
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			viewHolder.tv_item3.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			viewHolder.tv_item4.setText(strTemp);
			//买卖
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_MMLBMC);
			viewHolder.tv_item5.setText(strTemp);
			
			//开平
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_KPBZMC);
			viewHolder.tv_item6.setText(strTemp);

			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJJG);
			viewHolder.tv_item7.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJSL);
			viewHolder.tv_item8.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJJE);
			viewHolder.tv_item9.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_BDBZMC);
			viewHolder.tv_item10.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_CJBH);
			viewHolder.tv_item11.setText(strTemp);
			
			strTemp = mListData.GetFieldValueString(STEP_Define.STEP_MMLBSM);
			viewHolder.tv_item12.setText(strTemp);
		}
		
		return convertView;
	}
	
	public static class ViewHolder {

		TextView tv_item1;
		TextView tv_item2;
		TextView tv_item3;
		TextView tv_item4;
		TextView tv_item5;
		TextView tv_item6;
		TextView tv_item7;
		TextView tv_item8;
		TextView tv_item9;
		TextView tv_item10;
		TextView tv_item11;
		TextView tv_item12;
	}
	
	
	

}
