package cz.filipproch.reactor.util.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class FragmentTestActivity : AppCompatActivity() {

    private val FRAGMENT_TAG = "a_fragment"

    var fragment: TestFragment? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragment = TestFragment()

            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment, FRAGMENT_TAG)
                    .commitNow()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as TestFragment?
        }
    }
}