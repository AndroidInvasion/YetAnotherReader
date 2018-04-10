package ru.lionzxy.yetanotherreader

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import com.bluejamesbond.text.hyphen.DefaultHyphenator
import com.bluejamesbond.text.style.TextAlignment
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.android.synthetic.main.reader_toolbar.*
import kotlinx.android.synthetic.main.view_reader.*
import kotlinx.android.synthetic.main.view_reader_settings.*


class MainActivity : AppCompatActivity() {
    private var infoVisible = false
    private var settingsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        text.documentLayoutParams.textAlignment = TextAlignment.JUSTIFIED
        text.documentLayoutParams.hyphenator = DefaultHyphenator.getInstance(DefaultHyphenator.HyphenPattern.RU)
        text.documentLayoutParams.isHyphenated = true
        text.text = String(resources.openRawResource(R.raw.fish).readBytes())

        centerClick.setTapListener({ showInfo(!infoVisible) })
        setSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reader_toolbar, menu)
        return true
    }

    private fun setSettings() {
        val preferences = getSharedPreferences("reader", Context.MODE_PRIVATE)
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
            textSize += 2
            preferences.edit().putFloat("reader_textsize", textSize).apply()
            text.documentLayoutParams.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            text.setText(text.text)
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
            val animTop = AnimationUtils.loadAnimation(this, R.anim.reader_emergence_top)
            val animBottom = AnimationUtils.loadAnimation(this, R.anim.reader_emergence_bottom)

            reader_top_settings.startAnimation(animTop)
            reader_bottom_settings.startAnimation(animBottom)
        }
    }

    private fun showSettings(visible: Boolean) {
        reader_blackout.visibility = if (visible) View.VISIBLE else View.GONE
        reader_settings_panel.visibility = if (visible) View.VISIBLE else View.GONE

        settingsVisible = visible
        if (visible) {
            val animMiddle = AnimationUtils.loadAnimation(this, R.anim.reader_emergence_middle)

            reader_blackout.startAnimation(animMiddle)
        }
    }

    private fun setReaderColorAndWritePref(color: ReaderColor, preferences: SharedPreferences) {
        preferences.edit().putInt("reader_color", color.id).apply()
        setReaderColor(color)
    }

    @Suppress("DEPRECATION")
    private fun setReaderColor(color: ReaderColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text.background = ColorDrawable(resources.getColor(color.backgroundColor, theme))
            text.documentLayoutParams.textColor = resources.getColor(color.textColor, theme)
        } else {
            text.background = ColorDrawable(resources.getColor(color.backgroundColor))
            text.documentLayoutParams.textColor = resources.getColor(color.textColor)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
