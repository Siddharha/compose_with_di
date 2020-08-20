package com.app.l_pesa.common

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object CommonMethod {

    fun sessionTime(): Int {
        return 300000
    }

    fun splashTime(): Long {
        return 2000
    }


    fun openPrivacyUrl(context: Context,countrycode: String) {
        val uri = Uri.parse("https://l-pesa.com/$countrycode/pages/privacy-policy")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun openTermCondition(context: Context, countrycode: String) {
        val uri = Uri.parse("https://l-pesa.com/$countrycode/pages/term-service")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }


    //@IntRange(from = 0, to = 2)
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false //var result = 0. Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true //2
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true  //1
                    }else if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                        result = true //3
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true //2
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true //1
                    } else if (type == ConnectivityManager.TYPE_VPN){
                        result = true
                    }
                }
            }
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun dateConvert(inputDate:String): String? {
            return if(!TextUtils.isEmpty(inputDate))
            {
                val inputFormat  = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val date         = inputFormat.parse(inputDate)

                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                outputFormat.format(date!!)
            }
            else ""

    }

    @SuppressLint("SimpleDateFormat")
    fun dateConvertYMD(inputDate:String): String? {

        return if(!TextUtils.isEmpty(inputDate))
        {
            val inputFormat  = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date         = inputFormat.parse(inputDate)

            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            outputFormat.format(date!!)
        }
        else ""

    }


    fun customSnackBarError(view: View,context: Context,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(context,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(context).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle= customView.findViewById(R.id.txtTitle) as CommonTextRegular

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
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
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

    fun datePicker(ctx: Context, editText: CommonEditTextRegular) {

        val newCalendar = Calendar.getInstance()
        val fromDatePickerDialog = DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editText.setText(sdf.format(newDate.time))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))

        fromDatePickerDialog.show()
        fromDatePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    fun removeExtraSpace(input: String): String {
        var input = input
        input = input.trim { it <= ' ' }
        val x = ArrayList(Arrays.asList(*input.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        var i = 0
        while (i < x.size - 1) {
            if (x[i] == " " && x[i + 1] == " ") {
                x.removeAt(i)
                i--
            }
            i++
        }
        var word = ""
        for (each in x)
            word += each
        return word
    }


}
