package ru.lionzxy.yetanotherreaderlibrary

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.bluejamesbond.text.DocumentView
import com.bluejamesbond.text.hyphen.DefaultHyphenator
import com.bluejamesbond.text.style.TextAlignment
import kotlinx.android.synthetic.main.fragment_reader.*
import kotlinx.android.synthetic.main.reader_toolbar.*
import kotlinx.android.synthetic.main.view_reader.*
import kotlinx.android.synthetic.main.view_reader_settings.*
import ru.lionzxy.yetanotherreaderlibrary.data.Book
import ru.lionzxy.yetanotherreaderlibrary.data.ReaderColor

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 14.04.18
 */

class ReaderFragment : Fragment() {
    private var infoVisible = false
    private var settingsVisible = false
    private var nextListener: (() -> Unit)? = null

    companion object {
        val TAG = "ru.lionzxy.yetanotherreaderlibrary.ReaderFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return view ?: inflater.inflate(R.layout.fragment_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.documentLayoutParams.textAlignment = TextAlignment.JUSTIFIED
        text.documentLayoutParams.hyphenator = DefaultHyphenator.getInstance(com.bluejamesbond.text.R.raw.ru, context)
        text.documentLayoutParams.isHyphenated = true
        text.setOnLayoutProgressListener(object : DocumentView.ILayoutProgressListener {
            override fun onFinish() {
                progressbar.hide()
            }

            override fun onProgressUpdate(progress: Float) {}
            override fun onCancelled() {
                progressbar.hide()
            }

            override fun onStart() {
                progressbar.show()
            }
        })
        reader_seek_bar.setScrollView(reader_scrollview)
        toolbar_back.setOnClickListener { activity?.onBackPressed() }
        settings_next.setOnClickListener { nextListener?.invoke() }
        settings_back.setOnClickListener { activity?.onBackPressed() }

        centerClick.setTapListener({ showInfo(!infoVisible) })
        setSettings(view.context)

        val book = arguments?.get("book")
        if (book != null && book is Book) {
            setBook(book)
        }
    }

    public fun setNextListener(listener: () -> Unit) {
        this.nextListener = listener
    }

    private fun setBook(book: Book) {
        toolbar_title.text = book.title
        toolbar_author.text = book.author
        text.text = book.bookText
    }

    private fun setSettings(context: Context) {
        val preferences = context.getSharedPreferences("reader", Context.MODE_PRIVATE)
        val readerColor = preferences.getInt("reader_color", ReaderColor.WHITE.id)
        var textSize = preferences.getFloat("reader_textsize", 24f)

        val reader = ReaderColor.getReaderByNumber(readerColor)
        setReaderColor(reader)

        text.documentLayoutParams.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

        settings_color_white.setOnClickListener { setReaderColorAndWritePref(ReaderColor.WHITE, preferences) }
        settings_color_brown.setOnClickListener { setReaderColorAndWritePref(ReaderColor.BROWN, preferences) }
        settings_color_green.setOnClickListener { setReaderColorAndWritePref(ReaderColor.GREEN, preferences) }
        settings_color_black.setOnClickListener { setReaderColorAndWritePref(ReaderColor.BLACK, preferences) }

        settings_text_decrease.setOnClickListener {
            textSize = Math.max(2f, textSize - 2)
            preferences.edit().putFloat("reader_textsize", textSize).apply()
            text.documentLayoutParams.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            text.text = text.text
        }
        settings_text_increase.setOnClickListener {
            textSize = Math.min(48f, textSize + 2)
            preferences.edit().putFloat("reader_textsize", textSize).apply()
            text.documentLayoutParams.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            text.text = text.text
        }
    }

    private fun showInfo(visible: Boolean) {
        reader_top_settings.visibility = if (visible) View.VISIBLE else View.GONE
        reader_bottom_settings.visibility = if (visible) View.VISIBLE else View.GONE
        if (visible) {
            toolbar_settings.isClickable = false
            toolbar_settings.setOnClickListener { showSettings(!settingsVisible) }
        } else {
            toolbar_settings.setOnClickListener(null)
            toolbar_settings.isClickable = false
            showSettings(false)
        }

        infoVisible = visible
        if (visible) {
            val animTop = AnimationUtils.loadAnimation(context, R.anim.reader_emergence_top)
            val animBottom = AnimationUtils.loadAnimation(context, R.anim.reader_emergence_bottom)

            reader_top_settings.startAnimation(animTop)
            reader_bottom_settings.startAnimation(animBottom)
        }
    }

    private fun showSettings(visible: Boolean) {
        reader_blackout.visibility = if (visible) View.VISIBLE else View.GONE
        reader_settings_panel.visibility = if (visible) View.VISIBLE else View.GONE

        settingsVisible = visible
        if (visible) {
            val animMiddle = AnimationUtils.loadAnimation(context!!, R.anim.reader_emergence_middle)

            reader_blackout.startAnimation(animMiddle)
        }
    }

    private fun setReaderColorAndWritePref(color: ReaderColor, preferences: SharedPreferences) {
        preferences.edit().putInt("reader_color", color.id).apply()
        setReaderColor(color)
    }

    @Suppress("DEPRECATION")
    public fun setReaderColor(color: ReaderColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text.background = ColorDrawable(resources.getColor(color.backgroundColor, context?.theme))
            text.documentLayoutParams.textColor = resources.getColor(color.textColor, context?.theme)
        } else {
            text.background = ColorDrawable(resources.getColor(color.backgroundColor))
            text.documentLayoutParams.textColor = resources.getColor(color.textColor)
        }
    }

}