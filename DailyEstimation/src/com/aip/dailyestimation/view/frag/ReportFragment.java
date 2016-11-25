package com.aip.dailyestimation.view.frag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.CategoryBean;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.async.ReadAllCategoryAsync;
import com.aip.dailyestimation.common.async.ReadFilterReceiptAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.customdialog.CustomAlertDialog;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnCategorySelcted;
import com.aip.dailyestimation.customdialog.DatePickerUtil;
import com.aip.dailyestimation.customdialog.DatePickerUtil.IOnDateSetListner;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.aip.dailyestimation.vo.FilterReceipt;
import com.aip.dailyestimation.vo.FilterVO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportFragment extends CoreFragment implements OnClickListener {

	View mFilterContainer;

	MainActivity activity;

	DatabaseService mDatabaseService;

	FilterReceipt filterReceipt;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		filterReceipt = new FilterReceipt();

		if (mFilterContainer == null) {
			mFilterContainer = inflater.inflate(R.layout.option_filter,
					container, false);
			initFilter();
		} else {
			((ViewGroup) mFilterContainer.getParent())
					.removeView(mFilterContainer);
		}

		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		AdView mAdView = (AdView) mFilterContainer.findViewById(R.id.adView);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		mDatabaseService.deleteAllUserCategory();

		// All Receipt Remove From Database

		if (Util.isNetAvailable(getActivity())) {

			ReadAllCategoryAsync categoryAsync = new ReadAllCategoryAsync(
					getActivity(), new IResultListner() {

						@Override
						public void result(Object result, boolean isSuccess) {

							JSONObject object = (JSONObject) result;
							try {
								Log.d("ReadAllCategoryAsync",
										"" + object.toString(4));
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							try {

								try {
									if (object.optString("code").equals("1000")) {
										L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (object.getString("code").equals("200")) {

									JSONArray array = object
											.getJSONArray("data");

									mDatabaseService
											.deleteAllCategory(mDatabaseService
													.getCurrentUserId());

									for (int Counter = 0; Counter < array
											.length(); Counter++) {
										JSONObject obj = array
												.getJSONObject(Counter);

										CategoryBean categoryBean = new CategoryBean();

										categoryBean.setCategoryServerId(obj
												.getInt("id"));
										categoryBean.setServerId(obj
												.getInt("userId"));
										categoryBean.setCategoryName(obj
												.getString("categoryName"));

										Log.d("CategoryBean: "
												+ categoryBean
														.getCategoryName(),
												"ServerId: "
														+ categoryBean
																.getServerId());

										mDatabaseService.insertUpdateCategory(
												categoryBean, true);
									}

								} else {

									if (object.getString("code").equals("201")) {

										mDatabaseService
												.deleteAllCategory(mDatabaseService
														.getCurrentUserId());
									} else {
										L.alert(getActivity(),
												object.getString("message"));
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			categoryAsync.execute();

		}

		mDatabaseService.deleteAllUserReceipt();

		if (mDatabaseService.getAccount().getUserType().equals("free")) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			mAdView.setVisibility(View.VISIBLE);
			mAdView.bringToFront();
		} else {
			mAdView.setVisibility(View.INVISIBLE);
		}

		activity.setHeaderTitle(R.id.report_fragment);
		return mFilterContainer;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * This field is for filter
	 */
	TextView txtOptionFromDate;
	TextView txtOptionToDate;
	TextView txtOptionCategory;

	EditText editOptionAmount;
	EditText editMaxAmount;
	EditText editMinAmount;
	EditText editOptionComment;

	Calendar mCalendar, mFromCalendar, mToCalendar;

	FilterVO mFilterVO;

	private void initFilter() {

		mDatabaseService = (DatabaseService) DatabaseService
				.getInstance(activity.getApplicationContext());

		txtOptionFromDate = (TextView) mFilterContainer
				.findViewById(R.id.optionFromDate);
		txtOptionToDate = (TextView) mFilterContainer
				.findViewById(R.id.optionToDate);
		txtOptionCategory = (TextView) mFilterContainer
				.findViewById(R.id.optionCategory);

		editOptionAmount = (EditText) mFilterContainer
				.findViewById(R.id.optionAmount);
		editMaxAmount = (EditText) mFilterContainer
				.findViewById(R.id.optionMaxValue);
		editMinAmount = (EditText) mFilterContainer
				.findViewById(R.id.optionMinValue);
		editOptionComment = (EditText) mFilterContainer
				.findViewById(R.id.optionComment);

		(mFilterContainer.findViewById(R.id.optionSubmit))
				.setOnClickListener(this);
		(mFilterContainer.findViewById(R.id.optionClear))
				.setOnClickListener(this);
		txtOptionFromDate.setOnClickListener(this);
		txtOptionToDate.setOnClickListener(this);
		txtOptionCategory.setOnClickListener(this);

		mFilterVO = new FilterVO();
		clearFilter();
	}

	@Override
	public void onResume() {
		clearFilter();
		super.onResume();
	}

	private void clearFilter() {
		txtOptionFromDate.setText("");
		txtOptionToDate.setText("");
		txtOptionCategory.setText("");
		editOptionAmount.setText("");
		editMaxAmount.setText("");
		editMinAmount.setText("");
		editOptionComment.setText("");

		mFromCalendar = Calendar.getInstance();
		mFromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mFromCalendar.set(Calendar.MINUTE, 0);
		mFromCalendar.set(Calendar.SECOND, 0);
		mFromCalendar.set(Calendar.MILLISECOND, 0);

		mToCalendar = Calendar.getInstance();

		mToCalendar.set(Calendar.HOUR_OF_DAY, 23);
		mToCalendar.set(Calendar.MINUTE, 59);
		mToCalendar.set(Calendar.SECOND, 59);
		mToCalendar.set(Calendar.MILLISECOND, 999);

		mCalendar = Calendar.getInstance();
		mCalendar.set(Calendar.HOUR_OF_DAY, 23);
		mCalendar.set(Calendar.MINUTE, 59);
		mCalendar.set(Calendar.SECOND, 59);
		mCalendar.set(Calendar.MILLISECOND, 999);

		mFilterVO.clear();
		filterReceipt = new FilterReceipt();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.optionFromDate:
			pickFromDate();
			break;
		case R.id.optionToDate:
			pickToDate();
			break;
		case R.id.optionCategory:
			selectCategory();
			break;

		case R.id.optionSubmit:

			try {
				if (editOptionAmount.getText().toString().trim().length() > 0) {
					double amountInDouble = Validator.getValidAmount(activity,
							editOptionAmount.getText().toString().trim());
					mFilterVO.setAmount(amountInDouble);
				}

				if (editMaxAmount.getText().toString().trim().length() > 0) {
					double maxAmountInDouble = Validator
							.getValidAmount(activity, editMaxAmount.getText()
									.toString().trim());
					mFilterVO.setMaxAmount(maxAmountInDouble);
					filterReceipt.setMaxAmount(String
							.valueOf(maxAmountInDouble));
				}

				if (editMinAmount.getText().toString().trim().length() > 0) {
					double minAmountInDouble = Validator
							.getValidAmount(activity, editMinAmount.getText()
									.toString().trim());
					mFilterVO.setMinAmount(minAmountInDouble);
					filterReceipt.setMinAmount(String
							.valueOf(minAmountInDouble));
				}

			} catch (IllegalAccessException e) {
				L.alert(activity, e.getMessage());
				return;
			}
			mFilterVO.setComment(editOptionComment.getText().toString().trim());
			filterReceipt.setComment(editOptionComment.getText().toString()
					.trim());
			if (mFilterVO.isEmpty()) {
				L.alert(activity,
						"No filter applied. Please apply at least one filter.");
			} else {
				if (Util.isNetAvailable(getActivity())) {
					showReceipts(mFilterVO);
				} else {
					L.alert(getActivity(),
							getResources().getString(
									R.string.msg_internet_error));
				}
			}

			break;
		case R.id.optionClear:
			clearFilter();
			break;
		default:
			break;
		}
	}

	private void pickFromDate() {
		DatePickerUtil datePickerUtil = new DatePickerUtil(activity,
				mFromCalendar.get(Calendar.DAY_OF_MONTH),
				mFromCalendar.get(Calendar.MONTH),
				mFromCalendar.get(Calendar.YEAR), fromDateSetListner);
		if (mFilterVO.getToCalendar() != null) {
			datePickerUtil
					.setMaxDateLong(mToCalendar.getTimeInMillis() + 60000);
		} else
			datePickerUtil.setMaxDateLong(mCalendar.getTimeInMillis() + 60000);
		datePickerUtil.show();
	}

	IOnDateSetListner fromDateSetListner = new IOnDateSetListner() {

		@Override
		public void onDateSet(int yy, int mm, int dd) {
			mFromCalendar.set(Calendar.DAY_OF_MONTH, dd);
			mFromCalendar.set(Calendar.MONTH, mm);
			mFromCalendar.set(Calendar.YEAR, yy);

			if (mFromCalendar.after(mCalendar)) {
				L.alert(activity,
						getResources().getString(
								R.string.error_select_future_date));

				mFromCalendar = Calendar.getInstance();
			} else {

				setFromDate();
				/*
				 * if((Util.isSameDay(mFromCalendar, mToCalendar)) ||
				 * mFromCalendar.after(mToCalendar) ||
				 * txtOptionToDate.getText().toString().trim().length() < 1) {
				 * mToCalendar.setTimeInMillis(mFromCalendar.getTimeInMillis() +
				 * IConstants.MILLI_SECOND_OF_DAY); setToDate(); }
				 */
			}

		}
	};

	private void pickToDate() {
		DatePickerUtil datePickerUtil = new DatePickerUtil(activity,
				mToCalendar.get(Calendar.DAY_OF_MONTH),
				mToCalendar.get(Calendar.MONTH),
				mToCalendar.get(Calendar.YEAR), toDateSetListner);
		datePickerUtil.setMaxDateLong(mCalendar.getTimeInMillis() + 60000);
		if (mFilterVO.getFromCalendar() != null)
			datePickerUtil.setMinDateLong(mFromCalendar.getTimeInMillis());
		datePickerUtil.show();
	}

	IOnDateSetListner toDateSetListner = new IOnDateSetListner() {

		@Override
		public void onDateSet(int yy, int mm, int dd) {
			mToCalendar.set(Calendar.DAY_OF_MONTH, dd);
			mToCalendar.set(Calendar.MONTH, mm);
			mToCalendar.set(Calendar.YEAR, yy);

			if (mToCalendar.after(mCalendar)) {
				L.alert(activity,
						getResources().getString(
								R.string.error_select_future_date));

				mToCalendar = Calendar.getInstance();
			} else {

				setToDate();
				/*
				 * if(Util.isSameDay(mFromCalendar, mToCalendar) ||
				 * mFromCalendar.after(mToCalendar) ||
				 * txtOptionFromDate.getText().toString().trim().length() < 1) {
				 * mFromCalendar.setTimeInMillis(mToCalendar.getTimeInMillis() -
				 * IConstants.MILLI_SECOND_OF_DAY); //after one hour
				 * setFromDate(); }
				 */
			}

		}
	};

	private void setFromDate() {
		mFilterVO.setFromCalendar(mFromCalendar);
		txtOptionFromDate.setText(Util.getLongToDate(
				mFromCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));

		filterReceipt.setFromDate(String.valueOf(mFromCalendar
				.getTimeInMillis() / 1000));
		Log.d("setFromDate", "setFromDate: " + mFromCalendar.getTimeInMillis()
				/ 1000);

	}

	private void setToDate() {
		mFilterVO.setToCalendar(mToCalendar);
		txtOptionToDate.setText(Util.getLongToDate(
				mToCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));

		filterReceipt
				.setToDate(String.valueOf(mToCalendar.getTimeInMillis() / 1000));
		Log.d("setToDate", "setToDate: " + mToCalendar.getTimeInMillis() / 1000);

	}

	private void selectCategory() {

		List<String> categories = mDatabaseService.getAllLocalCategoriesName();
		if (categories == null || categories.size() == 0) {
			L.alert(activity,
					getResources().getString(R.string.no_category_found));
		} else {
			CustomAlertDialog.openCategoryOption(activity, categories,
					new IOnCategorySelcted() {

						@Override
						public void onCategorySelected(String cateogryName) {
							txtOptionCategory.setText(cateogryName);
							mFilterVO.setCategoryName(cateogryName);
							filterReceipt.setCategoryId(String.valueOf(mDatabaseService.getCategory(cateogryName)));
						}
					});
		}
	}

	private void showReceipts(FilterVO filterVO) {
		final FilterReceiptFragment filterreceiptFragment = new FilterReceiptFragment();
		if (filterVO != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(IConstants.ARG_FILTER, filterVO);
			filterreceiptFragment.setArguments(bundle);
		}

		ReadFilterReceiptAsync allReceiptAsync = new ReadFilterReceiptAsync(
				getActivity(), filterReceipt, new IResultListner() {

					@Override
					public void result(Object result, boolean isSuccess) {
						JSONObject object = (JSONObject) result;
						try {
							Log.d("ReadFilterReceiptAsync",
									"" + object.toString(4));

							try {
								if (object.optString("code").equals("1000")) {
									L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (object.getString("code").equals("200")) {

								JSONArray array = object.getJSONArray("data");

								mDatabaseService
										.deleteAllReceipt(mDatabaseService
												.getCurrentUserId());

								for (int Counter = 0; Counter < array.length(); Counter++) {

									JSONObject obj = array
											.getJSONObject(Counter);

									ReceiptBean receiptBean = new ReceiptBean();
									receiptBean.setServerId(obj.getInt("id"));

									try {
										receiptBean.setName(obj.getJSONObject(
												"addressBook").getString(
												"addressBookName"));
									} catch (Exception e) {
										receiptBean.setName(obj
												.getString("receiptName"));
									}

									// if (obj.getJSONObject("addressBook")
									// .length() > 0) {
									// receiptBean.setName(obj
									// .getJSONObject(
									// "addressBook")
									// .getString(
									// "addressBookName"));
									// } else {
									// receiptBean.setName(obj
									// .getString("receiptName"));
									// }

									// receiptBean.setCategoryName(obj
									// .getString("receiptCategory"));
									receiptBean.setCategoryName(obj
											.getJSONObject("category")
											.getString("categoryName"));
									receiptBean.setAmount(obj
											.getDouble("amount"));
									receiptBean.setTip(obj.getDouble("tip"));
									receiptBean.setComment(obj
											.getString("comment"));
									receiptBean.setDate(new Date(obj
											.getLong("date")));
									receiptBean.setCreatedAt(new Date(obj
											.getLong("createdDate")));
									receiptBean.setUpdateddAt(new Date(obj
											.getLong("modifiedDate")));
									receiptBean.setUserId(obj.getInt("userId"));
									receiptBean.setServerImgPath(obj.getString(
											"receiptImage").trim());
									// receiptBean.setReceiptID(obj
									// .getInt("localReceiptId"));
									receiptBean.setIsDelete(obj
											.getInt("isDelete"));
									receiptBean.setCategoryServerId(obj
											.getInt("categoryId"));
									receiptBean.setAddressServerId(obj
											.getInt("addressBookId"));

									// mDatabaseService
									// .insertUpdateReceipt(receiptBean);

									mDatabaseService.insertReceipt(receiptBean);

								}

								switchFragment(R.id.main_content,
										filterreceiptFragment);

							} else {

								if (object.getString("code").equals("201")) {

									mDatabaseService
											.deleteAllReceipt(mDatabaseService
													.getCurrentUserId());
									switchFragment(R.id.main_content,
											filterreceiptFragment);

								} else {
									L.alert(getActivity(),
											object.getString("message"));
								}

							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
		allReceiptAsync.execute();

	}

}