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

/**
 * 证券  -----------更多中 银行转证    的证券转银行 
 * 
 * @author pobo
 * @date   2015-11-27 下午2:30:34
 * @className ZQZQToBankFragment.java
 * @verson 1.0.0
 */
public class ZQZQToBankFragment extends Fragment implements OnClickListener{


	public LinearLayout mChooseBank;
	public View mView;
	public Activity mActivity;
	
	private LinearLayout mllayout_zqtobank_yhmm, mllayout_zqtobank_zjmm;
	private View mViewDiv3, mViewDiv4;
	private TextView mTV_zqtobank_yhmc, mTV_zqtobank_kzje;
	private EditText mEdit_zqtobank_zzje, mEdit_zqtobank_yhmm, mEdit_zqtobank_zjmm;
	private Button mBtn_zqtobank_ok;
	private boolean mIsViewReady = false;
	
	private ZQOnBankFragmentListener mListener;
	private String mPasswordYH, mPasswordZJ, mAccountZZ;//银行密码，资金密码，转账金额
	private String mStrKYZJ = "";
	private int mSelectBankIndex = 0;
	private String mCurrentBankName = "";
	private ArrayList<String> mBankArrayName;
	private ArrayList< Map<String, String> > mBankArray;
	private int mPasswordFlag = 0;//0:不要求;1:要求（废弃） 4需要资金密码 5需要银行密码 6 资金密码银行密码均要 7银行密码 资金密码 均不要
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView=inflater.inflate(R.layout.zq_bank_rb2, null);
		initView();
		mIsViewReady = true;
		updateBankView();
		return mView ;
	}
	
	

	// init the view 
	private void initView() {
		mChooseBank=(LinearLayout) mView.findViewById(R.id.llayout_zqtobank_choosebank);
		mChooseBank.setOnClickListener(this);
		
		mllayout_zqtobank_yhmm = (LinearLayout) mView.findViewById(R.id.llayout_zqtobank_yhmm);
		mllayout_zqtobank_zjmm = (LinearLayout) mView.findViewById(R.id.llayout_zqtobank_zjmm);
		mViewDiv3 = (View) mView.findViewById(R.id.view_zqtobank_div3);
		mViewDiv4 = (View) mView.findViewById(R.id.view_zqtobank_div4);
		
		mTV_zqtobank_yhmc = (TextView) mView.findViewById(R.id.tv_zqtobank_yhmc);
		mTV_zqtobank_yhmc.setText("无");
		mTV_zqtobank_kzje = (TextView) mView.findViewById(R.id.tv_zqtobank_kzje);
		mTV_zqtobank_kzje.setText(mStrKYZJ);
		
		mEdit_zqtobank_zzje = (EditText) mView.findViewById(R.id.edit_zqtobank_zzje);
		mEdit_zqtobank_yhmm = (EditText) mView.findViewById(R.id.edit_zqtobank_yhmm);
		mEdit_zqtobank_zjmm = (EditText) mView.findViewById(R.id.edit_zqtobank_zjmm);
		mBtn_zqtobank_ok = (Button) mView.findViewById(R.id.btn_zqtobank_ok);
		mBtn_zqtobank_ok.setOnClickListener(this);
		
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
		case R.id.llayout_zqtobank_choosebank:
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
			
		case R.id.btn_zqtobank_ok:
		{
			if (mSelectBankIndex >= mBankArrayName.size()) {
		        showDialog("银行信息错误!");
		        return;
		    }
			
			int ret = mPasswordFlag & 0xF;
			
			mAccountZZ = mEdit_zqtobank_zzje.getText().toString();
			if (mAccountZZ.isEmpty())
			{
				showDialog("转账金额不能为空，请重新输入！");
				return;
			}else if (STD.StringToValue(mAccountZZ) > STD.StringToValue(mStrKYZJ))
			{
				showDialog("转账金额超过可用资金，请重新输入！");
				return;
			}
			mPasswordYH = mEdit_zqtobank_yhmm.getText().toString();
			if (ret != 7 && mPasswordYH.isEmpty() && mllayout_zqtobank_yhmm.getVisibility() == View.VISIBLE)
			{
				showDialog("银行密码不能为空，请重新输入！");
				return;
			}
			mPasswordZJ = mEdit_zqtobank_zjmm.getText().toString();
			if (ret != 7 && mPasswordZJ.isEmpty() && mllayout_zqtobank_zjmm.getVisibility() == View.VISIBLE)
			{
				showDialog("资金密码不能为空，请重新输入！");
				return;
			}
			mListener.requestYZZZ_QZY(mSelectBankIndex, mAccountZZ, mPasswordYH, mPasswordZJ);
			mEdit_zqtobank_zzje.setText("");
			mEdit_zqtobank_yhmm.setText("");
			mEdit_zqtobank_zjmm.setText("");
		}
			break;
		default:
			break;
		}
	}
	
	public void updateZJInfo(String kyzj)
	{
		mStrKYZJ = kyzj;
		if (mIsViewReady)
		{
			mTV_zqtobank_kzje.setText(mStrKYZJ);
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
		    mPasswordFlag = nPwdFlag>>4;
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
		    mPasswordFlag = nPwdFlag>>4;
		}
		updateBankView();
	}
	
	public void updateBankView()
	{
		if (mIsViewReady)
		{
			if (mCurrentBankName.isEmpty())
			{
				mTV_zqtobank_yhmc.setText("无");
			}else
			{
				mTV_zqtobank_yhmc.setText(mCurrentBankName);
			}
			
			//0:不要求;1:要求（废弃） 4需要资金密码 5需要银行密码 6 资金密码银行密码均要
			//7银行密码 资金密码 均不要
			switch (mPasswordFlag & 0xF)
			{
			case 0:
			{
				mllayout_zqtobank_yhmm.setVisibility(View.GONE);
				mllayout_zqtobank_zjmm.setVisibility(View.GONE);
				mViewDiv3.setVisibility(View.GONE);
				mViewDiv4.setVisibility(View.GONE);
			}
				break;
			case 1:
			{
				mllayout_zqtobank_yhmm.setVisibility(View.GONE);
				mllayout_zqtobank_zjmm.setVisibility(View.VISIBLE);
				mViewDiv3.setVisibility(View.GONE);
				mViewDiv4.setVisibility(View.VISIBLE);
			}
				break;
			case 4:
			{
				mllayout_zqtobank_yhmm.setVisibility(View.GONE);
				mllayout_zqtobank_zjmm.setVisibility(View.VISIBLE);
				mViewDiv3.setVisibility(View.GONE);
				mViewDiv4.setVisibility(View.VISIBLE);
			}
				break;
			case 5:
			{
				mllayout_zqtobank_yhmm.setVisibility(View.VISIBLE);
				mllayout_zqtobank_zjmm.setVisibility(View.GONE);
				mViewDiv3.setVisibility(View.VISIBLE);
				mViewDiv4.setVisibility(View.GONE);
			}
				break;
			case 6:
			{
				mllayout_zqtobank_yhmm.setVisibility(View.VISIBLE);
				mllayout_zqtobank_zjmm.setVisibility(View.VISIBLE);
				mViewDiv3.setVisibility(View.VISIBLE);
				mViewDiv4.setVisibility(View.VISIBLE);
			}
				break;
			case 7:
			{
				mllayout_zqtobank_yhmm.setVisibility(View.GONE);
				mllayout_zqtobank_zjmm.setVisibility(View.GONE);
				mViewDiv3.setVisibility(View.GONE);
				mViewDiv4.setVisibility(View.GONE);
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
