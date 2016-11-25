package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.ImageUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.service.DatabaseService;
import com.squareup.picasso.Picasso;

public class ShowBIgImage extends Activity {

	DatabaseService mDatabaseService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_big_image_layout);

		mDatabaseService = DatabaseService.getInstance(ShowBIgImage.this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layRel);
		ImageView BigImage = (ImageView) findViewById(R.id.BigImageView);

//		AdView mAdView = (AdView) findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);

		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		ReceiptBean mReceiptBean = mDatabaseService.getReceipt(getIntent()
				.getIntExtra("ReceiptId", 0));

		if (mReceiptBean != null) {

//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//					R.drawable.receipt_active);
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//			byte[] bitMapData = stream.toByteArray();

//			if (Arrays.equals(bitMapData, mReceiptBean.getImageBytes())) {
//				finish();
//			} else {
				try {
					if (mReceiptBean.getImageBytes() != null){
						BigImage.setImageBitmap(ImageUtil
								.getBitamp(mReceiptBean.getImageBytes()));
					}else if (mReceiptBean.getServerImgPath() != null
							&& !mReceiptBean.getServerImgPath().equals("")  && Util.isNetAvailable(ShowBIgImage.this)){
						Picasso.with(ShowBIgImage.this)
						.load(mReceiptBean.getServerImgPath())
						.into(BigImage);
					}else{
						finish();
					}
						
				} catch (Exception e) {
					e.printStackTrace();
					finish();
				}
			}else{
				finish();
			}

//		} else {
//			finish();
//		}

		// try {
		// Log.d("BigImagePath", "BigImagePath: "
		// + getIntent().getStringExtra("BigImagePath"));
		// if (!getIntent().getStringExtra("BigImagePath").equals("")) {
		// Picasso.with(ShowBIgImage.this)
		// .load(getIntent().getStringExtra("BigImagePath"))
		// .into(BigImage);
		//
		// } else {
		//
		// // DatabaseService mDatabaseService = DatabaseService
		// // .getInstance(getApplicationContext());
		// // ReceiptBean bean = mDatabaseService.getReceipt(getIntent()
		// // .getIntExtra("ReceiptId", 1));
		// //
		// // ImageLoader imageLoader;
		// // imageLoader = new ImageLoader(ShowBIgImage.this);
		// // imageLoader.DisplayImage(null, BigImage, bean.getImageBytes());
		// finish();
		// }
		//
		// } catch (Exception e) {
		// finish();
		// BigImage.setImageResource(R.drawable.receipt_active);
		// e.printStackTrace();
		// }

	}
}
