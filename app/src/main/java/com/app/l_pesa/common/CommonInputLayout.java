package com.app.l_pesa.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputLayout;

public class CommonInputLayout extends TextInputLayout {

    public CommonInputLayout(Context context) {
        super(context);
        init(context);
    }

    public CommonInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonInputLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        /*if (!isInEditMode()) {*/
            Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Regular.ttf");
            this.setTypeface(face);
        //}

    }
}
