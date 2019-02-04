package com.app.l_pesa.common

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.annotation.StringRes
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView

import com.app.l_pesa.R

/**
 * Created by Intellij Amiya on 04-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

class CenteredToolbar : Toolbar {

    private var centeredTitleTextView: CommonTextRegular? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setTitle(@StringRes resId: Int) {
        val s = resources.getString(resId)
        title = s
    }

    override fun setTitle(title: CharSequence) {
        getCenteredTitleTextView().text = title
    }

    override fun getTitle(): CharSequence {
        return getCenteredTitleTextView().text.toString()
    }

    fun setTypeface(font: Typeface) {
        getCenteredTitleTextView().typeface = font
    }

    private fun getCenteredTitleTextView(): CommonTextRegular {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = CommonTextRegular(context)
            centeredTitleTextView!!.setSingleLine()
            centeredTitleTextView!!.ellipsize = TextUtils.TruncateAt.END
            centeredTitleTextView!!.gravity = Gravity.CENTER
            if (Build.VERSION.SDK_INT < 23) {
                centeredTitleTextView!!.setTextAppearance(context, R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
            } else {
                centeredTitleTextView!!.setTextAppearance(R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
            }


            val lp = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER
            centeredTitleTextView!!.layoutParams = lp

            addView(centeredTitleTextView)
        }
        return centeredTitleTextView as CommonTextRegular
    }
}