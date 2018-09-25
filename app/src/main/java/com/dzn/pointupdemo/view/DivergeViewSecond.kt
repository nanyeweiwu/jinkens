package com.dzn.pointupdemo.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import java.util.*

class DivergeViewSecond @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), Runnable {

    protected val mRandom = Random()

    protected var mDivergeInfos: ArrayList<DivergeInfo>? = null

    protected var mQueen: MutableList<Any>? = null

    var startPoint: PointF? = null
    protected var mPtEnd: PointF? = null

    protected var mDeadPool: ArrayList<DivergeInfo>? = ArrayList()

    private var mPaint: Paint? = null

    // private static final int mDefaultWidth = 100;
    // private static final int mAlphaOffset = 50;

    private var mDivergeViewProvider: DivergeViewProvider? = null

    private var mLastAddTime: Long = 0

    private var mThread: Thread? = null

    var isRunning = true
        private set

    private var mIsDrawing = false

    init {
        init()
    }


    /**
     * Loop
     */
    override fun run() {

        while (isRunning) {

            if (mDivergeViewProvider == null) {
                continue
            }

            if (mQueen == null) {
                continue
            }

            if (mIsDrawing) {
                //如果正在绘制，不要处理数据
                continue
            }

            if (mDivergeInfos == null) {
                continue
            }

            dealQueen()

            if (mDivergeInfos!!.size == 0) {
                continue
            }

            dealDiverge()

            mIsDrawing = true

            postInvalidate()

        }

        //停止
        release()
    }

    private fun dealDiverge() {
        var i = 0
        while (i < mDivergeInfos!!.size) {
            val divergeInfo = mDivergeInfos!![i]

            val timeLeft = 1.0f - divergeInfo.mDuration

            divergeInfo.mDuration += mDuration

            val x: Float
            val y: Float

            //二次贝塞尔
            val time1 = timeLeft * timeLeft
            val time2 = 2f * timeLeft * divergeInfo.mDuration
            val time3 = divergeInfo.mDuration * divergeInfo.mDuration
            x = (time1 * startPoint!!.x
                    + time2 * divergeInfo.mBreakPoint.x
                    + time3 * divergeInfo.mEndPoint.x)

            divergeInfo.mX = x

            y = (time1 * startPoint!!.y
                    + time2 * divergeInfo.mBreakPoint.y
                    + time3 * divergeInfo.mEndPoint.y)

            divergeInfo.mY = y

            if (divergeInfo.mY <= divergeInfo.mEndPoint.y) {
                mDivergeInfos!!.removeAt(i)
                mDeadPool!!.add(divergeInfo)
                i--
                i++
                continue
            }
            i++
        }
    }

    private fun dealQueen() {
        val now = System.currentTimeMillis()
        if (mQueen!!.size > 0 && now - mLastAddTime > mQueenDuration) {
            mLastAddTime = System.currentTimeMillis()
            var divergeInfo: DivergeInfo? = null
            if (mDeadPool!!.size > 0) {
                //死池里面有空闲的divergeNode
                divergeInfo = mDeadPool!![0]
                mDeadPool!!.removeAt(0)
            }
            if (divergeInfo == null) {
                divergeInfo = createDivergeNode(mQueen!![0])
            }
            divergeInfo.reset()
            divergeInfo.mType = mQueen!![0]
            mDivergeInfos!!.add(divergeInfo)
            mQueen!!.removeAt(0)
        }
    }

    interface DivergeViewProvider {
        fun getBitmap(obj: Any): Bitmap
    }


    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //不需要支持wrap_content

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

    }

    fun setDivergeViewProvider(divergeViewProvider: DivergeViewProvider) {
        mDivergeViewProvider = divergeViewProvider
    }

    fun startDiverges(obj: Any) {

        if (mDivergeInfos == null) {
            mDivergeInfos = ArrayList(30)
        }

        if (mQueen == null) {
            mQueen = Collections.synchronizedList(ArrayList(30))
        }

        mQueen!!.add(obj)
        // for(Object obj : objs) {
        //  mQueen.add(obj);
        // }

        if (mThread == null) {
            mThread = Thread(this)
            mThread!!.start()
        }
    }

    fun stop() {
        if (mDivergeInfos != null) {
            mDivergeInfos!!.clear()
        }

        if (mQueen != null) {
            mQueen!!.clear()
        }

        if (mDeadPool != null) {
            mDeadPool!!.clear()
        }

    }

    fun release() {
        stop()
        mPtEnd = null
        startPoint = null
        mDivergeInfos = null
        mQueen = null
        mDeadPool = null
    }

    fun setEndPoint(point: PointF) {
        mPtEnd = point
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning = false
    }

    override fun onDraw(canvas: Canvas) {

        if (isRunning && mDivergeViewProvider != null && mDivergeInfos != null) {
            for (divergeInfo in mDivergeInfos!!) {
                mPaint!!.alpha = (255 * divergeInfo.mY / startPoint!!.y).toInt()
                canvas.drawBitmap(mDivergeViewProvider!!.getBitmap(divergeInfo.mType),
                        divergeInfo.mX,
                        divergeInfo.mY,
                        mPaint)
            }
        }
        mIsDrawing = false
    }


    private fun getBreakPointF(scale1: Int, scale2: Int): PointF {

        val pointF = PointF()
        pointF.x = (mRandom.nextInt((measuredWidth - paddingRight + paddingLeft) / scale1) + measuredWidth / scale2).toFloat()
        pointF.y = (mRandom.nextInt((measuredHeight - paddingBottom + paddingTop) / scale1) + measuredHeight / scale2).toFloat()
        return pointF
    }

    protected fun createDivergeNode(type: Any): DivergeInfo {
        var endPoint = mPtEnd
        if (endPoint == null) {
            endPoint = PointF(mRandom.nextInt(measuredWidth).toFloat(), 0f)
        }
        // int height = mDivergeViewProvider == null ? mDefaultHeight : mDivergeViewProvider.getBitmap(type).getHeight();
        if (startPoint == null) {
            startPoint = PointF((measuredWidth / 2).toFloat(), (measuredHeight - mDefaultHeight).toFloat())//默认起始高度
        }
        return DivergeInfo(
                startPoint!!.x,
                startPoint!!.y,
                getBreakPointF(2, 3),
                endPoint,
                type)
    }

    inner class DivergeInfo(var mX: Float, var mY: Float, var mBreakPoint: PointF, var mEndPoint: PointF, var mType: Any) {
        var mDuration: Float = 0.toFloat()
        var mStartX: Float = 0.toFloat()
        var mStartY: Float = 0.toFloat()

        init {
            mDuration = 0.0f
            mStartX = mX
            mStartY = mY
        }

        fun reset() {
            mDuration = 0.0f
            mX = mStartX
            mY = mStartY
        }
    }

    companion object {

        val mDuration = 0.010f
        val mDefaultHeight = 100
        protected val mQueenDuration: Long = 200
    }
}