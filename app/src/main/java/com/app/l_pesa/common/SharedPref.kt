package com.app.l_pesa.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Intellij Amiya on 25-05-2018.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class SharedPref @SuppressLint("CommitPrefEdits")
constructor(context: Context) // Constructor
{


    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    //Default value
    var appVersion: String
        get() = pref.getString(KEY_SET_APP_VERSION, "1.0.0")!!
        set(appVersion) {
            editor.remove(KEY_SET_APP_VERSION)
            editor.putString(KEY_SET_APP_VERSION, appVersion)
            editor.commit()
        }


    var loginRequest: String
        get() = pref.getString(KEY_SET_LOGIN_REQUEST, "")!!
        set(loginRequest) {
            editor.remove(KEY_SET_LOGIN_REQUEST)
            editor.putString(KEY_SET_LOGIN_REQUEST, loginRequest)
            editor.commit()
        }

    var countryList: String
        get() = pref.getString(KEY_SET_COUNTRY_LIST, "")!!
        set(countryList) {
            editor.remove(KEY_SET_COUNTRY_LIST)
            editor.putString(KEY_SET_COUNTRY_LIST, countryList)
            editor.commit()
        }

    var userInfo: String
        get() = pref.getString(KEY_SET_USER_INFO, "")!!
        set(userInfo) {
            editor.remove(KEY_SET_USER_INFO)
            editor.putString(KEY_SET_USER_INFO, userInfo)
            editor.commit()
        }
    var accessToken: String
        get() = pref.getString(KEY_SET_ACCESS_TOKEN, "INIT")!!
        set(accessToken) {
            editor.remove(KEY_SET_ACCESS_TOKEN)
            editor.putString(KEY_SET_ACCESS_TOKEN, accessToken)
            editor.commit()
        }

    var userDashBoard: String
        get() = pref.getString(KEY_SET_DASHBOARD, "INIT")!!
        set(dashboard) {
            editor.remove(KEY_SET_DASHBOARD)
            editor.putString(KEY_SET_DASHBOARD, dashboard)
            editor.commit()
        }

    var userCreditScore: String
        get() = pref.getString(KEY_SET_CREDIT_SCORE, "")!!
        set(creditScore) {
            editor.remove(KEY_SET_CREDIT_SCORE)
            editor.putString(KEY_SET_CREDIT_SCORE, creditScore)
            editor.commit()
        }

    var countryCode: String
        get() = pref.getString(KEY_SET_COUNTRY_CODE, "tz")!!
        set(countryCode) {
            editor.remove(KEY_SET_COUNTRY_CODE)
            editor.putString(KEY_SET_COUNTRY_CODE, countryCode)
            editor.commit()
        }

    fun removeToken() {

        editor.remove("KEY_SET_ACCESS_TOKEN")
        editor.commit()
        editor.apply()
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()

    }


    companion object {
        private const val PREF_NAME = "L_PESA"

        // All Shared Preferences Keys Declare as #public
        private const val KEY_SET_APP_VERSION  = "KEY_SET_APP_VERSION"
        private const val KEY_SET_COUNTRY_LIST = "KEY_SET_COUNTRY_LIST"
        private const val KEY_SET_COUNTRY_CODE = "KEY_SET_COUNTRY_CODE"
        private const val KEY_SET_USER_INFO    = "KEY_SET_USER_INFO"
        private const val KEY_SET_LOGIN_REQUEST= "KEY_SET_LOGIN_REQUEST"
        private const val KEY_SET_ACCESS_TOKEN = "KEY_SET_ACCESS_TOKEN"
        private const val KEY_SET_DASHBOARD    = "KEY_SET_DASHBOARD"
        private const val KEY_SET_CREDIT_SCORE = "KEY_SET_CREDIT_SCORE"
    }


}
