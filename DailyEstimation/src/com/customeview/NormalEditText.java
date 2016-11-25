package com.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import com.aip.dailyestimation.common.util.IConstants;

public class NormalEditText extends EditText {

    public NormalEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NormalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + IConstants.FONT_GEN_NAME);
        setTypeface(tf);
        setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(50)});
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

}
