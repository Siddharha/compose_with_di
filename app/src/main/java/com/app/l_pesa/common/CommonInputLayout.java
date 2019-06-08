package com.app.l_pesa.common;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputLayout;
import android.util.AttributeSet;

/**
 * Created by Intellij Amiya on 06-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
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
