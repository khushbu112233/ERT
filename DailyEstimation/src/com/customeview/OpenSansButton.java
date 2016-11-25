package com.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.aip.dailyestimation.common.util.IConstants;

public class OpenSansButton extends Button {

	public OpenSansButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public OpenSansButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OpenSansButton(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+IConstants.FONT_NAME);
		setTypeface(tf);
	}
}
