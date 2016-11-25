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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.async.UpdateProfileAsync;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.ServiceHandler;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class UserProfileFragment extends CoreFragment {

	View rootView;

	DatabaseService mDatabaseService;

	EditText editEmail, editFirstName, editLastName, editPhone, editCompName;

	TextView txtUpdate;
	TextView txtUserType;
	TextView txtCloudOCR;
	Switch OCRSwitch;

	MainActivity activity;

	AccountBean accountBean;

	// final String PRODUCT_ID = "com.aip.receipttracker.100";
	// final String PRODUCT_ID = "android.test.purchased";
	// final String LICENSE_KEY =
	// "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhtpEwfetLawqqAFU46RlE53c68WU235+kWvAMeFAuWXw37lLaePG+wXk+yLTU3B0buntN2pYKBL64Jdsp39T35CaUL7sqI5P3PNzwR6wji9ghEtgUXCWtlmUBbwWKNU+7MMv+sLPzkiDCD1ypbX9Grjw8kxGYr5noS9BXoDNKgHL4BU1t22hA4dbT1tdFJIX4qWzKzxWrB2IMAc+zSH4Or+JiyGepTt0Gw3TxWOv7dCG527cimEvwm130b7ocqMdDRAnTTcXHB35yISrcbh2uzHbcPNbZQGQKhTn61zoEO3LlIPZUNYeL5NMQHLROZ0LtfVOHTkZEvW4EpJiBcPZtQIDAQAB";
	// // PUT

	// private static final String TAG = "ReceiptTracker";
	// IabHelper mHelper;
	// static final String ITEM_SKU = "com.aip.receipt.100";

	// IInAppBillingService mService;
	// ServiceConnection mServiceConn;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_user_profile,
					container, false);
			mDatabaseService = DatabaseService.getInstance(getActivity());
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}

		final SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

		init();
		activity = (MainActivity) getActivity();
		activity.setHeaderTitle(R.id.edit_user_profile);

		txtUserType.setText(mDatabaseService.getAccount().getUserType());

		if (mDatabaseService.getAccount().getUserType().equals("free")) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			mAdView.setVisibility(View.VISIBLE);
			mAdView.bringToFront();
		} else {
			mAdView.setVisibility(View.INVISIBLE);
		}

		if(sharedPref.contains("OCRSwitch")){
			if(sharedPref.getBoolean("OCRSwitch", true)){				
				OCRSwitch.setChecked(true);
			}else{				
				OCRSwitch.setChecked(false);
			}
		}else{
			sharedPref.edit().putBoolean("OCRSwitch", false).commit();
			OCRSwitch.setChecked(false);
		}
		
		OCRSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						sharedPref.edit().putBoolean("OCRSwitch", true).commit();
					}else{
						sharedPref.edit().putBoolean("OCRSwitch", false).commit();
					}
			}
		});
		
		return rootView;
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		super.onActivityCreated(savedInstanceState);
	}

	private void init() {

		editEmail = (EditText) rootView.findViewById(R.id.editEmail);
		editFirstName = (EditText) rootView.findViewById(R.id.editFirstName);
		editLastName = (EditText) rootView.findViewById(R.id.editLastName);
		editPhone = (EditText) rootView.findViewById(R.id.editMobile);
		editCompName = (EditText) rootView.findViewById(R.id.editCompanyName);

		OCRSwitch = (Switch) rootView.findViewById(R.id.OCRSwitch);
		
		txtUpdate = (TextView) rootView.findViewById(R.id.txtUpdate);
		txtUserType = (TextView) rootView.findViewById(R.id.txtUserType);

		// txtBuy.setVisibility(View.GONE);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		accountBean = mDatabaseService.getAccount();

		if (accountBean != null) {
			editEmail.setText(accountBean.getEmail());
			editFirstName.setText(accountBean.getFirstName());
			editLastName.setText(accountBean.getLastName());
			editPhone.setText(accountBean.getPhoneno());
			editCompName.setText(accountBean.getCompanyName());

			txtUpdate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					update();
				}
			});

		}
	}

	public void update() {
		String compName, firstName, lastName, email, mobile;

		try {
			email = Validator.getEmailAddressValid(activity,
					editEmail.getText());
		} catch (IllegalAccessException e) {
			L.alert(activity, e.getMessage());
			return;
		}

		firstName = editFirstName.getText().toString();
		if (!Validator.isValidateName(firstName)) {
			L.alert(activity,
					getResources().getString(R.string.invalid_firstname));
			return;
		}

		lastName = editLastName.getText().toString();
		if (!Validator.isValidateName(lastName)) {
			L.alert(activity,
					getResources().getString(R.string.invalid_lastname));
			return;
		}

		compName = editCompName.getText().toString();
		if (!Validator.isValidateName(compName)) {
			L.alert(activity, getResources()
					.getString(R.string.invalid_company));
			return;
		}

		try {
			mobile = Validator.getPhoneNumberValid(activity,
					editPhone.getText());
		} catch (IllegalAccessException e) {
			L.alert(activity, e.getMessage());
			return;
		}

		accountBean.setCompanyName(compName);
		accountBean.setEmail(email);
		accountBean.setFirstName(firstName);
		accountBean.setLastName(lastName);
		accountBean.setPhoneno(mobile);
		accountBean.setUpdatedAt(new Date(Calendar.getInstance()
				.getTimeInMillis()));
		
		if (Util.isNetAvailable(activity)) {
			doOnline();
		} else {
			L.alert(activity,
					getResources().getString(R.string.msg_internet_error));
		}

	}

	public void doOnline() {

		UpdateProfileAsync async = new UpdateProfileAsync(activity,
				accountBean, new IResultListner() {

					@Override
					public void result(Object result, boolean isSuccess) {

						JSONObject jsonObject = (JSONObject) result;

						try {
							if (jsonObject.optString("code").equals("1000")) {
								L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						JSONObject dataJson = jsonObject.optJSONObject("data");
						int serverId = dataJson.optInt("id");
						Log.e("#Registration#", "New server id is =" + serverId
								+ ">>Result is >>" + jsonObject.toString());
						accountBean.setServerId(serverId);
						accountBean.setIsSync(1);

						localUpdate();

					}
				});

		async.execute();
	}

	private void localUpdate() {
		int i = mDatabaseService.insertUpdateContact(accountBean);

		if (i > 0) {
			ServiceHandler.startExportService(activity);
			L.alert(activity,
					getResources().getString(R.string.msg_update_profile),
					new L.IL() {

						@Override
						public void onSuccess() {
							activity.onBackPressed();
						}

						@Override
						public void onCancel() {
							activity.onBackPressed();
						}
					});

		} else
			L.alert(activity,
					getResources().getString(R.string.msg_registration_error));

	}

}