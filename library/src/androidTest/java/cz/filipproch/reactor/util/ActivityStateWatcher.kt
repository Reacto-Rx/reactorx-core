package cz.filipproch.reactor.util

import android.app.Activity
import android.support.test.runner.lifecycle.ActivityLifecycleCallback
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

object ActivityStateWatcher : ActivityLifecycleCallback {

    val activityStates = hashMapOf<String, Stage>()

    val subject: BehaviorSubject<HashMap<String, Stage>> = BehaviorSubject.createDefault(activityStates)

    fun watchActivityStates() {
        ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(this)
    }

    fun cleanup() {
        ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(this)
        activityStates.clear()
    }

    fun watch(): Observable<HashMap<String, Stage>> {
        return subject
    }

    override fun onActivityLifecycleChanged(activity: Activity, stage: Stage) {
        Log.v("ActivityStateWatcher", "onActivityLifecycleChanged($activity, $stage)")
        activityStates.put(getActivityInstanceId(activity), stage)
        subject.onNext(activityStates)
    }

    fun getActivityInstanceId(activity: Activity): String {
        return "${activity.javaClass.name}@${System.identityHashCode(activity)}"
    }

}
