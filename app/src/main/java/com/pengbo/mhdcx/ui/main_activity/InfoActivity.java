package com.pengbo.mhdcx.ui.main_activity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 消息页
 * 
 * @author pobo
 * 
 */
public class InfoActivity extends HdActivity implements OnClickListener,
		OnItemClickListener {

	public TextView head_tv_option,// 左边的编辑按钮;
			head_btn_searchlist;// 右边的搜索期权列表
	// 消息 处理
	public static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market_info);
		initView();

	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		head_btn_searchlist = (TextView) this
				.findViewById(R.id.header_right_search);
		head_tv_option = (TextView) this
				.findViewById(R.id.header_middle_textview);

		head_tv_option.setText("消息");
		head_btn_searchlist.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onClick(View v) {
		
	}

}
