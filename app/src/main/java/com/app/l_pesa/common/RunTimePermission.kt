package com.app.l_pesa.common

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/*Beginning in Android 6.0 (API level 23), users grant permissions to apps while the app is running, not when they install the app.
    *  This approach streamlines the app install process, since the user does not need to grant permissions when they install or update the app.
    * It also gives the user more control over the app's functionality; for example, a user could choose to give a camera app access to the camera but not to the device location.
    * The user can revoke the permissions at any time, by going to the app's Settings screen.
    * */
class RunTimePermission(internal var activity: Activity) {

    fun checkPermissionForExternalStorage(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }


    fun checkPermissionForPhoneState(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionForCallPhone(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionForAccessFineLocation(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionForCamera(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        val CAMERA_PERMISSION_REQUEST_CODE = 1
    }


}