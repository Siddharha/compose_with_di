package com.app.l_pesa.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.ErrorDialogFragment
import com.google.android.gms.common.GoogleApiAvailability
import com.sinch.sanalytics.client.jni.ApplicationContext


fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun AppCompatActivity.hideKeyBoard() {
    try {
        CommonMethod.hideKeyboardView(this)
    } catch (exp: Exception) {
    }

}

fun isGooglePlayServicesAvailable( activity: AppCompatActivity): Boolean{
    var errorDialog:Dialog?=null
    val googleApiAvailability = GoogleApiAvailability.getInstance()

    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity.application)

    if (resultCode != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(resultCode)) {

            if (errorDialog == null) {
                errorDialog = googleApiAvailability.getErrorDialog(activity, resultCode, 2404)
                errorDialog.setCancelable(false)
            }

            if (!errorDialog?.isShowing!!)
                errorDialog.show()

        }
    }

    return resultCode == ConnectionResult.SUCCESS
}

