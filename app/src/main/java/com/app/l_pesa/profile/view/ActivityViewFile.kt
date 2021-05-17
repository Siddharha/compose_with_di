package com.app.l_pesa.profile.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.profile.model.ImageRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_view_file.*
import kotlinx.android.synthetic.main.content_activity_view_file.*

class ActivityViewFile : AppCompatActivity(), ImageRequestListener.Callback {


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
        val bundle      = intent.extras
        val fileName     = bundle!!.getString("FILE_NAME")
        loadImage(fileName!!)

    }

    private fun loadImage(fullImageUrl: String) {

        val requestOption = RequestOptions()
                   .placeholder(R.drawable.ic_id_no_image).centerCrop()

        Glide.with(this@ActivityViewFile).load(fullImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(Glide.with(this@ActivityViewFile)
                        .load(fullImageUrl)
                        .apply(requestOption))
                .apply(requestOption)
                .listener(ImageRequestListener(this))
                .into(imgFile)
    }

    override fun onFailure(message: String?) {

    }

    override fun onSuccess(dataSource: String) {

        progressBar.visibility= View.INVISIBLE
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
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

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@ActivityViewFile::class.java.simpleName)

    }

}
