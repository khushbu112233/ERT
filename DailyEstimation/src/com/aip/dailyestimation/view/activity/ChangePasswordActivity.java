package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.async.ChangePasswordAsync;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragmentActivity;
import com.aip.dailyestimation.service.DatabaseService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.layout_change_password)
public class ChangePasswordActivity extends CoreFragmentActivity {

    private Activity activity;
    @InjectView(R.id.editCurrentPassword)
    EditText editCurrentPassword;
    @InjectView(R.id.editNewPassword)
    EditText editNewPassword;
    @InjectView(R.id.editNewConfirmPassword)
    EditText editConfirmPassword;
    @InjectView(R.id.txtSubmit)
    TextView txtSubmit;

    DatabaseService mDatabaseService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mDatabaseService = DatabaseService.getInstance(activity);

        SharedPreferences sharedPref = ChangePasswordActivity.this
                .getPreferences(Context.MODE_PRIVATE);
        AdView mAdView = (AdView) findViewById(R.id.adView);

        if (mDatabaseService.getAccount().getUserType().equals("free")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.bringToFront();
        } else {
            mAdView.setVisibility(View.INVISIBLE);
        }

        txtSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onButtonClick() {
        try {
            String currPassword, password;

            currPassword = editCurrentPassword.getText().toString().trim();

            AccountBean accountBean = mDatabaseService.getAccount();

            if (currPassword.length() < 1) {
                L.alert(activity,
                        getResources().getString(R.string.msg_enter_password));
                return;
            }

            try {
                currPassword = Validator.getCurrentPasswordValid(activity,
                        editCurrentPassword.getText().toString().trim());
            } catch (IllegalAccessException e) {
                L.alert(activity, e.getMessage());
                return;
            }

            try {
                password = Validator.getNewPasswordValid(activity,
                        editNewPassword.getText());
            } catch (IllegalAccessException e) {
                L.alert(activity, e.getMessage());
                return;
            }

            try {
                password = Validator.getConfirmPasswordValid(activity,
                        editConfirmPassword.getText());
            } catch (IllegalAccessException e) {
                L.alert(activity, e.getMessage());
                return;
            }

            try {
                password = Validator.getConfPasswordMatch(activity,
                        editNewPassword.getText(),
                        editConfirmPassword.getText());
            } catch (IllegalAccessException e) {
                L.alert(activity, e.getMessage());
                return;
            }

            if (Util.isNetAvailable(activity)) {
                doOnline(accountBean.getServerId() + "", currPassword, password);

            } else {
                // if (!accountBean.getPassword().equals(currPassword)) {
                // L.alert(activity,
                // getResources().getString(
                // R.string.msg_password_invalid));
                // return;
                // }
                // updateLocal(password, true);

                L.alert(activity,
                        getResources().getString(R.string.msg_internet_error));

            }
        } catch (Exception e) {
            L.alert(activity, e.getMessage());
        }

    }

    public void doOnline(String id, String oldPassword, final String newPassword) {

        ChangePasswordAsync async = new ChangePasswordAsync(activity,
                new IResultListner() {

                    @Override
                    public void result(Object result, boolean isSuccess) {
                        JSONObject jsonObject = (JSONObject) result;

                        try {

                            try {
                                if (jsonObject.optString("code").equals("1000")) {
                                    L.alertLogout(activity, getResources().getString(R.string.msg_user_login_another_device));
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (jsonObject.getString("code").equals("200")) {

                                L.alert(activity, Util.getResponseMessage(
                                        activity, jsonObject), new L.IL() {

                                    @Override
                                    public void onSuccess() {
                                        updateLocal(newPassword, false);
                                        // activity.finish();
                                        activity.onBackPressed();
                                    }

                                    @Override
                                    public void onCancel() {
                                        updateLocal(newPassword, false);
                                        // activity.finish();
                                        activity.onBackPressed();
                                    }
                                });
                            } else {
                                L.alert(ChangePasswordActivity.this,
                                        jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            L.alert(ChangePasswordActivity.this, e.getMessage());
                        }
                    }
                });

        async.execute(id, oldPassword, newPassword);
    }

    private void updateLocal(String newPasswrod, boolean isShowSuccess) {
        AccountBean accountBean = mDatabaseService.getAccount();
        accountBean.setPassword(Util.convertPassMd5(newPasswrod));
        accountBean.setIsSync(0);
        int i = mDatabaseService.insertUpdateContact(accountBean);

        if (i > 0) {
            if (!isShowSuccess)
                return;

            L.alert(activity,
                    getResources().getString(R.string.msg_password_changed),
                    new L.IL() {

                        @Override
                        public void onSuccess() {
                            activity.finish();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        } else
            L.alert(activity,
                    getResources().getString(R.string.msg_database_error));
    }
}