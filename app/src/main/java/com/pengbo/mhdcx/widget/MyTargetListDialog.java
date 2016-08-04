package com.pengbo.mhdcx.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.pengbo.mhdcx.adapter.MyTargetDialogAdapter;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.R;

public class MyTargetListDialog {
	

	private Context context;
	private Dialog dialog;
	private TextView txt_cancel;
	private DialogcallbackTarget dialogcallback;

	private ListView listView;
	
	public MyTargetListDialog(Context con) {
	
		this.context = con;
		
		dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.setContentView(R.layout.view_mydialog);
		
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		lp.height = ViewTools.getScreenSize(con).heightPixels/2;
		dialogWindow.setAttributes(lp);
		
		txt_cancel = (TextView) dialog.findViewById(R.id.txt_mydialog_cancel);
		
		listView = (ListView) dialog.findViewById(R.id.device_list);
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
		
	}
	
	public interface DialogcallbackTarget {
		public void dialogdo(int index);
	}

	public void setDialogCallback(DialogcallbackTarget dialogcallback) {
		this.dialogcallback = dialogcallback;
	}
	
	public void setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
	}

	public void setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
	}
	
	public void setContent(ListAdapter adapter2) {
		listView.setAdapter(adapter2);
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
