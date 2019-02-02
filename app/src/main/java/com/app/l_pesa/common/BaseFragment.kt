package com.app.l_pesa.common

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Intellij Amiya on 2/2/19.
 * Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

abstract class BaseFragment  : Fragment() {

    lateinit var contentView: View

    val activity by lazy { getActivity() as BaseActivity }

    protected abstract fun getLayout(container: ViewGroup): View
    protected abstract fun afterActivityCreated(savedInstanceState: Bundle?)
    protected abstract fun afterFragmentResume()
    protected abstract fun onstopFragment()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container != null)
            contentView = getLayout(container)
        return contentView
    }

    fun inflateLayout(container: ViewGroup, layout: Int): View {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupBackPressedListener()
        afterActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onStop() {
        super.onStop()
        onstopFragment()
    }


    private fun setupBackPressedListener() {
        contentView.isFocusableInTouchMode = true
        contentView.requestFocus()
        contentView.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                activity.finish()
            }
            false
        }
    }
}

