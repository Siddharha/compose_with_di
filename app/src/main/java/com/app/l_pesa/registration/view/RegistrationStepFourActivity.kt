package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.registration.adapter.PersonalIdListAdapter
import kotlinx.android.synthetic.main.activity_registration_step_four.*
import kotlinx.android.synthetic.main.layout_registration_step_four.*

class RegistrationStepFourActivity : AppCompatActivity(), ICallBackId {


    private val idList     = arrayListOf("1","2","3","4")
    private val idNameList = arrayListOf("Passport", "Driving License", "National ID","Voter ID")
    private var typeId="0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_four)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

            startActivity(Intent(this@RegistrationStepFourActivity, RegistrationStepFiveActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }



}
