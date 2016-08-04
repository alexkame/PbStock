package com.pengbo.mhdzq.main_activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.utils.HttpUtil;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.NewsAdapter;
import com.pengbo.mhdzq.adapter.NewsClassifyHLVAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.data.NewsOneClassty;
import com.pengbo.mhdzq.data.NewsTitle;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.widget.HorizontalListView;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NewsActivity extends HdActivity implements OnCheckedChangeListener {

	private Context mContext;
	private RadioGroup mNewsReserchs;
	private ListView mListView;
	private NewsAdapter mListAdapter;
	private ArrayList<NewsTitle> mAllNews;
	private ArrayList<NewsTitle> mNews;
	// comment out this code because of change, maybe used in future.
	// private ArrayList<NewsTitle> imYanBaoNews;

	private HorizontalListView mHorizontal;
	private NewsClassifyHLVAdapter mNewsHlvAdapter;
	protected static final int UPDATE_UI = 1;

	private String mVersionName;
	private RadioGroup mRadioGroup;
	private Intent homeIntent;

	private String[] news_id;
	// comment out this code because of change, maybe used in future.
	// ,yanBao_id;
	private ArrayList<NewsOneClassty> mNewsOneClassties;//
	private ArrayList<NewsOneClassty> mListData;
	private boolean mIsSetListTop = false;

	private WebView mWebView;
	private String mUrl_Yanbao = "";

	// 区分新闻，研报
	private int mIndex = 0;
	
	private ExecutorService cachedThreadPool;

	/**
	 * 刷新数据
	 */
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE_UI:
				mListData.clear();
				mListData.addAll(mNewsOneClassties);
				// closeProgress();
				if (mListAdapter == null) {
					mListAdapter = new NewsAdapter(mContext, mListData);
				} else {

					mListAdapter.setData(mListData);
					mListAdapter.notifyDataSetChanged();

					if (mIsSetListTop) {
						// 新闻分类反复切换时应该每次都从最新最上开始显示
						mListView.setSelection(0);
						mIsSetListTop = false;
					}
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_news_activity);
		mContext = this;
		AppActivityManager.getAppManager().addActivity(this);
		
		
		initData();
		ReadIniFile();
		initView();

		SetView();
	}
	@Override
	protected void onDestroy() {
		AppActivityManager.getAppManager().removeActivity(this);
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			// 退出确认对话框
			AppActivityManager.getAppManager().AppExit(true);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	private void initData() {
		mAllNews = new ArrayList<NewsTitle>();
		mNews = new ArrayList<NewsTitle>();
		mNewsOneClassties = new ArrayList<NewsOneClassty>();
		mListData = new ArrayList<NewsOneClassty>();

		// comment out this code because of change, maybe used in future.
		// imYanBaoNews = new ArrayList<NewsTitle>();
	}

	// 进入期权首页
	private void showQiQuanPage(int id) {

		if (!PreferenceEngine.getInstance().getAPKVersion(mVersionName)) {
			// 不是第一次打开直接跳转到主页
			// 保存已经打开
			PreferenceEngine.getInstance().saveAPKVersion(mVersionName);
			//初始保存默认配置
			saveObjectToShared();
		}
		Intent intent = new Intent(NewsActivity.this, MainTabActivity.class);
		intent.putExtra("mId", id);
		startActivity(intent);
	}
	

	/**
	 * 保存一个默认的筛选条件 
	 * 行情合约筛选器中默认条件
	 */
	private void saveObjectToShared() {
		ScreenCondition sc=new ScreenCondition();
		sc.setCode(ScreenCondition.SCREEN_ALL_STOCK_CODE);
		sc.setMarket((short) 0);
		sc.setName("全部标的");
		sc.setGridViewId(4);
		sc.setField_hq_zdf(0.05f);
		sc.setLeverId(3);
		sc.setVitality(1);

		String mTiaoJianInfo = sc.getCode() + "|" + sc.getMarket() + "|" + sc.getName() + "|" + sc.getGridViewId() + "|" + sc.getField_hq_zdf() + "|" + sc.getLeverId() + "|"
				+ sc.getVitality();
		PreferenceEngine.getInstance().saveHQQueryCondition(mTiaoJianInfo);
	}
	
	
	/**
	 * 初始化View
	 */
	private void initView() {
		mRadioGroup = (RadioGroup) findViewById(R.id.rb_radioGroup);
		rb_setting = (RadioButton) findViewById(R.id.rb_setting);
		rb_setting.setChecked(true);
		zhengQuanIntent = new Intent(this, ZhengQuanActivity.class);
		homeIntent = new Intent(this, MainActivity.class);
		mVersionName = MyApp.getInstance().getPackageInfo().versionName;

		mNewsReserchs = (RadioGroup) this.findViewById(R.id.rgroup_news);
		mListView = (ListView) this.findViewById(R.id.lv_news_first);
//		mRadioButton1 = (RadioButton) this.findViewById(R.id.rbutton_news);
//		mRadioButton2 = (RadioButton) this.findViewById(R.id.rbutton_report);
		mHorizontal = (HorizontalListView) this
				.findViewById(R.id.news_hlv_listview);
		mNewsReserchs.setOnCheckedChangeListener(this);
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_qiquan: {
					MyApp.getInstance().setCurrentHQType(AppConstants.HQTYPE_QQ);
					showQiQuanPage(0);
					finish();
				}
					break;
				case R.id.rb_zhengquan: {
					MyApp.getInstance().setCurrentHQType(AppConstants.HQTYPE_ZQ);
					Intent intent1 = new Intent();
					intent1.putExtra("id", KeyDefine.KEY_MARKET_STOCK_SHSZ);
					intent1.setClass(getApplicationContext(), ZhengQuanActivity.class);
					startActivity(intent1);
					finish();
				}
					break;
				case R.id.rb_home:{
					startIntent(homeIntent, MainActivity.class);
					finish();
				}
					break;
				}
			}

			private void startIntent(Intent intent, Class clz) {
				if (intent != null)
					startActivity(intent);
				else {
					startActivity(new Intent(getApplicationContext(), clz));
				}
			}
		});

		mWebView = (WebView) this.findViewById(R.id.webView_yanbao);
		mWebView.getSettings().setDisplayZoomControls(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setVisibility(View.GONE);
		mWebView.loadUrl(mUrl_Yanbao);

		YanBaoWebViewClient client = new YanBaoWebViewClient();
		mWebView.setWebViewClient(client);
		mWebView.removeJavascriptInterface("searchBoxJavaBredge_");
	}

	/**
	 * 设置Adapter
	 */
	private void SetView() {
//		mRadioButton1.setText(mAllNews.get(0).getTitle());
//		mRadioButton2.setText(mAllNews.get(1).getTitle());

		if (mNews.size() > 0) {
			news_id = mNews.get(0).mIDs;
		}

		// comment out this code because of change, maybe used in future.
		// if(imYanBaoNews.size()>0){
		// yanBao_id = imYanBaoNews.get(0).mIDs;
		// }

		mNewsHlvAdapter = new NewsClassifyHLVAdapter(mContext, mNews,
				mHorizontal);
		mHorizontal.setAdapter(mNewsHlvAdapter);
		mHorizontal.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mIsSetListTop = true;

				mNewsHlvAdapter.setSelectIndex(position);
				mNewsHlvAdapter.notifyDataSetChanged();
				// showProgress();

				if (mIndex == 0) {
					news_id = mNews.get(position).getID();
					RequestHttpUtil(news_id, mNewsOneClassties, false);
				}
				// comment out this code because of change, maybe used in
				// future.
				// else {
				// yanBao_id = imYanBaoNews.get(position).getID();
				// RequestHttpUtil(yanBao_id,mNewsOneClassties,false);
				// }
			}
		});

		// 默认 给这几个属性一个值 即进来查询所有的时候的值
		// showProgress();
		mListAdapter = new NewsAdapter(mContext, mListData);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Intent intent = new Intent();
			String newid = mListData.get(position).getID();
			intent.putExtra("news_id", newid);
			intent.setClass(NewsActivity.this, NewsDetailActivity.class);
			startActivity(intent);

		}

	};
	private RadioButton rb_setting;
	private Thread myThread;
	private Intent zhengQuanIntent;

	@Override
	protected void onResume() {
		super.onResume();
		rb_setting.setChecked(true);
		mIsSetListTop = false;
		if (mIndex == 0) {
			// showProgress();
			RequestHttpUtil(news_id, mNewsOneClassties, false);
		}
		// comment out this code because of change, maybe used in future.
		// else{
		// RequestHttpUtil(yanBao_id,mNewsOneClassties,false);
		// }

	}

	/**
	 * 读取配置文件
	 */
	private void ReadIniFile() {

		MIniFile iniNews = new MIniFile();
		iniNews.setFilePath(getApplicationContext(), MyApp.DEFAULT_CONFIGPATH);
		String key = "news";
		String strAllNews = "";
		// title=新闻,1001|研报,1002
		strAllNews = iniNews.ReadString(key, "title", "");
		String temp = "";
		for (int i = 0; i < 10; i++) {
			temp = STD.GetValue(strAllNews, i + 1, '|');
			if (temp.isEmpty()) {
				break;
			}

			String title = STD.GetValue(temp, 1, ',');
			String substr = temp.substring(temp.indexOf(",") + 1);

			String[] ids = substr.split(",");
			NewsTitle n = new NewsTitle(title, ids);
			mAllNews.add(n);
		}
		// menu=财经资讯,101|外汇资讯,501|现货市场,402|期货市场,401,4001|期指市场,301
		String strAllMenus = "";
		strAllMenus = iniNews.ReadString(key, "menu", "");
		String temp2 = "";
		for (int i = 0; i < 10; i++) {
			temp2 = STD.GetValue(strAllMenus, i + 1, '|');
			if (temp2.isEmpty()) {
				break;
			}
			String title = STD.GetValue(temp2, 1, ',');
			String substr = temp2.substring(temp2.indexOf(",") + 1);

			String[] ids = substr.split(",");
			mNews.add(new NewsTitle(title, ids));
		}

		// cjrlurl=http://abchina-reg.pobo.net.cn/view/caijingrili.html
		//
		mUrl_Yanbao = iniNews.ReadString(key, "cjrlurl", "");

		// menu1=财经日历,506301
		// 注 研报多的话 以"|" 分开使用下面循环
		// comment out this code because of change, maybe used in future.
		/*
		 * String imYaobaoAllMenus = ""; imYaobaoAllMenus =
		 * iniNews.ReadString(key, "menu1", "");
		 * if(!imYaobaoAllMenus.isEmpty()){ String temp3 = ""; for (int i = 0; i
		 * < 50; i++) { temp3 = STD.GetValue(imYaobaoAllMenus, i + 1, '|'); if
		 * (temp3.isEmpty()) { break; } String title = STD.GetValue(temp3, 1,
		 * ','); String substr = temp3.substring(temp3.indexOf(",") + 1);
		 * 
		 * String[] ids = substr.split(","); imYanBaoNews.add(new
		 * NewsTitle(title, ids)); } }
		 */

	}

	/**
	 * 请求数据 HttpUtil.getNewss(news_id, mNewsOneClassties, false);
	 */
	private void RequestHttpUtil(final String[] news_id,
			final List<NewsOneClassty> mNewsOneClassties, final boolean flag) {
		if(cachedThreadPool == null)
		{
			cachedThreadPool = Executors.newCachedThreadPool();  
		}
		
		if(!cachedThreadPool.isShutdown())
		{
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					{
						HttpUtil.getNewss(news_id, mNewsOneClassties, flag);
						
						mHandler.sendEmptyMessage(UPDATE_UI);
					}
				} 
			});
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {

		case R.id.rbutton_news:
//			System.out.println("新闻------------------####");
			mIndex = 0;
			UpdateView(0);
			break;

		case R.id.rbutton_report:
			// System.out.println("研报------------------####");
			mIndex = 1;
			UpdateView(1);
			break;

		default:
			break;
		}

	}

	/**
	 * 更新新闻、研报
	 * 
	 * @param index
	 */
	private void UpdateView(int index) {
		switch (index) {
		case 0:
			if (mNews.size() > 1) {
				mNewsHlvAdapter.setNewsTitles(mNews);
				mNewsHlvAdapter.notifyDataSetChanged();
				mHorizontal.setVisibility(View.VISIBLE);
			} else {
				mHorizontal.setVisibility(View.GONE);
			}
			mListView.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);
			mIsSetListTop = true;
			// showProgress();
			RequestHttpUtil(news_id, mNewsOneClassties, false);
			break;
		case 1:
		// comment out this code because of change, maybe used in future.
		// if(imYanBaoNews.size()>1){
		// mNewsHlvAdapter.setNewsTitles(imYanBaoNews);
		// mNewsHlvAdapter.notifyDataSetChanged();
		// mHorizontal.setVisibility(View.VISIBLE);
		// }else
		{
			mHorizontal.setVisibility(View.GONE);
		}
			mWebView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			// 没有数据
			// comment out this code because of change, maybe used in future.
			// RequestHttpUtil(yanBao_id,mNewsOneClassties,false);

			break;
		default:
			break;
		}
	}

	private class YanBaoWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			return false;
		};

	}
	
	

}
