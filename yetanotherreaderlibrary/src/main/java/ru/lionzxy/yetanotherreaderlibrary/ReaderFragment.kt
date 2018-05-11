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
import ru.lionzxy.yetanotherreaderlibrary.data.GlideApp
import ru.lionzxy.yetanotherreaderlibrary.data.ReaderColor
import ru.lionzxy.yetanotherreaderlibrary.utils.AsyncListener

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 14.04.18
 */

class ReaderFragment : Fragment() {
    private var infoVisible = false
    private var settingsVisible = false
    private var nextListener: (() -> Unit)? = null
    private var buyListener: ((book: Book?) -> Unit)? = null
    private var layoutProgressListener: AsyncListener? = null
    private var book: Book? = null
    private var currentPage = 0

    companion object {
        const val TAG = "ru.lionzxy.yetanotherreaderlibrary.ReaderFragment"

        fun getReaderColor(context: Context): ReaderColor {
            val preferences = context.getSharedPreferences("reader", Context.MODE_PRIVATE)
            val id = preferences.getInt("reader_color", ReaderColor.WHITE.id)
            return ReaderColor.getReaderByNumber(id)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return view ?: inflater.inflate(R.layout.fragment_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.documentLayoutParams.textAlignment = TextAlignment.JUSTIFIED
        text.documentLayoutParams.hyphenator = DefaultHyphenator.getInstance(com.bluejamesbond.text.R.raw.ru, context)
        text.documentLayoutParams.isHyphenated = true
        layoutProgressListener = AsyncListener(object : DocumentView.ILayoutProgressListener {
            override fun onFinish() {
                progressbar?.hide()
            }

            override fun onProgressUpdate(progress: Float) {}
            override fun onCancelled() {
                progressbar?.hide()
            }

            override fun onStart() {
                progressbar?.show()
            }
        })
        text.setOnLayoutProgressListener(layoutProgressListener)
        reader_seek_bar.setScrollView(reader_scrollview)
        toolbar_back.setOnClickListener { activity?.onBackPressed() }
        settings_next.setOnClickListener { nextListener?.invoke() }
        reader_imread_next.setOnClickListener { nextListener?.invoke() }
        reader_imread_buy.setOnClickListener { buyListener?.invoke(book) }
        reader_blackout.setOnClickListener { showInfo(false) }
        settings_back.setOnClickListener { activity?.onBackPressed() }
        reader_scrollview.viewTreeObserver.addOnGlobalLayoutListener {
            reader_buy.layoutParams.height = reader_scrollview.height
            reader_buy.invalidate()
        }

        text.setTapListener({ showInfo(!infoVisible) })
        setSettings(view.context)

        val book = arguments?.get("book")
        if (book != null && book is Book) {
            setBookInside(book)
        }
    }

    @Suppress("unused")
    fun setBook(book: Book) {
        reader_scrollview.scrollTo(reader_scrollview.scrollX, 0)
        arguments?.putSerializable("book", book)
        setBookInside(book)
    }

    @Suppress("unused")
    fun turnPageForward() {
        turnPage(currentPage + 1)
    }

    @Suppress("unused")
    fun turnPageBack() {
        turnPage(currentPage - 1)
    }

    @Suppress("unused")
    fun setCurrentPage(page: Int) {
        turnPage(page)
    }

    @Suppress("unused")
    fun getCurrentPage(): Int {
        return currentPage
    }

    @Suppress("unused")
    fun setNextListener(listener: () -> Unit) {
        this.nextListener = listener
    }

    @Suppress("unused")
    fun setBuyListener(listener: (book: Book?) -> Unit) {
        this.buyListener = listener
    }

    @Suppress("unused")
    fun setBuyPreview(visible: Boolean) {
        reader_buy.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @Suppress("unused")
    fun setProgressBarVisible(visible: Boolean) {
        if (visible) {
            progressbar.show()
            reader_scrollview.visibility = View.INVISIBLE
        } else {
            progressbar.hide()
            reader_scrollview.visibility = View.VISIBLE
        }
    }

    private fun turnPage(page: Int) {
        currentPage = if (page < 0) 0 else if (page > text.pageCount) text.pageCount else page
        text.currentPage = currentPage
    }

    private fun setBookInside(book: Book) {
        this.book = book
        toolbar_title.text = book.title
        toolbar_author.text = book.author
        text.text = book.bookText
        reader_imread_booktitle.text = book.title
        reader_imread_bookauthor.text = book.author
        reader_imread_bookdescription.text = book.description
        if (book.imageUrl.isNotEmpty()) {
            GlideApp.with(this)
                    .load(book.imageUrl)
                    .placeholder(R.drawable.book)
                    .error(R.drawable.book)
                    .into(reader_imread_image)
        } else {
            reader_imread_image.setImageResource(R.drawable.book)
        }
    }

    private fun setSettings(context: Context) {
        val preferences = context.getSharedPreferences("reader", Context.MODE_PRIVATE)
        val reader = getReaderColor(context)
        var textSize = preferences.getFloat("reader_textsize", 24f)

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
        reader_blackout.visibility = if (visible) View.VISIBLE else View.GONE

        if (visible) {
            toolbar_settings.isClickable = false
            toolbar_settings.setOnClickListener { showSettings(!settingsVisible) }

            val animMiddle = AnimationUtils.loadAnimation(context!!, R.anim.reader_emergence_middle)
            reader_blackout.startAnimation(animMiddle)
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
        reader_settings_panel.visibility = if (visible) View.VISIBLE else View.GONE

        settingsVisible = visible
    }

    private fun setReaderColorAndWritePref(color: ReaderColor, preferences: SharedPreferences) {
        preferences.edit().putInt("reader_color", color.id).apply()
        setReaderColor(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutProgressListener?.listener = null
    }

    @Suppress("DEPRECATION")
    private fun setReaderColor(color: ReaderColor) {
        val textColor: Int
        val background: ColorDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            background = ColorDrawable(resources.getColor(color.backgroundColor, context?.theme))
            textColor = resources.getColor(color.textColor, context?.theme)
        } else {
            background = ColorDrawable(resources.getColor(color.backgroundColor))
            textColor = resources.getColor(color.textColor)
        }

        text.background = background
        text.documentLayoutParams.textColor = textColor
        reader_scrollview.background = background
        reader_buy.background = background
        reader_imread_title.setTextColor(textColor)
        reader_imread_bookauthor.setTextColor(textColor)
        reader_imread_bookdescription.setTextColor(textColor)
        reader_imread_booktitle.setTextColor(textColor)
        reader_buy.invalidate()

    }

}