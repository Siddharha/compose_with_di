package com.app.l_pesa.registration.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.app.l_pesa.R

class RegistrationStepTwoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)
    }



    override fun onBackPressed() {

        val intent = Intent(this@RegistrationStepTwoActivity, RegistrationStepOneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }


}
