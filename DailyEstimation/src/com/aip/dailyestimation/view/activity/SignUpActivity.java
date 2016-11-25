package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.async.SignUpAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragmentActivity;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.ServiceHandler;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_register)
public class SignUpActivity extends CoreFragmentActivity {

    private Activity activity;

    @InjectView(R.id.editCompanyName)
    EditText editCompanyName;
    @InjectView(R.id.editFirstName)
    EditText editFirstName;
    @InjectView(R.id.editLastName)
    EditText editLastName;
    @InjectView(R.id.editMobile)
    EditText editPhoneNo;

    @InjectView(R.id.editEmail)
    EditText editEmail;
    @InjectView(R.id.editPassword)
    EditText editPassword;
    @InjectView(R.id.editConfirmPassword)
    EditText editConfirmPassword;
    @InjectView(R.id.fbLink)
    ImageView fbLink;
    @InjectView(R.id.txtRegister)
    TextView txtRegister;
    @InjectView(R.id.chkPrivacyPolicy)
    CheckBox chkPrivacyPolicy;

    DatabaseService mDatabaseService;

    AccountBean accountBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        mDatabaseService = DatabaseService.getInstance(activity);

        editPhoneNo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});

        txtRegister.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                register();
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        chkPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        fbLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/pages/Easy-Receipt-Tracker/1617712505175762"));
                startActivity(i);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void register() {
        String compName, firstName, lastName, email, password, mobile;

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

        try {
            password = Validator.getConfPasswordMatch_(activity,
                    editPassword.getText(), editConfirmPassword.getText());
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

        compName = editCompanyName.getText().toString();
        if (!Validator.isValidateName(compName)) {
            L.alert(activity, getResources()
                    .getString(R.string.invalid_company));
            return;
        }

        try {
            mobile = Validator.getPhoneNumberValid(activity,
                    editPhoneNo.getText());
        } catch (IllegalAccessException e) {
            L.alert(activity, e.getMessage());
            return;
        }

        if (!chkPrivacyPolicy.isChecked()) {
            L.alert(activity,
                    getResources().getString(R.string.msg_accept_policy));
            return;
        }

        accountBean = new AccountBean();

        accountBean.setCompanyName(compName);
        accountBean.setEmail(email);
        accountBean.setFirstName(firstName);
        accountBean.setLastName(lastName);
        accountBean.setPassword(Util.convertPassMd5(password));
        accountBean.setPhoneno(mobile);
        accountBean.setCreatedAt(new Date(Calendar.getInstance()
                .getTimeInMillis()));
        accountBean.setUpdatedAt(new Date(Calendar.getInstance()
                .getTimeInMillis()));
        accountBean.setReceipt("");

        if (mDatabaseService.getAccount(email) != null) {
            L.alert(activity, getResources()
                    .getString(R.string.msg_email_exist));

        } else {
            if (Util.isNetAvailable(activity)) {
                doOnline();
            } else {
                L.alert(activity,
                        getResources().getString(R.string.msg_internet_error));
            }
        }

    }

    public void doOnline() {

        SignUpAsync async = new SignUpAsync(activity, accountBean,
                new IResultListner() {

                    @Override
                    public void result(Object result, boolean isSuccess) {

                        try {

                            JSONObject jsonObject = (JSONObject) result;

                            if (jsonObject.getString("code").equals("200")) {

                                JSONObject dataJson = jsonObject
                                        .optJSONObject("data");

                                SharedPrefrenceUtil.setPrefrence(SignUpActivity.this, IConstants.KEY_AUTH, dataJson.getString("authKey"));

                                int serverId = dataJson.optInt("id");
                                Log.e("#Registration#", "New server id is ="
                                        + serverId + ">>Result is >>"
                                        + jsonObject.toString());
                                accountBean.setServerId(serverId);
                                accountBean.setIsSync(1);

                                SharedPrefrenceUtil.setPrefrence(activity,
                                        IConstants.KEY_USER_ID,
                                        String.valueOf(accountBean.getID()));

                                localUpdate();

                                // L.alert(activity,
                                // Util.getResponseMessage(activity,
                                // jsonObject),
                                // new L.IL() {
                                //
                                // @Override
                                // public void onSuccess() {
                                // activity.finish();
                                // }
                                //
                                // @Override
                                // public void onCancel() {
                                // activity.finish();
                                // }
                                // });
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

        async.execute();
    }

    private void localUpdate() {
        accountBean.setUserType("Free");
        SharedPrefrenceUtil.setPrefrence(activity, IConstants.CLOUD_OCR_VALUE,
                "off");

        int i = mDatabaseService.insertUpdateContact(accountBean);

        if (i > 0) {
            ServiceHandler.startExportService(activity);
            L.alert(activity,
                    getResources().getString(R.string.msg_registration),
                    new L.IL() {

                        @Override
                        public void onSuccess() {

                            SharedPrefrenceUtil.setPrefrence(activity,
                                    IConstants.PREF_EMAIL, editEmail.getText()
                                            .toString().toLowerCase());
                            SharedPrefrenceUtil.setPrefrence(activity,
                                    IConstants.PREF_PASSWORD, editPassword
                                            .getText().toString());
                            SharedPrefrenceUtil.setPrefrence(activity,
                                    IConstants.CLOUD_OCR_VALUE, "off");
                            SharedPrefrenceUtil.setPrefrence(activity,
                                    "userType", "Free");
                            SharedPrefrenceUtil.setPrefrence(activity,
                                    IConstants.CLOUD_OCR_COUNTER, "0");

                            Intent mIntent = new Intent(activity,
                                    MainActivity.class);
                            startActivity(mIntent);
                            activity.finishAffinity();
                            activity.finish();
                        }

                        @Override
                        public void onCancel() {
                            activity.finish();
                        }
                    });

        } else
            L.alert(activity,
                    getResources().getString(R.string.msg_registration_error));

    }

}
