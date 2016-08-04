package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqGDZHAdapter.ViewHolder;
import com.pengbo.mhdzq.data.CPbGDZHData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ZqShijiaAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	
	private ArrayList<String> mShijiaList;
	
	public ZqShijiaAdapter(Context context,ArrayList<String> shijialist)
	{
		super();
		
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
		mShijiaList = shijialist;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mShijiaList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mShijiaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.zq_sj_list_item, null);

				viewHolder.FIELD_SJ = (TextView) convertView
						.findViewById(R.id.zq_sj);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String sj = mShijiaList.get(position);

		viewHolder.FIELD_SJ.setText(sj);
		return convertView;
	}
	
	class ViewHolder {
		TextView FIELD_SJ;// 市价类型
	}

}
