package com.aip.dailyestimation.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.SplashActivity;

/**
 * This class contain alert message helper
 *
 * @author aipxperts
 */
public class L {

    public static interface IL {
        public void onSuccess();

        public void onCancel();
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void alert(Context context, String msg) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setMessage(Html.fromHtml(msg));
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                        dialog.dismiss();
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void alertLogout(final Context context, String msg) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setMessage(Html.fromHtml(msg));
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        DatabaseService mDatabaseService = DatabaseService.getInstance(context);
//                    mDatabaseService.getAccount();
//                    mDatabaseService.deleteCurrentAccount();
                        mDatabaseService.clearPreference();
                        Intent intent = new Intent(context, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finishAffinity();
                        System.runFinalizersOnExit(true);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void alert(Context context, String title, String msg) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(Html.fromHtml(msg));
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                        dialog.dismiss();
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void alert(Context context, String title, String msg, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void alert(Context context, String msg, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void confirmDialog(Context context, String msg, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void confirmDialog(Context context, String title, String msg, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void confirmDialog(Context context, String msg, String positiveBtnText,
                                     String negativeBtnText, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void confirmDialog(Context context, String title, String msg, String positiveBtnText,
                                     String negativeBtnText, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void showAlertDialogOKCancelWithView(Activity context, final View view, final IL il, String positiveBtnText,
                                                       String negativeBtnText) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going to start.
     * @param title   - Title String that represents alert box title
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void showAlertDialogAction(Activity context, String msg, final IL il, String positiveBtnText,
                                             String negativeBtnText) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setTitle(Html.fromHtml("Receipt Tracker!"));
            alertDialogBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private static AlertDialog.Builder getBuilder(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder;
    }

    public static ProgressDialog progress(Context context, String msg) throws Exception {
        try {
            final ProgressDialog mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mProgressDialog.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mProgressDialog.dismiss();
                    }
                    return false;
                }
            });

            return mProgressDialog;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void ok(Context context, String msg) {

        Toast mytoast = new Toast(context);

        View view = getToastLayout(context, msg, "#0066CC", android.R.drawable.presence_online);
        mytoast.setView(view);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }

    public static void info(Context context, String msg) {

        Toast mytoast = new Toast(context);

        View view = getToastLayout(context, msg, "#666666", android.R.drawable.presence_away);
        mytoast.setView(view);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }

    public static void error(Context context, String msg) {

        Toast mytoast = new Toast(context);

        View view = getToastLayout(context, msg, "#FF0000", android.R.drawable.presence_busy);
        mytoast.setView(view);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }


    @SuppressLint("NewApi")
    private static View getToastLayout(Context context, String msg, String bgColor, int img) {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams imgParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(context);
        linearLayout.addView(imageView, imgParam);
        imageView.setPadding(20, 20, 20, 20);

        imgParam.gravity = Gravity.CENTER_VERTICAL;
        TextView text = new TextView(context);
        text.setPadding(20, 20, 40, 20);
        text.setText(msg);
        text.setTextSize(15);
        text.setTextColor(Color.WHITE);
        linearLayout.addView(text, imgParam);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(8);

        imageView.setImageResource(img);
        shape.setColor(Color.parseColor(bgColor));
        linearLayout.setBackground(shape);
        return linearLayout;
    }
}
