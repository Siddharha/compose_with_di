package com.app.l_pesa.user_device_data.broadcust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import com.app.l_pesa.user_device_data.inter.ICallBackUserPackageChangeUpdate
import com.app.l_pesa.user_device_data.models.UserInstalledPackageResponse
import com.app.l_pesa.user_device_data.presenter.PresenterMLService
import com.google.gson.JsonObject
import com.sinch.sanalytics.client.jni.ApplicationContext

class PackageChangeReceiver : BroadcastReceiver() {

    private val _presenterMLService :PresenterMLService by lazy { PresenterMLService() }

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.data?.encodedSchemeSpecificPart

        val appInfo = try{context?.applicationContext?.packageManager?.getApplicationInfo(packageName.toString(), PackageManager.GET_META_DATA)}catch (e:Exception){
            null
        }
        val appName = if(appInfo!=null){context?.applicationContext?.packageManager?.getApplicationLabel(appInfo)}else{
            ""
        }

        val jsonObject = JsonObject()
        val packageObj = JsonObject()
        packageObj.addProperty("package",packageName.toString())
        packageObj.addProperty("name",appName.toString() )

        when (intent?.action) {
            Intent.ACTION_PACKAGE_REMOVED -> {
                Toast.makeText(
                    context,
                    "${packageName.toString()} uninstalled",
                    Toast.LENGTH_SHORT
                ).show()

                changedPackageUpdate(packageObj, jsonObject, context,type = "uninstalled")
            }
            Intent.ACTION_PACKAGE_ADDED -> {
                Toast.makeText(
                    context,
                    "${packageName.toString()} installed",
                    Toast.LENGTH_SHORT
                ).show()

                changedPackageUpdate(packageObj, jsonObject, context,type = "installed")
            }
        }
    }

    private fun changedPackageUpdate(
        packageObj: JsonObject,
        jsonObject: JsonObject,
        context: Context?,
        type:String
    ) {
        packageObj.addProperty("status", type)

        jsonObject.add("package_change", packageObj)


        _presenterMLService.doPackageUpdate(
            context!!,
            jsonObject,
            object : ICallBackUserPackageChangeUpdate {
                override fun onSuccessPackageUpdate(response: UserInstalledPackageResponse?) {

                }

                override fun onErrorPackagegUpdate(message: String) {

                }

                override fun onFailurePackageUpdate(jsonMessage: String) {

                }

                override fun onIncompletePackageUpdate(jsonMessage: String) {

                }

            })
    }

}