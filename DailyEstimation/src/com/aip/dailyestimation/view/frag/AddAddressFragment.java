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
import com.aip.dailyestimation.bean.AddressBean;
import com.aip.dailyestimation.common.async.AddUpdateAddressAsync;
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

public class AddAddressFragment extends CoreFragment {

	View rootView;

	TextView txtAddAddress;
	EditText editAddressName;

	DatabaseService mDatabaseService;
	AddressBean mAddressBean;

	Boolean flag;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_add_address,
					container, false);
			init();
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		if (isEditMode())
			((MainActivity) getActivity())
					.setHeaderTitle(R.id.edit_address_fragment);
		else
			((MainActivity) getActivity())
					.setHeaderTitle(R.id.add_address_fragment);

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

		txtAddAddress = (TextView) rootView.findViewById(R.id.txtAddAddress);
		editAddressName = (EditText) rootView
				.findViewById(R.id.editAddressName);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		if (isEditMode()) {

			// txtAddCategory.setText(getResources().getString(R.string.update));
			mAddressBean = mDatabaseService.getAddress(getAddressId());
			if (mAddressBean != null) {
				editAddressName.setText(mAddressBean.getAddressName());
				editAddressName.setSelection(mAddressBean.getAddressName()
						.length() - 1);

			} else {
				L.alert(getActivity(), "No address found", new IL() {

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
			mAddressBean = new AddressBean();
			// txtAddCategory.setText(getResources().getString(R.string.add_category));
		}

		txtAddAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Util.isNetAvailable(getActivity())) {

					try {
						addAddress();
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

	private int getAddressId() {
		if (getArguments() != null
				&& getArguments().containsKey(IConstants.ARG_ID)) {
			return getArguments().getInt(IConstants.ARG_ID);
		}
		return -1;
	}

	private void addAddress() throws IllegalAccessException {
		String address = editAddressName.getText().toString().trim();
		if (!Validator.isValidateName(address)) {
			throw new IllegalAccessException(getActivity().getResources()
					.getString(R.string.invalid_address_name));
		}

		flag = true;
		if (!isEditMode()) {
			mAddressBean.setAddressName(address);
			mAddressBean.setCreatedAt(new Date());
			mAddressBean.setUpdatedAt(new Date());
			// mDatabaseService.insertUpdateAddress(mAddressBean, false);
			flag = false;
		} else {
			mAddressBean.setAddressName(address);
			mAddressBean.setUpdatedAt(new Date());
			// mDatabaseService.insertUpdateAddress(mAddressBean, true);
			flag = true;
		}

		AddUpdateAddressAsync addUpdateAddressAsync = new AddUpdateAddressAsync(
				getActivity(), mAddressBean, flag, new IResultListner() {

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

									mAddressBean.setAddressServerId(Integer
											.parseInt(object.getJSONObject(
													"data").getString("id")));

									if (flag) {
										mDatabaseService.insertUpdateCategory(
												mAddressBean, true);
									} else {
										mDatabaseService.insertUpdateCategory(
												mAddressBean, false);
									}

									String message = getResources().getString(
											R.string.address_added_success);
									if (isEditMode())
										message = getResources()
												.getString(
														R.string.address_updated_success);

									L.alert(getActivity(), message, new L.IL() {

										@Override
										public void onSuccess() {
											getActivity().onBackPressed();
										}

										@Override
										public void onCancel() {
											getActivity().onBackPressed();
											editAddressName.setText("");
										}
									});

								} catch (IllegalAccessException e) {
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
		addUpdateAddressAsync.execute();
	}
}