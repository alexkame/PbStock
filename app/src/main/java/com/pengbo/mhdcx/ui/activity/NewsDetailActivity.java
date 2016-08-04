package com.pengbo.mhdcx.ui.activity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
public class NewsDetailActivity extends HdActivity {
	WebView mWebView;
	String url = "http://124.74.201.22/HDNews2/Web/Hd_StkNewsDetail.aspx?";

	private String NewsID = "";
	private String mCode = "";
	private short mMarket = 0;
	private int TypeId=0;
	
	TextView mBiaoDiName,mBiaoDiName2, mDate;
	ImageView mRefresh;
	View mBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_reports_detail_webview);
		Intent intent = this.getIntent();
		NewsID = intent.getStringExtra("news_id");
		mCode = intent.getStringExtra("news_code");
		mMarket = intent.getShortExtra("news_market", (short) 0);
		TypeId=intent.getIntExtra("typeid", 0);

		initView();
	}

	private void initView() {
		mBiaoDiName=(TextView) this.findViewById(R.id.header_middle_tv_name1);
		mBiaoDiName2=(TextView) this.findViewById(R.id.header_middle_tv_name2);
		mDate=(TextView) this.findViewById(R.id.header_middle_tv_time);
		mRefresh=(ImageView) this.findViewById(R.id.screen_detail_header_right_refresh);
		mBack=this.findViewById(R.id.linearlayout_out_of_back);
	
		mBiaoDiName.setVisibility(View.GONE);
		mDate.setVisibility(View.GONE);
		mRefresh.setVisibility(View.GONE);
		
		
		mWebView = (WebView) this.findViewById(R.id.webView);
		String strPara = String.format("newsid=%s&market=%d&code=%s&skin=orange", NewsID, mMarket, mCode);
		mWebView.loadUrl(url + strPara);
		MyWebViewClient client = new MyWebViewClient();
		mWebView.setWebViewClient(client);
		mWebView.removeJavascriptInterface("searchBoxJavaBredge_");
		
		if(TypeId==1){
			mBiaoDiName2.setText("新闻公告");
		}else if(TypeId==2){
			mBiaoDiName2.setText("研究报告");
		}
		
		
		mBack.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				NewsDetailActivity.this.finish();
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
