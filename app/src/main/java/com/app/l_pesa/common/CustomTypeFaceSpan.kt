package com.app.l_pesa.common

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

import androidx.annotation.ColorInt

class CustomTypeFaceSpan(family: String, private val newType: Typeface, @param:ColorInt @get:ColorInt
val foregroundColor: Int) : TypefaceSpan(family) {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = foregroundColor
        applyCustomTypeFace(ds, newType)
        ds.isUnderlineText = false
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    override fun getSpanTypeId(): Int {
        return super.getSpanTypeId()
    }


    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        if (old == null) {
            oldStyle = 0
        } else {
            oldStyle = old.style
        }

        //  int fake = oldStyle & ~tf.getStyle();
        /*if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }*/

        /*if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
*/

        paint.typeface = tf
    }
}