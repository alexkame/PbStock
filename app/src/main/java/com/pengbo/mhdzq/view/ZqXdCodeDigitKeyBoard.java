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

public class ZqXdCodeDigitKeyBoard extends PopupWindow {

	public static final int KEYBOARD_LAYER_DIGIT = 1;

	private View mKeyboardView;

	private View[] mBtnkey_digits;
	private View mBtnKey_del;
	private Button mBtnKey_Complete;
	private Button mBtnKey_hide;

	private Button mBtnKey_600;
	private Button mBtnKey_601;
	private Button mBtnKey_000;
	private Button mBtnKey_002;
	private Button mBtnKey_300;

	private Button mBtnKey_ABC;
	private Button mBtn_Sys;

	private EditText mEditText;
	private Context mContext;
	private StringBuffer mInputString;

	private OnClickListener mClickListener;

	public void ResetKeyboard(EditText editText) {
		this.mInputString.setLength(0);
		this.mInputString.append(editText.getText().toString());
		mEditText = editText;
	}

	public ZqXdCodeDigitKeyBoard(Context context,
			OnClickListener itemOnListener, EditText editText) {
		super(context);

		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mKeyboardView = inflater.inflate(R.layout.zq_xd_code_keyboard, null);

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
		mBtnkey_digits = new View[10];
		for (int i = 0; i < 10; i++) {
			String strid = String.format("btn_digit_%d", i);

			mBtnkey_digits[i] = (View) mKeyboardView.findViewById(mContext
					.getResources().getIdentifier(strid, "id",
							mContext.getPackageName()));
			mBtnkey_digits[i].setOnClickListener(mClickListener);

		}

		mBtnKey_del = (View) mKeyboardView.findViewById(R.id.btn_digit_back);
		mBtnKey_Complete = (Button) mKeyboardView
				.findViewById(R.id.btn_digit_confirm);

		mBtnKey_hide = (Button) mKeyboardView.findViewById(R.id.btn_digit_hide);

		mBtnKey_600 = (Button) mKeyboardView.findViewById(R.id.btn_digit_600);
		mBtnKey_600.setOnClickListener(mClickListener);
		mBtnKey_601 = (Button) mKeyboardView.findViewById(R.id.btn_digit_601);
		mBtnKey_601.setOnClickListener(mClickListener);
		mBtnKey_000 = (Button) mKeyboardView.findViewById(R.id.btn_digit_000);
		mBtnKey_000.setOnClickListener(mClickListener);
		mBtnKey_002 = (Button) mKeyboardView.findViewById(R.id.btn_digit_002);
		mBtnKey_002.setOnClickListener(mClickListener);
		mBtnKey_300 = (Button) mKeyboardView.findViewById(R.id.btn_digit_300);
		mBtnKey_300.setOnClickListener(mClickListener);

		mBtnKey_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mEditText.getText().length() > 0) {
					String strTmp = mEditText.getText().toString();
					strTmp = strTmp.substring(0, strTmp.length() - 1);
					mEditText.setText(strTmp);
				}

			}
		});
		mBtnKey_Complete.setOnClickListener(mClickListener);
		mBtnKey_hide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ZqXdCodeDigitKeyBoard.this.dismiss();

			}
		});

		mBtn_Sys = (Button) mKeyboardView.findViewById(R.id.btn_digit_xt);

		mBtnKey_ABC = (Button) mKeyboardView.findViewById(R.id.btn_digit_ABC);

		mBtnKey_ABC.setOnClickListener(mClickListener);
		mBtn_Sys.setOnClickListener(mClickListener);
	}
}
