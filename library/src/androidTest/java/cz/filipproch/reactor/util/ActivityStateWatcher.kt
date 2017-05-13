package cz.filipproch.reactor.util

import android.app.Activity
import android.support.test.runner.lifecycle.ActivityLifecycleCallback
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

object ActivityStateWatcher : ActivityLifecycleCallback {

    val activityStates = hashMapOf<String, ActivityState>()

    val subject: BehaviorSubject<HashMap<String, ActivityState>> = BehaviorSubject.createDefault(activityStates)

    fun watchActivityStates() {
        ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(this)
    }

    fun cleanup() {
        ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(this)
        activityStates.clear()
    }

    fun watch(): Observable<HashMap<String, ActivityState>> {
        return subject
    }

    override fun onActivityLifecycleChanged(activity: Activity, stage: Stage) {
        Log.v("ActivityStateWatcher", "onActivityLifecycleChanged($activity, $stage)")
        activityStates.put(getActivityInstanceId(activity), ActivityState(activity, stage))
        subject.onNext(activityStates)
    }

    fun getActivityInstanceId(activity: Activity): String {
        return "${activity.javaClass.name}@${System.identityHashCode(activity)}"
    }

    data class ActivityState(
            val activity: Activity,
            val stage: Stage
    )

}
