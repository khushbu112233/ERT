package com.aip.dailyestimation.view.frag;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AddressBean;
import com.aip.dailyestimation.bean.CategoryBean;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.async.AddUpdateCategoryAsync;
import com.aip.dailyestimation.common.async.AddUpdateReceiptAsync;
import com.aip.dailyestimation.common.async.ReadAllAddressAsync;
import com.aip.dailyestimation.common.async.ReadAllCategoryAsync;
import com.aip.dailyestimation.common.util.BigImage;
import com.aip.dailyestimation.common.util.Global;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ImageUtil;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.L.IL;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.Validator;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.customdialog.CustomAlertDialog;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnCategorySelcted;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnListOptionSelcted;
import com.aip.dailyestimation.customdialog.DatePickerUtil;
import com.aip.dailyestimation.customdialog.DatePickerUtil.IOnDateSetListner;
import com.aip.dailyestimation.customdialog.TimePickerUtil.IOnTimeSetListner;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.aip.dailyestimation.view.activity.ShowBIgImageNew;
import com.customeview.NormalEditText;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ocr.CaptureActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddReceiptFragment extends CoreFragment {

    View rootView;

    TextView txtAddReceipt;

    TextView txtSelectDate;

    TextView txtSelectCategory;

    TextView txtAddCategory;

    // EditText editReceiptName;

    EditText editAmount;

    EditText editTip;

    EditText editComment;

    AutoCompleteTextView editReceiptName;

    TextView imgTakeAmount;

    ImageView imgBig;

    TextView txtGrabbedAmtCnt;

    DatabaseService mDatabaseService;

    ReceiptBean mReceiptBean;

    Calendar mCalendar;

    byte[] image;

    List<String> amounts;

    String view_select;

    Boolean UpdateImage = false;

    SharedPreferences prefs;

    ProgressBar pbar;

    Boolean UpdateImageNew = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            prefs = getActivity().getSharedPreferences("imageFilePath",
                    getActivity().MODE_PRIVATE);
            String set_val = prefs.getString("img", "");

            view_select = SharedPrefrenceUtil.getPrefrence(getActivity(),
                    "show_select_value_dialog", "");

            rootView = inflater.inflate(R.layout.fragment_add_receipt,
                    container, false);

            SharedPreferences sharedPref = getActivity().getPreferences(
                    Context.MODE_PRIVATE);

            if (getArguments().containsKey(IConstants.ARG_ID)) {
                rootView = inflater.inflate(R.layout.fragment_add_receipt_2,
                        container, false);
            } else if (getArguments().containsKey("image_set")) {
                rootView = inflater.inflate(R.layout.fragment_add_receipt_2,
                        container, false);
            } else if (sharedPref.contains("OCRSwitch")) {
                if (sharedPref.getBoolean("OCRSwitch", true)) {
                    // txtGrabbedAmtCnt.setVisibility(View.VISIBLE);
                    // imgTakeAmount.setVisibility(View.VISIBLE);
                    rootView = inflater.inflate(R.layout.fragment_add_receipt,
                            container, false);
                } else {
                    // txtGrabbedAmtCnt.setVisibility(View.INVISIBLE);
                    // imgTakeAmount.setVisibility(View.GONE);
                    rootView = inflater.inflate(
                            R.layout.fragment_add_receipt_2, container, false);
                }
            } else {
                sharedPref.edit().putBoolean("OCRSwitch", false).commit();
                rootView = inflater.inflate(R.layout.fragment_add_receipt_2,
                        container, false);
            }

			/*
             * if (getArguments().containsKey(IConstants.ARG_ID)) { rootView =
			 * inflater.inflate(R.layout.fragment_add_receipt, container,
			 * false); } else if (view_select.equals("false")) { rootView =
			 * inflater.inflate(R.layout.fragment_add_receipt, container,
			 * false); } else { rootView =
			 * inflater.inflate(R.layout.fragment_add_receipt, container,
			 * false); }
			 */

            init();
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        if (isEditMode()) {
            ((MainActivity) getActivity())
                    .setHeaderTitle(R.id.edit_receipt_fragment);

        } else
            ((MainActivity) getActivity())
                    .setHeaderTitle(R.id.add_receipt_fragment);

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
    public void onActivityCreated(Bundle savedInstanceState) {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onActivityCreated(savedInstanceState);
    }

    public void init() {

        mDatabaseService = DatabaseService.getInstance(getActivity());
        txtAddReceipt = (TextView) rootView.findViewById(R.id.txtAddReceipt);
        txtSelectDate = (TextView) rootView.findViewById(R.id.txtDate);
        txtSelectCategory = (TextView) rootView.findViewById(R.id.txtCategory);
        txtAddCategory = (TextView) rootView.findViewById(R.id.txtAddCategory);
        txtGrabbedAmtCnt = (TextView) rootView.findViewById(R.id.txtGrabbedAmt);

        editReceiptName = (AutoCompleteTextView) rootView
                .findViewById(R.id.editReceiptName);

        String[] AddressEntries = new String[mDatabaseService
                .getAllLocalAddressName().size()];
        AddressEntries = mDatabaseService.getAllLocalAddressName().toArray(
                AddressEntries);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, AddressEntries);
        editReceiptName.setAdapter(adapter);

        // editReceiptName = (EditText) rootView
        // .findViewById(R.id.editReceiptName);
        editAmount = (EditText) rootView.findViewById(R.id.txtAmount);
        editTip = (EditText) rootView.findViewById(R.id.editTip);
        editComment = (EditText) rootView.findViewById(R.id.editComment);

        imgTakeAmount = (TextView) rootView.findViewById(R.id.imgTakeAmount);
        imgBig = (ImageView) rootView.findViewById(R.id.imgImage);
        pbar = (ProgressBar) rootView.findViewById(R.id.pbar);
        pbar.setVisibility(View.INVISIBLE);

        imgBig.setDrawingCacheEnabled(true);
        // imgBig.buildDrawingCache();

        if (getArguments().containsKey(IConstants.ARG_ID)) {
            txtGrabbedAmtCnt.setVisibility(View.GONE);
        }

        editReceiptName.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(50)});
        txtSelectCategory.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(100)});
        editComment.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(150)});

        mCalendar = Calendar.getInstance();

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "imageFilePath", getActivity().MODE_PRIVATE);
        String set_val = prefs.getString("img", "");

        SharedPreferences sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);

        // if (sharedPref.contains("OCRSwitch")) {
        // if (sharedPref.getBoolean("OCRSwitch", true)) {
        // txtGrabbedAmtCnt.setVisibility(View.VISIBLE);
        // imgTakeAmount.setVisibility(View.VISIBLE);
        // } else {
        // txtGrabbedAmtCnt.setVisibility(View.INVISIBLE);
        // imgTakeAmount.setVisibility(View.GONE);
        // }
        // } else {
        // sharedPref.edit().putBoolean("OCRSwitch", false).commit();
        // }

        // set By Defoult If nothing set
        // imgBig.setImageResource(R.drawable.dashboard_receipt_selector);

        // it use when create manually and receive text ="" when it create
        // manually
        // if (set_val.equals("not_set")) {
        // txtGrabbedAmtCnt.setVisibility(View.INVISIBLE);
        // imgTakeAmount.setVisibility(View.VISIBLE);
        // // imgTakeAmount.setVisibility(View.GONE);
        // // LayoutParams lparams = new
        // // LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        // // editAmount.setLayoutParams(lparams);
        // }
        //
        // if (view_select.equals("false")) {
        // txtGrabbedAmtCnt.setVisibility(View.INVISIBLE);
        // imgTakeAmount.setVisibility(View.VISIBLE);
        // }
        if (view_select.equals("true")) {
            txtGrabbedAmtCnt.setVisibility(View.VISIBLE);
            imgTakeAmount.setVisibility(View.VISIBLE);

            if (getArguments().containsKey("text")) {
                List<String> date = Util.stripDate(getArguments().getString(
                        "text"));
                if (date.size() > 0) {
                    txtSelectDate.setText(date.get(0).toString());
                    String StringDate[] = date.get(0).split("/");
                    Log.d("Date-Month:", "" + StringDate[0]);
                    Log.d("Date-Day:", "" + StringDate[1]);
                    Log.d("Date-Year:", "" + StringDate[2]);

                    mCalendar.set(Calendar.MONTH,
                            Integer.parseInt(StringDate[0]) - 1);
                    mCalendar.set(Calendar.DAY_OF_MONTH,
                            Integer.parseInt(StringDate[1]));
                    mCalendar.set(Calendar.YEAR,
                            Integer.parseInt(StringDate[2]));

                    DatePickerUtil datePickerUtil = new DatePickerUtil(
                            getActivity(),
                            mCalendar.get(Calendar.DAY_OF_MONTH),
                            mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.YEAR), dateSetListner);
                    datePickerUtil
                            .setMaxDateLong(mCalendar.getTimeInMillis() + 60000);
                    Log.d("Calender Date", "" + mCalendar.get(Calendar.MONTH)
                            + 1 + "/" + mCalendar.get(Calendar.DAY_OF_MONTH)
                            + "/" + mCalendar.get(Calendar.YEAR));
                }
            }

        }

        if (isEditMode()) {
            // txtAddReceipt.setText(getResources().getString(R.string.update));

            Log.v("edit", "");
            mReceiptBean = mDatabaseService.getReceipt(getReceiptId());
            if (mReceiptBean != null) {

                try {
                    /*if (mReceiptBean.getImageBytes() != null)
                        imgBig.setImageBitmap(ImageUtil.getBitamp(mReceiptBean
								.getImageBytes()));*/
                    Log.v("editimage", mReceiptBean.getServerImgPath() + "");

                    if (mReceiptBean.getServerImgPath() != null && mReceiptBean.getServerImgPath().length() > 0) {
                        UpdateImageNew = true;
                        pbar.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity())
                                .load(mReceiptBean.getServerImgPath())
                                .error(R.drawable.receipt)
                                .into(imgBig, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        pbar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        // TODO Auto-generated method stub
                                        Log.v("error", "");
                                        imgBig.setImageResource(R.drawable.receipt);

                                    }
                                });
                    } else {
                        imgBig.setImageResource(R.drawable.receipt);
                        pbar.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                editReceiptName.setText(mReceiptBean.getName());
                editAmount.setText(Util.getAmount(mReceiptBean.getAmount()));
                editTip.setText(Util.getAmount(mReceiptBean.getTip()));
                editComment.setText(mReceiptBean.getComment());

                mCalendar.setTime(mReceiptBean.getDate());
                txtSelectDate.setText(Util.getLongToDate(
                        mCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));
                txtSelectCategory.setText(mReceiptBean.getCategoryName());

            } else {
                L.alert(getActivity(),
                        getResources().getString(
                                R.string.no_receipt_found_small), new IL() {

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
            mReceiptBean = new ReceiptBean();
            UpdateImageNew = true;
            // txtAddReceipt.setText(getResources().getString(R.string.add_receipt));
        }

        txtAddReceipt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    addReceipt();

                } catch (IllegalAccessException e) {
                    L.alert(getActivity(), e.getMessage());
                }
            }
        });

        txtSelectDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        txtSelectCategory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectCategory();
            }
        });

        txtAddCategory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setTitle("Enter Category Name");

                // Set up the input
                final NormalEditText input = new NormalEditText(getActivity());
                // Specify the type of input expected; this, for example, sets
                // the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String m_Text = "";
                                m_Text = input.getText().toString();

                                final String str = m_Text;

                                InputMethodManager imm = (InputMethodManager) getActivity()
                                        .getSystemService(
                                                Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView()
                                        .getWindowToken(), 0);

                                if (!m_Text.trim().equals("")) {

                                    final CategoryBean mCategoryBean = new CategoryBean();

                                    mCategoryBean.setCategoryName(m_Text);
                                    // mCategoryBean.setCreatedAt(new Date());
                                    mCategoryBean.setUpdatedAt(new Date());

                                    AddUpdateCategoryAsync addCategoryAsync = new AddUpdateCategoryAsync(
                                            getActivity(), mCategoryBean,
                                            false, new IResultListner() {

                                        @Override
                                        public void result(
                                                Object result,
                                                boolean isSuccess) {

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

                                                if (object.getString(
                                                        "code").equals(
                                                        "200")) {
                                                    try {

                                                        txtSelectCategory
                                                                .setText(str);

                                                        mCategoryBean
                                                                .setCategoryServerId(Integer
                                                                        .parseInt(object
                                                                                .getJSONObject(
                                                                                        "data")
                                                                                .getString(
                                                                                        "id")));

                                                        mDatabaseService
                                                                .insertUpdateCategory(
                                                                        mCategoryBean,
                                                                        false);

                                                        String message = getResources()
                                                                .getString(
                                                                        R.string.category_added_success);

                                                        L.alert(getActivity(),
                                                                message,
                                                                new L.IL() {

                                                                    @Override
                                                                    public void onSuccess() {
                                                                        // getActivity()
                                                                        // .onBackPressed();
                                                                    }

                                                                    @Override
                                                                    public void onCancel() {
                                                                        // getActivity()
                                                                        // .onBackPressed();
                                                                    }
                                                                });

                                                    } catch (Exception e) {
                                                        // TODO
                                                        // Auto-generated
                                                        // catch block
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    String message = object
                                                            .getString("message");
                                                    L.alert(getActivity(),
                                                            message);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            Log.d("CategoryBean Result",
                                                    ""
                                                            + result.toString());
                                        }
                                    });
                                    addCategoryAsync.execute();
                                } else {
                                    String message = "categoryName should not be blank.";
                                    L.alert(getActivity(), message);
                                }
                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                InputMethodManager imm = (InputMethodManager) getActivity()
                                        .getSystemService(
                                                Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView()
                                        .getWindowToken(), 0);
                                dialog.cancel();
                            }
                        });

                builder.show();
            }
        });

        imgTakeAmount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showScannedData();
            }
        });

        imgBig.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                startScan();

                return false;
            }
        });

        imgBig.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    // Bitmap bitmap = ((BitmapDrawable) BigImageview
                    // .getDrawable()).getBitmap();
                    // ImageView ShoBigImage = new ImageView(mContext);
                    // ShoBigImage.setImageBitmap(bitmap);

                    // Toast.makeText(mContext, "Item clicked " + Selected,
                    // Toast.LENGTH_SHORT).show();

