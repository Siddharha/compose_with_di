package com.app.l_pesa.common

import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.Spanned
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatEditText


class CommonEditTextRegular : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
        filters = arrayOf<InputFilter>(EmojiFilter())
    }

    private fun init(mContext: Context) {

        val face = Typeface.createFromAsset(mContext.assets, "fonts/Montserrat-Regular.ttf")
        this.typeface = face

    }

    private inner class EmojiFilter : InputFilter {

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            for (i in start until end) {
                val type = Character.getType(source[i])
                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                    return ""
                }
            }
            return null
        }
    }
}

