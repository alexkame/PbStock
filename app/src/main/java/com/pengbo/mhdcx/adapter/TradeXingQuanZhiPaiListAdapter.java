package com.pengbo.mhdcx.adapter;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 行权下单list中的listView 适配器 
 * @author pobo
 *
 */
public class TradeXingQuanZhiPaiListAdapter extends BaseAdapter {
	
	private MyApp mMyApp;
	private PBSTEP mListData;
	private Context context;
	private Handler mHandler;
	private DisplayMetrics mScreenSize;


	public TradeXingQuanZhiPaiListAdapter(Context context, PBSTEP datas, Handler handler) {
		this.context = context;
		mMyApp = (MyApp) this.context.getApplicationContext();
		this.mListData = datas;
		this.mHandler = handler;
		mScreenSize = ViewTools.getScreenSize(context);
	}
	
	
	@Override
	public int getCount() {
		return mListData.GetRecNum();
	}

	@Override
	public Object getItem(int position) {
		mListData.GotoRecNo(position);
		return mListData;
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
						R.layout.hv_xqzp_item_data, null);

				viewHolder.tv_item1 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item1);
				LayoutParams lp = viewHolder.tv_item1.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item1.setLayoutParams(lp);
				
				viewHolder.tv_item2 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item2);
				lp = viewHolder.tv_item2.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item2.setLayoutParams(lp);
				
				viewHolder.tv_item3 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item3);
				lp = viewHolder.tv_item3.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item3.setLayoutParams(lp);
				
				viewHolder.tv_item4 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item4);
				lp = viewHolder.tv_item4.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item4.setLayoutParams(lp);
				
				viewHolder.tv_item5 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item5);
				lp = viewHolder.tv_item5.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item5.setLayoutParams(lp);
				
				viewHolder.tv_item6 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item6);
				lp = viewHolder.tv_item6.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item6.setLayoutParams(lp);
				
				viewHolder.tv_item7 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item7);
				lp = viewHolder.tv_item7.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item7.setLayoutParams(lp);
				
				viewHolder.tv_item8 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item8);
				lp = viewHolder.tv_item8.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item8.setLayoutParams(lp);
				
				viewHolder.tv_item9 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item9);
				lp = viewHolder.tv_item9.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item9.setLayoutParams(lp);
				
				viewHolder.tv_item10 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item10);
				lp = viewHolder.tv_item10.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item10.setLayoutParams(lp);
				
				viewHolder.tv_item11 = (TextView) convertView
						.findViewById(R.id.hv_xqzp_item11);
				lp = viewHolder.tv_item11.getLayoutParams();
				lp.width = mScreenSize.widthPixels/4;
				viewHolder.tv_item11.setLayoutParams(lp);
				
				
				convertView.setTag(viewHolder);
			}
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position < this.getCount())
		{
			mListData.GotoRecNo(position);
			
			String marketCode = mListData.GetFieldValueString(STEP_Define.STEP_SCDM);
			if (marketCode.equalsIgnoreCase(Trade_Define.ENum_MARKET_SHQQA) == true)
			{
				viewHolder.tv_item1.setText("上海股票期权");
			}else if (marketCode.equalsIgnoreCase(Trade_Define.ENum_MARKET_SZQQA) == true)
			{
				viewHolder.tv_item1.setText("深圳股票期权");
			}else
			{
				viewHolder.tv_item1.setText("");
			}
			
			String stockCode = mListData.GetFieldValueString(STEP_Define.STEP_HYDM);
			viewHolder.tv_item2.setText(stockCode);
			
			String optionName = mListData.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			viewHolder.tv_item3.setText(optionName);
			
			String qqlb = mListData.GetFieldValueString(STEP_Define.STEP_HYLB);
			
			if (qqlb.equalsIgnoreCase(Trade_Define.TRADE_HYLB_GOU) == true)
			{
				viewHolder.tv_item4.setText("认购");
				viewHolder.tv_item4.setTextColor(ViewTools.getColor(1.0f));
			}else if (qqlb.equalsIgnoreCase(Trade_Define.TRADE_HYLB_GU) == true)
			{
				viewHolder.tv_item4.setText("认沽");
				viewHolder.tv_item4.setTextColor(ViewTools.getColor(-1.0f));
			}else
			{
				viewHolder.tv_item4.setText("");
			}
			
			char szbd = mListData.GetFieldValueCHAR(STEP_Define.STEP_BDBZ);
			String bdStr1 = ""; //（备兑或非备兑）
			if (szbd == PTK_Define.PTK_D_BD) {
				bdStr1 = "备兑";
			} else if(szbd == PTK_Define.PTK_D_FBD) {
				bdStr1 = "非备兑";
			}
			
			viewHolder.tv_item5.setText(bdStr1);
			
			char szmmlb = mListData.GetFieldValueCHAR(STEP_Define.STEP_MMLB);
			String mmlbStr1 = ""; //（买入或卖出）
			if (szmmlb == PTK_Define.PTK_D_Buy) {
				mmlbStr1 = "买入";
				viewHolder.tv_item6.setTextColor(ViewTools.getColor(1.0f));
			} else if(szmmlb == PTK_Define.PTK_D_Sell) {
				mmlbStr1 = "卖出";
				viewHolder.tv_item6.setTextColor(ViewTools.getColor(-1.0f));
			}
			viewHolder.tv_item6.setText(mmlbStr1);
			
			String qqbddm = mListData.GetFieldValueString(STEP_Define.STEP_BDDM);
			viewHolder.tv_item7.setText(qqbddm);
			
			String xqjg = mListData.GetFieldValueString(STEP_Define.STEP_XQJG);
			viewHolder.tv_item8.setText(xqjg);
			
			String xqsl = mListData.GetFieldValueString(STEP_Define.STEP_XQSL);
			viewHolder.tv_item9.setText(xqsl);
			
			float fbdsfsl = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_BDJFSL));
			String temp = "";
			if (fbdsfsl >= 0.0)
			{
				temp = String.format("应收%.2f", fbdsfsl);
				viewHolder.tv_item10.setTextColor(ViewTools.getColor(1.0f));
			}else
			{
				temp = String.format("应付%.2f", Math.abs(fbdsfsl));
				viewHolder.tv_item10.setTextColor(ViewTools.getColor(-1.0f));
			}
			viewHolder.tv_item10.setText(temp);
			
			float jsje = STD.StringToValue(mListData.GetFieldValueString(STEP_Define.STEP_JSJE));
			temp = "";
			if (jsje >= 0.0)
			{
				temp = String.format("应收%.2f", jsje);
				viewHolder.tv_item11.setTextColor(ViewTools.getColor(1.0f));
			}else
			{
				temp = String.format("应付%.2f", Math.abs(jsje));
				viewHolder.tv_item11.setTextColor(ViewTools.getColor(-1.0f));
			}
			viewHolder.tv_item11.setText(temp);
		}
		
		return convertView;
	}
	
	public static class ViewHolder {

		TextView tv_item1;	//期权名称	
		TextView tv_item2; //持仓
		TextView tv_item3; // 可用
		TextView tv_item4;
		TextView tv_item5;
		TextView tv_item6;
		TextView tv_item7;
		TextView tv_item8;
		TextView tv_item9;
		TextView tv_item10;
		TextView tv_item11;
	}
	
	
	

}
