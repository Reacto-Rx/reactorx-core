package cz.filipproch.reactor.ui

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.util.view.dialogfragment.DialogFragmentTestActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorDialogFragmentTest {

    @Rule
    @JvmField val activityRule = ActivityTestRule(DialogFragmentTestActivity::class.java)

    @Test
    fun testDialogFragmentLifecycle() {

    }

}