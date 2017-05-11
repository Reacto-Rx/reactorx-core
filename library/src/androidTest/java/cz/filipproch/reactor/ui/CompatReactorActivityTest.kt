package cz.filipproch.reactor.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import cz.filipproch.reactor.ui.events.ViewCreatedEvent
import cz.filipproch.reactor.ui.events.ViewResumedEvent
import cz.filipproch.reactor.ui.events.ViewStartedEvent
import cz.filipproch.reactor.util.assertThatTranslator
import cz.filipproch.reactor.util.view.ReactorViewTestHelper
import cz.filipproch.reactor.util.view.TestActivity
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class CompatReactorActivityTest {

    @Rule
    @JvmField val activityRule = ActivityTestRule(TestActivity::class.java)

    /**
     * Assert that all lifecycle methods were called, in correct order
     */
    @Test
    fun testActivityLifecycle() {
        val activity = activityRule.activity

        activity.helper.assertMethodsCalledInOrder(
                TestActivity.METHOD_CREATE_LAYOUT,
                TestActivity.METHOD_UI_CREATED,
                TestActivity.METHOD_POST_UI_CREATED,
                TestActivity.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM)
    }

    /**
     * Assert that all lifecycle methods were called, in correct order, when screen rotates
     */
    @Test
    fun testActivityRotationLifecycle() {
        rotateScreen()

        val activity = getRestartedActivityInstance() as TestActivity

        activity.helper.assertMethodsCalledInOrder(
                TestActivity.METHOD_CREATE_LAYOUT,
                TestActivity.METHOD_UI_RESTORED,
                TestActivity.METHOD_POST_UI_CREATED,
                TestActivity.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM)
    }

    /**
     * Assert that all lifecycle methods were called, in correct order, when [android.app.Activity] restarts
     */
    @Test
    fun testActivityRecreateLifecycle() {
        restartActivity()

        val activity = getRestartedActivityInstance() as TestActivity

        activity.helper.assertMethodsCalledInOrder(
                TestActivity.METHOD_CREATE_LAYOUT,
                TestActivity.METHOD_UI_RESTORED,
                TestActivity.METHOD_POST_UI_CREATED,
                TestActivity.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM)
    }

    @Test
    fun testViewHelperInitialization() {
        val viewHelper = activityRule.activity.reactorViewHelper

        assertThat(viewHelper).isNotNull()
    }

    @Test
    fun testTranslatorInitialization() {
        val translator = activityRule.activity.reactorViewHelper?.translator

        assertThat(translator).isNotNull()

        assertThatTranslator(translator)
                .receivedFollowingEventsInOrder(
                        ViewCreatedEvent::class.java,
                        ViewStartedEvent::class.java,
                        ViewResumedEvent::class.java
                )
    }

    @Test
    fun testTranslatorDeinitialization() {
        val translator = activityRule.activity.reactorViewHelper?.translator

        // clear the events
        translator?.receivedEvents?.clear()

        getInstrumentation().runOnMainSync { activityRule.activity.finish() }

        getInstrumentation().waitForIdleSync()

        assertThatTranslator(translator)
                .receivedFollowingEventsInOrder(
                        ViewCreatedEvent::class.java,
                        ViewStartedEvent::class.java,
                        ViewResumedEvent::class.java
                )
    }

    private fun restartActivity() {
        val activity = activityRule.activity
        getInstrumentation().runOnMainSync { activity.recreate() }

        onView(isRoot())
                .perform(click())
    }

    private fun rotateScreen() {
        val countDownLatch = CountDownLatch(1)

        val context = InstrumentationRegistry.getTargetContext()
        val orientation = context.resources.configuration.orientation

        val activity = activityRule.activity
        activity.requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity.runOnUiThread { activity.recreate() }

        getInstrumentation().waitForIdle { countDownLatch.countDown() }

        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            throw RuntimeException("Screen rotation failed", e)
        }
    }

    fun getRestartedActivityInstance(): Activity {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = resumedActivities.iterator().next()
            }
        }

        return checkNotNull(currentActivity)
    }

}
