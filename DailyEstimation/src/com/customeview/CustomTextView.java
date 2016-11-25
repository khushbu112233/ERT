package com.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.aip.dailyestimation.common.util.IConstants;

public class CustomTextView extends TextView implements OnTouchListener{

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+IConstants.FONT_GEN_NAME);
		setTypeface(tf);
		setOnTouchListener(this);
	}

	/**
	 * Specially for the dashboard screen
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			v.setFocusable(true);
			v.setSelected(true);
			break;
		case MotionEvent.ACTION_UP:
			v.setFocusable(false);
			v.setSelected(false);
			if(clickListener != null)
				clickListener.onClick(v);
		default:
			break;
		} 
		return true;
	}
	
	private OnClickListener clickListener;
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		clickListener = l;
	}
}
