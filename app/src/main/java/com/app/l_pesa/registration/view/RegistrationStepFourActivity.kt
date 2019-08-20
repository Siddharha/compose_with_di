package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.TextView
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.registration.adapter.PersonalIdListAdapter
import kotlinx.android.synthetic.main.activity_registration_step_four.*
import kotlinx.android.synthetic.main.layout_registration_step_four.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RegistrationStepFourActivity : AppCompatActivity(), ICallBackId {


    private val idList     = arrayListOf("1","2","3","4")
    private val idNameList = arrayListOf("Passport", "Driving License", "National ID","Voter ID")
    private var typeId="0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_four)
        setSupportActionBar(toolbar)
        toolbarFont(this@RegistrationStepFourActivity)

        initUI()

    }

    private fun showId()
    {
        val dialog= Dialog(this@RegistrationStepFourActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = PersonalIdListAdapter(this@RegistrationStepFourActivity, idList,idNameList,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepFourActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun initUI()
    {
       // showId()

        etIdType.setOnClickListener {
            showId()
        }

        imageCard.setOnClickListener {

            startActivity(Intent(this@RegistrationStepFourActivity, RegistrationStepFiveActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        btnSubmit.setOnClickListener {

        }

        val sharedPref=SharedPref(this@RegistrationStepFourActivity)
        if(!TextUtils.isEmpty(sharedPref.imagePathPersonal))
        {
            println("DATA++"+sharedPref.imagePathPersonal)
            handleRotation(sharedPref.imagePathPersonal)

            val imageFile=File(sharedPref.imagePath)
            val imagePath = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageCard.setImageBitmap(imagePath)

        }

    }

    private fun handleRotation(imgPath: String) {
        BitmapFactory.decodeFile(imgPath)?.let { origin ->
            try {
                ExifInterface(imgPath).apply {
                    getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                    ).let { orientation ->
                        when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> origin.rotate(90f)
                            ExifInterface.ORIENTATION_ROTATE_180 -> origin.rotate(180f)
                            ExifInterface.ORIENTATION_ROTATE_270 -> origin.rotate(270f)
                            ExifInterface.ORIENTATION_NORMAL -> origin
                            else -> origin.rotate(270f)
                        }.also { bitmap ->
                            //Update the input file with the new bytes.
                            try {
                                FileOutputStream(imgPath).use { fos ->
                                    bitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100,
                                            fos
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
        return Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
        )
    }

    override fun onClickIdType(position: Int, type: String) {

        etIdType.setText(idNameList[position])
        typeId=idList[position]
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
}
