package com.example.alinadiplom.kvector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.alinadiplom.R
import org.apache.poi.sl.usermodel.Background

class KVector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private lateinit var background: Bitmap
    var radius = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        radius = measuredWidth/2
        drawBackground(canvas)
        drawVector(canvas)

    }

    private fun drawBackground(canvas: Canvas?) {
        background = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.zones),
            radius*2, radius, true
        )
        canvas?.drawBitmap(background, 0f, 0f, null)
    }

    private fun drawVector(canvas: Canvas?) {
        //if ()
    }

    //private fun draw
}