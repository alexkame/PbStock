package com.pengbo.mhdcx.adapter;

import java.util.List;

import com.pengbo.mhdcx.bean.MyGridView;
import com.pengbo.mhdzq.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * ScreenActivity -->CorrectHotOptionActivity -->six gridview button
 * @author pobo
 *
 */
public class CorrectHotGridViewAdapter extends BaseAdapter {
	private List<MyGridView> myGridViews;
	private Context context;

	private int clickTemp = -1;

	// 标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
	}

	public CorrectHotGridViewAdapter(List<MyGridView> myGridViews, Context context) {
		this.myGridViews = myGridViews;
		this.context = context;
	}

	@Override
	public int getCount() {
		return myGridViews.size();
	}

	@Override
	public Object getItem(int position) {
		return myGridViews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.correcthot_gridview, null);
		TextView up_text = (TextView) v.findViewById(R.id.gv_textview_up);
		TextView down_text = (TextView) v.findViewById(R.id.gv_textview_down);
		
		MyGridView my = myGridViews.get(position);
		up_text.setText(my.getUptext());
		down_text.setText(my.getDowntext());
		
		if(clickTemp==position){
			v.setBackgroundResource(R.drawable.xuanxiang02);
		}else{
			v.setBackgroundResource(R.drawable.xuanxiang01);
		}
		return v;
	}

}
