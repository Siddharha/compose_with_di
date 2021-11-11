package com.app.l_pesa.dashboard.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.l_pesa.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_screen_slide_page_ad_1.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ScreenSlidePageAdFragment(page:Int) : Fragment() {

    private val page = page
    private lateinit var rootView:View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)

        rootView.apply {
            when(page){
                0 -> {
                    Glide.with(requireContext()).load(R.drawable.lpk_banner).into(this.imgBanner)
                    this.setOnClickListener {
                        doAsync {
                            val uri = Uri.parse("https://ico.lpesa.io")
                            val intent = Intent(Intent.ACTION_VIEW, uri)

                            uiThread {
                                startActivity(intent)

                            }
                    }


                    }
                }
                1 ->{
                    Glide.with(requireContext()).load(R.drawable.doller_loan).into(this.imgBanner)
                }//
            }
        }

        return rootView


    }

//        if(page == 0){
//        inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
//    }else{
//        inflater.inflate(R.layout.fragment_screen_slide_page_ad_1, container, false)
//    }


}
