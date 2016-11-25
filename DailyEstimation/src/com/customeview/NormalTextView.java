package com.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aip.dailyestimation.common.util.IConstants;

public class NormalTextView extends TextView {

	public NormalTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public NormalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NormalTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+IConstants.FONT_GEN_NAME);
		setTypeface(tf);
	}

}
