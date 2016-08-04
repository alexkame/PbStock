package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.R;

public class TradeQueryAdapter extends BaseAdapter {

	public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();
	private PBSTEP mLSDatas;
	private PBSTEP mLSDRWT;
	private LayoutInflater mInflater;
	private Handler mHandler;
	boolean tag = true;

	public TradeQueryAdapter(Context context, PBSTEP datas, Handler handler) {
		super();
		this.mLSDatas = datas;
		this.mHandler = handler;
		this.mInflater = LayoutInflater.from(context);
	}

	public PBSTEP getmLSDRWT() {
		return mLSDRWT;
	}

	public void setmLSDRWT(PBSTEP mLSDRWT) {
		this.mLSDRWT = mLSDRWT;
	}

	public boolean isTag() {
		return tag;
	}

	public void setTag(boolean tag) {
		this.tag = tag;
	}

	@Override
	public int getCount() {
		return mLSDatas.GetRecNum();
	}

	@Override
	public Object getItem(int arg0) {
		mLSDatas.GotoRecNo(arg0);
		return mLSDatas;
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentView) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				convertView = mInflater.inflate(R.layout.trade_detail_query_listview_item, null);
				viewHolder = new ViewHolder();

				viewHolder.mTime = (TextView) convertView.findViewById(R.id.tv_time);
				viewHolder.mOptionName = (TextView) convertView.findViewById(R.id.tv_optionname);
				viewHolder.mType = (TextView) convertView.findViewById(R.id.tv_type);

				viewHolder.mType2 = (TextView) convertView.findViewById(R.id.tv_type1);

				viewHolder.mWeituojia = (TextView) convertView.findViewById(R.id.tv_weituojia);
				viewHolder.mBeiDuiMC = (TextView) convertView.findViewById(R.id.tv_beidui);
				viewHolder.mNum = (TextView) convertView.findViewById(R.id.tv_num);
				viewHolder.mState1 = (TextView) convertView.findViewById(R.id.tv_state1);
				viewHolder.mState2 = (TextView) convertView.findViewById(R.id.tv_state2);
				viewHolder.mDealNum = (TextView) convertView.findViewById(R.id.tv_dealnum);
				viewHolder.mLayoutWTState = (LinearLayout) convertView.findViewById(R.id.linearlayout_wt_state);
				viewHolder.mLayoutCJNum = (LinearLayout) convertView.findViewById(R.id.linearlayout_cj_num);

				convertView.setTag(viewHolder);
				mHolderList.add(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set item values to the viewHolder:
		String mTime = "";
		String mOptionName = "";
		String mType = "";
		String mType2 = "";
		String mWeituojia = "";
		String mNum = "";
		String mState1 = "";
		String mDealNum = "";

		if (position < mLSDatas.GetRecNum()) {
			mLSDatas.GotoRecNo(position);
			mTime = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTSJ);
			mOptionName = mLSDatas.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			mType = mLSDatas.GetFieldValueString(STEP_Define.STEP_MMLBMC);// 买卖类型的名称
			mType2 = mLSDatas.GetFieldValueString(STEP_Define.STEP_KPBZMC);// 开平仓名称
			mNum = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTSL);
			
