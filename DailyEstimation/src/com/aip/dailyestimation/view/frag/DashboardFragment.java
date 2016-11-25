package com.aip.dailyestimation.view.frag;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.async.LogoutAsync;
import com.aip.dailyestimation.common.async.ReadUserDetails;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.L.IL;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.BillingActivity;
import com.aip.dailyestimation.view.activity.ChangePasswordActivity;
import com.aip.dailyestimation.view.activity.LoginActivity;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends CoreFragment implements OnClickListener {

    View rootView;

    TextView txtChangePwd;
    TextView txtCreateCategory;
    TextView txtCreateReceipt;
    TextView txtFilter;
    TextView txtProfile;
    TextView txtCreateAddress;
    TextView txtLogout;
    TextView txtCameraTip;
    TextView txtUpgrade;
    TextView txtiTimePunch;
    TextView txtiTimePunchImg;
    TextView Link;

    ImageView fbConnect;

    LinearLayout BottomMsg;
    ScrollView ScrView;

    DatabaseService mDatabaseService;
    AdView mAdView;

    Runnable mUpdateClockTask;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_dashboard, container,
                    false);
            mDatabaseService = DatabaseService.getInstance(getActivity());
            init();

        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        SharedPreferences sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);
        mAdView = (AdView) rootView.findViewById(R.id.adView);

        ((MainActivity) getActivity()).setHeaderTitle(R.id.dashboard_fragment);

        mDatabaseService = DatabaseService.getInstance(getActivity());

        if (mDatabaseService.getAccount().getUserType().equals("free")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.bringToFront();
            // HideShowLayout.setVisibility(View.VISIBLE);
        } else {
            mAdView.setVisibility(View.INVISIBLE);
            // HideShowLayout.setVisibility(View.INVISIBLE);
        }

        Link.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri
                            .parse("http://www.expense.solutions"));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(
                            getActivity(),
                            "No application can handle this request. \n"
                                    + " Please install a webbrowser",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.isNetAvailable(getActivity())) {
            // use for sync user data
            // ServiceHandler.startExportService(getActivity());

            ReadUserDetails details = new ReadUserDetails(getActivity(),
                    new IResultListner() {

                        @Override
                        public void result(Object result, boolean isSuccess) {

                            JSONObject jsonObject = (JSONObject) result;

                            try {

                                try {
                                    if (jsonObject.optString("code").equals("1000")) {
                                        L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (jsonObject.getString("code").equals("200")) {

                                    AccountBean accountBean = mDatabaseService
                                            .getAccount();

                                    mDatabaseService.deleteCurrentAccount();

                                    accountBean.setEmail(jsonObject
                                            .getJSONObject("data").getString(
                                                    "email"));
                                    accountBean.setFirstName(jsonObject
                                            .getJSONObject("data").getString(
                                                    "firstName"));
                                    accountBean.setLastName(jsonObject
                                            .getJSONObject("data").getString(
                                                    "lastName"));
                                    accountBean.setPassword(jsonObject
                                            .getJSONObject("data").getString(
                                                    "password"));
                                    accountBean.setCompanyName(jsonObject
                                            .getJSONObject("data").getString(
                                                    "companyName"));
                                    accountBean.setPhoneno(jsonObject
                                            .getJSONObject("data").getString(
                                                    "phone"));
                                    accountBean.setUserType(jsonObject
                                            .getJSONObject("data").getString(
                                                    "userType"));

                                    // Log.d("Account Read From Server", ""
                                    // + accountBean.toString());

                                    mDatabaseService
                                            .insertUpdateContact(accountBean);

                                    if (accountBean.getUserType()
                                            .equals("Paid")) {
                                        mAdView.setVisibility(View.INVISIBLE);
                                    }

                                    // Log.d("Account Read From Database", ""
                                    // + mDatabaseService.getAccount()
                                    // .toString());

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

            details.execute(String.valueOf(mDatabaseService.getAccount()
                    .getServerId()));

        }

        // Log.d("UserType: ", "" +
        // mDatabaseService.getAccount().getUserType());

    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onActivityCreated(savedInstanceState);
    }

    private void init() {

        mDatabaseService = DatabaseService.getInstance(getActivity());

        txtCreateCategory = (TextView) rootView
                .findViewById(R.id.dashboard_txtCategory);
        txtCreateAddress = (TextView) rootView
                .findViewById(R.id.dashboard_address_book);
        txtCreateReceipt = (TextView) rootView
                .findViewById(R.id.dashboard_txtReceipt);
        txtFilter = (TextView) rootView.findViewById(R.id.dashboard_txtReport);
        txtChangePwd = (TextView) rootView
                .findViewById(R.id.dashboard_txtChangePwd);
        txtProfile = (TextView) rootView
                .findViewById(R.id.dashboard_txtProfile);
        txtUpgrade = (TextView) rootView
                .findViewById(R.id.dashboard_txtUpgrade);
        txtLogout = (TextView) rootView.findViewById(R.id.dashboard_txtLogout);
        txtCameraTip = (TextView) rootView
                .findViewById(R.id.dashboard_txtCamera);
        txtiTimePunch = (TextView) rootView
                .findViewById(R.id.dashboard_txtiTimePunch);
        txtiTimePunchImg = (TextView) rootView
                .findViewById(R.id.dashboard_txtiTimePunchImg);

        fbConnect = (ImageView) rootView.findViewById(R.id.fbConnect);

        Link = (TextView) rootView.findViewById(R.id.Link);
        BottomMsg = (LinearLayout) rootView.findViewById(R.id.lin);
        ScrView = (ScrollView) rootView.findViewById(R.id.scr);

        txtChangePwd.setOnClickListener(this);
        txtProfile.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        txtUpgrade.setOnClickListener(this);
        txtCreateCategory.setOnClickListener(this);
        txtCreateReceipt.setOnClickListener(this);
        txtFilter.setOnClickListener(this);
        txtCreateAddress.setOnClickListener(this);
        txtCameraTip.setOnClickListener(this);
        txtiTimePunch.setOnClickListener(this);
        txtiTimePunchImg.setOnClickListener(this);

        fbConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/pages/Easy-Receipt-Tracker/1617712505175762"));
                startActivity(i);
            }
        });

        mUpdateClockTask = new Runnable() {
            public void run() {
                setTime();
                txtiTimePunch.postDelayed(this, 5000);

                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(1000); // You can manage the blinking time with this
//				// parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
////				anim.setRepeatCount(Animation.INFINITE);
                txtiTimePunch.startAnimation(anim);
            }
        };

        mUpdateClockTask.run();

    }

    public void setTime() {

        try {

            if (txtiTimePunch.getText().equals(
                    getResources().getString(R.string.dashboard_i_time_punch))) {
                txtiTimePunch.setText(R.string.dashboard_i_time_punch_try);
            } else {
                txtiTimePunch.setText(getResources().getString(
                        R.string.dashboard_i_time_punch));
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dashboard_txtChangePwd:
                startUrself(getActivity(), ChangePasswordActivity.class);
                break;

            case R.id.dashboard_txtCategory:
                switchFragment(R.id.main_content, new CategoryFragment());
                break;

            case R.id.dashboard_txtReceipt:
                switchFragment(R.id.main_content, new ReceiptFragment());
                break;

            case R.id.dashboard_txtProfile:
                switchFragment(R.id.main_content, new UserProfileFragment());
                break;

            case R.id.dashboard_txtReport:
                // ((MainActivity) getActivity()).showFilter();
                switchFragment(R.id.main_content, new ReportFragment());
                break;

            case R.id.dashboard_txtLogout:
                doLogout();
                break;

            case R.id.dashboard_txtUpgrade:
                if (Util.isNetAvailable(getActivity())) {
                    startUrself(getActivity(), BillingActivity.class);
                } else {
                    L.alert(getActivity(),
                            getResources().getString(R.string.msg_internet_error));
                }
                break;

            case R.id.dashboard_address_book:
                switchFragment(R.id.main_content, new AddressFragment());
                break;

            case R.id.dashboard_txtCamera:
                switchFragment(R.id.main_content, new CameraTipFragment());
                break;

            case R.id.dashboard_txtiTimePunch:
                Intent mIntent = new Intent(android.content.Intent.ACTION_VIEW);
                mIntent.setData(Uri
                        .parse("https://play.google.com/store/apps/details?id=com.viv.itimepunchplus"));
                startActivity(mIntent);
                break;

            case R.id.dashboard_txtiTimePunchImg:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(Uri
                        .parse("https://play.google.com/store/apps/details?id=com.viv.itimepunchplus"));
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void doLogout() {

        L.confirmDialog(getActivity(),
                getResources().getString(R.string.msg_logout), new IL() {

                    @Override
                    public void onSuccess() {
                        if (Util.isNetAvailable(getActivity())) {
                            LogoutAsync logoutAsync = new LogoutAsync(getActivity(), new IResultListner() {
                                @Override
                                public void result(Object result, boolean isSuccess) {
                                    if (result != null) {
                                        try {
                                            JSONObject object = new JSONObject(result.toString());
                                            if (object.getString("code").equals("200")) {
                                                mDatabaseService.clearPreference();
                                                startUrself(getActivity(), LoginActivity.class);
                                                getActivity().finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            logoutAsync.execute();
                        } else {
                            L.alert(getActivity(),
                                    getResources().getString(R.string.msg_internet_error));
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

}