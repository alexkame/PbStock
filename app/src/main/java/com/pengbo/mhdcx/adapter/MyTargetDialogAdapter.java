package com.pengbo.mhdcx.adapter;

import java.util.List;

import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyTargetDialogAdapter extends BaseAdapter {
	private List<TagCodeInfo> datas;	
	private Context context;
	public MyTargetDialogAdapter( Context context,List<TagCodeInfo>data) {
		this.context=context;
		this.datas=data;
	}
	
	
	public List<TagCodeInfo> getDatas() {
		return datas;
	}

	public void setDatas(List<TagCodeInfo> datas) {
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
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.view_mydialog_item, null);
		TextView tv_mydialog=(TextView) view.findViewById(R.id.view_mydialog_item_text);
		TagCodeInfo tTagCodeInfo=datas.get(position);
		tv_mydialog.setText(tTagCodeInfo.name);
		return view;
	}
}
