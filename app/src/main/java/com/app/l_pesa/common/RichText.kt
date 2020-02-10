package com.app.l_pesa.common

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView

data class RichText(
        var spannables: List<Pair<IntRange, () -> Unit>>? = null
)

fun TextView.richText(string: String, block: RichText.() -> Unit) {
    val richText = RichText().apply(block)

    val sp = SpannableString(string)

    richText.spannables?.forEach {
        sp.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                it.second()
            }
        }, it.first.first, it.first.last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(ForegroundColorSpan(Color.parseColor("#f2b50d")),it.first.first,it.first.last,Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        text = sp
        movementMethod = LinkMovementMethod.getInstance()
    }
}