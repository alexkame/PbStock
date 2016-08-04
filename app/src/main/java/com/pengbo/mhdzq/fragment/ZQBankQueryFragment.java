package com.pengbo.mhdzq.fragment;
import com.pengbo.mhdcx.adapter.BankQueryListAdapter;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ZQBankQueryFragment extends Fragment {
	
	private View mView;
	private Activity mActivity;
	private BankQueryListAdapter mListAdapter;

	private PBSTEP mLSList;
	private ListView mListView;
	private boolean mIsViewReady = false;
	
	
	@Override
	public void onAttach(Activity activity) {
		this.mActivity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.zq_bank_rb4,container, false);
		
		
		if (mListView == null) {
			mListView = (ListView) mView.findViewById(R.id.bank_rb4_listview);
			if (mLSList == null)
			{
				mLSList = new PBSTEP();
			}
			mListAdapter = new BankQueryListAdapter(mActivity, mLSList);
			mListView.setAdapter(mListAdapter);
		}
		mIsViewReady = true;
		return mView;
	}
	
	public void updateBankLS(PBSTEP aStep)
	{
		if (mLSList == null)
		{
			mLSList = new PBSTEP();
		}
		this.mLSList.Copy(aStep);
		if (mListAdapter != null)
		{
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	public void updateView()
	{
		if (mIsViewReady)
		{
			mListAdapter.notifyDataSetChanged();
		}
	}
}
