package com.pengbo.mhdzq.adapter;

import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.NewsTitle;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.widget.HorizontalListView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NewsClassifyHLVAdapter extends BaseAdapter {

	private List<NewsTitle> datas;
	private Context mContext;
	private DisplayMetrics mScreenSize;
	private LayoutInflater mInfalter;
	private int selectIndex = 0;

	// 标识选择的Item
	public void setSelectIndex(int i) {
		selectIndex = i;
	}

	public NewsClassifyHLVAdapter(Context con, List<NewsTitle> datas, HorizontalListView mhorz) {
		super();
		this.datas = datas;
		this.mContext = con;
		mScreenSize = ViewTools.getScreenSize(con);
		this.mInfalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	public  void setNewsTitles(List<NewsTitle> data){
		if(null == data || data.isEmpty()){
			return ;
		}
		this.datas =data;
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInfalter.inflate(R.layout.view_hlv_item, null);
			holder.mhlv_tv = (TextView) convertView.findViewById(R.id.hlv_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.mhlv_tv.setTextAppearance(mContext, R.style.top_category_scroll_view_item_text);
		// holder.mhlv_tv.setBackgroundResource(R.drawable.radio_buttong_bg);
		// holder.mhlv_tv.setTextColor(mContext.getResources().getColorStateList(R.color.top_category_scroll_text_color_day));

		LayoutParams lp = holder.mhlv_tv.getLayoutParams();
		lp.width = mScreenSize.widthPixels * 1 / 4;
		holder.mhlv_tv.setText(datas.get(position).getTitle());

		if (position == selectIndex) {
			int id = R.drawable.zq_img41_down_line_yellow;
			Drawable drawable = mContext.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
			holder.mhlv_tv.setCompoundDrawables(null, null, null, drawable);
			holder.mhlv_tv.setTextColor(ColorConstant.ZQ_COLOR_TIME);

		} else {
			holder.mhlv_tv.setCompoundDrawables(null, null, null, null);
			holder.mhlv_tv.setTextColor(ColorConstant.COLOR_BLACK_X);
		}

		return convertView;
	}

	private static class ViewHolder {

		private TextView mhlv_tv;
	}

}
