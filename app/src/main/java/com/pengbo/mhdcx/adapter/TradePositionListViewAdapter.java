package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.bean.Option;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 交易持仓中的listView 适配器 
 * @author pobo
 *
 */
public class TradePositionListViewAdapter extends BaseAdapter {
	
	public ArrayList<Option> mDatas;	
	public Context context;
    
	public List<Option> getDatas() {
		return mDatas;
	}

	public void setDatas(ArrayList<Option> datas) {
		this.mDatas = datas;
	}

	public TradePositionListViewAdapter(Context context, ArrayList<Option> datas) {
		this.context = context;
		this.mDatas = datas;
	}
	
	
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		if(null == convertView){
			synchronized (this) {

				viewHolder = new ViewHolder();
				LayoutInflater mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(
						R.layout.trade_position_listview_item, null);

				//viewHolder.image_position_quanliyiwu = (ImageView) convertView
				//		.findViewById(R.id.image_position_quanliyiwu);
				
				viewHolder.tv_position_name = (TextView) convertView
						.findViewById(R.id.tv_position_name);
				
				viewHolder.tv_position = (TextView) convertView
						.findViewById(R.id.tv_position);
				
				viewHolder.tv_canuse = (TextView) convertView
						.findViewById(R.id.tv_canuse);
				
				viewHolder.tv_average = (TextView) convertView
						.findViewById(R.id.tv_average);
				
				viewHolder.tv_nowprice = (TextView) convertView
						.findViewById(R.id.tv_nowprice);
				
				viewHolder.tv_fudongyingkui_up = (TextView) convertView
						.findViewById(R.id.tv_fudongyingkui_up);
				
				viewHolder.tv_fudongyingkui_down = (TextView) convertView
						.findViewById(R.id.tv_fudongyingkui_down);

				viewHolder.image_position_qi = (ImageView) convertView
						.findViewById(R.id.image_position_qi);
				
				viewHolder.tv_position_date = (TextView) convertView
						.findViewById(R.id.tv_position_date);
				
				viewHolder.tv_position_lastdays = (TextView) convertView
						.findViewById(R.id.tv_position_lastdays);

				viewHolder.image_position_bao = (ImageView) convertView
						.findViewById(R.id.image_position_bao);
				
				viewHolder.tv_position_bao = (TextView) convertView
						.findViewById(R.id.tv_position_bao);
				
				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Option option = mDatas.get(position);
		
		//viewHolder.image_position_quanliyiwu.setBackgroundResource(option.getImage_one());
		viewHolder.tv_position_name.setText(option.getMname()); 
		Drawable drawable = context.getResources().getDrawable(option.getImage_one());
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
		viewHolder.tv_position_name.setCompoundDrawables(drawable, null, null, null);
		
		viewHolder.tv_position.setText(option.getMchicang());
		viewHolder.tv_canuse.setText(option.getMcangcha());
		viewHolder.tv_average.setText(option.getAverateprice());
		viewHolder.tv_nowprice.setText(option.getMnownum());
		viewHolder.tv_fudongyingkui_up.setText(option.getFudongyk());
		viewHolder.tv_fudongyingkui_down.setText(option.getFudongyklv());
		int fudongcolor = ViewTools.getColor(STD.StringToValue(option.getFudongyk().toString()));
		viewHolder.tv_fudongyingkui_up.setTextColor(fudongcolor);
		viewHolder.tv_fudongyingkui_down.setTextColor(fudongcolor);
		
		viewHolder.image_position_qi.setBackgroundResource(option.getImage_two());
		viewHolder.tv_position_date.setText(option.getmDueTime());
		
		if(option.getDays()<=7){
			viewHolder.tv_position_lastdays.setTextColor(ColorConstant.COLOR_ALL_RED);	
		}else{
			viewHolder.tv_position_lastdays.setTextColor(ColorConstant.COLOR_ALL_BLUE);
		}
		viewHolder.tv_position_lastdays.setText(option.getMoldtime());

		
		if (option.getMMBZ() || option.getBDBZ())//买是权利，不需要保证金
		{
			viewHolder.image_position_bao.setVisibility(View.GONE);
			viewHolder.tv_position_bao.setVisibility(View.GONE);
		}else
		{
			viewHolder.image_position_bao.setVisibility(View.VISIBLE);
			viewHolder.tv_position_bao.setVisibility(View.VISIBLE);
			viewHolder.image_position_bao.setBackgroundResource(option.getImage_three());
			viewHolder.tv_position_bao.setText(option.getBaoZJ());
		}
		
		return convertView;
	}
	
	
	
	public static class ViewHolder {
		
		ImageView image_position_quanliyiwu; // 权利义务图标
		TextView tv_position_name;	//期权名称	
		TextView tv_position; //持仓
		TextView tv_canuse; // 可用
		TextView tv_average; //均价
		TextView tv_nowprice; //现价
		TextView tv_fudongyingkui_up; // 浮动盈亏
		TextView tv_fudongyingkui_down; //浮动盈亏比例
		ImageView image_position_qi; //到期日图标
		TextView tv_position_date; //到期日日期
		TextView tv_position_lastdays; //到期日剩余天数
		ImageView image_position_bao; //保证金/备 图标
		TextView tv_position_bao; //保证金数目
	}
	
	
	

}
