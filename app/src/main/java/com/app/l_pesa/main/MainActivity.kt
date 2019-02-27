package com.app.l_pesa.main

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.app.l_pesa.R
import android.support.v4.content.ContextCompat
import android.content.Intent
import android.location.*
import com.app.l_pesa.common.RunTimePermission
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.view.LoginActivity
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private var runTimePermission: RunTimePermission? = null
    private val permissionCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initUI()
        runTimePermission       =  RunTimePermission(this@MainActivity)
        if (!runTimePermission!!.checkPermissionForPhoneState() && !runTimePermission!!.checkPermissionForAccessFineLocation())
        {
            requestPermission()
        }
        else
        {
            initCurrentLocation()
        }
    }

    private fun requestPermission() {


        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)

    }


    private fun initCurrentLocation()
    {
        startLocationTrackerService()
    }

    private fun initUI()
    {
        buttonLogin.setOnClickListener {

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)

        }
    }

    private fun startLocationTrackerService()
    {
        val locationRequest                 = LocationRequest()
        val intervalLocation: Long          = 100
        locationRequest.interval            = intervalLocation
        val intervalLocationFastest: Long   = 50
        locationRequest.fastestInterval     = intervalLocationFastest
        locationRequest.priority            = LocationRequest.PRIORITY_HIGH_ACCURACY

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermission == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null)
                    {
                        geoCoderCountry(location.latitude,location.longitude)

                    } else
                    {
                        // Set Default
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)


                }

            }, null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initCurrentLocation()
            } else
            {

            }
               // Log.e(LOG_TAG, "grantResults does not match:" + grantResults.size)
        } else
        {}
           // Log.e(LOG_TAG, "requestCode does not match:$requestCode")
    }


    private fun geoCoderCountry( lat:Double, lng:Double)
    {
        val sharedPref=SharedPref(this@MainActivity)
        try
        {
            val geoCoder    = Geocoder(this@MainActivity, Locale.getDefault())
            val geoAddress :List<Address> = geoCoder.getFromLocation(lat, lng, 1)

            if(geoAddress.isNotEmpty())
            {
                val geoObject   = geoAddress[0]
                val countryCode   = geoObject.countryCode.toLowerCase()
                sharedPref.countryCode  = countryCode

            }

        }
        catch ( e: IOException)
        {

        }

    }

    override fun onBackPressed() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }


}
