package com.pengbo.mhdcx.ui.activity;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.dslv.DragSortListView;
import com.pengbo.mhdzq.dslv.DragSortListView.RemoveListener;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.zq_activity.HdActivity;

public class HeadEditActivity extends HdActivity implements OnClickListener {
	// 标题里面的插件
	private TextView tv_save, tv_middle, tv_right;
	private MyApp mMyApp;
	public ArrayList<TagCodeInfo> mTagCodeInfos;

	private DragSortListView mDragListView;  
    private DragAdapter mDragAdapter;

	// 监听器在手机拖动停下的时候触发
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {// from to 分别表示 被拖动控件原位置 和目标位置
			if (from != to) {
				TagCodeInfo item = (TagCodeInfo) mDragAdapter.getItem(from);// 得到listview的适配器

				mDragAdapter.remove(from);// 在适配器中”原位置“的数据。
				mDragAdapter.insert(item, to);// 在目标位置中插入被拖动的控件。
			}
		}
	};
	// 删除监听器，点击左边差号就触发。删除item操作。
	private RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			mDragAdapter.remove(which);
		}
	}; 
      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.head_edit_activity);
		mMyApp=(MyApp) getApplication();
		
		initDatas();
		// 初始化 插件
		initView();	
	}
	
	@Override
	protected void onResume() {
		
		ViewTools.isShouldForegraund = true;
		super.onResume();
	}

	private void initDatas() {
		mTagCodeInfos = new ArrayList<TagCodeInfo> ();
		mTagCodeInfos.addAll(mMyApp.getMyStockList(AppConstants.HQTYPE_QQ));
		
		mDragListView = (DragSortListView) findViewById(android.R.id.list);
		
		mDragListView.setDropListener(onDrop);  
		mDragListView.setRemoveListener(onRemove);  
          
          
        mDragAdapter = new DragAdapter(HeadEditActivity.this, mTagCodeInfos);  
        mDragListView.setAdapter(mDragAdapter);          
        mDragListView.setDragEnabled(true); //设置是否可拖动。
        mDragListView.setClickable(false);
	}


	/**
	 * 初始化 标头的插件
	 */
	private void initView() {
		
		tv_save = (TextView) this.findViewById(R.id.header_left_edit);
		tv_middle = (TextView) this.findViewById(R.id.header_middle_textview);
		//右边的搜索期权列表按钮隐藏 
		tv_right = (TextView) this.findViewById(R.id.header_right_search);

		tv_save.setVisibility(View.VISIBLE);
		tv_save.setText("保存");// 抬头的 左边 保存按钮
		tv_middle.setText("自选编辑 "); // 抬头中间的 文本内容
		tv_right.setVisibility(View.GONE);

		tv_save.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left_edit:
			saveEdit();
			HeadEditActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	private void saveEdit() {
		ArrayList<TagCodeInfo> newMyStockList = new ArrayList<TagCodeInfo>();

		for (int i = 0; i < mDragAdapter.getCount(); i++) {
			TagCodeInfo stock = (TagCodeInfo) mDragAdapter.getItem(i);
			newMyStockList.add(stock);
		}
		mMyApp.UpdateAllMyStockList(newMyStockList, AppConstants.HQTYPE_QQ);
	}
	
	public class DragAdapter extends BaseAdapter {  
	      
	    private Context context;  
	    ArrayList<TagCodeInfo> items;//适配器的数据源  
	       
	    public DragAdapter(Context context, ArrayList<TagCodeInfo> list){  
	        this.context = context;  
	        this.items = list;
	    } 
	    
	    @Override  
	    public int getCount() {   
	        return items.size();  
	    }  
	  
	    @Override  
	    public Object getItem(int arg0) {   
	        return items.get(arg0);  
	    }  
	  
	    @Override  
	    public long getItemId(int arg0) {   
	        return arg0;  
	    }  
	      
	    public void remove(int arg0) {//删除指定位置的item  
	        items.remove(arg0);  
	        this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源  
	    }  
	      
	    public void insert(TagCodeInfo item, int arg0) {  
	        items.add(arg0, item);  
	        this.notifyDataSetChanged();  
	    }  
	  
	    @Override  
	    public View getView(int position, View convertView, ViewGroup parent) {  
	    	TagCodeInfo item = (TagCodeInfo)getItem(position);  
	        ViewHolder viewHolder;  
	        if(convertView==null){  
	            viewHolder = new ViewHolder();  
	            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_click_remove, null); 
	            viewHolder.tvName = (TextView) convertView.findViewById(R.id.headEditActivity_text);
	            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.click_delete);
	            viewHolder.ivDragHandle = (ImageView) convertView.findViewById(R.id.drag_handle);
	            convertView.setTag(viewHolder);  
	        }else{  
	            viewHolder = (ViewHolder) convertView.getTag();  
	        }  

	        convertView.setBackgroundResource(R.color.white);
	        viewHolder.tvName.setText(item.name);  

	        viewHolder.ivDelete.setOnClickListener(new AddClickListener(position, viewHolder));

	        return convertView;
	    } 
	    
	    class AddClickListener implements OnClickListener {
			private int mPosition;
			private ViewHolder mViewHolder;

			public AddClickListener(int position, ViewHolder viewHolder) {
				this.mPosition = position;
				this.mViewHolder = viewHolder;
			}

			@Override
			public void onClick(View v) {
				if (v == mViewHolder.ivDelete) {
					if (items != null) {
						int num = getCount();
						if (this.mPosition < num)
						{
							mDragAdapter.remove(mPosition);//在适配器中”原位置“的数据。
						}
					}
					
				} 
			}
		}
	      
	    class ViewHolder {  
	        TextView tvName;
	        ImageView ivDelete;  
	        ImageView ivDragHandle; 
	    }  
	}
}
