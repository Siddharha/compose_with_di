package com.app.l_pesa.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomButton extends android.support.v7.widget.AppCompatButton
{
    public CustomButton(Context context) {
        super(context);
        init(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Regular.ttf");
        this.setTypeface(face);

    }
}

