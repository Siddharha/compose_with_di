package com.app.l_pesa.dev_options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.fragment_dev.view.*

class DevFragment : Fragment() {

    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       rootView = inflater.inflate(R.layout.fragment_dev, container, false)
        onActionPerform()
        return rootView
    }

    private fun onActionPerform() {
        rootView.smObserver.setOnCheckedChangeListener { buttonView, isChecked ->

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