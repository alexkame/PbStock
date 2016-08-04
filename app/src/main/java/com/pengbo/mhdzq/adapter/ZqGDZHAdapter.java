package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.FastSearchAdapter.ViewHolder;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CPbGDZHData;
import com.pengbo.mhdzq.data.SearchDataItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ZqGDZHAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private MyApp mMyApp;
	private Context context;

	private ArrayList<CPbGDZHData> mGdList;

	public ZqGDZHAdapter(MyApp myApp, Context con, ArrayList<CPbGDZHData> list1) {
		super();

		this.mInflater = LayoutInflater.from(con);
		this.context = con;
		this.mMyApp = myApp;
		mGdList = list1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mGdList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mGdList.get(position);
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
						R.layout.zq_gdzh_list_item, null);

				viewHolder.FIELD_GDZH = (TextView) convertView
						.findViewById(R.id.zq_guzh);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CPbGDZHData gddata = mGdList.get(position);

		viewHolder.FIELD_GDZH.setText(gddata.mGdzh);
		return convertView;
	}

	class ViewHolder {
		TextView FIELD_GDZH;// 股东帐户
	}

}
