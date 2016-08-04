package com.pengbo.mhdzq.view;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqShijiaAdapter;
import com.pengbo.mhdzq.fragment.TradeZqBuyFragment;
import com.pengbo.mhdzq.trade.data.Trade_Define;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ZqShijiaWindow extends PopupWindow {

	public static final String[] sKjbjTypesSH = new String[] { "五档即成剩撤",
			"五档即成转限" };

	public static final int KJBJ_TYPES_SH = 2;
	public static final int KJBJ_TYPES_SZ = 3;

	private int mCurrentKJBJTypes = KJBJ_TYPES_SH;
	public int mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
	public String mCurrentKJBJContent = "";

	public static final int[] sKjbjModeSH = new int[] {
			Trade_Define.WTPIRCEMODE_ZYWDJSCJSYCX, Trade_Define.WTPIRCEMODE_ZYWDJSCJSYXJ};

	public static final String[] sKjbjTypesSZ = new String[] { "五档即成剩撤",
			"即时成交剩撤", "全额成交或撤", "本方最优", "对手方最优" };
	public static final int[] sKjbjModeSZ = new int[] {
			Trade_Define.WTPIRCEMODE_ZYWDJSCJSYCX, Trade_Define.WTPIRCEMODE_JSCJSYCXWT,
			Trade_Define.WTPIRCEMODE_QECJHCXWT, Trade_Define.WTPIRCEMODE_BFZYJWT,
			Trade_Define.WTPIRCEMODE_DSZYJWT};

	private Context mContext;
	private View mKeyboardView;
	private ListView mShijiaView;
	private ZqShijiaAdapter mShijiaAdapter;
	private ArrayList<String> mShijiaArray;
	private TradeZqBuyFragment mFrag;

	public ZqShijiaWindow(Context context, OnClickListener itemOnListener,
			int market, TradeZqBuyFragment zqBuyFrag) {

		super(context);

		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mKeyboardView = inflater.inflate(R.layout.zq_shijia, null);

		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();

		this.setContentView(mKeyboardView);
		// 设置ShowPopWindow弹出窗体的宽

		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置ShowPopWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置ShowPopWindow弹出窗体可点击
		this.setFocusable(true);
		
		this.setOutsideTouchable(true);

		this.setAnimationStyle(R.style.ActionSheetDialogAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置ShowPopWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

		mShijiaView = (ListView) mKeyboardView.findViewById(R.id.zq_shijailist);

		mShijiaArray = new ArrayList();
		mShijiaAdapter = new ZqShijiaAdapter(context, mShijiaArray);
		initShijiaList(market);

		mFrag = zqBuyFrag;

		mShijiaView.setAdapter(mShijiaAdapter);

		mShijiaView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mCurrentKJBJTypes == KJBJ_TYPES_SZ) {

					mSellWTPriceMode = sKjbjModeSZ[position];
					mCurrentKJBJContent = sKjbjTypesSZ[position];

				} else {

					mSellWTPriceMode = sKjbjModeSH[position];
					mCurrentKJBJContent = sKjbjTypesSH[position];

				}

				mFrag.setPriceEditContent(mCurrentKJBJContent);
				ZqShijiaWindow.this.dismiss();
			}
		});
		
		
		mKeyboardView.setOnTouchListener(new OnTouchListener()// 需要设置，点击之后取消popupview，即使点击外面，也可以捕获事件
        {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                if (ZqShijiaWindow.this.isShowing())
                {
                	ZqShijiaWindow.this.dismiss();
                }
                return false;
			}
        });
	};

	public void resetMarket(int market)
	{
		initShijiaList(market);
	}
	
	private void initShijiaList(int market) {
		if (market == 1000) {
			mCurrentKJBJTypes = KJBJ_TYPES_SH;
			mShijiaArray.clear();
			for (int i = 0; i < sKjbjTypesSH.length; i++) {
				mShijiaArray.add(sKjbjTypesSH[i]);
			}
		} else {
			mCurrentKJBJTypes = KJBJ_TYPES_SZ;
			mShijiaArray.clear();
			for (int i = 0; i < sKjbjTypesSZ.length; i++) {
				mShijiaArray.add(sKjbjTypesSZ[i]);
			}
		}
	}

}
