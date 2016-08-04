package com.pengbo.mhdcx.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.trade.data.TradeAccountInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AccountDialogAdapter extends BaseAdapter {
	private ArrayList<TradeAccountInfo> datas;	

	private Context context;
	private LayoutInflater mInflater;
	private static AccountDialogAdapter mAdapter;

	public AccountDialogAdapter( Context con, ArrayList<TradeAccountInfo>data) {
		context = con;
		datas = data;
		mAdapter = this;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return datas.size();
	}
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			viewHolder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.view_mydialog_account_item, null);

			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.txt_account_dialog_title);
			viewHolder.tv_account = (TextView) convertView.findViewById(R.id.txt_account_dialog_name);

			viewHolder.btn_delete = (Button) convertView.findViewById(R.id.btn_account_dialog_del);
			viewHolder.btn_delete.setOnClickListener(new BtnDeleteListener(position, viewHolder));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		TradeAccountInfo info = (TradeAccountInfo)getItem(position);
		viewHolder.tv_name.setText(info.mName);
		viewHolder.tv_account.setText(info.mAccout);
		
		return convertView;
	}
	
	class BtnDeleteListener implements OnClickListener {
		private int mPosition;
		private ViewHolder mViewHolder;

		public BtnDeleteListener(int position, ViewHolder viewHolder) {
			this.mPosition = position;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			if (v == mViewHolder.btn_delete) {
				MyApp.getInstance().RemoveFromMyTradeAccount(mPosition);
				datas.remove(mPosition);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	class ViewHolder{
		TextView tv_name;		
		TextView tv_account;
		Button btn_delete;
	}

}
