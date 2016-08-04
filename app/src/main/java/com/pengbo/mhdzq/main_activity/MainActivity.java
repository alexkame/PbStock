package com.pengbo.mhdzq.main_activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.utils.HttpUtil;
import com.pengbo.mhdzq.adapter.DrawerListAdapter;
import com.pengbo.mhdzq.adapter.HomePagerAdapter;
import com.pengbo.mhdzq.adapter.ZQHomeNewsAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.control.indecator.CirclePageIndicator;
import com.pengbo.mhdzq.data.DBZQHomeActivityMarket;
import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.data.NewsHomepager;
import com.pengbo.mhdzq.data.NewsOneClassty;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ScrollLayout;
import com.pengbo.mhdzq.view.ListViewForScrollView;
import com.pengbo.mhdzq.view.PageControlView;
import com.pengbo.mhdzq.widget.AlertDialog;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.zq_activity.ZQAboutActivity;
import com.pengbo.mhdzq.zq_activity.ZQDongZZYWebActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

import android.R.color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

/**
 * main activity for mainhome and the view
 */
public class MainActivity extends HdActivity implements OnClickListener {
	// private static final String TAG_CONTENT = "TAG_CONTENT";
	private Context mContext;
	private DrawerListAdapter mGridViewAdapter;
	/** 最下部分的选项部分容器 */
	private RadioGroup mRadioGroup;
	/** 最下部分选项部分容器中的“首页” */
	private RadioButton rb_home;
	private Intent newsIntent;
	/** 广告图片切换部分 */
	private ViewPager vp_homepage;
	/** 新闻列表部分 */
	public ListViewForScrollView lv_ForSv;
	/** 首页新闻选项块切换部分 */
	public RadioGroup rg_homepager;
	/** 新闻选项块部分的国内资讯部分 */
	public RadioButton rb_todaynews;
	/** 新闻选项块部分的金融财经部分 */
	public RadioButton rb_info;
	/** 新闻选项块部分的股指期货部分 */
	public RadioButton rb_things;
	/** 存放由首页广告图片生成的ImageView */
	private ArrayList<ImageView> list;// viewpager where source from imageview

	private int TIME_DELAY = 3000;
	/** 首页中的两张广告图片 */
	int[] imagesid = { R.drawable.rr, R.drawable.ee };

	private Handler mHandler;
	/** 上部标题栏部分的标题，如首页的“东北证券” */
	private TextView mMiddleTitle;
	/** 产部标题样部分的右边“关于”文本 */
	private TextView mTVAbout;
	public ImageButton btnMenu;
	/** 图片广告切换时下部对应的小圆点切换 */
	private CirclePageIndicator titleIndicator;
	/** 软件版本 */
	private String mVersionName;
	/** 上部标题左边的用户图片 */
	private ImageView mImgUser;
	/** 首页中间各个功能模块的容器 */
	private ScrollLayout mScrollLayout;
	/** 首页中间各个功能模块的数值，以DBZQHomeActivityMarket存放在ArrayList中 */
	private ArrayList<DBZQHomeActivityMarket> mUserMarkets;
	private MyApp mMyApp;
	/** 加载分页，首页中间功能模块部分切换时对应的小圆点 */
	private PageControlView pageControl;
	/** 新闻列表后面的“查看更多新闻” */
	private TextView tv_findmore;

	public static final String DBZQ_USER_MARKETFILE = "DBZQHomeActivityMarket.xml";
	public ArrayList<DBZQHomeActivityMarket> mMarketArray;
	/** 除去标题部分的其它内容 */
	private ScrollView svInMainhome;
	protected static final int UPDATE_UI = 1;
	private ArrayList<NewsOneClassty> mNewsOneClassties;
	/** 存放新闻选项中的三个字段信息 */
	private ArrayList<NewsHomepager> mNews;
	/** 向lv_ForSv中放入新闻数据 */
	private ZQHomeNewsAdapter mListAdapter;
	/** 动态线程池 */
	private ExecutorService cachedThreadPool;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mMyApp = (MyApp) this.getApplication();

		svInMainhome = (ScrollView) findViewById(R.id.scrollview_mainhome);
		svInMainhome.smoothScrollTo(0, 0);

		mScrollLayout = (ScrollLayout) findViewById(R.id.scrolllayout_home);
		pageControl = (PageControlView) findViewById(R.id.pageControl_home);
		tv_findmore = (TextView) findViewById(R.id.tv_findmore);

