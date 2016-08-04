package com.pengbo.mhdcx.view;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Ctrl_Trend_Detail extends LinearLayout {

	private final static String TAG = Ctrl_Trend_Detail.class.getSimpleName();
	
	private static final int nDetailRow = 9; //
	
	private TextView[] mTVDetails;
	
	protected Context mContext;
	
	private TagLocalStockData mOptionData;
	private ArrayList<TagLocalDealData> mDealDataArray;
	private List<DetailData> mDetailDataList;

	
	public Ctrl_Trend_Detail(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
	}
	
	public Ctrl_Trend_Detail(Context context) {
        super(context);
        initData(context);
	}
	
	public void initData(Context context)
	{
		mDealDataArray = new ArrayList<TagLocalDealData>();
		mDetailDataList = new ArrayList<DetailData>();
	}
	
	@Override
	protected void onFinishInflate()
    {
		super.onFinishInflate();
		initView();
    }
	
	public void initView()
	{
		L.i(TAG, "initView");
		setFocusable(true);
        setFocusableInTouchMode(true);

		initCtrls();
	}

	public void initCtrls()
	{
		TextView[] mTextViews = { 
				(TextView) findViewById(R.id.textView011), 
				(TextView) findViewById(R.id.textView012),
				(TextView) findViewById(R.id.textView013),
 
				(TextView) findViewById(R.id.textView021),
				(TextView) findViewById(R.id.textView022),
				(TextView) findViewById(R.id.textView023), 

				(TextView) findViewById(R.id.textView031),
				(TextView) findViewById(R.id.textView032), 
				(TextView) findViewById(R.id.textView033),

				(TextView) findViewById(R.id.textView041), 
				(TextView) findViewById(R.id.textView042),
				(TextView) findViewById(R.id.textView043),

				(TextView) findViewById(R.id.textView051),
				(TextView) findViewById(R.id.textView052),
				(TextView) findViewById(R.id.textView053), 

				(TextView) findViewById(R.id.textView061),
				(TextView) findViewById(R.id.textView062), 
				(TextView) findViewById(R.id.textView063),

				(TextView) findViewById(R.id.textView071), 
				(TextView) findViewById(R.id.textView072),
				(TextView) findViewById(R.id.textView073),
 
				(TextView) findViewById(R.id.textView081),
				(TextView) findViewById(R.id.textView082),
				(TextView) findViewById(R.id.textView083), 

				(TextView) findViewById(R.id.textView091),
				(TextView) findViewById(R.id.textView092),
				(TextView) findViewById(R.id.textView093),

				/*(TextView) findViewById(R.id.textView101),
				(TextView) findViewById(R.id.textView102),
				(TextView) findViewById(R.id.textView103),

				(TextView) findViewById(R.id.textView111),
				(TextView) findViewById(R.id.textView112),
				(TextView) findViewById(R.id.textView113),

				(TextView) findViewById(R.id.textView121),
				(TextView) findViewById(R.id.textView122),
				(TextView) findViewById(R.id.textView123),*/

				};
		mTVDetails = mTextViews;
		mTextViews = null;
	}
	
	public void updateData(TagLocalStockData data, ArrayList<TagLocalDealData> dealdata)
	{
		mOptionData = data;
		mDealDataArray = dealdata;
		
		updateCtrls();
	}
	
	public void updateCtrls()
	{
		L.d(TAG, "updateCtrls");
		if (mOptionData == null || mDealDataArray == null)
		{
			L.e(TAG, "updateCtrls--->mOptionData == null || mDealDataArray == null");
			return;
		}

		int size = mDealDataArray.size();
		int num = (size < nDetailRow) ? size : nDetailRow;
		
		DetailData detailData;

    	for(int i = 0; i < num; i++)
    	{
    		detailData = new DetailData();
    		
    		int index = size - 1 - i;
    		TagLocalDealData tData = mDealDataArray.get(index);
    		TagLocalDealData tData_pre = null;
    		if (index > 0)
    		{
    			tData_pre = mDealDataArray.get(index - 1);
    		}
    		
    		String temp = "";
    		if (tData_pre != null && i != num - 1)
    		{
    			if ((tData.time/100 - tData_pre.time/100) == 0)
    			{
    				//说明该笔detail和上一笔是同一分钟，只要显示秒即可
    				temp = STD.getTimeSringss(tData.time);
    			}else
    			{
    				//说明该笔detail是新一分钟的，显示h和m
    				temp = STD.getTimeSringhhmm(tData.time/100);
    			}
    		}else
    		{
    			//该detail是第一笔显示的，只要显示h和m
    			temp = STD.getTimeSringhhmm(tData.time/100);
    		}
    		detailData.setTime(new DetailDataItem(temp, ColorConstant.COLOR_TIME));
        	
        	temp = ViewTools.getStringByPrice(tData.now, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);    	
            detailData.setPrice(new DetailDataItem(temp, ViewTools.getColor(tData.now, mOptionData.HQData.nLastClear)));

            temp = ViewTools.getVolume((long)tData.volume, mOptionData.market, 1, true);
        	if(tData.inoutflag == 1)//外盘
        	{
        		temp += "B";
            	detailData.setNum(new DetailDataItem(temp, ColorConstant.PRICE_UP));
        	} else if(tData.inoutflag == 2)//内盘
        	{
        		temp += "S";
            	detailData.setNum(new DetailDataItem(temp, ColorConstant.PRICE_DOWN));
        	} else
        	{
        		temp += "-";
        		detailData.setNum(new DetailDataItem(temp, ColorConstant.COLOR_END));
        	}
        	
        	mDetailDataList.add(0, detailData);
        	while(!mDetailDataList.isEmpty() && (mDetailDataList.size() > nDetailRow)) {
        		mDetailDataList.remove(nDetailRow);
        	}
    	}

    	String price_temp = new String();
    	for(int i = 0 ; i < nDetailRow ; i++ ){
    		if(i < num){
    			mTVDetails[i*3 + 0].setText(mDetailDataList.get(i).getTime().getString());
    			mTVDetails[i*3 + 0].setTextColor(mDetailDataList.get(i).getTime().getColor());
	    		
	    		price_temp = mDetailDataList.get(i).getPrice().getString();
	    		mTVDetails[i*3 + 1].setText(price_temp);
	    		mTVDetails[i*3 + 1].setTextColor(mDetailDataList.get(i).getPrice().getColor());
	    		
	    		mTVDetails[i*3 + 2].setText(mDetailDataList.get(i).getNum().getString());
	    		mTVDetails[i*3 + 2].setTextColor(mDetailDataList.get(i).getNum().getColor());
    		}else{//不满9个时下面的清空！
    			mTVDetails[i*3 + 0].setText("");
    			mTVDetails[i*3 + 1].setText("");
    			mTVDetails[i*3 + 2].setText("");
    		}
    	}
	}

	
	public void clearDetailData(){
		mDetailDataList.clear();
	}
	
	
	class DetailDataItem {
		private String str;
		private int color;

		public DetailDataItem(String s, int i) {
			str = s;
			color = i;
		}

		public DetailDataItem() {
			str = "----";
			color = 0;
		}

		public String getString() {
			return str;
		}

		public int getColor() {
			return color;
		}
	}
	
	class DetailData {
		private DetailDataItem time;
		private DetailDataItem price;
		private DetailDataItem num;

		public DetailData(DetailDataItem t, DetailDataItem p, DetailDataItem n) {
			time = t;
			price = p;
			num = n;
		}

		public DetailData() {
			time = new DetailDataItem();
			price = new DetailDataItem();
			num = new DetailDataItem();
		}

		public void setTime(DetailDataItem t) {
			time = t;
		}

		public void setPrice(DetailDataItem p) {
			price = p;
		}

		public void setNum(DetailDataItem n) {
			num = n;
		}

		public DetailDataItem getTime() {
			return time;
		}

		public DetailDataItem getPrice() {
			return price;
		}

		public DetailDataItem getNum() {
			return num;
		}

		public String toString() {
			return time.getString() + " " + price.getString() + " "
					+ num.getString();
		}
	}
}
