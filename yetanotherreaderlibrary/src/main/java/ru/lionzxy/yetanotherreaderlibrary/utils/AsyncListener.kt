package ru.lionzxy.yetanotherreaderlibrary.utils

import com.bluejamesbond.text.DocumentView

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 19.04.18
 */

class AsyncListener(var listener: DocumentView.ILayoutProgressListener?) : DocumentView.ILayoutProgressListener {
    override fun onFinish() {
        listener?.onFinish()
    }

    override fun onProgressUpdate(progress: Float) {
        listener?.onProgressUpdate(progress)
    }

    override fun onCancelled() {
        listener?.onCancelled()
    }

    override fun onStart() {
        listener?.onStart()
    }

}