package com.app.l_pesa.common


import android.graphics.drawable.Drawable

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.View.OnTouchListener

import android.widget.PopupWindow
import android.content.Context

/**
 * Custom popup window.
 *
 * @author Lorensius W. L. T <lorenz></lorenz>@londatiga.net>
 */
open class PopupWindows
/**
 * Constructor.
 *
 * @param context Context
 */
(   var mContext: Context) {
    var mWindow: PopupWindow
    open var mRootView: View? = null
    var mBackground: Drawable? = null
    var mWindowManager: WindowManager

    init {
        mWindow = PopupWindow(mContext)

        mWindow.setTouchInterceptor(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                mWindow.dismiss()

                return@OnTouchListener true
            }

            false
        })

        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * On dismiss
     */
    open fun onDismiss() {}

    /**
     * On show
     */
    fun onShow() {}

    /**
     * On pre show
     */
    protected fun preShow() {
        if (mRootView == null)
            throw IllegalStateException("setContentView was not called with a view to display.")

        onShow()

        if (mBackground != null)

            mWindow.setBackgroundDrawable(mBackground)

        mWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.isTouchable = true
        mWindow.isFocusable = true
        mWindow.isOutsideTouchable = true

        mWindow.contentView = mRootView
    }

    /**
     * Set background drawable.
     *
     * @param background Background drawable
     */
    fun setBackgroundDrawable(background: Drawable) {
        mBackground = background
    }

    /**
     * Set content view.
     *
     * @param root Root view
     */
    fun setContentView(root: View) {
        mRootView = root

        mWindow.contentView = root

    }

    /**
     * Set content view.
     *
     * @param layoutResID Resource id
     */
    fun setContentView(layoutResID: Int) {
        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        setContentView(inflator.inflate(layoutResID, null))
    }

    /**
     * Set listener on window dismissed.
     *
     * @param listener
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener) {
        mWindow.setOnDismissListener(listener)
    }

    /**
     * Dismiss the popup window.
     */
    fun dismiss() {
        mWindow.dismiss()
    }
}