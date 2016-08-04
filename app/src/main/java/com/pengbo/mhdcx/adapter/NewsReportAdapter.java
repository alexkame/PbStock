package com.pengbo.mhdcx.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pengbo.mhdcx.bean.News;
import com.pengbo.mhdzq.R;

/**
 * 新闻 公告 和 研究 报告 适配器
 * 
 * @author pobo
 * 
 */
public class NewsReportAdapter extends BaseAdapter {

	public List<News> datas;

	private Context context;

	public List<News> getData() {
		return datas;
	}

	public void setData(List<News> data) {
		this.datas = data;
	}

	public NewsReportAdapter(Context context, List<News> data) {
		this.datas = data;
		this.context = context;
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
		ViewHolder mViewHolder = null;

		if (null == convertView) {
			mViewHolder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.news_report_list_item,
					null);

			mViewHolder.mTitle = (TextView) convertView
					.findViewById(R.id.news_report_head);
			

			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		// set item values to the viewHolder:

		News mNews = datas.get(position);
		mViewHolder.mTitle.setText(mNews.Title.toString());		
		return convertView;
	}

	public static class ViewHolder {
		TextView mTitle;		
	}

}
