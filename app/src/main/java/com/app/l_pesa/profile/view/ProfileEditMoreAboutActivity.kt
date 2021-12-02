package com.app.l_pesa.profile.view

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.adapter.ProfileEduLvlAdapter
import com.app.l_pesa.profile.adapter.ProfileSourceOfIncomelAdapter
import com.app.l_pesa.profile.inter.ICallBackAdditionalInfoDropdown
import com.app.l_pesa.profile.inter.ICallBackClickMoreAbout
import com.app.l_pesa.profile.inter.ICallBackProfileAdditionalInfo
import com.app.l_pesa.profile.model.ResUserAdditionalInfoDropdowns
import com.app.l_pesa.profile.presenter.PresenterProfile
import kotlinx.android.synthetic.main.activity_profile_edit_more_about.*

class ProfileEditMoreAboutActivity : AppCompatActivity(), ICallBackClickMoreAbout,
    ICallBackAdditionalInfoDropdown {
    private val pref:SharedPref by lazy { SharedPref(this) }
    private val eduLvls:ArrayList<ResUserAdditionalInfoDropdowns.Data.EducationalLevel> by lazy { ArrayList() }
    private val incomeSources:ArrayList<ResUserAdditionalInfoDropdowns.Data.IncomeSource> by lazy { ArrayList() }
    private val netMonthlyIncomeList:ArrayList<ResUserAdditionalInfoDropdowns.Data.NetMonthlyIncome> by lazy { ArrayList() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_more_about)

        loadDropdownDatas()
        onActionPerform()
    }

    private fun loadDropdownDatas() {
        sfMoreAbout.startShimmer()
        val presenterAditionalInfo = PresenterProfile()

        presenterAditionalInfo.getAdditionalUserInfoDropdowns(this,object:
            ICallBackProfileAdditionalInfo {
            override fun onSessionTimeOut(jsonMessage: String) {
                if(sfMoreAbout.isShimmerStarted) {
                    sfMoreAbout.stopShimmer()
                    sfMoreAbout.hideShimmer()
                }
            }

            override fun onFailureAdditionalInfo(jsonMessage: String) {
                if(sfMoreAbout.isShimmerStarted) {
                    sfMoreAbout.stopShimmer()
                    sfMoreAbout.hideShimmer()
                }
            }

            override fun onSuccessAdditionalInfo(data: ResUserAdditionalInfoDropdowns.Data) {

                if(sfMoreAbout.isShimmerStarted) {
                    sfMoreAbout.stopShimmer()
                    sfMoreAbout.hideShimmer()
                }
                eduLvls.clear()
                eduLvls.addAll(data.educationalLevels)
                incomeSources.clear()
                incomeSources.addAll(data.incomeSources)
                netMonthlyIncomeList.clear()
                netMonthlyIncomeList.addAll(data.netMonthlyIncomes)
            }

        })
    }

    private fun onActionPerform() {
        etEduLvl.setOnClickListener {
            showDialogEduLvl()
        }

        etNameEmp.setOnClickListener {
            showDialogSourceOfIncome()
        }
    }

    private fun showDialogSourceOfIncome() {
        //val userDashBoard  = Gson().fromJson<ResDashboard.Data>(pref.userDashBoard, ResDashboard.Data::class.java)
        if(eduLvls.size>0)
        {
            val dialog= Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_more_about)
            val recyclerView                = dialog.findViewById(R.id.rvMoreAbout) as RecyclerView?
            val personalIdAdapter           = ProfileSourceOfIncomelAdapter(this, incomeSources,dialog,this)
            recyclerView?.layoutManager     = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recyclerView?.adapter           = personalIdAdapter
            dialog.show()
        }
    }

    private fun showDialogEduLvl()
    {
        //val userDashBoard  = Gson().fromJson<ResDashboard.Data>(pref.userDashBoard, ResDashboard.Data::class.java)
        if(eduLvls.size>0)
        {
            val dialog= Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_more_about)
            val recyclerView                = dialog.findViewById(R.id.rvMoreAbout) as RecyclerView?
            val personalIdAdapter           = ProfileEduLvlAdapter(this, eduLvls,dialog,this)
            recyclerView?.layoutManager     = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recyclerView?.adapter           = personalIdAdapter
            dialog.show()
        }
    }

    override fun onDropdownSourceOfIncomeSelected(incomeSource: ResUserAdditionalInfoDropdowns.Data.IncomeSource) {
        etNameEmp.setText(incomeSource.name)
    }

    override fun onDropdownEduLvlSelected(educationalLevel: ResUserAdditionalInfoDropdowns.Data.EducationalLevel) {
        etEduLvl.setText(educationalLevel.name)
    }
}

