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


    var countryList: String
        get() = pref.getString(KEY_SET_COUNTRY_LIST, "INIT")!!
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

    var profileInfo: String
        get() = pref.getString(KEY_SET_PROFILE_INFO, "")!!
        set(profileInfo) {
            editor.remove(KEY_SET_PROFILE_INFO)
            editor.putString(KEY_SET_PROFILE_INFO, profileInfo)
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

    var countryName: String
        get() = pref.getString(KEY_SET_COUNTRY_NAME, "")!!
        set(countryName) {
            editor.remove(KEY_SET_COUNTRY_NAME)
            editor.putString(KEY_SET_COUNTRY_NAME, countryName)
            editor.commit()
        }

    var countryFlag: String
        get() = pref.getString(KEY_SET_COUNTRY_FLAG, "")!!
        set(countryFlag) {
            editor.remove(KEY_SET_COUNTRY_FLAG)
            editor.putString(KEY_SET_COUNTRY_FLAG, countryFlag)
            editor.commit()
        }

    var profileUpdate: String
        get() = pref.getString(KEY_SET_PROFILE_UPDATE, "FALSE")!!
        set(profileUpdate) {
            editor.remove(KEY_SET_PROFILE_UPDATE)
            editor.putString(KEY_SET_PROFILE_UPDATE, profileUpdate)
            editor.commit()
        }

    var navigationTab: String
        get() = pref.getString(KEY_SET_NAVIGATION_TAB, "FALSE")!!
        set(navigationTab) {
            editor.remove(KEY_SET_NAVIGATION_TAB)
            editor.putString(KEY_SET_NAVIGATION_TAB, navigationTab)
            editor.commit()
        }

    var openTabLoan: String
        get() = pref.getString(KEY_SET_LOAN_TAB_PLAN, "CURRENT")!!
        set(openTabLoan) {
            editor.remove(KEY_SET_LOAN_TAB_PLAN)
            editor.putString(KEY_SET_LOAN_TAB_PLAN, openTabLoan)
            editor.commit()
        }

    var payFullAmount: String
        get() = pref.getString(KEY_SET_PAY_FULL_AMOUNT, "")!!
        set(payFullAmount) {
            editor.remove(KEY_SET_PAY_FULL_AMOUNT)
            editor.putString(KEY_SET_PAY_FULL_AMOUNT, payFullAmount)
            editor.commit()
        }

    var loanPlanList: String
        get() = pref.getString(KEY_SET_LOAN_PLAN_LIST, "")!!
        set(loanPlanList) {
            editor.remove(KEY_SET_LOAN_PLAN_LIST)
            editor.putString(KEY_SET_LOAN_PLAN_LIST, loanPlanList)
            editor.commit()
        }

    var lpkInfo: String
        get() = pref.getString(KEY_SET_LPK_INFO, "")!!
        set(lpkInfo) {
            editor.remove(KEY_SET_LPK_INFO)
            editor.putString(KEY_SET_LPK_INFO, lpkInfo)
            editor.commit()
        }

    var investRateMin: String
        get() = pref.getString(KEY_SET_MIN_INVEST_INTEREST, "")!!
        set(investRateMin) {
            editor.remove(KEY_SET_MIN_INVEST_INTEREST)
            editor.putString(KEY_SET_MIN_INVEST_INTEREST, investRateMin)
            editor.commit()
        }

    var investRateMax: String
        get() = pref.getString(KEY_SET_MAX_INVEST_INTEREST, "")!!
        set(investRateMax) {
            editor.remove(KEY_SET_MAX_INVEST_INTEREST)
            editor.putString(KEY_SET_MAX_INVEST_INTEREST, investRateMax)
            editor.commit()
        }

    var currentLoanCount: String
        get() = pref.getString(KEY_SET_CURRENT_LOAN_COUNT, "0")!!
        set(currentLoanCount) {
            editor.remove(KEY_SET_CURRENT_LOAN_COUNT)
            editor.putString(KEY_SET_CURRENT_LOAN_COUNT, currentLoanCount)
            editor.commit()
        }

    var businessLoanCount: String
        get() = pref.getString(KEY_SET_BUSINESS_LOAN_COUNT, "0")!!
        set(businessLoanCount) {
            editor.remove(KEY_SET_BUSINESS_LOAN_COUNT)
            editor.putString(KEY_SET_BUSINESS_LOAN_COUNT, businessLoanCount)
            editor.commit()
        }

    var deviceInfo: String
        get() = pref.getString(KEY_SET_DEVICE_INFO, "")!!
        set(deviceInfo) {
            editor.remove(KEY_SET_DEVICE_INFO)
            editor.putString(KEY_SET_DEVICE_INFO, deviceInfo)
            editor.commit()
        }

    fun removeShared() {

        editor.remove("KEY_SET_ACCESS_TOKEN")
        editor.remove("KEY_SET_PROFILE_INFO")
        editor.remove("KEY_SET_LPK_INFO")
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
        private const val KEY_SET_APP_VERSION           = "KEY_SET_APP_VERSION"
        private const val KEY_SET_COUNTRY_LIST          = "KEY_SET_COUNTRY_LIST"
        private const val KEY_SET_COUNTRY_CODE          = "KEY_SET_COUNTRY_CODE"
        private const val KEY_SET_USER_INFO             = "KEY_SET_USER_INFO"
        private const val KEY_SET_ACCESS_TOKEN          = "KEY_SET_ACCESS_TOKEN"
        private const val KEY_SET_DASHBOARD             = "KEY_SET_DASHBOARD"
        private const val KEY_SET_CREDIT_SCORE          = "KEY_SET_CREDIT_SCORE"
        private const val KEY_SET_PROFILE_INFO          = "KEY_SET_PROFILE_INFO"
        private const val KEY_SET_PROFILE_UPDATE        = "KEY_SET_PROFILE_UPDATE"
        private const val KEY_SET_NAVIGATION_TAB        = "KEY_SET_NAVIGATION_TAB"
        private const val KEY_SET_LOAN_TAB_PLAN         = "KEY_SET_LOAN_TAB_PLAN"
        private const val KEY_SET_PAY_FULL_AMOUNT       = "KEY_SET_PAY_FULL_AMOUNT"
        private const val KEY_SET_LOAN_PLAN_LIST        = "KEY_SET_LOAN_PLAN_LIST"
        private const val KEY_SET_LPK_INFO              = "KEY_SET_LPK_INFO"
        private const val KEY_SET_MIN_INVEST_INTEREST   = "KEY_SET_MIN_INVEST_INTEREST"
        private const val KEY_SET_MAX_INVEST_INTEREST   = "KEY_SET_MAX_INVEST_INTEREST"
        private const val KEY_SET_CURRENT_LOAN_COUNT    = "KEY_SET_CURRENT_LOAN_COUNT"
        private const val KEY_SET_BUSINESS_LOAN_COUNT   = "KEY_SET_BUSINESS_LOAN_COUNT"
        private const val KEY_SET_DEVICE_INFO           = "KEY_SET_DEVICE_INFO"
        private const val KEY_SET_COUNTRY_NAME          = "KEY_SET_COUNTRY_NAME"
        private const val KEY_SET_COUNTRY_FLAG          = "KEY_SET_COUNTRY_FLAG"
    }


}
