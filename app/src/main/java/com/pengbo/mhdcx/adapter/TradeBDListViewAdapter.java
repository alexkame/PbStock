package com.pengbo.mhdcx.adapter;

import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 备兑listView 适配器 
 * @author pobo
 *
 */
public class TradeBDListViewAdapter extends BaseAdapter {
	
	private PBSTEP mListData;
	private Context context;
	private boolean mIsCCOrWT; //true-CC,false-WT


	public TradeBDListViewAdapter(Context context, PBSTEP datas, boolean bCCOrWT) {
		this.context = context;
		this.mListData = datas;
		this.mIsCCOrWT = bCCOrWT;
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
						R.layout.lv_bd_item_data, null);

				viewHolder.tv_item1 = (TextView) convertView
						.findViewById(R.id.lv_bd_item1);
				
				viewHolder.tv_item2 = (TextView) convertView
						.findViewById(R.id.lv_bd_item2);
				
				viewHolder.tv_item3 = (TextView) convertView
						.findViewById(R.id.lv_bd_item3);
				
				viewHolder.tv_item4 = (TextView) convertView
						.findViewById(R.id.lv_bd_item4);
				
				viewHolder.tv_item5 = (TextView) convertView
						.findViewById(R.id.lv_bd_item5);

				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			String stockCode = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			
			if(mIsCCOrWT)
			{
				viewHolder.tv_item5.setVisibility(View.GONE);
				String stockName = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);
				String ksdsl = mListData.GetFieldValueString(STEP_Define.STEP_KSDSL);
				String ysdsl = mListData.GetFieldValueString(STEP_Define.STEP_KJSSL);
				viewHolder.tv_item1.setText(stockCode);
				viewHolder.tv_item2.setText(stockName);
				viewHolder.tv_item3.setText(ksdsl);
				viewHolder.tv_item4.setText(ysdsl);

			}else
			{
				String wtsj = mListData.GetFieldValueString(STEP_Define.STEP_WTSJ);
				String sqlb = mListData.GetFieldValueString(STEP_Define.STEP_FJYSQLB);
				String sqlbmc = mListData.GetFieldValueString(STEP_Define.STEP_FJYSQLBMC);
				String wtsl = mListData.GetFieldValueString(STEP_Define.STEP_WTSL);
				String bz = mListData.GetFieldValueString(STEP_Define.STEP_WTZTMC);
				viewHolder.tv_item1.setText(wtsj);
				viewHolder.tv_item2.setText(stockCode);
				
				if(sqlbmc.isEmpty())
				{
					if(sqlb.equals(String.format("%c", PTK_Define.PTK_FJYLB_SD)))
					{
						sqlb = "锁定";
					}else if(sqlb.equals(String.format("%c", PTK_Define.PTK_FJYLB_JS)))
					{
						sqlb = "解锁";
					}else
					{
						sqlb = "";
					}
				}else
				{
					sqlb = sqlbmc;
				}
						
				viewHolder.tv_item3.setText(sqlb);
				viewHolder.tv_item4.setText(wtsl);
				viewHolder.tv_item5.setText(bz);
				viewHolder.tv_item5.setVisibility(View.VISIBLE);
			}
		}
		
		return convertView;
	}
	
	public static class ViewHolder {

		TextView tv_item1;
		TextView tv_item2; 
		TextView tv_item3; 
		TextView tv_item4; 
		TextView tv_item5; 
	}
}
