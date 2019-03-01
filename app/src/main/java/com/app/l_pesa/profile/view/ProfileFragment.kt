package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.profile.inter.ICallBackUserInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception


class ProfileFragment: Fragment(), ICallBackUserInfo {


    companion object {
        fun newInstance(): Fragment {
            return ProfileFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        loadProfileInfo()
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            loadProfileInfo()
        }
    }

    private fun loadProfileInfo()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
           swipeRefreshLayout.isRefreshing=true
           val presenterUserInfo= PresenterUserInfo()
           presenterUserInfo.getProfileInfo(activity!!,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing=false
            customSnackBarError(llRoot,resources.getString(R.string.no_internet))
        }
    }

    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(activity!!).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }

    override fun onSuccessUserInfo(data: ResUserInfo.Data) {

        swipeRefreshLayout.isRefreshing=false
        setData(data)
    }

    override fun onErrorUserInfo(message: String) {

        swipeRefreshLayout.isRefreshing=false
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: ResUserInfo.Data)
    {

        /*Profile Information*/

        try {

            val options = RequestOptions()
            Glide.with(activity!!)
                    .load(data.userInfo.profileImage)
                    .apply(options)
                    .into(imgProfile)
        }
        catch (exception: Exception)
        {

        }

        txtPhone.text = data.userInfo.phoneNumber
        txtCreditScore.text = resources.getString(R.string.credit_score)+" "+data.userInfo.creditScore
        txtDOB.text = resources.getString(R.string.date_of_birth)+" "+data.userPersonalInfo.dob
        txtGender.text = resources.getString(R.string.gender)+" "+data.userPersonalInfo.sex
        txtMartialStatus.text = resources.getString(R.string.marital_status)+" "+data.userPersonalInfo.meritalStatus
        txtMotherName.text = resources.getString(R.string.mother_maiden_name)+" "+data.userPersonalInfo.motherMaidenName

        /* ID Information*/

        if(data.userIdsInfo.size>0)
        {
            if(data.userIdsInfo.size==1)
            {
                userInformation(data.userIdsInfo[0])
            }
            else
            {
                userInformation(data.userIdsInfo[data.userIdsInfo.size-1])
            }
        }

        /*Contact Information*/

        txtAddress.text=data.userContactInfo.streetAddress+", "+data.userContactInfo.city+"- "+data.userContactInfo.postalAddress
        txtEmail.text=data.userPersonalInfo.emailAddress
        txtContact.text=data.userContactInfo.phoneNumber

        /* Employment Info*/

        txtEmployeeType.text=resources.getString(R.string.type_of_employer)+" "+data.userEmploymentInfo.employerType
        txtEmployeeName.text=resources.getString(R.string.name_of_employer)+" "+data.userEmploymentInfo.employerName
        txtDepartment.text=resources.getString(R.string.department)+" "+data.userEmploymentInfo.department
        txtOccupation.text=resources.getString(R.string.occupation)+" "+data.userEmploymentInfo.position
        txtEmployeeID.text=resources.getString(R.string.employees_id_no)+" "+data.userEmploymentInfo.employeesIdNumber
        txtEmployeeCity.text=resources.getString(R.string.city)+" "+data.userEmploymentInfo.city

        /* Business Info*/

        txtBusinessName.text=resources.getString(R.string.business_name)+" "+data.userBusinessInfo.businessName
        txtTIN.text=resources.getString(R.string.tin_number)+" "+data.userBusinessInfo.tinNumber
        txtIDType.text=resources.getString(R.string.id_type)+" "+data.userBusinessInfo.idType
        txtIdNo.text=resources.getString(R.string.id_number)+" "+data.userBusinessInfo.idNumber



    }

    private fun userInformation(userIdsInfo: ResUserInfo.UserIdsInfo)
    {

        try {

            val options = RequestOptions()
            Glide.with(activity!!)
                    .load(userIdsInfo.fileName)
                    .apply(options)
                    .into(imgID)


        }
        catch (exception: Exception)
        {

        }

        txtIdType.text      = userIdsInfo.idTypeName
        txtIdNumber.text    = userIdsInfo.idNumber
        txtCreatedTime.text = userIdsInfo.created


    }
}

