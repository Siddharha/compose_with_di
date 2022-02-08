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
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.loanplan.view.LoanPlansFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_screen_slide_page_ad_1.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ScreenSlidePageAdFragment : Fragment() {

    //private val mBanner = banner
    lateinit var rootView:View
   // private var page = 0

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
//    companion object {
//        val pages by lazy{ ArrayList<Fragment>()}
//        fun instances(bannerItems: List<ResDashboard.Banner>?): List<Fragment> {
//            try {
//                for (index in bannerItems?.indices!!) {
//                    val f = ScreenSlidePageAdFragment(bannerItems[index])
//                    f.setPage(index)
//                    pages.add(f)
//                }
//                return pages
//            }catch (e:Exception){
//                return pages
//            }
//        }
//    }

    companion object{
      //  var instance: ScreenSlidePageAdFragment?=null
      //  val pages by lazy{ ScreenSlidePageAdFragment()}
        var newInstance : (banner:ResDashboard.Banner?)-> ScreenSlidePageAdFragment = {
          val args = Bundle()
          args.putSerializable("banner", it)
          val f = ScreenSlidePageAdFragment()
          f.arguments = args
          f
        }
    }

//    var getPage : (pageNo:Int)-> ScreenSlidePageAdFragment = {
//
//           // for (index in it?.indices!!) {
//                val f = ScreenSlidePageAdFragment(mBanner)
//                f.setPage(it)
//                //pages.add(f)
//          //  }
//            f
//
//    }

//    private fun setPage(p: Int) {
//        page = p
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val banner = arguments?.getSerializable("banner") as ResDashboard.Banner
            when(banner.type){
                "link" -> {
                    Glide.with(requireContext())
                        .load(banner.image)
                        .placeholder(R.drawable.lpk_banner)
                        .into(rootView.imgBanner)
                    rootView.imgBanner.setOnClickListener {
                        doAsync {
                            val uri = Uri.parse(banner.link)
                            val intent = Intent(Intent.ACTION_VIEW, uri)

                            uiThread {
                                startActivity(intent)

                            }
                        }


                    }
                }
                "component" ->{

                    if(banner.redirect == "loan_page"){
                        Glide.with(requireContext())
                            .load(banner.image)
                            .placeholder(R.drawable.doller_loan)
                            .into(rootView.imgBanner)
                        rootView.imgBanner.setOnClickListener {
                            doAsync {
                                pref.openTabLoan = "CURRENT"
                                uiThread {
                                    (context as DashboardActivity).navigateToFragment(LoanPlansFragment.newInstance())
                                }
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
