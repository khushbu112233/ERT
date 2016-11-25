package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.common.util.Global;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.customdialog.CustomAlertDialog;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnCategorySelcted;
import com.aip.dailyestimation.customdialog.DatePickerUtil;
import com.aip.dailyestimation.customdialog.DatePickerUtil.IOnDateSetListner;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.frag.AddReceiptFragment;
import com.aip.dailyestimation.view.frag.DashboardFragment;
import com.aip.dailyestimation.view.frag.ReceiptFragment;
import com.aip.dailyestimation.vo.FilterVO;
import com.google.android.gms.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.ocr.CaptureActivity;
import com.slidegmenu.BaseActivity;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "MainActivity";

    private MainActivity activity;

    DashboardFragment mDashboardFragment;

    FrameLayout mFilterContainer;

    boolean isFilterVisible;

    private String resultUrl = "result.txt";

    int flag = 0;

    private TextView txtHeaderTitle;

    DatabaseService mDatabaseService;

    static AdView mAdView;

    SharedPreferences sharedPref;

    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        setLeftAndRightMenu();
        mDatabaseService = DatabaseService.getInstance(activity);
        mFilterContainer = (FrameLayout) findViewById(R.id.frame_filterContainer);

        txtHeaderTitle = (TextView) findViewById(R.id.actBar_title);
        setToggleMenu((View) findViewById(R.id.actBar_imgDrawer));

        mDashboardFragment = new DashboardFragment();
        switchFragmentWithoutBackStack(mDashboardFragment, R.id.main_content);

        // mAdView = (AdView) findViewById(R.id.adView);
        // AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);
        // mAdView.bringToFront();

        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

        if (!sharedPref.contains("lastUpdateTime")) {
            sharedPref.edit().putLong("lastUpdateTime", 0);
        }

        long lastUpdateTime = sharedPref.getLong("lastUpdateTime", 0);

        if ((lastUpdateTime + (24 * 60 * 60 * 1000)) < System
                .currentTimeMillis()) {

			/* Save current timestamp for next Check */
            lastUpdateTime = System.currentTimeMillis();
            SharedPreferences.Editor editor = getPreferences(0).edit();
            editor.putLong("lastUpdateTime", lastUpdateTime);
            editor.commit();

			/* Start Update */
            if (Util.isNetAvailable(activity)) {
                try {
                    if (mDatabaseService.getAccount().getReceipt() != null
                            && !mDatabaseService.getAccount().getReceipt()
                            .equals("")) {
                        readBill();
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//         readBill();

    }

    public void readBill() {
        Intent mIntent = new Intent(activity, ConsumeBillingActivity.class);
        startActivity(mIntent);
    }

    public void setDataFromResultAcxtivity(String str) {
        Log.d("Received Data: ", "" + str);
        Toast.makeText(activity, "" + str, Toast.LENGTH_LONG).show();
    }

    public void setLeftAndRightMenu() {
        try {
            /*
             * SlidingMenu mRightSlidingMenu = getSlidingMenu();
			 * mRightSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
			 * mRightSlidingMenu
			 * .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			 * 
			 * mRightSlidingMenu.setSecondaryMenu(R.layout.right_menu_frame);
			 * mRightSlidingMenu
			 * .setSecondaryShadowDrawable(R.drawable.shadowright);
			 * getSupportFragmentManager() .beginTransaction()
			 * .replace(R.id.menu_frame_two, new RightMenuFragment()) .commit();
			 */

            mSlidingMenu.setOnClosedListener(new OnClosedListener() {

                @Override
                public void onClosed() {

                }
            });

            mSlidingMenu.setOnOpenedListener(new OnOpenedListener() {

                @Override
                public void onOpened() {
                    // mSlidingMenu.setSlidingEnabled(true);
                    // startBlurProcess();
                }
            });

            mSlidingMenu.setOnOpenListener(new OnOpenListener() {

                @Override
                public void onOpen() {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * If you want to start this activity from another
     */
    public static void startUrself(Activity context, Class<?> clzz) {
        Intent newActivity = new Intent(context, clzz);
        newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (isFilterVisible) {
            hideFilter();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static Bitmap getScaledBitmap(Bitmap b, int reqWidth, int reqHeight) {
        int bWidth = b.getWidth();
        int bHeight = b.getHeight();

        int nWidth = reqWidth;
        int nHeight = reqHeight;

        // float parentRatio = (float) reqHeight / reqWidth;
        float parentRatio = (float) bHeight / bWidth;

        nHeight = (int) (nWidth * parentRatio);
        // nHeight = bHeight;
        // nWidth = (int) (reqWidth * parentRatio);

        return Bitmap.createScaledBitmap(b, nWidth, nHeight, true);
    }

    public void setHeaderTitle(int id) {
        String title = getResources().getString(R.string.app_name);
        findViewById(R.id.actBar_rightText).setVisibility(View.GONE);
        findViewById(R.id.image_export).setVisibility(View.GONE);
        switch (id) {
            case R.id.dashboard_fragment:
                title = getResources().getString(R.string.dashboard);
                break;

            case R.id.category_fragment:
                title = getResources().getString(R.string.title_category);
                findViewById(R.id.actBar_rightText).setVisibility(View.VISIBLE);
                break;

            case R.id.receipt_fragment:
                title = getResources().getString(R.string.title_receipt);
                findViewById(R.id.actBar_rightText).setVisibility(View.VISIBLE);
                findViewById(R.id.image_export).setVisibility(View.GONE);
                findViewById(R.id.image_export).setEnabled(true);
                findViewById(R.id.image_export).setClickable(true);

                break;

            case R.id.add_category_fragment:
                title = getResources().getString(R.string.title_add_category);
                break;

            case R.id.edit_category_fragment:
                title = getResources().getString(R.string.title_update_category);
                break;

            case R.id.add_receipt_fragment:
                title = getResources().getString(R.string.title_add_receipt);
                break;

            case R.id.edit_receipt_fragment:
                title = getResources().getString(R.string.title_update_receipt);
                break;

            case R.id.report_fragment:
                title = getResources().getString(R.string.title_report);
                break;

            case R.id.edit_user_profile:
                title = getResources().getString(R.string.title_profile);
                break;

            case R.id.view_receipt_fragment:
                title = getResources().getString(R.string.title_view_receipt);
                break;

            case R.id.address_fragment:
                title = getResources().getString(R.string.title_address);
                findViewById(R.id.actBar_rightText).setVisibility(View.VISIBLE);
                break;

            case R.id.add_address_fragment:
                title = getResources().getString(R.string.title_add_address);
                break;

            case R.id.edit_address_fragment:
                title = getResources().getString(R.string.title_update_address);
                break;

            case R.id.camera_tip_fragment:
                title = getResources().getString(R.string.title_camera);
                break;

            default:
                break;
        }

        txtHeaderTitle.setText(title);
    }

    public void submitForm() {

        SharedPreferences prefs = this.getSharedPreferences("imageFilePath",
                getApplication().MODE_PRIVATE);
        prefs.edit().putString("img", "not_set").apply();
        SharedPrefrenceUtil.setPrefrence(activity, "show_select_value_dialog",
                "false");

        Intent data = new Intent();
        Bitmap Icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.receipt_active);
        Global.setImageBitmap(Icon);
        data.putExtra("text", "");
        data.putExtra("image_set", "yes");
        setResult(RESULT_OK, data);

        AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
        addReceiptFragment.setArguments(data.getExtras());
        switchFragment(R.id.main_content, addReceiptFragment);

    }

    public void startScan() {

        if (Util.isNetAvailable(activity)) {

            if (sharedPref.contains("OCRSwitch")) {
                if (sharedPref.getBoolean("OCRSwitch", true)) {
                    startActivityForResult(new Intent(activity,
                            CaptureActivity.class), 101);
                } else {
                    // Intent intent = new Intent(
                    // android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    // startActivityForResult(intent, 501);
                    // Intent i = new Intent(activity,
                    // Custom_CameraActivity.class);
                    // startActivity(i);
                    captureImage();
                }
            } else {
                sharedPref.edit().putBoolean("OCRSwitch", false).commit();
                captureImage();
            }

        } else {
            L.alert(MainActivity.this,
                    getResources().getString(R.string.msg_internet_error));
        }

    }

    public void captureImage() {
        // Intent intent = new Intent(
        // android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // startActivityForResult(intent, 501);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = new File(android.os.Environment.getExternalStorageDirectory(),
                "ReceiptTracker.jpg");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        startActivityForResult(intent, 501);

    }

	/*
     * check date velidation
	 * 
	 * private static String[] getDate(String desc) { int count = 0; String[]
	 * allMatches = new String[2]; Matcher m = Pattern .compile(
	 * "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d")
	 * .matcher(desc); while (m.find()) { allMatches[count] = m.group();
	 * count++; } return allMatches; }
	 */

	/*
     * public static boolean validateJavaDate(String strDate, String formatDate)
	 * { Check if date is 'null' if (strDate.trim().equals("")) { return true; }
	 * Date is not 'null' else {
	 * 
	 * Set preferred date format, For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy
	 * etc.
	 * 
	 * 
	 * // formatDate = "MM/dd/yyyy" type
	 * 
	 * SimpleDateFormat sdfrmt = new SimpleDateFormat(formatDate);
	 * sdfrmt.setLenient(false); Create Date object Date javaDate = null; parse
	 * the string into date form try { javaDate = sdfrmt.parse(strDate); //
	 * System.out.println("Date after validation: " + javaDate); } Date format
	 * is invalid catch (ParseException e) { //
	 * System.out.println("The date you provided is in an " // +
	 * "invalid date format."); return false; } Return 'true' - since date is in
	 * valid format return true; } }
	 */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && requestCode == 101) {
            AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
            addReceiptFragment.setArguments(data.getExtras());
            switchFragment(R.id.main_content, addReceiptFragment);

            if (data.getExtras().containsKey("text")) {
                // Log.d("Scaning Text: ", "" + data.getStringExtra("text"));

                SharedPrefrenceUtil.setPrefrence(activity, "SCAN_TEXT_DATA",
                        data.getStringExtra("text"));

                String text = data.getStringExtra("text");

                List<String> dateList = Util.stripDate(text);

                Log.d("Find Date: ", "" + dateList.toString());

				/*
                 * String text = data.getStringExtra("text");
				 * 
				 * String[] lines = text.split(System
				 * .getProperty("line.separator"));
				 * 
				 * for (int i = 0; i < lines.length; i++) {
				 * 
				 * String[] datess = lines[i].split(" ");
				 * 
				 * for (int j = 0; j < datess.length; j++) {
				 * 
				 * if (!datess[j].equals("")) { if (validateJavaDate(datess[j],
				 * "MM/dd/yyyy")) { Log.d("Find Date", "" + datess[j]);
				 * Toast.makeText(activity, datess[j],
				 * Toast.LENGTH_LONG).show(); } else if
				 * (validateJavaDate(datess[j], "MM-dd-yyyy")) {
				 * Log.d("Find Date", "" + datess[j]); Toast.makeText(activity,
				 * datess[j], Toast.LENGTH_LONG).show(); } }
				 * 
				 * }
				 * 
				 * }
				 */

            }

        } else if (requestCode == 501 && resultCode == RESULT_OK) {

            // try {
            // Bundle extras = data.getExtras();
            // data.putExtra("OFF_OCR", "OFF_OCR");
            //
            // if (extras.containsKey("data")) {
            // Bitmap bmp = (Bitmap) extras.get("data");
            //
            // // Global.setImageBitmap(bmp);
            //
            // Log.e("Before BitMap Image Size: ", "Width: " + bmp.getWidth()
            // + "  Height: " + bmp.getHeight());
            //
            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // byte[] image = baos.toByteArray();
            // if (image != null) {
            // Log.d(TAG, "image != null");
            // bmp = getResizedBitmap(bmp, 640, 640);
            // Global.setImageBitmap(bmp);
            // }
            //
            // Log.e("After BitMap Image Size: ", "Width: " + bmp.getWidth()
            // + "  Height: " + bmp.getHeight());
            //
            // AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
            // addReceiptFragment.setArguments(data.getExtras());
            // switchFragment(R.id.main_content, addReceiptFragment);
            //
            // } else {
            // Toast.makeText(getBaseContext(), "Fail to capture image",
            // Toast.LENGTH_LONG).show();
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // super.onActivityResult(requestCode, resultCode, data);
            // }

            try {

                Bundle extras;

                if (data == null) {
                    extras = new Bundle();
                } else if (data.getExtras() == null) {
                    extras = new Bundle();
                } else {
                    extras = data.getExtras();
                }

                extras.putString("OFF_OCR", "OFF_OCR");

                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());

                String ImagePath = "";

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("ReceiptTracker.jpg")) {
                        f = temp;
                        ImagePath = f.getPath();
                        Log.w("image size:", "" + temp.length());
                        break;
                    }

                }

                Bitmap thumbnail = (BitmapFactory.decodeFile(ImagePath));

                Log.e("Befor Image Size: ", "Width: " + thumbnail.getWidth()
                        + " Height: " + thumbnail.getHeight());

//                thumbnail = getResizedBitmap(thumbnail, 640, 640);
                thumbnail = getResizedBitmap(thumbnail, 640);

//                Log.e("After Image Size: ", "Width: " + thumbnail.getWidth()
//                        + " Height: " + thumbnail.getHeight());
//
//                Matrix mat = new Matrix();
//                mat.postRotate(90);
//
//                thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), mat, true);

                Global.setImageBitmap(thumbnail);

                AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
                addReceiptFragment.setArguments(extras);
                switchFragment(R.id.main_content, addReceiptFragment);

                Log.w("path of image from camera......******************.........",
                        ImagePath + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }

        // if(data == null ){
        // Log.e("dhims", "dhims null");
        // }else{
        // Log.e("dhims", "dhims code: " + requestCode);
        // }

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public Bitmap getResizedBitmap(Bitmap myBitmap, int newWidth) {
        final int maxSize = newWidth;
        int outWidth;
        int outHeight;
        int inWidth = myBitmap.getWidth();
        int inHeight = myBitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(myBitmap, outWidth, outHeight, false);
        return resizedBitmap;
    }

    public void showFilter() {
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(activity,
                R.anim.alpha_in);
        mFilterContainer.setVisibility(View.VISIBLE);
        mFilterContainer.requestFocus();
        mFilterContainer.bringToFront();
        mFilterContainer.setClickable(true);
        ((FrameLayout) findViewById(R.id.root_content)).setEnabled(false);
        myFadeInAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });

        mFilterContainer.startAnimation(myFadeInAnimation);
        initFilter();
        isFilterVisible = true;
    }

    public void hideFilter() {
        Animation myFadeOutAnimation = AnimationUtils.loadAnimation(activity,
                R.anim.alpha_out);
        myFadeOutAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFilterContainer.setVisibility(View.GONE);
            }
        });
        mFilterContainer.startAnimation(myFadeOutAnimation);
        ((FrameLayout) findViewById(R.id.root_content)).setEnabled(true);
        isFilterVisible = false;
    }

    /**
     * This field is for filter
     */
    TextView txtOptionFromDate;
    TextView txtOptionToDate;
    TextView txtOptionCategory;

    EditText editOptionAmount;
    EditText editOptionComment;

    Calendar mCalendar, mFromCalendar, mToCalendar;

    FilterVO mFilterVO;

    private void initFilter() {
        if (txtOptionFromDate == null) {
            txtOptionFromDate = (TextView) mFilterContainer
                    .findViewById(R.id.optionFromDate);
            txtOptionToDate = (TextView) mFilterContainer
                    .findViewById(R.id.optionToDate);
            txtOptionCategory = (TextView) mFilterContainer
                    .findViewById(R.id.optionCategory);

            editOptionAmount = (EditText) mFilterContainer
                    .findViewById(R.id.optionAmount);
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
        }
        clearFilter();

    }

    private void clearFilter() {
        txtOptionFromDate.setText("");
        txtOptionToDate.setText("");
        txtOptionCategory.setText("");
        editOptionAmount.setText("");
        editOptionComment.setText("");

        mFromCalendar = Calendar.getInstance();
        mFromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mFromCalendar.set(Calendar.MINUTE, 0);
        mFromCalendar.set(Calendar.SECOND, 0);
        mFromCalendar.set(Calendar.MILLISECOND, 0);

        mToCalendar = Calendar.getInstance();

        mToCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mToCalendar.set(Calendar.MINUTE, 0);
        mToCalendar.set(Calendar.SECOND, 0);
        mToCalendar.set(Calendar.MILLISECOND, 0);

        mCalendar = Calendar.getInstance();

        mFilterVO.clear();
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
                } catch (IllegalAccessException e) {
                    L.alert(activity, e.getMessage());
                    return;
                }
                mFilterVO.setComment(editOptionComment.getText().toString().trim());
                if (mFilterVO.isEmpty()) {
                    L.alert(activity,
                            "No filter applied. Please apply for at least one filter");
                } else {
                    hideFilter();
                    showReceipts(mFilterVO);
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
    }

    private void setToDate() {
        mFilterVO.setToCalendar(mToCalendar);
        txtOptionToDate.setText(Util.getLongToDate(
                mToCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));
    }

    private void selectCategory() {

        List<String> categories = mDatabaseService.getAllLocalCategoriesName();
        if (categories == null || categories.size() == 0) {
            L.alert(activity,
                    getResources().getString(R.string.no_category_added));
        } else {
            CustomAlertDialog.openCategoryOption(activity, categories,
                    new IOnCategorySelcted() {

                        @Override
                        public void onCategorySelected(String cateogryName) {
                            txtOptionCategory.setText(cateogryName);
                            mFilterVO.setCategoryName(cateogryName);
                        }
                    });
        }
    }

    private void showReceipts(FilterVO filterVO) {
        ReceiptFragment receiptFragment = new ReceiptFragment();
        if (filterVO != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IConstants.ARG_FILTER, filterVO);
            receiptFragment.setArguments(bundle);
        }
        switchFragment(R.id.main_content, receiptFragment);
    }
}
