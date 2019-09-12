package com.app.l_pesa.loanHistory.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.model.LoanHistoryTabPager
import com.app.l_pesa.main.view.MainActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_loan_list_history.*


class LoanHistoryListActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener{

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_list_history)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanHistoryListActivity)

        initUI()
        initTimer()


    }

    private fun initUI()
    {
        tabLayout=findViewById(R.id.tabLayout)
        viewPager=findViewById(R.id.viewPager)
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.personal_loan_history)))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.business_loan_history)))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout!!.tabMode = TabLayout.MODE_FIXED

        val adapter = LoanHistoryTabPager(supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        changeTabsFont()
        tabLayout!!.addOnTabSelectedListener(this)

        val sharedPref= SharedPref(this@LoanHistoryListActivity)

        Handler().postDelayed(
                {
                    if(sharedPref.openTabLoan=="CURRENT")
                    {
                        tabLayout!!.getTabAt(0)!!.select()
                    }
                    else
                    {
                        tabLayout!!.getTabAt(1)!!.select()
                        sharedPref.openTabLoan="BUSINESS"
                    }
                }, 100)


        imgFilter.setOnClickListener {

            if(viewPager!!.currentItem==0)
            {
                val fragment = adapter.instantiateItem(viewPager!!, 0) as androidx.fragment.app.Fragment
                if (fragment is CurrentLoanHistory) {
                    fragment.doFilter()

                }
            }
            else
            {
                val fragment = adapter.instantiateItem(viewPager!!, 1) as androidx.fragment.app.Fragment
                if (fragment is BusinessLoanHistory) {
                    fragment.doFilter()

                }
            }


            }

    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {

    }

    override fun onTabSelected(p0: TabLayout.Tab?) {

       viewPager!!.currentItem = p0!!.position
        val sharedPref= SharedPref(this@LoanHistoryListActivity)
        if(viewPager!!.currentItem==0)
        {
            sharedPref.openTabLoan="CURRENT"
        }
        else
        {
            sharedPref.openTabLoan="BUSINESS"
        }

    }

    private fun changeTabsFont() {
        val vg = tabLayout!!.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
                    tabViewChild.typeface = face
                }
            }
        }
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

     fun onSessionTimeOut(message: String) {

        val dialogBuilder = AlertDialog.Builder(this@LoanHistoryListActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@LoanHistoryListActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@LoanHistoryListActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }


    private fun initTimer() {

        countDownTimer= object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }}
        countDownTimer.start()

    }


    override fun onUserInteraction() {
        super.onUserInteraction()

        countDownTimer.cancel()
        countDownTimer.start()
    }


    public override fun onStop() {
        super.onStop()
        countDownTimer.cancel()

    }

}
