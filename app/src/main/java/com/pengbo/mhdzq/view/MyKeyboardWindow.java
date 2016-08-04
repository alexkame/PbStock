package com.pengbo.mhdzq.view;


import com.pengbo.mhdcx.fragment.TradeOrderFragment;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.wheel.widget.OnWheelChangedListener;
import com.pengbo.mhdcx.wheel.widget.WheelView;
import com.pengbo.mhdcx.wheel.widget.adapter.AbstractWheelTextAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;
import android.widget.RadioGroup.OnCheckedChangeListener;



public class MyKeyboardWindow extends PopupWindow implements OnCheckedChangeListener
{
	
	public static final int KJBJ_TYPES_NUM = 8;// 
	public static final int KJBJ_TYPES_NUM_SZ = 10;//
	public static final int KEYBOARD_LAYER_KJBJ = 0;
	public static final int KEYBOARD_LAYER_DIGIT = 1;
	public static final int KJBJ_TYPES_SH = 2;
	public static final int KJBJ_TYPES_SZ = 3;
	// KjbjTypes names
	public static final String[] sKjbjTypes = new String[] { "对手价", "最新价", "挂单价", "涨停价", "跌停价",
					"市价剩余转限价", "市价剩余撤销", "全额成交或撤销" };
	public static final int[] sKjbjModeSH = new int[] {Trade_Define.WTPRICEMODE_DSJ,
													Trade_Define.WTPRICEMODE_NOW,
													Trade_Define.WTPRICEMODE_GDJ,
													Trade_Define.WTPRICEMODE_UP,
													Trade_Define.WTPRICEMODE_DOWN,
													Trade_Define.WTPRICEMODE_SJSYZXJ,
													Trade_Define.WTPRICEMODE_SJSYCX,
													Trade_Define.WTPRICEMODE_QBCJHCX};
	
	public static final String[] sKjbjTypesSZ = new String[] { "对手价", "最新价", "挂单价", "涨停价", "跌停价",
		"对手方最优", "本方最优", "即时成交剩余撤销", "最优五档即时成交", "全额成交或撤销" };
	public static final int[] sKjbjModeSZ = new int[] {Trade_Define.WTPRICEMODE_DSJ,
													   Trade_Define.WTPRICEMODE_NOW,
													   Trade_Define.WTPRICEMODE_GDJ,
													   Trade_Define.WTPRICEMODE_UP,
													   Trade_Define.WTPRICEMODE_DOWN,
													   Trade_Define.WTPIRCEMODE_DSFZY_SZ,
													   Trade_Define.WTPIRCEMODE_BFZY_SZ,
													   Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ,
													   Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ,
													   Trade_Define.WTPIRCEMODE_QBCJHCX_SZ};

	public int mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
	public String mCurrentKJBJContent = "";
	private View mKeyboardView;
	private int mCurrentKeyboardLayer = KEYBOARD_LAYER_KJBJ;
	private int mCurrentKJBJTypes = KJBJ_TYPES_SH;
	
	private WheelView mKJBJWheelView;
	private RadioGroup mRG_keyboard;
	private Button mBtnComplete;
	private ImageView mFok;
	private Button[] mBtnkey_digits;
	private Button mBtnKey_del;
	private ImageButton mBtnKey_dot;
	private ViewFlipper mKeyboardFlipper;
	private Animation slide_in_left, slide_out_right;
	
	private Context mContext;
	private StringBuffer mInputString;
	private EditText mEditText;
	private ImageView mEditFok;
	private ImageView mEditFuWei;
	private OnClickListener mClickListener;
	private boolean mbOnlyDigit = false;
	private Handler mHandler;
	private boolean mbFok = false;
	
