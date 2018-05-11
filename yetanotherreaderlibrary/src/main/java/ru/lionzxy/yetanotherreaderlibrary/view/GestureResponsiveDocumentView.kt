package ru.lionzxy.yetanotherreaderlibrary.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.bluejamesbond.text.DocumentView

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 16.04.18
 */

class GestureResponsiveDocumentView : DocumentView {
    private val gestureListener = GestureListener()
    private val gestureDetector: GestureDetectorCompat = GestureDetectorCompat(context, gestureListener)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onInterceptTouchEvent(ev: MotionEvent?) = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        super.onTouchEvent(ev)
        if (ev != null) {
            gestureDetector.onTouchEvent(ev)
        }
        return true
    }

    @Suppress("unused")
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