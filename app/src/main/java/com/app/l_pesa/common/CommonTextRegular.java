package com.app.l_pesa.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

public class CommonTextRegular extends AppCompatTextView {

    public CommonTextRegular(Context context) {
        super(context);
        init(context);
    }

    public CommonTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonTextRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        if (!isInEditMode()) {
            Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Regular.ttf");
            this.setTypeface(face);
        }

    }
}
