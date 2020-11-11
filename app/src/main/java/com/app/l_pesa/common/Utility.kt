package com.app.l_pesa.common

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest


fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun AppCompatActivity.hideKeyBoard() {
    try {
        CommonMethod.hideKeyboardView(this)
    } catch (exp: Exception) {
    }

}

