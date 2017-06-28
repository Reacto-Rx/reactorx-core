package cz.filipproch.reactor.ui

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.ui.events.*
import cz.filipproch.reactor.util.*
import cz.filipproch.reactor.util.view.*
import cz.filipproch.reactor.util.view.fragment.FragmentTestActivity
import cz.filipproch.reactor.util.view.fragment.TestFragment
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorFragmentTest : ActivityTest() {

    @Rule
    @JvmField val activityRule = CustomActivityTestRule(FragmentTestActivity::class.java)

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
                        ViewCreatedEvent::class.java,
                        ViewStartedEvent::class.java,
                        ViewResumedEvent::class.java
                )
    }

    @Test
    fun testReceiveUiModelInResponseToEvent() {
        fragment.dispatch(ReturnUiModelEvent)

        getInstrumentation().waitForIdleSync()

        assertThat(fragment.helper.receivedUiModels).hasSize(1)

        assertThat(fragment.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testReceiveUiActionInResponseToEvent() {
        fragment.dispatch(ReturnUiActionEvent)

        getInstrumentation().waitForIdleSync()

        assertThat(fragment.helper.receivedUiActions).hasSize(1)

        assertThat(fragment.helper.receivedUiActions.first())
                .isInstanceOf(TestUiAction::class.java)
    }

    @Test
    fun testReceiveUiModelAfterOrientationChanges() {
        fragment.dispatch(ReturnUiModelEvent)

        changeActivityOrientation(activityRule.activity)

        assertThat(fragment.helper.receivedUiModels).hasSize(1)

        assertThat(fragment.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testReceiveUiModelAfterActivityRecreated() {
        fragment.dispatch(ReturnUiModelEvent)

        recreateActivity(activityRule.activity)

        assertThat(fragment.helper.receivedUiModels).hasSize(1)

        assertThat(fragment.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testNotReceiveUiActionAfterOrientationChanges() {
        fragment.dispatch(ReturnUiActionEvent)

        changeActivityOrientation(activityRule.activity)

        assertThat(fragment.helper.receivedUiActions).isEmpty()
    }

    @Test
    fun testNotReceiveUiActionAfterActivityRecreated() {
        fragment.dispatch(ReturnUiActionEvent)

        recreateActivity(activityRule.activity)

        assertThat(fragment.helper.receivedUiActions).isEmpty()
    }

}