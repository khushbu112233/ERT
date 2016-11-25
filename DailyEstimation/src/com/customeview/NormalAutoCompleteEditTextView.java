package com.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.aip.dailyestimation.common.util.IConstants;

public class NormalAutoCompleteEditTextView extends AutoCompleteTextView {

	public NormalAutoCompleteEditTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public NormalAutoCompleteEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NormalAutoCompleteEditTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/" + IConstants.FONT_GEN_NAME);
		setTypeface(tf);
	}

}
