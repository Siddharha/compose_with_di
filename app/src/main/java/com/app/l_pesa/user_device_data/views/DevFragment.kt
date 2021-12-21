package com.app.l_pesa.user_device_data.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod.isServiceRunning
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.user_device_data.inter.ICallBackUserPackageChangeUpdate
import com.app.l_pesa.user_device_data.models.UserInstalledPackageResponse
import com.app.l_pesa.user_device_data.presenter.PresenterMLService
import com.app.l_pesa.user_device_data.services.MlService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_dev.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class DevFragment : Fragment() {

    private lateinit var pref:SharedPref
    private lateinit var rootView: View
    private val PERMISSION_CODE = 100
    private val serviceIntent:Intent by lazy{Intent(requireContext(), MlService::class.java)}
    val _presenterMLService:PresenterMLService by lazy { PresenterMLService() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }
    }

    private fun initialize(){
        pref = SharedPref(requireContext())
    }

    private fun loadUIwithData(){
        pref.isMlService = isServiceRunning(requireContext(),MlService::class.java)
        rootView.smObserver.isChecked = pref.isMlService
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       rootView = inflater.inflate(R.layout.fragment_dev, container, false)
        initialize()
        loadUIwithData()
        onActionPerform()
        return rootView
    }

    private fun onActionPerform() {
        rootView.smObserver.setOnCheckedChangeListener { _, isChecked ->


                if(isChecked){

                    if (
                            ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.READ_SMS
                            ) != PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.RECEIVE_SMS
                            ) != PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.READ_PHONE_STATE
                            ) != PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.READ_CALL_LOG
                            ) != PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.READ_PHONE_NUMBERS
                            ) != PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED

                        // Manifest.permission.READ_CALL_LOG
                    )
                    {
                        rootView.smObserver.isChecked = false
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.READ_CALL_LOG),PERMISSION_CODE)
                    }else{
                        doAsync {
                            requireContext().startService(serviceIntent)
                        }

                    }

                } else{
                    doAsync {
                        if(isServiceRunning(requireContext(),MlService::class.java)){
                        requireContext().stopService(serviceIntent)
                        }
                    }

                }




        }
        rootView.smDataSync.setOnCheckedChangeListener { _, isChecked ->
            doAsync {
                val appList = installedApps()
                //val callLogHhistory = getCallDetails()
                uiThread {
                   // Log.e("app list", appList.toString() )
                    uploadInstalledAppList(appList)

                  //  Log.e("call log details",callLogHhistory)
                }
            }
        }
    }

    private fun uploadInstalledAppList(
        packageObj: JsonObject
    ) {


        _presenterMLService.doPackageUpdate(
            requireContext(),
            packageObj,
            object : ICallBackUserPackageChangeUpdate {
                override fun onSuccessPackageUpdate(response: UserInstalledPackageResponse?) {

                    if(response?.status?.isSuccess!!){
                        Toast.makeText(requireContext(),response?.status?.message,Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onErrorPackagegUpdate(message: String) {

                }

                override fun onFailurePackageUpdate(jsonMessage: String) {

                }

                override fun onIncompletePackageUpdate(jsonMessage: String) {

                }

            })
    }


    private fun getCallDetails():String {

        val sb =  StringBuffer()
        val managedCursor = requireActivity().managedQuery(
            CallLog.Calls.CONTENT_URI, null,
        null, null, null)
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        sb.append("Call Details :")
        while (managedCursor.moveToNext()) {
            val phNumber = managedCursor?.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getString(date)
            val callDayTime =  Date(callDate.toLong())
            val callDuration = managedCursor.getString(duration);
            var dir:String? = null
            val dircode = Integer.parseInt(callType)
            when (dircode) {
                 CallLog.Calls.OUTGOING_TYPE ->{
                     dir = "OUTGOING"
                     break
                 }

                CallLog.Calls.INCOMING_TYPE -> {dir = "INCOMING"
                break
                }

                 CallLog.Calls.MISSED_TYPE ->{
                     dir = "MISSED"
                     break
                 }

            }
            sb.append(" Phone Number:--- " + phNumber + " Call Type:--- "
            + dir + " Call Date:--- " + callDayTime
            + " Call duration in sec :--- " + callDuration)
            sb.append("----------------------------------")
        }
        managedCursor.close()
        return sb.toString()

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun installedApps():JsonObject
    {
        val jsonObject = JsonObject()
       // val appList:ArrayList<String> by lazy { ArrayList() }


         val list = requireActivity().packageManager.getInstalledPackages(0)

        val jsonArray = JsonArray()
        for (i in list.indices) {
            val packageInfo = list[i]
            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(requireActivity().packageManager).toString()
                val appPackage = packageInfo.applicationInfo.packageName

                val packageObj = JsonObject()
                packageObj.addProperty("package_name",appName)
                packageObj.addProperty("app_name",appPackage)
                jsonArray.add(packageObj)
               // appList.add(appName)
               // Log.e("App List$i", appName)
//                arrayAdapter = ArrayAdapter(this,
//                    R.layout.support_simple_spinner_dropdown_item, list as List)
//                listView.adapter = arrayAdapter
            }



        }

        jsonObject.add("app_list",jsonArray)

        return jsonObject
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                DevFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}