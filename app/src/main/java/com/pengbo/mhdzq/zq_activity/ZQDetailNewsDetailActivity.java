package com.pengbo.mhdzq.zq_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.pengbo.mhdzq.R;

/**
 * 证券详细页中的新闻详细页    此页的新闻  和研报  接入的数据   暂时  和  期权 的新闻  研报一样   
 * 
 * @author pobo
 * @date   2015-11-20 上午9:26:10
 * @className ZQDetailNewsDetailActivity.java
 * @verson 1.0.0
 */
public class ZQDetailNewsDetailActivity extends HdActivity{
	WebView mWebView;
	String url = "http://124.74.201.22/HDNews2/Web/Hd_StkNewsDetail.aspx?";

	private String NewsID = "";
	private String mCode = "";
	private short mMarket = 0;
	private int TypeId=0;
	
	TextView mMiddleTitleName;
	View mBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_detail_news_reports_detail_webview);
		Intent intent = this.getIntent();
		NewsID = intent.getStringExtra("news_id");
		mCode = intent.getStringExtra("news_code");
		mMarket = intent.getShortExtra("news_market", (short) 0);
		TypeId=intent.getIntExtra("typeid", 0);

		initView();
	}

	private void initView() {
		mMiddleTitleName = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		mBack = this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		mMiddleTitleName.setVisibility(View.VISIBLE);
		mBack.setVisibility(View.VISIBLE);
		
		
		mWebView = (WebView) this.findViewById(R.id.webView);
		String strPara = String.format("newsid=%s&market=%d&code=%s&skin=orange", NewsID, mMarket, mCode);
		mWebView.loadUrl(url + strPara);
		MyWebViewClient client = new MyWebViewClient();
		mWebView.setWebViewClient(client);
		mWebView.removeJavascriptInterface("searchBoxJavaBredge_");
		
		if(TypeId==1){
			mMiddleTitleName.setText("新闻公告");
		}else if(TypeId==2){
			mMiddleTitleName.setText("研究报告");
		}
		
		
		mBack.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				ZQDetailNewsDetailActivity.this.finish();
			}
		});
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

	private class MyWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			return false;
		};

	}
}