			char sjType = mLSDatas.GetFieldValueCHAR(STEP_Define.STEP_SJWTLB);
		    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
		    {
		    	mWeituojia = mLSDatas.GetFieldValueString(STEP_Define.STEP_SJWTLBMC);
		    }else
		    {
		    	mWeituojia = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTJG);
		    }
		    
		    String BDMC = mLSDatas.GetFieldValueString(STEP_Define.STEP_BDBZMC);
		    viewHolder.mBeiDuiMC.setText(BDMC);
			if (tag) {
				viewHolder.mLayoutWTState.setVisibility(View.VISIBLE);
				viewHolder.mLayoutCJNum.setVisibility(View.GONE);
				mState1 = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTZTMC);

				String mWTZT = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTZT);
				if (DataTools.isCDStatusEnabled(mWTZT)) {// aOption.getWTZT().toString())
					viewHolder.mState2.setVisibility(View.VISIBLE);
				} else {
					viewHolder.mState2.setVisibility(View.GONE);
				}
				viewHolder.mState2.setOnClickListener(new CheDanClickListener(position, viewHolder));
			} else {

				viewHolder.mLayoutWTState.setVisibility(View.GONE);
				viewHolder.mLayoutCJNum.setVisibility(View.VISIBLE);
				
				mNum = mLSDatas.GetFieldValueString(STEP_Define.STEP_CJSL);
				mTime = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTSJ);

			    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
			    {
			    	mWeituojia = mLSDatas.GetFieldValueString(STEP_Define.STEP_SJWTLBMC);
			    }else
			    {
			    	mWeituojia = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTJG);
			    }
				
				if (mNum.isEmpty() || mTime.isEmpty() || mWeituojia.isEmpty())
				{
					String wtbh = mLSDatas.GetFieldValueString(STEP_Define.STEP_WTBH);
					if (!wtbh.isEmpty()) {
						for (int j = 0; j < mLSDRWT.GetRecNum(); j++) {
							mLSDRWT.GotoRecNo(j);
							String wtbh2 = mLSDRWT.GetFieldValueString(STEP_Define.STEP_WTBH);
							if (wtbh.equalsIgnoreCase(wtbh2)) {
								if (mTime.isEmpty())
								{
									mTime = mLSDRWT.GetFieldValueString(STEP_Define.STEP_WTSJ);
								}
	
								if (mWeituojia.isEmpty())
								{
									sjType = mLSDRWT.GetFieldValueCHAR(STEP_Define.STEP_SJWTLB);
								    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
								    {
								    	mWeituojia = mLSDRWT.GetFieldValueString(STEP_Define.STEP_SJWTLBMC);
								    }else
								    {
								    	mWeituojia = mLSDRWT.GetFieldValueString(STEP_Define.STEP_WTJG);
								    }
								}
	
								if (mNum.isEmpty())
								{
									mNum = mLSDRWT.GetFieldValueString(STEP_Define.STEP_CJSL);
								}
								break;
							}
						}
					}
				}
				mDealNum = mLSDatas.GetFieldValueString(STEP_Define.STEP_CJJG);
			}
			viewHolder.mTime.setText(mTime);
			viewHolder.mOptionName.setText(mOptionName);
			viewHolder.mType.setText(mType);
			viewHolder.mType2.setText(mType2);
			viewHolder.mWeituojia.setText(mWeituojia);
			viewHolder.mNum.setText(STD.IntToString((int)STD.StringToValue(mNum)));
			viewHolder.mState1.setText(mState1);
			viewHolder.mDealNum.setText(mDealNum);

		} else {
			viewHolder.mTime.setText("");
			viewHolder.mOptionName.setText("");
			viewHolder.mType.setText("");
			viewHolder.mType2.setText("");
			viewHolder.mWeituojia.setText("");
			viewHolder.mNum.setText("");
			viewHolder.mState1.setText("");
			viewHolder.mState2.setText("");
			viewHolder.mDealNum.setText("");
			viewHolder.mBeiDuiMC.setText("");
		}

		return convertView;
	}

	class CheDanClickListener implements OnClickListener {

		private int mPosition;
		private ViewHolder mViewHolder;

		public CheDanClickListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.mState2) {
				// 撤单
				if (mHandler == null) return;

				Message msg = mHandler.obtainMessage();
				msg.what = TradeNetConnect.MSG_ADAPTER_CD_BUTTON_CLICK;
				msg.arg1 = mPosition;
				mHandler.sendMessage(msg);

			}
		}
	}

	class ViewHolder {

		TextView mTime;// 委托时间
		TextView mOptionName;// 期权名称
		TextView mType;// 买卖
		TextView mType2;// 开平
		TextView mWeituojia;
		TextView mBeiDuiMC;
		TextView mNum;// 当是前两个 时 是 委托数量 当是已成交时是成交数量
		TextView mState1;
		TextView mState2;
		TextView mDealNum;// 成交价格

		LinearLayout mLayoutWTState;
		LinearLayout mLayoutCJNum;
	}

}
