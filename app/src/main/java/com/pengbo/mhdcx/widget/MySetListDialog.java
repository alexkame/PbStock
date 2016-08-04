package com.pengbo.mhdcx.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.pengbo.mhdcx.adapter.MyDialogAdapter;
import com.pengbo.mhdzq.R;

/**
 * 设置中 直接覆盖在最上面一个 Dialog 但是会 有边框  填不满屏幕  暂时还是从新启动的  Activity 
 * @author pobo
 *
 */
public class MySetListDialog {

	private Context context;
	private Dialog dialog;
	
	private Dialogcallback dialogcallback;

	private ListView listView;
	
	public MySetListDialog(Context con) {
	
		this.context = con;
		
		dialog = new Dialog(context, R.style.ActionSheetDialogStyleSet);
		
		dialog.setContentView(R.layout.view_mysetdialog);
		
		listView = (ListView) dialog.findViewById(R.id.device_list);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialogcallback.dialogdo(position);
				dismiss();
				
			}
		});
		
	}
	
	public interface Dialogcallback {
		public void dialogdo(int string);
	}

	public void setDialogCallback(Dialogcallback dialogcallback) {
		this.dialogcallback = dialogcallback;
	}
	
	public void setContent(MyDialogAdapter adapter2) {
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
