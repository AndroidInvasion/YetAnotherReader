package ru.lionzxy.yetanotherreader.view

import android.content.Context
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.widget.ScrollView
import android.widget.SeekBar

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 14.04.18
 */

class SeekBarForScrollView : AppCompatSeekBar {
    private val MAX_SEEK_PROGRESS = 1000
    private var scrollView: ScrollView? = null
    private var scrollByScrollView = false

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun setScrollView(scrollView: ScrollView) {
        max = MAX_SEEK_PROGRESS
        this.scrollView = scrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener({
            scrollByScrollView = true
            progress = (scrollView.scrollY.toFloat() * MAX_SEEK_PROGRESS / getScrollRange()).toInt()
            scrollByScrollView = false
        })
        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                syncScrollProgress()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun getScrollRange(): Int {
        val tmp = scrollView ?: return 0
        var scrollRange = 1
        if (tmp.childCount > 0) {
            val child = tmp.getChildAt(0)
            scrollRange = Math.max(0, child.height - (tmp.height -
                    tmp.paddingBottom - tmp.paddingTop))
        }
        return scrollRange
    }

    private fun syncScrollProgress() {
        if (scrollByScrollView) {
            scrollByScrollView = false
            return
        }
        val progress = progress.toFloat() / max.toFloat()
        scrollView?.scrollTo(scrollView?.scrollX ?: 0, (progress * getScrollRange()).toInt())
    }


}