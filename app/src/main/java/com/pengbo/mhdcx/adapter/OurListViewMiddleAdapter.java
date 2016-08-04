/**
 * 
 */
package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.tools.ViewTools;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author
 * 
 */
public class OurListViewMiddleAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> list;
	private LayoutInflater mInflater;
	private DisplayMetrics mScreenSize;
	private int mPosition;

	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public OurListViewMiddleAdapter(Context context, ArrayList<String> list) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.mScreenSize = ViewTools.getScreenSize(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.our_listview_middle,
						null);
				viewHolder.tv_xqj = (TextView) convertView
						.findViewById(R.id.leftTv1);
				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position >= 0 && position < list.size() && position == mPosition) {
			convertView.setBackgroundResource(R.drawable.t_offer_beside_xingquanjia);
		} else {
			convertView.setBackgroundResource(R.color.t_offer_cfd0d1);
		}
		viewHolder.tv_xqj.setText(list.get(position));

		return convertView;
	}

	class ViewHolder {
		TextView tv_xqj;// 行权价
	}

}