package com.pengbo.mhdzq.widget;

import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 弹出的带取消的白色 对话框  选择对话框列表 数据直接显示在改变的地方 
 * @author pobo
 *
 */
public class MyListDialog {
	private Context context;
	private Dialog dialog;
	private TextView txt_cancel;
	private TextView txt_title;
	private Dialogcallback dialogcallback;

	private ListView listView;
	
	public MyListDialog(Context con) {
	
		this.context = con;
	}
	
	public MyListDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_mydialog, null);

		txt_title = (TextView) view.findViewById(R.id.txt_mydialog_title);
		txt_cancel = (TextView) view.findViewById(R.id.txt_mydialog_cancel);
		
		listView = (ListView) view.findViewById(R.id.device_list);
		txt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialogcallback.dialogdo(position);
				dismiss();				
			}
		});

		
		dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		lp.height = ViewTools.getScreenSize(context).heightPixels/2;
		dialogWindow.setAttributes(lp);

		return this;
	}
	
	public interface Dialogcallback {
		public void dialogdo(int string);
	}

	public void setDialogCallback(Dialogcallback dialogcallback) {
		this.dialogcallback = dialogcallback;
	}
	
	public void setTitle(String title)
	{
		txt_title.setText(title);
	}
	
	public void setContent(BaseAdapter adapter2) {
		listView.setAdapter(adapter2);
	}
	
	public void setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		//return this;
	}

	public void setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		//return this;
	}

	public void show() {
		dialog.show();
	}

	public void hide() {
		dialog.hide();
	}

	public void dismiss() {
		dialog.dismiss();
	}


}