	public void ResetKeyboard(EditText editText, ImageView editFok, ImageView editFuWei, 
			boolean bOnlyDigit, int sellWTPriceMode, int kjbjTypes, boolean bfok)
	{
		mbFok = bfok;
		this.mbOnlyDigit = bOnlyDigit;
		this.mSellWTPriceMode = sellWTPriceMode;
		this.mCurrentKJBJTypes = kjbjTypes;
		if(mbOnlyDigit)
		{
			mRG_keyboard.setVisibility(View.INVISIBLE);
			mFok.setVisibility(View.INVISIBLE);
			mRG_keyboard.check(R.id.key_rb_normal);
			
			mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			mCurrentKeyboardLayer = KEYBOARD_LAYER_DIGIT;
		}else
		{
			mRG_keyboard.setVisibility(View.VISIBLE);
			mFok.setVisibility(View.VISIBLE);
			
			switchKeyboardLayer(mSellWTPriceMode);
		}
		
		this.mEditText = editText;
		this.mEditFok = editFok;
		this.mEditFuWei = editFuWei;
		this.mInputString.setLength(0);
		this.mInputString.append(editText.getText().toString());
		
		switch(mSellWTPriceMode)
		{
		case Trade_Define.WTPRICEMODE_SJSYZXJ:
		case Trade_Define.WTPRICEMODE_SJSYCX:
		case Trade_Define.WTPRICEMODE_QBCJHCX:
		case Trade_Define.WTPIRCEMODE_DSFZY_SZ:
		case Trade_Define.WTPIRCEMODE_BFZY_SZ:
		case Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ:
		case Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ:
		case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:
			mFok.setEnabled(false);
			mFok.setVisibility(View.INVISIBLE);
			mEditFok.setVisibility(View.GONE);
			mFok.setImageResource(R.drawable.order_fox02);
			this.mbFok = false;
			break;
			
		default:
			mFok.setEnabled(true);
			mFok.setVisibility(View.VISIBLE);
			if(this.mbFok)
			{
				mFok.setImageResource(R.drawable.order_fox03);
			}else
			{
				mFok.setImageResource(R.drawable.order_fox02);
			}
			break;
		}
	}

	public MyKeyboardWindow(Handler handler, Activity context, OnClickListener itemOnListener,
			EditText editText, ImageView editFok, ImageView editFuWei, 
			boolean bOnlyDigit, int sellWTPriceMode, int kjbjTypes, boolean bfok) {
		super(context);
		mbFok = bfok;
		mHandler = handler;
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mKeyboardView = inflater.inflate(R.layout.keyboard_layers_1, null);
		
		mClickListener = itemOnListener;
		mbOnlyDigit = bOnlyDigit;
		this.mSellWTPriceMode = sellWTPriceMode;
		this.mCurrentKJBJTypes = kjbjTypes;

		mBtnComplete = (Button) mKeyboardView.findViewById(R.id.key_btn_confirm);
		mFok = (ImageView) mKeyboardView.findViewById(R.id.key_fok);
		mRG_keyboard = (RadioGroup) mKeyboardView.findViewById(R.id.key_rg_switch);
		mRG_keyboard.setOnCheckedChangeListener(this);
		
		mKeyboardFlipper = (ViewFlipper) mKeyboardView.findViewById(R.id.keyboard_flipper);
		slide_in_left = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
		slide_out_right = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);

		mKeyboardFlipper.setInAnimation(slide_in_left);
		mKeyboardFlipper.setOutAnimation(slide_out_right);
		
		mKJBJWheelView = (WheelView) mKeyboardView.findViewById(R.id.wheel_kjbj);
		mKJBJWheelView.setWheelBackground(R.drawable.wheel_bg_holo);
		mKJBJWheelView.setWheelForeground(R.drawable.wheel_val_holo);
		mKJBJWheelView.setShadowColor(0x00ffffff, 0x00ffffff, 0x00ffffff);
		if (mCurrentKJBJTypes == KJBJ_TYPES_SZ)
		{
			mKJBJWheelView.setVisibleItems(KJBJ_TYPES_NUM_SZ); // Number of items
		}else
		{
			mKJBJWheelView.setVisibleItems(KJBJ_TYPES_NUM); // Number of items
		}
		
		mKJBJWheelView.setViewAdapter(new KJBJAdapter(mContext));
		mKJBJWheelView.addChangingListener(wheelChangeListener);
		
