package ru.lionzxy.yetanotherreader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ru.lionzxy.yetanotherreaderlibrary.ReaderFragment
import ru.lionzxy.yetanotherreaderlibrary.data.Book

class MainActivity : AppCompatActivity() {
    lateinit var fragment: ReaderFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        var tmpFragment = supportFragmentManager.findFragmentByTag(ReaderFragment.TAG) as ReaderFragment?
        if (tmpFragment == null) {
            tmpFragment = ReaderFragment()
            tmpFragment.arguments = Bundle()
            tmpFragment.arguments!!.putSerializable("book", Book("Тестовая книга", "Никита Куликов",
                    String(resources.openRawResource(R.raw.fish).readBytes())))
            tmpFragment.retainInstance = true
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, tmpFragment, ReaderFragment.TAG)
                    .commit()
        }
        fragment = tmpFragment
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
