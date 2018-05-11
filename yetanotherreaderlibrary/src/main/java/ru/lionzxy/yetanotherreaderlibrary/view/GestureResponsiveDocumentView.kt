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

    @Suppress("unused")
    fun setFlingListener(listener: FlingListener) {
        gestureListener.flingListener = listener
    }

    fun setFlingListener(flingUpListener: (() -> Unit)? = null, flingDownListener: (() -> Unit)? = null,
                         flingLeftListener: (() -> Unit)? = null, flingRightListener: (() -> Unit)? = null) {
        gestureListener.flingListener = object : FlingListener {
            override fun onFlingUp() {
                flingUpListener?.invoke()
            }
            override fun onFlingDown() {
                flingDownListener?.invoke()
            }
            override fun onFlingLeft() {
                flingLeftListener?.invoke()
            }
            override fun onFlingRight() {
                flingRightListener?.invoke()
            }
        }
    }

    class GestureListener : GestureDetector.SimpleOnGestureListener() {
        var tapListener: TapListener? = null
        var flingListener: FlingListener? = null

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

    interface FlingListener {
        fun onFlingLeft()
        fun onFlingRight()
        fun onFlingUp()
        fun onFlingDown()
    }
}