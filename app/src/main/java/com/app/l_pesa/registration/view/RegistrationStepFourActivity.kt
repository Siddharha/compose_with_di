package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.adapter.TitleListAdapter
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.zoop.ICallBackZoop
import com.app.l_pesa.zoop.PresenterZoop
import com.app.l_pesa.zoop.ZoopInitFailureResponse
import com.app.l_pesa.zoop.ZoopInitResponse
import com.app.l_pesa.registration.adapter.PersonalIdListAdapter
import com.app.l_pesa.registration.inter.ICallBackRegisterFour
import com.app.l_pesa.registration.model.RegisterPageIdListResp
import com.app.l_pesa.registration.presenter.PresenterRegistrationFour
import kotlinx.android.synthetic.main.activity_registration_step_four.*
import kotlinx.android.synthetic.main.confirm_aadhaar_layout.*
import kotlinx.android.synthetic.main.fragment_personal_id_layout.*
import kotlinx.android.synthetic.main.layout_registration_step_four.*
import kotlinx.android.synthetic.main.layout_registration_step_four.etIdNumber
import kotlinx.android.synthetic.main.layout_registration_step_four.rootLayout
import org.json.JSONException
import org.json.JSONObject
import sdk.zoop.one.offline_aadhaar.zoopActivity.ZoopConsentActivity
import sdk.zoop.one.offline_aadhaar.zoopUtils.ZoopConstantUtils

class RegistrationStepFourActivity : AppCompatActivity(), ICallBackId, ICallBackRegisterFour, ICallBackZoop {

