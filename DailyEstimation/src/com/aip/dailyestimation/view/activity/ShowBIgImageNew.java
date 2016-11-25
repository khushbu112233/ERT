package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.common.util.BigImage;
import com.aip.dailyestimation.service.DatabaseService;

public class ShowBIgImageNew extends Activity {

    DatabaseService mDatabaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_big_image_layout);

        mDatabaseService = DatabaseService.getInstance(ShowBIgImageNew.this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layRel);
        ImageView mBigImage = (ImageView) findViewById(R.id.BigImageView);

        mBigImage.setImageBitmap(BigImage.getImageBitmap());

//		AdView mAdView = (AdView) findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);

        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

    }
}