		tv_findmore.setOnClickListener(new OnClickListenerForMore());

		if (mUserMarkets == null) {
			mUserMarkets = new ArrayList<DBZQHomeActivityMarket>();
		}
		mUserMarkets.addAll(mMyApp.mDBZQHomeMenuMarkets);

		mVersionName = MyApp.getInstance().getPackageInfo().versionName;

		mNews = new ArrayList<NewsHomepager>();

		cachedThreadPool = Executors.newCachedThreadPool();

		ReadFile();

		initView();

		setData();

		setAdapter();

		bindViewPagerwithIndecator();

	}

	class OnClickListenerForMore implements OnClickListener {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(getApplicationContext(),
					NewsActivity.class));
		}

	}

	@Override
	public void onResume() {
		updateHomeMarket();
		rb_home.setChecked(true);
		vp_homepage.setCurrentItem(0);
		handleViewPagerRolling();
		rb_todaynews.setChecked(true);
		super.onResume();
	}

	private void updateHomeMarket() {
		mUserMarkets.clear();
		mUserMarkets.addAll(mMyApp.mDBZQHomeMenuMarkets);
		mScrollLayout.removeAllViews();
		pageControl.removeAllViews();
		/** 首页中部包含大量小模块的容器的页数 */
		int pageNo = mUserMarkets.size() / AppConstants.HOME_PAGE_ITEM_SIZE + 1;
		for (int i = 0; i < pageNo; i++) {
			GridView appPage = new GridView(this);
			/** 存放了首页中部各个子模块 */
			mGridViewAdapter = new DrawerListAdapter(getApplicationContext(),
					mUserMarkets, i);
			// get the "i" page data
			appPage.setAdapter(mGridViewAdapter);
			// 设置点击appPage时的背景色
			appPage.setSelector(R.color.transparent);
			appPage.setNumColumns(4);
			appPage.setOnItemClickListener(listener);

			mScrollLayout.addView(appPage);
		}
		pageControl.bindScrollViewGroup(mScrollLayout);
		mScrollLayout.setToScreen(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 退出确认对话框
			AppActivityManager.getAppManager().AppExit(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 读取配置文件defaultConfig.ini中[homepage]字段，
	 * 得到国内资讯,10101|金融财经,10301|股指期货,30101,30102. 存放到mNews集合中
	 */
	public void ReadFile() {
		MIniFile iniNews = new MIniFile();
		iniNews.setFilePath(getApplicationContext(), MyApp.DEFAULT_CONFIGPATH);

		String key = "homepage";
		// newsmenu=金融财经,10301|国内资讯,10101|东证活动,
		String strAllMenus = "";
		strAllMenus = iniNews.ReadString(key, "newsmenu", "");
		String temp = "";
		for (int i = 0; i < 10; i++) {
			temp = STD.GetValue(strAllMenus, i + 1, '|');
			if (temp.isEmpty()) {
				break;
			}
			String title = STD.GetValue(temp, 1, ',');
			String substr = temp.substring(temp.indexOf(",") + 1);
			String[] ids = substr.split(",");
			if (mNews == null) {
				mNews = new ArrayList<NewsHomepager>();
			}
			mNews.add(new NewsHomepager(title, ids));
		}
	}

	/** 初始化首页中的控件 */
	private void initView() {
		mMiddleTitle = (TextView) findViewById(R.id.tv_public_black_head_title_middle_name);
		mTVAbout = (TextView) findViewById(R.id.tv_public_black_head_title_right_blue_about);
		mTVQiQuan = (TextView) findViewById(R.id.tv_public_black_head_title_right_blue_qiquan);
		mImgUser = (ImageView) findViewById(R.id.img_public_black_head_title_left_user);

		mMiddleTitle.setVisibility(View.VISIBLE);
		if (AppConstants.IS_NEED_ABOUT_SHOW_IN_MAINPAGER) {
			mTVAbout.setVisibility(View.VISIBLE);
		} else {
			mTVAbout.setVisibility(View.GONE);
		}
		// 不是财通的
		if (!AppConstants.IS_TEXTVIEW_REQUEIRED) {
			mTVAbout.setOnClickListener(this);
		} else {
			mTVAbout.setVisibility(View.GONE);
			mTVQiQuan.setVisibility(View.VISIBLE);
		}
		// mTVAbout.setOnClickListener(this);
		mImgUser.setOnClickListener(this);

		vp_homepage = (ViewPager) findViewById(R.id.vp_homepage);

		lv_ForSv = (ListViewForScrollView) findViewById(R.id.zq_home_listview);

		rg_homepager = (RadioGroup) findViewById(R.id.rg_homepager);
		rb_todaynews = (RadioButton) findViewById(R.id.rb_todaynews);
		rb_info = (RadioButton) findViewById(R.id.rb_info);
		rb_things = (RadioButton) findViewById(R.id.rb_things);

		mRadioGroup = (RadioGroup) findViewById(R.id.rb_radioGroup);
		rb_home = (RadioButton) findViewById(R.id.rb_home);

		newsIntent = new Intent(this, NewsActivity.class);

		mRadioGroup.setOnCheckedChangeListener(new CheckedChangeListener());
		rb_home.setChecked(true);
	}

	class CheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb_qiquan: {
				MyApp.getInstance().setCurrentHQType(AppConstants.HQTYPE_QQ);
				showQiQuanPage(0);
			}
				break;
			case R.id.rb_zhengquan: {
				MyApp.getInstance().setCurrentHQType(AppConstants.HQTYPE_ZQ);
				Intent intent1 = new Intent();
				intent1.putExtra("id", KeyDefine.KEY_MARKET_STOCK_SHSZ);
				intent1.setClass(getApplicationContext(),
						ZhengQuanActivity.class);
				startActivity(intent1);
			}
				break;
			case R.id.rb_setting:
				startIntent(newsIntent, NewsActivity.class);
				break;
			}
		}
	}

	public void startIntent(Intent intent, Class clz) {
		if (intent != null)
			startActivity(intent);
		else {
			startActivity(new Intent(getApplicationContext(), clz));
		}
	}

	/** make image viewpager rolling **/
	private void handleViewPagerRolling() {
		if (mHandler == null) {
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					int item = vp_homepage.getCurrentItem();

					if (item < list.size() - 1) {
						item++;
					} else {
						item = 0;
					}
					vp_homepage.setCurrentItem(item);
					mHandler.sendMessageDelayed(Message.obtain(), TIME_DELAY);
				};
			};
			mHandler.sendMessageDelayed(Message.obtain(), TIME_DELAY);
		}
	}

	private void bindViewPagerwithIndecator() {
		titleIndicator = (CirclePageIndicator) findViewById(R.id.CPIndicator_homepage);
		titleIndicator.setViewPager(vp_homepage);

		// Bind the indecator for the button with the vp which is displaying
		// thelitsview below
		rg_homepager
				.setOnCheckedChangeListener(new LvHomePagerChangeListener());
	}

	/** 请求数据 HttpUtil.getNewss(news_id, mNewsOneClassties, false); */
	private void RequestHttpUtil(final String[] news_id,
			final List<NewsOneClassty> mNewsOneClassties, final boolean flag) {
		if (cachedThreadPool == null) {
			cachedThreadPool = Executors.newCachedThreadPool();
		}

		if (!cachedThreadPool.isShutdown()) {
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					{
						HttpUtil.getNewss(news_id, mNewsOneClassties, flag);

						mHandler1.sendEmptyMessage(UPDATE_UI);
					}
				}
			});
		}
	}

	/** handle viewpager changing when the button checked **/
	class LvHomePagerChangeListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb_todaynews:
				RequestHttpUtil(mNews.get(0).getID(), mNewsOneClassties, false);
				// forMoreVisable(1);
				break;
			case R.id.rb_info:
				RequestHttpUtil(mNews.get(1).getID(), mNewsOneClassties, false);
				// forMoreVisable(2);
				break;
			case R.id.rb_things:
				RequestHttpUtil(mNews.get(2).getID(), mNewsOneClassties, false);
				// forMoreVisable(3);
				break;
			}
		}
	}

	/*
	 * public void forMoreVisable(final int i) { if (mListAdapter.getSize() ==
	 * 0) { tv_findmore.setVisibility(View.VISIBLE); } else if
	 * (mListAdapter.getSize() == 1) { tv_findmore.setVisibility(View.GONE); } }
	 */
	private void setAdapter() {
		HomePagerAdapter adapter = new HomePagerAdapter(list,
				getApplicationContext());
		vp_homepage.setAdapter(adapter);

		// zq_home_listview.setAdapter(lvAdapter1);
		lv_ForSv.setAdapter(mListAdapter);

		lv_ForSv.setFocusable(false);
	}

	/** 存放所有的新闻 */
	private ArrayList<NewsOneClassty> mListData;

	/** 为首页中的控制添加相应的图片、文字等信息 */
	private void setData() {
		// set data for image viewpager
		list = new ArrayList<ImageView>();
		for (int i = 0; i < imagesid.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(imagesid[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			list.add(imageView);
		}

		// set titles for three listviews
		rb_todaynews.setText(mNews.get(0).getTitle());
		rb_info.setText(mNews.get(1).getTitle());
		rb_things.setText(mNews.get(2).getTitle());

		mNewsOneClassties = new ArrayList<NewsOneClassty>();
		mListData = new ArrayList<NewsOneClassty>();
		mListAdapter = new ZQHomeNewsAdapter(getApplicationContext(), mListData);
		lv_ForSv.setAdapter(mListAdapter);

		lv_ForSv.setOnItemClickListener(mOnItemClickListener);
	}

	/** 刷新数据 */
	private Handler mHandler1 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE_UI:
				mListData.clear();
				mListData.addAll(mNewsOneClassties);

				// closeProgress();
				if (mListAdapter == null) {
					mListAdapter = new ZQHomeNewsAdapter(
							getApplicationContext(), mListData);
				} else {
					mListAdapter.setData(mListData);
					mListAdapter.notifyDataSetChanged();
				}
				// 大于10的时候
				if (mListAdapter.getSize() == 0) {
					tv_findmore.setVisibility(View.VISIBLE);
				} else if (mListAdapter.getSize() == 1) {// 其他情况
					tv_findmore.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		};
	};

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Intent intent = new Intent();
			String newid = mListData.get(position).getID();
			intent.putExtra("news_id", newid);
			intent.setClass(MainActivity.this, NewsDetailActivity.class);
			startActivity(intent);
		}
	};

	/**
	 * 进入期权首页
	 * 
	 * @param id
	 */
	private void showQiQuanPage(int id) {

		if (!PreferenceEngine.getInstance().getAPKVersion(mVersionName)) {
			// 不是第一次打开直接跳转到主页
			// 保存已经打开
			PreferenceEngine.getInstance().saveAPKVersion(mVersionName);
			// 初始保存默认配置
			saveObjectToShared();
		}
		Intent intent = new Intent(MainActivity.this, MainTabActivity.class);
		intent.putExtra("mId", id);
		startActivity(intent);
	}

	/** 保存一个默认的筛选条件 行情合约筛选器中默认条件 */
	private void saveObjectToShared() {
		ScreenCondition sc = new ScreenCondition();
		sc.setCode(ScreenCondition.SCREEN_ALL_STOCK_CODE);
		sc.setMarket((short) 0);
		sc.setName("全部标的");
		sc.setGridViewId(4);
		sc.setField_hq_zdf(0.05f);
		sc.setLeverId(3);
		sc.setVitality(1);

		String mTiaoJianInfo = sc.getCode() + "|" + sc.getMarket() + "|"
				+ sc.getName() + "|" + sc.getGridViewId() + "|"
				+ sc.getField_hq_zdf() + "|" + sc.getLeverId() + "|"
				+ sc.getVitality();
		PreferenceEngine.getInstance().saveHQQueryCondition(mTiaoJianInfo);
	}

	/** gridView 的onItemLick响应事件 */
	public OnItemClickListener listener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int currentScreen = mScrollLayout.getCurScreen();
			// 点击的是自定义按钮
			if ((currentScreen * AppConstants.HOME_PAGE_ITEM_SIZE + position) == mUserMarkets
					.size()) {
				// 点击首页中间多个小模块中的自定义按钮
				Intent intent = new Intent(getApplicationContext(),
						HomeEditActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_bottom_in,
						R.anim.slide_bottom_out);
			} else {
				MainTabActivity act = AppActivityManager.getAppManager()
						.getMainTabActivity();
				if (act != null) {
					act.setChangePage(MainTabActivity.PAGE_SCREEN);
				}

				DrawerListAdapter adapter = (DrawerListAdapter) parent
						.getAdapter();
				DBZQHomeActivityMarket market = adapter.getItem(position);
				String url = market.url;
				String mId = market.Id;

				Intent intent1 = new Intent();
				MyApp.getInstance().setCurrentHQType(AppConstants.HQTYPE_ZQ);
				switch (Integer.parseInt(mId)) {
				case KeyDefine.KEY_MARKET_SELF: // 证券自选
					intent1.putExtra("id", KeyDefine.KEY_MARKET_SELF);
					intent1.setClass(getApplicationContext(),
							ZhengQuanActivity.class);
					MainActivity.this.startActivity(intent1);
					break;
				case KeyDefine.KEY_MARKET_STOCK_SHSZ:
					// 当时财通证券时候,这里810使用为跳转 财衍观察
					if (AppConstants.IS_TEXTVIEW_REQUEIRED) {
						MyApp.getInstance().setCurrentHQType(
								AppConstants.HQTYPE_QQ);
						showQiQuanPage(KeyDefine.KEY_MARKET_STOCK_SHSZ);// 调用else，直接跳转到自选界面
					} else { // 跳转至沪深个股界面
						intent1.putExtra("id", KeyDefine.KEY_MARKET_STOCK_SHSZ);
						intent1.setClass(getApplicationContext(),
								ZhengQuanActivity.class);
						MainActivity.this.startActivity(intent1);
					}
					break;
				case KeyDefine.KEY_MARKET_EX_STOCK_ZS:
					if (AppConstants.IS_TEXTVIEW_REQUEIRED) // 当时财通证券时候
															// 这里810使用为跳转 财衍观察
					{
						intent1.setClass(getApplicationContext(),
								ObserverActivity.class);
					} else {
						intent1.putExtra("id", KeyDefine.KEY_MARKET_EX_STOCK_ZS);
						intent1.setClass(getApplicationContext(),
								ZhengQuanActivity.class);
					}
					MainActivity.this.startActivity(intent1);
					break;
				case KeyDefine.KEY_MARKET_EX_FUTURE_LD:
					intent1.putExtra("id", KeyDefine.KEY_MARKET_EX_FUTURE_LD);
					intent1.setClass(getApplicationContext(),
							ZhengQuanActivity.class);
					MainActivity.this.startActivity(intent1);
					break;
				case KeyDefine.KEY_MARKET_ZQJY:
					intent1.putExtra("id", KeyDefine.KEY_MARKET_ZQJY);
					intent1.setClass(getApplicationContext(),
							ZhengQuanActivity.class);
					MainActivity.this.startActivity(intent1);
					break;
				case KeyDefine.KEY_MARKET_QQJY:
					MyApp.getInstance()
							.setCurrentHQType(AppConstants.HQTYPE_QQ);
					showQiQuanPage(KeyDefine.KEY_MARKET_QQJY);
					break;
				case KeyDefine.KEY_MARKET_TJZQ:
					if (url != null) {
						intent1.putExtra("url", url);
						intent1.putExtra("name", market.Name);
						intent1.setClass(getApplicationContext(),
								ZQDongZZYWebActivity.class);
						MainActivity.this.startActivity(intent1);
					}

					break;
				case KeyDefine.KEY_MARKET_SDKH: {
					try {
						if (DataTools.isAPKAvalible(mContext,
								AppConstants.THIRD_APP_PACKAGE)) {
							mMyApp.doStartApplicationWithPackageName(mContext,
									AppConstants.THIRD_APP_PACKAGE);
						} else {

							new AlertDialog(mContext)
									.builder()
									.setTitle("提示")
									.setMsg("开户APP未安装，是否前往下载！")
									.setCancelable(false)
									.setCanceledOnTouchOutside(false)
									.setNegativeButton("取消",
											new OnClickListener() {
												@Override
												public void onClick(View v) {
												}
											})
									.setPositiveButton("确定",
											new OnClickListener() {
												@Override
												public void onClick(View v) {
													Intent i = new Intent();
													i.setAction("android.intent.action.VIEW");
													Uri content_uri_browsers = Uri
															.parse(AppConstants.THIRD_APP_DOWNLOAD_PATH);
													i.setData(content_uri_browsers);

													try {
														mContext.startActivity(i);
													} catch (ActivityNotFoundException e) {
														e.printStackTrace();
													}
												}
											}).show();
						}
					} catch (Exception e) {
						L.e("MainActivity", "exception" + e);
					}
				}
					break;
				}
			}
		}

	};
	/** 上部标题栏右边的“期权版” */
	private TextView mTVQiQuan;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_public_black_head_title_right_blue_about:
			Intent intent = new Intent(MainActivity.this, ZQAboutActivity.class);
			startActivity(intent);
			break;
		case R.id.img_public_black_head_title_left_user:
			// Toast.makeText(mMyApp, "start to login...",
			// Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

	}
}
