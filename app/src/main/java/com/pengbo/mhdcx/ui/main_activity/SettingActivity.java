package com.pengbo.mhdcx.ui.main_activity;

import java.util.StringTokenizer;

import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdcx.ui.activity.AdvancedModelBtnAry;
import com.pengbo.mhdcx.ui.activity.AllTradePrice;
import com.pengbo.mhdcx.ui.activity.DefaultAddNum;
import com.pengbo.mhdcx.ui.activity.FastAutoOrderCancelTime;
import com.pengbo.mhdcx.ui.activity.FastTradePrice;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.activity.SetModelActivity;
import com.pengbo.mhdcx.ui.activity.SetOnLineTimeActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.widget.AlertDialog;
import com.pengbo.mhdcx.widget.SwitchButton;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 交易设置页
 * 
 * @author pobo
 * 
 */
public class SettingActivity extends HdActivity {
	public final static int REQUEST_CODE_MODEL = 10001;
	public final static int REQUEST_CODE_ONLINETIME = 10002;
	/** 全部平仓默认委托价格 */
	public final static int REQUEST_CODE_AllTRADEDEFAULTPRICE = 10003;
	/** 快捷反手默认委托价格fastTradePrice */
	public final static int REQUEST_CODE_FASETRADEPRICE = 10004;
	/** fastAutoOrderCancelTime 快捷反手自动撤单时间 */
	public final static int REQUEST_CODE_FASTAUTOORDERCANCELTIME = 10005;
	/** defaultAddNum 交易数量默认加量 */
	public final static int REQUEST_CODE_DEFAULTADDNUM = 10006;
	/** advancedBtnAry高级交易按钮顺序 */
	public final static int REQUEST_CODE_ADVANCEDBTNARY = 10007;

	public final static String INTENT_QBPCTYPE = "key_qbpctype";
	public final static String INTENT_KJFSTYPE = "key_kjfstype";
	public final static String INTENT_KJFSCDTIME = "key_kjfscdtime";
	public final static String INTENT_JYBTNMODE = "key_jybtnmode";
	/** 中间的文本 */
	public TextView head_tv_option;
	/** 右边的搜索期权列表要隐藏 */
	public TextView head_btn_searchlist;

	public View mModel, mOnlineTime, mNotConform, mAllTradePrice,
			mFastTradePrice, mFastAutoCancelOrderTime, mAllmAddNum,
			mAdvancedBtnAry;
	private View mMRCDNum;

	public LayoutInflater inflater;
	/** 交易模式 */
	public TextView tv_model;
	/** 在线时长 */
	public TextView tv_onlinetime;
	/** 全部平仓默认价格 文本内容 */
	public TextView tv_allTradeDefaultPrice;
	public TextView tv_fastDefaultPrice, tv_fastAutoCancelTime, tv_tradeAddNum,
			tv_advancedBtnAry, tv_defaultChaiDanSL;
	/** 版本信息 */
	private TextView mTV_Version;

	public SwitchButton sb_default;

	public boolean isopen = false;
	private boolean mbWithoutConfirm = false;
	private int mTradeMode = Trade_Define.TRADE_MODE_PUTONG;
	private int mTradeOnLineTime;
	private int mTradeQBPCPriceType;
	private int mTradeKJFSPriceType;
	private int mTradeKJFSZDCDTime;
	private int mTradeOrderBtnMode;
	private int mTradeChaiDanSL;

