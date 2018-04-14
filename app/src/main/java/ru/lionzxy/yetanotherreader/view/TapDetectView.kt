package ru.lionzxy.yetanotherreader.view

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 05.04.18
 */

class TapDetectView : View {
    private val gestureListener = GestureListener()
    private val gestureDetector: GestureDetectorCompat = GestureDetectorCompat(context, gestureListener)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            gestureDetector.onTouchEvent(ev)
        }
        return true
    }

    fun setTapListener(listener: TapListener) {
        gestureListener.tapListener = listener
    }

    fun setTapListener(listener: () -> Unit) {
        gestureListener.tapListener = object : TapListener {
            override fun onSingleTap() {
                listener()
            }
        }
    }

    class GestureListener : GestureDetector.SimpleOnGestureListener() {
        var tapListener: TapListener? = null
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            tapListener?.onSingleTap()
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

    }

    interface TapListener {
        fun onSingleTap()
    }
}