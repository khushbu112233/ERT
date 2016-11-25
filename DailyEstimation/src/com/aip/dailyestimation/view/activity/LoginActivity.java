package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.async.SignInAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragmentActivity;
import com.aip.dailyestimation.service.DatabaseService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_login)
public class LoginActivity extends CoreFragmentActivity {

    private Activity activity;

    @InjectView(R.id.editEmail)
    EditText editEmail;
    @InjectView(R.id.editPassword)
    EditText editPassword;
    @InjectView(R.id.txtLogin)
    TextView txtLogin;
    @InjectView(R.id.txtRegister)
    TextView txtRegister;
    @InjectView(R.id.txtForgotPassword)
    TextView txtForgotPassword;
    @InjectView(R.id.chkRememberMe)
    CheckBox chkRememberMe;

    DatabaseService mDatabaseService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        mDatabaseService = DatabaseService.getInstance(activity);

        txtLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }

        });

        SharedPreferences sharedPref = LoginActivity.this
                .getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.commit();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        txtRegister.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switchActivity(activity, SignUpActivity.class);
            }
        });

        txtForgotPassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switchActivity(activity, ForgotPasswordActivity.class);
            }
        });

        String email = SharedPrefrenceUtil.getPrefrence(activity,
                IConstants.PREF_EMAIL, null);
        String password = SharedPrefrenceUtil.getPrefrence(activity,
                IConstants.PREF_PASSWORD, null);

        if (email != null && password != null) {
            editEmail.setText(email);
            editEmail.setSelection(email.length());
            AccountBean contactBean = mDatabaseService.isValidCredentials(
                    email, Util.convertPassMd5(password));

            if (contactBean != null) {
                Log.e("#Login:User Profile", "" + contactBean.toString());
                editPassword.setText(password);
                switchActivity(activity, MainActivity.class);
                activity.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void login() {
        String email, password;

        try {
            email = Validator.getEmailAddressValid(activity,
                    editEmail.getText().toString().toLowerCase());
        } catch (IllegalAccessException e) {
            L.alert(activity, e.getMessage());
            return;
        }

        try {
            password = Validator.getPasswordValid(activity,
                    editPassword.getText());
        } catch (IllegalAccessException e) {
            L.alert(activity, e.getMessage());
            return;
        }

        if (Util.isNetAvailable(activity)) {
            doOnline(email, password);
        } else {
            // doOffline(email, password);
            L.alert(activity,
                    getResources().getString(R.string.msg_internet_error));
        }

    }

    public void doOnline(final String usernaemOrEmail, final String password) {

        SignInAsync async = new SignInAsync(activity, new IResultListner() {

            @Override
            public void result(Object result, boolean isSuccess) {
                if (result != null) {
                    try {

                        JSONObject jsonObject = (JSONObject) result;

                        if (jsonObject.getString("code").equals("200")) {

                            Gson gson = new Gson();

                            JSONObject dataJson = jsonObject
                                    .optJSONObject("data");

                            SharedPrefrenceUtil.setPrefrence(LoginActivity.this, IConstants.KEY_AUTH, dataJson.getString("authKey"));

                            AccountBean accountBean = gson.fromJson(
                                    dataJson.toString(), AccountBean.class);
                            accountBean.setIsSync(1);
                            accountBean.setPassword(Util
                                    .convertPassMd5(password));

                            AccountBean localBean = mDatabaseService
                                    .getAccount(usernaemOrEmail);

                            if (localBean != null
                                    && mDatabaseService.getAccount(accountBean
                                    .getServerId()) == null) // Exist :
                            // email
                            // =
                            // true,
                            // server
                            // id
                            // =
                            // false
                            {

                                if (localBean.getServerId() < 0) // so this
                                // record
                                // still not
                                // updated
                                // from
                                // device to
                                // server
                                {
                                    // TODO conflict here because some one else
                                    // is
                                    // registered this email address

                                } else // this record updated at server but
                                {
                                    // TODO two different record for the same
                                    // email
                                    // address
                                }
                            } else if (localBean == null
                                    && mDatabaseService.getAccount(accountBean
                                    .getServerId()) != null) // Exist :
                            // email
                            // =
                            // false,
                            // server
                            // id
                            // =
                            // true
                            {
                                // email is updated here
                                localBean = mDatabaseService
                                        .getAccount(accountBean.getServerId());
                            }

                            if (localBean != null) // this account is already
                            // present in our local
                            // database. Update user
                            // detail
                            {
                                accountBean.setID(localBean.getID());

                                accountBean.setCreatedAt(localBean
                                        .getCreatedAt());
                                accountBean.setUpdatedAt(localBean
                                        .getUpdatedAt());

                            } else { // New account so add new record for this
                                // user

                                accountBean.setCreatedAt(new Date(Calendar
                                        .getInstance().getTimeInMillis()));
                                accountBean.setUpdatedAt(new Date(Calendar
                                        .getInstance().getTimeInMillis()));
                            }

                            accountBean.setUserType(dataJson
                                    .getString("userType"));

                            mDatabaseService.insertUpdateContact(accountBean);
                            Log.d("UserData: ",
                                    "UserData: " + accountBean.toString());

                            // SharedPreferences sharedPref = LoginActivity.this
                            // .getPreferences(Context.MODE_PRIVATE);
                            // SharedPreferences.Editor editor =
                            // sharedPref.edit();
                            // editor.putString("UserUnicId",
                            // String.valueOf(accountBean.getID()));
                            // editor.putString("userType",
                            // dataJson.getString("userType"));
                            // editor.putString("receiptCount",
                            // dataJson.getString("receiptCount"));
                            // editor.putString("CounterOCRFlag",
                            // dataJson.getString("receiptCount"));
                            // editor.commit();

                            SharedPrefrenceUtil.setPrefrence(activity,
                                    IConstants.KEY_USER_ID,
                                    String.valueOf(accountBean.getID()));
                            SharedPrefrenceUtil.setPrefrence(activity,
                                    "userType", dataJson.getString("userType"));
                            SharedPrefrenceUtil.setPrefrence(activity,
                                    IConstants.PREF_CURR_USER_TYPE,
                                    dataJson.getString("userType"));
                            if (dataJson.getString("userType").equalsIgnoreCase("Paid")) {
                                SharedPrefrenceUtil.setPrefrence(activity,
                                        IConstants.CLOUD_OCR_VALUE, "on");
                            } else {
                                SharedPrefrenceUtil.setPrefrence(activity,
                                        IConstants.CLOUD_OCR_VALUE, "off");
                            }

                            // use for user data sync
                            // ServiceHandler.startExportService(activity);

                            next(usernaemOrEmail, password);
                        } else {
                            L.alert(activity, jsonObject.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.alert(activity, e.getMessage());
                    }
                }
            }
        });

        async.execute(usernaemOrEmail, Util.convertPassMd5(password));
    }

    private void next(String email, String password) {
        if (chkRememberMe.isChecked()) {
            SharedPrefrenceUtil.setPrefrence(activity, IConstants.PREF_EMAIL,
                    email);
            SharedPrefrenceUtil.setPrefrence(activity,
                    IConstants.PREF_PASSWORD, password);
            // SharedPrefrenceUtil.setPrefrence(activity,
            // IConstants.CLOUD_OCR_VALUE, "off");
            // SharedPrefrenceUtil.setPrefrence(activity, "userType", "Free");
            // SharedPrefrenceUtil.setPrefrence(activity,
            // IConstants.CLOUD_OCR_COUNTER, "0");
        } else {
            SharedPrefrenceUtil.setPrefrence(activity, IConstants.PREF_EMAIL,
                    null);
            SharedPrefrenceUtil.setPrefrence(activity,
                    IConstants.PREF_PASSWORD, null);
        }
        switchActivity(activity, MainActivity.class);
        activity.finish();
    }

    private void doOffline(String email, String password) {

        AccountBean contactBean = mDatabaseService.isValidCredentials(email,
                Util.convertPassMd5(password));

        if (contactBean != null) {
            SharedPrefrenceUtil.setPrefrence(activity,
                    IConstants.PREF_CURR_USER_ID, contactBean.getServerId());
            SharedPrefrenceUtil.setPrefrence(activity,
                    IConstants.PREF_CURR_USER_TYPE, contactBean.getUserType());
            SharedPrefrenceUtil.setPrefrence(activity, "userType",
                    contactBean.getUserType());
            Log.e("#Current Local account id",
                    "Current Id is " + contactBean.getID());
            next(email, password);

        } else {

            L.alert(activity, getResources()
                    .getString(R.string.msg_login_error));
        }
    }
}