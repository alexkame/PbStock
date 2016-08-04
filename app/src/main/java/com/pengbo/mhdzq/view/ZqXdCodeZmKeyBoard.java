package com.pengbo.mhdzq.view;

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

public class ZqXdCodeZmKeyBoard extends PopupWindow {

	public static final int KEYBOARD_LAYER_DIGIT = 1;

	private View mKeyboardView;

	private Button[] mBtnkey_digits;
	private Button mBtnKey_del;
	private Button mBtnKey_Complete;
	private Button mBtnKey_Uplow;

    private Button mBtnKey_Hide;

	private Button mBtnKey_123;
	private Button mBtn_Sys;

	private EditText mEditText;
	private Context mContext;
	private StringBuffer mInputString;
	
	private boolean mIsUp = true;

	private char[] lowZMByte = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j','k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z' };
	private char[] upZMByte = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z' };

	private OnClickListener mClickListener;

	public void ResetKeyboard(EditText editText) {
		this.mInputString.setLength(0);
		this.mInputString.append(editText.getText().toString());
		mEditText = editText;
	}

	public ZqXdCodeZmKeyBoard(Context context, OnClickListener itemOnListener,
			EditText editText) {
		super(context);

		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mKeyboardView = inflater.inflate(R.layout.zq_xd_code_zm_keyboard, null);

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
		mBtnkey_digits = new Button[lowZMByte.length];
		for (int i = 0; i < lowZMByte.length; i++) {
			String strid = String.format("btn_zm_%c", lowZMByte[i]);

			mBtnkey_digits[i] = (Button) mKeyboardView.findViewById(mContext
					.getResources().getIdentifier(strid, "id",
							mContext.getPackageName()));
			mBtnkey_digits[i].setOnClickListener(mClickListener);

		}

		mBtnKey_del = (Button) mKeyboardView.findViewById(R.id.btn_zm_del);
		mBtnKey_Complete = (Button) mKeyboardView
				.findViewById(R.id.btn_zm_confirm);

		mBtnKey_Uplow = (Button) mKeyboardView.findViewById(R.id.btn_zm_uplow);
		mBtnKey_Uplow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mIsUp)
				{
					for (int i = 0; i < lowZMByte.length; i++) {
						mBtnkey_digits[i].setText(String.valueOf(lowZMByte[i]));
					}
					mIsUp = false;
				}
				else
				{
					for (int i = 0; i < lowZMByte.length; i++) {
						mBtnkey_digits[i].setText(String.valueOf(upZMByte[i]));
					}
					mIsUp = true;
				}
			}});

		mBtnKey_123 = (Button) mKeyboardView.findViewById(R.id.btn_zm_123);

		mBtn_Sys = (Button) mKeyboardView.findViewById(R.id.btn_zm_sys);
		
		mBtnKey_Hide = (Button) mKeyboardView.findViewById(R.id.btn_zm_hide);
		mBtnKey_Hide.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ZqXdCodeZmKeyBoard.this.dismiss();
			}});
		

		mBtnKey_del.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mEditText.getText().length() > 0) {
					String strTmp = mEditText.getText().toString();
					strTmp = strTmp.substring(0, strTmp.length() - 1);
					mEditText.setText(strTmp);
				}
				
			}});
		mBtnKey_Complete.setOnClickListener(mClickListener);
		mBtnKey_123.setOnClickListener(mClickListener);
		mBtn_Sys.setOnClickListener(mClickListener);
	}
}
