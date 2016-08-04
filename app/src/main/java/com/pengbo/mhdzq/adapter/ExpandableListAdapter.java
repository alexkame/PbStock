package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TopRankField;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.TopRankData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.zq_activity.PaiMingMoreActivity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private ArrayList<TopRankField> groupData;
	private HashMap<Short, ArrayList<TopRankData> > childDatas;
	private Context context;

	private TextView group_more;

	public ExpandableListAdapter(Activity mActivity, ArrayList<TopRankField> group, HashMap<Short, ArrayList<TopRankData> > childs) {
		this.context = mActivity;
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
			short key = ((TopRankField)getGroup(groupPosition)).id;
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
		
		short key = ((TopRankField)getGroup(groupPosition)).id;
		
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
		group_more.setOnClickListener(new MoreClickListener(groupPosition));

		TextView group_title = (TextView) convertView
				.findViewById(R.id.group_title);
		if (isExpanded) {
			group_title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zq_marketinfo_zhankai, 0,
					0, 0);
		} else {
			group_title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zq_marketinfo_shouqi, 0,
					0, 0);
		}
		if (getGroup(groupPosition) == null)
		{
			group_title.setText("--");
		}else
		{
			group_title.setText(((TopRankField)getGroup(groupPosition)).name);
		}

		return convertView;
	}

	/**
	 * 对一级标签下的二级标签进行设置
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = View.inflate(context, R.layout.zq_item_child_layout, null);
			vh.tv_child_name = (TextView) convertView.findViewById(R.id.tv_child_name);
			vh.tv_child_code = (TextView) convertView.findViewById(R.id.tv_child_code);
			vh.tv_child_price = (TextView) convertView.findViewById(R.id.tv_child_price);
			vh.tv_child_zdf = (TextView) convertView.findViewById(R.id.tv_child_zdf);
			convertView.setTag(vh);
		}
		else
		{
			vh = (ViewHolder) convertView.getTag();
		}

		TopRankData stockData = (TopRankData) getChild(groupPosition, childPosition);
		float fValue = 0.0f;
		if (stockData != null)
		{
			int nMarket = stockData.market;
			String code = stockData.code;
			ArrayList<CCodeTableItem> items = null;
			for (int j = 0; j < MyApp.getInstance().mCodeTableMarketNum && j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
		        if (nMarket == MyApp.getInstance().mCodeTable[j].mMarketId) {
		        	CDataCodeTable codeTable = MyApp.getInstance().mCodeTable[j];
		        	items = codeTable.getDataByCode(nMarket, code);
		        	break;
		        }
		    }
			
			String name = Global_Define.STRING_VALUE_EMPTY;
			String price = Global_Define.STRING_VALUE_EMPTY;
			String zdf = Global_Define.STRING_VALUE_EMPTY;
			CCodeTableItem item = new CCodeTableItem();
			if (items != null && items.size() > 0)
			{
				item = items.get(0);
				name = item.name;
				price = ViewTools.getStringByInt(stockData.nLastPrice,
						item.PriceDecimal, item.PriceRate, false);
			}
			stockData.name = name;
			fValue = stockData.fSortValue;
			
			vh.tv_child_name.setText(name);
			vh.tv_child_code.setText(stockData.code);
			vh.tv_child_price.setText(price);
			if (stockData.nLastClear != 0)
			{
				vh.tv_child_price.setTextColor(ViewTools.getColor(stockData.nLastPrice - stockData.nLastClear));
			}else
			{
				vh.tv_child_price.setTextColor(ViewTools.getColor(stockData.nLastPrice - stockData.nLastClose));
			}
			
			int dotlen = 2;
			if (item != null)
			{
				dotlen = item.PriceDecimal;
			}
			zdf = ViewTools.getStringByFloatPrice(fValue, 0, dotlen);
			String jiahao = "";
			if (((TopRankField)getGroup(groupPosition)).id == Global_Define.PBF_RANK_CHANGING_UP)
			{
				jiahao = "";
			}else
			{
				jiahao = "+";
			}
			
			if (fValue > 0f)
			{
				zdf = jiahao + zdf + "%";
			}else
			{
				zdf = zdf + "%";
			}
			
			vh.tv_child_zdf.setText(zdf);
		}else
		{
			vh.tv_child_name.setText(Global_Define.STRING_VALUE_EMPTY);
			vh.tv_child_code.setText(Global_Define.STRING_VALUE_EMPTY);
			vh.tv_child_price.setText(Global_Define.STRING_VALUE_EMPTY);
			vh.tv_child_zdf.setText(Global_Define.STRING_VALUE_EMPTY);
		}
		if (((TopRankField)getGroup(groupPosition)).id == Global_Define.PBF_RANK_CHANGING_UP)
		{
			vh.tv_child_zdf.setTextColor(Color.rgb(81, 81, 81));
		}else
		{
			vh.tv_child_zdf.setTextColor(ViewTools.getColor(fValue));
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
	
	/**
	 * 
	 * 排名更多点击事件
	 */
	class MoreClickListener implements OnClickListener {
		private int mPosition;

		public MoreClickListener(int position) {
			this.mPosition = position;
		}

		@Override
		public void onClick(View v) {
			TopRankField field = (TopRankField) getGroup(mPosition);
			Intent intent = new Intent();
			intent.putExtra("TOP_FIELD", (int)field.id);
			intent.putExtra("TOP_FIELD_NAME", field.name);
			intent.setClass(context, PaiMingMoreActivity.class);

			context.startActivity(intent);
		}
	}
	
	class ViewHolder
	{
		TextView tv_child_name,tv_child_code,tv_child_price,tv_child_zdf;
	}
}
