package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * adapter to init data for listview
 * 
 * @author pobo
 */
public class ListViewAdapter extends BaseAdapter {
	public ArrayList<String> tvList;
	public Activity mActivity;

	public ListViewAdapter(Activity mActivity, ArrayList<String> tvList) {
		super();
		this.tvList = tvList;
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		return tvList.size();
	}

	@Override
	public String getItem(int position) {
		return tvList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mActivity,
					R.layout.zq_listview_homepager, null);

			holder = new ViewHolder();
			holder.tv_news = (TextView) convertView.findViewById(R.id.tv_news);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_news.setText(getItem(position));

		if (position == 0) {
			holder.tv_news.setText("习大大开始对美国事访问");
		}
		if (position == 1) {
			holder.tv_news.setText("沪指大幅低开1.5%");
		}
		if (position == 2) {
			holder.tv_news.setText("传说中信涉嫌利用救市措施牟利");
		}
		if (position == 3) {
			holder.tv_news.setText("习大大开始对美国事访问");
		}

		holder.tv_news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mActivity, "什么什么什么什么鬼", 0).show();
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView tv_news;
	}

}