		if(mbOnlyDigit)
		{
			mRG_keyboard.setVisibility(View.INVISIBLE);
			mFok.setVisibility(View.INVISIBLE);
			mRG_keyboard.check(R.id.key_rb_normal);
		}else
		{
			mRG_keyboard.setVisibility(View.VISIBLE);
			mFok.setVisibility(View.VISIBLE);
			
			switchKeyboardLayer(mSellWTPriceMode);
		}
		
		// 设置ShowPopWindow的View
		this.setContentView(mKeyboardView);
		// 设置ShowPopWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置ShowPopWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置ShowPopWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置ShowPopWindow弹出窗体动画效果

		this.setAnimationStyle(R.style.ActionSheetDialogAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置ShowPopWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		
		mInputString = new StringBuffer();
		mEditText = editText;
		mInputString.append(mEditText.getText().toString());
		
		mEditFok = editFok;
		mEditFuWei = editFuWei;
		
		switch(mSellWTPriceMode)
		{
		case Trade_Define.WTPRICEMODE_SJSYZXJ:
		case Trade_Define.WTPRICEMODE_SJSYCX:
		case Trade_Define.WTPRICEMODE_QBCJHCX:
		case Trade_Define.WTPIRCEMODE_DSFZY_SZ:
		case Trade_Define.WTPIRCEMODE_BFZY_SZ:
		case Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ:
		case Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ:
		case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:
			mFok.setEnabled(false);
			mFok.setVisibility(View.INVISIBLE);
			mEditFok.setVisibility(View.GONE);
			mFok.setImageResource(R.drawable.order_fox02);
			this.mbFok = false;
			break;
			
		default:
			mFok.setEnabled(true);
			mFok.setVisibility(View.VISIBLE);
			if(this.mbFok)
			{
				mFok.setImageResource(R.drawable.order_fox03);
			}else
			{
				mFok.setImageResource(R.drawable.order_fox02);
			}
			
			break;
		}

		initCtrlListener();
		initKeyDigits();
	}
	
	private void switchKeyboardLayer(int sellWTPriceMode) {

		if (sellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
			mCurrentKeyboardLayer = KEYBOARD_LAYER_KJBJ;
			int index = -1;
			if (mCurrentKJBJTypes == KJBJ_TYPES_SZ)
			{
				index = STD.IsHaveInt(sKjbjModeSZ, sellWTPriceMode);
				mCurrentKJBJContent = sKjbjTypesSZ[index];
			}else
			{
				index = STD.IsHaveInt(sKjbjModeSH, sellWTPriceMode);
				mCurrentKJBJContent = sKjbjTypes[index];
			}
			if (index < 0)
			{
				index = 0;
			}
			mKJBJWheelView.setCurrentItem(index);
			mKJBJWheelView.invalidateWheel(true);
			mRG_keyboard.check(R.id.key_rb_kjbj);
		} else {
			mCurrentKeyboardLayer = KEYBOARD_LAYER_DIGIT;
			if (mCurrentKJBJTypes == KJBJ_TYPES_SZ)
			{
				mKJBJWheelView.setCurrentItem(KJBJ_TYPES_NUM_SZ / 2);
			}else
			{
				mKJBJWheelView.setCurrentItem(KJBJ_TYPES_NUM / 2);
			}
			
			mRG_keyboard.check(R.id.key_rb_normal);
		}
	}

	private void initCtrlListener()
	{
		mFok.setOnClickListener(mClickListener);

		mBtnComplete.setOnClickListener(mClickListener);
	}
	
	public void setKeyFOK(boolean bfok)
	{
		this.mbFok = bfok;
	}
	
	public int getKeyboardLayerIndex()
	{
		return mCurrentKeyboardLayer;
	}
	
	public int getKeyboardKJBJType()
	{
		if(mKJBJWheelView != null)
		{
			return mKJBJWheelView.getCurrentItem();
		}
		return -1;
	}

	/**
	 * 初始化键盘按钮
	 */

