package cz.filipproch.reactor.ui

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.Fragment
import cz.filipproch.reactor.ui.helper.ReactorTranslatorFragment
import cz.filipproch.reactor.util.*
import cz.filipproch.reactor.util.view.translatorfragment.TranslatorFragmentTestActivity
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorTranslatorFragmentTest : ActivityTest() {

    @Rule
    @JvmField val activityRule = CustomActivityTestRule(TranslatorFragmentTestActivity::class.java)

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

        activity = getResumedActivityInstance() as TranslatorFragmentTestActivity

        assertThat(activity.translator).isNotNull()

        assertThat(activity.translator).isEqualTo(originalTranslator)
    }

    @Test
    fun testFragmentInstanceDestroyed() {
        val activity = activityRule.activity
        val fragment = checkNotNull(activity.translatorFragment)

        var state: Fragment.SavedState? = null
        getInstrumentation().runOnMainSync {
            state = activity.supportFragmentManager.saveFragmentInstanceState(fragment)
        }

        // recreate fragment
        val newFragment = ReactorTranslatorFragment<TranslatorFragmentTestActivity.TranslatorFragmentTestTranslator>()
        newFragment.setInitialSavedState(state)

        getInstrumentation().runOnMainSync {
            activity.replaceFragmentWithNewInstance(newFragment)
        }

        getInstrumentation().waitForIdleSync()

        assertThat(newFragment.isInvalid).isTrue()
    }

}