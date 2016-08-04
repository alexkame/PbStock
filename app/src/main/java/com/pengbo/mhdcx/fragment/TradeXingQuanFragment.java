package com.pengbo.mhdcx.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.TradeXingQuanCDListAdapter;
import com.pengbo.mhdcx.adapter.TradeXingQuanListViewAdapter;
import com.pengbo.mhdcx.adapter.TradeXingQuanZhiPaiListAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdcx.ui.trade_activity.OnTradeFragmentListener;
import com.pengbo.mhdcx.view.HVListView;
public class TradeXingQuanFragment extends Fragment implements 
	OnCheckedChangeListener, OnItemClickListener, OnClickListener {
	
	public final static int VIEW_XQ = 0;
	public final static int VIEW_XQCD = 1;
	public final static int VIEW_XQZP = 2;
	public final static int VIEW_XQLSZP = 3;
	private MyApp mMyApp;
	public View mView;
	public Activity mActivity;
	
	private ListView mListView_XQ;
	private ListView mListView_XQCD;
	private HVListView mListView_XQZP;
	private LinearLayout mlLayout_xqHeader;
	private LinearLayout mlLayout_xqcdHeader;
	private LinearLayout mlLayout_xqzpHeader;
	private LinearLayout mlLayout_xqlszpCX;//历史指派查询
	private Button mBtn_ChaXun;
	private EditText mEdit_StartDate, mEdit_EndDate;
	private TradeXingQuanListViewAdapter mListAdapterXQ;
	private TradeXingQuanCDListAdapter mListAdapterXQCD;
	private TradeXingQuanZhiPaiListAdapter mListAdapterXQZP;
	private int mViewSwitcherIndex = VIEW_XQ; // 0:xq view;1:xqcd
	private TagLocalStockData mCurrentOptionData;
	
	private PBSTEP mListData_XQ;
	private PBSTEP mListData_XQWT;
	private PBSTEP mHoldList;
	private PBSTEP mListData_XQZP;//存储行权指派或者历史指派
	private boolean mbStartOrEnd = true;//true-选择起始日期
	private String mStartDate, mEndDate;//历史查询起始时间和结束时间（20150511）
	private int mStartYear = 2000, 
			mStartMonth = 1, 
			mStartDay = 1;
	private int mEndYear = 2000, 
			mEndMonth = 1, 
			mEndDay = 1;
	
	private OnTradeFragmentListener mListener;
	public com.pengbo.mhdcx.widget.AlertDialog mXQConfirmDialog;
	public com.pengbo.mhdcx.widget.AlertDialog mXQInputDialog;
	public DatePickerDialog mDatePickerDialog;
	
	Intent intent;
	public RadioButton mRBTJQKXQHE,mRBTXQWT,mRBTXQZP;//mRBTJQKXQHE 近期可行权合约       mRBTXQWT行权委托
	public RadioGroup mRG;
	private boolean mIsViewReady = false;
	public Dialog mProgress;
	
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case TradeNetConnect.MSG_ADAPTER_CD_BUTTON_CLICK: {
					proc_MSG_ADAPTER_XQCD_BUTTON_CLICK(msg);
				}
					break;
			}
			super.handleMessage(msg);
		};
	};
	
	public TradeXingQuanFragment(MyApp myApp)
	{
		mMyApp = myApp;
		initData();
	}

	private void initData()
	{
		if(mListData_XQ == null)
		{
		   mListData_XQ = new PBSTEP();
		}
		
		if(mHoldList == null)
		{
		   mHoldList = new PBSTEP();
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mXQConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
				getActivity().getParent()).builder();
		mXQInputDialog = new com.pengbo.mhdcx.widget.AlertDialog(
				getActivity().getParent()).builder();
		
	}

	@Override
	public void onAttach(Activity activity) {
		this.mActivity = activity;
		try {
			mListener = (OnTradeFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
		super.onAttach(activity);
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		mView=inflater.inflate(R.layout.trade_xingquan_fragment, null);
		mCurrentOptionData = new TagLocalStockData();
		initView();
		return mView;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_qsrq:// 点击起始日期
		{
			// 构建一个 DatePickerDialog 并显示
			mbStartOrEnd = true;
			mDatePickerDialog = new DatePickerDialog(getActivity()
						.getParent(), listener, mStartYear,
						mStartMonth - 1,
						mStartDay);
			mDatePickerDialog.show();
			
		
		}
			
			break;
		case R.id.edit_zzrq:// 点击终止日期
			// 构建一个 DatePickerDialog 并显示
			mbStartOrEnd = false;
			mDatePickerDialog = new DatePickerDialog(getActivity()
						.getParent(), listener, mEndYear,
						mEndMonth - 1,
						mEndDay);
			mDatePickerDialog.show();
			break;
		case R.id.btn_xingquan_chaxun:
			boolean bNeeded = true;
			if (mStartYear > mEndYear)
			{
				bNeeded = false;
			}else if(mStartYear == mEndYear)
			{
				if (mStartMonth > mEndMonth)
				{
					bNeeded = false;
				}else if(mStartMonth == mEndMonth)
				{
					if (mStartDay > mEndDay)
					{
						bNeeded = false;
					}
				}
			}
			if (!bNeeded)
			{
				Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), "起始日期应比截止日期早！", Toast.LENGTH_SHORT)
				.show();
				return;
			}
			showProgress();
			if (mListener != null)
			{
			    mListener.requestXQLSZP(mStartDate, mEndDate);
			}
			break;
		}
		
	}
	
	@Override
	public void onResume() {
		if (!this.isHidden()) {
			if (mViewSwitcherIndex == VIEW_XQ)
			{
				updateXQList();
			}else if (mViewSwitcherIndex == VIEW_XQCD)
			{
				updateXQCDList();
			}else if (mViewSwitcherIndex == VIEW_XQZP)
			{
				updateXQZPList();
			}else if (mViewSwitcherIndex == VIEW_XQLSZP)
			{
				updateXQLSZPList();
			}
		}
		closeProgress();
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if(!hidden)
		{
			if (mViewSwitcherIndex == VIEW_XQ)
			{
				updateXQList();
			}else if (mViewSwitcherIndex == VIEW_XQCD)
			{
				updateXQCDList();
			}else if (mViewSwitcherIndex == VIEW_XQZP)
			{
				updateXQZPList();
			}else if (mViewSwitcherIndex == VIEW_XQLSZP)
			{
				updateXQLSZPList();
			}
		}else
		{
			if (mXQConfirmDialog != null)
			{
				mXQConfirmDialog.dismiss();
			}
			if (mXQInputDialog != null)
			{
				mXQInputDialog.dismiss();
			}
			if (mDatePickerDialog != null)
			{
				mDatePickerDialog.dismiss();
			}
		}
		closeProgress();
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onDestroy() {
		closeProgress();
		if (mXQConfirmDialog != null)
		{
			mXQConfirmDialog.dismiss();
		}
		if (mXQInputDialog != null)
		{
			mXQInputDialog.dismiss();
		}
		if (mDatePickerDialog != null)
		{
			mDatePickerDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		closeProgress();
		super.onPause();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mRBTJQKXQHE=(RadioButton) mView.findViewById(R.id.jinqikexingquanheyue);
		mRBTXQWT=(RadioButton) mView.findViewById(R.id.xingquanweituo);
		mRBTXQZP=(RadioButton) mView.findViewById(R.id.xingquanzhipai);
		mRG=(RadioGroup) mView.findViewById(R.id.xingquan_radiogroup);
		mRG.setOnCheckedChangeListener(this);
		
		mlLayout_xqHeader = (LinearLayout) mView.findViewById(R.id.lLayout_xqheader);
		mlLayout_xqHeader.setVisibility(View.VISIBLE);
		mlLayout_xqcdHeader = (LinearLayout) mView.findViewById(R.id.lLayout_xqcdheader);
		mlLayout_xqcdHeader.setVisibility(View.GONE);
		mlLayout_xqzpHeader = (LinearLayout) mView.findViewById(R.id.lLayout_xqzpheader);
		mlLayout_xqzpHeader.setVisibility(View.GONE);
		
		mlLayout_xqlszpCX = (LinearLayout) mView.findViewById(R.id.llayout_xingquan_chaxun);
		mlLayout_xqlszpCX.setVisibility(View.GONE);
		
		mEdit_StartDate = (EditText) mView.findViewById(R.id.edit_qsrq);
		mEdit_EndDate = (EditText) mView.findViewById(R.id.edit_zzrq);
		mEdit_StartDate.setOnClickListener(this);
		mEdit_EndDate.setOnClickListener(this);
		
		mBtn_ChaXun = (Button) mView.findViewById(R.id.btn_xingquan_chaxun);
		mBtn_ChaXun.setVisibility(View.GONE);
		mBtn_ChaXun.setOnClickListener(this);
		// 设置行权指派宽度

		int iScreenWidth = ViewTools.getScreenSize(getActivity()).widthPixels;
		ViewGroup.LayoutParams params = mlLayout_xqzpHeader.getLayoutParams();

		params.width = (iScreenWidth/4) * 11;
		mlLayout_xqzpHeader.setLayoutParams(params);
		
		if (mListView_XQ == null) {
			mListView_XQ = (ListView) mView.findViewById(R.id.lv_xingquan);
			mListView_XQ.setOnItemClickListener(this);
			
			mListAdapterXQ = new TradeXingQuanListViewAdapter(getActivity(), mListData_XQ);
			mListView_XQ.setAdapter(mListAdapterXQ);
			mListView_XQ.setVisibility(View.VISIBLE);

		}
		
		if (mListView_XQCD == null) {
			mListView_XQCD = (ListView) mView.findViewById(R.id.lv_xingquan_cd);
			mListView_XQCD.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					// 撤单
					int pos = position;
					if (mListData_XQWT != null) {
						int num = mListData_XQWT.GetRecNum();
						if (pos < num) {
							mListData_XQWT.GotoRecNo(pos);
							String WTZT = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_WTZT);

							if(DataTools.isCDStatusEnabled(WTZT))
							{
								// pop message
								if (mXQConfirmDialog != null) {
									mXQConfirmDialog.dismiss();
								} else {
									mXQConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
											getActivity().getParent())
											.builder();
								}
								mXQConfirmDialog.clear();
								mXQConfirmDialog
										.setMsg("是否确定要进行撤单？")
										.setCancelable(false)
										.setCanceledOnTouchOutside(false)
										.setPositiveButton("确定",
												new OnClickListener() {
													@Override
													public void onClick(View v) {
														if (mListener != null)
														{
															String market = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_SCDM);
															String wtbh = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_WTBH);
															String xdxw = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_XDXW);
															String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(market);
															String xwh = mMyApp.mTradeData.GetXWHFromMarket(market);
															mListener.requestXQCD(market, wtbh, gdzh, xwh, xdxw);
														}

													}
												})
										.setNegativeButton("取消",
												new OnClickListener() {
													@Override
													public void onClick(View v) {

													}
												}).show();
							}else
							{
								// pop message
								if (mXQConfirmDialog != null) {
									mXQConfirmDialog.dismiss();
								} else {
									mXQConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
											getActivity().getParent())
											.builder();
								}
								mXQConfirmDialog.clear();
								mXQConfirmDialog
										.setMsg("此委托无法进行撤单")
										.setCancelable(false)
										.setCanceledOnTouchOutside(false)
										.setPositiveButton("确定",
												new OnClickListener() {
													@Override
													public void onClick(View v) {
														

													}
												})
										.show();
							}
						}
					}
				}
				
			});
			
			mListData_XQWT = new PBSTEP();
			mListAdapterXQCD = new TradeXingQuanCDListAdapter(getActivity(), mListData_XQWT, mHandler);
			mListView_XQCD.setAdapter(mListAdapterXQCD);
			mListView_XQCD.setVisibility(View.GONE);
		}
		
		if (mListView_XQZP == null) {
			mListView_XQZP = (HVListView) mView.findViewById(R.id.lv_xingquan_zp);
			mListView_XQZP.mListHead = mlLayout_xqzpHeader;
			mListView_XQZP.setWidth(params.width);
			mListView_XQZP.setItemId(R.id.hv_xqzp_item);
			mListView_XQZP.setScreenItemNum(4);
			mListData_XQZP = new PBSTEP();
			mListAdapterXQZP = new TradeXingQuanZhiPaiListAdapter(getActivity(), mListData_XQZP, mHandler);
			mListView_XQZP.setAdapter(mListAdapterXQZP);
			mListView_XQZP.setVisibility(View.GONE);
			mListView_XQZP.resetToDefaultPos();
		}
	}
	
	public void UpdateXQSL(PBSTEP aStep)
	{
		String kxqsl = "";
		String stockCode = mCurrentOptionData.HQData.code;
		int nMarket = mCurrentOptionData.HQData.market;

		String optionName = mCurrentOptionData.name;
		TagLocalStockData optionData = null;
		if (aStep.GetRecNum() > 0)
		{
			//可行权数量
			kxqsl = aStep.GetFieldValueStringWithBackup(STEP_Define.STEP_KXQSL, STEP_Define.STEP_WTSL);
			//期权代码
			stockCode = aStep.GetFieldValueString(STEP_Define.STEP_HYDM);
			//市场代码
			String marketCode = aStep.GetFieldValueString(STEP_Define.STEP_SCDM);
			
			nMarket = TradeData.GetHQMarketFromTradeMarket(marketCode);
			optionData = new TagLocalStockData();
			if(mMyApp.mHQData.getData(optionData, (short) nMarket, stockCode, false))
			{
				mCurrentOptionData = optionData;
				optionName = optionData.name;
			}
		}
		String num = "";
		if (!kxqsl.isEmpty()) {
			num = kxqsl;
		}else
		{
			kxqsl = Global_Define.STRING_VALUE_EMPTY;
		}
		String msg1 = String.format("%s\n可行权数量：%s", optionName, kxqsl);
		if (mXQInputDialog != null) {
			mXQInputDialog.setMsg(msg1);
			mXQInputDialog.setEdit(num);
		}
	}
	
	//aStep - 可行权数量查询返回数据
	public void ConfirmToXQ(PBSTEP aStep)
	{
		String kxqsl = "";
		String stockCode = mCurrentOptionData.HQData.code;
		int nMarket = mCurrentOptionData.HQData.market;

		String optionName = mCurrentOptionData.name;
		TagLocalStockData optionData = null;
		if (aStep.GetRecNum() > 0)
		{
			//可行权数量
			kxqsl = aStep.GetFieldValueStringWithBackup(STEP_Define.STEP_KXQSL, STEP_Define.STEP_WTSL);

			//期权代码
			stockCode = aStep.GetFieldValueString(STEP_Define.STEP_HYDM);
			//市场代码
			String marketCode = aStep.GetFieldValueString(STEP_Define.STEP_SCDM);
			
			nMarket = TradeData.GetHQMarketFromTradeMarket(marketCode);
			optionData = new TagLocalStockData();
			if(mMyApp.mHQData.getData(optionData, (short) nMarket, stockCode, false))
			{
				mCurrentOptionData = optionData;
				optionName = optionData.name;
			}
		}

		String num = "";
		if (!kxqsl.isEmpty()) {
			num = kxqsl;
		}else
		{
			kxqsl = Global_Define.STRING_VALUE_EMPTY;
		}
		String msg1 = String.format("%s\n可行权数量：%s", optionName, kxqsl);

		if (mXQInputDialog != null) {
			mXQInputDialog.dismiss();
		} else {
			mXQInputDialog = new com.pengbo.mhdcx.widget.AlertDialog(
					getActivity().getParent()).builder();
		}

		mXQInputDialog.clear();
		mXQInputDialog.setMsg(msg1);
		mXQInputDialog.setEdit(num);
		mXQInputDialog.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确认行权", new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 行权
						final String num = mXQInputDialog.getEditText();
						double xqjz = calcXQJZ(STD.StringToInt(num));
						if (xqjz < 0)
						{
							String msg = String.format("当前合约价值为%.2f小于0，若您行权会给您带来损失，是否继续行权？", xqjz);
							//pop message
							if (mXQConfirmDialog != null) {
								mXQConfirmDialog.dismiss();
							} else {
								mXQConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
										getActivity().getParent()).builder();
							}
							mXQConfirmDialog.clear();
							mXQConfirmDialog.setTitle("提示").setMsg(msg)
											.setCancelable(false)
											.setCanceledOnTouchOutside(false)
											.setPositiveButton("继续行权", new OnClickListener() {
										@Override
										public void onClick(View v) {
											// 行权
											if (mListener != null)
											{
												String marketCode = TradeData.GetTradeMarketFromHQMarket(mCurrentOptionData.HQData.market, mCurrentOptionData.group);
												String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(marketCode);
												String xwh = mMyApp.mTradeData.GetXWHFromMarket(marketCode);
												mListener.requestXQ(marketCode, mCurrentOptionData.HQData.code, num, gdzh, xwh, "1");
											}
											
										}
									}).setNegativeButton("取消", new OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}).show();
						}else
						{
							//行权
							if (mListener != null)
							{
								String marketCode = TradeData.GetTradeMarketFromHQMarket(mCurrentOptionData.HQData.market, mCurrentOptionData.group);
								String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(marketCode);
								String xwh = mMyApp.mTradeData.GetXWHFromMarket(marketCode);
								mListener.requestXQ(marketCode, mCurrentOptionData.HQData.code, num, gdzh, xwh, "1");
							}
						}
						
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}
	
	private double calcXQJZ(int xqsl)
	{
		double dInValue = 0.0;
		TagLocalStockData stockData = new TagLocalStockData(); //标的
		mMyApp.mStockConfigData.search(stockData, mCurrentOptionData.optionData.StockMarket, mCurrentOptionData.optionData.StockCode);
		
		float fStockPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData );
		float fOptionExecutePrice = mCurrentOptionData.optionData.StrikePrice;
		
		//0->看涨(认购)  1->看跌(认沽)
        if (mCurrentOptionData.optionData.OptionCP == 0) {
        	dInValue = fStockPrice - fOptionExecutePrice;
        }
        else if (mCurrentOptionData.optionData.OptionCP == 1)
        {
        	dInValue = fOptionExecutePrice - fStockPrice ;
        }
        
        return (dInValue*mCurrentOptionData.optionData.StrikeUnit*xqsl);
	}
	
	public void updateXQList() {
		if (this.mViewSwitcherIndex == VIEW_XQ)
		{
			GetHoldStock_XQ(mListData_XQ);
			mListAdapterXQ.notifyDataSetChanged();
		}
	}

	public void updateXQCDList() {
		if (this.mViewSwitcherIndex == VIEW_XQCD) {
			mMyApp.mTradeData.GetFJYWT(mListData_XQWT, false);
			mListAdapterXQCD.notifyDataSetChanged();
		}
	}
	
	public void updateXQZPList() {
		if (this.mViewSwitcherIndex == VIEW_XQZP) {
			mMyApp.mTradeData.GetXQZP(mListData_XQZP);
			mListAdapterXQZP.notifyDataSetChanged();
		}
	}
	
	public void updateXQLSZPList() {
		if (this.mViewSwitcherIndex == VIEW_XQLSZP) {
			closeProgress();
			mListData_XQZP.Free();
			mMyApp.mTradeData.GetXQLSZP(mListData_XQZP);
			mListAdapterXQZP.notifyDataSetChanged();
		}
	}
	
	public int	GetHoldStock_XQ(PBSTEP aStep)
	{
		mMyApp.mTradeData.GetHoldStock(mHoldList);
		aStep.Copy(mHoldList);
        
        int nIndex = 0;
        while (aStep.GetRecNum() > 0) {
            
            aStep.GotoRecNo(nIndex);
            
            TagLocalStockData stockData = new TagLocalStockData();
			String code = aStep.GetFieldValueString(STEP_Define.STEP_HYDM);
			String market = aStep.GetFieldValueString(STEP_Define.STEP_SCDM);
			
			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
			mMyApp.mHQData.getData(stockData, (short) nMarket, code, false);

			float fMMBZ = STD.StringToValue(aStep.GetFieldValueString(STEP_Define.STEP_MMLB));
			boolean bBuy = (fMMBZ == 0f ? true : false);
            //int nDays = ViewTools.getDaysDruationFromToday(stockData.optionData.StrikeDate);
            boolean canXQ = ViewTools.isStrikeDateInMonth(stockData.optionData.StrikeDate);
			String currentNum = aStep.GetFieldValueString(STEP_Define.STEP_DQSL);
            float fNum = STD.StringToValue(currentNum);
			if (!canXQ || !bBuy || fNum <= 0) {//nDays > 30
				nIndex = aStep.DeleteCurrRecord();
			}else
            {
                nIndex++;
            }
            
            if (nIndex == -1 || nIndex >= aStep.GetRecNum()) {
                break;
            }
        }
		return aStep.GetRecNum();
	}
	
	private void changeView(int index) {
		switch (index) {
		case VIEW_XQ:
			if (mViewSwitcherIndex != index) {
				mViewSwitcherIndex = index;
				mlLayout_xqHeader.setVisibility(View.VISIBLE);
				mlLayout_xqcdHeader.setVisibility(View.GONE);
				mlLayout_xqzpHeader.setVisibility(View.GONE);
				mListView_XQ.setVisibility(View.VISIBLE);
				mListView_XQCD.setVisibility(View.GONE);
				mListView_XQZP.setVisibility(View.GONE);
				mlLayout_xqlszpCX.setVisibility(View.GONE);
				mBtn_ChaXun.setVisibility(View.GONE);
				updateXQList();
			}
			break;
		case VIEW_XQCD:
			if (mViewSwitcherIndex != index) {
				mListener.requestXQWT();
				mViewSwitcherIndex = index;
				mlLayout_xqHeader.setVisibility(View.GONE);
				mlLayout_xqcdHeader.setVisibility(View.VISIBLE);
				mlLayout_xqzpHeader.setVisibility(View.GONE);
				mListView_XQ.setVisibility(View.GONE);
				mListView_XQCD.setVisibility(View.VISIBLE);
				mListView_XQZP.setVisibility(View.GONE);
				mlLayout_xqlszpCX.setVisibility(View.GONE);
				mBtn_ChaXun.setVisibility(View.GONE);
				updateXQCDList();
			}
			break;
		case VIEW_XQZP:
			if (mViewSwitcherIndex != index) {
				mListener.requestXQZP(null, null, null);
				mViewSwitcherIndex = index;
				mlLayout_xqHeader.setVisibility(View.GONE);
				mlLayout_xqcdHeader.setVisibility(View.GONE);
				mlLayout_xqzpHeader.setVisibility(View.VISIBLE);
				mListView_XQ.setVisibility(View.GONE);
				mListView_XQCD.setVisibility(View.GONE);
				mListView_XQZP.setVisibility(View.VISIBLE);
				mlLayout_xqlszpCX.setVisibility(View.GONE);
				mBtn_ChaXun.setVisibility(View.GONE);
				updateXQZPList();
			}
			break;
		case VIEW_XQLSZP:
			if (mViewSwitcherIndex != index) {

				Calendar cal = Calendar.getInstance(); 
				mEndYear = cal.get(Calendar.YEAR);    
				mEndMonth = cal.get(Calendar.MONTH) + 1;  
				mEndDay = cal.get(Calendar.DATE);
				String strM = (mEndMonth < 10) ? ("0"+mEndMonth) : (""+mEndMonth);
				String strD = (mEndDay < 10) ? ("0"+mEndDay) : (""+mEndDay);
				mEndDate = String.format("%d%s%s", mEndYear, strM, strD);
				
				cal.add(Calendar.MONTH, -1);
				mStartYear = cal.get(Calendar.YEAR);    
				mStartMonth = cal.get(Calendar.MONTH)+1;    
				mStartDay = cal.get(Calendar.DATE);
				strM = (mStartMonth < 10) ? ("0"+mStartMonth) : (""+mStartMonth);
				strD = (mStartDay < 10) ? ("0"+mStartDay) : (""+mStartDay);
				mStartDate = String.format("%d%s%s", mStartYear, strM, strD);
				
				mEdit_StartDate.setText(ViewTools.formatDate(mStartDate));
				mEdit_EndDate.setText(ViewTools.formatDate(mEndDate));

				mViewSwitcherIndex = index;
				mlLayout_xqHeader.setVisibility(View.GONE);
				mlLayout_xqcdHeader.setVisibility(View.GONE);
				mlLayout_xqzpHeader.setVisibility(View.VISIBLE);
				mListView_XQ.setVisibility(View.GONE);
				mListView_XQCD.setVisibility(View.GONE);
				mListView_XQZP.setVisibility(View.VISIBLE);
				mlLayout_xqlszpCX.setVisibility(View.VISIBLE);
				mBtn_ChaXun.setVisibility(View.VISIBLE);
				updateXQLSZPList();
			}
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.jinqikexingquanheyue:
			changeView(VIEW_XQ);
			break;
		case R.id.xingquanweituo:
			changeView(VIEW_XQCD);
			break;
		case R.id.xingquanzhipai:
			changeView(VIEW_XQZP);
			break;
		case R.id.lishichaxun:
			changeView(VIEW_XQLSZP);
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (this.mListData_XQ != null) {
			int num = this.mListData_XQ.GetRecNum();
			if (position < num) {

				TagLocalStockData optionData = new TagLocalStockData();
				mListData_XQ.GotoRecNo(position);
				
				String code = mListData_XQ
						.GetFieldValueString(STEP_Define.STEP_HYDM);
				String market = mListData_XQ
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
				mMyApp.mHQData.getData(optionData, (short) nMarket, code, false);

				mCurrentOptionData = optionData;
				{
					//行权
					//查询可行权数量
					if (mListener != null)
					{
						String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(market);
						String xwh = mMyApp.mTradeData.GetXWHFromMarket(market);
						mListener.requestKXQSL(code, market, gdzh, xwh);
					}
				}
			}
		}
	}
	
	private void proc_MSG_ADAPTER_XQCD_BUTTON_CLICK(Message msg) {
		// 撤单
		int pos = msg.arg1;
		if (mListData_XQWT != null) {
			int num = mListData_XQWT.GetRecNum();
			if (pos < num) {
				mListData_XQWT.GotoRecNo(pos);

				if (mListener != null)
				{
					String market = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_SCDM);
					String wtbh = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_WTBH);
					String xdxw = mListData_XQWT.GetFieldValueString(STEP_Define.STEP_XDXW);
					String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(market);
					String xwh = mMyApp.mTradeData.GetXWHFromMarket(market);
					mListener.requestXQCD(market, wtbh, gdzh, xwh, xdxw);
				}
			}
		}
	}
	
	// 日期选择对话框的 DateSet 事件监听器
	private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() { //

		@Override
		public void onDateSet(DatePicker arg0, int year, int month, int day) {
			
			if (mbStartOrEnd)
			{
				mStartYear = year;
				mStartMonth = month + 1;
				mStartDay = day;
				
				String strM = (mStartMonth < 10) ? ("0"+mStartMonth) : (""+mStartMonth);
				String strD = (mStartDay < 10) ? ("0"+mStartDay) : (""+mStartDay);
				mStartDate = String.format("%d%s%s", mStartYear, strM, strD);
				
				mEdit_StartDate.setText(ViewTools.formatDate(mStartDate));
			}else
			{
				mEndYear = year;
				mEndMonth = month + 1;
				mEndDay = day;
				
				String strM = (mEndMonth < 10) ? ("0"+mEndMonth) : (""+mEndMonth);
				String strD = (mEndDay < 10) ? ("0"+mEndDay) : (""+mEndDay);
				mEndDate = String.format("%d%s%s", mEndYear, strM, strD);
				
				mEdit_EndDate.setText(ViewTools.formatDate(mEndDate));
			}
		}
	};
	
	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(getActivity().getParent(), R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText("查询中，请稍后......");
			mProgress.setCancelable(true);
		}
		mProgress.show();
	}

	protected void closeProgress() {

		if (mProgress != null && mProgress.isShowing()) {
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}
}