//                    ReceiptBean mReceiptBeanTemp = mDatabaseService.getReceipt(getReceiptId());
//
//                    Intent mIntent = new Intent(getActivity(),
//                            ShowBIgImage.class);
//                    mIntent.putExtra("BigImagePath",
//                            mReceiptBeanTemp.getServerImgPath());
//                    mIntent.putExtra("ReceiptId", mReceiptBeanTemp.getReceiptID());
//                    getActivity().startActivity(mIntent);
//
//                    mReceiptBean = mReceiptBeanTemp;

                    if (!UpdateImageNew) {
                        return;
                    }

                    ReceiptBean mReceiptBeanTemp = mDatabaseService.getReceipt(getReceiptId());

                    Intent mIntent = new Intent(getActivity(),
                            ShowBIgImageNew.class);
                    Bitmap bitmap = ((BitmapDrawable) imgBig.getDrawable()).getBitmap();
                    BigImage.setImageBitmap(bitmap);
                    getActivity().startActivity(mIntent);

                    if (getReceiptId() != -1) {
                        mReceiptBean = mReceiptBeanTemp;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        parseData(getArguments());

        if (Util.isNetAvailable(getActivity())) {
            ReadAllAddressAsync addressAsync = new ReadAllAddressAsync(
                    getActivity(), new IResultListner() {

                @Override
                public void result(Object result, boolean isSuccess) {

                    JSONObject object = (JSONObject) result;
                    try {
                        Log.d("ReadAllAddressAsync",
                                "" + object.toString(4));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

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

                            JSONArray array = object
                                    .getJSONArray("data");

                            mDatabaseService
                                    .deleteAllAddress(mDatabaseService
                                            .getCurrentUserId());

                            for (int Counter = 0; Counter < array
                                    .length(); Counter++) {
                                JSONObject obj = array
                                        .getJSONObject(Counter);

                                AddressBean addressBean = new AddressBean();
                                addressBean.setAddressServerId(obj
                                        .getInt("id"));
                                addressBean.setServerId(obj
                                        .getInt("userId"));
                                addressBean.setAddressName(obj
                                        .getString("addressBookName"));

                                mDatabaseService.insertUpdateAddress(
                                        addressBean, true);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            addressAsync.execute();

            ReadAllCategoryAsync categoryAsync = new ReadAllCategoryAsync(
                    getActivity(), new IResultListner() {

                @Override
                public void result(Object result, boolean isSuccess) {

                    JSONObject object = (JSONObject) result;
                    try {
                        Log.d("ReadAllCategoryAsync",
                                "" + object.toString(4));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

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

                            JSONArray array = object
                                    .getJSONArray("data");

                            mDatabaseService
                                    .deleteAllCategory(mDatabaseService
                                            .getCurrentUserId());

                            for (int Counter = 0; Counter < array
                                    .length(); Counter++) {
                                JSONObject obj = array
                                        .getJSONObject(Counter);

                                CategoryBean categoryBean = new CategoryBean();

                                categoryBean.setCategoryServerId(obj
                                        .getInt("id"));
                                categoryBean.setServerId(obj
                                        .getInt("userId"));
                                categoryBean.setCategoryName(obj
                                        .getString("categoryName"));

                                Log.d("CategoryBean: "
                                                + categoryBean
                                                .getCategoryName(),
                                        "ServerId: "
                                                + categoryBean
                                                .getServerId());

                                mDatabaseService.insertUpdateCategory(
                                        categoryBean, true);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            categoryAsync.execute();
        }

    }

    private boolean isEditMode() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_ID)) {
            return true;
        }
        return false;
    }

    private int getReceiptId() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_ID)) {
            return getArguments().getInt(IConstants.ARG_ID);
        }
        return -1;
    }

    @SuppressLint("NewApi")
    private void addReceipt() throws IllegalAccessException {

        double amountInDouble = 0d;
        double tipInDouble = 0d;
        String amountString = editAmount.getText().toString().trim();
        String tipString = editTip.getText().toString().trim();

        String receiptName = editReceiptName.getText().toString().trim();
        if (!Validator.isValidateName(receiptName)) {
            throw new IllegalAccessException(getActivity().getResources()
                    .getString(R.string.invalid_receipt_name));
        }

        String category = txtSelectCategory.getText().toString().trim();
        if (!Validator.isValidateName(category)) {
            throw new IllegalAccessException(getActivity().getResources()
                    .getString(R.string.invalid_category_name));
        }
        amountInDouble = Validator.getValidAmount(getActivity(), amountString);
        if (tipString != null && tipString.trim().length() > 0) {
            try {
                tipInDouble = Validator.getValidTip(getActivity(), tipString);
            } catch (Exception e) {
                throw new IllegalAccessException(getActivity().getResources()
                        .getString(R.string.empty_amount));
            }
        }
        String comment = editComment.getText().toString();
        /*
         * if (!Validator.isValidateName(comment)) { throw new
		 * IllegalAccessException
		 * (getActivity().getResources().getString(R.string.invalid_comment)); }
		 */

        if (isEditMode()) {
            mReceiptBean.setAmount(amountInDouble);
            mReceiptBean.setTip(tipInDouble);
            mReceiptBean.setCategoryName(category);
            mReceiptBean.setComment(comment);
            mReceiptBean.setUpdateddAt(new Date());
            mReceiptBean.setDate(mCalendar.getTime());
            mReceiptBean.setName(editReceiptName.getText().toString().trim());

            try {
                mReceiptBean.setCategoryServerId(mDatabaseService.getCategory(
                        category).getCategoryServerId());
            } catch (Exception e) {
                mReceiptBean.setCategoryServerId(-1);
                e.printStackTrace();
            }

            try {
                mReceiptBean.setAddressServerId(mDatabaseService.getAddress(
                        editReceiptName.getText().toString().trim())
                        .getAddressServerId());
            } catch (Exception e) {
                // add force fully when no address id exist
                mReceiptBean.setAddressServerId(-1);
            }

            Log.v("imagepathelse", mReceiptBean.getServerImgPath() + "");
//            if (mReceiptBean.getServerImgPath() != null && mReceiptBean.getServerImgPath().length() > 0) {
//                pbar.setVisibility(View.VISIBLE);
//                try {
//                    Picasso.with(getActivity())
//                            .load(mReceiptBean.getServerImgPath())
//                            .error(R.drawable.receipt)
//                            .into(imgBig, new Callback() {
//                                @Override
//                                public void onSuccess() {
//                                    pbar.setVisibility(View.INVISIBLE);
//                                }
//
//                                @Override
//                                public void onError() {
//                                    // TODO Auto-generated method stub
//
//                                    Log.v("error", "");
//                                    pbar.setVisibility(View.INVISIBLE);
//                                    imgBig.setImageResource(R.drawable.receipt);
//                                }
//                            });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    imgBig.setImageResource(R.drawable.receipt);
//                    pbar.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                imgBig.setImageResource(R.drawable.receipt);
//                pbar.setVisibility(View.INVISIBLE);
//            }

            if (UpdateImage) {
                try {
                    Bitmap bm = Global.getImageBitmap();

                    if (bm != null) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        mReceiptBean.setImageBytes(byteArray);

                        if (image != null) {
                            mReceiptBean.setImageBytes(image);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            // remove comment code for update image

            // Start comment

            // Bitmap bm = Global.getImageBitmap();
            // ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            // byte[] byteArray = stream.toByteArray();
            // mReceiptBean.setImageBytes(byteArray);

            // End comment

            // if (image != null) {
            // // Toast.makeText(getActivity(), "Path SET FROM IF",
            // // Toast.LENGTH_LONG).show();
            // mReceiptBean.setImageBytes(image);
            // } else {
            // // Toast.makeText(getActivity(), "Path NOT SET FROM IF",
            // // Toast.LENGTH_LONG).show();
            // }

            Log.d("EDIT IF", "" + mReceiptBean.toString());

        } else {

            try {

                Log.d("Add Force Fully", "Add Force Fully");
                mReceiptBean.setReceiptID(-1);// force add new receipt
                mReceiptBean.setAmount(amountInDouble);
                mReceiptBean.setTip(tipInDouble);
                mReceiptBean.setCategoryName(category);
                mReceiptBean.setComment(comment);
                mReceiptBean.setCreatedAt(new Date());
                mReceiptBean.setUpdateddAt(new Date());
                mReceiptBean.setDate(mCalendar.getTime());
                mReceiptBean.setName(editReceiptName.getText().toString().trim());
                mReceiptBean.setCategoryServerId(mDatabaseService.getCategory(
                        category).getCategoryServerId());

                try {
                    mReceiptBean.setAddressServerId(mDatabaseService.getAddress(
                            editReceiptName.getText().toString().trim())
                            .getAddressServerId());
                } catch (Exception e) {
                    // add force fully when no address id exist
                    mReceiptBean.setAddressServerId(-1);
                }

                // Global.getImageBitmap()

                // use when user add manually receipt

                if (getArguments().containsKey("image_set")) {
                    if (!getArguments().getString("image_set", "yes").equals("yes")) {
                        Bitmap bm = Global.getImageBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        mReceiptBean.setImageBytes(byteArray);
                    }
                } else {
                    Bitmap bm = Global.getImageBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    mReceiptBean.setImageBytes(byteArray);
                }

                // mReceiptBean.setImageBytes(byteArray);

                // if (image != null) {
                // if (view_select.equals("true")){
                // mReceiptBean.setImageBytes(image);
                // }
                // // Toast.makeText(getActivity(), "Path SET FROM ELSE",
                // // Toast.LENGTH_LONG).show();
                // } else {
                // // Toast.makeText(getActivity(), "Path NOT SET FROM ELSE",
                // // Toast.LENGTH_LONG).show();
                // }
                Log.d("EDIT ELSE", "" + mReceiptBean.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        mReceiptBean.setIsSync(0);
        mReceiptBean.setUserId(mDatabaseService.getCurrentUserId());
        mDatabaseService.insertUpdateReceipt(mReceiptBean);

        if (Util.isNetAvailable(getActivity())) {

            AddUpdateReceiptAsync receiptAsync = new AddUpdateReceiptAsync(
                    getActivity(), mReceiptBean, new IResultListner() {

                @Override
                public void result(Object result, boolean isSuccess) {
                    if (result != null && result instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) result;

                        try {
                            if (jsonObject.getString("code").equals(
                                    "200")) {

                                if (Util.filterResponse(getActivity(),
                                        jsonObject)) {

                                    ReceiptBean serverReceiptBean = Util
                                            .getReceiptBean(jsonObject);

                                    if (serverReceiptBean != null) {
                                        Log.e("AddupdateReceipt",
                                                "Sending data>>"
                                                        + mReceiptBean
                                                        .toString());
                                        Log.e("AddupdateReceipt",
                                                "Server response>>"
                                                        + serverReceiptBean
                                                        .toString());

                                        // if (serverReceiptBean
                                        // .getServerImgPath() != null)
                                        // {
                                        // mReceiptBean
                                        // .setServerImgPath(serverReceiptBean
                                        // .getServerImgPath());
                                        //
                                        // Picasso.with(getActivity())
                                        // .load(mReceiptBean
                                        // .getServerImgPath())
                                        // .into(imgBig);
                                        //
                                        // }

                                        if (serverReceiptBean
                                                .getServerId() > 0) {
                                            mReceiptBean
                                                    .setServerId(serverReceiptBean
                                                            .getServerId());
                                        }
                                    }
                                    mReceiptBean.setIsSync(1);

                                    Log.e("AddupdateReceipt",
                                            "Local updated data>>"
                                                    + mReceiptBean
                                                    .toString());
                                    addLocalUpdate();
                                }
                            } else if (jsonObject.optString("code").equals(
                                    "1000")) {
                                L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
                                return;
                            } else {
                                String message = jsonObject
                                        .getString("message");
                                L.alert(getActivity(), message);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            });

            receiptAsync.doServerCall();
        } else {
            // addLocalUpdate();
            L.alert(getActivity(),
                    getResources().getString(R.string.msg_internet_error));
        }

        // ServiceHandler.startExportService(getActivity());

    }

    private void addLocalUpdate() {
        mDatabaseService.insertUpdateReceipt(mReceiptBean);
        String message = getResources().getString(
                R.string.receipt_added_success);

        if (isEditMode())
            message = getResources()
                    .getString(R.string.receipt_updated_success);
        L.alert(getActivity(), message, new L.IL() {

            @Override
            public void onSuccess() {
                getActivity().onBackPressed();

                if (UpdateImage) {
                    try {
                        Picasso.with(getActivity()).invalidate(
                                mReceiptBean.getServerImgPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancel() {
                editReceiptName.setText("");
            }
        });
    }

    private void pickDate() {

        DatePickerUtil datePickerUtil = new DatePickerUtil(getActivity(),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.YEAR),
                dateSetListner);
        datePickerUtil.setMaxDateLong(mCalendar.getTimeInMillis() + 60000);
        datePickerUtil.show();
    }

    IOnDateSetListner dateSetListner = new IOnDateSetListner() {

        @Override
        public void onDateSet(int yy, int mm, int dd) {
            mCalendar.set(Calendar.DAY_OF_MONTH, dd);
            mCalendar.set(Calendar.MONTH, mm);
            mCalendar.set(Calendar.YEAR, yy);

			/*
             * TimePickerUtil timePickerUtil = new TimePickerUtil(getActivity(),
			 * mCalendar.get(Calendar.HOUR_OF_DAY),
			 * mCalendar.get(Calendar.MINUTE), false, onTimeSetListner);
			 * timePickerUtil.show();
			 */

            if (mCalendar.after(Calendar.getInstance())) {
                L.alert(getActivity(),
                        getResources().getString(
                                R.string.error_select_future_date));

                mCalendar = Calendar.getInstance();
            } else {
                txtSelectDate.setText(Util.getLongToDate(
                        mCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));
            }
        }
    };

    IOnTimeSetListner onTimeSetListner = new IOnTimeSetListner() {

        @Override
        public void onTimeSet(int hourOfDay, int minuteOfHour) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minuteOfHour);

            if (mCalendar.after(Calendar.getInstance())) {
                L.alert(getActivity(),
                        getResources().getString(
                                R.string.error_select_future_date));

                mCalendar = Calendar.getInstance();
            } else {
                txtSelectDate.setText(Util.getLongToDate(
                        mCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));
            }
        }

    };

    private void selectCategory() {

        List<String> categories = mDatabaseService.getAllLocalCategoriesName();
        if (categories == null || categories.size() == 0) {
            L.confirmDialog(getActivity(),
                    getResources().getString(R.string.no_category_added),
                    new IL() {

                        @Override
                        public void onSuccess() {
                            addCategory();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        } else {
            CustomAlertDialog.openCategoryOption(getActivity(), categories,
                    new IOnCategorySelcted() {

                        @Override
                        public void onCategorySelected(String cateogryName) {
                            txtSelectCategory.setText(cateogryName);
                        }
                    });
        }

    }

    private void addCategory() {
        AddCategoryFragment addCategoryFragment = new AddCategoryFragment();
        switchFragment(R.id.main_content, addCategoryFragment);
    }

    private void startScan() {
        UpdateImage = true;
        // startActivityForResult(
        // new Intent(getActivity(), CaptureActivity.class), 99);

        if (prefs.contains("OCRSwitch")) {
            if (prefs.getBoolean("OCRSwitch", true)) {
                startActivityForResult(new Intent(getActivity(),
                        CaptureActivity.class), 99);
            } else {
                // Intent intent = new Intent(
                // android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                // startActivityForResult(intent, 501);
                captureImage();
            }
        } else {
            prefs.edit().putBoolean("OCRSwitch", false).commit();
            captureImage();
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

    private void parseData(Bundle data) {


        try {

            if (data.containsKey("OFF_OCR")) {
                image = ImageUtil.getByteArrayFromBitmap(Global
                        .getImageBitmap());
                imgBig.setImageBitmap(Global.getImageBitmap());
                pbar.setVisibility(View.INVISIBLE);
                Log.d("Set Image", "Set Image");
            } else if (data.containsKey("text")) {
                String text = data.getString("text");
                System.out.println("ocrResult text >> " + text);
                amounts = Util.stripDigits(text);
                try {
                    image = ImageUtil.getByteArrayFromBitmap(Global
                            .getImageBitmap());
                    imgBig.setImageBitmap(Global.getImageBitmap());
                    pbar.setVisibility(View.INVISIBLE);
                    Log.d("Set Image", "Set Image");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (amounts != null && amounts.size() > 0) {
                    setOcrCount(amounts.size());
                } else
                    setOcrCount(0);
            } else
                setOcrCount(0);

            // set defoult image when user press submit form button
            if (data.containsKey("image_set")) {
                imgBig.setImageResource(R.drawable.dashboard_receipt_selector);
                pbar.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setOcrCount(0);
        }
    }

    private void showScannedData() {
        pbar.setVisibility(View.INVISIBLE);
        if (amounts != null && amounts.size() > 0) {
            editAmount.setText(amounts.get(0));
            if (amounts.size() > 1) {
                CustomAlertDialog.openListOption(getActivity(),
                        "Choose amount", amounts, new IOnListOptionSelcted() {

                            @Override
                            public void onOptionSelected(String option) {
                                editAmount.setText(option);
                            }
                        });
            }
        } else {
            L.error(getActivity(),
                    "No any amount is detected. Please long tap on image and try again");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (data != null && requestCode == 101) {
            String text = data.getStringExtra("text");
            System.out.println("ocrResult text >> " + text);
            amounts = Util.stripDigits(text);

            try {
                image = ImageUtil.getByteArrayFromBitmap(Global
                        .getImageBitmap());
                pbar.setVisibility(View.INVISIBLE);
                imgBig.setImageBitmap(Global.getImageBitmap());
                UpdateImageNew = true;
                pbar.setVisibility(View.INVISIBLE);

                // showScannedData();
                if (amounts != null && amounts.size() > 0) {
                    setOcrCount(amounts.size());
                } else
                    setOcrCount(0);
            } catch (Exception e) {
                e.printStackTrace();
                setOcrCount(0);
            }
        } else if (requestCode == 501 && resultCode == getActivity().RESULT_OK) {
            // try {
            // Bundle extras = data.getExtras();
            // // data.putExtra("OFF_OCR", "OFF_OCR");
            //
            // if (extras.containsKey("data")) {
            // Bitmap bmp = (Bitmap) extras.get("data");
            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // byte[] image = baos.toByteArray();
            // if (image != null) {
            // Log.d("AddReceiptFragment", "image != null");
            // bmp = getResizedBitmap(bmp, 640, 640);
            // Global.setImageBitmap(bmp);
            // imgBig.setImageBitmap(bmp);
            // }
            //
            // // AddReceiptFragment addReceiptFragment = new
            // // AddReceiptFragment();
            // // addReceiptFragment.setArguments(data.getExtras());
            // // switchFragment(R.id.main_content, addReceiptFragment);
            //
            // } else {
            // Toast.makeText(getActivity(), "Fail to capture image",
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

                Log.e("After Image Size: ", "Width: " + thumbnail.getWidth()
                        + " Height: " + thumbnail.getHeight());

//                Matrix mat = new Matrix();
//                mat.postRotate(90);
//
//                thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), mat, true);

                Global.setImageBitmap(thumbnail);

                imgBig.setImageBitmap(thumbnail);
                UpdateImageNew = true;
                pbar.setVisibility(View.INVISIBLE);

                Log.w("path of image from camera......******************.........",
                        ImagePath + "");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setOcrCount(int count) {

        if (count > 0) {
            txtGrabbedAmtCnt.setText("Found " + count + " OCR items");
            txtGrabbedAmtCnt.setVisibility(View.VISIBLE);
        } else {
            txtGrabbedAmtCnt.setVisibility(View.GONE);
        }
        txtGrabbedAmtCnt.setText("Found " + count + " OCR item");
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

}