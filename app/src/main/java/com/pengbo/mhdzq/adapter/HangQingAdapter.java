package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.AutoScaleTextView;
import com.pengbo.mhdzq.zq_activity.ZQMarketDetailActivity;


import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * adapter to init data for listview
 * @author pobo
 */
public class HangQingAdapter extends BaseAdapter {
	private ArrayList<CCodeTableItem> listDatas;
	public Activity mActivity;
	public boolean flag = false;
	public HangQingAdapter(Activity activity, ArrayList<CCodeTableItem> datas, boolean flag) {
		super();
		listDatas = datas;
		mActivity = activity;
		this.flag = flag;
	}
	
	public HangQingAdapter(Activity activity, ArrayList<CCodeTableItem> datas) {
		super();
		listDatas = datas;
		mActivity = activity;
	}

	@Override
	public int getCount() {
		return listDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return listDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mActivity, R.layout.zq_lv_item_hangqing,null);

			viewHolder = new ViewHolder();
			viewHolder.tv_name = (AutoScaleTextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);
			viewHolder.tv_price = (AutoScaleTextView) convertView.findViewById(R.id.tv_price);
			viewHolder.tv_zdf = (TextView) convertView.findViewById(R.id.tv_zdf);
			viewHolder.tv_zd = (TextView) convertView.findViewById(R.id.tv_zd);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CCodeTableItem codeItem = listDatas.get(position);
		TagLocalStockData aStockData = new TagLocalStockData();
		MyApp.getInstance().mHQData_ZQ.getData(aStockData, codeItem.market, codeItem.code, false);

		viewHolder.tv_name.setText(codeItem.name.replaceAll(" ", ""));
		
		viewHolder.tv_code.setText(codeItem.code.replaceAll(" ", ""));
		
		viewHolder.tv_price.setText((ViewTools.getStringByFieldID(aStockData, Global_Define.FIELD_HQ_NOW)).replaceAll(" ", ""));
		viewHolder.tv_price.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_NOW));

		viewHolder.tv_zdf.setText(ViewTools.getStringByFieldID(aStockData, Global_Define.FIELD_HQ_ZDF_SIGN));
		viewHolder.tv_zdf.setTextColor(Color.rgb(255, 255, 255));
		if (aStockData.HQData.nLastPriceForCalc == 0)
		{
			if (aStockData.HQData.nLastPrice != 0)
	        {
				aStockData.HQData.nLastPriceForCalc = aStockData.HQData.nLastPrice;
	        }else
	        {
	        	aStockData.HQData.nLastPriceForCalc = aStockData.HQData.nLastClear;
	        }
		}
		int nZD = (aStockData.HQData.nLastPriceForCalc - aStockData.HQData.nLastClear);
		if (nZD > 0) {
			viewHolder.tv_zdf.setBackgroundResource(R.drawable.zq_zixuan_edit_red_zhang);
		} else if (nZD == 0) {
			viewHolder.tv_zdf.setBackgroundResource(R.drawable.zq_zixuan_edit_gray);
		} else
		{
			viewHolder.tv_zdf.setBackgroundResource(R.drawable.zq_zixuan_edit_green_die);
		}

		if(flag == true)
		{
			viewHolder.tv_zd.setVisibility(View.VISIBLE);
			viewHolder.tv_zd.setText(ViewTools.getStringByFieldID(aStockData, Global_Define.FIELD_HQ_ZD));
			viewHolder.tv_zd.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_NOW));
		}else
		{
			viewHolder.tv_zd.setVisibility(View.GONE);
		}
		return convertView;
	}


	class ViewHolder {
		AutoScaleTextView tv_name;
		TextView tv_code;
		AutoScaleTextView tv_price;
		TextView tv_zdf;
		TextView tv_zd;

	}

}