	protected void initKeyDigits() {
		mBtnkey_digits = new Button[10];
		for (int i = 0; i < 10; i++) 
		{
			String strid = String.format("btn_digit_%d", i);
			
			mBtnkey_digits[i] = (Button) mKeyboardView.findViewById(mContext.getResources().getIdentifier(strid, "id", mContext.getPackageName()));
			mBtnkey_digits[i].setOnClickListener(mClickListener);

		}

		mBtnKey_del = (Button) mKeyboardView.findViewById(R.id.btn_digit_delete);
		mBtnKey_dot = (ImageButton) mKeyboardView.findViewById(R.id.btn_digit_dian);

		mBtnKey_del.setOnClickListener(mClickListener);

		mBtnKey_dot.setOnClickListener(mClickListener);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.key_rb_normal:
			mCurrentKeyboardLayer = KEYBOARD_LAYER_DIGIT;
			mKeyboardFlipper.showNext();
			break;
		case R.id.key_rb_kjbj:
			mCurrentKeyboardLayer = KEYBOARD_LAYER_KJBJ;
			mKeyboardFlipper.showPrevious();
			break;

		default:
			break;
		}
	}
	
	OnWheelChangedListener wheelChangeListener = new OnWheelChangedListener()
	{

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (mCurrentKeyboardLayer == KEYBOARD_LAYER_KJBJ)
			{
				if (mCurrentKJBJTypes == KJBJ_TYPES_SZ)
				{
					if (newValue >=0 && newValue <= sKjbjModeSZ.length)
					{
						if (mSellWTPriceMode != sKjbjModeSZ[newValue])
						{
							mSellWTPriceMode = sKjbjModeSZ[newValue];
							mCurrentKJBJContent = sKjbjTypesSZ[newValue];
						}
					}
				}else
				{
					if (newValue >=0 && newValue <= sKjbjModeSH.length)
					{
						if (mSellWTPriceMode != sKjbjModeSH[newValue])
						{
							mSellWTPriceMode = sKjbjModeSH[newValue];
							mCurrentKJBJContent = sKjbjTypes[newValue];
						}
					}
				}
			}
			switch(mSellWTPriceMode)
			{
			case Trade_Define.WTPRICEMODE_SJSYZXJ:
			case Trade_Define.WTPRICEMODE_SJSYCX:
			case Trade_Define.WTPRICEMODE_QBCJHCX:
			case Trade_Define.WTPIRCEMODE_DSFZY_SZ:
			case Trade_Define.WTPIRCEMODE_BFZY_SZ:
			case Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ:
			case Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ:
			case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:
				mFok.setEnabled(false);
				mFok.setVisibility(View.INVISIBLE);
				mEditFok.setVisibility(View.GONE);
				mFok.setImageResource(R.drawable.order_fox02);
				mbFok = false;
				if (mHandler != null)
				{
					Message msg = mHandler.obtainMessage();
					msg.what = TradeOrderFragment.TRADE_STATUS_FOK;
					msg.arg1 = 0; //fok disable
					mHandler.sendMessage(msg);
				}
				break;
				
			default:
				mFok.setEnabled(true);
				mFok.setVisibility(View.VISIBLE);
				if(mbFok)
				{
					mFok.setImageResource(R.drawable.order_fox03);
				}else
				{
					mFok.setImageResource(R.drawable.order_fox02);
				}
				break;
			}	
		}
		
	};
	
	public class KJBJAdapter extends AbstractWheelTextAdapter {

		/**
		 * Constructor
		 */
		protected KJBJAdapter(Context context) {
			super(context, R.layout.keyboard_kjbj_item, NO_RESOURCE);
			setItemTextResource(R.id.keyboard_kjbj_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			if (mCurrentKJBJTypes == KJBJ_TYPES_SZ)
			{
				return sKjbjTypesSZ.length;
			}else
			{
				return sKjbjTypes.length;
			}
		}

		@Override
		protected CharSequence getItemText(int index) {
			if (mCurrentKJBJTypes == KJBJ_TYPES_SZ)
			{
				if (index >= 0 && index <= sKjbjTypesSZ.length)
				{
					return sKjbjTypesSZ[index];
				}else
				{
					return "";
				}
				
			}else
			{
				if (index >= 0 && index <= sKjbjTypes.length) {
					return sKjbjTypes[index];
				} else {
					return "";
				}
			}
		}

	}

}