    private lateinit var progressDialog: ProgressDialog
    lateinit var idList:ArrayList<String>
    lateinit var idNameList:ArrayList<String> /*= arrayListOf("Passport", "Driving License", "National ID","Voter ID")*/
    private var typeId     =""
    lateinit var pref:SharedPref
    private var zoop_id = ""
    private var zoop_aadhaar_no = ""
    private lateinit var titleAdapter:PersonalIdListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_four)
        setSupportActionBar(toolbar)
        toolbarFont(this@RegistrationStepFourActivity)
        initialize()
        initLoader()
        getIdList()
        initUI()

    }

    private fun initialize(){
        pref = SharedPref(this)

        idNameList = ArrayList()
        idList = ArrayList()
    }
    private fun getIdList() {
        progressDialog.show()
        PresenterRegistrationFour().doGetIdList(this,pref.countryCode,this)
    }

    private fun showId()
    {
        val dialog= Dialog(this@RegistrationStepFourActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        titleAdapter                = PersonalIdListAdapter(this@RegistrationStepFourActivity, idList,idNameList,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepFourActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun initUI()
    {
       // showId()

        etIdType.setOnClickListener {

            showId()
        }

        btnSubmit.setOnClickListener {

            try {
                CommonMethod.hideKeyboardView(this@RegistrationStepFourActivity)
            } catch (exp: Exception) {

            }

           /* if(TextUtils.isEmpty(etIdNumber.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFourActivity, resources.getString(R.string.required_id_number))
            }
            else
            {*/
                if (CommonMethod.isNetworkAvailable(this@RegistrationStepFourActivity)) {

                    if(pref.countryCode =="in" && etIdType.text.toString() == "Aadhaar Card"){
                        progressDialog.show()
                        val presenterZoop = PresenterZoop()
                        presenterZoop.doOfflineAadharInit(this, this)
                    }else {
                    val bundle     = Bundle()
                    bundle.putString("id_type",typeId)
                    bundle.putString("id_number",zoop_aadhaar_no)
                    val intent = Intent(this@RegistrationStepFourActivity, RegistrationStepFiveActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent,bundle)
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    }
                }
                else{
                    CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFourActivity, resources.getString(R.string.no_internet))
                }
            //}

        }

    }


    override fun onClickIdType(position: Int, type: String) {
            etIdType.setText(idNameList[position])
            typeId = idList[position]

        if(pref.countryCode =="in" && etIdType.text.toString() == "Aadhaar Card"){
            tilNumber.visibility = View.GONE
        }else{
            tilNumber.visibility = View.VISIBLE
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

    override fun onBackPressed() {

    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@RegistrationStepFourActivity::class.java.simpleName)

    }

    override fun onSuccessIdListResp(list: List<RegisterPageIdListResp.Data.IdType>) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        idNameList.clear()
        idList.clear()
       for(itm in list){
           idNameList.add(itm.name)
           idList.add(itm.id.toString())
       }
        titleAdapter.notifyDataSetChanged()
    }

    override fun onErrorIdListResp(jsonMessage: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        Log.e("resp",jsonMessage)
    }

    private fun initLoader() {
        progressDialog = ProgressDialog(this, R.style.MyAlertDialogStyle)
        val message = SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun openOfflineAadhaarActivity() {
        val gatewayIntent =  Intent(this, ZoopConsentActivity::class.java)
        gatewayIntent.putExtra(ZoopConstantUtils.ZOOP_TRANSACTION_ID, pref.zoopGatewayId)
        gatewayIntent.putExtra(ZoopConstantUtils.ZOOP_BASE_URL, "prod.aadhaarapi.com")
        // gatewayIntent.putExtra(ZOOP_EMAIL, Email) //not mandatory
//gatewayIntent.putExtra(ZOOP_UID, uid); //not mandatory
        //   gatewayIntent.putExtra(ZOOP_PHONE, phone); //not mandatory
        //  gatewayIntent.putExtra(ZOOP_IS_ASSIST_MODE_ONLY, false); //not mandatory
        gatewayIntent.putExtra(ZoopConstantUtils.ZOOP_REQUEST_TYPE, ZoopConstantUtils.OFFLINE_AADHAAR)
        startActivityForResult(gatewayIntent, ZoopConstantUtils.REQUEST_AADHAARAPI)
    }


    override fun onSucessInit(response: ZoopInitResponse) {
        if (progressDialog.isShowing){
            progressDialog.dismiss()
        }
        pref.zoopGatewayId = response.data.response.id   //setting zoop trans id to pref.
        openOfflineAadhaarActivity()
    }

    override fun onFailureInit(response: ZoopInitFailureResponse) {

    }

    override fun onUnknownErr(mgs: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            ZoopConstantUtils.REQUEST_AADHAARAPI ->
            {
                var requestType = "null";
                if (data?.hasExtra(ZoopConstantUtils.ZOOP_REQUEST_TYPE)!!) {
                    requestType = data.getStringExtra(ZoopConstantUtils.ZOOP_REQUEST_TYPE)!!
                    //Log.d(ZOOP_TAG, " res 1" + requestType);
                } else {
                    //Log.d(ZOOP_TAG, " res 1" + requestType);
                }

                if (resultCode == RESULT_CANCELED) {
                    val responseString = data . getStringExtra (ZoopConstantUtils.ZOOP_RESULT)
                    CommonMethod.customSnackBarError(rootLayout, this, "Aadhaar Not Verified!")
                    Log.e("SDK test error ", requestType + " err " + responseString + resultCode);
                }

                if (requestType.equals(ZoopConstantUtils.OFFLINE_AADHAAR,true)) {
                    //String responseString1 = data.getStringExtra(ZOOP_RESULT);
                    if (resultCode == ZoopConstantUtils.OFFLINE_AADHAAR_SUCCESS) {
                        val responseString = data. getStringExtra (ZoopConstantUtils.ZOOP_RESULT)
//                            tvResult.setVisibility(View.VISIBLE)
//                            resultDisplayLayout.setVisibility(View.VISIBLE);
//                            llResultBg.setVisibility(View.VISIBLE);

                        try {
                            val jsonObject =  JSONObject(responseString!!)
                            //parseResultJson(jsonObject)
                            showConfirmAadhaarPopup(jsonObject)
                            CommonMethod.customSnackBarSuccess(rootLayout, this, "Aadhaar verified with ${jsonObject.getString("id")}")
                        } catch ( e: JSONException) {
                            e.printStackTrace();
                        }

                        // tvResult.setText(String.format("complete Response: %s", responseString));
                        // Log.d("SDK test result ", requestType + " res " + responseString);
                    }

                    if (resultCode == ZoopConstantUtils.OFFLINE_AADHAAR_ERROR) {
                        val errorString = data . getStringExtra (ZoopConstantUtils.ZOOP_RESULT)

                        try {
                            val jResp = JSONObject(errorString!!)
                            //sdk_response
                            CommonMethod.customSnackBarError(rootLayout, this, "${jResp.getString("sdk_response")}")
                            // tvResult.setText(errorString);
//                            tvResult.setVisibility(View.VISIBLE);
//                            resultDisplayLayout.setVisibility(View.GONE);
//                            llResultBg.setVisibility(View.GONE);
//                            tvResult.setText(errorString);
                            Log.d("SDK test error ", requestType + " err " + errorString);
                        }catch (e:java.lang.Exception){
                            e.printStackTrace()
                        }
                    }
                } else {
                    // Log.d(ZOOP_TAG, " res 1" + requestType);
                }
            }
        }


    }

    private fun showConfirmAadhaarPopup(jsonObject: JSONObject) {
        val jsonObjectTrans = jsonObject.optJSONObject("transaction_data")
        val imgUser = jsonObjectTrans?.optString("Image")
        val AadharNo = jsonObjectTrans?.optString("AadhaarInfo")
        val AddressEnglish = jsonObjectTrans?.optString("AddressEnglish")
        val EmailInfo = jsonObjectTrans?.optString("EmailInfo")
        val PhoneInfo = jsonObjectTrans?.optString("PhoneInfo")
        val UserSelfie = jsonObjectTrans?.optString("UserSelfie")

        val basicInfo = jsonObjectTrans?.optJSONObject("BasicInfo")
        val UserName = basicInfo?.optString("Name")
        val DOB = basicInfo?.optString("DOB")
        val Gender = basicInfo?.optString("Gender")
         zoop_id = jsonObject.optString("id")
        zoop_aadhaar_no = AadharNo!!
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.confirm_aadhaar_layout)

        with(dialog){

            val imageBytes = Base64.decode(imgUser, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes?.size!!)
            imgAadhaar.setImageBitmap(decodedImage)

            val imageBytesSelfy = Base64.decode(imgUser, Base64.DEFAULT)
            val decodedImageSelfy = BitmapFactory.decodeByteArray(imageBytesSelfy, 0, imageBytesSelfy?.size!!)
            imgUserSelfy.setImageBitmap(decodedImageSelfy)
            tvAddress.text = AddressEnglish
            tvEmail.text = if(EmailInfo?.isNotBlank()!!) EmailInfo else "---"
            tvPhone.text =  if(PhoneInfo?.isNotBlank()!!) PhoneInfo else "---"
            tvAadhaar.text = AadharNo
            tvName.text = UserName
            tvDob.text = DOB
            tvGender.text = Gender

            yesBtn.setOnClickListener {
                val bundle     = Bundle()
                bundle.putString("id_type",typeId)
                bundle.putString("id_number",zoop_aadhaar_no)
                bundle.putString("zoop_id",zoop_id)
                //TODO: need to pass zoop id to final reg. before that have to confirm in api this param already there or not?
                val intent = Intent(this@RegistrationStepFourActivity, RegistrationStepFiveActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent,bundle)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
                dialog.dismiss()
            }

        }
//        val body = dialog.findViewById(R.id.body) as TextView
//        body.text = title
//        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
//        val noBtn = dialog.findViewById(R.id.noBtn) as TextView
//        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

}

