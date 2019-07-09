package com.app.l_pesa.calculator.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_loan_calculator.*
import android.text.Spannable
import com.app.l_pesa.common.CustomTypeFaceSpan
import android.text.SpannableString
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.animation.ValueAnimator


class LoanCalculatorFragment:Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return LoanCalculatorFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(com.app.l_pesa.R.layout.fragment_loan_calculator, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()
    }

    private fun initData()
    {

        ti_loan_type.typeface=Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf");
        ti_loan_type.setOnClickListener {

            val popupMenuOBJ = PopupMenu(activity!!, ti_loan_type)
            popupMenuOBJ.menuInflater.inflate(com.app.l_pesa.R.menu.menu_loan_type, popupMenuOBJ.menu)

            popupMenuOBJ.setOnMenuItemClickListener { item: MenuItem? ->

                ti_loan_type.setText(item!!.title)
                ti_loan_type.setBackgroundColor(Color.TRANSPARENT)
                ti_loan_type.backgroundTintList=(ContextCompat.getColorStateList(activity!!, android.R.color.transparent))

                true
            }

            val menu = popupMenuOBJ.menu
            for (i in 0 until menu.size()) {
                val mi = menu.getItem(i)
                applyFontToMenuItem(mi)
            }

            popupMenuOBJ.show()
        }


        seekBar.progress=50

        seekBar.setOnTouchListener { _, _ -> true }
    }

    private fun applyFontToMenuItem(mi: MenuItem) {

        val font = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypeFaceSpan("", font, Color.parseColor("#535559")), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

}