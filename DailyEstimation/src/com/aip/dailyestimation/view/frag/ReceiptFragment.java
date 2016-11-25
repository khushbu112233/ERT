package com.aip.dailyestimation.view.frag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.adapter.ReceiptAdapter;
import com.aip.dailyestimation.common.async.DeleteReceiptAsync;
import com.aip.dailyestimation.common.async.ReadAllReceiptAsync;
import com.aip.dailyestimation.common.util.FunctionHelper;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.customdialog.CustomAlertDialog;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnOptionSelcted;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.FilterService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.aip.dailyestimation.vo.FilterVO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class ReceiptFragment extends CoreFragment {

    View rootView;

    Timer timer;

    private String resultUrl = "result.txt";

    TextView txtNoReceipt;
    ListView listReceipt;

    DatabaseService mDatabaseService;

    FilterService mFilterService;

    FunctionHelper mFunctionHelper;

    Dialog dialog;

    int ReceiptID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_receipt, container,
                    false);
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        ((MainActivity) getActivity()).setHeaderTitle(R.id.receipt_fragment);

        init();

        SharedPreferences sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

        if (mDatabaseService.getAccount().getUserType().equals("free")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.bringToFront();
        } else {
            mAdView.setVisibility(View.INVISIBLE);
        }

        Log.d("ReceiptCount", "Total: " + mDatabaseService.getReceiptCount());

        if (Util.isNetAvailable(getActivity())) {

            // ReadAllAddressAsync addressAsync = new ReadAllAddressAsync(
            // getActivity(), new IResultListner() {
            //
            // @Override
            // public void result(Object result, boolean isSuccess) {
            //
            // JSONObject object = (JSONObject) result;
            // try {
            // Log.d("ReadAllAddressAsync",
            // "" + object.toString(4));
            // } catch (JSONException e1) {
            // e1.printStackTrace();
            // }
            //
            // try {
            // if (object.getString("code").equals("200")) {
            //
            // JSONArray array = object
            // .getJSONArray("data");
            //
            // mDatabaseService
            // .deleteAllAddress(mDatabaseService
            // .getCurrentUserId());
            //
            // for (int Counter = 0; Counter < array
            // .length(); Counter++) {
            // JSONObject obj = array
            // .getJSONObject(Counter);
            //
            // AddressBean addressBean = new AddressBean();
            // addressBean.setAddressServerId(obj
            // .getInt("id"));
            // addressBean.setServerId(obj
            // .getInt("userId"));
            // addressBean.setAddressName(obj
            // .getString("addressBookName"));
            //
            // mDatabaseService.insertUpdateAddress(
            // addressBean, true);
            // }
            //
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // }
            // });
            // addressAsync.execute();
            //
            // ReadAllCategoryAsync categoryAsync = new ReadAllCategoryAsync(
            // getActivity(), new IResultListner() {
            //
            // @Override
            // public void result(Object result, boolean isSuccess) {
            //
            // JSONObject object = (JSONObject) result;
            // try {
            // Log.d("ReadAllCategoryAsync",
            // "" + object.toString(4));
            // } catch (JSONException e1) {
            // e1.printStackTrace();
            // }
            //
            // try {
            // if (object.getString("code").equals("200")) {
            //
            // JSONArray array = object
            // .getJSONArray("data");
            //
            // mDatabaseService
            // .deleteAllCategory(mDatabaseService
            // .getCurrentUserId());
            //
            // for (int Counter = 0; Counter < array
            // .length(); Counter++) {
            // JSONObject obj = array
            // .getJSONObject(Counter);
            //
            // CategoryBean categoryBean = new CategoryBean();
            //
            // categoryBean.setCategoryServerId(obj
            // .getInt("id"));
            // categoryBean.setServerId(obj
            // .getInt("userId"));
            // categoryBean.setCategoryName(obj
            // .getString("categoryName"));
            //
            // Log.d("CategoryBean: "
            // + categoryBean
            // .getCategoryName(),
            // "ServerId: "
            // + categoryBean
            // .getServerId());
            //
            // mDatabaseService.insertUpdateCategory(
            // categoryBean, true);
            // }
            //
            // }
            //
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // }
            // });
            // categoryAsync.execute();

            ReadAllReceiptAsync allReceiptAsync = new ReadAllReceiptAsync(
                    getActivity(), new IResultListner() {

                @Override
                public void result(Object result, boolean isSuccess) {
                    JSONObject object = (JSONObject) result;
                    try {
                        Log.d("ReadAllReceiptAsync",
                                "" + object.toString(4));

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
                                    .deleteAllReceipt(mDatabaseService
                                            .getCurrentUserId());

                            for (int Counter = 0; Counter < array
                                    .length(); Counter++) {

                                JSONObject obj = array
                                        .getJSONObject(Counter);

                                ReceiptBean receiptBean = new ReceiptBean();
                                receiptBean.setServerId(obj
                                        .getInt("id"));

                                try {
                                    receiptBean.setName(obj
                                            .getJSONObject(
                                                    "addressBook")
                                            .getString(
                                                    "addressBookName"));
                                } catch (Exception e) {
                                    receiptBean.setName(obj
                                            .getString("receiptName"));
                                }

                                try {
                                    receiptBean.setCategoryName(obj
                                            .getJSONObject("category")
                                            .getString("categoryName"));
                                } catch (Exception e) {
                                    receiptBean.setCategoryName(obj
                                            .optString("receiptCategory"));
                                }

                                receiptBean.setAmount(obj
                                        .getDouble("amount"));
                                receiptBean.setTip(obj.getDouble("tip"));
                                receiptBean.setComment(obj
                                        .getString("comment"));
                                receiptBean.setDate(new Date(obj
                                        .getLong("date")));
                                receiptBean.setCreatedAt(new Date(obj
                                        .getLong("createdDate")));
                                receiptBean.setUpdateddAt(new Date(obj
                                        .getLong("modifiedDate")));
                                receiptBean.setUserId(obj
                                        .getInt("userId"));
                                receiptBean.setServerImgPath(obj
                                        .getString("receiptImage")
                                        .trim());
//										receiptBean.setReceiptID(obj
//												.getInt("localReceiptId"));
                                receiptBean.setIsDelete(obj
                                        .getInt("isDelete"));
                                receiptBean.setCategoryServerId(obj
                                        .getInt("categoryId"));
                                receiptBean.setAddressServerId(obj
                                        .getInt("addressBookId"));

                                // mDatabaseService
                                // .insertUpdateReceipt(receiptBean);
                                mDatabaseService
                                        .insertReceipt(receiptBean);

                            }

                            // refrehList();
                            receiptAdapter = new ReceiptAdapter(
                                    getActivity(), getReceipts());
                            listReceipt.setAdapter(receiptAdapter);
                            receiptAdapter.notifyDataSetChanged();
                            handler.sendEmptyMessage(2);

                        } else {

                            if (object.getString("code").equals("201")) {

                                mDatabaseService
                                        .deleteAllReceipt(mDatabaseService
                                                .getCurrentUserId());

                                listReceipt.setEmptyView(txtNoReceipt);

										/*if (!(mDatabaseService
                                                .getReceiptCount() > 0)) {
											L.confirmDialog(
													getActivity(),
													getResources()
															.getString(
																	R.string.no_receipt_added),
													new IL() {

														@Override
														public void onSuccess() {
															// AddReceiptFragment
															// addReceiptFragment
															// =
															// new
															// AddReceiptFragment();
															// switchFragment(R.id.main_content,
															// addReceiptFragment);

															if (Util.isNetAvailable(getActivity())) {
																next(-1);
															} else {
																L.alert(getActivity(),
																		getResources()
																				.getString(
																						R.string.msg_internet_error));
															}

														}

														@Override
														public void onCancel() {

														}
													});
										}*/
                            } else {
                                L.alert(getActivity(),
                                        object.getString("message"));
                            }

                        }

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            allReceiptAsync.execute();

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.isNetAvailable(getActivity())) {
            // refrehList();
            // receiptAdapter.notifyDataSetChanged();
        } else {
            listReceipt.setEmptyView(txtNoReceipt);
            L.alert(getActivity(),
                    getResources().getString(R.string.msg_internet_error));
        }

        // try {
        // // start read data from server
        //
        // timer = new Timer();
        // timer.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // RefreshAdapter();
        // }
        //
        // private void RefreshAdapter() {
        // // TODO Auto-generated method stub
        // // do your thing here, such as execute AsyncTask or send
        // // data to
        // // server
        // mHandler.obtainMessage(1).sendToTarget();
        // ServiceHandler.startExportService(getActivity());
        // Log.d("Timer Work", "Timer Run");
        // }
        // }, 1, TimeUnit.MINUTES.toMillis(1));
        //
        // // TimeUnit.MINUTES.toMillis(minit)
        // // convert 1 minit to milisecconds
        //
        // // stop read data from server
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            refrehList();
            receiptAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // try {
        // timer.cancel();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

    }

    private void init() {

        txtNoReceipt = (TextView) rootView.findViewById(R.id.txtNoReceiptFound);
        listReceipt = (ListView) rootView.findViewById(R.id.list_receipts);

        mDatabaseService = DatabaseService.getInstance(getActivity());
        mFilterService = FilterService.getInstance(getActivity());

        mFunctionHelper = FunctionHelper.getFunctionHelper(getActivity());
        getActivity().findViewById(R.id.actBar_rightText).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (Util.isNetAvailable(getActivity())) {
                            next(-1);
                        } else {
                            L.alert(getActivity(),
                                    getResources().getString(
                                            R.string.msg_internet_error));
                        }
                    }
                });

        // listReceipt.setEmptyView(txtNoReceipt);

        if (Util.isNetAvailable(getActivity())) {
            // refrehList();
            receiptAdapter = new ReceiptAdapter(getActivity(), getReceipts());
            // listReceipt.setAdapter(receiptAdapter);
        } else {
            // L.alert(getActivity(),
            // getResources().getString(R.string.msg_internet_error));
        }

        listReceipt.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                final int Selected = position;

                try {
                    final int receiptId = v.getId();
                    ReceiptID = receiptId;
                    final ReceiptBean receiptBean = mDatabaseService
                            .getReceipt(receiptId);
                    view(receiptBean.getReceiptID());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        listReceipt.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View v,
                                           int position, long id) {

                final int receiptId = v.getId();
                final ReceiptBean receiptBean = mDatabaseService
                        .getReceipt(receiptId);
                final String categoryName = ((TextView) v
                        .findViewById(R.id.item_txtName)).getText().toString();
                CustomAlertDialog.openOption(getActivity(), categoryName,
                        new IOnOptionSelcted() {

                            @Override
                            public void onOptionSelected(int id) {
                                switch (id) {
                                    case R.id.update:
                                        onEdit(receiptBean);
                                        break;
                                    case R.id.delete:
                                        onDelete(receiptBean);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                return true;
            }
        });
    }

    private void next(int id) {
        if (id != -1) {
            AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(IConstants.ARG_ID, id);
            addReceiptFragment.setArguments(bundle);
            switchFragment(R.id.main_content, addReceiptFragment);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Your Choice").setItems(
                    R.array.Select_choice,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item

                            if (which == 0) {
                                ((MainActivity) getActivity()).startScan();
                            } else if (which == 1) {
                                ((MainActivity) getActivity()).submitForm();
                            } else {
                                // cancel dialog
                            }

                        }
                    });

            builder.create();
            builder.show();

        }

    }

    private void view(int id) {

        // use below code for only show receipt

        // if (id != -1) {
        // ViewReceiptFragment viewReceiptFragment = new ViewReceiptFragment();
        // Bundle bundle = new Bundle();
        // bundle.putInt(IConstants.ARG_ID, id);
        // viewReceiptFragment.setArguments(bundle);
        // switchFragment(R.id.main_content, viewReceiptFragment);
        // }

        if (Util.isNetAvailable(getActivity())) {

            ReceiptBean receiptBean = mDatabaseService.getReceipt(ReceiptID);

            if (receiptBean != null) {
                AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(IConstants.ARG_ID, receiptBean.getReceiptID());
                addReceiptFragment.setArguments(bundle);
                switchFragment(R.id.main_content, addReceiptFragment);
            }
        } else {
            L.alert(getActivity(),
                    getResources().getString(R.string.msg_internet_error));
        }

    }

    ReceiptAdapter receiptAdapter;

    private void refrehList() {

		/*
         * Thread t = new Thread(new Runnable() {
		 *
		 * @Override public void run() { try { handler.sendEmptyMessage(0);
		 * receiptAdapter = new ReceiptAdapter(getActivity(), getReceipts());
		 * handler.sendEmptyMessage(2); } catch (Exception e) {
		 * e.printStackTrace(); }finally { handler.sendEmptyMessage(1); } } });
		 *
		 * t.start();
		 */

        try {
            // handler.sendEmptyMessage(0);
            receiptAdapter = new ReceiptAdapter(getActivity(), getReceipts());
            handler.sendEmptyMessage(2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // handler.sendEmptyMessage(1);
        }

    }

    private void onDelete(final ReceiptBean receiptBean) {
        if (receiptBean == null)
            return;

        L.confirmDialog(
                getActivity(),
                getResources().getString(R.string.msg_receipt_delete,
                        receiptBean.getName()), new L.IL() {

                    @Override
                    public void onSuccess() {
                        try {
                            if (receiptBean.getServerId() > 0) {
                                if (Util.isNetAvailable(getActivity()))
                                    onlineDelete(receiptBean);
                                else {
                                    receiptBean.setIsDelete(1);
                                    mDatabaseService
                                            .insertUpdateReceipt(receiptBean);
                                    refrehList();
                                }
                            } else {
                                mDatabaseService.deleteReceipt(receiptBean
                                        .getReceiptID());
                                refrehList();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    public void onEdit(ReceiptBean receiptBean) {
        if (receiptBean != null) {
            next(receiptBean.getReceiptID());
        }
    }

    private List<ReceiptBean> getReceipts() {
        List<ReceiptBean> receiptBeans = null;

        String SearchByAddress = SharedPrefrenceUtil.getPrefrence(
                getActivity(), "SearchByAddress", "false");

        if (SearchByAddress.equals("true")) {
            SharedPrefrenceUtil.setPrefrence(getActivity(), "SearchByAddress",
                    "false");
            if (getAddress() != null) {
                receiptBeans = mFilterService
                        .getAllReceiptsByAddress(getAddress());
                txtNoReceipt.setText("No receipt found in this " + getAddress()
                        + " address");
            }
        } else if (getCategory() != null) {
            receiptBeans = mFilterService.getAllReceipts(getCategory());
            txtNoReceipt.setText("No receipt found in this " + getCategory()
                    + " category");
        } else if (getFilter() != null) {
            receiptBeans = mFilterService.getAllReceipts(getFilter());
            txtNoReceipt.setText("No receipt found for your search");
        } else {
            receiptBeans = mDatabaseService.getAllLocalReceipts();
        }

        if (receiptBeans == null)
            receiptBeans = new ArrayList<ReceiptBean>();
        Log.d("SetAdapter", "SetAdapter");

        return receiptBeans;
    }

    private String getCategory() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_ID)) {
            return getArguments().getString(IConstants.ARG_ID);
        }

        return null;
    }

    private String getAddress() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_ID)) {
            return getArguments().getString(IConstants.ARG_ID);
        }

        return null;
    }

    private FilterVO getFilter() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_FILTER)) {
            return (FilterVO) getArguments().getSerializable(
                    IConstants.ARG_FILTER);
        }

        return null;
    }

    private void onlineDelete(final ReceiptBean receiptBean) {
        DeleteReceiptAsync deleteReceiptAsync = new DeleteReceiptAsync(
                getActivity(), new IResultListner() {

            @Override
            public void result(Object result, boolean isSuccess) {
                if (isSuccess) {
                    mDatabaseService.deleteReceipt(receiptBean
                            .getReceiptID());
                } else {
                    receiptBean.setIsDelete(1);
                    mDatabaseService.insertUpdateReceipt(receiptBean);
                }
                refrehList();
            }
        });

        deleteReceiptAsync.execute(receiptBean.getServerId());
    }

    Handler handler = new Handler(Looper.getMainLooper(), new Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    mFunctionHelper.disableUserInteration();
                    break;
                case 1:
                    mFunctionHelper.enableUserInteration();
                    break;

                case 2:
                    listReceipt.setAdapter(receiptAdapter);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

}