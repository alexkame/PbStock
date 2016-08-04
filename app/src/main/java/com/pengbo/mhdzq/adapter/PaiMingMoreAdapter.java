package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TopRankData;
import com.pengbo.mhdzq.data.PublicData.TopRankField;
import com.pengbo.mhdzq.tools.ViewTools;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * adapter to init data for listview
 * @author pobo
 */
public class PaiMingMoreAdapter extends BaseAdapter {
	private ArrayList<TopRankData> listDatas;
	private Activity mActivity;
	private boolean flag = false;
	private int fieldId;
	public PaiMingMoreAdapter(Activity activity, ArrayList<TopRankData> datas, boolean flag, int id) {
		super();
		listDatas = datas;
		mActivity = activity;
		this.flag = flag;
		fieldId = id;
	}
	
	public PaiMingMoreAdapter(Activity activity, ArrayList<TopRankData> datas, int id) {
		super();
		listDatas = datas;
		mActivity = activity;
		fieldId = id;
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
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);
			viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			viewHolder.tv_zdf = (TextView) convertView.findViewById(R.id.tv_zdf);
			viewHolder.tv_zd = (TextView) convertView.findViewById(R.id.tv_zd);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		TopRankData stockData = (TopRankData) getItem(position);
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
			
			viewHolder.tv_name.setText(name);
			viewHolder.tv_code.setText(stockData.code);
			viewHolder.tv_price.setText(price);
			if (stockData.nLastClear != 0)
			{
				viewHolder.tv_price.setTextColor(ViewTools.getColor(stockData.nLastPrice - stockData.nLastClear));
			}else
			{
				viewHolder.tv_price.setTextColor(ViewTools.getColor(stockData.nLastPrice - stockData.nLastClose));
			}
			
			int dotlen = 2;
			if (item != null)
			{
				dotlen = item.PriceDecimal;
			}
			zdf = ViewTools.getStringByFloatPrice(fValue, 0, dotlen);
			String jiahao = "";
			if (fieldId == Global_Define.PBF_RANK_CHANGING_UP)
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
			
			viewHolder.tv_zdf.setText(zdf);
		}else
		{
			viewHolder.tv_name.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_code.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_price.setText(Global_Define.STRING_VALUE_EMPTY);
			viewHolder.tv_zdf.setText(Global_Define.STRING_VALUE_EMPTY);
		}
		if (fieldId == Global_Define.PBF_RANK_CHANGING_UP)
		{
			viewHolder.tv_zdf.setTextColor(Color.rgb(81, 81, 81));
		}else
		{
			viewHolder.tv_zdf.setTextColor(ViewTools.getColor(fValue));
		}
		return convertView;
	}


	class ViewHolder {
		TextView tv_name;
		TextView tv_code;
		TextView tv_price;
		TextView tv_zdf;
		TextView tv_zd;

	}

}
