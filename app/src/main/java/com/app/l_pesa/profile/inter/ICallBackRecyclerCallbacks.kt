package com.app.l_pesa.profile.inter

import android.view.View

interface ICallBackRecyclerCallbacks<T> {

    fun onItemClick(view: View, position: Int, item: T)
}