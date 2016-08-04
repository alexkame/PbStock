package com.pengbo.mhdcx.adapter;

import java.util.List;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.view.MyHScrollView;
import com.pengbo.mhdcx.view.MyHScrollView.OnScrollChangedListener;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HeadOptionListAdapter extends BaseAdapter{
	
	private List<TagLocalStockData> datas;

	private List<TagCodeInfo> mTagCodeInfos;// 通过条件查询把数据的TagCodeInfo的集合也传递过来

	private LayoutInflater mInflater;
	private View mLinearLayout;
	private MyApp mMyApp;
	private Context context;
	private DisplayMetrics mScreenSize;

	//private OnPinnedItemClickListener mOnItemClickListener;

	private TagLocalStockData aStockData;

	public HeadOptionListAdapter(MyApp myApp, Context con, List<TagLocalStockData> datas,
			View mHead, List<TagCodeInfo> mTagCodeInfos) {
		super();
		this.datas = datas;
		this.mTagCodeInfos = mTagCodeInfos;
		this.mInflater = LayoutInflater.from(con);
		this.mLinearLayout = mHead;
		this.context = con;
		mScreenSize = ViewTools.getScreenSize(context);
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
				convertView = mInflater.inflate(R.layout.item_optionlist, null);

				MyHScrollView scrollView1 = (MyHScrollView) convertView
						.findViewById(R.id.horizontalScrollView1);

				viewHolder.scrollView = scrollView1;
				
				// 添加按钮
				viewHolder.imageButton_add=(ImageButton) convertView.findViewById(R.id.item_add_minus);
				
				// 搜索页加入自选的按钮
				
				//删除自选股按钮 
				viewHolder.imageButton_del=(ImageButton) convertView.findViewById(R.id.item_add_minus_del);

				viewHolder.FIELD_HQ_NAME_ANSI = (TextView) convertView
						.findViewById(R.id.item1);
				LayoutParams lp = viewHolder.FIELD_HQ_NAME_ANSI.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_NAME_ANSI.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_NOW = (TextView) convertView
						.findViewById(R.id.item2);
				lp = viewHolder.FIELD_HQ_NOW.getLayoutParams();
				lp.width = mScreenSize.widthPixels*7/30;
				viewHolder.FIELD_HQ_NOW.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_ZDF = (TextView) convertView
						.findViewById(R.id.item3);
				lp = viewHolder.FIELD_HQ_ZDF.getLayoutParams();
				lp.width = mScreenSize.widthPixels*7/30;
				viewHolder.FIELD_HQ_ZDF.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_YJL = (TextView) convertView
						.findViewById(R.id.item4);
				lp = viewHolder.FIELD_HQ_YJL.getLayoutParams();
				lp.width = mScreenSize.widthPixels*7/30;
				viewHolder.FIELD_HQ_YJL.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_XQJ = (TextView) convertView
						.findViewById(R.id.item5);
				lp = viewHolder.FIELD_HQ_XQJ.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_XQJ.setLayoutParams(lp);
				
				viewHolder.mbuyprice = (TextView) convertView
						.findViewById(R.id.item6);
				lp = viewHolder.mbuyprice.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.mbuyprice.setLayoutParams(lp);
				
				viewHolder.mbuytotal = (TextView) convertView
						.findViewById(R.id.item7);
				lp = viewHolder.mbuytotal.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.mbuytotal.setLayoutParams(lp);
				
				viewHolder.msellprice = (TextView) convertView
						.findViewById(R.id.item8);
				lp = viewHolder.msellprice.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.msellprice.setLayoutParams(lp);
				
				viewHolder.mselltotal = (TextView) convertView
						.findViewById(R.id.item9);
				lp = viewHolder.mselltotal.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.mselltotal.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_VOLUME = (TextView) convertView
						.findViewById(R.id.item10);
				lp = viewHolder.FIELD_HQ_VOLUME.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_VOLUME.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_CURVOL = (TextView) convertView
						.findViewById(R.id.item11);
				lp = viewHolder.FIELD_HQ_CURVOL.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_CURVOL.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_CCL = (TextView) convertView
						.findViewById(R.id.item12);
				lp = viewHolder.FIELD_HQ_CCL.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_CCL.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_CC = (TextView) convertView
						.findViewById(R.id.item13);
				lp = viewHolder.FIELD_HQ_CC.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_CC.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_GGL = (TextView) convertView
						.findViewById(R.id.item14);
				lp = viewHolder.FIELD_HQ_GGL.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_GGL.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_ZSGGL = (TextView) convertView
						.findViewById(R.id.item15);
				lp = viewHolder.FIELD_HQ_ZSGGL.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_ZSGGL.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_NZJZ = (TextView) convertView
						.findViewById(R.id.item16);
				lp = viewHolder.FIELD_HQ_NZJZ.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_NZJZ.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_SJJZ = (TextView) convertView
						.findViewById(R.id.item17);
				lp = viewHolder.FIELD_HQ_SJJZ.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_SJJZ.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_SXZ = (TextView) convertView
						.findViewById(R.id.item18);
				lp = viewHolder.FIELD_HQ_SXZ.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_SXZ.setLayoutParams(lp);
				
				viewHolder.FIELD_HQ_EXPIRE_DATE = (TextView) convertView
						.findViewById(R.id.item19);
				lp = viewHolder.FIELD_HQ_EXPIRE_DATE.getLayoutParams();
				lp.width = mScreenSize.widthPixels*3/10;
				viewHolder.FIELD_HQ_EXPIRE_DATE.setLayoutParams(lp);

				MyHScrollView headSrcrollView = (MyHScrollView) mLinearLayout
						.findViewById(R.id.horizontalScrollView1);
				headSrcrollView
						.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
								scrollView1));

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
		viewHolder.FIELD_HQ_NOW.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_NOW));
		viewHolder.FIELD_HQ_NOW.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_NOW));
		
		viewHolder.FIELD_HQ_ZDF.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_ZDF));
		viewHolder.FIELD_HQ_ZDF.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_NOW));
		
		viewHolder.FIELD_HQ_YJL.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_YJL, aStockInfo));// -----

		viewHolder.FIELD_HQ_XQJ.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_XQJ));// 行权价

		viewHolder.mbuyprice.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_BUYPRICE));
		viewHolder.mbuyprice.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_BUYPRICE));
		
		viewHolder.mbuytotal.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_BVOLUME1));

		viewHolder.msellprice.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_SELLPRICE));
		viewHolder.msellprice.setTextColor(ViewTools.getColorByFieldID(aStockData, Global_Define.FIELD_HQ_SELLPRICE));
		viewHolder.mselltotal.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_SVOLUME1));

		viewHolder.FIELD_HQ_VOLUME.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_VOLUME));

		viewHolder.FIELD_HQ_CURVOL.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_CURVOL));
		viewHolder.FIELD_HQ_CCL.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_CCL));

		viewHolder.FIELD_HQ_CC.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_CC));

		viewHolder.FIELD_HQ_GGL.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_GGL, aStockInfo));

		viewHolder.FIELD_HQ_ZSGGL.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_ZSGGL, aStockInfo));// --------------------

		viewHolder.FIELD_HQ_NZJZ.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_NZJZ, aStockInfo));// ---------------

		viewHolder.FIELD_HQ_SJJZ.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_SJJZ, aStockInfo));
		viewHolder.FIELD_HQ_SXZ.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_SXZ, aStockInfo));
		viewHolder.FIELD_HQ_EXPIRE_DATE.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_EXPIRE_DATE, aStockInfo));
		
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

	class OnScrollChangedListenerImp implements OnScrollChangedListener {
		MyHScrollView mScrollViewArg;

		public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
			mScrollViewArg = scrollViewar;
		}

		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			mScrollViewArg.smoothScrollTo(l, t);
		}
	};

	class ViewHolder {
		TextView FIELD_HQ_NAME_ANSI;// 期权名称
		TextView FIELD_HQ_NOW;// 最新价
		TextView FIELD_HQ_ZDF;// 涨跌幅
		TextView FIELD_HQ_YJL;// 溢价率
		TextView FIELD_HQ_XQJ;// 行权价
		TextView mbuyprice;
		TextView mbuytotal;
		TextView msellprice;
		TextView mselltotal;
		TextView FIELD_HQ_VOLUME;// 总量
		TextView FIELD_HQ_CURVOL;// 现量
		TextView FIELD_HQ_CCL;// 持仓量
		TextView FIELD_HQ_CC;// 仓差
		TextView FIELD_HQ_GGL;// 杠杆率
		TextView FIELD_HQ_ZSGGL;// 真实杠杆率
		TextView FIELD_HQ_NZJZ;// 内在价值
		TextView FIELD_HQ_SJJZ;// 时间价值
		TextView FIELD_HQ_SXZ;// 实虚值
		TextView FIELD_HQ_EXPIRE_DATE;// 到期日
		HorizontalScrollView scrollView;
		
		
		ImageButton imageButton_add;// 此股不在自选股中 显示可添加按钮 
		ImageButton imageButton_del;//此股 在自选股中显示可删除按钮  
	}

//	public void setOnPinnedItemClickListener(
//			OnPinnedItemClickListener mOnItemClickListener) {
//		this.mOnItemClickListener = mOnItemClickListener;
//	}
//
//	public interface OnPinnedItemClickListener {
//		void onPinnedItemClick(TagCodeInfo tagcodeinfo, boolean clash);
//	}



}
