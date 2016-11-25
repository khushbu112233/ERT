package com.aip.dailyestimation.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.ImageLoader;
import com.aip.dailyestimation.common.util.ImageUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.ShowBIgImage;

import java.util.List;

public class ReceiptAdapter extends BaseAdapter {

    List<ReceiptBean> mReceiptBeans;

    Context mContext;

    ImageUtil mImageUtil;

    ImageLoader imageLoader;

    DatabaseService mDatabaseService;

    public ReceiptAdapter(Context context, List<ReceiptBean> receiptBeans) {
        this.mContext = context;
        this.mReceiptBeans = receiptBeans;
        mImageUtil = new ImageUtil();
        mDatabaseService = DatabaseService.getInstance(mContext);
        imageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getCount() {
        return mReceiptBeans.size();
    }

    @Override
    public ReceiptBean getItem(int position) {
        return mReceiptBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mReceiptBeans.get(position).getReceiptID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final int Selected = position;

        Holder holder = null;
        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(R.layout.item_receipt,
                    null, false);
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        final ImageView BigImageview = holder.imgPhoto;

        final ReceiptBean bean = getItem(position);
        holder.txtReceiptName.setText(bean.getName());
        holder.txtAmount.setText(Util.getAmount(bean.getAmount() + bean.getTip()));
        holder.txtCategory.setText(bean.getCategoryName());
        try {
            holder.txtDate.setText(Util.getLongToDate(bean.getDate().getTime(),
                    IConstants.DATE_FORMAT));

            // if (bean.getIsSync() == 1) { // && bean.getServerImgPath() !=
            // null // && !bean.getServerImgPath().equals("")
            // Picasso.with(mContext).load(bean.getServerImgPath())
            // .into(holder.imgPhoto);
            // } else {
            // // imageLoader.DisplayImage(null, holder.imgPhoto,
            // // bean.getImageBytes());
            // holder.imgPhoto.setImageBitmap(ImageUtil.getBitamp(bean.getImageBytes()));
            // }

			/*if (bean.getServerImgPath().trim().equals("")) {
                holder.imgPhoto
						.setImageResource(R.drawable.dashboard_receipt_selector);
				holder.prog.setVisibility(View.INVISIBLE);
			} else {
				holder.prog.setVisibility(View.VISIBLE);
				Picasso.with(mContext).load(bean.getServerImgPath())
						.into(holder.imgPhoto);
			}*/

            // else if (bean.getImageBytes() != null)
            // holder.imgPhoto.setImageBitmap(ImageUtil.getBitamp(bean
            // .getImageBytes()));

            // if (bean.getServerImgPath() != null)
            // Picasso.with(mContext)
            // .load(bean.getServerImgPath())
            // .into(holder.imgPhoto);
            // else if (bean.getImageBytes() != null)
            // holder.imgPhoto.setImageBitmap(ImageUtil.getBitamp(bean
            // .getImageBytes()));

            // ReceiptBean bean2 =
            // mDatabaseService.getReceipt(bean.getReceiptID());
            // imageLoader.DisplayImage(null, holder.imgPhoto,
            // bean2.getImageBytes());

            // holder.imgPhoto.setImageBitmap(ImageUtil.getBitamp(bean.getImageBytes()));
        } catch (Exception e) {
        }

        holder.imgPhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    // Bitmap bitmap = ((BitmapDrawable) BigImageview
                    // .getDrawable()).getBitmap();
                    // ImageView ShoBigImage = new ImageView(mContext);
                    // ShoBigImage.setImageBitmap(bitmap);

                    // Toast.makeText(mContext, "Item clicked " + Selected,
                    // Toast.LENGTH_SHORT).show();

                    Log.v("image", bean.getServerImgPath() + "");
                    Intent mIntent = new Intent(mContext, ShowBIgImage.class);
                    mIntent.putExtra("BigImagePath", bean.getServerImgPath());
                    mIntent.putExtra("ReceiptId", bean.getReceiptID());
                    mContext.startActivity(mIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        row.setId(bean.getReceiptID());
        return row;
    }

    static class Holder {
        final ImageView imgPhoto;
        final TextView txtReceiptName;
        final TextView txtCategory, txtAmount, txtDate;
        final ProgressBar prog;

        public Holder(View v) {
            imgPhoto = (ImageView) v.findViewById(R.id.item_img);
            txtReceiptName = (TextView) v.findViewById(R.id.item_txtName);
            txtAmount = (TextView) v.findViewById(R.id.item_txtAmount);
            txtDate = (TextView) v.findViewById(R.id.item_txtDate);
            txtCategory = (TextView) v.findViewById(R.id.item_txtCategory);
            prog = (ProgressBar) v.findViewById(R.id.progBar);
        }
    }
}