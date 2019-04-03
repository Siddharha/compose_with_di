package com.app.l_pesa.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.regex.Matcher
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

            val outputFormat = SimpleDateFormat("MMMM dd,yyyy")
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
        val requiredSize = 85
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
        selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 85 , outputStream)
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

    fun passwordRegex(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher

        val passwordPattern =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,16}\$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()

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

    fun countNumeric(number: String): Int {
        var flag = 0
        for (i in 0 until number.length) {
            if (Character.isDigit(number[i])) {
                flag++
            }
        }
        return flag
    }

    fun hasSymbol(data: CharSequence): Boolean {
        val password = data.toString()
        return !password.matches("[A-Za-z0-9 ]*".toRegex())
    }

    fun hasUpperCase(data: CharSequence): Boolean {
        val password = data.toString()
        return password != password.toLowerCase()
    }

    fun hasLowerCase(data: CharSequence): Boolean {
        val password = data.toString()
        return password != password.toUpperCase()
    }





}
