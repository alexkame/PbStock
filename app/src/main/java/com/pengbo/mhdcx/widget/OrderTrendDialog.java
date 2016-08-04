package com.pengbo.mhdcx.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.view.TrendLineView;

public class OrderTrendDialog implements OnClickListener{
	private Context context;
	private Dialog dialog;
	private ImageView mClose;
	private Display mDisplay;
	private int mPosX;
	private int mPosY;
	private FrameLayout mTrendLayout;
	private TextView mTV_StockName;
	private TextView mTV_StockPrice;
	private TextView mTV_OptionName;
	private TextView mTV_OptionPrice;
	private TrendLineView 	mTrendLineView;
	private TagLocalStockData mOptionData;
	private TagLocalStockData mStockData;
	
	public OrderTrendDialog(Context con, int posx, int posy) {
	
		this.context = con;
		mPosX = posx;
		mPosY = posy;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mDisplay = windowManager.getDefaultDisplay();
	
	}
	
	public OrderTrendDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_order_trend_dialog, null);

		// 设置Dialog最小宽度为屏幕宽度
		view.setMinimumWidth(mDisplay.getWidth());

		mClose = (ImageView) view.findViewById(R.id.order_trend_close);
		mClose.setOnClickListener(this);
		
		mTV_StockName = (TextView) view.findViewById(R.id.order_trend_tv_stockname);
		mTV_StockPrice = (TextView) view.findViewById(R.id.order_trend_tv_stockprice);
		
		mTV_OptionName = (TextView) view.findViewById(R.id.order_trend_tv_optionname);
		mTV_OptionPrice = (TextView) view.findViewById(R.id.order_trend_tv_optionprice);
		
		mTrendLayout = (FrameLayout) view.findViewById(R.id.trend_layout);
		mTrendLineView = new TrendLineView(context, false);
		
		mTrendLayout.addView(mTrendLineView);
        mTrendLineView.requestFocus();
        //mTrendLineView.updateData(mOptionData);
		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.TrendDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = mPosX;
		lp.y = mPosY;
		dialogWindow.setAttributes(lp);

		return this;
	}

	public OrderTrendDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public OrderTrendDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}
	
	public OrderTrendDialog updateOptionData(TagLocalStockData optionData, TagLocalStockData stockData)
	{
		mOptionData = optionData;
		mStockData = stockData;
		updateOptionView();
		mTrendLineView.updateData(optionData, stockData);
		mTrendLineView.updateAllView();
		return this;
	}
	
	//更新标的信息显示
	private void updateOptionView()
	{
		// 合约名称，涨跌幅
		if (mOptionData != null) {
			mTV_OptionName.setText(ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_NAME_ANSI));

			// 现价幅度等
			String nowPrice = ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_NOW);
			String fd = ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_ZDF_SIGN);
			mTV_OptionPrice.setText(String.format("%s %s", nowPrice, fd));
			mTV_OptionPrice.setTextColor(ViewTools.getColorByFieldID(mOptionData,
					Global_Define.FIELD_HQ_NOW));

		} else {
			mTV_OptionName.setText(Global_Define.STRING_VALUE_EMPTY);
			mTV_OptionPrice.setText(Global_Define.STRING_VALUE_EMPTY);
		}
		
		// 标的名称，涨跌幅
		if (mStockData != null) {
			mTV_StockName.setText(ViewTools.getStringByFieldID(mStockData,
					Global_Define.FIELD_HQ_NAME_ANSI));

			// 现价幅度等
			String nowPrice = ViewTools.getStringByFieldID(mStockData,
					Global_Define.FIELD_HQ_NOW);
			String fd = ViewTools.getStringByFieldID(mStockData,
					Global_Define.FIELD_HQ_ZDF_SIGN);
			mTV_StockPrice.setText(String.format("%s %s", nowPrice, fd));
			mTV_StockPrice.setTextColor(ViewTools.getColorByFieldID(mStockData,
					Global_Define.FIELD_HQ_NOW));

		} else {
			mTV_StockName.setText(Global_Define.STRING_VALUE_EMPTY);
			mTV_StockPrice.setText(Global_Define.STRING_VALUE_EMPTY);
		}
	}

	public void show() {
		dialog.show();
	}

	public void hide() {
		dialog.hide();
	}

	public void dismiss() {
		dialog.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_trend_close:
			dismiss();
			break;

		default:
			break;
		}
	}
		

	
}
