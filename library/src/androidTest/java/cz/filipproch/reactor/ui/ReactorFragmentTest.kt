package cz.filipproch.reactor.ui

import android.os.Build
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.ui.events.*
import cz.filipproch.reactor.util.*
import cz.filipproch.reactor.util.view.FragmentTestActivity
import cz.filipproch.reactor.util.view.ReactorViewTestHelper
import cz.filipproch.reactor.util.view.TestFragment
import org.assertj.core.api.Java6Assertions.assertThat
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

    private val fragment: TestFragment
        get() = checkNotNull((getResumedActivityInstance() as FragmentTestActivity).fragment)

    @Test
    fun testFragmentLifecycle() {
        fragment.helper.assertMethodsCalledInOrder(
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                TestFragment.METHOD_UI_CREATED,
                TestFragment.METHOD_POST_UI_CREATED,
                TestFragment.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    @Test
    fun testFragmentRotationLifecycle() {
        changeActivityOrientation(activityRule.activity)

        fragment.helper.assertMethodsCalledInOrder(
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                TestFragment.METHOD_UI_RESTORED,
                TestFragment.METHOD_POST_UI_CREATED,
                TestFragment.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    @Test
    fun testFragmentActivityRecreateLifecycle() {
        recreateActivity(activityRule.activity)

        fragment.helper.assertMethodsCalledInOrder(
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                TestFragment.METHOD_UI_RESTORED,
                TestFragment.METHOD_POST_UI_CREATED,
                TestFragment.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    @Test
    fun testViewHelperInitialization() {
        val viewHelper = fragment.reactorViewHelper

        assertThat(viewHelper).isNotNull()
    }

    @Test
    fun testTranslatorReceivedEventsActivityCleanStart() {
        val translator = fragment.reactorViewHelper?.translator

        assertThatTranslator(translator)
                .receivedFollowingEventsInOrder(
                        ViewCreatedEvent::class.java,
                        ViewStartedEvent::class.java,
                        ViewResumedEvent::class.java
                )
    }

    @Test
    fun testTranslatorReceivedEventsActivityFinish() {
        val activity = activityRule.activity
        val translator = checkNotNull(fragment.reactorViewHelper?.translator)

        // clear the events
        translator.receivedEvents.clear()

        finishActivitySync(activity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            assertThat(activity.isDestroyed).isTrue()
        }

        assertThatTranslator(translator)
                .receivedFollowingEventsInOrder(
                        ViewPausedEvent::class.java,
                        ViewStoppedEvent::class.java
                )

        assertThat(translator.isDestroyed).isTrue()
    }

    @Test
    fun testTranslatorReceivedEventsScreenRotation() {
        val activity = activityRule.activity
        val translator = checkNotNull(fragment.reactorViewHelper?.translator)

        // clear all received events, this is covered by different test
        translator.receivedEvents.clear()

        changeActivityOrientation(activity)

        assertThatTranslator(translator)
                .receivedFollowingEventsInOrder(
                        ViewPausedEvent::class.java,
                        ViewStoppedEvent::class.java,
                        ViewDestroyedEvent::class.java,
                        ViewCreatedEvent::class.java,
                        ViewStartedEvent::class.java,
                        ViewResumedEvent::class.java
                )
    }

}