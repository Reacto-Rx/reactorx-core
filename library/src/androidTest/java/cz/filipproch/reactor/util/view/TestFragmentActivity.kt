package cz.filipproch.reactor.util.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class TestFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, TestFragment())
                    .commit()
        }
    }
}