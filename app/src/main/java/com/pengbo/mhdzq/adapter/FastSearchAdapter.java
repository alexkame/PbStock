package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.SearchDataItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class FastSearchAdapter extends BaseAdapter implements Filterable {

	private ArrayList<SearchDataItem> datas1, datas2;
	private LayoutInflater mInflater;
	private MyApp mMyApp;

	private Context context;
	private LayoutInflater inflater;

	private ArrayFilter mFilter;

	private final int MAX_COUNT_SEARCH_RESULT = 5;

	public FastSearchAdapter(MyApp myApp, Context con,
			ArrayList<SearchDataItem> list1, ArrayList<SearchDataItem> list2) {
		super();
		this.datas1 = list1;
		this.datas2 = list2;

		this.mInflater = LayoutInflater.from(con);
		this.context = con;
		this.mMyApp = myApp;
	}

	public void setDatas(ArrayList<SearchDataItem> list) {
		this.datas1 = list;
	}

	@Override
	public int getCount() {
		return datas1.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas1.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.zq_fast_search_listview_item, null);

				viewHolder.FIELD_HQ_NAME_ANSI = (TextView) convertView
						.findViewById(R.id.zq_fast_search_item_name);
				viewHolder.FIELD_HQ_CODE = (TextView) convertView
						.findViewById(R.id.zq_fast_search_item_code);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position < datas1.size()) {
			SearchDataItem item = datas1.get(position);

			viewHolder.FIELD_HQ_NAME_ANSI.setText(item.name);
			viewHolder.FIELD_HQ_CODE.setText(item.code);
		}
		return convertView;
	}

	class ViewHolder {
		TextView FIELD_HQ_NAME_ANSI;// 期权名称
		TextView FIELD_HQ_CODE;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {
			mFilter = new ArrayFilter(this.datas1, this.datas2);
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {

		private ArrayList<SearchDataItem> mSearchResultList, mSearchStockList;

		public ArrayFilter(ArrayList<SearchDataItem> datas1,
				ArrayList<SearchDataItem> datas2) {
			mSearchResultList = new ArrayList<SearchDataItem>();
			mSearchStockList = datas2;

		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {

			FilterResults results = new FilterResults();

			if (prefix == null) {
				results.values = null;
				results.count = 0;
				return results;
			}

			doSearch(prefix.toString());

			synchronized (this) {
				ArrayList<SearchDataItem> values = new ArrayList<SearchDataItem>();

				for (int i = 0; i < mSearchResultList.size(); i++) {
					values.add(mSearchResultList.get(i));
				}

				results.values = values;
				results.count = values.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// noinspection unchecked
			synchronized (this) {
				if (results.values != null) {
					ArrayList<SearchDataItem> values = (ArrayList<SearchDataItem>) results.values;
					datas1.clear();
					for (int i = 0; i < values.size(); i++) {
						datas1.add(values.get(i));
					}
				}
			}
			if (datas1.size() > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

		private void doSearch(String searchText) {
			mSearchResultList.clear();
			if (searchText.length() == 0) {
				notifyDataSetChanged();
				return;
			}
			// this.mListView_history.setVisibility(View.GONE);
			// this.mTVClearHistory.setVisibility(View.GONE);
			// this.mTVHistoryTitle.setVisibility(View.GONE);

			String keyStr = "";
			if (mSearchStockList == null)
				return;

			for (int i = 0; i < this.mSearchStockList.size(); i++) {
				SearchDataItem item = mSearchStockList.get(i);
				char ch = searchText.charAt(0);// 取出第一个字符
				if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {

					keyStr = item.code;
					if (keyStr.toUpperCase().startsWith(
							searchText.toUpperCase())
							|| keyStr.toUpperCase().endsWith(
									searchText.toUpperCase())) {
						mSearchResultList.add(item);
						continue;
					}

					keyStr = item.extcode;
					if (keyStr.toUpperCase().startsWith(
							searchText.toUpperCase())
							|| keyStr.toUpperCase().endsWith(
									searchText.toUpperCase())) {
						mSearchResultList.add(item);
						continue;
					}

					keyStr = item.jianpin;
					if (keyStr.toUpperCase().contains(searchText.toUpperCase())) {
						mSearchResultList.add(item);
					}
				} else if (ch >= '0' && ch <= '9') {
					keyStr = item.code;
					if (keyStr.startsWith(searchText)
							|| keyStr.endsWith(searchText)) {
						mSearchResultList.add(item);
					}
				} else {
					keyStr = item.name;
					if (keyStr.contains(searchText)) {
						mSearchResultList.add(item);
					}
				}
				if (mSearchResultList.size() >= MAX_COUNT_SEARCH_RESULT) {
					break;
				}
			}
		}

	}

}
