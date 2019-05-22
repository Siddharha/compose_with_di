package com.app.l_pesa.pinview.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.content_pin_set.*



class PinSetActivity : AppCompatActivity() {

    private lateinit  var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_set)

        initLoader()
        initData()

    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@PinSetActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun initData()
    {
        pass_code_view.setOnTextChangeListener { text ->
            if (text.length == 6) {

                if(CommonMethod.isNetworkAvailable(this@PinSetActivity))
                {
                    progressDialog.show()
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@PinSetActivity,resources.getString(R.string.no_internet))
                }

            }
        }
    }

}
