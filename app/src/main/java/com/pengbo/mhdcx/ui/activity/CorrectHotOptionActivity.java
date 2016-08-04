package com.pengbo.mhdcx.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.CorrectHotGridViewAdapter;
import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdcx.adapter.MyDialogAdapter;
import com.pengbo.mhdcx.adapter.MyTargetDialogAdapter;
import com.pengbo.mhdcx.bean.MyGridView;
import com.pengbo.mhdcx.bean.ScreenCondition;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdcx.widget.MyListDialog;
import com.pengbo.mhdcx.widget.MyTargetListDialog;
import com.pengbo.mhdcx.widget.MyListDialog.Dialogcallback;
import com.pengbo.mhdcx.widget.MyTargetListDialog.DialogcallbackTarget;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class CorrectHotOptionActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, OnItemClickListener {

	
	public static final int RESULT_CODE_EDIT = 10000;
	public TextView head_tv_middle, head_btn_edit,// 左边的编辑按钮
			head_btn_searchlist;// 右边的搜索期权列表
	private TextView mTV_ZDTitle;

	private Intent intent;

	/******* six button ***/
	public CorrectHotGridViewAdapter editGridViewAdapter;

	/** 标的物 和 涨跌幅度 两个 显示文本的 TextView 下面是 两个控件 所在的条目 **/
	public TextView mTV_SubjectMatter, mTV_UpAndDown;
	
	/** 标的物 和 涨跌幅度 两个 两个控件 所在的条目 **/
	public LinearLayout mLayout_SubjectMatter, mLayout_UpAndDown;

	
	private GridView gv;

	private RadioGroup radiogroup_ganggan, radiogroup_huoyuedu;

	private RadioButton radiobutton_ganggan0, radiobutton_ganggan1, radiobutton_ganggan2,
			radiobutton_ganggan3, radiobutton_huoyuedu1, radiobutton_huoyuedu2,
			radiobutton_huoyuedu3;

	private Button btn_start_optional;

	private HeadListDataService hService;

	private MyTargetListDialog myTargetDialog;// 弹出的标的对话框 存储的是对象


	private ScreenCondition sc;
	
	private String stock_Code = null;
	private short stock_Market = 0;
	private String stock_Name=null;
	private int stock_GridID=0;
	public float  stock_hq_zdf=0f;  //涨跌幅度

	public int stock_LeverId=3;  //杠杆倍数 -默认是全部，0-<10;1-10~20;2->20;3-全部
	public int	stock_Vitality=0;  //活跃度 
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.correct_hotoption_activity);
		initView();
		initSpinner();// 标的物 Spinner 相关
		initSixButton();// 长短期看涨 
		initSpinner2();// 涨跌幅度
		readDetailScreen();
		afterReadCheckedView();

	}


	private void afterReadCheckedView() {
		//读完之后把值全部附值给全局的属性 
		stock_Code=sc.getCode();
		stock_Market=sc.getMarket();
		stock_Name=sc.getName();
		stock_GridID=sc.getGridViewId();
		stock_hq_zdf=sc.getField_hq_zdf();
		stock_LeverId=sc.getLeverId();
		stock_Vitality=sc.getVitality();
		
		L.e("首次进入默认被选中的参数", stock_Code+","+stock_Market+","+stock_Name+","+stock_GridID+","+stock_hq_zdf+","+stock_LeverId+","+stock_Vitality);
		mTV_SubjectMatter.setText(sc == null ? "错" : sc.getName());
		editGridViewAdapter.setSeclection(sc.getGridViewId());
		mTV_UpAndDown.setText(((int) (sc.getField_hq_zdf() * 100) + "%"));

		if (sc.getLeverId() == 0) {
			radiobutton_ganggan1.setChecked(true);
			// radiogroup_ganggan.check(R.id.ganggan_1);
		} else if (sc.getLeverId() == 1) {
			radiobutton_ganggan2.setChecked(true);
			// radiogroup_ganggan.check(R.id.ganggan_2);
		} else if (sc.getLeverId() == 2) {
			radiobutton_ganggan3.setChecked(true);
			// radiogroup_ganggan.check(R.id.ganggan_3);
		} else if (sc.getLeverId() == 3) {
			radiobutton_ganggan0.setChecked(true);
		}

		if (sc.getVitality() == 0) {
			radiobutton_huoyuedu1.setChecked(true);
			// radiogroup_ganggan.check(R.id.huoyuedu_1);
		} else if (sc.getVitality() == 1) {
			radiobutton_huoyuedu2.setChecked(true);
			// radiogroup_ganggan.check(R.id.huoyuedu_1);
		} else if (sc.getVitality() == 2) {
			radiobutton_huoyuedu3.setChecked(true);
			// radiogroup_ganggan.check(R.id.huoyuedu_1);
		}
		
		if (stock_GridID < 3)
		{
			mTV_ZDTitle.setText(this.getResources().getString(R.string.IDS_KanZhangFuDu));
		}else
		{
			mTV_ZDTitle.setText(this.getResources().getString(R.string.IDS_KanDieFuDu));
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		hService = new HeadListDataService(CorrectHotOptionActivity.this);

		/** 修改筛选条件界面抬头中间的文本控件初始化及设置 **/
		head_tv_middle = (TextView) this
				.findViewById(R.id.header_middle_textview);
		//head_tv_middle.setText("修改筛选条件");

		head_tv_middle.setText(R.string.IDS_XiuGaiShaiXuanTiaoJian);
		/** 修改筛选条件页面取消按钮的显示 及监听事件 **/
		head_btn_edit = (TextView) this.findViewById(R.id.header_left_edit);
		head_btn_edit.setText(R.string.IDS_QuXiao);
		head_btn_edit.setVisibility(View.VISIBLE);
		head_btn_edit.setOnClickListener(this);

		/** 修改筛选条件页面要把期权搜索按钮隐藏 此页 不展示 **/
		head_btn_searchlist = (TextView) this
				.findViewById(R.id.header_right_search);
		head_btn_searchlist.setVisibility(View.GONE);

		radiogroup_ganggan = (RadioGroup) this
				.findViewById(R.id.ganggan_radiogroup);
		radiobutton_ganggan0 = (RadioButton) this.findViewById(R.id.ganggan_0);
		radiobutton_ganggan1 = (RadioButton) this.findViewById(R.id.ganggan_1);
		radiobutton_ganggan2 = (RadioButton) this.findViewById(R.id.ganggan_2);
		radiobutton_ganggan3 = (RadioButton) this.findViewById(R.id.ganggan_3);

		radiogroup_huoyuedu = (RadioGroup) this
				.findViewById(R.id.huoyuedu_radiogroup);
		radiobutton_huoyuedu1 = (RadioButton) this
				.findViewById(R.id.huoyuedu_1);
		radiobutton_huoyuedu2 = (RadioButton) this
				.findViewById(R.id.huoyuedu_2);
		radiobutton_huoyuedu3 = (RadioButton) this
				.findViewById(R.id.huoyuedu_3);
		
		radiogroup_ganggan.setOnCheckedChangeListener(this);
		radiogroup_huoyuedu.setOnCheckedChangeListener(this);

		// 开始筛选按钮
		btn_start_optional = (Button) this
				.findViewById(R.id.correct_hot_page_start_optional_btn);
		btn_start_optional.setOnClickListener(this);
		
		mTV_ZDTitle = (TextView) this.findViewById(R.id.tv_zdtitle);
	}

	/**
	 * 标的物 Spinner 相关
	 */
	private void initSpinner() {

		mTV_SubjectMatter = (TextView) this
				.findViewById(R.id.btn_customerSpinner);

		// 点击整个条目 ，弹出对话框 ------
		mLayout_SubjectMatter = (LinearLayout) this
				.findViewById(R.id.correct_hotoption_activity_layout_biaodiwu);

		mLayout_SubjectMatter.setOnClickListener(this);
	}

	/**
	 * 初始化"短期看涨", "中期看涨", "长期看涨 ", "短期看跌 ","中期看跌", "长期看跌"
	 * 
	 */

	private void initSixButton() {
		gv = (GridView) this.findViewById(R.id.correct_hotoption_gv);
		List<MyGridView> lists = new ArrayList<MyGridView>();
		String[] tv_ups = new String[] { "短期看涨", "中期看涨", "长期看涨 ", "短期看跌 ",
				"中期看跌", "长期看跌" };
		String[] tv_downs = new String[] { "一个月左右", "1~3个月", "3~12个月", "一个月左右",
				"1~3个月", "3~12个月" };
		for (int i = 0; i < tv_ups.length; i++) {
			MyGridView myGridView = new MyGridView();
			myGridView.setUptext(tv_ups[i]);
			myGridView.setDowntext(tv_downs[i]);
			lists.add(myGridView);
		}
		editGridViewAdapter = new CorrectHotGridViewAdapter(lists,
				CorrectHotOptionActivity.this);
		gv.setAdapter(editGridViewAdapter);
		gv.setOnItemClickListener(this);
		
		
	}

	/**
	 * 涨跌幅度
	 */
	private void initSpinner2() {
		mTV_UpAndDown = (TextView) this.findViewById(R.id.btn_customerSpinner2);

		// 点击整个条目 ，弹出对话框 ------
		mLayout_UpAndDown = (LinearLayout) this
				.findViewById(R.id.correct_hotoption_activity_layout_upanddown);

		mLayout_UpAndDown.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left_edit:
			// intent = new Intent(this, ScreenActivity.class);
			// startActivity(intent);
			this.finish();
			break;

		case R.id.correct_hotoption_activity_layout_biaodiwu:

			myTargetDialog = new MyTargetListDialog(
					CorrectHotOptionActivity.this);
			MyTargetDialogAdapter adapter = new MyTargetDialogAdapter(
					CorrectHotOptionActivity.this, getTargetTagCodeInfo());
			myTargetDialog.setContent(adapter);
			myTargetDialog.setDialogCallback(dialogcallback_biaodi);
			myTargetDialog.show();
			break;

		case R.id.correct_hotoption_activity_layout_upanddown:
			MyListDialog myDialog = new MyListDialog(
					CorrectHotOptionActivity.this).builder();
			MyDialogAdapter adapter_zdf = new MyDialogAdapter(
					CorrectHotOptionActivity.this, getPercentDataUpDown());
			myDialog.setContent(adapter_zdf);
			myDialog.setDialogCallback(dialogcallback2);
			myDialog.setCancelable(true);
			myDialog.setCanceledOnTouchOutside(true);
			myDialog.show();

			break;
		case R.id.correct_hot_page_start_optional_btn:
			intent = new Intent();
			
			saveObjectToShared();
			
			setResult(RESULT_CODE_EDIT, intent);

			CorrectHotOptionActivity.this.finish();
		default:
			break;
		}
	}

	private List<TagCodeInfo> getTargetTagCodeInfo() {
		List<TagCodeInfo> mTagCodeInfo = new ArrayList<TagCodeInfo>();
		List<TagCodeInfo> mNetTagCodeInfo = null;
		mNetTagCodeInfo = hService.getTagCodeInfoRemoveNull();
		TagCodeInfo info = new TagCodeInfo();
		info.name = "全部标的";
		info.code = ScreenCondition.SCREEN_ALL_STOCK_CODE;
		info.group = 0;
		info.market = 0;
		info.group = 0;
		mTagCodeInfo.add(info);
		for (int i = 0; i < mNetTagCodeInfo.size(); i++) {
			mTagCodeInfo.add(mNetTagCodeInfo.get(i));
		}
		
		for (int i = 0; i < mTagCodeInfo.size(); i++) {
			for (int j = mTagCodeInfo.size()-1; j > i; j--) {
				String s1 =mTagCodeInfo.get(j).name;
				String s2 =mTagCodeInfo.get(i).name;
				if (s1.equals(s2)) {
					mTagCodeInfo.get(j).name = s1 +"("+mTagCodeInfo.get(j).code+")";
					mTagCodeInfo.get(i).name = s2 +"("+mTagCodeInfo.get(i).code+")";
				}
			}
		}

		
		return mTagCodeInfo;
	}

	/**
	 * 标的对象的回掉函数
	 */
	DialogcallbackTarget dialogcallback_biaodi = new DialogcallbackTarget() {

		@Override
		public void dialogdo(final int index) {
			mTV_SubjectMatter.setText(getTargetTagCodeInfo().get(index).name);

			new Thread() {
				public void run() {
					stock_Code = getTargetTagCodeInfo().get(index).code;
					stock_Market = getTargetTagCodeInfo().get(index).market;
					stock_Name=getTargetTagCodeInfo().get(index).name;

					L.e("============= dialogcallback  标的 ==============",
							"----------------");
					L.e("stockCode,stockMarket", stock_Code+","+stock_Market+","+stock_Name+","+stock_GridID+","+stock_hq_zdf+","+stock_LeverId+","+stock_Vitality);
					L.e("============= dialogcallcack  标的  ==============",
							"----------------");
					
				};
			}.start();
		}

	};

	/**
	 * 给涨跌度附值
	 * 
	 * @return
	 */
	private List<String> getDataUpDown() {
		List<String> dataupdown = new ArrayList<String>();
		dataupdown.add("0.05");
		dataupdown.add("0.1");
		dataupdown.add("0.15");
		dataupdown.add("0.2");
		dataupdown.add("0.3");
		dataupdown.add("0.5");
		return dataupdown;
	}
	
	private List<String> getPercentDataUpDown(){
		List<String> percents=new ArrayList<String>();
		percents.add("5%");
		percents.add("10%");
		percents.add("15%");
		percents.add("20%");
		percents.add("30%");
		percents.add("50%");
		return percents;
		
	}

	/**
	 * 涨跌度的回掉函数
	 */
	Dialogcallback dialogcallback2 = new Dialogcallback() {
		@Override
		public void dialogdo(final int index) {
			mTV_UpAndDown.setText((int) (STD.StringToValue(getDataUpDown().get(
					index)) * 100)
					+ "%");
			
			new Thread() {
				public void run() {
					stock_hq_zdf = STD.StringToValue(getDataUpDown().get(index));

					L.e("=============涨跌度的回掉函数=============",
							"----------------");
					L.e("stockCode,stockMarket", stock_Code+","+stock_Market+","+stock_Name+","+stock_GridID+","+stock_hq_zdf+","+stock_LeverId+","+stock_Vitality);
					L.e("===========涨跌度的回掉函数 ==============",
							"----------------");
					
				};
			}.start();

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// arg1是当前item的view，通过它可以获得该项中的各个组件。
		// arg2是当前item的ID。这个id根据你在适配器中的写法可以自己定义。
		// arg3是当前的item在listView中的相对位置！
		// 设置适配器
		editGridViewAdapter.setSeclection(position);
		editGridViewAdapter.notifyDataSetChanged();

		stock_GridID=position;
		
		L.e("点击 GridView后改变的值", stock_Code+","+stock_Market+","+stock_Name+","+stock_GridID+","+stock_hq_zdf+","+stock_LeverId+","+stock_Vitality);
		//

		if (stock_GridID < 3)
		{
			mTV_ZDTitle.setText(this.getResources().getString(R.string.IDS_KanZhangFuDu));
		}else
		{
			mTV_ZDTitle.setText(this.getResources().getString(R.string.IDS_KanDieFuDu));
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		switch (checkedId) {
		case R.id.ganggan_0:
			stock_LeverId=3;
			break;
		case R.id.ganggan_1:
			stock_LeverId=0;
			break;
		case R.id.ganggan_2:
			stock_LeverId=1;
			break;
		case R.id.ganggan_3:
			stock_LeverId=2;
			break;
		case R.id.huoyuedu_1:
			stock_Vitality=0;
			break;
		case R.id.huoyuedu_2:
			stock_Vitality=1;
			break;
		case R.id.huoyuedu_3:
			stock_Vitality=2;
			break;
		default:
			break;
		}
	}
	
	
	
	public void readDetailScreen() {String mGetSave = PreferenceEngine.getInstance().getHQQueryCondition();

	sc = new ScreenCondition();

	sc = new ScreenCondition();
	sc.setCode(STD.GetValue(mGetSave, 1, '|'));
	sc.setMarket((short) STD.StringToInt(STD.GetValue(mGetSave, 2, '|')));
	sc.setName(STD.GetValue(mGetSave, 3, '|'));
	sc.setGridViewId(STD.StringToInt(STD.GetValue(mGetSave, 4, '|')));
	sc.setField_hq_zdf(STD.StringToValue(STD.GetValue(mGetSave, 5, '|')));
	sc.setLeverId(STD.StringToInt(STD.GetValue(mGetSave, 6, '|')));
	sc.setVitality(STD.StringToInt(STD.GetValue(mGetSave, 7, '|')));
}
	
	
	private void saveObjectToShared() {
		ScreenCondition scon=new ScreenCondition();
		scon=new ScreenCondition();
		scon.setCode(stock_Code);
		scon.setMarket(stock_Market);
		scon.setName(stock_Name);
		scon.setGridViewId(stock_GridID);
		scon.setField_hq_zdf(stock_hq_zdf);
		scon.setLeverId(stock_LeverId);
		scon.setVitality(stock_Vitality);

		String mTiaoJianInfo = scon.getCode() + "|" + scon.getMarket() + "|" + scon.getName() + "|" + scon.getGridViewId() + "|" + scon.getField_hq_zdf() + "|" + scon.getLeverId()
				+ "|" + scon.getVitality();
		PreferenceEngine.getInstance().saveHQQueryCondition(mTiaoJianInfo);
	}


}
