package cz.filipproch.reactor.ui

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.util.changeActivityOrientation
import cz.filipproch.reactor.util.finishActivitySync
import cz.filipproch.reactor.util.getRestartedActivityInstance
import cz.filipproch.reactor.util.view.TranslatorFragmentTestActivity
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorTranslatorFragmentTest {

    @Rule
    @JvmField val activityRule = ActivityTestRule(TranslatorFragmentTestActivity::class.java)

    @Test
    fun testTranslatorCreated() {
        val activity = activityRule.activity

        assertThat(activity.translator).isNotNull()
    }

    @Test
    fun testTranslatorDestroyed() {
        val activity = activityRule.activity
        val fragment = checkNotNull(activity.translatorFragment)

        finishActivitySync(activity)

        assertThat(fragment.isDestroyed).isTrue()
    }

    @Test
    fun testTranslatorPersistence() {
        var activity = activityRule.activity

        val originalTranslator = activity.translator

        changeActivityOrientation(activity)

        activity = getRestartedActivityInstance() as TranslatorFragmentTestActivity

        assertThat(activity.translator).isNotNull()

        assertThat(activity.translator).isEqualTo(originalTranslator)
    }

}