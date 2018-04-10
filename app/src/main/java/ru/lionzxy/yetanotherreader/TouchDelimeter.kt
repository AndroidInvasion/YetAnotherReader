package ru.lionzxy.yetanotherreader

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Отличная статья: https://stfalcon.com/ru/blog/post/learning-android-gestures
 *
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 08.04.18
 */

class TouchDelimeter : FrameLayout {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    /**
     * @return Если хотя бы одна дочерняя вью заинтересованна принимать события, отправлять всем
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var oneInterested = false
        for (i in 0..childCount) {
            val view = getChildAt(i)
            if (view != null && executeOnTouchEvent(view, event)) {
                // Опрашиваем все дочерние вью
                oneInterested = true
            }
        }
        return oneInterested
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    private fun executeOnTouchEvent(view: View, ev: MotionEvent?): Boolean {
        if (view is ViewGroup) {
            var oneInterested = view.onTouchEvent(ev)
            for (i in 0..view.childCount) {
                val childView = view.getChildAt(i)
                if (childView != null && executeOnTouchEvent(childView, ev)) {
                    oneInterested = true
                }
            }
            return oneInterested
        } else {
            return view.onTouchEvent(ev)
        }
    }
}