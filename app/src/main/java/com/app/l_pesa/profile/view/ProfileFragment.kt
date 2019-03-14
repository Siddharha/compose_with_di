package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackUserInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception
import java.util.ArrayList


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
        loadProfileInfo(true)
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
        loadProfileInfo(false)
        }
    }

    fun loadProfileInfo(shimmerStatus: Boolean)
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            if(shimmerStatus)
            {
                shimmerLayout.startShimmerAnimation()
            }
            else
            {
                shimmerLayout.stopShimmerAnimation()
            }

            val presenterUserInfo= PresenterUserInfo()
            presenterUserInfo.getProfileInfo(activity!!,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing=false
            shimmerLayout.startShimmerAnimation()
            customSnackBarError(llRoot,resources.getString(R.string.no_internet))
        }

        imgEditPersonalInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing)
            {
                startActivity(Intent(activity, ProfileEditPersonalActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }


        }

        imgEditContactInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing)
            {
                startActivity(Intent(activity, ProfileEditContactInfoActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }


        }

        imgEditEmpInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing)
            {
                startActivity(Intent(activity, ProfileEditEmpInfoActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }


        }

        imgEditEmpInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing)
            {
                startActivity(Intent(activity, ProfileEditEmpInfoActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }


        }

        imgEditBusinessInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing)
            {
                startActivity(Intent(activity, ProfileEditBusinessInfoActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }

        }

        imgEditIdInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing)
            {
                startActivity(Intent(activity, ProfileEditIdInfoActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }

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
        shimmerLayout.stopShimmerAnimation()
        setData(data)
    }

    override fun onErrorUserInfo(message: String) {
        shimmerLayout.stopShimmerAnimation()

        swipeRefreshLayout.isRefreshing=false
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: ResUserInfo.Data)
    {

        val sharedPrefOBJ= SharedPref(activity!!)
        val gson                          = Gson()
        val profileData                   = gson.toJson(data)
        sharedPrefOBJ.profileInfo         = profileData


        /*Profile Information*/

        try {

            val options = RequestOptions()
            options.error(R.drawable.ic_id_no_image)
            Glide.with(activity!!)
                    .load(resources.getString(R.string.profile_image_url)+data.userInfo!!.profileImage)
                    .apply(options)
                    .into(imgProfile)
        }
        catch (exception: Exception)
        {

        }
        if(!TextUtils.isEmpty(data.userPersonalInfo!!.emailAddress))
        {
            txtEmail.text=data.userPersonalInfo!!.emailAddress
        }

        txtPhone.text = data.userInfo!!.phoneNumber
        txtCreditScore.text = resources.getString(R.string.credit_score)+" "+data.userInfo!!.creditScore
        txtDOB.text = resources.getString(R.string.date_of_birth)+" "+data.userPersonalInfo!!.dob
        txtGender.text = resources.getString(R.string.gender)+" "+data.userPersonalInfo!!.sex
        txtMartialStatus.text = resources.getString(R.string.marital_status)+" "+data.userPersonalInfo!!.meritalStatus
        txtMotherName.text = resources.getString(R.string.mother_maiden_name)+" "+data.userPersonalInfo!!.motherMaidenName

        /* ID Information*/

        if(data.userIdsPersonalInfo!!.size>0)
        {
            userInformationPersonal(data.userIdsPersonalInfo!!)
        }
        else
        {
            if(data.userIdsBusinessInfo!!.size>0)
            {
                userInformationBusiness(data.userIdsBusinessInfo!!)
            }
            else
            {

            }
        }


        /*Contact Information*/

        if(!TextUtils.isEmpty(data.userContactInfo!!.city) && !TextUtils.isEmpty(data.userContactInfo!!.postalAddress))
        {
            txtAddress.text=data.userContactInfo!!.streetAddress+", "+data.userContactInfo!!.city+"- "+data.userContactInfo!!.postalAddress
        }


        if(!TextUtils.isEmpty(data.userContactInfo!!.phoneNumber))
        {
            txtContact.text=data.userContactInfo!!.phoneNumber
        }


        /* Employment Info*/

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.employerType))
        {
            txtEmployeeType.text=resources.getString(R.string.type_of_employer)+" "+data.userEmploymentInfo!!.employerType
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.employerName))
        {
            txtEmployeeName.text=resources.getString(R.string.name_of_employer)+" "+data.userEmploymentInfo!!.employerName
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.department))
        {
            txtDepartment.text=resources.getString(R.string.department)+" "+data.userEmploymentInfo!!.department
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.position))
        {
            txtOccupation.text = resources.getString(R.string.occupation) + " " + data.userEmploymentInfo!!.position
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.employeesIdNumber))
        {
            txtEmployeeID.text=resources.getString(R.string.employees_id_no)+" "+data.userEmploymentInfo!!.employeesIdNumber
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.city))
        {
            txtEmployeeCity.text=resources.getString(R.string.city)+" "+data.userEmploymentInfo!!.city
        }



        /* Business Info*/

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.businessName))
        {
            txtBusinessName.text=resources.getString(R.string.business_name)+" "+data.userBusinessInfo!!.businessName
        }

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.tinNumber))
        {
            txtTIN.text=resources.getString(R.string.tin_number)+" "+data.userBusinessInfo!!.tinNumber
        }

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.idType))
        {
            txtIDType.text=resources.getString(R.string.id_type)+" "+returnIdType(data.userBusinessInfo!!.idType)
        }

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.idNumber))
        {
            txtIdNo.text=resources.getString(R.string.id_number)+" "+data.userBusinessInfo!!.idNumber
        }


    }

    private fun returnIdType(idType:String): String {
        return when (idType) {
            "passport"          -> "Passport"
            "driving_license"   -> "Drivers License"
            "national_id"       -> "National ID"
            else                -> "Voters ID"
        }
    }

    private fun userInformationPersonal(userIdsInfo: ArrayList<ResUserInfo.UserIdsPersonalInfo>)
    {

        val totalSize = 0 until userIdsInfo.size

        for(i in totalSize)
        {
            if(userIdsInfo[i].verified==1)
            {
                loadIdInfoPersonal(userIdsInfo[i])
                break
            }
            else
            {
                loadIdInfoPersonal(userIdsInfo[i])
                break
            }

        }


    }

    private fun loadIdInfoPersonal(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo)
    {
        try {

            val options = RequestOptions()
            options.error(R.drawable.ic_id_no_image)
            Glide.with(activity!!)
                    .load(resources.getString(R.string.upload_business_url)+userIdsPersonalInfo.fileName)
                    .apply(options)
                    .into(imgID)

        }
        catch (exception: Exception)
        {

        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.idTypeName))
        {
            txtIdType.text      = userIdsPersonalInfo.idTypeName
        }
        if(!TextUtils.isEmpty(userIdsPersonalInfo.idNumber))
        {
            txtIdNumber.text    = userIdsPersonalInfo.idNumber
        }
        if(!TextUtils.isEmpty(userIdsPersonalInfo.created))
        {
            txtCreatedTime.text = userIdsPersonalInfo.created
        }
    }

    private fun loadIdInfoBusiness(userIdsPersonalInfo: ResUserInfo.UserIdsBusinessInfo)
    {
        try {

            val options = RequestOptions()
            options.error(R.drawable.ic_id_no_image)
            Glide.with(activity!!)
                    .load(resources.getString(R.string.profile_image_url)+userIdsPersonalInfo.fileName)
                    .apply(options)
                    .into(imgID)


        }
        catch (exception: Exception)
        {

        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.idTypeName))
        {
            txtIdType.text      = userIdsPersonalInfo.idTypeName
        }
        if(!TextUtils.isEmpty(userIdsPersonalInfo.idNumber))
        {
            txtIdNumber.text    = userIdsPersonalInfo.idNumber
        }
        if(!TextUtils.isEmpty(userIdsPersonalInfo.created))
        {
            txtCreatedTime.text = userIdsPersonalInfo.created
        }
    }

    private fun userInformationBusiness(userIdsBusinessInfo: ArrayList<ResUserInfo.UserIdsBusinessInfo>)
    {
        val totalSize = 0 until userIdsBusinessInfo.size

        for(i in totalSize)
        {
            if(userIdsBusinessInfo[i].verified==1)
            {
                loadIdInfoBusiness(userIdsBusinessInfo[i])
                break
            }
            else
            {
                loadIdInfoBusiness(userIdsBusinessInfo[i])
                break
            }

        }

    }


}

