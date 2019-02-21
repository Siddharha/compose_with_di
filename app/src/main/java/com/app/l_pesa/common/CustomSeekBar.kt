package com.app.l_pesa.common

/**
 * Created by Intellij Amiya on 21/2/19.
 * Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
import java.util.ArrayList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet

import com.app.l_pesa.dashboard.model.SeekBarProgress


class CustomSeekBar : AppCompatSeekBar {

    private var mProgressItemsList: ArrayList<SeekBarProgress>? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    fun initData(progressItemsList: ArrayList<SeekBarProgress>) {
        this.mProgressItemsList = progressItemsList
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int,
                           heightMeasureSpec: Int) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (mProgressItemsList!!.size > 0) {
            val progressBarWidth = width
            val progressBarHeight = height
            val thumboffset = thumbOffset
            var lastProgressX = 0
            var progressItemWidth: Int
            var progressItemRight: Int
            for (i in mProgressItemsList!!.indices) {
                val progressItem = mProgressItemsList!![i]
                val progressPaint = Paint()
                progressPaint.color = ContextCompat.getColor(context,
                        progressItem.color)

                progressItemWidth = (progressItem.progressItemPercentage * progressBarWidth / 100).toInt()

                progressItemRight = lastProgressX + progressItemWidth

                // for last item give right to progress item to the width
                if (i == mProgressItemsList!!.size - 1 && progressItemRight != progressBarWidth) {
                    progressItemRight = progressBarWidth
                }
                val progressRect = Rect()
                progressRect.set(lastProgressX, thumboffset / 2,
                        progressItemRight, progressBarHeight - thumboffset / 2)
                canvas.drawRect(progressRect, progressPaint)
                lastProgressX = progressItemRight
            }
            super.onDraw(canvas)
        }

    }

}