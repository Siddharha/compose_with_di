package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.pinview.model.LoginData
import com.app.l_pesa.profile.inter.ICallBackProfileFinValidate
import com.app.l_pesa.profile.inter.ICallBackUserInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterProfile
import com.app.l_pesa.profile.presenter.PresenterUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.facebook.shimmer.Shimmer
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.imgProfile
import kotlinx.android.synthetic.main.fragment_profile.txtDOB
import java.util.*


class ProfileFragment: Fragment(), ICallBackUserInfo, ICallBackProfileFinValidate {


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
        onActionPerform()
    }

    private fun onActionPerform() {
        swIsEmp.setOnCheckedChangeListener { s, isChecked ->

            val jsonObject = JsonObject()
            jsonObject.addProperty("business_info", isChecked)
            val presenterBusinessValidator = PresenterProfile()
            presenterBusinessValidator.setIsEmp(requireContext(), jsonObject,this)



        }

        swIsBusiness.setOnCheckedChangeListener { s, isChecked ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("employment_info", isChecked)
            val presenterEmployeeValidator = PresenterProfile()

            presenterEmployeeValidator.setHasBusiness(requireContext(), jsonObject,this)
        }

        imgEditMoreOfYou.setOnClickListener {
            //edit more about you
            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
            {
                startActivity(Intent(activity, ProfileEditMoreAboutActivity::class.java))
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }
        }
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
        loadProfileInfo(false)
        }
    }

    private fun loadProfileInfo(shimmerStatus: Boolean)
    {

        Handler(Looper.myLooper()!!).postDelayed({
            (activity as DashboardActivity).visibleFilter(false)
            (activity as DashboardActivity).visibleButton(false)
        }, 200)

        val logger = AppEventsLogger.newLogger(activity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Profile Information")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        val options = RequestOptions()
        options.placeholder(R.drawable.ic_user)
        Glide.with(requireActivity())
                .load("")
                .apply(options)
                .into(imgProfile)

        if(CommonMethod.isNetworkAvailable(requireActivity()))
        {
            if(shimmerStatus)
            {
                shimmerLayout.startShimmer()
            }
            else
            {
                shimmerLayout.stopShimmer()
            }

            val presenterUserInfo= PresenterUserInfo()
            presenterUserInfo.getProfileInfo(requireActivity(),this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing=false
            shimmerLayout.startShimmer()
            customSnackBarError(llRoot,resources.getString(R.string.no_internet))
        }

        imgEditPersonalInfo.setOnClickListener {

            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
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

            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
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

            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
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

            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
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

            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
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

            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
            {
                    startActivity(Intent(activity, ProfileEditIdInfoActivity::class.java))
                    activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(llRoot,resources.getString(R.string.please_wait))
            }

        }

        imgEditStInfo.setOnClickListener {
            if(!swipeRefreshLayout.isRefreshing && !shimmerLayout.isShimmerStarted)
            {
                startActivity(Intent(activity, ProfileEditStatementInfoActivity::class.java))
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
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(requireActivity()).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }

    override fun onSuccessUserInfo(data: ResUserInfo.Data) {

         swipeRefreshLayout.isRefreshing=false
        if (shimmerLayout.isShimmerStarted) {
            shimmerLayout.stopShimmer()
                    shimmerLayout.setShimmer(Shimmer.AlphaHighlightBuilder()
                                    .setBaseAlpha(1f)
                                    .setIntensity(0f)
                                    .build())
            shimmerLayout.stopShimmer()
            shimmerLayout.clearAnimation()
        }
        setData(data)
    }

    override fun onErrorUserInfo(message: String) {
        shimmerLayout.stopShimmer()
        swipeRefreshLayout.isRefreshing=false
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSessionTimeOut(jsonMessage: String) {

        shimmerLayout.stopShimmer()
        swipeRefreshLayout.isRefreshing=false
        val dialogBuilder = AlertDialog.Builder(requireActivity(),R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(requireActivity())
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity, MainActivity::class.java))
                    requireActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    requireActivity().finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }

    override fun onFailureHasBusiness(jsonMessage: String) {
        swIsBusiness.isChecked = !swIsBusiness.isChecked
    }

    override fun onSuccessHasBusiness(jsonMessage: String) {
        cvBAndPInfo.visibility = if(swIsBusiness.isChecked) View.VISIBLE else View.GONE
    }

    override fun onFailureIsEmp(jsonMessage: String) {
        swIsEmp.isChecked = !swIsEmp.isChecked

    }

    override fun onSuccessIsEmp(jsonMessage: String) {
        empInfo.visibility = if(swIsEmp.isChecked) View.VISIBLE else View.GONE

    }

    @SuppressLint("SetTextI18n")
    fun setData(data: ResUserInfo.Data)
    {

        swIsEmp.isChecked = data.employeeInfo!!
        swIsBusiness.isChecked = data.businessInfo!!

        empInfo.visibility = if(data.employeeInfo!!) View.VISIBLE else View.GONE
        cvBAndPInfo.visibility = if(data.businessInfo!!) View.VISIBLE else View.GONE

        val sharedPrefOBJ= SharedPref(requireActivity())
        val profileData            = Gson().toJson(data)
        sharedPrefOBJ.profileInfo         = profileData

        getProfileInfo(data)

        val pImg = data.userInfo!!.profileImage
        /*Profile Information*/

        if(pImg.contains("http",false)){
            try {
                val options = RequestOptions()
                Glide.with(requireActivity())
                        .load(pImg)
                        .apply(options)
                        .into(imgProfile)
            }
            catch (exception: Exception)
            {

            }
        }else{
            try {
                val options = RequestOptions()
                Glide.with(requireActivity())
                        .load(BuildConfig.PROFILE_IMAGE_URL+data.userInfo!!.profileImage)
                        .apply(options)
                        .into(imgProfile)
            }
            catch (exception: Exception)
            {

            }
        }



        if(!TextUtils.isEmpty(data.userPersonalInfo!!.emailAddress))
        {
            txtEmail.text=data.userPersonalInfo!!.emailAddress
        }

        txtPhone.text = data.userInfo!!.phoneNumber
        txtCreditScore.text = resources.getString(R.string.credit_score)+" "+data.userInfo!!.creditScore


        if(!TextUtils.isEmpty(data.userPersonalInfo!!.dob))
        {
            txtDOB.text = resources.getString(R.string.date_of_birth)+" "+data.userPersonalInfo!!.dob
        }
        else
        {
            txtDOB.text =resources.getString(R.string.date_of_birth)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userPersonalInfo!!.sex))
        {
            if(data.userPersonalInfo!!.sex=="M")
            {
                txtGender.text = resources.getString(R.string.gender)+" Male"
            }
            else
            {
                txtGender.text = resources.getString(R.string.gender)+" Female"
            }

        }
        else
        {
            txtGender.text = resources.getString(R.string.gender)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userPersonalInfo!!.meritalStatus))
        {
            txtMartialStatus.text = resources.getString(R.string.marital_status)+" "+data.userPersonalInfo!!.meritalStatus
        }
        else
        {
            txtMartialStatus.text =resources.getString(R.string.marital_status)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userPersonalInfo!!.motherMaidenName))
        {
            txtMotherName.text = resources.getString(R.string.mother_maiden_name)+" "+data.userPersonalInfo!!.motherMaidenName
        }
        else
        {
            txtMotherName.text =resources.getString(R.string.mother_maiden_name)+" "+resources.getText(R.string.dash_line)
        }


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
                txtIdType.text=resources.getText(R.string.dash_line)
                txtIdNumber.text=resources.getText(R.string.dash_line)
                txtCreatedTime.text=resources.getText(R.string.dash_line)
            }
        }

        //Statement info
        tvNoOfSt.text = "No of uploaded statements: ${data.totalStatements}"


        /*Contact Information*/

        if(!TextUtils.isEmpty(data.userContactInfo!!.city) && !TextUtils.isEmpty(data.userContactInfo!!.postalAddress))
        {
            txtAddress.text=data.userContactInfo!!.streetAddress+", "+data.userContactInfo!!.city+"- "+data.userContactInfo!!.postalAddress
        }
        else
        {
            txtAddress.text=resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userContactInfo!!.phoneNumber))
        {
            txtContact.text=data.userContactInfo!!.phoneNumber
        }
        else
        {
            txtContact.text=resources.getText(R.string.dash_line)
        }


        /* Employment Info*/

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.employerType))
        {
            txtEmployeeType.text=resources.getString(R.string.type_of_employer)+" "+data.userEmploymentInfo!!.employerType
        }
        else
        {
            txtEmployeeType.text=resources.getString(R.string.type_of_employer)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.employerName))
        {
            txtEmployeeName.text=resources.getString(R.string.name_of_employer)+" "+data.userEmploymentInfo!!.employerName
        }
        else
        {
            txtEmployeeName.text=resources.getString(R.string.name_of_employer)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.department))
        {
            txtDepartment.text=resources.getString(R.string.department)+" "+data.userEmploymentInfo!!.department
        }
        else
        {
            txtDepartment.text=resources.getString(R.string.department)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.position))
        {
            txtOccupation.text = resources.getString(R.string.occupation)+" "+ data.userEmploymentInfo!!.position
        }
        else
        {
            txtOccupation.text=resources.getString(R.string.occupation)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.employeesIdNumber))
        {
            txtEmployeeID.text=resources.getString(R.string.employees_id_no)+" "+data.userEmploymentInfo!!.employeesIdNumber
        }
        else
        {
            txtEmployeeID.text=resources.getString(R.string.employees_id_no)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userEmploymentInfo!!.city))
        {
            txtEmployeeCity.text=resources.getString(R.string.city)+" "+data.userEmploymentInfo!!.city
        }
        else
        {
            txtEmployeeCity.text=resources.getString(R.string.city)+" "+resources.getText(R.string.dash_line)
        }



        /* Business Info*/

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.businessName))
        {
            txtBusinessName.text=resources.getString(R.string.business_name)+" "+data.userBusinessInfo!!.businessName
        }
        else
        {
            txtBusinessName.text=resources.getString(R.string.business_name)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.tinNumber))
        {
            txtTIN.text=resources.getString(R.string.tin_number)+" "+data.userBusinessInfo!!.tinNumber
        }
        else
        {
            txtTIN.text=resources.getString(R.string.tin_number)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.idType))
        {
            txtBusinessIdType.text=resources.getString(R.string.id_type)+" "+returnIdType(data.userBusinessInfo!!.idType)
        }
        else
        {
            txtBusinessIdType.text=resources.getString(R.string.id_type)+" "+resources.getText(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(data.userBusinessInfo!!.idNumber))
        {
            txtIdNo.text=resources.getString(R.string.id_number)+" "+data.userBusinessInfo!!.idNumber
        }
        else
        {
            txtIdNo.text=resources.getString(R.string.id_number)+" "+resources.getText(R.string.dash_line)
        }


    }

    private fun getProfileInfo(data: ResUserInfo.Data)
    {
        val sharedPrefOBJ= SharedPref(requireActivity())
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        userData.user_personal_info.first_name      =data.userPersonalInfo!!.firstName
        userData.user_personal_info.middle_name     =data.userPersonalInfo!!.middleName
        userData.user_personal_info.last_name       =data.userPersonalInfo!!.lastName
        userData.user_personal_info.profile_image   =data.userPersonalInfo!!.profileImage
        userData.user_info.credit_score             =data.userInfo!!.creditScore
        val json = Gson().toJson(userData)
        sharedPrefOBJ.userInfo = json

        (requireActivity() as DashboardActivity).initData()
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
            options.placeholder(R.drawable.ic_id_no_image)

            Glide.with(requireActivity())
                    .load(BuildConfig.BUSINESS_IMAGE_URL+userIdsPersonalInfo.fileName)
                    .apply(options)
                    .into(imgInformation)

        }
        catch (exception: Exception)
        {

        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.idTypeName))
        {
            txtIdType.text    = userIdsPersonalInfo.idTypeName
        }
        else
        {
            txtIdType.text=resources.getString(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.idNumber))
        {
            txtIdNumber.text    = userIdsPersonalInfo.idNumber
        }
        else
        {
            txtIdNumber.text=resources.getString(R.string.dash_line)
        }
        if(!TextUtils.isEmpty(userIdsPersonalInfo.created))
        {
            txtCreatedTime.text = userIdsPersonalInfo.created
        }
        else
        {
            txtCreatedTime.text=resources.getString(R.string.dash_line)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadIdInfoBusiness(userIdsPersonalInfo: ResUserInfo.UserIdsBusinessInfo)
    {
        try {

            val options = RequestOptions()
            options.error(R.drawable.ic_id_no_image)
            options.placeholder(R.drawable.ic_id_no_image)
            Glide.with(requireActivity())
                    .load(BuildConfig.BUSINESS_IMAGE_URL+userIdsPersonalInfo.fileName)
                    .apply(options)
                    .into(imgInformation)


        }
        catch (exception: Exception)
        {

        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.idTypeName))
        {
            txtIdType.text = userIdsPersonalInfo.idTypeName
        }
        else
        {
            txtIdType.text =resources.getString(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.idNumber))
        {
            txtIdNumber.text    = userIdsPersonalInfo.idNumber
        }
        else
        {
            txtIdNumber.text=resources.getString(R.string.dash_line)
        }

        if(!TextUtils.isEmpty(userIdsPersonalInfo.created))
        {
            txtCreatedTime.text = userIdsPersonalInfo.created
        }
        else
        {
            txtCreatedTime.text=resources.getString(R.string.dash_line)
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

