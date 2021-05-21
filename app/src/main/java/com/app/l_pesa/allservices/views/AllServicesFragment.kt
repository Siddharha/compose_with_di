package com.app.l_pesa.allservices.views

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_all_services.view.*


class AllServicesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val sasaLogo = "https://play-lh.googleusercontent.com/nSizQBW5A6z19AHpKBYxKBoTLG9kQ9Sn80ixHpP7PgEmx1MJciKa002aFzYCxIjPpVs=s200-rw"
  private lateinit var rootView: View
  private  val pref: SharedPref by lazy{SharedPref(requireContext())}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun checkServiceVisibilityForCountry() {
        if(pref.countryName == "Kenya") {
            rootView.cvSasaDoctor.visibility = View.VISIBLE
            rootView.tvNoService.visibility = View.GONE

            Glide.with(rootView)
                    .load(sasaLogo)
                    .into(rootView.imgLogo)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_all_services, container, false)
        checkServiceVisibilityForCountry()
        onActionPerform()
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                AllServicesFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    private fun onActionPerform(){
        rootView.cvSasaDoctor.setOnClickListener {
            val intent = Intent(requireContext(), SasaDoctorActivity::class.java)
            intent.putExtra("logo",sasaLogo)
            startActivity(intent)
        }
    }
}