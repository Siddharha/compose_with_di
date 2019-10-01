package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.registration.adapter.PersonalIdListAdapter
import kotlinx.android.synthetic.main.activity_registration_step_four.*
import kotlinx.android.synthetic.main.layout_registration_step_four.*

class RegistrationStepFourActivity : AppCompatActivity(), ICallBackId {


    private val idList     = arrayListOf("1","2","3","4")
    private val idNameList = arrayListOf("Passport", "Driving License", "National ID","Voter ID")
    private var typeId     =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_four)
        setSupportActionBar(toolbar)
        toolbarFont(this@RegistrationStepFourActivity)

        initUI()

    }

    private fun showId()
    {
        val dialog= Dialog(this@RegistrationStepFourActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = PersonalIdListAdapter(this@RegistrationStepFourActivity, idList,idNameList,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepFourActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun initUI()
    {
        showId()

        etIdType.setOnClickListener {

            showId()
        }

        btnSubmit.setOnClickListener {

            try {
                CommonMethod.hideKeyboardView(this@RegistrationStepFourActivity)
            } catch (exp: Exception) {

            }

            if(TextUtils.isEmpty(etIdNumber.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFourActivity, resources.getString(R.string.required_id_number))
            }
            else
            {
                if (CommonMethod.isNetworkAvailable(this@RegistrationStepFourActivity)) {

                    val bundle     = Bundle()
                    bundle.putString("id_type",typeId)
                    bundle.putString("id_number",etIdNumber.text.toString())
                    val intent = Intent(this@RegistrationStepFourActivity, RegistrationStepFiveActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent,bundle)
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                else{
                    CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFourActivity, resources.getString(R.string.no_internet))
                }
            }

        }

    }


    override fun onClickIdType(position: Int, type: String) {

        etIdType.setText(idNameList[position])
        typeId=idList[position]
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onBackPressed() {

    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@RegistrationStepFourActivity::class.java.simpleName)

    }


}
