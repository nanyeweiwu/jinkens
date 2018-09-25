package com.dzn.pointupdemo

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.dzn.pointupdemo.view.DivergeViewSecond
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mDivergeView: DivergeViewSecond? = null
    private var mBtnStart: Button? = null
    private var mImageView: ImageView? = null
    private var mList: ArrayList<Bitmap>? = null
    private var mIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnStart = findViewById(R.id.btnStart)
        mImageView = findViewById(R.id.iv_start)
        mList = ArrayList()
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mList!!.add((ResourcesCompat.getDrawable(resources, R.mipmap.like, null) as BitmapDrawable).bitmap)
        mBtnStart?.postDelayed({ mDivergeView!!.startDiverges(10) }, 1000)
        mBtnStart!!.setOnClickListener {
            //            if (mIndex == 5) {
//                mIndex = 0
//            }
//            mDivergeView!!.startDiverges(mIndex)
//            mIndex++
            this@MainActivity.startActivity(android.content.Intent(this@MainActivity, ThirdActivity::class.java))
        }
        mDivergeView = findViewById(R.id.divergeView)
        mDivergeView!!.post {
            mDivergeView!!.setEndPoint(PointF((mDivergeView!!.measuredWidth / 5).toFloat(), 0f))
            mDivergeView!!.setDivergeViewProvider(Provider())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mList != null) {
            mList!!.clear()
            mList = null
        }

    }

    internal inner class Provider : DivergeViewSecond.DivergeViewProvider {
        override fun getBitmap(obj: Any): Bitmap {
            return if (mList == null) null as Bitmap else mList!![obj as Int]
        }
    }
}