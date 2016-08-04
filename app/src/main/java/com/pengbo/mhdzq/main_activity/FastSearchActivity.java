package com.pengbo.mhdzq.main_activity;
import java.util.ArrayList;
import org.apache.http.util.EncodingUtils;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.FastSearchAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.SearchDataItem;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class FastSearchActivity extends HdActivity implements OnClickListener {

	public static final int MAX_COUNT_SEARCH_RESULT = 100; //最多显示的搜索结果
	
	public static final String FILENAME_SEARCH_HISTORY = "xh_searchhistory.dat";
	private TextView mCancel;
	private EditText mEditTextSerch;
	private ListView mListView;
	private ListView mListView_history;
	private TextView mTVHistoryTitle, mTVClearHistory;
	private FastSearchAdapter mListAdapter;
	private FastSearchAdapter mListAdapter_history;
	
	private ArrayList<SearchDataItem> mHistorySearchList; //历史搜索结果
	private ArrayList<SearchDataItem> mSearchResultList; // 搜索结果
	private ArrayList<SearchDataItem> mSearchStockList;//被搜索的所有合约
	private MyApp mMyApp;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_fast_search_activity);
		initData();
		initView();
		initListView();
	}
	
	private void initData()
	{
		mMyApp = (MyApp) getApplication();
		mSearchStockList = new ArrayList<SearchDataItem>();
		mSearchStockList = mMyApp.getSearchCodeArray();
		
		mSearchResultList = new ArrayList<SearchDataItem>();
		
		this.initHistorySearchResultFromFile();
	}

	private void initView() {
		mCancel = (TextView) this.findViewById(R.id.fast_search_cancel);
		
		mEditTextSerch = (EditText) this.findViewById(R.id.fast_search_edittext);
		mCancel.setOnClickListener(this);
		mEditTextSerch.setOnClickListener(this);
		mEditTextSerch.addTextChangedListener(new TextWatcher() {           
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
            	doSearch(mEditTextSerch.getText().toString());
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {                  
            }  
              

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}  
        });  
	}
	
	
	private void initListView() {

		if (mListView== null) {
			mListView= (ListView) findViewById(R.id.fast_search_lv);
			mListAdapter = new FastSearchAdapter(mMyApp, getApplicationContext(), mSearchResultList,null);
			mListView.setAdapter(mListAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					SearchDataItem item = mSearchResultList.get(pos);
					addToHistory(item);//添加到历史搜索列表
					
					mMyApp.mCurrentStockArray.clear();
//					Intent mIntent = new Intent();
////					mIntent.setClass(FastSearchActivity.this, XianHuoDetailActivity.class);
//
////					TagCodeInfo optionCodeInfo = new TagCodeInfo(item.market, item.code, item.group, item.name);
////					XianHuoDetailActivity.mOptionCodeInfo = optionCodeInfo;
//
////					mMyApp.setCurrentOption(optionCodeInfo);
////					startActivity(mIntent);
				}
//				
			});
		}
		if (mListView_history == null) {
			mListView_history= (ListView) findViewById(R.id.lv_fast_search_history);

			mListAdapter_history = new FastSearchAdapter(mMyApp, this, mHistorySearchList,null);
			mListView_history.setAdapter(mListAdapter_history);
//			mListView_history.setOnItemClickListener(new OnItemClickListener(){
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int pos, long arg3) {
//					SearchDataItem item = mHistorySearchList.get(pos);
//					
//					mMyApp.mCurrentStockArray.clear();
//					Intent mIntent = new Intent();
//					mIntent.setClass(FastSearchActivity.this, XianHuoDetailActivity.class);
//
//					TagCodeInfo optionCodeInfo = new TagCodeInfo(item.market, item.code, item.group, item.name);
//					XianHuoDetailActivity.mOptionCodeInfo = optionCodeInfo;
//
//					mMyApp.setCurrentOption(optionCodeInfo);
//					startActivity(mIntent);
//				}
//			});
		}
		mTVHistoryTitle = (TextView) this.findViewById(R.id.tv_history);
		mTVHistoryTitle.setVisibility(View.VISIBLE);
		mTVClearHistory = (TextView) this.findViewById(R.id.tv_clear_history);
		mTVClearHistory.setOnClickListener(this);
		mTVClearHistory.setVisibility(View.VISIBLE);
	}
	
	private int initHistorySearchResultFromFile()
	{
		if (mHistorySearchList == null)
		{
			mHistorySearchList = new ArrayList<SearchDataItem>();
		}
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(FILENAME_SEARCH_HISTORY);
		byte[] data = new byte[size + 1];
		
		int ret = file.readFile(FILENAME_SEARCH_HISTORY, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明自选股文件不存在
			return -1;
		}

		// 解析添加自选股数据
		int offset = 0;
		// 个数
		int num = MyByteBuffer.getShort(data, offset);
		offset += 2;

		for (int i = 0; i < num; i++) {
			short market = MyByteBuffer.getShort(data, offset);
			offset += 2;

			int len = STD.strlen(data, offset, Global_Define.HQ_CODE_LEN);
			String code = new String(data, offset, len);
			offset += Global_Define.HQ_CODE_LEN;

			len = MyByteBuffer.getShort(data, offset);
			offset += 2;

			byte[] user = new byte[len];
			MyByteBuffer.getBytes(data, offset, user, 0, len);
			String name = EncodingUtils.getString(user, "UTF-8");
			offset += len;

			SearchDataItem item = new SearchDataItem();
			item.market = market;
			item.code = code;
			item.name = name;
			mHistorySearchList.add(item);
		}
		return 0;
	}
	
	private void addToHistory(SearchDataItem item)
	{
		if (mHistorySearchList == null)
		{
			mHistorySearchList = new ArrayList<SearchDataItem> ();
		}
		boolean bNeedAdd = true;
		for (int i = 0; i < mHistorySearchList.size(); i++)
		{
			if(item.code.equalsIgnoreCase(mHistorySearchList.get(i).code) 
					&& item.market == mHistorySearchList.get(i).market)
			{
				bNeedAdd = false;
				break;
			}
		}
		if (bNeedAdd)
		{
			mHistorySearchList.add(item);
		}
		saveHistorySearchResultToFile();
	}
	
	private void clearHistorySearchResultFromFile()
	{
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(FILENAME_SEARCH_HISTORY);

		if (size < 0)// 文件不存在
		{
			return;
		}
		file.deleteFile(FILENAME_SEARCH_HISTORY);
	}
	
	private void saveHistorySearchResultToFile()
	{
		int num = mHistorySearchList.size();
		byte[] data = new byte[2 + num * 100];
		int offset = 0;

		// 个数
		MyByteBuffer.putShort(data, offset, (short) num);
		offset += 2;

		//
		for (int i = 0; i < num; i++) {
			//
			MyByteBuffer.putShort(data, offset, (short) mHistorySearchList.get(i).market);
			offset += 2;

			//
			byte[] temp = mHistorySearchList.get(i).code.getBytes();
			int len = Math.min(temp.length, Global_Define.HQ_CODE_LEN);
			MyByteBuffer.putBytes(data, offset, temp, 0, len);
			offset += Global_Define.HQ_CODE_LEN;

			temp = mHistorySearchList.get(i).name.getBytes();
			int namelen = temp.length;
			//
			MyByteBuffer.putShort(data, offset, (short) namelen);
			offset += 2;

			MyByteBuffer.putBytes(data, offset, temp, 0, namelen);
			offset += namelen;
		}
		//
		FileService file = new FileService(this.getApplicationContext());
		try {
			//
			file.saveToFile(FILENAME_SEARCH_HISTORY, data, offset);
			// 保存成功
			L.i("FastSearchActivity", "saveHistorySearchResultToFile Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e("FastSearchActivity", "saveHistorySearchResultToFile Error!");
		}
	}
	
	private void doSearch(String searchText)
	{
		this.mSearchResultList.clear();
		if(searchText.length() == 0)
		{
			if(mHistorySearchList.size() > 0)
			{
				this.mListView_history.setVisibility(View.VISIBLE);
				this.mTVClearHistory.setVisibility(View.VISIBLE);
				this.mTVHistoryTitle.setVisibility(View.VISIBLE);
				mListAdapter.notifyDataSetChanged();
			}
			mListAdapter.notifyDataSetChanged();
			return;
		}
		this.mListView_history.setVisibility(View.GONE);
		this.mTVClearHistory.setVisibility(View.GONE);
		this.mTVHistoryTitle.setVisibility(View.GONE);
		
		String keyStr = "";
		if (mSearchStockList == null)
			return;
		
		for (int i = 0; i < this.mSearchStockList.size(); i++)
		{
			SearchDataItem item = mSearchStockList.get(i);
			char ch = searchText.charAt(0);//取出第一个字符
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {

				keyStr = item.code;
				if (keyStr.toUpperCase().startsWith(searchText.toUpperCase()) || keyStr.toUpperCase().endsWith(searchText.toUpperCase()))
	            {
	            	mSearchResultList.add(item);
	            	continue;
	            }
				
				keyStr = item.extcode;
				if (keyStr.toUpperCase().startsWith(searchText.toUpperCase()) || keyStr.toUpperCase().endsWith(searchText.toUpperCase()))
	            {
	            	mSearchResultList.add(item);
	            	continue;
	            }
	            
	            keyStr = item.jianpin;
	            if (keyStr.toUpperCase().contains(searchText.toUpperCase()))
	            {
	            	mSearchResultList.add(item);
	            }
	        }
	        else if (ch >= '0' && ch <= '9')
	        {
	        	keyStr = item.code;
	            if (keyStr.startsWith(searchText) || keyStr.endsWith(searchText))
	            {
	            	mSearchResultList.add(item);
	            }
	        }
	        else
	        {
	        	keyStr = item.name;
	        	if (keyStr.contains(searchText))
	        	{
	        		mSearchResultList.add(item);
	        	}
	        }
	        if (mSearchResultList.size() >= MAX_COUNT_SEARCH_RESULT) {
	            break;
	        }
		}
		
		mListAdapter.notifyDataSetChanged();
		
	}
	
	private void clearHistorySearch()
	{
		if(mHistorySearchList == null)
		{
			return;
		}
		mHistorySearchList.clear();
		mListAdapter_history.notifyDataSetChanged();
		clearHistorySearchResultFromFile();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMyApp.setHQPushNetHandler(null);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
//			case R.id.fast_search_cancel:
//				FastSearchActivity.this.finish();
//				break;
//			case R.id.tv_clear_history:
//			{
//				clearHistorySearch();
//			}
//				break;
			default:
				break;
		}
	}

}
