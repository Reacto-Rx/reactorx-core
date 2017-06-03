package cz.filipproch.reactor.ui

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.ui.events.*
import cz.filipproch.reactor.util.*
import cz.filipproch.reactor.util.view.*
import cz.filipproch.reactor.util.view.activity.ActivityTestCompatActivity
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorCompatActivityTest : ActivityTest() {

    @Rule
    @JvmField val activityRule = CustomActivityTestRule(ActivityTestCompatActivity::class.java)

    /**
     * Assert that all lifecycle methods were called, in correct order
     */
    @Test
    fun testActivityLifecycle() {
        val activity = activityRule.activity

        activity.helper.assertMethodsCalledInOrder(
                ActivityTestCompatActivity.METHOD_CREATE_LAYOUT,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                ActivityTestCompatActivity.METHOD_UI_CREATED,
                ActivityTestCompatActivity.METHOD_POST_UI_CREATED,
                ActivityTestCompatActivity.METHOD_UI_READY,
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

        activity = getResumedActivityInstance() as ActivityTestCompatActivity

        activity.helper.assertMethodsCalledInOrder(
                ActivityTestCompatActivity.METHOD_CREATE_LAYOUT,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                ActivityTestCompatActivity.METHOD_UI_RESTORED,
                ActivityTestCompatActivity.METHOD_POST_UI_CREATED,
                ActivityTestCompatActivity.METHOD_UI_READY,
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

        activity = getResumedActivityInstance() as ActivityTestCompatActivity

        activity.helper.assertMethodsCalledInOrder(
                ActivityTestCompatActivity.METHOD_CREATE_LAYOUT,
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                ActivityTestCompatActivity.METHOD_UI_RESTORED,
                ActivityTestCompatActivity.METHOD_POST_UI_CREATED,
                ActivityTestCompatActivity.METHOD_UI_READY,
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
                        ViewCreatedEvent::class.java,
                        ViewStartedEvent::class.java,
                        ViewResumedEvent::class.java
                )
    }

    @Test
    fun testReceiveUiModelInResponseToEvent() {
        val activity = activityRule.activity
        activity.dispatch(ReturnUiModelEvent)

        getInstrumentation().waitForIdleSync()

        assertThat(activity.helper.receivedUiModels).hasSize(1)

        assertThat(activity.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testReceiveUiActionInResponseToEvent() {
        val activity = activityRule.activity
        activity.dispatch(ReturnUiActionEvent)

        getInstrumentation().waitForIdleSync()

        assertThat(activity.helper.receivedUiActions).hasSize(1)

        assertThat(activity.helper.receivedUiActions.first())
                .isInstanceOf(TestUiAction::class.java)
    }

    @Test
    fun testReceiveUiModelAfterOrientationChanges() {
        var activity = activityRule.activity
        activity.dispatch(ReturnUiModelEvent)

        changeActivityOrientation(activity)

        activity = getResumedActivityInstance() as ActivityTestCompatActivity

        assertThat(activity.helper.receivedUiModels).hasSize(1)

        assertThat(activity.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testReceiveUiModelAfterActivityRecreated() {
        var activity = activityRule.activity
        activity.dispatch(ReturnUiModelEvent)

        recreateActivity(activity)

        activity = getResumedActivityInstance() as ActivityTestCompatActivity

        assertThat(activity.helper.receivedUiModels).hasSize(1)

        assertThat(activity.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testNotReceiveUiActionAfterOrientationChanges() {
        var activity = activityRule.activity
        activity.dispatch(ReturnUiActionEvent)

        changeActivityOrientation(activity)

        activity = getResumedActivityInstance() as ActivityTestCompatActivity

        assertThat(activity.helper.receivedUiActions).isEmpty()
    }

    @Test
    fun testNotReceiveUiActionAfterActivityRecreated() {
        var activity = activityRule.activity
        activity.dispatch(ReturnUiActionEvent)

        recreateActivity(activity)

        activity = getResumedActivityInstance() as ActivityTestCompatActivity

        assertThat(activity.helper.receivedUiActions).isEmpty()
    }

}
