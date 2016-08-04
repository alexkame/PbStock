package com.pengbo.mhdzq.widget;

import java.util.ArrayList;
import java.util.Map;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.FastSearchAdapter;
import com.pengbo.mhdzq.adapter.ZqGDZHAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CPbGDZHData;
import com.pengbo.mhdzq.data.SearchDataItem;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ZqGdPopWindow extends PopupWindow {

	private View contentView;
	private ListView lvResults;
	public static final int MAX_COUNT_SEARCH_RESULT = 8; // 最多显示的搜索结果

	// 下拉列表项被单击的监听器
	private OnItemClickListener listener;
	private MyApp mMyApp;
	//private ArrayList<SearchDataItem> mHistorySearchList; // 历史搜索结果
	private ArrayList<CPbGDZHData> mGdZhList; // 股东帐户


	private ZqGDZHAdapter mListAdapter;
	private View mZqCodeEdit;

	public ZqGdPopWindow(MyApp myApp, final Context context,
			LayoutInflater layoutInflater, ArrayList<CPbGDZHData> list1,
			View edit,
			OnItemClickListener itemClickListener) {
		super(context);

		mGdZhList = list1;

		mZqCodeEdit = edit;
		this.mMyApp = myApp;
		this.listener = itemClickListener;

		contentView = layoutInflater.inflate(R.layout.zq_gdzh_list, null);
		this.setContentView(contentView); // 设置悬浮窗体内显示的内容View

		this.setWidth(mZqCodeEdit.getWidth()); // 设置悬浮窗体的宽度
		// this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT); // 设置悬浮窗体的高度
		//this.setHeight(200); // 设置悬浮窗体的高度
		//this.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 设置悬浮窗体背景
//		// this.setAnimationStyle(R.style.PopupAnimation);
		this.setFocusable(true); // menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
		//this.setOutsideTouchable(true); // 可以再外部点击隐藏掉PopupWindow
//
		lvResults = (ListView) contentView.findViewById(R.id.zq_gdzhlist);
		mListAdapter = new ZqGDZHAdapter(mMyApp, context,mGdZhList);
		lvResults.setAdapter(mListAdapter);
//
//		// 如果PopupWindow中的下拉列表项被单击了
//
//		// 则通知外部的下拉列表项单击监听器并传递当前单击项的数据
		lvResults.setOnItemClickListener(itemClickListener);


	}


}
