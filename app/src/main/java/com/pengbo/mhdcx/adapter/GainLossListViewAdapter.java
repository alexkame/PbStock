package com.pengbo.mhdcx.adapter;
import java.util.List;
import com.pengbo.mhdzq.data.TagProfitRecord;
import com.pengbo.mhdzq.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GainLossListViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<TagProfitRecord> lists;
	
	
	public List<TagProfitRecord> getLists() {
		return lists;
	}

	public void setLists(List<TagProfitRecord> lists) {
		this.lists = lists;
	}

	public GainLossListViewAdapter(Context mContext, List<TagProfitRecord> lists) {
		super();
		this.mContext = mContext;
		this.lists = lists;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.gain_loss_analysis_listview_item, null);
		TextView tv_1=(TextView) view.findViewById(R.id.tv_gain_loss_analysis_tv1);
		TextView tv_2=(TextView) view.findViewById(R.id.tv_gain_loss_analysis_tv2);
		TextView tv_3=(TextView) view.findViewById(R.id.tv_gain_loss_analysis_tv3);
		TextView tv_4=(TextView) view.findViewById(R.id.tv_gain_loss_analysis_tv4);
		TagProfitRecord tag=lists.get(position);
		
		tv_1.setText(String.format("%.2f", tag.price));
		tv_2.setText(String.format("%.2f%s", tag.syl * 100, "%"));
		tv_3.setText(String.format("%.2f", tag.sy));
		tv_4.setText(String.format("%.2f%s", tag.rate * 100, "%"));
		
		return view;
		
	}

}
