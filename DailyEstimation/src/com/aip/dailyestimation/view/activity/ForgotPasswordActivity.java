package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.common.async.ForgotPasswordAsync;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragmentActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends CoreFragmentActivity {

	private Activity activity;
	@InjectView(R.id.editForgotEmail)
	EditText editEmail;
	@InjectView(R.id.txtSendMail)
	TextView txtSubmit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;

		txtSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onButtonClick();
			}
		});

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onButtonClick() {
		try {
			String email = Validator.getEmailAddressValid(activity,
					editEmail.getText());
			/*
			 * L.alert(activity, "Your password sent to "+email+"", new L.IL() {
			 * 
			 * @Override public void onSuccess() { activity.finish(); }
			 * 
			 * @Override public void onCancel() { editEmail.setText(""); } });
			 */

			if (Util.isNetAvailable(activity)) {
				doOnline(email);
			} else {
				L.alert(activity,
						getResources().getString(R.string.msg_internet_error));
			}

		} catch (IllegalAccessException e) {
			L.alert(activity, e.getMessage());
		}

	}

	public void doOnline(String usernaemOrEmail) {

		if (!Util.isNetAvailable(activity)) {
			L.alert(activity, getString(R.string.msg_internet_error));
			return;
		}

		ForgotPasswordAsync async = new ForgotPasswordAsync(activity,
				new IResultListner() {

					@Override
					public void result(Object result, boolean isSuccess) {
						JSONObject jsonObject = (JSONObject) result;

						try {
							if (jsonObject.getString("code").equals("200")) {

								L.alert(activity, Util.getResponseMessage(
										activity, jsonObject), new L.IL() {

									@Override
									public void onSuccess() {
										activity.finish();
									}

									@Override
									public void onCancel() {
										activity.finish();
									}
								});
							} else {
								L.alert(activity,
										jsonObject.getString("message"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							L.alert(activity, e.getMessage());
						}
					}
				});

		async.execute(usernaemOrEmail);
	}
}
