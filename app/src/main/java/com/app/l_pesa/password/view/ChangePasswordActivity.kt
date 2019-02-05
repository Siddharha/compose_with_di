package com.app.l_pesa.password.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.activity_change_password.*
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_change_password.*
import android.text.Editable
import android.text.TextWatcher
import android.graphics.Typeface
import android.widget.TextView
import android.app.Activity
import android.text.TextUtils


class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarFont(this@ChangePasswordActivity)
        textWatcherPassword()
        cancelButton()


    }

    private fun cancelButton()
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }

        buttonSubmit.setOnClickListener {


            if(TextUtils.isEmpty(etCurrentPassword.text.toString()))
            {

            }

        }
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    private fun textWatcherPassword()
    {
        etNewPassword.addTextChangedListener(textWatcherPasswordRule)

    }

    private val textWatcherPasswordRule = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {

            if(s.length<8)
            {
                imgRuleOne.setImageResource(R.drawable.ic_red_tick)
                txt_rule_one.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleOne.setImageResource(R.drawable.ic_green_tick)
                txt_rule_one.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(countNumeric(s.toString())==0)
            {
                imgRuleTwo.setImageResource(R.drawable.ic_red_tick)
                txt_rule_two.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleTwo.setImageResource(R.drawable.ic_green_tick)
                txt_rule_two.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(!hasLowerCase(s.toString()))
            {
                imgRuleThree.setImageResource(R.drawable.ic_red_tick)
                txt_rule_three.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleThree.setImageResource(R.drawable.ic_green_tick)
                txt_rule_three.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(!hasUpperCase(s.toString()))
            {
                imgRuleFour.setImageResource(R.drawable.ic_red_tick)
                txt_rule_four.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleFour.setImageResource(R.drawable.ic_green_tick)
                txt_rule_four.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
            if(!hasSymbol(s.toString()))
            {
                imgRuleFive.setImageResource(R.drawable.ic_red_tick)
                txt_rule_five.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorRed))
            }
            else
            {
                imgRuleFive.setImageResource(R.drawable.ic_green_tick)
                txt_rule_five.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,R.color.colorPrimary))
            }
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

    private fun hasSymbol(data: CharSequence): Boolean {
        val password = data.toString()
        return !password.matches("[A-Za-z0-9 ]*".toRegex())
    }

    private fun hasUpperCase(data: CharSequence): Boolean {
        val password = data.toString()
        return password != password.toLowerCase()
    }

    private fun hasLowerCase(data: CharSequence): Boolean {
        val password = data.toString()
        return password != password.toUpperCase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // todo: goto back activity from here

                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
