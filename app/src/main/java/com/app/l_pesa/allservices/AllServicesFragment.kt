package com.app.l_pesa.allservices

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.fragment_all_services.*


class AllServicesFragment : Fragment() {
    // TODO: Rename and change types of parameters
  private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_all_services, container, false)
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
        cvSasaDoctor.setOnClickListener {

        }
    }
}