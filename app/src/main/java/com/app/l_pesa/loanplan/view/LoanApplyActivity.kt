package com.app.l_pesa.loanplan.view

import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.loanplan.adapter.DescriptionAdapter
import com.app.l_pesa.loanplan.inter.ICallBackDescription

import kotlinx.android.synthetic.main.activity_loan_apply.*
import kotlinx.android.synthetic.main.content_loan_apply.*

class LoanApplyActivity : AppCompatActivity(), ICallBackDescription {

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
        val product_id   = bundle!!.getString("PRODUCT_ID")
        val loan_type    = bundle.getString("LOAN_TYPE")

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


        }


    }

    private fun loadDescription()
    {
        etChooseDescription.setText(listTitle[0])
        loanPurpose=listTitle[0]
        etChooseDescription.isFocusable =false
        etChooseDescription.setOnClickListener {

            val dialog= Dialog(this@LoanApplyActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_country)
            val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
            val titleAdapter                = DescriptionAdapter(this@LoanApplyActivity, listTitle,dialog,this)
            recyclerView?.layoutManager     = LinearLayoutManager(this@LoanApplyActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView?.adapter           = titleAdapter
            dialog.show()
        }


    }

    override fun onSelectDescription(s: String) {

        etChooseDescription.setText(s)
        loanPurpose=s
        if(loanPurpose=="Others")
        {
            rlPurpose.visibility        = View.VISIBLE
            txt_max_words.visibility    = View.VISIBLE

        }
        else
        {
            rlPurpose.visibility        = View.GONE
            txt_max_words.visibility    = View.GONE
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
