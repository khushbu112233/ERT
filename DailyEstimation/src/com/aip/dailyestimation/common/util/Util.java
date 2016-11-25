package com.aip.dailyestimation.common.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.bean.DateTypeAdapter;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class Util {

    static String[] AMOUNT_PREFIX = {"total", "$", "rs", "amount", "tax",
            "fees", "cost", "rent", "price"};

    public static String convertPassMd5(String pass) {
        Logger.info("#convertPassMd5#Original", pass);
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
            Logger.info("#convertPassMd5#Converted", password);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    /**
     * Returns boolean representing status of network availability. If network
     * is available it returns true else returns false.
     *
     * @param context - Object of Context, context from where the activity is going
     *                to start.
     * @return boolean
     */
    public static synchronized boolean isNetAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public String getPrimaryEmail(Context context) {
        try {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String possibleEmail = account.name;

                    return possibleEmail;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public void setHideSoftKeyboard(Context context, EditText mEditTextCommon) {
        InputMethodManager mInputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(
                mEditTextCommon.getWindowToken(), 0);
    }

    public void setShowSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideKeyboard(Activity activity, View view) {
        if (activity != null) {

            if (view != null) {
                try {
                    InputMethodManager imm = (InputMethodManager) activity
                            .getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                activity.getWindow()
                        .setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }

        }
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    /**
     * Returns long representing date in long format.
     *
     * @param date     - it represents date in String format like date =
     *                 "12-December-2012";
     * @param dformate - it represents Date-Format like Ex.dformat ="dd-MMM-yyyy"
     * @return long
     */
    @SuppressLint("SimpleDateFormat")
    public static long getDateToLong(String date, String dformate) {
        // longdate = "12-December-2012";
        long milliseconds = 0;
        try {
            SimpleDateFormat f = new SimpleDateFormat(dformate);
            Date d = f.parse(date);
            milliseconds = d.getTime();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return milliseconds;
    }

    /**
     * Returns String representing date in String format.
     *
     * @param longdate - it represents date in long format Ex. long date =
     *                 1343805819061
     * @param dformate - it represents Date-Format like d format = "MM/dd/yyyy"
     * @return String
     */
    public static String getLongToDate(long longdate, String dformate) {
        // longdate = "12-December-2012";
        String dateString = "";

        try {
            dateString = DateFormat.format(dformate, new Date(longdate))
                    .toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dateString;
    }

    public static String getAmount(double amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(amount);
    }

    public static boolean filterResponse(Activity activity,
                                         JSONObject jsonObject) {

        try {
            if (jsonObject.getString("code").equals("1000")) {
                return true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        if (!Util.isNetAvailable(activity)) {
            L.alert(activity, "Please check your internet connection");

        } else if (jsonObject != null) {
            // show error here if present other wise return true

            try {
                Gson gson = new Gson();
                Response response = gson.fromJson(jsonObject.toString(),
                        Response.class);

                Log.d("Filter Response: ", "" + response.toString());
                if (response.getCode() != 200) {
                    Log.e("ERROR:WS", "Response : " + jsonObject.toString());
                    //alert(activity, response.getMessage());
                    return false;
                } else
                    return true;

            } catch (Exception e) {
                Logger.error("Error in server response", jsonObject.toString()
                        + " :: " + e.getMessage());

                L.alert(activity, "Server Error. No message from server!");
            }
        }

        return false;
    }

    public static String getResponseMessage(Activity activity,
                                            JSONObject jsonObject) {
        return jsonObject.optString("message",
                "Server Error. No message from server!");
    }

    public static List<String> stripDigits(final CharSequence input) {
        final StringBuilder sb = new StringBuilder(input.length());
        Set<String> digits = new HashSet<String>();
        String lines[] = input.toString().split("\\r?\\n");

        for (String line : lines) {

            boolean isPrefixExist = true;
            for (String prefix : AMOUNT_PREFIX) {
                if (line.toLowerCase(Locale.US).contains(
                        prefix.toLowerCase(Locale.US))) {
                    isPrefixExist = true;
                    break;
                }
            }
            if (isPrefixExist) {
                String[] splited = line.toString().trim().split("\\s+");
                for (String word : splited) {
                    boolean isDigit = false;
                    for (int i = 0; i < word.length(); i++) {
                        final char c = word.charAt(i);
                        if ((c > 47 && c < 58)) {
                            sb.append(c);
                            isDigit = true;
                        } else if (c == 46
                                && !(sb.toString().length() > 0 && !isDigit)) {
                            if (!isDigit) {
                                sb.append("0");
                            }
                            sb.append(c);
                        }
                    }
                    if (isDigit) {
                        String num = sb.toString().trim();

                        if (num.endsWith(".")) {
                            num += "0";
                        }

                        try {

                            double numInDouble = Double.parseDouble(num);
                            // digits.add(String.valueOf(numInDouble));

                            // convert this code upto two decimal place
                            // DecimalFormat df2 = new DecimalFormat("###.##");
                            // digits.add(String.valueOf(Double.valueOf(df2.format(numInDouble))));

                            numInDouble = Math.round(numInDouble * 100);
                            numInDouble = numInDouble / 100;

                            digits.add(String.valueOf(numInDouble));

                        } catch (Exception e) {
                        }

                        // System.out.println("Digiti Found :: "+sb.toString());
                    }
                    sb.setLength(0);
                }
            }

        }
        return new ArrayList<String>(digits);
    }

    public static boolean validateJavaDate(String strDate, String formatDate) {
        /* Check if date is 'null' */
        if (strDate.trim().equals("")) {
            return true;
        }
        /* Date is not 'null' */
        else {
			/*
			 * Set preferred date format, For example MM-dd-yyyy,
			 * MM.dd.yyyy,dd.MM.yyyy etc.
			 */

            // formatDate = "MM/dd/yyyy" type

            SimpleDateFormat sdfrmt = new SimpleDateFormat(formatDate);
            sdfrmt.setLenient(false);
			/* Create Date object */
            Date javaDate = null;
			/* parse the string into date form */
            try {
                javaDate = sdfrmt.parse(strDate);
                // System.out.println("Date after validation: " + javaDate);
            }
			/* Date format is invalid */ catch (ParseException e) {
                // System.out.println("The date you provided is in an "
                // + "invalid date format.");
                return false;
            }
			/* Return 'true' - since date is in valid format */
            return true;
        }
    }

    public static List<String> stripDate(final String input) {

        List<String> dates = new ArrayList<String>();

        String[] lines = input.split(System.getProperty("line.separator"));

        for (int i = 0; i < lines.length; i++) {

            String[] datess = lines[i].split(" ");

            for (int j = 0; j < datess.length; j++) {

                if (!datess[j].equals("")) {
                    if (validateJavaDate(datess[j], "mm/dd/yyyy")) {
                        // Date date = new Date();
                        // String DATE_FORMAT = "MM/dd/yyyy";
                        // SimpleDateFormat sdf = new
                        // SimpleDateFormat(DATE_FORMAT);
                        // System.out.println("Today is " +
                        // sdf.format(datess[j]));

                        dates.add(datess[j]);

                    } /*
					 * else if (validateJavaDate(datess[j], "MM-dd-yyyy")) {
					 * dates.add(datess[j]); }
					 */
                }

            }

        }

        return dates;

    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2
                .get(Calendar.DAY_OF_YEAR);

        return sameDay;
    }

    public static AccountBean getToAccountBean(AccountBean from, AccountBean to) {

        if (from != null && to != null) {
            if (from.getCompanyName() != null) {
                to.setCompanyName(from.getCompanyName());
            }

            if (from.getCreatedAt() != null) {
                to.setCreatedAt(from.getCreatedAt());
            }

            if (from.getEmail() != null) {
                to.setEmail(from.getEmail());
            }

            if (from.getFirstName() != null) {
                to.setFirstName(from.getFirstName());
            }

            if (from.getID() > 0) {
                to.setID(from.getID());
            }

            to.setIsSync(from.getIsSync());

            if (from.getLastName() != null) {
                to.setLastName(from.getLastName());
            }

            if (from.getPassword() != null) {
                to.setPassword(from.getPassword());
            }

            if (from.getPhoneno() != null) {
                to.setPhoneno(from.getPhoneno());
            }

            if (from.getServerId() > 0) {
                to.setServerId(from.getServerId());
            }

            if (from.getUpdatedAt() != null) {
                to.setUpdatedAt(from.getUpdatedAt());
            }
        }

        return to;
    }

    public static ReceiptBean getToReceiptBean(ReceiptBean from, ReceiptBean to) {

        if (from != null && to != null) {
            if (from.getUserId() > 0) {
                to.setUserId(from.getUserId());
            }

            if (from.getAmount() != 0d) {
                to.setAmount(from.getAmount());
            }

            if (from.getCategoryName() != null) {
                to.setCategoryName(from.getCategoryName());
            }

            if (from.getComment() != null) {
                to.setComment(from.getComment());
            }

            if (from.getCreatedAt() != null) {
                to.setCreatedAt(from.getCreatedAt());
            }

            if (from.getDate() != null) {
                to.setDate(from.getDate());
            }

            to.setIsSync(from.getIsSync());

            if (from.getImageBytes() != null) {
                to.setImageBytes(from.getImageBytes());
            }

            if (from.getName() != null) {
                to.setName(from.getName());
            }

            if (from.getReceiptID() > 0) {
                to.setReceiptID(from.getReceiptID());
            }

            if (from.getServerId() > 0) {
                to.setServerId(from.getServerId());
            }

            if (from.getUpdateddAt() != null) {
                to.setUpdateddAt(from.getUpdateddAt());
            }

            if (from.getServerImgPath() != null) {
                to.setServerImgPath(from.getServerImgPath());
            }

            if (from.getTip() > 0d) {
                to.setTip(from.getTip());
            }

            // if (from.getCategoryServerId() > 0) {
            // to.setCategoryServerId(from.getCategoryServerId());
            // }
            //
            // if (from.getAddressServerId() > 0) {
            // to.setAddressServerId(from.getAddressServerId());
            // }

            to.setIsDelete(from.getIsDelete());

        }

        return to;
    }

    public static ReceiptBean getReceiptBean(JSONObject response) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        Gson gson = builder.create();

        JSONObject dataJson = response.optJSONObject("data");

        if (dataJson != null) {
            ReceiptBean receiptBean = gson.fromJson(dataJson.toString(),
                    ReceiptBean.class);
            return receiptBean;
        }

        return null;
    }

    public static ReceiptHelper getReceiptBeans(JSONObject response) {
        if (response != null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
            Gson gson = builder.create();

            ReceiptHelper receiptHelper = gson.fromJson(response.toString(),
                    ReceiptHelper.class);
            return receiptHelper;
        }

        return null;
    }
}