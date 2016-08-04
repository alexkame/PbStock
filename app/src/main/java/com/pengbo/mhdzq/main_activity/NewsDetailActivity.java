package com.pengbo.mhdzq.main_activity;


import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsDetailActivity extends HdActivity {

	TextView mBiaoDiName,mBiaoDiName2, mDate;
	ImageView mRefresh;
	View mBack;
	
	private WebView mWebView;
	String url = "http://news.huidian.net/HDNews2/Web/Hd_NewsDetail.aspx?";
	private String NewsID = "";
	private String mUrl;
	//private WebSettings mSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_reports_detail_webview);
		Intent intent = this.getIntent();
		NewsID = intent.getStringExtra("news_id");
		initView();
	}

	private void initView() {

		mBiaoDiName=(TextView) this.findViewById(R.id.header_middle_tv_name1);
		mBiaoDiName2=(TextView) this.findViewById(R.id.header_middle_tv_name2);
		mDate=(TextView) this.findViewById(R.id.header_middle_tv_time);
		mRefresh=(ImageView) this.findViewById(R.id.screen_detail_header_right_refresh);
		mBack = this.findViewById(R.id.linearlayout_out_of_back);
	
		mBiaoDiName.setVisibility(View.GONE);
		mDate.setVisibility(View.GONE);
		mRefresh.setVisibility(View.GONE);
		
		mBiaoDiName2.setText("新闻详细");

		mWebView = (WebView) this.findViewById(R.id.webView); 

		String strPara = String.format("newsid=%s", NewsID);
		mUrl = url + strPara;
		mWebView.loadUrl(mUrl);
		MyWebViewClient client = new MyWebViewClient();
		mWebView.setWebViewClient(client);
	//	mSetting = mWebView.getSettings();
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//destroyWeb();
				NewsDetailActivity.this.finish();

			}
		});
	}
	@Override
	protected void onDestroy() {
        mWebView.destroy();
		super.onDestroy();
	}


	private class MyWebViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			return false;
		};

	}
}
