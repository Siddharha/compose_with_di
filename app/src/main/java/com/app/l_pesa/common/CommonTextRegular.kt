package com.app.l_pesa.common

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatTextView


class CommonTextRegular : AppCompatTextView {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(mContext: Context) {
        if (!isInEditMode) {
            val face = Typeface.createFromAsset(mContext.assets, "fonts/Montserrat-Regular.ttf")
            this.typeface = face
        }

    }
}
