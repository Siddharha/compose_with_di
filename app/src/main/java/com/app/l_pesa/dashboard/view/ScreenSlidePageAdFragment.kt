package com.app.l_pesa.dashboard.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanplan.view.LoanPlansFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_screen_slide_page_ad_1.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ScreenSlidePageAdFragment : Fragment() {


    private var page = 0
    private val pref:SharedPref by lazy { SharedPref(requireContext()) }
    //private lateinit var rootView:View
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        rootView = inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
//
//
//
//        return rootView
//
//
//    }
    companion object {
        fun defiInstance(): Fragment {
            val f = ScreenSlidePageAdFragment()
            f.setPage(0)
            return f
        }
        fun loanInstance(): Fragment {
            val f =ScreenSlidePageAdFragment()
            f.setPage(1)
            return f
        }
    }

    private fun setPage(p: Int) {
        page = p

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            when(page){
                0 -> {
                    Glide.with(requireContext()).load(R.drawable.lpk_banner).into(imgBanner)
                    imgBanner.setOnClickListener {
                        doAsync {
                            val uri = Uri.parse("https://www.lpkdefi.com")
                            val intent = Intent(Intent.ACTION_VIEW, uri)

                            uiThread {
                                startActivity(intent)

                            }
                        }


                    }
                }
                1 ->{
                    Glide.with(requireContext()).load(R.drawable.doller_loan).into(this.imgBanner)
                    imgBanner.setOnClickListener {
                        doAsync {
                            pref.openTabLoan = "CURRENT"
                            uiThread {
                                (context as DashboardActivity).navigateToFragment(LoanPlansFragment.newInstance())
                            }
                        }


                    }

                }//
            }

    }

//        if(page == 0){
//        inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
//    }else{
//        inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
//    }


}
