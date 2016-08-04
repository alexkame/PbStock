package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.CPBMarket;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;


public class DZBKEditFixedListAdapter extends BaseAdapter {

	private ArrayList<CPBMarket> datas;

	private LayoutInflater mInflater;

	private Context context;

	public DZBKEditFixedListAdapter(Context con, ArrayList<CPBMarket> datas) {
		super();
		this.datas = datas;

		this.mInflater = LayoutInflater.from(con);
		this.context = con;
	}


	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parentView) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_home_fixed, null);

				
				//viewHolder.img_market = (ImageView) convertView.findViewById(R.id.img_market);
				viewHolder.tv_market_name = (TextView) convertView.findViewById(R.id.tv_market_name);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		convertView.setEnabled(false);
		// set item values to the viewHolder:
		CPBMarket item = datas.get(position);
		int resID = context.getResources().getIdentifier(item.NormalIcon, "drawable", context.getPackageName());
		//viewHolder.img_market.setImageResource(resID);
		viewHolder.tv_market_name.setText(item.Name);
		viewHolder.tv_market_name.setEnabled(false);
		
		int id = R.drawable.img24_updown;
		Drawable drawable = context.getResources().getDrawable(resID);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight()); // 设置边界
		viewHolder.tv_market_name.setCompoundDrawables(drawable, null, null,
				null);
		viewHolder.tv_market_name.setCompoundDrawablePadding(45);
		return convertView;
	}

	class ViewHolder {

		TextView tv_market_name;
		ImageView img_market;
	}

}
