package cz.filipproch.reactor.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import java.util.concurrent.CountDownLatch

fun finishActivitySync(activity: Activity) {
    executeActionAndWaitForActivityStage({
        activity.finish()
    }, { targetActivity, stage ->
        targetActivity == activity
                && stage == Stage.DESTROYED
    }, onMainThread = true)
}

fun recreateActivity(activity: Activity) {
    executeActionAndWaitForActivityStage({
        activity.recreate()
    }, { targetActivity, stage ->
        targetActivity != activity
                && targetActivity.javaClass == activity.javaClass
                && stage == Stage.RESUMED
    }, onMainThread = true)
}

fun changeActivityOrientation(activity: Activity) {
    executeActionAndWaitForActivityStage({
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
}

fun getRestartedActivityInstance(): Activity {
    var currentActivity: Activity? = null
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
        if (resumedActivities.iterator().hasNext()) {
            currentActivity = resumedActivities.iterator().next()
        }
    }

    return checkNotNull(currentActivity)
}

fun executeActionAndWaitForActivityStage(
        action: () -> Unit,
        checkStage: (activity: Activity, stage: Stage) -> Boolean,
        onMainThread: Boolean = false
) {
    val countDownLatch = CountDownLatch(1)

    val callback = { targetActivity: Activity, stage: Stage ->
        if (checkStage.invoke(targetActivity, stage)) {
            countDownLatch.countDown()
        }
    }

    if (onMainThread) {
        getInstrumentation().runOnMainSync {
            ActivityLifecycleMonitorRegistry.getInstance()
                    .addLifecycleCallback(callback)

            action.invoke()
        }
    } else {
        getInstrumentation().runOnMainSync {
            ActivityLifecycleMonitorRegistry.getInstance()
                    .addLifecycleCallback(callback)
        }

        action.invoke()
    }

    try {
        countDownLatch.await()
    } catch (e: InterruptedException) {
        throw RuntimeException("Action failed", e)
    }

    ActivityLifecycleMonitorRegistry.getInstance()
            .removeLifecycleCallback(callback)
}