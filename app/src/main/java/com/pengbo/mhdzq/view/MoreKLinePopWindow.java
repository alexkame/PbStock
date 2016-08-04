package com.pengbo.mhdzq.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.KLinePopWindowAdapter;

public class MoreKLinePopWindow extends PopupWindow{


	private Context context;
	private PopupWindow mPopWindow;
	private PopWindowCallBack popcallback;
	private ListView lv;

	public MoreKLinePopWindow(Context con, View mRadioButton,boolean isLandscape) {
		this.context = con;
		mPopWindow = new PopupWindow(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view_window = inflater.inflate(R.layout.zq_market_detail_kline_more_popwindow, null);
		mPopWindow.setContentView(view_window);
		
		int width = mRadioButton.getWidth();
		int height = mRadioButton.getHeight();
		if(!isLandscape){
			mPopWindow.setWidth(width);
			mPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}else{
			mPopWindow.setWidth(width*8/10);
			mPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
		
		
		
	
		
		mPopWindow.setBackgroundDrawable(new ColorDrawable(0));
		//mPopWindow.setBackgroundDrawable(new ColorDrawable(R.drawable.zq_detail_rb_3minnute));
		// mPopWindow.setAnimationStyle(R.style.PopMenuAnimation);
		mPopWindow.setFocusable(true);// 设置焦距 让点其他条目或后退键时条目消失

		// 设置window弹出来的位置
		int[] location = new int[2];
		// 获取一个空间在手机屏幕上的位置
		mRadioButton.getLocationOnScreen(location);
		int x = location[0];

		mRadioButton.getLocationOnScreen(location);
		int y = location[1];

		if(!isLandscape){
			mPopWindow.showAtLocation(mRadioButton, Gravity.LEFT | Gravity.TOP, x-20, y + mRadioButton.getHeight());
		}else{
			
			mPopWindow.showAtLocation(mRadioButton, Gravity.NO_GRAVITY, x+50, y-mPopWindow.getHeight());
		}
		
		lv = (ListView) view_window.findViewById(R.id.zq_detail_kline_more_popwindow_listview);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				popcallback.popwindowdo(position);
				mPopWindow.dismiss();
			}
		});
	}

	public interface PopWindowCallBack {

		public void popwindowdo(int index);
	}

	public void setPopWindowCallback(PopWindowCallBack popwindowcallback) {
		this.popcallback = popwindowcallback;
	}

	public void setContent(KLinePopWindowAdapter adapter) {
		lv.setAdapter(adapter);
	}
}
