package com.app.l_pesa.profile.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kotlinx.android.synthetic.main.activity_view_file.*
import kotlinx.android.synthetic.main.content_activity_view_file.*

class ActivityViewFile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_file)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ActivityViewFile)
        initData()

    }

    private fun initData()
    {
        val bundle       = intent.extras
        val fileName     = bundle!!.getString("FILE_NAME")

        val options = RequestOptions()
        options.error(R.drawable.ic_id_no_image)
        options.placeholder(R.drawable.ic_id_no_image)
        Glide.with(this@ActivityViewFile)
                .load(fileName)
                .apply(options)
                .into(imgFile)
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

}
