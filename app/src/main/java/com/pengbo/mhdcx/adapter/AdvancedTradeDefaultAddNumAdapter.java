package com.pengbo.mhdcx.adapter;

import java.util.List;

import com.pengbo.mhdzq.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AdvancedTradeDefaultAddNumAdapter extends BaseAdapter{
	private List<Integer> datas;
	private Context context;
	ImageView img_save1;
	
	
	private int clickTemp = -1;

	// 标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
	}
	
	public AdvancedTradeDefaultAddNumAdapter(List<Integer> datas,Context con) {
		this.datas=datas;
		this.context=con;
	}
	public List<Integer> getDatas() {
		return datas;
	}
	public void setDatas(List<Integer> datas) {
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

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=View.inflate(context, R.layout.setting_advanced_btn_array_item, null);
		ImageView img=(ImageView) view.findViewById(R.id.buy_sell_image);
		img_save1=(ImageView) view.findViewById(R.id.set_advanced_imag_chose);	
		img.setBackgroundResource(datas.get(position));	
		if(clickTemp==position){
			img_save1.setBackgroundResource(R.drawable.save02);
		}else{
			img_save1.setAlpha(0);
		}
		return view;
	}

}

