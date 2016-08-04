package com.pengbo.mhdcx.adapter;

import java.util.List;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TradeSearchOptionListAdapter extends BaseAdapter {

	private List<TagLocalStockData> datas;

	private List<TagCodeInfo> mTagCodeInfos;// 通过条件查询把数据的TagCodeInfo的集合也传递过来

	private LayoutInflater mInflater;
	// private View mLinearLayout;
	private MyApp mMyApp;
	private Context context;

	// private OnPinnedItemClickListener mOnItemClickListener;

	private TagLocalStockData aStockData;

	public TradeSearchOptionListAdapter(MyApp myApp, Context con,
			List<TagLocalStockData> datas, List<TagCodeInfo> mTagCodeInfos) {
		super();
		this.datas = datas;
		this.mTagCodeInfos = mTagCodeInfos;
		this.mInflater = LayoutInflater.from(con);
		this.context = con;
		mMyApp = myApp;
		// selfStock=new ArrayList<TagCodeInfo>();
	}

	public List<TagLocalStockData> getDatas() {
		return datas;
	}

	public void setDatas(List<TagLocalStockData> datas) {
		this.datas = datas;
	}

	public List<TagCodeInfo> getmTagCodeInfos() {
		return mTagCodeInfos;
	}

	public void setmTagCodeInfos(List<TagCodeInfo> mTagCodeInfos) {
		this.mTagCodeInfos = mTagCodeInfos;
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
	public View getView(final int position, View convertView,
			ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.trade_item_search_optionlist, null);

				viewHolder.FIELD_HQ_NAME_ANSI = (TextView) convertView
						.findViewById(R.id.item1);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set item values to the viewHolder:

		// aStockData 合约信息
		aStockData = datas.get(position);

		// aStockInfo 合约对应的标的信息
		TagLocalStockData aStockInfo = new TagLocalStockData();

		mMyApp.mStockConfigData.search(aStockInfo,
				aStockData.optionData.StockMarket,
				aStockData.optionData.StockCode);

		viewHolder.FIELD_HQ_NAME_ANSI.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_NAME_ANSI));

		return convertView;
	}

	class ViewHolder {
		TextView FIELD_HQ_NAME_ANSI;// 期权名称

	}
}
