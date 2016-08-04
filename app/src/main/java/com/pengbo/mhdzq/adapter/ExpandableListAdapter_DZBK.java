package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CPBMarket;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ExpandableListAdapter_DZBK extends BaseExpandableListAdapter {

	private ArrayList<CPBMarket> groupData;
	private HashMap<String, ArrayList<CCodeTableItem> > childDatas;
	private Context context;
	public boolean flag = false;

	private TextView group_more;
	
	public ExpandableListAdapter_DZBK(Activity mActivity, ArrayList<CPBMarket> group, HashMap<String, ArrayList<CCodeTableItem> > childs, boolean bflag) {
		this.context = mActivity;
		this.groupData = group;
		this.childDatas = childs;
		this.flag = bflag;
	}

	public ExpandableListAdapter_DZBK(Activity mActivity, ArrayList<CPBMarket> group, HashMap<String, ArrayList<CCodeTableItem> > childs) {
		this.context = mActivity;
		this.groupData = group;
		this.childDatas = childs;
	}
	
	public void setData(ArrayList<CPBMarket> group, HashMap<String, ArrayList<CCodeTableItem> > childs)
	{
		this.groupData = group;
		this.childDatas = childs;
	}

	/**
	 * 获取一级标签总数
	 */
	@Override
	public int getGroupCount() {
		if (groupData == null)
		{
			return 0;
		}
		return groupData.size();
	}

	/**
	 * 获取一级标签下二级标签的总数
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupData == null || getGroupCount() <= 0)
		{
			return 0;
		}else
		{
			if (getGroup(groupPosition) == null)
			{
				return 0;
			}
			
			String key = ((CPBMarket)getGroup(groupPosition)).Id;
			if (key == null || key.isEmpty())
			{
				return 0;
			}
			
			if(childDatas.get(key) == null)
			{
				return 0;
			}else
			{
				return childDatas.get(key).size();
			}
		}
	}

	/**
	 * 获取一级标签内容
	 */
	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition >= 0 && groupPosition < getGroupCount())
		{
			return groupData.get(groupPosition);
		}else
		{
			return null;
		}
		
	}

	/**
	 * 获取一级标签下二级标签的内容
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (getGroup(groupPosition) == null)
		{
			return null;
		}
		
		String key = ((CPBMarket)getGroup(groupPosition)).Id;
		if (key == null || key.isEmpty())
		{
			return null;
		}
		
		if (childDatas.get(key) != null && childPosition >= 0 && childPosition < getChildrenCount(groupPosition))
		{
			return childDatas.get(key).get(childPosition);
		}else
		{
			return null;
		}
	}

	/**
	 * 获取一级标签的ID
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * 获取二级标签的ID
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * 指定位置相应的组视图
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 对一级标签进行设置
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = (LinearLayout) LinearLayout.inflate(context,
				R.layout.zq_item_group_layout, null);
		group_more = (TextView) convertView.findViewById(R.id.group_more);
		group_more.setVisibility(View.GONE);
		
		TextView group_title = (TextView) convertView
				.findViewById(R.id.group_title);
		if (isExpanded) {
			group_title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zq_marketinfo_zhankai, 0,
					0, 0);
		} else {
			group_title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zq_marketinfo_shouqi, 0,
					0, 0);
		}
		String strTitle = "";
		int nCount = getGroupCount();
		if (getGroup(groupPosition) == null)
		{
			strTitle = "--";
		}else
		{
			strTitle = ((CPBMarket)getGroup(groupPosition)).Name + String.format("(%d/%d)", groupPosition+1, nCount);
		}
		group_title.setText(strTitle);

		return convertView;
	}

	/**
	 * 对一级标签下的二级标签进行设置
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.zq_lv_item_hangqing,null);

			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);
			viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			viewHolder.tv_zdf = (TextView) convertView.findViewById(R.id.tv_zdf);
			viewHolder.tv_zd = (TextView) convertView.findViewById(R.id.tv_zd);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CCodeTableItem codeItem = (CCodeTableItem) getChild(groupPosition, childPosition);
		if (codeItem != null)
		{
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
			viewHolder.tv_zd.setText(ViewTools.getStringByFieldID(aStockData, Global_Define.FIELD_HQ_ZD));
			viewHolder.tv_zd.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_NOW));
		}else
		{
			viewHolder.tv_name.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_code.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_price.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_zdf.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_zdf.setBackgroundResource(R.drawable.zq_zixuan_edit_gray);
			viewHolder.tv_zd.setText(Global_Define.STRING_VALUE_EMPTY);	
		}
		if(flag == true)
		{
			viewHolder.tv_zd.setVisibility(View.VISIBLE);
		}else
		{
			viewHolder.tv_zd.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	/**
	 * 当选择子节点的时候，调用该方法
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}
	
	class ViewHolder {
		TextView tv_name;
		TextView tv_code;
		TextView tv_price;
		TextView tv_zdf;
		TextView tv_zd;
	}
}
