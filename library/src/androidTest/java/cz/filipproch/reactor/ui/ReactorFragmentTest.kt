package cz.filipproch.reactor.ui

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.util.view.FragmentTestActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorFragmentTest {

    @Rule
    @JvmField val activityRule = ActivityTestRule(FragmentTestActivity::class.java)

    @Test
    fun testFragmentLifecycle() {

    }

}