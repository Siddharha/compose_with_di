package com.app.l_pesa.pin.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.app.l_pesa.R

import kotlinx.android.synthetic.main.activity_set_up_pin.*

class SetUpPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_pin)
        setSupportActionBar(toolbar)

    }

}
