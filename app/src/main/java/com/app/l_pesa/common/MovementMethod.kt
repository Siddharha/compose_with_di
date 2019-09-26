package com.app.l_pesa.common

import android.text.Selection
import android.text.Spannable
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView

internal class MovementMethod private constructor() : android.text.method.MovementMethod {

    override fun initialize(widget: TextView, text: Spannable) {
        Selection.setSelection(text, 0)
    }

    override fun onKeyDown(widget: TextView, text: Spannable, keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyUp(widget: TextView, text: Spannable, keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyOther(view: TextView, text: Spannable, event: KeyEvent): Boolean {
        return false
    }

    override fun onTakeFocus(widget: TextView, text: Spannable, direction: Int) {
        //Intentionally Empty
    }

    override fun onTrackballEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return false
    }

    override fun onGenericMotionEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return false
    }

    override fun canSelectArbitrarily(): Boolean {
        return false
    }

    companion object {

        private var sInstance: MovementMethod? = null

        val instance: android.text.method.MovementMethod
            get() {
                if (sInstance == null) {
                    sInstance = MovementMethod()
                }

                return sInstance!!
            }
    }
}