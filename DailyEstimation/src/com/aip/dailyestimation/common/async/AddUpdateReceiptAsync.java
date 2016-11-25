package com.aip.dailyestimation.common.async;

import android.app.Activity;
import android.util.Log;

import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ImageUtil;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.WebServiceReader;
import com.loopj.android.http.RequestParams;

import java.io.File;

public class AddUpdateReceiptAsync {

    private Activity activity;

    private IResultListner iResultListner;

    private ReceiptBean receiptBean;

    public AddUpdateReceiptAsync(Activity activity, ReceiptBean receiptBean,
                                 IResultListner iResultListner) {
        this.activity = activity;
        this.iResultListner = iResultListner;
        this.receiptBean = receiptBean;
    }

    public void doServerCall() {
        DatabaseService mDatabaseService = DatabaseService
                .getInstance(activity);

        WebServiceReader mWebServiceReader = new WebServiceReader();

        RequestParams param = new RequestParams();
        param.put(IConstants.KEY_USER_ID, mDatabaseService.getAccount()
                .getServerId());
        if (receiptBean.getIsSync() == 0) {
            try {
                // JSONObject param = new JSONObject();
                param.put(IConstants.KEY_LOCAL_RECEIPT_ID,
                        receiptBean.getReceiptID());
                param.put(IConstants.KEY_RECEIPT_NAME, receiptBean.getName());
                param.put(IConstants.KEY_RECEIPT_CAT,
                        receiptBean.getCategoryName());
                param.put(IConstants.KEY_AMOUNT, receiptBean.getAmount());
                param.put(IConstants.KEY_DATE, receiptBean.getDate().getTime());
                param.put(IConstants.KEY_TIP, receiptBean.getTip());
                param.put(IConstants.KEY_COMMENT, receiptBean.getComment());
                param.put(IConstants.KEY_USER_ID, receiptBean.getUserId());
                param.put(IConstants.KEY_IS_DELETE, receiptBean.getIsDelete());
                param.put(IConstants.KEY_CREATED_AT, receiptBean.getCreatedAt()
                        .getTime());
                param.put(IConstants.KEY_UPDATED_AT, receiptBean
                        .getUpdateddAt().getTime());
                param.put(IConstants.KEY_CATEGORY_SERVER_ID, receiptBean
                        .getCategoryServerId());
                param.put(IConstants.KEY_ADDRESS_SERVER_ID, receiptBean
                        .getAddressServerId());
                param.put(IConstants.KEY_DEVICE,
                        IConstants.KEY_DEVICE_VALUE);
                Log.e("AddReceipt ", "!!! " + receiptBean.getDate().getTime());
                if (receiptBean.getServerId() != -1) {
                    param.put(IConstants.KEY_ID, receiptBean.getServerId());
                } else
                    param.put(IConstants.KEY_ID, "");

                String auth = SharedPrefrenceUtil.getPrefrence(activity, IConstants.KEY_AUTH, "");
                param.put(IConstants.KEY_AUTH, auth);

                // dataJson.put("data["+i+"]", param);
                if (receiptBean.getImageBytes() == null) {
                    param.put("receiptImage", "");
                } else {

                    File file = ImageUtil.getFileFromByte(receiptBean
                            .getImageBytes());
                    if (file != null)
                        param.put("receiptImage", file);
                    else
                        param.put("receiptImage", "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (receiptBean.getServerId() > 0) {
            // TODO update receipt
            mWebServiceReader.doUpload(activity, true,
                    mWebServiceReader.getUpdateReceiptUrl(), param,
                    iResultListner);

        } else {
            // TODO Add receipt
            mWebServiceReader
                    .doUpload(activity, true,
                            mWebServiceReader.getAddReceiptUrl(), param,
                            iResultListner);

            Log.e("AddReceipt ", "@@@ " + param.toString());
        }
    }
}