package com.pengbo.mhdzq.adapter;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.base.Param;

public class ZqParamAdapter extends BaseAdapter {

	private List<Param> datas;
	private LayoutInflater mInflater;
	private Context context;
	private Integer index=-1;

	public ZqParamAdapter(Context con, List<Param> mDatas) {
		this.context = con;
		this.datas = mDatas;
		this.mInflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		return datas.size();
	}
	
	public void updateItem(int position, String strJX)
	{
		datas.get(position).setmJXEditDay(strJX);
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			synchronized (this) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.zq_param_listview_item, null);
				viewHolder.mJXParamName=(TextView) convertView.findViewById(R.id.junxianname);
				viewHolder.mJXParmEdit=(EditText) convertView.findViewById(R.id.edittext_junxian);
				viewHolder.mDay=(TextView) convertView.findViewById(R.id.tv_junxian_day);
				
				viewHolder.mJXParmEdit.setTag(position);
				viewHolder.mJXParmEdit.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction()==MotionEvent.ACTION_UP){
							index=(Integer) v.getTag();
						}
						return false;
					}
				});
				
				
				class MyTextWatcher implements TextWatcher{
					private ViewHolder mHolder;
					public MyTextWatcher(ViewHolder holder) {
						mHolder = holder;
					}

					
					@Override
					public void afterTextChanged(Editable s) {
						if(!s.toString().equals("")&&s!=null){
							// 通过tag来取position<strong>
							int position = (Integer) mHolder.mJXParmEdit.getTag();
							System.out.println("--------------------" + position);
							updateItem(position, s.toString().trim());
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {						
					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {						
					}
					
				}
				
				viewHolder.mJXParmEdit.addTextChangedListener(new MyTextWatcher(viewHolder));
				convertView.setTag(viewHolder);
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mJXParmEdit.setTag(position);
		}
		Param mParam=datas.get(position);
		final int pos = position;
		viewHolder.mJXParamName.setText(mParam.getmJXName());
		
		//viewHolder.mJXParmEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		viewHolder.mJXParmEdit.setText(mParam.getmJXEditDay());		
		//viewHolder.mJXParmEdit.setSelection(mParam.getmJXEditDay().length());//将光标追踪到内容的最后
		
		
		viewHolder.mJXParmEdit.clearFocus();
		if(index!=-1&&index==position){
			viewHolder.mJXParmEdit.requestFocus();
			viewHolder.mJXParmEdit.setSelection(mParam.getmJXEditDay().length());
		}

		viewHolder.mDay.setText(mParam.getmJXR());
		return convertView;
	}
	
	
	class ViewHolder {
		TextView mJXParamName;
		EditText mJXParmEdit;
		TextView mDay;
	}

}
