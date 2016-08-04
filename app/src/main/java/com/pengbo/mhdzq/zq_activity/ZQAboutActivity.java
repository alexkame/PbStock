package com.pengbo.mhdzq.zq_activity;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.view.AutoScaleTextView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 关于界面
 * 
 * @author pobo
 * 
 */
public class ZQAboutActivity extends HdActivity implements OnClickListener{
	public final static String TAG = ZQAboutActivity.class.getSimpleName();
	public final static String INTRODUCE_TEXT = "introduce_of_company.txt";// "关于公司该产品的介绍";
	
	private MyApp mMyApp;
	private ImageView mImgBack;
	private TextView mMiddleTitle,mTVContent;
	private AutoScaleTextView tv_version;
	private Button btn_upgrade;
	
	private String mReadStr;
	public Context mContext;
	private AutoScaleTextView mTVHQFWQ,mTVJYFWQ;//行情服务器     交易服务器  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_about_activity);
		mContext = this;
		mMyApp = (MyApp) this.getApplication();
		ReadTextAbout();
	    initView();
	    
	}
	
	
	/**
	 * mImgBack
	 */
	private void initView() {
		mImgBack = (ImageView) this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		mImgBack.setVisibility(View.VISIBLE);
		mImgBack.setOnClickListener(this);
		
		
		mMiddleTitle = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		mMiddleTitle.setVisibility(View.VISIBLE);
		mMiddleTitle.setText("关于");
		mTVContent = (TextView) this.findViewById(R.id.tv_about_content);
		
		mTVHQFWQ = (AutoScaleTextView) this.findViewById(R.id.tv_hangqing_fwq);
		mTVJYFWQ = (AutoScaleTextView) this.findViewById(R.id.tv_jiaoyi_fwq);
		
		String stra = STD.ToDBC(mReadStr);
		String content = "    版本号：" + AppConstants.APP_VERSION_INFO + "\n" 
				 + "    " + stra;
		
		mTVContent.setText(content);
		
		tv_version = (AutoScaleTextView) this.findViewById(R.id.tv_version);
		btn_upgrade = (Button) this.findViewById(R.id.btn_upgrade);
		btn_upgrade.setOnClickListener(this);
		
	}
	
	
	
	/**
	 * 更新服务器的地址 
	 */
	private void updateFWQContent() {
		
		if(mMyApp.mCertifyNet.getSuccessAddr() != null || mMyApp.mCertifyNet.getSuccessAddr().length() > 0){
			mTVHQFWQ.setText(mMyApp.mCertifyNet.getSuccessAddr());
		}else{
			mTVHQFWQ.setText("未连接");
		}
		
		
		if(mMyApp.mTradeData.mTradeLoginFlag  &&  mMyApp.mTradeNet.getSuccessAddr() != null){
			if(mMyApp.mTradeData.mHQType == AppConstants.HQTYPE_QQ){
				mTVJYFWQ.setText(mMyApp.mTradeNet.getSuccessAddr()+" (期权) ");
			}else if(mMyApp.mTradeData.mHQType == AppConstants.HQTYPE_ZQ){
				mTVJYFWQ.setText(mMyApp.mTradeNet.getSuccessAddr()+" (证券) ");
			}else{
				mTVJYFWQ.setText(mMyApp.mTradeNet.getSuccessAddr());
			}
			
		}else{
			mTVJYFWQ.setText("未连接");
		}
			
		
		
	}

	
	private void ReadTextAbout() {
		mReadStr = new String();
		mReadStr = getTxtString(INTRODUCE_TEXT);
	}
	
	
	private String getTxtString(String path) {
		if (path == null || path.length() <= 0) {
			L.e(TAG, "getTxtString--->path==null");
			return "";
		}
		String result = new String();
		final String ENCODING = "UTF-8";
		try {
			InputStream in = mContext.getResources().getAssets().open(path);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, ENCODING);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
			result = "";
		}

		return result;
	}
	
	private void showLatestVersion()
	{
//		[APP_Android]
//		lastverion=200
//		url=http://android.myapp.com/myapp/detail.htm?apkName=com.hdxhbank.mhdxh
//		message=有新的软件版本，是否前往APP市场更新？

		String latestVersion = mMyApp.mNewestVersion;
		String versionDisplay = mMyApp.mNewestVersionName;
		if (versionDisplay.isEmpty())
		{
			versionDisplay = "最新版本:" + AppConstants.APP_VERSION_INFO;
		}
		if (latestVersion.isEmpty())
		{
			latestVersion = mMyApp.mVersionCode;
		}
		
		if (!latestVersion.isEmpty())
		{
			String nowVersion = PreferenceEngine.getInstance().getVersionCode();
			if(nowVersion.isEmpty())
			{
				nowVersion = mMyApp.mVersionCode;
			}
			int nVer_Now = STD.StringToInt(nowVersion);
			int nVer_Last = STD.StringToInt(mMyApp.mNewestVersion);
			if (nVer_Now < nVer_Last)
			{
				btn_upgrade.setText("点击更新");
				btn_upgrade.setEnabled(true);
			}else
			{
				btn_upgrade.setText("无需更新");
				btn_upgrade.setEnabled(false);
			}
		}else
		{
			btn_upgrade.setText("无需更新");
			btn_upgrade.setEnabled(false);
		}

		tv_version.setText(versionDisplay);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.img_public_black_head_title_left_blue_back:
			ZQAboutActivity.this.finish();
			break;
			
		case R.id.btn_upgrade:
		{
			Intent intent = new Intent();
	   		intent.setAction("android.intent.action.VIEW");
	   		Uri content_uri_browsers = Uri.parse(mMyApp.mUpdateURL);
	   		intent.setData(content_uri_browsers);

	   		try {
	   			startActivity(intent);
	   		} catch (ActivityNotFoundException e) {
	   			e.printStackTrace();
	   		}
		}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		showLatestVersion();
		updateFWQContent();
		super.onResume();
	}


}
