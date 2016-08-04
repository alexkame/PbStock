package com.pengbo.mhdcx.adapter;


import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BankQueryListAdapter extends BaseAdapter {

	private PBSTEP mLSDatas;

	private LayoutInflater mInflater;

	public BankQueryListAdapter(Context context, PBSTEP datas) {
		super();
		this.mLSDatas = datas;
		this.mInflater = LayoutInflater.from(context);
	}
	

	public PBSTEP getDatas() {
		return mLSDatas;
	}

	public void setDatas(PBSTEP datas) {
		this.mLSDatas = datas;
	}

	@Override
	public int getCount() {
		return mLSDatas.GetRecNum();
	}

	@Override
	public Object getItem(int pos) {
		mLSDatas.GotoRecNo(pos);
		return mLSDatas;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				convertView = mInflater.inflate(R.layout.bank_query_listview_item, null);
				viewHolder = new ViewHolder();

				viewHolder.mBankName = (TextView) convertView.findViewById(R.id.tv_bankname);
				viewHolder.mZZJE = (TextView) convertView.findViewById(R.id.tv_zzje);
				viewHolder.mYHYE = (TextView) convertView.findViewById(R.id.tv_yhye);
				viewHolder.mZZLX = (TextView) convertView.findViewById(R.id.tv_zzlx);
				viewHolder.mZZZT = (TextView) convertView.findViewById(R.id.tv_zzzt);
				
				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set item values to the viewHolder:

		if (position < mLSDatas.GetRecNum())
		{
			mLSDatas.GotoRecNo(position);
			String bankName = mLSDatas.GetFieldValueString(STEP_Define.STEP_YHMC);
			viewHolder.mBankName.setText(bankName);
			
			String zzje = mLSDatas.GetFieldValueString(STEP_Define.STEP_ZZJE);
			viewHolder.mZZJE.setText(zzje);
			
			String yhye = mLSDatas.GetFieldValueString(STEP_Define.STEP_YHYE);
			viewHolder.mYHYE.setText(yhye);
			
			String zzlx = mLSDatas.GetFieldValueString(STEP_Define.STEP_YZYWSM);
			viewHolder.mZZLX.setText(zzlx);
			
			String zzzt = mLSDatas.GetFieldValueString(STEP_Define.STEP_YZYWZTSM);
			viewHolder.mZZZT.setText(zzzt);
		}else
		{
			viewHolder.mBankName.setText("");
			viewHolder.mZZJE.setText("");
			viewHolder.mYHYE.setText("");
			viewHolder.mZZLX.setText("");
			viewHolder.mZZZT.setText("");
		}
		
		return convertView;
	}

	class ViewHolder {
		TextView mBankName;
		TextView mZZJE;
		TextView mYHYE;
		TextView mZZLX;
		TextView mZZZT;
	}
}
