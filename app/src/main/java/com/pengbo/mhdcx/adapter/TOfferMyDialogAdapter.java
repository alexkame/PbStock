package com.pengbo.mhdcx.adapter;

import java.util.List;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.tools.ColorConstant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TOfferMyDialogAdapter extends BaseAdapter{

	private List<TagCodeInfo> datas;
	private Context context;
	private int clickTemp = -1;
	ImageView img_save1;

	// 标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
	}

	public TOfferMyDialogAdapter(Context context, List<TagCodeInfo> datas) {
		super();
		this.datas = datas;
		this.context = context;
	}
	

	public List<TagCodeInfo> getDatas() {
		return datas;
	}

	public void setDatas(List<TagCodeInfo> datas) {
		this.datas = datas;
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
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toffer_biaodi_dialog_item, null);
		TextView tv_mydialog = (TextView) view
				.findViewById(R.id.view_mydialog_item_text);
		View mCheckView=view.findViewById(R.id.check_view);
		

		img_save1 = (ImageView) view.findViewById(R.id.imag_chose);
		TagCodeInfo tTagCodeInfo=datas.get(position);
		tv_mydialog.setText(tTagCodeInfo.name);
		if (clickTemp == position) {
			img_save1.setImageResource(R.drawable.check_point);
			//tv_mydialog.setTextColor(R.color.t_offer_1b67f2);
			//tv_mydialog.setTextColor(color.t_offer_1b67f2);
			//tv_mydialog.setTextColor(Color.BLUE);
			tv_mydialog.setTextColor(ColorConstant.COLOR_ALL_BLUE);
			
			
			mCheckView.setBackgroundResource(R.drawable.pop_unselect);
		} else {
			img_save1.setAlpha(0);
		}
		return view;
	}

}
