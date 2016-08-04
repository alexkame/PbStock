package com.pengbo.mhdzq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pengbo.mhdzq.R;
/**
 * 详细页面中点击一分钟线时 弹出的PopWindow 上展示的数据用 listview 对应的   Adapter 
 * 
 * @author pobo
 * @date   2015-11-2 上午10:08:20
 * @className KLinePopWindowAdapter.java
 * @verson 1.0.0
 */
public class KLinePopWindowAdapter extends BaseAdapter {

	private String[] datas;
	private Context context;

	public KLinePopWindowAdapter(Context context, String[] strs) {
		this.context = context;
		this.datas = strs;
	}

	@Override
	public int getCount() {
		return datas.length;
	}

	@Override
	public Object getItem(int position) {
		return datas[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.zq_market_detail_kline_more_popwindow_item, null);
		TextView pop_item = (TextView) view.findViewById(R.id.tv_zq_detail_kline_popwindow_item);
		pop_item.setText(datas[position]);
		return view;
	}
}
