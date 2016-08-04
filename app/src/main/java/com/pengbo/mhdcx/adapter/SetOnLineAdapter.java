package com.pengbo.mhdcx.adapter;

import java.util.List;

import com.pengbo.mhdzq.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetOnLineAdapter extends BaseAdapter{
	private List<String> datas;
	private Context context;
	ImageView img_save1;
	
	
	private int clickTemp = -1;

	// 标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
	}
	
	public SetOnLineAdapter(List<String> datas,Context con) {
		this.datas=datas;
		this.context=con;
	}
	public List<String> getDatas() {
		return datas;
	}
	public void setDatas(List<String> datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=View.inflate(context, R.layout.set_online_listview_item, null);
		TextView tv=(TextView) view.findViewById(R.id.time);
		img_save1=(ImageView) view.findViewById(R.id.imag_chose);
		tv.setText(datas.get(position).toString());		
		
		
		if(clickTemp==position){
			img_save1.setBackgroundResource(R.drawable.save02);
		}else{
			img_save1.setAlpha(0);
		}
		return view;
	}

}

