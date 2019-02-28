package com.app.l_pesa.profile.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.profile.inter.ICallBackUserInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception


class ProfileFragment: Fragment(), ICallBackUserInfo {


    companion object {
        fun newInstance(): Fragment {
            return ProfileFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileInfo()
    }

    private fun loadProfileInfo()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
           val presenterUserInfo= PresenterUserInfo()
            presenterUserInfo.getProfileInfo(activity!!,this)
        }
    }

    override fun onSuccessUserInfo(data: ResUserInfo.Data) {

        setData(data)
    }

    override fun onErrorUserInfo(message: String) {

    }

    private fun setData(data: ResUserInfo.Data)
    {

        Toast.makeText(activity,"",Toast.LENGTH_SHORT).show()
        try {

            val options = RequestOptions()
            Glide.with(activity!!)
                    .load(data.userInfo.profileImage)
                    .apply(options)
                    .into(imgProfile)
        }
        catch (exception: Exception)
        {

        }
    }
}