	/** 当为高级模式时 显示 隐藏的 的一些 条件 设置项 最外层 是由linearlayout来包裹 */
	private LinearLayout mOutMiddleLayout;
	private LinearLayout mOutOtherLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mbWithoutConfirm = PreferenceEngine.getInstance()
				.getOrderWithoutConfirm();
		mTradeQBPCPriceType = PreferenceEngine.getInstance()
				.getTradeAllPingCangPrice();
		mTradeKJFSPriceType = PreferenceEngine.getInstance()
				.getTradeKJFSWTPrice();
		mTradeKJFSZDCDTime = PreferenceEngine.getInstance()
				.getTradeKJFSAutoCDTime();
		mTradeOrderBtnMode = PreferenceEngine.getInstance()
				.getTradeSeniorModeWTBtn();
		initView();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_MODEL:// 交易模式
			if (resultCode == SetModelActivity.RESULT_CODE_MODEL) {
			}
			break;
		case REQUEST_CODE_ONLINETIME:// 在线时间
			if (resultCode == SetOnLineTimeActivity.RESULT_CODE_ONLINETIME) {
				Bundle bundle = data.getExtras();
				String str_onlinetime = bundle.getString("back_onlinetime");
			}

			break;
		case REQUEST_CODE_AllTRADEDEFAULTPRICE:// 全部平仓默认委托价格
			if (resultCode == AllTradePrice.RESULT_CODE_AllTradePrice) {
				this.mTradeQBPCPriceType = data.getIntExtra(INTENT_QBPCTYPE,
						Trade_Define.WTPRICEMODE_DSJ);
			}
			break;
		case REQUEST_CODE_FASETRADEPRICE:// 快捷反手默认委托价格
			if (resultCode == FastTradePrice.RESULT_CODE_FastPrice) {
				this.mTradeKJFSPriceType = data.getIntExtra(INTENT_KJFSTYPE,
						Trade_Define.WTPRICEMODE_DSJ);
			}
			break;
		case REQUEST_CODE_FASTAUTOORDERCANCELTIME:// 快捷反手自动撤单时间
			if (resultCode == FastAutoOrderCancelTime.RESULT_CODE_FastAutoCancelOrderTime) {
				this.mTradeKJFSZDCDTime = data.getIntExtra(INTENT_KJFSCDTIME,
						10);
			}
			break;
		case REQUEST_CODE_DEFAULTADDNUM:// 交易数量默认加量
			if (resultCode == DefaultAddNum.RESULT_CODE_defaultAddNum) {

			}
			break;
		case REQUEST_CODE_ADVANCEDBTNARY:// 高级交易按钮顺序
			if (resultCode == AdvancedModelBtnAry.RESULT_CODE_ADVANCEDBTNary) {
				this.mTradeOrderBtnMode = data.getIntExtra(INTENT_JYBTNMODE, 0);
			}
			break;

