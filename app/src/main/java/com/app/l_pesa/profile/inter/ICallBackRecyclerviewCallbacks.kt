package com.app.l_pesa.profile.inter

import android.view.View

interface ICallBackRecyclerviewCallbacks<T> {

    fun onItemClick(view: View, position: Int, item: T)
}