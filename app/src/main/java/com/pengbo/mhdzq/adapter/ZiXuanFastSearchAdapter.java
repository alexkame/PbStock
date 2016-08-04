package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.SearchDataItem;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ZiXuanFastSearchAdapter extends BaseAdapter implements Filterable{
	
	private ArrayList<SearchDataItem> datas;
	private LayoutInflater mInflater;
	private MyApp mMyApp;
	private Context context;

	public ZiXuanFastSearchAdapter(MyApp myApp, Context con, ArrayList<SearchDataItem> list) {
		super();
		this.datas = list;
		this.mInflater = LayoutInflater.from(con);
		this.context = con;
		this.mMyApp = myApp;
	}

	public ArrayList<SearchDataItem> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<SearchDataItem> list) {
		this.datas = list;
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
	public View getView(final int position, View convertView, ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.zq_zixuan_fastsearch_listview_item, null);
				
				// 添加按钮
				viewHolder.imageButton_add=(ImageView) convertView.findViewById(R.id.img_btn_item_add_minus);
				
				//删除自选股按钮 
				viewHolder.imageButton_del=(ImageView) convertView.findViewById(R.id.img_btn_item_add_minus_del);

				viewHolder.FIELD_HQ_NAME_ANSI = (TextView) convertView.findViewById(R.id.tv_fast_search_item_name);
				viewHolder.FIELD_HQ_CODE=(TextView) convertView.findViewById(R.id.tv_fast_search_item_code);

				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		SearchDataItem item = datas.get(position);

		viewHolder.FIELD_HQ_NAME_ANSI.setText(item.name);
		viewHolder.FIELD_HQ_CODE.setText(item.code);
		
		// 判断是否自选股 
		boolean flag = mMyApp.IsStockExist(item.code, item.market,AppConstants.HQTYPE_ZQ);

		if(!flag){
			viewHolder.imageButton_add.setVisibility(View.VISIBLE);
			viewHolder.imageButton_del.setVisibility(View.INVISIBLE);
		}else{
			viewHolder.imageButton_add.setVisibility(View.INVISIBLE);
			viewHolder.imageButton_del.setVisibility(View.VISIBLE);
		}
		
		viewHolder.imageButton_add.setOnClickListener(new AddOrDeleteListener(position, viewHolder));
		viewHolder.imageButton_del.setOnClickListener(new AddOrDeleteListener(position, viewHolder));

		return convertView;
	}
	
	class AddOrDeleteListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public AddOrDeleteListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.imageButton_add) {
				//mMyApp.mChangeMyStockFromStockInfo = true;
				TagCodeInfo codeInfo = new TagCodeInfo(datas.get(mPosition).market,datas.get(mPosition).code, datas.get(mPosition).group, datas.get(mPosition).name);
				int ret = mMyApp.AddtoMyStock(codeInfo,AppConstants.HQTYPE_ZQ);

				if (ret == 0) {
					mViewHolder.imageButton_add.setVisibility(View.INVISIBLE);
					mViewHolder.imageButton_del.setVisibility(View.VISIBLE);
					Toast.makeText(context, "添加到自选股", Toast.LENGTH_SHORT)
							.show();
				} else if (ret == -1) {
					Toast.makeText(context, "自选股已存在！", Toast.LENGTH_SHORT)
							.show();
				} else if (ret == -2) {
					Toast.makeText(context, "自选股超过最大限制！", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (v == mViewHolder.imageButton_del) {
				//mMyApp.mChangeMyStockFromStockInfo = true;
				int size = mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ).size();
				int delPos = -1;
				for (int i = 0; i < size; i++) {
					
					if (datas.get(mPosition).code.equalsIgnoreCase(mMyApp
							.getMyStockList(AppConstants.HQTYPE_ZQ).get(i).code)
							&& datas.get(mPosition).market == mMyApp
									.getMyStockList(AppConstants.HQTYPE_ZQ).get(i).market) {
						delPos = i;
						break;
					}
				}

				int ret = mMyApp.RemoveFromMyStock(delPos,AppConstants.HQTYPE_ZQ);
				if (ret == 0) {
					mViewHolder.imageButton_add.setVisibility(View.VISIBLE);
					mViewHolder.imageButton_del.setVisibility(View.INVISIBLE);
					Toast.makeText(context, "该自选股已删除！", Toast.LENGTH_SHORT)
							.show();
				}
			}

		}
	}

	class ViewHolder {
		TextView FIELD_HQ_NAME_ANSI;// 期权名称
		ImageView imageButton_add;// 此股不在自选股中 显示可添加按钮 
		ImageView imageButton_del;//此股 在自选股中显示可删除按钮  
		TextView FIELD_HQ_CODE;
	}

	@Override
	public Filter getFilter() {
		return null;
	}
}
