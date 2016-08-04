package com.pengbo.mhdzq.fragment;

import java.util.ArrayList;
import java.util.Map;

import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.zq_trade_activity.ZQBankToSecutityActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZQOnBankFragmentListener;
import com.pengbo.mhdzq.zq_trade_activity.ZQTradeMoreChooseBankActivity;
import com.pengbo.mhdzq.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ZQBankYHYEFragment extends Fragment implements OnClickListener{

	public LinearLayout mChooseBank;
	public View mView;
	public Activity mActivity;
	
	private LinearLayout mllayout_yhye_yhmm, mllayout_yhye_zjmm;
	private View mViewDiv1, mViewDiv2;
	private TextView mTV_yhye_yhmc;
	private EditText mEdit_yhye_yhmm, mEdit_yhye_zjmm;
	private Button mBtn_yhye_ok;
	private boolean mIsViewReady = false;
	
	private ZQOnBankFragmentListener mListener;
	private String mPasswordYH, mPasswordZJ;//银行密码，资金密码
	
	private int mSelectBankIndex = 0;
	private String mCurrentBankName = "";
	private ArrayList<String> mBankArrayName;
	private ArrayList< Map<String, String> > mBankArray;
	private int mPasswordFlag = 0;//0:不要求;1:要求（废弃） 4需要资金密码 5需要银行密码 6 资金密码银行密码均要 7银行密码 资金密码 均不要
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView=inflater.inflate(R.layout.zq_bank_rb3, null);
		initView();
		mIsViewReady = true;
		updateBankView();
		return mView ;
	}
	
	
	// init the view 
	private void initView() {
		mChooseBank=(LinearLayout)mView.findViewById(R.id.llayout_yhye_choosebank);
		mChooseBank.setOnClickListener(this);
		
		mllayout_yhye_yhmm = (LinearLayout) mView.findViewById(R.id.llayout_yhye_yhmm);
		mllayout_yhye_zjmm = (LinearLayout) mView.findViewById(R.id.llayout_yhye_zjmm);
		mViewDiv1 = (View) mView.findViewById(R.id.view_yhye_div1);
		mViewDiv2 = (View) mView.findViewById(R.id.view_yhye_div2);
		
		mTV_yhye_yhmc = (TextView) mView.findViewById(R.id.tv_yhye_yhmc);
		mTV_yhye_yhmc.setText("无");
		
		mEdit_yhye_yhmm = (EditText) mView.findViewById(R.id.edit_yhye_yhmm);
		mEdit_yhye_zjmm = (EditText) mView.findViewById(R.id.edit_yhye_zjmm);
		mBtn_yhye_ok = (Button) mView.findViewById(R.id.btn_yhye_ok);
		mBtn_yhye_ok.setOnClickListener(this);
		
		mPasswordYH = "";
		mPasswordZJ = "";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;	
		
		try {
			mListener = (ZQOnBankFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnBankToZQFragmentListener");
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llayout_yhye_choosebank:
		{
			if (mBankArrayName.size() <= 0)
			{
				return;
			}
			Intent intent = new Intent();
			intent.setClass(mActivity, ZQTradeMoreChooseBankActivity.class);

			intent.putExtra("mmlx", mBankArrayName);
			intent.putExtra("pwtype", mSelectBankIndex);

			getActivity().startActivityForResult(intent, ZQBankToSecutityActivity.REQUEST_CODE_BANK);
		}
			break;
		case R.id.btn_yhye_ok:
		{
			if (mSelectBankIndex >= mBankArrayName.size()) {
		        showDialog("银行信息错误!");
		        return;
		    }
			int ret = mPasswordFlag & 0xF;
			if (ret == 3)
			{
				showDialog("此银行不能查询余额!");
				return;
			}
			
			mPasswordYH = mEdit_yhye_yhmm.getText().toString();
			if (ret != 7 && mPasswordYH.isEmpty() && mllayout_yhye_yhmm.getVisibility() == View.VISIBLE)
			{
				showDialog("银行密码不能为空，请重新输入！");
				return;
			}
			mPasswordZJ = mEdit_yhye_zjmm.getText().toString();
			if (ret != 7 && mPasswordZJ.isEmpty() && mllayout_yhye_zjmm.getVisibility() == View.VISIBLE)
			{
				showDialog("资金密码不能为空，请重新输入！");
				return;
			}
			mListener.request_YHYE(mSelectBankIndex, mPasswordYH, mPasswordZJ);
			mEdit_yhye_yhmm.setText("");
			mEdit_yhye_zjmm.setText("");
			
		}
			break;
		default:
			break;
		}
	}
	
	public void updateCurrentBankIndex(int index)
	{
		if (index >=0 && index < mBankArrayName.size())
		{
			this.mSelectBankIndex = index;
		}else
		{
			return;
		}
		int nPwdFlag = 0x1111;
		if (mBankArrayName != null && mSelectBankIndex < mBankArrayName.size())
		{
			mCurrentBankName = mBankArrayName.get(mSelectBankIndex);
		    Map<String, String> temp = mBankArray.get(mSelectBankIndex);
		    nPwdFlag = STD.StringToInt(temp.get(ZQBankToSecutityActivity.KEY_PWDFLAG));
		    mPasswordFlag = nPwdFlag>>8;
		}
		
		updateBankView();
	}
	
	public void updateBankInfo(ArrayList<String> bankNameArray, ArrayList< Map<String, String> > bankArray )
	{
		mBankArrayName = bankNameArray;
		mBankArray = bankArray;
		int nPwdFlag = 0x1111;
		if (mBankArrayName != null && mSelectBankIndex < mBankArrayName.size())
		{
			mCurrentBankName = mBankArrayName.get(mSelectBankIndex);
		    Map<String, String> temp = mBankArray.get(mSelectBankIndex);
		    nPwdFlag = STD.StringToInt(temp.get(ZQBankToSecutityActivity.KEY_PWDFLAG));
		    mPasswordFlag = nPwdFlag>>8;
		}
		updateBankView();
	}
	
	public void updateBankView()
	{
		if (mIsViewReady)
		{
			if (mCurrentBankName.isEmpty())
			{
				mTV_yhye_yhmc.setText("无");
			}else
			{
				mTV_yhye_yhmc.setText(mCurrentBankName);
			}
			
			//0:不要求;1:要求（废弃） 4需要资金密码 5需要银行密码 6 资金密码银行密码均要
			//7银行密码 资金密码 均不要
			switch (mPasswordFlag & 0xF)
			{
			case 0:
			{
				mllayout_yhye_yhmm.setVisibility(View.GONE);
				mllayout_yhye_zjmm.setVisibility(View.GONE);
				mViewDiv1.setVisibility(View.GONE);
				mViewDiv2.setVisibility(View.GONE);
			}
				break;
			case 1:
			{
				mllayout_yhye_yhmm.setVisibility(View.GONE);
				mllayout_yhye_zjmm.setVisibility(View.VISIBLE);
				mViewDiv1.setVisibility(View.GONE);
				mViewDiv2.setVisibility(View.VISIBLE);
			}
				break;
			case 4:
			{
				mllayout_yhye_yhmm.setVisibility(View.GONE);
				mllayout_yhye_zjmm.setVisibility(View.VISIBLE);
				mViewDiv1.setVisibility(View.GONE);
				mViewDiv2.setVisibility(View.VISIBLE);
			}
				break;
			case 5:
			{
				mllayout_yhye_yhmm.setVisibility(View.VISIBLE);
				mllayout_yhye_zjmm.setVisibility(View.GONE);
				mViewDiv1.setVisibility(View.VISIBLE);
				mViewDiv2.setVisibility(View.GONE);
			}
				break;
			case 6:
			{
				mllayout_yhye_yhmm.setVisibility(View.VISIBLE);
				mllayout_yhye_zjmm.setVisibility(View.VISIBLE);
				mViewDiv1.setVisibility(View.VISIBLE);
				mViewDiv2.setVisibility(View.VISIBLE);
			}
				break;
			case 7:
			{
				mllayout_yhye_yhmm.setVisibility(View.GONE);
				mllayout_yhye_zjmm.setVisibility(View.GONE);
				mViewDiv1.setVisibility(View.GONE);
				mViewDiv2.setVisibility(View.GONE);
			}
				break;
			}
		}
	}
	
	private void showDialog(String strmsg) {

		AlertDialog alertDialog = new AlertDialog.Builder(this.mActivity)
			.setTitle("提示")
			.setMessage(strmsg)
			.setCancelable(true)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.create();
		alertDialog.show();
	}
	
}
