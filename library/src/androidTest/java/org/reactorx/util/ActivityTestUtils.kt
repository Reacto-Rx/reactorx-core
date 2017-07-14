package org.reactorx.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun finishActivitySync(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {
        activity.finish()
    }, { sameActivityInstance, stage ->
        sameActivityInstance && stage == Stage.DESTROYED
    }, onMainThread = true)

    getInstrumentation().waitForIdleSync()
}

fun waitForActivityToFinish(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {}, { sameActivityInstance, stage ->
        sameActivityInstance
                && stage == Stage.DESTROYED
    })

    getInstrumentation().waitForIdleSync()
}

fun recreateActivity(activity: Activity) {
    executeActionAndWaitForActivityStage(activity, {
        activity.recreate()
    }, { sameActivityInstance, stage ->
        sameActivityInstance.not()
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
    }, { sameActivityInstance, stage ->
        sameActivityInstance.not()
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

fun executeActionAndWaitForActivityStage(
        activity: Activity,
        action: () -> Unit,
        checkStage: (sameActivityInstance: Boolean, stage: Stage) -> Boolean,
        onMainThread: Boolean = false,
        timeout: Long = 60000
) {
    val countDownLatch = CountDownLatch(1)

    val activityClazz = activity.javaClass.name
    val activityInstanceId = ActivityStateWatcher.getActivityInstanceId(activity)

    val disposable = ActivityStateWatcher.watch()
            .subscribe {
                it.filter { it.key.startsWith(activityClazz) }
                        .forEach { (key, state) ->
                            if (checkStage.invoke(key == activityInstanceId, state.stage)) {
                                countDownLatch.countDown()
                            }
                        }
            }

    if (onMainThread) {
        getInstrumentation().runOnMainSync { action.invoke() }
    } else {
        action.invoke()
    }

    try {
        val result = countDownLatch.await(timeout, TimeUnit.MILLISECONDS)
        if (result.not()) {
            val stage = ActivityStateWatcher.activityStates[activityInstanceId]
            throw RuntimeException("Activity action failed - timeout (${timeout}ms elapsed) - original Activity current stage = $stage")
        }
    } catch (e: InterruptedException) {
        throw RuntimeException("Activity action failed", e)
    }

    disposable.dispose()
}