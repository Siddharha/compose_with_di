package com.app.l_pesa.loanplan.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.inter.ICallBackLoanApply
import com.app.l_pesa.loanplan.adapter.DescriptionAdapter
import com.app.l_pesa.loanplan.inter.ICallBackDescription
import com.app.l_pesa.loanplan.presenter.PresenterLoanApply
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_loan_apply.*
import kotlinx.android.synthetic.main.content_loan_apply.*



class LoanApplyActivity : AppCompatActivity(), ICallBackDescription, ICallBackLoanApply {


    private var loanPurpose=""
    private val listTitle = arrayListOf("For Transport","To Pay Bills","To Clear Debit","To Buy Foodstuff","Emergency Purposes","To Buy Medicine","Build Credit","Others")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanApplyActivity)

        initData()

    }

    private fun initData()
    {
        val bundle       = intent.extras
        val productID    = bundle!!.getString("PRODUCT_ID")
        val loanType     = bundle.getString("LOAN_TYPE")


        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
        }

        loadDescription()

        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        buttonSubmit.setOnClickListener {

            if(TextUtils.isEmpty(loanPurpose))
            {
                showDescription()
                CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.required_loan_purpose))
            }
            else if(loanPurpose=="Others" && TextUtils.isEmpty(etDescription.text.toString()))
            {
                etDescription.requestFocus()
                CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.required_loan_purpose_description))
            }
            else
            {
                val alertDialog = AlertDialog.Builder(this@LoanApplyActivity)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.want_to_apply_loan))
                alertDialog.setPositiveButton("Yes") { _, _ -> applyLoan(loanType,productID) }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()


            }

        }


    }

    private fun applyLoan(loan_type: String?, product_id: String?)
    {
        if(CommonMethod.isNetworkAvailable(this@LoanApplyActivity))
                {
                    buttonSubmit.isClickable =false
                    swipeRefreshLayout.isRefreshing = true
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("loan_type",loan_type)
                    jsonObject.addProperty("product_id",product_id)
                    if(loanPurpose=="Others")
                    {
                        jsonObject.addProperty("loan_purpose",etDescription.text.toString())
                    }
                    else
                    {
                        jsonObject.addProperty("loan_purpose",loanPurpose)

                    }

                    jsonObject.addProperty("latitude","22.56")
                    jsonObject.addProperty("longitude","88.36")

                    val presenterLoanApply= PresenterLoanApply()
                    presenterLoanApply.doLoanApply(this@LoanApplyActivity,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.no_internet))
                }
    }

    private fun loadDescription()
    {
        etChooseDescription.isFocusable =false
        etChooseDescription.setOnClickListener {

            showDescription()
        }


    }

    private fun showDescription()
    {

        val dialog= Dialog(this@LoanApplyActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = DescriptionAdapter(this@LoanApplyActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@LoanApplyActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    override fun onSelectDescription(s: String) {

        etChooseDescription.setText(s)
        loanPurpose=s
        if(loanPurpose=="Others")
        {
            txt_loan_description.visibility     = View.VISIBLE
            tilDescription.visibility           = View.VISIBLE
            txt_max_words.visibility            = View.VISIBLE
            txt_loan_description.visibility     = View.VISIBLE
            etDescription.requestFocus()

        }
        else
        {
            txt_loan_description.visibility     = View.GONE
            tilDescription.visibility           = View.GONE
            txt_max_words.visibility            = View.GONE
            txt_loan_description.visibility     = View.GONE
        }
    }

    override fun onSuccessLoanApply() {
        swipeRefreshLayout.isRefreshing = false
        buttonSubmit.isClickable =true
        val sharedPref=SharedPref(this@LoanApplyActivity)
        sharedPref.navigationTab=resources.getString(R.string.open_tab_loan)
        val intent = Intent(this@LoanApplyActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finishAffinity()
    }

    override fun onErrorLoanApply(message: String) {
        buttonSubmit.isClickable =true
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,message)
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
