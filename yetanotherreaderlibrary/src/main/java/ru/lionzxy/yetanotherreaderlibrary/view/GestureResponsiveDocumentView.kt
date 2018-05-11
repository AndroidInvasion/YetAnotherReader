package ru.lionzxy.yetanotherreaderlibrary.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.bluejamesbond.text.PaginatedDocumentView

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 16.04.18
 */

class GestureResponsiveDocumentView : PaginatedDocumentView {
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

    fun setFlingUpBoundaryVelocity(velocity: Float) {
        gestureListener.flingUpBoundaryVelocity = velocity
    }
    fun setFlingDownBoundaryVelocity(velocity: Float) {
        gestureListener.flingDownBoundaryVelocity = velocity
    }
    fun setFlingLeftBoundaryVelocity(velocity: Float) {
        gestureListener.flingLeftBoundaryVelocity = velocity
    }
    fun setFlingRightBoundaryVelocity(velocity: Float) {
        gestureListener.flingRightBoundaryVelocity = velocity
    }
    fun setFlingHorizontalBoundaryVelocity(velocity: Float) {
        setFlingLeftBoundaryVelocity(velocity)
        setFlingRightBoundaryVelocity(velocity)
    }
    fun setFlingVerticalBoundaryVelocity(velocity: Float) {
        setFlingUpBoundaryVelocity(velocity)
        setFlingDownBoundaryVelocity(velocity)
    }
    fun setFlingBoundaryVelocity(velocity: Float) {
        setFlingVerticalBoundaryVelocity(velocity)
        setFlingHorizontalBoundaryVelocity(velocity)
    }

    class GestureListener : GestureDetector.SimpleOnGestureListener() {
        var flingUpBoundaryVelocity = 0F
        var flingDownBoundaryVelocity = 0F
        var flingLeftBoundaryVelocity = 0F
        var flingRightBoundaryVelocity = 0F

        var tapListener: TapListener? = null
        var flingListener: FlingListener? = null

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            tapListener?.onSingleTap()
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (-velocityX > flingRightBoundaryVelocity) {
                flingListener?.onFlingRight()
            }
            if (velocityX > flingLeftBoundaryVelocity) {
                flingListener?.onFlingLeft()
            }
            if (-velocityY > flingUpBoundaryVelocity) {
                flingListener?.onFlingUp()
            }
            if (velocityY > flingDownBoundaryVelocity) {
                flingListener?.onFlingDown()
            }
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