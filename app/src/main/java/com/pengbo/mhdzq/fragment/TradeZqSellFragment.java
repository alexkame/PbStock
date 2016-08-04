package com.pengbo.mhdzq.fragment;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;

import android.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

public class TradeZqSellFragment extends TradeZqBuyFragment {

	
	public static TradeZqSellFragment newInstance()
	{
		TradeZqSellFragment f = new TradeZqSellFragment();
	    return f;
	}

	protected void requestKMSL(String price, TagLocalStockData optionData) {
		// if (mListener != null) {
		// mListener.requestKMSL(price, optionData, getSJType(), false);
		// }
		updateKMSLView("0", false);
	}

	protected void requestWTClick(int btnIndex) {
		requestWTClick(btnIndex, "卖出");
	}

	protected void requestWT() {
		String marketCode = TradeData.GetTradeMarketFromHQMarket(
				mOptionData.HQData.market, mOptionData.group);
		String gdzh = mZqGudongText.getText().toString();
		String xwh = MyApp.getInstance().mTradeData
				.GetXWHFromMarket(mCurrentMaket);

		mOrderRecord.mMMLB = PTK_Define.PTK_D_Sell;

		mOrderRecord.mKPBZ = PTK_Define.PTK_OF_Open;
		mOrderRecord.mWTPrice = mWTPrice;
		mOrderRecord.mWTSL = mZQBuyAmount.getText().toString();
		mOrderRecord.mMarketCode = marketCode;
		mOrderRecord.mGDZH = gdzh;
		mOrderRecord.mXWH = xwh;
		mOrderRecord.mStockCode = mOptionData.HQData.code;
		mOrderRecord.mSJType = getSJType();

		if (mListener != null) {
			mListener.requestWT(mOrderRecord);
		}
		// setInitPriceAndVolume();// 下单后重置委托价格和数量

	}

	protected void initBuyBtnView() {
		if (mZqBuyBtn != null) {
			mZqBuyBtn.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.zq_xd_sell_btn_selector));
			mZqBuyBtn.setText("卖出");

			mZQBuyAmount.setHint("卖出数量");
		}
	}

	protected void updatePriceView() {
		if (mZQPrice.length() == 0) {
			if (mOptionData.HQData.buyPrice.length > 0) {
				String price = ViewTools.getStringByPrice(
						mOptionData.HQData.buyPrice[0], 0,
						mOptionData.PriceDecimal, mOptionData.PriceRate);
				mZQPrice.setText(price);
			}
		}
	}

	public void updateKMSLView(String kmsl, boolean init) {
		// Float km = Float.valueOf(kmsl);
		//
		// mKMSL = km.intValue();

		String keyong = "0";

		if (mListData_CC != null) {
			for (int i = 0; i < mListData_CC.GetRecNum(); i++) {
				mListData_CC.GotoRecNo(i);

				String market = mListData_CC
						.GetFieldValueString(STEP_Define.STEP_SCDM);

				String jycode = mListData_CC
						.GetFieldValueString(STEP_Define.STEP_HYDM);

				String hqCode = mMyApp.mTradeData
						.GetHQCodeFromTradeCode(jycode);

				int nMarket = TradeData.GetHQMarketFromTradeMarket(market);

				if (mOptionData != null) {
					if (nMarket == mOptionData.market
							&& hqCode.equals(mOptionData.code)) {
						keyong = mListData_CC
								.GetFieldValueString(STEP_Define.STEP_KYSL);
						Float km = Float.valueOf(keyong);
						mKMSL = km.intValue();
						break;
					}
				}
			}
		}

		String kmtmp = ViewTools.getStringByVolume(mKMSL, 0, 1, 6, true);

		if (init == true) {
			mZQKMAmount.setText("可卖" + "----" + "股");
			mKMSL = 0;
		} else {
			mZQKMAmount.setText("可卖" + String.valueOf(kmtmp) + "股");
		}
	}

}
