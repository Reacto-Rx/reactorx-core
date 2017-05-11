package cz.filipproch.reactor.ui

import android.os.Build
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.ui.events.*
import cz.filipproch.reactor.util.*
import cz.filipproch.reactor.util.view.CompatActivityTestActivity
import cz.filipproch.reactor.util.view.ReactorViewTestHelper
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class CompatReactorActivityTest {

    @Rule
    @JvmField val activityRule = ActivityTestRule(CompatActivityTestActivity::class.java)

    /**
     * Assert that all lifecycle methods were called, in correct order
     */
    @Test
    fun testActivityLifecycle() {
        val activity = activityRule.activity

        activity.helper.assertMethodsCalledInOrder(
                CompatActivityTestActivity.METHOD_CREATE_LAYOUT,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                CompatActivityTestActivity.METHOD_UI_CREATED,
                CompatActivityTestActivity.METHOD_POST_UI_CREATED,
                CompatActivityTestActivity.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    /**
     * Assert that all lifecycle methods were called, in correct order, when screen rotates
     */
    @Test
    fun testActivityRotationLifecycle() {
        var activity = activityRule.activity

        changeActivityOrientation(activity)

        activity = getResumedActivityInstance() as CompatActivityTestActivity

        activity.helper.assertMethodsCalledInOrder(
                CompatActivityTestActivity.METHOD_CREATE_LAYOUT,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                CompatActivityTestActivity.METHOD_UI_RESTORED,
                CompatActivityTestActivity.METHOD_POST_UI_CREATED,
                CompatActivityTestActivity.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM)
    }

    /**
     * Assert that all lifecycle methods were called, in correct order, when [android.app.Activity] restarts
     */
    @Test
    fun testActivityRecreateLifecycle() {
        var activity = activityRule.activity

        recreateActivity(activity)

        activity = getResumedActivityInstance() as CompatActivityTestActivity

        activity.helper.assertMethodsCalledInOrder(
                CompatActivityTestActivity.METHOD_CREATE_LAYOUT,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                CompatActivityTestActivity.METHOD_UI_RESTORED,
                CompatActivityTestActivity.METHOD_POST_UI_CREATED,
                CompatActivityTestActivity.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM)
    }

    @Test
    fun testViewHelperInitialization() {
        val viewHelper = activityRule.activity.reactorViewHelper

        assertThat(viewHelper).isNotNull()
    }

    @Test
    fun testTranslatorReceivedEventsActivityCleanStart() {
        val translator = activityRule.activity.reactorViewHelper?.translator

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
        val translator = checkNotNull(activity.reactorViewHelper?.translator)

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
        val translator = checkNotNull(activity.reactorViewHelper?.translator)

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
