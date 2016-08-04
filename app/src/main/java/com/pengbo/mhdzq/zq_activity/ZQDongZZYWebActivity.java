package com.pengbo.mhdzq.zq_activity;


import com.pengbo.mhdzq.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *  
 * 证券首页中的 “ 东证 之衍   ” 的跳转网页   gridview的点击显示网页
 * @author pobo
 * @date   2015-11-20 上午10:15:15
 * @className NewsWebActivity.java
 * @verson 1.0.0
 */
public class ZQDongZZYWebActivity extends HdActivity {

	TextView mMiddleTitle;
	ImageView mBack;
	TextView mTvPrePage;
	
	WebView mWebView;
	private String NewsUrl = "";
	private String NewsName = "";
	private WebSettings mSetting;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_dongzhengzhiyan_webview);
		Intent intent = getIntent();
		NewsUrl = intent.getStringExtra("url");
		NewsName = intent.getStringExtra("name");
		
		//尝试解决某些机型出现的service相关异常
//		try {
//			Class<?> c =Class.forName("com.android.org.chromium.com.googlecode.eyesfree.braille.selfbraille.SelfBrailleClient");
//			name = c.getSimpleName();
//			bindService(service, conn, flags)
//		} catch (ClassNotFoundException e) {
//			System.out.println("===============   "+name);
//			e.printStackTrace();
//		}
		
		initView();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private void initView() {
		mMiddleTitle = (TextView)findViewById(R.id.tv_public_black_head_title_middle_name);
		mBack = (ImageView)findViewById(R.id.img_public_black_head_title_left_blue_back);
		mTvPrePage = (TextView)findViewById(R.id.tv_public_black_head_title_right_blue_prepage);
		
		mMiddleTitle.setVisibility(View.VISIBLE);
		mBack.setVisibility(View.VISIBLE);
		mTvPrePage.setVisibility(View.VISIBLE);
		
		mMiddleTitle.setText(NewsName);

		mWebView = (WebView) findViewById(R.id.mWebView);
		mWebView.loadUrl(NewsUrl);
		MyWebViewClient client = new MyWebViewClient();
		mWebView.setWebViewClient(client);
		mSetting = mWebView.getSettings();
		mSetting.setJavaScriptEnabled(true);
		mSetting.setSupportZoom(true);
		mSetting.setBuiltInZoomControls(true);
		mSetting.setDisplayZoomControls(false);
		
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ZQDongZZYWebActivity.this.finish();
			}
		});

		mTvPrePage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				}else
				{
					mTvPrePage.setEnabled(false);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			mWebView.getClass().getMethod("onResume").invoke(mWebView,(Object[])null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWebView.getClass().getMethod("onPause").invoke(mWebView,(Object[])null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mWebView != null) {
		((LinearLayout)findViewById(R.id.ll_forwebview)).removeView(mWebView);
		mWebView.removeAllViews();
		mWebView.destroy();
		mWebView = null;
		}
	}
	
	private class MyWebViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			mWebView.loadUrl(url);
			if (mWebView.canGoBack())
			{
				mTvPrePage.setEnabled(true);
			}
			return false;
		}

	}
}
