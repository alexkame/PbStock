package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.ui.activity.ScreenDetailActivity;
import com.pengbo.mhdzq.view.CHScrollView;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.tools.ViewTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomOptionListAdapter extends BaseAdapter {
	/** 存放ViewHolder的List */
	public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();
	/** 向ListView中添加的数据集 */
	private List<TagLocalStockData> datas;
	private LayoutInflater mInflater;
	/** 横滚部分包含大量TextView的LinearLayout，除去第1列固定列 */
	private View mLinearLayout;
	private MyApp mApp;
	private Context context;
	/** 包含该Adapter的Activity */
	private HdActivity activity;
	private ViewHolder viewHolder = null;
	private DisplayMetrics mScreenSize;
	/** 页面要展示的数据集中的单条 */
	private TagLocalStockData aStockData;

	public CustomOptionListAdapter(Context con, List<TagLocalStockData> datas,
			View mHead) {
		super();
		this.datas = datas;
		this.mInflater = LayoutInflater.from(con);
		this.mLinearLayout = mHead;
		this.context = con;
		mScreenSize = ViewTools.getScreenSize(context);
		mApp = (MyApp) ((Activity) con).getApplication();
		activity = (HdActivity) con;
	}

	public List<TagLocalStockData> getDatas() {
		return datas;
	}

	public void setDatas(List<TagLocalStockData> datas) {
		this.datas = datas;
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
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parentView) {
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.custom_listview_item,
						null);
				/** 页面主体数据滚动部分除去第1列固定的其余部分，包含在CHScrollView中 */
				CHScrollView hscrollView = (CHScrollView) convertView
						.findViewById(R.id.item_scroll);
				activity.addHViews(hscrollView);

				viewHolder.llayout_title = (LinearLayout) convertView
						.findViewById(R.id.layout);
				viewHolder.llayout_data = (LinearLayout) convertView
						.findViewById(R.id.llayout_content);
				viewHolder.hscrollView = hscrollView;

				viewHolder.FIELD_HQ_NAME_ANSI = (TextView) convertView
						.findViewById(R.id.item1);
				LayoutParams lp = viewHolder.FIELD_HQ_NAME_ANSI
						.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_NAME_ANSI.setLayoutParams(lp);

				viewHolder.FIELD_HQ_NOW = (TextView) convertView
						.findViewById(R.id.item2);
				lp = viewHolder.FIELD_HQ_NOW.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 7 / 30;
				viewHolder.FIELD_HQ_NOW.setLayoutParams(lp);

				viewHolder.FIELD_HQ_ZDF = (TextView) convertView
						.findViewById(R.id.item3);
				lp = viewHolder.FIELD_HQ_ZDF.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 7 / 30;
				viewHolder.FIELD_HQ_ZDF.setLayoutParams(lp);

				viewHolder.FIELD_HQ_YJL = (TextView) convertView
						.findViewById(R.id.item4);
				lp = viewHolder.FIELD_HQ_YJL.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 7 / 30;
				viewHolder.FIELD_HQ_YJL.setLayoutParams(lp);

				viewHolder.FIELD_HQ_XQJ = (TextView) convertView
						.findViewById(R.id.item5);
				lp = viewHolder.FIELD_HQ_XQJ.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_XQJ.setLayoutParams(lp);

				viewHolder.mbuyprice = (TextView) convertView
						.findViewById(R.id.item6);
				lp = viewHolder.mbuyprice.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.mbuyprice.setLayoutParams(lp);

				viewHolder.mbuytotal = (TextView) convertView
						.findViewById(R.id.item7);
				lp = viewHolder.mbuytotal.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.mbuytotal.setLayoutParams(lp);

				viewHolder.msellprice = (TextView) convertView
						.findViewById(R.id.item8);
				lp = viewHolder.msellprice.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.msellprice.setLayoutParams(lp);

				viewHolder.mselltotal = (TextView) convertView
						.findViewById(R.id.item9);
				lp = viewHolder.mselltotal.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.mselltotal.setLayoutParams(lp);

				viewHolder.FIELD_HQ_VOLUME = (TextView) convertView
						.findViewById(R.id.item10);
				lp = viewHolder.FIELD_HQ_VOLUME.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_VOLUME.setLayoutParams(lp);

				viewHolder.FIELD_HQ_CURVOL = (TextView) convertView
						.findViewById(R.id.item11);
				lp = viewHolder.FIELD_HQ_CURVOL.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_CURVOL.setLayoutParams(lp);

				viewHolder.FIELD_HQ_CCL = (TextView) convertView
						.findViewById(R.id.item12);
				lp = viewHolder.FIELD_HQ_CCL.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_CCL.setLayoutParams(lp);

				viewHolder.FIELD_HQ_CC = (TextView) convertView
						.findViewById(R.id.item13);
				lp = viewHolder.FIELD_HQ_CC.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_CC.setLayoutParams(lp);

				viewHolder.FIELD_HQ_GGL = (TextView) convertView
						.findViewById(R.id.item14);
				lp = viewHolder.FIELD_HQ_GGL.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_GGL.setLayoutParams(lp);

				viewHolder.FIELD_HQ_ZSGGL = (TextView) convertView
						.findViewById(R.id.item15);
				lp = viewHolder.FIELD_HQ_ZSGGL.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_ZSGGL.setLayoutParams(lp);

				viewHolder.FIELD_HQ_NZJZ = (TextView) convertView
						.findViewById(R.id.item16);
				lp = viewHolder.FIELD_HQ_NZJZ.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_NZJZ.setLayoutParams(lp);

				viewHolder.FIELD_HQ_SJJZ = (TextView) convertView
						.findViewById(R.id.item17);
				lp = viewHolder.FIELD_HQ_SJJZ.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_SJJZ.setLayoutParams(lp);

				viewHolder.FIELD_HQ_SXZ = (TextView) convertView
						.findViewById(R.id.item18);
				lp = viewHolder.FIELD_HQ_SXZ.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_SXZ.setLayoutParams(lp);

				viewHolder.FIELD_HQ_EXPIRE_DATE = (TextView) convertView
						.findViewById(R.id.item19);
				lp = viewHolder.FIELD_HQ_EXPIRE_DATE.getLayoutParams();
				lp.width = mScreenSize.widthPixels * 3 / 10;
				viewHolder.FIELD_HQ_EXPIRE_DATE.setLayoutParams(lp);

				convertView.setTag(viewHolder);
				mHolderList.add(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set item values to the viewHolder:

		// aStockData 合约信息
		aStockData = datas.get(position);

		// aTagCodeInfo = mTagCodeInfos.get(position);

		// TagLocalStockData aStockData = new TagLocalStockData();
		// ViewTools.getStringbyFieldID(aStockData,0);

		// aStockInfo 合约对应的标的信息
		TagLocalStockData aStockInfo = new TagLocalStockData();

		mApp.mStockConfigData.search(aStockInfo,
				aStockData.optionData.StockMarket,
				aStockData.optionData.StockCode);

		viewHolder.FIELD_HQ_NAME_ANSI.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_NAME_ANSI));

		viewHolder.FIELD_HQ_NOW.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_NOW));
		viewHolder.FIELD_HQ_NOW.setTextColor(ViewTools.getColorByFieldID(
				aStockData, Global_Define.FIELD_HQ_NOW));

		viewHolder.FIELD_HQ_ZDF.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_ZDF));
		viewHolder.FIELD_HQ_ZDF.setTextColor(ViewTools.getColorByFieldID(
				aStockData, Global_Define.FIELD_HQ_NOW));

		viewHolder.FIELD_HQ_YJL.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_YJL, aStockInfo));// -----

		viewHolder.FIELD_HQ_XQJ.setText(ViewTools.getStringByFieldID(
				aStockData, Global_Define.FIELD_HQ_XQJ));// 行权价

		viewHolder.mbuyprice.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_BUYPRICE));
		viewHolder.mbuyprice.setTextColor(ViewTools.getColorByFieldID(
				aStockData, Global_Define.FIELD_HQ_BUYPRICE));
		viewHolder.mbuytotal.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_BVOLUME1));

		viewHolder.msellprice.setText(ViewTools.getStringByFieldID(aStockData,
				Global_Define.FIELD_HQ_SELLPRICE));
		viewHolder.msellprice.setTextColor(ViewTools.getColorByFieldID(
				aStockData, Global_Define.FIELD_HQ_SELLPRICE));
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

		viewHolder.llayout_title.setOnClickListener(new clickListener(position,
				viewHolder));
		viewHolder.llayout_data.setOnClickListener(new clickListener(position,
				viewHolder));
		// viewHolder.rlayot_block.setOnClickListener(new
		// clickListener(position, viewHolder));

		return convertView;
	}

	class clickListener implements OnClickListener, OnTouchListener {

		private int mPosition;
		private ViewHolder mViewHolder;

		public clickListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.llayout_title || v == mViewHolder.llayout_data) {
				mViewHolder.llayout_title.setPressed(true);
				mViewHolder.llayout_title
						.setBackgroundResource(R.drawable.list_item_color_bg);

				TagLocalStockData option = datas.get(mPosition);
				Intent mIntent = new Intent();
				mIntent.setClass(context, ScreenDetailActivity.class);

				TagCodeInfo optionCodeInfo = new TagCodeInfo(
						option.HQData.market, option.HQData.code, option.group,
						option.name);

				TagCodeInfo stockCodeInfo = new TagCodeInfo(
						option.optionData.StockMarket,
						option.optionData.StockCode, option.group, option.name);
				ScreenDetailActivity.mOptionCodeInfo = optionCodeInfo;
				ScreenDetailActivity.mStockCodeInfo = stockCodeInfo;

				context.startActivity(mIntent);
			}
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			return false;
		}

	}

	class ViewHolder {
		CHScrollView hscrollView;
		LinearLayout llayout_title;
		LinearLayout llayout_data;
		/** 期权名称 **/
		TextView FIELD_HQ_NAME_ANSI;
		/** 最新价 **/
		TextView FIELD_HQ_NOW;
		/** 涨跌幅 **/
		TextView FIELD_HQ_ZDF;
		/** 溢价率 **/
		TextView FIELD_HQ_YJL;
		/** 行权价 **/
		TextView FIELD_HQ_XQJ;
		TextView mbuyprice;
		TextView mbuytotal;
		TextView msellprice;
		TextView mselltotal;
		/** 总量 **/
		TextView FIELD_HQ_VOLUME;
		/** 现量 **/
		TextView FIELD_HQ_CURVOL;
		/** 持仓量 **/
		TextView FIELD_HQ_CCL;
		/** 仓差 **/
		TextView FIELD_HQ_CC;
		/** 杠杆率 **/
		TextView FIELD_HQ_GGL;
		/** 真实杠杆率 **/
		TextView FIELD_HQ_ZSGGL;
		/** 内在价值 **/
		TextView FIELD_HQ_NZJZ;
		/** 时间价值 **/
		TextView FIELD_HQ_SJJZ;
		/** 实虚值 **/
		TextView FIELD_HQ_SXZ;
		/** 到期日 **/
		TextView FIELD_HQ_EXPIRE_DATE;
	}
}
