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
import com.aip.dailyestimation.common.async.AddUpdateCategoryAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.L.IL;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AddCategoryFragment extends CoreFragment {

	View rootView;

	TextView txtAddCategory;
	EditText editCategoryName;

	DatabaseService mDatabaseService;
	CategoryBean mCategoryBean;

	Boolean flag;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_add_category,
					container, false);
			init();
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		if (isEditMode())
			((MainActivity) getActivity())
					.setHeaderTitle(R.id.edit_category_fragment);
		else
			((MainActivity) getActivity())
					.setHeaderTitle(R.id.add_category_fragment);

		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

		if (mDatabaseService.getAccount().getUserType().equals("free")) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			mAdView.setVisibility(View.VISIBLE);
			mAdView.bringToFront();
		} else {
			mAdView.setVisibility(View.INVISIBLE);
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		super.onActivityCreated(savedInstanceState);
	}

	public void init() {

		txtAddCategory = (TextView) rootView.findViewById(R.id.txtAddCategory);
		editCategoryName = (EditText) rootView
				.findViewById(R.id.editCategoryName);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		if (isEditMode()) {

			// txtAddCategory.setText(getResources().getString(R.string.update));
			mCategoryBean = mDatabaseService.getCategory(getCategoryId());
			if (mCategoryBean != null) {
				editCategoryName.setText(mCategoryBean.getCategoryName());
				editCategoryName.setSelection(mCategoryBean.getCategoryName()
						.length() - 1);

			} else {
				L.alert(getActivity(), "No category found", new IL() {

					@Override
					public void onSuccess() {
						getActivity().onBackPressed();
					}

					@Override
					public void onCancel() {
						getActivity().onBackPressed();
					}
				});
			}
		} else {
			mCategoryBean = new CategoryBean();
			// txtAddCategory.setText(getResources().getString(R.string.add_category));
		}

		txtAddCategory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Util.isNetAvailable(getActivity())) {
					try {
						addCategory();
					} catch (IllegalAccessException e) {
						L.alert(getActivity(), e.getMessage());
					}

					Log.d("CurrentUserId",
							"" + mDatabaseService.getCurrentUserId());

				} else {
					L.alert(getActivity(),
							getResources().getString(
									R.string.msg_internet_error));
				}
			}
		});
	}

	private boolean isEditMode() {
		if (getArguments() != null
				&& getArguments().containsKey(IConstants.ARG_ID)) {
			return true;
		}
		return false;
	}

	private int getCategoryId() {
		if (getArguments() != null
				&& getArguments().containsKey(IConstants.ARG_ID)) {
			return getArguments().getInt(IConstants.ARG_ID);
		}
		return -1;
	}

	private void addCategory() throws IllegalAccessException {
		String category = editCategoryName.getText().toString().trim();
		if (!Validator.isValidateName(category)) {
			throw new IllegalAccessException(getActivity().getResources()
					.getString(R.string.invalid_category_name));
		}

		flag = true;
		if (!isEditMode()) {
			mCategoryBean.setCategoryName(category);
			mCategoryBean.setCreatedAt(new Date());
			mCategoryBean.setUpdatedAt(new Date());
			// mDatabaseService.insertUpdateCategory(mCategoryBean, false);
			flag = false;
		} else {
			mCategoryBean.setCategoryName(category);
			mCategoryBean.setUpdatedAt(new Date());
			// mDatabaseService.insertUpdateCategory(mCategoryBean, true);
			flag = true;
		}

		AddUpdateCategoryAsync addCategoryAsync = new AddUpdateCategoryAsync(
				getActivity(), mCategoryBean, flag, new IResultListner() {

					@Override
					public void result(Object result, boolean isSuccess) {

						JSONObject object = (JSONObject) result;

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
								try {

									mCategoryBean.setCategoryServerId(Integer
											.parseInt(object.getJSONObject(
													"data").getString("id")));

									if (flag) {
										mDatabaseService.insertUpdateCategory(
												mCategoryBean, true);
									} else {
										mDatabaseService.insertUpdateCategory(
												mCategoryBean, false);
									}

									String message = getResources().getString(
											R.string.category_added_success);
									if (isEditMode())
										message = getResources()
												.getString(
														R.string.category_updated_success);
									L.alert(getActivity(), message, new L.IL() {

										@Override
										public void onSuccess() {
											getActivity().onBackPressed();
										}

										@Override
										public void onCancel() {
											getActivity().onBackPressed();
											editCategoryName.setText("");
										}
									});

								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {
								String message = object.getString("message");
								L.alert(getActivity(), message);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Log.d("CategoryBean Result", "" + result.toString());
					}
				});
		addCategoryAsync.execute();

	}
}