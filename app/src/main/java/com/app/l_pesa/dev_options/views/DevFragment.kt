package com.app.l_pesa.dev_options.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod.isServiceRunning
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dev_options.services.MlService
import kotlinx.android.synthetic.main.fragment_dev.view.*
import org.jetbrains.anko.doAsync

class DevFragment : Fragment() {

    private lateinit var pref:SharedPref
    private lateinit var rootView: View
    private val PERMISSION_CODE = 100
    private val serviceIntent:Intent by lazy{Intent(requireContext(), MlService::class.java)}
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
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    )
                    {
                        rootView.smObserver.isChecked = false
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),PERMISSION_CODE)
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