package com.pengbo.mhdzq.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.NewsOneClassty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	public List<NewsOneClassty> datas;

	private Context context;

	public static final String TODAY = "今天";
	public static final String YESTERDAY = "昨天";

	private String date;

	private String strSet;

	private String title;

	private NewsOneClassty mClassty;

	public List<NewsOneClassty> getData() {
		return datas;
	}

	public void setData(List<NewsOneClassty> data) {
		this.datas = data;
	}

	public NewsAdapter(Context context, List<NewsOneClassty> data) {
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
			convertView = mInflater.inflate(R.layout.news_list_item, null);

			mViewHolder.mTitle = (TextView) convertView
					.findViewById(R.id.news_report_head);
			mViewHolder.mTime = (TextView) convertView
					.findViewById(R.id.news_report_time);

			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			title = "";
			date = "";
			if (datas != null && position >=0 && position < datas.size()) {
				mClassty = datas.get(position);
				if (mClassty != null) {
					title = mClassty.getTitle();
					if (title != null) {
						title = title.replace(" ", "");
					} 
					date = mClassty.getPubTime();
				}
			}
			String setTitle = ToDBC(title);
			int titleLen = title.length();
			//添加避免StringIndexOutOfBoundsException
			if (title.contains("〗")) {
				int indexA = title.indexOf("〗");
				if( indexA + 1 <= titleLen)
				{
					setTitle = title.substring(indexA + 1, titleLen);
				}
			} else if (title.contains("】")) {
				int indexB = title.indexOf("】");
				if( indexB + 1 <= titleLen)
				{
				setTitle = title.substring(indexB + 1, titleLen);
				}
			}
			mViewHolder.mTitle.setText(setTitle);
			
			strSet = date;
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		Calendar c;
		SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");

		c = Calendar.getInstance();

		c.setTime(new Date(c.getTime().getTime() - 1000 * 60 * 60 * 24));
		String yesterday = df.format(c.getTime());

		c = Calendar.getInstance();

		String today = df.format(c.getTime());

		String year = "";
		//避免StringIndexOutOfBoundsException，来自于sybstring的使用
		if (date != null) {
			int len =date.length();
			if (len >= 6) {
				year = date.substring(0, len - 6);
			}

			if (year.equals(today)) {
				if (len >= 5) {
					strSet = date.substring(len - 5, len);
				}

			} else if (year.equals(yesterday)) {
				strSet = "昨天";
			} else {
				if (len > 5) {
					int yearlen = year.length();
						if(yearlen>=5)
						{
							strSet = year.substring(yearlen - 5, yearlen);
						}
				}
			}
		} else {
			strSet = "";
		}
		mViewHolder.mTime.setText(strSet);
		return convertView;
	}

	public static class ViewHolder {

		TextView mTitle;
		TextView mTime;
	}

	/**
	 * 格式化以便内容长德时候有空格及数字会有很多间距
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

}
