package com.app.l_pesa.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;


/**
 * Created by Intellij Amiyo on 07-02-2018.
 */


public class CommonEditTextRegular extends AppCompatEditText
{
    public CommonEditTextRegular(Context context) {
        super(context);
        init(context);
    }

    public CommonEditTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonEditTextRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        setFilters(new InputFilter[]{new EmojiFilter()});
    }

    private void init(Context mContext) {

            Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Regular.ttf");
            this.setTypeface(face);

    }

    private class EmojiFilter implements InputFilter {

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

