package com.aip.dailyestimation.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.common.util.ReadOcrPackage;

import java.util.List;

public class ReadOcrPackageAdapter extends BaseAdapter {

	Context mContext;

	List<ReadOcrPackage> mReadOcrPackage;

	public ReadOcrPackageAdapter(Context context,
			List<ReadOcrPackage> readocrpackage) {
		this.mContext = context;
		this.mReadOcrPackage = readocrpackage;
	}

	@Override
	public int getCount() {
		return mReadOcrPackage.size();
	}

	@Override
	public Object getItem(int position) {
		return mReadOcrPackage.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		final int Selected = position;

		Holder holder = null;
		if (row == null) {
			row = LayoutInflater.from(mContext).inflate(R.layout.item_ocr,
					null, false);
			holder = new Holder(row);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}

		ReadOcrPackage ocrPackage = mReadOcrPackage.get(position);
		
		holder.mNoOfReceipt.setText("No Off Receipt " + ocrPackage.getNoOfReceipt());
		holder.mAndroidPrice.setText("$" + ocrPackage.getAndroidPrice());
		holder.mStatus.setText("BUY");

		return row;
	}

	static class Holder {
		// final TextView mId;
		// final TextView mTitle;
		final TextView mNoOfReceipt;
		// final TextView mIosPrice;
		final TextView mAndroidPrice;
		final TextView mStatus;

		public Holder(View v) {

			mNoOfReceipt = (TextView) v.findViewById(R.id.txtNoOfReceipt);
			mAndroidPrice = (TextView) v.findViewById(R.id.txtPriceReceipt);
			mStatus = (TextView) v.findViewById(R.id.txtBuyOcr);

		}
	}
}
