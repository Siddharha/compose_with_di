package com.app.l_pesa.lpk.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.fragment_lpk.*

class LpkFragment: Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return LpkFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_lpk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }

    private fun initData()
    {
        cardSavings.setOnClickListener {
            startActivity(Intent(activity, LPKSavingsActivity::class.java))
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

        }
    }
}