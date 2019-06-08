package com.app.l_pesa.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.*

object BitmapResize {

    fun shrinkBitmap(ctx: Context, uri: Uri, width: Int, height: Int): Bitmap {
        var input: InputStream
        try {
            input = ctx.contentResolver.openInputStream(uri)!!
        } catch (e: FileNotFoundException) {
            throw IllegalStateException(e)
        }

        if (!input.markSupported()) {
            input = BufferedInputStream(input)
        }

        try {
            input.mark(input.available())
        } catch (e: IOException) {
            e.printStackTrace()
        }


        val bmpFactoryOptions = BitmapFactory.Options()
        bmpFactoryOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(input, null, bmpFactoryOptions)

        val heightRatio = Math.ceil((bmpFactoryOptions.outHeight / height.toFloat()).toDouble()).toInt()
        val widthRatio = Math.ceil((bmpFactoryOptions.outWidth / width.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false

        try {
            input.reset()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var bitmap = BitmapFactory.decodeStream(input, null, bmpFactoryOptions)
        bitmap = bitmap!!.rotateBitmapIfRequiredOne(ctx)

        return bitmap
    }

    private fun Bitmap.rotateBitmapIfRequiredOne(ctx: Context): Bitmap {
        var bitmap = this
        val imagePath = File(ctx.filesDir, "images")
        val imgFile = File(imagePath, "user.jpg")

        var exif: androidx.exifinterface.media.ExifInterface? = null
        try {

            exif = androidx.exifinterface.media.ExifInterface(imgFile.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        when (exif!!.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED)) {

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap, 90f)

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap, 180f)

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap, 270f)

            else -> println("No rotation required")
        }
        return bitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }



}
