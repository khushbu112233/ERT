package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.aip.dailyestimation.Notification.GCMClientManager;
import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.async.SignInAsync;
import com.aip.dailyestimation.common.async.StickyLoginAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.ReceiptHelper;
import com.aip.dailyestimation.common.util.ReceiptHelper.DeletedId;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.core.CoreFragmentActivity;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.ServiceHandler;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.ocr.OcrInitAsyncTask2;

import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends CoreFragmentActivity {

    private Activity activity;
    DatabaseService mDatabaseService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        ServiceHandler.startExportService(activity);

        mDatabaseService = DatabaseService.getInstance(activity);



        /* try {
            PackageInfo info = getPackageManager().getPackageInfo("com.aip.dailyestimation", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray()); Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } } catch (PackageManager.NameNotFoundException e) { } catch (NoSuchAlgorithmException e) { }*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);



        boolean isFirstTimeInstalled = SharedPrefrenceUtil.getPrefrence(
                activity, IConstants.PREF_FIRST_TIME_INSTALLED, true);

        SharedPreferences sharedPref = SplashActivity.this
                .getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // new version support auth so logout old version user
        // Start
        try {
            if (!sharedPref.contains(IConstants.KEY_AUTH)) {
                mDatabaseService.clearPreference();
                editor.putString(IConstants.KEY_AUTH, "");
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // End

        editor.putString("userType", "Free");
        editor.commit();
        SharedPrefrenceUtil.setPrefrence(activity, "userType", "Free");

        AdView mAdView = (AdView) findViewById(R.id.adView);

//		try {
//
//			if (mDatabaseService.getAccount().getUserType().equals("free")) {
//				AdRequest adRequest = new AdRequest.Builder().build();
//				mAdView.loadAd(adRequest);
//				mAdView.setVisibility(View.VISIBLE);
//				mAdView.bringToFront();
//			} else {
//				mAdView.setVisibility(View.INVISIBLE);
//			}
//
//		} catch (Exception e) {
//			// e.printStackTrace();
//		}

        if (isFirstTimeInstalled) {

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    initOcr();
                    return null;
                }
            }.execute();

        } else {
            /*
             * SyncAsync syncAsync = new SyncAsync(activity, new
			 * IResultListner() {
			 * 
			 * @Override public void result(Object result, boolean isSuccess) {
			 * 
			 * if(result != null && result instanceof JSONObject) { JSONObject
			 * jsonObject = (JSONObject)result; Log.e("Response",
			 * "SYNC ::SPLASH ::"+jsonObject); updateSyncData(jsonObject); }else
			 * Log.e("Response", "SYNC ::SPLASH ::null"); doStuff(); } });
			 * 
			 * syncAsync.sync();
			 */

            doStuff();
        }

        try {
            new PrefetchData().execute();
        } catch (Exception e) {

        }

        if (false) {

            String PROJECT_NUMBER = "353699131784";
            GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {

                    Log.d("Registration id", registrationId);
                    //send this registrationId to your server
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });

            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
                ;
                String deviceToken = gcm.register(PROJECT_NUMBER);
                Log.d("Registration id", deviceToken);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void doStuff() {
        String email = SharedPrefrenceUtil.getPrefrence(activity,
                IConstants.PREF_EMAIL, null);
        String password = SharedPrefrenceUtil.getPrefrence(activity,
                IConstants.PREF_PASSWORD, null);
        if (email != null && password != null) {
            AccountBean contactBean = mDatabaseService.isValidCredentials(
                    email, Util.convertPassMd5(password));


            if (contactBean != null) {
                Log.e("#SPlash:User Profile", "" + contactBean.toString());

                if (Util.isNetAvailable(activity)) {
                    String authKey=SharedPrefrenceUtil.getPrefrence(SplashActivity.this, "authKey", "");
                    doOnline(authKey);
                } else {
                    // doOffline(email, password);
                    L.alert(activity,
                            getResources().getString(R.string.msg_internet_error));
                }

               // go(MainActivity.class);
            } else
                go(LoginActivity.class);
        } else
            go(LoginActivity.class);
    }

    private void go(final Class<?> clzz) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                switchActivity(activity, clzz);
                activity.finish();
            }
        }, IConstants.SPLASH_TIME);
    }

    private void initOcr() {
        try {
            // Start AsyncTask to install language data and init OCR
            TessBaseAPI baseApi = new TessBaseAPI();
            new OcrInitAsyncTask2(this, baseApi, "eng", "English",
                    TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED,
                    new IResultListner() {

                        @Override
                        public void result(Object result, boolean isSuccess) {
                            SharedPrefrenceUtil
                                    .setPrefrence(
                                            activity,
                                            IConstants.PREF_FIRST_TIME_INSTALLED,
                                            false);
                            go(LoginActivity.class);
                        }
                    }).execute(getStorageDirectory().toString());
        } catch (Exception e) {
        }
    }

    /**
     * Finds the proper location on the SD card where we can save files.
     */
    private File getStorageDirectory() {

        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (RuntimeException e) {
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            try {
                return getExternalFilesDir(Environment.MEDIA_MOUNTED);
            } catch (NullPointerException e) {
            }
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

        } else {

        }
        return null;
    }

    private void updateSyncData(JSONObject jsonObject) {
        try {
            // Log.e("ExportDataService:",
            // "Receipt Sync::Result >> "+jsonObject.toString());
            ReceiptHelper receiptHelper = Util.getReceiptBeans(jsonObject);

            List<ReceiptBean> receiptBeans = null;

            if (receiptHelper != null)
                receiptBeans = receiptHelper.getReceiptBeans();

            // TODO Insert and/or update receipt here
            if (receiptBeans != null) {
                for (ReceiptBean receiptBean : receiptBeans) {

                    receiptBean.setIsSync(1);
                    ReceiptBean localReceiptBean = mDatabaseService
                            .getReceipt(receiptBean.getReceiptID());

                    if (localReceiptBean == null)
                        localReceiptBean = mDatabaseService
                                .getReceiptByServerId(receiptBean.getServerId());

                    if (localReceiptBean != null) {
                        // TODO update receipt
                        localReceiptBean = Util.getToReceiptBean(receiptBean,
                                localReceiptBean);
                        mDatabaseService.insertUpdateReceipt(localReceiptBean);
                        Log.i("Export Data Service", "Updated receipt bean >> "
                                + localReceiptBean.toString());
                    } else {
                        // TODO insert receipt
                        mDatabaseService.insertUpdateReceipt(receiptBean);

                        Log.i("Export Data Service",
                                "New Inserted receipt bean >> "
                                        + receiptBean.toString());
                    }
                }
            }

            // TODO Delete receipt here

            List<DeletedId> deletedIds = null;

            if (receiptHelper != null)
                deletedIds = receiptHelper.getDeletedIds();

            if (deletedIds != null) {
                for (DeletedId deletedId : deletedIds) {

                    mDatabaseService.deleteServerReceipt(deletedId.getId());

                    Log.e("Deleted Receipt", ">>" + deletedId.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                // ...
                // register device to Google Cloud Messaging if not already done
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                reg_token_update(SplashActivity.this, gcm.register("353699131784"));
                Log.e("DHIMS", "" + gcm.register("353699131784"));

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;

        }
    }
    public static void reg_token_update(Context context,String reg_token)
    {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sp_reg_token", reg_token);
        editor.commit();
    }



    public void doOnline(final String authkey) {

        StickyLoginAsync async = new StickyLoginAsync(activity, new IResultListner() {

            @Override
            public void result(Object result, boolean isSuccess) {
                if (result != null) {
                    try {

                        JSONObject jsonObject = (JSONObject) result;

                        if (jsonObject.getString("code").equals("200")) {

                            Log.v("jsonObject", jsonObject + "");
                            Gson gson = new Gson();

                            JSONObject dataJson = jsonObject
                                    .optJSONObject("data");

                            SharedPrefrenceUtil.setPrefrence(SplashActivity.this, IConstants.KEY_AUTH, dataJson.getString("authKey"));

                            AccountBean accountBean = gson.fromJson(
                                    dataJson.toString(), AccountBean.class);
                            accountBean.setIsSync(1);

                            AccountBean localBean = mDatabaseService
                                    .getAccount(dataJson.getString("email"));

                            if (localBean != null
                                    && mDatabaseService.getAccount(accountBean
                                    .getServerId()) == null) // Exist :

                            {

                                if (localBean.getServerId() < 0) // so this

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

                            switchActivity(activity, MainActivity.class);
                            activity.finish();


                        } else {
//                            L.alert(activity, jsonObject.getString("message"));
                            go(LoginActivity.class);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.alert(activity, e.getMessage());
                    }
                }
            }
        });

        async.execute(authkey);
    }
}