		default:
			break;
		}
	}

	/**初始化控件 */
	private void initView() {
		head_btn_searchlist = (TextView) this
				.findViewById(R.id.header_right_search);
		head_tv_option = (TextView) this
				.findViewById(R.id.header_middle_textview);
		head_tv_option.setText("设置 ");
		head_btn_searchlist.setVisibility(View.GONE);
		// 中间默认下是隐藏的 linearlayout
		mOutMiddleLayout = (LinearLayout) this
				.findViewById(R.id.setting_activity_middle_out_layout);
		mOutOtherLayout = (LinearLayout) this
				.findViewById(R.id.setting_activity_middle_out_layout_other);

		mTV_Version = (TextView) this
				.findViewById(R.id.trade_activity_tv_version);
		mTV_Version.setText(AppConstants.APP_VERSION_INFO);
	}

	@Override
	protected void onResume() {

		/**** 交易模式的切换 ***/
		initView_mModel();

		/**** 在线时间 ****/
		initOnlineTime();

		/** 下单无需 确认 **/

		initOrderNoConform();
		/** 全部平仓默认委托价格 **/
		initAllWeiTuoprice();

		/*** 快捷反手默认委托价格 ***/
		initFastDefaultPrice();

		/** 快捷反手自动撤单时间 ****/
		initFastAutoCancelOrderTime();
		/****** 交易数量默认加量 *********/
		initTradeNumDefaultAdd();

		/****** 高级交易按钮 顺序 ******/
		initAdvancedTradeBtnArray();

		initMRCDNum();

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	/**
	 * 下单无需 确认
	 */
	private void initOrderNoConform() {

		mNotConform = this.findViewById(R.id.setting_activity_relativelayout4);

		sb_default = (SwitchButton) this.findViewById(R.id.sb_default);
		sb_default.setChecked(mbWithoutConfirm);

		sb_default.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					new AlertDialog(SettingActivity.this).builder()
							.setTitle("警告")

							.setMsg("打开此功能后，下单时将没有确认提示，请谨慎使用")
							.setCancelable(false)
							.setCanceledOnTouchOutside(false)
							.setPositiveButton("确认", new OnClickListener() {

								@Override
								public void onClick(View v) {
									mbWithoutConfirm = true;
									PreferenceEngine.getInstance()
											.saveOrderWithoutConfirm(true);
								}
							}).setNegativeButton("取消", new OnClickListener() {

								@Override
								public void onClick(View v) {
									sb_default.setChecked(false);
								}
							}).show();
				} else {
					mbWithoutConfirm = false;
					PreferenceEngine.getInstance().saveOrderWithoutConfirm(
							false);
				}
			}
		});
	}

	/**
	 * 交易模式 普通 高级 item 及里面的 控件
	 */
	private void initView_mModel() {
		/*** 交易模式 普通 高级 item 及里面的 控件 *****/
		mModel = this.findViewById(R.id.setting_activity_linearlayout2);
		tv_model = (TextView) this.findViewById(R.id.trade_activity_tv_model);
		mModel.setVisibility(View.GONE);
		mTradeMode = PreferenceEngine.getInstance().getTradeMode();
		if (mTradeMode == Trade_Define.TRADE_MODE_PUTONG) {
			tv_model.setText("普通交易模式");
			mOutMiddleLayout.setVisibility(View.GONE);
			mOutOtherLayout.setVisibility(View.GONE);
		} else if (mTradeMode == Trade_Define.TRADE_MODE_GAOJI) {
			tv_model.setText("高级交易模式");
			mOutMiddleLayout.setVisibility(View.VISIBLE);
			mOutOtherLayout.setVisibility(View.VISIBLE);
		}
		mModel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this,
						com.pengbo.mhdcx.ui.activity.SetModelActivity.class);

				intent.putExtra("TradeMode", mTradeMode);
				// if (tv_model.getText().equals("普通交易模式")) {
				// intent.putExtra("TagChooseOrNot", "general_tag");
				// } else if (tv_model.getText().equals("高级交易模式")) {
				// intent.putExtra("TagChooseOrNot", "advanced_tag");
				// }
				startActivityForResult(intent, REQUEST_CODE_MODEL);
			}
		});
	}

	/**
	 * 在线时间 item 及里面的控件
	 */
	private void initOnlineTime() {
		int online = PreferenceEngine.getInstance().getTradeOnlineTime();
		String tv_time = null;
		if (online == 5) {
			tv_time = "5分钟";
		} else if (online == 10) {
			tv_time = "10分钟";
		} else if (online == 15) {
			tv_time = "15分钟";
		} else if (online == 30) {
			tv_time = "30分钟";
		} else if (online == 60) {
			tv_time = "60分钟";
		} else if (online == 120) {
			tv_time = "120分钟";
		} else if (online == 180) {
			tv_time = "180分钟";
		} else {
			tv_time = "15分钟";
		}
		mOnlineTime = this.findViewById(R.id.setting_activity_linearlayout3);
		tv_onlinetime = (TextView) this
				.findViewById(R.id.trade_activity_tv_online_time);
		tv_onlinetime.setText(tv_time);
		mOnlineTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(
						SettingActivity.this,
						com.pengbo.mhdcx.ui.activity.SetOnLineTimeActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ONLINETIME);
			}
		});
	}

	/**
	 * 全部平仓委托价格
	 */

	private void initAllWeiTuoprice() {
		String tv_allPrice = null;
		switch (mTradeQBPCPriceType) {
		case Trade_Define.WTPRICEMODE_DSJ:
			tv_allPrice = "对手价";
			break;
		case Trade_Define.WTPRICEMODE_NOW:
			tv_allPrice = "最新价";
			break;
		case Trade_Define.WTPRICEMODE_GDJ:
			tv_allPrice = "挂单价";
			break;
		case Trade_Define.WTPRICEMODE_UP:
			tv_allPrice = "涨停价";
			break;
		case Trade_Define.WTPRICEMODE_DOWN:
			tv_allPrice = "跌停价";
			break;
		case Trade_Define.WTPRICEMODE_QBCJHCX:
		case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:
			tv_allPrice = "全额成交或撤销";
			break;
		default:
			break;
		}

		mAllTradePrice = this.findViewById(R.id.setting_activity_alltradeprice);

		tv_allTradeDefaultPrice = (TextView) this
				.findViewById(R.id.trade_activity_tv_alltradedefaultprice);

		tv_allTradeDefaultPrice.setText(tv_allPrice);
		mAllTradePrice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(INTENT_QBPCTYPE, mTradeQBPCPriceType);
				intent.setClass(SettingActivity.this,
						com.pengbo.mhdcx.ui.activity.AllTradePrice.class);
				startActivityForResult(intent,
						REQUEST_CODE_AllTRADEDEFAULTPRICE);
			}
		});
	}

	/**
	 * 快捷反手默认委托价格
	 */
	private void initFastDefaultPrice() {

		String tv_fastPrice = null;
		switch (mTradeKJFSPriceType) {
		case Trade_Define.WTPRICEMODE_DSJ:
			tv_fastPrice = "对手价";
			break;
		case Trade_Define.WTPRICEMODE_NOW:
			tv_fastPrice = "最新价";
			break;
		case Trade_Define.WTPRICEMODE_GDJ:
			tv_fastPrice = "挂单价";
			break;
		case Trade_Define.WTPRICEMODE_UP:
			tv_fastPrice = "涨停价";
			break;
		case Trade_Define.WTPRICEMODE_DOWN:
			tv_fastPrice = "跌停价";
			break;
		case Trade_Define.WTPRICEMODE_QBCJHCX:
		case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:
			tv_fastPrice = "全额成交或撤销";
			break;
		default:
			break;
		}
		mFastTradePrice = this
				.findViewById(R.id.setting_activity_fastdefaultprice);

		tv_fastDefaultPrice = (TextView) this
				.findViewById(R.id.setting_activity_tv_fastdefaultprice);

		tv_fastDefaultPrice.setText(tv_fastPrice);
		mFastTradePrice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra(INTENT_KJFSTYPE, mTradeKJFSPriceType);
				intent.setClass(SettingActivity.this,
						com.pengbo.mhdcx.ui.activity.FastTradePrice.class);
				startActivityForResult(intent, REQUEST_CODE_FASETRADEPRICE);
			}
		});
	}

	/**
	 * 快捷反手自动撤单时间
	 */
	private void initFastAutoCancelOrderTime() {

		String tv_fastautoPrice = null;
		if (mTradeKJFSZDCDTime == 10) {
			tv_fastautoPrice = "10秒";
		} else if (mTradeKJFSZDCDTime == 20) {
			tv_fastautoPrice = "20秒";
		} else if (mTradeKJFSZDCDTime == 30) {
			tv_fastautoPrice = "30秒";
		} else {
			tv_fastautoPrice = "10秒";
		}

		mFastAutoCancelOrderTime = this
				.findViewById(R.id.setting_activity_fastautoprice);

		tv_fastAutoCancelTime = (TextView) this
				.findViewById(R.id.setting_activity_tv_fastautoprice);

		tv_fastAutoCancelTime.setText(tv_fastautoPrice);
		mFastAutoCancelOrderTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(INTENT_KJFSCDTIME, mTradeKJFSZDCDTime);
				intent.setClass(
						SettingActivity.this,
						com.pengbo.mhdcx.ui.activity.FastAutoOrderCancelTime.class);
				startActivityForResult(intent,
						REQUEST_CODE_FASTAUTOORDERCANCELTIME);
			}
		});
	}

	/**
	 * 交易数量默认加量
	 */
	private void initTradeNumDefaultAdd() {

		int addNum = PreferenceEngine.getInstance().getTradeOrderIncreaseNum();

		mAllmAddNum = this.findViewById(R.id.setting_activity_default_add_num);

		tv_tradeAddNum = (TextView) this
				.findViewById(R.id.trade_activity_tv_default_add_num);

		tv_tradeAddNum.setText(addNum + "");

		mAllmAddNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog dialog = new AlertDialog(SettingActivity.this);
				dialog.builder();
				dialog.setTitle("提示");
				dialog.setMsg("请输入默认交易加量数");
				dialog.setEdit2();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setPositiveButton("确认", new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String num = dialog.getEditText2();
						int mNum = STD.StringToInt(num);
						// Toast.makeText(SettingActivity.this, mNum+"",
						// Toast.LENGTH_SHORT).show();
						if (mNum > 0) {
							// save the num
							PreferenceEngine.getInstance()
									.saveTradeOrderIncreaseNum(mNum);
							tv_tradeAddNum.setText(mNum + "");
						} else {
							Toast.makeText(SettingActivity.this, "请输入大于0的数字",
									Toast.LENGTH_LONG).show();
						}

					}
				});
				dialog.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				dialog.show();
			}
		});

	}

	/**
	 * 高级交易按钮 顺序 *
	 */
	private void initAdvancedTradeBtnArray() {

		String tv_tradeNumAdd = null;

		if (mTradeOrderBtnMode == 1) {
			tv_tradeNumAdd = "买开  卖开  卖平   买平";
		} else {
			tv_tradeNumAdd = "买开  卖平  卖开  买平  ";
		}

		mAdvancedBtnAry = this
				.findViewById(R.id.setting_activity_advancedtradebtnarray);

		tv_advancedBtnAry = (TextView) this
				.findViewById(R.id.setting_activity_tv_advancedtradebtnarray);

		tv_advancedBtnAry.setText(tv_tradeNumAdd);
		mAdvancedBtnAry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(INTENT_JYBTNMODE, mTradeOrderBtnMode);
				intent.setClass(SettingActivity.this,
						com.pengbo.mhdcx.ui.activity.AdvancedModelBtnAry.class);
				startActivityForResult(intent, REQUEST_CODE_ADVANCEDBTNARY);
			}
		});
	}

	/**
	 * 默认拆单数量
	 */
	private void initMRCDNum() {

		int cdNum = PreferenceEngine.getInstance().getTradeMRCDNum();

		mMRCDNum = this.findViewById(R.id.setting_activity_chaidan);

		tv_defaultChaiDanSL = (TextView) this
				.findViewById(R.id.trade_activity_tv_chaidan);

		tv_defaultChaiDanSL.setText(cdNum + "");

		mMRCDNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog dialog = new AlertDialog(SettingActivity.this);
				dialog.builder();
				dialog.setTitle("提示");
				dialog.setMsg("请输入默认拆单手数");
				dialog.setEdit2();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setPositiveButton("确认", new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String num = dialog.getEditText2();
						int nCount = STD.StringToInt(num);
						if (nCount >= 0) {
							// save the num
							PreferenceEngine.getInstance().saveTradeMRCDNum(
									nCount);
							tv_defaultChaiDanSL.setText(nCount + "");
						} else {
							Toast.makeText(SettingActivity.this, "请输入不小于0的数字",
									Toast.LENGTH_LONG).show();
						}
					}
				});
				dialog.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				dialog.show();
			}
		});
	}

}
