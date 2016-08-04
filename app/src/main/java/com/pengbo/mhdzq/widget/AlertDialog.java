package com.pengbo.mhdzq.widget;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.tools.STD;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private TextView txt_msg1;
	private EditText edit_num;
	private EditText edit_num2;
	private TextView txt_optionname;
	private TextView txt_optionnamevalue;
	private TextView txt_optioncode;
	private TextView txt_optioncodevalue;
	private TextView txt_wtjg;
	private TextView txt_wtjgvalue;
	private TextView txt_wtsl;
	private TextView txt_wtslvalue;
	private TextView txt_jylx;
	private TextView txt_jylxvalue;
	private TextView txt_bzj;
	private TextView txt_bzjvalue;
	private LinearLayout lLayout_kjfs;
	private ImageView img_sepLine;
	private TextView txt_optionname_fs;
	private TextView txt_optionnamevalue_fs;
	private TextView txt_optioncode_fs;
	private TextView txt_optioncodevalue_fs;
	private TextView txt_wtjg_fs;
	private TextView txt_wtjgvalue_fs;
	private TextView txt_wtsl_fs;
	private TextView txt_wtslvalue_fs;
	private TextView txt_jylx_fs;
	private TextView txt_jylxvalue_fs;
	private TextView txt_bzj_fs;
	private TextView txt_bzjvalue_fs;
	private Button btn_neg;
	private Button btn_pos;
	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showMsg1 = false;
	private boolean showEdit = false;
	private boolean showEdit2 = false;
	private boolean showOptionInfo = false;
	private boolean showOptionBZJ = false;
	private boolean showOptionJYLX = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private boolean showKJFS = false;
	private boolean showOptionInfo_fs = false;
	private boolean showOptionBZJ_fs = false;
	private boolean showOptionJYLX_fs = false;

	public AlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public AlertDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_alertdialog, null);

		// 获取自定义Dialog布局中的控件
		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_title.setVisibility(View.GONE);
		txt_msg = (TextView) view.findViewById(R.id.txt_msg);
		txt_msg.setVisibility(View.GONE);
		txt_msg1 = (TextView) view.findViewById(R.id.txt_msg1);
		txt_msg1.setVisibility(View.GONE);
		edit_num = (EditText) view.findViewById(R.id.edit_num);
		edit_num2 = (EditText) view.findViewById(R.id.edit_num2);
		edit_num2.setVisibility(View.GONE);
		edit_num.setVisibility(View.GONE);

		txt_optionname = (TextView) view.findViewById(R.id.txt_optionname);
		txt_optionname.setVisibility(View.GONE);
		txt_optionnamevalue = (TextView) view
				.findViewById(R.id.txt_optionnamevalue);
		txt_optionnamevalue.setVisibility(View.GONE);
		txt_optioncode = (TextView) view.findViewById(R.id.txt_optioncode);
		txt_optioncode.setVisibility(View.GONE);
		txt_optioncodevalue = (TextView) view
				.findViewById(R.id.txt_optioncodevalue);
		txt_optioncodevalue.setVisibility(View.GONE);
		txt_wtjg = (TextView) view.findViewById(R.id.txt_wtjg);
		txt_wtjg.setVisibility(View.GONE);
		txt_wtjgvalue = (TextView) view.findViewById(R.id.txt_wtjgvalue);
		txt_wtjgvalue.setVisibility(View.GONE);
		txt_wtsl = (TextView) view.findViewById(R.id.txt_wtsl);
		txt_wtsl.setVisibility(View.GONE);
		txt_wtslvalue = (TextView) view.findViewById(R.id.txt_wtslvalue);
		txt_wtslvalue.setVisibility(View.GONE);
		txt_bzj = (TextView) view.findViewById(R.id.txt_bzj);
		txt_bzj.setVisibility(View.GONE);
		txt_bzjvalue = (TextView) view.findViewById(R.id.txt_bzjvalue);
		txt_bzjvalue.setVisibility(View.GONE);
		txt_jylx = (TextView) view.findViewById(R.id.txt_jylx);
		txt_jylx.setVisibility(View.GONE);
		txt_jylxvalue = (TextView) view.findViewById(R.id.txt_jylxvalue);
		txt_jylxvalue.setVisibility(View.GONE);

		lLayout_kjfs = (LinearLayout) view.findViewById(R.id.layout_kjfs);
		lLayout_kjfs.setVisibility(View.GONE);
		img_sepLine = (ImageView) view.findViewById(R.id.sep_line);
		img_sepLine.setVisibility(View.GONE);
		txt_optionname_fs = (TextView) view
				.findViewById(R.id.txt_optionname_fs);
		txt_optionname_fs.setVisibility(View.GONE);
		txt_optionnamevalue_fs = (TextView) view
				.findViewById(R.id.txt_optionnamevalue_fs);
		txt_optionnamevalue_fs.setVisibility(View.GONE);
		txt_optioncode_fs = (TextView) view
				.findViewById(R.id.txt_optioncode_fs);
		txt_optioncode_fs.setVisibility(View.GONE);
		txt_optioncodevalue_fs = (TextView) view
				.findViewById(R.id.txt_optioncodevalue_fs);
		txt_optioncodevalue_fs.setVisibility(View.GONE);
		txt_wtjg_fs = (TextView) view.findViewById(R.id.txt_wtjg_fs);
		txt_wtjg_fs.setVisibility(View.GONE);
		txt_wtjgvalue_fs = (TextView) view.findViewById(R.id.txt_wtjgvalue_fs);
		txt_wtjgvalue_fs.setVisibility(View.GONE);
		txt_wtsl_fs = (TextView) view.findViewById(R.id.txt_wtsl_fs);
		txt_wtsl_fs.setVisibility(View.GONE);
		txt_wtslvalue_fs = (TextView) view.findViewById(R.id.txt_wtslvalue_fs);
		txt_wtslvalue_fs.setVisibility(View.GONE);
		txt_bzj_fs = (TextView) view.findViewById(R.id.txt_bzj_fs);
		txt_bzj_fs.setVisibility(View.GONE);
		txt_bzjvalue_fs = (TextView) view.findViewById(R.id.txt_bzjvalue_fs);
		txt_bzjvalue_fs.setVisibility(View.GONE);
		txt_jylx_fs = (TextView) view.findViewById(R.id.txt_jylx_fs);
		txt_jylx_fs.setVisibility(View.GONE);
		txt_jylxvalue_fs = (TextView) view.findViewById(R.id.txt_jylxvalue_fs);
		txt_jylxvalue_fs.setVisibility(View.GONE);

		btn_neg = (Button) view.findViewById(R.id.btn_neg);
		btn_neg.setVisibility(View.GONE);
		btn_pos = (Button) view.findViewById(R.id.btn_pos);
		btn_pos.setVisibility(View.GONE);
		img_line = (ImageView) view.findViewById(R.id.img_line);
		img_line.setVisibility(View.GONE);

		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
				.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

		return this;
	}

	public AlertDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("标题");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public AlertDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("内容");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

	public AlertDialog setMsg1(String msg, int color) {
		showMsg1 = true;
		if ("".equals(msg)) {
			txt_msg1.setText("内容");
		} else {
			txt_msg1.setText(msg);
		}
		if (color != -1) {
			txt_msg1.setTextColor(color);
		}
		return this;
	}

	public AlertDialog setEdit(String str) {
		showEdit = true;
		edit_num.setText(str);
		return this;
	}

	public String getEditText() {
		return edit_num.getText().toString();
	}

	public AlertDialog setEdit2() {
		showEdit2 = true;
		edit_num2.setText("");
		return this;
	}

	public String getEditText2() {
		return edit_num2.getText().toString();
	}

	public AlertDialog setKJFS() {
		showKJFS = true;

		return this;
	}

	public AlertDialog setOptionInfo_fs(String name, String code, String wtjg,
			String wtsl) {
		showOptionInfo_fs = true;
		txt_optionnamevalue_fs.setText(name);
		txt_optioncodevalue_fs.setText(code);
		txt_wtjgvalue_fs.setText(wtjg);
		txt_wtslvalue_fs.setText(wtsl);
		return this;
	}

	public AlertDialog setOptionBZJ_fs(String bzj) {
		if (bzj == null) {
			return this;
		}
		showOptionBZJ_fs = true;
		if ("".equals(bzj)) {
			txt_bzjvalue_fs.setText("0");
		} else {
			txt_bzjvalue_fs.setText(bzj);
		}
		return this;
	}

	public AlertDialog setOptionJYLX_fs(String jylx) {
		if (jylx == null) {
			return this;
		}
		showOptionJYLX_fs = true;
		if ("".equals(jylx)) {
			txt_jylxvalue_fs.setText("--");
		} else {
			txt_jylxvalue_fs.setText(jylx);
		}
		return this;
	}

	public AlertDialog setOptionInfo(String name, String code, String wtjg,
			String wtsl) {
		showOptionInfo = true;
		txt_optionnamevalue.setText(name);
		txt_optioncodevalue.setText(code);
		txt_wtjgvalue.setText(wtjg);
		txt_wtslvalue.setText(wtsl);
		return this;
	}

	public AlertDialog setOptionBZJ(String bzj) {
		if (bzj == null) {
			return this;
		}
		showOptionBZJ = true;
		if ("".equals(bzj)) {
			txt_bzjvalue.setText("0");
		} else {
			txt_bzjvalue.setText(bzj);
		}
		return this;
	}

	public AlertDialog setOptionJYLX(String jylx) {
		if (jylx == null) {
			return this;
		}
		showOptionJYLX = true;
		if ("".equals(jylx)) {
			txt_jylxvalue.setText("--");
		} else {
			txt_jylxvalue.setText(jylx);
		}
		return this;
	}

	public AlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public AlertDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	public AlertDialog setPositiveButton(String text,
			final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText("确定");
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	public AlertDialog setNegativeButton(String text,
			final OnClickListener listener) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText("取消");
		} else {
			btn_neg.setText(text);
		}
		btn_neg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	private void setLayout() {
		if (!showTitle && !showMsg) {
			txt_title.setText("提示");
			txt_title.setVisibility(View.VISIBLE);
		} else {
			txt_title.setVisibility(View.GONE);
		}

		if (showTitle) {
			txt_title.setVisibility(View.VISIBLE);
		} else {
			txt_title.setVisibility(View.GONE);
		}

		if (showMsg) {
			txt_msg.setVisibility(View.VISIBLE);
		} else {
			txt_msg.setVisibility(View.GONE);
		}

		if (showMsg1) {
			txt_msg1.setVisibility(View.VISIBLE);
		} else {
			txt_msg1.setVisibility(View.GONE);
		}

		if (showEdit) {
			edit_num.setVisibility(View.VISIBLE);
		} else {
			edit_num.setVisibility(View.GONE);
		}

		if (showEdit2) {
			edit_num2.setVisibility(View.VISIBLE);
		} else {
			edit_num2.setVisibility(View.GONE);
		}

		if (showOptionInfo) {
			txt_optionname.setVisibility(View.VISIBLE);
			txt_optionnamevalue.setVisibility(View.VISIBLE);
			txt_optioncode.setVisibility(View.VISIBLE);
			txt_optioncodevalue.setVisibility(View.VISIBLE);
			txt_wtjg.setVisibility(View.VISIBLE);
			txt_wtjgvalue.setVisibility(View.VISIBLE);
			txt_wtsl.setVisibility(View.VISIBLE);
			txt_wtslvalue.setVisibility(View.VISIBLE);
		} else {
			txt_optionname.setVisibility(View.GONE);
			txt_optionnamevalue.setVisibility(View.GONE);
			txt_optioncode.setVisibility(View.GONE);
			txt_optioncodevalue.setVisibility(View.GONE);
			txt_wtjg.setVisibility(View.GONE);
			txt_wtjgvalue.setVisibility(View.GONE);
			txt_wtsl.setVisibility(View.GONE);
			txt_wtslvalue.setVisibility(View.GONE);
		}

		if (showOptionBZJ) {
			txt_bzj.setVisibility(View.VISIBLE);
			txt_bzjvalue.setVisibility(View.VISIBLE);
		} else {
			txt_bzj.setVisibility(View.GONE);
			txt_bzjvalue.setVisibility(View.GONE);
		}

		if (showOptionJYLX) {
			txt_jylx.setVisibility(View.VISIBLE);
			txt_jylxvalue.setVisibility(View.VISIBLE);
		} else {
			txt_jylx.setVisibility(View.GONE);
			txt_jylxvalue.setVisibility(View.GONE);
		}

		if (showKJFS) {
			lLayout_kjfs.setVisibility(View.VISIBLE);
			img_sepLine.setVisibility(View.VISIBLE);
		} else {
			lLayout_kjfs.setVisibility(View.GONE);
			img_sepLine.setVisibility(View.GONE);
		}
		if (showOptionInfo_fs) {
			txt_optionname_fs.setVisibility(View.VISIBLE);
			txt_optionnamevalue_fs.setVisibility(View.VISIBLE);
			txt_optioncode_fs.setVisibility(View.VISIBLE);
			txt_optioncodevalue_fs.setVisibility(View.VISIBLE);
			txt_wtjg_fs.setVisibility(View.VISIBLE);
			txt_wtjgvalue_fs.setVisibility(View.VISIBLE);
			txt_wtsl_fs.setVisibility(View.VISIBLE);
			txt_wtslvalue_fs.setVisibility(View.VISIBLE);
		} else {
			txt_optionname_fs.setVisibility(View.GONE);
			txt_optionnamevalue_fs.setVisibility(View.GONE);
			txt_optioncode_fs.setVisibility(View.GONE);
			txt_optioncodevalue_fs.setVisibility(View.GONE);
			txt_wtjg_fs.setVisibility(View.GONE);
			txt_wtjgvalue_fs.setVisibility(View.GONE);
			txt_wtsl_fs.setVisibility(View.GONE);
			txt_wtslvalue_fs.setVisibility(View.GONE);
		}

		if (showOptionBZJ_fs) {
			txt_bzj_fs.setVisibility(View.VISIBLE);
			txt_bzjvalue_fs.setVisibility(View.VISIBLE);
		} else {
			txt_bzj_fs.setVisibility(View.GONE);
			txt_bzjvalue_fs.setVisibility(View.GONE);
		}

		if (showOptionJYLX_fs) {
			txt_jylx_fs.setVisibility(View.VISIBLE);
			txt_jylxvalue_fs.setVisibility(View.VISIBLE);
		} else {
			txt_jylx_fs.setVisibility(View.GONE);
			txt_jylxvalue_fs.setVisibility(View.GONE);
		}

		if (!showPosBtn && !showNegBtn) {
			btn_pos.setText("确定");
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		if (showPosBtn && showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
			img_line.setVisibility(View.VISIBLE);
		}

		if (showPosBtn && !showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}

		if (!showPosBtn && showNegBtn) {
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}
	}

	public void show() {
		if (context instanceof Activity) {

			Activity ac = (Activity) context;
			if (!ac.isFinishing()) {
				setLayout();
				dialog.show();
			}
		}
		else
		{
			setLayout();
			dialog.show();			
		}
	}

	public void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void clear() {
		showTitle = false;
		showMsg = false;
		showMsg1 = false;
		showEdit = false;
		showOptionInfo = false;
		showOptionBZJ = false;
		showOptionJYLX = false;
		showPosBtn = false;
		showNegBtn = false;

		showKJFS = false;
		showOptionInfo_fs = false;
		showOptionBZJ_fs = false;
		showOptionJYLX_fs = false;

		btn_pos.setVisibility(View.GONE);
		btn_neg.setVisibility(View.GONE);
		img_line.setVisibility(View.GONE);
	}
}
