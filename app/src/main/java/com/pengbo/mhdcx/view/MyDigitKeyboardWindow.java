package com.pengbo.mhdcx.view;


import com.pengbo.mhdzq.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;



public class MyDigitKeyboardWindow extends PopupWindow {
	public static final int KEYBOARD_LAYER_DIGIT = 1;

	private View mKeyboardView;

	private Button[] mBtnkey_digits;
	private ImageButton mBtnKey_del;
	private Button mBtnKey_Complete;
	private EditText mEditText;
	private Context mContext;
	private StringBuffer mInputString;

	private OnClickListener mClickListener;
	
	public void ResetKeyboard(EditText editText)
	{
		this.mInputString.setLength(0);
		this.mInputString.append(editText.getText().toString());
	}

	public MyDigitKeyboardWindow(Context context, OnClickListener itemOnListener,
			EditText editText) {
		super(context);

		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mKeyboardView = inflater.inflate(R.layout.keyboard_layer_digit_1, null);
		
		mClickListener = itemOnListener;
		
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

		initKeyDigits();
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

		mBtnKey_del = (ImageButton) mKeyboardView.findViewById(R.id.btn_digit_delete);
		mBtnKey_Complete = (Button) mKeyboardView.findViewById(R.id.btn_digit_wancheng);

		mBtnKey_del.setOnClickListener(mClickListener);
		mBtnKey_Complete.setOnClickListener(mClickListener);
	}
}
