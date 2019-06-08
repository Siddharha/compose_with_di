package com.app.l_pesa.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.app.l_pesa.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
object CommonMethod {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            var isConnected = false
            val activeNetwork = connectivityManager.activeNetworkInfo
            isConnected = activeNetwork != null && activeNetwork.isConnected

        return isConnected
    }

    @SuppressLint("SimpleDateFormat")
    fun dateConvert(inputDate:String): String? {
            return if(!TextUtils.isEmpty(inputDate))
            {
                val inputFormat  = SimpleDateFormat("dd/MM/yyyy")
                val date         = inputFormat.parse(inputDate)

                val outputFormat = SimpleDateFormat("MMM dd, yyyy")
                outputFormat.format(date)
            }
            else ""

    }

    @SuppressLint("SimpleDateFormat")
    fun dateConvertYMD(inputDate:String): String? {

        return if(!TextUtils.isEmpty(inputDate))
        {
            val inputFormat  = SimpleDateFormat("dd-MM-yyyy")
            val date         = inputFormat.parse(inputDate)

            val outputFormat = SimpleDateFormat("yyyy-MM-dd")
            outputFormat.format(date)
        }
        else ""

    }

    fun fileCompress(fileOBJ: File): File
    {
        val bitOptionOBJ    = BitmapFactory.Options()
        bitOptionOBJ.inJustDecodeBounds      = true
        bitOptionOBJ.inSampleSize            = 6

        var inputStream = FileInputStream(fileOBJ)
        BitmapFactory.decodeStream(inputStream, null, bitOptionOBJ)
        inputStream.close()
        val requiredSize = 75
        var scale = 1
        while (bitOptionOBJ.outWidth / scale / 2 >= requiredSize && bitOptionOBJ.outHeight / scale / 2 >= requiredSize) {
            scale *= 2
        }

        val bitOptionNewOBJ        = BitmapFactory.Options()
        bitOptionNewOBJ.inSampleSize                = scale
        inputStream                                 = FileInputStream(fileOBJ)
        val selectedBitmap                  = BitmapFactory.decodeStream(inputStream, null, bitOptionNewOBJ)
        inputStream.close()

        fileOBJ.createNewFile()
        val outputStream = FileOutputStream(fileOBJ)
        selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 65 , outputStream)
        return fileOBJ

    }

    fun customSnackBarError(view: View,context: Context,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(context,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(context).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }

    fun customSnackBarSuccess(view: View,context: Context,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        snackBarOBJ.duration = 5000
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(context,R.color.color_green_success))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(context).inflate(R.layout.snackbar_success, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }


    fun commonCatchBlock(exp:Exception,contextOBJ: Context): String {

        return if(exp.message!!.contains("SocketTimeoutException") || exp.message!!.contains("ConnectException"))
        {
            contextOBJ.getString(R.string.poor_internet)
        }
        else if(exp.message!!.contains("<html>") || exp.message!!.contains("IOException") || exp.message!!.contains("<!DOCTYPE"))
        {
            (contextOBJ.getString(R.string.server_issue))
        }
        else
        {
            (contextOBJ.getString(R.string.something_went_wrong))
        }

    }


    fun isValidEmailAddress(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun hideKeyboardView(activity: AppCompatActivity)
    {
        try {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
