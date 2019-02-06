package com.app.l_pesa.common

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardActivity
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




    fun setSnackBar(context:Context, coordinatorLayout: View, snackTitle: String)
    {
        val snackBar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_SHORT)
        snackBar.show()
        val view = snackBar.view
        val txtView = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        val font = Typeface.createFromAsset(context.assets,"fonts/Montserrat-Regular.ttf")
        txtView.typeface = font
        txtView.gravity = Gravity.CENTER_HORIZONTAL
        txtView.textSize = 12F

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

    fun hideKeyboard(activity: AppCompatActivity)
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
