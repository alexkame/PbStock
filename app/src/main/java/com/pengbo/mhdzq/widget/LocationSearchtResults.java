package com.pengbo.mhdzq.widget;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.FastSearchAdapter;
import com.pengbo.mhdzq.app.MyApp;
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

public class LocationSearchtResults extends PopupWindow {

	private View contentView;
	private ListView lvResults;
	public static final int MAX_COUNT_SEARCH_RESULT = 8; // 最多显示的搜索结果

	// 下拉列表项被单击的监听器
	private OnItemClickListener listener;
	private MyApp mMyApp;
	//private ArrayList<SearchDataItem> mHistorySearchList; // 历史搜索结果
	private ArrayList<SearchDataItem> mSearchResultList; // 搜索结果
	private ArrayList<SearchDataItem> mSearchStockList;// 被搜索的所有合约

	private FastSearchAdapter mListAdapter;
	private EditText mZqCodeEdit;

	public LocationSearchtResults(MyApp myApp, final Context context,
			LayoutInflater layoutInflater, ArrayList<SearchDataItem> list1,
			ArrayList<SearchDataItem> list2, EditText edit,
			OnItemClickListener itemClickListener) {
		super(context);

		mSearchResultList = list1;
		mSearchStockList = list2;
		mZqCodeEdit = edit;

		this.mMyApp = myApp;
		this.listener = itemClickListener;

		contentView = layoutInflater.inflate(R.layout.zq_dropdown_list, null);
		this.setContentView(contentView); // 设置悬浮窗体内显示的内容View

		this.setWidth(mZqCodeEdit.getWidth() - 5); // 设置悬浮窗体的宽度
		// this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT); // 设置悬浮窗体的高度
		//this.setHeight(200); // 设置悬浮窗体的高度
		//this.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 设置悬浮窗体背景
//		// this.setAnimationStyle(R.style.PopupAnimation);
		//this.setFocusable(true); // menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
		//this.setOutsideTouchable(true); // 可以再外部点击隐藏掉PopupWindow
//
		lvResults = (ListView) contentView.findViewById(R.id.zq_lvResults);
		mListAdapter = new FastSearchAdapter(mMyApp, context, mSearchResultList ,mSearchStockList);
		lvResults.setAdapter(mListAdapter);
//
//		// 如果PopupWindow中的下拉列表项被单击了
//
//		// 则通知外部的下拉列表项单击监听器并传递当前单击项的数据
		lvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (listener != null) {

				}
			}
		});

		mZqCodeEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

//			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				doSearch(mZqCodeEdit.getText().toString());

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void doSearch(String searchText) {
		this.mSearchResultList.clear();
		if (searchText.length() == 0) {
			mListAdapter.notifyDataSetChanged();
			return;
		}
		// this.mListView_history.setVisibility(View.GONE);
		// this.mTVClearHistory.setVisibility(View.GONE);
		// this.mTVHistoryTitle.setVisibility(View.GONE);

		String keyStr = "";
		if (mSearchStockList == null)
			return;

		for (int i = 0; i < this.mSearchStockList.size(); i++) {
			SearchDataItem item = mSearchStockList.get(i);
			char ch = searchText.charAt(0);// 取出第一个字符
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {

				keyStr = item.code;
				if (keyStr.toUpperCase().startsWith(searchText.toUpperCase())
						|| keyStr.toUpperCase().endsWith(
								searchText.toUpperCase())) {
					mSearchResultList.add(item);
					continue;
				}

				keyStr = item.extcode;
				if (keyStr.toUpperCase().startsWith(searchText.toUpperCase())
						|| keyStr.toUpperCase().endsWith(
								searchText.toUpperCase())) {
					mSearchResultList.add(item);
					continue;
				}

				keyStr = item.jianpin;
				if (keyStr.toUpperCase().contains(searchText.toUpperCase())) {
					mSearchResultList.add(item);
				}
			} else if (ch >= '0' && ch <= '9') {
				keyStr = item.code;
				if (keyStr.startsWith(searchText)
						|| keyStr.endsWith(searchText)) {
					mSearchResultList.add(item);
				}
			} else {
				keyStr = item.name;
				if (keyStr.contains(searchText)) {
					mSearchResultList.add(item);
				}
			}
			if (mSearchResultList.size() >= MAX_COUNT_SEARCH_RESULT) {
				break;
			}
		}

		mListAdapter.notifyDataSetChanged();

	}
}
