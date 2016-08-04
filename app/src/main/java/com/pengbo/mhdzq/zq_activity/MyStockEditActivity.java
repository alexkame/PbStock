package com.pengbo.mhdzq.zq_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.dslv.DragSortListView;
import com.pengbo.mhdzq.dslv.DragSortListView.RemoveListener;
import com.pengbo.mhdzq.tools.ViewTools;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyStockEditActivity extends HdActivity implements OnClickListener {

	// 标题里面的插件
	private TextView tv_middle;
	private ImageView iv_back;
	private MyApp mMyApp;

	private ArrayList<TagCodeInfo> mTagCodeInfos;
	
	private DragSortListView mDragListView;  
    private DragAdapter mDragAdapter;
    
  //监听器在手机拖动停下的时候触发  
    private DragSortListView.DropListener onDrop =  
       new DragSortListView.DropListener() {  
           @Override  
          public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置  
              if (from != to) { 
                  TagCodeInfo item = (TagCodeInfo)mDragAdapter.getItem(from);//得到listview的适配器  

                  mDragAdapter.remove(from);//在适配器中”原位置“的数据。  
                  mDragAdapter.insert(item, to);//在目标位置中插入被拖动的控件。  
              }  
          }  
      };  
  //删除监听器，点击左边差号就触发。删除item操作。  
  private RemoveListener onRemove =  
      new DragSortListView.RemoveListener() {  
          @Override  
          public void remove(int which) {  
        	  mDragAdapter.remove(which);  
          }  
      };  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_zixuan_edit_activity);
		mMyApp = (MyApp) getApplication();

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
		mTagCodeInfos.addAll(mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ));
		
		mDragListView = (DragSortListView) findViewById(R.id.dslvlist_zq);
		
		mDragListView.setDropListener(onDrop);  
		mDragListView.setRemoveListener(onRemove);  
          
          
        mDragAdapter = new DragAdapter(MyStockEditActivity.this, mTagCodeInfos);  
        mDragListView.setAdapter(mDragAdapter);          
        mDragListView.setDragEnabled(true); //设置是否可拖动。
        mDragListView.setClickable(false);
	}


	/**
	 * 初始化 标头的插件
	 */
	private void initView() {
		
		tv_middle = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		tv_middle.setVisibility(View.VISIBLE);
		tv_middle.setText("编辑自选 "); // 抬头中间的 文本内容
		
		iv_back = (ImageView) this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		iv_back.setVisibility(View.VISIBLE);
		iv_back.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_public_black_head_title_left_blue_back:
			MyStockEditActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	
	@Override
	public void finish() {
		saveEdit();
		super.finish();
		this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}
	
	private void saveEdit() {

		ArrayList<TagCodeInfo> newMyStockList = new ArrayList<TagCodeInfo>();

		for (int i = 0; i < mDragAdapter.getCount(); i++) {
			TagCodeInfo stock = (TagCodeInfo) mDragAdapter.getItem(i);
			newMyStockList.add(stock);
		}
		mMyApp.UpdateAllMyStockList(newMyStockList, AppConstants.HQTYPE_ZQ);
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
	            convertView = LayoutInflater.from(context).inflate(R.layout.zq_zixuan_list_item_drag, null); 
	            viewHolder.tvName = (TextView) convertView.findViewById(R.id.headEditActivity_text);
	            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.click_delete);
	            viewHolder.ivDragHandle = (ImageView) convertView.findViewById(R.id.drag_handle);
	            viewHolder.img_zhiding = (ImageView) convertView.findViewById(R.id.img_zhiding_icon);
	            convertView.setTag(viewHolder);  
	        }else{  
	            viewHolder = (ViewHolder) convertView.getTag();  
	        }  

	        convertView.setBackgroundResource(R.color.white);
	        viewHolder.tvName.setText(item.name);  

	        viewHolder.img_zhiding.setOnClickListener(new AddClickListener(position, viewHolder));
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
				if (v == mViewHolder.img_zhiding) {
					//撤单
					if (items != null) {
						int num = getCount();
						if (mPosition < num && mPosition != 0)
						{
							TagCodeInfo item = (TagCodeInfo)getItem(mPosition);
							mDragAdapter.remove(mPosition);//在适配器中”原位置“的数据。  
			                mDragAdapter.insert(item, 0);//在目标位置中插入被拖动的控件。
						}
					}
					
				}else if (v == mViewHolder.ivDelete) {
					//撤单
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
	        ImageView img_zhiding;  
	        ImageView ivDelete;  
	        ImageView ivDragHandle; 
	    }  
	}  
}
