package cz.filipproch.reactor.ui

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.ui.events.*
import cz.filipproch.reactor.util.*
import cz.filipproch.reactor.util.view.*
import cz.filipproch.reactor.util.view.dialogfragment.DialogFragmentTestActivity
import cz.filipproch.reactor.util.view.dialogfragment.TestDialogFragment
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class ReactorDialogFragmentTest : ActivityTest() {

    @Rule
    @JvmField val activityRule = CustomActivityTestRule(DialogFragmentTestActivity::class.java)

    private val dialog: TestDialogFragment
        get() = checkNotNull((getResumedActivityInstance() as DialogFragmentTestActivity).fragment)

    @Test
    fun testDialogFragmentLifecycle() {
        dialog.helper.assertMethodsCalledInOrder(
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                TestDialogFragment.METHOD_UI_CREATED,
                TestDialogFragment.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    @Test
    fun testDialogFragmentRotationLifecycle() {
        changeActivityOrientation(activityRule.activity)

        dialog.helper.assertMethodsCalledInOrder(
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                TestDialogFragment.METHOD_UI_RESTORED,
                TestDialogFragment.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    @Test
    fun testDialogFragmentRecreateLifecycle() {
        recreateActivity(activityRule.activity)

        dialog.helper.assertMethodsCalledInOrder(
                ReactorViewTestHelper.METHOD_EMITTERS_INIT,
                TestDialogFragment.METHOD_UI_RESTORED,
                TestDialogFragment.METHOD_UI_READY,
                ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM,
                ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM
        )
    }

    @Test
    fun testViewHelperInitialization() {
        val viewHelper = dialog.reactorViewHelper

        assertThat(viewHelper).isNotNull()
    }

    @Test
    fun testTranslatorReceivedEventsActivityCleanStart() {
        val translator = dialog.reactorViewHelper?.translator

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
        val translator = checkNotNull(dialog.reactorViewHelper?.translator)

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
        val translator = checkNotNull(dialog.reactorViewHelper?.translator)

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
        dialog.dispatch(ReturnUiModelEvent)

        getInstrumentation().waitForIdleSync()

        assertThat(dialog.helper.receivedUiModels).hasSize(1)

        assertThat(dialog.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testReceiveUiActionInResponseToEvent() {
        dialog.dispatch(ReturnUiActionEvent)

        getInstrumentation().waitForIdleSync()

        assertThat(dialog.helper.receivedUiActions).hasSize(1)

        assertThat(dialog.helper.receivedUiActions.first())
                .isInstanceOf(TestUiAction::class.java)
    }

    @Test
    fun testReceiveUiModelAfterOrientationChanges() {
        dialog.dispatch(ReturnUiModelEvent)

        changeActivityOrientation(activityRule.activity)

        assertThat(dialog.helper.receivedUiModels).hasSize(1)

        assertThat(dialog.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testReceiveUiModelAfterActivityRecreated() {
        dialog.dispatch(ReturnUiModelEvent)

        recreateActivity(activityRule.activity)

        assertThat(dialog.helper.receivedUiModels).hasSize(1)

        assertThat(dialog.helper.receivedUiModels.first())
                .isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun testNotReceiveUiActionAfterOrientationChanges() {
        dialog.dispatch(ReturnUiActionEvent)

        changeActivityOrientation(activityRule.activity)

        assertThat(dialog.helper.receivedUiActions).isEmpty()
    }

    @Test
    fun testNotReceiveUiActionAfterActivityRecreated() {
        dialog.dispatch(ReturnUiActionEvent)

        recreateActivity(activityRule.activity)

        assertThat(dialog.helper.receivedUiActions).isEmpty()
    }

}