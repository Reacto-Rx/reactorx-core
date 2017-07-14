package org.reactorx.util.view.dialogfragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class DialogFragmentTestActivity : AppCompatActivity() {

    private val FRAGMENT_TAG = "a_fragment"

    var fragment: TestDialogFragment? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragment = TestDialogFragment()

            fragment?.show(supportFragmentManager, FRAGMENT_TAG)
            supportFragmentManager.executePendingTransactions()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as TestDialogFragment?
        }
    }

}