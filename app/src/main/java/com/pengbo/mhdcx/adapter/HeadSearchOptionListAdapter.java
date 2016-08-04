package com.pengbo.mhdcx.adapter;

import java.util.List;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HeadSearchOptionListAdapter extends BaseAdapter{
	
	private List<TagLocalStockData> datas;

	private List<TagCodeInfo> mTagCodeInfos;// 通过条件查询把数据的TagCodeInfo的集合也传递过来

	private LayoutInflater mInflater;
//	private View mLinearLayout;
	private MyApp mMyApp;
	private Context context;

	//private OnPinnedItemClickListener mOnItemClickListener;

	private TagLocalStockData aStockData;

	public HeadSearchOptionListAdapter(MyApp myApp, Context con, List<TagLocalStockData> datas, List<TagCodeInfo> mTagCodeInfos) {
		super();
		this.datas = datas;
		this.mTagCodeInfos = mTagCodeInfos;
		this.mInflater = LayoutInflater.from(con);
		this.context = con;
		mMyApp = myApp;
//		selfStock=new ArrayList<TagCodeInfo>();
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
	public View getView(final int position, View convertView, ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_search_optionlist, null);
				
				// 添加按钮
				viewHolder.imageButton_add=(ImageButton) convertView.findViewById(R.id.item_add_minus);
				
				// 搜索页加入自选的按钮
				
				//删除自选股按钮 
				viewHolder.imageButton_del=(ImageButton) convertView.findViewById(R.id.item_add_minus_del);

				viewHolder.FIELD_HQ_NAME_ANSI = (TextView) convertView.findViewById(R.id.item1);
				

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
		
		// 判断是否自选股 
		boolean flag = mMyApp.IsStockExist(aStockData.HQData.code, aStockData.HQData.market, AppConstants.HQTYPE_QQ);

		//L.e("HeadOptionListAdapter__" + flag + ",code = " + mTagCodeInfos.get(position).code +",name " + mTagCodeInfos.get(position).name + " ,market");
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
				TagCodeInfo codeInfo = new TagCodeInfo(datas.get(mPosition).HQData.market,datas.get(mPosition).HQData.code,(short)datas.get(mPosition).GroupOffset,datas.get(mPosition).name);
				int ret = mMyApp.AddtoMyStock(codeInfo, AppConstants.HQTYPE_QQ);

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
				int size = mMyApp.getMyStockList(AppConstants.HQTYPE_QQ).size();
				int delPos = -1;
				for (int i = 0; i < size; i++) {
					
					if (datas.get(mPosition).HQData.code.equals(mMyApp
							.getMyStockList(AppConstants.HQTYPE_QQ).get(i).code)
							&& datas.get(mPosition).HQData.market == mMyApp
									.getMyStockList(AppConstants.HQTYPE_QQ).get(i).market) {
						delPos = i;
						break;
					}
				}

				int ret = mMyApp.RemoveFromMyStock(delPos, AppConstants.HQTYPE_QQ);
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
		ImageButton imageButton_add;// 此股不在自选股中 显示可添加按钮 
		ImageButton imageButton_del;//此股 在自选股中显示可删除按钮  
	}
}
