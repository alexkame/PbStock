package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CPBMarket;
import com.pengbo.mhdzq.data.DBZQHomeActivityMarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DrawerListAdapter extends BaseAdapter {

	private ArrayList<DBZQHomeActivityMarket> mListData;
	private LayoutInflater mInflater;
	private LinearLayout layout_item;
	private TextView tvName;
	private ImageView ivIcon;
	private final Context context;
	private int mPage;

	public DrawerListAdapter(Context context,
			ArrayList<DBZQHomeActivityMarket> datas, int page) {
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListData = new ArrayList<DBZQHomeActivityMarket>();
		int i = page * AppConstants.HOME_PAGE_ITEM_SIZE;
		int iEnd = i + AppConstants.HOME_PAGE_ITEM_SIZE;
		while ((i < datas.size()) && (i < iEnd)) {
			mListData.add(datas.get(i));
			i++;
		}

		// 当前页未满，在最后添加自定义按钮
		if (mListData.size() < AppConstants.HOME_PAGE_ITEM_SIZE) {
			DBZQHomeActivityMarket market = new DBZQHomeActivityMarket();
			market.Name = "自定义";
			market.NormalIcon = "img17_add_big";
			mListData.add(market);
		}
		this.mPage = page;
	}

	public void setDatas(ArrayList<DBZQHomeActivityMarket> datas, int page) {
		mListData = new ArrayList<DBZQHomeActivityMarket>();
		int i = page * AppConstants.HOME_PAGE_ITEM_SIZE;
		int iEnd = i + AppConstants.HOME_PAGE_ITEM_SIZE;
		while ((i < datas.size()) && (i < iEnd)) {
			mListData.add(datas.get(i));
			i++;
		}

		// 当前页未满，在最后添加自定义按钮
		if (mListData.size() < AppConstants.HOME_PAGE_ITEM_SIZE) {
			DBZQHomeActivityMarket market = new DBZQHomeActivityMarket();
			market.Name = "自定义";
			market.NormalIcon = "img17_add_big";
			mListData.add(market);
		}
		this.mPage = page;
	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	@Override
	public DBZQHomeActivityMarket getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parentView) {
		DBZQHomeActivityMarket item = mListData.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.zq_home_gridview_item,
					null);
			layout_item = (LinearLayout) convertView
					.findViewById(R.id.llayout_home_gridview_item);
			ivIcon = (ImageView) convertView.findViewById(R.id.img_icon);
			tvName = (TextView) convertView.findViewById(R.id.tv_name);

			convertView.setTag(layout_item);
		} else {
			layout_item = (LinearLayout) convertView.getTag();
		}

		int resID = context.getResources().getIdentifier(item.NormalIcon,
				"drawable", context.getPackageName());
		ivIcon.setImageResource(resID);
		tvName.setText(item.Name);
		return convertView;
	}
}
