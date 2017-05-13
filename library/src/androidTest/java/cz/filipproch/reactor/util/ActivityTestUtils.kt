package cz.filipproch.reactor.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.lifecycle.ActivityLifecycleCallback
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun finishActivitySync(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {
        activity.finish()
    }, { targetActivity, stage ->
        targetActivity == activity
                && stage == Stage.DESTROYED
    }, onMainThread = true)

    getInstrumentation().waitForIdleSync()
}

fun waitForActivityToFinish(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {}, { targetActivity, stage ->
        targetActivity == activity
                && stage == Stage.DESTROYED
    })

    getInstrumentation().waitForIdleSync()
}

fun recreateActivity(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {
        activity.recreate()
    }, { targetActivity, stage ->
        targetActivity != activity
                && targetActivity.javaClass == activity.javaClass
                && stage == Stage.RESUMED
    }, onMainThread = true)

    getInstrumentation().waitForIdleSync()
}

fun changeActivityOrientation(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {
        val context = InstrumentationRegistry.getTargetContext()
        val orientation = context.resources.configuration.orientation

        activity.requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }, { targetActivity, stage ->
        targetActivity != activity
                && targetActivity.javaClass == activity.javaClass
                && stage == Stage.RESUMED
    })

    getInstrumentation().waitForIdleSync()
}

fun getResumedActivityInstance(): Activity {
    var currentActivity: Activity? = null
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
        if (resumedActivities.iterator().hasNext()) {
            currentActivity = resumedActivities.iterator().next()
        }
    }

    return checkNotNull(currentActivity)
}

object ActivityStateWatcher : ActivityLifecycleCallback {

    val activityStates = hashMapOf<Activity, Stage>()

    fun watchActivityStates() {
        ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(this)
    }

    fun cleanup() {
        ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(this)
        activityStates.clear()
    }

    override fun onActivityLifecycleChanged(activity: Activity, stage: Stage) {
        activityStates.put(activity, stage)
    }

}

fun executeActionAndWaitForActivityStage(
        activity: Activity,
        action: () -> Unit,
        checkStage: (activity: Activity, stage: Stage) -> Boolean,
        onMainThread: Boolean = false,
        timeout: Long = 20000
) {
    val countDownLatch = CountDownLatch(1)

    val callback = { targetActivity: Activity, stage: Stage ->
        if (checkStage.invoke(targetActivity, stage)) {
            countDownLatch.countDown()
        }
    }

    getInstrumentation().runOnMainSync {
        ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(callback)

        if (onMainThread) {
            action.invoke()
        }
    }

    if (onMainThread.not()) {
        action.invoke()
    }

    if (ActivityStateWatcher.activityStates.containsKey(activity)) {
        val stage = ActivityStateWatcher.activityStates[activity]
        if (checkStage.invoke(activity, checkNotNull(stage))) {
            countDownLatch.countDown()
        }
    }

    try {
        val result = countDownLatch.await(timeout, TimeUnit.MILLISECONDS)
        if (result.not()) {
            val stage = ActivityStateWatcher.activityStates[activity]
            throw RuntimeException("Activity action failed - timeout (${timeout}ms elapsed) - current stage = $stage")
        }
    } catch (e: InterruptedException) {
        throw RuntimeException("Activity action failed", e)
    }

    ActivityLifecycleMonitorRegistry.getInstance()
            .removeLifecycleCallback(callback)
}