package com.pengbo.mhdzq.main_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.HomeEditFixedListAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.DBZQHomeActivityMarket;
import com.pengbo.mhdzq.dslv.DragSortListView;
import com.pengbo.mhdzq.dslv.DragSortListView.RemoveListener;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.zq_activity.HdActivity;
//import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


//@SuppressLint("ShowToast")
public class HomeEditActivity extends HdActivity implements OnClickListener {

	// 标题里面的插件
	private TextView tv_middle, tv_right;
	private MyApp mMyApp;
	private ArrayList<DBZQHomeActivityMarket> mDBZQMarkets;//首页显示列表
	private ArrayList<DBZQHomeActivityMarket> mDBZQMarketsFixed;//固定列表
	private ArrayList<DBZQHomeActivityMarket> mDBZQMarketsEdit;//可编辑列表
	public List<String> mNames;
	private ListView mListViewFixed;
	private HomeEditFixedListAdapter mFixedListAdapter;
	
	private DragSortListView mDragListView;  
    private DragAdapter mDragAdapter;
    private ArrayList<DBZQHomeActivityMarket> mDBZQMarketList;//显示的list data，包含可编辑，自定义title，添加列表

  //监听器在手机拖动停下的时候触发  
    private DragSortListView.DropListener onDrop =  
       new DragSortListView.DropListener() {  
           @Override  
          public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置  
              if (from != to) { 
            	  DBZQHomeActivityMarket item = (DBZQHomeActivityMarket)mDragAdapter.getItem(from);//得到listview的适配器  
                  if(to >= mDragAdapter.mSepIndex && item.IsDefault)
                  {
                	  item.IsDefault = false;
                	  mDragAdapter.mSepIndex--;
                  }else if(to <= mDragAdapter.mSepIndex && !item.IsDefault)
                  {
                	  item.IsDefault = true;
                	  mDragAdapter.mSepIndex++;
                  }else if(from == mDragAdapter.mSepIndex)
                  {
                	  return;
                  }
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
		setContentView(R.layout.home_edit_activity);
		mMyApp=(MyApp) getApplication();
		
		mDBZQMarkets = mMyApp.mDBZQHomeMenuMarkets;
		//mPBMarketsAdd = new ArrayList<CPBMarket>();
		mDBZQMarketsEdit = new ArrayList<DBZQHomeActivityMarket>();
		mDBZQMarketList = new ArrayList<DBZQHomeActivityMarket>();
		mNames=new ArrayList<String>();
		for (int i = 0; i < mDBZQMarkets.size(); i++) {
			DBZQHomeActivityMarket info = mDBZQMarkets.get(i);
			if (!info.IsFixed)
			{
				mDBZQMarketsEdit.add(info);
				mDBZQMarketList.add(info);
			}			
		}
		//添加自定义title到显示list
		DBZQHomeActivityMarket data = new DBZQHomeActivityMarket();
		data.Name = "自定义添加";
		data.Id = "";
		mDBZQMarketList.add(data);
		
		//添加剩余的市场是可自定义添加列表到显示
		for (int i = 0; i < mMyApp.mDBZQMarketArray.size(); i++)
		{
			boolean bNeedAdd = true;
			DBZQHomeActivityMarket market = mMyApp.mDBZQMarketArray.get(i);
			for(int j = 0; j < mDBZQMarkets.size(); j++)
			{
				if (mDBZQMarkets.get(j).Id.equalsIgnoreCase(market.Id))
				{
					bNeedAdd = false;
					break;
				}
			}
			if(bNeedAdd)
			{
				market.IsDefault = false;
				//mPBMarketsAdd.add(market);
				mDBZQMarketList.add(market);
			}
		}
		
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
		mDragListView = (DragSortListView) findViewById(R.id.dslvList);
		
		mDragListView.setDropListener(onDrop);  
		mDragListView.setRemoveListener(onRemove);  
          
        mDragAdapter = new DragAdapter(HomeEditActivity.this, mDBZQMarketList, mDBZQMarketsEdit.size());  
        mDragListView.setAdapter(mDragAdapter);          
        mDragListView.setDragEnabled(true); //设置是否可拖动。
        mDragListView.setClickable(false);
	}


	/**
	 * 初始化 标头的插件
	 */
	private void initView() {
		
		tv_middle = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		//右边的搜索期权列表按钮隐藏 
		tv_right = (TextView) this.findViewById(R.id.tv_public_black_head_title_right_blue_save);

		tv_middle.setText("自定义快捷入口 "); // 抬头中间的 文本内容
		tv_right.setText("完成");
		tv_middle.setVisibility(View.VISIBLE);
		tv_right.setVisibility(View.VISIBLE);

		//tv_save.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		
		this.mListViewFixed = (ListView) this.findViewById(R.id.lv_home_fixed);
		mDBZQMarketsFixed = new ArrayList<DBZQHomeActivityMarket>();
		for (int i = 0; i < mDBZQMarkets.size(); i++)
		{
			DBZQHomeActivityMarket market = mDBZQMarkets.get(i);
			if(market.IsFixed)
			{
				mDBZQMarketsFixed.add(market);
			}
		}
		this.mFixedListAdapter = new HomeEditFixedListAdapter(this, mDBZQMarketsFixed);
		mListViewFixed.setAdapter(mFixedListAdapter);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_public_black_head_title_right_blue_save:
			saveEdit();
			HomeEditActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}
	
	private void saveEdit() {

		ArrayList<DBZQHomeActivityMarket> newMarketList = new ArrayList<DBZQHomeActivityMarket>();
		for (int i = 0; i < mDBZQMarketsFixed.size(); i++)
		{
			DBZQHomeActivityMarket market = mDBZQMarketsFixed.get(i);
			newMarketList.add(market);
		}
		for (int i = 0; i < mDragAdapter.getCount(); i++)
		{
			DBZQHomeActivityMarket market = (DBZQHomeActivityMarket)mDragAdapter.getItem(i);
			if(market.IsDefault && !market.Id.isEmpty())
			{
				newMarketList.add(market);
			}
		}
		mMyApp.UpdateAllDBZQHomeMarketList(newMarketList);
	}
	
	public class DragAdapter extends BaseAdapter {  
	      
	    private Context context;  
	    ArrayList<DBZQHomeActivityMarket> items;//适配器的数据源  
	    public int mSepIndex;//自定义添加title分割所在index
	       
	    public DragAdapter(Context context,ArrayList<DBZQHomeActivityMarket> list, int separateIndex){  
	        this.context = context;  
	        this.items = list;
	        mSepIndex = separateIndex;
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
	      
	    public void insert(DBZQHomeActivityMarket item, int arg0) {  
	        items.add(arg0, item);  
	        this.notifyDataSetChanged();  
	    }  
	  
	    @Override  
	    public View getView(int position, View convertView, ViewGroup parent) {  
	    	DBZQHomeActivityMarket item = (DBZQHomeActivityMarket)getItem(position);  
	        ViewHolder viewHolder;  
	        if(convertView==null){  
	            viewHolder = new ViewHolder();  
	            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_home_drag, null); 
	            viewHolder.tvName = (TextView) convertView.findViewById(R.id.headEditActivity_text);
	            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.click_delete);
	            viewHolder.ivDragHandle = (ImageView) convertView.findViewById(R.id.drag_handle);
	            viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_market_icon);
	            viewHolder.img_add = (ImageView) convertView.findViewById(R.id.click_add);
	            convertView.setTag(viewHolder);  
	        }else{  
	            viewHolder = (ViewHolder) convertView.getTag();  
	        }  

	        viewHolder.tvName.setText(item.Name);  
	        if (item.Id.isEmpty())
	        {
	        	viewHolder.ivDelete.setVisibility(View.GONE);
	        	viewHolder.ivDragHandle.setVisibility(View.GONE);
	        	viewHolder.img_icon.setVisibility(View.GONE);
	        	viewHolder.img_add.setVisibility(View.GONE);
	        	convertView.setBackgroundResource(R.color.demand_e5e5e5);
	        }else
	        {
	        	convertView.setBackgroundResource(R.color.white);
	        	if (item.IsDefault)
	        	{
	        		viewHolder.ivDelete.setVisibility(View.VISIBLE);
		        	viewHolder.ivDragHandle.setVisibility(View.VISIBLE);
		        	viewHolder.img_icon.setVisibility(View.VISIBLE);
		        	viewHolder.img_add.setVisibility(View.GONE);
	        	}else
	        	{
	        		viewHolder.ivDelete.setVisibility(View.GONE);
		        	viewHolder.ivDragHandle.setVisibility(View.VISIBLE);
		        	viewHolder.img_icon.setVisibility(View.VISIBLE);
		        	viewHolder.img_add.setVisibility(View.VISIBLE);
		        	
	        	}

	        	
	        }
	        viewHolder.img_add.setOnClickListener(new AddClickListener(position, viewHolder));
	        viewHolder.ivDelete.setOnClickListener(new AddClickListener(position, viewHolder));
	        int resId = context.getResources().getIdentifier(item.NormalIcon, "drawable", context.getPackageName());
	        viewHolder.img_icon.setImageResource(resId);

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
				if (v == mViewHolder.img_add) {
					//撤单
					if (items != null) {
						int num = getCount();
						if (this.mPosition < num)
						{
							DBZQHomeActivityMarket item = (DBZQHomeActivityMarket)getItem(mPosition);
							item.IsDefault = true;
							mDragAdapter.remove(mPosition);//在适配器中”原位置“的数据。  
			                mDragAdapter.insert(item, mSepIndex);//在目标位置中插入被拖动的控件。
			                mSepIndex++;
						}
					}
				}else if (v == mViewHolder.ivDelete) {
					//撤单
					if (items != null) {
						int num = getCount();
						if (this.mPosition < num)
						{
							DBZQHomeActivityMarket item = (DBZQHomeActivityMarket)getItem(mPosition);
							item.IsDefault = false;
							mDragAdapter.remove(mPosition);//在适配器中”原位置“的数据。
							mSepIndex--;
			                mDragAdapter.insert(item, mSepIndex+1);//在目标位置中插入被拖动的控件。
						}
					}
					
				} 
			}
		}
	      
	    class ViewHolder {  
	        TextView tvName;  
	        ImageView img_icon;  
	        ImageView ivDelete;  
	        ImageView ivDragHandle; 
	        ImageView img_add;
	    }  
	}  
}
