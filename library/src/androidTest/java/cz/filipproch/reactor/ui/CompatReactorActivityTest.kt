package cz.filipproch.reactor.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.util.view.TestActivity
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.app.Activity
import android.support.test.runner.lifecycle.Stage
import cz.filipproch.reactor.util.view.ReactorViewTestHelper


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
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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